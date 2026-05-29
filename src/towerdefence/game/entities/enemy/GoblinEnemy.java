package towerdefence.game.entities.enemy;

import towerdefence.util.Vector2D;
import static towerdefence.system.config.Constants.EnemyConstants.Goblin.*;

import java.util.List;

public abstract class GoblinEnemy extends EnemyEntity{
    /**
     * Creates a new GoblinEnemy with the given position and path.
     * The GoblinEnemy has predefined width, height, speed, health, score value, and base damage as defined in the EnemyConstants class.
     * @param x x part of the position to spawn the GoblinEnemy
     * @param y y part of the position to spawn the GoblinEnemy
     * @param path the path the GoblinEnemy will follow
     */
    public GoblinEnemy(double x, double y, List<Vector2D> path) {
        super(x,y,WIDTH,HEIGHT,SPEED,HEALTH,SCORE_VALUE,BASE_DAMAGE,path);
    }
}
