package towerdefence.ui.menu;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

// create a dropdownmenu of parameter T
public class DropdownMenu<T> extends JComboBox<T> {

    private static final int ARC = 18;
    private static final Color BG = new Color(50, 50, 50);
    private static final Color BG_HOVER = new Color(70, 70, 70);
    private static final Color BORDER = new Color(255, 255, 255, 60);
    private static final Color POPUP_BG = new Color(40, 40, 40);
    private static final Color SELECT_BG = new Color(52, 120, 246);
    private static final Color ARROW_BG = new Color(60, 60, 60);

    private Shape shape;
    private JList<?> popupList;

    /**
     * creates a dropdown menu with the given items and default styling
     *
     * @param items the items to display in the dropdown menu, can have different types
     */
    public DropdownMenu(T[] items) {
        this(items, 220, 40, 14);
    }

    /**
     * creates a dropdown menu with the given items and custom styling
     *
     * @param items    the items to display in the dropdown menu, can have different types
     * @param width    the width of the dropdown
     * @param height   the height of the dropdown
     * @param fontSize the font size of the dropdown items
     */
    public DropdownMenu(T[] items, int width, int height, int fontSize) {
        super(items);
        setOpaque(false);
        setFocusable(false);
        setFont(new Font("Consolas", Font.PLAIN, fontSize));
        setForeground(Color.WHITE);
        setBackground(BG);
        setPreferredSize(new Dimension(width, height));
        setMaximumSize(new Dimension(width, height));
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        setUI(buildUI());
        setRenderer(buildRenderer());
        popupList.setCursor(getCursor());
    }

    // -------------------------------------------------------------------------
    // Painting
    // -------------------------------------------------------------------------

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(BG);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), ARC, ARC);

        g2.setColor(BORDER);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, ARC, ARC);

        Object selected = getSelectedItem();
        if (selected != null) {
            g2.setColor(Color.WHITE);
            g2.setFont(getFont());
            FontMetrics fm = g2.getFontMetrics();
            String text = selected.toString();
            int textY = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
            g2.drawString(text, 12, textY);
        }

        g2.dispose();
    }

    // not needed cause is drawn in paintcomponent
    @Override
    protected void paintBorder(Graphics g) {
    }

    @Override
    public boolean contains(int x, int y) {
        if (shape == null || !shape.getBounds().equals(getBounds())) {
            shape = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), ARC, ARC);
        }
        return shape.contains(x, y);
    }

    /**
     * builds a custom UI for the dropdown menu that includes a custom arrow button and a custom popup menu with specific styling for the list items,
     * providing a cohesive and visually appealing user interface consistent with the overall design of the game
     *
     * @return a BasicComboBoxUI with custom implementations for the arrow button and popup menu, as well as an overridden method to prevent drawing the default background of the selected item in the combo box, allowing for a fully customized appearance of the dropdown menu
     */
    // AI
    private BasicComboBoxUI buildUI() {
        return new BasicComboBoxUI() {

            // override arrowbutton
            @Override
            protected JButton createArrowButton() {
                JButton btn = new JButton() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                        g2.setColor(getModel().isRollover() ? BG_HOVER : ARROW_BG);
                        g2.fillRoundRect(-ARC, 0, getWidth() + ARC, getHeight(), ARC, ARC);

                        int cx = getWidth() / 2;
                        int cy = getHeight() / 2;
                        int[] px = {cx - 5, cx + 5, cx};
                        int[] py = {cy - 3, cy - 3, cy + 4};
                        g2.setColor(Color.WHITE);
                        g2.fillPolygon(px, py, 3);

                        g2.dispose();
                    }
                };
                btn.setPreferredSize(new Dimension(30, 0));
                btn.setBorderPainted(false);
                btn.setFocusPainted(false);
                btn.setContentAreaFilled(false);
                btn.setRolloverEnabled(true);
                return btn;
            }

            // override popup
            @Override
            protected ComboPopup createPopup() {
                BasicComboPopup popup = new BasicComboPopup(comboBox) {
                    @Override
                    protected void configurePopup() {
                        super.configurePopup();
                        setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80)));
                        setBackground(POPUP_BG);
                    }
                };
                popup.getList().setBackground(POPUP_BG);
                popup.getList().setForeground(Color.WHITE);
                popup.getList().setFont(new Font("Consolas", Font.PLAIN, 14));
                popup.getList().setSelectionBackground(SELECT_BG);
                popup.getList().setSelectionForeground(Color.WHITE);
                popupList = popup.getList();
                return popup;
            }

            @Override
            public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
            }
        };
    }

    @Override
    public void setCursor(Cursor cursor) {
        super.setCursor(cursor);
        if (popupList != null) popupList.setCursor(cursor);
    }

    /**
     * builds a custom cell renderer for the dropdown menu that styles the list items with specific foreground and background colors, font, and padding,
     * ensuring that the items in the dropdown menu are visually consistent with the overall design of the game and provide a clear indication of which item is currently selected or hovered over by the user
     *
     * @return a DefaultListCellRenderer that customizes the appearance of the list items in the dropdown menu, including foreground and background colors, font, and padding, to create a visually appealing and cohesive user interface for the dropdown menu
     */
    private DefaultListCellRenderer buildRenderer() {
        return new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                JLabel lbl = (JLabel) super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);
                lbl.setForeground(Color.WHITE);
                lbl.setBackground(isSelected ? SELECT_BG : POPUP_BG);
                lbl.setFont(new Font("Consolas", Font.PLAIN, 14));
                lbl.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 8));
                lbl.setOpaque(true);
                return lbl;
            }
        };
    }
}