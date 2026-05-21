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

}