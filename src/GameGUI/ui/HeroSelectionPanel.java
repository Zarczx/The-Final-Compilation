package GameGUI.ui;

import GameGUI.model.entity.DataManager;
import GameGUI.model.entity.HeroData;
import GameGUI.model.entity.HeroData.HeroDefinition;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.function.Consumer;

public class HeroSelectionPanel extends JPanel {

    private Consumer<HeroDefinition> onHeroConfirmed;
    private HeroDefinition selectedHero = null;
    private final List<HeroCard> cards = new ArrayList<>();

    // ── Brown / Gold palette ──────────────────────────────────────────────────
    private static final Color BG_DARK = new Color(0, 0, 0);
    private static final Color BG_CARD      = new Color(18, 15, 10);
    private static final Color BG_SELECTED  = new Color(40, 28, 8);
    private static final Color BORDER_SEL   = new Color(200, 160, 40);
    private static final Color BORDER_NORM  = new Color(90, 60, 10);
    private static final Color GOLD         = new Color(200, 160, 40);
    private static final Color GOLD_DIM     = new Color(120, 80, 10);
    private static final Color GOLD_BRIGHT  = new Color(240, 200, 80);
    private static final Color TEXT_BRIGHT  = new Color(210, 200, 180);
    private static final Color TEXT_DIM     = new Color(140, 115, 75);
    private static final Color HP_GREEN     = new Color(39, 174, 96);
    private static final Color ATK_RED      = new Color(192, 57, 43);
    private static final Color DEF_BLUE     = new Color(52, 120, 192);
    private static final Color SKILL_AMBER  = new Color(210, 150, 60);
    private static final Color PASSIVE_GOLD = new Color(220, 185, 80);
    private static final Color ULT_ORANGE   = new Color(220, 130, 40);

    // ── Fonts ─────────────────────────────────────────────────────────────────
    private static Font FONT_PIXEL_LG;
    private static Font FONT_PIXEL_MD;
    private static Font FONT_PIXEL_SM;
    private static Font FONT_PIXEL_XS;

    static {
        try {
            java.io.InputStream fs = HeroSelectionPanel.class
                    .getResourceAsStream("/assets/AssetFont/Pixelari.ttf");
            if (fs != null) {
                Font base = Font.createFont(Font.TRUETYPE_FONT, fs);
                FONT_PIXEL_LG = base.deriveFont(Font.BOLD, 22f);
                FONT_PIXEL_MD = base.deriveFont(Font.BOLD, 15f);
                FONT_PIXEL_SM = base.deriveFont(Font.BOLD, 12f);
                FONT_PIXEL_XS = base.deriveFont(Font.BOLD, 10f);
            }
        } catch (Exception ignored) {}
        if (FONT_PIXEL_LG == null) {
            FONT_PIXEL_LG = new Font("Monospaced", Font.BOLD, 22);
            FONT_PIXEL_MD = new Font("Monospaced", Font.BOLD, 15);
            FONT_PIXEL_SM = new Font("Monospaced", Font.BOLD, 12);
            FONT_PIXEL_XS = new Font("Monospaced", Font.BOLD, 10);
        }
    }

    private JButton confirmBtn;
    private JTextArea storyPreview;

    public HeroSelectionPanel() {
        setLayout(new BorderLayout(0, 0));
        setBackground(BG_DARK);
        setBorder(new EmptyBorder(28, 36, 28, 36));
        buildUI();
    }

    public void setOnHeroConfirmed(Consumer<HeroDefinition> cb) { this.onHeroConfirmed = cb; }

    // ─────────────────────────────────────────────────────────────────────────
    private void buildUI() {

        // ── Title banner ──────────────────────────────────────────────────────
        JPanel titlePanel = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // subtle horizontal rule below
                g2.setColor(BORDER_NORM);
                g2.setStroke(new BasicStroke(1));
                g2.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
                g2.dispose();
            }
        };
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(BG_DARK);
        titlePanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel eyebrow = new JLabel("✦  CHRONICLES OF BATTLE  ✦", SwingConstants.CENTER);
        eyebrow.setFont(FONT_PIXEL_XS);
        eyebrow.setForeground(GOLD_DIM);
        eyebrow.setAlignmentX(CENTER_ALIGNMENT);

        JLabel title = new JLabel("Choose Your Champion", SwingConstants.CENTER);
        title.setFont(FONT_PIXEL_LG);
        title.setForeground(GOLD);
        title.setAlignmentX(CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Your fate is written by the warrior you become", SwingConstants.CENTER);
        sub.setFont(FONT_PIXEL_XS);
        sub.setForeground(TEXT_DIM);
        sub.setAlignmentX(CENTER_ALIGNMENT);

        titlePanel.add(eyebrow);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(title);
        titlePanel.add(Box.createVerticalStrut(3));
        titlePanel.add(sub);

        // ── Hero card grid ────────────────────────────────────────────────────
        JPanel grid = new JPanel(new GridLayout(1, DataManager.getData().getHeroes().size(), 14, 0));
        grid.setBackground(BG_DARK);

        for (HeroDefinition def : DataManager.getData().getHeroes()) {
            HeroCard card = new HeroCard(def);
            card.addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) { selectHero(def, card); }
                @Override public void mouseEntered(MouseEvent e) { if (def != selectedHero) card.setHover(true); }
                @Override public void mouseExited (MouseEvent e) { card.setHover(false); }
            });
            cards.add(card);
            grid.add(card);
        }

        // ── Story preview ─────────────────────────────────────────────────────
        storyPreview = new JTextArea("Select a hero to read their story...") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(14, 11, 6));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        storyPreview.setEditable(false);
        storyPreview.setLineWrap(true);
        storyPreview.setWrapStyleWord(true);
        storyPreview.setFont(FONT_PIXEL_XS);
        storyPreview.setForeground(TEXT_DIM);
        storyPreview.setOpaque(false);
        storyPreview.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_NORM, 1),
                new EmptyBorder(10, 14, 10, 14)
        ));
        storyPreview.setPreferredSize(new Dimension(0, 72));

        // ── Confirm button ────────────────────────────────────────────────────
        confirmBtn = new JButton("⚔ Enter the Arena") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = isEnabled()
                        ? (getModel().isRollover()
                        ? new Color(55, 38, 8)
                        : new Color(30, 22, 5))
                        : new Color(20, 18, 14);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                Color border = isEnabled() ? BORDER_SEL : BORDER_NORM;
                g2.setColor(border);
                g2.setStroke(new BasicStroke(isEnabled() ? 2f : 1f));
                g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        confirmBtn.setFont(FONT_PIXEL_SM);
        confirmBtn.setEnabled(false);
        confirmBtn.setForeground(new Color(120, 90, 30));
        confirmBtn.setContentAreaFilled(false);
        confirmBtn.setBorderPainted(false);
        confirmBtn.setFocusPainted(false);
        confirmBtn.setOpaque(false);
        confirmBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        confirmBtn.setBorder(new EmptyBorder(11, 44, 11, 44));

        confirmBtn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                if (confirmBtn.isEnabled()) {
                    confirmBtn.setForeground(GOLD_BRIGHT);
                    confirmBtn.repaint();
                }
            }
            @Override public void mouseExited(MouseEvent e) {
                confirmBtn.setForeground(confirmBtn.isEnabled() ? GOLD : new Color(120, 90, 30));
                confirmBtn.repaint();
            }
        });

        confirmBtn.addActionListener(e -> {
            if (selectedHero != null && onHeroConfirmed != null)
                onHeroConfirmed.accept(selectedHero);
        });

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setBackground(BG_DARK);
        btnPanel.setBorder(new EmptyBorder(12, 0, 0, 0));
        btnPanel.add(confirmBtn);

        JPanel bottom = new JPanel(new BorderLayout(0, 0));
        bottom.setBackground(BG_DARK);
        bottom.setBorder(new EmptyBorder(16, 0, 0, 0));
        bottom.add(storyPreview, BorderLayout.CENTER);
        bottom.add(btnPanel, BorderLayout.SOUTH);

        add(titlePanel, BorderLayout.NORTH);
        add(grid,       BorderLayout.CENTER);
        add(bottom,     BorderLayout.SOUTH);
    }

    // ─────────────────────────────────────────────────────────────────────────
    private void selectHero(HeroDefinition def, HeroCard clickedCard) {
        selectedHero = def;
        for (HeroCard c : cards) {
            c.setSelected(c.getDefinition() == def);
            c.setHover(false);
        }
        storyPreview.setForeground(TEXT_BRIGHT);
        storyPreview.setText(def.backstory != null ? def.backstory : "");
        confirmBtn.setEnabled(true);
        confirmBtn.setForeground(GOLD);
        confirmBtn.repaint();
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  Inner class: HeroCard
    // ═════════════════════════════════════════════════════════════════════════
    private static class HeroCard extends JPanel {
        private final HeroDefinition def;
        private boolean selected = false;
        private boolean hover    = false;
        private final JLabel nameLabel;

        HeroCard(HeroDefinition def) {
            this.def = def;
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBackground(BG_CARD);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setOpaque(true);
            refreshBorder();

            // ── Sprite top area ───────────────────────────────────────────────
            JPanel top = new JPanel(null) {
                @Override public Dimension getPreferredSize() { return new Dimension(0, 130); }
                @Override public Dimension getMaximumSize()   { return new Dimension(Integer.MAX_VALUE, 130); }
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    // dark gradient bg for sprite area
                    GradientPaint gp = new GradientPaint(
                            0, 0, new Color(8, 6, 3),
                            0, getHeight(), new Color(18, 14, 6));
                    g2.setPaint(gp);
                    g2.fillRect(0, 0, getWidth(), getHeight());
                    // bottom divider line
                    g2.setColor(BORDER_NORM);
                    g2.setStroke(new BasicStroke(1));
                    g2.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
                    g2.dispose();
                }
            };
            top.setOpaque(false);
            top.setAlignmentX(LEFT_ALIGNMENT);

            loadSpriteInto(top, def);

            // ── Body area ─────────────────────────────────────────────────────
            JPanel body = new JPanel();
            body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
            body.setOpaque(false);
            body.setBorder(new EmptyBorder(11, 15, 14, 15));
            body.setAlignmentX(LEFT_ALIGNMENT);

            nameLabel = new JLabel(def.name);
            nameLabel.setFont(FONT_PIXEL_MD);
            nameLabel.setForeground(GOLD);
            nameLabel.setAlignmentX(LEFT_ALIGNMENT);

            JLabel roleLabel = new JLabel(def.role);
            roleLabel.setFont(FONT_PIXEL_XS);
            roleLabel.setForeground(TEXT_DIM);
            roleLabel.setAlignmentX(LEFT_ALIGNMENT);
            roleLabel.setBorder(new EmptyBorder(0, 0, 9, 0));

            StatBar hpBar  = new StatBar("HP",  def.maxHp,  150, HP_GREEN);
            StatBar atkBar = new StatBar("ATK", def.attack,   40, ATK_RED);
            StatBar defBar = new StatBar("DEF", def.defense,  20, DEF_BLUE);

            // thin gold separator
            JPanel sepLine = new JPanel() {
                @Override protected void paintComponent(Graphics g) {
                    g.setColor(BORDER_NORM);
                    g.fillRect(0, 0, getWidth(), 1);
                }
            };
            sepLine.setOpaque(false);
            sepLine.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
            sepLine.setPreferredSize(new Dimension(0, 1));

            body.add(nameLabel);
            body.add(roleLabel);
            body.add(hpBar);
            body.add(Box.createVerticalStrut(4));
            body.add(atkBar);
            body.add(Box.createVerticalStrut(4));
            body.add(defBar);
            body.add(Box.createVerticalStrut(8));
            body.add(sepLine);
            body.add(Box.createVerticalStrut(7));

            body.add(Box.createVerticalStrut(6));
            body.add(makeSkillRow("✨", "Passive", PASSIVE_GOLD, def.passive));
            body.add(Box.createVerticalStrut(6));
            body.add(makeSkillRow(def.skills[0].icon, "Skill 1 – " + def.skills[0].name, SKILL_AMBER, null));
            body.add(Box.createVerticalStrut(6));
            body.add(makeSkillRow(def.skills[1].icon, "Skill 2 – " + def.skills[1].name, SKILL_AMBER, null));
            body.add(Box.createVerticalStrut(6));
            body.add(makeSkillRow(def.skills[2].icon, "Ultimate – " + def.skills[2].name, ULT_ORANGE, null));

            add(top);
            add(body);
        }

        // ── Custom card painting with rounded feel ────────────────────────────
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color bg = selected ? BG_SELECTED : (hover ? new Color(28, 20, 8) : BG_CARD);
            g2.setColor(bg);
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
            super.paintComponent(g);
        }

        private void refreshBorder() {
            if (selected) {
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER_SEL, 2),
                        BorderFactory.createEmptyBorder(0, 0, 0, 0)
                ));
            } else {
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER_NORM, 1),
                        BorderFactory.createEmptyBorder(0, 0, 0, 0)
                ));
            }
        }

        // ── Sprite loader (same logic as before) ──────────────────────────────
        private void loadSpriteInto(JPanel top, HeroDefinition def) {
            String path = switch (def.name) {
                case "Kael Saint Laurent"  -> "/assets/KaelAssets/KaelHeroSelection.png";
                case "Karl Clover Dior IV" -> "/assets/KarlAssets/KarlHeroSelection.png";
                case "Simon Versace"       -> "/assets/SimonAssets/SimonHeroSelection.png";
                default                    -> "/assets/NullAssets/NullHeroSelection.png";
            };
            int frames = switch (def.name) {
                case "Karl Clover Dior IV" -> 9;
                default                    -> 8;
            };
            int startFrame = def.name.equals("Kael Saint Laurent") ? 6 : 0;

            JLabel spriteLabel = new JLabel();
            spriteLabel.setHorizontalAlignment(SwingConstants.CENTER);
            spriteLabel.setVerticalAlignment(SwingConstants.CENTER);

            java.net.URL sheetUrl = getClass().getResource(path);
            if (sheetUrl != null) {
                try {
                    BufferedImage sheet = ImageIO.read(sheetUrl);
                    final int FRAMES = frames;
                    int fw = sheet.getWidth() / FRAMES, fh = sheet.getHeight();
                    ImageIcon[] icons = new ImageIcon[FRAMES];
                    for (int i = 0; i < FRAMES; i++) {
                        BufferedImage crop = sheet.getSubimage(i * fw, 0, fw, fh);
                        BufferedImage t    = new BufferedImage(fw, fh, BufferedImage.TYPE_INT_ARGB);
                        for (int x = 0; x < fw; x++)
                            for (int y = 0; y < fh; y++) {
                                int px = crop.getRGB(x, y);
                                int r = (px >> 16) & 0xFF, gv = (px >> 8) & 0xFF, b = px & 0xFF;
                                t.setRGB(x, y, (r < 30 && gv < 30 && b < 30) ? 0 : px);
                            }
                        icons[i] = new ImageIcon(t);
                    }
                    spriteLabel.setIcon(icons[startFrame]);
                    int[] fi = {startFrame};
                    new Timer(120, e -> {
                        fi[0] = (fi[0] + 1) % FRAMES;
                        spriteLabel.setIcon(icons[fi[0]]);
                    }).start();
                } catch (Exception ex) {
                    spriteLabel.setText(def.emoji);
                    spriteLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
                    spriteLabel.setForeground(GOLD_DIM);
                }
            } else {
                spriteLabel.setText(def.emoji);
                spriteLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
                spriteLabel.setForeground(GOLD_DIM);
            }

            top.addComponentListener(new ComponentAdapter() {
                @Override public void componentResized(ComponentEvent e) {
                    spriteLabel.setBounds(0, 0, top.getWidth(), top.getHeight());
                }
            });
            top.add(spriteLabel);
        }

        private static JPanel makeSkillRow(String icon, String title, Color color, String subtitle) {
            JPanel row = new JPanel();
            row.setLayout(new BoxLayout(row, BoxLayout.Y_AXIS));
            row.setOpaque(false);
            row.setAlignmentX(LEFT_ALIGNMENT);

            JLabel titleLbl = new JLabel(icon + " " + title);
            titleLbl.setFont(FONT_PIXEL_XS);
            titleLbl.setForeground(color);
            titleLbl.setAlignmentX(LEFT_ALIGNMENT);
            row.add(titleLbl);

            if (subtitle != null && !subtitle.isBlank()) {
                // Show only the short name before " — " or " – "
                String shortSub = subtitle.split(" — | – ")[0].trim();
                JLabel subLbl = new JLabel("   " + shortSub);
                subLbl.setFont(FONT_PIXEL_XS);
                subLbl.setForeground(TEXT_DIM);
                subLbl.setAlignmentX(LEFT_ALIGNMENT);
                row.add(subLbl);
            }

            return row;
        }

        HeroDefinition getDefinition() { return def; }

        void setSelected(boolean sel) {
            this.selected = sel;
            refreshBorder();
            nameLabel.setForeground(sel ? GOLD_BRIGHT : GOLD);
            repaint();
        }

        void setHover(boolean h) {
            if (selected) return;
            this.hover = h;
            repaint();
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  Inner class: StatBar
    // ═════════════════════════════════════════════════════════════════════════
    private static class StatBar extends JPanel {
        StatBar(String label, int value, int maxValue, Color barColor) {
            setLayout(new BorderLayout(6, 0));
            setOpaque(false);
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 18));
            setAlignmentX(LEFT_ALIGNMENT);

            JLabel lbl = new JLabel(label);
            lbl.setFont(FONT_PIXEL_XS);
            lbl.setForeground(TEXT_DIM);
            lbl.setPreferredSize(new Dimension(30, 14));

            JProgressBar bar = new JProgressBar(0, maxValue) {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(22, 16, 6));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
                    int fill = (int)((double) getValue() / getMaximum() * getWidth());
                    if (fill > 0) {
                        g2.setColor(getForeground());
                        g2.fillRoundRect(0, 0, fill, getHeight(), getHeight(), getHeight());
                    }
                    g2.dispose();
                }
            };
            bar.setValue(value);
            bar.setStringPainted(false);
            bar.setForeground(barColor);
            bar.setOpaque(false);
            bar.setBorderPainted(false);
            bar.setPreferredSize(new Dimension(0, 7));

            JLabel val = new JLabel(String.valueOf(value));
            val.setFont(FONT_PIXEL_XS);
            val.setForeground(TEXT_BRIGHT);
            val.setPreferredSize(new Dimension(32, 14));
            val.setHorizontalAlignment(SwingConstants.RIGHT);

            add(lbl, BorderLayout.WEST);
            add(bar, BorderLayout.CENTER);
            add(val, BorderLayout.EAST);
        }
    }
}