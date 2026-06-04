package towerdefence.ui.input;

import towerdefence.util.Vector2D;

import java.awt.*;
import java.util.Optional;

public interface InputMapping {
    Optional<Vector2D> screenToWorld(Point p);

    Point worldToScreen(Vector2D p);
}
