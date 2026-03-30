package GameGUI;

import GameGUI.BattleLogic.*;
import GameGUI.HeroData.HeroDefinition;
import GameGUI.HeroData.EnemyDefinition;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class BattlePanel extends JPanel {

    private Runnable onReturnToSelection;
    private Runnable onRestartBattle;

    private BattleLogic engine;
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

    private Image heroIdleSheet;
    private Image kaelBladeRushSheet;
    private boolean isPlayingBladeRush     = false;
    private boolean isPlayingPiercingSlash = false;
    private boolean isPlayingEternalCross  = false;
    private boolean isPlayingKaelHurt      = false;

    // ════════════════════════════════════════════
    // ★ ENEMY SPRITE FIELDS — Rotfang Wolf
    // ════════════════════════════════════════════
    private JLabel enemySpriteLabel;
    private javax.swing.Timer enemyIdleTimer;
    private int enemySpriteFrame = 0;

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

    // ════════════════════════════════════════════
    // ★ ENEMY SPRITE FIELDS — Shade Sprite
    // ════════════════════════════════════════════
    private BufferedImage[] spriteIdleFrames;
    private BufferedImage[] spriteHurtFrames;
    private BufferedImage[] spriteTricksterFrames;
    private BufferedImage[] spriteDefeatFrames;
    private BufferedImage[] spriteEntranceFrames;

    private static final int SPRITE_IDLE_COUNT      = 2;
    private static final int SPRITE_HURT_COUNT      = 2;
    private static final int SPRITE_TRICKSTER_COUNT = 6;
    private static final int SPRITE_DEFEAT_COUNT    = 8;
    private static final int SPRITE_ENTRANCE_COUNT  = 9;
    private static final int SPRITE_ENEMY_SPEED     = 170;

    private boolean isPlayingSpriteHurt      = false;
    private boolean isPlayingSpriteTrickster = false;
    private boolean isPlayingSpriteDefeat    = false;
    private boolean isPlayingSpriteEntrance  = false;

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
    private JLabel turnLabel;
    private javax.swing.JTextPane logArea;

    // ════════════════════════════════════════════
    // ★ IMAGE-BASED SKILL BUTTONS
    // ════════════════════════════════════════════
    private JButton skill1Btn, skill2Btn, skipTurnBtn, ultimateBtn;
    private JButton battleContinueBtn;
    private JButton attackBtn, defendBtn, specialBtn;
    private JLabel specialCdLabel;
    private JPanel resultOverlay;
    private JLabel resultIcon, resultTitle, resultSub;

    // Cached icons for the ultimate button (normal / hovered / cooldown)
    private ImageIcon ultimateNormalIcon;
    private ImageIcon ultimateHoverIcon;
    private ImageIcon ultimateCooldownIcon;

    public BattlePanel() {
        setLayout(null);
        setPreferredSize(new Dimension(1280, 720));
        setBackground(BG_DARK);
        buildUI();
    }

    public void setOnReturnToSelection(Runnable r) { this.onReturnToSelection = r; }
    public void setOnRestartBattle(Runnable r)     { this.onRestartBattle = r; }
    public void setOnEnemyGroupDefeated(java.util.function.BiConsumer<Integer, Runnable> cb) { this.onEnemyGroupDefeated = cb; }

    private java.util.List<EnemyDefinition> enemySequence;
    private int enemySequenceIndex = 0;
    private int enemyFightIndex    = 0;
    private Runnable onSequenceComplete;
    private java.util.function.BiConsumer<Integer, Runnable> onEnemyGroupDefeated;

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

    private BattleLogic.Combatant savedHeroCombatant = null;

    private void startNextFight() {
        if (enemySequenceIndex >= enemySequence.size()) {
            if (onSequenceComplete != null) onSequenceComplete.run();
            return;
        }
        EnemyDefinition eDef = enemySequence.get(enemySequenceIndex);
        this.enemyDef = eDef;
        Combatant heroToUse = (savedHeroCombatant != null)
                ? savedHeroCombatant : HeroData.buildHero(heroDef);
        savedHeroCombatant = null;
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
        startHeroIdleAnimation();
        startEnemyIdleAnimation(eDef);
    }

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
        startHeroIdleAnimation();
        startEnemyIdleAnimation(enemy);
    }

    // ════════════════════════════════════════════════════════════════════════
    // ★ HELPER — load a scaled ImageIcon from /assets/GUIButtons/
    // ════════════════════════════════════════════════════════════════════════
    private ImageIcon loadBtnIcon(String filename, int w, int h) {
        java.net.URL url = getClass().getResource("/assets/GUIButtons/" + filename);
        if (url == null) return null;
        return new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH));
    }

    // ════════════════════════════════════════════════════════════════════════
    // ★ HELPER — build a transparent image-only JButton with hover swap
    // ════════════════════════════════════════════════════════════════════════
    private JButton makeImageSkillBtn(String normalImg, String hoverImg,
                                      int x, int y, int w, int h) {
        ImageIcon normal = loadBtnIcon(normalImg, w, h);
        ImageIcon hover  = loadBtnIcon(hoverImg,  w, h);

        JButton btn = new JButton(normal != null ? normal : new ImageIcon());
        btn.setBounds(x, y, w, h);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        if (normal != null && hover != null) {
            btn.addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { if (btn.isEnabled()) btn.setIcon(hover); }
                @Override public void mouseExited (MouseEvent e) { btn.setIcon(normal); }
            });
        }
        return btn;
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
        turnLabel.setOpaque(false);
        add(turnLabel);

        roundLabel = new JLabel("Round 1", SwingConstants.CENTER);
        roundLabel.setBounds(0, 36, 1280, 22);
        roundLabel.setFont(new Font("Monospaced", Font.BOLD, 13));
        roundLabel.setForeground(GOLD);
        roundLabel.setOpaque(false);
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

        // ════════════════════════════════════════════════════════════════════
        // ★ IMAGE-BASED SKILL BUTTONS
        //   All four buttons share the same Y, W, H and are evenly spaced.
        // ════════════════════════════════════════════════════════════════════
        int btnY = 468, btnW = 95, btnH = 58, btnGap = 12;
        int startX = (1280 - (4 * btnW + 3 * btnGap)) / 2;

        // ── Skill 1 : Blade Rush ──
        skill1Btn = makeImageSkillBtn(
                "BladeRush.png", "BladeRushHovered.png",
                startX, btnY, btnW, btnH);
        skill1Btn.addActionListener(e -> onPlayerAction(BattleLogic.BattleAction.SKILL1));
        add(skill1Btn);

        // ── Skill 2 : Piercing Slash ──
        skill2Btn = makeImageSkillBtn(
                "PiercingSlash.png", "PiercingSlashHovered.png",
                startX + (btnW + btnGap), btnY, btnW, btnH);
        skill2Btn.addActionListener(e -> onPlayerAction(BattleLogic.BattleAction.SKILL2));
        add(skill2Btn);

        // ── Ultimate : Eternal Cross Slash (cooldown-aware) ──
        ultimateNormalIcon   = loadBtnIcon("EternalCrossSlash.png",         btnW, btnH);
        ultimateHoverIcon    = loadBtnIcon("EternalCrossSlashHovered.png",   btnW, btnH);
        ultimateCooldownIcon = loadBtnIcon("EternalCrossSlashCooldown.png",  btnW, btnH);

        ultimateBtn = new JButton(ultimateNormalIcon != null ? ultimateNormalIcon : new ImageIcon());
        ultimateBtn.setBounds(startX + 2 * (btnW + btnGap), btnY, btnW, btnH);
        ultimateBtn.setOpaque(false);
        ultimateBtn.setContentAreaFilled(false);
        ultimateBtn.setBorderPainted(false);
        ultimateBtn.setFocusPainted(false);
        ultimateBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        ultimateBtn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                if (ultimateBtn.isEnabled() && ultimateHoverIcon != null)
                    ultimateBtn.setIcon(ultimateHoverIcon);
            }
            @Override public void mouseExited(MouseEvent e) {
                updateUltimateIcon();   // restore correct icon (normal or cooldown)
            }
        });
        ultimateBtn.addActionListener(e -> onPlayerAction(BattleLogic.BattleAction.ULTIMATE));
        add(ultimateBtn);

        // ── Skip Turn ──
        // ── Skip Turn ──
        skipTurnBtn = makeImageSkillBtn(
                "SkipTurn.png", "SkipTurnHovered.png",
                startX + 3 * (btnW + btnGap), btnY, btnW, btnH);
        skipTurnBtn.addActionListener(e -> onPlayerAction(BattleLogic.BattleAction.SKIP_TURN));
        add(skipTurnBtn);

        attackBtn = skill1Btn; defendBtn = skipTurnBtn; specialBtn = ultimateBtn;

        specialCdLabel = new JLabel("");
        specialCdLabel.setBounds(0, 60, 1280, 20);
        specialCdLabel.setFont(new Font("Monospaced", Font.BOLD, 12));
        specialCdLabel.setForeground(new Color(200, 100, 50));
        specialCdLabel.setHorizontalAlignment(SwingConstants.CENTER);
        specialCdLabel.setOpaque(false);
        add(specialCdLabel);

        resultOverlay = buildResultOverlay();
        resultOverlay.setBounds(0, 0, 1280, 720);
        resultOverlay.setVisible(false);
        add(resultOverlay);

        setComponentZOrder(resultOverlay,     0);
        setComponentZOrder(specialCdLabel,    1);
        setComponentZOrder(roundLabel,        2);
        setComponentZOrder(turnLabel,         3);
        setComponentZOrder(logArea,           3);
        setComponentZOrder(battleContinueBtn, 4);
        setComponentZOrder(menuBtn,           5);
        setComponentZOrder(backBtn,           6);
        setComponentZOrder(exitBtn,           7);
        setComponentZOrder(skill1Btn,         8);
        setComponentZOrder(skill2Btn,         9);
        setComponentZOrder(skipTurnBtn,       10);
        setComponentZOrder(ultimateBtn,       11);
        setComponentZOrder(dialogueBg,        13);
        setComponentZOrder(heroCard,          14);
        setComponentZOrder(enemyCard,         15);
        setComponentZOrder(heroSpriteLabel,   16);
        setComponentZOrder(enemySpriteLabel,  17);
        setComponentZOrder(battleBg,          18);
        setComponentZOrder(theBg,             19);
    }

    // ════════════════════════════════════════════════════════════════════════
    // ★ UPDATE ULTIMATE BUTTON ICON based on cooldown state
    // ════════════════════════════════════════════════════════════════════════
    private void updateUltimateIcon() {
        if (ultimateBtn == null) return;
        boolean onCooldown = engine != null && !engine.canUseUltimate();
        if (onCooldown && ultimateCooldownIcon != null) {
            ultimateBtn.setIcon(ultimateCooldownIcon);
        } else if (ultimateNormalIcon != null) {
            ultimateBtn.setIcon(ultimateNormalIcon);
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // ★ HERO SPRITE LABEL
    // ════════════════════════════════════════════════════════════════════════
    private JLabel buildHeroSpriteLabel() {
        try {
            java.net.URL url = getClass().getResource("/assets/KaelAssets/KaelIdle.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / SPRITE_FRAME_COUNT, fh = sheet.getHeight();
                idleFrames = new BufferedImage[SPRITE_FRAME_COUNT];
                for (int i = 0; i < SPRITE_FRAME_COUNT; i++)
                    idleFrames[i] = removeBlackBg(sheet.getSubimage(i * fw, 0, fw, fh));
            }
        } catch (Exception ex) { System.out.println("KaelIdle error: " + ex.getMessage()); }

        try {
            java.net.URL url = getClass().getResource("/assets/KaelAssets/KaelBladeRush.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / BLADE_RUSH_FRAME_COUNT, fh = sheet.getHeight();
                bladeRushFrames = new BufferedImage[BLADE_RUSH_FRAME_COUNT];
                for (int i = 0; i < BLADE_RUSH_FRAME_COUNT; i++)
                    bladeRushFrames[i] = removeBlackBg(sheet.getSubimage(i * fw, 0, fw, fh));
            }
        } catch (Exception ex) { System.out.println("KaelBladeRush error: " + ex.getMessage()); }

        try {
            java.net.URL url = getClass().getResource("/assets/KaelAssets/KaelPiercingSlash.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / PIERCING_SLASH_FRAME_COUNT, fh = sheet.getHeight();
                piercingSlashFrames = new BufferedImage[PIERCING_SLASH_FRAME_COUNT];
                for (int i = 0; i < PIERCING_SLASH_FRAME_COUNT; i++)
                    piercingSlashFrames[i] = removeBlackBg(sheet.getSubimage(i * fw, 0, fw, fh));
            }
        } catch (Exception ex) { System.out.println("KaelPiercingSlash error: " + ex.getMessage()); }

        try {
            java.net.URL url = getClass().getResource("/assets/KaelAssets/KaelEternalCrossSlash.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / ETERNAL_CROSS_FRAME_COUNT, fh = sheet.getHeight();
                eternalCrossFrames = new BufferedImage[ETERNAL_CROSS_FRAME_COUNT];
                for (int i = 0; i < ETERNAL_CROSS_FRAME_COUNT; i++)
                    eternalCrossFrames[i] = removeBlackBg(sheet.getSubimage(i * fw, 0, fw, fh));
            }
        } catch (Exception ex) { System.out.println("KaelEternalCross error: " + ex.getMessage()); }

        try {
            java.net.URL url = getClass().getResource("/assets/KaelAssets/KaelHurt.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / KAEL_HURT_FRAME_COUNT, fh = sheet.getHeight();
                kaelHurtFrames = new BufferedImage[KAEL_HURT_FRAME_COUNT];
                for (int i = 0; i < KAEL_HURT_FRAME_COUNT; i++)
                    kaelHurtFrames[i] = removeBlackBg(sheet.getSubimage(i * fw, 0, fw, fh));
            }
        } catch (Exception ex) { System.out.println("KaelHurt error: " + ex.getMessage()); }

        JLabel sprite = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                BufferedImage[] frames =
                        isPlayingBladeRush     ? bladeRushFrames :
                                isPlayingPiercingSlash ? piercingSlashFrames :
                                        isPlayingEternalCross  ? eternalCrossFrames :
                                                isPlayingKaelHurt      ? kaelHurtFrames :
                                                        idleFrames;
                if (frames == null || heroSpriteFrame >= frames.length) return;
                BufferedImage frame = frames[heroSpriteFrame];
                if (frame == null) return;
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON);
                g2.drawImage(frame, 0, 0, (int)(frame.getWidth() * SPRITE_SCALE), (int)(frame.getHeight() * SPRITE_SCALE), null);
                g2.dispose();
            }
        };

        int labelW = idleFrames != null ? (int)(idleFrames[0].getWidth()  * SPRITE_SCALE) : SPRITE_W;
        int labelH = idleFrames != null ? (int)(idleFrames[0].getHeight() * SPRITE_SCALE) : SPRITE_H;
        sprite.setBounds(IDLE_X, IDLE_Y, labelW, labelH);
        sprite.setOpaque(false);
        return sprite;
    }

    // ════════════════════════════════════════════════════════════════════════
    // ★ ENEMY SPRITE LABEL — loads Wolf + ShadeSprite animations
    // ════════════════════════════════════════════════════════════════════════
    private JLabel buildEnemySpriteLabel() {

        try {
            java.net.URL url = getClass().getResource("/assets/World1EnemyAssets/RodtfangWolfIdle.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / WOLF_FRAME_COUNT, fh = sheet.getHeight();
                wolfIdleFrames = new BufferedImage[WOLF_FRAME_COUNT];
                for (int i = 0; i < WOLF_FRAME_COUNT; i++)
                    wolfIdleFrames[i] = removeBlackBg(sheet.getSubimage(i * fw, 0, fw, fh));
            }
        } catch (Exception ex) { System.out.println("WolfIdle error: " + ex.getMessage()); }

        try {
            java.net.URL url = getClass().getResource("/assets/World1EnemyAssets/RodtfangWolfHurt.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / WOLF_HURT_FRAME_COUNT, fh = sheet.getHeight();
                wolfHurtFrames = new BufferedImage[WOLF_HURT_FRAME_COUNT];
                for (int i = 0; i < WOLF_HURT_FRAME_COUNT; i++)
                    wolfHurtFrames[i] = removeBlackBg(sheet.getSubimage(i * fw, 0, fw, fh));
            }
        } catch (Exception ex) { System.out.println("WolfHurt error: " + ex.getMessage()); }

        try {
            java.net.URL url = getClass().getResource("/assets/World1EnemyAssets/RodtfangWolfSavageHowl.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / WOLF_HOWL_FRAME_COUNT, fh = sheet.getHeight();
                wolfSavageHowlFrames = new BufferedImage[WOLF_HOWL_FRAME_COUNT];
                for (int i = 0; i < WOLF_HOWL_FRAME_COUNT; i++)
                    wolfSavageHowlFrames[i] = removeBlackBg(sheet.getSubimage(i * fw, 0, fw, fh));
            }
        } catch (Exception ex) { System.out.println("WolfSavageHowl error: " + ex.getMessage()); }

        try {
            java.net.URL url = getClass().getResource("/assets/World1EnemyAssets/RodtfangWolfDefeat.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / WOLF_DEFEAT_FRAME_COUNT, fh = sheet.getHeight();
                wolfDefeatFrames = new BufferedImage[WOLF_DEFEAT_FRAME_COUNT];
                for (int i = 0; i < WOLF_DEFEAT_FRAME_COUNT; i++)
                    wolfDefeatFrames[i] = removeBlackBg(sheet.getSubimage(i * fw, 0, fw, fh));
            }
        } catch (Exception ex) { System.out.println("WolfDefeat error: " + ex.getMessage()); }

        try {
            java.net.URL url = getClass().getResource("/assets/World1EnemyAssets/RodtfangWolfEntrance.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / WOLF_ENTRANCE_FRAME_COUNT, fh = sheet.getHeight();
                wolfEntranceFrames = new BufferedImage[WOLF_ENTRANCE_FRAME_COUNT];
                for (int i = 0; i < WOLF_ENTRANCE_FRAME_COUNT; i++)
                    wolfEntranceFrames[i] = removeBlackBg(sheet.getSubimage(i * fw, 0, fw, fh));
            }
        } catch (Exception ex) { System.out.println("WolfEntrance error: " + ex.getMessage()); }

        try {
            java.net.URL url = getClass().getResource("/assets/World1EnemyAssets/ShadeSpriteIdle.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / SPRITE_IDLE_COUNT, fh = sheet.getHeight();
                spriteIdleFrames = new BufferedImage[SPRITE_IDLE_COUNT];
                for (int i = 0; i < SPRITE_IDLE_COUNT; i++)
                    spriteIdleFrames[i] = removeBlackBg(sheet.getSubimage(i * fw, 0, fw, fh));
            }
        } catch (Exception ex) { System.out.println("ShadeSpriteIdle error: " + ex.getMessage()); }

        try {
            java.net.URL url = getClass().getResource("/assets/World1EnemyAssets/ShadeSpriteHurt.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / SPRITE_HURT_COUNT, fh = sheet.getHeight();
                spriteHurtFrames = new BufferedImage[SPRITE_HURT_COUNT];
                for (int i = 0; i < SPRITE_HURT_COUNT; i++)
                    spriteHurtFrames[i] = removeBlackBg(sheet.getSubimage(i * fw, 0, fw, fh));
            }
        } catch (Exception ex) { System.out.println("ShadeSpriteHurt error: " + ex.getMessage()); }

        try {
            java.net.URL url = getClass().getResource("/assets/World1EnemyAssets/ShadeSpriteTricksterStrike.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / SPRITE_TRICKSTER_COUNT, fh = sheet.getHeight();
                spriteTricksterFrames = new BufferedImage[SPRITE_TRICKSTER_COUNT];
                for (int i = 0; i < SPRITE_TRICKSTER_COUNT; i++)
                    spriteTricksterFrames[i] = removeBlackBg(sheet.getSubimage(i * fw, 0, fw, fh));
            }
        } catch (Exception ex) { System.out.println("ShadeSpriteTrickster error: " + ex.getMessage()); }

        try {
            java.net.URL url = getClass().getResource("/assets/World1EnemyAssets/ShadeSpriteDefeat.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / SPRITE_DEFEAT_COUNT, fh = sheet.getHeight();
                spriteDefeatFrames = new BufferedImage[SPRITE_DEFEAT_COUNT];
                for (int i = 0; i < SPRITE_DEFEAT_COUNT; i++)
                    spriteDefeatFrames[i] = removeBlackBg(sheet.getSubimage(i * fw, 0, fw, fh));
            }
        } catch (Exception ex) { System.out.println("ShadeSpriteDefeat error: " + ex.getMessage()); }

        try {
            java.net.URL url = getClass().getResource("/assets/World1EnemyAssets/ShadeSpriteEntrance.png");
            if (url != null) {
                BufferedImage sheet = ImageIO.read(url);
                int fw = sheet.getWidth() / SPRITE_ENTRANCE_COUNT, fh = sheet.getHeight();
                spriteEntranceFrames = new BufferedImage[SPRITE_ENTRANCE_COUNT];
                for (int i = 0; i < SPRITE_ENTRANCE_COUNT; i++)
                    spriteEntranceFrames[i] = removeBlackBg(sheet.getSubimage(i * fw, 0, fw, fh));
            }
        } catch (Exception ex) { System.out.println("ShadeSpriteEntrance error: " + ex.getMessage()); }

        JLabel sprite = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                BufferedImage[] frames;
                boolean isShade = enemyDef != null && enemyDef.name.equals("Shade Sprite");
                if      (isPlayingWolfHurt)        frames = wolfHurtFrames;
                else if (isPlayingWolfSavageHowl)  frames = wolfSavageHowlFrames;
                else if (isPlayingWolfDefeat)       frames = wolfDefeatFrames;
                else if (isPlayingWolfEntrance)     frames = wolfEntranceFrames;
                else if (isPlayingSpriteHurt)       frames = spriteHurtFrames;
                else if (isPlayingSpriteTrickster)  frames = spriteTricksterFrames;
                else if (isPlayingSpriteDefeat)     frames = spriteDefeatFrames;
                else if (isPlayingSpriteEntrance)   frames = spriteEntranceFrames;
                else if (isShade)                   frames = spriteIdleFrames;
                else                                frames = wolfIdleFrames;

                if (frames == null || enemySpriteFrame >= frames.length) return;
                BufferedImage frame = frames[enemySpriteFrame];
                if (frame == null) return;
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON);
                double scale = isShade ? SPRITE_SCALE : ENEMY_SCALE;
                g2.drawImage(frame, 0, 0, (int)(frame.getWidth() * scale), (int)(frame.getHeight() * scale), null);
                g2.dispose();
            }
        };

        int labelW = wolfIdleFrames != null ? (int)(wolfIdleFrames[0].getWidth()  * ENEMY_SCALE) : SPRITE_W;
        int labelH = wolfIdleFrames != null ? (int)(wolfIdleFrames[0].getHeight() * ENEMY_SCALE) : SPRITE_H;
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
                int r = (px >> 16) & 0xFF, g = (px >> 8) & 0xFF, b = px & 0xFF;
                out.setRGB(x, y, (r < 30 && g < 30 && b < 30) ? 0x00000000 : px);
            }
        return out;
    }

    // ════════════════════════════════════════════════════════════════════════
    // ★ HERO IDLE ANIMATION
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

    // ════════════════════════════════════════════════════════════════════════
    // ★ ENEMY IDLE ANIMATION
    // ════════════════════════════════════════════════════════════════════════
    private void startEnemyIdleAnimation(EnemyDefinition eDef) {
        if (enemyIdleTimer != null && enemyIdleTimer.isRunning()) enemyIdleTimer.stop();
        isPlayingWolfHurt = false; isPlayingWolfSavageHowl = false;
        isPlayingWolfDefeat = false; isPlayingWolfEntrance = false;
        isPlayingSpriteHurt = false; isPlayingSpriteTrickster = false;
        isPlayingSpriteDefeat = false; isPlayingSpriteEntrance = false;
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
            int w = (int)(spriteIdleFrames[0].getWidth()  * SPRITE_SCALE);
            int h = (int)(spriteIdleFrames[0].getHeight() * SPRITE_SCALE);
            enemySpriteLabel.setBounds(SHADE_X, SHADE_Y + 8, w, h);
            enemySpriteLabel.setVisible(true);
            enemyIdleTimer = new javax.swing.Timer(220, e -> {
                enemySpriteFrame = (enemySpriteFrame + 1) % SPRITE_IDLE_COUNT;
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

    // ════════════════════════════════════════════════════════════════════════
    // ★ WOLF ANIMATIONS
    // ════════════════════════════════════════════════════════════════════════
    private void playWolfDefeatAnimation(Runnable onDone) {
        if (enemyIdleTimer != null && enemyIdleTimer.isRunning()) enemyIdleTimer.stop();
        if (wolfDefeatFrames == null) { enemySpriteLabel.setVisible(false); if (onDone != null) onDone.run(); return; }
        isPlayingWolfDefeat = true;
        enemySpriteFrame = 0;
        int w = (int)(wolfDefeatFrames[0].getWidth()  * ENEMY_SCALE);
        int h = (int)(wolfDefeatFrames[0].getHeight() * ENEMY_SCALE);
        enemySpriteLabel.setBounds(ENEMY_X, ENEMY_Y, w, h);
        enemySpriteLabel.repaint();
        int[] frame = {0};
        javax.swing.Timer t = new javax.swing.Timer(300, null);
        t.addActionListener(e -> {
            if (frame[0] < WOLF_DEFEAT_FRAME_COUNT) {
                enemySpriteFrame = frame[0]++; enemySpriteLabel.repaint();
            } else {
                t.stop();
                delay(400, () -> { isPlayingWolfDefeat = false; enemySpriteLabel.setVisible(false); if (onDone != null) onDone.run(); });
            }
        });
        t.start();
    }

    private void playWolfEntranceAnimation(Runnable onDone) {
        if (wolfEntranceFrames == null) { if (enemyDef != null) startEnemyIdleAnimation(enemyDef); if (onDone != null) onDone.run(); return; }
        isPlayingWolfEntrance = true;
        enemySpriteFrame = 0;
        int w = (int)(wolfEntranceFrames[0].getWidth()  * ENEMY_SCALE);
        int h = (int)(wolfEntranceFrames[0].getHeight() * ENEMY_SCALE);
        enemySpriteLabel.setBounds(WOLF_ENTRANCE_START_X, ENEMY_Y, w, h);
        enemySpriteLabel.setVisible(true);
        enemySpriteLabel.repaint();
        javax.swing.Timer frameTimer = new javax.swing.Timer(120, null);
        frameTimer.addActionListener(e -> { enemySpriteFrame = (enemySpriteFrame + 1) % WOLF_ENTRANCE_FRAME_COUNT; enemySpriteLabel.repaint(); });
        frameTimer.start();
        int[] currentX = {WOLF_ENTRANCE_START_X};
        javax.swing.Timer slideTimer = new javax.swing.Timer(16, null);
        slideTimer.addActionListener(e -> {
            currentX[0] -= 10;
            if (currentX[0] <= ENEMY_X) {
                enemySpriteLabel.setLocation(ENEMY_X, ENEMY_Y);
                slideTimer.stop(); frameTimer.stop();
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
        isPlayingWolfHurt = true;
        enemySpriteFrame = 0;
        int w = (int)(wolfHurtFrames[0].getWidth()  * ENEMY_SCALE);
        int h = (int)(wolfHurtFrames[0].getHeight() * ENEMY_SCALE);
        enemySpriteLabel.setBounds(ENEMY_X, ENEMY_Y, w, h);
        enemySpriteLabel.repaint();
        int[] frame = {0};
        javax.swing.Timer t = new javax.swing.Timer(170, null);
        t.addActionListener(e -> {
            if (frame[0] < WOLF_HURT_FRAME_COUNT) { enemySpriteFrame = frame[0]++; enemySpriteLabel.repaint(); }
            else { t.stop(); isPlayingWolfHurt = false; if (enemyDef != null) startEnemyIdleAnimation(enemyDef); if (onDone != null) onDone.run(); }
        });
        t.start();
    }

    private void playWolfSavageHowlAnimation(Runnable onDone) {
        if (enemyIdleTimer != null && enemyIdleTimer.isRunning()) enemyIdleTimer.stop();
        if (wolfSavageHowlFrames == null || !enemySpriteLabel.isVisible()) { if (onDone != null) onDone.run(); return; }
        isPlayingWolfSavageHowl = true;
        enemySpriteFrame = 0;
        int w = (int)(wolfSavageHowlFrames[0].getWidth()  * ENEMY_SCALE);
        int h = (int)(wolfSavageHowlFrames[0].getHeight() * ENEMY_SCALE);
        enemySpriteLabel.setBounds(ENEMY_X, ENEMY_Y, w, h);
        enemySpriteLabel.repaint();
        int[] frame = {0};
        javax.swing.Timer t = new javax.swing.Timer(170, null);
        t.addActionListener(e -> {
            if (frame[0] < WOLF_HOWL_FRAME_COUNT) { enemySpriteFrame = frame[0]++; enemySpriteLabel.repaint(); }
            else { t.stop(); isPlayingWolfSavageHowl = false; if (enemyDef != null) startEnemyIdleAnimation(enemyDef); if (onDone != null) onDone.run(); }
        });
        t.start();
    }

    // ════════════════════════════════════════════════════════════════════════
    // ★ SHADE SPRITE ANIMATIONS
    // ════════════════════════════════════════════════════════════════════════
    private void playSpriteHurtAnimation(Runnable onDone) {
        if (enemyIdleTimer != null && enemyIdleTimer.isRunning()) enemyIdleTimer.stop();
        if (spriteHurtFrames == null || !enemySpriteLabel.isVisible()) { if (onDone != null) onDone.run(); return; }
        isPlayingSpriteHurt = true;
        enemySpriteFrame = 0;
        int w = (int)(spriteHurtFrames[0].getWidth()  * SPRITE_SCALE);
        int h = (int)(spriteHurtFrames[0].getHeight() * SPRITE_SCALE);
        enemySpriteLabel.setBounds(SHADE_X, SHADE_Y, w, h);
        enemySpriteLabel.repaint();
        int[] frame = {0};
        javax.swing.Timer t = new javax.swing.Timer(170, null);
        t.addActionListener(e -> {
            if (frame[0] < SPRITE_HURT_COUNT) { enemySpriteFrame = frame[0]++; enemySpriteLabel.repaint(); }
            else { t.stop(); isPlayingSpriteHurt = false; if (enemyDef != null) startEnemyIdleAnimation(enemyDef); if (onDone != null) onDone.run(); }
        });
        t.start();
    }

    private void playSpriteTricksterAnimation(Runnable onDone) {
        if (enemyIdleTimer != null && enemyIdleTimer.isRunning()) enemyIdleTimer.stop();
        if (spriteTricksterFrames == null || !enemySpriteLabel.isVisible()) { if (onDone != null) onDone.run(); return; }
        isPlayingSpriteTrickster = true;
        enemySpriteFrame = 0;
        int w = (int)(spriteTricksterFrames[0].getWidth()  * SPRITE_SCALE);
        int h = (int)(spriteTricksterFrames[0].getHeight() * SPRITE_SCALE);
        enemySpriteLabel.setBounds(SHADE_X - 20, SHADE_Y - 15, w, h);
        enemySpriteLabel.repaint();
        int[] frame = {0};
        javax.swing.Timer t = new javax.swing.Timer(170, null);
        t.addActionListener(e -> {
            if (frame[0] < SPRITE_TRICKSTER_COUNT) { enemySpriteFrame = frame[0]++; enemySpriteLabel.repaint(); }
            else { t.stop(); isPlayingSpriteTrickster = false; if (enemyDef != null) startEnemyIdleAnimation(enemyDef); if (onDone != null) onDone.run(); }
        });
        t.start();
    }

    private void playSpriteDefeatAnimation(Runnable onDone) {
        if (enemyIdleTimer != null && enemyIdleTimer.isRunning()) enemyIdleTimer.stop();
        if (spriteDefeatFrames == null) { enemySpriteLabel.setVisible(false); if (onDone != null) onDone.run(); return; }
        isPlayingSpriteDefeat = true;
        enemySpriteFrame = 0;
        int w = (int)(spriteDefeatFrames[0].getWidth()  * SPRITE_SCALE);
        int h = (int)(spriteDefeatFrames[0].getHeight() * SPRITE_SCALE);
        enemySpriteLabel.setBounds(SHADE_X, SHADE_Y, w, h);
        enemySpriteLabel.repaint();
        int[] frame = {0};
        javax.swing.Timer t = new javax.swing.Timer(300, null);
        t.addActionListener(e -> {
            if (frame[0] < SPRITE_DEFEAT_COUNT) { enemySpriteFrame = frame[0]++; enemySpriteLabel.repaint(); }
            else { t.stop(); delay(400, () -> { isPlayingSpriteDefeat = false; enemySpriteLabel.setVisible(false); if (onDone != null) onDone.run(); }); }
        });
        t.start();
    }

    private void playSpriteEntranceAnimation(Runnable onDone) {
        if (spriteEntranceFrames == null) { if (enemyDef != null) startEnemyIdleAnimation(enemyDef); if (onDone != null) onDone.run(); return; }
        isPlayingSpriteEntrance = true;
        enemySpriteFrame = 0;
        int w = (int)(spriteEntranceFrames[0].getWidth()  * SPRITE_SCALE);
        int h = (int)(spriteEntranceFrames[0].getHeight() * SPRITE_SCALE);
        enemySpriteLabel.setBounds(WOLF_ENTRANCE_START_X, SHADE_Y, w, h);
        enemySpriteLabel.setVisible(true);
        enemySpriteLabel.repaint();
        javax.swing.Timer frameTimer = new javax.swing.Timer(120, null);
        frameTimer.addActionListener(e -> { enemySpriteFrame = (enemySpriteFrame + 1) % SPRITE_ENTRANCE_COUNT; enemySpriteLabel.repaint(); });
        frameTimer.start();
        int[] currentX = {WOLF_ENTRANCE_START_X};
        javax.swing.Timer slideTimer = new javax.swing.Timer(16, null);
        slideTimer.addActionListener(e -> {
            currentX[0] -= 10;
            if (currentX[0] <= SHADE_X) {
                enemySpriteLabel.setLocation(SHADE_X, SHADE_Y);
                slideTimer.stop(); frameTimer.stop();
                isPlayingSpriteEntrance = false;
                if (enemyDef != null) startEnemyIdleAnimation(enemyDef);
                if (onDone != null) onDone.run();
            } else { enemySpriteLabel.setLocation(currentX[0], SHADE_Y); }
        });
        slideTimer.start();
    }

    // ════════════════════════════════════════════════════════════════════════
    // ★ KAEL HURT ANIMATION
    // ════════════════════════════════════════════════════════════════════════
    private void playKaelHurtAnimation(Runnable onDone) {
        if (heroIdleTimer != null && heroIdleTimer.isRunning()) heroIdleTimer.stop();
        if (kaelHurtFrames == null) { startHeroIdleAnimation(); if (onDone != null) onDone.run(); return; }
        isPlayingKaelHurt = true;
        heroSpriteFrame = 0;
        int w = (int)(kaelHurtFrames[0].getWidth()  * SPRITE_SCALE);
        int h = (int)(kaelHurtFrames[0].getHeight() * SPRITE_SCALE);
        heroSpriteLabel.setBounds(IDLE_X, IDLE_Y, w, h);
        heroSpriteLabel.repaint();
        int[] frame = {0};
        javax.swing.Timer t = new javax.swing.Timer(150, null);
        t.addActionListener(e -> {
            if (frame[0] < KAEL_HURT_FRAME_COUNT) { heroSpriteFrame = frame[0]++; heroSpriteLabel.repaint(); }
            else { t.stop(); isPlayingKaelHurt = false; startHeroIdleAnimation(); if (onDone != null) onDone.run(); }
        });
        t.start();
    }

    private boolean isShadeSprite() { return enemyDef != null && enemyDef.name.equals("Shade Sprite"); }

    private void playEnemyHurt(Runnable onDone)    { if (isShadeSprite()) playSpriteHurtAnimation(onDone);      else playWolfHurtAnimation(onDone); }
    private void playEnemyAttack(Runnable onDone)  { if (isShadeSprite()) playSpriteTricksterAnimation(onDone); else playWolfSavageHowlAnimation(onDone); }
    private void playEnemyDefeat(Runnable onDone)  { if (isShadeSprite()) playSpriteDefeatAnimation(onDone);    else playWolfDefeatAnimation(onDone); }
    private void playEnemyEntrance(Runnable onDone){ if (isShadeSprite()) playSpriteEntranceAnimation(onDone);  else playWolfEntranceAnimation(onDone); }

    private int getActionX(BufferedImage[] frames) {
        int labelW = (int)(frames[0].getWidth()  * SPRITE_SCALE);
        int idleW  = idleFrames != null ? (int)(idleFrames[0].getWidth() * SPRITE_SCALE) : SPRITE_W;
        return ACTION_X_BASE - (labelW - idleW) / 2;
    }

    private void restoreIdle() { startHeroIdleAnimation(); }

    // ════════════════════════════════════════════════════════════════════════
    // ★ BLADE RUSH
    // ════════════════════════════════════════════════════════════════════════
    private void playBladeRushAnimation(Runnable onDone) {
        if (heroIdleTimer != null && heroIdleTimer.isRunning()) heroIdleTimer.stop();
        if (bladeRushFrames == null) { startHeroIdleAnimation(); if (onDone != null) onDone.run(); return; }
        isPlayingBladeRush = true; heroSpriteFrame = 0;
        int lW = (int)(bladeRushFrames[0].getWidth()  * SPRITE_SCALE);
        int lH = (int)(bladeRushFrames[0].getHeight() * SPRITE_SCALE);
        heroSpriteLabel.setBounds(getActionX(bladeRushFrames), ACTION_Y, lW, lH);
        heroSpriteLabel.repaint();
        int[] frame = {0};
        javax.swing.Timer t = new javax.swing.Timer(130, null);
        t.addActionListener(e -> {
            if (frame[0] < BLADE_RUSH_FRAME_COUNT) { heroSpriteFrame = frame[0]++; heroSpriteLabel.repaint(); }
            else { t.stop(); isPlayingBladeRush = false; restoreIdle(); if (onDone != null) onDone.run(); }
        });
        t.start();
    }

    // ════════════════════════════════════════════════════════════════════════
    // ★ PIERCING SLASH
    // ════════════════════════════════════════════════════════════════════════
    private void playPiercingSlashAnimation(Runnable onDone) {
        if (heroIdleTimer != null && heroIdleTimer.isRunning()) heroIdleTimer.stop();
        if (piercingSlashFrames == null) { startHeroIdleAnimation(); if (onDone != null) onDone.run(); return; }
        isPlayingPiercingSlash = true; heroSpriteFrame = 0;
        int lW = (int)(piercingSlashFrames[0].getWidth()  * SPRITE_SCALE);
        int lH = (int)(piercingSlashFrames[0].getHeight() * SPRITE_SCALE);
        heroSpriteLabel.setBounds(getActionX(piercingSlashFrames), ACTION_Y, lW, lH);
        heroSpriteLabel.repaint();
        int[] frame = {0};
        javax.swing.Timer t = new javax.swing.Timer(110, null);
        t.addActionListener(e -> {
            if (frame[0] < PIERCING_SLASH_FRAME_COUNT) { heroSpriteFrame = frame[0]++; heroSpriteLabel.repaint(); }
            else { t.stop(); isPlayingPiercingSlash = false; restoreIdle(); if (onDone != null) onDone.run(); }
        });
        t.start();
    }

    // ════════════════════════════════════════════════════════════════════════
    // ★ ETERNAL CROSS SLASH
    // ════════════════════════════════════════════════════════════════════════
    private void playEternalCrossSlashAnimation(Runnable onDone) {
        if (heroIdleTimer != null && heroIdleTimer.isRunning()) heroIdleTimer.stop();
        if (eternalCrossFrames == null) { startHeroIdleAnimation(); if (onDone != null) onDone.run(); return; }
        isPlayingEternalCross = true; heroSpriteFrame = 0;
        int lW = (int)(eternalCrossFrames[0].getWidth()  * SPRITE_SCALE);
        int lH = (int)(eternalCrossFrames[0].getHeight() * SPRITE_SCALE);
        heroSpriteLabel.setBounds(getActionX(eternalCrossFrames), ACTION_Y, lW, lH);
        heroSpriteLabel.repaint();
        int[] frame = {0}, repeat = {0};
        javax.swing.Timer t = new javax.swing.Timer(60, null);
        t.addActionListener(e -> {
            if (frame[0] < ETERNAL_CROSS_FRAME_COUNT) { heroSpriteFrame = frame[0]++; heroSpriteLabel.repaint(); }
            else {
                repeat[0]++;
                if (repeat[0] < 2) { frame[0] = 0; heroSpriteFrame = 0; heroSpriteLabel.repaint(); }
                else { t.stop(); isPlayingEternalCross = false; restoreIdle(); if (onDone != null) onDone.run(); }
            }
        });
        t.start();
    }

    // ════════════════════════════════════════════════════════════════════════
    // ★ PLAYER ACTION
    // ════════════════════════════════════════════════════════════════════════
    private void onPlayerAction(BattleLogic.BattleAction action) {
        if (animating) return;
        if (engine.getCurrentTurn() != TurnOwner.PLAYER) return;
        animating = true;
        setActionsEnabled(false);
        clearLog();
        setTurnLabel(true);

        ActionResult pr = engine.playerAction(action);
        refreshBattleUI();

        Runnable afterHeroAnim = () -> {
            boolean enemyDied = engine.checkOutcome() == BattleOutcome.VICTORY;

            if (enemyDied) {
                playEnemyHurt(() -> {
                    clearLog();
                    if (pr != null) addLogFromResult(pr, true);
                    playEnemyDefeat(() -> {
                        handleVictory();
                        animating = false;
                    });
                });
            } else {
                playEnemyHurt(() -> {
                    clearLog();
                    if (pr != null) addLogFromResult(pr, true);
                    Timer t1 = new Timer(900, e -> {
                        engine.advanceToEnemyTurn();
                        setTurnLabel(false);
                        playEnemyAttack(() -> {
                            ActionResult er = engine.enemyTurn();
                            refreshBattleUI();
                            playKaelHurtAnimation(() -> {
                                clearLog();
                                if (er != null) addEnemyAttackLog(er);
                                if (engine.checkOutcome() == BattleOutcome.DEFEAT) {
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

        if (action == BattleLogic.BattleAction.SKILL1)        playBladeRushAnimation(afterHeroAnim);
        else if (action == BattleLogic.BattleAction.SKILL2)   playPiercingSlashAnimation(afterHeroAnim);
        else if (action == BattleLogic.BattleAction.ULTIMATE) playEternalCrossSlashAnimation(afterHeroAnim);
        else {
            if (pr != null) addLogFromResult(pr, true);
            if (engine.checkOutcome() == BattleOutcome.VICTORY) { handleVictory(); animating = false; return; }
            Timer t1 = new Timer(900, e -> {
                engine.advanceToEnemyTurn(); setTurnLabel(false);
                playEnemyAttack(() -> {
                    ActionResult er = engine.enemyTurn(); refreshBattleUI();
                    playKaelHurtAnimation(() -> {
                        clearLog(); if (er != null) addEnemyAttackLog(er);
                        if (engine.checkOutcome() == BattleOutcome.DEFEAT) handleDefeat();
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

    private void addEnemyAttackLog(ActionResult r) {
        if (enemyDef == null) { addLogFromResult(r, false); return; }
        String msg = switch (enemyDef.name) {
            case "Rotfang Wolf"  -> "Rotfang Wolf uses Savage Howl";
            case "Shade Sprite"  -> "Shade Sprite uses Trickster Strike";
            default              -> null;
        };
        if (msg != null) {
            if (r.logMessage != null && !r.logMessage.isEmpty()) {
                java.util.regex.Matcher m = java.util.regex.Pattern.compile("\\d+").matcher(r.logMessage);
                msg += m.find() ? " and dealt " + m.group() + " damage!" : "!";
            } else { msg += "!"; }
            addLog(msg, new Color(160, 20, 20));
            if (r.dotDamageApplied > 0) addLog("Burn tick: " + r.dotDamageApplied, new Color(150, 60, 0));
        } else {
            addLogFromResult(r, false);
        }
    }

    private void addWolfAttackLog(ActionResult r) { addEnemyAttackLog(r); }

    private void setTurnLabel(boolean isPlayer) {
        if (turnLabel == null) return;
        turnLabel.setText(isPlayer ? "YOUR TURN" : "ENEMY TURN");
        turnLabel.setForeground(isPlayer ? GREEN : RED);
    }

    private void handleDefeat() {
        addLog("You have fallen...", RED);
        if (engine.isPhoenixSoulstoneAvailable()) {
            engine.attemptRevive(); clearLog();
            addLog("Phoenix Soulstone activated! Revived!", GREEN);
            refreshBattleUI(); setTurnLabel(true); setActionsEnabled(true); animating = false; return;
        }
        if (!engine.isReviveUsed()) {
            JOptionPane.showInputDialog(this, "Q: What keyword is used to inherit a class in Java?");
            String ans = JOptionPane.showInputDialog(this, "Answer:");
            if (ans != null && ans.trim().equalsIgnoreCase("extends")) {
                engine.confirmRevive(); clearLog();
                addLog("Correct! Revived at 50% HP!", GREEN);
                refreshBattleUI(); setTurnLabel(true); setActionsEnabled(true); animating = false; return;
            }
        }
        showResult(false);
    }

    private enum PostVictoryStep { NONE, LOOT, LEVEL_UP_ANNOUNCE, LEVEL_UP_STATS, LEVEL_UP_RESTORE, OBJECTIVE, VICTORY_FLAVOUR, LOOT_FLAVOUR }
    private PostVictoryStep postVictoryStep = PostVictoryStep.NONE;
    private int lvlUp_level, lvlUp_hpGain, lvlUp_newHp, lvlUp_atkGain, lvlUp_newAtk, lvlUp_defGain, lvlUp_newDef;
    private EnemyDefinition postVictoryEnemy = null;
    private Runnable postVictoryNext = null;
    private Font normalLogFont = null, smallLogFont = null;

    private void initLogFonts() {
        if (normalLogFont != null) return;
        try {
            java.io.InputStream fs = getClass().getResourceAsStream("/assets/AssetFont/Pixelari.ttf");
            if (fs != null) { Font base = Font.createFont(Font.TRUETYPE_FONT, fs); normalLogFont = base.deriveFont(Font.BOLD, 19f); smallLogFont = base.deriveFont(Font.BOLD, 14f); }
        } catch (Exception ex) { }
        if (normalLogFont == null) normalLogFont = new Font("Dialog", Font.BOLD, 16);
        if (smallLogFont  == null) smallLogFont  = new Font("Dialog", Font.BOLD, 12);
    }
    private void setLogFontSmall()  { initLogFonts(); logArea.setFont(smallLogFont); }
    private void setLogFontNormal() { initLogFonts(); logArea.setFont(normalLogFont); }
    private void setLogFontSize(int size) { logArea.setFont(logArea.getFont().deriveFont((float) size)); }

    private void onContinuePressed() {
        if (postVictoryStep == PostVictoryStep.NONE) return;
        battleContinueBtn.setEnabled(false);
        switch (postVictoryStep) {
            case LOOT -> { clearLog(); if (lvlUp_level > 0) { postVictoryStep = PostVictoryStep.LEVEL_UP_ANNOUNCE; setLogFontSmall(); addLog("✨ LEVEL UP! You are now Level " + lvlUp_level + "! ✨", new Color(130, 85, 0)); } else { postVictoryStep = PostVictoryStep.OBJECTIVE; setLogFontNormal(); addLog(getObjectiveCompleteText(postVictoryEnemy), GOLD); } battleContinueBtn.setEnabled(true); }
            case LEVEL_UP_ANNOUNCE -> { postVictoryStep = PostVictoryStep.LEVEL_UP_STATS; clearLog(); addLog("💚 Max HP  : +" + lvlUp_hpGain + "  →  " + lvlUp_newHp, new Color(0, 120, 50)); addLog("⚔  Max ATK : +" + lvlUp_atkGain + "  →  " + lvlUp_newAtk, new Color(140, 70, 0)); addLog("🛡  DEF     : +" + lvlUp_defGain + "  →  " + lvlUp_newDef, new Color(20, 70, 160)); battleContinueBtn.setEnabled(true); }
            case LEVEL_UP_STATS -> { postVictoryStep = PostVictoryStep.LEVEL_UP_RESTORE; clearLog(); addLog("50% of HP & Stamina Restored!", new Color(0, 100, 40)); battleContinueBtn.setEnabled(true); }
            case LEVEL_UP_RESTORE -> { postVictoryStep = PostVictoryStep.OBJECTIVE; setLogFontNormal(); clearLog(); addLog(getObjectiveCompleteText(postVictoryEnemy), GOLD); battleContinueBtn.setEnabled(true); }
            case OBJECTIVE -> { postVictoryStep = PostVictoryStep.VICTORY_FLAVOUR; clearLog(); addLog(getVictoryFlavourText(postVictoryEnemy), GREEN); battleContinueBtn.setEnabled(true); }
            case VICTORY_FLAVOUR -> { postVictoryStep = PostVictoryStep.LOOT_FLAVOUR; clearLog(); addLog(getLootFlavourText(postVictoryEnemy), new Color(100, 65, 10)); battleContinueBtn.setEnabled(true); }
            case LOOT_FLAVOUR -> { postVictoryStep = PostVictoryStep.NONE; postVictoryEnemy = null; clearLog(); setLogFontNormal(); if (postVictoryNext != null) { Runnable next = postVictoryNext; postVictoryNext = null; next.run(); } }
        }
    }

    private void startPostVictorySequence(EnemyDefinition eDef, Runnable onDone) {
        postVictoryEnemy = eDef; postVictoryNext = onDone; postVictoryStep = PostVictoryStep.LOOT; lvlUp_level = 0;
        if (engine != null) { int xp = eDef.xpReward * eDef.count; engine.getHero().gainExp(xp); if (engine.getHero().lastLevelUpMsg != null) parseLevelUpMsg(engine.getHero()); }
        setLogFontSmall(); clearLog(); addLootText(eDef); battleContinueBtn.setEnabled(true);
        turnLabel.setText(""); roundLabel.setText(""); specialCdLabel.setText("");
        stopEnemyAnimation();
    }

    private void addLootText(EnemyDefinition eDef) {
        addLog("You received:", GOLD);
        int shards = eDef.name.equals("The Hollow Stag") ? 10 : 1;
        addLog("  " + shards + " Soul Shard" + (shards > 1 ? "s" : ""), new Color(20, 80, 160));
        addLog("  +" + (eDef.xpReward * eDef.count) + " XP", new Color(140, 90, 0));
    }

    private void parseLevelUpMsg(BattleLogic.Combatant hero) {
        if (hero.lastLevelUpMsg == null) return;
        String[] p = hero.lastLevelUpMsg.split("\\|"); hero.lastLevelUpMsg = null;
        if (p.length < 8) return;
        lvlUp_level = Integer.parseInt(p[1]); lvlUp_hpGain = Integer.parseInt(p[2]); lvlUp_newHp = Integer.parseInt(p[3]);
        lvlUp_atkGain = Integer.parseInt(p[4]); lvlUp_newAtk = Integer.parseInt(p[5]); lvlUp_defGain = Integer.parseInt(p[6]); lvlUp_newDef = Integer.parseInt(p[7]);
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
                    delay(800, () -> playEnemyEntrance(() -> startNextFight()));
                });
            } else {
                savedHeroCombatant = engine.getHero();
                enemySequenceIndex++;
                enemyFightIndex = 0;
                setActionsEnabled(false);

                Runnable afterSequence;
                if (enemySequenceIndex < enemySequence.size()) {
                    if (onEnemyGroupDefeated != null) {
                        int idx = enemySequenceIndex;
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

    private String getPerKillMessage(EnemyDefinition eDef, int killed) {
        String n = eDef.name, score = killed + "/" + eDef.count;
        return switch (n) {
            case "Rotfang Wolf"    -> "The wolf whimpers and dissolves into black smoke. (" + score + ")";
            case "Shade Sprite"    -> "You dispelled the Shade Sprite! (" + score + ")";
            case "Dreadbark Treant"-> "You felled the Dreadbark Treant! (" + score + ")";
            case "Carrion Bat"     -> "You slayed the Carrion Bat! (" + score + ")";
            case "The Hollow Stag" -> "The Hollow Stag has fallen! (" + score + ")";
            default -> n + " defeated! (" + score + ")";
        };
    }

    private String getNextApproachMessage(EnemyDefinition eDef, int killed) {
        String n = eDef.name; int next = killed + 1;
        return switch (n) {
            case "Rotfang Wolf"    -> "Another wolf snarls and steps forward! (" + next + "/" + eDef.count + ")";
            case "Shade Sprite"    -> "The mist swirls — another soul screams into existence! (" + next + "/" + eDef.count + ")";
            case "Dreadbark Treant"-> "The ground quakes again! The second ancient giant lumbers forward! (" + next + "/" + eDef.count + ")";
            case "Carrion Bat"     -> "Another screech echoes above — the swarm continues! (" + next + "/" + eDef.count + ")";
            default -> "Another " + n + " approaches! (" + next + "/" + eDef.count + ")";
        };
    }

    private String getGroupClearMessage(EnemyDefinition eDef) {
        return switch (eDef.name) {
            case "Rotfang Wolf"    -> "Victory! The last Rotfang Wolf collapses. The forest feels no safer.";
            case "Shade Sprite"    -> "The sprites disperse like fog in the wind. The whispering stops.";
            case "Dreadbark Treant"-> "The Treants collapse. Where they fall, green sprouts rise from ash.";
            case "Carrion Bat"     -> "The last bat crashes down. The stench of decay lifts into the cold wind.";
            case "The Hollow Stag" -> "MINI-BOSS DEFEATED! The Stag dissolves into particles of pure light.";
            default -> eDef.name + " group cleared!";
        };
    }

    private void showResult(boolean victory) {
        resultIcon.setText(victory ? "WIN" : "LOSE"); resultTitle.setText(victory ? "VICTORY" : "DEFEAT");
        resultTitle.setForeground(victory ? GOLD : RED); resultSub.setText(victory ? heroDef.name + " wins!" : heroDef.name + " has fallen...");
        resultOverlay.setBounds(0, 0, getWidth(), getHeight()); setComponentZOrder(resultOverlay, 0);
        resultOverlay.setVisible(true); revalidate(); repaint();
    }

    private JPanel buildResultOverlay() {
        JPanel overlay = new JPanel(new GridBagLayout()); overlay.setBackground(OVERLAY_BG); overlay.setOpaque(true);
        JPanel card = new JPanel(); card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS)); card.setBackground(new Color(20, 18, 36));
        card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(GOLD_DIM, 1), new EmptyBorder(36, 50, 36, 50)));
        resultIcon = new JLabel("WIN", SwingConstants.CENTER); resultIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64)); resultIcon.setAlignmentX(CENTER_ALIGNMENT);
        resultTitle = new JLabel("VICTORY", SwingConstants.CENTER); resultTitle.setFont(FONT_RESULT); resultTitle.setForeground(GOLD); resultTitle.setAlignmentX(CENTER_ALIGNMENT);
        resultSub = new JLabel(" ", SwingConstants.CENTER); resultSub.setFont(new Font("Monospaced", Font.ITALIC, 13)); resultSub.setForeground(TEXT_DIM); resultSub.setAlignmentX(CENTER_ALIGNMENT); resultSub.setBorder(new EmptyBorder(4, 0, 24, 0));
        JButton restartBtn = new JButton("Fight Again"); restartBtn.setFont(FONT_BTN); restartBtn.setForeground(new Color(20,15,5)); restartBtn.setBackground(new Color(120,92,24));
        restartBtn.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(GOLD,1),new EmptyBorder(10,24,10,24))); restartBtn.setFocusPainted(false); restartBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); restartBtn.setAlignmentX(CENTER_ALIGNMENT);
        restartBtn.addActionListener(e -> { if (onRestartBattle != null) onRestartBattle.run(); });
        JButton backBtn2 = new JButton("New Champion"); backBtn2.setFont(FONT_BTN); backBtn2.setForeground(GOLD); backBtn2.setBackground(new Color(22,20,38));
        backBtn2.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(GOLD_DIM,1),new EmptyBorder(10,24,10,24))); backBtn2.setFocusPainted(false); backBtn2.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); backBtn2.setAlignmentX(CENTER_ALIGNMENT);
        backBtn2.addActionListener(e -> { if (onReturnToSelection != null) onReturnToSelection.run(); });
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0)); btns.setBackground(new Color(20,18,36)); btns.add(restartBtn); btns.add(backBtn2); btns.setAlignmentX(CENTER_ALIGNMENT);
        card.add(resultIcon); card.add(Box.createVerticalStrut(8)); card.add(resultTitle); card.add(resultSub); card.add(btns); overlay.add(card); return overlay;
    }

    private void addLogFromResult(ActionResult r, boolean isPlayer) {
        Color c = isPlayer ? new Color(0, 110, 45) : new Color(160, 20, 20);
        if (r.isBerserk) c = new Color(160, 50, 10); if (r.wasDefend) c = BLUE; if (r.isSpecial) c = PURPLE;
        addLog(r.logMessage, c); if (r.dotDamageApplied > 0) addLog("Burn tick: " + r.dotDamageApplied, new Color(150, 60, 0));
    }

    private void addLog(String text, Color color) {
        javax.swing.text.StyledDocument doc = logArea.getStyledDocument();
        javax.swing.text.SimpleAttributeSet attrs = new javax.swing.text.SimpleAttributeSet();
        javax.swing.text.StyleConstants.setForeground(attrs, color); javax.swing.text.StyleConstants.setFontFamily(attrs, logArea.getFont().getFamily());
        javax.swing.text.StyleConstants.setFontSize(attrs, logArea.getFont().getSize()); javax.swing.text.StyleConstants.setBold(attrs, true);
        try { doc.insertString(doc.getLength(), text + "\n", attrs); } catch (javax.swing.text.BadLocationException e) { }
        logArea.setCaretPosition(doc.getLength());
    }

    private void clearLog() { logArea.setText(""); }

    private void setActionsEnabled(boolean enabled) {
        skill1Btn.setEnabled(enabled && (engine == null || engine.canUseSkill1()));
        skill2Btn.setEnabled(enabled && (engine == null || engine.canUseSkill2()));
        skipTurnBtn.setEnabled(enabled);
        ultimateBtn.setEnabled(enabled && engine != null && engine.canUseUltimate());
        // ★ Update ultimate icon to reflect cooldown state whenever actions are toggled
        updateUltimateIcon();
    }

    // ════════════════════════════════════════════════════════════════════════
    // ★ FALLBACK plain-color skill button (used if image file is missing)
    // ════════════════════════════════════════════════════════════════════════
    private JButton makeSkillBtnFallback(String label, Color bg, Color border, int x, int y, int w, int h) {
        JButton btn = new JButton(label); btn.setBounds(x, y, w, h); btn.setFont(new Font("Monospaced", Font.BOLD, 12));
        btn.setForeground(Color.WHITE); btn.setBackground(bg); btn.setFocusPainted(false); btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(border, 2), BorderFactory.createEmptyBorder(6, 8, 6, 8)));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { if (btn.isEnabled()) btn.setBackground(border.darker()); }
            @Override public void mouseExited(MouseEvent e)  { btn.setBackground(bg); }
        });
        return btn;
    }

    private void delay(int ms, Runnable action) {
        javax.swing.Timer t = new javax.swing.Timer(ms, null); t.setRepeats(false);
        t.addActionListener(e -> { t.stop(); action.run(); }); t.start();
    }

    public void showResultOverlay(JPanel overlay) {
        add(overlay); setComponentZOrder(overlay, 0); overlay.setBounds(0, 0, getWidth(), getHeight()); overlay.setVisible(true); repaint();
    }

    private JButton makeBtn(String normalPath, String hoverPath, int x, int y, int w, int h, String fallback, int hoverOffset) {
        JButton btn = new JButton(); btn.setBounds(x, y, w, h); btn.setOpaque(false); btn.setContentAreaFilled(false); btn.setBorderPainted(false); btn.setFocusPainted(false); btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        java.net.URL nUrl = getClass().getResource(normalPath); java.net.URL hUrl = getClass().getResource(hoverPath);
        if (nUrl != null) {
            ImageIcon ni = new ImageIcon(new ImageIcon(nUrl).getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH));
            ImageIcon hi = hUrl != null ? new ImageIcon(new ImageIcon(hUrl).getImage().getScaledInstance(w + hoverOffset, h + hoverOffset, Image.SCALE_SMOOTH)) : ni;
            btn.setIcon(ni);
            btn.addMouseListener(new MouseAdapter() { @Override public void mouseEntered(MouseEvent e) { btn.setIcon(hi); } @Override public void mouseExited(MouseEvent e) { btn.setIcon(ni); } });
        } else { btn.setText(fallback); btn.setForeground(Color.WHITE); btn.setContentAreaFilled(true); btn.setBackground(BG_PANEL); }
        return btn;
    }

    private JPanel buildStatCard(boolean isHero) {
        final Color cardBg = new Color(10, 9, 20, 220), cardBorder = isHero ? GREEN_DARK : RED_DARK;
        JPanel card = new JPanel(null) {
            @Override public boolean isOptimizedDrawingEnabled() { return false; }
            @Override protected void paintComponent(Graphics g) { Graphics2D g2 = (Graphics2D) g.create(); g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); g2.setColor(cardBg); g2.fillRect(0,0,getWidth(),getHeight()); g2.setColor(cardBorder); g2.drawRect(0,0,getWidth()-1,getHeight()-1); g2.dispose(); }
            @Override protected void paintChildren(Graphics g) { Graphics2D g2 = (Graphics2D) g.create(); g2.setClip(0,0,getWidth(),getHeight()); super.paintChildren(g2); g2.dispose(); }
        };
        card.setOpaque(false);
        JLabel emoji = new JLabel("", SwingConstants.CENTER);
        emoji.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        emoji.setBounds(4, 4, 40, 40);
        card.add(emoji);
        JLabel name = new JLabel(""); name.setFont(new Font("Monospaced", Font.BOLD, 11)); name.setForeground(isHero ? GREEN : RED); name.setBounds(50,4,215,16); card.add(name);
        JLabel lvlLbl = new JLabel("Lv.1"); lvlLbl.setFont(new Font("Monospaced", Font.BOLD, 10)); lvlLbl.setForeground(GOLD); lvlLbl.setBounds(270,4,60,16); card.add(lvlLbl);
        JLabel role = new JLabel(""); role.setFont(new Font("Monospaced", Font.ITALIC, 9)); role.setForeground(TEXT_DIM); role.setBounds(50,20,215,14); card.add(role);
        JLabel hpLbl = new JLabel("HP"); hpLbl.setFont(FONT_STAT); hpLbl.setForeground(TEXT_DIM); hpLbl.setBounds(4,38,40,14); card.add(hpLbl);
        JProgressBar hpBar = new JProgressBar(0,100); hpBar.setValue(100); hpBar.setForeground(isHero ? GREEN : RED); hpBar.setBackground(new Color(30,28,50)); hpBar.setBorder(null); hpBar.setBounds(50,38,175,8); card.add(hpBar);
        JLabel hpText = new JLabel("—"); hpText.setFont(FONT_STAT); hpText.setForeground(TEXT_BRIGHT); hpText.setBounds(228,34,62,16); card.add(hpText);
        JLabel statusLbl = new JLabel(" "); statusLbl.setFont(new Font("Monospaced", Font.BOLD, 9)); statusLbl.setForeground(BLUE); statusLbl.setBounds(50,72,215,14); card.add(statusLbl);
        if (isHero) {
            heroEmojiLbl=emoji; heroNameLbl=name; heroRoleLbl=role; heroLvlLbl=lvlLbl; heroHpBar=hpBar; heroHpText=hpText; heroStatusLbl=statusLbl;
            JLabel epLbl = new JLabel("EP"); epLbl.setFont(FONT_STAT); epLbl.setForeground(TEXT_DIM); epLbl.setBounds(4,54,40,14); card.add(epLbl);
            JProgressBar energyBar = new JProgressBar(0,100); energyBar.setValue(100); energyBar.setForeground(new Color(200,180,80)); energyBar.setBackground(new Color(30,28,50)); energyBar.setBorder(null); energyBar.setBounds(50,54,175,6); card.add(energyBar);
            JLabel energyText = new JLabel("—"); energyText.setFont(FONT_STAT); energyText.setForeground(TEXT_BRIGHT); energyText.setBounds(228,50,62,14); card.add(energyText);
            heroEnergyBar=energyBar; heroEnergyText=energyText;
        } else {
            enemyEmojiLbl=emoji; enemyNameLbl=name; enemyRoleLbl=role;
            enemyHpBar=hpBar; enemyHpText=hpText; enemyStatusLbl=statusLbl;
        }
        return card;
    }

    private void populateCombatantUI() {
        Combatant h = engine.getHero(), e = engine.getEnemy();
        heroEmojiLbl.setText(heroDef.emoji); heroNameLbl.setText(heroDef.name); heroRoleLbl.setText(heroDef.role);
        if (heroLvlLbl != null) heroLvlLbl.setText("Lv." + h.level);
        String enemyEmoji = switch (enemyDef.name) {
            case "Rotfang Wolf" -> "🐾";
            case "Shade Sprite" -> "👻";
            default -> enemyDef.emoji;
        };
        enemyEmojiLbl.setText(enemyEmoji);
        enemyNameLbl.setText(enemyDef.name); enemyRoleLbl.setText(enemyDef.role);
        heroHpBar.setMaximum(h.maxHp); enemyHpBar.setMaximum(e.maxHp);
        if (heroEnergyBar != null) heroEnergyBar.setMaximum(h.maxEnergy);
        // Note: image buttons don't need text updates from skills[]
        updateEnergyLabel(switch (heroDef.role) { case "Swordsman" -> "ST"; case "Archer" -> "AR"; case "Mage" -> "MP"; default -> "EP"; });
    }

    private void updateEnergyLabel(String label) { }

    private void refreshBattleUI() {
        Combatant h = engine.getHero(), e = engine.getEnemy();
        heroHpBar.setValue(h.currentHp); heroHpText.setText(h.currentHp + "/" + h.maxHp);
        if (heroLvlLbl != null) heroLvlLbl.setText("Lv." + h.level);
        enemyHpBar.setValue(e.currentHp); enemyHpText.setText(e.currentHp + "/" + e.maxHp);
        if (heroEnergyBar != null) { heroEnergyBar.setValue(h.energy); heroEnergyText.setText(h.energy + "/" + h.maxEnergy); }
        roundLabel.setText("Round " + engine.getRound());
        int cd = h.specialCooldown;
        ultimateBtn.setEnabled(cd == 0 && (engine == null || engine.canUseUltimate()));
        specialCdLabel.setText(cd > 0 ? "CD: " + cd : "");
        // ★ Swap ultimate icon based on cooldown
        updateUltimateIcon();
        skill1Btn.setEnabled(engine == null || engine.canUseSkill1());
        skill2Btn.setEnabled(engine == null || engine.canUseSkill2());
        heroStatusLbl.setText(h.defending ? "Defending" : " ");
        enemyStatusLbl.setText(e.defending ? "Defending" : " ");
    }
}