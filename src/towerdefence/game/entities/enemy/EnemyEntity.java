package towerdefence.game.entities.enemy;

import towerdefence.game.ecs.components.PoisonEffectComponent;
import towerdefence.game.entities.MovingEntity;
import towerdefence.game.interfaces.Damageable;
import towerdefence.system.config.Constants;
import towerdefence.system.logging.GameLogger;
import towerdefence.util.Vector2D;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.logging.Logger;

/**
 * Abstract class for enemy entities in the game
 * handles health, damage, path following, scoring
 */
public abstract class EnemyEntity extends MovingEntity implements Damageable {
    private static final Logger logger = GameLogger.getLogger();
    private final int maxHealth;
    private final int scoreValue;
    private final int damage;
    private final double[] offset;
    protected List<Vector2D> path;
    protected int currentWaypointIndex = 0;
    private int health;
    private double hitFlashTimer = 0.0;
    private Optional<PoisonEffectComponent> poisonComponent = Optional.empty();

    /**
     * Creates a new EnemyEntity with the given parameters.
     *
     * @param x          x part of the position of the enemy
     * @param y          y part of the position of the enemy
     * @param width      the width of the enemy in pixels
     * @param height     the height of the enemy in pixels
     * @param speed      the speed of the enemy
     * @param health     the health of the enemy
     * @param scoreValue the score value awarded (resources) to the player when this enemy is killed
     * @param damage     the damage this enemy will deal to the player (health) if it reaches the end of its path
     * @param path       the path the enemy will follow, represented as a list of waypoints (Vector2D)
     */
    public EnemyEntity(double x, double y, double width, double height, double speed, int health, int scoreValue, int damage, List<Vector2D> path) {
        super(x, y, width, height, speed);
        this.health = health;
        this.maxHealth = health;
        this.scoreValue = scoreValue;
        this.damage = damage;
        this.offset = computeOffset();

        // Apply the offset to the initial position and path waypoints to prevent perfect overlap of enemies spawning at the same location.
        setX(x + offset[0]);
        setY(y + offset[1]);
        // We create a new list of waypoints with the offset applied, rather than modifying the original list, to avoid side effects on other enemies that might use the same path.
        this.path = path.stream()
                .map(wp -> new Vector2D(wp.x() + offset[0], wp.y() + offset[1]))
                .toList();
    }

    /**
     * Computes a random offset for the enemy's position to prevent them from overlapping perfectly when they spawn at the same point. The offset is a random vector with a length between 0 and MAX_OFFSET, in a random direction.
     *
     * @return an array of two doubles representing the x and y components of the offset
     */
    private static double[] computeOffset() {
        Random rand = new Random();
        double angle = rand.nextDouble() * 2 * Math.PI;
        double offset = rand.nextDouble() * 2 * Constants.EnemyConstants.MAX_OFFSET;
        double x = Math.cos(angle) * offset;
        double y = Math.sin(angle) * offset;
        return new double[]{x, y};
    }

    // --------------------------------------------
    // update
    // --------------------------------------------


    /**
     * updates the enemy each game tick
     *
     * @param dt the time elapsed since the last update
     */
    @Override
    public void update(double dt) {
        // effects update
        poisonTick(dt);

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
     *
     * @param dt the elapsed time
     */
    protected void followPath(double dt) {
        if (path == null || path.isEmpty()) {
            logger.warning("path is null or empty, cannot follow path");
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
            if (distance < Constants.Gameplay.PATH_POINT_EPSILON) {
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
     *
     * @return true if enemy reached the end
     */
    public boolean hasReachedEnd() {
        return path != null && currentWaypointIndex >= path.size();
    }

    /**
     * applies poison damage over time if the enemy is currently poisoned, and removes the poison effect if it has expired
     *
     * @param deltaTime the amount of time that has passed since the last update, in seconds
     */
    private void poisonTick(double deltaTime) {
        if (poisonComponent.isPresent()) {
            PoisonEffectComponent pc = poisonComponent.get();
            if (pc.update(deltaTime)) {
                takeDamage(pc.getDamagePerTick());
            }
            if (pc.isExpired()) {
                poisonComponent = Optional.empty();
            }
        }
    }

    // -------------------------------------------------
    // health & damage
    // -------------------------------------------------

    /**
     * applies damage to the enemy
     *
     * @param damage the amount of damage to apply
     */
    @Override
    public void takeDamage(int damage) {
        this.health -= damage;
        this.health = Math.max(0, this.health);
        hitFlashTimer = Constants.EnemyConstants.HIT_FLASH_DURATION;
        logger.fine("Enemy took " + damage + " damage, health: " + this.health + "/" + this.maxHealth);
        if (isDead()) {
            logger.info("Enemy died, score: " + scoreValue);
            markForRemoval();
        }
    }

    /**
     * returns if enemy is dead or not
     *
     * @return true if the object is dead, false otherwise
     */
    @Override
    public boolean isDead() {
        return health <= 0;
    }

    /**
     * returns the current health of the enemy
     *
     * @return health of enemy
     */
    @Override
    public int getHealth() {
        return health;
    }

    /**
     * returns the max health of the enemy
     *
     * @return max health of enemy
     */
    @Override
    public int getMaxHealth() {
        return maxHealth;
    }

    /**
     * applies a poison effect to the enemy. If the enemy is already poisoned, the poison effect will be refreshed (duration reset).
     *
     * @param poison the poison effect to apply to the enemy, gets scaled to with the enemy resistance
     */
    public void applyPoisonEffect(PoisonEffectComponent poison) {
        if (getPoisonResistance() >= 1.0) {
            return;
        }
        if (poisonComponent.isPresent()) {
            poisonComponent.get().refresh();
        } else {
            int scaledDamage = (int) (poison.getDamagePerTick() * (1.0 - getPoisonResistance()));
            PoisonEffectComponent scaled = new PoisonEffectComponent(poison.getDuration(), poison.getInterval(), scaledDamage);
            poisonComponent = Optional.of(scaled);
        }
    }

    // ------------------------------------------------------
    // getters
    // ------------------------------------------------------

    /**
     * returns the score value awarded to the player when this enemy is killed
     *
     * @return score value of enemy
     */
    public int getScoreValue() {
        return scoreValue;
    }

    /**
     * returns the damage value done to the player when this enemy reaches the end of its path
     *
     * @return damage value of enemy
     */
    public int getDamage() {
        return damage;
    }

    /**
     * returns the path that the enemy follows.
     * unmodifiable so that external code cannot change the enemy's path directly, which could cause bugs in movement logic.
     *
     * @return list of Vector2D points
     */
    public List<Vector2D> getPath() {
        return Collections.unmodifiableList(path);
    }

    /**
     * returns the index of the current waypoint the enemy is moving towards
     *
     * @return index of current waypoint
     */
    public int getCurrentWaypointIndex() {
        return currentWaypointIndex;
    }

    /**
     * returns whether the enemy is currently flashing from being hit
     *
     * @return true if the enemy is currently flashing, false otherwise
     */
    public boolean isHitFlashing() {
        return hitFlashTimer > 0.0;
    }

    /**
     * returns whether the enemy is currently poisoned
     *
     * @return true if the enemy is currently poisoned, false otherwise
     */
    public boolean isPoisoned() {
        return poisonComponent.isPresent();
    }

    /**
     * returns resistance to poison damage as a value between 0.0 and 1.0, where 0.0 means no resistance (full poison damage) and 1.0 means full resistance (no poison damage).
     * This can be overridden by subclasses to give certain enemy types resistance to poison.
     *
     * @return the poison resistance of the enemy, between 0.0 and 1.0
     */
    public double getPoisonResistance() {
        return 0.0;
    }

    /**
     * returns the progress of the enemy along its path as a value between 0.0 and the number of waypoints in the path.
     * @return the progress of the enemy along its path
     */
    public double getPathProgress() {
        return getCurrentWaypointIndex() -
                getPosition().distanceTo(getPath().get(
                        Math.min(getCurrentWaypointIndex(), getPath().size() - 1)
                ));
    }

    // ---------------------------------------
    // debug
    // ---------------------------------------

    /**
     * gives string representation of enemy
     *
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
