package towerdefence.j2d;

import towerdefence.game.Game;
import towerdefence.game.entity.enemy.EnemyEntity;
import towerdefence.game.entity.projectile.Projectile;
import towerdefence.game.entity.tower.Tower;
import towerdefence.game.input.InputCommand;
import towerdefence.game.interfaces.GameView;
import towerdefence.game.interfaces.InputSource;
import towerdefence.game.interfaces.RenderContext;
import towerdefence.game.util.Vector2D;
import towerdefence.system.config.Config;
import towerdefence.system.logging.GameLogger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;
//TODO panel uitdunnen en input opschonen/ andere tactiek miss?

public class J2dGamePanel extends JPanel implements GameView, InputSource {
    private final Game game;
    private JFrame frame;
    private final Config config;
    private static final Logger logger = GameLogger.getLogger();

    private long fpsLastTime = System.currentTimeMillis();
    private int fpsFrames = 0;
    private int fps = 0;

    private final Queue<InputCommand> inputQueue = new ConcurrentLinkedQueue<>();

    public J2dGamePanel(Game game, Config config) {
        this.game = game;
        this.config = config;
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
        installInputHandlers();
        SwingUtilities.invokeLater(this::requestFocusInWindow);
    }

    @Override
    public void render() {
        ensureWindow();
        updateFps();
        repaint();
    }

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
        RenderContext ctx = new J2dRenderContext(g2d);

        drawPath(g2d);
        drawTowerSpots(g2d);
        drawEntities(ctx);
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

    private void drawEntities(RenderContext ctx) {
        for (EnemyEntity e : game.getEnemies()) {
            e.draw(ctx);
        }
        for (Tower t : game.getTowers()) {
            t.draw(ctx);
        }
        for (Projectile p : game.getProjectiles()) {
            p.draw(ctx);
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
    public List<InputCommand> pollCommands() {
        List<InputCommand> commands = new ArrayList<>();
        InputCommand command;
        while ((command = inputQueue.poll()) != null) {
            commands.add(command);
        }
        return commands;
    }

    private void installInputHandlers() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
            if (!SwingUtilities.isLeftMouseButton(e)) {
                return;
            }
            Integer spotIndex = findIndexFromClick(e.getPoint());
            if (spotIndex != null) {
                inputQueue.add(InputCommand.placeTowerAtSpot(spotIndex));
            }
            }
        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                logger.finest("key pressed");
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    logger.info("Escape pressed");
                    inputQueue.add(InputCommand.quitGame());
                } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    logger.info("Enter pressed");
                    inputQueue.add(InputCommand.pauseToggled());
                }
            }
        });
    }

    private Integer findIndexFromClick(Point p) {
        //TODO replace vector2D with point (allready existing class)
        Vector2D pScaled = screenToWorld(p);
        if (pScaled == null) return null;

        List<Vector2D> spots = game.getTowerSpots();

        int best = -1;
        double bestD = config.getGameWidth()*2;

        for (int i = 0; i< spots.size(); i++) {
            double d = pScaled.distanceTo(spots.get(i));
            if (d <= config.getTowerSpotRadius() && d < bestD) {
                bestD = d;
                best = i;
            }
        }

        if (best >=0) {
            return best;
        } else {
            return null;
        }
    }

    private Vector2D screenToWorld(Point p) {
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
