package towerdefence.game.interfaces;

public interface GameView {
    /**
     * Renders the game state to the screen. This method should be called once per frame, and should update the display based on the current game state.
     *
     * @param deltaTime the time in seconds since the last frame was rendered. This can be used to update animations and other time-based effects in the game.
     */
    void render(double deltaTime);

    /**
     * Resets the game view to its initial state. This method should be called when starting a new game or when restarting after a game over. It should clear any existing game state and prepare the view for a new game session.
     */
    void reset();

    void enemyKilled(int reward,int x, int y);

    void towerPlaced(int price, int x, int y);

    void towerSold(int refund, int x, int y);
}
