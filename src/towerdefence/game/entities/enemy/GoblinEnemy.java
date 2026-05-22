package towerdefence.game.entities.enemy;

import towerdefence.util.Vector2D;
import static towerdefence.system.config.Constants.EnemyConstants.Goblin.*;

import java.util.List;

public abstract class GoblinEnemy extends EnemyEntity{
    public GoblinEnemy(double x, double y, List<Vector2D> path) {
        super(x,y,WIDTH,HEIGHT,SPEED,HEALTH,SCORE_VALUE,BASE_DAMAGE,path);
    }
}
