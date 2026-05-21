package towerdefence.game.entities.projectile;

import towerdefence.game.entities.enemy.EnemyEntity;

public abstract class HomingProjectile extends Projectile {
    private static final double HEIGHT=6.0;
    private static final double WIDTH=13.0;
    private static final double SPEED=70.0;

    public HomingProjectile(double x, double y, int damage, EnemyEntity target) {
        super(x, y, WIDTH, HEIGHT, SPEED, damage, target);
    }
}
