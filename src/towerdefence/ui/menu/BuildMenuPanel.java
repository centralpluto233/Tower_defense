package towerdefence.ui.menu;

import towerdefence.game.Game;
import towerdefence.game.entities.tower.TowerOption;
import towerdefence.system.config.Constants;
import towerdefence.util.Vector2D;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class BuildMenuPanel extends JPanel {
    private Vector2D selectedSpot;
    private Game game;

    private final JPanel towerOptionPanel = new JPanel();

    private static final List<TowerOption> towerOptions = List.of(
            new TowerOption("Archer", Constants.TowerConstants.Archer.COST),
            new TowerOption("Crossbow", Constants.TowerConstants.CrossBow.COST)
    );

    public BuildMenuPanel(Game game) {
        setOpaque(false);
        setLayout(null);

        towerOptionPanel.setLayout(new BoxLayout(towerOptionPanel, BoxLayout.Y_AXIS));
        towerOptionPanel.setBackground(new Color(30,30,30,220));
        towerOptionPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        for (TowerOption towerOption : towerOptions) {
            JButton button = new JButton(towerOption.type());
            button.addActionListener(_ -> {
                game.placeTower(towerOption.type(), selectedSpot);
            hide();
            });
            towerOptionPanel.add(button);
        }

        add(towerOptionPanel);
        setVisible(false);
    }

    public void showAt(Vector2D spot, Point screenPos) {
        this.selectedSpot = spot;
        towerOptionPanel.setSize(towerOptionPanel.getPreferredSize());

        int px = Math.min(screenPos.x, getWidth()  - towerOptionPanel.getWidth()  - 8);
        int py = Math.min(screenPos.y, getHeight() - towerOptionPanel.getHeight() - 8);
        towerOptionPanel.setLocation(px, py);

        setVisible(true);
        repaint();
    }

    public void hide() {
        setVisible(false);
    }

    @Override
    public boolean contains(int x, int y) {
        if (!isVisible()) return false;
        Point p = SwingUtilities.convertPoint(this, x, y, towerOptionPanel);
        return towerOptionPanel.contains(p);
    }
}
