package towerdefence.game.entities.enemy;

import towerdefence.game.entities.MovingEntity;
import towerdefence.game.interfaces.Damageable;
import towerdefence.util.Vector2D;
import towerdefence.system.logging.GameLogger;

import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/**
 * Abstract class for enemy entities in the game
 * handles health, damage, path following, scoring
 */
public abstract class EnemyEntity extends MovingEntity implements Damageable {
    private static final Logger logger = GameLogger.getLogger();

    private int health;
    private final int maxHealth;
    private final int scoreValue;
    private final int damage;

    protected List<Vector2D> path;
    protected int currentWaypointIndex = 0;

    private static final double HIT_FLASH_DURATION = 0.15;
    private double hitFlashTimer = 0.0;

    /**
     * Creates a new EnemyEntity with the given parameters.
     * @param x x part of the position of the enemy
     * @param y y part of the position of the enemy
     * @param width the width of the enemy in pixels
     * @param height the height of the enemy in pixels
     * @param speed the speed of the enemy
     * @param health the health of the enemy
     * @param scoreValue the score value awarded (resources) to the player when this enemy is killed
     * @param damage the damage this enemy will deal to the player (health) if it reaches the end of its path
     * @param path the path the enemy will follow, represented as a list of waypoints (Vector2D)
     */
    public EnemyEntity(double x, double y, double width, double height, double speed, int health, int scoreValue, int damage, List<Vector2D> path) {
        super(x, y, width, height, speed);
        this.health = health;
        this.maxHealth = health;
        this.scoreValue = scoreValue;
        this.damage = damage;
        this.path = path;
    }

    /**
     * updates the enemy each game tick
     * @param dt the time elapsed since the last update
     */
    @Override
    public void update(double dt) {
        if (hitFlashTimer > 0.0) {
            hitFlashTimer -= dt;
        }
        if (isDead()) {
            markForRemoval();
            return;
        }
        followPath(dt);
    }

    /**
     * Moves the enemy along its path based on its speed and the elapsed time (dt).
     * @param dt the elapsed time
     */
    protected void followPath(double dt) {
        if (path == null || path.isEmpty()) {
            return;
        }

        if (hasReachedEnd()) {
            markForRemoval();
            return;
        }

        double remaining = getSpeed() * dt;
        double x = getX();
        double y = getY();

        while (remaining > 0.0 && currentWaypointIndex < path.size()) {
            Vector2D target = path.get(currentWaypointIndex);

            double dx = target.x() - x;
            double dy = target.y() - y;
            double distance = Math.hypot(dx, dy);

            // Sta je exact op dit waypoint? Dan direct naar de volgende.
            if (distance < 1e-9) {
                currentWaypointIndex++;
                continue;
            }

            if (remaining >= distance) {
                // Bereik waypoint binnen dit frame, gebruik restbudget verder.
                x = target.x();
                y = target.y();
                remaining -= distance;
                currentWaypointIndex++;
            } else {
                // Beweeg richting waypoint met exact resterende afstand.
                double t = remaining / distance;
                x += dx * t;
                y += dy * t;
                remaining = 0.0;
            }
        }

        setX(x);
        setY(y);

        if (hasReachedEnd()) {
            markForRemoval();
        }
    }


    /**
     * returns whether the enemy has reached the end of its path
     * @return true if enemy reached the end
     */
    public boolean hasReachedEnd() {
        return path != null && currentWaypointIndex >= path.size();
    }

    /**
     * applies damage to the enemy
     * @param damage the amount of damage to apply
     */
    @Override
    public void takeDamage(int damage) {
        this.health -= damage;
        this.health = Math.max(0, this.health);
        hitFlashTimer = HIT_FLASH_DURATION;
        logger.fine("Enemy took "+damage+ " damage, health: "+this.health+ "/"+this.maxHealth);
        if (isDead()) {
            logger.info("Enemy died, score: "+scoreValue);
            markForRemoval();
        }
    }

    /**
     * returns if enemy is dead or not
     * @return true if the object is dead, false otherwise
     */
    @Override
    public boolean isDead() {
        return health <= 0;
    }

    /**
     * returns the current health of the enemy
     * @return health of enemy
     */
    @Override
    public int getHealth() {
        return health;
    }

    /**
     * returns the max health of the enemy
     * @return max health of enemy
     */
    @Override
    public int getMaxHealth() {
        return maxHealth;
    }

    /**
     * returns the score value awarded to the player when this enemy is killed
     * @return score value of enemy
     */
    public int getScoreValue() {
        return scoreValue;
    }

    /**
     * returns the damage value done to the player when this enemy reaches the end of its path
     * @return damage value of enemy
     */
    public int getDamage() {
        return damage;
    }

    /**
     * returns the path that the enemy follows.
     * unmodifiable so that external code cannot change the enemy's path directly, which could cause bugs in movement logic.
     * @return list of Vector2D points
     */
    public List<Vector2D> getPath() {
        return Collections.unmodifiableList(path);
    }

    /**
     * returns the index of the current waypoint the enemy is moving towards
     * @return index of current waypoint
     */
    public int getCurrentWaypointIndex() {
        return currentWaypointIndex;
    }

    /**
     * returns whether the enemy is currently flashing from being hit
     * @return true if the enemy is currently flashing, false otherwise
     */
    public boolean isHitFlashing() {
        return hitFlashTimer > 0.0;
    }

    /**
     * gives string representation of enemy
     * @return string to print nicely
     */
    @Override
    public String toString() {
        return super.toString()
                + ", Enemy{health=" + health
                + "/" + maxHealth
                + ", scoreValue=" + scoreValue
                + ", damage=" + damage
                + ", waypointIndex=" + currentWaypointIndex
                + ", pathSize=" + (path == null ? 0 : path.size())
                + ", reachedEnd=" + hasReachedEnd()
                + "}";
    }



}
