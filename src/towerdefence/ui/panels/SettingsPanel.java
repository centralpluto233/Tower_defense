package towerdefence.ui.menu;

import javax.swing.*;
import java.awt.*;

public class SettingsPanel extends JPanel {
    public SettingsPanel(Runnable onBack) {
        setLayout(null);
        setBackground(Color.DARK_GRAY);

        JLabel label = new JLabel("Settings");
        label.setForeground(Color.WHITE);
        label.setBounds(20, 20, 300, 40);
        add(label);

        JButton backButton = new JButton("Back");
        backButton.setBounds(280, 360, 240, 50);
        backButton.addActionListener(e -> onBack.run());
        add(backButton);
    }
}