package towerdefence.game.entities.tower;

import towerdefence.game.interfaces.ProjectileFactory;

import static towerdefence.system.config.Constants.ProjectileConstants.Archer;
import static towerdefence.system.config.Constants.TowerConstants.Archer.*;

public abstract class ArcherTower extends Tower {
    /**
     * Creates a new ArcherTower with the given position and projectile factory.
     * The ArcherTower has predefined width, height, range, damage, cooldown, cost, and projectile speed as defined in the TowerConstants and ProjectileConstants classes.
     *
     * @param x                 x part of the position to spawn the ArcherTower
     * @param y                 y part of the position to spawn the ArcherTower
     * @param projectileFactory the factory used to create projectiles when the ArcherTower attacks. This allows for flexibility in defining different types of projectiles that the ArcherTower can use, such as standard arrows or special arrows with unique effects.
     */
    public ArcherTower(double x, double y, ProjectileFactory projectileFactory) {
        super(x, y, WIDTH, HEIGHT, RANGE, DAMAGE, COOLDOWN, COST, Archer.SPEED, projectileFactory);
    }
}
