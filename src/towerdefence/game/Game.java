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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
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
    private Stopwatch renderStopwatch;
    private final List<GameListener> gameListeners = new ArrayList<>();
    private LuaEngine luaEngine;


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
     * initialize the game with the config and the graphics
     * @param config configuration for the game
     * @param factory factory for creating game objects
     */
    public void init(Config config, AbstractGameFactory factory) {
        logger.info("Creating Game with factory: " + (factory == null ? "null" : factory.getClass().getSimpleName()));
        this.factory = factory;
        this.config = config;

        this.luaEngine = new LuaEngine(config);
        this.currentLevel = luaEngine.loadLevelData();

        this.manager = new ECSManager();
        this.waveSpawnSystem = new WaveSpawnSystem();

        this.stats = new RunStats(currentLevel.getLives(), currentLevel.getResources());
        this.entityManager = new EntityManager(stats);

        this.inputHandler = new InputHandler(factory, stats, entityManager, currentLevel);

        this.view = factory.createGameView(this, config);
        this.inputSource = factory.createInputSource(this, config, this.view);
        this.inputSource.installHandlers();

        this.renderStopwatch = new Stopwatch(config.getTargetRenderFps());

        logger.info("Game constructed");
    }

    public void addGameListener(GameListener listener) {
        this.gameListeners.add(listener);
    }

    private void notifyListeners(Consumer<GameListener> event) {
        gameListeners.forEach(event);
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
            render(deltaTime);

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

    private void render(double deltaTime) {
        if (renderStopwatch.hasTimePassed(1.0/config.getTargetRenderFps())) {
            renderStopwatch.tick();
            view.render(deltaTime);
        }
    }

    /**
     * Starts Game
     */
    public void start() {
        waveSpawnSystem.init(currentLevel, manager);
        stats = new RunStats(currentLevel.getLives(), currentLevel.getResources());
        entityManager = new EntityManager(stats);
        inputHandler = new InputHandler(factory, stats, entityManager, currentLevel);
        gameState = GameState.RUNNING;
        notifyListeners(GameListener::onGameStarted);
    }

    public void loadLevel(String levelName) {
        if (gameState != GameState.MENU) stop();
        LevelData newLevel = luaEngine.loadLevelData(levelName);
        if (!newLevel.getLevelName().equals(currentLevel.getLevelName())) {
            currentLevel = newLevel;
            view.reset();
            waveSpawnSystem.reset(manager);
        }
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
            notifyListeners(GameListener::onGameOver);
            return;
        }

        if (manager.getAllEntities().isEmpty() && entityManager.hasNoEnemies()) {
            gameState = GameState.LEVEL_WON;
            notifyListeners(GameListener::onLevelWon);
        }
    }

    /**
     * Stops Game
     */
    public void stop() {
        gameState= GameState.MENU;
        entityManager.clear();
        waveSpawnSystem.reset(manager);
        notifyListeners(GameListener::onGameStopped);
        logger.info("Stopping Game");
    }

    public void exit() {
        gameState= GameState.EXIT;
        notifyListeners(GameListener::onGameStopped);
        logger.info("Exiting Game");
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

    public int getMaxLives() {return stats.getMaxLives();}

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
            notifyListeners(GameListener::onGamePaused);
        } else if (gameState == GameState.PAUSE) {
            gameState = GameState.RUNNING;
            notifyListeners(GameListener::onGameResumed);
        }
    }

    public void notifyBuildMenuRequested(int spotIndex, Vector2D spot) {
        notifyListeners(l -> l.onBuildMenuRequested(spotIndex,spot));
    }

    public void notifyBuildMenuClose() {
        notifyListeners(GameListener::onBuildMenuClosed);
    }

    public InputSource getInputSource() {
        return inputSource;
    }
}
