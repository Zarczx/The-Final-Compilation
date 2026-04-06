package GameGUI.ui;

import GameGUI.model.Combatant;
import GameGUI.model.HeroData.HeroDefinition;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class InventoryDialog extends JDialog {

    private final Combatant currentHero;
    private final HeroDefinition heroDef;

    // Darkest Dungeon Color Palette
    private static final Color BG_DARK = new Color(15, 15, 18);
    private static final Color BG_PANEL = new Color(24, 24, 28);
    private static final Color BORDER_BRONZE = new Color(100, 85, 60);
    private static final Color TEXT_PARCHMENT = new Color(210, 205, 185);
    private static final Color TEXT_MUTED = new Color(140, 135, 120);
    private static final Color TEXT_BLOOD = new Color(180, 40, 40);
    private static final Color TEXT_GOLD = new Color(220, 180, 60);
    private static final Color TEXT_BLUE = new Color(80, 140, 200);

    private static final Color GREEN = new Color(80, 160, 80);
    // Standardized Fonts
    private static final Font FONT_TITLE = new Font("Monospaced", Font.BOLD, 16);
    private static final Font FONT_TEXT = new Font("Monospaced", Font.BOLD, 14);
    private static final Font FONT_SMALL = new Font("Monospaced", Font.PLAIN, 12);

    // Dynamic UI Elements
    private JLabel hpLabel, energyLabel, atkLabel, defLabel;

    public InventoryDialog(Window owner, HeroDefinition heroDef, Combatant currentHero) {
        super(owner, "CHAR SHEET : " + heroDef.name.toUpperCase(), Dialog.ModalityType.APPLICATION_MODAL);
        this.heroDef = heroDef;
        this.currentHero = currentHero;

        setSize(650, 480);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());
        setResizable(false);
        getContentPane().setBackground(BG_DARK);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(BG_DARK);
        tabbedPane.setForeground(TEXT_PARCHMENT);
        tabbedPane.setFont(FONT_TITLE);
        tabbedPane.setFocusable(false);

        // Remove standard tab borders to make it look flatter
        tabbedPane.setBorder(new EmptyBorder(5, 5, 5, 5));

        // We combine Stats and Inventory into one dense "Roster" view
        tabbedPane.addTab(" ROSTER ", createCharacterSheetPanel());
        tabbedPane.addTab(" SKILLS ", createSkillsPanel());

        add(tabbedPane, BorderLayout.CENTER);

        // Brutalist Close Button
        JButton closeBtn = new JButton("RETURN TO BATTLE");
        closeBtn.setFont(FONT_TITLE);
        closeBtn.setBackground(new Color(40, 10, 10));
        closeBtn.setForeground(TEXT_PARCHMENT);
        closeBtn.setFocusPainted(false);
        closeBtn.setBorder(BorderFactory.createLineBorder(TEXT_BLOOD, 2));
        closeBtn.setPreferredSize(new Dimension(0, 40));
        closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeBtn.addActionListener(e -> dispose());

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(new EmptyBorder(5, 10, 10, 10));
        bottomPanel.setBackground(BG_DARK);
        bottomPanel.add(closeBtn, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    // ════════════════════════════════════════════════════════════════════════
    // ★ UNIFIED CHARACTER SHEET (Stats + Equipment + Consumables)
    // ════════════════════════════════════════════════════════════════════════
    private JPanel createCharacterSheetPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 0));
        panel.setBackground(BG_DARK);
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));

        // LEFT COLUMN: Vitals & Equipment
        JPanel leftCol = new JPanel();
        leftCol.setLayout(new BoxLayout(leftCol, BoxLayout.Y_AXIS));
        leftCol.setBackground(BG_DARK);

        leftCol.add(createBoxedPanel("VITALS & COMBAT", createStatsContent()));
        leftCol.add(Box.createRigidArea(new Dimension(0, 10)));
        leftCol.add(createBoxedPanel("EQUIPMENT", createEquipContent()));

        // RIGHT COLUMN: Provisions (Potions)
        JPanel rightCol = new JPanel();
        rightCol.setLayout(new BoxLayout(rightCol, BoxLayout.Y_AXIS));
        rightCol.setBackground(BG_DARK);

        rightCol.add(createBoxedPanel("PROVISIONS", createProvisionsContent()));

        panel.add(leftCol);
        panel.add(rightCol);

        return panel;
    }

    private JPanel createStatsContent() {
        JPanel p = new JPanel(new GridLayout(0, 1, 0, 5));
        p.setBackground(BG_PANEL);

        p.add(createStatRow("CLASS", heroDef.role, TEXT_PARCHMENT));
        p.add(createStatRow("LEVEL", String.valueOf(currentHero.level), TEXT_PARCHMENT));

        hpLabel = new JLabel(currentHero.currentHp + " / " + currentHero.maxHp);
        hpLabel.setForeground(TEXT_BLOOD);
        hpLabel.setFont(FONT_TEXT);
        p.add(createDynamicRow("HP", hpLabel));

        energyLabel = new JLabel(currentHero.energy + " / " + currentHero.maxEnergy);
        energyLabel.setForeground(TEXT_BLUE);
        energyLabel.setFont(FONT_TEXT);
        p.add(createDynamicRow("ENERGY", energyLabel));

        atkLabel = new JLabel(String.valueOf(currentHero.attack));
        atkLabel.setForeground(TEXT_GOLD);
        atkLabel.setFont(FONT_TEXT);
        p.add(createDynamicRow("DMG", atkLabel));

        defLabel = new JLabel(String.valueOf(currentHero.defense));
        defLabel.setForeground(TEXT_PARCHMENT);
        defLabel.setFont(FONT_TEXT);
        p.add(createDynamicRow("DEF", defLabel));

        p.add(createStatRow("SHARDS", String.valueOf(currentHero.soulShards), TEXT_BLUE));

        return p;
    }

    private JPanel createEquipContent() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(BG_PANEL);

        Combatant.Weapon wpn = currentHero.inventory.getEquippedWeapon();
        Combatant.Armor arm = currentHero.inventory.getEquippedArmor();

        p.add(createStatRow("WEAPON", wpn != null ? wpn.getName() : "Bare Hands", TEXT_GOLD));
        if (wpn != null) {
            if (wpn.lifestealPercent > 0) p.add(createStatRow(" ↳ Lifesteal", wpn.lifestealPercent + "%", TEXT_BLOOD));
            if (wpn.extraHitChance > 0) p.add(createStatRow(" ↳ Extra Hit", wpn.extraHitChance + "%", TEXT_GOLD));
            if (wpn.stunChance > 0) p.add(createStatRow(" ↳ Stun", wpn.stunChance + "%", TEXT_BLUE));
            if (wpn.bleedChance > 0) p.add(createStatRow(" ↳ Bleed", wpn.bleedChance + "%", TEXT_BLOOD));
            // Add enchantments loop if they exist
        }

        p.add(Box.createRigidArea(new Dimension(0, 10)));

        p.add(createStatRow("ARMOR", arm != null ? arm.getName() : "Rags", TEXT_GOLD));
        if (arm != null) {
            if (arm.immuneDebuff) p.add(createStatRow(" ↳ Immune", "Debuffs", GREEN));
            if (arm.reflectChance > 0) p.add(createStatRow(" ↳ Reflect", arm.reflectPercent + "% DMG", TEXT_GOLD));
        }

        return p;
    }

    private JPanel createProvisionsContent() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(BG_PANEL);

        int nPots = currentHero.inventory.getNormalHealingPotions();
        int fPots = currentHero.inventory.getFullHealingPotions();
        int ePots = currentHero.inventory.getEnergyPotions();

        p.add(createPotionRow("Minor Salve", nPots, 20, "normal"));
        p.add(Box.createRigidArea(new Dimension(0, 15)));
        p.add(createPotionRow("Major Flask", fPots, currentHero.maxHp, "full"));
        p.add(Box.createRigidArea(new Dimension(0, 15)));
        p.add(createPotionRow("Energy Tonic", ePots, 20, "energy"));

        return p;
    }

    // ════════════════════════════════════════════════════════════════════════
    // ★ SKILLS TAB (Dense text, rigid lines)
    // ════════════════════════════════════════════════════════════════════════
    private JPanel createSkillsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BG_DARK);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        if (heroDef.skills != null) {
            for (var skill : heroDef.skills) {
                JPanel skillBox = createBoxedPanel(skill.name.toUpperCase(), createSingleSkillContent(skill));
                panel.add(skillBox);
                panel.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        }

        JScrollPane scroll = new JScrollPane(panel);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.add(scroll, BorderLayout.CENTER);
        return wrap;
    }

    private JPanel createSingleSkillContent(GameGUI.model.HeroData.SkillDef skill) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(BG_PANEL);

        int minDmg = (int)(currentHero.attack * skill.minMultiplier);
        int maxDmg = (int)(currentHero.attack * skill.maxMultiplier);

        String pierce = skill.pierceArmor ? " [PIERCE]" : "";
        String dmgText = (minDmg == maxDmg) ? String.valueOf(minDmg) : (minDmg + "-" + maxDmg);

        JLabel dmgLabel = new JLabel("DMG: " + dmgText + pierce);
        dmgLabel.setFont(FONT_TITLE);
        dmgLabel.setForeground(TEXT_BLOOD);
        p.add(dmgLabel);
        p.add(Box.createRigidArea(new Dimension(0, 5)));

        JTextArea desc = new JTextArea(skill.description);
        desc.setLineWrap(true);
        desc.setWrapStyleWord(true);
        desc.setEditable(false);
        desc.setOpaque(false);
        desc.setForeground(TEXT_MUTED);
        desc.setFont(FONT_SMALL);
        p.add(desc);

        return p;
    }

    // ════════════════════════════════════════════════════════════════════════
    // ★ UI BUILDER HELPERS
    // ════════════════════════════════════════════════════════════════════════

    // Creates a rigid, bronze-bordered box with a title (Classic Darkest Dungeon style)
    private JPanel createBoxedPanel(String title, JPanel content) {
        JPanel box = new JPanel(new BorderLayout());
        box.setBackground(BG_PANEL);

        Border line = BorderFactory.createLineBorder(BORDER_BRONZE, 1);
        Border titled = BorderFactory.createTitledBorder(line, title, TitledBorder.LEFT, TitledBorder.TOP, FONT_TITLE, TEXT_GOLD);
        Border padded = BorderFactory.createCompoundBorder(titled, new EmptyBorder(10, 10, 10, 10));

        box.setBorder(padded);
        box.add(content, BorderLayout.CENTER);
        return box;
    }

    private JPanel createStatRow(String title, String value, Color valColor) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);

        JLabel t = new JLabel(title);
        t.setForeground(TEXT_MUTED);
        t.setFont(FONT_TEXT);

        JLabel v = new JLabel(value);
        v.setForeground(valColor);
        v.setFont(FONT_TEXT);
        v.setHorizontalAlignment(SwingConstants.RIGHT);

        row.add(t, BorderLayout.WEST);
        row.add(v, BorderLayout.EAST);
        return row;
    }

    private JPanel createDynamicRow(String title, JLabel valueLabel) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);

        JLabel t = new JLabel(title);
        t.setForeground(TEXT_MUTED);
        t.setFont(FONT_TEXT);

        valueLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        row.add(t, BorderLayout.WEST);
        row.add(valueLabel, BorderLayout.EAST);
        return row;
    }

    // A blocky, utilitarian potion row
    private JPanel createPotionRow(String name, int initialCount, int restoreAmount, String type) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(300, 35));

        JLabel nLbl = new JLabel(name + "  x");
        nLbl.setForeground(TEXT_PARCHMENT);
        nLbl.setFont(FONT_TEXT);

        JLabel countLbl = new JLabel(String.valueOf(initialCount));
        countLbl.setForeground(TEXT_GOLD);
        countLbl.setFont(FONT_TITLE);

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        left.setOpaque(false);
        left.add(nLbl);
        left.add(countLbl);

        JButton useBtn = new JButton("USE");
        useBtn.setFont(FONT_TITLE);
        useBtn.setBackground(new Color(20, 40, 20)); // Muted DD green
        useBtn.setForeground(TEXT_PARCHMENT);
        useBtn.setFocusPainted(false);
        useBtn.setBorder(BorderFactory.createLineBorder(new Color(40, 80, 40), 1));
        useBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        if (initialCount <= 0) useBtn.setEnabled(false);

        useBtn.addActionListener(e -> {
            boolean isEnergy = type.equals("energy");
            int curr = isEnergy ? currentHero.energy : currentHero.currentHp;
            int max = isEnergy ? currentHero.maxEnergy : currentHero.maxHp;

            if (curr >= max) {
                JOptionPane.showMessageDialog(this, (isEnergy?"Energy":"HP")+" Full.", "Notice", JOptionPane.PLAIN_MESSAGE);
                return;
            }

            switch(type) {
                case "normal": currentHero.inventory.useNormalHealingPotion(); break;
                case "full":   currentHero.inventory.useFullHealingPotion(); break;
                case "energy": currentHero.inventory.useEnergyPotion(); break;
            }

            if (isEnergy) currentHero.energy = Math.min(currentHero.maxEnergy, currentHero.energy + restoreAmount);
            else currentHero.currentHp = Math.min(currentHero.maxHp, currentHero.currentHp + restoreAmount);

            updateStatLabels();

            int newCount = Integer.parseInt(countLbl.getText()) - 1;
            countLbl.setText(String.valueOf(newCount));
            if (newCount <= 0) useBtn.setEnabled(false);
        });

        row.add(left, BorderLayout.WEST);
        row.add(useBtn, BorderLayout.EAST);
        return row;
    }

    private void updateStatLabels() {
        if (hpLabel != null) hpLabel.setText(currentHero.currentHp + " / " + currentHero.maxHp);
        if (energyLabel != null) energyLabel.setText(currentHero.energy + " / " + currentHero.maxEnergy);
    }
}