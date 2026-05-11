package GameGUI.ui;

import GameGUI.model.entity.base.Combatant;
import GameGUI.model.entity.data.EnemyData;
import GameGUI.model.entity.data.HeroData;
import GameGUI.model.entity.data.definition.HeroDefinition;

import javax.swing.*;
import java.awt.*;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public class MenuDialog extends JDialog {

    private static final Color BG_DARK    = new Color(15, 15, 15);
    private static final Color PANEL_DARK = new Color(25, 25, 25);
    private static final Color GOLD       = new Color(160, 140, 90);
    private static final Color TEXT_DIM   = new Color(130, 120, 100);

    public MenuDialog(Window parent, Combatant hero, HeroDefinition heroDef,
                      Combatant enemy, EnemyData enemyDef,
                      Runnable onUpdate, Supplier<String> bossTauntSupplier,
                      BooleanSupplier isBossCheck, Runnable onFullyClose) {
        super(parent, "Menu", ModalityType.APPLICATION_MODAL);
        setUndecorated(true);

        boolean isBoss = isBossCheck != null && isBossCheck.getAsBoolean();
        setSize(420, isBoss ? 400 : 340);
        setLocationRelativeTo(parent);

        JPanel root = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_DARK);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(GOLD);
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 20, 20);
            }
        };
        root.setOpaque(false);
        root.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));
        setContentPane(root);

        JLabel title = new JLabel("Menu", SwingConstants.CENTER);
        title.setFont(new Font("Georgia", Font.BOLD, 26));
        title.setForeground(GOLD);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
        root.add(title, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);

        // Boss taunt banner
        if (isBoss && bossTauntSupplier != null) {
            String taunt = bossTauntSupplier.get();
            JLabel tauntLabel = new JLabel(
                    "<html><div style='text-align:center; width:320px;'>" + taunt + "</div></html>",
                    SwingConstants.CENTER);
            tauntLabel.setFont(new Font("Georgia", Font.ITALIC, 13));
            tauntLabel.setForeground(new Color(200, 80, 80));
            tauntLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            tauntLabel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(140, 40, 40), 1),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)));
            centerPanel.add(tauntLabel);
            centerPanel.add(Box.createVerticalStrut(14));
        }

        JButton inventoryBtn   = menuBtn("Inventory");
        JButton playerStatsBtn = menuBtn("Player Stats");
        JButton enemyStatsBtn  = menuBtn("Enemy Stats");

        // Each sub-dialog calls onFullyClose when IT closes — timer stays paused the whole time
        inventoryBtn.addActionListener(e -> {
            dispose();
            InventoryDialog inv = new InventoryDialog(
                    (JFrame) SwingUtilities.getWindowAncestor((Component) parent), hero, onUpdate);
            inv.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override public void windowClosed(java.awt.event.WindowEvent ev) {
                    if (onFullyClose != null) onFullyClose.run();
                }
            });
            inv.setVisible(true);
        });

        playerStatsBtn.addActionListener(e -> {
            dispose();
            PlayerStatsDialog ps = new PlayerStatsDialog(parent, hero, heroDef);
            ps.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override public void windowClosed(java.awt.event.WindowEvent ev) {
                    if (onFullyClose != null) onFullyClose.run();
                }
            });
            ps.setVisible(true);
        });

        enemyStatsBtn.addActionListener(e -> {
            dispose();
            EnemyStatsDialog es = new EnemyStatsDialog(parent, enemy, enemyDef);
            es.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override public void windowClosed(java.awt.event.WindowEvent ev) {
                    if (onFullyClose != null) onFullyClose.run();
                }
            });
            es.setVisible(true);
        });

        centerPanel.add(inventoryBtn);
        centerPanel.add(Box.createVerticalStrut(12));
        centerPanel.add(playerStatsBtn);
        centerPanel.add(Box.createVerticalStrut(12));
        centerPanel.add(enemyStatsBtn);
        root.add(centerPanel, BorderLayout.CENTER);

        // Close button — also resumes timer
        JButton closeBtn = new JButton("  Back to Battle");
        closeBtn.setFont(new Font("Georgia", Font.BOLD, 13));
        closeBtn.setForeground(TEXT_DIM);
        closeBtn.setBackground(BG_DARK);
        closeBtn.setFocusPainted(false);
        closeBtn.setBorderPainted(false);
        closeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        closeBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { closeBtn.setForeground(GOLD); }
            public void mouseExited (java.awt.event.MouseEvent e) { closeBtn.setForeground(TEXT_DIM); }
        });
        closeBtn.addActionListener(e -> {
            dispose();
            if (onFullyClose != null) onFullyClose.run();
        });

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setOpaque(false);
        bottomPanel.add(closeBtn);
        root.add(bottomPanel, BorderLayout.SOUTH);
    }

    private JButton menuBtn(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Georgia", Font.BOLD, 16));
        btn.setForeground(GOLD);
        btn.setBackground(PANEL_DARK);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GOLD, 1),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(new Color(40, 35, 20)); }
            public void mouseExited(java.awt.event.MouseEvent e)  { btn.setBackground(PANEL_DARK); }
        });
        return btn;
    }
}