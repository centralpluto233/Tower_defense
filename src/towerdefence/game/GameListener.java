package towerdefence.game;

import towerdefence.util.Vector2D;

public interface GameListener {
    void onGameStarted();
    void onGamePaused();
    void onGameResumed();
    void onGameStopped();
    void onGameOver();
    void onLevelWon();
    void onBuildMenuRequested(int spotIndex, Vector2D spotPosition);
    void onBuildMenuClosed();
}
