package towerdefence.game.entities.enemy;

import towerdefence.util.Vector2D;

import java.util.List;

public abstract class BatEnemy extends EnemyEntity{
    private static final double WIDTH = 10.0*2;
    private static final double HEIGHT = 7.0*2;
    private static final double SPEED = 50.0;
    private static final int HEALTH = 10;
    private static final int SCORE_VALUE = 5;
    private static final int BASE_DAMAGE = 1;

    public BatEnemy(double x, double y, List<Vector2D> path) {
        super(x,y,WIDTH,HEIGHT,SPEED,HEALTH,SCORE_VALUE,BASE_DAMAGE,path);
    }
}
