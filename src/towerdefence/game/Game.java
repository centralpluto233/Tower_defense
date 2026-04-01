package towerdefence.game;

import towerdefence.system.logging.GameLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import towerdefence.game.entity.Entity;

public class Game {
    private static Game instance;
    private static final Logger logger = GameLogger.getLogger();
    private final List<Entity> entities = new ArrayList<>();
    private boolean running = false;

    private Game() {
        logger.info("Game created");
    }

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
     * Initializes Game
     */
    public void init() {
        logger.info("Initializing Game");
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

}
