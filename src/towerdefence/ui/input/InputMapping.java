package towerdefence.ui.input;

import towerdefence.util.Vector2D;

import java.awt.*;

public interface InputMapping {
    Vector2D screenToWorld(Point p);
}
