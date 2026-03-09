package GameGUI;

import GameGUI.BattleLogic.*;
import GameGUI.HeroData.HeroDefinition;
import GameGUI.HeroData.EnemyDefinition;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;

/**
 * BattlePanel.java
 *
 * Full battle screen panel.
 *
 * Integrates:
 *  - BattleLogic (ported from Battle.java) for all combat calculations
 *  - Sprite / animation system from GameScreen.java's anonymous JPanel
 *  - HP/Energy bars mirroring displayBattleStats() from Battle.java
 *  - Typed battle log (mirrors Battle.java's PrintUtil / ColorUtil output)
 *  - Action buttons: Attack, Defend, Special (mirrors preBattleOptions() + turn())
 *  - Revive logic (mirrors Phoenix Soulstone + ReviveTrial flow)
 *  - Victory / Defeat overlays with Restart and Back options
 *
 * Separation of concerns:
 *  - This class handles ONLY display and user interaction
 *  - All combat math lives in BattleLogic.java
 *  - All data lives in HeroData.java
 */
public class BattlePanel extends JPanel {

    // ─── Callbacks ────────────────────────────────────────────────────────────
    private Runnable onReturnToSelection;
    private Runnable onRestartBattle;

    // ─── Battle engine ────────────────────────────────────────────────────────
    private BattleLogic engine;
    private HeroDefinition heroDef;
    private EnemyDefinition enemyDef;

    // ─── Image Assets ─────────────────────────────────────────────────────────
    private Image backgroundImage;
    private Image heroSprite;
    private Image enemySprite;

    // ─── State ────────────────────────────────────────────────────────────────
    private boolean animating = false;

    // ─── Colors ───────────────────────────────────────────────────────────────
    private static final Color BG_DARK      = new Color(12, 10, 22);
    private static final Color BG_PANEL     = new Color(22, 20, 38);
    private static final Color BG_LOG       = new Color(10, 9, 18);
    private static final Color GOLD         = new Color(201, 168, 76);
    private static final Color GOLD_DIM     = new Color(100, 78, 30);
    private static final Color TEXT_BRIGHT  = new Color(240, 232, 208);
    private static final Color TEXT_DIM     = new Color(140, 125, 95);
    private static final Color GREEN        = new Color(39, 174, 96);
    private static final Color GREEN_DARK   = new Color(26, 107, 74);
    private static final Color RED          = new Color(192, 57, 43);
    private static final Color RED_DARK     = new Color(100, 20, 20);
    private static final Color BLUE         = new Color(52, 120, 219);
    private static final Color PURPLE       = new Color(130, 60, 200);
    private static final Color OVERLAY_BG   = new Color(0, 0, 0, 200);

    // ─── Fonts ────────────────────────────────────────────────────────────────
    private static final Font FONT_TITLE  = new Font("Monospaced", Font.BOLD,  16);
    private static final Font FONT_NAME   = new Font("Monospaced", Font.BOLD,  14);
    private static final Font FONT_STAT   = new Font("Monospaced", Font.PLAIN, 11);
    private static final Font FONT_LOG    = new Font("Monospaced", Font.PLAIN, 12);
    private static final Font FONT_BTN    = new Font("Monospaced", Font.BOLD,  12);
    private static final Font FONT_RESULT = new Font("Monospaced", Font.BOLD,  28);
    private static final Font FONT_EMOJI  = new Font("Segoe UI Emoji", Font.PLAIN, 56);

    // ─── Child widgets ────────────────────────────────────────────────────────

    // Hero panel
    private JLabel heroEmojiLbl, heroNameLbl, heroRoleLbl;
    private JProgressBar heroHpBar, heroEnergyBar;
    private JLabel heroHpText, heroEnergyText;
    private JLabel heroStatusLbl;

    // Enemy panel
    private JLabel enemyEmojiLbl, enemyNameLbl, enemyRoleLbl;
    private JProgressBar enemyHpBar;
    private JLabel enemyHpText;
    private JLabel enemyStatusLbl;

    // Shared
    private JLabel roundLabel;
    private JLabel turnLabel;

    // Battle log
    private JTextArea logArea;

    // Action buttons
    private JButton attackBtn, defendBtn, specialBtn;
    private JLabel specialCdLabel;

    // Result overlay
    private JPanel resultOverlay;
    private JLabel resultIcon, resultTitle, resultSub;




    // ─── Constructor ──────────────────────────────────────────────────────────

    public BattlePanel() {
        setLayout(new BorderLayout(0, 8));
        setBackground(BG_DARK);
        setBorder(new EmptyBorder(20, 24, 20, 24));
        buildUI();
    }

    public void setOnReturnToSelection(Runnable r) { this.onReturnToSelection = r; }
    public void setOnRestartBattle(Runnable r)     { this.onRestartBattle     = r; }

    // ─── Init / Reset ─────────────────────────────────────────────────────────

    /**
     * Called by GameScreen when the player confirms their hero.
     * Mirrors Battle(player, enemy) constructor + startBattle().
     */
    public void startBattle(HeroDefinition hero, EnemyDefinition enemy) {
        this.heroDef  = hero;
        this.enemyDef = enemy;

        backgroundImage = loadSafeImage(getBackgroundPath(enemy.worldLevel));
        heroSprite  = loadSafeImage("/assets/" + hero.name.toLowerCase() + ".png");
        enemySprite = loadSafeImage("/assets/" + enemy.name.toLowerCase() + ".png");


        Combatant heroC  = HeroData.buildHero(hero);
        Combatant enemyC = HeroData.buildEnemy(enemy);
        this.engine = new BattleLogic(heroC, enemyC, false); // no soulstone by default

        populateCombatantUI();
        refreshBattleUI();
        clearLog();
        addLog("⚔  BATTLE START! " + hero.name.toUpperCase()
                + " VS " + enemy.name.toUpperCase(), GOLD);
        addLog("════════════════════════════════════════", GOLD_DIM);

        resultOverlay.setVisible(false);
        setActionsEnabled(true);
        animating = false;
    }

    private Image loadSafeImage(String path) {
        try {
            var resource = getClass().getResource(path);
            if (resource != null) {
                return new ImageIcon(resource).getImage();
            }
        } catch (Exception e) {
            System.out.println("Asset missing at: " + path);
        }
        return null; // Return null so paintComponent knows to use a placeholder
    }

    private String getBackgroundPath(int worldLevel) {
        return switch (worldLevel) {
            case 1  -> "/assets/battleBackground/battle_background.png";

            // These will use the same one as placeholders for now
            case 2  -> "/assets/battleBackground/battle_background.png";
            case 3  -> "/assets/battleBackground/battle_background.png";
            default -> "/assets/battleBackground/battle_background.png";
        };
    }

    // ─── UI Construction ──────────────────────────────────────────────────────

    private void buildUI() {
        // ── Top bar: round + title ──
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(BG_DARK);
        topBar.setBorder(new EmptyBorder(0, 0, 8, 0));

        roundLabel = new JLabel("Round 1", SwingConstants.CENTER);
        roundLabel.setFont(FONT_TITLE);
        roundLabel.setForeground(GOLD);

        turnLabel = new JLabel("Your Turn ▶", SwingConstants.CENTER);
        turnLabel.setFont(FONT_STAT);
        turnLabel.setForeground(GREEN);

        topBar.add(roundLabel, BorderLayout.CENTER);
        topBar.add(turnLabel,  BorderLayout.EAST);

        // ── Arena row ──
        JPanel arena = new JPanel(new GridLayout(1, 3, 16, 0));
        arena.setBackground(BG_DARK);

        JPanel heroPanel  = buildCombatantPanel(true);
        JPanel vsCol      = buildVsColumn();
        JPanel enemyPanel = buildCombatantPanel(false);

        arena.add(heroPanel);
        arena.add(vsCol);
        arena.add(enemyPanel);

        // ── Battle log ──
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);
        logArea.setFont(FONT_LOG);
        logArea.setBackground(BG_LOG);
        logArea.setForeground(TEXT_DIM);
        logArea.setBorder(new EmptyBorder(10, 12, 10, 12));

        JScrollPane logScroll = new JScrollPane(logArea);
        logScroll.setPreferredSize(new Dimension(0, 130));
        logScroll.setBorder(BorderFactory.createLineBorder(new Color(50, 45, 75), 1));
        logScroll.setBackground(BG_LOG);
        logScroll.getViewport().setBackground(BG_LOG);
        logScroll.getVerticalScrollBar().setBackground(BG_DARK);

        JLabel logTitle = new JLabel("  ⚔ Battle Chronicle");
        logTitle.setFont(new Font("Monospaced", Font.BOLD, 11));
        logTitle.setForeground(GOLD_DIM);
        logTitle.setBackground(new Color(16, 14, 28));
        logTitle.setOpaque(true);
        logTitle.setBorder(new EmptyBorder(4, 8, 4, 0));

        JPanel logWrap = new JPanel(new BorderLayout());
        logWrap.setBackground(BG_LOG);
        logWrap.add(logTitle,  BorderLayout.NORTH);
        logWrap.add(logScroll, BorderLayout.CENTER);

        // ── Action bar ──
        JPanel actionBar = buildActionBar();


        // ── Layout Assembly ──
        // Use a single BorderLayout for the whole BattlePanel
        setLayout(new BorderLayout(0, 10));

        // Assemble the "Arena" (Middle section)
        JPanel arenaWrapper = new JPanel(new BorderLayout());
        arenaWrapper.setOpaque(false); // Crucial for background!
        arenaWrapper.add(arena, BorderLayout.CENTER);

        // Assemble the "Lower" section (Log + Actions)
        JPanel lower = new JPanel(new BorderLayout(0, 8));
        lower.setOpaque(false); // Crucial for background!
        lower.add(logWrap,   BorderLayout.CENTER);
        lower.add(actionBar, BorderLayout.SOUTH);

        // Add everything to the BattlePanel (this)
        add(topBar,       BorderLayout.NORTH);
        add(arenaWrapper, BorderLayout.CENTER);
        add(lower,        BorderLayout.SOUTH);

        // ── Final Transparency Checks ──
        arena.setOpaque(false);
        heroPanel.setOpaque(false);
        vsCol.setOpaque(false);
        enemyPanel.setOpaque(false);
        topBar.setOpaque(false);

        // Result overlay is handled by GameScreen.showResultOverlay
        resultOverlay = buildResultOverlay();
        resultOverlay.setVisible(false);


    }

    private JPanel buildCombatantPanel(boolean isHero) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BG_PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(2, 0, 0, 0, isHero ? GREEN : RED),
                BorderFactory.createLineBorder(new Color(50, 45, 75), 1)
        ));

        JLabel emoji = new JLabel("", SwingConstants.CENTER);
        emoji.setFont(FONT_EMOJI);
        emoji.setAlignmentX(CENTER_ALIGNMENT);
        emoji.setBorder(new EmptyBorder(16, 0, 8, 0));

        JLabel name = new JLabel("", SwingConstants.CENTER);
        name.setFont(FONT_NAME);
        name.setForeground(isHero ? GREEN : RED);
        name.setAlignmentX(CENTER_ALIGNMENT);

        JLabel role = new JLabel("", SwingConstants.CENTER);
        role.setFont(new Font("Monospaced", Font.ITALIC, 10));
        role.setForeground(TEXT_DIM);
        role.setAlignmentX(CENTER_ALIGNMENT);
        role.setBorder(new EmptyBorder(0, 0, 12, 0));

        // HP bar
        JPanel hpRow = buildBarRow("HP", isHero ? GREEN : RED);
        JProgressBar hpBar = (JProgressBar) ((JPanel) hpRow.getComponent(1)).getComponent(0);
        JLabel hpText = (JLabel) hpRow.getComponent(2);

        // Energy bar (hero only)
        JPanel energyRow = null;
        JProgressBar energyBar = null;
        JLabel energyText = null;
        if (isHero) {
            energyRow = buildBarRow("Energy", new Color(200, 180, 80));
            energyBar = (JProgressBar) ((JPanel) energyRow.getComponent(1)).getComponent(0);
            energyText = (JLabel) energyRow.getComponent(2);
        }

        // Stats mini row
        JPanel statsRow = buildMiniStats(isHero);

        // Status label
        JLabel statusLbl = new JLabel(" ", SwingConstants.CENTER);
        statusLbl.setFont(new Font("Monospaced", Font.BOLD, 10));
        statusLbl.setForeground(BLUE);
        statusLbl.setAlignmentX(CENTER_ALIGNMENT);
        statusLbl.setBorder(new EmptyBorder(4, 0, 8, 0));

        // Store references
        if (isHero) {
            heroEmojiLbl  = emoji;
            heroNameLbl   = name;
            heroRoleLbl   = role;
            heroHpBar     = hpBar;
            heroHpText    = hpText;
            heroEnergyBar = energyBar;
            heroEnergyText= energyText;
            heroStatusLbl = statusLbl;
        } else {
            enemyEmojiLbl = emoji;
            enemyNameLbl  = name;
            enemyRoleLbl  = role;
            enemyHpBar    = hpBar;
            enemyHpText   = hpText;
            enemyStatusLbl= statusLbl;
        }

        panel.add(emoji);
        panel.add(name);
        panel.add(role);

        JPanel barWrap = new JPanel();
        barWrap.setLayout(new BoxLayout(barWrap, BoxLayout.Y_AXIS));
        barWrap.setBackground(BG_PANEL);
        barWrap.setBorder(new EmptyBorder(0, 16, 0, 16));
        barWrap.setAlignmentX(LEFT_ALIGNMENT);
        barWrap.add(hpRow);
        if (isHero && energyRow != null) {
            barWrap.add(Box.createVerticalStrut(4));
            barWrap.add(energyRow);
        }

        panel.add(barWrap);
        panel.add(Box.createVerticalStrut(8));
        panel.add(statsRow);
        panel.add(statusLbl);

        return panel;
    }

    private JPanel buildBarRow(String label, Color barColor) {
        JPanel row = new JPanel(new BorderLayout(6, 0));
        row.setBackground(BG_PANEL);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        row.setAlignmentX(LEFT_ALIGNMENT);

        JLabel lbl = new JLabel(label);
        lbl.setFont(FONT_STAT);
        lbl.setForeground(TEXT_DIM);
        lbl.setPreferredSize(new Dimension(46, 14));

        JProgressBar bar = new JProgressBar(0, 100);
        bar.setValue(100);
        bar.setStringPainted(false);
        bar.setForeground(barColor);
        bar.setBackground(new Color(30, 28, 50));
        bar.setBorder(null);
        bar.setPreferredSize(new Dimension(0, 8));

        JPanel barWrap = new JPanel(new BorderLayout());
        barWrap.setBackground(BG_PANEL);
        barWrap.add(bar, BorderLayout.CENTER);

        JLabel val = new JLabel("—");
        val.setFont(FONT_STAT);
        val.setForeground(TEXT_BRIGHT);
        val.setPreferredSize(new Dimension(64, 14));
        val.setHorizontalAlignment(SwingConstants.RIGHT);

        row.add(lbl,     BorderLayout.WEST);
        row.add(barWrap, BorderLayout.CENTER);
        row.add(val,     BorderLayout.EAST);
        return row;
    }

    private JPanel buildMiniStats(boolean isHero) {
        JPanel p = new JPanel(new GridLayout(1, 2, 8, 0));
        p.setBackground(BG_PANEL);
        p.setBorder(new EmptyBorder(4, 16, 4, 16));
        p.setAlignmentX(LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        // These are decorative placeholders — populated in populateCombatantUI()
        p.add(buildMiniStatBox("ATK", isHero ? "—" : "—", RED));
        p.add(buildMiniStatBox("DEF", isHero ? "—" : "—", BLUE));

        if (isHero) {
            // Store panels for update
            p.setName("heroStats");
        } else {
            p.setName("enemyStats");
        }
        return p;
    }

    private JPanel buildMiniStatBox(String label, String value, Color accent) {
        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBackground(new Color(18, 16, 30));
        box.setBorder(BorderFactory.createLineBorder(new Color(45, 40, 65), 1));

        JLabel val = new JLabel(value, SwingConstants.CENTER);
        val.setFont(new Font("Monospaced", Font.BOLD, 18));
        val.setForeground(GOLD);
        val.setAlignmentX(CENTER_ALIGNMENT);

        JLabel lbl = new JLabel(label, SwingConstants.CENTER);
        lbl.setFont(new Font("Monospaced", Font.PLAIN, 9));
        lbl.setForeground(TEXT_DIM);
        lbl.setAlignmentX(CENTER_ALIGNMENT);

        box.add(Box.createVerticalStrut(4));
        box.add(val);
        box.add(lbl);
        box.add(Box.createVerticalStrut(4));
        return box;
    }

    private JPanel buildVsColumn() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(BG_DARK);
        p.setPreferredSize(new Dimension(80, 0));

        JLabel vs = new JLabel("VS", SwingConstants.CENTER);
        vs.setFont(new Font("Monospaced", Font.BOLD, 28));
        vs.setForeground(GOLD_DIM);
        vs.setAlignmentX(CENTER_ALIGNMENT);

        p.add(Box.createVerticalGlue());
        p.add(vs);
        p.add(Box.createVerticalGlue());
        return p;
    }

    private JPanel buildActionBar() {
        JPanel bar = new JPanel(new GridLayout(1, 3, 12, 0));
        bar.setBackground(BG_DARK);
        bar.setBorder(new EmptyBorder(4, 0, 0, 0));

        attackBtn = buildActionButton("⚔  Attack",  "Deal physical damage",      RED_DARK,   RED);
        defendBtn = buildActionButton("🛡  Defend",  "Halve incoming damage",      new Color(20, 30, 80), BLUE);
        specialBtn= buildActionButton("✨ Special",  "Unleash special ability",    new Color(40, 10, 70), PURPLE);

        specialCdLabel = new JLabel("", SwingConstants.CENTER);
        specialCdLabel.setFont(new Font("Monospaced", Font.PLAIN, 9));
        specialCdLabel.setForeground(TEXT_DIM);

        attackBtn.addActionListener(e -> onPlayerAction(BattleLogic.BattleAction.ATTACK));
        defendBtn.addActionListener(e -> onPlayerAction(BattleLogic.BattleAction.DEFEND));
        specialBtn.addActionListener(e -> onPlayerAction(BattleLogic.BattleAction.SPECIAL));

        bar.add(attackBtn);
        bar.add(defendBtn);
        bar.add(specialBtn);
        return bar;
    }

    private JButton buildActionButton(String text, String tooltip, Color bg, Color border) {
        JButton btn = new JButton("<html><center>" + text + "<br><small style='color:#888'>"
                + tooltip + "</small></center></html>");
        btn.setFont(FONT_BTN);
        btn.setForeground(TEXT_BRIGHT);
        btn.setBackground(bg);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(border, 1),
                new EmptyBorder(12, 8, 12, 8)
        ));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            Color origBg = bg;
            @Override public void mouseEntered(MouseEvent e) {
                if (btn.isEnabled()) btn.setBackground(border.darker());
            }
            @Override public void mouseExited(MouseEvent e) {
                btn.setBackground(origBg);
            }
        });
        return btn;
    }

    private JPanel buildResultOverlay() {
        JPanel overlay = new JPanel(new GridBagLayout());
        overlay.setBackground(OVERLAY_BG);
        overlay.setOpaque(true);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(new Color(20, 18, 36));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GOLD_DIM, 1),
                new EmptyBorder(36, 50, 36, 50)
        ));

        resultIcon  = new JLabel("🏆", SwingConstants.CENTER);
        resultIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64));
        resultIcon.setAlignmentX(CENTER_ALIGNMENT);

        resultTitle = new JLabel("VICTORY", SwingConstants.CENTER);
        resultTitle.setFont(FONT_RESULT);
        resultTitle.setForeground(GOLD);
        resultTitle.setAlignmentX(CENTER_ALIGNMENT);

        resultSub = new JLabel(" ", SwingConstants.CENTER);
        resultSub.setFont(new Font("Monospaced", Font.ITALIC, 13));
        resultSub.setForeground(TEXT_DIM);
        resultSub.setAlignmentX(CENTER_ALIGNMENT);
        resultSub.setBorder(new EmptyBorder(4, 0, 24, 0));

        JButton restartBtn = new JButton("⚔  Fight Again");
        restartBtn.setFont(FONT_BTN);
        restartBtn.setForeground(new Color(20, 15, 5));
        restartBtn.setBackground(new Color(120, 92, 24));
        restartBtn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GOLD, 1),
                new EmptyBorder(10, 24, 10, 24)
        ));
        restartBtn.setFocusPainted(false);
        restartBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        restartBtn.setAlignmentX(CENTER_ALIGNMENT);
        restartBtn.addActionListener(e -> { if (onRestartBattle != null) onRestartBattle.run(); });

        JButton backBtn = new JButton("↩  New Champion");
        backBtn.setFont(FONT_BTN);
        backBtn.setForeground(GOLD);
        backBtn.setBackground(new Color(22, 20, 38));
        backBtn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GOLD_DIM, 1),
                new EmptyBorder(10, 24, 10, 24)
        ));
        backBtn.setFocusPainted(false);
        backBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backBtn.setAlignmentX(CENTER_ALIGNMENT);
        backBtn.addActionListener(e -> { if (onReturnToSelection != null) onReturnToSelection.run(); });

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
        btns.setBackground(new Color(20, 18, 36));
        btns.add(restartBtn);
        btns.add(backBtn);
        btns.setAlignmentX(CENTER_ALIGNMENT);

        card.add(resultIcon);
        card.add(Box.createVerticalStrut(8));
        card.add(resultTitle);
        card.add(resultSub);
        card.add(btns);

        overlay.add(card);
        return overlay;
    }

    // ─── Populate UI from data ────────────────────────────────────────────────

    private void populateCombatantUI() {
        Combatant h = engine.getHero();
        Combatant e = engine.getEnemy();

        heroEmojiLbl.setText(heroDef.emoji);
        heroNameLbl.setText(heroDef.name);
        heroRoleLbl.setText(heroDef.role);

        enemyEmojiLbl.setText(enemyDef.emoji);
        enemyNameLbl.setText(enemyDef.name);
        enemyRoleLbl.setText(enemyDef.role);

        heroHpBar.setMaximum(h.maxHp);
        enemyHpBar.setMaximum(e.maxHp);
        if (heroEnergyBar != null) heroEnergyBar.setMaximum(h.maxEnergy);

        // Special button label
        if (h.special != null) {
            specialBtn.setText("<html><center>" + h.special.icon + " " + h.special.name
                    + "<br><small style='color:#888'>" + h.special.description
                    + "</small></center></html>");
        }
    }

    // ─── Refresh live stats ────────────────────────────────────────────────────

    /**
     * Mirrors Battle.java displayBattleStats() — updates all bars and labels.
     */
    private void refreshBattleUI() {
        Combatant h = engine.getHero();
        Combatant e = engine.getEnemy();

        // HP bars
        heroHpBar.setValue(h.currentHp);
        heroHpText.setText(h.currentHp + "/" + h.maxHp);

        enemyHpBar.setValue(e.currentHp);
        enemyHpText.setText(e.currentHp + "/" + e.maxHp);

        // Energy bar (mirrors player.getEnergy() / player.getMaxEnergy())
        if (heroEnergyBar != null) {
            heroEnergyBar.setValue(h.energy);
            heroEnergyText.setText(h.energy + "/" + h.maxEnergy);
        }

        // Round counter
        roundLabel.setText("Round " + engine.getRound());

        // Turn indicator
        boolean isPlayerTurn = engine.getCurrentTurn() == TurnOwner.PLAYER;
        turnLabel.setText(isPlayerTurn ? "Your Turn ▶" : "Enemy Turn ▶");
        turnLabel.setForeground(isPlayerTurn ? GREEN : RED);

        // Special cooldown
        int cd = h.specialCooldown;
        if (cd > 0) {
            specialBtn.setEnabled(false);
            specialCdLabel.setText("Cooldown: " + cd);
        } else {
            specialBtn.setEnabled(true);
            specialCdLabel.setText("");
        }

        // Status badges
        heroStatusLbl.setText(h.defending  ? "🛡 Defending" : " ");
        enemyStatusLbl.setText(e.defending ? "🛡 Defending" : " ");
    }

    // ─── Action handling ──────────────────────────────────────────────────────

    /**
     * Mirrors Battle.java's player.turn(enemy) call.
     * Runs: player action → refresh → check → enemy turn → refresh → check.
     */
    private void onPlayerAction(BattleLogic.BattleAction action) {
        if (animating) return;
        if (engine.getCurrentTurn() != TurnOwner.PLAYER) return;

        animating = true;
        setActionsEnabled(false);

        // Player acts
        ActionResult playerResult = engine.playerAction(action);
        if (playerResult != null) {
            addLogFromResult(playerResult, true);
        }
        refreshBattleUI();

        // Check win
        BattleOutcome outcome = engine.checkOutcome();
        if (outcome == BattleOutcome.VICTORY) {
            showResult(true);
            animating = false;
            return;
        }

        // Enemy turn after a short delay (mirrors PrintUtil.pause(800) in Battle.java)
        Timer enemyDelay = new Timer(900, e -> {
            engine.advanceToEnemyTurn();
            turnLabel.setText("Enemy Turn ▶");
            turnLabel.setForeground(RED);

            Timer enemyAct = new Timer(500, ev -> {
                ActionResult enemyResult = engine.enemyTurn();
                if (enemyResult != null) {
                    addLogFromResult(enemyResult, false);
                }
                refreshBattleUI();

                BattleOutcome outcome2 = engine.checkOutcome();
                if (outcome2 == BattleOutcome.DEFEAT) {
                    handleDefeat();
                } else {
                    setActionsEnabled(true);
                }
                animating = false;
            });
            enemyAct.setRepeats(false);
            enemyAct.start();
        });
        enemyDelay.setRepeats(false);
        enemyDelay.start();
    }

    // ─── Defeat / Revive ─────────────────────────────────────────────────────

    /**
     * Mirrors Battle.java's defeat flow:
     * 1. Check Phoenix Soulstone
     * 2. Check ReviveTrial (one use)
     * 3. Game Over
     */
    private void handleDefeat() {
        addLog("💀 " + heroDef.name + " has fallen...", RED);

        // Phoenix Soulstone check (mirrors Battle.java lines 110–122)
        if (engine.isPhoenixSoulstoneAvailable()) {
            engine.attemptRevive();
            for (String msg : engine.getBattleLog()) {
                if (msg.contains("Soulstone")) addLog(msg, GREEN);
            }
            refreshBattleUI();
            setActionsEnabled(true);
            return;
        }

        // Revive trial check (mirrors ReviveTrial.run())
        if (!engine.isReviveUsed()) {
            int answer = JOptionPane.showConfirmDialog(
                    this,
                    "💀 You collapse... but a second chance awaits!\n\n" +
                            "⚠  Khai's Java Trial:\n" +
                            "Answer correctly to be revived at 50% HP.\n\n" +
                            "Q: What keyword is used to inherit a class in Java?",
                    "Khai's Revive Trial",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            // Simulate the trial — mirrors ReviveTrial.run(player)
            String userAnswer = JOptionPane.showInputDialog(
                    this, "Type your answer:", "Java Trial", JOptionPane.PLAIN_MESSAGE
            );

            boolean correct = userAnswer != null
                    && userAnswer.trim().equalsIgnoreCase("extends");

            if (correct) {
                engine.confirmRevive();
                addLog("✨ Correct! Knowledge revives you!", GREEN);
                addLog("💚 Restored to 50% HP and Energy.", GREEN);
                refreshBattleUI();
                setActionsEnabled(true);
                return;
            } else {
                addLog("❌ Incorrect. Your journey ends here...", RED);
            }
        } else {
            addLog("💀 No more second chances.", RED);
        }

        // Game Over — mirrors Battle.java gameOver()
        showResult(false);
    }

    // ─── Result screen ────────────────────────────────────────────────────────

    private void showResult(boolean victory) {
        addLog(victory
                        ? "🏆 Victory! " + heroDef.name + " has triumphed!"
                        : "☠  Defeat. " + heroDef.name + "'s story ends in shadow.",
                victory ? GOLD : RED);

        resultIcon.setText(victory ? "🏆" : "💔");
        resultTitle.setText(victory ? "VICTORY" : "DEFEAT");
        resultTitle.setForeground(victory ? GOLD : RED);
        resultSub.setText(victory
                ? heroDef.name + " has vanquished " + enemyDef.name + "!"
                : heroDef.name + " has fallen in battle...");

        // Show the overlay on top of this panel via the parent GameScreen
        if (getParent() instanceof GameScreen gs) {
            gs.showResultOverlay(resultOverlay);
        } else {
            // Fallback: embed overlay directly
            setLayout(new OverlayLayout(this));
            add(resultOverlay);
            resultOverlay.setVisible(true);
            revalidate();
            repaint();
        }
    }

    // ─── Battle log helpers ───────────────────────────────────────────────────

    /**
     * Mirrors Battle.java's colored print calls (ColorUtil.boldBrightGreen, boldBrightRed, etc.)
     * The GUI uses text color instead of ANSI codes.
     */
    private void addLogFromResult(ActionResult r, boolean isPlayer) {
        Color color = isPlayer ? GREEN : RED;
        if (r.isBerserk)  color = new Color(255, 100, 50);
        if (r.wasDefend)  color = BLUE;
        if (r.isSpecial)  color = PURPLE;
        addLog(r.logMessage, color);
        if (r.dotDamageApplied > 0) {
            addLog("🔥 Burn/Poison tick: " + r.dotDamageApplied, new Color(200, 120, 50));
        }
    }

    private void addLog(String text, Color color) {
        // Append styled text to the log area
        // JTextArea doesn't support per-line color natively without StyledDocument
        // We use a simple prefix convention and keep a JTextArea for readability
        logArea.append(text + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    private void clearLog() {
        logArea.setText("");
    }

    private void setActionsEnabled(boolean enabled) {
        attackBtn.setEnabled(enabled);
        defendBtn.setEnabled(enabled);
        if (enabled && engine != null) {
            specialBtn.setEnabled(engine.getHero().specialCooldown == 0);
        } else {
            specialBtn.setEnabled(false);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // 1. Draw the Background Image
        if (backgroundImage != null) {
            // This stretches the PNG to fill the entire 1280x720 panel
            g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            // Fallback: If image fails, draw your dark theme background
            g2d.setColor(BG_DARK);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }

        // 2. Draw Hero Sprite (Placeholders until assets are ready)
        drawEntity(g2d, heroSprite, 150, 220, "HERO", GREEN);

        // 3. Draw Enemy Sprite (Placeholders until assets are ready)
        drawEntity(g2d, enemySprite, 880, 220, "ENEMY", RED);

        // 4. Subtle Overlay (Makes the UI text easier to read over a busy image)
        g2d.setColor(new Color(0, 0, 0, 40));
        g2d.fillRect(0, 0, getWidth(), getHeight());
    }

    /** Helper to draw a sprite or a placeholder box if the sprite is null */
    private void drawEntity(Graphics2D g2d, Image img, int x, int y, String label, Color color) {
        if (img != null) {
            g2d.drawImage(img, x, y, 250, 250, this);
        } else {
            // Transparent box placeholder
            g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 100));
            g2d.fillRect(x, y, 200, 200);
            g2d.setColor(Color.WHITE);
            g2d.setFont(FONT_STAT);
            g2d.drawString(label + " ASSET MISSING", x + 10, y + 100);
        }
    }
}