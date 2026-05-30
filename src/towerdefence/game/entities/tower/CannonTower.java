package towerdefence.game.entities.tower;

import towerdefence.game.interfaces.ProjectileFactory;

import static towerdefence.system.config.Constants.ProjectileConstants.CannonBall;
import static towerdefence.system.config.Constants.TowerConstants.Cannon.*;

public abstract class CannonTower extends Tower {
    /**
     * Creates a new CannonTower with the given position and projectile factory.
     * The CannonTower has predefined width, height, range, damage, cooldown, cost, and projectile speed as defined in the TowerConstants and ProjectileConstants classes.
     *
     * @param x                 x part of the position to spawn the CannonTower
     * @param y                 y part of the position to spawn the CannonTower
     * @param projectileFactory the factory used to create projectiles when the CannonTower attacks. This allows for flexibility in defining different types of projectiles that the CannonTower can use, such as standard arrows or special arrows with unique effects.
     */
    public CannonTower(double x, double y, ProjectileFactory projectileFactory) {
        super(x, y, WIDTH, HEIGHT, RANGE, DAMAGE, COOLDOWN, COST, CannonBall.SPEED, projectileFactory);
    }
}
