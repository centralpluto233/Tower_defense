package towerdefence.ui.input;

import towerdefence.util.Vector2D;

import java.awt.*;
import java.util.Optional;

// interface to map visual coords to game coords or the other way arround
public interface InputMapping {
    Optional<Vector2D> screenToWorld(Point p);

    Point worldToScreen(Vector2D p);
}
