package towerdefence.game.entity.tower;

import towerdefence.game.entity.Entity;
import towerdefence.game.entity.enemy.EnemyEntity;
import towerdefence.game.interfaces.Attacker;
import towerdefence.game.interfaces.Damageable;
import towerdefence.system.logging.GameLogger;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public abstract class Tower extends Entity implements Attacker {
    private static final Logger logger = GameLogger.getLogger();

    private final double range;
    private final int damage;
    private final double cooldown;
    private double timeSinceLastAttack;

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
    public Tower(double x, double y, double width, double height, double range, int damage, double cooldown) {
        super(x, y, width, height);
        this.range = range;
        this.damage = damage;
        this.cooldown = cooldown;
        this.timeSinceLastAttack= cooldown;
    }

    protected void attack(Damageable target) {
        target.takeDamage(damage);
        timeSinceLastAttack=0;
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

    public void updateCombat(List<EnemyEntity> enemies) {
        if (canAttack()) {
            logger.finest("Attacking");
            Optional<EnemyEntity> target = findTargetInRange(enemies);
            if (target.isPresent()) {
                logger.finest("Target found"+target);
            }
            target.ifPresent(this::attack);
        }
    }

    private Optional<EnemyEntity> findTargetInRange(List<EnemyEntity> enemies) {
        double towerX = getX();
        double towerY = getY();
        double rangeSq = range*range;

        return enemies.stream()
                .filter(enemy -> {
                    double dx = enemy.getX() - towerX;
                    double dy = enemy.getY() - towerY;
                    return dx * dx + dy * dy <= rangeSq;
                }).findFirst();
    }

    @Override
    public String toString() {
        return super.toString()
                + ", range=" + range
                + ", damage=" + damage
                + ", cooldown=" + cooldown
                + ", timeSinceLastAttack=" + timeSinceLastAttack;
    }
    
}
