package game.entity;
import game.util.Vector2D;

/**
 * An abstract class representing a moving entity in the game.
 */
public abstract class MovingEntity extends Entity{
    
    private Vector2D velocity;
    private double speed;

    /**
     * Returns the speed of the entity.
     * @return the speed
     */
    public double getSpeed() {
        return speed;
    }

    /**
     * Sets the speed of the entity.
     * @param speed the new speed
     */
    public void setSpeed(double speed) {
        this.speed = speed;
    }

    /**
     * Returns the velocity of the entity.
     * @return the velocity
     */
    public Vector2D getVelocity() {
        return velocity;
    }

    /**
     * Constructs a MovingEntity with the specified position and movement properties.
     * @param x the x-coordinate of the entity's position
     * @param y the y-coordinate of the entity's position
     * @param width the width of the entity
     * @param height the height of the entity
     * @param velocityX the x-component of the entity's velocity
     * @param velocityY the y-component of the entity's velocity
     * @param speed the speed of the entity
     */
    public MovingEntity(double x, double y, double width, double height, double velocityX, double velocityY, double speed) {
        super(x, y, width, height);
        this.velocity = new Vector2D(velocityX, velocityY);
        this.speed = speed;
    }

    /**
     * Sets the direction of the entity's movement.
     * @param directionX the x-component of the direction
     * @param directionY the y-component of the direction
     */
    public void setDirection(double directionX, double directionY) {
        this.velocity = new Vector2D(directionX, directionY);
    }

    /**
     * Moves the entity based on its velocity and the time elapsed.
     * @param deltaTime the time elapsed since the last update
     */
    protected void move(double deltaTime) {
        setX(getX() + velocity.x() * speed * deltaTime);
        setY(getY() + velocity.y() * speed * deltaTime);
    }

    /**
     * Follows a path based on the time elapsed.
     * @param deltaTime the time elapsed since the last update
     */
    protected abstract void followPath(double deltaTime);

    /**
     * Returns a string representation of the moving entity.
     * @return the string representation
     */
    @Override
    public String toString() {
        return "MovingEntity{ (x,y)=" + getPosition() + ", width=" + getWidth() + ", height=" + getHeight() + ", velocity=" + velocity + ", speed=" + speed + " }";
    }

}