package towerdefence.ui.menu;

import towerdefence.game.Game;
import towerdefence.game.GameListener;
import towerdefence.game.entities.tower.Tower;
import towerdefence.game.entities.tower.TowerOption;
import towerdefence.game.input.InputCommand;
import towerdefence.system.config.Constants;
import towerdefence.ui.input.InputMapping;
import towerdefence.ui.j2d.assets.AssetManager;
import towerdefence.util.Vector2D;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class BuildMenuPanel extends JPanel implements GameListener {
    private static final List<TowerOption> towerOptions = List.of(
            new TowerOption("Archer", Constants.TowerConstants.Archer.COST,
                    "sprites/entities/towers/archer_icon.png",
                    Constants.TowerConstants.Archer.WIDTH,
                    Constants.TowerConstants.Archer.HEIGHT),
            new TowerOption("Crossbow", Constants.TowerConstants.CrossBow.COST,
                    "sprites/entities/towers/crossbow.png",
                    Constants.TowerConstants.CrossBow.WIDTH,
                    Constants.TowerConstants.CrossBow.HEIGHT),
            new TowerOption("Cannon", Constants.TowerConstants.Cannon.COST,
                    "sprites/entities/towers/cannon.png",
                    Constants.TowerConstants.Cannon.WIDTH,
                    Constants.TowerConstants.Cannon.HEIGHT),
            new TowerOption("Wizard", Constants.TowerConstants.Wizard.COST,
                    "sprites/entities/towers/wizard.png",
                    Constants.TowerConstants.Wizard.WIDTH,
                    Constants.TowerConstants.Wizard.HEIGHT)
    );
    private final InputMapping inputMapping;
    private final JPanel towerOptionPanel = new JPanel();
    private final Cursor hoverCursor;
    private final Cursor noMoneyCursor;
    private final Game game;
    private Consumer<InputCommand> commandSink = _ -> {
    };
    private Optional<Integer> selectedSpotIndex = Optional.empty();

    /**
     * Constructs the build menu panel.
     *
     * @param game          The game instance to query for resources and listen to game events.
     * @param inputMapping  The input mapping to convert world coordinates to screen coordinates for menu positioning.
     * @param assetManager  The asset manager to load tower icons for the menu.
     * @param hoverCursor   The cursor to show when hovering over an affordable tower option.
     * @param noMoneyCursor The cursor to show when hovering over an unaffordable tower option.
     */
    public BuildMenuPanel(Game game, InputMapping inputMapping, AssetManager assetManager, Cursor hoverCursor, Cursor noMoneyCursor) {
        this.inputMapping = inputMapping;
        this.hoverCursor = hoverCursor;
        this.noMoneyCursor = noMoneyCursor;
        this.game = game;
        setOpaque(false);
        setLayout(null);

        towerOptionPanel.setLayout(new BoxLayout(towerOptionPanel, BoxLayout.Y_AXIS));
        towerOptionPanel.setBackground(new Color(30, 30, 30, 220));
        towerOptionPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        for (TowerOption towerOption : towerOptions) {
            JButton button = createTowerCard(towerOption, assetManager);
            button.addActionListener(_ -> {
                if (selectedSpotIndex.isPresent()) {
                    commandSink.accept(InputCommand.placeTowerAtSpot(selectedSpotIndex.get(), towerOption.type()));
                    closeBuildMenu();
                }
            });
            towerOptionPanel.add(button);
            towerOptionPanel.add(Box.createVerticalStrut(4));
        }

        add(towerOptionPanel);
        setVisible(false);
    }

    /**
     * Creates a JButton representing a tower option in the build menu, with custom rendering to show the tower icon, name, and cost, as well as visual feedback for affordability and hover state.
     *
     * @param option The tower option to create a button for.
     * @param assets The asset manager to load the tower icon sprite.
     * @return A JButton configured to represent the tower option in the build menu.
     */
    private JButton createTowerCard(TowerOption option, AssetManager assets) {
        java.awt.image.BufferedImage sprite = assets.getSprite(option.spritePath());
        Image icon = scaleForMenu(sprite, option, 40);

        JButton btn = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                boolean canAfford = game.getResources() >= option.cost();
                // mouse over model
                boolean rollover = getModel().isRollover();

                if (!canAfford) {
                    if (rollover) {
                        g2.setColor(new Color(110, 40, 40, 230));
                    } else {
                        g2.setColor(new Color(80, 30, 30, 200));
                    }
                } else {
                    if (rollover) {
                        g2.setColor(new Color(80, 80, 80, 220));
                    } else {
                        g2.setColor(new Color(50, 50, 50, 200));
                    }
                }

                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

                int iconW = icon.getWidth(null);
                int iconH = icon.getHeight(null);
                int iconX = 8 + (40 - iconW) / 2;
                int iconY = 8 + (40 - iconH) / 2;
                g2.drawImage(icon, iconX, iconY, null);

                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Arial", Font.BOLD, 13));
                g2.drawString(option.type(), 56, 22);

                if (canAfford) {
                    g2.setColor(new Color(255, 210, 60));
                } else {
                    g2.setColor(new Color(255, 120, 120));
                }
                g2.setFont(new Font("Arial", Font.PLAIN, 12));
                g2.drawString("" + option.cost(), 56, 40);

                g2.dispose();
            }
        };

        btn.setPreferredSize(new Dimension(160, 56));
        btn.setMaximumSize(new Dimension(160, 56));
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        // custom cursors
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                boolean canAfford = game.getResources() >= option.cost();
                btn.setCursor(canAfford ? hoverCursor : noMoneyCursor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setCursor(null);
            }
        });
        return btn;
    }

    /**
     * Scales the given source image to fit within a specified box size for display in the build menu, maintaining the aspect ratio based on the tower's game height.
     *
     * @param src     the source to scale
     * @param option  the tower option to scale for
     * @param boxSize the size of the box to fit the image into
     * @return a scaled Image instance that fits within the specified box size while maintaining the aspect ratio
     */
    private Image scaleForMenu(BufferedImage src, TowerOption option, int boxSize) {
        double tallest = towerOptions.stream()
                .mapToDouble(TowerOption::gameHeight)
                .max().orElse(1);
        double scale = (boxSize / tallest) * option.gameHeight() / src.getHeight();
        int newW = (int) (src.getWidth() * scale);
        int newH = (int) (src.getHeight() * scale);
        return src.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
    }

    /**
     * Shows the build menu at the specified screen position, adjusting its location to ensure it remains fully visible within the parent panel's bounds.
     *
     * @param screenPos the screen position where the build menu should be displayed, typically corresponding to the location of a tower spot in the game world
     */
    public void showAt(Point screenPos) {
        towerOptionPanel.setSize(towerOptionPanel.getPreferredSize());

        int px = Math.min(screenPos.x, getWidth() - towerOptionPanel.getWidth() - 8);
        int py = Math.min(screenPos.y, getHeight() - towerOptionPanel.getHeight() - 8);
        towerOptionPanel.setLocation(px, py);

        setVisible(true);
        repaint();
    }

    public void setCommandSink(Consumer<InputCommand> sink) {
        this.commandSink = sink;
    }

    public void closeBuildMenu() {
        setVisible(false);
        selectedSpotIndex = Optional.empty();
    }

    @Override
    public boolean contains(int x, int y) {
        if (!isVisible()) return false;
        Point p = SwingUtilities.convertPoint(this, x, y, towerOptionPanel);
        return towerOptionPanel.contains(p);
    }

    @Override
    public void onBuildMenuRequested(int spotIndex, Vector2D spotPosition) {
        this.selectedSpotIndex = Optional.of(spotIndex);
        Point screenPos = inputMapping.worldToScreen(spotPosition);
        showAt(screenPos);
    }


    @Override
    public void onGameStarted() {
    }

    @Override
    public void onGamePaused() {
        closeBuildMenu();
    }

    @Override
    public void onGameResumed() {
    }

    @Override
    public void onGameStopped() {
        closeBuildMenu();
    }

    @Override
    public void onGameOver() {
        closeBuildMenu();
    }

    @Override
    public void onLevelWon() {
        closeBuildMenu();
    }

    @Override
    public void onBuildMenuClosed() {
        closeBuildMenu();
    }

    @Override
    public void onTowerSelected(Tower tower, int index) {
        closeBuildMenu();
    }
}
