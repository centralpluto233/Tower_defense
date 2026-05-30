package towerdefence.ui.j2d;

import towerdefence.game.AbstractGameFactory;
import towerdefence.game.Game;
import towerdefence.game.entities.enemy.BatEnemy;
import towerdefence.game.entities.enemy.BigSlime;
import towerdefence.game.entities.enemy.GoblinEnemy;
import towerdefence.game.entities.projectile.ArcherArrow;
import towerdefence.game.entities.projectile.CannonBall;
import towerdefence.game.entities.projectile.CrossBowArrow;
import towerdefence.game.entities.tower.ArcherTower;
import towerdefence.game.entities.tower.CannonTower;
import towerdefence.game.entities.tower.CrossBowTower;
import towerdefence.game.interfaces.GameView;
import towerdefence.game.interfaces.InputSource;
import towerdefence.system.config.Config;
import towerdefence.ui.input.SwingInputSource;
import towerdefence.ui.j2d.assets.AssetManager;
import towerdefence.ui.j2d.entity.enemy.J2dBatEnemy;
import towerdefence.ui.j2d.entity.enemy.J2dBigSlime;
import towerdefence.ui.j2d.entity.enemy.J2dGoblinEnemy;
import towerdefence.ui.j2d.entity.projectile.J2dArcherArrow;
import towerdefence.ui.j2d.entity.projectile.J2dCannonBall;
import towerdefence.ui.j2d.entity.projectile.J2dCrossBowArrow;
import towerdefence.ui.j2d.entity.tower.J2dArcherTower;
import towerdefence.ui.j2d.entity.tower.J2dCannonTower;
import towerdefence.ui.j2d.entity.tower.J2dCrossBowTower;
import towerdefence.util.Vector2D;

import java.util.List;

public class J2dFactory extends AbstractGameFactory {
    private final J2dGraphicsContext graphicsContext;
    private final AssetManager assetManager = new AssetManager();
    private J2dGamePanel gamePanel;

    public J2dFactory() {
        this.graphicsContext = new J2dGraphicsContext();
        this.assetManager.loadAll();
    }

    private J2dGraphicsContext getGraphics() {
        return this.graphicsContext;
    }

    @Override
    public GameView createGameView(Game game, Config config) {
        this.gamePanel = new J2dGamePanel(game, config, graphicsContext, assetManager);
        return gamePanel;
    }

    @Override
    public InputSource createInputSource(Game game, Config config, GameView gameView) {
        J2dGamePanel panel = (J2dGamePanel) gameView;
        SwingInputSource inputSource = new SwingInputSource(game, config, panel, panel);
        gamePanel.setCommandSink(inputSource::queueCommand);
        return inputSource;
    }

    // enemies
    @Override
    public GoblinEnemy createGoblin(double x, double y, List<Vector2D> path) {
        return new J2dGoblinEnemy(x, y, path, getGraphics(), assetManager);
    }

    public BatEnemy createBat(double x, double y, List<Vector2D> path) {
        return new J2dBatEnemy(x, y, path, getGraphics(), assetManager);
    }

    public BigSlime createBigSlime(double x, double y, List<Vector2D> path) {
        return new J2dBigSlime(x, y, path, getGraphics(), assetManager);
    }

    // towers
    @Override
    public CrossBowTower createCrossBowTower(double x, double y) {
        return new J2dCrossBowTower(x, y,
                (pos, dmg, target) -> createCrossBowArrow(pos.x(), pos.y(), dmg, target),
                getGraphics(), assetManager);
    }

    @Override
    public ArcherTower createArcherTower(double x, double y) {
        return new J2dArcherTower(x, y,
                (pos, dmg, target) -> createArcherArrow(pos.x(), pos.y(), dmg, target),
                getGraphics(), assetManager);
    }

    @Override
    public CannonTower createCannonTower(double x, double y) {
        return new J2dCannonTower(x, y,
                (pos, dmg, target) -> createCannonBall(pos.x(), pos.y(), dmg, target),
                getGraphics(), assetManager);
    }

    // projectiles
    @Override
    protected CrossBowArrow createCrossBowArrow(double x, double y, int damage, Vector2D targetPosition) {
        return new J2dCrossBowArrow(x, y, damage, targetPosition, graphicsContext, assetManager);
    }

    @Override
    protected ArcherArrow createArcherArrow(double x, double y, int damage, Vector2D targetPosition) {
        return new J2dArcherArrow(x, y, damage, targetPosition, graphicsContext, assetManager);
    }

    @Override
    protected CannonBall createCannonBall(double x, double y, int damage, Vector2D targetPosition) {
        return new J2dCannonBall(x, y, damage, targetPosition, graphicsContext, assetManager);
    }
}