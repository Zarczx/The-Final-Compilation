package GameGUI.ui;

import GameGUI.model.Combatant;
import GameGUI.model.HeroData.HeroDefinition;
import GameGUI.model.HeroData.EnemyDefinition;
import GameGUI.model.HeroFactory;
import GameGUI.logic.BattleManager;
import GameGUI.logic.ProgressionService;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
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

    // ════════════════════════════════════════════
    // ★ HERO SPRITE FIELDS
    // ════════════════════════════════════════════
    private JLabel heroSpriteLabel;
    private javax.swing.Timer heroIdleTimer;
    private int heroSpriteFrame = 0;

    private static final int SPRITE_FRAME_COUNT         = 2;
    private static final int BLADE_RUSH_FRAME_COUNT     = 6;
    private static final int PIERCING_SLASH_FRAME_COUNT = 8;
    private static final int ETERNAL_CROSS_FRAME_COUNT  = 8;
    private static final int KAEL_HURT_FRAME_COUNT      = 2;

    private static final int    SPRITE_W     = 80;
    private static final int    SPRITE_H     = 64;
    private static final double SPRITE_SCALE = 1.5;
    private static final int SHADE_X = 900;
    private static final int SHADE_Y = 340;

    private BufferedImage[] idleFrames;
    private BufferedImage[] bladeRushFrames;
    private BufferedImage[] piercingSlashFrames;
    private BufferedImage[] eternalCrossFrames;
    private BufferedImage[] kaelHurtFrames;

    private boolean isPlayingBladeRush     = false;
    private boolean isPlayingPiercingSlash = false;
    private boolean isPlayingEternalCross  = false;
    private boolean isPlayingKaelHurt      = false;

    // ════════════════════════════════════════════
    // ★ ENEMY SPRITE FIELDS
    // ════════════════════════════════════════════
    private JLabel enemySpriteLabel;
    private javax.swing.Timer enemyIdleTimer;
    private int enemySpriteFrame = 0;

    // Wolf
    private BufferedImage[] wolfIdleFrames;
    private BufferedImage[] wolfHurtFrames;
    private BufferedImage[] wolfSavageHowlFrames;
    private BufferedImage[] wolfDefeatFrames;
    private BufferedImage[] wolfEntranceFrames;

    private static final int    WOLF_FRAME_COUNT          = 5;
    private static final int    WOLF_HURT_FRAME_COUNT     = 2;
    private static final int    WOLF_HOWL_FRAME_COUNT     = 4;
    private static final int    WOLF_DEFEAT_FRAME_COUNT   = 4;
    private static final int    WOLF_ENTRANCE_FRAME_COUNT = 5;
    private static final int    WOLF_SPEED                = 170;
    private static final double ENEMY_SCALE               = 1.5;
    private static final int    ENEMY_Y                   = 365;
    private static final int    ENEMY_X                   = 900;
    private static final int    WOLF_ENTRANCE_START_X     = 1100;

    private boolean isPlayingWolfHurt       = false;
    private boolean isPlayingWolfSavageHowl = false;
    private boolean isPlayingWolfDefeat     = false;
    private boolean isPlayingWolfEntrance   = false;

    // Shade Sprite
    private BufferedImage[] spriteIdleFrames;
    private BufferedImage[] spriteHurtFrames;
    private BufferedImage[] spriteTricksterFrames;
    private BufferedImage[] spriteDefeatFrames;
    private BufferedImage[] spriteEntranceFrames;

    private static final int SPRITE_IDLE_COUNT = 5;
    private static final int SPRITE_HURT_COUNT      = 5;
    private static final int SPRITE_TRICKSTER_COUNT = 5;
    private static final int SPRITE_DEFEAT_COUNT    = 5;
    private static final int SPRITE_ENTRANCE_COUNT  = 5;
    private static final int SPRITE_ENTRANCE_SPEED  = 3000; // ADD THIS

    private boolean isPlayingSpriteHurt      = false;
    private boolean isPlayingSpriteTrickster = false;
    private boolean isPlayingSpriteDefeat    = false;
    private boolean isPlayingSpriteEntrance  = false;

    // Hollow Stag
    private BufferedImage[] stagIdleFrames;
    private BufferedImage[] stagHurtFrames;
    private BufferedImage[] stagDeathlyChargeFrames;
    private BufferedImage[] stagBlackenedHowlFrames;
    private BufferedImage[] stagEntranceFrames;

    private static final int STAG_IDLE_FRAME_COUNT     = 5;
    private static final int STAG_HURT_FRAME_COUNT      = 5;
    private static final int STAG_CHARGE_FRAME_COUNT    = 5;
    private static final int STAG_HOWL_FRAME_COUNT      = 5;
    private static final int STAG_ENTRANCE_FRAME_COUNT  = 5;
    private static final int STAG_SPEED                 = 250; // slower than wolf (170)
    private static final int STAG_ENTRANCE_START_X      = 850;
    private static final int STAG_X                     = ENEMY_X - 120;
    private static final int STAG_Y                     = ENEMY_Y - 170;

    private boolean isPlayingStagHurt        = false;
    private boolean isPlayingStagCharge      = false;
    private boolean isPlayingStagHowl        = false;
    private boolean isPlayingStagDefeat      = false;
    private boolean isPlayingStagEntrance    = false;

    // ════════════════════════════════════════════
    // ★ POSITION CONSTANTS
    // ════════════════════════════════════════════
    private static final int IDLE_X        = 200;
    private static final int IDLE_Y        = 340;
    private static final int ACTION_X_BASE = 210;
    private static final int ACTION_Y      = 280;

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

    public Combatant getCurrentHero() { return currentHero; }

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
        setActionsEnabled(false);
        animating = true;
        postVictoryStep = PostVictoryStep.NONE;
        setLogFontNormal();
        if (battleContinueBtn != null) battleContinueBtn.setEnabled(false);
        setTurnLabel(true);
        startHeroIdleAnimation();

        boolean isStagFight = eDef.name.equals("The Hollow Stag");

        if (isStagFight) {
            JLabel minibossLabel = new JLabel("MINIBOSS ENCOUNTER : THE HOLLOW STAG", SwingConstants.CENTER);
            minibossLabel.setBounds(0, 220, 1280, 50);
            minibossLabel.setForeground(new Color(220, 20, 20));
            minibossLabel.setFont(new Font("Serif", Font.BOLD | Font.ITALIC, 26));
            add(minibossLabel);
            setComponentZOrder(minibossLabel, 0);
            repaint();

            delay(2500, () -> {
                // fade out
                float[] alpha = {1.0f};
                javax.swing.Timer fadeOut = new javax.swing.Timer(16, null);
                fadeOut.addActionListener(ev -> {
                    alpha[0] = Math.max(0f, alpha[0] - 0.03f);
                    minibossLabel.setForeground(new Color(220/255f, 20/255f, 20/255f, alpha[0]));
                    if (alpha[0] <= 0f) {
                        fadeOut.stop();
                        remove(minibossLabel);
                        repaint();
                        playEnemyEntrance(() -> {
                            setActionsEnabled(true);
                            animating = false;
                        });
                    }
                });
                fadeOut.start();
            });
        } else {
            playEnemyEntrance(() -> {
                setActionsEnabled(true);
                animating = false;
            });
        }
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

        heroSpriteLabel = buildHeroSpriteLabel();
        add(heroSpriteLabel);
        enemySpriteLabel = buildEnemySpriteLabel();
        add(enemySpriteLabel);

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

        // Updated sizes for a rounder, balanced button!
        int btnY = 453, btnW = 78, btnH = 78, btnGap = 24;
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
        setComponentZOrder(heroSpriteLabel, 16);
        setComponentZOrder(enemySpriteLabel, 17);
        setComponentZOrder(battleBg, 18);
        setComponentZOrder(theBg, 19);
    }

    // ════════════════════════════════════════════════════════════════════════
    // ★ DYNAMIC IMAGE BUTTON CONFIGURATOR
    // ════════════════════════════════════════════════════════════════════════
    private void configureSkillButton(JButton btn, String skillName, String iconText, Color bg, Color border) {
        String normalPath = null, hoverPath = null, disabledPath = null;

        if (skillName.equals("Blade Rush")) {
            normalPath = "/assets/KaelAssets/BladeRush.png";
            hoverPath = "/assets/KaelAssets/BladeRushHovered.png";
        } else if (skillName.equals("Piercing Slash")) {
            normalPath = "/assets/KaelAssets/PiercingSlash.png";
            hoverPath = "/assets/KaelAssets/PiercingSlashHovered.png";
        } else if (skillName.equals("Eternal Cross Slash")) {
            normalPath = "/assets/KaelAssets/EternalCrossSlash.png";
            hoverPath = "/assets/KaelAssets/EternalCrossSlashHovered.png";
            disabledPath = null;
        } else if (skillName.equals("Skip Turn")) {
            normalPath = "/assets/KaelAssets/SkipTurn.png";
            hoverPath  = "/assets/KaelAssets/SkipTurnHovered.png";
        }

        java.net.URL nUrl = (normalPath != null) ? getClass().getResource(normalPath) : null;

        if (nUrl != null) {
            btn.setText("");
            btn.setContentAreaFilled(false);
            btn.setBorderPainted(false);
            btn.setOpaque(false);

            try {
                btn.setIcon(new ImageIcon(new ImageIcon(nUrl).getImage().getScaledInstance(73, 73, Image.SCALE_SMOOTH)));
                java.net.URL hUrl = null;
                if (hoverPath != null) {
                    hUrl = getClass().getResource(hoverPath);
                    if (hUrl != null)
                        btn.setRolloverIcon(new ImageIcon(new ImageIcon(hUrl).getImage().getScaledInstance(78, 78, Image.SCALE_SMOOTH)));
                } else
                    btn.setRolloverIcon(new ImageIcon(new ImageIcon(hUrl).getImage().getScaledInstance(73, 73, Image.SCALE_SMOOTH)));

                if (disabledPath != null) {
                    java.net.URL dUrl = getClass().getResource(disabledPath);
                    if (dUrl != null)
                        btn.setDisabledIcon(new ImageIcon(new ImageIcon(dUrl).getImage().getScaledInstance(150, 65, Image.SCALE_SMOOTH)));
                } else btn.setDisabledIcon(null);
            } catch (Exception e) {
                System.out.println("Error loading image for " + skillName);
            }
        } else {
            btn.setIcon(null);
            btn.setRolloverIcon(null);
            try {
                java.awt.image.BufferedImage base = ImageIO.read(nUrl);
                java.awt.image.BufferedImage out = new java.awt.image.BufferedImage(73, 73, java.awt.image.BufferedImage.TYPE_INT_ARGB);
                Graphics2D gx = out.createGraphics();
                gx.drawImage(base, 0, 0, 73, 73, null);
                gx.setColor(new Color(255, 255, 255, 140));
                gx.fillRect(0, 0, 150, 65);
                gx.dispose();
                btn.setDisabledIcon(new ImageIcon(out));
            } catch (Exception ex) { btn.setDisabledIcon(null); }
            btn.setContentAreaFilled(true);
            btn.setBorderPainted(true);
            btn.setOpaque(true);
            btn.setBackground(bg);
            btn.setText((iconText != null && !iconText.isEmpty() ? iconText + " " : "") + skillName);
            btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(border, 2),
                    BorderFactory.createEmptyBorder(6, 8, 6, 8)));
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // ★ SPRITE LOADERS
    // ════════════════════════════════════════════════════════════════════════
    private JLabel buildHeroSpriteLabel() {
        try {
            java.net.URL url = getClass().getResource("/assets/KaelAssets/KaelIdle.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / SPRITE_FRAME_COUNT, fh = sheet.getHeight();
                idleFrames = new BufferedImage[SPRITE_FRAME_COUNT];
                for (int i = 0; i < SPRITE_FRAME_COUNT; i++) idleFrames[i] = removeBlackBg(sheet.getSubimage(i * fw, 0, fw, fh));
            }
        } catch (Exception ex) {}

        try {
            java.net.URL url = getClass().getResource("/assets/KaelAssets/KaelBladeRush.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / BLADE_RUSH_FRAME_COUNT, fh = sheet.getHeight();
                bladeRushFrames = new BufferedImage[BLADE_RUSH_FRAME_COUNT];
                for (int i = 0; i < BLADE_RUSH_FRAME_COUNT; i++) bladeRushFrames[i] = removeBlackBg(sheet.getSubimage(i * fw, 0, fw, fh));
            }
        } catch (Exception ex) {}

        try {
            java.net.URL url = getClass().getResource("/assets/KaelAssets/KaelPiercingSlash.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / PIERCING_SLASH_FRAME_COUNT, fh = sheet.getHeight();
                piercingSlashFrames = new BufferedImage[PIERCING_SLASH_FRAME_COUNT];
                for (int i = 0; i < PIERCING_SLASH_FRAME_COUNT; i++) piercingSlashFrames[i] = removeBlackBg(sheet.getSubimage(i * fw, 0, fw, fh));
            }
        } catch (Exception ex) {}

        try {
            java.net.URL url = getClass().getResource("/assets/KaelAssets/KaelEternalCrossSlash.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / ETERNAL_CROSS_FRAME_COUNT, fh = sheet.getHeight();
                eternalCrossFrames = new BufferedImage[ETERNAL_CROSS_FRAME_COUNT];
                for (int i = 0; i < ETERNAL_CROSS_FRAME_COUNT; i++) eternalCrossFrames[i] = removeBlackBg(sheet.getSubimage(i * fw, 0, fw, fh));
            }
        } catch (Exception ex) {}

        try {
            java.net.URL url = getClass().getResource("/assets/KaelAssets/KaelHurt.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / KAEL_HURT_FRAME_COUNT, fh = sheet.getHeight();
                kaelHurtFrames = new BufferedImage[KAEL_HURT_FRAME_COUNT];
                for (int i = 0; i < KAEL_HURT_FRAME_COUNT; i++) kaelHurtFrames[i] = removeBlackBg(sheet.getSubimage(i * fw, 0, fw, fh));
            }
        } catch (Exception ex) {}

        JLabel sprite = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                BufferedImage[] frames =
                        isPlayingBladeRush     ? bladeRushFrames :
                                isPlayingPiercingSlash ? piercingSlashFrames :
                                        isPlayingEternalCross  ? eternalCrossFrames :
                                                isPlayingKaelHurt      ? kaelHurtFrames : idleFrames;
                if (frames == null || heroSpriteFrame >= frames.length) return;
                BufferedImage frame = frames[heroSpriteFrame];
                if (frame == null) return;
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON);
                g2.drawImage(frame, 0, 0, (int)(frame.getWidth() * SPRITE_SCALE), (int)(frame.getHeight() * SPRITE_SCALE), null);
                g2.dispose();

                boolean isShade = enemyDef != null && enemyDef.name.equals("Shade Sprite");
                boolean isStag  = enemyDef != null && enemyDef.name.equals("The Hollow Stag");

                if      (isPlayingWolfHurt)        frames = wolfHurtFrames;
                else if (isPlayingWolfSavageHowl)  frames = wolfSavageHowlFrames;
                else if (isPlayingWolfDefeat)      frames = wolfDefeatFrames;
                else if (isPlayingWolfEntrance)    frames = wolfEntranceFrames;
                else if (isPlayingSpriteHurt)      frames = spriteHurtFrames;
                else if (isPlayingSpriteTrickster) frames = spriteTricksterFrames;
                else if (isPlayingSpriteDefeat)    frames = spriteDefeatFrames;
                else if (isPlayingSpriteEntrance)  frames = spriteEntranceFrames;
                else if (isPlayingStagHurt)        frames = stagHurtFrames;
                else if (isPlayingStagCharge)      frames = stagDeathlyChargeFrames;
                else if (isPlayingStagHowl)        frames = stagBlackenedHowlFrames;
                else if (isPlayingStagEntrance)    frames = stagEntranceFrames;
                else if (isStag)                   frames = stagIdleFrames;
                else if (isShade)                  frames = spriteIdleFrames;
                else                               frames = wolfIdleFrames;

                double scale = (isShade || isStag) ? SPRITE_SCALE : ENEMY_SCALE;
            }
        };

        int labelW = idleFrames != null ? (int)(idleFrames[0].getWidth()  * SPRITE_SCALE) : SPRITE_W;
        int labelH = idleFrames != null ? (int)(idleFrames[0].getHeight() * SPRITE_SCALE) : SPRITE_H;
        sprite.setBounds(IDLE_X, IDLE_Y, labelW, labelH);
        sprite.setOpaque(false);
        return sprite;
    }

    private JLabel buildEnemySpriteLabel() {
        try {
            java.net.URL url = getClass().getResource("/assets/World1EnemyAssets/RodtfangWolfIdle.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / WOLF_FRAME_COUNT, fh = sheet.getHeight();
                wolfIdleFrames = new BufferedImage[WOLF_FRAME_COUNT];
                for (int i = 0; i < WOLF_FRAME_COUNT; i++) wolfIdleFrames[i] = removeBlackBg(sheet.getSubimage(i * fw, 0, fw, fh));
            }
        } catch (Exception ex) {}

        try {
            java.net.URL url = getClass().getResource("/assets/World1EnemyAssets/RodtfangWolfHurt.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / WOLF_HURT_FRAME_COUNT, fh = sheet.getHeight();
                wolfHurtFrames = new BufferedImage[WOLF_HURT_FRAME_COUNT];
                for (int i = 0; i < WOLF_HURT_FRAME_COUNT; i++) wolfHurtFrames[i] = removeBlackBg(sheet.getSubimage(i * fw, 0, fw, fh));
            }
        } catch (Exception ex) {}

        try {
            java.net.URL url = getClass().getResource("/assets/World1EnemyAssets/RodtfangWolfSavageHowl.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / WOLF_HOWL_FRAME_COUNT, fh = sheet.getHeight();
                wolfSavageHowlFrames = new BufferedImage[WOLF_HOWL_FRAME_COUNT];
                for (int i = 0; i < WOLF_HOWL_FRAME_COUNT; i++) wolfSavageHowlFrames[i] = removeBlackBg(sheet.getSubimage(i * fw, 0, fw, fh));
            }
        } catch (Exception ex) {}

        try {
            java.net.URL url = getClass().getResource("/assets/World1EnemyAssets/RodtfangWolfDefeat.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / WOLF_DEFEAT_FRAME_COUNT, fh = sheet.getHeight();
                wolfDefeatFrames = new BufferedImage[WOLF_DEFEAT_FRAME_COUNT];
                for (int i = 0; i < WOLF_DEFEAT_FRAME_COUNT; i++) wolfDefeatFrames[i] = removeBlackBg(sheet.getSubimage(i * fw, 0, fw, fh));
            }
        } catch (Exception ex) {}

        try {
            java.net.URL url = getClass().getResource("/assets/World1EnemyAssets/RodtfangWolfEntrance.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / WOLF_ENTRANCE_FRAME_COUNT, fh = sheet.getHeight();
                wolfEntranceFrames = new BufferedImage[WOLF_ENTRANCE_FRAME_COUNT];
                for (int i = 0; i < WOLF_ENTRANCE_FRAME_COUNT; i++) wolfEntranceFrames[i] = removeBlackBg(sheet.getSubimage(i * fw, 0, fw, fh));
            }
        } catch (Exception ex) {}

        try {
            java.net.URL url = getClass().getResource("/assets/World1EnemyAssets/ShadeSpriteIdle.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / SPRITE_IDLE_COUNT, fh = sheet.getHeight();
                spriteIdleFrames = new BufferedImage[SPRITE_IDLE_COUNT];
                for (int i = 0; i < SPRITE_IDLE_COUNT; i++) spriteIdleFrames[i] = removeBlackBg(sheet.getSubimage(i * fw, 0, fw, fh));
            }
        } catch (Exception ex) {}

        try {
            java.net.URL url = getClass().getResource("/assets/World1EnemyAssets/ShadeSpriteHurt.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / SPRITE_HURT_COUNT, fh = sheet.getHeight();
                spriteHurtFrames = new BufferedImage[SPRITE_HURT_COUNT];
                for (int i = 0; i < SPRITE_HURT_COUNT; i++) spriteHurtFrames[i] = removeBlackBg(sheet.getSubimage(i * fw, 0, fw, fh));
            }
        } catch (Exception ex) {}

        try {
            java.net.URL url = getClass().getResource("/assets/World1EnemyAssets/ShadeSpriteTricksterStrike.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / SPRITE_TRICKSTER_COUNT, fh = sheet.getHeight();
                spriteTricksterFrames = new BufferedImage[SPRITE_TRICKSTER_COUNT];
                for (int i = 0; i < SPRITE_TRICKSTER_COUNT; i++) spriteTricksterFrames[i] = removeBlackBg(sheet.getSubimage(i * fw, 0, fw, fh));
            }
        } catch (Exception ex) {}

        try {
            java.net.URL url = getClass().getResource("/assets/World1EnemyAssets/ShadeSpriteDefeat.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / SPRITE_DEFEAT_COUNT, fh = sheet.getHeight();
                spriteDefeatFrames = new BufferedImage[SPRITE_DEFEAT_COUNT];
                for (int i = 0; i < SPRITE_DEFEAT_COUNT; i++) spriteDefeatFrames[i] = removeBlackBg(sheet.getSubimage(i * fw, 0, fw, fh));
            }
        } catch (Exception ex) {}

        try {
            java.net.URL url = getClass().getResource("/assets/World1EnemyAssets/ShadeSpriteEntrance.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / SPRITE_ENTRANCE_COUNT, fh = sheet.getHeight();
                spriteEntranceFrames = new BufferedImage[SPRITE_ENTRANCE_COUNT];
                for (int i = 0; i < SPRITE_ENTRANCE_COUNT; i++) spriteEntranceFrames[i] = removeBlackBg(sheet.getSubimage(i * fw, 0, fw, fh));
            }
        } catch (Exception ex) {}

        try {
            java.net.URL url = getClass().getResource("/assets/World1EnemyAssets/HollowStagEntrance.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / STAG_ENTRANCE_FRAME_COUNT, fh = sheet.getHeight();
                stagEntranceFrames = new BufferedImage[STAG_ENTRANCE_FRAME_COUNT];
                for (int i = 0; i < STAG_ENTRANCE_FRAME_COUNT; i++) stagEntranceFrames[i] = removeBlackBg(sheet.getSubimage(i * fw, 0, fw, fh));
            }
        } catch (Exception ex) {}

        try {
            java.net.URL url = getClass().getResource("/assets/World1EnemyAssets/HollowStagIdle.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / STAG_IDLE_FRAME_COUNT, fh = sheet.getHeight();
                stagIdleFrames = new BufferedImage[STAG_IDLE_FRAME_COUNT];
                for (int i = 0; i < STAG_IDLE_FRAME_COUNT; i++) stagIdleFrames[i] = removeBlackBg(sheet.getSubimage(i * fw, 0, fw, fh));
            }
        } catch (Exception ex) {}

        try {
            java.net.URL url = getClass().getResource("/assets/World1EnemyAssets/HollowStagHurt.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / STAG_HURT_FRAME_COUNT, fh = sheet.getHeight();
                stagHurtFrames = new BufferedImage[STAG_HURT_FRAME_COUNT];
                for (int i = 0; i < STAG_HURT_FRAME_COUNT; i++) stagHurtFrames[i] = removeBlackBg(sheet.getSubimage(i * fw, 0, fw, fh));
            }
        } catch (Exception ex) {}

        try {
            java.net.URL url = getClass().getResource("/assets/World1EnemyAssets/HollowStagDeathlyCharge.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / STAG_CHARGE_FRAME_COUNT, fh = sheet.getHeight();
                stagDeathlyChargeFrames = new BufferedImage[STAG_CHARGE_FRAME_COUNT];
                for (int i = 0; i < STAG_CHARGE_FRAME_COUNT; i++) stagDeathlyChargeFrames[i] = removeBlackBg(sheet.getSubimage(i * fw, 0, fw, fh));
            }
        } catch (Exception ex) {}

        try {
            java.net.URL url = getClass().getResource("/assets/World1EnemyAssets/HollowStagBlackenedHowl.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / STAG_HOWL_FRAME_COUNT, fh = sheet.getHeight();
                stagBlackenedHowlFrames = new BufferedImage[STAG_HOWL_FRAME_COUNT];
                for (int i = 0; i < STAG_HOWL_FRAME_COUNT; i++) stagBlackenedHowlFrames[i] = removeBlackBg(sheet.getSubimage(i * fw, 0, fw, fh));
            }
        } catch (Exception ex) {}

        JLabel sprite = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                BufferedImage[] frames;
                boolean isShade = enemyDef != null && enemyDef.name.equals("Shade Sprite");
                boolean isStag  = enemyDef != null && enemyDef.name.equals("The Hollow Stag");

                if      (isPlayingWolfHurt)        frames = wolfHurtFrames;
                else if (isPlayingWolfSavageHowl)  frames = wolfSavageHowlFrames;
                else if (isPlayingWolfDefeat)      frames = wolfDefeatFrames;
                else if (isPlayingWolfEntrance)    frames = wolfEntranceFrames;
                else if (isPlayingSpriteHurt)      frames = spriteHurtFrames;
                else if (isPlayingSpriteTrickster) frames = spriteTricksterFrames;
                else if (isPlayingSpriteDefeat)    frames = spriteDefeatFrames;
                else if (isPlayingSpriteEntrance)  frames = spriteEntranceFrames;
                else if (isPlayingStagHurt)        frames = stagHurtFrames;
                else if (isPlayingStagCharge)      frames = stagDeathlyChargeFrames;
                else if (isPlayingStagHowl)        frames = stagBlackenedHowlFrames;
                else if (isPlayingStagEntrance)    frames = stagEntranceFrames;
                else if (isPlayingStagDefeat)      frames = stagHurtFrames;
                else if (isStag)                   frames = stagIdleFrames;
                else if (isShade)                  frames = spriteIdleFrames;
                else                               frames = wolfIdleFrames;

                if (frames == null || enemySpriteFrame >= frames.length) return;
                BufferedImage frame = frames[enemySpriteFrame];
                if (frame == null) return;
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON);
                double scale = ENEMY_SCALE;
                g2.drawImage(frame, 0, 0, (int)(frame.getWidth() * scale), (int)(frame.getHeight() * scale), null);
                g2.dispose();
            }
        };

        // Size the label to the largest frame across all enemies so nothing overflows
        int labelW = 0, labelH = 0;
        if (wolfIdleFrames != null) {
            labelW = Math.max(labelW, (int)(wolfIdleFrames[0].getWidth()  * ENEMY_SCALE));
            labelH = Math.max(labelH, (int)(wolfIdleFrames[0].getHeight() * ENEMY_SCALE));
        }
        if (spriteIdleFrames != null) {
            labelW = Math.max(labelW, (int)(spriteIdleFrames[0].getWidth()  * ENEMY_SCALE));
            labelH = Math.max(labelH, (int)(spriteIdleFrames[0].getHeight() * ENEMY_SCALE));
        }
        if (stagIdleFrames != null) {
            labelW = Math.max(labelW, (int)(stagIdleFrames[0].getWidth()  * ENEMY_SCALE));
            labelH = Math.max(labelH, (int)(stagIdleFrames[0].getHeight() * ENEMY_SCALE));
        }
        if (labelW == 0) labelW = SPRITE_W;
        if (labelH == 0) labelH = SPRITE_H;

        sprite.setBounds(ENEMY_X, ENEMY_Y, labelW, labelH);
        sprite.setOpaque(false);
        sprite.setVisible(false);
        return sprite;
    }

    private BufferedImage removeBlackBg(BufferedImage src) {
        BufferedImage out = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int x = 0; x < src.getWidth(); x++)
            for (int y = 0; y < src.getHeight(); y++) {
                int px = src.getRGB(x, y);
                int r = (px >> 16) & 0xFF;
                int g = (px >> 8)  & 0xFF;
                int b =  px        & 0xFF;
                // Only remove pixels that are very close to pure black
                // AND are near the border or surrounded by other black pixels
                boolean isPureBlack = r < 15 && g < 15 && b < 15;
                out.setRGB(x, y, isPureBlack ? 0x00000000 : px);
            }
        return out;
    }

    // ════════════════════════════════════════════════════════════════════════
    // ★ ANIMATION TRIGGERS
    // ════════════════════════════════════════════════════════════════════════
    private void startHeroIdleAnimation() {
        if (heroIdleTimer != null && heroIdleTimer.isRunning()) heroIdleTimer.stop();
        isPlayingBladeRush = false; isPlayingPiercingSlash = false;
        isPlayingEternalCross = false; isPlayingKaelHurt = false;
        heroSpriteFrame = 0;
        if (idleFrames != null) {
            int w = (int)(idleFrames[0].getWidth()  * SPRITE_SCALE);
            int h = (int)(idleFrames[0].getHeight() * SPRITE_SCALE);
            heroSpriteLabel.setBounds(IDLE_X, IDLE_Y, w, h);
        }
        heroIdleTimer = new javax.swing.Timer(220, e -> {
            heroSpriteFrame = (heroSpriteFrame + 1) % SPRITE_FRAME_COUNT;
            if (heroSpriteLabel != null) heroSpriteLabel.repaint();
        });
        heroIdleTimer.start();
    }

    private void startEnemyIdleAnimation(EnemyDefinition eDef) {
        if (enemyIdleTimer != null && enemyIdleTimer.isRunning()) enemyIdleTimer.stop();
        isPlayingWolfHurt = false; isPlayingWolfSavageHowl = false;
        isPlayingWolfDefeat = false; isPlayingWolfEntrance = false;
        isPlayingSpriteHurt = false; isPlayingSpriteTrickster = false;
        isPlayingSpriteDefeat = false; isPlayingSpriteEntrance = false;
        isPlayingStagHurt = false; isPlayingStagCharge = false;
        isPlayingStagHowl = false; isPlayingStagDefeat = false; isPlayingStagEntrance = false;
        enemySpriteFrame = 0;

        if (eDef.name.equals("Rotfang Wolf") && wolfIdleFrames != null) {
            int w = (int)(wolfIdleFrames[0].getWidth()  * ENEMY_SCALE);
            int h = (int)(wolfIdleFrames[0].getHeight() * ENEMY_SCALE);
            enemySpriteLabel.setBounds(ENEMY_X, ENEMY_Y, w, h);
            enemySpriteLabel.setVisible(true);
            enemyIdleTimer = new javax.swing.Timer(WOLF_SPEED, e -> {
                enemySpriteFrame = (enemySpriteFrame + 1) % WOLF_FRAME_COUNT;
                if (enemySpriteLabel != null) enemySpriteLabel.repaint();
            });
            enemyIdleTimer.start();
        } else if (eDef.name.equals("Shade Sprite") && spriteIdleFrames != null) {
            int w = (int)(spriteIdleFrames[0].getWidth()  * ENEMY_SCALE);
            int h = (int)(spriteIdleFrames[0].getHeight() * ENEMY_SCALE);
            enemySpriteLabel.setBounds(SHADE_X, SHADE_Y, w, h);
            enemySpriteLabel.setVisible(true);
            enemyIdleTimer = new javax.swing.Timer(220, e -> {
                enemySpriteFrame = (enemySpriteFrame + 1) % SPRITE_IDLE_COUNT;
                if (enemySpriteLabel != null) enemySpriteLabel.repaint();
            });
            enemyIdleTimer.start();
        } else if (eDef.name.equals("The Hollow Stag") && stagIdleFrames != null) {
            int w = (int)(stagIdleFrames[0].getWidth()  * ENEMY_SCALE);
            int h = (int)(stagIdleFrames[0].getHeight() * ENEMY_SCALE);
            enemySpriteLabel.setBounds(STAG_X, STAG_Y, w, h);
            enemySpriteLabel.setVisible(true);
            enemyIdleTimer = new javax.swing.Timer(STAG_SPEED, e -> {
                enemySpriteFrame = (enemySpriteFrame + 1) % STAG_IDLE_FRAME_COUNT;
                if (enemySpriteLabel != null) enemySpriteLabel.repaint();
            });
            enemyIdleTimer.start();
        } else {
            enemySpriteLabel.setVisible(false);
        }
    }

    private void stopEnemyAnimation() {
        if (enemyIdleTimer != null && enemyIdleTimer.isRunning()) enemyIdleTimer.stop();
        if (enemySpriteLabel != null) enemySpriteLabel.setVisible(false);
    }

    private boolean isShadeSprite() { return enemyDef != null && enemyDef.name.equals("Shade Sprite"); }

    private boolean isStag() { return enemyDef != null && enemyDef.name.equals("The Hollow Stag"); }

    private void playEnemyHurt(Runnable onDone) {
        if (isStag())        playStagHurtAnimation(onDone);
        else if (isShadeSprite()) playSpriteHurtAnimation(onDone);
        else                 playWolfHurtAnimation(onDone);
    }

    private void playEnemyAttack(Runnable onDone) {
        if (isStag()) {
            String lastSkill = (engine != null) ? engine.getLastEnemySkillName() : null;
            if ("Blackened Howl".equals(lastSkill)) playStagBlackenedHowlAnimation(onDone);
            else                                     playStagDeathlyChargeAnimation(onDone);
        } else if (isShadeSprite()) playSpriteTricksterAnimation(onDone);
        else                        playWolfSavageHowlAnimation(onDone);
    }

    private void playEnemyDefeat(Runnable onDone) {
        if (isStag())             playStagDefeatAnimation(onDone);
        else if (isShadeSprite()) playSpriteDefeatAnimation(onDone);
        else                      playWolfDefeatAnimation(onDone);
    }

    private void playEnemyEntrance(Runnable onDone) {
        if (isStag())             playStagEntranceAnimation(onDone);
        else if (isShadeSprite()) playSpriteEntranceAnimation(onDone);
        else                      playWolfEntranceAnimation(onDone);
    }


    // WOLF
    private void playWolfDefeatAnimation(Runnable onDone) {
        if (enemyIdleTimer != null && enemyIdleTimer.isRunning()) enemyIdleTimer.stop();
        if (wolfDefeatFrames == null) { enemySpriteLabel.setVisible(false); if (onDone != null) onDone.run(); return; }
        isPlayingWolfDefeat = true; enemySpriteFrame = 0;
        int w = (int)(wolfDefeatFrames[0].getWidth()  * ENEMY_SCALE), h = (int)(wolfDefeatFrames[0].getHeight() * ENEMY_SCALE);
        enemySpriteLabel.setBounds(ENEMY_X, ENEMY_Y, w, h); enemySpriteLabel.repaint();
        int[] frame = {0};
        javax.swing.Timer t = new javax.swing.Timer(300, e -> {
            if (frame[0] < WOLF_DEFEAT_FRAME_COUNT) { enemySpriteFrame = frame[0]++; enemySpriteLabel.repaint(); }
            else { ((javax.swing.Timer)e.getSource()).stop(); delay(400, () -> { isPlayingWolfDefeat = false; enemySpriteLabel.setVisible(false); if (onDone != null) onDone.run(); }); }
        });
        t.start();
    }

    private void playWolfEntranceAnimation(Runnable onDone) {
        if (wolfEntranceFrames == null) { if (enemyDef != null) startEnemyIdleAnimation(enemyDef); if (onDone != null) onDone.run(); return; }
        isPlayingWolfEntrance = true; enemySpriteFrame = 0;
        int w = (int)(wolfEntranceFrames[0].getWidth()  * ENEMY_SCALE), h = (int)(wolfEntranceFrames[0].getHeight() * ENEMY_SCALE);
        enemySpriteLabel.setBounds(WOLF_ENTRANCE_START_X, ENEMY_Y, w, h); enemySpriteLabel.setVisible(true); enemySpriteLabel.repaint();
        javax.swing.Timer frameTimer = new javax.swing.Timer(120, e -> { enemySpriteFrame = (enemySpriteFrame + 1) % WOLF_ENTRANCE_FRAME_COUNT; enemySpriteLabel.repaint(); });
        frameTimer.start();
        int[] currentX = {WOLF_ENTRANCE_START_X};
        javax.swing.Timer slideTimer = new javax.swing.Timer(16, e -> {
            currentX[0] -= 10;
            if (currentX[0] <= ENEMY_X) {
                enemySpriteLabel.setLocation(ENEMY_X, ENEMY_Y);
                ((javax.swing.Timer)e.getSource()).stop(); frameTimer.stop();
                isPlayingWolfEntrance = false;
                if (enemyDef != null) startEnemyIdleAnimation(enemyDef);
                if (onDone != null) onDone.run();
            } else { enemySpriteLabel.setLocation(currentX[0], ENEMY_Y); }
        });
        slideTimer.start();
    }

    private void playWolfHurtAnimation(Runnable onDone) {
        if (enemyIdleTimer != null && enemyIdleTimer.isRunning()) enemyIdleTimer.stop();
        if (wolfHurtFrames == null || !enemySpriteLabel.isVisible()) { if (onDone != null) onDone.run(); return; }
        isPlayingWolfHurt = true; enemySpriteFrame = 0;
        int w = (int)(wolfHurtFrames[0].getWidth()  * ENEMY_SCALE), h = (int)(wolfHurtFrames[0].getHeight() * ENEMY_SCALE);
        enemySpriteLabel.setBounds(ENEMY_X, ENEMY_Y, w, h); enemySpriteLabel.repaint();
        int[] frame = {0};
        javax.swing.Timer t = new javax.swing.Timer(170, e -> {
            if (frame[0] < WOLF_HURT_FRAME_COUNT) { enemySpriteFrame = frame[0]++; enemySpriteLabel.repaint(); }
            else { ((javax.swing.Timer)e.getSource()).stop(); isPlayingWolfHurt = false; if (enemyDef != null) startEnemyIdleAnimation(enemyDef); if (onDone != null) onDone.run(); }
        });
        t.start();
    }

    private void playWolfSavageHowlAnimation(Runnable onDone) {
        if (enemyIdleTimer != null && enemyIdleTimer.isRunning()) enemyIdleTimer.stop();
        if (wolfSavageHowlFrames == null || !enemySpriteLabel.isVisible()) { if (onDone != null) onDone.run(); return; }
        isPlayingWolfSavageHowl = true; enemySpriteFrame = 0;
        int w = (int)(wolfSavageHowlFrames[0].getWidth()  * ENEMY_SCALE), h = (int)(wolfSavageHowlFrames[0].getHeight() * ENEMY_SCALE);
        enemySpriteLabel.setBounds(ENEMY_X, ENEMY_Y, w, h); enemySpriteLabel.repaint();
        int[] frame = {0};
        javax.swing.Timer t = new javax.swing.Timer(170, e -> {
            if (frame[0] < WOLF_HOWL_FRAME_COUNT) { enemySpriteFrame = frame[0]++; enemySpriteLabel.repaint(); }
            else { ((javax.swing.Timer)e.getSource()).stop(); isPlayingWolfSavageHowl = false; if (enemyDef != null) startEnemyIdleAnimation(enemyDef); if (onDone != null) onDone.run(); }
        });
        t.start();
    }

    // SPRITE
    private void playSpriteHurtAnimation(Runnable onDone) {
        if (enemyIdleTimer != null && enemyIdleTimer.isRunning()) enemyIdleTimer.stop();
        if (spriteHurtFrames == null || !enemySpriteLabel.isVisible()) { if (onDone != null) onDone.run(); return; }
        isPlayingSpriteHurt = true; enemySpriteFrame = 0;
        int w = (int)(spriteHurtFrames[0].getWidth()  * ENEMY_SCALE);
        int h = (int)(spriteHurtFrames[0].getHeight() * ENEMY_SCALE);
        enemySpriteLabel.setBounds(SHADE_X, SHADE_Y, w, h); enemySpriteLabel.repaint();
        int[] frame = {0};
        javax.swing.Timer t = new javax.swing.Timer(170, e -> {
            if (frame[0] < SPRITE_HURT_COUNT) { enemySpriteFrame = frame[0]++; enemySpriteLabel.repaint(); }
            else { ((javax.swing.Timer)e.getSource()).stop(); isPlayingSpriteHurt = false; if (enemyDef != null) startEnemyIdleAnimation(enemyDef); if (onDone != null) onDone.run(); }
        });
        t.start();
    }

    private void playSpriteTricksterAnimation(Runnable onDone) {
        if (enemyIdleTimer != null && enemyIdleTimer.isRunning()) enemyIdleTimer.stop();
        if (spriteTricksterFrames == null || !enemySpriteLabel.isVisible()) { if (onDone != null) onDone.run(); return; }
        isPlayingSpriteTrickster = true; enemySpriteFrame = 0;
        int w = (int)(spriteTricksterFrames[0].getWidth()  * ENEMY_SCALE);
        int h = (int)(spriteTricksterFrames[0].getHeight() * ENEMY_SCALE);
        enemySpriteLabel.setBounds(SHADE_X - 20, SHADE_Y - 15, w, h); enemySpriteLabel.repaint();
        int[] frame = {0};
        javax.swing.Timer t = new javax.swing.Timer(170, e -> {
            if (frame[0] < SPRITE_TRICKSTER_COUNT) { enemySpriteFrame = frame[0]++; enemySpriteLabel.repaint(); }
            else { ((javax.swing.Timer)e.getSource()).stop(); isPlayingSpriteTrickster = false; if (enemyDef != null) startEnemyIdleAnimation(enemyDef); if (onDone != null) onDone.run(); }
        });
        t.start();
    }

    private void playSpriteDefeatAnimation(Runnable onDone) {
        if (enemyIdleTimer != null && enemyIdleTimer.isRunning()) enemyIdleTimer.stop();
        if (spriteDefeatFrames == null) { enemySpriteLabel.setVisible(false); if (onDone != null) onDone.run(); return; }
        isPlayingSpriteDefeat = true; enemySpriteFrame = 0;
        int w = (int)(spriteDefeatFrames[0].getWidth()  * ENEMY_SCALE);
        int h = (int)(spriteDefeatFrames[0].getHeight() * ENEMY_SCALE);
        enemySpriteLabel.setBounds(SHADE_X, SHADE_Y, w, h); enemySpriteLabel.repaint();
        int[] frame = {0};
        javax.swing.Timer t = new javax.swing.Timer(300, e -> {
            if (frame[0] < SPRITE_DEFEAT_COUNT) { enemySpriteFrame = frame[0]++; enemySpriteLabel.repaint(); }
            else { ((javax.swing.Timer)e.getSource()).stop(); delay(400, () -> { isPlayingSpriteDefeat = false; enemySpriteLabel.setVisible(false); if (onDone != null) onDone.run(); }); }
        });
        t.start();
    }

    private void playSpriteEntranceAnimation(Runnable onDone) {
        if (spriteEntranceFrames == null) { if (enemyDef != null) startEnemyIdleAnimation(enemyDef); if (onDone != null) onDone.run(); return; }
        isPlayingSpriteEntrance = true; enemySpriteFrame = 0;
        int w = (int)(spriteEntranceFrames[0].getWidth()  * ENEMY_SCALE);
        int h = (int)(spriteEntranceFrames[0].getHeight() * ENEMY_SCALE);
        enemySpriteLabel.setBounds(STAG_ENTRANCE_START_X, SHADE_Y, w, h);
        enemySpriteLabel.setVisible(true); enemySpriteLabel.repaint();
        javax.swing.Timer frameTimer = new javax.swing.Timer(SPRITE_ENTRANCE_SPEED, e -> {
            enemySpriteFrame = (enemySpriteFrame + 1) % SPRITE_ENTRANCE_COUNT;
            enemySpriteLabel.repaint();
        });
        frameTimer.start();
        int[] currentX = {STAG_ENTRANCE_START_X};
        javax.swing.Timer slideTimer = new javax.swing.Timer(40, e -> {
            currentX[0] -= 3;
            if (currentX[0] <= SHADE_X) {
                enemySpriteLabel.setLocation(SHADE_X, SHADE_Y);
                ((javax.swing.Timer)e.getSource()).stop(); frameTimer.stop();
                isPlayingSpriteEntrance = false;
                if (enemyDef != null) startEnemyIdleAnimation(enemyDef);
                if (onDone != null) onDone.run();
            } else { enemySpriteLabel.setLocation(currentX[0], SHADE_Y); }
        });
        slideTimer.start();
    }

    //HOLLOW STAG
    private void playStagEntranceAnimation(Runnable onDone) {
        if (stagEntranceFrames == null) { if (enemyDef != null) startEnemyIdleAnimation(enemyDef); if (onDone != null) onDone.run(); return; }
        isPlayingStagEntrance = true; enemySpriteFrame = 0;
        int w = (int)(stagEntranceFrames[0].getWidth()  * ENEMY_SCALE);
        int h = (int)(stagEntranceFrames[0].getHeight() * ENEMY_SCALE);
        enemySpriteLabel.setBounds(STAG_ENTRANCE_START_X, STAG_Y, w, h);
        enemySpriteLabel.setVisible(true); enemySpriteLabel.repaint();
        javax.swing.Timer frameTimer = new javax.swing.Timer(STAG_SPEED, e -> {
            enemySpriteFrame = (enemySpriteFrame + 1) % STAG_ENTRANCE_FRAME_COUNT;
            enemySpriteLabel.repaint();
        });
        frameTimer.start();
        int[] currentX = {STAG_ENTRANCE_START_X};
        javax.swing.Timer slideTimer = new javax.swing.Timer(40, e -> {
            currentX[0] -= 3;
            if (currentX[0] <= STAG_X) {
                enemySpriteLabel.setLocation(STAG_X, STAG_Y);
                ((javax.swing.Timer)e.getSource()).stop(); frameTimer.stop();
                isPlayingStagEntrance = false;
                if (enemyDef != null) startEnemyIdleAnimation(enemyDef);
                if (onDone != null) onDone.run();
            } else { enemySpriteLabel.setLocation(currentX[0], STAG_Y); }
        });
        slideTimer.start();
    }

    private void playStagHurtAnimation(Runnable onDone) {
        if (enemyIdleTimer != null && enemyIdleTimer.isRunning()) enemyIdleTimer.stop();
        if (stagHurtFrames == null || !enemySpriteLabel.isVisible()) { if (onDone != null) onDone.run(); return; }
        isPlayingStagHurt = true; enemySpriteFrame = 0;
        int w = (int)(stagHurtFrames[0].getWidth()  * ENEMY_SCALE);
        int h = (int)(stagHurtFrames[0].getHeight() * ENEMY_SCALE);
        enemySpriteLabel.setBounds(STAG_X, STAG_Y, w, h); enemySpriteLabel.repaint();
        int[] frame = {0};
        javax.swing.Timer t = new javax.swing.Timer(170, e -> {
            if (frame[0] < STAG_HURT_FRAME_COUNT) { enemySpriteFrame = frame[0]++; enemySpriteLabel.repaint(); }
            else { ((javax.swing.Timer)e.getSource()).stop(); isPlayingStagHurt = false; if (enemyDef != null) startEnemyIdleAnimation(enemyDef); if (onDone != null) onDone.run(); }
        });
        t.start();
    }

    private void playStagDeathlyChargeAnimation(Runnable onDone) {
        if (enemyIdleTimer != null && enemyIdleTimer.isRunning()) enemyIdleTimer.stop();
        if (stagDeathlyChargeFrames == null || !enemySpriteLabel.isVisible()) { if (onDone != null) onDone.run(); return; }
        isPlayingStagCharge = true; enemySpriteFrame = 0;
        int w = (int)(stagDeathlyChargeFrames[0].getWidth()  * ENEMY_SCALE);
        int h = (int)(stagDeathlyChargeFrames[0].getHeight() * ENEMY_SCALE);
        enemySpriteLabel.setBounds(STAG_X, STAG_Y, w, h); enemySpriteLabel.repaint();
        int[] frame = {0};
        javax.swing.Timer t = new javax.swing.Timer(170, e -> {
            if (frame[0] < STAG_CHARGE_FRAME_COUNT) { enemySpriteFrame = frame[0]++; enemySpriteLabel.repaint(); }
            else { ((javax.swing.Timer)e.getSource()).stop(); isPlayingStagCharge = false; if (enemyDef != null) startEnemyIdleAnimation(enemyDef); if (onDone != null) onDone.run(); }
        });
        t.start();
    }

    private void playStagBlackenedHowlAnimation(Runnable onDone) {
        if (enemyIdleTimer != null && enemyIdleTimer.isRunning()) enemyIdleTimer.stop();
        if (stagBlackenedHowlFrames == null || !enemySpriteLabel.isVisible()) { if (onDone != null) onDone.run(); return; }
        isPlayingStagHowl = true; enemySpriteFrame = 0;
        int w = (int)(stagBlackenedHowlFrames[0].getWidth()  * ENEMY_SCALE);
        int h = (int)(stagBlackenedHowlFrames[0].getHeight() * ENEMY_SCALE);
        enemySpriteLabel.setBounds(STAG_X, STAG_Y, w, h); enemySpriteLabel.repaint();
        int[] frame = {0};
        javax.swing.Timer t = new javax.swing.Timer(170, e -> {
            if (frame[0] < STAG_HOWL_FRAME_COUNT) { enemySpriteFrame = frame[0]++; enemySpriteLabel.repaint(); }
            else { ((javax.swing.Timer)e.getSource()).stop(); isPlayingStagHowl = false; if (enemyDef != null) startEnemyIdleAnimation(enemyDef); if (onDone != null) onDone.run(); }
        });
        t.start();
    }

    private void playStagDefeatAnimation(Runnable onDone) {
        if (enemyIdleTimer != null && enemyIdleTimer.isRunning()) enemyIdleTimer.stop();
        if (stagHurtFrames == null) { enemySpriteLabel.setVisible(false); if (onDone != null) onDone.run(); return; }
        isPlayingStagDefeat = true; enemySpriteFrame = 0;
        int w = (int)(stagHurtFrames[0].getWidth()  * ENEMY_SCALE);
        int h = (int)(stagHurtFrames[0].getHeight() * ENEMY_SCALE);
        enemySpriteLabel.setBounds(STAG_X, STAG_Y, w, h); enemySpriteLabel.repaint();
        int[] frame = {0};
        javax.swing.Timer t = new javax.swing.Timer(300, e -> {
            if (frame[0] < STAG_HURT_FRAME_COUNT) { enemySpriteFrame = frame[0]++; enemySpriteLabel.repaint(); }
            else { ((javax.swing.Timer)e.getSource()).stop(); delay(400, () -> { isPlayingStagDefeat = false; enemySpriteLabel.setVisible(false); if (onDone != null) onDone.run(); }); }
        });
        t.start();
    }

    // HERO ACTIONS
    private void playKaelHurtAnimation(Runnable onDone) {
        if (heroIdleTimer != null && heroIdleTimer.isRunning()) heroIdleTimer.stop();
        if (kaelHurtFrames == null) { startHeroIdleAnimation(); if (onDone != null) onDone.run(); return; }
        isPlayingKaelHurt = true; heroSpriteFrame = 0;
        int w = (int)(kaelHurtFrames[0].getWidth()  * SPRITE_SCALE), h = (int)(kaelHurtFrames[0].getHeight() * SPRITE_SCALE);
        heroSpriteLabel.setBounds(IDLE_X, IDLE_Y, w, h); heroSpriteLabel.repaint();
        int[] frame = {0};
        javax.swing.Timer t = new javax.swing.Timer(150, e -> {
            if (frame[0] < KAEL_HURT_FRAME_COUNT) { heroSpriteFrame = frame[0]++; heroSpriteLabel.repaint(); }
            else { ((javax.swing.Timer)e.getSource()).stop(); isPlayingKaelHurt = false; startHeroIdleAnimation(); if (onDone != null) onDone.run(); }
        });
        t.start();
    }

    private int getActionX(BufferedImage[] frames) {
        int labelW = (int)(frames[0].getWidth()  * SPRITE_SCALE);
        int idleW  = idleFrames != null ? (int)(idleFrames[0].getWidth() * SPRITE_SCALE) : SPRITE_W;
        return ACTION_X_BASE - (labelW - idleW) / 2;
    }

    private void playBladeRushAnimation(Runnable onDone) {
        if (heroIdleTimer != null && heroIdleTimer.isRunning()) heroIdleTimer.stop();
        if (bladeRushFrames == null) { startHeroIdleAnimation(); if (onDone != null) onDone.run(); return; }
        isPlayingBladeRush = true; heroSpriteFrame = 0;
        int lW = (int)(bladeRushFrames[0].getWidth()  * SPRITE_SCALE), lH = (int)(bladeRushFrames[0].getHeight() * SPRITE_SCALE);
        heroSpriteLabel.setBounds(getActionX(bladeRushFrames), ACTION_Y, lW, lH); heroSpriteLabel.repaint();
        int[] frame = {0};
        javax.swing.Timer t = new javax.swing.Timer(130, e -> {
            if (frame[0] < BLADE_RUSH_FRAME_COUNT) { heroSpriteFrame = frame[0]++; heroSpriteLabel.repaint(); }
            else { ((javax.swing.Timer)e.getSource()).stop(); isPlayingBladeRush = false; startHeroIdleAnimation(); if (onDone != null) onDone.run(); }
        });
        t.start();
    }

    private void playPiercingSlashAnimation(Runnable onDone) {
        if (heroIdleTimer != null && heroIdleTimer.isRunning()) heroIdleTimer.stop();
        if (piercingSlashFrames == null) { startHeroIdleAnimation(); if (onDone != null) onDone.run(); return; }
        isPlayingPiercingSlash = true; heroSpriteFrame = 0;
        int lW = (int)(piercingSlashFrames[0].getWidth()  * SPRITE_SCALE), lH = (int)(piercingSlashFrames[0].getHeight() * SPRITE_SCALE);
        heroSpriteLabel.setBounds(getActionX(piercingSlashFrames), ACTION_Y, lW, lH); heroSpriteLabel.repaint();
        int[] frame = {0};
        javax.swing.Timer t = new javax.swing.Timer(110, e -> {
            if (frame[0] < PIERCING_SLASH_FRAME_COUNT) { heroSpriteFrame = frame[0]++; heroSpriteLabel.repaint(); }
            else { ((javax.swing.Timer)e.getSource()).stop(); isPlayingPiercingSlash = false; startHeroIdleAnimation(); if (onDone != null) onDone.run(); }
        });
        t.start();
    }

    private void playEternalCrossSlashAnimation(Runnable onDone) {
        if (heroIdleTimer != null && heroIdleTimer.isRunning()) heroIdleTimer.stop();
        if (eternalCrossFrames == null) { startHeroIdleAnimation(); if (onDone != null) onDone.run(); return; }
        isPlayingEternalCross = true; heroSpriteFrame = 0;
        int lW = (int)(eternalCrossFrames[0].getWidth()  * SPRITE_SCALE), lH = (int)(eternalCrossFrames[0].getHeight() * SPRITE_SCALE);
        heroSpriteLabel.setBounds(getActionX(eternalCrossFrames), ACTION_Y, lW, lH); heroSpriteLabel.repaint();
        int[] frame = {0}, repeat = {0};
        javax.swing.Timer t = new javax.swing.Timer(60, e -> {
            if (frame[0] < ETERNAL_CROSS_FRAME_COUNT) { heroSpriteFrame = frame[0]++; heroSpriteLabel.repaint(); }
            else {
                repeat[0]++;
                if (repeat[0] < 2) { frame[0] = 0; heroSpriteFrame = 0; heroSpriteLabel.repaint(); }
                else { ((javax.swing.Timer)e.getSource()).stop(); isPlayingEternalCross = false; startHeroIdleAnimation(); if (onDone != null) onDone.run(); }
            }
        });
        t.start();
    }

    // ════════════════════════════════════════════════════════════════════════
    // ★ BATTLE LOGIC
    // ════════════════════════════════════════════════════════════════════════
    private void onPlayerAction(BattleManager.BattleAction action) {
        if (animating || engine.getCurrentTurn() != BattleManager.TurnOwner.PLAYER) return;
        animating = true;
        setActionsEnabled(false);
        clearLog();
        setTurnLabel(true);

        BattleManager.ActionResult pResult = engine.playerAction(action);
        refreshBattleUI();

        Runnable afterHeroAnim = () -> {
            if (engine.checkOutcome() == BattleManager.BattleOutcome.VICTORY) {
                playEnemyHurt(() -> {
                    clearLog();
                    if (pResult != null) addLogFromResult(pResult, true);
                    playEnemyDefeat(() -> {
                        handleVictory();
                        animating = false;
                    });
                });
            } else {
                playEnemyHurt(() -> {
                    clearLog();
                    if (pResult != null) addLogFromResult(pResult, true);
                    Timer t1 = new Timer(900, e -> {
                        engine.advanceToEnemyTurn();
                        setTurnLabel(false);
                        playEnemyAttack(() -> {
                            BattleManager.ActionResult er = engine.enemyTurn();
                            refreshBattleUI();
                            playKaelHurtAnimation(() -> {
                                clearLog();
                                if (er != null) addEnemyAttackLog(er);
                                if (engine.checkOutcome() == BattleManager.BattleOutcome.DEFEAT) {
                                    handleDefeat();
                                } else {
                                    Timer t3 = new Timer(800, ev2 -> {
                                        clearLog(); setTurnLabel(true);
                                        setActionsEnabled(true); animating = false;
                                    });
                                    t3.setRepeats(false); t3.start();
                                }
                            });
                        });
                    });
                    t1.setRepeats(false); t1.start();
                });
            }
        };

        if (action == BattleManager.BattleAction.SKILL1)        playBladeRushAnimation(afterHeroAnim);
        else if (action == BattleManager.BattleAction.SKILL2)   playPiercingSlashAnimation(afterHeroAnim);
        else if (action == BattleManager.BattleAction.ULTIMATE) playEternalCrossSlashAnimation(afterHeroAnim);
        else {
            if (pResult != null) addLogFromResult(pResult, true);
            if (engine.checkOutcome() == BattleManager.BattleOutcome.VICTORY) { handleVictory(); animating = false; return; }
            Timer t1 = new Timer(900, e -> {
                engine.advanceToEnemyTurn(); setTurnLabel(false);
                playEnemyAttack(() -> {
                    BattleManager.ActionResult er = engine.enemyTurn(); refreshBattleUI();
                    playKaelHurtAnimation(() -> {
                        clearLog(); if (er != null) addEnemyAttackLog(er);
                        if (engine.checkOutcome() == BattleManager.BattleOutcome.DEFEAT) handleDefeat();
                        else {
                            Timer t3 = new Timer(800, ev2 -> { clearLog(); setTurnLabel(true); setActionsEnabled(true); animating = false; });
                            t3.setRepeats(false); t3.start();
                        }
                    });
                });
            });
            t1.setRepeats(false); t1.start();
        }
    }

    private void addEnemyAttackLog(BattleManager.ActionResult r) {
        if (enemyDef == null) { addLogFromResult(r, false); return; }

        String msg;
        if (enemyDef.name.equals("The Hollow Stag")) {
            String skillUsed = engine.getLastEnemySkillName();
            java.util.regex.Matcher m = java.util.regex.Pattern.compile("\\d+").matcher(r.logMessage != null ? r.logMessage : "");
            msg = "The Hollow Stag uses " + skillUsed + (m.find() ? " and dealt " + m.group() + " damage!" : "!");
        } else {
            msg = switch (enemyDef.name) {
                case "Rotfang Wolf" -> "Rotfang Wolf uses Savage Howl";
                case "Shade Sprite" -> "Shade Sprite uses Trickster Strike";
                default             -> null;
            };
            if (msg != null) {
                if (r.logMessage != null && !r.logMessage.isEmpty()) {
                    java.util.regex.Matcher m = java.util.regex.Pattern.compile("\\d+").matcher(r.logMessage);
                    msg += m.find() ? " and dealt " + m.group() + " damage!" : "!";
                } else { msg += "!"; }
            }
        }

        if (msg != null) {
            addLog(msg, RED);
            if (r.dotDamageApplied > 0) addLog("Burn tick: " + r.dotDamageApplied, new Color(150, 60, 0));
        } else {
            addLogFromResult(r, false);
        }
    }

    private void handleVictory() {
        if (enemySequence != null) {
            EnemyDefinition eDef = enemySequence.get(enemySequenceIndex);
            enemyFightIndex++;
            clearLog();
            if (enemyFightIndex < eDef.count) {
                savedHeroCombatant = engine.getHero();
                addLog(getPerKillMessage(eDef, enemyFightIndex), GREEN);
                delay(1500, () -> {
                    clearLog();
                    addLog(getNextApproachMessage(eDef, enemyFightIndex), GOLD);
                    delay(800, () -> {
                        // startNextFight sets up the new enemy then plays entrance itself,
                        // so just call it directly — no extra entrance here
                        startNextFight();
                    });
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
        int shards = eDef.name.equals("The Hollow Stag") ? 10 : 1;
        addLog("  " + shards + " Soul Shard" + (shards > 1 ? "s" : ""), new Color(20, 80, 160));
        currentHero.soulShards += shards;
        addLog("  +" + xp + " XP", new Color(140, 90, 0));

        battleContinueBtn.setEnabled(true);
        turnLabel.setText("");
        roundLabel.setText("");
        specialCdLabel.setText("");
        stopEnemyAnimation();
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
                    addLog(getObjectiveCompleteText(postVictoryEnemy), GOLD);
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
                addLog(getObjectiveCompleteText(postVictoryEnemy), GOLD);
                battleContinueBtn.setEnabled(true);
            }
            case OBJECTIVE -> {
                postVictoryStep = PostVictoryStep.VICTORY_FLAVOUR;
                clearLog();
                addLog(getVictoryFlavourText(postVictoryEnemy), GREEN);
                battleContinueBtn.setEnabled(true);
            }
            case VICTORY_FLAVOUR -> {
                postVictoryStep = PostVictoryStep.LOOT_FLAVOUR;
                clearLog();
                addLog(getLootFlavourText(postVictoryEnemy), new Color(100, 65, 10));
                battleContinueBtn.setEnabled(true);
            }
            case LOOT_FLAVOUR -> {
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

        try {
            if ((boolean) currentHero.getClass().getField("hasPhoenixSoulstone").get(currentHero)) {
                currentHero.getClass().getField("hasPhoenixSoulstone").set(currentHero, false);
                currentHero.currentHp = currentHero.maxHp;
                clearLog();
                addLog("Phoenix Soulstone activated! Revived!", GREEN);
                refreshBattleUI(); setTurnLabel(true); setActionsEnabled(true); animating = false; return;
            }
        } catch (Exception ignored) {}

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

    // ════════════════════════════════════════════════════════════════════════
    // ★ FLAVOUR TEXTS
    // ════════════════════════════════════════════════════════════════════════
    private String getPerKillMessage(EnemyDefinition eDef, int killed) {
        String score = killed + "/" + eDef.count;
        return switch (eDef.name) {
            case "Rotfang Wolf"    -> "The wolf whimpers and dissolves into black smoke. (" + score + ")";
            case "Shade Sprite"    -> "You dispelled the Shade Sprite! (" + score + ")";
            case "Dreadbark Treant"-> "You felled the Dreadbark Treant! (" + score + ")";
            case "Carrion Bat"     -> "You slayed the Carrion Bat! (" + score + ")";
            case "The Hollow Stag" -> "The Hollow Stag has fallen! (" + score + ")";
            default -> eDef.name + " defeated! (" + score + ")";
        };
    }

    private String getNextApproachMessage(EnemyDefinition eDef, int killed) {
        int next = killed + 1;
        return switch (eDef.name) {
            case "Rotfang Wolf"    -> "Another wolf snarls and steps forward! (" + next + "/" + eDef.count + ")";
            case "Shade Sprite"    -> "The mist swirls — another soul screams into existence! (" + next + "/" + eDef.count + ")";
            case "Dreadbark Treant"-> "The ground quakes again! The second ancient giant lumbers forward! (" + next + "/" + eDef.count + ")";
            case "Carrion Bat"     -> "Another screech echoes above — the swarm continues! (" + next + "/" + eDef.count + ")";
            default -> "Another " + eDef.name + " approaches! (" + next + "/" + eDef.count + ")";
        };
    }

    private String getObjectiveCompleteText(EnemyDefinition eDef) {
        return switch (eDef.name) {
            case "Rotfang Wolf"    -> "OBJECTIVE: DEFEAT 3 ROTFANG WOLVES!  (3/3)";
            case "Shade Sprite"    -> "OBJECTIVE: DEFEAT 2 SHADE SPRITES!  (2/2)";
            case "Dreadbark Treant"-> "OBJECTIVE: DEFEAT 2 DREADBARK TREANTS!  (2/2)";
            case "Carrion Bat"     -> "OBJECTIVE: DEFEAT 4 CARRION BATS!  (4/4)";
            case "The Hollow Stag" -> "OBJECTIVE: DEFEAT THE HOLLOW STAG!  COMPLETE";
            default -> "OBJECTIVE: " + eDef.name.toUpperCase() + " CLEARED!";
        };
    }

    private String getVictoryFlavourText(EnemyDefinition eDef) {
        return switch (eDef.name) {
            case "Rotfang Wolf"    -> "Victory! The last of the Rotfang Wolves collapses.\nThe adrenaline in your veins cools, but the forest feels no safer.";
            case "Shade Sprite"    -> "With a final shriek, the sprites disperse like fog in the wind.\nThe mist recedes. The whispering in your mind finally stops.";
            case "Dreadbark Treant"-> "The massive Treants freeze and collapse.\nWhere they fall, small green sprouts rise from the ash.";
            case "Carrion Bat"     -> "The last bat crashes into the ground.\nThe forest grows quiet. The stench of decay lifts into the cold wind.";
            case "The Hollow Stag" -> "MINI-BOSS DEFEATED!\nThe Stag staggers. The white fire in its antlers flickers and dies.\nIt dissolves into particles of pure light.";
            default -> "You have defeated " + eDef.name + "!";
        };
    }

    private String getLootFlavourText(EnemyDefinition eDef) {
        return switch (eDef.name) {
            case "Rotfang Wolf"    -> "You bandage your wounds and collect what little the wolves carried.";
            case "Shade Sprite"    -> "You feel your strength returning after overcoming the darkness.";
            case "Dreadbark Treant"-> "You emerge covered in dust, but victorious. Treasures fall from the decaying wood.";
            case "Carrion Bat"     -> "You catch your breath. You feel stronger... and richer.";
            case "The Hollow Stag" -> "You reach out and grasp the light. It pulses with quiet power.";
            default -> "You collect your rewards.";
        };
    }

    // ════════════════════════════════════════════════════════════════════════
    // ★ GENERAL UTILITIES
    // ════════════════════════════════════════════════════════════════════════
    private void parseLevelUpMsg(String msg) {
        String[] p = msg.split("\\|");
        if (p.length < 8) return;
        lvlUp_level   = Integer.parseInt(p[1]); lvlUp_hpGain  = Integer.parseInt(p[2]); lvlUp_newHp   = Integer.parseInt(p[3]);
        lvlUp_atkGain = Integer.parseInt(p[4]); lvlUp_newAtk  = Integer.parseInt(p[5]); lvlUp_defGain = Integer.parseInt(p[6]); lvlUp_newDef  = Integer.parseInt(p[7]);
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
        try { doc.insertString(doc.getLength(), text + "\n", attrs); } catch (Exception e) {}
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

        String enemyEmoji = switch (enemyDef.name) {
            case "Rotfang Wolf" -> "🐾";
            case "Shade Sprite" -> "👻";
            default -> enemyDef.emoji;
        };
        enemyEmojiLbl.setText(enemyEmoji);
        enemyNameLbl.setText(enemyDef.name);
        enemyRoleLbl.setText(enemyDef.role);

        heroHpBar.setMaximum(currentHero.maxHp);
        enemyHpBar.setMaximum(currentEnemy.maxHp);
        if (heroEnergyBar != null) heroEnergyBar.setMaximum(currentHero.maxEnergy);

        if (heroDef.skills != null && heroDef.skills.length >= 3) {
            configureSkillButton(skill1Btn, heroDef.skills[0].name, heroDef.skills[0].icon, new Color(60, 30, 90), new Color(130, 60, 200));
            configureSkillButton(skill2Btn, heroDef.skills[1].name, heroDef.skills[1].icon, new Color(30, 60, 90), new Color(52, 120, 219));
            configureSkillButton(ultimateBtn, heroDef.skills[2].name, heroDef.skills[2].icon, new Color(90, 30, 30), new Color(192, 57, 43));
        }
        configureSkillButton(skipTurnBtn, "Skip Turn", "", new Color(30, 60, 40), new Color(39, 174, 96));
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

    // ════════════════════════════════════════════════════════════════════════
    // ★ SWING BUILDER HELPERS
    // ════════════════════════════════════════════════════════════════════════
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
        JPanel overlay = new JPanel(new GridBagLayout());
        overlay.setBackground(OVERLAY_BG);
        overlay.setOpaque(true);

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

        JPanel itemsPanel = new JPanel(new GridLayout(1, 2, 40, 0));
        itemsPanel.setBackground(new Color(20, 18, 36));
        itemsPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

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

    public void enableDevTools() {
        JPanel devPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        devPanel.setBackground(new Color(0, 0, 0, 0));
        devPanel.setOpaque(false);
        devPanel.setBounds(0, 100, 550, 28);

        JLabel lbl = new JLabel("DEV DMG:");
        lbl.setFont(new Font("Monospaced", Font.BOLD, 11));
        lbl.setForeground(new Color(255, 200, 0));
        devPanel.add(lbl);

        // Spinner — multiples of 50, range 50–5000
        SpinnerNumberModel spinModel = new SpinnerNumberModel(50, 50, 5000, 50);
        JSpinner dmgSpinner = new JSpinner(spinModel);
        dmgSpinner.setPreferredSize(new Dimension(80, 22));
        dmgSpinner.setFont(new Font("Monospaced", Font.BOLD, 11));
        devPanel.add(dmgSpinner);

        JButton hitHeroBtn = new JButton("Hit Hero");
        hitHeroBtn.setFont(new Font("Monospaced", Font.BOLD, 10));
        hitHeroBtn.setForeground(Color.WHITE);
        hitHeroBtn.setBackground(new Color(140, 30, 30));
        hitHeroBtn.setFocusPainted(false);
        hitHeroBtn.addActionListener(e -> {
            if (currentHero == null) return;
            int dmg = (int) dmgSpinner.getValue();
            currentHero.currentHp = Math.max(0, currentHero.currentHp - dmg);
            refreshBattleUI();
            addLog("[DEV] Hero hit for " + dmg + " damage!", new Color(255, 80, 80));
        });
        devPanel.add(hitHeroBtn);

        JButton hitEnemyBtn = new JButton("Hit Enemy");
        hitEnemyBtn.setFont(new Font("Monospaced", Font.BOLD, 10));
        hitEnemyBtn.setForeground(Color.WHITE);
        hitEnemyBtn.setBackground(new Color(30, 100, 140));
        hitEnemyBtn.setFocusPainted(false);
        hitEnemyBtn.addActionListener(e -> {
            if (currentEnemy == null) return;
            int dmg = (int) dmgSpinner.getValue();
            currentEnemy.currentHp = Math.max(0, currentEnemy.currentHp - dmg);
            refreshBattleUI();
            addLog("[DEV] Enemy hit for " + dmg + " damage!", new Color(80, 180, 255));
        });
        devPanel.add(hitEnemyBtn);

        JButton killEnemyBtn = new JButton("Kill Enemy");
        killEnemyBtn.setFont(new Font("Monospaced", Font.BOLD, 10));
        killEnemyBtn.setForeground(Color.WHITE);
        killEnemyBtn.setBackground(new Color(80, 30, 120));
        killEnemyBtn.setFocusPainted(false);
        killEnemyBtn.addActionListener(e -> {
            if (currentEnemy == null) return;
            currentEnemy.currentHp = 0;
            refreshBattleUI();
            addLog("[DEV] Enemy instantly killed!", new Color(200, 100, 255));
        });
        devPanel.add(killEnemyBtn);

        add(devPanel);
        setComponentZOrder(devPanel, 0);
        revalidate();
        repaint();
    }
}