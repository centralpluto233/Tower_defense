package towerdefence.game;

import towerdefence.game.interfaces.GameView;
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
    private final List<Entity> entities = new ArrayList<>();
    private boolean running = false;
    private Config config;
    private GameView view;
    private AbstractGameFactory factory;
    private LuaEngine levelEngine;
    private LevelData currentLevel;

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
    }

    /**
     * run the game loop
     */
    public void run() {
        start();
        Stopwatch stopwatch = new Stopwatch(config.getTargetFps());
        logger.info("Game loop started");

        var enemy = factory.createGoblin(currentLevel.getPath().getFirst().x(), currentLevel.getPath().getFirst().y(),currentLevel.getPath());
        entities.add(enemy);


        while (isRunning()) {
            double deltaTime = stopwatch.tick();
            update(deltaTime);
            view.render();
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

    /**
     * Updates game and all entities
     * @param deltaTime time between frames
     */
    public void update(double deltaTime) {
        if (!running) {
            return;
        }
        logger.finest("Updating Game");
        for (Entity entity: entities) {
            logger.finest("Updating Entity "+entity.toString());
            entity.update(deltaTime);
        }

        // cleanup phase after update
        entities.removeIf(this::shouldRemove);
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
        running = false;
        logger.info("Stopping Game");
    }

    /**
     * Adds entity to entity-list
     * @param entity entity to add to list
     */
    public void addEntity(Entity entity) {
        if (entity == null) {
            logger.warning("Attempted to add null entity to game");
            return;
        }
        logger.info("Adding Entity "+entity);
        entities.add(entity);
    }

    /**
     * Removes entity from entity-list
     * @param entity entity to remove from list
     */
    public void removeEntity(Entity entity) {
        if (entity == null) {
            logger.warning("Attempted to remove null entity from game");
            return;
        }
        logger.info("Removing Entity "+entity);
        entities.remove(entity);
    }

    /**
     * returns all entities
     * @return list of entities
     */
    public List<Entity> getEntities() {
        return entities;
    }

    public List<Vector2D> getPath() {
        return currentLevel.getPath();
    }

    public List<Vector2D> getTowerSpots() {
        return currentLevel.getTowerSpots();
    }
}
