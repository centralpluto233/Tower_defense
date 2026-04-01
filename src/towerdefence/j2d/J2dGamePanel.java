package towerdefence.j2d;

import towerdefence.game.Game;
import towerdefence.game.entity.Entity;
import towerdefence.game.interfaces.GameView;
import towerdefence.system.config.Config;

import javax.swing.*;
import java.awt.*;

public class J2dGamePanel extends JPanel implements GameView {
    private final Game game;
    private JFrame frame;
    private Config config;
    private final J2dFactory factory;

    public J2dGamePanel(Game game, Config config, J2dFactory factory) {
        this.game = game;
        this.config = config;
        this.factory = factory;
        setPreferredSize(new Dimension(config.getWindowWidth(), config.getWindowHeight()));
        setBackground(Color.BLACK);
        setDoubleBuffered(true);
    }

    public void ensureWindow() {
        if (frame != null) {
            return;
        }

        frame = new JFrame(config.getWindowTitle());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(this);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    @Override
    public void render() {
        ensureWindow();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        factory.setGraphics2D(g2d);

        for (Entity e : game.getEntities()) {
            e.draw();
        }
    }

}
