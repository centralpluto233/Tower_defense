package towerdefence.ui.j2d;

import towerdefence.game.Game;
import towerdefence.level.TileType;
import towerdefence.system.config.Config;
import towerdefence.ui.j2d.assets.AssetManager;
import towerdefence.ui.j2d.assets.MapTheme;
import towerdefence.ui.j2d.assets.SpriteAnimation;
import towerdefence.util.Vector2D;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

public class J2dMapRenderer {

    private record Decoration(BufferedImage sprite, int x, int y) {}
    private record Tree(int x, int y, int type, double scale) {}

    private static final int    TREE_COUNT = 25;
    private static final int    TREE_SEED  = 18;
    private static final double TREE_SCALE_BASE = 1.5;
    private static final double CASTLE_W = 52 * 2.5;
    private static final double CASTLE_H = 38 * 2.5;

    private final Game         game;
    private final Config       config;
    private final AssetManager assets;

    private final List<Decoration>            decorations = new ArrayList<>();
    private final List<Tree>                  trees       = new ArrayList<>();
    private final List<BufferedImage>         decSprites;
    private final Map<TileType, BufferedImage> pathTiles  = new EnumMap<>(TileType.class);
    private final SpriteAnimation             castle;

    private BufferedImage backgroundCache;
    private MapTheme      currentTheme;
    private final int     pathClearance;

    public J2dMapRenderer(Game game, Config config, AssetManager assets, MapTheme initialTheme) {
        this.game         = game;
        this.config       = config;
        this.assets       = assets;
        this.currentTheme = initialTheme;
        this.pathClearance = config.getTileSize();

        this.decSprites = List.of(
                assets.getSprite("sprites/map/decoration/mushroom.png"),
                assets.getSprite("sprites/map/decoration/mushroom.png"),
                assets.getSprite("sprites/map/decoration/rock_1.png"),
                assets.getSprite("sprites/map/decoration/rock_2.png"),
                assets.getSprite("sprites/map/decoration/rock_3.png")
        );

        this.castle = new SpriteAnimation(assets.getSprite("sprites/map/castle.png"), 4, 30);

        rebuildAll();
    }

    /**
     * Rebuilds background cache and decorations when the theme changes.
     * @param theme the new map theme to apply
     */
    public void rebuild(MapTheme theme) {
        this.currentTheme = theme;
        rebuildAll();
    }

    /**
     * draws background (decorations, path)
     * @param g2d the Graphics2D context to draw on
     */
    public void drawBackground(Graphics2D g2d) {
        g2d.drawImage(backgroundCache, 0, 0, null);
    }

    public void drawTowerSpots(Graphics2D g2d) {
        BufferedImage spot = assets.getSprite("sprites/map/" + currentTheme.folder() + "/towerspot.png");
        List<Vector2D> spots = game.getTowerSpots();
        if (spots == null || spots.isEmpty()) return;

        int size = (int) (config.getTileSize() * 0.8);
        for (Vector2D s : spots) {
            if (game.towerSpotOccupied(s)) continue;
            g2d.drawImage(spot, (int) s.x() - size / 2, (int) s.y() - size / 2, size, size, null);
        }
    }

    public void drawCastle(Graphics2D g2d, double deltaTime) {
        castle.update(deltaTime);
        BufferedImage frame = castle.getCurrentFrame();
        Vector2D end = game.getPaths().getFirst().getLast();
        int w = (int) CASTLE_W;
        int h = (int) CASTLE_H;
        g2d.drawImage(frame,
                (int) end.x() - w / 2,
                (int) end.y() - 5 * h / 6,
                w, h, null);
    }

    /**
     * Draws semi-transparent colored overlays on path tiles for debugging purposes.
     * @param g2d the Graphics2D context to draw on
     */
    public void drawPathDebug(Graphics2D g2d) {
        if (game.getGrid() == null) return;
        int ts     = config.getTileSize();
        int margin = config.getSpawnmargin();
        g2d.setFont(new Font("Consolas", Font.BOLD, 8));

        for (int row = 0; row < game.getGrid().getRows(); row++) {
            for (int col = 0; col < game.getGrid().getCols(); col++) {
                TileType type = game.getGrid().getTile(row, col);
                if (type == TileType.GRASS) continue;
                int x = margin + col * ts, y = margin + row * ts;
                g2d.setColor(debugColor(type));
                g2d.fillRect(x, y, ts, ts);
                g2d.setColor(Color.WHITE);
                g2d.drawString(debugLabel(type), x + 2, y + ts - 2);
            }
        }
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    /**
     * Rebuilds path tiles, decorations, trees, and background cache. Should be called whenever the map theme changes.
     */
    private void rebuildAll() {
        loadPathTiles();
        decorations.clear();
        generateDecorations();
        trees.clear();
        generateTrees();
        buildBackgroundCache();
    }

    /**
     * Loads path tile images from the current theme's sprite sheet and maps them to their corresponding TileType.
     */
    private void loadPathTiles() {
        pathTiles.clear();
        BufferedImage s = assets.getSprite("sprites/map/" + currentTheme.folder() + "/path.png");
        int t = 16;
        pathTiles.put(TileType.CORNER_SE, s.getSubimage(0,     0, t, t));
        pathTiles.put(TileType.PATH_H,    s.getSubimage(t,     0, t, t));
        pathTiles.put(TileType.CORNER_SW, s.getSubimage(2*t,   0, t, t));
        pathTiles.put(TileType.PATH_V,    s.getSubimage(0,     t, t, t));
        pathTiles.put(TileType.CORNER_NE, s.getSubimage(0,   2*t, t, t));
        pathTiles.put(TileType.CORNER_NW, s.getSubimage(2*t, 2*t, t, t));
        pathTiles.put(TileType.T_NES,     s.getSubimage(0,   4*t, t, t));
        pathTiles.put(TileType.T_WES,     s.getSubimage(t,   3*t, t, t));
        pathTiles.put(TileType.T_WNE,     s.getSubimage(0,   3*t, t, t));
        pathTiles.put(TileType.T_WNS,     s.getSubimage(2*t, 3*t, t, t));
        pathTiles.put(TileType.CROSS,     s.getSubimage(t,   4*t, t, t));
    }

    /**
     * Builds the background cache by tiling grass, drawing the path, decorations, and trees. This is called whenever the theme changes to optimize rendering performance.
     */
    private void buildBackgroundCache() {
        BufferedImage grass = assets.getSprite("sprites/map/" + currentTheme.folder() + "/grass.png");
        backgroundCache = new BufferedImage(
                config.getGameWidth(), config.getGameHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = backgroundCache.createGraphics();
        tileGrass(g, grass);
        drawPath(g);
        drawDecorations(g);
        drawTrees(g);
        g.dispose();
    }

    /**
     * Tiles the given grass image across the entire background cache to create a seamless grassy background.
     * @param g the Graphics2D context of the background cache to draw on
     * @param tile the grass tile image to be repeated across the background
     */
    private void tileGrass(Graphics2D g, BufferedImage tile) {
        int ts   = config.getTileSize();
        int cols = (int) Math.ceil((double) config.getGameWidth()  / ts);
        int rows = (int) Math.ceil((double) config.getGameHeight() / ts);
        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++)
                g.drawImage(tile, c * ts, r * ts, ts, ts, null);
    }

    private void drawPath(Graphics2D g) {
        if (game.getGrid() == null) return;
        int ts     = config.getTileSize();
        int margin = config.getSpawnmargin();
        for (int row = 0; row < game.getGrid().getRows(); row++) {
            for (int col = 0; col < game.getGrid().getCols(); col++) {
                BufferedImage tile = pathTiles.get(game.getGrid().getTile(row, col));
                if (tile == null) continue;
                g.drawImage(tile,
                        margin + col * ts, margin + row * ts,
                        margin + col * ts + ts, margin + row * ts + ts,
                        0, 0, tile.getWidth(), tile.getHeight(), null);
            }
        }
    }

    private void drawDecorations(Graphics2D g) {
        for (Decoration d : decorations) {
            g.drawImage(d.sprite(), d.x(), d.y(), null);
        }
    }

    /**
     * Draws trees on the background cache based on their type and scale.
     * Trees are randomly generated but will not be placed too close to the path to avoid visual clutter.
     * @param g the Graphics2D context of the background cache to draw on
     */
    private void drawTrees(Graphics2D g) {
        String folder = "sprites/map/" + currentTheme.folder() + "/";
        BufferedImage[] sprites = {
                assets.getSprite(folder + "tree1.png"),
                assets.getSprite(folder + "tree2.png")
        };
        for (Tree t : trees) {
            BufferedImage sp = sprites[t.type()];
            int w = (int) (sp.getWidth()  * t.scale());
            int h = (int) (sp.getHeight() * t.scale());
            g.drawImage(sp, t.x() - w / 2, t.y() - h / 2, w, h, null);
        }
    }

    /**
     * Generates random tree positions (with fixed seed) and types while ensuring they are not placed too close to the path.
     * The number of trees and their distribution is determined by a fixed seed for consistency across runs.
     */
    private void generateTrees() {
        Random rng = new Random(TREE_SEED);
        for (int i = 0; i < TREE_COUNT; i++) {
            int x = rng.nextInt(config.getGameWidth());
            int y = rng.nextInt(config.getGameHeight());
            if (isTooCloseToPath(x, y)) continue;
            // pick between 2 three types
            trees.add(new Tree(x, y, rng.nextInt(2), TREE_SCALE_BASE + rng.nextDouble()));
        }
    }

    /**
     * Generates random decoration positions while ensuring they are not placed too close to the path.
     */
    private void generateDecorations() {
        Random rng = new Random();
        int count = (int) (config.getGameWidth() * config.getGameHeight() * 0.12 / 1000);
        for (int i = 0; i < count; i++) {
            int x = rng.nextInt(config.getGameWidth());
            int y = rng.nextInt(config.getGameHeight());
            if (isTooCloseToPath(x, y)) continue;
            decorations.add(new Decoration(decSprites.get(rng.nextInt(decSprites.size())), x, y));
        }
    }

    /**
     * Checks if the given coordinates (cx, cy) are within a certain distance (pathClearance) from any segment of the path.
     * @param cx the x-coordinate to check
     * @param cy the y-coordinate to check
     * @return true if the point is too close to the path, false otherwise
     */
    private boolean isTooCloseToPath(int cx, int cy) {
        for (List<Vector2D> path : game.getPaths()) {
            for (int i = 0; i < path.size() - 1; i++) {
                if (distToSegment(cx, cy, path.get(i), path.get(i + 1)) < pathClearance)
                    return true;
            }
        }
        return false;
    }

    /**
     * Calculates the shortest distance from a point (px, py) to a line segment defined by points a and b.
     * @param px point x
     * @param py point y
     * @param a segment start point
     * @param b segment end point
     * @return the shortest distance from (px, py) to the line segment ab
     */
    // AI
    private double distToSegment(double px, double py, Vector2D a, Vector2D b) {
        double dx = b.x() - a.x();
        double dy = b.y() - a.y();
        if (dx == 0 && dy == 0) {
            return Math.hypot(px - a.x(), py - a.y());
        }
        double t = Math.clamp(((px - a.x()) * dx + (py - a.y()) * dy) / (dx * dx + dy * dy), 0, 1);
        return Math.hypot(px - (a.x() + t * dx), py - (a.y() + t * dy));
    }

    /**
     * Returns a semi-transparent color corresponding to the given TileType for debugging purposes.
     * @param type the TileType to get the debug color for
     * @return a Color object representing the debug color for the given TileType
     */
    private static Color debugColor(TileType type) {
        return switch (type) {
            case PATH_H    -> new Color(200, 100,   0, 180);
            case PATH_V    -> new Color(  0, 100, 200, 180);
            case CORNER_SE -> new Color(200,   0,   0, 180);
            case CORNER_SW -> new Color(  0, 200,   0, 180);
            case CORNER_NE -> new Color(200, 200,   0, 180);
            case CORNER_NW -> new Color(200,   0, 200, 180);
            case T_NES     -> new Color(255, 128,   0, 180);
            case T_WES     -> new Color(  0, 255, 128, 180);
            case T_WNE     -> new Color(128,   0, 255, 180);
            case T_WNS     -> new Color(  0, 128, 255, 180);
            case CROSS     -> new Color(255, 255, 255, 180);
            default        -> new Color(128, 128, 128, 180);
        };
    }

    /**
     * Returns a short label for the given TileType for debugging purposes.
     * @param type the TileType to get the debug label for
     * @return a String representing the debug label for the given TileType
     */
    private static String debugLabel(TileType type) {
        return switch (type) {
            case PATH_H    -> "H";
            case PATH_V    -> "V";
            case CORNER_SE -> "SE";
            case CORNER_SW -> "SW";
            case CORNER_NE -> "NE";
            case CORNER_NW -> "NW";
            case T_NES     -> "TNES";
            case T_WES     -> "TWES";
            case T_WNE     -> "TWNE";
            case T_WNS     -> "TWNS";
            case CROSS     -> "X";
            default        -> "?";
        };
    }
}