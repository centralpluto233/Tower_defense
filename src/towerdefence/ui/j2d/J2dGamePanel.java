package towerdefence.ui.j2d;

import towerdefence.game.Game;
import towerdefence.game.entities.enemy.EnemyEntity;
import towerdefence.game.entities.projectile.Projectile;
import towerdefence.game.entities.tower.Tower;
import towerdefence.game.input.InputCommand;
import towerdefence.game.interfaces.GameView;
import towerdefence.system.config.Config;
import towerdefence.ui.input.InputMapping;
import towerdefence.ui.j2d.assets.AssetManager;
import towerdefence.ui.j2d.assets.SpriteAnimation;
import towerdefence.ui.menu.BuildMenuPanel;
import towerdefence.ui.menu.MenuOverlayPanel;
import towerdefence.util.Vector2D;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Consumer;
import java.util.logging.Logger;

public class J2dGamePanel extends JPanel implements GameView, InputMapping {
    private final Game game;
    private JFrame frame;
    private final Config config;
    private final J2dGraphicsContext graphicsContext;
    private final BuildMenuPanel buildMenuPanel;
    private final AssetManager assetManager;
    private final Logger logger =  Logger.getLogger(getClass().getSimpleName());

    private record Decoration(BufferedImage sprite, int x, int y) {}

    private final List<Decoration> decorations = new ArrayList<>();
    private final List<BufferedImage> decSprites;
    private final SpriteAnimation castle;
    private BufferedImage backgroundCache;

    private long fpsLastTime = System.currentTimeMillis();
    private int fpsFrames = 0;
    private int fps = 0;

    private double healthBarShakeTimer = 0.0;
    private static final double SHAKE_DURATION= 0.4;
    private static final int SHAKE_INTENSITY = 4;

    private final BufferedImage full;
    private final BufferedImage half;
    private final BufferedImage empty;

    private final SpriteAnimation coin;
    private final Cursor normalCursor;
    private final Cursor buildCursor;
    private final Cursor noMoneyCursor;
    private final Cursor sellCursor;

    private double deltaTime = 0;
    int DECORATION_PATH_CLEARANCE = 32;

    private record FloatingText(String text, Color color, double x, double y, double startLifetime, double lifetime) {}
    private final List<FloatingText> floatingTexts = new ArrayList<>();

    // ---------------------------------------------------------------------
    // Construction / initialization
    // ---------------------------------------------------------------------
    public J2dGamePanel(Game game, Config config, J2dGraphicsContext graphicsContext, AssetManager assetManager) {
        this.game = game;
        this.config = config;
        this.graphicsContext = graphicsContext;
        this.assetManager = assetManager;
        setPreferredSize(new Dimension(config.getGameWidth(), config.getGameHeight()));
        setBackground(Color.BLACK);
        setDoubleBuffered(true);

        BufferedImage normalCursorImg = assetManager.getSprite("sprites/hud/Cursor_1.png");
        this.normalCursor = Toolkit.getDefaultToolkit().createCustomCursor(normalCursorImg, new Point(0, 0), "normalCursor");

        BufferedImage hoverCursorImg = assetManager.getSprite("sprites/hud/Cursor_2.png");
        Cursor hoverCursor = Toolkit.getDefaultToolkit().createCustomCursor(hoverCursorImg, new Point(0, 0), "hoverCursor");

        BufferedImage buildCursorImg = assetManager.getSprite("sprites/hud/BuildCursor.png");
        this.buildCursor = Toolkit.getDefaultToolkit().createCustomCursor(buildCursorImg, new Point(0, 0), "buildCursor");

        BufferedImage sellCursorImg = assetManager.getSprite("sprites/hud/sellcursor.png");
        this.sellCursor = Toolkit.getDefaultToolkit().createCustomCursor(sellCursorImg, new Point(0, 0), "buildCursor");

        BufferedImage noMoneyCursorImg = assetManager.getSprite("sprites/hud/Cursor_3.png");
        this.noMoneyCursor = Toolkit.getDefaultToolkit().createCustomCursor(noMoneyCursorImg, new Point(0, 0), "noMoneyCursor");

        setLayout(new OverlayLayout(this));
        MenuOverlayPanel overlay = new MenuOverlayPanel(game, config, hoverCursor);
        overlay.setAlignmentX(0.0f);
        overlay.setAlignmentY(0.0f);
        add(overlay);
        game.addGameListener(overlay);

        BuildMenuPanel buildMenu = new BuildMenuPanel(game,this, assetManager, hoverCursor, noMoneyCursor);
        buildMenu.setAlignmentX(0.0f);
        buildMenu.setAlignmentY(0.0f);
        add(buildMenu);
        this.buildMenuPanel = buildMenu;
        game.addGameListener(buildMenu);

        BufferedImage castleSprite = assetManager.getSprite("sprites/map/castle.png");
        this.castle = new SpriteAnimation(castleSprite, 4, 30);

        BufferedImage coinSprite = assetManager.getSprite("sprites/hud/Coin-Sheet.png");
        this.coin = new SpriteAnimation(coinSprite, 4, 30);

        this.decSprites = List.of(
                assetManager.getSprite("sprites/map/decoration/mushroom.png"),
                assetManager.getSprite("sprites/map/decoration/mushroom.png"),
                assetManager.getSprite("sprites/map/decoration/rock_1.png"),
                assetManager.getSprite("sprites/map/decoration/rock_2.png"),
                assetManager.getSprite("sprites/map/decoration/rock_3.png")
        );

        this.empty = assetManager.getSprite("sprites/hud/heart_empty.png");
        this.half = assetManager.getSprite("sprites/hud/heart_half.png");
        this.full = assetManager.getSprite("sprites/hud/heart_full.png");



        generateDecorations();
        buildBackgroundCache();
    }

    // ---------------------------------------------------------------------
    // Window / lifecycle
    // ---------------------------------------------------------------------
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
        this.setCursor(normalCursor);

        frame.setFocusTraversalKeysEnabled(false);
        SwingUtilities.invokeLater(this::requestFocusInWindow);
    }

    @Override
    public void reset() {
        this.decorations.clear();
        generateDecorations();
        buildBackgroundCache();
    }

    @Override
    public void render(double deltaTime) {
        this.deltaTime = deltaTime;
        ensureWindow();
        updateFps();
        repaint();
    }

    // ---------------------------------------------------------------------
    // Paint / rendering entry point
    // ---------------------------------------------------------------------
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d = getScaledGraphics(g2d);
        graphicsContext.setGraphics2D(g2d);

        g2d.drawImage(backgroundCache, 0, 0, null);

        drawEntities(this.deltaTime);
        drawFloatingTexts(g2d, this.deltaTime);
        this.drawCastle(g2d, game.getPath().getLast());
        drawStats(g2d);
        if (config.getDebug()) {
            drawFps(g2d);
        }
        g2d.dispose();
    }

    // ---------------------------------------------------------------------
    // Input / callbacks
    // ---------------------------------------------------------------------
    public void setCommandSink(Consumer<InputCommand> commandSink) {
        buildMenuPanel.setCommandSink(commandSink);
    }

    public Cursor getBuildCursor() {
        return buildCursor;
    }

    public Cursor getNormalCursor() {
        return normalCursor;
    }

    public Cursor getSellCursor() {
        return sellCursor;
    }

    // ---------------------------------------------------------------------
    // Coordinate mapping
    // ---------------------------------------------------------------------
    @Override
    public Optional<Vector2D> screenToWorld(Point p) {
        double virtualW = config.getViewWidth();
        double virtualH = config.getViewHeight();
        double screenW = this.getWidth();
        double screenH = this.getHeight();

        double scale = Math.min(screenW / virtualW, screenH / virtualH);
        double offsetX = (screenW - virtualW * scale) / 2;
        double offsetY = (screenH - virtualH * scale) / 2;

        if (p.x < offsetX || p.y < offsetY || p.x > offsetX + virtualW * scale || p.y > offsetY + virtualH * scale) {
            return Optional.empty();
        }
        return Optional.of(new Vector2D(((p.x - offsetX) / scale) + config.getSpawnmargin(), ((p.y - offsetY) / scale) + config.getSpawnmargin()));
    }

    @Override
    public Point worldToScreen(Vector2D v) {
        double scaleX = (double) getWidth() / config.getGameWidth();
        double scaleY = (double) getHeight() / config.getGameHeight();
        return new Point(
                (int) (v.x() * scaleX),
                (int) (v.y() * scaleY)
        );
    }

    // ---------------------------------------------------------------------
    // Internal rendering setup
    // ---------------------------------------------------------------------
    private void buildBackgroundCache() {
        BufferedImage pathTile = assetManager.getSprite("sprites/map/path.png");
        BufferedImage grassTile = assetManager.getSprite("sprites/map/grass.png");

        backgroundCache = new BufferedImage(config.getGameWidth(), config.getGameHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = backgroundCache.createGraphics();
        drawBackground(g, grassTile);
        drawPath(g, pathTile);
        drawDecorations(g);
        drawTowerSpots(g);
        g.dispose();
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

    private void spawnFloatingText(String text, Color color, double x, double y) {
        this.floatingTexts.add(new FloatingText(text,color,x,y,0.3,0.3));
    }

    // ---------------------------------------------------------------------
    // Background / map helpers
    // ---------------------------------------------------------------------
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

    private void drawBackground(Graphics2D g2d, BufferedImage grassTile) {
        int tileSize = 32;

        int cols = (int) Math.ceil((double) config.getGameWidth() / tileSize);
        int rows = (int) Math.ceil((double) config.getGameHeight() / tileSize);

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                g2d.drawImage(grassTile, col * tileSize, row * tileSize, tileSize, tileSize, null);
            }
        }
    }

    private void drawPath(Graphics2D g2d, BufferedImage pathTile) {
        List<Vector2D> path = game.getPath();
        if (path == null || path.size() < 2) {
            logger.warning("path is null or to small");
            return;
        }

        int tileSize = 32;
        int half = tileSize / 2;

        for (int i = 0; i < path.size() - 1; i++) {
            Vector2D a = path.get(i);
            Vector2D b = path.get(i + 1);

            double dx = b.x() - a.x();
            double dy = b.y() - a.y();
            double dist = Math.hypot(dx, dy);
            double angle = Math.atan2(dy, dx);

            int count = (int) Math.ceil(dist / tileSize);

            for (int s = 0; s <= count; s++) {
                double t = (count == 0) ? 0 : (double) s / count;
                int tx = (int) (a.x() + dx * t);
                int ty = (int) (a.y() + dy * t);

                Graphics2D g2dTile = (Graphics2D) g2d.create();
                g2dTile.rotate(angle, tx, ty);
                g2dTile.drawImage(pathTile, tx - half, ty - half, tileSize, tileSize, null);
                g2dTile.dispose();
            }
        }
    }

    private void drawDecorations(Graphics2D g2d) {
        for (Decoration d : decorations) {
            g2d.drawImage(d.sprite(), d.x(), d.y(), null);
        }
    }

    private void drawTowerSpots(Graphics2D g2d) {
        List<Vector2D> towerSpots = game.getTowerSpots();
        if (towerSpots == null || towerSpots.isEmpty()) {
            logger.warning("towerSpots is null or empty");
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

    private void generateDecorations() {
        double density = 0.1;
        Random rng = new Random();
        int count = (int) (config.getGameWidth() * config.getGameHeight() * density / 1000);

        for (int i = 0; i < count; i++) {
            int x = rng.nextInt(config.getGameWidth());
            int y = rng.nextInt(config.getGameHeight());

            if (isTooCloseToPath(x, y, DECORATION_PATH_CLEARANCE)) continue;

            BufferedImage sprite = decSprites.get(rng.nextInt(decSprites.size()));
            decorations.add(new Decoration(sprite, x, y));
        }
    }

    private boolean isTooCloseToPath(int cx, int cy, int minDist) {
        List<Vector2D> path = game.getPath();
        for (int i = 0; i < path.size() - 1; i++) {
            Vector2D a = path.get(i);
            Vector2D b = path.get(i + 1);

            double dist = distanceToSegment(cx, cy, a.x(), a.y(), b.x(), b.y());
            if (dist < minDist) return true;
        }
        return false;
    }

    private double distanceToSegment(double px, double py,
                                     double ax, double ay,
                                     double bx, double by) {
        double dx = bx - ax, dy = by - ay;
        if (dx == 0 && dy == 0) return Math.hypot(px - ax, py - ay);
        double t = Math.clamp(((px - ax) * dx + (py - ay) * dy) / (dx * dx + dy * dy), 0, 1);
        return Math.hypot(px - (ax + t * dx), py - (ay + t * dy));
    }

    // ---------------------------------------------------------------------
    // Entity / stat rendering
    // ---------------------------------------------------------------------
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

    private void drawCastle(Graphics2D g2d, Vector2D end) {
        castle.update(deltaTime);
        BufferedImage frame = castle.getCurrentFrame();
        double width = 52 * 2.5;
        double height = 38 * 2.5;
        g2d.drawImage(frame, (int) end.x() - (int) width / 2, (int) end.y() - (5 * (int) height / 6), (int) width, (int) height, null);
    }

    private void drawStats(Graphics2D g2d) {
        drawHealthBar(g2d);
        drawResources(g2d);
        drawWaveInfo(g2d);
    }

    private void drawHealthBar(Graphics2D g2d) {
        int shakeX = 0;
        int shakeY = 0;

        if (healthBarShakeTimer > 0) {
            shakeX = (int) ((Math.random() - 0.5) * 2 * SHAKE_INTENSITY);
            shakeY = (int) ((Math.random() - 0.5) * 2 * SHAKE_INTENSITY);
            healthBarShakeTimer -= deltaTime;
        }

        int lives = game.getLives();
        int maxLives = game.getMaxLives();

        double heartSize_x = 10 * 2;
        double heartSize_y = 9 * 2;
        int fullHearts = lives / 2;
        boolean hasHalf = (lives % 2) != 0;
        int totalHearts = maxLives / 2;

        for (int i = 0; i < totalHearts; i++) {
            BufferedImage sprite;
            if (i < fullHearts) {
                sprite = full;
            } else if (i == fullHearts && hasHalf) {
                sprite = half;
            } else {
                sprite = empty;
            }
            g2d.drawImage(sprite, (int) ((10 + config.getSpawnmargin() + i * (heartSize_x + 4))+shakeX), (10 + config.getSpawnmargin()+shakeY), (int) heartSize_x, (int) heartSize_y, null);
        }

        healthBarShakeTimer = Math.max(0, healthBarShakeTimer - deltaTime);
    }

    @Override
    public void enemyKilled(int reward,int x, int y) {
        spawnFloatingText("+" + reward, new Color(255, 213, 65), x, y);
    }

    @Override
    public void towerPlaced(int price,int x, int y) {
        spawnFloatingText("-" + price, new Color(255, 0, 0), x, y);
    }

    @Override
    public void towerSold(int refund,int x, int y) {
        spawnFloatingText("+" + refund, new Color(255, 213, 65), x, y);
    }

    @Override
    public void enemyReachedBase(int damage) {
        healthBarShakeTimer= SHAKE_DURATION;
    }

    private void drawFloatingTexts(Graphics2D g2d, double dt) {
        floatingTexts.removeIf(ft -> ft.lifetime() <= 0);
        List<FloatingText> updated = new ArrayList<>();

        g2d.setFont(new Font("Consolas", Font.BOLD, 14));

        for (FloatingText ft : floatingTexts) {
            double progress = 1.0 - (ft.lifetime() / ft.startLifetime());
            int offsetY = (int) (progress * 30);
            double fadeStart = 0.3;
            double alphaRatio = Math.min(1.0, ft.lifetime() / fadeStart);
            int alpha = (int) (255 * alphaRatio);

            Color c = ft.color();
            g2d.setColor(new Color(c.getRed(),c.getGreen(),c.getBlue(), Math.max(0, alpha)));
            g2d.drawString(ft.text(), (int) ft.x(), (int) ft.y() - offsetY);

            updated.add(new FloatingText(ft.text(), c, ft.x(), ft.y(), ft.startLifetime(),ft.lifetime() - dt));
        }

        floatingTexts.clear();
        floatingTexts.addAll(updated);
    }

    private void drawResources(Graphics2D g2d) {
        coin.update(deltaTime);
        BufferedImage frame = coin.getCurrentFrame();
        double frameSize = 8 * 2.5;
        int resources = game.getResources();

        int x = 10 + config.getSpawnmargin();
        int y = 40 + config.getSpawnmargin();

        g2d.drawImage(frame, x, y, (int) frameSize, (int) frameSize, null);

        g2d.setColor(new Color(255, 213, 65));
        g2d.setFont(new Font("Consolas", Font.BOLD, 17));
        g2d.drawString(String.valueOf(resources), x + 28, y + 17);
    }

    private void drawWaveInfo(Graphics2D g2d) {
        if (game.isGameNotRunning()) return;

        int currentWave = game.getCurrentWave();
        int totalWaves = game.getTotalWaves();
        double countdown = game.getNextWaveCountdown();

        int totalHearts = game.getMaxLives() / 2;
        int rightEdge = config.getSpawnmargin() + totalHearts * (20 + 4);

        int y = 40 + config.getSpawnmargin();

        // draw wave text right-aligned to rightEdge
        g2d.setFont(new Font("Consolas", Font.BOLD, 17));
        g2d.setColor(Color.WHITE);
        String waveText = "Wave " + currentWave + " / " + totalWaves;
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(waveText, rightEdge - fm.stringWidth(waveText), y + 17);

        if (countdown > 0) {
            g2d.setFont(new Font("Consolas", Font.PLAIN, 13));
            g2d.setColor(new Color(255, 210, 60));
            FontMetrics fmSmall = g2d.getFontMetrics();
            String nextText = "Next: " + String.format("%.0f", countdown) + "s";
            g2d.drawString(nextText, rightEdge - fmSmall.stringWidth(nextText), y + 33);
        }
    }

    // ---------------------------------------------------------------------
    // Frame / debug overlays
    // ---------------------------------------------------------------------
    private void drawFps(Graphics2D g2d) {
        g2d.setFont(new Font("Consolas", Font.BOLD, 18));

        g2d.setColor(Color.WHITE);
        g2d.drawString("FPS: " + fps, 650 + config.getSpawnmargin(), 50 + config.getSpawnmargin());
    }

    // ---------------------------------------------------------------------
    // Static helpers
    // ---------------------------------------------------------------------
    public static void drawEntityHealthBar(Graphics2D g2d, double x, double y, double entityWidth, int health, int maxHealth) {
        int barWidth = (int) Math.max(entityWidth, 20);
        int barHeight = 3;
        int bx = (int) (x - barWidth / 2.0);
        int by = (int) (y - barHeight / 2.0) - 12;
        double percentage = (double) health / maxHealth;

        Color fill = gradientHealthColor(percentage);

        g2d.setColor(new Color(0, 0, 0, 160));
        g2d.fillRoundRect(bx - 1, by - 1, barWidth + 2, barHeight + 2, 3, 3);

        g2d.setColor(fill);
        g2d.fillRoundRect(bx, by, (int) (barWidth * percentage), barHeight, 3, 3);
    }

    private static Color gradientHealthColor(double percentage) { // GitHub Copilot gradient color
        // Clamp to [0, 1]
        percentage = Math.clamp(percentage, 0, 1);

        int r, g, b;
        double t;// 0 to 1 over second half
        if (percentage < 0.5) {
            // Red to Yellow (0% to 50%)
            t = percentage * 2;
            r = 255;
            g = (int) (165 * t);  // 0 → 165
        } else {
            // Yellow to Green (50% to 100%)
            t = (percentage - 0.5) * 2;
            r = (int) (255 - 255 * t);  // 255 → 0
            g = 255;
        }
        b = 0;
        return new Color(r, g, b);
    }

}
