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
import towerdefence.ui.j2d.assets.MapTheme;
import towerdefence.ui.menu.BuildMenuPanel;
import towerdefence.ui.menu.MenuOverlayPanel;
import towerdefence.util.Vector2D;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class J2dGamePanel extends JPanel implements GameView, InputMapping {

    private final Game               game;
    private final Config             config;
    private final J2dGraphicsContext graphicsContext;
    private final BuildMenuPanel     buildMenuPanel;

    private final J2dMapRenderer mapRenderer;
    private final J2dHudRenderer hudRenderer;

    private MapTheme currentTheme;
    private double   deltaTime = 0;

    private long fpsLastTime = System.currentTimeMillis();
    private int  fpsFrames   = 0;
    private int  fps         = 0;

    private final Cursor normalCursor;
    private final Cursor buildCursor;
    private final Cursor sellCursor;

    private JFrame frame;

    // -------------------------------------------------------------------------
    // Construction
    // -------------------------------------------------------------------------

    public J2dGamePanel(Game game, Config config, J2dGraphicsContext graphicsContext, AssetManager assets) {
        this.game            = game;
        this.config          = config;
        this.graphicsContext = graphicsContext;
        this.currentTheme    = MapTheme.valueOf(config.getMapTheme().toUpperCase());

        setPreferredSize(new Dimension(config.getGameWidth(), config.getGameHeight()));
        setBackground(Color.BLACK);
        setDoubleBuffered(true);

        this.normalCursor = makeCursor(assets, "sprites/hud/Cursor_1.png",     "normalCursor");
        Cursor hoverCursor = makeCursor(assets, "sprites/hud/Cursor_2.png",    "hoverCursor");
        this.buildCursor  = makeCursor(assets, "sprites/hud/BuildCursor.png",  "buildCursor");
        this.sellCursor   = makeCursor(assets, "sprites/hud/sellcursor.png",   "sellCursor");
        Cursor noMoney    = makeCursor(assets, "sprites/hud/Cursor_3.png",     "noMoneyCursor");

        this.mapRenderer = new J2dMapRenderer(game, config, assets, currentTheme);
        this.hudRenderer = new J2dHudRenderer(game, config, assets);

        setLayout(new OverlayLayout(this));

        MenuOverlayPanel overlay = new MenuOverlayPanel(game, config, hoverCursor, this::setTheme, assets);
        overlay.setAlignmentX(0.0f);
        overlay.setAlignmentY(0.0f);
        add(overlay);
        game.addGameListener(overlay);

        BuildMenuPanel buildMenu = new BuildMenuPanel(game, this, assets, hoverCursor, noMoney);
        buildMenu.setAlignmentX(0.0f);
        buildMenu.setAlignmentY(0.0f);
        add(buildMenu);
        this.buildMenuPanel = buildMenu;
        game.addGameListener(buildMenu);
    }

    // -------------------------------------------------------------------------
    // GameView
    // -------------------------------------------------------------------------

    /**
     * Called by the game every frame to render the current state of the game. This method should trigger a repaint of the panel, and can also be used to update any animations or other time-based effects in the game.
     * @param deltaTime the time in seconds since the last frame was rendered. This can be used to update animations and other time-based effects in the game.
     */
    @Override
    public void render(double deltaTime) {
        this.deltaTime = deltaTime;
        ensureWindow();
        updateFps();
        repaint();
    }

    /**
     * Called by the game when a new level is loaded, to reset the map renderer with the new level data.
     */
    @Override
    public void reset() {
        mapRenderer.rebuild(currentTheme);
    }

    /**
     * Called by the game when an enemy is killed, to show the reward as floating text.
     * @param reward the amount of money rewarded for killing the enemy
     * @param x the x coordinate of the enemy that was killed
     * @param y the y coordinate of the enemy that was killed
     */
    @Override
    public void enemyKilled(int reward, int x, int y) {
        hudRenderer.spawnFloatingText("+" + reward, new Color(255, 213, 65), x, y);
    }

    /**
     * Called by the game when a tower is placed, to show the cost as floating text.
     * @param price the amount of money spent on placing the tower
     * @param x the x coordinate of the tower that was placed
     * @param y the y coordinate of the tower that was placed
     */
    @Override
    public void towerPlaced(int price, int x, int y) {
        hudRenderer.spawnFloatingText("-" + price, new Color(255, 0, 0), x, y);
    }

    /**
     * Called by the game when a tower is sold, to show the refund as floating text.
     * @param refund the amount of money refunded for selling the tower
     * @param x the x coordinate of the tower that was sold
     * @param y the y coordinate of the tower that was sold
     */
    @Override
    public void towerSold(int refund, int x, int y) {
        hudRenderer.spawnFloatingText("+" + refund, new Color(255, 213, 65), x, y);
    }

    /**
     * Called by the game when an enemy reaches the base, to trigger a shake effect on the HUD.
     * @param damage the amount of damage dealt to the base by the enemy that reached it
     */
    @Override
    public void enemyReachedBase(int damage) {
        hudRenderer.triggerShake();
    }

    // -------------------------------------------------------------------------
    // InputMapping
    // -------------------------------------------------------------------------

    /**
     * Converts a point in screen coordinates (e.g. from a mouse click) to world coordinates in the game.
     * This method takes into account the current scaling and offset of the view, and returns an Optional containing the world coordinates if the point is within the view, or an empty Optional if it is outside the view.
     * @param p the point in screen coordinates to convert
     * @return an Optional containing the world coordinates as a Vector2D if the point is within the view, or an empty Optional if it is outside the view
     */
    @Override
    public Optional<Vector2D> screenToWorld(Point p) {
        double vw = config.getViewWidth(),   vh = config.getViewHeight();
        double sw = getWidth(),              sh = getHeight();
        double scale = Math.min(sw / vw, sh / vh);
        double ox = (sw - vw * scale) / 2,  oy = (sh - vh * scale) / 2;

        if (p.x < ox || p.y < oy || p.x > ox + vw * scale || p.y > oy + vh * scale)
            return Optional.empty();

        return Optional.of(new Vector2D(
                (p.x - ox) / scale + config.getSpawnmargin(),
                (p.y - oy) / scale + config.getSpawnmargin()));
    }

    /**
     * Converts a point in world coordinates (e.g. the position of an enemy or tower) to screen coordinates for rendering.
     * This method takes into account the current scaling and offset of the view, and returns a Point containing the screen coordinates where the world point should be rendered.
     * @param v the point in world coordinates to convert
     * @return a Point containing the screen coordinates where the world point should be rendered
     */
    @Override
    public Point worldToScreen(Vector2D v) {
        return new Point(
                (int) (v.x() * (double) getWidth()  / config.getGameWidth()),
                (int) (v.y() * (double) getHeight() / config.getGameHeight()));
    }

    // -------------------------------------------------------------------------
    // Cursor / thema
    // -------------------------------------------------------------------------

    public void setCommandSink(Consumer<InputCommand> sink) { buildMenuPanel.setCommandSink(sink); }
    public Cursor getBuildCursor()  { return buildCursor;  }
    public Cursor getNormalCursor() { return normalCursor; }
    public Cursor getSellCursor()   { return sellCursor;   }

    public void setTheme(MapTheme theme) {
        this.currentTheme = theme;
        mapRenderer.rebuild(theme);
    }

    // -------------------------------------------------------------------------
    // Rendering
    // -------------------------------------------------------------------------

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = scaledContext((Graphics2D) g);
        graphicsContext.setGraphics2D(g2d);

        mapRenderer.drawBackground(g2d);
        mapRenderer.drawTowerSpots(g2d);
        drawEntities();
        mapRenderer.drawCastle(g2d, deltaTime);
        hudRenderer.draw(g2d, deltaTime);

        if (config.getDebug()) {
            hudRenderer.drawFps(g2d, fps);
            mapRenderer.drawPathDebug(g2d);
        }

        g2d.dispose();
    }

    // -------------------------------------------------------------------------
    // Static helper, gebruikt door entity-klassen
    // -------------------------------------------------------------------------

    public static void drawEntityHealthBar(Graphics2D g2d, double x, double y,
                                           double entityWidth, int health, int maxHealth) {
        int bw  = (int) Math.max(entityWidth, 20);
        int bh  = 3;
        int bx  = (int) (x - bw / 2.0);
        int by  = (int) (y - bh / 2.0) - 12;
        double pct = Math.clamp((double) health / maxHealth, 0, 1);

        g2d.setColor(new Color(0, 0, 0, 160));
        g2d.fillRoundRect(bx - 1, by - 1, bw + 2, bh + 2, 3, 3);
        g2d.setColor(healthColor(pct));
        g2d.fillRoundRect(bx, by, (int) (bw * pct), bh, 3, 3);
    }

    /**
     * Helper method to determine the color of the health bar based on the percentage of health remaining.
     * The color transitions smoothly from green (full health) to red (no health).
     * @param pct the percentage of health remaining, as a value between 0 and 1
     * @return a Color object representing the color of the health bar, where 1.0 is green and 0.0 is red
     */
    private static Color healthColor(double pct) {
        if (pct < 0.5) {
            return new Color(255, (int) (165 * pct * 2), 0);
        } else {
            return new Color((int) (255 * (1 - (pct - 0.5) * 2)), 255, 0);
        }
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    /**
     * Ensures that the game window is created and visible.
     * If the window has already been created, this method does nothing. Otherwise, it creates a new JFrame, sets it up with the appropriate properties (such as title, size, and close operation), and makes it visible.
     */
    private void ensureWindow() {
        if (frame != null) return;
        frame = new JFrame(config.getWindowTitle());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(this);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setUndecorated(true);
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setFocusTraversalKeysEnabled(false);
        frame.setFocusable(true);
        this.setCursor(normalCursor);
        SwingUtilities.invokeLater(this::requestFocusInWindow);
    }

    /**
     * updates the fps counter
     */
    private void updateFps() {
        fpsFrames++;
        long now = System.currentTimeMillis();
        if (now - fpsLastTime >= 1000) {
            fps = fpsFrames;
            fpsFrames = 0;
            fpsLastTime = now;
        }
    }

    private void drawEntities() {
        List<EnemyEntity>  enemies     = new ArrayList<>(game.getEnemies());
        List<Tower>        towers      = new ArrayList<>(game.getTowers());
        List<Projectile>   projectiles = new ArrayList<>(game.getProjectiles());
        enemies    .forEach(e -> e.draw(deltaTime));
        towers     .forEach(t -> t.draw(deltaTime));
        projectiles.forEach(p -> p.draw(deltaTime));
    }

    /**
     * Creates a new Graphics2D context that is scaled and translated according to the current view configuration.
     * @param g2d the original Graphics2D context to create a new context from
     * @return a new Graphics2D context that is scaled and translated to fit the game view according to the configuration settings
     */
    private Graphics2D scaledContext(Graphics2D g2d) {
        double vw = config.getViewWidth(),  vh = config.getViewHeight();
        double sw = getWidth(),             sh = getHeight();
        double scale = Math.min(sw / vw, sh / vh);
        double ox = (sw - vw * scale) / 2, oy = (sh - vh * scale) / 2;

        Graphics2D ctx = (Graphics2D) g2d.create();
        ctx.translate(ox, oy);
        ctx.scale(scale, scale);
        ctx.translate(-config.getSpawnmargin(), -config.getSpawnmargin());
        ctx.setClip(config.getSpawnmargin(), config.getSpawnmargin(),
                config.getViewWidth(),   config.getViewHeight());
        return ctx;
    }

    /**
     * Helper method to create a custom cursor from an image in the asset manager.
     * The cursor will be created with the hotspot in the top-left corner (0, 0) and the specified name.
     * @param assets the AssetManager to load the cursor image from
     * @param path the path to the cursor image in the asset manager
     * @param name the name to assign to the cursor (used for debugging purposes)
     * @return a Cursor object representing the custom cursor created from the specified image
     */
    private static Cursor makeCursor(AssetManager assets, String path, String name) {
        return Toolkit.getDefaultToolkit()
                .createCustomCursor(assets.getSprite(path), new Point(0, 0), name);
    }
}