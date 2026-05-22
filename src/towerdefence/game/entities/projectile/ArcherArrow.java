package towerdefence.game.entities.projectile;

import towerdefence.util.Vector2D;
import static towerdefence.system.config.Constants.ProjectileConstants.Archer.*;

public abstract class ArcherArrow extends Projectile {
    @Override
    public void update(double deltaTime) {

    }

    public ArcherArrow(double x, double y, int damage, Vector2D targetPosition) {
        super(x, y, WIDTH, HEIGHT, SPEED, damage,targetPosition);
    }
}
