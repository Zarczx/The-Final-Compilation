package GameGUI.ui;

import GameGUI.model.entity.Combatant;
import GameGUI.model.equipment.Weapon;
import GameGUI.model.equipment.Armor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class InventoryDialog extends JDialog {

    private final Combatant combatant;
    private final Runnable onUpdate;

    // --- Thematic Colors & Fonts ---
    private static final Color BG_DARK = new Color(15, 15, 15);
    private static final Color PANEL_DARK = new Color(25, 25, 25);
    private static final Color GOLD_MUTED = new Color(160, 140, 90);
    private static final Color TEXT_LIGHT = new Color(210, 210, 200);
    private static final Color BTN_HOVER = new Color(40, 40, 40);

    private static final Font FONT_REG = new Font("Georgia", Font.PLAIN, 14);
    private static final Font FONT_TITLE = new Font("Georgia", Font.BOLD, 16);
    private static final Font FONT_LARGE = new Font("Georgia", Font.BOLD, 22);

    // --- UI Components ---
    private JLabel hpLbl, energyLbl, weaponLbl, armorLbl;
    private JLabel normPotLbl, fullPotLbl, energyPotLbl, shardsLbl;
    private JLabel feedbackLbl; // <-- NEW: Label for in-dialog feedback

    public InventoryDialog(JFrame parent, Combatant combatant, Runnable onUpdate) {
        super(parent, "Status & Inventory", true);
        this.combatant = combatant;
        this.onUpdate = onUpdate;

        setSize(550, 450); // Slightly taller to fit the feedback text
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_DARK);

        UIManager.put("ToolTip.background", PANEL_DARK);
        UIManager.put("ToolTip.foreground", TEXT_LIGHT);
        UIManager.put("ToolTip.border", new LineBorder(GOLD_MUTED, 1));
        UIManager.put("ToolTip.font", FONT_REG);

        initComponents();
        updateUI();
    }

    private void initComponents() {
        JPanel mainGrid = new JPanel(new GridLayout(1, 2, 15, 0));
        mainGrid.setBackground(BG_DARK);
        mainGrid.setBorder(new EmptyBorder(15, 15, 15, 15));

        // ==========================================
        // LEFT PANEL: Stats & Equipment
        // ==========================================
        JPanel leftPanel = createThematicPanel("Status");
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

        JLabel nameLbl = new JLabel(combatant.emoji + " " + combatant.name);
        nameLbl.setFont(FONT_LARGE);
        nameLbl.setForeground(GOLD_MUTED);
        nameLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        hpLbl = createStyledLabel("❤ HP: ");
        energyLbl = createStyledLabel("⚡ Energy: ");

        leftPanel.add(nameLbl);
        leftPanel.add(Box.createVerticalStrut(20));
        leftPanel.add(hpLbl);
        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(energyLbl);
        leftPanel.add(Box.createVerticalStrut(30));

        JLabel equipTitle = new JLabel("Equipment (Hover for Stats)");
        equipTitle.setFont(new Font("Georgia", Font.ITALIC, 12));
        equipTitle.setForeground(Color.GRAY);
        equipTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        weaponLbl = createStyledLabel("⚔️ Weapon: ");
        armorLbl = createStyledLabel("🛡️ Armor: ");

        leftPanel.add(equipTitle);
        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(weaponLbl);
        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(armorLbl);

        // ==========================================
        // RIGHT PANEL: Items & Souls
        // ==========================================
        JPanel rightPanel = createThematicPanel("Inventory");
        rightPanel.setLayout(new GridLayout(4, 1, 0, 10));

        JPanel shardsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        shardsPanel.setOpaque(false);
        shardsLbl = new JLabel("💎 Soul Shards: ");
        shardsLbl.setFont(FONT_TITLE);
        shardsLbl.setForeground(new Color(150, 200, 255));
        shardsPanel.add(shardsLbl);

        normPotLbl = createStyledLabel("🧪 Normal Flask: 0");
        JButton useNormBtn = createThematicButton("Consume");
        useNormBtn.addActionListener(e -> usePotion("normal"));

        fullPotLbl = createStyledLabel("🩸 Crimson Flask: 0");
        JButton useFullBtn = createThematicButton("Consume");
        useFullBtn.addActionListener(e -> usePotion("full"));

        energyPotLbl = createStyledLabel("✨ Cerulean Flask: 0");
        JButton useEnergyBtn = createThematicButton("Consume");
        useEnergyBtn.addActionListener(e -> usePotion("energy"));

        rightPanel.add(shardsPanel);
        rightPanel.add(createItemRow(normPotLbl, useNormBtn));
        rightPanel.add(createItemRow(fullPotLbl, useFullBtn));
        rightPanel.add(createItemRow(energyPotLbl, useEnergyBtn));

        // ==========================================
        // BOTTOM: Feedback & Close Action
        // ==========================================
        JPanel bottomContainer = new JPanel(new BorderLayout());
        bottomContainer.setBackground(BG_DARK);

        // NEW: Feedback Label instead of JOptionPane
        feedbackLbl = new JLabel(" ");
        feedbackLbl.setForeground(GOLD_MUTED);
        feedbackLbl.setFont(new Font("Georgia", Font.ITALIC, 14));
        feedbackLbl.setHorizontalAlignment(SwingConstants.CENTER);
        feedbackLbl.setBorder(new EmptyBorder(10, 0, 10, 0));

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBackground(BG_DARK);
        JButton closeBtn = createThematicButton("Return");
        closeBtn.addActionListener(e -> dispose());
        bottomPanel.add(closeBtn);

        bottomContainer.add(feedbackLbl, BorderLayout.NORTH);
        bottomContainer.add(bottomPanel, BorderLayout.SOUTH);

        // Assemble
        mainGrid.add(leftPanel);
        mainGrid.add(rightPanel);
        add(mainGrid, BorderLayout.CENTER);
        add(bottomContainer, BorderLayout.SOUTH);
    }

    private JPanel createThematicPanel(String title) {
        JPanel panel = new JPanel();
        panel.setBackground(PANEL_DARK);
        TitledBorder border = BorderFactory.createTitledBorder(
                new LineBorder(GOLD_MUTED, 1), title,
                TitledBorder.CENTER, TitledBorder.TOP, FONT_TITLE, GOLD_MUTED);
        panel.setBorder(BorderFactory.createCompoundBorder(border, new EmptyBorder(10, 10, 10, 10)));
        return panel;
    }

    private JLabel createStyledLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(TEXT_LIGHT);
        lbl.setFont(FONT_REG);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        return lbl;
    }

    private JButton createThematicButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Georgia", Font.BOLD, 12));
        btn.setBackground(BG_DARK);
        btn.setForeground(GOLD_MUTED);
        btn.setFocusPainted(false);
        btn.setBorder(new LineBorder(GOLD_MUTED, 1));
        btn.setPreferredSize(new Dimension(80, 30));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { btn.setBackground(BTN_HOVER); }
            public void mouseExited(java.awt.event.MouseEvent evt) { btn.setBackground(BG_DARK); }
        });
        return btn;
    }

    private JPanel createItemRow(JLabel label, JButton button) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);
        row.add(label, BorderLayout.CENTER);
        row.add(button, BorderLayout.EAST);
        return row;
    }

    // --- Game Logic ---

    private void usePotion(String type) {
        // Delegate entirely to the Inventory manager (which passes it to Potions)
        String msg = switch (type) {
            case "normal" -> combatant.inventory.useNormalHealingPotion();
            case "full"   -> combatant.inventory.useFullHealingPotion();
            case "energy" -> combatant.inventory.useEnergyPotion();
            default       -> "Error";
        };

        // Display the text inside the dialog instead of a pop-up
        feedbackLbl.setText(msg);

        // Make the text red if it failed, or green if it succeeded
        if (msg.contains("❌")) {
            feedbackLbl.setForeground(new Color(200, 80, 80));
        } else {
            feedbackLbl.setForeground(new Color(120, 200, 120));
        }

        updateUI(); // (Or updateUI() depending on what you named it in your file)

        if (onUpdate != null) {
            onUpdate.run();
        }
    }

    private void updateUI() {
        hpLbl.setText(String.format("<html>❤ HP: <font color='#C85A5A'>%d / %d</font></html>",
                combatant.currentHp, combatant.maxHp));
        energyLbl.setText(String.format("<html>⚡ Energy: <font color='#5A96C8'>%d / %d</font></html>",
                combatant.energy, combatant.maxEnergy));

        Weapon w = combatant.inventory.getEquippedWeapon();
        if (w != null) {
            weaponLbl.setText("⚔️ " + w.name);
            weaponLbl.setToolTipText(generateWeaponTooltip(w));
        } else {
            weaponLbl.setText("⚔️ Bare Fists");
            weaponLbl.setToolTipText("No weapon equipped.");
        }

        Armor a = combatant.inventory.getEquippedArmor();
        if (a != null) {
            armorLbl.setText("🛡️ " + a.name);
            armorLbl.setToolTipText(generateArmorTooltip(a));
        } else {
            armorLbl.setText("🛡️ Unarmored");
            armorLbl.setToolTipText("No armor equipped.");
        }

        // Point these to combatant.potions instead of inventory!
        normPotLbl.setText("🧪 Normal Flasks: " + combatant.inventory.potions.getNormalHealingPotions());
        fullPotLbl.setText("🩸 Crimson Flasks: " + combatant.inventory.potions.getFullHealingPotions());
        energyPotLbl.setText("✨ Cerulean Flasks: " + combatant.inventory.potions.getEnergyPotions());
        shardsLbl.setText("💎 Soul Shards: " + combatant.soulShards);
    }

    // --- HTML Tooltip Generators ---

    // --- HTML Tooltip Generators ---

    private String generateWeaponTooltip(Weapon w) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><div style='padding:5px;'>");
        sb.append("<b style='color:#A08C5A;'>").append(w.name).append(" ").append(w.rarity).append("</b><br><br>");
        sb.append("Attack Power: <font color='#C85A5A'>+").append(w.getAtkBuff()).append("</font><br>");

        // Calculate combined totals (Base + Magic Shop upgrades)
        int totalLifesteal = w.lifestealPercent + w.addLifestealPercent;
        if (totalLifesteal > 0) sb.append("Lifesteal: ").append(totalLifesteal).append("%<br>");
        if (w.energyPerAttack > 0) sb.append("Energy Regained: ").append(w.energyPerAttack).append("<br>");
        if (w.poisonChance > 0) sb.append("Poison Build-up: ").append(w.poisonChance).append("%<br>");
        if (w.bleedChance > 0) sb.append("Bleed Chance: ").append(w.bleedChance).append("%<br>");
        if (w.stunChance > 0) sb.append("Stagger Chance: ").append(w.stunChance).append("%<br>");
        if (w.freezeChance > 0) sb.append("Freeze Chance: ").append(w.freezeChance).append("%<br>");
        if (w.extraHitChance > 0) sb.append("Twin-strike Chance: ").append(w.extraHitChance).append("%<br>");

        // Show specific enchantments if they exist
        if (!w.enchantments.isEmpty()) {
            sb.append("<br><b style='color:#D0A0FF;'>Active Enchantments:</b><br>");
            for (java.util.Map.Entry<String, String> entry : w.enchantments.entrySet()) {
                sb.append("&nbsp;&nbsp;").append(entry.getKey()).append(" <font color='gray'>").append(entry.getValue()).append("</font><br>");
            }
        }

        sb.append("</div></html>");
        return sb.toString();
    }

    private String generateArmorTooltip(Armor a) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><div style='padding:5px;'>");
        sb.append("<b style='color:#A08C5A;'>").append(a.name).append(" ").append(a.rarity).append("</b><br><br>");

        // Use getDefBuff() because in Armor.java it returns (defBuff + addDefBuff)
        sb.append("Physical Defense: <font color='#789678'>+").append(a.getDefBuff()).append("</font><br>");

        if (a.hpBuff > 0) sb.append("Vitality Boost: +").append(a.hpBuff).append("<br>");
        if (a.immuneDebuff) sb.append("Resists Debuffs<br>");
        if (a.immuneEffects) sb.append("Resists Status Ailments<br>");
        if (a.reflectChance > 0) sb.append("Thorns: ").append(a.reflectChance).append("% chance (").append(a.reflectPercent).append("% DMG)<br>");

        // Check if the Magic Shop fortified it
        if (a.hasEnchantment) {
            sb.append("<br><b style='color:#D0A0FF;'>Active Enchantments:</b><br>");
            sb.append("&nbsp;&nbsp;🛡️ Fortified Plating <font color='gray'>(+10 DEF)</font><br>");
        }

        sb.append("</div></html>");
        return sb.toString();
    }
}