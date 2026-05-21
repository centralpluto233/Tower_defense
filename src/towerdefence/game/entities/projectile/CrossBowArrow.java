package towerdefence.game.entities.projectile;

import towerdefence.game.entities.enemy.EnemyEntity;
import towerdefence.util.Vector2D;

public abstract class CrossBowArrow extends Projectile {
    private static final double HEIGHT=6.0*0.8;
    private static final double WIDTH=13.0*0.8;
    public static final double SPEED=90.0;

    public CrossBowArrow(double x, double y, int damage, EnemyEntity target, Vector2D targetPosition) {
        super(x, y, WIDTH, HEIGHT, SPEED, damage, target, targetPosition);
    }
}
