package towerdefence.game;

import towerdefence.game.ecs.ECSManager;
import towerdefence.game.ecs.systems.WaveSpawnSystem;
import towerdefence.game.entity.enemy.EnemyEntity;
import towerdefence.game.entity.projectile.Projectile;
import towerdefence.game.entity.tower.Tower;
import towerdefence.game.interfaces.GameView;
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
    private boolean running = false;
    private Config config;
    private GameView view;
    private AbstractGameFactory factory;
    private LuaEngine levelEngine;
    private LevelData currentLevel;
    private ECSManager manager;
    private WaveSpawnSystem waveSpawnSystem;
    private RunStats stats;

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
     * Returns if game is running
     * @return true if game is running, else false
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * initialise the game with the config and the graphics
     * @param config configuration for the game
     * @param view abstract graphics for the game
     */
    public void init(Config config, GameView view, AbstractGameFactory factory, LuaEngine engine) {
        logger.info("Initializing Game");
        this.config = config;
        this.view = view;
        this.factory = factory;
        this.levelEngine = engine;
        this.currentLevel= levelEngine.loadLevelData();
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

        while (isRunning()) {
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
        running = true;
        logger.info("Starting Game");
    }
    //TODO game wat uitdunnen, niet alles moet in game gebeuren
    //TODO code opkuisen, onnodige functies?

    /**
     * Updates game and all entities
     * @param deltaTime time between frames
     */
    public void update(double deltaTime) {
        if (!running) {
            return;
        }
        logger.finest("Updating Game");

        waveSpawnSystem.update(deltaTime,manager,this,factory);

        for (Tower tower: towers) {
            logger.finest("Updating Tower "+tower.toString());
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
                        stop();
                    }
                }
            }
        }
        enemies.removeIf(this::shouldRemove);

        if (manager.getAllEntities().isEmpty() && this.enemies.isEmpty()) {
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
        if (running) {
            running = false;
            logger.info("Stopping Game");
        }
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
}
