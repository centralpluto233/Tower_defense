package towerdefence.game;

import towerdefence.game.entity.enemy.GoblinEnemy;
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

}
