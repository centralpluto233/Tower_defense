package towerdefence.game.entities.tower;

import towerdefence.game.interfaces.ProjectileFactory;

import static towerdefence.system.config.Constants.TowerConstants.Archer.*;
import static towerdefence.system.config.Constants.ProjectileConstants.*;

public abstract class ArcherTower extends Tower{
    public ArcherTower(double x, double y, ProjectileFactory projectileFactory) {
        super(x,y,WIDTH,HEIGHT,RANGE,DAMAGE,COOLDOWN,COST,Archer.SPEED, projectileFactory);
    }
}
