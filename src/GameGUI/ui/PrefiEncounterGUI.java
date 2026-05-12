package GameGUI.ui;

import GameGUI.model.entity.base.Combatant;
import GameGUI.model.entity.data.WeaponDef;
import GameGUI.model.equipment.Armor;
import GameGUI.model.equipment.Bow;
import GameGUI.model.equipment.Staff;
import GameGUI.model.equipment.Sword;
import GameGUI.model.equipment.Weapon;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.io.File;

/**
 * PrefiEncounterGUI — redesigned to match PlayerStatsDialog's
 * dark-brown / gold aesthetic, using Pixelari as the display font.
 */
public class PrefiEncounterGUI extends JPanel {

    // ── Palette (mirrors PlayerStatsDialog) ───────────────────────────────────
    private static final Color C_BG           = new Color(15,  15,  15);
    private static final Color C_PANEL        = new Color(25,  25,  25);
    private static final Color C_CARD         = new Color(30,  25,  15);
    private static final Color C_CARD2        = new Color(22,  18,  10);
    private static final Color C_GOLD         = new Color(160, 140, 90);
    private static final Color C_GOLD_BRIGHT  = new Color(220, 190, 110);
    private static final Color C_GOLD_BORDER  = new Color(80,  70,  40);
    private static final Color C_GOLD_BORDER2 = new Color(100, 85,  45);
    private static final Color C_TEXT         = new Color(210, 210, 200);
    private static final Color C_TEXT_DIM     = new Color(130, 120, 100);
    private static final Color C_GREEN        = new Color(100, 200, 100);
    private static final Color C_GREEN_BG     = new Color(10,  28,  10);
    private static final Color C_GREEN_BDR    = new Color(40,  100, 40);
    private static final Color C_RED          = new Color(200, 80,  80);
    private static final Color C_RED_BG       = new Color(28,  10,  10);
    private static final Color C_RED_BDR      = new Color(100, 30,  30);
    private static final Color C_BLUE         = new Color(90,  150, 220);
    private static final Color C_BLUE_BDR     = new Color(30,  60,  100);

    // ── Fonts ─────────────────────────────────────────────────────────────────
    private static Font PIXEL_18, PIXEL_15, PIXEL_13, PIXEL_12, PIXEL_11, PIXEL_10;
    private static final Font FALLBACK_TITLE = new Font("Georgia", Font.BOLD,   16);
    private static final Font FALLBACK_BODY  = new Font("Georgia", Font.PLAIN,  12);
    private static final Font FALLBACK_SMALL = new Font("Georgia", Font.ITALIC, 11);

    static {
        try {
            Font base = Font.createFont(Font.TRUETYPE_FONT,
                    new File("assets/AssetFont/Pixelari.ttf"));
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(base);
            PIXEL_18 = base.deriveFont(Font.PLAIN, 18f);
            PIXEL_15 = base.deriveFont(Font.PLAIN, 15f);
            PIXEL_13 = base.deriveFont(Font.PLAIN, 13f);
            PIXEL_12 = base.deriveFont(Font.PLAIN, 12f);
            PIXEL_11 = base.deriveFont(Font.PLAIN, 11f);
            PIXEL_10 = base.deriveFont(Font.PLAIN, 10f);
        } catch (Exception ex) {
            PIXEL_18 = FALLBACK_TITLE; PIXEL_15 = FALLBACK_TITLE;
            PIXEL_13 = FALLBACK_BODY;  PIXEL_12 = FALLBACK_BODY;
            PIXEL_11 = FALLBACK_BODY;  PIXEL_10 = FALLBACK_SMALL;
        }
    }

    // ── Quiz data ─────────────────────────────────────────────────────────────
    private static final String[] Q_TEXT = {
            "Kael, Karl, and Simon use the same attributes (HP, Level, Defense, etc.) " +
                    "defined in the Character class.\n\nWhich OOP concept is this?",

            "Inventory prevents direct access to the item list; only methods modify it.\n\n" +
                    "Which OOP concept is this?",

            "The showBackstory() methods of Kael, Karl, and Simon override " +
                    "Character.showBackstory(), so calling them prints a unique backstory " +
                    "for each character.\n\nWhich OOP concept is this?",

            "Weapon is an abstract class. Sword, Bow, and Staff extend it and implement " +
                    "its methods differently, but all share the same abstract interface defined " +
                    "by Weapon.\n\nWhich OOP concept is this?"
    };
    private static final String[][] Q_OPTIONS = {
            {"Polymorphism", "Encapsulation", "Inheritance", "Abstraction"},
            {"Polymorphism", "Encapsulation", "Inheritance", "Abstraction"},
            {"Abstraction",  "Polymorphism",  "Encapsulation", "Inheritance"},
            {"Encapsulation", "Abstraction",  "Inheritance",  "Polymorphism"}
    };
    private static final int[] Q_CORRECT  = {2, 1, 1, 1};
    private static final int   TIME_LIMIT = 15;

    // ── Screen names ──────────────────────────────────────────────────────────
    private static final String SCREEN_INTRO  = "intro";
    private static final String SCREEN_QUIZ   = "quiz";
    private static final String SCREEN_FAIL   = "fail";
    private static final String SCREEN_PASS   = "pass";
    private static final String SCREEN_REWARD = "reward";

    // ── State ─────────────────────────────────────────────────────────────────
    private final Object   player;
    private final Runnable onFinished;

    private int     currentQuestion = 0;
    private int     timeLeft        = TIME_LIMIT;
    private Timer   countdownTimer;
    private boolean weaponFlipped   = false;
    private boolean armorFlipped    = false;
    private Weapon  legendaryWeapon;
    private Armor   legendaryArmor;

    // ── Layout ────────────────────────────────────────────────────────────────
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel     cardPanel  = new JPanel(cardLayout);

    // ── Quiz widgets ──────────────────────────────────────────────────────────
    private JLabel    qCounterLabel;
    private JTextArea qTextArea;
    private JPanel    qOptionsPanel;
    private JLabel    timerLabel;
    private JPanel    timerBarPanel;
    private JPanel    qPipsPanel;
    private JPanel    qFeedbackPanel;

    // ── Reward widgets ────────────────────────────────────────────────────────
    private JPanel rewardChoiceArea;
    private JPanel rewardConfirmArea;
    private JLabel rewardConfirmDesc;
    private JPanel flipPaneWeapon;
    private JPanel flipPaneArmor;
    private JPanel weaponBack;
    private JPanel armorBack;

    // ── Constructor ───────────────────────────────────────────────────────────
    public PrefiEncounterGUI(Object player, Runnable onFinished) {
        this.player     = player;
        this.onFinished = onFinished;

        setLayout(new BorderLayout());
        setBackground(C_BG);
        setPreferredSize(new Dimension(620, 700));

        // Outer gold border frame (same style as PlayerStatsDialog root)
        JPanel frame = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(C_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(C_GOLD);
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 20, 20);
            }
        };
        frame.setOpaque(false);
        frame.setBorder(BorderFactory.createEmptyBorder(20, 28, 20, 28));

        cardPanel.setBackground(C_BG);
        frame.add(cardPanel, BorderLayout.CENTER);
        add(frame, BorderLayout.CENTER);

        buildScreens();
        showScreen(SCREEN_INTRO);
    }

    // =========================================================================
    //  SCREEN BUILDER
    // =========================================================================

    private void buildScreens() {
        cardPanel.add(buildIntroScreen(),  SCREEN_INTRO);
        cardPanel.add(buildQuizScreen(),   SCREEN_QUIZ);
        cardPanel.add(buildFailScreen(),   SCREEN_FAIL);
        cardPanel.add(buildPassScreen(),   SCREEN_PASS);
        cardPanel.add(buildRewardScreen(), SCREEN_REWARD);
    }

    private void showScreen(String name) {
        cardLayout.show(cardPanel, name);
        cardPanel.revalidate();
        cardPanel.repaint();
    }

    // =========================================================================
    //  INTRO SCREEN
    // =========================================================================

    private JScrollPane buildIntroScreen() {
        JPanel content = vBox();
        content.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        content.add(screenTitle("THE TRIAL OF PREFI"));
        content.add(vGap(4));
        content.add(subtitle("An ancient encounter awaits"));
        content.add(vGap(16));
        content.add(goldDivider());
        content.add(vGap(16));

        content.add(quoteCard(
                "\"Before you stands the final threshold. To claim a legendary artifact, " +
                        "you must first prove your command of the arcane disciplines that govern all things...\""
        ));
        content.add(vGap(16));
        content.add(rulesCard());
        content.add(vGap(20));
        content.add(primaryBtn("ENTER THE TRIAL", e -> startQuiz()));
        content.add(vGap(8));
        content.add(ghostBtn("Walk Away", e -> showScreen(SCREEN_FAIL)));

        return scrollWrap(content);
    }

    private JPanel rulesCard() {
        JPanel card = goldCard();
        card.add(sectionHeader("Trial Rules"));
        card.add(vGap(10));
        String[] rules = {
                "◆   4 questions on Object-Oriented Principles",
                "◆   15 seconds per question",
                "◆   One wrong answer ends the trial",
                "◆   Success grants a legendary artifact"
        };
        for (String r : rules) {
            JLabel l = new JLabel(r);
            l.setFont(PIXEL_12);
            l.setForeground(C_TEXT_DIM);
            l.setAlignmentX(Component.LEFT_ALIGNMENT);
            card.add(l);
            card.add(vGap(4));
        }
        return card;
    }

    // =========================================================================
    //  QUIZ SCREEN
    // =========================================================================

    private JScrollPane buildQuizScreen() {
        JPanel content = vBox();
        content.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        content.add(screenTitle("OOP TRIAL"));
        content.add(vGap(12));

        // counter + pips row
        qCounterLabel = new JLabel("Question 1 of 4");
        qCounterLabel.setFont(PIXEL_11);
        qCounterLabel.setForeground(C_TEXT_DIM);
        qCounterLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(qCounterLabel);
        content.add(vGap(6));

        qPipsPanel = new JPanel(new GridLayout(1, Q_TEXT.length, 6, 0));
        qPipsPanel.setBackground(C_BG);
        qPipsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 6));
        qPipsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(qPipsPanel);
        content.add(vGap(8));

        // timer row
        JPanel timerRow = new JPanel(new BorderLayout());
        timerRow.setBackground(C_BG);
        timerRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        timerRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 18));
        timerLabel = new JLabel("15s remaining");
        timerLabel.setFont(PIXEL_10);
        timerLabel.setForeground(C_TEXT_DIM);
        timerRow.add(timerLabel, BorderLayout.EAST);
        content.add(timerRow);
        content.add(vGap(4));

        // timer bar
        timerBarPanel = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(40, 35, 20));
                g2.fillRoundRect(0, 0, getWidth(), 5, 4, 4);
                Color barColor = timeLeft <= 5 ? C_RED : C_GOLD;
                g2.setColor(barColor);
                int w = (int)(getWidth() * (timeLeft / (double)TIME_LIMIT));
                if (w > 0) g2.fillRoundRect(0, 0, w, 5, 4, 4);
            }
        };
        timerBarPanel.setBackground(C_BG);
        timerBarPanel.setPreferredSize(new Dimension(0, 5));
        timerBarPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 5));
        timerBarPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(timerBarPanel);
        content.add(vGap(14));

        // question card
        JPanel qCard = goldCard();
        qTextArea = new JTextArea();
        qTextArea.setFont(PIXEL_12);
        qTextArea.setForeground(C_TEXT);
        qTextArea.setBackground(new Color(30, 25, 15));
        qTextArea.setEditable(false);
        qTextArea.setLineWrap(true);
        qTextArea.setWrapStyleWord(true);
        qTextArea.setOpaque(false);
        qTextArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        qCard.add(qTextArea);
        qCard.add(vGap(12));

        qOptionsPanel = new JPanel();
        qOptionsPanel.setLayout(new BoxLayout(qOptionsPanel, BoxLayout.Y_AXIS));
        qOptionsPanel.setBackground(new Color(30, 25, 15));
        qOptionsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        qCard.add(qOptionsPanel);
        content.add(qCard);
        content.add(vGap(10));

        qFeedbackPanel = new JPanel();
        qFeedbackPanel.setLayout(new BoxLayout(qFeedbackPanel, BoxLayout.Y_AXIS));
        qFeedbackPanel.setBackground(C_BG);
        qFeedbackPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(qFeedbackPanel);

        return scrollWrap(content);
    }

    // =========================================================================
    //  FAIL SCREEN
    // =========================================================================

    private JScrollPane buildFailScreen() {
        JPanel content = vBox();
        content.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        content.add(screenTitle("TRIAL FAILED"));
        content.add(vGap(16));
        content.add(goldDivider());
        content.add(vGap(16));

        JPanel verdict = resultCard(C_RED_BG, C_RED_BDR);
        verdict.add(resultTitle("The Trial Ends", C_RED));
        verdict.add(vGap(8));
        verdict.add(resultBody("\"You lack the foundation required to wield true power.\""));
        content.add(verdict);
        content.add(vGap(16));

        content.add(quoteCard(
                "No legendary artifacts will be granted. The figure's form dissolves into violet smoke. " +
                        "You step forward… toward the Final Boss."
        ));
        content.add(vGap(20));
        content.add(goldDivider());
        content.add(vGap(16));
        content.add(primaryBtn("PROCEED TO FINAL BOSS", e -> finish()));

        return scrollWrap(content);
    }

    // =========================================================================
    //  PASS SCREEN
    // =========================================================================

    private JScrollPane buildPassScreen() {
        JPanel content = vBox();
        content.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        content.add(screenTitle("TRIAL COMPLETE"));
        content.add(vGap(16));
        content.add(goldDivider());
        content.add(vGap(16));

        JPanel verdict = resultCard(C_GREEN_BG, C_GREEN_BDR);
        verdict.add(resultTitle("Mastery Proven", C_GREEN));
        verdict.add(vGap(8));
        verdict.add(resultBody("\"Impressive… you demonstrated complete command of OOP.\""));
        content.add(verdict);
        content.add(vGap(16));

        content.add(quoteCard(
                "The figure raises both hands. Two artifacts swirl into existence from a burst of violet fire..."
        ));
        content.add(vGap(20));
        content.add(primaryBtn("CLAIM YOUR REWARD", e -> {
            resolveWeapon();
            showScreen(SCREEN_REWARD);
        }));

        return scrollWrap(content);
    }

    // =========================================================================
    //  REWARD SCREEN
    // =========================================================================

    private JScrollPane buildRewardScreen() {
        JPanel content = vBox();
        content.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        content.add(screenTitle("CHOOSE YOUR ARTIFACT"));
        content.add(vGap(4));
        content.add(subtitle("Tap a card to reveal — only one may be claimed"));
        content.add(vGap(14));
        content.add(goldDivider());
        content.add(vGap(14));

        // flip cards
        JPanel flipRow = new JPanel(new GridLayout(1, 2, 12, 0));
        flipRow.setBackground(C_BG);
        flipRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        flipRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 210));
        flipPaneWeapon = buildFlipCard(true);
        flipPaneArmor  = buildFlipCard(false);
        flipRow.add(flipPaneWeapon);
        flipRow.add(flipPaneArmor);
        content.add(flipRow);

        // choice area
        rewardChoiceArea = new JPanel();
        rewardChoiceArea.setLayout(new BoxLayout(rewardChoiceArea, BoxLayout.Y_AXIS));
        rewardChoiceArea.setBackground(C_BG);
        rewardChoiceArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        rewardChoiceArea.setVisible(false);

        JLabel choiceHint = new JLabel("Choose wisely — the other shall be destroyed", SwingConstants.CENTER);
        choiceHint.setFont(PIXEL_11);
        choiceHint.setForeground(C_TEXT_DIM);
        choiceHint.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel choiceBtns = new JPanel(new GridLayout(1, 2, 10, 0));
        choiceBtns.setBackground(C_BG);
        choiceBtns.setAlignmentX(Component.LEFT_ALIGNMENT);
        choiceBtns.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        JButton btnWeapon = primaryBtn("CLAIM WEAPON", e -> chooseArtifact(true));
        JButton btnArmor  = blueBtn("CLAIM ARMOR",  e -> chooseArtifact(false));
        choiceBtns.add(btnWeapon);
        choiceBtns.add(btnArmor);

        JButton btnDecline = ghostBtn("Walk Away", e -> declineReward());

        rewardChoiceArea.add(vGap(14));
        rewardChoiceArea.add(goldDivider());
        rewardChoiceArea.add(vGap(10));
        rewardChoiceArea.add(choiceHint);
        rewardChoiceArea.add(vGap(10));
        rewardChoiceArea.add(choiceBtns);
        rewardChoiceArea.add(vGap(6));
        rewardChoiceArea.add(btnDecline);

        // confirm area
        rewardConfirmArea = new JPanel();
        rewardConfirmArea.setLayout(new BoxLayout(rewardConfirmArea, BoxLayout.Y_AXIS));
        rewardConfirmArea.setBackground(C_BG);
        rewardConfirmArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        rewardConfirmArea.setVisible(false);

        JPanel confirmCard = goldCard();
        rewardConfirmDesc = new JLabel("<html></html>");
        rewardConfirmDesc.setFont(PIXEL_11);
        rewardConfirmDesc.setForeground(C_TEXT);
        rewardConfirmDesc.setAlignmentX(Component.LEFT_ALIGNMENT);
        confirmCard.add(rewardConfirmDesc);

        JButton btnProceed = primaryBtn("PROCEED TO FINAL BOSS", e -> finish());

        rewardConfirmArea.add(vGap(14));
        rewardConfirmArea.add(goldDivider());
        rewardConfirmArea.add(vGap(10));
        rewardConfirmArea.add(confirmCard);
        rewardConfirmArea.add(vGap(10));
        rewardConfirmArea.add(btnProceed);

        content.add(rewardChoiceArea);
        content.add(rewardConfirmArea);

        return scrollWrap(content);
    }

    // =========================================================================
    //  FLIP CARDS
    // =========================================================================

    private JPanel buildFlipCard(boolean isWeapon) {
        JPanel container = new JPanel(new CardLayout());
        container.setPreferredSize(new Dimension(0, 200));
        container.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        container.setBackground(C_BG);

        // FRONT
        JPanel front = new RoundedPanel(10, C_CARD2, C_GOLD_BORDER);
        front.setLayout(new BoxLayout(front, BoxLayout.Y_AXIS));
        front.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        JLabel runeGlyph = centredLbl(isWeapon ? "⚔  ᚦ ᚨ ᚱ" : "🛡  ᚲ ᚾ ᚹ", C_TEXT_DIM, PIXEL_11);
        JLabel qMark     = centredLbl("?", C_GOLD, new Font("Georgia", Font.BOLD, 36));
        JLabel typeLabel = centredLbl(isWeapon ? "Legendary Weapon" : "Legendary Armor", C_TEXT_DIM, PIXEL_10);
        front.add(Box.createVerticalGlue());
        front.add(runeGlyph); front.add(vGap(6));
        front.add(qMark);     front.add(vGap(6));
        front.add(typeLabel);
        front.add(Box.createVerticalGlue());

        // BACK
        Color backBg  = isWeapon ? new Color(30, 22, 5) : new Color(10, 18, 30);
        Color backBdr = isWeapon ? C_GOLD_BORDER2       : C_BLUE_BDR;
        JPanel back = new RoundedPanel(10, backBg, backBdr);
        back.setLayout(new BoxLayout(back, BoxLayout.Y_AXIS));
        back.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        Color nameColor  = isWeapon ? C_GOLD_BRIGHT : C_BLUE;
        Color statColor  = isWeapon ? C_TEXT        : new Color(160, 190, 210);

        JLabel backIcon  = centredLbl(isWeapon ? "⚔" : "🛡", Color.WHITE, new Font("Segoe UI Emoji", Font.PLAIN, 30));
        JLabel backName  = centredLbl(isWeapon ? "Celestial Edge" : "Celestial Battlegear", nameColor, PIXEL_13);
        JLabel backType  = centredLbl(isWeapon ? "Legendary Sword" : "Legendary Armor", C_TEXT_DIM, PIXEL_10);
        JLabel backStats = centredLbl(
                isWeapon
                        ? "<html><center>+50 ATK &nbsp;·&nbsp; 15% Lifesteal<br>30% Extra Hit Chance</center></html>"
                        : "<html><center>+50 DEF &nbsp;·&nbsp; Immune to Debuffs<br>30% Reflect · 20% Dmg Reflect</center></html>",
                statColor, PIXEL_11
        );

        if (isWeapon) {
            backName.setName("weaponName");
            backType.setName("weaponType");
            backStats.setName("weaponStats");
            weaponBack = back;
        } else {
            armorBack = back;
        }

        back.add(Box.createVerticalGlue());
        back.add(backIcon);  back.add(vGap(4));
        back.add(backName);  back.add(vGap(2));
        back.add(backType);  back.add(vGap(6));
        back.add(backStats);
        back.add(Box.createVerticalGlue());

        CardLayout cl = (CardLayout) container.getLayout();
        container.add(front, "front");
        container.add(back,  "back");
        cl.show(container, "front");

        container.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        container.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (isWeapon && weaponFlipped) return;
                if (!isWeapon && armorFlipped)  return;
                fadeFlip(container, front, back, isWeapon);
            }
        });

        return container;
    }

    private void fadeFlip(JPanel container, JPanel front, JPanel back, boolean isWeapon) {
        final float[] alpha = {1.0f};
        Timer fadeOut = new Timer(16, null);
        fadeOut.addActionListener(e -> {
            alpha[0] -= 0.1f;
            if (alpha[0] <= 0f) {
                alpha[0] = 0f;
                fadeOut.stop();
                CardLayout cl = (CardLayout) container.getLayout();
                cl.show(container, "back");
                Timer fadeIn = new Timer(16, null);
                fadeIn.addActionListener(ev -> {
                    alpha[0] += 0.1f;
                    if (alpha[0] >= 1f) {
                        alpha[0] = 1f;
                        fadeIn.stop();
                        if (isWeapon) weaponFlipped = true;
                        else          armorFlipped  = true;
                        if (weaponFlipped && armorFlipped) {
                            rewardChoiceArea.setVisible(true);
                            rewardChoiceArea.revalidate();
                            rewardChoiceArea.repaint();
                        }
                    }
                    container.repaint();
                });
                container.repaint();
                fadeIn.start();
            }
            container.repaint();
        });
        fadeOut.start();
    }

    // =========================================================================
    //  QUIZ LOGIC
    // =========================================================================

    private void startQuiz() {
        currentQuestion = 0;
        showScreen(SCREEN_QUIZ);
        loadQuestion();
    }

    private void loadQuestion() {
        stopCountdown();
        qFeedbackPanel.removeAll();

        String   question = Q_TEXT[currentQuestion];
        String[] options  = Q_OPTIONS[currentQuestion];

        qCounterLabel.setText("Question " + (currentQuestion + 1) + " of " + Q_TEXT.length);
        qTextArea.setText(question);

        // pips
        qPipsPanel.removeAll();
        for (int i = 0; i < Q_TEXT.length; i++) {
            final Color pipColor;
            if      (i < currentQuestion) pipColor = C_GOLD;
            else if (i == currentQuestion) pipColor = C_GOLD_BRIGHT;
            else                           pipColor = new Color(40, 35, 20);
            JPanel pip = new JPanel() {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(pipColor);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 4, 4);
                }
            };
            pip.setBackground(C_BG);
            pip.setPreferredSize(new Dimension(0, 6));
            qPipsPanel.add(pip);
        }

        // options
        qOptionsPanel.removeAll();
        JButton[] optBtns = new JButton[options.length];
        for (int i = 0; i < options.length; i++) {
            final int idx = i;
            JButton optBtn = makeOptionBtn(i + 1, options[i], false, false, false);
            optBtn.addActionListener(e -> selectAnswer(idx, optBtns, options.length));
            optBtns[i] = optBtn;
            qOptionsPanel.add(optBtn);
            qOptionsPanel.add(vGap(5));
        }

        qPipsPanel.revalidate(); qPipsPanel.repaint();
        qOptionsPanel.revalidate(); qOptionsPanel.repaint();
        qFeedbackPanel.revalidate(); qFeedbackPanel.repaint();

        timeLeft = TIME_LIMIT;
        updateTimerDisplay();
        startCountdown(optBtns);
    }

    private void startCountdown(JButton[] optBtns) {
        countdownTimer = new Timer(1000, null);
        countdownTimer.addActionListener(e -> {
            timeLeft--;
            updateTimerDisplay();
            if (timeLeft <= 0) {
                stopCountdown();
                onTimeout(optBtns);
            }
        });
        countdownTimer.start();
    }

    private void stopCountdown() {
        if (countdownTimer != null) { countdownTimer.stop(); countdownTimer = null; }
    }

    private void updateTimerDisplay() {
        timerLabel.setText(timeLeft + "s remaining");
        timerLabel.setForeground(timeLeft <= 5 ? C_RED : C_TEXT_DIM);
        timerBarPanel.repaint();
    }

    private void selectAnswer(int selectedIdx, JButton[] optBtns, int total) {
        stopCountdown();
        int correct   = Q_CORRECT[currentQuestion];
        boolean isOk  = selectedIdx == correct;

        for (int i = 0; i < total; i++) {
            optBtns[i].setEnabled(false);
            styleOptionBtn(optBtns[i], i == selectedIdx && !isOk, i == correct, false);
        }
        showFeedback(isOk,
                isOk ? "✦  Correct — the runes glow with approval  ✦"
                        : "✦  Wrong — the trial is forfeit  ✦");

        Timer pause = new Timer(1300, e -> {
            if (isOk) {
                currentQuestion++;
                if (currentQuestion >= Q_TEXT.length) showScreen(SCREEN_PASS);
                else loadQuestion();
            } else {
                showScreen(SCREEN_FAIL);
            }
        });
        pause.setRepeats(false);
        pause.start();
    }

    private void onTimeout(JButton[] optBtns) {
        for (JButton b : optBtns) { b.setEnabled(false); styleOptionBtn(b, false, false, true); }
        showFeedback(false, "✦  Time expired — you must think faster  ✦");
        Timer pause = new Timer(1300, e -> showScreen(SCREEN_FAIL));
        pause.setRepeats(false);
        pause.start();
    }

    private void showFeedback(boolean correct, String message) {
        qFeedbackPanel.removeAll();
        Color bg  = correct ? C_GREEN_BG  : C_RED_BG;
        Color bdr = correct ? C_GREEN_BDR : C_RED_BDR;
        Color fg  = correct ? C_GREEN     : C_RED;

        JPanel fb = new RoundedPanel(8, bg, bdr);
        fb.setLayout(new BorderLayout());
        fb.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        fb.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        fb.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lbl = new JLabel(message, SwingConstants.CENTER);
        lbl.setFont(PIXEL_11);
        lbl.setForeground(fg);
        fb.add(lbl, BorderLayout.CENTER);

        qFeedbackPanel.add(fb);
        qFeedbackPanel.revalidate();
        qFeedbackPanel.repaint();
    }

    // =========================================================================
    //  REWARD LOGIC
    // =========================================================================

    private void resolveWeapon() {
        legendaryArmor = new Armor(Armor.CELESTIAL_BATTLEGEAR);
        String classType = getClassType();
        WeaponDef wDef = switch (classType) {
            case "Swordsman" -> Sword.CELESTIAL_EDGE;
            case "Archer"    -> Bow.GOLDEN_TALON;
            default          -> Staff.CHRONOMANCER_STAFF;
        };
        legendaryWeapon = createWeapon(wDef);
        updateWeaponCard();
    }

    private Weapon createWeapon(WeaponDef def) {
        return switch (def.type) {
            case SWORD -> new Sword(def);
            case BOW   -> new Bow(def);
            case STAFF -> new Staff(def);
        };
    }

    private void updateWeaponCard() {
        if (legendaryWeapon == null || weaponBack == null) return;
        for (Component c : weaponBack.getComponents()) {
            if (!(c instanceof JLabel lbl)) continue;
            String n = lbl.getName();
            if (n == null) continue;
            switch (n) {
                case "weaponName"  -> lbl.setText(legendaryWeapon.name);
                case "weaponType"  -> lbl.setText(switch (legendaryWeapon.type) {
                    case SWORD -> "Legendary Sword";
                    case BOW   -> "Legendary Bow";
                    case STAFF -> "Legendary Staff";
                });
                case "weaponStats" -> lbl.setText(
                        "<html><center>+" + legendaryWeapon.atkBuff + " ATK &nbsp;·&nbsp; " +
                                legendaryWeapon.lifestealPercent + "% Lifesteal<br>" +
                                legendaryWeapon.extraHitChance + "% Extra Hit Chance</center></html>");
            }
        }
        weaponBack.revalidate(); weaponBack.repaint();
    }

    private void chooseArtifact(boolean choseWeapon) {
        rewardChoiceArea.setVisible(false);
        rewardConfirmArea.setVisible(true);
        rewardConfirmArea.revalidate(); rewardConfirmArea.repaint();

        if (choseWeapon) {
            equipWeapon();
            rewardConfirmDesc.setText("<html><body style='width:380px'>" +
                    "<b>" + legendaryWeapon.name + "</b> claimed.<br><br>" +
                    "You grasp the legendary weapon... it hums with ancient cosmic power. " +
                    "The Celestial Battlegear emits a final mournful chime — then shatters into golden dust." +
                    "</body></html>");
            rewardConfirmDesc.setForeground(C_GOLD_BRIGHT);
        } else {
            equipArmor();
            rewardConfirmDesc.setText("<html><body style='width:380px'>" +
                    "<b>Celestial Battlegear</b> claimed.<br><br>" +
                    "A warm celestial aura surrounds you as you don the armor. " +
                    "The " + (legendaryWeapon != null ? legendaryWeapon.name : "Legendary Weapon") +
                    " releases a final burst of violet light — then dissolves into ash." +
                    "</body></html>");
            rewardConfirmDesc.setForeground(C_BLUE);
        }
    }

    private void declineReward() {
        rewardChoiceArea.setVisible(false);
        rewardConfirmArea.setVisible(true);
        rewardConfirmArea.revalidate(); rewardConfirmArea.repaint();
        rewardConfirmDesc.setText("<html><body style='width:380px'>" +
                "You back away slowly...<br><br>" +
                "The violet flames dim, disappointed. Both artifacts crumble to dust — unclaimed. " +
                "You proceed to the Final Boss with only what you carried." +
                "</body></html>");
        rewardConfirmDesc.setForeground(C_TEXT_DIM);
    }

    private void equipWeapon() {
        if (player == null || legendaryWeapon == null) return;
        Combatant c = (Combatant) player;
        c.inventory.setEquippedWeapon(legendaryWeapon);
        c.recalculateBuffs();
    }

    private void equipArmor() {
        if (player == null || legendaryArmor == null) return;
        Combatant c = (Combatant) player;
        c.inventory.setEquippedArmor(legendaryArmor);
        c.recalculateBuffs();
    }

    private String getClassType() {
        if (player == null) return "Mage";
        try {
            var m = player.getClass().getMethod("getClassType");
            Object r = m.invoke(player);
            return r != null ? r.toString() : "Mage";
        } catch (Exception e) { return "Mage"; }
    }

    private void finish() {
        stopCountdown();
        if (onFinished != null) onFinished.run();
    }

    // =========================================================================
    //  WIDGET HELPERS
    // =========================================================================

    /** Option button — numbered badge + label, gold border card style */
    private JButton makeOptionBtn(int number, String text, boolean wrong, boolean correct, boolean dim) {
        Color bg  = correct ? new Color(10, 25, 10) : wrong ? new Color(28, 8, 8) : C_CARD;
        Color bdr = correct ? C_GREEN_BDR           : wrong ? C_RED_BDR           : C_GOLD_BORDER;
        Color fg  = correct ? C_GREEN               : wrong ? C_RED               : dim ? C_TEXT_DIM : C_TEXT;

        JButton b = new JButton();
        b.setLayout(new BorderLayout(10, 0));
        b.setBackground(bg);
        b.setForeground(fg);
        b.setFont(PIXEL_12);
        b.setBorder(new CompoundBorder(
                new LineBorder(bdr, 1, true),
                BorderFactory.createEmptyBorder(9, 12, 9, 12)
        ));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.setOpaque(true);

        JLabel badge = new JLabel(String.valueOf(number), SwingConstants.CENTER);
        badge.setFont(PIXEL_10);
        badge.setForeground(C_GOLD);
        badge.setPreferredSize(new Dimension(22, 22));
        badge.setBorder(new LineBorder(C_GOLD_BORDER2, 1, true));

        JLabel txt = new JLabel(text);
        txt.setFont(PIXEL_12);
        txt.setForeground(fg);

        b.add(badge, BorderLayout.WEST);
        b.add(txt,   BorderLayout.CENTER);

        b.addMouseListener(new MouseAdapter() {
            final Color origBg = bg;
            @Override public void mouseEntered(MouseEvent e) {
                if (b.isEnabled()) { b.setBackground(new Color(45, 38, 20)); b.repaint(); }
            }
            @Override public void mouseExited(MouseEvent e) {
                if (b.isEnabled()) { b.setBackground(origBg); b.repaint(); }
            }
        });
        return b;
    }

    private void styleOptionBtn(JButton b, boolean wrong, boolean correct, boolean dim) {
        Color bg  = correct ? new Color(10, 25, 10) : wrong ? new Color(28, 8, 8) : C_CARD;
        Color bdr = correct ? C_GREEN_BDR           : wrong ? C_RED_BDR           : C_GOLD_BORDER;
        Color fg  = correct ? C_GREEN               : wrong ? C_RED               : dim ? C_TEXT_DIM : C_TEXT;
        b.setBackground(bg);
        b.setBorder(new CompoundBorder(
                new LineBorder(bdr, 1, true),
                BorderFactory.createEmptyBorder(9, 12, 9, 12)
        ));
        for (Component c : b.getComponents()) {
            if (c instanceof JLabel lbl) lbl.setForeground(fg);
        }
        b.repaint();
    }

    // ── Layout helpers ────────────────────────────────────────────────────────

    private JPanel vBox() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(C_BG);
        return p;
    }

    private JLabel screenTitle(String text) {
        JLabel l = new JLabel(text, SwingConstants.CENTER);
        l.setFont(PIXEL_18);
        l.setForeground(C_GOLD);
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        l.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        return l;
    }

    private JLabel subtitle(String text) {
        JLabel l = new JLabel(text, SwingConstants.CENTER);
        l.setFont(PIXEL_10);
        l.setForeground(C_TEXT_DIM);
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        l.setMaximumSize(new Dimension(Integer.MAX_VALUE, 18));
        return l;
    }

    private JLabel sectionHeader(String text) {
        JLabel l = new JLabel(text.toUpperCase(), SwingConstants.CENTER);
        l.setFont(PIXEL_13);
        l.setForeground(C_GOLD);
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        l.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));
        return l;
    }

    /** A bordered card panel matching PlayerStatsDialog's skillCard look */
    private JPanel goldCard() {
        JPanel card = new RoundedPanel(10, C_CARD, C_GOLD_BORDER);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        return card;
    }

    private JPanel quoteCard(String text) {
        JPanel card = goldCard();
        JLabel l = new JLabel("<html><body style='width:440px'><i>" + text + "</i></body></html>");
        l.setFont(PIXEL_11);
        l.setForeground(C_TEXT_DIM);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(l);
        return card;
    }

    private JPanel resultCard(Color bg, Color border) {
        JPanel p = new RoundedPanel(10, bg, border);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        return p;
    }

    private JLabel resultTitle(String text, Color color) {
        JLabel l = new JLabel(text, SwingConstants.CENTER);
        l.setFont(PIXEL_15);
        l.setForeground(color);
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        return l;
    }

    private JLabel resultBody(String text) {
        JLabel l = new JLabel("<html><body style='width:440px'><i>" + text + "</i></body></html>");
        l.setFont(PIXEL_11);
        l.setForeground(C_TEXT_DIM);
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        return l;
    }

    private JPanel goldDivider() {
        JPanel r = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(C_GOLD_BORDER);
                g.fillRect(0, 0, getWidth(), 1);
            }
        };
        r.setBackground(C_BG);
        r.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        r.setPreferredSize(new Dimension(0, 1));
        r.setAlignmentX(Component.LEFT_ALIGNMENT);
        return r;
    }

    private JLabel centredLbl(String text, Color fg, Font font) {
        JLabel l = new JLabel(text, SwingConstants.CENTER);
        l.setFont(font);
        l.setForeground(fg);
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        return l;
    }

    /** Primary gold button (same hover as PlayerStatsDialog close btn) */
    private JButton primaryBtn(String text, ActionListener al) {
        JButton b = makeStyledBtn(text, C_GOLD_BRIGHT, C_CARD, C_GOLD_BORDER2);
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        b.addActionListener(al);
        return b;
    }

    private JButton blueBtn(String text, ActionListener al) {
        JButton b = makeStyledBtn(text, C_BLUE, C_CARD, C_BLUE_BDR);
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        b.addActionListener(al);
        return b;
    }

    private JButton ghostBtn(String text, ActionListener al) {
        JButton b = makeStyledBtn(text, C_TEXT_DIM, C_BG, C_GOLD_BORDER);
        b.setFont(PIXEL_11);
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        b.addActionListener(al);
        return b;
    }

    private JButton makeStyledBtn(String text, Color fg, Color bg, Color border) {
        JButton b = new JButton(text);
        b.setFont(PIXEL_12);
        b.setForeground(fg);
        b.setBackground(bg);
        b.setOpaque(true);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(new CompoundBorder(
                new LineBorder(border, 1, true),
                BorderFactory.createEmptyBorder(10, 16, 10, 16)
        ));
        b.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                b.setForeground(C_GOLD);
                b.setBorder(new CompoundBorder(
                        new LineBorder(C_GOLD, 1, true),
                        BorderFactory.createEmptyBorder(10, 16, 10, 16)));
            }
            @Override public void mouseExited(MouseEvent e) {
                b.setForeground(fg);
                b.setBorder(new CompoundBorder(
                        new LineBorder(border, 1, true),
                        BorderFactory.createEmptyBorder(10, 16, 10, 16)));
            }
        });
        return b;
    }

    private JScrollPane scrollWrap(JPanel content) {
        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.setBackground(C_BG);
        scroll.getViewport().setBackground(C_BG);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.getVerticalScrollBar().setBackground(C_BG);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    private static Component vGap(int h) { return Box.createRigidArea(new Dimension(0, h)); }

    // =========================================================================
    //  INNER: RoundedPanel
    // =========================================================================

    private static class RoundedPanel extends JPanel {
        private final int   radius;
        private final Color bg, border;

        RoundedPanel(int radius, Color bg, Color border) {
            this.radius = radius; this.bg = bg; this.border = border;
            setOpaque(false);
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bg);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), radius*2, radius*2));
            g2.setColor(border);
            g2.setStroke(new BasicStroke(1));
            g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth()-1, getHeight()-1, radius*2, radius*2));
            g2.dispose();
            super.paintComponent(g);
        }
    }
}