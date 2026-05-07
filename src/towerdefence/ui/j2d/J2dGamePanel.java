package towerdefence.ui.j2d;

import towerdefence.game.Game;
import towerdefence.game.entities.enemy.EnemyEntity;
import towerdefence.game.entities.projectile.Projectile;
import towerdefence.game.entities.tower.Tower;
import towerdefence.game.interfaces.GameView;
import towerdefence.util.Vector2D;
import towerdefence.system.config.Config;
import towerdefence.system.logging.GameLogger;
import towerdefence.ui.input.InputMapping;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class J2dGamePanel extends JPanel implements GameView, InputMapping {
    private final Game game;
    private JFrame frame;
    private final Config config;
    private static final Logger logger = GameLogger.getLogger();
    private final J2dGraphicsContext graphicsContext;

    private long fpsLastTime = System.currentTimeMillis();
    private int fpsFrames = 0;
    private int fps = 0;

    public J2dGamePanel(Game game, Config config, J2dGraphicsContext graphicsContext) {
        this.game = game;
        this.config = config;
        this.graphicsContext = graphicsContext;
        setPreferredSize(new Dimension(config.getGameWidth(), config.getGameHeight()));
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
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setUndecorated(true);
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setFocusable(true);

        frame.setFocusTraversalKeysEnabled(false);
        SwingUtilities.invokeLater(this::requestFocusInWindow);
    }

    @Override
    public void render() {
        ensureWindow();
        updateFps();
        repaint();
    }
    //TODO gamepanel aanpassen om menu te tekenen

    private void updateFps() {
        fpsFrames++;
        long now = System.currentTimeMillis();

        if (now - fpsLastTime >= 1000) {
            fps = fpsFrames;
            fpsFrames = 0;
            fpsLastTime = now;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d = getScaledGraphics(g2d);
        graphicsContext.setGraphics2D(g2d);

        drawPath(g2d);
        drawTowerSpots(g2d);
        drawEntities();
        drawStats(g2d);
        if (config.getDebug()) {
            drawFps(g2d);
        }
        g2d.dispose();
    }

    private Graphics2D getScaledGraphics(Graphics2D g2d) {
        double virtualW = config.getGameWidth();
        double virtualH = config.getGameHeight();
        double screenW = this.getWidth();
        double screenH = this.getHeight();

        double scale = Math.min(screenW / virtualW, screenH / virtualH);
        double offsetX = (screenW - virtualW * scale) / 2;
        double offsetY = (screenH - virtualH * scale) / 2;

        Graphics2D g2d2 = (Graphics2D) g2d.create();
        g2d2.translate(offsetX, offsetY);
        g2d2.scale(scale, scale);

        return g2d2;
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
        int r = config.getTowerSpotRadius();

        for (Vector2D spot : towerSpots) {
            int x = (int) spot.x() - r;
            int y = (int) spot.y() - r;
            g2d.fillOval(x, y, r * 2, r * 2);
        }
    }

    private void drawEntities() {
        List<EnemyEntity> enemiesSnapshot = new ArrayList<>(game.getEnemies());
        List<Tower> towersSnapshot = new ArrayList<>(game.getTowers());
        List<Projectile> projectilesSnapshot = new ArrayList<>(game.getProjectiles());

        for (EnemyEntity e : enemiesSnapshot) {
            e.draw();
        }
        for (Tower t : towersSnapshot) {
            t.draw();
        }
        for (Projectile p : projectilesSnapshot) {
            p.draw();
        }

    }

    private void drawFps(Graphics2D g2d) {
        g2d.setFont(new Font("Consolas", Font.BOLD, 18));

        g2d.setColor(Color.WHITE);
        g2d.drawString("FPS: " + fps, 650, 28);
    }

    private void drawStats(Graphics2D g2d) {
        g2d.setFont(new Font("Consolas", Font.BOLD, 18));

        g2d.setColor(Color.WHITE);
        g2d.drawString("Lives: " + game.getLives(), 18, 30);
        g2d.drawString("Resources: " + game.getResources(), 18, 50);
    }

    @Override
    public Vector2D screenToWorld(Point p) {
        double virtualW = config.getGameWidth();
        double virtualH = config.getGameHeight();
        double screenW = this.getWidth();
        double screenH = this.getHeight();

        double scale = Math.min(screenW / virtualW, screenH / virtualH);
        double offsetX = (screenW - virtualW * scale) / 2;
        double offsetY = (screenH - virtualH * scale) / 2;

        if (p.x < offsetX || p.y < offsetY || p.x > offsetX + virtualW*scale || p.y > offsetY + virtualH*scale) {
            return null;
        }
        return new Vector2D((p.x - offsetX)/scale, (p.y - offsetY) / scale);
    }

}
