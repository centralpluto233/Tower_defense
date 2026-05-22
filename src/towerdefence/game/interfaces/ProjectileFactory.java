package towerdefence.game.interfaces;

import towerdefence.game.entities.projectile.Projectile;
import towerdefence.util.Vector2D;

public interface ProjectileFactory {
    Projectile create(Vector2D position, int damage, Vector2D targetPosition);
}
