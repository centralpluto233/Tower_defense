package towerdefence.ui.menu;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class GameButton extends JButton {
    private static final int ARC = 18;

    private final Color normalColor;
    private final Color hoverColor;
    private final Color pressedColor;
    private final Color borderColor = new Color(255, 255, 255, 60);
    private final Color textColor = Color.WHITE;

    private Shape shape;

    public GameButton(String text) {
        this(text, new Color(52, 120, 246));
    }

    public GameButton(String text, Color baseColor) {
        super(text);
        this.normalColor  = baseColor;
        this.hoverColor   = baseColor.brighter();
        this.pressedColor = baseColor.darker();

        setFocusPainted(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setOpaque(false);
        setForeground(textColor);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setFont(new Font("Consolas", Font.BOLD, 20));
        setPreferredSize(new Dimension(220, 48));
    }

    // --- Factory methodes ---
    public static GameButton primary(String text) {
        return new GameButton(text, new Color(52, 120, 246));  // blauw
    }

    public static GameButton success(String text) {
        return new GameButton(text, new Color(50, 160, 80));   // groen
    }

    public static GameButton danger(String text) {
        return new GameButton(text, new Color(190, 60, 60));   // rood
    }

    public static GameButton muted(String text) {
        return new GameButton(text, new Color(80, 85, 100));   // grijs
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        ButtonModel model = getModel();
        Color fill = normalColor;

        if (!model.isEnabled()) {
            fill = new Color(90, 90, 90);
        } else if (model.isPressed()) {
            fill = pressedColor;
        } else if (model.isRollover()) {
            fill = hoverColor;
        }

        g2.setColor(fill);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), ARC, ARC);

        g2.setColor(borderColor);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, ARC, ARC);

        g2.dispose();
        super.paintComponent(g);
    }

    @Override
    protected void paintBorder(Graphics g) {}

    @Override
    public boolean contains(int x, int y) {
        if (shape == null || !shape.getBounds().equals(getBounds())) {
            shape = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), ARC, ARC);
        }
        return shape.contains(x, y);
    }
}