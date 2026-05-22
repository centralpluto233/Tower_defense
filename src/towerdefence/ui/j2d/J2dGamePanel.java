package towerdefence.ui.j2d;

import towerdefence.game.Game;
import towerdefence.game.entities.enemy.EnemyEntity;
import towerdefence.game.entities.projectile.Projectile;
import towerdefence.game.entities.tower.Tower;
import towerdefence.game.interfaces.GameView;
import towerdefence.ui.j2d.assets.AssetManager;
import towerdefence.ui.menu.MenuOverlayPanel;
import towerdefence.util.Vector2D;
import towerdefence.system.config.Config;
import towerdefence.system.logging.GameLogger;
import towerdefence.ui.input.InputMapping;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

public class J2dGamePanel extends JPanel implements GameView, InputMapping {
    private final Game game;
    private JFrame frame;
    private final Config config;
    private static final Logger logger = GameLogger.getLogger();
    private final J2dGraphicsContext graphicsContext;
    private final MenuOverlayPanel overlay;

    private final BufferedImage pathTile;
    private final BufferedImage grassTile;

    private record Decoration(BufferedImage sprite, int x, int y) {};
    private final List<Decoration> decorations = new ArrayList<>();
    private final List<BufferedImage> decSprites;

    private long fpsLastTime = System.currentTimeMillis();
    private int fpsFrames = 0;
    private int fps = 0;

    private double deltaTime=0;

    public J2dGamePanel(Game game, Config config, J2dGraphicsContext graphicsContext, AssetManager assetManager) {
        this.game = game;
        this.config = config;
        this.graphicsContext = graphicsContext;
        setPreferredSize(new Dimension(config.getGameWidth(), config.getGameHeight()));
        setBackground(Color.BLACK);
        setDoubleBuffered(true);

        setLayout(new OverlayLayout(this));
        overlay= new MenuOverlayPanel(game);
        overlay.setAlignmentX(0.0f);
        overlay.setAlignmentY(0.0f);
        add(overlay);

        game.setGameListener(overlay);
        this.pathTile = assetManager.getSprite("sprites/map/path.png");
        this.grassTile = assetManager.getSprite("sprites/map/grass.png");

        this.decSprites = List.of(
        assetManager.getSprite("sprites/map/decoration/mushroom.png"),
        assetManager.getSprite("sprites/map/decoration/mushroom.png"),
        assetManager.getSprite("sprites/map/decoration/rock_1.png"),
        assetManager.getSprite("sprites/map/decoration/rock_2.png"),
        assetManager.getSprite("sprites/map/decoration/rock_3.png")
        );

        generateDecorations(0.08);
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
    public void render(double deltaTime) {
        this.deltaTime = deltaTime;
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

        drawBackground(g2d);
        drawDecorations(g2d);
        drawPath(g2d);
        drawTowerSpots(g2d);
        drawEntities(this.deltaTime);
        drawStats(g2d);
        if (config.getDebug()) {
            drawFps(g2d);
        }
        g2d.dispose();
    }

    private Graphics2D getScaledGraphics(Graphics2D g2d) {
        double virtualW = config.getViewWidth();
        double virtualH = config.getViewHeight();
        double screenW = this.getWidth();
        double screenH = this.getHeight();

        double scale = Math.min(screenW / virtualW, screenH / virtualH);
        double offsetX = (screenW - virtualW * scale) / 2;
        double offsetY = (screenH - virtualH * scale) / 2;

        Graphics2D g2d2 = (Graphics2D) g2d.create();
        g2d2.translate(offsetX, offsetY);
        g2d2.scale(scale, scale);

        g2d2.translate(-config.getSpawnmargin(), -config.getSpawnmargin());
        g2d2.setClip(
                config.getSpawnmargin(),
                config.getSpawnmargin(),
                config.getViewWidth(),
                config.getViewHeight()
        );

        return g2d2;
    }

    private void drawBackground(Graphics2D g2d) {
        int tileSize = 32;

        int cols = (int) Math.ceil((double) config.getGameWidth()  / tileSize);
        int rows = (int) Math.ceil((double) config.getGameHeight() / tileSize);

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                g2d.drawImage(grassTile, col * tileSize, row * tileSize, tileSize, tileSize, null);
            }
        }
    }

    private void generateDecorations(double density) {
        Random rng = new Random();
        int count = (int)(config.getGameWidth() * config.getGameHeight() * density / 1000);

        for (int i = 0; i < count; i++) {
            int x = rng.nextInt(config.getGameWidth());
            int y = rng.nextInt(config.getGameHeight());

            if (isTooCloseToPath(x, y, 32+32/2)) continue;

            BufferedImage sprite = decSprites.get(rng.nextInt(decSprites.size()));
            decorations.add(new Decoration(sprite, x, y));
        }
    }

    private boolean isTooCloseToPath(int cx, int cy, int minDist) {
        for (Vector2D p : game.getPath()) {
            if (Math.hypot(cx - p.x(), cy - p.y()) < minDist) return true;
        }
        return false;
    }

    private void drawDecorations(Graphics2D g2d) {
        for (Decoration d : decorations) {
            g2d.drawImage(d.sprite(), d.x(), d.y(), null);
        }
    }

    private void drawPath(Graphics2D g2d) {
        List<Vector2D> path = game.getPath();
        if (path == null || path.size() < 2) return;

        int tileSize = 32;
        int half = tileSize / 2;

        for (int i = 0; i < path.size() - 1; i++) {
            Vector2D a = path.get(i);
            Vector2D b = path.get(i + 1);

            double dx = b.x() - a.x();
            double dy = b.y() - a.y();
            double dist = Math.hypot(dx, dy);
            double angle = Math.atan2(dy, dx); // hoek in radialen

            int count = (int) Math.ceil(dist / tileSize);

            for (int s = 0; s <= count; s++) {
                double t = (count == 0) ? 0 : (double) s / count;
                int tx = (int)(a.x() + dx * t);
                int ty = (int)(a.y() + dy * t);

                // sla graphics state op, roteer rond het middelpunt van de tile
                Graphics2D g2dTile = (Graphics2D) g2d.create();
                g2dTile.rotate(angle, tx, ty);
                g2dTile.drawImage(pathTile, tx - half, ty - half, tileSize, tileSize, null);
                g2dTile.dispose();
            }
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

    private void drawEntities(double deltaTime) {
        List<EnemyEntity> enemiesSnapshot = new ArrayList<>(game.getEnemies());
        List<Tower> towersSnapshot = new ArrayList<>(game.getTowers());
        List<Projectile> projectilesSnapshot = new ArrayList<>(game.getProjectiles());

        for (EnemyEntity e : enemiesSnapshot) {
            e.draw(deltaTime);
        }
        for (Tower t : towersSnapshot) {
            t.draw(deltaTime);
        }
        for (Projectile p : projectilesSnapshot) {
            p.draw(deltaTime);
        }

    }

    private void drawFps(Graphics2D g2d) {
        g2d.setFont(new Font("Consolas", Font.BOLD, 18));

        g2d.setColor(Color.WHITE);
        g2d.drawString("FPS: " + fps, 650+config.getSpawnmargin(), 50+config.getSpawnmargin());
    }

    private void drawStats(Graphics2D g2d) {
        g2d.setFont(new Font("Consolas", Font.BOLD, 18));

        g2d.setColor(Color.WHITE);
        g2d.drawString("Lives: " + game.getLives(), 18+config.getSpawnmargin(), 30+config.getSpawnmargin());
        g2d.drawString("Resources: " + game.getResources(), 18+config.getSpawnmargin(), 50+config.getSpawnmargin());
    }

    @Override
    public Vector2D screenToWorld(Point p) {
        double virtualW = config.getViewWidth();
        double virtualH = config.getViewHeight();
        double screenW = this.getWidth();
        double screenH = this.getHeight();

        double scale = Math.min(screenW / virtualW, screenH / virtualH);
        double offsetX = (screenW - virtualW * scale) / 2;
        double offsetY = (screenH - virtualH * scale) / 2;

        if (p.x < offsetX || p.y < offsetY || p.x > offsetX + virtualW*scale || p.y > offsetY + virtualH*scale) {
            return null;
        }
        return new Vector2D(((p.x - offsetX)/scale)+config.getSpawnmargin(), ((p.y - offsetY) / scale)+config.getSpawnmargin());
    }

}
