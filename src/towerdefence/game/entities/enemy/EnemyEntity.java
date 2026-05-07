package towerdefence.game.entities.enemy;

import towerdefence.game.entities.MovingEntity;
import towerdefence.game.interfaces.Damageable;
import towerdefence.util.Vector2D;
import towerdefence.system.logging.GameLogger;

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

    /**
     * creates an EnemyEntity
     * @param x starting x position
     * @param y starting y position
     * @param width width of the entities
     * @param height height of the entities
     * @param speed speed in game units per second
     * @param health starting and max health
     * @param scoreValue score when killed
     * @param damage damage dealt to base wen reaching end
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
        if (isDead()) {
            markForRemoval();
            return;
        }
        followPath(dt);
    }

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

    public int getScoreValue() {
        return scoreValue;
    }

    public int getDamage() {
        return damage;
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
