package towerdefence.game;

import towerdefence.game.ecs.ECSManager;
import towerdefence.game.ecs.systems.WaveSpawnSystem;
import towerdefence.game.entities.Entity;
import towerdefence.game.entities.EntityManager;
import towerdefence.game.entities.enemy.EnemyEntity;
import towerdefence.game.entities.projectile.Projectile;
import towerdefence.game.entities.tower.Tower;
import towerdefence.game.input.InputCommand;
import towerdefence.game.input.InputHandler;
import towerdefence.game.interfaces.GameView;
import towerdefence.game.interfaces.InputSource;
import towerdefence.game.stats.RunStats;
import towerdefence.level.GameGrid;
import towerdefence.level.LevelData;
import towerdefence.level.LuaEngine;
import towerdefence.system.config.Config;
import towerdefence.system.config.Constants;
import towerdefence.system.logging.GameLogger;
import towerdefence.system.time.Stopwatch;
import towerdefence.util.Vector2D;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.Logger;

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

    // ---------------------------------------------------------------------
    // Singleton / construction
    // ---------------------------------------------------------------------

    private Game() {
        logger.info("Game created");
    }

    /**
     * singleton game
     *
     * @return game instance
     */
    public static Game getInstance() {
        if (instance == null) {
            instance = new Game();
        }
        return instance;
    }

    // ---------------------------------------------------------------------
    // Initialization / setup
    // ---------------------------------------------------------------------

    /**
     * initialize the game with the config and the graphics
     *
     * @param config  configuration for the game
     * @param factory factory for creating game objects
     */
    public void init(Config config, AbstractGameFactory factory) {
        logger.info("Creating Game with factory: " + factory.getClass().getSimpleName());
        this.factory = factory;
        this.config = config;

        this.luaEngine = new LuaEngine(config);
        try {
            this.currentLevel = luaEngine.loadLevelData();
        } catch (Exception e) {
            logger.warning("Failed to load default level: " + e.getMessage());
            throw new RuntimeException(e);
        }

        this.manager = new ECSManager();
        this.waveSpawnSystem = new WaveSpawnSystem();

        resetGameState();

        this.view = factory.createGameView(this, config);
        setCallbacks();
        this.inputSource = factory.createInputSource(this, config, this.view);
        this.inputSource.installHandlers();

        this.renderStopwatch = new Stopwatch(config.getTargetRenderFps());

        logger.info("Game constructed");
    }

    /**
     * reset the runtime state for the current level
     */
    private void resetGameState() {
        stats = new RunStats(currentLevel.getLives(), currentLevel.getResources());
        entityManager = new EntityManager(stats);
        inputHandler = new InputHandler(factory, stats, entityManager, currentLevel);
        setCallbacks();
    }

    /**
     * set callbacks for entity events to update the view accordingly, such as when an enemy is killed, a tower is placed or sold, or an enemy escapes
     */
    private void setCallbacks() {
        if (view != null) {
            entityManager.setOnEnemyKilledCallback(enemy -> {
                int reward = enemy.getScoreValue();
                view.enemyKilled(reward,(int) enemy.getX(),(int) enemy.getY());
            });

            entityManager.setOnTowerPlacedCallback(tower -> {
                int price = tower.getCost();
                view.towerPlaced(price,(int) tower.getX(),(int) tower.getY());
            });

            entityManager.setOnTowerSoldCallback(tower -> {
                int refund= tower.getRefund();
                view.towerSold(refund,(int) tower.getX(),(int) tower.getY());
            });

            entityManager.setOnEnemyEscapedCallback(enemy -> {
                int damage= enemy.getDamage();
                view.enemyReachedBase(damage);
            });
        }
    }

    /**
     * Load a new level by name, resetting the game state and notifying the view and wave spawn system of the new level data.
     * @param levelName the name of the level to load, which should correspond to a Lua level script file that can be loaded by the LuaEngine
     */
    public void loadLevel(String levelName) {
        if (gameState != GameState.MENU) stop();
        try {
            LevelData newLevel = luaEngine.loadLevelData(levelName);
            if (!newLevel.getLevelName().equals(currentLevel.getLevelName())) {
                currentLevel = newLevel;
                view.reset();
                waveSpawnSystem.reset();
            }
        } catch (Exception e) {
            logger.warning("Failed to load level: " + levelName + ". Error: " + e.getMessage());
        }
    }

    /**
     * Get the name of the next level in the sequence after the current level, if it exists.
     * @return an Optional containing the name of the next level if it exists, or an empty Optional if the current level is the last one in the sequence or if the current level's name cannot be found in the list of levels
     */
    public Optional<String> getNextLevel() {
        List<String> levels = Constants.LevelConstants.LEVEL_NAMES;
        int currentIndex = levels.indexOf(currentLevel.getLevelFile()); // exacte match
        if (currentIndex >= 0 && currentIndex < levels.size() - 1) {
            return Optional.of(levels.get(currentIndex + 1));
        }
        return Optional.empty();
    }

    // ---------------------------------------------------------------------
    // Lifecycle
    // ---------------------------------------------------------------------

    /**
     * Starts Game
     */
    public void start() {
        resetGameState();
        waveSpawnSystem.init(currentLevel, manager);
        gameState = GameState.RUNNING;
        notifyListeners(GameListener::onGameStarted);
    }

    /**
     * Stops Game
     */
    public void stop() {
        gameState = GameState.MENU;
        entityManager.clear();
        waveSpawnSystem.reset();
        notifyListeners(GameListener::onGameStopped);
        logger.info("Stopping Game");
    }

    /**
     * exits the game
     */
    public void exit() {
        gameState = GameState.EXIT;
        notifyListeners(GameListener::onGameStopped);
        logger.info("Exiting Game");
    }

    /**
     * toggles between paused and running
     */
    public void togglePause() {
        if (gameState == GameState.RUNNING) {
            gameState = GameState.PAUSE;
            notifyListeners(GameListener::onGamePaused);
        } else if (gameState == GameState.PAUSE) {
            gameState = GameState.RUNNING;
            notifyListeners(GameListener::onGameResumed);
        }
    }

    /**
     * run the game loop
     */
    public void run() {
        Stopwatch gamestopwatch = new Stopwatch(config.getTargetFps());
        logger.info("Game loop started");

        while (gameState != GameState.EXIT) {
            double deltaTime = gamestopwatch.tick();
            if (config.getDebug()) {
                double speedup = Math.clamp(config.getDebugSpeedup(), Constants.Timing.DEBUG_SPEEDUP_MIN, Constants.Timing.DEBUG_SPEEDUP_MAX);
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

    /**
     * renders current frame if stopwatch tells it's time
     * @param deltaTime time between frames, used for animations and smooth rendering
     */
    private void render(double deltaTime) {
        if (renderStopwatch.hasTimePassed(1.0 / config.getTargetRenderFps())) {
            renderStopwatch.tick();
            view.render(deltaTime);
        }
    }

    /**
     * Updates game and all entities
     *
     * @param deltaTime time between frames
     */
    public void update(double deltaTime) {
        processInput();
        if (isGameNotRunning()) {
            return;
        }

        logger.finest("Updating Game");

        try {
            waveSpawnSystem.update(deltaTime, manager, this, factory);
        } catch (Exception e) {
            logger.warning("Error updating wave spawn system: " + e.getMessage());
            this.exit();
        }

        boolean defeated = entityManager.update(deltaTime, this);
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
     * Handle queued input commands
     */
    private void processInput() {
        if (inputSource == null) {
            logger.warning("Input source is null, cannot process input");
            return;
        }

        List<InputCommand> commands = inputSource.pollCommands();

        if (Set.of(GameState.MENU, GameState.PAUSE).contains(gameState)) {
            return; // queue al leeggetrokken, commands gewoon weggooien, inputs apart behandeld door JButtons en listeners, geen game input
        }

        try {
            for (InputCommand command : commands) {
                inputHandler.handleCommand(command, this);
            }
        } catch (Exception e) {
            logger.warning("Error processing input command: " + e.getMessage());
            this.exit();
        }
    }

    // ---------------------------------------------------------------------
    // Entity accessors
    // ---------------------------------------------------------------------

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

    public List<List<Vector2D>> getPaths() {
        return currentLevel.getPaths();
    }

    public List<Vector2D> getTowerSpots() {
        return currentLevel.getTowerSpots();
    }

    public boolean towerSpotOccupied(Vector2D spot) {
        return entityManager.isTowerSpotOccupied(spot);
    }

    // ---------------------------------------------------------------------
    // State getters
    // ---------------------------------------------------------------------

    public int getLives() {
        return stats.getLives();
    }

    public int getMaxLives() {
        return stats.getMaxLives();
    }

    public int getResources() {
        return stats.getResources();
    }

    public GameState getGameState() {
        return gameState;
    }

    public boolean isGameNotRunning() {
        return gameState != GameState.RUNNING;
    }

    public int getCurrentWave() {
        return waveSpawnSystem.getCurrentWave();
    }

    public int getTotalWaves() {
        return waveSpawnSystem.getTotalWaves();
    }

    public double getNextWaveCountdown() {
        return waveSpawnSystem.getNextWaveCountdown();
    }

    public GameGrid getGrid() {
        return currentLevel.getGrid();
    }

    // ---------------------------------------------------------------------
    // Listeners
    // ---------------------------------------------------------------------

    public void addGameListener(GameListener listener) {
        this.gameListeners.add(listener);
    }

    private void notifyListeners(Consumer<GameListener> event) {
        gameListeners.forEach(event);
    }

    public void notifyBuildMenuRequested(int spotIndex, Vector2D spot) {
        notifyListeners(l -> l.onBuildMenuRequested(spotIndex, spot));
    }

    public void notifyBuildMenuClose() {
        notifyListeners(GameListener::onBuildMenuClosed);
    }
}