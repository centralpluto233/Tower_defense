package towerdefence.game.entities.tower;

import static towerdefence.system.config.Constants.TowerConstants.CrossBow.*;

public abstract class CrossBowTower extends Tower{
    public CrossBowTower(double x, double y) {
        super(x,y,WIDTH,HEIGHT,RANGE,DAMAGE,COOLDOWN,COST);
    }
}
