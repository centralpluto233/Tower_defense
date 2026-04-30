package towerdefence.ui.menu;

import towerdefence.game.GameState;
import towerdefence.game.input.InputAction;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.Set;

public class MenuButton {
    private final String label;
    private final Rectangle2D.Double bounds;
    private final InputAction action;

    private final Set<GameState> visibleStates;
    private final Set<GameState> enabledStates;

    private final Color baseColor;
    private final Color hoverColor;
    private final Color disabledColor;
    private final Color textColor;

    public MenuButton(String label, double x, double y, double width, double height, InputAction action, Set<GameState>  visibleStates, Set<GameState> enabledStates, Color baseColor, Color hoverColor, Color disabledColor, Color textColor) {
        this.label = label;
        this.bounds = new Rectangle2D.Double(x, y, width, height);
        this.action = action;

        this.visibleStates = visibleStates;
        this.enabledStates = enabledStates;

        this.baseColor = baseColor;
        this.hoverColor = hoverColor;
        this.disabledColor = disabledColor;
        this.textColor = textColor;
    }

    public boolean isVisibleIn(GameState state) {
        return visibleStates.contains(state);
    }

    public boolean isEnabledIn(GameState state) {
        return enabledStates.contains(state);
    }

    public boolean contains(Point p) {
        return bounds.contains(p);
    }

    public String getLabel() {
        return label;
    }

    public Rectangle2D.Double getBounds() {
        return bounds;
    }

    public InputAction getAction() {
        return action;
    }

    public Color getBaseColor() {
        return baseColor;
    }

    public Color getHoverColor() {
        return hoverColor;
    }

    public Color getDisabledColor() {
        return disabledColor;
    }

    public Color getTextColor() {
        return textColor;
    }
}
