package towerdefence.ui.menu;

import javax.swing.*;
import java.awt.*;

public class MainMenuPanel extends JPanel {
    public MainMenuPanel(Runnable onStart, Runnable onSettings) {
        setLayout(null);
        setBackground(Color.BLACK);

        JLabel title = new JLabel("Tower Defence");
        title.setForeground(Color.WHITE);
        title.setBounds(20, 20, 300, 40);
        add(title);

        JButton startButton = new JButton("Start Game");
        startButton.setBounds(280, 220, 240, 50);
        startButton.addActionListener(e -> onStart.run());
        add(startButton);

        JButton settingsButton = new JButton("Settings");
        settingsButton.setBounds(280, 290, 240, 50);
        settingsButton.addActionListener(e -> onSettings.run());
        add(settingsButton);
    }
}