package towerdefence.game;

import towerdefence.game.entity.enemy.EnemyEntity;
import towerdefence.game.entity.enemy.GoblinEnemy;
import towerdefence.game.entity.projectile.HomingProjectile;
import towerdefence.game.entity.tower.BasicTower;

/**
 * Abstract factory for creating game entities
 * Subclasses provide visuals
 */
public abstract class AbstractGameFactory {
    // Enemies
    public abstract GoblinEnemy createGoblin(double x, double y);

    // Towers
    public abstract BasicTower createBasicTower(double x, double y);

    // Projectiles
    public abstract HomingProjectile createHomingProjectile(double x, double y, int damage, EnemyEntity target);
}
