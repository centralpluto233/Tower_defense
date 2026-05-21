package towerdefence.game;

public interface GameListener {
    void onGameStarted();
    void onGamePaused();
    void onGameResumed();
    void onGameStopped();
    void onGameOver();
    void onLevelWon();
}
