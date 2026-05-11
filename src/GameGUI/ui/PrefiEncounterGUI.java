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

/**
 * PrefiEncounterGUI — Java Swing version of the PrefiEncounter event.
 *
 * Drop this panel into your main JFrame / JPanel wherever you want
 * the event to appear. Wire onFinished to resume your game flow.
 *
 * Usage:
 *   PrefiEncounterGUI panel = new PrefiEncounterGUI(player, () -> {
 *       // called when the encounter ends (pass OR fail)
 *       showNextScreen();
 *   });
 *   parentPanel.add(panel);
 */
public class PrefiEncounterGUI extends JPanel {

    // ── Colours (dark fantasy palette) ───────────────────────────────────────
    private static final Color C_BG           = hex("#0d0a14");
    private static final Color C_SURFACE      = hex("#120920");
    private static final Color C_SURFACE2     = hex("#140d20");
    private static final Color C_GOLD         = hex("#c9a227");
    private static final Color C_GOLD_LIGHT   = hex("#f0c84a");
    private static final Color C_PURPLE_DIM   = hex("#3a1f6e");
    private static final Color C_PURPLE_MID   = hex("#6b3fa0");
    private static final Color C_PURPLE_TEXT  = hex("#b89fd8");
    private static final Color C_TEXT         = hex("#e8d9b5");
    private static final Color C_TEXT_MUTED   = hex("#7a6a9a");
    private static final Color C_GREEN        = hex("#70c070");
    private static final Color C_GREEN_BG     = hex("#06140a");
    private static final Color C_GREEN_BORDER = hex("#207040");
    private static final Color C_RED          = hex("#c05050");
    private static final Color C_RED_BG       = hex("#140606");
    private static final Color C_RED_BORDER   = hex("#702020");
    private static final Color C_BLUE_TEXT    = hex("#70b0f0");
    private static final Color C_BLUE_BORDER  = hex("#103060");
    private static final Color C_BLUE_BG      = hex("#0a1020");

    // ── Fonts ─────────────────────────────────────────────────────────────────
    private static final Font FONT_GEORGIA_12       = new Font("Georgia", Font.PLAIN,  12);
    private static final Font FONT_GEORGIA_12_ITAL  = new Font("Georgia", Font.ITALIC, 12);
    private static final Font FONT_GEORGIA_13_ITAL  = new Font("Georgia", Font.ITALIC, 13);
    private static final Font FONT_GEORGIA_11       = new Font("Georgia", Font.PLAIN,  11);
    private static final Font FONT_SANS_BOLD_14     = new Font("SansSerif", Font.BOLD, 14);
    private static final Font FONT_SANS_BOLD_12     = new Font("SansSerif", Font.BOLD, 12);
    private static final Font FONT_SANS_10          = new Font("SansSerif", Font.PLAIN, 10);

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

    private static final int[] Q_CORRECT = {2, 1, 1, 1};
    private static final int   TIME_LIMIT = 15;

    // ── Screen names for CardLayout ───────────────────────────────────────────
    private static final String SCREEN_INTRO  = "intro";
    private static final String SCREEN_QUIZ   = "quiz";
    private static final String SCREEN_FAIL   = "fail";
    private static final String SCREEN_PASS   = "pass";
    private static final String SCREEN_REWARD = "reward";

    // ── State ─────────────────────────────────────────────────────────────────
    private final Object   player;
    private final Runnable onFinished;

    private int   currentQuestion = 0;
    private int   timeLeft        = TIME_LIMIT;
    private Timer countdownTimer;

    private boolean weaponFlipped = false;
    private boolean armorFlipped  = false;
    private Weapon  legendaryWeapon;
    private Armor   legendaryArmor;

    // ── Layout ────────────────────────────────────────────────────────────────
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel     cardPanel  = new JPanel(cardLayout);

    // ── Quiz widgets updated per-question ─────────────────────────────────────
    private JLabel    qCounterLabel;
    private JTextArea qTextArea;
    private JPanel    qOptionsPanel;
    private JLabel    timerLabel;
    private JPanel    timerBarPanel;
    private int       timerBarWidth = 0;
    private JPanel    qPipsPanel;
    private JPanel    qFeedbackPanel;

    // ── Reward widgets ────────────────────────────────────────────────────────
    private JPanel  rewardChoiceArea;
    private JPanel  rewardConfirmArea;
    private JLabel  rewardConfirmDesc;
    private JPanel  flipPaneWeapon;
    private JPanel  flipPaneArmor;
    private JPanel  weaponFront, weaponBack;
    private JPanel  armorFront,  armorBack;

    // ── Constructor ───────────────────────────────────────────────────────────
    public PrefiEncounterGUI(Object player, Runnable onFinished) {
        this.player      = player;
        this.onFinished  = onFinished;

        setLayout(new BorderLayout());
        setBackground(C_BG);
        setPreferredSize(new Dimension(720, 620));

        cardPanel.setBackground(C_BG);
        add(cardPanel, BorderLayout.CENTER);

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
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(C_BG);
        content.setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));

        content.add(titleBar("The Trial of Prefi", "An ancient encounter awaits"));
        content.add(vGap(12));
        content.add(figureBox("The Figure speaks",
                "\"Before you stands the final threshold. To claim a legendary artifact, " +
                        "you must first prove your command of the arcane disciplines that govern all things...\""));
        content.add(vGap(12));
        content.add(rulesBox());
        content.add(vGap(16));
        content.add(primaryBtn("Enter the Trial", e -> startQuiz()));
        content.add(vGap(8));
        content.add(ghostBtn("Walk Away", e -> showScreen(SCREEN_FAIL)));

        return scrollWrap(content);
    }

    private JPanel rulesBox() {
        JPanel box = surfaceBox();
        box.add(mutedLabel("Trial Rules"));
        box.add(vGap(6));
        String[] rules = {
                "◆   4 questions on Object-Oriented Principles",
                "◆   15 seconds per question",
                "◆   One wrong answer ends the trial",
                "◆   Success grants a legendary artifact"
        };
        for (String r : rules) {
            JLabel l = new JLabel(r);
            l.setFont(FONT_GEORGIA_12);
            l.setForeground(hex("#a090c0"));
            l.setAlignmentX(Component.LEFT_ALIGNMENT);
            box.add(l);
        }
        return box;
    }

    // =========================================================================
    //  QUIZ SCREEN
    // =========================================================================

    private JScrollPane buildQuizScreen() {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(C_BG);
        content.setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));

        // counter
        qCounterLabel = new JLabel("Question 1 of 4");
        qCounterLabel.setFont(FONT_GEORGIA_11);
        qCounterLabel.setForeground(C_TEXT_MUTED);
        qCounterLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // pips
        qPipsPanel = new JPanel(new GridLayout(1, Q_TEXT.length, 6, 0));
        qPipsPanel.setBackground(C_BG);
        qPipsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 6));
        qPipsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // timer row
        JPanel timerRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        timerRow.setBackground(C_BG);
        timerRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        timerRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        timerLabel = new JLabel("15s remaining");
        timerLabel.setFont(FONT_SANS_10);
        timerLabel.setForeground(C_TEXT_MUTED);
        timerRow.add(timerLabel);

        // timer bar
        timerBarPanel = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hex("#1a0d30"));
                g2.fillRoundRect(0, 0, getWidth(), 4, 4, 4);
                Color barColor = timeLeft <= 5 ? C_RED : C_GOLD;
                g2.setColor(barColor);
                int w = (int)(getWidth() * (timeLeft / (double) TIME_LIMIT));
                if (w > 0) g2.fillRoundRect(0, 0, w, 4, 4, 4);
            }
        };
        timerBarPanel.setBackground(C_BG);
        timerBarPanel.setPreferredSize(new Dimension(0, 4));
        timerBarPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 4));
        timerBarPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // question tablet
        JPanel tablet = runeTablet();

        qTextArea = new JTextArea();
        qTextArea.setFont(FONT_GEORGIA_13_ITAL);
        qTextArea.setForeground(C_PURPLE_TEXT);
        qTextArea.setBackground(hex("#100c1c"));
        qTextArea.setEditable(false);
        qTextArea.setLineWrap(true);
        qTextArea.setWrapStyleWord(true);
        qTextArea.setOpaque(false);
        qTextArea.setAlignmentX(Component.LEFT_ALIGNMENT);

        qOptionsPanel = new JPanel();
        qOptionsPanel.setLayout(new BoxLayout(qOptionsPanel, BoxLayout.Y_AXIS));
        qOptionsPanel.setBackground(hex("#100c1c"));
        qOptionsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        tablet.add(qTextArea);
        tablet.add(vGap(10));
        tablet.add(qOptionsPanel);

        qFeedbackPanel = new JPanel();
        qFeedbackPanel.setLayout(new BoxLayout(qFeedbackPanel, BoxLayout.Y_AXIS));
        qFeedbackPanel.setBackground(C_BG);
        qFeedbackPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        content.add(titleBar("OOP Trial", ""));
        content.add(vGap(8));
        content.add(qCounterLabel);
        content.add(vGap(6));
        content.add(qPipsPanel);
        content.add(vGap(4));
        content.add(timerRow);
        content.add(vGap(2));
        content.add(timerBarPanel);
        content.add(vGap(10));
        content.add(tablet);
        content.add(vGap(8));
        content.add(qFeedbackPanel);

        return scrollWrap(content);
    }

    // =========================================================================
    //  FAIL SCREEN
    // =========================================================================

    private JScrollPane buildFailScreen() {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(C_BG);
        content.setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));

        JPanel verdictBox = new RoundedPanel(8, C_RED_BG, C_RED_BORDER);
        verdictBox.setLayout(new BoxLayout(verdictBox, BoxLayout.Y_AXIS));
        verdictBox.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        verdictBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel failTitle = centeredLabel("The Trial Ends", C_RED, FONT_SANS_BOLD_14);
        JLabel failMsg   = centeredLabel("\"You lack the foundation required to wield true power.\"",
                hex("#8a8a8a"), FONT_GEORGIA_12_ITAL);
        verdictBox.add(failTitle);
        verdictBox.add(vGap(6));
        verdictBox.add(failMsg);

        content.add(titleBar("Trial Failed", ""));
        content.add(vGap(12));
        content.add(verdictBox);
        content.add(vGap(12));
        content.add(figureBox("The Figure speaks",
                "No legendary artifacts will be granted. The figure's form dissolves into violet smoke. " +
                        "You step forward… toward the Final Boss."));
        content.add(vGap(12));
        content.add(divider());
        content.add(vGap(12));
        content.add(primaryBtn("Proceed to Final Boss", e -> finish()));

        return scrollWrap(content);
    }

    // =========================================================================
    //  PASS SCREEN
    // =========================================================================

    private JScrollPane buildPassScreen() {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(C_BG);
        content.setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));

        JPanel verdictBox = new RoundedPanel(8, C_GREEN_BG, C_GREEN_BORDER);
        verdictBox.setLayout(new BoxLayout(verdictBox, BoxLayout.Y_AXIS));
        verdictBox.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        verdictBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel passTitle = centeredLabel("Mastery Proven", C_GREEN, FONT_SANS_BOLD_14);
        JLabel passMsg   = centeredLabel("\"Impressive… you demonstrated complete command of OOP.\"",
                hex("#8a8a8a"), FONT_GEORGIA_12_ITAL);
        verdictBox.add(passTitle);
        verdictBox.add(vGap(6));
        verdictBox.add(passMsg);

        content.add(titleBar("Trial Complete", ""));
        content.add(vGap(12));
        content.add(verdictBox);
        content.add(vGap(12));
        content.add(figureBox("The Figure speaks",
                "The figure raises both hands. Two artifacts swirl into existence from a burst of violet fire..."));
        content.add(vGap(12));
        content.add(primaryBtn("Claim Your Reward", e -> {
            resolveWeapon();
            showScreen(SCREEN_REWARD);
        }));

        return scrollWrap(content);
    }

    // =========================================================================
    //  REWARD SCREEN
    // =========================================================================

    private JScrollPane buildRewardScreen() {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(C_BG);
        content.setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));

        JLabel hint = new JLabel("Tap a card to reveal — only one may be claimed");
        hint.setFont(FONT_SANS_10);
        hint.setForeground(C_TEXT_MUTED);
        hint.setAlignmentX(Component.LEFT_ALIGNMENT);

        // flip cards side by side
        JPanel flipRow = new JPanel(new GridLayout(1, 2, 12, 0));
        flipRow.setBackground(C_BG);
        flipRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        flipRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        flipPaneWeapon = buildFlipCard(true);
        flipPaneArmor  = buildFlipCard(false);
        flipRow.add(flipPaneWeapon);
        flipRow.add(flipPaneArmor);

        // choice area
        rewardChoiceArea = new JPanel();
        rewardChoiceArea.setLayout(new BoxLayout(rewardChoiceArea, BoxLayout.Y_AXIS));
        rewardChoiceArea.setBackground(C_BG);
        rewardChoiceArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        rewardChoiceArea.setVisible(false);

        JLabel choiceHint = centeredLabel("Choose wisely — the other shall be destroyed",
                C_TEXT_MUTED, FONT_GEORGIA_12_ITAL);

        JPanel choiceBtns = new JPanel(new GridLayout(1, 2, 8, 0));
        choiceBtns.setBackground(C_BG);
        choiceBtns.setAlignmentX(Component.LEFT_ALIGNMENT);
        choiceBtns.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        JButton btnWeapon = styledBtn("Claim Weapon", C_GOLD, C_SURFACE, C_GOLD);
        btnWeapon.addActionListener(e -> chooseArtifact(true));
        JButton btnArmor  = styledBtn("Claim Armor",  C_BLUE_TEXT, C_SURFACE, C_BLUE_BORDER);
        btnArmor.addActionListener(e -> chooseArtifact(false));
        choiceBtns.add(btnWeapon);
        choiceBtns.add(btnArmor);

        JButton btnDecline = styledBtn("Walk Away", C_TEXT_MUTED, C_BG, C_PURPLE_DIM);
        btnDecline.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnDecline.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btnDecline.addActionListener(e -> declineReward());

        rewardChoiceArea.add(vGap(8));
        rewardChoiceArea.add(divider());
        rewardChoiceArea.add(vGap(8));
        rewardChoiceArea.add(choiceHint);
        rewardChoiceArea.add(vGap(8));
        rewardChoiceArea.add(choiceBtns);
        rewardChoiceArea.add(vGap(6));
        rewardChoiceArea.add(btnDecline);

        // confirm area
        rewardConfirmArea = new JPanel();
        rewardConfirmArea.setLayout(new BoxLayout(rewardConfirmArea, BoxLayout.Y_AXIS));
        rewardConfirmArea.setBackground(C_BG);
        rewardConfirmArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        rewardConfirmArea.setVisible(false);

        JPanel confirmTablet = runeTablet();
        rewardConfirmDesc = new JLabel("<html></html>");
        rewardConfirmDesc.setFont(FONT_GEORGIA_12_ITAL);
        rewardConfirmDesc.setForeground(C_PURPLE_TEXT);
        rewardConfirmDesc.setAlignmentX(Component.LEFT_ALIGNMENT);
        confirmTablet.add(rewardConfirmDesc);

        JButton btnProceed = styledBtn("Proceed to Final Boss", C_GOLD, C_SURFACE, C_GOLD);
        btnProceed.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnProceed.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btnProceed.addActionListener(e -> finish());

        rewardConfirmArea.add(vGap(8));
        rewardConfirmArea.add(divider());
        rewardConfirmArea.add(vGap(8));
        rewardConfirmArea.add(confirmTablet);
        rewardConfirmArea.add(vGap(8));
        rewardConfirmArea.add(btnProceed);

        content.add(titleBar("Choose Your Artifact", ""));
        content.add(vGap(8));
        content.add(hint);
        content.add(vGap(10));
        content.add(flipRow);
        content.add(rewardChoiceArea);
        content.add(rewardConfirmArea);

        return scrollWrap(content);
    }

    // =========================================================================
    //  FLIP CARD (fade cross-fade effect via Timer)
    // =========================================================================

    private JPanel buildFlipCard(boolean isWeapon) {
        JPanel container = new JPanel(new CardLayout());
        container.setPreferredSize(new Dimension(0, 190));
        container.setMaximumSize(new Dimension(Integer.MAX_VALUE, 190));
        container.setBackground(C_BG);

        // ── FRONT ────────────────────────────────────────────────────────────
        JPanel front = new RoundedPanel(8, C_SURFACE, C_PURPLE_DIM);
        front.setLayout(new BoxLayout(front, BoxLayout.Y_AXIS));
        front.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JLabel runeGlyph = centeredLabel(isWeapon ? "⚔  ᚦ ᚨ ᚱ" : "🛡  ᚲ ᚾ ᚹ", hex("#4a2a80"), FONT_GEORGIA_12);
        JLabel qMark     = centeredLabel("?", C_PURPLE_MID, new Font("SansSerif", Font.BOLD, 32));
        JLabel typeLabel = centeredLabel(isWeapon ? "Legendary Weapon" : "Legendary Armor",
                hex("#4a2a80"), FONT_SANS_10);
        front.add(Box.createVerticalGlue());
        front.add(runeGlyph);
        front.add(vGap(8));
        front.add(qMark);
        front.add(vGap(8));
        front.add(typeLabel);
        front.add(Box.createVerticalGlue());

        // ── BACK ─────────────────────────────────────────────────────────────
        Color backBg     = isWeapon ? hex("#1a1000") : C_BLUE_BG;
        Color backBorder = isWeapon ? hex("#6b5010") : C_BLUE_BORDER;
        JPanel back = new RoundedPanel(8, backBg, backBorder);
        back.setLayout(new BoxLayout(back, BoxLayout.Y_AXIS));
        back.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        Color nameColor = isWeapon ? C_GOLD_LIGHT : C_BLUE_TEXT;
        Color statColor = isWeapon ? hex("#c9a880") : hex("#90b0c8");
        Color typeColor = isWeapon ? hex("#8a6a30") : hex("#4878a0");

        JLabel backIcon  = centeredLabel(isWeapon ? "⚔️" : "🛡️", Color.WHITE,
                new Font("SansSerif", Font.PLAIN, 28));
        JLabel backName  = centeredLabel(isWeapon ? "Celestial Edge" : "Celestial Battlegear",
                nameColor, new Font("SansSerif", Font.BOLD, 14));
        JLabel backType  = centeredLabel(isWeapon ? "Legendary Sword" : "Legendary Armor",
                typeColor, FONT_SANS_10);
        JLabel backStats = centeredLabel(isWeapon
                        ? "<html><center>+50 ATK &nbsp;·&nbsp; 15% Lifesteal<br>30% Extra Hit Chance</center></html>"
                        : "<html><center>+50 DEF &nbsp;·&nbsp; Immune to Debuffs<br>30% Reflect &nbsp;·&nbsp; 20% Dmg Reflect</center></html>",
                statColor, FONT_GEORGIA_11);

        back.add(Box.createVerticalGlue());
        back.add(backIcon);
        back.add(vGap(6));
        back.add(backName);
        back.add(vGap(2));
        back.add(backType);
        back.add(vGap(6));
        back.add(backStats);
        back.add(Box.createVerticalGlue());

        // store references for later stat update
        if (isWeapon) {
            weaponFront = front;
            weaponBack  = back;
            // keep backName/backType for later update via component index
            // tag them so we can find them
            backName.setName("weaponName");
            backType.setName("weaponType");
            backStats.setName("weaponStats");
        } else {
            armorFront = front;
            armorBack  = back;
        }

        CardLayout cl = (CardLayout) container.getLayout();
        container.add(front, "front");
        container.add(back,  "back");
        cl.show(container, "front");

        // click → fade cross-fade
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

    /**
     * Cross-fade: fade out the front panel's alpha, swap to back via CardLayout,
     * then fade back in. Implemented with a javax.swing.Timer.
     */
    private void fadeFlip(JPanel container, JPanel front, JPanel back, boolean isWeapon) {
        // Phase 1: fade out
        final float[] alpha = {1.0f};
        Timer fadeOut = new Timer(16, null);
        fadeOut.addActionListener(e -> {
            alpha[0] -= 0.08f;
            if (alpha[0] <= 0f) {
                alpha[0] = 0f;
                fadeOut.stop();
                // swap
                CardLayout cl = (CardLayout) container.getLayout();
                cl.show(container, "back");

                // Phase 2: fade in
                Timer fadeIn = new Timer(16, null);
                fadeIn.addActionListener(ev -> {
                    alpha[0] += 0.08f;
                    if (alpha[0] >= 1f) {
                        alpha[0] = 1f;
                        fadeIn.stop();
                        // mark flipped and check if both done
                        if (isWeapon) weaponFlipped = true;
                        else          armorFlipped  = true;
                        if (weaponFlipped && armorFlipped) {
                            rewardChoiceArea.setVisible(true);
                            rewardChoiceArea.revalidate();
                            rewardChoiceArea.repaint();
                        }
                    }
                    setAlphaOnPanel(back, alpha[0]);
                });
                setAlphaOnPanel(back, 0f);
                fadeIn.start();
            }
            setAlphaOnPanel(front, alpha[0]);
        });
        fadeOut.start();
    }

    /** Approximates alpha by blending the panel color toward the bg color. */
    private void setAlphaOnPanel(JPanel panel, float alpha) {
        // We just repaint — actual alpha compositing requires a custom paint approach.
        // For simplicity, we repaint the container which triggers the card visible.
        panel.getParent().repaint();
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

        // rebuild pips
        qPipsPanel.removeAll();
        for (int i = 0; i < Q_TEXT.length; i++) {
            final Color pipColor;
            if      (i < currentQuestion)  pipColor = C_GOLD;
            else if (i == currentQuestion)  pipColor = C_PURPLE_MID;
            else                            pipColor = hex("#2a1850");
            JPanel pip = new JPanel() {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(pipColor);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 4, 4);
                }
            };
            pip.setBackground(C_BG);
            pip.setPreferredSize(new Dimension(0, 4));
            qPipsPanel.add(pip);
        }

        // rebuild options
        qOptionsPanel.removeAll();
        JButton[] optBtns = new JButton[options.length];
        for (int i = 0; i < options.length; i++) {
            final int idx = i;
            JButton optBtn = optionBtn(i + 1, options[i], false, false, false);
            optBtn.addActionListener(e -> selectAnswer(idx, optBtns, options.length));
            optBtns[i] = optBtn;
            qOptionsPanel.add(optBtn);
            qOptionsPanel.add(vGap(6));
        }

        qPipsPanel.revalidate(); qPipsPanel.repaint();
        qOptionsPanel.revalidate(); qOptionsPanel.repaint();
        qFeedbackPanel.revalidate(); qFeedbackPanel.repaint();

        // start countdown
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
        if (countdownTimer != null) {
            countdownTimer.stop();
            countdownTimer = null;
        }
    }

    private void updateTimerDisplay() {
        timerLabel.setText(timeLeft + "s remaining");
        timerLabel.setForeground(timeLeft <= 5 ? C_RED : C_TEXT_MUTED);
        timerBarPanel.repaint();
    }

    private void selectAnswer(int selectedIdx, JButton[] optBtns, int total) {
        stopCountdown();
        int     correct   = Q_CORRECT[currentQuestion];
        boolean isCorrect = selectedIdx == correct;

        for (int i = 0; i < total; i++) {
            optBtns[i].setEnabled(false);
            boolean isCorrectBtn = (i == correct);
            boolean isWrongBtn   = (i == selectedIdx && !isCorrect);
            styleOptionBtn(optBtns[i], isWrongBtn, isCorrectBtn, (!isCorrectBtn && !isWrongBtn));
        }

        showFeedback(isCorrect, isCorrect
                ? "✦  Correct — the runes glow with approval  ✦"
                : "✦  Wrong — the trial is forfeit  ✦");

        Timer pause = new Timer(1300, e -> {
            if (isCorrect) {
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
        for (JButton b : optBtns) {
            b.setEnabled(false);
            styleOptionBtn(b, false, false, true);
        }
        showFeedback(false, "✦  Time expired — you must think faster  ✦");

        Timer pause = new Timer(1300, e -> showScreen(SCREEN_FAIL));
        pause.setRepeats(false);
        pause.start();
    }

    private void showFeedback(boolean correct, String message) {
        qFeedbackPanel.removeAll();

        Color fgColor  = correct ? C_GREEN      : C_RED;
        Color bgColor  = correct ? C_GREEN_BG   : C_RED_BG;
        Color bdrColor = correct ? C_GREEN_BORDER: C_RED_BORDER;

        JPanel fb = new RoundedPanel(6, bgColor, bdrColor);
        fb.setLayout(new BorderLayout());
        fb.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        fb.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        fb.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lbl = new JLabel(message, SwingConstants.CENTER);
        lbl.setFont(FONT_GEORGIA_11);
        lbl.setForeground(fgColor);
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
            String name = lbl.getName();
            if (name == null) continue;
            switch (name) {
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
        weaponBack.revalidate();
        weaponBack.repaint();
    }

    private void chooseArtifact(boolean choseWeapon) {
        rewardChoiceArea.setVisible(false);
        rewardConfirmArea.setVisible(true);
        rewardConfirmArea.revalidate();
        rewardConfirmArea.repaint();

        // dim the unchosen card
        JPanel fadeTarget = choseWeapon ? flipPaneArmor : flipPaneWeapon;
        fadeTarget.setEnabled(false);
        fadeTarget.setOpaque(true);

        if (choseWeapon) {
            equipWeapon();
            rewardConfirmDesc.setText("<html>" +
                    legendaryWeapon.name + " claimed.<br><br>" +
                    "You grasp the legendary weapon... it hums with ancient cosmic power. " +
                    "The Celestial Battlegear emits a final mournful chime — then shatters into golden dust." +
                    "</html>");
            rewardConfirmDesc.setForeground(C_GOLD);
        } else {
            equipArmor();
            rewardConfirmDesc.setText("<html>" +
                    "Celestial Battlegear claimed.<br><br>" +
                    "A warm celestial aura surrounds you as you don the armor. " +
                    "The " + (legendaryWeapon != null ? legendaryWeapon.name : "Legendary Weapon") +
                    " releases a final burst of violet light — then dissolves into ash." +
                    "</html>");
            rewardConfirmDesc.setForeground(C_BLUE_TEXT);
        }
    }

    private void declineReward() {
        rewardChoiceArea.setVisible(false);
        rewardConfirmArea.setVisible(true);
        rewardConfirmArea.revalidate();
        rewardConfirmArea.repaint();

        rewardConfirmDesc.setText("<html>" +
                "You back away slowly...<br><br>" +
                "The violet flames dim, disappointed. Both artifacts crumble to dust — unclaimed. " +
                "You proceed to the Final Boss with only what you carried." +
                "</html>");
        rewardConfirmDesc.setForeground(C_TEXT_MUTED);
    }

    private void equipWeapon() {
        if (player == null || legendaryWeapon == null) return;
        Combatant c = (Combatant) player;

        // NOTE: If your method in InventoryManager is called something else
        // (like setEquippedWeapon or setWeapon), change the word below!
        c.inventory.setEquippedWeapon(legendaryWeapon);
        c.recalculateBuffs();
    }

    private void equipArmor() {
        if (player == null || legendaryArmor == null) return;
        Combatant c = (Combatant) player;

        // Same here: change equipArmor to match your InventoryManager method!
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
    //  WIDGET / STYLE HELPERS
    // =========================================================================

    /** Option button — numbered badge + text, coloured by state */
    private JButton optionBtn(int number, String text, boolean wrong, boolean correct, boolean dim) {
        JButton b = styledOptionBtn(number, text, wrong, correct, dim);
        return b;
    }

    private JButton styledOptionBtn(int number, String text, boolean wrong, boolean correct, boolean dim) {
        Color bg    = correct ? hex("#081a08") : wrong ? hex("#200808") : hex("#1a1028");
        Color bdr   = correct ? hex("#307040") : wrong ? hex("#a03030") : hex("#3a2060");
        Color fg    = correct ? C_GREEN        : wrong ? C_RED         : dim ? hex("#4a3a6a") : C_PURPLE_TEXT;

        JButton b = new JButton();
        b.setLayout(new BorderLayout(10, 0));
        b.setBackground(bg);
        b.setForeground(fg);
        b.setFont(FONT_GEORGIA_12);
        b.setBorder(new CompoundBorder(
                new LineBorder(bdr, 1, true),
                BorderFactory.createEmptyBorder(9, 10, 9, 10)
        ));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        b.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel badge = new JLabel(String.valueOf(number), SwingConstants.CENTER);
        badge.setFont(FONT_SANS_10);
        badge.setForeground(hex("#8a6ab0"));
        badge.setPreferredSize(new Dimension(22, 22));
        badge.setBorder(new LineBorder(C_PURPLE_MID, 1, true));

        JLabel txt = new JLabel(text);
        txt.setFont(FONT_GEORGIA_12);
        txt.setForeground(fg);

        b.add(badge, BorderLayout.WEST);
        b.add(txt, BorderLayout.CENTER);
        b.setOpaque(true);

        // hover
        b.addMouseListener(new MouseAdapter() {
            Color origBg = bg;
            @Override public void mouseEntered(MouseEvent e) {
                if (b.isEnabled()) { b.setBackground(hex("#2a1050")); b.repaint(); }
            }
            @Override public void mouseExited(MouseEvent e) {
                if (b.isEnabled()) { b.setBackground(origBg); b.repaint(); }
            }
        });
        return b;
    }

    private void styleOptionBtn(JButton b, boolean wrong, boolean correct, boolean dim) {
        Color bg  = correct ? hex("#081a08") : wrong ? hex("#200808") : hex("#1a1028");
        Color bdr = correct ? hex("#307040") : wrong ? hex("#a03030") : hex("#3a2060");
        Color fg  = correct ? C_GREEN        : wrong ? C_RED         : dim ? hex("#4a3a6a") : C_PURPLE_TEXT;
        b.setBackground(bg);
        b.setBorder(new CompoundBorder(
                new LineBorder(bdr, 1, true),
                BorderFactory.createEmptyBorder(9, 10, 9, 10)
        ));
        // recolor label children
        for (Component c : b.getComponents()) {
            if (c instanceof JLabel lbl) lbl.setForeground(fg);
        }
        b.repaint();
    }

    private JPanel titleBar(String title, String subtitle) {
        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBackground(C_BG);
        box.setAlignmentX(Component.LEFT_ALIGNMENT);
        box.setBorder(new MatteBorder(0, 0, 1, 0, C_PURPLE_MID));

        JLabel t = new JLabel(title.toUpperCase(), SwingConstants.CENTER);
        t.setFont(new Font("SansSerif", Font.BOLD, 15));
        t.setForeground(C_GOLD);
        t.setAlignmentX(Component.CENTER_ALIGNMENT);
        box.add(t);

        if (subtitle != null && !subtitle.isEmpty()) {
            JLabel s = new JLabel(subtitle, SwingConstants.CENTER);
            s.setFont(FONT_GEORGIA_11);
            s.setForeground(C_TEXT_MUTED);
            s.setAlignmentX(Component.CENTER_ALIGNMENT);
            box.add(s);
        }
        box.add(vGap(10));
        return box;
    }

    private JPanel figureBox(String labelText, String bodyText) {
        JPanel box = surfaceBox();
        box.add(mutedLabel(labelText));
        box.add(vGap(4));
        JLabel body = new JLabel("<html><i>" + bodyText + "</i></html>");
        body.setFont(FONT_GEORGIA_12_ITAL);
        body.setForeground(C_PURPLE_TEXT);
        body.setAlignmentX(Component.LEFT_ALIGNMENT);
        box.add(body);
        return box;
    }

    private JPanel surfaceBox() {
        RoundedPanel box = new RoundedPanel(8, C_SURFACE2, C_PURPLE_DIM);
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));
        box.setAlignmentX(Component.LEFT_ALIGNMENT);
        return box;
    }

    private JPanel runeTablet() {
        RoundedPanel box = new RoundedPanel(8, hex("#100c1c"), hex("#4a2a80"));
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));
        box.setAlignmentX(Component.LEFT_ALIGNMENT);
        return box;
    }

    private JLabel mutedLabel(String text) {
        JLabel l = new JLabel(text.toUpperCase());
        l.setFont(FONT_SANS_10);
        l.setForeground(C_TEXT_MUTED);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JLabel centeredLabel(String text, Color fg, Font font) {
        JLabel l = new JLabel(text, SwingConstants.CENTER);
        l.setFont(font);
        l.setForeground(fg);
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        return l;
    }

    private JPanel divider() {
        JPanel r = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(C_PURPLE_DIM);
                g.fillRect(0, 0, getWidth(), 1);
            }
        };
        r.setBackground(C_BG);
        r.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        r.setPreferredSize(new Dimension(0, 1));
        r.setAlignmentX(Component.LEFT_ALIGNMENT);
        return r;
    }

    private JButton primaryBtn(String text, ActionListener al) {
        JButton b = styledBtn(text.toUpperCase(), C_GOLD, C_SURFACE, C_PURPLE_MID);
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        b.addActionListener(al);
        return b;
    }

    private JButton ghostBtn(String text, ActionListener al) {
        JButton b = styledBtn(text.toUpperCase(), C_TEXT_MUTED, C_BG, C_PURPLE_DIM);
        b.setFont(FONT_GEORGIA_11);
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        b.addActionListener(al);
        return b;
    }

    private JButton styledBtn(String text, Color fg, Color bg, Color border) {
        JButton b = new JButton(text.toUpperCase());
        b.setFont(FONT_SANS_BOLD_12);
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
                b.setBackground(hex("#2a1050"));
                b.setBorder(new CompoundBorder(
                        new LineBorder(C_GOLD, 1, true),
                        BorderFactory.createEmptyBorder(10, 16, 10, 16)));
            }
            @Override public void mouseExited(MouseEvent e) {
                b.setBackground(bg);
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
        scroll.getViewport().setBackground(C_BG);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.getVerticalScrollBar().setBackground(C_BG);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    private static Component vGap(int height) {
        return Box.createRigidArea(new Dimension(0, height));
    }

    private static Color hex(String h) {
        return Color.decode(h);
    }

    // =========================================================================
    //  INNER CLASS: RoundedPanel
    // =========================================================================

    /** A JPanel with rounded corners, custom background and border color. */
    private static class RoundedPanel extends JPanel {
        private final int   radius;
        private final Color bg;
        private final Color border;

        RoundedPanel(int radius, Color bg, Color border) {
            this.radius = radius;
            this.bg     = bg;
            this.border = border;
            setOpaque(false);
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bg);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), radius * 2, radius * 2));
            g2.setColor(border);
            g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1, getHeight() - 1, radius * 2, radius * 2));
            g2.dispose();
            super.paintComponent(g);
        }
    }
}