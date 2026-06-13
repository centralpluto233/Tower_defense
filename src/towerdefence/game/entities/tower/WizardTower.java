package towerdefence.game.entities.tower;

import towerdefence.game.interfaces.ProjectileFactory;

import static towerdefence.system.config.Constants.ProjectileConstants.Poison;
import static towerdefence.system.config.Constants.TowerConstants.Wizard.*;

public abstract class WizardTower extends Tower {
    /**
     * Creates a new Wizard tower with the given position and projectile factory.
     * The Wizard tower has predefined width, height, range, damage, cooldown, cost, and projectile speed as defined in the TowerConstants and ProjectileConstants classes.
     *
     * @param x                 x part of the position to spawn the Wizard tower
     * @param y                 y part of the position to spawn the Wizard tower
     * @param projectileFactory the factory used to create projectiles when the Wizard tower attacks. This allows for flexibility in defining different types of projectiles that the Wizard tower can use, such as standard arrows or special arrows with unique effects.
     */
    public WizardTower(double x, double y, ProjectileFactory projectileFactory) {
        super(x, y, WIDTH, HEIGHT, RANGE, DAMAGE, COOLDOWN, COST, Poison.SPEED, projectileFactory);
    }
}
