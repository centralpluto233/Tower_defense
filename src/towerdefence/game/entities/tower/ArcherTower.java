package towerdefence.game.entities.tower;

public abstract class ArcherTower extends Tower{
    private static final double RANGE = 120.0;
    private static final int DAMAGE = 5;
    private static final double COOLDOWN = 0.7;
    private static final double WIDTH = 16.0*2;
    private static final double HEIGHT = 23.0*2;
    private static final int COST = 10;

    public ArcherTower(double x, double y) {
        super(x,y,WIDTH,HEIGHT,RANGE,DAMAGE,COOLDOWN,COST);
    }
}
