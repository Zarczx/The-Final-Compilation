package GameGUI.ui;

import GameGUI.model.entity.Combatant;
import GameGUI.model.entity.HeroData;

import javax.swing.*;
import java.awt.*;

public class EnemyStatsDialog extends JDialog {

    private static final Color BG_DARK    = new Color(15, 15, 15);
    private static final Color PANEL_DARK = new Color(25, 25, 25);
    private static final Color GOLD       = new Color(160, 140, 90);
    private static final Color TEXT_LIGHT = new Color(210, 210, 200);
    private static final Color TEXT_DIM   = new Color(130, 120, 100);
    private static final Color RED        = new Color(200, 80, 80);

    public EnemyStatsDialog(Window parent, Combatant enemy, HeroData.EnemyDefinition enemyDef) {
        super(parent, "Enemy Stats", ModalityType.APPLICATION_MODAL);
        setUndecorated(true);
        setSize(480, 520);
        setLocationRelativeTo(parent);

        JPanel root = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_DARK);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(new Color(160, 50, 50));
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 20, 20);
            }
        };
        root.setOpaque(false);
        root.setBorder(BorderFactory.createEmptyBorder(24, 28, 20, 28));
        setContentPane(root);

        // ── Title ─────────────────────────────────────────────────────────
        JLabel title = new JLabel(enemyDef.emoji + "  " + enemyDef.name, SwingConstants.CENTER);
        title.setFont(new Font("Georgia", Font.BOLD, 20));
        title.setForeground(RED);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
        root.add(title, BorderLayout.NORTH);

        // ── Content ───────────────────────────────────────────────────────
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        content.add(dimLabel("Role: " + enemyDef.role));
        content.add(Box.createVerticalStrut(14));
        content.add(divider());
        content.add(Box.createVerticalStrut(10));

        content.add(sectionHeader("Stats"));
        content.add(Box.createVerticalStrut(6));
        content.add(statRow("❤  HP",      enemy.currentHp + " / " + enemy.maxHp, new Color(200, 80, 80)));
        content.add(Box.createVerticalStrut(4));
        content.add(statRow("⚔  Attack",  String.valueOf(enemy.attack),  new Color(220, 160, 60)));
        content.add(Box.createVerticalStrut(4));
        content.add(statRow("🛡  Defense", String.valueOf(enemy.defense), new Color(100, 180, 120)));
        content.add(Box.createVerticalStrut(14));
        content.add(divider());
        content.add(Box.createVerticalStrut(10));

        // Skills placeholder section
        content.add(sectionHeader("Skills"));
        content.add(Box.createVerticalStrut(8));

        // Enemy skills are not in EnemyDefinition yet — show placeholders
        String[][] enemySkills = getEnemySkills(enemyDef.name);
        for (String[] skill : enemySkills) {
            content.add(skillCard(skill[0], skill[1], skill[2]));
            content.add(Box.createVerticalStrut(8));
        }

        JScrollPane scroll = new JScrollPane(content);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(12);
        root.add(scroll, BorderLayout.CENTER);

        // ── Close ─────────────────────────────────────────────────────────
        JButton closeBtn = new JButton("✕  Back");
        closeBtn.setFont(new Font("Georgia", Font.BOLD, 13));
        closeBtn.setForeground(TEXT_DIM);
        closeBtn.setBackground(BG_DARK);
        closeBtn.setFocusPainted(false);
        closeBtn.setBorderPainted(false);
        closeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        closeBtn.addActionListener(e -> dispose());
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottom.setOpaque(false);
        bottom.add(closeBtn);
        root.add(bottom, BorderLayout.SOUTH);
    }

    // ── Enemy skill lookup ────────────────────────────────────────────────
    private String[][] getEnemySkills(String name) {
        return switch (name) {
            case "Rotfang Wolf"     -> new String[][]{
                    {"🐾", "Savage Bite",   "A ferocious bite that tears through flesh."},
                    {"🌑", "Savage Howl",   "Lets out a terrifying howl, boosting its power."}};
            case "Shade Sprite"     -> new String[][]{
                    {"👻", "Trickster Strike", "A deceptive hit from the shadows."}};
            case "Dreadbark Treant" -> new String[][]{
                    {"🌿", "Root Snare",    "Entangles the enemy in magical roots."}};
            case "Carrion Bat"      -> new String[][]{
                    {"🦇", "Sonic Screech", "A piercing screech that disorients the target."}};
            case "The Hollow Stag"  -> new String[][]{
                    {"⚡", "Deathly Charge",  "Charges with tremendous force."},
                    {"🌑", "Blackened Howl",  "Unleashes a dark howl that weakens the target."}};
            case "Plague Vermin"    -> new String[][]{
                    {"☠️", "Plague Bite",    "Infects with a deadly disease."}};
            case "Forsaken Cultist" -> new String[][]{
                    {"🔮", "Shadow Bolt",    "Hurls a bolt of dark energy."}};
            case "Blight Hound"     -> new String[][]{
                    {"💀", "Corpse Explosion", "Detonates a rotten corpse for area damage."}};
            case "Ghoul Footman"    -> new String[][]{
                    {"🧟", "Rotten Cleave",   "A wide slash with a decaying blade."}};
            case "The Black Jailer" -> new String[][]{
                    {"⛓️", "Shackling Chains", "Binds the target, preventing movement."},
                    {"🔗", "Tormenting Lash",  "Cracks a whip, inflicting massive pain."}};
            case "Luther Von"       -> new String[][]{
                    {"👑", "Crown of Despair", "Channels dark royal energy to weaken enemies."},
                    {"🌑", "Dark Ascension",   "Ascends to a more powerful dark form."},
                    {"⚔️", "King's Wrath",     "Unleashes the full fury of a corrupted king."}};
            case "Flame Revenant"   -> new String[][]{
                    {"🔥", "Ember Burst",      "Erupts in a burst of scorching flames."}};
            case "Bone Warlock"     -> new String[][]{
                    {"💀", "Marrow Bolt",      "Fires a bolt forged from shattered bone."}};
            case "Obsidian Crusher" -> new String[][]{
                    {"🗿", "Magma Slam",       "Slams the ground, sending magma flying."}};
            case "Soulflayer Gargoyle" -> new String[][]{
                    {"🦇", "Soul Scream",      "Lets out a scream that tears at the soul."}};
            case "Zyrryl"           -> new String[][]{
                    {"🛡️", "Bone Shield",     "Raises an impenetrable shield of bone."},
                    {"⚔️", "Great Cleaver",   "Delivers a devastating overhead cleave."}};
            case "Khai the Necromancer" -> new String[][]{
                    {"💜", "Soul Drain",       "Drains the life force of the target."},
                    {"🌀", "Encapsulation",    "Traps the target in a dark orb."},
                    {"🌑", "Dark Ascension",   "Rises to an unstoppable dark form."}};
            default -> new String[][]{{"❓", "Unknown Skill", "This enemy's skills are unknown."}};
        };
    }

    private JPanel skillCard(String icon, String name, String desc) {
        JPanel card = new JPanel(new BorderLayout(10, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(35, 15, 15));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(new Color(100, 40, 40));
                g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        // Icon box
        JPanel iconBox = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(50, 20, 20));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(new Color(120, 50, 50));
                g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(icon)) / 2;
                int y = (getHeight() + fm.getAscent()) / 2 - 4;
                g2.drawString(icon, x, y);
            }
        };
        iconBox.setPreferredSize(new Dimension(48, 48));
        iconBox.setOpaque(false);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel nameLbl = new JLabel(name);
        nameLbl.setFont(new Font("Georgia", Font.BOLD, 14));
        nameLbl.setForeground(RED);

        JLabel descLbl = new JLabel("<html><body style='width:260px'>" + desc + "</body></html>");
        descLbl.setFont(new Font("Georgia", Font.PLAIN, 11));
        descLbl.setForeground(TEXT_DIM);

        textPanel.add(nameLbl);
        textPanel.add(Box.createVerticalStrut(3));
        textPanel.add(descLbl);

        card.add(iconBox, BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);
        return card;
    }

    private JLabel sectionHeader(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Georgia", Font.BOLD, 15));
        lbl.setForeground(GOLD);
        return lbl;
    }

    private JLabel dimLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Georgia", Font.ITALIC, 13));
        lbl.setForeground(TEXT_DIM);
        return lbl;
    }

    private JPanel statRow(String key, String value, Color valueColor) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));
        JLabel k = new JLabel(key);
        k.setFont(new Font("Georgia", Font.PLAIN, 14));
        k.setForeground(TEXT_DIM);
        JLabel v = new JLabel(value);
        v.setFont(new Font("Georgia", Font.BOLD, 14));
        v.setForeground(valueColor);
        row.add(k, BorderLayout.WEST);
        row.add(v, BorderLayout.EAST);
        return row;
    }

    private JSeparator divider() {
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(80, 70, 50));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return sep;
    }
}