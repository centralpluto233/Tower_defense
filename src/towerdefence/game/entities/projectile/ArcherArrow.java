package towerdefence.game.entities.projectile;

import towerdefence.util.Vector2D;

import static towerdefence.system.config.Constants.ProjectileConstants.Archer.*;

public abstract class ArcherArrow extends Projectile {
    private final Vector2D start;
    private final double duration;
    private double progress = 0;

    /**
     * Updates the position of the ArcherArrow along a curved arc towards the target position.
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
     * Calculates the position of the ArcherArrow along a curved arc based on the progress towards the target position.
     *
     * @param p the progress towards the target position (0.0 to 1.0)
     * @return the position of the ArcherArrow at the given progress along the arc
     */
    private Vector2D getPositionOnArc(double p) {
        double x = start.x() + (targetPos.x() - start.x()) * p;
        double y = start.y() + (targetPos.y() - start.y()) * p;
        double arc = Math.sin(p * Math.PI) * ARC_HEIGHT;
        return new Vector2D(x, y - arc);
    }

    /**
     * Creates a new ArcherArrow with the given position, damage, and target position.
     * The ArcherArrow will travel in a curved arc towards the target position.
     *
     * @param x              x part of the position to spawn the ArcherArrow
     * @param y              y part of the position to spawn the ArcherArrow
     * @param damage         the damage this ArcherArrow will deal to an enemy upon impact
     * @param targetPosition the position that the ArcherArrow will travel towards
     */
    public ArcherArrow(double x, double y, int damage, Vector2D targetPosition) {
        super(x, y, WIDTH, HEIGHT, SPEED, damage, targetPosition);
        this.start = new Vector2D(x, y);
        this.duration = start.distanceTo(targetPosition) / SPEED;
    }

    /**
     * Checks if the ArcherArrow has reached its target position. The ArcherArrow is considered to have reached the target when its progress towards the target is 100% (1.0).
     * @return true if the ArcherArrow has reached its target position, false otherwise
     */
    @Override
    public boolean hasReachedTarget() {
        return progress >= 1.0;
    }
}
