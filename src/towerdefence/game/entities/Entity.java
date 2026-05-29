package towerdefence.game.entities;

import towerdefence.util.Vector2D;

public abstract class Entity {

    private final double width;
    private final double height;
    private Vector2D position;
    private boolean markedForRemoval = false;

    /**
     * Constructs an Entity with the specified position and dimensions.
     *
     * @param x      the x-coordinate of the entities' position
     * @param y      the y-coordinate of the entity's position
     * @param width  the width of the entities
     * @param height the height of the entities
     */
    public Entity(double x, double y, double width, double height) {
        this.width = width;
        this.height = height;
        this.position = new Vector2D(x, y);
    }

    /**
     * Returns the x-coordinate of the entity's position.
     *
     * @return the x-coordinate
     */
    public double getX() {
        return position.x();
    }

    /**
     * Sets the x-coordinate of the entities' position.
     *
     * @param x the new x-coordinate
     */
    protected void setX(double x) {
        this.position = new Vector2D(x, this.position.y());
    }

    /**
     * Returns the y-coordinate of the entities' position.
     *
     * @return the y-coordinate
     */
    public double getY() {
        return position.y();
    }

    /**
     * Sets the y-coordinate of the entities' position.
     *
     * @param y the new y-coordinate
     */
    protected void setY(double y) {
        this.position = new Vector2D(this.position.x(), y);
    }

    /**
     * Returns the width of the entities.
     *
     * @return the width
     */
    public double getWidth() {
        return width;
    }

    /**
     * Returns the height of the entities.
     *
     * @return the height
     */
    public double getHeight() {
        return height;
    }

    /**
     * Returns the position of the entities.
     *
     * @return the position
     */
    public Vector2D getPosition() {
        return position;
    }

    /**
     * Returns the position of the entities adjusted for drawing, which is the top-left corner of the bounding box.
     *
     * @return the draw position as a Vector2D
     */
    public Vector2D getDrawPosition() {
        return new Vector2D(getX() - getWidth() / 2, getY() - getHeight() / 2);
    }

    public boolean overlaps(Entity target) {
        return getX() - getWidth() / 2 < target.getX() + target.getWidth() / 2
                && getX() + getWidth() / 2 > target.getX() - target.getWidth() / 2
                && getY() - getHeight() / 2 < target.getY() + target.getHeight() / 2
                && getY() + getHeight() / 2 > target.getY() - target.getHeight() / 2;
    }

    /**
     * Returns a string representation of the entities.
     *
     * @return the string representation
     */
    @Override
    public String toString() {
        return "Entity{ (x,y)=" + getPosition() + ", width=" + width + ", height=" + height + " }";
    }

    /**
     * Updates the entities' state based on the time elapsed.
     *
     * @param deltaTime the time elapsed since the last update
     */
    public abstract void update(double deltaTime);

    /**
     * Marks entities for removal (dead or out of bounds)
     */
    public void markForRemoval() {
        this.markedForRemoval = true;
    }

    /**
     * Returns a boolean indicating whether the entities are marked for removal.
     *
     * @return true if the entities is marked for removal, false otherwise
     */
    public boolean isMarkedForRemoval() {
        return this.markedForRemoval;
    }

    /**
     * abstract draw method
     */
    public abstract void draw(double deltaTime);
}
