package towerdefence.game.entity.enemy;

import towerdefence.game.util.Vector2D;

import java.util.List;

public abstract class GoblinEnemy extends EnemyEntity{
    private static final double WIDTH = 20.0;
    private static final double HEIGHT = 20.0;
    private static final double SPEED = 20.0;
    private static final int HEALTH = 30;
    private static final int SCORE_VALUE = 10;
    private static final int BASE_DAMAGE = 1;

    public GoblinEnemy(double x, double y, List<Vector2D> path) {
        super(x,y,WIDTH,HEIGHT,SPEED,HEALTH,SCORE_VALUE,BASE_DAMAGE,path);
    }
}
