package towerdefence.game.entities.enemy;

import towerdefence.util.Vector2D;

import java.util.List;

import static towerdefence.system.config.Constants.EnemyConstants.BigSlime.*;

public abstract class BigSlime extends EnemyEntity {
    /**
     * Creates a new BigSlime with the given position and path.
     * The BigSlime has predefined width, height, speed, health, score value, and base damage as defined in the EnemyConstants class.
     *
     * @param x    x part of the position to spawn the BigSlime
     * @param y    y part of the position to spawn the BigSlime
     * @param path the path the BigSlime will follow
     */
    public BigSlime(double x, double y, List<Vector2D> path) {
        super(x, y, WIDTH, HEIGHT, SPEED, HEALTH, SCORE_VALUE, BASE_DAMAGE, path);
    }
}