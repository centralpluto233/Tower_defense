package game.entity;

/**
 * An abstract class representing a moving entity in the game.
 */
public abstract class MovingEntity extends Entity{
    
    private double velocityX;
    private double velocityY;
    private double speed;
    
    /**
     * Returns the x-component of the entity's velocity.
     * @return the x-component of the velocity
     */
    public double getVelocityX() {
        return velocityX;
    }

    /**
     * Returns the y-component of the entity's velocity.
     * @return the y-component of the velocity
     */
    public double getVelocityY() {
        return velocityY;
    }

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
     * Constructs a MovingEntity with the specified position and movement properties.
     * @param x the x-coordinate of the entity's position
     * @param y the y-coordinate of the entity's position
     * @param width the width of the entity
     * @param height the height of the entity
     * @param velocityX the x-component of the entity's velocity
     * @param velocityY the y-component of the entity's velocity
     * @param speed the speed of the entity
     */
    public MovingEntity(int x, int y, double width, double height, double velocityX, double velocityY, double speed) {
        super(x, y, width, height);
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.speed = speed;
    }

    /**
     * Sets the direction of the entity's movement.
     * @param directionX the x-component of the direction
     * @param directionY the y-component of the direction
     */
    public void setDirection(double directionX, double directionY) {
        this.velocityX = directionX;
        this.velocityY = directionY;
    }

    /**
     * Moves the entity based on its velocity and the time elapsed.
     * @param deltaTime the time elapsed since the last update
     */
    public void move(double deltaTime) {
        setX(getX() + velocityX * speed * deltaTime);
        setY(getY() + velocityY * speed * deltaTime);
    }

}