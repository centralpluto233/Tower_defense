package towerdefence.game.entities.enemy;

import towerdefence.util.Vector2D;
import static towerdefence.system.config.Constants.EnemyConstants.Bat.*;

import java.util.List;

public abstract class BatEnemy extends EnemyEntity{
    /**
     * Creates a new BatEnemy with the given position and path.
     * The BatEnemy has predefined width, height, speed, health, score value, and base damage as defined in the EnemyConstants class.
     * @param x x part of the position to spawn the BatEnemy
     * @param y y part of the position to spawn the BatEnemy
     * @param path the path the BatEnemy will follow
     */
    public BatEnemy(double x, double y, List<Vector2D> path) {
        super(x,y,WIDTH,HEIGHT,SPEED,HEALTH,SCORE_VALUE,BASE_DAMAGE,path);
    }
}
