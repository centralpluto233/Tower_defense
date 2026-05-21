package towerdefence.game.entities.projectile;

import towerdefence.game.entities.enemy.EnemyEntity;

public abstract class CrossBowArrow extends Projectile {
    private static final double HEIGHT=6.0*0.8;
    private static final double WIDTH=13.0*0.8;
    private static final double SPEED=90.0;

    public CrossBowArrow(double x, double y, int damage, EnemyEntity target) {
        super(x, y, WIDTH, HEIGHT, SPEED, damage, target);
    }
}
