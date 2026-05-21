package towerdefence.ui.j2d;

import towerdefence.game.AbstractGameFactory;
import towerdefence.game.Game;
import towerdefence.game.entities.enemy.EnemyEntity;
import towerdefence.game.entities.enemy.GoblinEnemy;
import towerdefence.game.entities.projectile.CrossBowArrow;
import towerdefence.game.entities.tower.BasicTower;
import towerdefence.game.interfaces.GameView;
import towerdefence.game.interfaces.InputSource;
import towerdefence.system.config.Config;
import towerdefence.ui.input.SwingInputSource;
import towerdefence.ui.j2d.assets.AssetManager;
import towerdefence.util.Vector2D;
import towerdefence.ui.j2d.entity.enemy.J2dGoblinEnemy;
import towerdefence.ui.j2d.entity.projectile.J2dCrossBowArrow;
import towerdefence.ui.j2d.entity.tower.J2dBasicTower;

import java.util.List;

public class J2dFactory extends AbstractGameFactory {
    private J2dGraphicsContext graphicsContext;
    private final AssetManager assetManager = new AssetManager();

    public J2dFactory() {
        this.graphicsContext = new J2dGraphicsContext();
    }

    private J2dGraphicsContext getGraphics() {
        return this.graphicsContext;
    }

    @Override
    public GameView createGameView(Game game, Config config) {
        return new J2dGamePanel(game, config, graphicsContext);
    }

    @Override
    public InputSource createInputSource(Game game, Config config, GameView gameView) {
        J2dGamePanel panel = (J2dGamePanel) gameView;
        return new SwingInputSource(game, config, panel, panel);
    }

    @Override
    public GoblinEnemy createGoblin(double x, double y, List<Vector2D> path) {
        return new J2dGoblinEnemy(x,y,path, getGraphics(),assetManager);
    }

    @Override
    public BasicTower createBasicTower(double x, double y) {
        return new J2dBasicTower(x,y, getGraphics(),assetManager);
    }

    @Override
    public CrossBowArrow createHomingProjectile(double x, double y, int damage,Vector2D targetPosition) {
        return new J2dCrossBowArrow(x,y,damage,targetPosition,graphicsContext,assetManager);
    }
}