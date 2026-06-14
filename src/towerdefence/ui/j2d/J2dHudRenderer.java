package towerdefence.ui.j2d;

import towerdefence.game.Game;
import towerdefence.system.config.Config;
import towerdefence.ui.j2d.assets.AssetManager;
import towerdefence.ui.j2d.assets.SpriteAnimation;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

// Responsible for drawing the HUD (health, resources, wave info, floating texts)
public class J2dHudRenderer {

    private record FloatingText(String text, Color color, double x, double y,
                                double startLifetime, double lifetime) {}

    private static final double SHAKE_DURATION  = 0.4;
    private static final int    SHAKE_INTENSITY = 4;
    private static final int    HEART_W         = 10 * 2;
    private static final int    HEART_H         = 9  * 2;
    private static final int    HEART_GAP       = 4;
    private static final double COIN_SIZE       = 8 * 2.5;
    private static final double FLOATING_RISE   = 30.0;
    private static final double FLOATING_FADE   = 0.3;

    private final Game            game;
    private final Config          config;
    private final SpriteAnimation coin;
    private final BufferedImage   full, half, empty, box;

    private final List<FloatingText> floatingTexts = new ArrayList<>();
    private double shakeTimer = 0.0;

    /**
     * initializes the HUD renderer with necessary assets and references
     * @param game reference to the main game object to access game state (lives, resources, waves)
     * @param config reference to the configuration for layout settings (like spawn margin)
     * @param assets asset manager to load necessary sprites for the HUD (hearts, coin, box)
     */
    public J2dHudRenderer(Game game, Config config, AssetManager assets) {
        this.game   = game;
        this.config = config;
        this.full   = assets.getSprite("sprites/hud/heart_full.png");
        this.half   = assets.getSprite("sprites/hud/heart_half.png");
        this.empty  = assets.getSprite("sprites/hud/heart_empty.png");
        this.box    = assets.getSprite("sprites/hud/box.png");
        this.coin   = new SpriteAnimation(assets.getSprite("sprites/hud/Coin-Sheet.png"), 4, 30);
    }

    /**
     * healthbar shake
     */
    public void triggerShake() {
        shakeTimer = SHAKE_DURATION;
    }

    /**
     * spawns floating text
     * @param text text to show
     * @param color color of the text
     * @param x x-coordinate in pixels (relative to the top-left corner of the HUD)
     * @param y y-coordinate in pixels (relative to the top-left corner of the HUD)
     */
    public void spawnFloatingText(String text, Color color, double x, double y) {
        floatingTexts.add(new FloatingText(text, color, x, y, FLOATING_FADE, FLOATING_FADE));
    }

    /**
     * draws hud
     * @param g2d graphics context to draw on
     * @param deltaTime time since last frame in seconds (used for animations and timers)
     */
    public void draw(Graphics2D g2d, double deltaTime) {
        drawHudBox(g2d);
        drawHearts(g2d, deltaTime);
        drawResources(g2d, deltaTime);
        drawWaveInfo(g2d);
        drawFloatingTexts(g2d, deltaTime);
    }

    /**
     * draws fps counter (for debugging)
     * @param g2d graphics context to draw on
     * @param fps current frames per second to display
     */
    public void drawFps(Graphics2D g2d, int fps) {
        g2d.setFont(new Font("Consolas", Font.BOLD, 18));
        g2d.setColor(Color.WHITE);
        g2d.drawString("FPS: " + fps, 650 + config.getSpawnmargin(), 50 + config.getSpawnmargin());
    }

    // -------------------------------------------------------------------------
    // Private
    // -------------------------------------------------------------------------

    /**
     * draws the background box of the HUD
     * @param g2d graphics context to draw on
     */
    private void drawHudBox(Graphics2D g2d) {
        g2d.drawImage(box, config.getSpawnmargin(), config.getSpawnmargin(), 128 * 2, 45 * 2, null);
    }

    private void drawHearts(Graphics2D g2d, double deltaTime) {
        int sx = 0, sy = 0;
        if (shakeTimer > 0) {
            sx = (int) ((Math.random() - 0.5) * 2 * SHAKE_INTENSITY);
            sy = (int) ((Math.random() - 0.5) * 2 * SHAKE_INTENSITY);
            shakeTimer = Math.max(0, shakeTimer - deltaTime);
        }

        int lives      = game.getLives();
        int maxLives   = game.getMaxLives();
        int fullHearts = lives / 2;
        boolean hasHalf = (lives % 2) != 0;
        int total      = maxLives / 2;

        for (int i = 0; i < total; i++) {
            BufferedImage sprite = (i < fullHearts) ? full
                    : (i == fullHearts && hasHalf) ? half
                      : empty;
            int x = 10 + config.getSpawnmargin() + i * (HEART_W + HEART_GAP) + sx;
            int y = 10 + config.getSpawnmargin() + sy;
            g2d.drawImage(sprite, x, y, HEART_W, HEART_H, null);
        }
    }

    private void drawResources(Graphics2D g2d, double deltaTime) {
        coin.update(deltaTime);
        BufferedImage frame = coin.getCurrentFrame();
        int x = 10 + config.getSpawnmargin();
        int y = 40 + config.getSpawnmargin();
        g2d.drawImage(frame, x, y, (int) COIN_SIZE, (int) COIN_SIZE, null);
        g2d.setColor(new Color(255, 182, 27));
        g2d.setFont(new Font("Consolas", Font.BOLD, 17));
        g2d.drawString(String.valueOf(game.getResources()), x + 28, y + 17);
    }

    private void drawWaveInfo(Graphics2D g2d) {
        if (game.isGameNotRunning()) return;

        int    currentWave = game.getCurrentWave();
        int    totalWaves  = game.getTotalWaves();
        double countdown   = game.getNextWaveCountdown();
        int    rightEdge   = config.getSpawnmargin() + (game.getMaxLives() / 2) * (HEART_W + HEART_GAP);
        int    y           = 40 + config.getSpawnmargin();

        g2d.setFont(new Font("Consolas", Font.BOLD, 17));
        g2d.setColor(Color.WHITE);
        String waveText = "Wave " + currentWave + " / " + totalWaves;
        FontMetrics fm  = g2d.getFontMetrics();
        g2d.drawString(waveText, rightEdge - fm.stringWidth(waveText), y + 17);

        if (countdown > 0) {
            g2d.setFont(new Font("Consolas", Font.PLAIN, 13));
            g2d.setColor(new Color(255, 182, 27));
            FontMetrics fmSmall = g2d.getFontMetrics();
            String next = "Next: " + String.format("%.0f", countdown) + "s";
            g2d.drawString(next, rightEdge - fmSmall.stringWidth(next), y + 33);
        }
    }

    private void drawFloatingTexts(Graphics2D g2d, double deltaTime) {
        g2d.setFont(new Font("Consolas", Font.BOLD, 14));
        List<FloatingText> next = new ArrayList<>(floatingTexts.size());

        for (FloatingText ft : floatingTexts) {
            double remaining = ft.lifetime() - deltaTime;
            if (remaining <= 0) continue;

            double progress = 1.0 - (ft.lifetime() / ft.startLifetime());
            int    offsetY  = (int) (progress * FLOATING_RISE);
            int    alpha    = (int) (255 * Math.min(1.0, ft.lifetime() / FLOATING_FADE));
            Color  c        = ft.color();

            g2d.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), Math.max(0, alpha)));
            g2d.drawString(ft.text(), (int) ft.x(), (int) ft.y() - offsetY);
            next.add(new FloatingText(ft.text(), c, ft.x(), ft.y(), ft.startLifetime(), remaining));
        }

        floatingTexts.clear();
        floatingTexts.addAll(next);
    }
}