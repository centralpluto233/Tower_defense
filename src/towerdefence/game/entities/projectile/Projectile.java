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

        double x = target.getX()+(target.getWidth()/2);
        double y = target.getY()+(target.getHeight()/2);
        double dx = x - this.getX();
        double dy = y - this.getY();
        Vector2D targetCenter = new Vector2D(x, y);
        double distance = this.getPosition().distanceTo(targetCenter);

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
