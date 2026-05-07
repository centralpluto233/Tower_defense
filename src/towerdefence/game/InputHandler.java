package towerdefence.game;

import towerdefence.game.entities.tower.Tower;
import towerdefence.game.input.InputCommand;
import towerdefence.game.stats.RunStats;
import towerdefence.level.LevelData;
import towerdefence.system.logging.GameLogger;

import java.util.logging.Logger;

public class InputHandler {
    private static final Logger logger = GameLogger.getLogger();

    private final AbstractGameFactory factory;
    private final RunStats stats;
    private final EntityManager entityManager;
    private final LevelData levelData;

    public InputHandler(AbstractGameFactory factory, RunStats stats, EntityManager entityManager, LevelData levelData) {
        this.factory = factory;
        this.stats = stats;
        this.entityManager = entityManager;
        this.levelData = levelData;
    }

    public void handleCommand(InputCommand command, Game game) {
        if (command == null) {
            logger.warning("Command is null");
            return;
        }

        switch (command.action()) {
            case PLACE_TOWER_AT_INDEX -> handlePlaceTower(command.spotIndex());
            case PAUSE_TOGGLE -> game.togglePause();
            case QUIT_GAME -> game.stop();
            case START_GAME -> game.start();
            default -> logger.warning("Unknown command"+ command.action());
        }
    }

    private void handlePlaceTower(int spotIndex) {
        // Validate spot index
        if (spotIndex < 0 || spotIndex >= levelData.getTowerSpots().size()) {
            logger.warning("Invalid tower spot index: " + spotIndex);
            return;
        }

        var spot = levelData.getTowerSpots().get(spotIndex);

        // Check if spot is already occupied
        if (entityManager.isTowerSpotOccupied(spot)) {
            logger.warning("Tower spot already occupied at index: " + spotIndex);
            return;
        }

        // Try to create and place tower
        Tower newTower = factory.createBasicTower(spot.x(), spot.y());
        int cost = newTower.get_cost();

        if (stats.trySpendResources(cost)) {
            entityManager.addEntity(newTower);
            logger.info("Tower placed at spot: " + spotIndex);
        } else {
            logger.warning("Not enough resources to place tower. Cost: " + cost);
        }
    }
}
