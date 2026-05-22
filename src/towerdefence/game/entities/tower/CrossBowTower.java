package towerdefence.game.entities.tower;

import static towerdefence.system.config.Constants.TowerConstants.CrossBow.*;
import towerdefence.game.interfaces.ProjectileFactory;

public abstract class CrossBowTower extends Tower{
    public CrossBowTower(double x, double y, ProjectileFactory projectileFactory) {
        super(x,y,WIDTH,HEIGHT,RANGE,DAMAGE,COOLDOWN,COST,projectileFactory);
    }
}
