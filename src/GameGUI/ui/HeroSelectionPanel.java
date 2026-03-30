package GameGUI.ui;

import GameGUI.model.HeroData;
import GameGUI.model.HeroData.HeroDefinition;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * HeroSelectionPanel — Purely a UI View.
 * Displays the heroes from the HeroData registry and fires a callback when one is chosen.
 */
public class HeroSelectionPanel extends JPanel {

    private Consumer<HeroDefinition> onHeroConfirmed;
    private HeroDefinition selectedHero = null;
    private final List<HeroCard> cards = new ArrayList<>();

    // Colors from your original design
    private static final Color BG_DARK     = new Color(28, 26, 46);
    private static final Color BG_CARD     = new Color(38, 36, 60);
    private static final Color BG_SELECTED = new Color(55, 50, 90);
    private static final Color BORDER_SEL  = new Color(201, 168, 76);
    private static final Color BORDER_NORM = new Color(70, 65, 100);
    private static final Color GOLD        = new Color(201, 168, 76);
    private static final Color TEXT_BRIGHT = new Color(240, 232, 208);
    private static final Color TEXT_DIM    = new Color(140, 125, 95);

    // Fonts
    private static final Font FONT_TITLE = new Font("Monospaced", Font.BOLD, 24);
    private static final Font FONT_STAT  = new Font("Monospaced", Font.PLAIN, 12);
    private static final Font FONT_DESC  = new Font("Monospaced", Font.ITALIC, 14);

    private JButton confirmBtn;
    private JTextArea backstoryArea;

    public HeroSelectionPanel() {
        setLayout(new BorderLayout());
        setBackground(BG_DARK);
        setBorder(new EmptyBorder(30, 50, 30, 50));

        buildUI();
    }

    public void setOnHeroConfirmed(Consumer<HeroDefinition> callback) {
        this.onHeroConfirmed = callback;
    }

    private void buildUI() {
        // --- Header ---
        JLabel titleLabel = new JLabel("SELECT YOUR CHAMPION", SwingConstants.CENTER);
        titleLabel.setFont(FONT_TITLE);
        titleLabel.setForeground(GOLD);
        titleLabel.setBorder(new EmptyBorder(0, 0, 30, 0));
        add(titleLabel, BorderLayout.NORTH);

        // --- Center: Hero Cards ---
        JPanel gridPanel = new JPanel(new GridLayout(1, HeroData.HEROES.size(), 20, 0));
        gridPanel.setBackground(BG_DARK);
        gridPanel.setOpaque(false);

        // Generate a card for every hero in the Registry
        for (HeroDefinition def : HeroData.HEROES) {
            HeroCard card = new HeroCard(def);
            cards.add(card);
            gridPanel.add(card);
        }
        add(gridPanel, BorderLayout.CENTER);

        // --- Bottom: Backstory & Confirm ---
        JPanel bottomPanel = new JPanel(new BorderLayout(20, 0));
        bottomPanel.setBackground(BG_DARK);
        bottomPanel.setBorder(new EmptyBorder(30, 0, 0, 0));

        backstoryArea = new JTextArea("Select a champion to view their origins...");
        backstoryArea.setFont(FONT_DESC);
        backstoryArea.setForeground(TEXT_DIM);
        backstoryArea.setBackground(BG_CARD);
        backstoryArea.setLineWrap(true);
        backstoryArea.setWrapStyleWord(true);
        backstoryArea.setEditable(false);
        backstoryArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_NORM, 1),
                new EmptyBorder(10, 15, 10, 15)
        ));

        confirmBtn = new JButton("ENTER THE ARENA");
        confirmBtn.setFont(new Font("Monospaced", Font.BOLD, 16));
        confirmBtn.setBackground(BG_CARD);
        confirmBtn.setForeground(TEXT_DIM);
        confirmBtn.setEnabled(false);
        confirmBtn.setFocusPainted(false);
        confirmBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        confirmBtn.setPreferredSize(new Dimension(250, 60));

        confirmBtn.addActionListener(e -> {
            if (selectedHero != null && onHeroConfirmed != null) {
                onHeroConfirmed.accept(selectedHero);
            }
        });

        bottomPanel.add(backstoryArea, BorderLayout.CENTER);
        bottomPanel.add(confirmBtn, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void selectHero(HeroCard card) {
        // Deselect all
        for (HeroCard c : cards) {
            c.setSelected(false);
        }

        // Select clicked
        card.setSelected(true);
        selectedHero = card.heroDef;

        // Update UI info
        backstoryArea.setText(selectedHero.name + " - " + selectedHero.role + "\n\n" + selectedHero.backstory);

        // Enable Confirm Button
        confirmBtn.setEnabled(true);
        confirmBtn.setBackground(new Color(60, 120, 60)); // Green for ready
        confirmBtn.setForeground(TEXT_BRIGHT);
        confirmBtn.setBorder(BorderFactory.createLineBorder(GOLD, 2));
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  Inner class: HeroCard
    // ═══════════════════════════════════════════════════════════════════════════
    private class HeroCard extends JPanel {
        public final HeroDefinition heroDef;
        private boolean isSelected = false;

        HeroCard(HeroDefinition def) {
            this.heroDef = def;
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBackground(BG_CARD);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_NORM, 2),
                    new EmptyBorder(15, 15, 15, 15)
            ));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            // Emoji Icon
            JLabel icon = new JLabel(def.emoji, SwingConstants.CENTER);
            icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 50));
            icon.setAlignmentX(CENTER_ALIGNMENT);

            // Name
            JLabel nameLbl = new JLabel(def.name, SwingConstants.CENTER);
            nameLbl.setFont(new Font("Monospaced", Font.BOLD, 14));
            nameLbl.setForeground(TEXT_BRIGHT);
            nameLbl.setAlignmentX(CENTER_ALIGNMENT);

            // Role
            JLabel roleLbl = new JLabel(def.role, SwingConstants.CENTER);
            roleLbl.setFont(new Font("Monospaced", Font.ITALIC, 12));
            roleLbl.setForeground(GOLD);
            roleLbl.setAlignmentX(CENTER_ALIGNMENT);
            roleLbl.setBorder(new EmptyBorder(0, 0, 15, 0));

            add(icon);
            add(Box.createVerticalStrut(10));
            add(nameLbl);
            add(roleLbl);

            // Stats using your StatBar
            add(new StatBar("HP", def.maxHp, 200, new Color(40, 160, 60)));
            add(Box.createVerticalStrut(8));
            add(new StatBar("ATK", def.attack, 30, new Color(200, 60, 40)));
            add(Box.createVerticalStrut(8));
            add(new StatBar("DEF", def.defense, 20, new Color(40, 100, 200)));
            add(Box.createVerticalStrut(8));
            add(new StatBar("ENG", def.maxEnergy, 150, new Color(200, 180, 40)));

            addMouseListener(new MouseAdapter() {
                @Override public void mousePressed(MouseEvent e) { selectHero(HeroCard.this); }
                @Override public void mouseEntered(MouseEvent e) {
                    if (!isSelected) setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(TEXT_DIM, 2), new EmptyBorder(15, 15, 15, 15)));
                }
                @Override public void mouseExited(MouseEvent e) {
                    if (!isSelected) setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER_NORM, 2), new EmptyBorder(15, 15, 15, 15)));
                }
            });
        }

        public void setSelected(boolean b) {
            isSelected = b;
            setBackground(b ? BG_SELECTED : BG_CARD);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(b ? BORDER_SEL : BORDER_NORM, 2),
                    new EmptyBorder(15, 15, 15, 15)
            ));
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  Inner class: StatBar
    // ═══════════════════════════════════════════════════════════════════════════
    private static class StatBar extends JPanel {
        StatBar(String label, int value, int maxValue, Color barColor) {
            setLayout(new BorderLayout(6, 0));
            setBackground(new Color(0,0,0,0)); // Transparent to inherit card bg
            setOpaque(false);
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
            bar.setBackground(new Color(20, 18, 30)); // Darker track
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