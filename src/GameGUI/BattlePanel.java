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
    private static final Color GOLD       = new Color(201, 168, 76);
    private static final Color GOLD_DIM   = new Color(100, 78, 30);
    private static final Color TEXT_BRIGHT= new Color(240, 232, 208);
    private static final Color TEXT_DIM   = new Color(140, 125, 95);
    private static final Color GREEN      = new Color(39, 174, 96);
    private static final Color GREEN_DARK = new Color(26, 107, 74);
    private static final Color RED        = new Color(192, 57, 43);
    private static final Color RED_DARK   = new Color(100, 20, 20);
    private static final Color BLUE       = new Color(52, 120, 219);
    private static final Color PURPLE     = new Color(130, 60, 200);
    private static final Color OVERLAY_BG = new Color(0, 0, 0, 200);

    // Fonts
    private static final Font FONT_TITLE  = new Font("Monospaced", Font.BOLD, 16);
    private static final Font FONT_STAT   = new Font("Monospaced", Font.PLAIN, 11);
    private static final Font FONT_BTN    = new Font("Monospaced", Font.BOLD, 12);
    private static final Font FONT_RESULT = new Font("Monospaced", Font.BOLD, 28);

    // Widgets
    private JLabel heroEmojiLbl, heroNameLbl, heroRoleLbl;
    private JProgressBar heroHpBar, heroEnergyBar;
    private JLabel heroHpText, heroEnergyText, heroStatusLbl;

    private JLabel enemyEmojiLbl, enemyNameLbl, enemyRoleLbl;
    private JProgressBar enemyHpBar;
    private JLabel enemyHpText, enemyStatusLbl;

    private JLabel roundLabel, turnLabel;
    private JTextArea logArea;
    private JButton skill1Btn, skill2Btn, skipTurnBtn, ultimateBtn;
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

    public void startBattle(HeroDefinition hero, EnemyDefinition enemy) {
        this.heroDef  = hero;
        this.enemyDef = enemy;
        Combatant heroC  = HeroData.buildHero(hero);
        Combatant enemyC = HeroData.buildEnemy(enemy);
        this.engine = new BattleLogic(heroC, enemyC, false);
        populateCombatantUI();
        refreshBattleUI();
        clearLog();
        addLog("BATTLE START! " + hero.name.toUpperCase() + " VS " + enemy.name.toUpperCase(), GOLD);
        resultOverlay.setVisible(false);
        setActionsEnabled(true);
        animating = false;
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

        // ── LAYER 3: HUD — round + turn inside hero card (no floating labels) ──
        roundLabel = new JLabel("Round 1", SwingConstants.CENTER);
        roundLabel.setBounds(440, 10, 400, 26);
        roundLabel.setFont(FONT_TITLE);
        roundLabel.setForeground(GOLD);
        roundLabel.setVisible(false); // hidden — shown inside stat cards

        turnLabel = new JLabel("Your Turn ▶", SwingConstants.CENTER);
        turnLabel.setBounds(440, 38, 400, 20);
        turnLabel.setFont(FONT_STAT);
        turnLabel.setForeground(GREEN);
        turnLabel.setVisible(false); // hidden — shown inside stat cards
        add(roundLabel);
        add(turnLabel);

        // ── LAYER 4: HUD — enemy stat card top-right ──
        JPanel enemyCard = buildStatCard(false);
        enemyCard.setBounds(930, 8, 340, 90);
        add(enemyCard);

        // ── LAYER 5: DialogueBox.png — exact same as intro screen ──
        JLabel dialogueBg = new JLabel();
        dialogueBg.setBounds(-40, 453, 1053, 343);
        java.net.URL dbUrl = getClass().getResource("/assets/GUIButtons/DialogueBox.png");
        if (dbUrl != null) {
            dialogueBg.setIcon(new ImageIcon(new ImageIcon(dbUrl).getImage().getScaledInstance(1053, 343, Image.SCALE_SMOOTH)));
        }
        add(dialogueBg);

        // ── LAYER 6: Log text — exact same position/font as intro dialogueBox ──
        logArea = new JTextArea();
        logArea.setBounds(104, 541, 900, 100);
        logArea.setEditable(false);
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);
        logArea.setOpaque(false);
        logArea.setBackground(new Color(0, 0, 0, 0));
        logArea.setForeground(Color.BLACK);
        try {
            java.io.InputStream fs = getClass().getResourceAsStream("/assets/AssetFont/Pixelari.ttf");
            if (fs != null) logArea.setFont(Font.createFont(Font.TRUETYPE_FONT, fs).deriveFont(Font.BOLD, 19f));
            else logArea.setFont(new Font("Dialog", Font.BOLD, 16));
        } catch (Exception ex) { logArea.setFont(new Font("Dialog", Font.BOLD, 16)); }
        logArea.setBorder(BorderFactory.createEmptyBorder(30, 70, 30, 20));
        add(logArea);

        // ── LAYER 7: Buttons — exact same positions as intro screen ──
        JButton continueBtn = makeBtn("/assets/GUIButtons/Continue.png", "/assets/GUIButtons/ContinueHover.png", 964, 554, 154, 64, "Continue", 10);
        JButton menuBtn     = makeBtn("/assets/GUIButtons/Menu.png",     "/assets/GUIButtons/MenuHover.png",     1103, 555, 148, 58, "Menu", 0);
        JButton backBtn     = makeBtn("/assets/GUIButtons/Back.png",     "/assets/GUIButtons/BackHover.png",     970, 613, 140, 50, "Back", 20);
        JButton exitBtn     = makeBtn("/assets/GUIButtons/Exit.png",     "/assets/GUIButtons/ExitHover.png",     1108, 613, 140, 50, "Exit", 19);
        exitBtn.addActionListener(e -> System.exit(0));
        add(continueBtn);
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

        skill1Btn.addActionListener(e   -> onPlayerAction(BattleLogic.BattleAction.ATTACK));
        skill2Btn.addActionListener(e   -> onPlayerAction(BattleLogic.BattleAction.SPECIAL));
        skipTurnBtn.addActionListener(e -> onPlayerAction(BattleLogic.BattleAction.DEFEND));
        ultimateBtn.addActionListener(e -> onPlayerAction(BattleLogic.BattleAction.SPECIAL));

        add(skill1Btn);
        add(skill2Btn);
        add(skipTurnBtn);
        add(ultimateBtn);

        // Compat aliases
        attackBtn  = skill1Btn;
        defendBtn  = skipTurnBtn;
        specialBtn = ultimateBtn;
        specialCdLabel = new JLabel("");
        specialCdLabel.setBounds(startX, btnY + btnH + 2, 4 * btnW + 3 * btnGap, 16);
        specialCdLabel.setFont(new Font("Monospaced", Font.PLAIN, 9));
        specialCdLabel.setForeground(TEXT_DIM);
        specialCdLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(specialCdLabel);

        // ── LAYER 8: Result overlay (topmost) ──
        resultOverlay = buildResultOverlay();
        resultOverlay.setBounds(0, 0, 1280, 720);
        resultOverlay.setVisible(false);
        add(resultOverlay);

        // Z-order: index 0 = front, last added = furthest back by default
        // Re-order so backgrounds are behind everything
        setComponentZOrder(resultOverlay, 0);
        setComponentZOrder(logArea,        1);
        setComponentZOrder(continueBtn,    2);
        setComponentZOrder(menuBtn,        3);
        setComponentZOrder(backBtn,        4);
        setComponentZOrder(exitBtn,        5);
        setComponentZOrder(skill1Btn,      6);
        setComponentZOrder(skill2Btn,      7);
        setComponentZOrder(skipTurnBtn,    8);
        setComponentZOrder(ultimateBtn,    9);
        setComponentZOrder(specialCdLabel,10);
        setComponentZOrder(dialogueBg,    11);
        setComponentZOrder(heroCard,      12);
        setComponentZOrder(roundLabel,    13);
        setComponentZOrder(turnLabel,     14);
        setComponentZOrder(enemyCard,     15);
        setComponentZOrder(battleBg,      16);
        setComponentZOrder(theBg,         17);
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
        JPanel card = new JPanel(null);
        card.setBackground(new Color(10, 9, 20, 200));
        card.setOpaque(true);
        card.setBorder(BorderFactory.createLineBorder(isHero ? GREEN_DARK : RED_DARK, 1));

        JLabel emoji = new JLabel("", SwingConstants.CENTER);
        emoji.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        emoji.setBounds(4, 4, 40, 40);
        card.add(emoji);

        JLabel name = new JLabel("");
        name.setFont(new Font("Monospaced", Font.BOLD, 11));
        name.setForeground(isHero ? GREEN : RED);
        name.setBounds(50, 4, 280, 16);
        card.add(name);

        JLabel role = new JLabel("");
        role.setFont(new Font("Monospaced", Font.ITALIC, 9));
        role.setForeground(TEXT_DIM);
        role.setBounds(50, 20, 280, 14);
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
        hpBar.setBounds(50, 38, 200, 8);
        card.add(hpBar);

        JLabel hpText = new JLabel("—");
        hpText.setFont(FONT_STAT); hpText.setForeground(TEXT_BRIGHT);
        hpText.setBounds(255, 34, 80, 16);
        card.add(hpText);

        JLabel statusLbl = new JLabel(" ");
        statusLbl.setFont(new Font("Monospaced", Font.BOLD, 9));
        statusLbl.setForeground(BLUE);
        statusLbl.setBounds(50, 72, 280, 14);
        card.add(statusLbl);

        if (isHero) {
            heroEmojiLbl = emoji; heroNameLbl = name; heroRoleLbl = role;
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
            energyBar.setBounds(50, 54, 200, 6);
            card.add(energyBar);

            JLabel energyText = new JLabel("—");
            energyText.setFont(FONT_STAT); energyText.setForeground(TEXT_BRIGHT);
            energyText.setBounds(255, 50, 80, 14);
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
        enemyEmojiLbl.setText(enemyDef.emoji);
        enemyNameLbl.setText(enemyDef.name);
        enemyRoleLbl.setText(enemyDef.role);
        heroHpBar.setMaximum(h.maxHp);
        enemyHpBar.setMaximum(e.maxHp);
        if (heroEnergyBar != null) heroEnergyBar.setMaximum(h.maxEnergy);
        if (h.special != null) {
            skill2Btn.setText(h.special.icon + " " + h.special.name);
            ultimateBtn.setText("★ Ultimate");
        }
    }

    private void refreshBattleUI() {
        Combatant h = engine.getHero();
        Combatant e = engine.getEnemy();
        heroHpBar.setValue(h.currentHp);
        heroHpText.setText(h.currentHp + "/" + h.maxHp);
        enemyHpBar.setValue(e.currentHp);
        enemyHpText.setText(e.currentHp + "/" + e.maxHp);
        if (heroEnergyBar != null) {
            heroEnergyBar.setValue(h.energy);
            heroEnergyText.setText(h.energy + "/" + h.maxEnergy);
        }
        roundLabel.setText("Round " + engine.getRound());
        boolean isPlayer = engine.getCurrentTurn() == TurnOwner.PLAYER;
        turnLabel.setText(isPlayer ? "Your Turn ▶" : "Enemy Turn ▶");
        turnLabel.setForeground(isPlayer ? GREEN : RED);
        int cd = h.specialCooldown;
        specialBtn.setEnabled(cd == 0);
        specialCdLabel.setText(cd > 0 ? "CD: " + cd : "");
        heroStatusLbl.setText(h.defending  ? "Defending" : " ");
        enemyStatusLbl.setText(e.defending ? "Defending" : " ");
    }

    private void onPlayerAction(BattleLogic.BattleAction action) {
        if (animating) return;
        if (engine.getCurrentTurn() != TurnOwner.PLAYER) return;
        animating = true;
        setActionsEnabled(false);
        ActionResult pr = engine.playerAction(action);
        if (pr != null) addLogFromResult(pr, true);
        refreshBattleUI();
        if (engine.checkOutcome() == BattleOutcome.VICTORY) { showResult(true); animating = false; return; }
        Timer t1 = new Timer(900, e -> {
            engine.advanceToEnemyTurn();
            Timer t2 = new Timer(500, ev -> {
                ActionResult er = engine.enemyTurn();
                if (er != null) addLogFromResult(er, false);
                refreshBattleUI();
                if (engine.checkOutcome() == BattleOutcome.DEFEAT) handleDefeat();
                else setActionsEnabled(true);
                animating = false;
            });
            t2.setRepeats(false); t2.start();
        });
        t1.setRepeats(false); t1.start();
    }

    private void handleDefeat() {
        addLog("You have fallen...", RED);
        if (engine.isPhoenixSoulstoneAvailable()) {
            engine.attemptRevive(); refreshBattleUI(); setActionsEnabled(true); return;
        }
        if (!engine.isReviveUsed()) {
            JOptionPane.showInputDialog(this, "Q: What keyword is used to inherit a class in Java?");
            String ans = JOptionPane.showInputDialog(this, "Answer:");
            if (ans != null && ans.trim().equalsIgnoreCase("extends")) {
                engine.confirmRevive();
                addLog("Correct! Revived at 50% HP!", GREEN);
                refreshBattleUI(); setActionsEnabled(true); return;
            }
        }
        showResult(false);
    }

    private void showResult(boolean victory) {
        resultIcon.setText(victory ? "WIN" : "LOSE");
        resultTitle.setText(victory ? "VICTORY" : "DEFEAT");
        resultTitle.setForeground(victory ? GOLD : RED);
        resultSub.setText(victory ? heroDef.name + " wins!" : heroDef.name + " has fallen...");
        resultOverlay.setVisible(true);
        setComponentZOrder(resultOverlay, 0);
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
        Color c = isPlayer ? GREEN : RED;
        if (r.isBerserk) c = new Color(255,100,50);
        if (r.wasDefend) c = BLUE;
        if (r.isSpecial) c = PURPLE;
        addLog(r.logMessage, c);
        if (r.dotDamageApplied > 0) addLog("Burn tick: " + r.dotDamageApplied, new Color(200,120,50));
    }

    private void addLog(String text, Color color) {
        logArea.append(text + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    private void clearLog() { logArea.setText(""); }

    private void setActionsEnabled(boolean enabled) {
        skill1Btn.setEnabled(enabled);
        skill2Btn.setEnabled(enabled);
        skipTurnBtn.setEnabled(enabled);
        if (enabled && engine != null) ultimateBtn.setEnabled(engine.getHero().specialCooldown == 0);
        else ultimateBtn.setEnabled(false);
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

    public void showResultOverlay(JPanel overlay) {
        add(overlay);
        setComponentZOrder(overlay, 0);
        overlay.setBounds(0, 0, getWidth(), getHeight());
        overlay.setVisible(true);
        repaint();
    }
}