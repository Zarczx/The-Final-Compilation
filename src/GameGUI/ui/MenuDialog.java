package GameGUI.ui;

import GameGUI.model.entity.base.Combatant;
import GameGUI.model.entity.data.EnemyData;
import GameGUI.model.entity.data.HeroData;

import javax.swing.*;
import java.awt.*;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public class MenuDialog extends JDialog {

    private static final Color BG_DARK    = new Color(15, 15, 15);
    private static final Color GOLD       = new Color(160, 140, 90);
    private static final Color TEXT_DIM   = new Color(130, 120, 100);

    public MenuDialog(Window parent, Combatant hero, HeroData.HeroDefinition heroDef,
                      Combatant enemy, EnemyData enemyDef,
                      Runnable onUpdate, Supplier<String> bossTauntSupplier,
                      BooleanSupplier isBossCheck, Runnable onFullyClose) {
        super(parent, "Menu", ModalityType.APPLICATION_MODAL);
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));

        boolean isBoss = isBossCheck != null && isBossCheck.getAsBoolean();
        setSize(400, isBoss ? 580 : 500);
        setLocationRelativeTo(parent);

        // Load card background
        ImageIcon menuBg = loadIcon("assets/MenuAssets/Menu.png");
        Image menuBgImage = (menuBg != null) ? menuBg.getImage() : null;

        JPanel root = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (menuBgImage != null) {
                    g2.drawImage(menuBgImage, 0, 0, getWidth(), getHeight(), this);
                } else {
                    // Fallback dark background
                    g2.setColor(BG_DARK);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                    g2.setColor(GOLD);
                    g2.setStroke(new BasicStroke(2));
                    g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 20, 20);
                }
            }
        };
        root.setOpaque(false);
        root.setBorder(BorderFactory.createEmptyBorder(50, 30, 30, 30));
        setContentPane(root);

        // Center panel
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);

        // Boss taunt banner
        if (isBoss && bossTauntSupplier != null) {
            String taunt = bossTauntSupplier.get();
            JLabel tauntLabel = new JLabel(
                    "<html><div style='text-align:center; width:300px;'>" + taunt + "</div></html>",
                    SwingConstants.CENTER);
            tauntLabel.setFont(new Font("Georgia", Font.ITALIC, 13));
            tauntLabel.setForeground(new Color(200, 80, 80));
            tauntLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            tauntLabel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(140, 40, 40), 1),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)));
            centerPanel.add(tauntLabel);
            centerPanel.add(Box.createVerticalStrut(12));
        }

        JButton inventoryBtn   = menuBtn("Inventory",   "Inventory",   210);
        JButton playerStatsBtn = menuBtn("Player Stats", "PlayerStats", 210);
        JButton enemyStatsBtn  = menuBtn("Enemy Stats",  "EnemyStats",  210);

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

        centerPanel.add(Box.createVerticalStrut(40)); // gap between title and first button
        centerPanel.add(inventoryBtn);
        centerPanel.add(Box.createVerticalStrut(12));
        centerPanel.add(playerStatsBtn);
        centerPanel.add(Box.createVerticalStrut(12));
        centerPanel.add(enemyStatsBtn);

        root.add(centerPanel, BorderLayout.CENTER);

        // Back to Battle button
        ImageIcon backNormal = loadIconFitWidth("assets/MenuAssets/BackToBattleButton.png", 150);
        ImageIcon backHover  = loadIconFitWidth("assets/MenuAssets/BackToBattleButtonHover.png", 150);

        JButton closeBtn = new JButton(backNormal);
        closeBtn.setRolloverIcon(backHover);
        closeBtn.setRolloverEnabled(true);
        closeBtn.setContentAreaFilled(false);
        closeBtn.setBorderPainted(false);
        closeBtn.setFocusPainted(false);
        closeBtn.setOpaque(false);
        closeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        closeBtn.addActionListener(e -> {
            dispose();
            if (onFullyClose != null) onFullyClose.run();
        });
        closeBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                utils.SoundUtil.play("HoverSound.wav");
            }
        });
        closeBtn.addActionListener(e -> utils.SoundUtil.play("SelectSound2.wav"));

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setOpaque(false);
        bottomPanel.add(closeBtn);
        root.add(bottomPanel, BorderLayout.SOUTH);
    }

    private ImageIcon loadIconFitWidth(String path, int targetWidth) {
        ImageIcon raw = loadIcon(path);
        if (raw == null) return null;
        int w = raw.getIconWidth();
        int h = raw.getIconHeight();
        int targetHeight = (w == 0) ? 50 : (int)((double) h / w * targetWidth);
        Image scaled = raw.getImage().getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    private ImageIcon loadIcon(String path) {
        java.net.URL url = getClass().getClassLoader().getResource(path);
        if (url != null) return new ImageIcon(url);
        java.io.File f = new java.io.File(path);
        if (f.exists()) return new ImageIcon(f.getAbsolutePath());
        System.err.println("Asset not found: " + path);
        return null;
    }

    private JButton menuBtn(String text, String assetName, int width) {
        ImageIcon normalIcon = loadIconFitWidth("assets/MenuAssets/" + assetName + ".png", width);
        ImageIcon hoverIcon  = loadIconFitWidth("assets/MenuAssets/" + assetName + "Hover.png", width);

        JButton btn = new JButton(normalIcon);
        btn.setRolloverIcon(hoverIcon);
        btn.setRolloverEnabled(true);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);

        if (normalIcon != null) {
            Dimension d = new Dimension(normalIcon.getIconWidth(), normalIcon.getIconHeight());
            btn.setPreferredSize(d);
            btn.setMaximumSize(d);
        }

        // ✅ Hover sound
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                utils.SoundUtil.play("HoverSound.wav");
            }
        });

        // ✅ Click sound
        btn.addActionListener(e -> utils.SoundUtil.play("SelectSound.wav"));

        return btn;
    }
}