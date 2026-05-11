package GameGUI.ui;

import GameGUI.model.entity.base.Combatant;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class InventoryDialog extends JDialog {

    private final Combatant combatant;
    private final Runnable onUpdate;

    private static final Color BG_DARK    = new Color(15, 15, 15);
    private static final Color PANEL_DARK = new Color(25, 25, 25);
    private static final Color GOLD       = new Color(160, 140, 90);
    private static final Color TEXT_LIGHT = new Color(210, 210, 200);
    private static final Color TEXT_DIM   = new Color(130, 120, 100);

    private JLabel normPotLbl, fullPotLbl, energyPotLbl, shardsLbl, feedbackLbl;

    public InventoryDialog(JFrame parent, Combatant combatant, Runnable onUpdate) {
        super(parent, "Inventory", true);
        this.combatant = combatant;
        this.onUpdate  = onUpdate;
        setUndecorated(true);
        setSize(400, 380);
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
        root.setBorder(BorderFactory.createEmptyBorder(24, 28, 16, 28));
        setContentPane(root);

        // ── Title ─────────────────────────────────────────────────────────
        JLabel title = new JLabel("Inventory", SwingConstants.CENTER);
        title.setFont(new Font("Georgia", Font.BOLD, 22));
        title.setForeground(GOLD);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));
        root.add(title, BorderLayout.NORTH);

        // ── Content ───────────────────────────────────────────────────────
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        // Soul Shards
        shardsLbl = new JLabel("", SwingConstants.CENTER);
        shardsLbl.setFont(new Font("Georgia", Font.BOLD, 18));
        shardsLbl.setForeground(new Color(150, 200, 255));
        shardsLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(shardsLbl);
        content.add(Box.createVerticalStrut(20));

        // Divider
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(80, 70, 50));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        content.add(sep);
        content.add(Box.createVerticalStrut(16));

        // Flask label
        JLabel flaskTitle = new JLabel("Potions", SwingConstants.CENTER);
        flaskTitle.setFont(new Font("Georgia", Font.ITALIC, 13));
        flaskTitle.setForeground(TEXT_DIM);
        flaskTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(flaskTitle);
        content.add(Box.createVerticalStrut(12));

        // Normal Flask row
        normPotLbl = rowLabel("🧪 Normal Healing Potion: 0");
        JButton useNormBtn = flaskBtn("Consume");
        useNormBtn.addActionListener(e -> usePotion("normal"));
        content.add(flaskRow(normPotLbl, useNormBtn));
        content.add(Box.createVerticalStrut(10));

        // Crimson Flask row
        fullPotLbl = rowLabel("🩸 Full Healing Potion: 0");
        JButton useFullBtn = flaskBtn("Consume");
        useFullBtn.addActionListener(e -> usePotion("full"));
        content.add(flaskRow(fullPotLbl, useFullBtn));
        content.add(Box.createVerticalStrut(10));

        // Cerulean Flask row
        energyPotLbl = rowLabel("✨ Energy Potion: 0");
        JButton useEnergyBtn = flaskBtn("Consume");
        useEnergyBtn.addActionListener(e -> usePotion("energy"));
        content.add(flaskRow(energyPotLbl, useEnergyBtn));

        root.add(content, BorderLayout.CENTER);

        // ── Bottom ────────────────────────────────────────────────────────
        feedbackLbl = new JLabel(" ", SwingConstants.CENTER);
        feedbackLbl.setFont(new Font("Georgia", Font.ITALIC, 13));
        feedbackLbl.setForeground(GOLD);

        JButton closeBtn = new JButton("✕  Close");
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
        closeBtn.addActionListener(e -> dispose());

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

    private JLabel rowLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Georgia", Font.PLAIN, 15));
        lbl.setForeground(TEXT_LIGHT);
        return lbl;
    }

    private JButton flaskBtn(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Georgia", Font.BOLD, 12));
        btn.setForeground(GOLD);
        btn.setBackground(PANEL_DARK);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(GOLD, 1),
                BorderFactory.createEmptyBorder(4, 12, 4, 12)));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(new Color(50, 44, 22)); }
            public void mouseExited (java.awt.event.MouseEvent e) { btn.setBackground(PANEL_DARK); }
        });
        return btn;
    }

    private JPanel flaskRow(JLabel label, JButton button) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        row.add(label, BorderLayout.CENTER);
        row.add(button, BorderLayout.EAST);
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
        shardsLbl.setText("Soul Shards: " + combatant.getSoulShards());
        normPotLbl.setText("🧪 Normal Healing Potion: "   + combatant.inventory.potions.getNormalHealingPotions());
        fullPotLbl.setText("🩸 Full Healing Potion: "  + combatant.inventory.potions.getFullHealingPotions());
        energyPotLbl.setText("✨ Energy Potion: " + combatant.inventory.potions.getEnergyPotions());
    }
}