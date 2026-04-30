package towerdefence.ui.j2d;

import towerdefence.game.GameState;
import towerdefence.system.config.Config;
import towerdefence.ui.menu.MenuButton;
import towerdefence.ui.menu.MenuScreen;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class J2dMenuRenderer {
    public void drawMenu(Graphics2D g2d, MenuScreen menuScreen, GameState gameState, Config config, Point mouse) {
        g2d.setColor(new Color(0,0,0,170));
        g2d.fillRect(0,0,config.getGameWidth(), config.getGameHeight());

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Consolas", Font.BOLD, 42));
        g2d.drawString(menuScreen.getTitle(), 230, 150);

        g2d.setFont(new Font("Consolas", Font.BOLD, 22));

        for (MenuButton button : menuScreen.getVisibleButtons(gameState)) {
            if (mouse != null){
                if (button.isEnabledIn(gameState)) {
                    if (button.contains(mouse)) {
                        g2d.setColor(button.getHoverColor());
                    } else {
                        g2d.setColor(button.getBaseColor());
                    }
                } else {
                    g2d.setColor(button.getDisabledColor());
                }
            }

            Rectangle2D.Double bounds = button.getBounds();
            g2d.fill(bounds);

            g2d.setColor(Color.WHITE);
            g2d.draw(bounds);

            int tx = (int) (bounds.x+18);
            int ty = (int) (bounds.y+32);
            g2d.setColor(button.getTextColor());
            g2d.drawString(button.getLabel(), tx, ty);
        }
    }
}
