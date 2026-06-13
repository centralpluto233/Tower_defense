package towerdefence.game.input;

import towerdefence.game.AbstractGameFactory;
import towerdefence.game.entities.EntityManager;
import towerdefence.game.Game;
import towerdefence.game.entities.tower.Tower;
import towerdefence.game.stats.RunStats;
import towerdefence.level.LevelData;
import towerdefence.system.logging.GameLogger;
import towerdefence.util.Vector2D;

import java.util.logging.Logger;

public class InputHandler {
    private static final Logger logger = GameLogger.getLogger();

    private final AbstractGameFactory factory;
    private final RunStats stats;
    private final EntityManager entityManager;
    private final LevelData levelData;

    /**
     * Constructs an InputHandler with the necessary dependencies.
     *
     * @param factory       the game factory used to create new entities like towers
     * @param stats         the RunStats object used to track player resources and other hud during the game
     * @param entityManager the EntityManager used to manage game entities, allowing the InputHandler to add new towers to the game when they are placed
     * @param levelData     the LevelData containing information about the current level, such as tower spots, which the InputHandler uses to determine where towers can be placed
     */
    public InputHandler(AbstractGameFactory factory, RunStats stats, EntityManager entityManager, LevelData levelData) {
        this.factory = factory;
        this.stats = stats;
        this.entityManager = entityManager;
        this.levelData = levelData;
    }

    /**
     * Handles an input command by performing the appropriate action in the game.
     * This method is called by the game loop whenever there are new input commands to process.
     *
     * @param command the InputCommand to handle, which contains information about the action to perform and any relevant parameters (like tower type or spot index)
     * @param game    the Game instance, which is passed to this method so that it can call methods on the game to perform actions like starting the game, toggling pause, or notifying the game of build menu interactions
     */
    public void handleCommand(InputCommand command, Game game) throws Exception {
        switch (command.action()) {
            case PLACE_TOWER_AT_INDEX -> {
                int spotIndex = command.spotIndex()
                        .orElseThrow(() -> new IllegalArgumentException("Missing spot index"));

                Vector2D spot = levelData.getTowerSpots().get(spotIndex);

                if (entityManager.isTowerSpotOccupied(spot)) return;

                if (command.towerType().isEmpty()) {
                    game.notifyBuildMenuRequested(spotIndex, spot);
                } else {
                    handlePlaceTower(spotIndex, command.towerType().get());
                }
            }
            case SELL_TOWER -> {
                int towerIndex = command.spotIndex()
                        .orElseThrow(() -> new IllegalArgumentException("Missing tower index"));

                int refund = entityManager.sellTower(towerIndex);
                stats.addResources(refund);
            }
            case PAUSE_TOGGLE -> game.togglePause();
            case QUIT_GAME -> game.stop();
            case START_GAME -> game.start();
            case CLOSE_BUILD_MENU -> game.notifyBuildMenuClose();
            default -> logger.warning("Unknown command" + command.action());
        }
    }

    /**
     * Handles the logic for placing a tower on the game map when the player issues a command to place a tower at a specific spot index with a specific tower type.
     *
     * @param spotIndex the index of the tower spot where the player wants to place the tower, which is used to look up the actual position of the spot from the level data
     * @param towerType the type of tower the player wants to place (e.g., "Crossbow", "Archer"), which is used to determine which specific tower entity to create using the factory
     */
    private void handlePlaceTower(int spotIndex, String towerType) throws Exception {
        var spot = levelData.getTowerSpots().get(spotIndex);
        Tower newTower = factory.createTower(towerType, spot);
        int cost = newTower.getCost();

        if (stats.trySpendResources(cost)) {
            entityManager.addEntity(newTower);
        }
    }
}
