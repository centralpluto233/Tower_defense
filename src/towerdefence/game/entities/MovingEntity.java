package towerdefence.game.entities;

import towerdefence.util.Vector2D;

public abstract class MovingEntity extends Entity {
    private Vector2D velocity;
    private final double speed;

    /**
     * Returns the speed of the entities.
     *
     * @return the speed
     */
    public double getSpeed() {
        return speed;
    }

    /**
     * Returns the velocity of the entities.
     *
     * @return the velocity
     */
    public Vector2D getVelocity() {
        return velocity;
    }

    /**
     * Constructs a MovingEntity with the specified position and movement properties.
     *
     * @param x      the x-coordinate of the entities' position
     * @param y      the y-coordinate of the entities' position
     * @param width  the width of the entities
     * @param height the height of the entities
     * @param speed  the speed of the entities
     */
    public MovingEntity(double x, double y, double width, double height, double speed) {
        super(x, y, width, height);
        this.velocity = new Vector2D(0, 0);
        this.speed = speed;
    }

    /**
     * Sets the direction of the entities' movement.
     *
     * @param directionX the x-component of the direction
     * @param directionY the y-component of the direction
     */
    protected void setVelocity(double directionX, double directionY) {
        this.velocity = new Vector2D(directionX, directionY);
    }

    /**
     * Sets the velocity of the entities to the specified Vector2D.
     *
     * @param velocity the new velocity as a Vector2D
     */
    protected void setVelocity(Vector2D velocity) {
        this.velocity = velocity;
    }

    /**
     * Moves the entities based on its velocity and the time elapsed.
     *
     * @param deltaTime the time elapsed since the last update
     */
    protected void move(double deltaTime) {
        setX(getX() + velocity.x() * speed * deltaTime);
        setY(getY() + velocity.y() * speed * deltaTime);
    }

    /**
     * Returns a string representation of the moving entities.
     *
     * @return the string representation
     */
    @Override
    public String toString() {
        return "MovingEntity{ (x,y)=" + getPosition() + ", width=" + getWidth() + ", height=" + getHeight() + ", velocity=" + velocity + ", speed=" + speed + " }";
    }

}