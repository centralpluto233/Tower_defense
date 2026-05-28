package towerdefence.game;

import towerdefence.game.entities.enemy.BatEnemy;
import towerdefence.game.entities.enemy.GoblinEnemy;
import towerdefence.game.entities.projectile.ArcherArrow;
import towerdefence.game.entities.projectile.CrossBowArrow;
import towerdefence.game.entities.tower.ArcherTower;
import towerdefence.game.entities.tower.CrossBowTower;
import towerdefence.game.entities.tower.Tower;
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
    public abstract BatEnemy createBat(double x, double y, List<Vector2D> path);

    // Towers
    public abstract CrossBowTower createCrossBowTower(double x, double y);
    public abstract ArcherTower createArcherTower(double x, double y);
    public Tower createTower(String type, Vector2D spot) {
        return switch (type) {
            case "Crossbow" -> createCrossBowTower(spot.x(), spot.y());
            case "Archer" -> createArcherTower(spot.x(), spot.y());
            default -> throw new IllegalArgumentException("Unknown tower type: " + type);
        };
    }

    // Projectiles
    protected abstract CrossBowArrow createCrossBowArrow(double x, double y, int damage, Vector2D targetPosition);
    protected abstract ArcherArrow createArcherArrow(double x, double y, int damage, Vector2D targetPosition);
}