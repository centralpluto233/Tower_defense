package towerdefence.ui.j2d;

import towerdefence.game.AbstractGameFactory;
import towerdefence.game.entities.enemy.EnemyEntity;
import towerdefence.game.entities.enemy.GoblinEnemy;
import towerdefence.game.entities.projectile.HomingProjectile;
import towerdefence.game.entities.tower.BasicTower;
import towerdefence.util.Vector2D;
import towerdefence.ui.j2d.entity.enemy.J2dGoblinEnemy;
import towerdefence.ui.j2d.entity.projectile.J2dHomingProjectile;
import towerdefence.ui.j2d.entity.tower.J2dBasicTower;

import java.util.List;

public class J2dFactory extends AbstractGameFactory {
    private J2dGraphicsContext graphicsContext;

    public J2dFactory(J2dGraphicsContext graphicsContext) {
        this.graphicsContext = graphicsContext;
    }

    private J2dGraphicsContext getGraphics() {
        return this.graphicsContext;
    }

    @Override
    public GoblinEnemy createGoblin(double x, double y, List<Vector2D> path) {
        return new J2dGoblinEnemy(x,y,path, getGraphics());
    }

    @Override
    public BasicTower createBasicTower(double x, double y) {
        return new J2dBasicTower(x,y, getGraphics());
    }

    @Override
    public HomingProjectile createHomingProjectile(double x, double y, int damage, EnemyEntity target) {
        return new J2dHomingProjectile(x,y,damage,target, getGraphics());
    }
}