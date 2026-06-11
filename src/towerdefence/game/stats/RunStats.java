package towerdefence.game.stats;

import towerdefence.system.logging.GameLogger;

import java.util.logging.Logger;

public class RunStats {
    private static final Logger logger = GameLogger.getLogger();
    private int kills;
    private int escaped;
    private int lives;
    private final int maxLives;
    private int resources;

    /**
     * Creates a new RunStats object with the given initial lives and resources. The maxLives is set to the initial lives, and kills and escaped are initialized to 0.
     *
     * @param lives     the initial number of lives the player has at the start of the run
     * @param resources the initial amount of resources the player has at the start of the run
     */
    public RunStats(int lives, int resources) {
        this.lives = lives;
        this.maxLives = lives;
        this.resources = resources;
        this.escaped = 0;
        this.kills = 0;
    }

    /**
     * Updates the hud when an enemy is killed. Increments the kill count and adds the reward resources to the total resources.
     *
     * @param rewardResources the amount of resources awarded to the player for killing the enemy, which will be added to the total resources
     */
    public void onEnemyKilled(int rewardResources) {
        resources += rewardResources;
        kills++;
        logger.info("Enemy killed! Total kills: " + kills + ", resources: " + resources);
    }

    /**
     * Updates the hud when an enemy escapes. Increments the escaped count and subtracts the damage from the player's lives. If lives drop to 0 or below, they are set to 0.
     *
     * @param damage the amount of damage the enemy deals to the player when it escapes, which will be subtracted from the player's lives
     */
    public void onEnemyEscaped(int damage) {
        escaped++;
        lives -= damage;
        if (lives <= 0) {
            lives = 0;
        }
        logger.info("Enemy escaped! Total escaped: " + escaped + ", lives left: " + lives);
    }

    /**
     * Attempts to spend the given amount of resources. If the player has enough resources, the cost is subtracted from the total resources and true is returned.
     * If not enough resources are available, a warning is logged and false is returned without changing the total resources.
     *
     * @param cost the amount of resources to spend, which will be subtracted from the total resources if the player has enough
     * @return true if the resources were successfully spent (enough resources available), false if not enough resources were available to spend
     */
    public boolean trySpendResources(int cost) {
        if ((resources - cost) < 0) {
            logger.warning("Not enough resources! Current: " + resources + ", required: " + cost);
            return false;
        } else {
            resources -= cost;
            logger.info("Resources spent: " + cost + ", remaining: " + resources);
            return true;
        }
    }

    public void addResources(int amount) {
        this.resources+= amount;
    }

    /**
     * Checks if the player has been defeated by verifying if the number of lives has dropped to 0. If lives are 0, the player is considered defeated and true is returned. Otherwise, false is returned.
     *
     * @return true if the player has been defeated (lives are 0), false otherwise
     */
    public boolean isDefeated() {
        return (lives == 0);
    }

    /**
     * Returns the amount of lives the player has left
     *
     * @return the current number of lives the player has remaining, which indicates how many more enemies can escape before the player is defeated
     */
    public int getLives() {
        return lives;
    }

    /**
     * Returns the total amount of resources the player currently has available to spend on towers and upgrades
     *
     * @return the current amount of resources the player has, which can be used to build towers and purchase upgrades
     */
    public int getResources() {
        return resources;
    }

    /**
     * Returns the max amount of lives the player started with at the beginning of the run, which can be used for display purposes to show the player's progress towards defeat (e.g., "Lives: 3/10")
     *
     * @return the initial number of lives the player had at the start of the run, which represents the maximum lives and can be used for display purposes to show how many lives have been lost (e.g., "Lives: 3/10")
     */
    public int getMaxLives() {
        return maxLives;
    }
}
