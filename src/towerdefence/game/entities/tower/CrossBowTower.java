package towerdefence.game.entities.tower;

import towerdefence.game.interfaces.ProjectileFactory;

import static towerdefence.system.config.Constants.ProjectileConstants.Crossbow;
import static towerdefence.system.config.Constants.TowerConstants.CrossBow.*;

public abstract class CrossBowTower extends Tower {
    /**
     * Creates a new CrossBowTower with the given position and projectile factory.
     * The CrossBowTower has predefined width, height, range, damage, cooldown, cost, and projectile speed as defined in the TowerConstants and ProjectileConstants classes.
     *
     * @param x                 x part of the position to spawn the CrossBowTower
     * @param y                 y part of the position to spawn the CrossBowTower
     * @param projectileFactory the factory used to create projectiles when the CrossBowTower attacks. This allows for flexibility in defining different types of projectiles that the CrossBowTower can use, such as standard crossbow bolts or special bolts with unique effects.
     */
    public CrossBowTower(double x, double y, ProjectileFactory projectileFactory) {
        super(x, y, WIDTH, HEIGHT, RANGE, DAMAGE,COOLDOWN, COST, Crossbow.SPEED, projectileFactory);
    }
}
