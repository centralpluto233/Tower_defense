package towerdefence.j2d;

import towerdefence.game.AbstractGameFactory;
import towerdefence.game.entity.enemy.GoblinEnemy;
import towerdefence.game.entity.tower.BasicTower;
import towerdefence.j2d.entity.enemy.J2dGoblinEnemy;
import towerdefence.j2d.entity.tower.J2dBasicTower;

import java.awt.*;

public class J2dFactory extends AbstractGameFactory {
    private Graphics2D g2d;

    public void setGraphics2D(Graphics2D g2d) {
        this.g2d = g2d;
    }

    @Override
    public GoblinEnemy createGoblin(double x, double y) {
        return new J2dGoblinEnemy(x,y,g2d);
    }

    @Override
    public BasicTower createBasicTower(double x, double y) {
        return new J2dBasicTower(x,y,g2d);
    }
}