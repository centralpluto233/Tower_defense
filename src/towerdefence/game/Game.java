package towerdefence.game;

import towerdefence.game.ecs.ECSManager;
import towerdefence.game.ecs.systems.WaveSpawnSystem;
import towerdefence.game.entities.enemy.EnemyEntity;
import towerdefence.game.entities.projectile.Projectile;
import towerdefence.game.entities.tower.Tower;
import towerdefence.game.interfaces.GameView;
import towerdefence.game.interfaces.InputSource;
import towerdefence.game.input.InputCommand;
import towerdefence.game.stats.RunStats;
import towerdefence.util.Vector2D;
import towerdefence.level.LevelData;
import towerdefence.level.LuaEngine;
import towerdefence.system.logging.GameLogger;
import towerdefence.system.config.Config;
import towerdefence.system.time.Stopwatch;

import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import towerdefence.game.entities.Entity;

public class Game {
    private static Game instance;
    private static final Logger logger = GameLogger.getLogger();
    private Config config;
    private GameView view;
    private AbstractGameFactory factory;
    private LevelData currentLevel;
    private ECSManager manager;
    private WaveSpawnSystem waveSpawnSystem;
    private RunStats stats;
    private InputSource inputSource;
    private GameState gameState = GameState.MENU;
    private EntityManager entityManager;
    private InputHandler inputHandler;
    private GameListener gameListener;
    private Stopwatch renderStopwatch;


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
     * @param factory factory for creating game objects
     */
    public void init(Config config, AbstractGameFactory factory) {
        logger.info("Creating Game with factory: " + (factory == null ? "null" : factory.getClass().getSimpleName()));
        this.factory = factory;
        this.config = config;

        LuaEngine engine = new LuaEngine(config);
        this.currentLevel = engine.loadLevelData();

        this.manager = new ECSManager();
        this.waveSpawnSystem = new WaveSpawnSystem();
        this.waveSpawnSystem.init(currentLevel,manager);

        this.stats = new RunStats(currentLevel.getLives(), currentLevel.getResources());
        this.entityManager = new EntityManager(stats);

        this.inputHandler = new InputHandler(factory, stats, entityManager, currentLevel);

        this.view = factory.createGameView(this, config);
        this.inputSource = factory.createInputSource(this, config, this.view);
        this.inputSource.installHandlers();

        this.renderStopwatch = new Stopwatch(config.getTargetRenderFps());

        logger.info("Game constructed");
    }

    public void setGameListener(GameListener listener) {
        this.gameListener = listener;
    }

    /**
     * run the game loop
     */
    public void run() {
        Stopwatch gamestopwatch = new Stopwatch(config.getTargetFps());
        logger.info("Game loop started");

        while (gameState!=GameState.EXIT) {
            double deltaTime = gamestopwatch.tick();
            if (config.getDebug()) {
                double speedup = Math.clamp(config.getDebugSpeedup(), 0.1, 20.0);
                deltaTime *= speedup;
            }
            update(deltaTime);
            render();

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

    private void render() {
        if (renderStopwatch.hasTimePassed(1.0/config.getTargetRenderFps())) {
            renderStopwatch.tick();
            view.render();
        }
    }

    /**
     * Starts Game
     */
    public void start() {
        gameState= GameState.RUNNING;
        gameListener.onGameStarted();
        logger.info("Starting Game");
    }
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

        waveSpawnSystem.update(deltaTime, manager, this, factory);

        boolean defeated = entityManager.update(deltaTime, factory, this);
        if (defeated) {
            gameState = GameState.GAME_OVER;
            gameListener.onGameOver();
            stop();
            return;
        }

        if (manager.getAllEntities().isEmpty() && entityManager.hasNoEnemies()) {
            gameState = GameState.LEVEL_WON;
            gameListener.onLevelWon();
            stop();
        }
    }

    /**
     * Stops Game
     */
    public void stop() {
        gameState= GameState.EXIT;
        gameListener.onGameStopped();
        logger.info("Stopping Game");
    }

    private void processInput() {
        if (inputSource == null) {
            logger.warning("Input source is null, cannot process input");
            return;
        }

        List<InputCommand> commands = inputSource.pollCommands();

        if (Set.of(GameState.MENU, GameState.PAUSE).contains(gameState)) {
            return; // queue al leeggetrokken, commands gewoon weggooien
        }

        for (InputCommand command : commands) {
            inputHandler.handleCommand(command, this);
        }
    }

    public void addEntity(Entity entity) {
        entityManager.addEntity(entity);
    }

    public List<EnemyEntity> getEnemies() {
        return entityManager.getEnemies();
    }

    public List<Tower> getTowers() {
        return entityManager.getTowers();
    }

    public List<Projectile> getProjectiles() {
        return entityManager.getProjectiles();
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

    public void togglePause() {
        if (gameState == GameState.RUNNING) {
            gameState = GameState.PAUSE;
            gameListener.onGamePaused();
        } else if (gameState == GameState.PAUSE) {
            gameState = GameState.RUNNING;
            gameListener.onGameResumed();
        }
    }
}
