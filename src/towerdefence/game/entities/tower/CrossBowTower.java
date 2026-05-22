package towerdefence.game.entities.tower;

public abstract class CrossBowTower extends Tower{
    private static final double RANGE = 150.0;
    private static final int DAMAGE = 10;
    private static final double COOLDOWN = 0.5;
    private static final double WIDTH = 18.0*2;
    private static final double HEIGHT = 33.0*2;
    private static final int COST = 20;

    public CrossBowTower(double x, double y) {
        super(x,y,WIDTH,HEIGHT,RANGE,DAMAGE,COOLDOWN,COST);
    }
}
