package towerdefence.game.entity;
import towerdefence.game.interfaces.RenderContext;
import towerdefence.game.util.Vector2D;

public abstract class Entity {

    private final double width;
    private final double height;
    private Vector2D position;
    private boolean markedForRemoval= false;

    /**
     * Constructs an Entity with the specified position and dimensions.
     * @param x the x-coordinate of the entity's position
     * @param y the y-coordinate of the entity's position
     * @param width the width of the entity
     * @param height the height of the entity
     */
    public Entity(double x, double y, double width, double height) {
        this.width = width;
        this.height = height;
        this.position = new Vector2D(x, y);
    }

    /**
     * Returns the x-coordinate of the entity's position.
     * @return the x-coordinate
     */
    public double getX() {
        return position.x();
    }

    /**
     * Sets the x-coordinate of the entity's position.
     * @param x the new x-coordinate
     */
    protected void setX(double x) {
        this.position = new Vector2D(x, this.position.y());
    }

    /**
     * Returns the y-coordinate of the entity's position.
     * @return the y-coordinate
     */
    public double getY() {
        return position.y();
    }

    /**
     * Sets the y-coordinate of the entity's position.
     * @param y the new y-coordinate
     */
    public void setY(double y) {
        this.position = new Vector2D(this.position.x(), y);
    }

    /**
     * Returns the width of the entity.
     * @return the width
     */
    public double getWidth() {
        return width;
    }

    /**
     * Returns the height of the entity.
     * @return the height
     */
    public double getHeight() {
        return height;
    }

    /**
     * Returns the position of the entity.
     * @return the position
     */
    public Vector2D getPosition() {
        return position;
    }

    /**
     * Returns a string representation of the entity.
     * @return the string representation
     */
    @Override
    public String toString() {
        return "Entity{ (x,y)=" + getPosition() + ", width=" + width + ", height=" + height + " }";
    }

    /**
     * Updates the entity's state based on the time elapsed.
     * @param deltaTime the time elapsed since the last update
     */
    public abstract void update(double deltaTime);

    /**
     * Marks entity for removal (dead or out of bounds)
     */
    protected void markForRemoval() {
        this.markedForRemoval = true;
    }

    /**
     * Returns a boolean indicating whether the entity is marked for removal.
     * @return true if the entity is marked for removal, false otherwise
     */
    public boolean isMarkedForRemoval() {
        return this.markedForRemoval;
    }

    /**
     * abstract draw method
     */
    public abstract void draw(RenderContext Ctx);
}
