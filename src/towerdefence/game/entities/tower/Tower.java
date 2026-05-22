package towerdefence.game.entities.tower;

import towerdefence.game.AbstractGameFactory;
import towerdefence.game.Game;
import towerdefence.game.entities.Entity;
import towerdefence.game.entities.enemy.EnemyEntity;
import towerdefence.game.entities.projectile.CrossBowArrow;
import towerdefence.game.entities.projectile.Projectile;
import towerdefence.game.interfaces.Attacker;
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

    /**
     * constructs a tower
     * @param x x position
     * @param y y position
     * @param width hitbox width
     * @param height hitbox height
     * @param range range for attacks
     * @param damage damage dealt in attack
     * @param cooldown cooldown between attacks
     */
    public Tower(double x, double y, double width, double height, double range, int damage, double cooldown, int cost) {
        super(x, y, width, height);
        this.range = range;
        this.damage = damage;
        this.cooldown = cooldown;
        this.timeSinceLastAttack= cooldown;
        this.cost = cost;
    }

    @Override
    public double getRange() {
        return range;
    }

    @Override
    public int getDamage() {
        return damage;
    }

    protected boolean canAttack() {
        return timeSinceLastAttack >= cooldown;
    }

    @Override
    public void update(double deltaTime) {
        timeSinceLastAttack += deltaTime;
    }

    public void updateCombat(List<EnemyEntity> enemies, AbstractGameFactory factory, Game game) {
        if (canAttack()) {
            logger.finest("Attacking");
            Optional<EnemyEntity> target = findTargetInRange(enemies);
            if (target.isPresent()) {
                logger.fine("Target found " + target);

                Vector2D targetPosition = predictWaypointAim(target.get());
                Projectile projectile = factory.createHomingProjectile(
                        getSpawnPosition().x(),
                        getSpawnPosition().y(),
                        damage,
                        targetPosition
                );

                game.addEntity(projectile);
                timeSinceLastAttack = 0;
            }
        }
    }


    private Optional<EnemyEntity> findTargetInRange(List<EnemyEntity> enemies) {
        return enemies.stream()
                .filter(enemy -> getPosition().
                        distanceTo(enemy.getPosition())<= range).findFirst();
    }

    @Override
    public String toString() {
        return super.toString()
                + ", range=" + range
                + ", damage=" + damage
                + ", cooldown=" + cooldown
                + ", timeSinceLastAttack=" + timeSinceLastAttack;
    }

    public int get_cost(){
        return cost;
    }

    public abstract Vector2D getSpawnPosition();

    public Vector2D predictWaypointAim(EnemyEntity enemy) {

        double ex = enemy.getX();
        double ey = enemy.getY();
        int waypointIndex = enemy.getCurrentWaypointIndex();
        List<Vector2D> path = enemy.getPath();
        double enemySpeed = enemy.getSpeed();

        double timeStep = 0.05;
        double maxTime = 3.0;
        double elapsed = 0;

        while (elapsed < maxTime) {
            double remaining = enemySpeed * timeStep;
            while (remaining > 0 && waypointIndex < path.size()) {
                Vector2D wp = path.get(waypointIndex);
                double dx = wp.x() - ex;
                double dy = wp.y() - ey;
                double dist = Math.hypot(dx, dy);

                if (dist < 1e-9) {
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
            if (distToArrow <= CrossBowArrow.SPEED * elapsed) {
                return new Vector2D(ex, ey);
            }
        }

        return enemy.getPosition();
    }
    
}
