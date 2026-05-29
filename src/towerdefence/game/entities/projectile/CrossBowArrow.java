package towerdefence.game.entities.projectile;

import towerdefence.util.Vector2D;

import static towerdefence.system.config.Constants.ProjectileConstants.Crossbow.*;

public abstract class CrossBowArrow extends Projectile {
    /**
     * Creates a new CrossBowArrow with the given position, damage, and target position.
     * The CrossBowArrow will travel in a straight line towards the target position.
     *
     * @param x              x part of the position to spawn the CrossBowArrow
     * @param y              y part of the position to spawn the CrossBowArrow
     * @param damage         the damage this CrossBowArrow will deal to an enemy upon impact
     * @param targetPosition the position that the CrossBowArrow will travel towards
     */
    public CrossBowArrow(double x, double y, int damage, Vector2D targetPosition) {
        super(x, y, WIDTH, HEIGHT, SPEED, damage, targetPosition);
    }
}
