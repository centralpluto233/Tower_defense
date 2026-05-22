package towerdefence.game.entities.projectile;

import towerdefence.util.Vector2D;
import static towerdefence.system.config.Constants.ProjectileConstants.Crossbow.*;

public abstract class CrossBowArrow extends Projectile {


    public CrossBowArrow(double x, double y, int damage, Vector2D targetPosition) {
        super(x, y, WIDTH, HEIGHT, SPEED, damage,targetPosition);
    }
}
