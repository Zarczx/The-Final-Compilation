package GameGUI.ui;

import GameGUI.model.entity.base.Combatant;

import javax.swing.*;
import java.awt.*;

public class InventoryDialog extends JDialog {

    private final Combatant combatant;
    private final Runnable onUpdate;

    private static final Color GOLD       = new Color(160, 140, 90);
    private static final Color TEXT_LIGHT = new Color(210, 210, 200);

    private JLabel normPotLbl, fullPotLbl, energyPotLbl, shardsLbl, feedbackLbl;

    public InventoryDialog(JFrame parent, Combatant combatant, Runnable onUpdate) {
        super(parent, "Inventory", true);
        this.combatant = combatant;
        this.onUpdate  = onUpdate;
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));
        setSize(460, 460);
        setLocationRelativeTo(parent);

        ImageIcon bgIcon  = loadIcon("assets/InventoryAssets/InventoryGUI.png");
        Image     bgImage = (bgIcon != null) ? bgIcon.getImage() : null;

        JPanel root = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (bgImage != null) {
                    g2.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
                } else {
                    g2.setColor(new Color(15, 15, 15));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                    g2.setColor(GOLD);
                    g2.setStroke(new BasicStroke(2));
                    g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 20, 20);
                }
            }
        };
        root.setOpaque(false);
        root.setBorder(BorderFactory.createEmptyBorder(70, 50, 40, 50));
        setContentPane(root);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        // Soul Shards row — number sits inline to the left of the right edge
        JPanel shardsRow = new JPanel(new BorderLayout());
        shardsRow.setOpaque(false);
        shardsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        shardsRow.setPreferredSize(new Dimension(0, 34));
        shardsLbl = new JLabel("0", SwingConstants.RIGHT);
        shardsLbl.setFont(new Font("Georgia", Font.BOLD, 18));
        shardsLbl.setForeground(new Color(160, 80, 255));
        // Right padding so it doesn't hug the very edge
        shardsLbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 18));
        shardsRow.add(shardsLbl, BorderLayout.CENTER);
        content.add(shardsRow);
        content.add(Box.createVerticalStrut(20));

        // Normal Healing Potion row
        normPotLbl = potionCountLabel("0");
        JButton useNormBtn = consumeBtn();
        useNormBtn.addActionListener(e -> usePotion("normal"));
        content.add(potionRow(normPotLbl, useNormBtn));
        content.add(Box.createVerticalStrut(10));

        // Full Healing Potion row
        fullPotLbl = potionCountLabel("0");
        JButton useFullBtn = consumeBtn();
        useFullBtn.addActionListener(e -> usePotion("full"));
        content.add(potionRow(fullPotLbl, useFullBtn));
        content.add(Box.createVerticalStrut(10));

        // Energy Potion row
        energyPotLbl = potionCountLabel("0");
        JButton useEnergyBtn = consumeBtn();
        useEnergyBtn.addActionListener(e -> usePotion("energy"));
        content.add(potionRow(energyPotLbl, useEnergyBtn));

        root.add(content, BorderLayout.CENTER);

        // Bottom
        feedbackLbl = new JLabel(" ", SwingConstants.CENTER);
        feedbackLbl.setFont(new Font("Georgia", Font.ITALIC, 13));
        feedbackLbl.setForeground(GOLD);

        ImageIcon exitNormal = loadIconFitWidth("assets/InventoryAssets/ExitInventory.png", 130);
        ImageIcon exitHover  = loadIconFitWidth("assets/InventoryAssets/ExitHoverInventory.png", 130);

        JButton closeBtn = new JButton(exitNormal);
        closeBtn.setRolloverIcon(exitHover);
        closeBtn.setRolloverEnabled(true);
        closeBtn.setContentAreaFilled(false);
        closeBtn.setBorderPainted(false);
        closeBtn.setFocusPainted(false);
        closeBtn.setOpaque(false);
        closeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        closeBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { utils.SoundUtil.play("HoverSound.wav"); }
        });
        closeBtn.addActionListener(e -> { utils.SoundUtil.play("SelectSound2.wav"); dispose(); });

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));
        bottom.add(feedbackLbl, BorderLayout.NORTH);
        JPanel closePnl = new JPanel(new FlowLayout(FlowLayout.CENTER));
        closePnl.setOpaque(false);
        closePnl.add(closeBtn);
        bottom.add(closePnl, BorderLayout.SOUTH);
        root.add(bottom, BorderLayout.SOUTH);

        refreshUI();
    }

    /**
     * Red bold count label — fixed width so it always sits
     * flush right before the Consume button.
     */
    private JLabel potionCountLabel(String text) {
        JLabel lbl = new JLabel(text, SwingConstants.RIGHT);
        lbl.setFont(new Font("Georgia", Font.BOLD, 16));
        lbl.setForeground(new Color(220, 60, 60)); // red
        lbl.setPreferredSize(new Dimension(38, 44));
        lbl.setMinimumSize(new Dimension(38, 44));
        lbl.setMaximumSize(new Dimension(38, 44));
        return lbl;
    }

    private JButton consumeBtn() {
        ImageIcon normalIcon = loadIconFitWidth("assets/InventoryAssets/Consume.png", 125);
        ImageIcon hoverIcon  = loadIconFitWidth("assets/InventoryAssets/ConsumeHover.png", 125);

        JButton btn = new JButton(normalIcon);
        btn.setRolloverIcon(hoverIcon);
        btn.setRolloverEnabled(true);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        if (normalIcon != null) {
            Dimension d = new Dimension(normalIcon.getIconWidth(), normalIcon.getIconHeight());
            btn.setPreferredSize(d);
            btn.setMaximumSize(d);
            btn.setMinimumSize(d);
        }

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { utils.SoundUtil.play("HoverSound.wav"); }
        });
        btn.addActionListener(e -> utils.SoundUtil.play("SelectSound2.wav"));
        return btn;
    }

    /**
     * Layout: [spacer fills left] [red count, right-aligned] [8px gap] [Consume btn]
     * Everything is vertically centred via GridBagLayout CENTER anchor.
     */
    private JPanel potionRow(JLabel countLabel, JButton button) {
        JPanel row = new JPanel(new GridBagLayout());
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        row.setPreferredSize(new Dimension(0, 44));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy  = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill   = GridBagConstraints.NONE;

        // Invisible spacer — pushes count + button to the right
        gbc.gridx   = 0;
        gbc.weightx = 1.0;
        gbc.fill    = GridBagConstraints.HORIZONTAL;
        gbc.insets  = new Insets(0, 0, 0, 0);
        row.add(Box.createHorizontalGlue(), gbc);

        // Red count — right-aligned, fixed width
        gbc.gridx   = 1;
        gbc.weightx = 0;
        gbc.fill    = GridBagConstraints.NONE;
        gbc.insets  = new Insets(0, 0, 0, 8); // 8px gap between count and button
        row.add(countLabel, gbc);

        // Consume button
        gbc.gridx   = 2;
        gbc.insets  = new Insets(0, 0, 0, 0);
        row.add(button, gbc);

        return row;
    }

    private void usePotion(String type) {
        String msg = switch (type) {
            case "normal" -> combatant.inventory.useNormalHealingPotion();
            case "full"   -> combatant.inventory.useFullHealingPotion();
            case "energy" -> combatant.inventory.useEnergyPotion();
            default       -> "Error";
        };
        feedbackLbl.setText(msg);
        feedbackLbl.setForeground(msg.contains("❌") ? new Color(200, 80, 80) : new Color(120, 200, 120));
        refreshUI();
        if (onUpdate != null) onUpdate.run();
    }

    private void refreshUI() {
        shardsLbl.setText(String.valueOf(combatant.getSoulShards()));
        normPotLbl.setText(String.valueOf(combatant.inventory.potions.getNormalHealingPotions()));
        fullPotLbl.setText(String.valueOf(combatant.inventory.potions.getFullHealingPotions()));
        energyPotLbl.setText(String.valueOf(combatant.inventory.potions.getEnergyPotions()));
    }

    private ImageIcon loadIconFitWidth(String path, int targetWidth) {
        ImageIcon raw = loadIcon(path);
        if (raw == null) return null;
        int w = raw.getIconWidth();
        int h = raw.getIconHeight();
        int targetHeight = (w == 0) ? 40 : (int)((double) h / w * targetWidth);
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
}
