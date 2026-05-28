package towerdefence.game;

import towerdefence.game.entities.tower.Tower;
import towerdefence.game.input.InputCommand;
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
            case PLACE_TOWER_AT_INDEX -> {
                Vector2D spot = levelData.getTowerSpots().get(command.spotIndex());
                if (command.towerType().isEmpty()) {
                    game.notifyBuildMenuRequested(command.spotIndex(),spot);
                } else {
                    handlePlaceTower(command.spotIndex(),command.towerType().get(), game);
                }
            }
            case PAUSE_TOGGLE -> game.togglePause();
            case QUIT_GAME -> game.stop();
            case START_GAME -> game.start();
            default -> logger.warning("Unknown command"+ command.action());
        }
    }


    private void handlePlaceTower(int spotIndex, String towerType, Game game) {
        var spot = levelData.getTowerSpots().get(spotIndex);

        if (entityManager.isTowerSpotOccupied(spot)) return;

        Tower newTower = factory.createTower(towerType, spot);
        int cost = newTower.get_cost();
        if (stats.trySpendResources(cost)) {
            entityManager.addEntity(newTower);
        }
    }
}
