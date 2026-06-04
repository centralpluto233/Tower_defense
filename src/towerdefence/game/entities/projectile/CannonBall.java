package towerdefence.game.entities.projectile;

import towerdefence.game.ecs.components.SplashComponent;
import towerdefence.game.entities.Entity;
import towerdefence.util.Vector2D;

import static towerdefence.system.config.Constants.ProjectileConstants.CannonBall.*;
import static towerdefence.system.config.Constants.TowerConstants.Cannon.SPLASHRADIUS;

public abstract class CannonBall extends Projectile {
    private final Vector2D start;
    private final double duration;
    private double progress = 0;

    /**
     * Updates the position of the CannonBall along a curved arc towards the target position.
     *
     * @param deltaTime the time elapsed since the last update
     */
    @Override
    public void update(double deltaTime) {
        progress += deltaTime / duration;
        progress = Math.min(progress, 1.0);

        Vector2D currentPos = getPositionOnArc(progress);
        Vector2D nextPos = getPositionOnArc(Math.min(progress + 0.01, 1.0));

        setX(currentPos.x());
        setY(currentPos.y());

        setVelocity(nextPos.subtract(currentPos).normalize());
    }

    /**
     * Calculates the position of the CannonBall along a curved arc based on the progress towards the target position.
     *
     * @param p the progress towards the target position (0.0 to 1.0)
     * @return the position of the CannonBall at the given progress along the arc
     */
    private Vector2D getPositionOnArc(double p) {
        double x = start.x() + (targetPos.x() - start.x()) * p;
        double y = start.y() + (targetPos.y() - start.y()) * p;
        double arc = Math.sin(p * Math.PI) * ARC_HEIGHT;
        return new Vector2D(x, y - arc);
    }

    /**
     * Creates a new CannonBall with the given position, damage, and target position.
     * The CannonBall will travel in a curved arc towards the target position.
     *
     * @param x              x part of the position to spawn the CannonBall
     * @param y              y part of the position to spawn the CannonBall
     * @param damage         the damage this CannonBall will deal to an enemy upon impact
     * @param targetPosition the position that the CannonBall will travel towards
     */
    public CannonBall(double x, double y, int damage, Vector2D targetPosition) {
        super(x, y, WIDTH, HEIGHT, SPEED, damage, targetPosition);
        this.start = new Vector2D(x, y);
        this.duration = start.distanceTo(targetPosition) / SPEED;
        setSplashComponent(new SplashComponent(SPLASHRADIUS));
    }

    @Override
    public boolean overlaps(Entity enemy) {
        return false;
    }

    @Override
    public boolean hasReachedTarget() {
        return progress >= 1.0;
    }
}
