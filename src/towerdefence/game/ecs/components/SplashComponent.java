package towerdefence.game.ecs.components;

import towerdefence.game.entities.enemy.EnemyEntity;
import towerdefence.util.Vector2D;

import java.util.List;

public class SplashComponent {
    private final double radius;

    /**
     * Component to apply splash damage to enemies within a certain radius of an origin point.
     *
     * @param radius the radius within which enemies will take splash damage
     */
    public SplashComponent(double radius) {
        this.radius = radius;
    }

    /**
     * Applies splash damage to all enemies within the radius of the origin point. The damage falls off linearly with distance from the origin, so that enemies at the edge of the radius take less damage than those closer to the origin.
     *
     * @param origin  the point from which the splash damage originates
     * @param damage  the maximum damage that can be dealt to an enemy at the origin point
     * @param enemies the list of enemies to check for splash damage
     */
    public void apply(Vector2D origin, int damage, List<EnemyEntity> enemies) {
        for (EnemyEntity e : enemies) {
            if (!e.isMarkedForRemoval()) {
                double dist = origin.distanceTo(e.getPosition());
                if (dist <= radius) {
                    double falloff = 1.0 - (dist / radius);
                    e.takeDamage((int) (damage * falloff));
                }
            }
        }
    }
}
