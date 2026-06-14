package towerdefence.ui.menu;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;

public class GameButton extends JButton {
    private static final int ARC = 18;

    private final Color normalColor;
    private final Color hoverColor;
    private final Color pressedColor;
    private final Color borderColor = new Color(255, 255, 255, 60);
    private final Color textColor = Color.WHITE;

    private BufferedImage image;
    private Shape shape;

    /**
     * creates a button with the specified text and base color. The button will have a hover effect and a pressed effect based on the base color.
     * @param text the text that the button should display
     * @param baseColor the base color of the button, used to calculate hover and pressed colors
     */
    public GameButton(String text, Color baseColor) {
        this(text, baseColor, 180, 40, 20);
    }

    /**
     * creates a button with the specified text, base color, width, height and font size. The button will have a hover effect and a pressed effect based on the base color.
     * @param text the text that the button should display
     * @param baseColor the base color of the button, used to calculate hover and pressed colors
     * @param width the width of the button
     * @param height the height of the button
     * @param fontSize the font size of the button text
     */
    public GameButton(String text, Color baseColor, int width, int height, int fontSize) {
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
        setFont(new Font("Consolas", Font.BOLD, fontSize));
        setPreferredSize(new Dimension(width, height));
        setMaximumSize(new Dimension(width, height));
    }

    /**
     * creates a button that displays the given image, scaled by the given factor. The button will have a hover effect and a pressed effect that tint the image.
     * @param image the image to display on the button
     * @param scale the factor by which to scale the image, e.g. 1.0 for original size, 0.5 for half size, etc.
     * @return a GameButton that displays the given image with hover and pressed effects
     */
    public static GameButton icon(BufferedImage image, double scale) {
        GameButton btn = new GameButton("", new Color(0, 0, 0, 0));
        btn.image = image;
        btn.setPreferredSize(new Dimension(
                (int) (image.getWidth() * scale),
                (int) (image.getHeight() * scale)
        ));
        return btn;
    }

    public static GameButton primary(String text) {
        return new GameButton(text, new Color(52, 120, 246));
    }

    public static GameButton success(String text) {
        return new GameButton(text, new Color(50, 160, 80));
    }

    public static GameButton danger(String text) {
        return new GameButton(text, new Color(190, 60, 60));
    }

    public static GameButton muted(String text) {
        return new GameButton(text, new Color(80, 85, 100));
    }

    public static GameButton primary(String text, int width, int height, int fontSize) {
        return new GameButton(text, new Color(52, 120, 246), width, height, fontSize);
    }

    public static GameButton success(String text, int width, int height, int fontSize) {
        return new GameButton(text, new Color(50, 160, 80), width, height, fontSize);
    }

    public static GameButton danger(String text, int width, int height, int fontSize) {
        return new GameButton(text, new Color(190, 60, 60), width, height, fontSize);
    }

    public static GameButton muted(String text, int width, int height, int fontSize) {
        return new GameButton(text, new Color(80, 85, 100), width, height, fontSize);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        ButtonModel model = getModel();

        if (normalColor.getAlpha() > 0) {
            Color fill = normalColor;
            if (!model.isEnabled()) fill = new Color(90, 90, 90);
            else if (model.isPressed()) fill = pressedColor;
            else if (model.isRollover()) fill = hoverColor;

            g2d.setColor(fill);
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), ARC, ARC);
            g2d.setColor(borderColor);
            g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, ARC, ARC);
        }

        if (image != null) {
            int padding = 4;
            int w = getWidth() - padding * 2;
            int h = getHeight() - padding * 2;

            BufferedImage scaled = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            Graphics2D tmp = scaled.createGraphics();
            tmp.drawImage(image, 0, 0, w, h, null);
            tmp.dispose();

            if (model.isPressed()) {
                RescaleOp tint = new RescaleOp(
                        new float[]{0.6f, 0.6f, 0.6f, 1.0f},
                        new float[]{0, 0, 0, 0}, null
                );
                g2d.drawImage(scaled, tint, padding, padding);
            } else if (model.isRollover()) {
                RescaleOp tint = new RescaleOp(
                        new float[]{1.3f, 1.3f, 1.3f, 1.0f},
                        new float[]{0, 0, 0, 0}, null
                );
                g2d.drawImage(scaled, tint, padding, padding);
            } else {
                g2d.drawImage(scaled, padding, padding, null);
            }
        } else {
            FontMetrics fm = g2d.getFontMetrics();
            int textX = (getWidth() - fm.stringWidth(getText())) / 2;
            int textY = (getHeight() + fm.getAscent() - (fm.getDescent() / 2)) / 2;
            g2d.setColor(textColor);
            g2d.drawString(getText(), textX, textY);
        }

        g2d.dispose();
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