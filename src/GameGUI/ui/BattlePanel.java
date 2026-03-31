package GameGUI.ui;

import GameGUI.model.Combatant;
import GameGUI.model.HeroData.HeroDefinition;
import GameGUI.model.HeroData.EnemyDefinition;
import GameGUI.model.HeroFactory;
import GameGUI.logic.BattleManager;
import GameGUI.logic.ProgressionService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class BattlePanel extends JPanel {

    private Runnable onReturnToSelection;
    private Runnable onRestartBattle;
    private BiConsumer<Integer, Runnable> onEnemyGroupDefeated;

    private BattleManager engine;
    private Combatant currentHero;
    private Combatant currentEnemy;
    private HeroDefinition heroDef;
    private EnemyDefinition enemyDef;
    private boolean animating = false;

    // Colors
    private static final Color BG_DARK    = new Color(12, 10, 22);
    private static final Color BG_PANEL   = new Color(22, 20, 38);
    private static final Color GOLD       = new Color(120, 80, 10);
    private static final Color GOLD_DIM   = new Color(80, 55, 10);
    private static final Color TEXT_BRIGHT= new Color(240, 232, 208);
    private static final Color TEXT_DIM   = new Color(140, 125, 95);
    private static final Color GREEN      = new Color(0, 110, 45);
    private static final Color GREEN_DARK = new Color(0, 80, 30);
    private static final Color RED        = new Color(160, 20, 20);
    private static final Color RED_DARK   = new Color(110, 10, 10);
    private static final Color BLUE       = new Color(30, 80, 180);
    private static final Color PURPLE     = new Color(90, 30, 160);
    private static final Color OVERLAY_BG = new Color(0, 0, 0, 200);
    private static final Color BORDER_NORM = new Color(70, 65, 100);

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

    private JLabel roundLabel, turnLabel;
    private javax.swing.JTextPane logArea;
    private JButton skill1Btn, skill2Btn, skipTurnBtn, ultimateBtn, battleContinueBtn;
    private JLabel specialCdLabel;
    private JPanel resultOverlay;
    private JLabel resultIcon, resultTitle, resultSub;

    // Loot Choice UI
    private JPanel lootChoiceOverlay;
    private JLabel lootItem1Icon, lootItem1Name;
    private JTextArea lootItem1Desc;
    private JButton lootItem1Btn;

    private JLabel lootItem2Icon, lootItem2Name;
    private JTextArea lootItem2Desc;
    private JButton lootItem2Btn;

    // Sequence tracking
    private List<EnemyDefinition> enemySequence;
    private int enemySequenceIndex = 0;
    private int enemyFightIndex    = 0;
    private Runnable onSequenceComplete;
    private Combatant savedHeroCombatant = null;

    // Level up tracking
    private enum PostVictoryStep { NONE, LOOT, LEVEL_UP_ANNOUNCE, LEVEL_UP_STATS, LEVEL_UP_RESTORE, OBJECTIVE, VICTORY_FLAVOUR, LOOT_FLAVOUR }
    private PostVictoryStep postVictoryStep = PostVictoryStep.NONE;
    private int lvlUp_level, lvlUp_hpGain, lvlUp_newHp, lvlUp_atkGain, lvlUp_newAtk, lvlUp_defGain, lvlUp_newDef;
    private EnemyDefinition postVictoryEnemy = null;
    private Runnable postVictoryNext = null;

    private Font normalLogFont = null;
    private Font smallLogFont  = null;

    public BattlePanel() {
        setLayout(null);
        setPreferredSize(new Dimension(1280, 720));
        setBackground(BG_DARK);
        buildUI();
    }

    // Returns the live hero data so the Magic Shop can read their Soul Shards and Stats
    public Combatant getCurrentHero() {
        return currentHero;
    }

    public void setOnReturnToSelection(Runnable r) { this.onReturnToSelection = r; }
    public void setOnRestartBattle(Runnable r)     { this.onRestartBattle = r; }
    public void setOnEnemyGroupDefeated(BiConsumer<Integer, Runnable> cb) { this.onEnemyGroupDefeated = cb; }

    public void startEnemySequence(HeroDefinition hero, List<EnemyDefinition> sequence, Runnable onComplete) {
        this.heroDef = hero;
        this.enemySequence = new ArrayList<>(sequence);
        this.enemySequenceIndex = 0;
        this.enemyFightIndex    = 0;
        this.onSequenceComplete = onComplete;
        startNextFight();
    }

    private void startNextFight() {
        if (enemySequenceIndex >= enemySequence.size()) {
            if (onSequenceComplete != null) onSequenceComplete.run();
            return;
        }

        EnemyDefinition eDef = enemySequence.get(enemySequenceIndex);
        this.enemyDef = eDef;

        currentHero = (savedHeroCombatant != null) ? savedHeroCombatant : HeroFactory.createHero(heroDef);
        savedHeroCombatant = null;
        currentEnemy = HeroFactory.createEnemy(eDef);

        this.engine = new BattleManager(currentHero, currentEnemy, heroDef, false);

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

    private void buildUI() {
        JLabel theBg = new JLabel();
        theBg.setBounds(0, 0, 1280, 720);
        theBg.setOpaque(true);
        theBg.setBackground(BG_DARK);
        java.net.URL theBgUrl = getClass().getResource("/assets/Backgrounds/TheBackground.png");
        if (theBgUrl != null) theBg.setIcon(new ImageIcon(new ImageIcon(theBgUrl).getImage().getScaledInstance(1280, 720, Image.SCALE_SMOOTH)));
        add(theBg);

        JLabel battleBg = new JLabel();
        battleBg.setBounds(0, 0, 1280, 520);
        battleBg.setOpaque(false);
        java.net.URL battleBgUrl = getClass().getResource("/assets/Backgrounds/World1BattleBackground.png");
        if (battleBgUrl != null) battleBg.setIcon(new ImageIcon(new ImageIcon(battleBgUrl).getImage().getScaledInstance(1280, 520, Image.SCALE_SMOOTH)));
        add(battleBg);

        JPanel heroCard = buildStatCard(true);
        heroCard.setBounds(10, 8, 340, 90);
        add(heroCard);

        JPanel enemyCard = buildStatCard(false);
        enemyCard.setBounds(920, 8, 340, 90);
        add(enemyCard);

        JLabel dialogueBg = new JLabel();
        dialogueBg.setBounds(-40, 453, 1053, 343);
        java.net.URL dbUrl = getClass().getResource("/assets/GUIButtons/DialogueBox.png");
        if (dbUrl != null) dialogueBg.setIcon(new ImageIcon(new ImageIcon(dbUrl).getImage().getScaledInstance(1053, 343, Image.SCALE_SMOOTH)));
        add(dialogueBg);

        turnLabel = new JLabel("YOUR TURN", SwingConstants.CENTER);
        turnLabel.setBounds(0, 8, 1280, 26);
        turnLabel.setFont(new Font("Monospaced", Font.BOLD, 16));
        turnLabel.setForeground(GREEN);
        add(turnLabel);

        roundLabel = new JLabel("Round 1", SwingConstants.CENTER);
        roundLabel.setBounds(0, 36, 1280, 22);
        roundLabel.setFont(new Font("Monospaced", Font.BOLD, 13));
        roundLabel.setForeground(GOLD);
        add(roundLabel);

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

        battleContinueBtn = makeBtn("/assets/GUIButtons/Continue.png", "/assets/GUIButtons/ContinueHover.png", 964, 554, 154, 64, "Continue", 10);
        battleContinueBtn.addActionListener(e -> onContinuePressed());
        JButton menuBtn = makeBtn("/assets/GUIButtons/Menu.png", "/assets/GUIButtons/MenuHover.png", 1103, 555, 148, 58, "Menu", 0);
        JButton backBtn = makeBtn("/assets/GUIButtons/Back.png", "/assets/GUIButtons/BackHover.png", 970, 613, 140, 50, "Back", 20);
        JButton exitBtn = makeBtn("/assets/GUIButtons/Exit.png", "/assets/GUIButtons/ExitHover.png", 1108, 613, 140, 50, "Exit", 19);
        exitBtn.addActionListener(e -> System.exit(0));

        add(battleContinueBtn); add(menuBtn); add(backBtn); add(exitBtn);

        int btnY = 478, btnW = 140, btnH = 48, btnGap = 12;
        int startX = (1280 - (4 * btnW + 3 * btnGap)) / 2;

        skill1Btn   = makeSkillBtn("Skill 1", new Color(60, 30, 90), new Color(130, 60, 200), startX, btnY, btnW, btnH);
        skill2Btn   = makeSkillBtn("Skill 2", new Color(30, 60, 90), new Color(52, 120, 219), startX + (btnW + btnGap), btnY, btnW, btnH);
        ultimateBtn = makeSkillBtn("Ultimate", new Color(90, 30, 30), new Color(192, 57, 43), startX + 2*(btnW+btnGap), btnY, btnW, btnH);
        skipTurnBtn = makeSkillBtn("Skip Turn", new Color(30, 60, 40), new Color(39, 174, 96), startX + 3*(btnW+btnGap), btnY, btnW, btnH);

        skill1Btn.addActionListener(e   -> onPlayerAction(BattleManager.BattleAction.SKILL1));
        skill2Btn.addActionListener(e   -> onPlayerAction(BattleManager.BattleAction.SKILL2));
        ultimateBtn.addActionListener(e -> onPlayerAction(BattleManager.BattleAction.ULTIMATE));
        skipTurnBtn.addActionListener(e -> onPlayerAction(BattleManager.BattleAction.SKIP_TURN));

        add(skill1Btn); add(skill2Btn); add(skipTurnBtn); add(ultimateBtn);

        specialCdLabel = new JLabel("");
        specialCdLabel.setBounds(0, 60, 1280, 20);
        specialCdLabel.setFont(new Font("Monospaced", Font.BOLD, 12));
        specialCdLabel.setForeground(new Color(200, 100, 50));
        specialCdLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(specialCdLabel);

        resultOverlay = buildResultOverlay();
        resultOverlay.setBounds(0, 0, 1280, 720);
        resultOverlay.setVisible(false);
        add(resultOverlay);

        lootChoiceOverlay = buildLootChoiceOverlay();
        lootChoiceOverlay.setBounds(0, 0, 1280, 720);
        lootChoiceOverlay.setVisible(false);
        add(lootChoiceOverlay);

        setComponentZOrder(resultOverlay, 0);
        setComponentZOrder(specialCdLabel, 1);
        setComponentZOrder(roundLabel, 2);
        setComponentZOrder(turnLabel, 3);
        setComponentZOrder(logArea, 3);
        setComponentZOrder(battleContinueBtn, 4);
        setComponentZOrder(menuBtn, 5);
        setComponentZOrder(backBtn, 6);
        setComponentZOrder(exitBtn, 7);
        setComponentZOrder(skill1Btn, 8);
        setComponentZOrder(skill2Btn, 9);
        setComponentZOrder(skipTurnBtn, 10);
        setComponentZOrder(ultimateBtn, 11);
        setComponentZOrder(dialogueBg, 13);
        setComponentZOrder(heroCard, 14);
        setComponentZOrder(enemyCard, 15);
        setComponentZOrder(battleBg, 16);
        setComponentZOrder(theBg, 17);
    }

    private void onPlayerAction(BattleManager.BattleAction action) {
        if (animating || engine.getCurrentTurn() != BattleManager.TurnOwner.PLAYER) return;
        animating = true;
        setActionsEnabled(false);

        clearLog();
        setTurnLabel(true);
        BattleManager.ActionResult pResult = engine.playerAction(action);
        if (pResult != null) addLogFromResult(pResult, true);
        refreshBattleUI();

        if (engine.checkOutcome() == BattleManager.BattleOutcome.VICTORY) {
            handleVictory();
            return;
        }

        Timer t1 = new Timer(900, e -> {
            engine.advanceToEnemyTurn();
            setTurnLabel(false);

            Timer t2 = new Timer(500, ev -> {
                clearLog();
                BattleManager.ActionResult eResult = engine.enemyTurn();
                if (eResult != null) addLogFromResult(eResult, false);
                refreshBattleUI();

                if (engine.checkOutcome() == BattleManager.BattleOutcome.DEFEAT) {
                    handleDefeat();
                } else {
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

    private void handleVictory() {
        if (enemySequence != null) {
            EnemyDefinition eDef = enemySequence.get(enemySequenceIndex);
            enemyFightIndex++;
            clearLog();
            if (enemyFightIndex < eDef.count) {
                savedHeroCombatant = engine.getHero();
                addLog(eDef.name + " defeated! (" + enemyFightIndex + "/" + eDef.count + ")", GREEN);
                delay(2000, () -> {
                    clearLog();
                    addLog("Another " + eDef.name + " approaches!", GOLD);
                    delay(1200, this::startNextFight);
                });
            } else {
                savedHeroCombatant = engine.getHero();
                enemySequenceIndex++;
                enemyFightIndex = 0;
                setActionsEnabled(false);

                Runnable afterSequence = () -> {
                    if (enemySequenceIndex < enemySequence.size()) {
                        if (onEnemyGroupDefeated != null) onEnemyGroupDefeated.accept(enemySequenceIndex, this::startNextFight);
                        else delay(500, this::startNextFight);
                    } else {
                        if (onSequenceComplete != null) onSequenceComplete.run();
                    }
                };

                startPostVictorySequence(eDef, afterSequence);
            }
        } else {
            showResult(true);
        }
    }

    private void startPostVictorySequence(EnemyDefinition eDef, Runnable onDone) {
        postVictoryEnemy = eDef;
        postVictoryNext = onDone;
        postVictoryStep = PostVictoryStep.LOOT;
        lvlUp_level = 0;

        int xp = eDef.xpReward * eDef.count;
        boolean leveledUp = ProgressionService.gainExp(currentHero, xp, 1);

        if (leveledUp && currentHero.lastLevelUpData != null) {
            parseLevelUpMsg(currentHero.lastLevelUpData);
            currentHero.lastLevelUpData = null;
        }

        setLogFontSmall();
        clearLog();
        addLog("You received:", GOLD);
        addLog("  +" + xp + " XP", new Color(140, 90, 0));
        addLog("  + Soul Shards", new Color(20, 80, 160));

        battleContinueBtn.setEnabled(true);
        turnLabel.setText("");
        roundLabel.setText("");
        specialCdLabel.setText("");
    }

    private void onContinuePressed() {
        if (postVictoryStep == PostVictoryStep.NONE) return;
        battleContinueBtn.setEnabled(false);

        switch (postVictoryStep) {
            case LOOT -> {
                clearLog();
                if (lvlUp_level > 0) {
                    postVictoryStep = PostVictoryStep.LEVEL_UP_ANNOUNCE;
                    setLogFontSmall();
                    addLog("✨ LEVEL UP! You are now Level " + lvlUp_level + "! ✨", new Color(130, 85, 0));
                } else {
                    postVictoryStep = PostVictoryStep.OBJECTIVE;
                    setLogFontNormal();
                    addLog("OBJECTIVE: " + postVictoryEnemy.name.toUpperCase() + " CLEARED!", GOLD);
                }
                battleContinueBtn.setEnabled(true);
            }
            case LEVEL_UP_ANNOUNCE -> {
                postVictoryStep = PostVictoryStep.LEVEL_UP_STATS;
                clearLog();
                addLog("💚 Max HP  : +" + lvlUp_hpGain  + "  →  " + lvlUp_newHp,  new Color(0, 120, 50));
                addLog("⚔  Max ATK : +" + lvlUp_atkGain + "  →  " + lvlUp_newAtk, new Color(140, 70, 0));
                addLog("🛡  DEF     : +" + lvlUp_defGain + "  →  " + lvlUp_newDef, new Color(20, 70, 160));
                battleContinueBtn.setEnabled(true);
            }
            case LEVEL_UP_STATS -> {
                postVictoryStep = PostVictoryStep.LEVEL_UP_RESTORE;
                clearLog();
                addLog("50% of HP & Energy Restored!", new Color(0, 100, 40));
                battleContinueBtn.setEnabled(true);
            }
            case LEVEL_UP_RESTORE -> {
                postVictoryStep = PostVictoryStep.OBJECTIVE;
                setLogFontNormal();
                clearLog();
                addLog("OBJECTIVE: " + postVictoryEnemy.name.toUpperCase() + " CLEARED!", GOLD);
                battleContinueBtn.setEnabled(true);
            }
            case OBJECTIVE -> {
                postVictoryStep = PostVictoryStep.VICTORY_FLAVOUR;
                clearLog();
                addLog("Victory! You collect your rewards and move on.", GREEN);
                battleContinueBtn.setEnabled(true);
            }
            case VICTORY_FLAVOUR -> {
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

    private void handleDefeat() {
        addLog("You have fallen...", RED);
        // Integrate the learning check or revive logic here
        String ans = JOptionPane.showInputDialog(this, "Q: What keyword is used to inherit a class in Java?");
        if (ans != null && ans.trim().equalsIgnoreCase("extends")) {
            currentHero.currentHp = currentHero.maxHp / 2;
            currentHero.energy = currentHero.maxEnergy / 2;
            clearLog();
            addLog("Correct! Revived at 50% HP!", GREEN);
            refreshBattleUI();
            setTurnLabel(true);
            setActionsEnabled(true);
            animating = false;
        } else {
            showResult(false);
        }
    }

    private void parseLevelUpMsg(String msg) {
        String[] p = msg.split("\\|");
        if (p.length < 8) return;
        lvlUp_level   = Integer.parseInt(p[1]);
        lvlUp_hpGain  = Integer.parseInt(p[2]);
        lvlUp_newHp   = Integer.parseInt(p[3]);
        lvlUp_atkGain = Integer.parseInt(p[4]);
        lvlUp_newAtk  = Integer.parseInt(p[5]);
        lvlUp_defGain = Integer.parseInt(p[6]);
        lvlUp_newDef  = Integer.parseInt(p[7]);
    }

    private void addLogFromResult(BattleManager.ActionResult r, boolean isPlayer) {
        Color c = isPlayer ? GREEN : RED;
        if (r.wasDefend) c = BLUE;
        if (r.isSpecial) c = PURPLE;
        addLog(r.logMessage, c);
        if (r.dotDamageApplied > 0) addLog("Status tick: " + r.dotDamageApplied, new Color(150, 60, 0));
    }

    private void addLog(String text, Color color) {
        javax.swing.text.StyledDocument doc = logArea.getStyledDocument();
        javax.swing.text.SimpleAttributeSet attrs = new javax.swing.text.SimpleAttributeSet();
        javax.swing.text.StyleConstants.setForeground(attrs, color);
        javax.swing.text.StyleConstants.setFontFamily(attrs, logArea.getFont().getFamily());
        javax.swing.text.StyleConstants.setFontSize(attrs, logArea.getFont().getSize());
        javax.swing.text.StyleConstants.setBold(attrs, true);
        try { doc.insertString(doc.getLength(), text + "\n", attrs); }
        catch (Exception e) {}
        logArea.setCaretPosition(doc.getLength());
    }

    private void clearLog() { logArea.setText(""); }

    private void setTurnLabel(boolean isPlayer) {
        if (turnLabel != null) {
            turnLabel.setText(isPlayer ? "YOUR TURN" : "ENEMY TURN");
            turnLabel.setForeground(isPlayer ? GREEN : RED);
        }
    }

    private void setActionsEnabled(boolean enabled) {
        skill1Btn.setEnabled(enabled && (engine == null || engine.canUseSkill1()));
        skill2Btn.setEnabled(enabled && (engine == null || engine.canUseSkill2()));
        skipTurnBtn.setEnabled(enabled);
        ultimateBtn.setEnabled(enabled && engine != null && engine.canUseUltimate());
    }

    private void populateCombatantUI() {
        heroEmojiLbl.setText(heroDef.emoji);
        heroNameLbl.setText(heroDef.name);
        heroRoleLbl.setText(heroDef.role);
        if (heroLvlLbl != null) heroLvlLbl.setText("Lv." + currentHero.level);
        enemyEmojiLbl.setText(enemyDef.emoji);
        enemyNameLbl.setText(enemyDef.name);
        enemyRoleLbl.setText(enemyDef.role);

        heroHpBar.setMaximum(currentHero.maxHp);
        enemyHpBar.setMaximum(currentEnemy.maxHp);
        if (heroEnergyBar != null) heroEnergyBar.setMaximum(currentHero.maxEnergy);

        if (heroDef.skills != null && heroDef.skills.length >= 3) {
            skill1Btn.setText(heroDef.skills[0].icon + " " + heroDef.skills[0].name);
            skill2Btn.setText(heroDef.skills[1].icon + " " + heroDef.skills[1].name);
            ultimateBtn.setText(heroDef.skills[2].icon + " " + heroDef.skills[2].name);
        }
    }

    private void refreshBattleUI() {
        heroHpBar.setValue(currentHero.currentHp);
        heroHpText.setText(currentHero.currentHp + "/" + currentHero.maxHp);
        if (heroLvlLbl != null) heroLvlLbl.setText("Lv." + currentHero.level);

        enemyHpBar.setValue(currentEnemy.currentHp);
        enemyHpText.setText(currentEnemy.currentHp + "/" + currentEnemy.maxHp);

        if (heroEnergyBar != null) {
            heroEnergyBar.setValue(currentHero.energy);
            heroEnergyText.setText(currentHero.energy + "/" + currentHero.maxEnergy);
        }

        roundLabel.setText("Round " + engine.getRound());
        int cd = currentHero.specialCooldown;
        specialCdLabel.setText(cd > 0 ? "CD: " + cd : "");

        skill1Btn.setEnabled(engine == null || engine.canUseSkill1());
        skill2Btn.setEnabled(engine == null || engine.canUseSkill2());
        ultimateBtn.setEnabled(cd == 0 && (engine == null || engine.canUseUltimate()));

        heroStatusLbl.setText(currentHero.defending ? "Defending" : " ");
        enemyStatusLbl.setText(currentEnemy.defending ? "Defending" : " ");
    }

    private void initLogFonts() {
        if (normalLogFont != null) return;
        try {
            java.io.InputStream fs = getClass().getResourceAsStream("/assets/AssetFont/Pixelari.ttf");
            if (fs != null) {
                Font base = Font.createFont(Font.TRUETYPE_FONT, fs);
                normalLogFont = base.deriveFont(Font.BOLD, 19f);
                smallLogFont  = base.deriveFont(Font.BOLD, 14f);
            }
        } catch (Exception ex) {}
        if (normalLogFont == null) normalLogFont = new Font("Dialog", Font.BOLD, 16);
        if (smallLogFont  == null) smallLogFont  = new Font("Dialog", Font.BOLD, 12);
    }

    private void setLogFontSmall()  { initLogFonts(); logArea.setFont(smallLogFont); }
    private void setLogFontNormal() { initLogFonts(); logArea.setFont(normalLogFont); }

    private void delay(int ms, Runnable action) {
        javax.swing.Timer t = new javax.swing.Timer(ms, null);
        t.setRepeats(false);
        t.addActionListener(e -> { t.stop(); action.run(); });
        t.start();
    }

    private void showResult(boolean victory) {
        resultIcon.setText(victory ? "WIN" : "LOSE");
        resultTitle.setText(victory ? "VICTORY" : "DEFEAT");
        resultTitle.setForeground(victory ? GOLD : RED);
        resultSub.setText(victory ? heroDef.name + " wins!" : heroDef.name + " has fallen...");
        resultOverlay.setBounds(0, 0, getWidth(), getHeight());
        setComponentZOrder(resultOverlay, 0);
        resultOverlay.setVisible(true);
        revalidate(); repaint();
    }

    // --- Swing Builder Helpers ---

    private JButton makeBtn(String normalPath, String hoverPath, int x, int y, int w, int h, String fallback, int hoverOffset) {
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

    private JPanel buildStatCard(boolean isHero) {
        final Color cardBg = new Color(10, 9, 20, 220);
        final Color cardBorder = isHero ? GREEN_DARK : RED_DARK;
        JPanel card = new JPanel(null) {
            @Override public boolean isOptimizedDrawingEnabled() { return false; }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(cardBg); g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(cardBorder); g2.drawRect(0, 0, getWidth()-1, getHeight()-1);
                g2.dispose();
            }
        };
        card.setOpaque(false);

        JLabel emoji = new JLabel("", SwingConstants.CENTER);
        emoji.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        emoji.setBounds(4, 4, 40, 40); card.add(emoji);

        JLabel name = new JLabel("");
        name.setFont(new Font("Monospaced", Font.BOLD, 11));
        name.setForeground(isHero ? GREEN : RED);
        name.setBounds(50, 4, 215, 16); card.add(name);

        JLabel lvlLbl = new JLabel("Lv.1");
        lvlLbl.setFont(new Font("Monospaced", Font.BOLD, 10));
        lvlLbl.setForeground(GOLD);
        lvlLbl.setBounds(270, 4, 60, 16); card.add(lvlLbl);

        JLabel role = new JLabel("");
        role.setFont(new Font("Monospaced", Font.ITALIC, 9));
        role.setForeground(TEXT_DIM);
        role.setBounds(50, 20, 215, 14); card.add(role);

        JLabel hpLbl = new JLabel("HP");
        hpLbl.setFont(FONT_STAT); hpLbl.setForeground(TEXT_DIM);
        hpLbl.setBounds(4, 38, 40, 14); card.add(hpLbl);

        JProgressBar hpBar = new JProgressBar(0, 100);
        hpBar.setValue(100); hpBar.setForeground(isHero ? GREEN : RED);
        hpBar.setBackground(new Color(30, 28, 50)); hpBar.setBorder(null);
        hpBar.setBounds(50, 38, 175, 8); card.add(hpBar);

        JLabel hpText = new JLabel("—");
        hpText.setFont(FONT_STAT); hpText.setForeground(TEXT_BRIGHT);
        hpText.setBounds(228, 34, 62, 16); card.add(hpText);

        JLabel statusLbl = new JLabel(" ");
        statusLbl.setFont(new Font("Monospaced", Font.BOLD, 9));
        statusLbl.setForeground(BLUE);
        statusLbl.setBounds(50, 72, 215, 14); card.add(statusLbl);

        if (isHero) {
            heroEmojiLbl = emoji; heroNameLbl = name; heroRoleLbl = role; heroLvlLbl = lvlLbl;
            heroHpBar = hpBar; heroHpText = hpText; heroStatusLbl = statusLbl;

            JLabel epLbl = new JLabel("EP");
            epLbl.setFont(FONT_STAT); epLbl.setForeground(TEXT_DIM);
            epLbl.setBounds(4, 54, 40, 14); card.add(epLbl);

            JProgressBar energyBar = new JProgressBar(0, 100);
            energyBar.setValue(100); energyBar.setForeground(new Color(200, 180, 80));
            energyBar.setBackground(new Color(30, 28, 50)); energyBar.setBorder(null);
            energyBar.setBounds(50, 54, 175, 6); card.add(energyBar);

            JLabel energyText = new JLabel("—");
            energyText.setFont(FONT_STAT); energyText.setForeground(TEXT_BRIGHT);
            energyText.setBounds(228, 50, 62, 14); card.add(energyText);

            heroEnergyBar = energyBar; heroEnergyText = energyText;
        } else {
            enemyEmojiLbl = emoji; enemyNameLbl = name; enemyRoleLbl = role;
            enemyHpBar = hpBar; enemyHpText = hpText; enemyStatusLbl = statusLbl;
        }
        return card;
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
        restartBtn.setFocusPainted(false); restartBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        restartBtn.setAlignmentX(CENTER_ALIGNMENT);
        restartBtn.addActionListener(e -> { if (onRestartBattle != null) onRestartBattle.run(); });

        JButton backBtn2 = new JButton("New Champion");
        backBtn2.setFont(FONT_BTN); backBtn2.setForeground(GOLD);
        backBtn2.setBackground(new Color(22,20,38));
        backBtn2.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(GOLD_DIM,1),new EmptyBorder(10,24,10,24)));
        backBtn2.setFocusPainted(false); backBtn2.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backBtn2.setAlignmentX(CENTER_ALIGNMENT);
        backBtn2.addActionListener(e -> { if (onReturnToSelection != null) onReturnToSelection.run(); });

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
        btns.setBackground(new Color(20,18,36));
        btns.add(restartBtn); btns.add(backBtn2);

        card.add(resultIcon); card.add(Box.createVerticalStrut(8));
        card.add(resultTitle); card.add(resultSub); card.add(btns);
        overlay.add(card);
        return overlay;
    }

    private JPanel buildLootChoiceOverlay() {
        JPanel overlay = new JPanel(new GridBagLayout()); // Centers the card
        overlay.setBackground(OVERLAY_BG); // Same 200 alpha black as your result screen
        overlay.setOpaque(true);

        // Main Card Container
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(new Color(20, 18, 36));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GOLD, 2),
                new EmptyBorder(30, 40, 30, 40)));

        JLabel title = new JLabel("CHOOSE YOUR REWARD", SwingConstants.CENTER);
        title.setFont(FONT_RESULT);
        title.setForeground(GOLD);
        title.setAlignmentX(CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Only one may be taken...", SwingConstants.CENTER);
        subtitle.setFont(new Font("Monospaced", Font.ITALIC, 14));
        subtitle.setForeground(TEXT_DIM);
        subtitle.setAlignmentX(CENTER_ALIGNMENT);

        // Side-by-Side Container
        JPanel itemsPanel = new JPanel(new GridLayout(1, 2, 40, 0));
        itemsPanel.setBackground(new Color(20, 18, 36));
        itemsPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        // --- ITEM 1 PANEL ---
        JPanel p1 = new JPanel();
        p1.setLayout(new BoxLayout(p1, BoxLayout.Y_AXIS));
        p1.setBackground(new Color(30, 28, 45));
        p1.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_NORM, 1),
                new EmptyBorder(15, 15, 15, 15)));

        lootItem1Icon = new JLabel("?", SwingConstants.CENTER);
        lootItem1Icon.setFont(new Font("Monospaced", Font.BOLD, 48));
        lootItem1Icon.setForeground(TEXT_DIM);
        lootItem1Icon.setPreferredSize(new Dimension(100, 100));
        lootItem1Icon.setAlignmentX(CENTER_ALIGNMENT);
        // TODO for your team: lootItem1Icon.setIcon(new ImageIcon("path/to/image.png"));

        lootItem1Name = new JLabel("Item 1 Name", SwingConstants.CENTER);
        lootItem1Name.setFont(new Font("Monospaced", Font.BOLD, 16));
        lootItem1Name.setForeground(TEXT_BRIGHT);
        lootItem1Name.setAlignmentX(CENTER_ALIGNMENT);

        lootItem1Desc = new JTextArea("Item 1 Description.\n+Stats\nEffects");
        lootItem1Desc.setFont(new Font("Monospaced", Font.PLAIN, 12));
        lootItem1Desc.setForeground(TEXT_DIM);
        lootItem1Desc.setBackground(new Color(30, 28, 45));
        lootItem1Desc.setLineWrap(true);
        lootItem1Desc.setWrapStyleWord(true);
        lootItem1Desc.setEditable(false);
        lootItem1Desc.setFocusable(false);
        lootItem1Desc.setAlignmentX(CENTER_ALIGNMENT);
        lootItem1Desc.setBorder(new EmptyBorder(10, 0, 15, 0));

        lootItem1Btn = makeSkillBtn("Take Item", new Color(40, 80, 40), GREEN, 0, 0, 120, 40);
        lootItem1Btn.setAlignmentX(CENTER_ALIGNMENT);

        p1.add(lootItem1Icon); p1.add(Box.createVerticalStrut(10));
        p1.add(lootItem1Name); p1.add(lootItem1Desc); p1.add(lootItem1Btn);

        // --- ITEM 2 PANEL ---
        JPanel p2 = new JPanel();
        p2.setLayout(new BoxLayout(p2, BoxLayout.Y_AXIS));
        p2.setBackground(new Color(30, 28, 45));
        p2.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_NORM, 1),
                new EmptyBorder(15, 15, 15, 15)));

        lootItem2Icon = new JLabel("?", SwingConstants.CENTER);
        lootItem2Icon.setFont(new Font("Monospaced", Font.BOLD, 48));
        lootItem2Icon.setForeground(TEXT_DIM);
        lootItem2Icon.setPreferredSize(new Dimension(100, 100));
        lootItem2Icon.setAlignmentX(CENTER_ALIGNMENT);
        // TODO for your team: lootItem2Icon.setIcon(new ImageIcon("path/to/image.png"));

        lootItem2Name = new JLabel("Item 2 Name", SwingConstants.CENTER);
        lootItem2Name.setFont(new Font("Monospaced", Font.BOLD, 16));
        lootItem2Name.setForeground(TEXT_BRIGHT);
        lootItem2Name.setAlignmentX(CENTER_ALIGNMENT);

        lootItem2Desc = new JTextArea("Item 2 Description.\n+Stats\nEffects");
        lootItem2Desc.setFont(new Font("Monospaced", Font.PLAIN, 12));
        lootItem2Desc.setForeground(TEXT_DIM);
        lootItem2Desc.setBackground(new Color(30, 28, 45));
        lootItem2Desc.setLineWrap(true);
        lootItem2Desc.setWrapStyleWord(true);
        lootItem2Desc.setEditable(false);
        lootItem2Desc.setFocusable(false);
        lootItem2Desc.setAlignmentX(CENTER_ALIGNMENT);
        lootItem2Desc.setBorder(new EmptyBorder(10, 0, 15, 0));

        lootItem2Btn = makeSkillBtn("Take Item", new Color(40, 80, 40), GREEN, 0, 0, 120, 40);
        lootItem2Btn.setAlignmentX(CENTER_ALIGNMENT);

        p2.add(lootItem2Icon); p2.add(Box.createVerticalStrut(10));
        p2.add(lootItem2Name); p2.add(lootItem2Desc); p2.add(lootItem2Btn);

        itemsPanel.add(p1);
        itemsPanel.add(p2);

        card.add(title);
        card.add(subtitle);
        card.add(itemsPanel);
        overlay.add(card);

        return overlay;
    }
}