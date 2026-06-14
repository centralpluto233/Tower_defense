package towerdefence.game.interfaces;

import towerdefence.game.entities.projectile.Projectile;
import towerdefence.util.Vector2D;

// used to create different projectiles from different types of towers
public interface ProjectileFactory {
    /**
     * Creates a new Projectile with the specified position, damage, and target position.
     *
     * @param position       the position where the Projectile will be spawned
     * @param damage         the damage this Projectile will deal to an enemy upon impact
     * @param targetPosition the position that the Projectile will travel towards
     * @return a new Projectile instance with the given parameters
     */
    Projectile create(Vector2D position, int damage, Vector2D targetPosition);
}
