package towerdefence.game.entity.projectile;

import towerdefence.game.entity.enemy.EnemyEntity;

public abstract class HomingProjectile extends Projectile {
    private static final double HEIGHT=5.0;
    private static final double WIDTH=5.0;
    private static final double SPEED=30.0;

    public HomingProjectile(double x, double y, int damage, EnemyEntity target) {
        super(x, y, WIDTH, HEIGHT, SPEED, damage, target);
    }
}
