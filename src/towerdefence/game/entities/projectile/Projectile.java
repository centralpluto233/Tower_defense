package towerdefence.game.entities.projectile;

import towerdefence.game.ecs.components.SplashComponent;
import towerdefence.game.entities.MovingEntity;
import towerdefence.game.entities.enemy.EnemyEntity;
import towerdefence.util.Vector2D;

import java.util.Optional;

public abstract class Projectile extends MovingEntity {
    private final int damage;
    protected final Vector2D targetPos;
    private Optional<SplashComponent> splashComponentOptional = Optional.empty();

    /**
     * Creates a new Projectile with the given position, size, speed, damage, and target position.
     *
     * @param x         x part of the position to spawn the Projectile
     * @param y         y part of the position to spawn the Projectile
     * @param width     the width of the Projectile
     * @param height    the height of the Projectile
     * @param speed     the speed at which the Projectile will travel
     * @param damage    the damage this Projectile will deal to an enemy upon impact
     * @param targetPos the position that the Projectile will travel towards
     */
    public Projectile(double x, double y, double width, double height, double speed, int damage, Vector2D targetPos) {
        super(x, y, width, height, speed);
        this.damage = damage;
        this.targetPos = targetPos;

        Vector2D dir = targetPos.subtract(new Vector2D(x, y)).normalize();
        setVelocity(dir.x(), dir.y());
    }

    /**
     * Updates the position of the Projectile based on its velocity and checks if it has reached or passed the target position.
     * If it has, the Projectile is marked for removal.
     *
     * @param dt the time elapsed since the last update
     */
    @Override
    public void update(double dt) {
        move(dt);
    }

    /**
     * Deals damage to the given EnemyEntity by calling its takeDamage method with this Projectile's damage value.
     *
     * @param enemy the EnemyEntity to deal damage to
     */
    public void dealDamage(EnemyEntity enemy) {
        enemy.takeDamage(damage);
    }

    /**
     * Sets the SplashComponent for this Projectile, which defines the area of effect for splash damage when the Projectile impacts.
     * @param splashComponentOptional the SplashComponent to set for this Projectile
     */
    public void setSplashComponent(SplashComponent splashComponentOptional) {
        this.splashComponentOptional = Optional.of(splashComponentOptional);
    }

    /**
     * Gets the SplashComponent for this Projectile, if it has one. The SplashComponent defines the area of effect for splash damage when the Projectile impacts.
     * @return an Optional containing the SplashComponent if this Projectile has one, or an empty Optional if it does not
     */
    public Optional<SplashComponent> getSplashComponent() {
        return splashComponentOptional;
    }

    /**
     * Gets the damage value of this Projectile, which is the amount of damage it will deal to an enemy upon impact.
     * @return the damage value of this Projectile
     */
    public int getDamage() {
        return damage;
    }

    /**
     * Checks if the Projectile has reached or passed its target position. The Projectile is considered to have reached the target if the dot product of the direction vector (velocity) and the vector from the Projectile's current position to the target position is negative, indicating that the Projectile has passed beyond the target.
     * @return true if the Projectile has reached or passed its target position, false otherwise
     */
    public boolean hasReachedTarget() {
        Vector2D toTarget = targetPos.subtract(getPosition());
        Vector2D dir = new Vector2D(getVelocity().x(), getVelocity().y());
        return dir.dot(toTarget) < 0;
    }
}
