package towerdefence.ui.j2d;

import towerdefence.game.AbstractGameFactory;
import towerdefence.game.entity.enemy.EnemyEntity;
import towerdefence.game.entity.enemy.GoblinEnemy;
import towerdefence.game.entity.projectile.HomingProjectile;
import towerdefence.game.entity.tower.BasicTower;
import towerdefence.game.util.Vector2D;
import towerdefence.ui.j2d.entity.enemy.J2dGoblinEnemy;
import towerdefence.ui.j2d.entity.projectile.J2dHomingProjectile;
import towerdefence.ui.j2d.entity.tower.J2dBasicTower;

import java.util.List;

public class J2dFactory extends AbstractGameFactory {

    @Override
    public GoblinEnemy createGoblin(double x, double y, List<Vector2D> path) {
        return new J2dGoblinEnemy(x,y,path);
    }

    @Override
    public BasicTower createBasicTower(double x, double y) {
        return new J2dBasicTower(x,y);
    }

    @Override
    public HomingProjectile createHomingProjectile(double x, double y, int damage, EnemyEntity target) {
        return new J2dHomingProjectile(x,y,damage,target);
    }
}