package towerdefence.ui.menu;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class ToggleButton extends JToggleButton {
    private static final int ARC = 18;
    private final Color onColor = new Color(50, 160, 80);
    private final Color offColor = new Color(80, 85, 100);
    private final Color borderColor = new Color(255, 255, 255, 60);

    private Shape shape;

    public ToggleButton(String label, boolean initialState) {
        super(label, initialState);
        setFocusPainted(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setOpaque(false);
        setForeground(Color.WHITE);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setFont(new Font("Consolas", Font.BOLD, 20));
        setPreferredSize(new Dimension(220, 48));
        setMaximumSize(new Dimension(220, 48));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        boolean on = isSelected();
        ButtonModel model = getModel();

        Color base = on ? onColor : offColor;
        Color fill = model.isPressed() ? base.darker()
                : model.isRollover() ? base.brighter()
                  : base;

        g2.setColor(fill);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), ARC, ARC);

        g2.setColor(borderColor);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, ARC, ARC);

        FontMetrics fm = g2.getFontMetrics();
        int textX = (getWidth() - fm.stringWidth(getText())) / 2;
        int textY = (getHeight() + fm.getAscent() - fm.getDescent() / 2) / 2;
        g2.setColor(Color.WHITE);
        g2.drawString(getText(), textX, textY);

        int boxSize = 14;
        int boxX = getWidth() - 24;
        int boxY = (getHeight() - boxSize) / 2;

        g2.setColor(new Color(255, 255, 255, 180));
        g2.drawRoundRect(boxX, boxY, boxSize, boxSize, 4, 4);

        if (on) {
            g2.setColor(new Color(100, 255, 120));
            g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            g2.drawLine(boxX + 3, boxY + 7, boxX + 6, boxY + 11);
            g2.drawLine(boxX + 6, boxY + 11, boxX + 12, boxY + 3);
        } else {
            g2.setColor(new Color(255, 100, 100));
            g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            g2.drawLine(boxX + 3, boxY + 3, boxX + 11, boxY + 11);
            g2.drawLine(boxX + 11, boxY + 3, boxX + 3, boxY + 11);
        }

        g2.dispose();
    }

    @Override
    protected void paintBorder(Graphics g) {
    }

    @Override
    public boolean contains(int x, int y) {
        if (shape == null || !shape.getBounds().equals(getBounds())) {
            shape = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), ARC, ARC);
        }
        return shape.contains(x, y);
    }
}