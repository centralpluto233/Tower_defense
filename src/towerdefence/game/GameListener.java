package towerdefence.game;

import towerdefence.game.entities.tower.Tower;
import towerdefence.util.Vector2D;

/**
 * Interface for listening to game events (used by menus)
 * Used to show correct menus in the correct place
 */
public interface GameListener {
    void onGameStarted();

    void onGamePaused();

    void onGameResumed();

    void onGameStopped();

    void onGameOver();

    void onLevelWon();

    void onBuildMenuRequested(int spotIndex, Vector2D spotPosition);

    void onBuildMenuClosed();

    void onTowerSelected(Tower tower, int spotIndex);
}
