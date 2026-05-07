package towerdefence.game;

import towerdefence.game.entities.enemy.EnemyEntity;
import towerdefence.game.entities.enemy.GoblinEnemy;
import towerdefence.game.entities.projectile.HomingProjectile;
import towerdefence.game.entities.tower.BasicTower;
import towerdefence.game.interfaces.GameView;
import towerdefence.game.interfaces.InputSource;
import towerdefence.system.config.Config;
import towerdefence.util.Vector2D;

import java.util.List;

/**
 * Abstract factory for creating game entities
 * Subclasses provide visuals
 */
public abstract class AbstractGameFactory {
    public abstract GameView createGameView(Game game, Config config);
    public abstract InputSource createInputSource(Game game, Config config, GameView gameView);
    // Enemies
    public abstract GoblinEnemy createGoblin(double x, double y, List<Vector2D> path);

    // Towers
    public abstract BasicTower createBasicTower(double x, double y);

    // Projectiles
    public abstract HomingProjectile createHomingProjectile(double x, double y, int damage, EnemyEntity target);
}