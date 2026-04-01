package towerdefence.game;

import towerdefence.game.entity.enemy.GoblinEnemy;

/**
 * Abstract factory for creating game entities
 * Subclasses provide visuals
 */
public abstract class AbstractGameFactory {
    // Enemies
    public abstract GoblinEnemy createGoblin(double x, double y);

}
