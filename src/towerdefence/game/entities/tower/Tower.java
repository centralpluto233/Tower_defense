package towerdefence.game.entities.tower;

import towerdefence.game.Game;
import towerdefence.game.entities.Entity;
import towerdefence.game.entities.enemy.EnemyEntity;
import towerdefence.game.entities.projectile.Projectile;
import towerdefence.game.interfaces.Attacker;
import towerdefence.game.interfaces.ProjectileFactory;
import towerdefence.system.config.Constants;
import towerdefence.system.logging.GameLogger;
import towerdefence.util.Vector2D;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public abstract class Tower extends Entity implements Attacker {
    private static final Logger logger = GameLogger.getLogger();

    private final double range;
    private final int damage;
    private final double cooldown;
    private double timeSinceLastAttack;
    private final int cost;
    private final ProjectileFactory projectileFactory;
    private final double projectileSpeed;

    /**
     * Creates a new Tower with the given parameters.
     *
     * @param x                 x part of the position to spawn the Tower
     * @param y                 y part of the position to spawn the Tower
     * @param width             width of the Tower
     * @param height            height of the Tower
     * @param range             the maximum distance at which the Tower can attack enemies.
     *                          This determines how far the Tower can reach to target and attack enemies on the game map.
     * @param damage            the amount of damage this Tower will deal to an enemy upon a successful attack.
     *                          This value is used to reduce the health of the targeted enemy when hit by a projectile fired from this Tower.
     * @param cooldown          the time (in seconds) that must pass between consecutive attacks by this Tower.
     *                          After the Tower attacks, it must wait for this cooldown period before it can attack
     * @param cost              the amount of resources required to build this Tower.
     *                          This value is used in the game's resource management system to determine if the player can afford to place this Tower on the game map.
     * @param projectileSpeed   the speed at which projectiles fired from this Tower will travel.
     *                          This value is used to calculate the trajectory and timing of projectiles as they move towards their target enemies.
     * @param projectileFactory the factory used to create projectiles when the Tower attacks. This allows for flexibility in defining different types of projectiles that the Tower can use, such as standard arrows or special arrows with unique effects.
     */
    public Tower(double x, double y, double width, double height, double range, int damage, double cooldown, int cost, double projectileSpeed, ProjectileFactory projectileFactory) {
        super(x, y, width, height);
        this.range = range;
        this.damage = damage;
        this.cooldown = cooldown;
        this.timeSinceLastAttack = cooldown;
        this.cost = cost;
        this.projectileFactory = projectileFactory;
        this.projectileSpeed = projectileSpeed;
    }

    /**
     * Gets the attack range of the Tower. This is the maximum distance at which the Tower can target and attack enemies on the game map.
     *
     * @return the attack range of the Tower
     */
    @Override
    public double getRange() {
        return range;
    }

    /**
     * Gets the damage value of the Tower. This is the amount of damage this Tower will deal to an enemy upon a successful attack.
     *
     * @return the damage value of the Tower
     */
    @Override
    public int getDamage() {
        return damage;
    }

    /**
     * Gets the cooldown time of the Tower. This is the time (in seconds) that must pass between consecutive attacks by this Tower.
     *
     * @return the cooldown time of the Tower
     */
    protected boolean canAttack() {
        return timeSinceLastAttack >= cooldown;
    }

    /**
     * Updates the Tower's internal state each game tick. This method is responsible for updating the time since the last attack, which is used to determine if the Tower can attack again based on its cooldown.
     *
     * @param deltaTime the time elapsed since the last update
     */
    @Override
    public void update(double deltaTime) {
        timeSinceLastAttack += deltaTime;
    }

    /**
     * Handles the combat logic for the Tower. This method checks if the Tower can attack based on its cooldown, finds a target enemy within range, predicts the target's future position, creates a projectile aimed at that position, and adds the projectile to the game.
     *
     * @param enemies the list of enemy entities currently in the game. The Tower will search through this list to find a target within its attack range.
     * @param game    the game instance, which is used to add the created projectile to the game world. This allows the projectile to be updated and rendered as part of the game's entity management system.
     */
    public void updateCombat(List<EnemyEntity> enemies, Game game) {
        if (canAttack()) {
            logger.finest("Attacking");
            Optional<EnemyEntity> target = findTargetInRange(enemies);
            if (target.isPresent()) {
                logger.fine("Target found " + target);

                Vector2D targetPosition = predictWaypointAim(target.get());
                Projectile projectile = projectileFactory.create(
                        getSpawnPosition(),
                        damage,
                        targetPosition
                );
                logger.finest("Projectile: " + projectile);

                game.addEntity(projectile);
                timeSinceLastAttack = 0;
            }
        }
    }


    /**
     * Finds the first enemy within the Tower's attack range from the provided list of enemies.
     * The method checks each enemy's distance from the Tower and returns the first one that is within the specified range.
     *
     * @param enemies the list of enemy entities to search through for a target within range.
     *                The Tower will iterate through this list to find an enemy that is close enough to attack based on the Tower's range attribute.
     * @return an Optional containing the first EnemyEntity found within range, or an empty Optional if no enemies are within range.
     */
    private Optional<EnemyEntity> findTargetInRange(List<EnemyEntity> enemies) {
        return enemies.stream()
                .filter(enemy -> getPosition().
                        distanceTo(enemy.getPosition()) <= range).findFirst();
    }

    /**
     * Returns a string representation of the Tower, including its position, size, range, damage, cooldown, and time since last attack.
     *
     * @return a string representation of the Tower with its attributes for debugging and logging purposes
     */
    @Override
    public String toString() {
        return super.toString()
                + ", range=" + range
                + ", damage=" + damage
                + ", cooldown=" + cooldown
                + ", timeSinceLastAttack=" + timeSinceLastAttack;
    }

    /**
     * Gets the cost of the Tower. This is the amount of resources required to build this Tower, and is used in the game's resource management system to determine if the player can afford to place this Tower on the game map.
     *
     * @return the cost of the Tower
     */
    public int get_cost() {
        return cost;
    }

    /**
     * Gets the spawn position of the projectile, this is dependent of the graphics that are used; therefore abstract
     *
     * @return the position where the projectile should be spawned when the Tower attacks. This is typically the position of the Tower itself, but may be adjusted based on the Tower's design and graphics to ensure that projectiles appear to originate from the correct location on the Tower.
     */
    public abstract Vector2D getSpawnPosition();

    /**
     * Predicts the future position of the target enemy based on its current position, speed, and path.
     * This method simulates the enemy's movement along its path for a short duration to estimate where the enemy will be when the projectile reaches it, allowing the Tower to aim ahead of the enemy's current position for a more accurate attack.
     *
     * @param enemy the EnemyEntity for which to predict the future position. The method uses the enemy's current position, speed, and path to simulate its movement and estimate where it will be in the near future, which is crucial for aiming projectiles effectively in a tower defense game.
     * @return a Vector2D representing the predicted future position of the target enemy, which the Tower will aim at when firing a projectile. If the prediction fails or the enemy is expected to be out of range, it may return the enemy's current position as a fallback.
     */
    public Vector2D predictWaypointAim(EnemyEntity enemy) {

        double ex = enemy.getX();
        double ey = enemy.getY();
        int waypointIndex = enemy.getCurrentWaypointIndex();
        List<Vector2D> path = enemy.getPath();
        double enemySpeed = enemy.getSpeed();

        double timeStep = Constants.Timing.TOWER_AIM_STEP_SECONDS;
        double maxTime = Constants.Timing.TOWER_AIM_MAX_LOOKAHEAD_SECONDS;
        double elapsed = 0;

        while (elapsed < maxTime) {
            double remaining = enemySpeed * timeStep;
            while (remaining > 0 && waypointIndex < path.size()) {
                Vector2D wp = path.get(waypointIndex);
                double dx = wp.x() - ex;
                double dy = wp.y() - ey;
                double dist = Math.hypot(dx, dy);

                if (dist < Constants.Gameplay.PATH_POINT_EPSILON) {
                    waypointIndex++;
                    continue;
                }

                if (remaining >= dist) {
                    ex = wp.x();
                    ey = wp.y();
                    remaining -= dist;
                    waypointIndex++;
                } else {
                    double t = remaining / dist;
                    ex += dx * t;
                    ey += dy * t;
                    remaining = 0;
                }
            }

            elapsed += timeStep;

            double distToArrow = Math.hypot(ex - this.getX(), ey - this.getY());
            if (distToArrow <= projectileSpeed * elapsed) {
                return new Vector2D(ex, ey);
            }
        }

        return enemy.getPosition();
    }

}
