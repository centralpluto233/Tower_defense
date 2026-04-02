package towerdefence.j2d;

import towerdefence.game.Game;
import towerdefence.game.entity.Entity;
import towerdefence.game.interfaces.GameView;
import towerdefence.game.util.Vector2D;
import towerdefence.system.config.Config;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class J2dGamePanel extends JPanel implements GameView {
    private final Game game;
    private JFrame frame;
    private final Config config;
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
        frame.setResizable(false);
        frame.setFocusable(true);
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

        drawPath(g2d);
        drawTowerSpots(g2d);
        drawEntities(g2d);
    }

    private void drawPath(Graphics2D g2d) {
        List<Vector2D> path = game.getPath();
        if (path == null || path.size() < 2) {
            return;
        }

        g2d.setColor(new Color(220, 180, 60));
        g2d.setStroke(new BasicStroke(6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        for (int i = 0; i < path.size() - 1; i++) {
            Vector2D a = path.get(i);
            Vector2D b = path.get(i + 1);
            g2d.drawLine((int) a.x(), (int) a.y(), (int) b.x(), (int) b.y());
        }
    }

    private void drawTowerSpots(Graphics2D g2d) {
        List<Vector2D> towerSpots = game.getTowerSpots();
        if (towerSpots == null || towerSpots.isEmpty()) {
            return;
        }

        g2d.setColor(new Color(80, 170, 255));
        int r = 8;

        for (Vector2D spot : towerSpots) {
            int x = (int) spot.x() - r;
            int y = (int) spot.y() - r;
            g2d.fillOval(x, y, r * 2, r * 2);
        }
    }

    private void drawEntities(Graphics2D g2d) {
        for (Entity e : game.getEntities()) {
            e.draw();
        }
    }
}
