package towerdefence.ui.input;

import towerdefence.util.Vector2D;

import java.awt.*;

public interface InputMapping {
    public Vector2D screenToWorld(Point p);
}
