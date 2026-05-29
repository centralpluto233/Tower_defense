package towerdefence.util;

public record Vector2D(double x, double y) {

    /**
     * Calculates the distance between this vector and another vector.
     *
     * @param other The other vector to which the distance is calculated.
     * @return The distance between this vector and the other vector.
     */
    public double distanceTo(Vector2D other) {
        double dx = other.x - this.x;
        double dy = other.y - this.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Calculates the angle of this vector in radians, where 0 radians points to the right (positive x-axis) and angles increase counterclockwise.
     *
     * @return The angle of this vector in radians.
     */
    public double angle() {
        return Math.atan2(this.y, this.x);
    }

    /**
     * Subtracts another vector from this vector and returns the resulting vector.
     *
     * @param other The vector to be subtracted from this vector.
     * @return A new Vector2D that is the result of subtracting the other vector from this vector.
     */
    public Vector2D subtract(Vector2D other) {
        return new Vector2D(this.x - other.x, this.y - other.y);
    }

    /**
     * Adds another vector to this vector and returns the resulting vector.
     *
     * @param other The vector to be added to this vector.
     * @return A new Vector2D that is the result of adding the other vector to this vector.
     */
    public Vector2D add(Vector2D other) {
        return new Vector2D(this.x + other.x, this.y + other.y);
    }

    /**
     * Calculates the length (magnitude) of this vector.
     *
     * @return The length of this vector.
     */
    public double length() {
        return Math.sqrt(x * x + y * y);
    }

    /**
     * Normalizes this vector (scales it to have a length of 1) and returns the resulting vector. If the length of the vector is very small (less than 0.0001), it returns a zero vector to avoid division by zero.
     *
     * @return A new Vector2D that is the normalized version of this vector, or a zero vector if the length is too small.
     */
    public Vector2D normalize() {
        double len = length();
        if (len < 0.0001) return new Vector2D(0, 0);
        return new Vector2D(x / len, y / len);
    }

    /**
     * Calculates the dot product of this vector with another vector and returns the result.
     *
     * @param other The other vector with which to calculate the dot product.
     * @return The dot product of this vector and the other vector.
     */
    public double dot(Vector2D other) {
        return x * other.x + y * other.y;
    }
}