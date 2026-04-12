package towerdefence.game.entity.projectile;

import towerdefence.game.entity.MovingEntity;
import towerdefence.game.entity.enemy.EnemyEntity;

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

        double dx = target.getX() - getX();
        double dy = target.getY() - getY();
        double distance = target.getPosition().distanceTo(getPosition());

        double maxStep = getSpeed() * dt;

        if (distance <= maxStep || distance <= 0.001) {
            target.takeDamage(damage);
            markForRemoval();
            return;
        }

        setDirection(dx / distance, dy/distance);
        move(dt);

    }
}
