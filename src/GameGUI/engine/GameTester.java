package GameGUI.engine;

import GameGUI.model.entity.data.DataManager;
import GameGUI.model.entity.data.HeroData.ArmorDef;
import GameGUI.model.entity.data.HeroData.HeroDefinition;
import GameGUI.model.entity.data.HeroData.WeaponDef;

import GameGUI.model.equipment.Armor;
import GameGUI.model.equipment.Sword;
import GameGUI.model.equipment.Bow;
import GameGUI.model.equipment.Staff;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * GameTester — A standalone launcher for developers.
 * Includes a custom Hero Builder and Warp Menu.
 */
public class GameTester {

    public static void main(String[] args) {
        JDialog dialog = new JDialog((Frame) null, "Developer Test Menu", true);
        dialog.setLayout(new BorderLayout());
        dialog.setResizable(false);
        dialog.getContentPane().setBackground(new Color(20, 18, 36));

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Monospaced", Font.BOLD, 12));

        // ══════════════════════════════════════════════════════════════
        // ★ TAB 1: HERO BUILDER
        // ══════════════════════════════════════════════════════════════
        JPanel builderPanel = new JPanel(new GridLayout(7, 2, 10, 15));
        builderPanel.setBackground(new Color(20, 18, 36));
        builderPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Create UI Elements
        JComboBox<HeroDefinition> heroCombo = new JComboBox<>(DataManager.getData().getHeroes().toArray(new HeroDefinition[0]));
        heroCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof HeroDefinition h) setText(h.emoji + " " + h.name + " (" + h.role + ")");
                return this;
            }
        });

        WeaponDef[] swords = {
                Sword.OLD_BROADSWORD, Sword.IRON_SHORTSWORD, Sword.TWINSTRIKE_BLADE,
                Sword.LIFEBOND_BLADE, Sword.ECLIPSE_GREATSWORD, Sword.CELESTIAL_EDGE
        };
        WeaponDef[] bows = {
                Bow.WOODEN_BOW, Bow.OAK_LONGBOW, Bow.TWINSHOT_BOW,
                Bow.LIFEBLOOM_BOW, Bow.AETHERSTRIKE_BOW, Bow.GOLDEN_TALON
        };
        WeaponDef[] staffs = {
                Staff.WOODEN_STAFF, Staff.APPRENTICE_STAFF, Staff.MYSTIC_MIND_STAFF,
                Staff.FLAMEHEART_STAFF, Staff.AETHERIC_STAFF, Staff.CHRONOMANCER_STAFF
        };

        JComboBox<WeaponDef> weaponCombo = new JComboBox<>();
        weaponCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof WeaponDef w) setText(w.name + " (+" + w.atkBuff + " ATK)");
                return this;
            }
        });

        ArmorDef[] allArmors = {
                Armor.LEATHER_GUARD, Armor.IRON_VANGUARD, Armor.AEGIS_MAIL,
                Armor.VANGUARD_ROBE, Armor.SKYFORGE_PLATE, Armor.CELESTIAL_BATTLEGEAR
        };
        JComboBox<ArmorDef> armorCombo = new JComboBox<>(allArmors);
        armorCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof ArmorDef a) setText(a.name + " (+" + a.defBuff + " DEF)");
                return this;
            }
        });

        // Stat Spinners
        JSpinner hpSpin = new JSpinner(new SpinnerNumberModel(100, 1, 99999, 10));
        JSpinner enSpin = new JSpinner(new SpinnerNumberModel(50, 1, 9999, 5));
        JSpinner atkSpin = new JSpinner(new SpinnerNumberModel(10, 1, 9999, 2));
        JSpinner defSpin = new JSpinner(new SpinnerNumberModel(5, 0, 9999, 1));

        // Auto-update spinners and weapon choices when changing hero
        heroCombo.addActionListener(e -> {
            HeroDefinition h = (HeroDefinition) heroCombo.getSelectedItem();
            if (h != null) {
                hpSpin.setValue(h.maxHp);
                enSpin.setValue(h.maxEnergy);
                atkSpin.setValue(h.attack);
                defSpin.setValue(h.defense);

                // Filter weapons by class
                switch (h.role) {
                    case "Swordsman" -> weaponCombo.setModel(new DefaultComboBoxModel<>(swords));
                    case "Archer"    -> weaponCombo.setModel(new DefaultComboBoxModel<>(bows));
                    case "Mage"      -> weaponCombo.setModel(new DefaultComboBoxModel<>(staffs));
                    default          -> weaponCombo.setModel(new DefaultComboBoxModel<>(staffs)); // Fallback
                }
            }
        });
        heroCombo.setSelectedIndex(0); // Trigger the update immediately on load

        // Helper to format labels
        java.util.function.BiConsumer<String, JComponent> addRow = (label, comp) -> {
            JLabel l = new JLabel(label);
            l.setForeground(new Color(220, 200, 120));
            l.setFont(new Font("Monospaced", Font.BOLD, 14));
            builderPanel.add(l);
            builderPanel.add(comp);
        };

        addRow.accept("Select Hero:", heroCombo);
        addRow.accept("Select Weapon:", weaponCombo);
        addRow.accept("Select Armor:", armorCombo);
        addRow.accept("Base Max HP:", hpSpin);
        addRow.accept("Base Max Energy:", enSpin);
        addRow.accept("Base ATK:", atkSpin);
        addRow.accept("Base DEF:", defSpin);

        tabs.addTab("1. Build Hero", builderPanel);

        // ══════════════════════════════════════════════════════════════
        // ★ TAB 2: WARP MENU
        // ══════════════════════════════════════════════════════════════
        JPanel warpPanel = new JPanel();
        warpPanel.setLayout(new BoxLayout(warpPanel, BoxLayout.Y_AXIS));
        warpPanel.setBackground(new Color(20, 18, 36));
        warpPanel.setBorder(BorderFactory.createEmptyBorder(6, 16, 16, 16));

        int[] chosen = {-1};

        class Section {
            void add(JPanel parent, String sectionTitle, String[] btnLabels, int startIndex) {
                JLabel lbl = new JLabel("  " + sectionTitle);
                lbl.setFont(new Font("Monospaced", Font.BOLD, 11));
                lbl.setForeground(new Color(140, 125, 95));
                lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
                parent.add(lbl);

                JPanel row = null;
                for (int i = 0; i < btnLabels.length; i++) {
                    if (i % 5 == 0) {
                        row = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
                        row.setBackground(new Color(20, 18, 36));
                        row.setAlignmentX(Component.LEFT_ALIGNMENT);
                        parent.add(row);
                    }
                    final int idx = startIndex + i;
                    JButton btn = new JButton(btnLabels[i]);
                    btn.setFont(new Font("Monospaced", Font.BOLD, 11));
                    btn.setForeground(Color.WHITE);
                    btn.setBackground(new Color(38, 35, 60));
                    btn.setFocusPainted(false);
                    btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    btn.addActionListener(e -> {
                        chosen[0] = idx;
                        dialog.dispose();
                    });
                    row.add(btn);
                }
                parent.add(Box.createVerticalStrut(10));
            }
        }
        Section sec = new Section();

        sec.add(warpPanel, "GENERAL", new String[]{"Start Normally"}, 0);
        sec.add(warpPanel, "WORLD 1", new String[]{"Full W1 Run", "Rotfang Wolf", "Shade Sprite", "Dreadbark Treant", "Carrion Bat", "Hollow Stag (Boss)"}, 1);
        sec.add(warpPanel, "WORLD 2", new String[]{"Full W2 Run", "Plague Vermin", "Forsaken Cultist", "Blight Hound", "Ghoul Footman", "The Black Jailer", "Luther Von (Boss)"}, 7);
        sec.add(warpPanel, "WORLD 3", new String[]{"Full W3 Run", "Flame Revenant", "Bone Warlock", "Obsidian Crusher", "Soulflayer Gargoyle", "Zyrryl (Mini-Boss)"}, 14);
        sec.add(warpPanel, "MISC", new String[]{"Magic Shop", "Final Boss (Khai)", "Khai (Debug)"}, 20);

        tabs.addTab("2. Launch Game", warpPanel);
        dialog.add(tabs, BorderLayout.CENTER);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);

        if (chosen[0] == -1) System.exit(0);

        // ══════════════════════════════════════════════════════════════
        // ★ LAUNCH GAME WITH CUSTOM HERO
        // ══════════════════════════════════════════════════════════════
        JFrame window = new JFrame("The Final Compilation — TEST MODE");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.getContentPane().setPreferredSize(new Dimension(1280, 720));
        window.pack();
        window.setLocationRelativeTo(null);
        window.setResizable(false);
        window.setLayout(new BorderLayout());

        GameScreen mainScreen = new GameScreen();
        window.add(mainScreen, BorderLayout.CENTER);
        window.setVisible(true);

        // Create the custom hero from the builder tab choices!
        HeroDefinition base = (HeroDefinition) heroCombo.getSelectedItem();
        HeroDefinition customTestHero = new HeroDefinition(
                base.name, base.role, base.emoji, base.backstory, base.passive,
                (int) hpSpin.getValue(), (int) atkSpin.getValue(), (int) defSpin.getValue(), (int) enSpin.getValue(),
                base.skills,
                (WeaponDef) weaponCombo.getSelectedItem(),
                (ArmorDef) armorCombo.getSelectedItem()
        );

        final boolean[] battleStarted = {false};

        // ★ FIX: Shifted the switch statement to account for the new Prefi Encounter at index 21
        switch (chosen[0]) {
            case 0  -> System.out.println("Starting normally...");
            case 1  -> { mainScreen.debugSkipToWorld1Battle(customTestHero);                          battleStarted[0] = true; }
            case 2  -> { mainScreen.debugSkipToEnemy(customTestHero, "Rotfang Wolf",        1);       battleStarted[0] = true; }
            case 3  -> { mainScreen.debugSkipToEnemy(customTestHero, "Shade Sprite",        1);       battleStarted[0] = true; }
            case 4  -> { mainScreen.debugSkipToEnemy(customTestHero, "Dreadbark Treant",    1);       battleStarted[0] = true; }
            case 5  -> { mainScreen.debugSkipToEnemy(customTestHero, "Carrion Bat",         1);       battleStarted[0] = true; }
            case 6  -> { mainScreen.debugSkipToHollowStag(customTestHero);                            battleStarted[0] = true; }
            case 7  -> { mainScreen.debugSkipToWorld2(customTestHero);                                battleStarted[0] = true; }
            case 8  -> { mainScreen.debugSkipToEnemy(customTestHero, "Plague Vermin",       2);       battleStarted[0] = true; }
            case 9  -> { mainScreen.debugSkipToEnemy(customTestHero, "Forsaken Cultist",    2);       battleStarted[0] = true; }
            case 10 -> { mainScreen.debugSkipToEnemy(customTestHero, "Blight Hound",        2);       battleStarted[0] = true; }
            case 11 -> { mainScreen.debugSkipToEnemy(customTestHero, "Ghoul Footman",       2);       battleStarted[0] = true; }
            case 12 -> { mainScreen.debugSkipToEnemy(customTestHero, "The Black Jailer",        2);       battleStarted[0] = true; }
            case 13 -> { mainScreen.debugSkipToEnemy(customTestHero, "Luther Von", 2); battleStarted[0] = true; }
            case 14 -> { mainScreen.debugSkipToWorld3(customTestHero);                                battleStarted[0] = true; }
            case 15 -> { mainScreen.debugSkipToEnemy(customTestHero, "Flame Revenant",      3);       battleStarted[0] = true; }
            case 16 -> { mainScreen.debugSkipToEnemy(customTestHero, "Bone Warlock",        3);       battleStarted[0] = true; }
            case 17 -> { mainScreen.debugSkipToEnemy(customTestHero, "Obsidian Crusher",    3);       battleStarted[0] = true; }
            case 18 -> { mainScreen.debugSkipToEnemy(customTestHero, "Soulflayer Gargoyle", 3);       battleStarted[0] = true; }
            case 19 -> { mainScreen.debugSkipToEnemy(customTestHero, "Zyrryl",              3);       battleStarted[0] = true; }
            case 20 -> mainScreen.debugSkipToShop(customTestHero);
            case 21 -> { mainScreen.debugSkipToFinalBoss(customTestHero);                             battleStarted[0] = true; }
            case 22 -> { mainScreen.debugSkipToEnemy(customTestHero, "Khai the Gray", 3);                      battleStarted[0] = true; }

        }

        if (battleStarted[0]) {
            mainScreen.enableDevTools();
        }
    }
}