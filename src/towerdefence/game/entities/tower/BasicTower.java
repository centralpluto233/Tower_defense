package towerdefence.game.entities.tower;

public abstract class BasicTower extends Tower{
    private static final double RANGE = 120.0;
    private static final int DAMAGE = 5;
    private static final double COOLDOWN = 0.5;
    private static final double WIDTH = 18.0*2;
    private static final double HEIGHT = 33.0*2;
    private static final int COST = 20;

    public BasicTower(double x, double y) {
        super(x,y,WIDTH,HEIGHT,RANGE,DAMAGE,COOLDOWN,COST);
    }
}
