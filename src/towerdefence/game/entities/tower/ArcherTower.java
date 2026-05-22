package towerdefence.game.entities.tower;

import towerdefence.game.interfaces.ProjectileFactory;

import static towerdefence.system.config.Constants.TowerConstants.Archer.*;

public abstract class ArcherTower extends Tower{
    public ArcherTower(double x, double y, ProjectileFactory projectileFactory) {
        super(x,y,WIDTH,HEIGHT,RANGE,DAMAGE,COOLDOWN,COST, projectileFactory);
    }
}
