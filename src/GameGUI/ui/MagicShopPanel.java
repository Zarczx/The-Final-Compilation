package GameGUI.ui;

import GameGUI.model.entity.Combatant;
import GameGUI.model.entity.ShopPassive;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.InputStream;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.imageio.ImageIO;

public class MagicShopPanel extends JPanel {

    // ── Icon cache (no reprocessing on hover) ────────────────────────────────
    private final java.util.Map<String, ImageIcon> iconCache = new java.util.HashMap<>();

    // ── State ────────────────────────────────────────────────────────────────
    private Combatant player;
    private Runnable  onLeaveShop;

    // ── Widgets ──────────────────────────────────────────────────────────────
    private JLabel shardsLabel;
    private JPanel itemsGrid;

    // ── Paths ────────────────────────────────────────────────────────────────
    private static final String ASSETS    = "assets/MagicShopGUIAssets/";
    private static final String FONT_PATH = "assets/AssetFont/Pixelari.ttf";

    // ── Button image table { itemName, normalPng, hoveredPng } ───────────────
    private static final String[][] BTN_TABLE = {
            { "Vitality Blessing", "VitalityCost.png",        "VitalityCostHovered.png"       },
            { "Attack Infusion",   "AttackInfusionCost.png",  "AttackInfusionCostHovered.png" },
            { "Vital Surge",       "VitalCost.png",           "VitalHovered.png"              },
            { "Shock Bind",        "VenomCost.png",           "VenomCostHovered.png"          },
            { "Frost Arrow",       "FrostCost.png",           "FrostCostHovered.png"          },
            { "Arc Surge",         "ArcSurgeCost.png",        "ArcSurgeCostHovered.png"       },
            { "Venom Infusion",    "VenomCost.png",           "VenomCostHovered.png"          },
            { "Razor Edge",        "RazorEdgeCost.png",       "RazorEdgeCostHovered.png"      },
            { "Fortified Plating", "FortifiedCost.png",       "FortifiedCostHovered.png"      },
            { "Phoenix Soulstone", "PhoenixSoulStone.png",    "PhoenixSoulStoneHovered.png"   },
    };

    // ── Button display size ───────────────────────────────────────────────────
    private static final int BTN_W   = 110;
    private static final int BTN_H   = 52;
    private static final int LEAVE_W = 180;
    private static final int LEAVE_H = 60;

    // ── Absolute X positions for left/right column button slots ──────────────
    // Tweak these two values to slide ALL buttons left/right together
    private static final int LEFT_X  = 695;   // left column button X
    private static final int RIGHT_X = 1110;  // right column button X

    // ── Absolute Y positions for each row ────────────────────────────────────
    // Tweak these to shift individual rows up/down
    private static final int ROW1_Y = 135;
    private static final int ROW2_Y = 227;
    private static final int ROW3_Y = 320;
    private static final int ROW4_Y = 412;
    private static final int ROW5_Y = 505;

    // ── Font ──────────────────────────────────────────────────────────────────
    private Font pixelariFont;

    // ════════════════════════════════════════════════════════════════════════
    public MagicShopPanel() {
        setOpaque(false);
        loadFont();
        buildLayout();
    }

    // ── Public API ────────────────────────────────────────────────────────────
    public void setOnLeaveShop(Runnable r) { this.onLeaveShop = r; }

    public void loadPlayer(Combatant c) {
        this.player = c;
        prewarmCache();
        updateShardsDisplay();
        populateGrid();
    }

    // ── Pre-load all icons so hover is instant ────────────────────────────────
    private void prewarmCache() {
        for (String[] row : BTN_TABLE) {
            scaledIcon(row[1], BTN_W, BTN_H);
            scaledIcon(row[2], BTN_W, BTN_H);
        }
        scaledIcon("TakeItemOnly.png", BTN_W,   BTN_H);
        scaledIcon("SwordmanOnly.png", BTN_W,   BTN_H);
        scaledIcon("ArcherOnly.png",   BTN_W,   BTN_H);
        scaledIcon("MageOnly.png",     BTN_W,   BTN_H);
        scaledIcon("LeaveShop.png",    LEAVE_W, LEAVE_H);
        scaledIcon("LeaveShopHovered.png", LEAVE_W, LEAVE_H);
    }

    // ── Background ────────────────────────────────────────────────────────────
    @Override
    protected void paintComponent(Graphics g) {
        Image bg = loadImg("MagicShop.png");
        if (bg != null) {
            g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(new Color(180, 140, 70));
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    //  LAYOUT
    // ════════════════════════════════════════════════════════════════════════

    private void showStyledMessage(String message, String title, boolean isError) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), title, true);
        dialog.setUndecorated(true);
        dialog.setSize(320, 160);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout(10, 10)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(30, 15, 50));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(new Color(120, 60, 200));
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 20, 20);
            }
        };
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));

        JLabel msg = new JLabel(message, SwingConstants.CENTER);
        msg.setFont(pixelariFont.deriveFont(Font.BOLD, 16f));
        msg.setForeground(isError ? new Color(255, 80, 80) : new Color(200, 160, 255));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(pixelariFont.deriveFont(Font.BOLD, 18f));
        titleLabel.setForeground(new Color(255, 215, 30));

        JButton ok = new JButton("OK");
        ok.setFont(pixelariFont.deriveFont(Font.BOLD, 14f));
        ok.setForeground(new Color(255, 215, 30));
        ok.setBackground(new Color(70, 30, 120));
        ok.setFocusPainted(false);
        ok.setBorder(BorderFactory.createLineBorder(new Color(120, 60, 200), 2));
        ok.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        ok.setPreferredSize(new Dimension(80, 32));
        ok.addActionListener(e -> dialog.dispose());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setOpaque(false);
        btnPanel.add(ok);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(msg,        BorderLayout.CENTER);
        panel.add(btnPanel,   BorderLayout.SOUTH);
        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }

    private void buildLayout() {
        setLayout(null);

        // ── Soul Shards label ─────────────────────────────────────────────────
        shardsLabel = new JLabel("Soul Shards: 0", SwingConstants.LEFT);
        shardsLabel.setFont(pixelariFont.deriveFont(Font.BOLD, 13f));
        shardsLabel.setForeground(new Color(90, 0, 140));  // dark violet
        shardsLabel.setBounds(1074, 42, 220, 36);
        add(shardsLabel);

        // ── Items layer (absolute, full panel size) ───────────────────────────
        itemsGrid = new JPanel(null);
        itemsGrid.setOpaque(false);
        itemsGrid.setBounds(0, 0, 1280, 720);
        add(itemsGrid);

        // ── Leave Shop ────────────────────────────────────────────────────────
        JButton leave = imageBtn("LeaveShop.png", "LeaveShopHovered.png", LEAVE_W, LEAVE_H);
        // Centered below the grid, over the parchment's leave button slot
        leave.setBounds(1020, 615, LEAVE_W, LEAVE_H);
        leave.addActionListener(e -> { if (onLeaveShop != null) onLeaveShop.run(); });
        add(leave);
    }

    // ════════════════════════════════════════════════════════════════════════
    //  ITEM GRID  – each button placed at exact pixel coordinates
    // ════════════════════════════════════════════════════════════════════════
    // ════════════════════════════════════════════════════════════════════════
    //  ITEM GRID  – each button placed at exact pixel coordinates
    // ════════════════════════════════════════════════════════════════════════
    private void populateGrid() {
        itemsGrid.removeAll();
        if (player == null) { itemsGrid.revalidate(); itemsGrid.repaint(); return; }

        var wpn   = player.inventory.getEquippedWeapon();
        var armor = player.inventory.getEquippedArmor();

        // ── Row 1 ─────────────────────────────────────────────────────────────
        slot("Vitality Blessing", 10, LEFT_X,  ROW1_Y,
                () -> false,
                b -> { player.setMaxHp(player.getMaxHp() + 100); player.heal(100); },
                "All");

        slot("Attack Infusion", 12, RIGHT_X, ROW1_Y,
                () -> false,
                b -> { player.setBaseAttack(player.getBaseAttack() + 8); player.recalculateBuffs(); },
                "All");

        // ── Row 2 ─────────────────────────────────────────────────────────────
        slot("Vital Surge", 28, LEFT_X, ROW2_Y,
                () -> player.hasPassive(ShopPassive.VITAL_SURGE),
                b -> { player.addPassive(ShopPassive.VITAL_SURGE);
                    if (wpn != null) { wpn.addLifestealPercent += 5;
                        wpn.enchantments.put("💖 Vital Surge", "(+5% Lifesteal)"); }},
                "Swordsman", "Archer", "Mage");

        slot("Shock Bind", 30, RIGHT_X, ROW2_Y,
                () -> player.hasPassive(ShopPassive.SHOCK_BIND),
                b -> { player.addPassive(ShopPassive.SHOCK_BIND);
                    if (wpn != null) { wpn.stunChance += 20;
                        wpn.enchantments.put("⛓️ Shockbind", "(20% Stun chance)"); }},
                "Swordsman");

        // ── Row 3 ─────────────────────────────────────────────────────────────
        slot("Frost Arrow", 30, LEFT_X, ROW3_Y,
                () -> player.hasPassive(ShopPassive.FROST_ARROW),
                b -> { player.addPassive(ShopPassive.FROST_ARROW);
                    if (wpn != null) { wpn.freezeChance += 20;
                        wpn.enchantments.put("❄️ Frost Arrow", "(20% Freeze chance)"); }},
                "Archer");

        slot("Arc Surge", 26, RIGHT_X, ROW3_Y,
                () -> player.hasPassive(ShopPassive.ARC_SURGE),
                b -> { player.addPassive(ShopPassive.ARC_SURGE);
                    if (wpn != null) { wpn.energyPerAttack += 3;
                        wpn.enchantments.put("✨ Arc Surge", "(+3 Energy per hit)"); }},
                "Mage");

        // ── Row 4 ─────────────────────────────────────────────────────────────
        slot("Venom Infusion", 30, LEFT_X, ROW4_Y,
                () -> player.hasPassive(ShopPassive.VENOM_INFUSION),
                b -> { player.addPassive(ShopPassive.VENOM_INFUSION);
                    if (wpn != null) { wpn.poisonChance += 20;
                        wpn.enchantments.put("☠️ Venom Infusion", "(+20% Poison chance)"); }},
                "All");

        slot("Razor Edge", 32, RIGHT_X, ROW4_Y,
                () -> player.hasPassive(ShopPassive.RAZOR_EDGE),
                b -> { player.addPassive(ShopPassive.RAZOR_EDGE);
                    if (wpn != null) { wpn.bleedChance += 20;
                        wpn.enchantments.put("🩸 Razor Edge", "(+20% Bleed chance)"); }},
                "Swordsman", "Archer");

        // ── Row 5 ─────────────────────────────────────────────────────────────
        slot("Fortified Plating", 26, LEFT_X, ROW5_Y,
                () -> player.hasPassive(ShopPassive.FORTIFIED_PLATING),
                b -> { player.addPassive(ShopPassive.FORTIFIED_PLATING);
                    if (armor != null) { armor.addDefBuff += 10;
                        armor.hasEnchantment = true; player.recalculateBuffs(); }},
                "All");

        slot("Phoenix Soulstone", 40, RIGHT_X, ROW5_Y,
                () -> player.hasPassive(ShopPassive.PHOENIX_SOULSTONE),
                b -> player.addPassive(ShopPassive.PHOENIX_SOULSTONE),
                "All");

        itemsGrid.revalidate();
        itemsGrid.repaint();
    }

    // ── Places one button at exact pixel coords ───────────────────────────────
    private void slot(String name, int cost, int x, int y,
                      Supplier<Boolean> owned, Consumer<JButton> apply,
                      String... roles) {
        JButton btn = makeButton(name, cost, owned, apply, roles);
        btn.setBounds(x, y, BTN_W, BTN_H);
        itemsGrid.add(btn);
    }

    // ════════════════════════════════════════════════════════════════════════
    //  BUTTON LOGIC
    // ════════════════════════════════════════════════════════════════════════
    private JButton makeButton(String name, int cost,
                               Supplier<Boolean> owned, Consumer<JButton> apply,
                               String[] roles) {

        // Class restricted
        if (!classAllowed(roles)) {
            return imageBtn(onlyImg(roles[0]), onlyImg(roles[0]), BTN_W, BTN_H);
        }

        // Already owned
        if (owned.get()) {
            JButton b = imageBtn("TakeItemOnly.png", "TakeItemOnly.png", BTN_W, BTN_H);
            b.setEnabled(false);
            return b;
        }

        // Purchasable
        String[] imgs = btnImgs(name);
        JButton  b    = imageBtn(imgs[0], imgs[1], BTN_W, BTN_H);

        b.addActionListener(e -> {
            if (player.getSoulShards() >= cost) {
                player.setSoulShards(player.getSoulShards() - cost);
                apply.accept(b);
                updateShardsDisplay();
                ImageIcon oi = scaledIcon("TakeItemOnly.png", BTN_W, BTN_H);
                if (oi != null) { b.setIcon(oi); b.setRolloverIcon(oi); b.setPressedIcon(oi); }
                b.setEnabled(false);
                showStyledMessage("Purchased " + name + "!", "Success", false);
            } else {
                showStyledMessage("Not enough Soul Shards!", "Insufficient Shards", true);
            }
        });
        return b;
    }

    private boolean classAllowed(String[] roles) {
        if (roles.length == 0 || roles[0].equalsIgnoreCase("All")) return true;
        if (player == null) return false;
        for (String r : roles) if (player.getClassType().equalsIgnoreCase(r)) return true;
        return false;
    }

    private String onlyImg(String role) {
        return switch (role.toLowerCase()) {
            case "swordsman" -> "SwordmanOnly.png";
            case "archer"    -> "ArcherOnly.png";
            case "mage"      -> "MageOnly.png";
            default          -> "SwordmanOnly.png";
        };
    }

    private String[] btnImgs(String name) {
        for (String[] row : BTN_TABLE)
            if (row[0].equals(name)) return new String[]{ row[1], row[2] };
        return new String[]{ "VitalityCost.png", "VitalityCostHovered.png" };
    }

    // ════════════════════════════════════════════════════════════════════════
    //  IMAGE BUTTON
    // ════════════════════════════════════════════════════════════════════════
    private JButton imageBtn(String normal, String hover, int w, int h) {
        JButton btn = new JButton();
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(w, h));

        ImageIcon ni = scaledIcon(normal, w, h);
        ImageIcon hi = scaledIcon(hover,  w, h);
        if (ni != null) {
            btn.setIcon(ni);
            btn.setRolloverIcon(hi != null ? hi : ni);
            btn.setPressedIcon(hi != null ? hi : ni);
        } else {
            btn.setText(normal.replace(".png", ""));
            btn.setForeground(new Color(255, 220, 80));
        }
        return btn;
    }

    // ════════════════════════════════════════════════════════════════════════
    //  ASSET HELPERS
    // ════════════════════════════════════════════════════════════════════════
    private Image loadImg(String filename) {
        try {
            File f = new File(ASSETS + filename);
            if (f.exists()) return ImageIO.read(f);
            InputStream is = getClass().getResourceAsStream("/" + ASSETS + filename);
            if (is != null) return ImageIO.read(is);
        } catch (Exception ignored) {}
        return null;
    }

    private ImageIcon scaledIcon(String filename, int w, int h) {
        String key = filename + "_" + w + "_" + h;
        if (iconCache.containsKey(key)) return iconCache.get(key);
        Image img = loadImg(filename);
        if (img == null) return null;
        ImageIcon icon = new ImageIcon(img.getScaledInstance(w, h, Image.SCALE_SMOOTH));
        iconCache.put(key, icon);
        return icon;
    }

    // ════════════════════════════════════════════════════════════════════════
    //  FONT
    // ════════════════════════════════════════════════════════════════════════
    private void loadFont() {
        try {
            File f = new File(FONT_PATH);
            if (f.exists()) {
                pixelariFont = Font.createFont(Font.TRUETYPE_FONT, f).deriveFont(Font.BOLD, 22f);
            } else {
                InputStream is = getClass().getResourceAsStream("/" + FONT_PATH);
                if (is != null)
                    pixelariFont = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(Font.BOLD, 22f);
            }
            if (pixelariFont != null)
                GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(pixelariFont);
        } catch (Exception e) {
            System.err.println("[MagicShop] Font failed: " + e.getMessage());
        }
        if (pixelariFont == null) pixelariFont = new Font("Monospaced", Font.BOLD, 22);
    }

    // ── Shard display ─────────────────────────────────────────────────────────
    private void updateShardsDisplay() {
        if (player != null && shardsLabel != null)
            shardsLabel.setText("Soul Shards: " + player.getSoulShards());
    }
}