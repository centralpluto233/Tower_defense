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

    /**
     * Updates the game view when enemy is killed
     *
     * @param reward the amount of money rewarded for killing the enemy
     * @param x      the x coordinate of the enemy that was killed
     * @param y      the y coordinate of the enemy that was killed
     */
    void enemyKilled(int reward, int x, int y);

    /**
     * Updates the game view when enemy has reached base
     *
     * @param damage the amount of damage dealt to the base by the enemy that reached it
     */
    void enemyReachedBase(int damage);

    /**
     * Updates the game view when a tower is placed
     *
     * @param price the amount of money spent on placing the tower
     * @param x     the x coordinate of the tower that was placed
     * @param y     the y coordinate of the tower that was placed
     */
    void towerPlaced(int price, int x, int y);

    /**
     * Updates the game view when a tower is sold
     *
     * @param refund the amount of money refunded for selling the tower
     * @param x      the x coordinate of the tower that was sold
     * @param y      the y coordinate of the tower that was sold
     */
    void towerSold(int refund, int x, int y);
}
