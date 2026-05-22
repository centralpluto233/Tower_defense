package towerdefence.util;

public record Vector2D (double x, double y) {
    
    public double distanceTo(Vector2D other) {
        double dx = other.x - this.x;
        double dy = other.y - this.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public double angle() {
        return Math.atan2(this.y, this.x);
    }

    public Vector2D subtract(Vector2D other) {
        return new Vector2D(this.x - other.x, this.y - other.y);
    }

    public Vector2D add(Vector2D other) {
        return new Vector2D(this.x + other.x, this.y + other.y);
    }

    public double length() {
        return Math.sqrt(x * x + y * y);
    }

    public Vector2D normalize() {
        double len = length();
        if (len < 0.0001) return new Vector2D(0, 0);
        return new Vector2D(x / len, y / len);
    }

    public double dot(Vector2D other) {
        return x * other.x + y * other.y;
    }
}