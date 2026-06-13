package towerdefence.game.entities.projectile;

import towerdefence.game.ecs.components.PoisonEffectComponent;
import towerdefence.game.entities.enemy.EnemyEntity;
import towerdefence.util.Vector2D;

import static towerdefence.system.config.Constants.ProjectileConstants.Poison.*;

public abstract class PoisonProjectile extends Projectile {
    private final double poisonDuration;
    private final double poisonInterval;
    private final int poisonDamage;

    public PoisonProjectile(double x, double y, int damage, Vector2D targetPosition) {
        super(x, y, WIDTH, HEIGHT, SPEED, damage, targetPosition);
        this.poisonDuration = POISON_DURATION;
        this.poisonInterval = POISON_INTERVAL;
        this.poisonDamage = POISON_DAMAGE;
    }

    /**
     * Deals damage to the given EnemyEntity and applies a poison effect that damages the enemy over time.
     * @param enemy the EnemyEntity to deal damage to
     */
    @Override
    public void dealDamage(EnemyEntity enemy) {
        super.dealDamage(enemy);
        enemy.applyPoisonEffect(new PoisonEffectComponent(poisonDuration, poisonInterval, poisonDamage));
    }
}
