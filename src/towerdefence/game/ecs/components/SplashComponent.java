package towerdefence.game.ecs.components;

import towerdefence.game.entities.enemy.EnemyEntity;
import towerdefence.util.Vector2D;

import java.util.List;

public class SplashComponent {
    private final double radius;

    public SplashComponent(double radius) {
        this.radius = radius;
    }

    public void apply(Vector2D origin, int damage, List<EnemyEntity> enemies) {
        for (EnemyEntity e: enemies) {
            if (!e.isMarkedForRemoval()) {
                double dist = origin.distanceTo(e.getPosition());
                if (dist <= radius) {
                    double falloff = 1.0- (dist / radius);
                    e.takeDamage((int) (damage * falloff));
                }
            }
        }
    }
}
