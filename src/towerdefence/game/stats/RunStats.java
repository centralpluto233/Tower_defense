package towerdefence.game.stats;

import towerdefence.system.logging.GameLogger;

import java.util.logging.Logger;

public class RunStats {
    private static final Logger logger = GameLogger.getLogger();
    private int kills;
    private int escaped;
    private int lives;
    private int resources;

    public RunStats(int lives, int resources) {
        this.lives = lives;
        this.resources = resources;
        this.escaped = 0;
        this.kills = 0;
    }

    public void onEnemyKilled(int rewardResources) {
        resources+= rewardResources;
        kills++;
        logger.info("Enemy killed! Total kills: " + kills + ", resources: " + resources);
    }

    public void onEnemyEscaped(int damage) {
        escaped++;
        lives -= damage;
        if (lives <= 0) {
            lives = 0;
        }
        logger.info("Enemy escaped! Total escaped: " + escaped + ", lives left: " + lives);
    }

    public boolean trySpendResources(int cost) {
        if ((resources-cost) <= 0) {
            logger.warning("Not enough resources! Current: " + resources + ", required: " + cost);
            return false;
        } else {
            resources -= cost;
            logger.info("Resources spent: " + cost + ", remaining: " + resources);
            return true;
        }
    }

    public boolean isDefeated() {
        return (lives==0);
    }

    public int getLives() {
        return lives;
    }

    public int getResources() {
        return resources;
    }
}
