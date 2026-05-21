package towerdefence.game.entities.projectile;

import towerdefence.game.entities.MovingEntity;
import towerdefence.game.entities.enemy.EnemyEntity;
import towerdefence.util.Vector2D;

public abstract class Projectile extends MovingEntity {
    private final int damage;
    private final EnemyEntity target;

    public Projectile(double x, double y, double width, double height, double speed, int damage, EnemyEntity target) {
        super(x, y, width, height, speed);
        this.damage = damage;
        this.target = target;
    }

    public void update(double dt) {
        if (target.isDead() || target.hasReachedEnd()) {
            markForRemoval();
            return;
        }

        Vector2D toTarget = target.getPosition().subtract(getPosition());

        if (overlaps(target)) {
            target.takeDamage(damage);
            markForRemoval();
            return;
        }

        Vector2D dir = toTarget.normalize();
        setDirection(dir.x(), dir.y());
        move(dt);

    }
}
