package GameGUI;

import GameGUI.BattleLogic.*;
import GameGUI.HeroData.HeroDefinition;
import GameGUI.HeroData.EnemyDefinition;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

public class BattlePanel extends JPanel {

    private Runnable onReturnToSelection;
    private Runnable onRestartBattle;

    private BattleLogic engine;
    private HeroDefinition heroDef;
    private EnemyDefinition enemyDef;
    private boolean animating = false;

    // Colors
    private static final Color BG_DARK    = new Color(12, 10, 22);
    private static final Color BG_PANEL   = new Color(22, 20, 38);
    private static final Color GOLD       = new Color(120, 80, 10);   // dark amber — fight headers, loot
    private static final Color GOLD_DIM   = new Color(80, 55, 10);
    private static final Color TEXT_BRIGHT= new Color(240, 232, 208);
    private static final Color TEXT_DIM   = new Color(140, 125, 95);
    private static final Color GREEN      = new Color(0, 110, 45);    // dark green — player actions, victory
    private static final Color GREEN_DARK = new Color(0, 80, 30);
    private static final Color RED        = new Color(160, 20, 20);   // dark red — enemy actions, death
    private static final Color RED_DARK   = new Color(110, 10, 10);
    private static final Color BLUE       = new Color(30, 80, 180);   // dark blue — defend
    private static final Color PURPLE     = new Color(90, 30, 160);   // dark purple — special/ultimate
    private static final Color OVERLAY_BG = new Color(0, 0, 0, 200);

    // Fonts
    private static final Font FONT_TITLE  = new Font("Monospaced", Font.BOLD, 16);
    private static final Font FONT_STAT   = new Font("Monospaced", Font.PLAIN, 11);
    private static final Font FONT_BTN    = new Font("Monospaced", Font.BOLD, 12);
    private static final Font FONT_RESULT = new Font("Monospaced", Font.BOLD, 28);

    // Widgets
    private JLabel heroEmojiLbl, heroNameLbl, heroRoleLbl, heroLvlLbl;
    private JProgressBar heroHpBar, heroEnergyBar;
    private JLabel heroHpText, heroEnergyText, heroStatusLbl;

    private JLabel enemyEmojiLbl, enemyNameLbl, enemyRoleLbl;
    private JProgressBar enemyHpBar;
    private JLabel enemyHpText, enemyStatusLbl;

    private JLabel roundLabel;
    private JLabel turnLabel;  // shown inside dialogue box
    private javax.swing.JTextPane logArea;
    private JButton skill1Btn, skill2Btn, skipTurnBtn, ultimateBtn;
    private JButton battleContinueBtn;
    private JButton attackBtn, defendBtn, specialBtn; // kept for setActionsEnabled compat
    private JLabel specialCdLabel;
    private JPanel resultOverlay;
    private JLabel resultIcon, resultTitle, resultSub;

    public BattlePanel() {
        setLayout(null);
        setPreferredSize(new Dimension(1280, 720));
        setBackground(BG_DARK);
        buildUI();
    }

    public void setOnReturnToSelection(Runnable r) { this.onReturnToSelection = r; }
    public void setOnRestartBattle(Runnable r)     { this.onRestartBattle = r; }
    public void setOnEnemyGroupDefeated(java.util.function.BiConsumer<Integer, Runnable> cb) { this.onEnemyGroupDefeated = cb; }

    // World enemy sequence tracking
    private java.util.List<EnemyDefinition> enemySequence;
    private int enemySequenceIndex = 0;
    private int enemyFightIndex    = 0; // which fight within current enemy type
    private Runnable onSequenceComplete;
    // Called when one enemy GROUP is fully defeated: args = (nextEnemyIndex, resumeFight)
    private java.util.function.BiConsumer<Integer, Runnable> onEnemyGroupDefeated;

    /** Start a world enemy sequence (multiple enemy types, each fought N times). */
    public void startEnemySequence(HeroDefinition hero,
                                   java.util.List<EnemyDefinition> sequence,
                                   Runnable onComplete) {
        this.heroDef = hero;
        this.enemySequence = new java.util.ArrayList<>(sequence);
        this.enemySequenceIndex = 0;
        this.enemyFightIndex    = 0;
        this.onSequenceComplete = onComplete;
        startNextFight();
    }

    // Saved hero combatant — preserves HP/energy between fights of same enemy type
    private BattleLogic.Combatant savedHeroCombatant = null;

    private void startNextFight() {
        if (enemySequenceIndex >= enemySequence.size()) {
            if (onSequenceComplete != null) onSequenceComplete.run();
            return;
        }
        EnemyDefinition eDef = enemySequence.get(enemySequenceIndex);
        this.enemyDef = eDef;
        // Use saved hero if fighting another of the same enemy type, else fresh hero
        Combatant heroToUse = (savedHeroCombatant != null)
                ? savedHeroCombatant : HeroData.buildHero(heroDef);
        savedHeroCombatant = null; // consumed
        Combatant enemyC = HeroData.buildEnemy(eDef);
        this.engine = new BattleLogic(heroToUse, enemyC, false);
        this.engine.setHeroDef(heroDef);
        populateCombatantUI();
        refreshBattleUI();
        clearLog();
        int fightNum = enemyFightIndex + 1;
        addLog(eDef.name.toUpperCase() + " " + fightNum + "/" + eDef.count + " — FIGHT!", GOLD);
        resultOverlay.setVisible(false);
        setActionsEnabled(true);
        animating = false;
        postVictoryStep = PostVictoryStep.NONE;
        setLogFontNormal();
        if (battleContinueBtn != null) battleContinueBtn.setEnabled(false);
        setTurnLabel(true);
    }

    /** Legacy single-battle start */
    public void startBattle(HeroDefinition hero, EnemyDefinition enemy) {
        this.heroDef  = hero;
        this.enemyDef = enemy;
        this.enemySequence = null;
        Combatant heroC  = HeroData.buildHero(hero);
        Combatant enemyC = HeroData.buildEnemy(enemy);
        this.engine = new BattleLogic(heroC, enemyC, false);
        this.engine.setHeroDef(hero);
        populateCombatantUI();
        refreshBattleUI();
        clearLog();
        addLog("BATTLE START! " + hero.name.toUpperCase() + " VS " + enemy.name.toUpperCase(), GOLD);
        resultOverlay.setVisible(false);
        setActionsEnabled(true);
        animating = false;
        if (battleContinueBtn != null) battleContinueBtn.setEnabled(false);
        setTurnLabel(true);
    }

    private void buildUI() {
        // ── LAYER 0: TheBackground.png (full 1280x720, bottommost) ──
        JLabel theBg = new JLabel();
        theBg.setBounds(0, 0, 1280, 720);
        theBg.setOpaque(true);
        theBg.setBackground(BG_DARK);
        java.net.URL theBgUrl = getClass().getResource("/assets/Backgrounds/TheBackground.png");
        if (theBgUrl != null) {
            theBg.setIcon(new ImageIcon(new ImageIcon(theBgUrl).getImage().getScaledInstance(1280, 720, Image.SCALE_SMOOTH)));
        }
        add(theBg);

        // ── LAYER 1: World1BattleBackground.png (scene 1280x520) ──
        JLabel battleBg = new JLabel();
        battleBg.setBounds(0, 0, 1280, 520);
        battleBg.setOpaque(false);
        java.net.URL battleBgUrl = getClass().getResource("/assets/Backgrounds/World1BattleBackground.png");
        if (battleBgUrl != null) {
            battleBg.setIcon(new ImageIcon(new ImageIcon(battleBgUrl).getImage().getScaledInstance(1280, 520, Image.SCALE_SMOOTH)));
        }
        add(battleBg);

        // ── LAYER 2: HUD — hero stat card top-left ──
        JPanel heroCard = buildStatCard(true);
        heroCard.setBounds(10, 8, 340, 90);
        add(heroCard);

        // Round and turn labels are created inside the dialogue box area below

        // ── LAYER 4: HUD — enemy stat card top-right ──
        JPanel enemyCard = buildStatCard(false);
        enemyCard.setBounds(920, 8, 340, 90);
        add(enemyCard);

        // ── LAYER 5: DialogueBox.png — exact same as intro screen ──
        JLabel dialogueBg = new JLabel();
        dialogueBg.setBounds(-40, 453, 1053, 343);
        java.net.URL dbUrl = getClass().getResource("/assets/GUIButtons/DialogueBox.png");
        if (dbUrl != null) {
            dialogueBg.setIcon(new ImageIcon(new ImageIcon(dbUrl).getImage().getScaledInstance(1053, 343, Image.SCALE_SMOOTH)));
        }
        add(dialogueBg);

        // ── LAYER 6a: Turn + Round indicators inside dialogue box ──
        turnLabel = new JLabel("YOUR TURN", SwingConstants.CENTER);
        turnLabel.setBounds(0, 8, 1280, 26);
        turnLabel.setFont(new Font("Monospaced", Font.BOLD, 16));
        turnLabel.setForeground(GREEN);
        turnLabel.setOpaque(false);
        add(turnLabel);

        roundLabel = new JLabel("Round 1", SwingConstants.CENTER);
        roundLabel.setBounds(0, 36, 1280, 22);
        roundLabel.setFont(new Font("Monospaced", Font.BOLD, 13));
        roundLabel.setForeground(GOLD);
        roundLabel.setOpaque(false);
        add(roundLabel);

        // ── LAYER 6: Log text (JTextPane for colored text) ──
        logArea = new javax.swing.JTextPane();
        logArea.setBounds(160, 575, 780, 100);
        logArea.setEditable(false);
        logArea.setOpaque(false);
        logArea.setBackground(new Color(0, 0, 0, 0));
        try {
            java.io.InputStream fs = getClass().getResourceAsStream("/assets/AssetFont/Pixelari.ttf");
            if (fs != null) logArea.setFont(Font.createFont(Font.TRUETYPE_FONT, fs).deriveFont(Font.BOLD, 19f));
            else logArea.setFont(new Font("Dialog", Font.BOLD, 16));
        } catch (Exception ex) { logArea.setFont(new Font("Dialog", Font.BOLD, 16)); }
        logArea.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        add(logArea);

        // ── LAYER 7: Buttons — exact same positions as intro screen ──
        battleContinueBtn = makeBtn("/assets/GUIButtons/Continue.png", "/assets/GUIButtons/ContinueHover.png", 964, 554, 154, 64, "Continue", 10);
        battleContinueBtn.addActionListener(e -> onContinuePressed());
        JButton menuBtn     = makeBtn("/assets/GUIButtons/Menu.png",     "/assets/GUIButtons/MenuHover.png",     1103, 555, 148, 58, "Menu", 0);
        JButton backBtn     = makeBtn("/assets/GUIButtons/Back.png",     "/assets/GUIButtons/BackHover.png",     970, 613, 140, 50, "Back", 20);
        JButton exitBtn     = makeBtn("/assets/GUIButtons/Exit.png",     "/assets/GUIButtons/ExitHover.png",     1108, 613, 140, 50, "Exit", 19);
        exitBtn.addActionListener(e -> System.exit(0));
        add(battleContinueBtn);
        add(menuBtn);
        add(backBtn);
        add(exitBtn);

        // ── Action buttons: centered above the dialogue box ──
        // DialogueBox starts at y=453. Buttons sit just above it, centered.
        // 4 buttons, each ~140x48, spaced 12px apart, total width = 4*140 + 3*12 = 596
        // Center: x = (1280 - 596) / 2 = 342
        int btnY  = 478;
        int btnW  = 140;
        int btnH  = 48;
        int btnGap = 12;
        int startX = (1280 - (4 * btnW + 3 * btnGap)) / 2;

        skill1Btn   = makeSkillBtn("Skill 1",            new Color(60, 30, 90),  new Color(130, 60, 200),
                startX,                       btnY, btnW, btnH);
        skill2Btn   = makeSkillBtn("Eternal Cross",      new Color(30, 60, 90),  new Color(52, 120, 219),
                startX + (btnW + btnGap),     btnY, btnW, btnH);
        ultimateBtn = makeSkillBtn("Eternal Cross Slash", new Color(90, 30, 30),  new Color(192, 57, 43),
                startX + 2*(btnW+btnGap),     btnY, btnW, btnH);
        skipTurnBtn = makeSkillBtn("Skip Turn",           new Color(30, 60, 40),  new Color(39, 174, 96),
                startX + 3*(btnW+btnGap),     btnY, btnW, btnH);

        skill1Btn.addActionListener(e   -> onPlayerAction(BattleLogic.BattleAction.SKILL1));
        skill2Btn.addActionListener(e   -> onPlayerAction(BattleLogic.BattleAction.SKILL2));
        skipTurnBtn.addActionListener(e -> onPlayerAction(BattleLogic.BattleAction.SKIP_TURN));
        ultimateBtn.addActionListener(e -> onPlayerAction(BattleLogic.BattleAction.ULTIMATE));

        add(skill1Btn);
        add(skill2Btn);
        add(skipTurnBtn);
        add(ultimateBtn);

        // Compat aliases
        attackBtn  = skill1Btn;
        defendBtn  = skipTurnBtn;
        specialBtn = ultimateBtn;
        specialCdLabel = new JLabel("");
        specialCdLabel.setBounds(0, 60, 1280, 20);
        specialCdLabel.setFont(new Font("Monospaced", Font.BOLD, 12));
        specialCdLabel.setForeground(new Color(200, 100, 50));
        specialCdLabel.setHorizontalAlignment(SwingConstants.CENTER);
        specialCdLabel.setOpaque(false);
        add(specialCdLabel);

        // ── LAYER 8: Result overlay (topmost) ──
        resultOverlay = buildResultOverlay();
        resultOverlay.setBounds(0, 0, 1280, 720);
        resultOverlay.setVisible(false);
        add(resultOverlay);

        // Z-order: index 0 = front, last added = furthest back by default
        // Re-order so backgrounds are behind everything
        // Z-order: 0=front, higher=behind
        setComponentZOrder(resultOverlay,     0);
        setComponentZOrder(specialCdLabel,    1);
        setComponentZOrder(roundLabel,        2);
        setComponentZOrder(turnLabel,         3);
        setComponentZOrder(logArea,           3);  // in front of dialogueBg
        setComponentZOrder(battleContinueBtn, 4);
        setComponentZOrder(menuBtn,           5);
        setComponentZOrder(backBtn,           6);
        setComponentZOrder(exitBtn,           7);
        setComponentZOrder(skill1Btn,         8);
        setComponentZOrder(skill2Btn,         9);
        setComponentZOrder(skipTurnBtn,       10);
        setComponentZOrder(ultimateBtn,       11);
        setComponentZOrder(dialogueBg,        13); // behind log text
        setComponentZOrder(heroCard,          14);
        setComponentZOrder(enemyCard,         15);
        setComponentZOrder(battleBg,          16);
        setComponentZOrder(theBg,             17);
    }

    private JButton makeBtn(String normalPath, String hoverPath,
                            int x, int y, int w, int h, String fallback, int hoverOffset) {
        JButton btn = new JButton();
        btn.setBounds(x, y, w, h);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        java.net.URL nUrl = getClass().getResource(normalPath);
        java.net.URL hUrl = getClass().getResource(hoverPath);
        if (nUrl != null) {
            ImageIcon ni = new ImageIcon(new ImageIcon(nUrl).getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH));
            ImageIcon hi = hUrl != null ? new ImageIcon(new ImageIcon(hUrl).getImage().getScaledInstance(w + hoverOffset, h + hoverOffset, Image.SCALE_SMOOTH)) : ni;
            btn.setIcon(ni);
            btn.addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { btn.setIcon(hi); }
                @Override public void mouseExited (MouseEvent e) { btn.setIcon(ni); }
            });
        } else {
            btn.setText(fallback);
            btn.setForeground(Color.WHITE);
            btn.setContentAreaFilled(true);
            btn.setBackground(BG_PANEL);
        }
        return btn;
    }

    private JPanel buildStatCard(boolean isHero) {
        final Color cardBg = new Color(10, 9, 20, 220);
        final Color cardBorder = isHero ? GREEN_DARK : RED_DARK;
        JPanel card = new JPanel(null) {
            @Override public boolean isOptimizedDrawingEnabled() { return false; }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(cardBg);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(cardBorder);
                g2.drawRect(0, 0, getWidth()-1, getHeight()-1);
                g2.dispose();
            }
            @Override protected void paintChildren(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setClip(0, 0, getWidth(), getHeight());
                super.paintChildren(g2);
                g2.dispose();
            }
        };
        card.setOpaque(false);

        JLabel emoji = new JLabel("", SwingConstants.CENTER);
        emoji.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        emoji.setBounds(4, 4, 40, 40);
        card.add(emoji);

        JLabel name = new JLabel("");
        name.setFont(new Font("Monospaced", Font.BOLD, 11));
        name.setForeground(isHero ? GREEN : RED);
        name.setBounds(50, 4, 215, 16);
        card.add(name);

        JLabel lvlLbl = new JLabel("Lv.1");
        lvlLbl.setFont(new Font("Monospaced", Font.BOLD, 10));
        lvlLbl.setForeground(GOLD);
        lvlLbl.setBounds(270, 4, 60, 16);
        card.add(lvlLbl);

        JLabel role = new JLabel("");
        role.setFont(new Font("Monospaced", Font.ITALIC, 9));
        role.setForeground(TEXT_DIM);
        role.setBounds(50, 20, 215, 14);
        card.add(role);

        JLabel hpLbl = new JLabel("HP");
        hpLbl.setFont(FONT_STAT); hpLbl.setForeground(TEXT_DIM);
        hpLbl.setBounds(4, 38, 40, 14);
        card.add(hpLbl);

        JProgressBar hpBar = new JProgressBar(0, 100);
        hpBar.setValue(100);
        hpBar.setForeground(isHero ? GREEN : RED);
        hpBar.setBackground(new Color(30, 28, 50));
        hpBar.setBorder(null);
        hpBar.setBounds(50, 38, 175, 8);
        card.add(hpBar);

        JLabel hpText = new JLabel("—");
        hpText.setFont(FONT_STAT); hpText.setForeground(TEXT_BRIGHT);
        hpText.setBounds(228, 34, 62, 16);
        card.add(hpText);

        JLabel statusLbl = new JLabel(" ");
        statusLbl.setFont(new Font("Monospaced", Font.BOLD, 9));
        statusLbl.setForeground(BLUE);
        statusLbl.setBounds(50, 72, 215, 14);
        card.add(statusLbl);

        if (isHero) {
            heroEmojiLbl = emoji; heroNameLbl = name; heroRoleLbl = role; heroLvlLbl = lvlLbl;
            heroHpBar = hpBar; heroHpText = hpText; heroStatusLbl = statusLbl;

            JLabel epLbl = new JLabel("EP");
            epLbl.setFont(FONT_STAT); epLbl.setForeground(TEXT_DIM);
            epLbl.setBounds(4, 54, 40, 14);
            card.add(epLbl);

            JProgressBar energyBar = new JProgressBar(0, 100);
            energyBar.setValue(100);
            energyBar.setForeground(new Color(200, 180, 80));
            energyBar.setBackground(new Color(30, 28, 50));
            energyBar.setBorder(null);
            energyBar.setBounds(50, 54, 175, 6);
            card.add(energyBar);

            JLabel energyText = new JLabel("—");
            energyText.setFont(FONT_STAT); energyText.setForeground(TEXT_BRIGHT);
            energyText.setBounds(228, 50, 62, 14);
            card.add(energyText);

            heroEnergyBar = energyBar; heroEnergyText = energyText;
        } else {
            enemyEmojiLbl = emoji; enemyNameLbl = name; enemyRoleLbl = role;
            enemyHpBar = hpBar; enemyHpText = hpText; enemyStatusLbl = statusLbl;
        }

        return card;
    }

    private void populateCombatantUI() {
        Combatant h = engine.getHero();
        Combatant e = engine.getEnemy();
        heroEmojiLbl.setText(heroDef.emoji);
        heroNameLbl.setText(heroDef.name);
        heroRoleLbl.setText(heroDef.role);
        if (heroLvlLbl != null) heroLvlLbl.setText("Lv." + h.level);
        enemyEmojiLbl.setText(enemyDef.emoji);
        enemyNameLbl.setText(enemyDef.name);
        enemyRoleLbl.setText(enemyDef.role);
        heroHpBar.setMaximum(h.maxHp);
        enemyHpBar.setMaximum(e.maxHp);
        if (heroEnergyBar != null) heroEnergyBar.setMaximum(h.maxEnergy);
        // Set skill button labels from hero definition
        if (heroDef.skills != null && heroDef.skills.length >= 3) {
            skill1Btn.setText(heroDef.skills[0].icon + " " + heroDef.skills[0].name +
                    " (" + engine.getSkill1Cost() + ")");
            skill2Btn.setText(heroDef.skills[1].icon + " " + heroDef.skills[1].name +
                    " (" + engine.getSkill2Cost() + ")");
            ultimateBtn.setText(heroDef.skills[2].icon + " " + heroDef.skills[2].name +
                    " (" + engine.getUltimateCost() + ")");
        }
        // Energy label based on class
        String energyLabel = switch (heroDef.role) {
            case "Swordsman" -> "ST";
            case "Archer"    -> "AR";
            case "Mage"      -> "MP";
            default          -> "EP";
        };
        // Find EP label and update it
        updateEnergyLabel(energyLabel);
    }

    private void updateEnergyLabel(String label) {
        // EP label is added inside heroCard — find via name
        // (already set as "EP" text — we just update skill buttons, EP label stays)
    }

    private void refreshBattleUI() {
        Combatant h = engine.getHero();
        Combatant e = engine.getEnemy();
        heroHpBar.setValue(h.currentHp);
        heroHpText.setText(h.currentHp + "/" + h.maxHp);
        if (heroLvlLbl != null) heroLvlLbl.setText("Lv." + h.level);
        enemyHpBar.setValue(e.currentHp);
        enemyHpText.setText(e.currentHp + "/" + e.maxHp);
        if (heroEnergyBar != null) {
            heroEnergyBar.setValue(h.energy);
            heroEnergyText.setText(h.energy + "/" + h.maxEnergy);
        }
        roundLabel.setText("Round " + engine.getRound());
        boolean isPlayer = engine.getCurrentTurn() == TurnOwner.PLAYER;
        // turnLabel is updated by setTurnLabel() — don't override it here
        int cd = h.specialCooldown;
        ultimateBtn.setEnabled(cd == 0 && (engine == null || engine.canUseUltimate()));
        specialCdLabel.setText(cd > 0 ? "CD: " + cd : "");
        // Also refresh skill buttons based on energy
        skill1Btn.setEnabled(engine == null || engine.canUseSkill1());
        skill2Btn.setEnabled(engine == null || engine.canUseSkill2());
        heroStatusLbl.setText(h.defending  ? "Defending" : " ");
        enemyStatusLbl.setText(e.defending ? "Defending" : " ");
    }

    private void onPlayerAction(BattleLogic.BattleAction action) {
        if (animating) return;
        if (engine.getCurrentTurn() != TurnOwner.PLAYER) return;
        animating = true;
        setActionsEnabled(false);

        // Hero turn
        clearLog();
        setTurnLabel(true);
        ActionResult pr = engine.playerAction(action);
        if (pr != null) addLogFromResult(pr, true);
        refreshBattleUI();
        if (engine.checkOutcome() == BattleOutcome.VICTORY) {
            handleVictory(); animating = false; return;
        }

        Timer t1 = new Timer(900, e -> {
            engine.advanceToEnemyTurn();
            setTurnLabel(false);

            Timer t2 = new Timer(500, ev -> {
                clearLog(); // clear hero log just before enemy acts
                ActionResult er = engine.enemyTurn();
                if (er != null) addLogFromResult(er, false);
                refreshBattleUI();
                if (engine.checkOutcome() == BattleOutcome.DEFEAT) handleDefeat();
                else {
                    // Stay on enemy log briefly, then switch to player turn
                    Timer t3 = new Timer(800, ev2 -> {
                        clearLog();
                        setTurnLabel(true);
                        setActionsEnabled(true);
                        animating = false;
                    });
                    t3.setRepeats(false); t3.start();
                }
            });
            t2.setRepeats(false); t2.start();
        });
        t1.setRepeats(false); t1.start();
    }

    private void setTurnLabel(boolean isPlayer) {
        if (turnLabel == null) return;
        turnLabel.setText(isPlayer ? "YOUR TURN" : "ENEMY TURN");
        turnLabel.setForeground(isPlayer ? GREEN : RED);
    }

    private void handleDefeat() {
        addLog("You have fallen...", RED);
        if (engine.isPhoenixSoulstoneAvailable()) {
            engine.attemptRevive();
            clearLog();
            addLog("Phoenix Soulstone activated! Revived!", GREEN);
            refreshBattleUI();
            setTurnLabel(true);
            setActionsEnabled(true);
            animating = false;
            return;
        }
        if (!engine.isReviveUsed()) {
            JOptionPane.showInputDialog(this, "Q: What keyword is used to inherit a class in Java?");
            String ans = JOptionPane.showInputDialog(this, "Answer:");
            if (ans != null && ans.trim().equalsIgnoreCase("extends")) {
                engine.confirmRevive();
                clearLog();
                addLog("Correct! Revived at 50% HP!", GREEN);
                refreshBattleUI();
                setTurnLabel(true);
                setActionsEnabled(true);
                animating = false;
                return;
            }
        }
        showResult(false);
    }

    // ─── Post-victory sequence ────────────────────────────────────────────────
    // Driven entirely by the Continue button after a group is cleared.
    // Steps: LOOT → OBJECTIVE → VICTORY_FLAVOUR → LOOT_FLAVOUR → inter-dialogue
    private enum PostVictoryStep { NONE, LOOT, LEVEL_UP_ANNOUNCE, LEVEL_UP_STATS, LEVEL_UP_RESTORE, OBJECTIVE, VICTORY_FLAVOUR, LOOT_FLAVOUR }
    private PostVictoryStep postVictoryStep = PostVictoryStep.NONE;
    // Level-up data stored across the 3 sub-steps
    private int lvlUp_level, lvlUp_hpGain, lvlUp_newHp, lvlUp_atkGain, lvlUp_newAtk, lvlUp_defGain, lvlUp_newDef;
    private EnemyDefinition postVictoryEnemy = null;  // enemy group that was just cleared
    private Runnable postVictoryNext = null;           // called when sequence finishes

    // Cached fonts for normal vs small text
    private Font normalLogFont  = null;
    private Font smallLogFont   = null;

    private void initLogFonts() {
        if (normalLogFont != null) return;
        try {
            java.io.InputStream fs = getClass().getResourceAsStream("/assets/AssetFont/Pixelari.ttf");
            if (fs != null) {
                Font base = Font.createFont(Font.TRUETYPE_FONT, fs);
                normalLogFont = base.deriveFont(Font.BOLD, 19f);
                smallLogFont  = base.deriveFont(Font.BOLD, 14f);
            }
        } catch (Exception ex) { /* fallback below */ }
        if (normalLogFont == null) normalLogFont = new Font("Dialog", Font.BOLD, 16);
        if (smallLogFont  == null) smallLogFont  = new Font("Dialog", Font.BOLD, 12);
    }

    private void setLogFontSmall()  { initLogFonts(); logArea.setFont(smallLogFont); }
    private void setLogFontNormal() { initLogFonts(); logArea.setFont(normalLogFont); }
    private void setLogFontSize(int size) {
        logArea.setFont(logArea.getFont().deriveFont((float)size));
    }

    /** Called when Continue is pressed — advances the post-victory sequence. */
    private void onContinuePressed() {
        if (postVictoryStep == PostVictoryStep.NONE) return;
        battleContinueBtn.setEnabled(false);

        switch (postVictoryStep) {
            case LOOT -> {
                // LOOT screen dismissed — if leveled show announce, else objective
                clearLog();
                if (lvlUp_level > 0) {
                    postVictoryStep = PostVictoryStep.LEVEL_UP_ANNOUNCE;
                    setLogFontSmall();
                    addLog("✨ LEVEL UP! You are now Level " + lvlUp_level + "! ✨", new Color(130, 85, 0));
                } else {
                    postVictoryStep = PostVictoryStep.OBJECTIVE;
                    setLogFontNormal();
                    addLog(getObjectiveCompleteText(postVictoryEnemy), GOLD);
                }
                battleContinueBtn.setEnabled(true);
            }
            case LEVEL_UP_ANNOUNCE -> {
                // Announce dismissed — show stat gains
                postVictoryStep = PostVictoryStep.LEVEL_UP_STATS;
                clearLog();
                addLog("💚 Max HP  : +" + lvlUp_hpGain  + "  →  " + lvlUp_newHp,  new Color(0, 120, 50));
                addLog("⚔  Max ATK : +" + lvlUp_atkGain + "  →  " + lvlUp_newAtk, new Color(140, 70, 0));
                addLog("🛡  DEF     : +" + lvlUp_defGain + "  →  " + lvlUp_newDef, new Color(20, 70, 160));
                battleContinueBtn.setEnabled(true);
            }
            case LEVEL_UP_STATS -> {
                // Stats dismissed — show restore message
                postVictoryStep = PostVictoryStep.LEVEL_UP_RESTORE;
                clearLog();
                addLog("50% of HP & Stamina Restored!", new Color(0, 100, 40));
                battleContinueBtn.setEnabled(true);
            }
            case LEVEL_UP_RESTORE -> {
                // Restore dismissed — back to normal font, show objective
                postVictoryStep = PostVictoryStep.OBJECTIVE;
                setLogFontNormal();
                clearLog();
                addLog(getObjectiveCompleteText(postVictoryEnemy), GOLD);
                battleContinueBtn.setEnabled(true);
            }
            case OBJECTIVE -> {
                // After objective: show victory flavour
                postVictoryStep = PostVictoryStep.VICTORY_FLAVOUR;
                clearLog();
                addLog(getVictoryFlavourText(postVictoryEnemy), GREEN);
                battleContinueBtn.setEnabled(true);
            }
            case VICTORY_FLAVOUR -> {
                // After victory flavour: show loot flavour
                postVictoryStep = PostVictoryStep.LOOT_FLAVOUR;
                clearLog();
                addLog(getLootFlavourText(postVictoryEnemy), new Color(100, 65, 10));
                battleContinueBtn.setEnabled(true);
            }
            case LOOT_FLAVOUR -> {
                // Sequence done — proceed to inter-dialogue or next group
                postVictoryStep = PostVictoryStep.NONE;
                postVictoryEnemy = null;
                clearLog();
                setLogFontNormal();
                if (postVictoryNext != null) {
                    Runnable next = postVictoryNext;
                    postVictoryNext = null;
                    next.run();
                }
            }
        }
    }

    /** Start the post-group-clear sequence: shrink font, show loot/XP, then hand off to Continue. */
    private void startPostVictorySequence(EnemyDefinition eDef, Runnable onDone) {
        postVictoryEnemy = eDef;
        postVictoryNext  = onDone;
        postVictoryStep  = PostVictoryStep.LOOT;
        lvlUp_level = 0; // reset — will be set if hero levels up

        // Apply XP now, before showing loot screen, and parse any level-up data
        if (engine != null) {
            int xp = eDef.xpReward * eDef.count;
            engine.getHero().gainExp(xp);
            if (engine.getHero().lastLevelUpMsg != null) {
                parseLevelUpMsg(engine.getHero()); // populates lvlUp_* fields, clears msg
            }
        }

        setLogFontSmall();
        clearLog();
        addLootText(eDef);
        battleContinueBtn.setEnabled(true);
        turnLabel.setText("");
        roundLabel.setText("");
        specialCdLabel.setText("");
    }

    /** Build the compact loot/XP block shown at small font size (no XP application here). */
    private void addLootText(EnemyDefinition eDef) {
        addLog("You received:", GOLD);
        int shards = eDef.name.equals("The Hollow Stag") ? 10 : 1;
        addLog("  " + shards + " Soul Shard" + (shards > 1 ? "s" : ""), new Color(20, 80, 160));
        int xp = eDef.xpReward * eDef.count;
        addLog("  +" + xp + " XP", new Color(140, 90, 0));
    }

    /** Show level-up details in small font. Parses the LVL_UP| structured message. */
    /** Parse level-up msg into panel fields. Clears lastLevelUpMsg. */
    private void parseLevelUpMsg(BattleLogic.Combatant hero) {
        if (hero.lastLevelUpMsg == null) return;
        String[] p = hero.lastLevelUpMsg.split("\\|");
        hero.lastLevelUpMsg = null;
        if (p.length < 8) return;
        lvlUp_level   = Integer.parseInt(p[1]);
        lvlUp_hpGain  = Integer.parseInt(p[2]);
        lvlUp_newHp   = Integer.parseInt(p[3]);
        lvlUp_atkGain = Integer.parseInt(p[4]);
        lvlUp_newAtk  = Integer.parseInt(p[5]);
        lvlUp_defGain = Integer.parseInt(p[6]);
        lvlUp_newDef  = Integer.parseInt(p[7]);
    }

    private String getObjectiveCompleteText(EnemyDefinition eDef) {
        return switch (eDef.name) {
            case "Rotfang Wolf"     -> "OBJECTIVE: DEFEAT 3 ROTFANG WOLVES!  (3/3)";
            case "Shade Sprite"     -> "OBJECTIVE: DEFEAT 2 SHADE SPRITES!  (2/2)";
            case "Dreadbark Treant" -> "OBJECTIVE: DEFEAT 2 DREADBARK TREANTS!  (2/2)";
            case "Carrion Bat"      -> "OBJECTIVE: DEFEAT 4 CARRION BATS!  (4/4)";
            case "The Hollow Stag"  -> "OBJECTIVE: DEFEAT THE HOLLOW STAG!  COMPLETE";
            default                 -> "OBJECTIVE: " + eDef.name.toUpperCase() + " CLEARED!";
        };
    }

    private String getVictoryFlavourText(EnemyDefinition eDef) {
        return switch (eDef.name) {
            case "Rotfang Wolf"     ->
                    "Victory! The last of the Rotfang Wolves collapses.\n" +
                            "The adrenaline in your veins cools, but the forest feels no safer.";
            case "Shade Sprite"     ->
                    "With a final shriek, the sprites disperse like fog in the wind.\n" +
                            "The mist recedes. The whispering in your mind finally stops.";
            case "Dreadbark Treant" ->
                    "The massive Treants freeze and collapse.\n" +
                            "Where they fall, small green sprouts rise from the ash.";
            case "Carrion Bat"      ->
                    "The last bat crashes into the ground.\n" +
                            "The forest grows quiet. The stench of decay lifts into the cold wind.";
            case "The Hollow Stag"  ->
                    "MINI-BOSS DEFEATED!\n" +
                            "The Stag staggers. The white fire in its antlers flickers and dies.\n" +
                            "It dissolves into particles of pure light.";
            default -> "You have defeated " + eDef.name + "!";
        };
    }

    private String getLootFlavourText(EnemyDefinition eDef) {
        return switch (eDef.name) {
            case "Rotfang Wolf"     -> "You bandage your wounds and collect what little the wolves carried.";
            case "Shade Sprite"     -> "You feel your strength returning after overcoming the darkness.";
            case "Dreadbark Treant" -> "You emerge covered in dust, but victorious. Treasures fall from the decaying wood.";
            case "Carrion Bat"      -> "You catch your breath. You feel stronger... and richer.";
            case "The Hollow Stag"  -> "You reach out and grasp the light. It pulses with quiet power.";
            default                 -> "You collect your rewards.";
        };
    }

    private void handleVictory() {
        if (enemySequence != null) {
            EnemyDefinition eDef = enemySequence.get(enemySequenceIndex);
            enemyFightIndex++;
            clearLog();
            if (enemyFightIndex < eDef.count) {
                // More of same enemy type — show per-kill msg for 2s, then approach msg, then fight
                savedHeroCombatant = engine.getHero();
                addLog(getPerKillMessage(eDef, enemyFightIndex), GREEN);
                delay(2000, () -> {
                    clearLog();
                    addLog(getNextApproachMessage(eDef, enemyFightIndex), GOLD);
                    delay(1200, () -> startNextFight());
                });
            } else {
                // Entire group cleared — save leveled hero so it carries to next group
                savedHeroCombatant = engine.getHero();
                enemySequenceIndex++;
                enemyFightIndex = 0;
                setActionsEnabled(false);

                // Determine what happens after the sequence finishes
                Runnable afterSequence;
                if (enemySequenceIndex < enemySequence.size()) {
                    if (onEnemyGroupDefeated != null) {
                        int idx = enemySequenceIndex; // capture before lambda
                        afterSequence = () -> onEnemyGroupDefeated.accept(idx, () -> startNextFight());
                    } else {
                        afterSequence = () -> delay(500, this::startNextFight);
                    }
                } else {
                    afterSequence = () -> showResult(true);
                }

                startPostVictorySequence(eDef, afterSequence);
            }
        } else {
            showResult(true);
        }
    }

    /** Per-kill defeat message shown after each individual enemy dies within a group. */
    private String getPerKillMessage(EnemyDefinition eDef, int killed) {
        String n = eDef.name;
        String score = killed + "/" + eDef.count;
        return switch (n) {
            case "Rotfang Wolf"      -> "The wolf whimpers and dissolves into black smoke. (" + score + ")";
            case "Shade Sprite"      -> "You dispelled the Shade Sprite! (" + score + ")";
            case "Dreadbark Treant"  -> "You felled the Dreadbark Treant! (" + score + ")";
            case "Carrion Bat"       -> "You slayed the Carrion Bat! (" + score + ")";
            case "The Hollow Stag"   -> "The Hollow Stag has fallen! (" + score + ")";
            default                  -> n + " defeated! (" + score + ")";
        };
    }

    /** Message shown between kills of the same enemy type — next one approaching. */
    private String getNextApproachMessage(EnemyDefinition eDef, int killed) {
        String n = eDef.name;
        int next = killed + 1;
        return switch (n) {
            case "Rotfang Wolf"     -> "Another wolf snarls and steps forward! (" + next + "/" + eDef.count + ")";
            case "Shade Sprite"     -> "The mist swirls — another soul screams into existence! (" + next + "/" + eDef.count + ")";
            case "Dreadbark Treant" -> "The ground quakes again! The second ancient giant lumbers forward! (" + next + "/" + eDef.count + ")";
            case "Carrion Bat"      -> "Another screech echoes above — the swarm continues! (" + next + "/" + eDef.count + ")";
            default                 -> "Another " + n + " approaches! (" + next + "/" + eDef.count + ")";
        };
    }

    /** Message shown after the entire group of that enemy type is cleared. */
    private String getGroupClearMessage(EnemyDefinition eDef) {
        return switch (eDef.name) {
            case "Rotfang Wolf"     -> "Victory! The last Rotfang Wolf collapses. The forest feels no safer.";
            case "Shade Sprite"     -> "The sprites disperse like fog in the wind. The whispering stops.";
            case "Dreadbark Treant" -> "The Treants collapse. Where they fall, green sprouts rise from ash.";
            case "Carrion Bat"      -> "The last bat crashes down. The stench of decay lifts into the cold wind.";
            case "The Hollow Stag"  -> "MINI-BOSS DEFEATED! The Stag dissolves into particles of pure light.";
            default                 -> eDef.name + " group cleared!";
        };
    }

    private void showResult(boolean victory) {
        resultIcon.setText(victory ? "WIN" : "LOSE");
        resultTitle.setText(victory ? "VICTORY" : "DEFEAT");
        resultTitle.setForeground(victory ? GOLD : RED);
        resultSub.setText(victory ? heroDef.name + " wins!" : heroDef.name + " has fallen...");
        resultOverlay.setBounds(0, 0, getWidth(), getHeight());
        setComponentZOrder(resultOverlay, 0);
        resultOverlay.setVisible(true);
        revalidate();
        repaint();
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
                new EmptyBorder(36, 50, 36, 50)));

        resultIcon  = new JLabel("WIN", SwingConstants.CENTER);
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

        JButton restartBtn = new JButton("Fight Again");
        restartBtn.setFont(FONT_BTN); restartBtn.setForeground(new Color(20,15,5));
        restartBtn.setBackground(new Color(120,92,24));
        restartBtn.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(GOLD,1),new EmptyBorder(10,24,10,24)));
        restartBtn.setFocusPainted(false);
        restartBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        restartBtn.setAlignmentX(CENTER_ALIGNMENT);
        restartBtn.addActionListener(e -> { if (onRestartBattle != null) onRestartBattle.run(); });

        JButton backBtn2 = new JButton("New Champion");
        backBtn2.setFont(FONT_BTN); backBtn2.setForeground(GOLD);
        backBtn2.setBackground(new Color(22,20,38));
        backBtn2.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(GOLD_DIM,1),new EmptyBorder(10,24,10,24)));
        backBtn2.setFocusPainted(false);
        backBtn2.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backBtn2.setAlignmentX(CENTER_ALIGNMENT);
        backBtn2.addActionListener(e -> { if (onReturnToSelection != null) onReturnToSelection.run(); });

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
        btns.setBackground(new Color(20,18,36));
        btns.add(restartBtn); btns.add(backBtn2);
        btns.setAlignmentX(CENTER_ALIGNMENT);

        card.add(resultIcon); card.add(Box.createVerticalStrut(8));
        card.add(resultTitle); card.add(resultSub); card.add(btns);
        overlay.add(card);
        return overlay;
    }

    private void addLogFromResult(ActionResult r, boolean isPlayer) {
        // Player dialogue: dark green. Enemy dialogue: dark red. Override for special states.
        Color c = isPlayer ? new Color(0, 110, 45) : new Color(160, 20, 20);
        if (r.isBerserk) c = new Color(160, 50, 10);
        if (r.wasDefend) c = BLUE;
        if (r.isSpecial) c = PURPLE;
        addLog(r.logMessage, c);
        if (r.dotDamageApplied > 0) addLog("Burn tick: " + r.dotDamageApplied, new Color(150, 60, 0));
    }

    private void addLog(String text, Color color) {
        javax.swing.text.StyledDocument doc = logArea.getStyledDocument();
        javax.swing.text.SimpleAttributeSet attrs = new javax.swing.text.SimpleAttributeSet();
        javax.swing.text.StyleConstants.setForeground(attrs, color);
        javax.swing.text.StyleConstants.setFontFamily(attrs, logArea.getFont().getFamily());
        javax.swing.text.StyleConstants.setFontSize(attrs, logArea.getFont().getSize());
        javax.swing.text.StyleConstants.setBold(attrs, true);
        try {
            doc.insertString(doc.getLength(), text + "\n", attrs);
        } catch (javax.swing.text.BadLocationException e) { /* ignore */ }
        logArea.setCaretPosition(doc.getLength());
    }

    private void clearLog() {
        logArea.setText("");
    }

    private void setActionsEnabled(boolean enabled) {
        skill1Btn.setEnabled(enabled && (engine == null || engine.canUseSkill1()));
        skill2Btn.setEnabled(enabled && (engine == null || engine.canUseSkill2()));
        skipTurnBtn.setEnabled(enabled);
        ultimateBtn.setEnabled(enabled && engine != null && engine.canUseUltimate());
    }

    private JButton makeSkillBtn(String label, Color bg, Color border, int x, int y, int w, int h) {
        JButton btn = new JButton(label);
        btn.setBounds(x, y, w, h);
        btn.setFont(new Font("Monospaced", Font.BOLD, 12));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(border, 2),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { if (btn.isEnabled()) btn.setBackground(border.darker()); }
            @Override public void mouseExited (MouseEvent e) { btn.setBackground(bg); }
        });
        return btn;
    }

    private void delay(int ms, Runnable action) {
        javax.swing.Timer t = new javax.swing.Timer(ms, null);
        t.setRepeats(false);
        t.addActionListener(e -> { t.stop(); action.run(); });
        t.start();
    }

    public void showResultOverlay(JPanel overlay) {
        add(overlay);
        setComponentZOrder(overlay, 0);
        overlay.setBounds(0, 0, getWidth(), getHeight());
        overlay.setVisible(true);
        repaint();
    }
}