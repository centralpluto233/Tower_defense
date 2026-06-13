package towerdefence.game.entities.enemy;

import towerdefence.util.Vector2D;

import java.util.List;

import static towerdefence.system.config.Constants.EnemyConstants.Skeleton.*;

public abstract class SkeletonEnemy extends EnemyEntity {
    /**
     * Creates a new Skeleton with the given position and path.
     * The Skeleton has predefined width, height, speed, health, score value, and base damage as defined in the EnemyConstants class.
     *
     * @param x    x part of the position to spawn the skeleton
     * @param y    y part of the position to spawn the skeleton
     * @param path the path the skeleton will follow
     */
    public SkeletonEnemy(double x, double y, List<Vector2D> path) {
        super(x, y, WIDTH, HEIGHT, SPEED, HEALTH, SCORE_VALUE, BASE_DAMAGE, path);
    }

    @Override
    public double getPoisonResistance() {
        return 1.0;
    }
}