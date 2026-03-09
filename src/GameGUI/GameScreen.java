package GameGUI;

import GameGUI.HeroData.HeroDefinition;
import GameGUI.HeroData.EnemyDefinition;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

/**
 * GameScreen.java  — REFACTORED
 *
 * This is the main game container. It was originally a monolithic class
 * containing: intro dialogue, login popup, exam popup, character selection,
 * battle choice popup, sprite rendering, and HP bars — all inline.
 *
 * After refactoring, responsibilities are separated:
 *
 *  ┌─────────────────────────────────────────────────────────────┐
 *  │  GameScreen      (this file)                                │
 *  │   Orchestrates screen transitions via CardLayout            │
 *  │   Owns: dialogue, login popup, exam popup (from original)   │
 *  │   Delegates to:                                             │
 *  │    ├── HeroSelectionPanel  (hero picking screen)            │
 *  │    ├── BattlePanel         (combat screen)                  │
 *  │    └── BattleLogic         (pure engine, no Swing)          │
 *  │                                                             │
 *  │  Data lives in: HeroData.java                               │
 *  └─────────────────────────────────────────────────────────────┘
 *
 * Preserved from original GameScreen.java:
 *  - All dialogue[] strings (intro story)
 *  - kaelStory[] (now generalized to hero story via HeroData)
 *  - Login popup (username + password)
 *  - Exam popup
 *  - Character selection popup (now full HeroSelectionPanel)
 *  - Battle choice popup (Fight / Run)
 *  - Typing animation (startTyping)
 *  - continueDialogue() flow
 *  - JLayeredPane structure
 *  - Sprite animation system (moved to BattlePanel)
 *  - createButton() helper
 */
public class GameScreen extends JPanel {

    // ─── Screen names (CardLayout keys) ──────────────────────────────────────
    private static final String SCREEN_INTRO     = "intro";
    private static final String SCREEN_SELECTION = "selection";
    private static final String SCREEN_BATTLE    = "battle";

    // ─── Layout ───────────────────────────────────────────────────────────────
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel     cardPanel  = new JPanel(cardLayout);

    // ─── Sub-panels ───────────────────────────────────────────────────────────
    private HeroSelectionPanel heroSelectionPanel;
    private BattlePanel        battlePanel;

    // ─── Intro screen widgets (preserved from original GameScreen.java) ───────
    JTextArea dialogueBox;
    JButton   continueBtn, menuBtn, backBtn, exitBtn;

    JPanel    loginPopup, examPopup;
    JTextField    usernameField;
    JPasswordField passwordField;
    JButton   loginStartBtn, loginExitBtn;
    JButton   examStartBtn,  examExitBtn;

    JPanel    battleChoicePopup;
    JButton   fightBtn, runBtn;

    Timer     typingTimer;
    int       dialogueIndex = 0;
    int       charIndex     = 0;

    // ─── State ────────────────────────────────────────────────────────────────
    private HeroDefinition confirmedHero = null;

    // ─── Preserved dialogue strings (from original GameScreen.java) ──────────
    String[] dialogues = {
            "💡 It's just another Tuesday, you come in for your Java examination.\n" +
                    "You walk in and Professor Khai greets you warmly as you sit before\n" +
                    "the CodeChum login screen.\n" +
                    "You place your hands on the keyboard and login... L15Y07W.... ⌨️",

            "The moment you press \"Start\", the monitor ripples like water... 🌊\n" +
                    "The screen glitches... ⚡\n" +
                    "And the world turns to black as the room seems to wrap around you. 🕳️",

            "When you come to your senses, you're no longer in the lab.\n" +
                    "You wake up in an unfamiliar place. 👁️"
    };

    // ─── Constructor ──────────────────────────────────────────────────────────

    public GameScreen() {
        setLayout(new BorderLayout());
        setBackground(new Color(28, 26, 44));

        cardPanel.setBackground(new Color(28, 26, 44));

        // Build each screen
        cardPanel.add(buildIntroScreen(), SCREEN_INTRO);

        heroSelectionPanel = new HeroSelectionPanel();
        heroSelectionPanel.setOnHeroConfirmed(this::onHeroConfirmed);
        cardPanel.add(heroSelectionPanel, SCREEN_SELECTION);

        battlePanel = new BattlePanel();
        battlePanel.setOnReturnToSelection(this::goToSelection);
        battlePanel.setOnRestartBattle(this::restartBattle);
        cardPanel.add(battlePanel, SCREEN_BATTLE);

        add(cardPanel, BorderLayout.CENTER);

        // Start the intro
        startTyping(dialogues[dialogueIndex], null);
    }

    // ─── Screen navigation ────────────────────────────────────────────────────

    private void goToIntro()     { cardLayout.show(cardPanel, SCREEN_INTRO);     }
    private void goToSelection() { cardLayout.show(cardPanel, SCREEN_SELECTION); }

    private void goToBattle() {
        if (confirmedHero == null) return;
        EnemyDefinition enemy = pickEnemy();
        battlePanel.startBattle(confirmedHero, enemy);
        cardLayout.show(cardPanel, SCREEN_BATTLE);
    }

    private void restartBattle() {
        // Same hero, new enemy
        goToBattle();
    }

    /** Picks an enemy matching the current world level (mirrors StoryEngine world flow). */
    private EnemyDefinition pickEnemy() {
        // For now picks a random enemy — extend to use StoryEngine.getCurrWorldLevel()
        var enemies = HeroData.ENEMIES;
        return enemies.get(new Random().nextInt(enemies.size()));
    }

    // ─── Hero confirmed callback ──────────────────────────────────────────────

    /**
     * Called by HeroSelectionPanel when the player clicks "Enter the Arena".
     * Mirrors the old Kael button listener + fightBtn listener flow.
     */
    private void onHeroConfirmed(HeroDefinition hero) {
        this.confirmedHero = hero;

        // Pick an enemy (you already have this helper method)
        EnemyDefinition enemy = pickEnemy();

        // Tell the battle panel to load this specific data
        battlePanel.startBattle(confirmedHero, enemy);

        // Switch the view to the battle screen
        cardLayout.show(cardPanel, SCREEN_BATTLE);
    }

    // ─── Result overlay (called by BattlePanel) ───────────────────────────────

    /**
     * BattlePanel calls this to display the victory/defeat overlay.
     * Using a JLayeredPane on the battle screen ensures it sits on top.
     */
    public void showResultOverlay(JPanel overlay) {
        // Instead of looking for the JFrame, add it directly to the BattlePanel
        // since the BattlePanel is what's currently visible during a result.
        battlePanel.add(overlay);
        battlePanel.setComponentZOrder(overlay, 0); // Force it to the front
        overlay.setBounds(0, 0, battlePanel.getWidth(), battlePanel.getHeight());
        overlay.setVisible(true);
        battlePanel.repaint();
    }

    private JLayeredPane getLayeredPane() {
        Container parent = getParent();
        while (parent != null) {
            if (parent instanceof JFrame frame) return frame.getLayeredPane();
            parent = parent.getParent();
        }
        // Fallback: create our own
        JLayeredPane lp = new JLayeredPane();
        add(lp, BorderLayout.NORTH);
        return lp;
    }

    // ─── Intro screen (preserved from original GameScreen.java) ──────────────

    private JPanel buildIntroScreen() {
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setBackground(new Color(40, 40, 60));

        JPanel wrapper = new JPanel(null) {
            @Override public void doLayout() {
                super.doLayout();
                layeredPane.setBounds(0, 0, getWidth(), getHeight());
            }
        };
        wrapper.setBackground(new Color(40, 40, 60));
        wrapper.setLayout(null);
        wrapper.setPreferredSize(new Dimension(1280, 720));

        // Background panel (scene art placeholder)
        JPanel sceneBg = new JPanel();
        sceneBg.setBounds(0, 0, 1280, 520);
        sceneBg.setBackground(new Color(30, 28, 50));

        // Dialogue box — preserved from original
        dialogueBox = new JTextArea();
        dialogueBox.setBounds(33, 533, 933, 153);
        dialogueBox.setEditable(false);
        dialogueBox.setLineWrap(true);
        dialogueBox.setWrapStyleWord(true);
        dialogueBox.setFont(new Font("Monospaced", Font.PLAIN, 13));
        dialogueBox.setBackground(new Color(20, 18, 34));
        dialogueBox.setForeground(new Color(220, 210, 180));
        dialogueBox.setBorder(BorderFactory.createLineBorder(new Color(80, 70, 110), 2));

        // Side buttons — preserved from original
        continueBtn = createButton("Continue", 1000, 560, 140, 50);
        menuBtn     = createButton("Menu",     1133, 560, 140, 50);
        backBtn     = createButton("Back",     1000, 613, 140, 50);
        exitBtn     = createButton("Exit",     1133, 613, 140, 50);

        exitBtn.addActionListener(e -> System.exit(0));
        continueBtn.addActionListener(e -> continueDialogue());

        // Login popup — preserved from original
        loginPopup = buildLoginPopup();

        // Exam popup — preserved from original
        examPopup  = buildExamPopup();

        // Battle choice popup — preserved from original, but Fight now goes to selection
        battleChoicePopup = buildBattleChoicePopup();

        layeredPane.setBounds(0, 0, 1280, 720);
        layeredPane.add(sceneBg,           JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(dialogueBox,       JLayeredPane.PALETTE_LAYER);
        layeredPane.add(continueBtn,       JLayeredPane.PALETTE_LAYER);
        layeredPane.add(menuBtn,           JLayeredPane.PALETTE_LAYER);
        layeredPane.add(backBtn,           JLayeredPane.PALETTE_LAYER);
        layeredPane.add(exitBtn,           JLayeredPane.PALETTE_LAYER);
        layeredPane.add(loginPopup,        JLayeredPane.MODAL_LAYER);
        layeredPane.add(examPopup,         JLayeredPane.MODAL_LAYER);
        layeredPane.add(battleChoicePopup, JLayeredPane.MODAL_LAYER);

        wrapper.add(layeredPane);
        return wrapper;
    }

    // ─── Login popup — preserved from original GameScreen.java ───────────────

    private JPanel buildLoginPopup() {
        JPanel popup = new JPanel(null);
        popup.setBounds(200, 367, 667, 147);
        popup.setBackground(new Color(80, 80, 120));
        popup.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));
        popup.setVisible(false);

        JLabel title = new JLabel("CodeChum Login");
        title.setBounds(200, 10, 250, 25);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        popup.add(title);

        JLabel user = new JLabel("Username:");
        user.setBounds(100, 53, 100, 20);
        user.setForeground(Color.WHITE);
        popup.add(user);

        usernameField = new JTextField();
        usernameField.setBounds(207, 53, 267, 25);
        popup.add(usernameField);

        JLabel pass = new JLabel("Password:");
        pass.setBounds(100, 87, 100, 20);
        pass.setForeground(Color.WHITE);
        popup.add(pass);

        passwordField = new JPasswordField();
        passwordField.setBounds(207, 87, 267, 25);
        popup.add(passwordField);

        loginStartBtn = new JButton("Start");
        loginStartBtn.setBounds(500, 53, 80, 25);
        popup.add(loginStartBtn);

        loginExitBtn = new JButton("Exit");
        loginExitBtn.setBounds(500, 87, 80, 25);
        loginExitBtn.addActionListener(e -> System.exit(0));
        popup.add(loginExitBtn);

        // Mirrors original: hide login → show exam popup
        loginStartBtn.addActionListener(e -> {
            if (!usernameField.getText().isEmpty() && passwordField.getPassword().length > 0) {
                loginPopup.setVisible(false);
                startTyping("Logging in...", () -> examPopup.setVisible(true));
            }
        });

        return popup;
    }

    // ─── Exam popup — preserved from original GameScreen.java ────────────────

    private JPanel buildExamPopup() {
        JPanel popup = new JPanel(null);
        popup.setBounds(133, 400, 800, 100);
        popup.setBackground(new Color(50, 50, 90));
        popup.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));
        popup.setVisible(false);

        JLabel examLabel = new JLabel("OOP1 Final Exam Batch 1 - G1");
        examLabel.setBounds(200, 10, 400, 25);
        examLabel.setForeground(Color.WHITE);
        popup.add(examLabel);

        examStartBtn = new JButton("Start");
        examStartBtn.setBounds(267, 53, 80, 25);
        popup.add(examStartBtn);

        examExitBtn = new JButton("Exit");
        examExitBtn.setBounds(360, 53, 80, 25);
        examExitBtn.addActionListener(e -> System.exit(0));
        popup.add(examExitBtn);

        // Mirrors original: hide exam → advance dialogue to index 1
        examStartBtn.addActionListener(e -> {
            examPopup.setVisible(false);
            dialogueIndex = 1;
            continueBtn.setEnabled(true);
            startTyping(dialogues[dialogueIndex], null);
        });

        return popup;
    }

    // ─── Battle choice popup — preserved from original GameScreen.java ────────

    private JPanel buildBattleChoicePopup() {
        JPanel popup = new JPanel(null);
        popup.setBounds(333, 400, 333, 100);
        popup.setBackground(new Color(70, 70, 110));
        popup.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));
        popup.setVisible(false);

        JLabel battleLabel = new JLabel("A wild enemy appears! What will you do?");
        battleLabel.setBounds(20, 7, 300, 25);
        battleLabel.setForeground(Color.WHITE);
        popup.add(battleLabel);

        fightBtn = new JButton("Fight");
        fightBtn.setBounds(40, 47, 100, 33);
        popup.add(fightBtn);

        runBtn = new JButton("Run");
        runBtn.setBounds(187, 47, 100, 33);
        popup.add(runBtn);

        // Fight → go to hero selection (replaces original's inline battle panel)
        fightBtn.addActionListener(e -> {
            battleChoicePopup.setVisible(false);
            goToSelection(); // ← New: full hero selection screen
        });

        runBtn.addActionListener(e -> {
            dialogueBox.setText("You chose to RUN! The enemy gives chase... for now.");
            battleChoicePopup.setVisible(false);
        });

        return popup;
    }

    // ─── Dialogue system — preserved from original GameScreen.java ────────────

    /**
     * Mirrors original continueDialogue() — handles intro flow and transitions.
     * Extended: after dialogues finish, shows battleChoicePopup (not characterPopup directly).
     */
    private void continueDialogue() {
        if (typingTimer != null && typingTimer.isRunning()) {
            typingTimer.stop();
            dialogueBox.setText(dialogues[Math.min(dialogueIndex, dialogues.length - 1)]);
            return;
        }

        dialogueIndex++;

        if (dialogueIndex == 1) {
            loginPopup.setVisible(true);
            continueBtn.setEnabled(false);
            dialogueBox.setText("");
            return;
        }

        if (dialogueIndex >= dialogues.length) {
            continueBtn.setEnabled(false);
            battleChoicePopup.setVisible(true);
            dialogueBox.setText("Choose your character.");
            return;
        }

        startTyping(dialogues[dialogueIndex], null);
    }

    /**
     * Typing animation — preserved exactly from original GameScreen.java.
     */
    private void startTyping(String text, Runnable callback) {
        dialogueBox.setText("");
        charIndex = 0;

        typingTimer = new Timer(30, e -> {
            if (charIndex < text.length()) {
                dialogueBox.append(String.valueOf(text.charAt(charIndex)));
                charIndex++;
            } else {
                typingTimer.stop();
                if (callback != null) callback.run();
            }
        });
        typingTimer.start();
    }

    /**
     * createButton helper — preserved from original GameScreen.java.
     */
    private JButton createButton(String text, int x, int y, int w, int h) {
        JButton btn = new JButton(text);
        btn.setBounds(x, y, w, h);
        return btn;
    }
}