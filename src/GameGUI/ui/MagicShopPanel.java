package GameGUI.ui;

import GameGUI.model.entity.Combatant;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class MagicShopPanel extends JPanel {

    private Combatant player;
    private Runnable onLeaveShop;

    private JLabel shardsLabel;
    private JPanel itemsContainer;

    // Colors
    private static final Color BG_DEEP       = new Color(8,  6,  20);
    private static final Color BG_PANEL      = new Color(14, 11, 32);
    private static final Color BG_CARD       = new Color(20, 16, 45);
    private static final Color BORDER_GLOW   = new Color(100, 60, 200);
    private static final Color TEXT_TITLE    = new Color(240, 230, 255);
    private static final Color TEXT_DESC     = new Color(160, 150, 180);
    private static final Color GOLD          = new Color(255, 215, 0);

    public MagicShopPanel() {
        setLayout(new BorderLayout());
        setBackground(BG_DEEP);
        buildUI();
    }

    public void setOnLeaveShop(Runnable onLeaveShop) {
        this.onLeaveShop = onLeaveShop;
    }

    public void loadPlayer(Combatant currentCombatant) {
        this.player = currentCombatant;
        updateShardsDisplay();

        // Rebuild the shop to check which items the player already owns
        buildItemsList();
    }

    private void buildUI() {
        // --- HEADER ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BG_PANEL);
        headerPanel.setBorder(new EmptyBorder(20, 40, 20, 40));

        JLabel titleLabel = new JLabel("🏺 THE MYSTIC MERCHANT", SwingConstants.LEFT);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 28));
        titleLabel.setForeground(TEXT_TITLE);

        shardsLabel = new JLabel("💠 Soul Shards: 0", SwingConstants.RIGHT);
        shardsLabel.setFont(new Font("Monospaced", Font.BOLD, 20));
        shardsLabel.setForeground(GOLD);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(shardsLabel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // --- ITEMS SCROLL PANE ---
        itemsContainer = new JPanel();
        // GridLayout(0, 2) means 2 columns, unlimited rows
        itemsContainer.setLayout(new GridLayout(0, 2, 20, 20));
        itemsContainer.setBackground(BG_DEEP);
        itemsContainer.setBorder(new EmptyBorder(20, 40, 20, 40));

        JScrollPane scrollPane = new JScrollPane(itemsContainer);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Smooth scrolling
        add(scrollPane, BorderLayout.CENTER);

        // --- FOOTER ---
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBackground(BG_DEEP);
        footerPanel.setBorder(new EmptyBorder(10, 0, 30, 60));

        JButton leaveBtn = new JButton("Leave Shop");
        leaveBtn.setFont(new Font("Monospaced", Font.BOLD, 18));
        leaveBtn.setForeground(Color.WHITE);
        leaveBtn.setBackground(new Color(180, 40, 40));
        leaveBtn.setFocusPainted(false);
        leaveBtn.setPreferredSize(new Dimension(200, 50));
        leaveBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        leaveBtn.addActionListener(e -> {
            if (onLeaveShop != null) onLeaveShop.run();
        });

        footerPanel.add(leaveBtn);
        add(footerPanel, BorderLayout.SOUTH);
    }

    private void buildItemsList() {
        itemsContainer.removeAll();
        if (player == null) return;

        var weapon = player.inventory.getEquippedWeapon();
        var armor = player.inventory.getEquippedArmor();

        // =====================================================================
        // 💎 PERMANENT STAT UPGRADES
        // =====================================================================
        itemsContainer.add(createItemCard(
                "Vitality Blessing", "Permanent +100 Max HP.", "💖", 10,
                () -> false, // Can buy multiple times
                btn -> { player.maxHp += 100; player.heal(100); },
                "All"
        ));

        itemsContainer.add(createItemCard(
                "Attack Infusion", "Permanent +8 Base ATK.", "⚔️", 12,
                () -> false,
                btn -> { player.baseAttack += 8; player.recalculateBuffs(); },
                "All"
        ));

        // =====================================================================
        // 🌟 WEAPON ENCHANTMENTS
        // =====================================================================
        itemsContainer.add(createItemCard(
                "Vital Surge", "+5% Lifesteal on attacks.", "❤️", 28,
                () -> player.hasVitalSurge,
                btn -> {
                    player.hasVitalSurge = true;
                    if (weapon != null) {
                        weapon.addLifestealPercent += 5;
                        weapon.enchantments.put("💖 Vital Surge", "(+5% Lifesteal)");
                    }
                },
                "Swordsman", "Archer", "Mage"
        ));

        itemsContainer.add(createItemCard(
                "Shock Bind", "20% chance to stun target.", "⚡", 30,
                () -> player.hasShockBind,
                btn -> {
                    player.hasShockBind = true;
                    if (weapon != null) {
                        weapon.stunChance += 20;
                        weapon.enchantments.put("⛓️ Shockbind", "(20% Stun chance)");
                    }
                },
                "Swordsman" // Restricted!
        ));

        itemsContainer.add(createItemCard(
                "Frost Arrow", "20% chance to freeze target.", "❄️", 30,
                () -> player.hasFrostArrow,
                btn -> {
                    player.hasFrostArrow = true;
                    if (weapon != null) {
                        weapon.freezeChance += 20;
                        weapon.enchantments.put("❄️ Frost Arrow", "(20% Freeze chance)");
                    }
                },
                "Archer" // Restricted!
        ));

        itemsContainer.add(createItemCard(
                "Arc Surge", "+3 Energy restored per attack.", "✨", 26,
                () -> player.hasArcSurge,
                btn -> {
                    player.hasArcSurge = true;
                    if (weapon != null) {
                        weapon.energyPerAttack += 3;
                        weapon.enchantments.put("✨ Arc Surge", "(+3 Energy per hit)");
                    }
                },
                "Mage" // Restricted!
        ));

        itemsContainer.add(createItemCard(
                "Venom Infusion", "20% chance to poison target.", "☠️", 30,
                () -> player.hasVenomInfusion,
                btn -> {
                    player.hasVenomInfusion = true;
                    if (weapon != null) {
                        weapon.poisonChance += 20;
                        weapon.enchantments.put("☠️ Venom Infusion", "(+20% Poison chance)");
                    }
                },
                "All"
        ));

        itemsContainer.add(createItemCard(
                "Razor Edge", "20% chance to inflict Bleed.", "🩸", 32,
                () -> player.hasRazorEdge,
                btn -> {
                    player.hasRazorEdge = true;
                    if (weapon != null) {
                        weapon.bleedChance += 20;
                        weapon.enchantments.put("🩸 Razor Edge", "(+20% Bleed chance)");
                    }
                },
                "Swordsman", "Archer" // Allowed for two classes!
        ));

        // =====================================================================
        // 🛡️ ARMOR & SPECIAL
        // =====================================================================
        itemsContainer.add(createItemCard(
                "Fortified Plating", "Armor gains +10 DEF.", "🛡️", 26,
                () -> player.hasFortifiedPlating,
                btn -> {
                    player.hasFortifiedPlating = true;
                    if (armor != null) {
                        armor.addDefBuff += 10;
                        armor.hasEnchantment = true;
                        player.recalculateBuffs();
                    }
                },
                "All"
        ));

        itemsContainer.add(createItemCard(
                "Phoenix Soulstone", "Revive once upon death.", "🕊️", 40,
                () -> player.hasPhoenixSoulstone,
                btn -> { player.hasPhoenixSoulstone = true; }, // You can link this to inventory later
                "All"
        ));

        itemsContainer.revalidate();
        itemsContainer.repaint();
    }

    // Notice the new "String... allowedRoles" parameter at the end!
    private JPanel createItemCard(String name, String desc, String icon, int cost,
                                  Supplier<Boolean> isOwned, Consumer<JButton> applyUpgrade,
                                  String... allowedRoles) {

        JPanel card = new JPanel(new BorderLayout(15, 10));
        card.setBackground(BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_GLOW, 1),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel iconLabel = new JLabel(icon, SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        iconLabel.setPreferredSize(new Dimension(60, 60));

        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setOpaque(false);

        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        nameLabel.setForeground(TEXT_TITLE);

        JLabel descLabel = new JLabel(desc);
        descLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        descLabel.setForeground(TEXT_DESC);

        textPanel.add(nameLabel);
        textPanel.add(descLabel);

        JButton buyBtn = new JButton();
        buyBtn.setFont(new Font("Monospaced", Font.BOLD, 14));
        buyBtn.setFocusPainted(false);
        buyBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        buyBtn.setPreferredSize(new Dimension(140, 40));

        // --- Verify Class Restrictions ---
        boolean isClassAllowed = false;
        if (allowedRoles.length == 0 || allowedRoles[0].equalsIgnoreCase("All")) {
            isClassAllowed = true;
        } else {
            for (String role : allowedRoles) {
                if (player.getClassType().equalsIgnoreCase(role)) {
                    isClassAllowed = true;
                    break;
                }
            }
        }

        // --- Setup Button State ---
        if (!isClassAllowed) {
            buyBtn.setText(allowedRoles[0] + " Only"); // e.g. "Swordsman Only"
            buyBtn.setBackground(new Color(80, 30, 30)); // Deep red for locked
            buyBtn.setForeground(new Color(150, 100, 100));
            buyBtn.setEnabled(false);
        } else if (isOwned.get()) {
            buyBtn.setText("Owned");
            buyBtn.setBackground(new Color(40, 40, 40));
            buyBtn.setForeground(Color.GRAY);
            buyBtn.setEnabled(false);
        } else {
            buyBtn.setText("Buy (" + cost + " 💠)");
            buyBtn.setBackground(new Color(60, 40, 100));
            buyBtn.setForeground(Color.WHITE);

            buyBtn.addActionListener(e -> {
                if (player.soulShards >= cost) {
                    player.soulShards -= cost;
                    applyUpgrade.accept(buyBtn);
                    updateShardsDisplay();

                    buyBtn.setText("Owned");
                    buyBtn.setBackground(new Color(40, 40, 40));
                    buyBtn.setForeground(Color.GRAY);
                    buyBtn.setEnabled(false);

                    JOptionPane.showMessageDialog(this, "Purchased " + name + "!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Not enough Soul Shards!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
        }

        card.add(iconLabel, BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);
        card.add(buyBtn, BorderLayout.EAST);

        return card;
    }

    private void updateShardsDisplay() {
        if (player != null && shardsLabel != null) {
            shardsLabel.setText("💠 Soul Shards: " + player.soulShards);
        }
    }
}