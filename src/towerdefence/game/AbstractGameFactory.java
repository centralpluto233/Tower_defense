package towerdefence.game;

import towerdefence.game.entities.enemy.BatEnemy;
import towerdefence.game.entities.enemy.BigSlime;
import towerdefence.game.entities.enemy.GoblinEnemy;
import towerdefence.game.entities.projectile.ArcherArrow;
import towerdefence.game.entities.projectile.CannonBall;
import towerdefence.game.entities.projectile.CrossBowArrow;
import towerdefence.game.entities.projectile.PoisonProjectile;
import towerdefence.game.entities.tower.*;
import towerdefence.game.interfaces.GameView;
import towerdefence.game.interfaces.InputSource;
import towerdefence.system.config.Config;
import towerdefence.util.Vector2D;

import java.util.List;

public abstract class AbstractGameFactory {
    /**
     * Factory method to create a GameView instance based on the provided Game and Config objects.
     * This method is abstract and must be implemented by subclasses to return a concrete GameView implementation.
     *
     * @param game   the Game instance for which the GameView is being created
     * @param config the Config object containing game configuration settings that may influence the GameView creation (e.g., window size, rendering options)
     * @return a new GameView instance that is appropriate for the given Game and Config
     */
    public abstract GameView createGameView(Game game, Config config);

    /**
     * Factory method to create an InputSource instance based on the provided Game, Config, and GameView objects.
     * This method is abstract and must be implemented by subclasses to return a concrete InputSource implementation
     *
     * @param game     the Game instance for which the InputSource is being created, allowing the InputSource to interact with the game state and logic
     * @param config   the Config object containing game configuration settings that may influence the InputSource creation (e.g., input mappings, control schemes)
     * @param gameView the GameView instance that the InputSource may need to interact with for rendering-related input handling (e.g., mouse coordinates, screen dimensions)
     * @return a new InputSource instance that is appropriate for the given Game, Config, and GameView
     */
    public abstract InputSource createInputSource(Game game, Config config, GameView gameView);

    // Enemies
    public abstract GoblinEnemy createGoblin(double x, double y, List<Vector2D> path);

    public abstract BatEnemy createBat(double x, double y, List<Vector2D> path);

    public abstract BigSlime createBigSlime(double x, double y, List<Vector2D> path);

    // Towers
    public abstract CrossBowTower createCrossBowTower(double x, double y);

    public abstract ArcherTower createArcherTower(double x, double y);

    public abstract CannonTower createCannonTower(double x, double y);

    public abstract WizardTower createWizardTower(double x, double y);

    /**
     * Factory method to create a tower based on the given type and position.
     * This method abstracts away the specific tower creation logic and allows for easy extension by simply adding new cases to the switch statement for additional tower types.
     *
     * @param type the type of tower to create (e.g., "Crossbow", "Archer")
     * @param spot the position where the tower should be created, represented as a Vector2D object containing x and y coordinates
     * @return a new Tower instance of the specified type at the given position
     */
    public Tower createTower(String type, Vector2D spot) throws Exception {
        return switch (type) {
            case "Crossbow" -> createCrossBowTower(spot.x(), spot.y());
            case "Archer" -> createArcherTower(spot.x(), spot.y());
            case "Cannon" -> createCannonTower(spot.x(), spot.y());
            case "Wizard" -> createWizardTower(spot.x(), spot.y());
            default -> throw new Exception("Unknown tower type: " + type);
        };
    }

    // Projectiles
    protected abstract CrossBowArrow createCrossBowArrow(double x, double y, int damage, Vector2D targetPosition);

    protected abstract ArcherArrow createArcherArrow(double x, double y, int damage, Vector2D targetPosition);

    protected abstract CannonBall createCannonBall(double x, double y, int damage, Vector2D targetPosition);

    protected abstract PoisonProjectile createPoisonProjectile(double x, double y, int damage, Vector2D targetPosition);
}