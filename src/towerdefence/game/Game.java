package towerdefence.game;

import towerdefence.game.ecs.ECSManager;
import towerdefence.game.ecs.systems.WaveSpawnSystem;
import towerdefence.game.entity.enemy.EnemyEntity;
import towerdefence.game.entity.projectile.Projectile;
import towerdefence.game.entity.tower.Tower;
import towerdefence.game.interfaces.GameView;
import towerdefence.game.interfaces.InputSource;
import towerdefence.game.input.InputCommand;
import towerdefence.game.stats.RunStats;
import towerdefence.game.util.Vector2D;
import towerdefence.lua.LevelData;
import towerdefence.lua.LuaEngine;
import towerdefence.system.logging.GameLogger;
import towerdefence.system.config.Config;
import towerdefence.system.time.Stopwatch;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import towerdefence.game.entity.Entity;

public class Game {
    private static Game instance;
    private static final Logger logger = GameLogger.getLogger();
    private final List<EnemyEntity> enemies = new ArrayList<>();
    private final List<Tower> towers = new ArrayList<>();
    private final List<Projectile> projectiles = new ArrayList<>();
    private Config config;
    private GameView view;
    private AbstractGameFactory factory;
    private LevelData currentLevel;
    private ECSManager manager;
    private WaveSpawnSystem waveSpawnSystem;
    private RunStats stats;
    private InputSource inputSource;
    private GameState gameState = GameState.MENU;


    private Game() {
        logger.info("Game created");
    }

    /**
     * singleton game
     * @return game instance
     */
    public static Game getInstance() {
        if (instance == null) {
            instance = new Game();
        }
        return instance;
    }

    /**
     * initialise the game with the config and the graphics
     * @param config configuration for the game
     * @param view abstract graphics for the game
     */
    public void init(Config config, GameView view, AbstractGameFactory factory, LuaEngine engine, InputSource inputSource) {
        logger.info("Initializing Game");
        this.config = config;
        this.view = view;
        this.factory = factory;
        this.inputSource = inputSource;
        this.currentLevel= engine.loadLevelData();
        this.manager = new ECSManager();
        this.waveSpawnSystem = new WaveSpawnSystem();
        this.waveSpawnSystem.init(currentLevel,manager);
        this.stats = new RunStats(currentLevel.getLives(),currentLevel.getResources());
    }

    /**
     * run the game loop
     */
    public void run() {
        start();
        Stopwatch stopwatch = new Stopwatch(config.getTargetFps());
        logger.info("Game loop started");

        while (gameState!=GameState.EXIT) {
            double deltaTime = stopwatch.tick();
            if (config.getDebug()) {
                double speedup = Math.clamp(config.getDebugSpeedup(), 0.1, 20.0);
                deltaTime *= speedup;
            }
            update(deltaTime);
            view.render();

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                logger.warning("Game loop interrupted: " + e.getMessage());
                Thread.currentThread().interrupt();
                break;
            }
        }

        stop();
        logger.info("Game loop finished");
    }

    /**
     * Starts Game
     */
    public void start() {
        gameState= GameState.RUNNING;
        logger.info("Starting Game");
    }
    //TODO game wat uitdunnen, niet alles moet in game gebeuren
    //TODO code opkuisen, onnodige functies?

    /**
     * Updates game and all entities
     * @param deltaTime time between frames
     */
    public void update(double deltaTime) {
        processInput();
        if (!isGameRunning()) {
            return;
        }

        logger.finest("Updating Game");

        waveSpawnSystem.update(deltaTime,manager,this,factory);

        for (Tower tower: towers) {
            logger.finest("Updating Tower "+tower.toString());
            tower.updateCombat(enemies, factory, this);
            tower.update(deltaTime);
        }
        for (Projectile projectile: projectiles) {
            logger.finest("Updating Projectile "+projectile.toString());
            projectile.update(deltaTime);
        }
        for (EnemyEntity enemy: enemies) {
            logger.finest("Updating Enemy "+enemy.toString());
            enemy.update(deltaTime);
        }

        // cleanup phase after update
        for (EnemyEntity enemy: enemies) {
            if (enemy.isMarkedForRemoval()) {
                if (enemy.isDead()) {
                    stats.onEnemyKilled(enemy.getScoreValue());
                } else {
                    stats.onEnemyEscaped(enemy.getDamage());
                    if (stats.isDefeated()) {
                        gameState=GameState.GAME_OVER;
                        stop();
                    }
                }
            }
        }
        enemies.removeIf(this::shouldRemove);
        projectiles.removeIf(this::shouldRemove);

        if (manager.getAllEntities().isEmpty() && this.enemies.isEmpty()) {
            gameState=GameState.LEVEL_WON;
            stop();
        }
    }

    /**
     * Checks if entity needs to be removed
     * @param entity entity to check
     * @return true if entity needs to be removed, else false
     */
    private boolean shouldRemove(Entity entity) {
        if (entity.isMarkedForRemoval()) {
            return true;
        }
        return false;
    }

    /**
     * Stops Game
     */
    public void stop() {
        gameState= GameState.EXIT;
        logger.info("Stopping Game");
    }

    /**
     * Adds entity to one of the entities lists
     * @param entity entity to add to list
     */
    public void addEntity(Entity entity) {
        if (entity == null) {
            logger.warning("Attempted to add null entity to game");
            return;
        }
        if (entity instanceof Tower) {towers.add((Tower) entity);}
        if (entity instanceof Projectile) {projectiles.add((Projectile) entity);}
        if (entity instanceof EnemyEntity) {enemies.add((EnemyEntity) entity);}
    }

    /**
     * Removes entity from correct entity-list
     * @param entity entity to remove from list
     */
    public void removeEntity(Entity entity) {
        if (entity == null) {
            logger.warning("Attempted to remove null entity from game");
            return;
        }
        if (entity instanceof Tower) {towers.remove((Tower) entity);}
        if (entity instanceof Projectile) {projectiles.remove((Projectile) entity);}
        if (entity instanceof EnemyEntity) {enemies.remove((EnemyEntity) entity);}
    }

    private void processInput() {
        if (inputSource == null) {
            logger.warning("Input source is null, cannot process input");
            return;
        }

        for (InputCommand command : inputSource.pollCommands()) {
            switch (command.action()) {
                case PLACE_TOWER_AT_INDEX -> {
                    if (gameState==GameState.RUNNING) {
                        int index = command.spotIndex();
                        if (index >= 0 && index < currentLevel.getTowerSpots().size()) {
                            Vector2D spot = currentLevel.getTowerSpots().get(index);

                            if (isTowerSpotOccupied(spot)) {
                                logger.warning("Tower spot already occupied");
                                continue;
                            }

                            Tower newTower = factory.createBasicTower(spot.x(), spot.y());
                            if (stats.trySpendResources(newTower.get_cost())) {
                                addEntity(newTower);
                            } else {
                                logger.warning("Not enough resources to place tower");
                            }
                        } else {
                            logger.warning("Invalid tower spot index: " + index);
                        }
                    }
                }
                case PAUSE_TOGGLE -> {
                    togglePause();
                    logger.info("pause toggled");
                }
                case QUIT_GAME -> {
                    this.stop();
                }
            }
        }
    }

    private boolean isTowerSpotOccupied(Vector2D spot) {
        for (Tower tower : towers) {
            if (tower.getX() == spot.x() && tower.getY() == spot.y()) {
                return true;
            }
        }
        return false;
    }


    public List<EnemyEntity> getEnemies() {
        return enemies;
    }

    public List<Tower> getTowers() {
        return towers;
    }

    public List<Projectile> getProjectiles() {
        return projectiles;
    }

    public List<Vector2D> getPath() {
        return currentLevel.getPath();
    }

    public List<Vector2D> getTowerSpots() {
        return currentLevel.getTowerSpots();
    }

    public int getLives() {
        return stats.getLives();
    }

    public int getResources() {
        return stats.getResources();
    }

    public GameState getGameState() {
        return gameState;
    }

    public boolean isGameRunning() {
        return gameState== GameState.RUNNING;
    }

    private void togglePause() {
        if (gameState == GameState.RUNNING) {
            gameState = GameState.PAUSE;
        } else if (gameState == GameState.PAUSE) {
            gameState = GameState.RUNNING;
        }
    }
}
