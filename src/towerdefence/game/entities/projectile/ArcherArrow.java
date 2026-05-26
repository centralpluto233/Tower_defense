package towerdefence.game.entities.projectile;

import towerdefence.util.Vector2D;
import static towerdefence.system.config.Constants.ProjectileConstants.Archer.*;

public abstract class ArcherArrow extends Projectile {
    private final Vector2D start;
    private final double duration;
    private double progress = 0;

    @Override
    public void update(double deltaTime) {
        progress += deltaTime / duration;
        progress = Math.min(progress, 1.0);

        Vector2D currentPos = getPositionOnArc(progress);
        Vector2D nextPos = getPositionOnArc(Math.min(progress + 0.01, 1.0));

        setX(currentPos.x());
        setY(currentPos.y());

        setVelocity(nextPos.subtract(currentPos).normalize());

        if (progress >= 1.0) {
            markForRemoval();
        }
    }

    private Vector2D getPositionOnArc(double p) {
        double x = start.x() + (targetPos.x() - start.x()) * p;
        double y = start.y() + (targetPos.y() - start.y()) * p;
        double arc = Math.sin(p * Math.PI) * ARC_HEIGHT;
        return new Vector2D(x, y - arc);
    }

    public ArcherArrow(double x, double y, int damage, Vector2D targetPosition) {
        super(x, y, WIDTH, HEIGHT, SPEED, damage,targetPosition);
        this.start=new Vector2D(x,y);
        this.duration = start.distanceTo(targetPosition) / SPEED;
    }
}
