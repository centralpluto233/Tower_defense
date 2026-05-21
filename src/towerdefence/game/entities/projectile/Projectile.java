package towerdefence.game.entities.projectile;

import towerdefence.game.entities.MovingEntity;
import towerdefence.game.entities.enemy.EnemyEntity;
import towerdefence.util.Vector2D;

public abstract class Projectile extends MovingEntity {
    private final int damage;
    private final Vector2D targetPos;

    public Projectile(double x, double y, double width, double height, double speed, int damage, Vector2D targetPos) {
        super(x, y, width, height, speed);
        this.damage = damage;
        this.targetPos = targetPos;

        Vector2D dir = targetPos.subtract(new Vector2D(x, y)).normalize();
        setDirection(dir.x(), dir.y());
    }

    public void update(double dt) {
        move(dt);

        Vector2D toTarget = targetPos.subtract(getPosition());
        Vector2D dir = new Vector2D(getVelocity().x(), getVelocity().y());
        if (dir.dot(toTarget) < 0) {
            markForRemoval();
        }
    }

    public void dealDamage(EnemyEntity enemy) {
        enemy.takeDamage(damage);
    }
}
