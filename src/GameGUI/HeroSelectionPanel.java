package GameGUI;

import GameGUI.HeroData.HeroDefinition;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * HeroSelectionPanel.java
 *
 * Dedicated panel for hero selection.
 *
 * Extracted from GameScreen.java's inline characterPopup (which was a small
 * JPanel with two buttons). This is a full, standalone screen that:
 *
 *  - Displays each hero from HeroData.HEROES as a card with stats
 *  - Highlights the selected hero
 *  - Shows character story preview (mirrors kaelStory[0] reveal)
 *  - Locks the Confirm button until a hero is chosen
 *  - Fires onHeroConfirmed(HeroDefinition) callback → GameScreen transitions
 */
public class HeroSelectionPanel extends JPanel {

    // ─── Callbacks ────────────────────────────────────────────────────────────
    private Consumer<HeroDefinition> onHeroConfirmed;

    // ─── State ────────────────────────────────────────────────────────────────
    private HeroDefinition selectedHero = null;
    private final List<HeroCard> cards  = new ArrayList<>();

    // ─── Colors (theme matches GameScreen dark palette) ───────────────────────
    private static final Color BG_DARK     = new Color(28, 26, 46);
    private static final Color BG_CARD     = new Color(38, 36, 60);
    private static final Color BG_SELECTED = new Color(55, 50, 90);
    private static final Color BORDER_SEL  = new Color(201, 168, 76);
    private static final Color BORDER_NORM = new Color(70, 65, 100);
    private static final Color GOLD        = new Color(201, 168, 76);
    private static final Color GOLD_DIM    = new Color(120, 95, 45);
    private static final Color TEXT_BRIGHT = new Color(240, 232, 208);
    private static final Color TEXT_DIM    = new Color(150, 135, 105);
    private static final Color HP_GREEN    = new Color(39, 174, 96);
    private static final Color ATK_RED     = new Color(192, 57, 43);
    private static final Color DEF_BLUE    = new Color(52, 120, 192);

    // ─── Fonts ────────────────────────────────────────────────────────────────
    private static final Font FONT_TITLE  = new Font("Monospaced", Font.BOLD,  20);
    private static final Font FONT_EYEBROW= new Font("Monospaced", Font.PLAIN, 11);
    private static final Font FONT_CARD_NAME = new Font("Monospaced", Font.BOLD, 14);
    private static final Font FONT_CARD_ROLE = new Font("Monospaced", Font.ITALIC, 11);
    private static final Font FONT_STAT   = new Font("Monospaced", Font.PLAIN, 11);
    private static final Font FONT_BTN    = new Font("Monospaced", Font.BOLD,  13);
    private static final Font FONT_STORY  = new Font("Monospaced", Font.PLAIN, 12);

    // ─── Child widgets ────────────────────────────────────────────────────────
    private JButton confirmBtn;
    private JTextArea storyPreview;

    // ─── Constructor ──────────────────────────────────────────────────────────

    public HeroSelectionPanel() {
        setLayout(new BorderLayout(0, 0));
        setBackground(BG_DARK);
        setBorder(new EmptyBorder(30, 40, 30, 40));
        buildUI();
    }

    public void setOnHeroConfirmed(Consumer<HeroDefinition> cb) { this.onHeroConfirmed = cb; }

    // ─── UI Construction ──────────────────────────────────────────────────────

    private void buildUI() {
        // ── Title area ──
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(BG_DARK);
        titlePanel.setBorder(new EmptyBorder(0, 0, 24, 0));

        JLabel eyebrow = new JLabel("CHRONICLES OF BATTLE", SwingConstants.CENTER);
        eyebrow.setFont(FONT_EYEBROW);
        eyebrow.setForeground(GOLD_DIM);
        eyebrow.setAlignmentX(CENTER_ALIGNMENT);

        JLabel title = new JLabel("Choose Your Champion", SwingConstants.CENTER);
        title.setFont(FONT_TITLE);
        title.setForeground(GOLD);
        title.setAlignmentX(CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Your fate is written by the warrior you become", SwingConstants.CENTER);
        sub.setFont(FONT_CARD_ROLE);
        sub.setForeground(TEXT_DIM);
        sub.setAlignmentX(CENTER_ALIGNMENT);

        titlePanel.add(eyebrow);
        titlePanel.add(Box.createVerticalStrut(6));
        titlePanel.add(title);
        titlePanel.add(Box.createVerticalStrut(4));
        titlePanel.add(sub);

        // ── Hero card grid ──
        JPanel grid = new JPanel(new GridLayout(1, HeroData.HEROES.size(), 16, 0));
        grid.setBackground(BG_DARK);

        for (HeroDefinition def : HeroData.HEROES) {
            HeroCard card = new HeroCard(def);
            card.addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) { selectHero(def, card); }
                @Override public void mouseEntered(MouseEvent e) { if (def != selectedHero) card.setHover(true); }
                @Override public void mouseExited (MouseEvent e) { card.setHover(false); }
            });
            cards.add(card);
            grid.add(card);
        }

        // ── Story preview area ──
        storyPreview = new JTextArea("Select a hero to read their story...");
        storyPreview.setEditable(false);
        storyPreview.setLineWrap(true);
        storyPreview.setWrapStyleWord(true);
        storyPreview.setFont(FONT_STORY);
        storyPreview.setForeground(TEXT_DIM);
        storyPreview.setBackground(new Color(20, 18, 32));
        storyPreview.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_NORM, 1),
                new EmptyBorder(12, 14, 12, 14)
        ));
        storyPreview.setPreferredSize(new Dimension(0, 80));

        // ── Confirm button ──
        confirmBtn = new JButton("⚔  Enter the Arena");
        confirmBtn.setFont(FONT_BTN);
        confirmBtn.setEnabled(false);
        confirmBtn.setForeground(new Color(40, 30, 10));
        confirmBtn.setBackground(new Color(80, 60, 20));
        confirmBtn.setFocusPainted(false);
        confirmBtn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GOLD_DIM, 1),
                new EmptyBorder(12, 40, 12, 40)
        ));
        confirmBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        confirmBtn.addActionListener(e -> {
            if (selectedHero != null && onHeroConfirmed != null)
                onHeroConfirmed.accept(selectedHero);
        });

        // Hover effect on button
        confirmBtn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                if (confirmBtn.isEnabled()) {
                    confirmBtn.setBackground(new Color(130, 100, 30));
                    confirmBtn.setForeground(new Color(10, 8, 2));
                }
            }
            @Override public void mouseExited(MouseEvent e) {
                if (selectedHero != null) {
                    confirmBtn.setBackground(new Color(80, 60, 20));
                    confirmBtn.setForeground(new Color(40, 30, 10));
                }
            }
        });

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setBackground(BG_DARK);
        btnPanel.setBorder(new EmptyBorder(16, 0, 0, 0));
        btnPanel.add(confirmBtn);

        // ── Bottom area: story + button ──
        JPanel bottom = new JPanel(new BorderLayout(0, 0));
        bottom.setBackground(BG_DARK);
        bottom.setBorder(new EmptyBorder(20, 0, 0, 0));
        bottom.add(storyPreview, BorderLayout.CENTER);
        bottom.add(btnPanel,     BorderLayout.SOUTH);

        add(titlePanel, BorderLayout.NORTH);
        add(grid,        BorderLayout.CENTER);
        add(bottom,      BorderLayout.SOUTH);
    }

    // ─── Selection logic ──────────────────────────────────────────────────────
    private void selectHero(HeroDefinition def, HeroCard clickedCard) {
        selectedHero = def;

        // Update all card visual states
        for (HeroCard c : cards) {
            c.setSelected(c.getDefinition() == def);
            c.setHover(false);
        }

        // Show story preview (mirrors kaelStory[0])
        storyPreview.setForeground(TEXT_BRIGHT);
        storyPreview.setText(def.story.length > 0 ? def.story[0] : "");

        // Unlock confirm button
        confirmBtn.setEnabled(true);
        confirmBtn.setBackground(new Color(120, 92, 24));
        confirmBtn.setForeground(new Color(10, 8, 2));
        confirmBtn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GOLD, 1),
                new EmptyBorder(12, 40, 12, 40)
        ));
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  Inner class: HeroCard
    // ═══════════════════════════════════════════════════════════════════════════

    private static class HeroCard extends JPanel {
        private final HeroDefinition def;
        private boolean selected = false;
        private boolean hover    = false;

        private final JLabel emojiLabel;
        private final JLabel nameLabel;
        private final JLabel roleLabel;
        private final StatBar hpBar, atkBar, defBar;
        private final JLabel specialLabel;

        HeroCard(HeroDefinition def) {
            this.def = def;
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBackground(BG_CARD);
            setBorder(BorderFactory.createLineBorder(BORDER_NORM, 1));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            JPanel top = new JPanel();
            top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
            top.setBackground(new Color(30, 28, 50));
            top.setBorder(new EmptyBorder(20, 0, 12, 0));
            top.setAlignmentX(LEFT_ALIGNMENT);
            top.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

            emojiLabel = new JLabel(def.emoji, SwingConstants.CENTER);
            emojiLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
            emojiLabel.setAlignmentX(CENTER_ALIGNMENT);

            top.add(emojiLabel);

            JPanel body = new JPanel();
            body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
            body.setBackground(BG_CARD);
            body.setBorder(new EmptyBorder(12, 16, 16, 16));
            body.setAlignmentX(LEFT_ALIGNMENT);

            nameLabel = new JLabel(def.name);
            nameLabel.setFont(FONT_CARD_NAME);
            nameLabel.setForeground(GOLD);
            nameLabel.setAlignmentX(LEFT_ALIGNMENT);

            roleLabel = new JLabel(def.role);
            roleLabel.setFont(FONT_CARD_ROLE);
            roleLabel.setForeground(TEXT_DIM);
            roleLabel.setAlignmentX(LEFT_ALIGNMENT);
            roleLabel.setBorder(new EmptyBorder(0, 0, 10, 0));

            hpBar  = new StatBar("HP",  def.hp,      150, HP_GREEN);
            atkBar = new StatBar("ATK", def.attack,   40, ATK_RED);
            defBar = new StatBar("DEF", def.defense,  20, DEF_BLUE);

            JSeparator sep = new JSeparator();
            sep.setForeground(BORDER_NORM);
            sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

            specialLabel = new JLabel(def.skills[2].icon + " " + def.skills[2].name);
            specialLabel.setFont(new Font("Monospaced", Font.ITALIC, 11));
            specialLabel.setForeground(new Color(160, 120, 220));
            specialLabel.setAlignmentX(LEFT_ALIGNMENT);
            specialLabel.setBorder(new EmptyBorder(8, 0, 0, 0));

            body.add(nameLabel);
            body.add(roleLabel);
            body.add(hpBar);
            body.add(Box.createVerticalStrut(4));
            body.add(atkBar);
            body.add(Box.createVerticalStrut(4));
            body.add(defBar);
            body.add(Box.createVerticalStrut(8));
            body.add(sep);
            body.add(specialLabel);

            add(top);
            add(body);
        }

        HeroDefinition getDefinition() { return def; }

        void setSelected(boolean sel) {
            this.selected = sel;
            repaint();
            setBackground(sel ? BG_SELECTED : BG_CARD);
            setBorder(BorderFactory.createLineBorder(sel ? BORDER_SEL : BORDER_NORM, sel ? 2 : 1));
            nameLabel.setForeground(sel ? new Color(240, 210, 120) : GOLD);
        }

        void setHover(boolean h) {
            if (selected) return;
            this.hover = h;
            setBackground(h ? new Color(45, 43, 68) : BG_CARD);
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  Inner class: StatBar  (reusable stat row with mini progress bar)
    // ═══════════════════════════════════════════════════════════════════════════

    private static class StatBar extends JPanel {
        StatBar(String label, int value, int maxValue, Color barColor) {
            setLayout(new BorderLayout(6, 0));
            setBackground(BG_CARD);
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 18));
            setAlignmentX(LEFT_ALIGNMENT);

            JLabel lbl = new JLabel(label);
            lbl.setFont(FONT_STAT);
            lbl.setForeground(TEXT_DIM);
            lbl.setPreferredSize(new Dimension(28, 14));

            JProgressBar bar = new JProgressBar(0, maxValue);
            bar.setValue(value);
            bar.setStringPainted(false);
            bar.setForeground(barColor);
            bar.setBackground(new Color(40, 38, 60));
            bar.setBorder(BorderFactory.createEmptyBorder());
            bar.setPreferredSize(new Dimension(0, 6));

            JLabel val = new JLabel(String.valueOf(value));
            val.setFont(FONT_STAT);
            val.setForeground(TEXT_BRIGHT);
            val.setPreferredSize(new Dimension(28, 14));
            val.setHorizontalAlignment(SwingConstants.RIGHT);

            add(lbl, BorderLayout.WEST);
            add(bar, BorderLayout.CENTER);
            add(val, BorderLayout.EAST);
        }
    }
}