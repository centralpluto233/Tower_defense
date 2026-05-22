package towerdefence.game.entities.tower;

import static towerdefence.system.config.Constants.TowerConstants.Archer.*;

public abstract class ArcherTower extends Tower{
    public ArcherTower(double x, double y) {
        super(x,y,WIDTH,HEIGHT,RANGE,DAMAGE,COOLDOWN,COST);
    }
}
