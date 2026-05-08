package GameGUI.ui;

import GameGUI.model.entity.Combatant;
import GameGUI.model.entity.HeroData;
import GameGUI.model.equipment.Armor;
import GameGUI.model.equipment.Weapon;

import javax.swing.*;
import java.awt.*;

public class PlayerStatsDialog extends JDialog {

    private static final Color BG_DARK    = new Color(15, 15, 15);
    private static final Color PANEL_DARK = new Color(25, 25, 25);
    private static final Color GOLD       = new Color(160, 140, 90);
    private static final Color TEXT_LIGHT = new Color(210, 210, 200);
    private static final Color TEXT_DIM   = new Color(130, 120, 100);

    public PlayerStatsDialog(Window parent, Combatant hero, HeroData.HeroDefinition heroDef) {
        super(parent, "Player Stats", ModalityType.APPLICATION_MODAL);
        setUndecorated(true);

        // ── Size: widen to 560, but clamp to parent bounds ────────────────
        int dialogW = 560;
        int dialogH = 820;

        if (parent != null) {
            Dimension parentSize = parent.getSize();
            dialogW = Math.min(dialogW, parentSize.width  - 40);
            dialogH = Math.min(dialogH, parentSize.height - 40);
        }
        setSize(dialogW, dialogH);
        setLocationRelativeTo(parent); // always centred inside parent

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
        root.setBorder(BorderFactory.createEmptyBorder(24, 32, 20, 32));
        setContentPane(root);

        // ── Title ─────────────────────────────────────────────────────────
        JLabel title = new JLabel(heroDef.emoji + "  " + heroDef.name, SwingConstants.CENTER);
        title.setFont(new Font("Georgia", Font.BOLD, 22));
        title.setForeground(GOLD);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
        root.add(title, BorderLayout.NORTH);

        // ── Content panel (no scroll) ─────────────────────────────────────
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        content.add(sectionLabel("Role: " + heroDef.role));
        content.add(Box.createVerticalStrut(4));
        content.add(dimLabel("Passive: " + heroDef.passive));
        content.add(Box.createVerticalStrut(14));
        content.add(divider());
        content.add(Box.createVerticalStrut(10));

        content.add(sectionHeader("Core Stats"));
        content.add(Box.createVerticalStrut(6));
        content.add(statLabel("❤  HP",      hero.currentHp + " / " + hero.maxHp,    new Color(200, 80,  80)));
        content.add(Box.createVerticalStrut(4));
        content.add(statLabel("⚡ Energy",  hero.energy    + " / " + hero.maxEnergy, new Color(90,  150, 220)));
        content.add(Box.createVerticalStrut(4));
        content.add(statLabel("⚔  Attack",  String.valueOf(hero.attack),              new Color(220, 160, 60)));
        content.add(Box.createVerticalStrut(4));
        content.add(statLabel("🛡  Defense", String.valueOf(hero.defense),             new Color(100, 180, 120)));
        content.add(Box.createVerticalStrut(4));
        content.add(statLabel("⭐ Level",   String.valueOf(hero.level),               GOLD));
        content.add(Box.createVerticalStrut(14));
        content.add(divider());
        content.add(Box.createVerticalStrut(10));

        content.add(sectionHeader("Equipment"));
        content.add(Box.createVerticalStrut(6));
        Weapon w = hero.inventory.getEquippedWeapon();
        Armor  a = hero.inventory.getEquippedArmor();
        content.add(statLabel("⚔  Weapon", w != null ? w.name : "None", TEXT_LIGHT));
        content.add(Box.createVerticalStrut(4));
        content.add(statLabel("🛡  Armor",  a != null ? a.name : "None", TEXT_LIGHT));
        content.add(Box.createVerticalStrut(14));
        content.add(divider());
        content.add(Box.createVerticalStrut(10));

        content.add(sectionHeader("Skills"));
        content.add(Box.createVerticalStrut(8));
        if (heroDef.skills != null) {
            for (HeroData.SkillDef skill : heroDef.skills) {
                content.add(skillCard(skill.icon, skill.name, skill.description));
                content.add(Box.createVerticalStrut(8));
            }
        }

        root.add(content, BorderLayout.CENTER);

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

    private JPanel skillCard(String icon, String name, String description) {
        JPanel card = new JPanel(new BorderLayout(12, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(30, 25, 15));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(new Color(80, 70, 40));
                g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        JPanel iconBox = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(40, 35, 20));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(new Color(100, 85, 45));
                g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth()  - fm.stringWidth(icon)) / 2;
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
        nameLbl.setForeground(GOLD);

        String shortDesc = description.split("\n")[0];
        JLabel descLbl = new JLabel("<html><body style='width:320px'>" + shortDesc + "</body></html>");
        descLbl.setFont(new Font("Georgia", Font.PLAIN, 11));
        descLbl.setForeground(TEXT_DIM);

        textPanel.add(nameLbl);
        textPanel.add(Box.createVerticalStrut(3));
        textPanel.add(descLbl);

        card.add(iconBox,   BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);
        return card;
    }

    private JLabel sectionHeader(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Georgia", Font.BOLD, 15));
        lbl.setForeground(GOLD);
        return lbl;
    }

    private JLabel sectionLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Georgia", Font.BOLD, 14));
        lbl.setForeground(TEXT_LIGHT);
        return lbl;
    }

    private JLabel dimLabel(String text) {
        JLabel lbl = new JLabel("<html><body style='width:440px'>" + text + "</body></html>");
        lbl.setFont(new Font("Georgia", Font.ITALIC, 12));
        lbl.setForeground(TEXT_DIM);
        return lbl;
    }

    private JPanel statLabel(String key, String value, Color valueColor) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));

        JLabel keyLbl = new JLabel(key);
        keyLbl.setFont(new Font("Georgia", Font.PLAIN, 14));
        keyLbl.setForeground(TEXT_DIM);

        JLabel valLbl = new JLabel(value);
        valLbl.setFont(new Font("Georgia", Font.BOLD, 14));
        valLbl.setForeground(valueColor);

        row.add(keyLbl, BorderLayout.WEST);
        row.add(valLbl, BorderLayout.EAST);
        return row;
    }

    private JSeparator divider() {
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(80, 70, 50));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return sep;
    }
}