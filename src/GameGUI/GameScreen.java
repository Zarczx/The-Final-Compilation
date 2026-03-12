package GameGUI;

import GameGUI.HeroData.HeroDefinition;
import GameGUI.HeroData.EnemyDefinition;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class GameScreen extends JPanel {

    private static final String SCREEN_INTRO        = "intro";
    private static final String SCREEN_SELECTION    = "selection";
    private static final String SCREEN_POST_SELECT  = "postSelect";
    private static final String SCREEN_WORLD1_INTRO = "world1Intro";
    private static final String SCREEN_BATTLE       = "battle";

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel     cardPanel  = new JPanel(cardLayout);

    private HeroSelectionPanel heroSelectionPanel;
    private BattlePanel        battlePanel;
    private JPanel             postSelectPanel;
    private JPanel             world1IntroPanel;

    private JTextArea postDialogueBox;
    private JButton   postContinueBtn;
    private int       postDialogueIndex = 0;

    private JTextArea w1DialogueBox;
    private JButton   w1ContinueBtn;
    private int       w1DialogueIndex  = 0;
    private Timer     w1TypingTimer;

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

    private JLabel sceneBgLabel;
    private HeroDefinition confirmedHero = null;

    String[] dialogues = {
            "💡 It's just another Tuesday, you come in for your Java examination.",

            "You walk in and Professor Khai greets you warmly as you sit before\n" +
                    "the CodeChum login screen.",

            "You place your hands on the keyboard and login... L15Y07W.... ⌨️",

            "Enter your Username, password, and click Sign in.",
    };

    // ─── Constructor ──────────────────────────────────────────────────────────

    public GameScreen() {
        setLayout(new BorderLayout());
        setBackground(new Color(28, 26, 44));

        cardPanel.setBackground(new Color(28, 26, 44));

        cardPanel.add(buildIntroScreen(), SCREEN_INTRO);

        heroSelectionPanel = new HeroSelectionPanel();
        heroSelectionPanel.setOnHeroConfirmed(this::onHeroConfirmed);
        cardPanel.add(heroSelectionPanel, SCREEN_SELECTION);

        postSelectPanel = buildPostSelectScreen();
        cardPanel.add(postSelectPanel, SCREEN_POST_SELECT);

        world1IntroPanel = buildWorld1IntroScreen();
        cardPanel.add(world1IntroPanel, SCREEN_WORLD1_INTRO);

        battlePanel = new BattlePanel();
        battlePanel.setOnReturnToSelection(this::goToSelection);
        battlePanel.setOnRestartBattle(this::restartBattle);
        cardPanel.add(battlePanel, SCREEN_BATTLE);

        add(cardPanel, BorderLayout.CENTER);

        setSceneBackground("assets/Backgrounds/NGEBackground.png");
        startTyping(dialogues[dialogueIndex], null);
    }

    // ─── Screen navigation ────────────────────────────────────────────────────

    private void goToIntro()       { cardLayout.show(cardPanel, SCREEN_INTRO);        }
    private void goToSelection()   { cardLayout.show(cardPanel, SCREEN_SELECTION);    }
    private void goToPostSelect()  { cardLayout.show(cardPanel, SCREEN_POST_SELECT);  }
    private void goToWorld1Intro() { cardLayout.show(cardPanel, SCREEN_WORLD1_INTRO); }

    private void goToBattle() {
        if (confirmedHero == null) return;
        EnemyDefinition enemy = pickEnemy();
        battlePanel.startBattle(confirmedHero, enemy);
        cardLayout.show(cardPanel, SCREEN_BATTLE);
    }

    private void restartBattle() { goToBattle(); }

    private EnemyDefinition pickEnemy() {
        var enemies = HeroData.ENEMIES;
        return enemies.get(new Random().nextInt(enemies.size()));
    }

    // ─── Hero confirmed ───────────────────────────────────────────────────────

    private void onHeroConfirmed(HeroDefinition hero) {
        this.confirmedHero = hero;
        buildPostSelectDialogues(hero);
        goToPostSelect();
        postDialogueIndex = 0;
        startPostTyping();
    }

    // ─── Result overlay ───────────────────────────────────────────────────────

    public void showResultOverlay(JPanel overlay) {
        battlePanel.add(overlay);
        battlePanel.setComponentZOrder(overlay, 0);
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
        JLayeredPane lp = new JLayeredPane();
        add(lp, BorderLayout.NORTH);
        return lp;
    }

    // ─── Background helper ────────────────────────────────────────────────────

    private void setSceneBackground(String resourcePath) {
        if (sceneBgLabel == null) return;
        String absPath = resourcePath.startsWith("/") ? resourcePath : "/" + resourcePath;
        java.net.URL url = getClass().getResource(absPath);
        if (url != null) {
            ImageIcon raw    = new ImageIcon(url);
            Image     scaled = raw.getImage().getScaledInstance(1280, 520, Image.SCALE_SMOOTH);
            sceneBgLabel.setIcon(new ImageIcon(scaled));
            sceneBgLabel.setText("");
        } else {
            sceneBgLabel.setIcon(null);
            sceneBgLabel.setText("<html><center><font color='#ff6666'>⚠ Missing:<br>"
                    + resourcePath + "</font></center></html>");
            System.out.println("[GameScreen] Asset not found: " + absPath);
        }
        sceneBgLabel.repaint();
    }

    // ─── Intro screen ─────────────────────────────────────────────────────────

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

        sceneBgLabel = new JLabel();
        sceneBgLabel.setBounds(0, 0, 1280, 520);
        sceneBgLabel.setHorizontalAlignment(SwingConstants.CENTER);
        sceneBgLabel.setVerticalAlignment(SwingConstants.CENTER);
        sceneBgLabel.setBackground(new Color(30, 28, 50));
        sceneBgLabel.setOpaque(true);

        dialogueBox = new JTextArea();
        dialogueBox.setBounds(33, 533, 933, 153);
        dialogueBox.setEditable(false);
        dialogueBox.setLineWrap(true);
        dialogueBox.setWrapStyleWord(true);
        dialogueBox.setFont(new Font("Monospaced", Font.PLAIN, 13));
        dialogueBox.setBackground(new Color(20, 18, 34));
        dialogueBox.setForeground(new Color(220, 210, 180));
        dialogueBox.setBorder(BorderFactory.createLineBorder(new Color(80, 70, 110), 2));

        continueBtn = createImageButton(
                "/assets/GUIButtons/Continue.png", "/assets/GUIButtons/ContinueHover.png",
                994, 558, 154, 64, "Continue");
        menuBtn = createImageButton(
                "/assets/GUIButtons/Menu.png", "/assets/GUIButtons/MenuHover.png",
                1128, 560, 148, 58, "Menu");
        backBtn = createImageButton(
                "/assets/GUIButtons/Back.png", "/assets/GUIButtons/BackHover.png",
                1000, 613, 140, 50, "Back");
        exitBtn = createImageButton(
                "/assets/GUIButtons/Exit.png", "/assets/GUIButtons/ExitHover.png",
                1133, 613, 140, 50, "Exit");

        exitBtn.addActionListener(e -> System.exit(0));
        continueBtn.addActionListener(e -> continueDialogue());
        // continueBtn starts ENABLED — it gets disabled only at step 3 (sign-in prompt)

        loginPopup        = buildLoginPopup();
        examPopup         = buildExamPopup();
        battleChoicePopup = buildBattleChoicePopup();

        layeredPane.setBounds(0, 0, 1280, 720);
        layeredPane.add(sceneBgLabel,      JLayeredPane.DEFAULT_LAYER);
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

    // ─── Login popup ──────────────────────────────────────────────────────────

    private JPanel buildLoginPopup() {
        JPanel popup = new JPanel(null);
        popup.setBounds(535, 163, 260, 95);
        popup.setOpaque(false);
        popup.setVisible(false);

        usernameField = new JTextField("username@email.edu");
        usernameField.setBounds(27, 17, 232, 14);
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        usernameField.setForeground(Color.WHITE);
        usernameField.setBackground(Color.WHITE);
        usernameField.setOpaque(true);
        usernameField.setCaretColor(Color.DARK_GRAY);
        usernameField.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
        usernameField.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                if (usernameField.getText().equals("username@email.edu")) {
                    usernameField.setText("");
                    usernameField.setForeground(Color.DARK_GRAY);
                }
            }
            @Override public void focusLost(FocusEvent e) {
                if (usernameField.getText().isBlank()) {
                    usernameField.setText("username@email.edu");
                    usernameField.setForeground(Color.WHITE);
                }
            }
        });
        popup.add(usernameField);

        passwordField = new JPasswordField();
        passwordField.setBounds(25, 37, 232, 14);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        passwordField.setForeground(Color.DARK_GRAY);
        passwordField.setBackground(Color.WHITE);
        passwordField.setOpaque(true);
        passwordField.setCaretColor(Color.DARK_GRAY);
        passwordField.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
        popup.add(passwordField);

        loginStartBtn = new JButton();
        loginStartBtn.setBounds(24, 63, 235, 23);
        loginStartBtn.setOpaque(false);
        loginStartBtn.setContentAreaFilled(false);
        loginStartBtn.setBorderPainted(false);
        loginStartBtn.setFocusPainted(false);
        loginStartBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        java.net.URL signInUrl = getClass().getResource("/assets/GUIButtons/NGESignUPButton.png");
        if (signInUrl != null) {
            ImageIcon raw    = new ImageIcon(signInUrl);
            Image     scaled = raw.getImage().getScaledInstance(235, 23, Image.SCALE_SMOOTH);
            loginStartBtn.setIcon(new ImageIcon(scaled));
        } else {
            loginStartBtn.setText("Sign In");
            loginStartBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
            loginStartBtn.setForeground(Color.WHITE);
            loginStartBtn.setBackground(new Color(60, 100, 200));
            loginStartBtn.setContentAreaFilled(true);
            loginStartBtn.setBorderPainted(false);
            System.out.println("[GameScreen] Missing: /assets/GUIButtons/NGESignUPButton.png");
        }
        popup.add(loginStartBtn);

        loginExitBtn = new JButton();
        loginExitBtn.setVisible(false);
        popup.add(loginExitBtn);

        // ── Sign In action ────────────────────────────────────────────────────
        // CHANGED: type "Logging in..." → wait 1s → NGEBatch1 + examPopup + "Press Start..."
        loginStartBtn.addActionListener(e -> {
            String user = usernameField.getText().trim();
            String pass = new String(passwordField.getPassword()).trim();
            boolean emptyUser = user.isBlank() || user.equals("username@email.edu");
            if (emptyUser || pass.isBlank()) return;
            loginPopup.setVisible(false);
            startTyping("Logging in...", () -> {
                Timer logDelay = new Timer(1000, ev -> {
                    setSceneBackground("assets/Backgrounds/NGEBatch1.png");
                    examPopup.setVisible(true);
                    startTyping("Press Start to start the exam.", null);
                });
                logDelay.setRepeats(false);
                logDelay.start();
            });
        });

        return popup;
    }

    // ─── Exam popup — transparent, image Start button only ───────────────────

    private JPanel buildExamPopup() {
        JPanel popup = new JPanel(null);
        // CHANGED: x moved left by 2px, width increased by 2px
        popup.setBounds(908, 199, 182, 59);
        popup.setOpaque(false);
        popup.setVisible(false);

        examExitBtn = new JButton();
        examExitBtn.setVisible(false);
        popup.add(examExitBtn);

        examStartBtn = new JButton();
        examStartBtn.setBounds(0, 0, 182, 59);
        examStartBtn.setOpaque(false);
        examStartBtn.setContentAreaFilled(false);
        examStartBtn.setBorderPainted(false);
        examStartBtn.setFocusPainted(false);
        examStartBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        java.net.URL startUrl = getClass().getResource("/assets/GUIButtons/NGEStartButton.png");
        if (startUrl != null) {
            ImageIcon raw    = new ImageIcon(startUrl);
            Image     scaled = raw.getImage().getScaledInstance(182, 59, Image.SCALE_SMOOTH);
            examStartBtn.setIcon(new ImageIcon(scaled));
        } else {
            examStartBtn.setText("Start");
            examStartBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
            examStartBtn.setForeground(Color.WHITE);
            examStartBtn.setBackground(new Color(60, 160, 80));
            examStartBtn.setContentAreaFilled(true);
            System.out.println("[GameScreen] Missing: /assets/GUIButtons/NGEStartButton.png");
        }
        popup.add(examStartBtn);

        examStartBtn.addActionListener(e -> {
            examPopup.setVisible(false);
            continueBtn.setEnabled(true);
            runLightsSequence();
        });

        return popup;
    }

    // ─── Lights-out cinematic sequence ───────────────────────────────────────
    //
    //  1. NGEBeforeLights.png (1s)
    //  2. Screen shakes for 1.5s
    //  3. Dialogue: "Screen flickers..."
    //  4. NGEBeforeLights2.png
    //  5. Dialogue: "Everything goes silent."
    //  6. NGEBeforeLights3.jpg (1s) → NGEBeforeLights4.jpg (1s) → NGEBeforeLights5.jpg (1s)
    //  7. Dialogue: "The screen glitches... ⚡\nAnd the world turns to black..."
    //  8. NGEBeforeLights6.png
    //  9. Dialogue: "When you come to your senses..." → after typing → goToSelection()
    //
    private void runLightsSequence() {
        // Step 1: NGEBeforeLights.png for 1s
        setSceneBackground("assets/Backgrounds/NGEBeforeLights.png");
        delay(1000, () -> {

            // Step 2: Screen shake for 1.5s
            Point origin = getLocation();
            int[] shakeTick = {0};
            Timer shakeTimer = new Timer(50, null);
            shakeTimer.addActionListener(ev -> {
                int dx = (shakeTick[0] % 2 == 0) ? 5 : -5;
                int dy = (shakeTick[0] % 4 < 2) ? 3 : -3;
                setLocation(origin.x + dx, origin.y + dy);
                shakeTick[0]++;
            });
            shakeTimer.setRepeats(true);
            shakeTimer.start();

            delay(1500, () -> {
                shakeTimer.stop();
                setLocation(origin);

                // Step 2b: pause 1s after shake
                delay(1000, () -> {

                    // Step 3: "Screen flickers..."
                    startTyping("Screen flickers...", () -> {

                        // Step 3b: wait 1s after dialogue
                        delay(1000, () -> {

                            // Step 4: NGEBeforeLights2.png
                            setSceneBackground("assets/Backgrounds/NGEBeforeLights2.png");

                            // Step 5: "Everything goes silent."
                            startTyping("Everything goes silent.", () -> {

                                // Step 6: Frames 3→4→5 each 1s
                                String[] midFrames = {
                                        "assets/Backgrounds/NGEBeforeLights3.jpg",
                                        "assets/Backgrounds/NGEBeforeLights4.jpg",
                                        "assets/Backgrounds/NGEBeforeLights5.jpg",
                                };
                                int[] fi = {0};
                                Timer midTimer = new Timer(1000, null);
                                midTimer.addActionListener(em -> {
                                    if (fi[0] < midFrames.length) {
                                        setSceneBackground(midFrames[fi[0]++]);
                                    } else {
                                        midTimer.stop();

                                        // Step 7: Glitch dialogue
                                        startTyping(
                                                "The screen glitches... ⚡\n" +
                                                        "And the world turns to black as the room seems to wrap around you. 🕳️",
                                                () -> {
                                                    // Step 8: NGEBeforeLights6.png
                                                    setSceneBackground("assets/Backgrounds/NGEBeforeLights6.png");

                                                    // Step 9: Final dialogue → goToSelection
                                                    startTyping(
                                                            "When you come to your senses, you're no longer in the lab.\n" +
                                                                    "You wake up in an unfamiliar place. 👁️",
                                                            () -> goToSelection()
                                                    );
                                                }
                                        );
                                    }
                                });
                                midTimer.setRepeats(true);
                                midTimer.start();
                            });
                        });
                    });
                });
            });
        });
    }

    /** Fire {@code action} once after {@code ms} milliseconds on the EDT. */
    private void delay(int ms, Runnable action) {
        Timer t = new Timer(ms, null);
        t.setRepeats(false);
        t.addActionListener(e -> { t.stop(); action.run(); });
        t.start();
    }

    // ─── Battle choice popup ──────────────────────────────────────────────────

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

        fightBtn.addActionListener(e -> {
            battleChoicePopup.setVisible(false);
            goToBattle();
        });

        runBtn.addActionListener(e -> {
            dialogueBox.setText("You chose to RUN! The enemy gives chase... for now.");
            battleChoicePopup.setVisible(false);
        });

        return popup;
    }

    // ─── Dialogue system ─────────────────────────────────────────────────────
    //
    //  Step 0  → NGEBackground, type dialogue[0]                   → Continue
    //  Step 1  → type dialogue[1] first, THEN Khai2(1s)→Khai(1.5s)→Khai2 → Continue
    //  Step 2  → NGELoginUser(1s)→NGELoginUser2, type dialogue[2]  → Continue
    //  Step 3  → CHANGED: NGELogin2 + loginPopup shown simultaneously, type dialogue[3]
    //            continueBtn DISABLED — user must Sign In
    //  Sign In → CHANGED: "Logging in..."(1s) → NGEBatch1 + examPopup + "Press Start..."
    //
    private void continueDialogue() {
        if (typingTimer != null && typingTimer.isRunning()) {
            typingTimer.stop();
            dialogueBox.setText(dialogues[Math.min(dialogueIndex, dialogues.length - 1)]);
            return;
        }

        dialogueIndex++;

        // ── Step 1: type dialogue first, then do Khai background animation ────
        if (dialogueIndex == 1) {
            continueBtn.setEnabled(false);

            startTyping(dialogues[1], () -> {
                setSceneBackground("assets/Backgrounds/NGESirKhai2.png");

                Timer khaiPhase2 = new Timer(1000, e -> {
                    setSceneBackground("assets/Backgrounds/NGESirKhai.png");

                    Timer khaiPhase3 = new Timer(1500, e2 -> {
                        setSceneBackground("assets/Backgrounds/NGESirKhai2.png");
                        continueBtn.setEnabled(true);
                    });
                    khaiPhase3.setRepeats(false);
                    khaiPhase3.start();
                });
                khaiPhase2.setRepeats(false);
                khaiPhase2.start();
            });
            return;
        }

        // ── Step 2: NGELoginUser(1s) → NGELoginUser2, then type dialogue[2] ──
        if (dialogueIndex == 2) {
            continueBtn.setEnabled(false);

            setSceneBackground("assets/Backgrounds/NGELoginUser.png");

            Timer loginUserTimer = new Timer(1000, e -> {
                setSceneBackground("assets/Backgrounds/NGELoginUser2.png");
                startTyping(dialogues[2], () -> continueBtn.setEnabled(true));
            });
            loginUserTimer.setRepeats(false);
            loginUserTimer.start();
            return;
        }

        // ── Step 3: CHANGED — NGELogin2 + loginPopup at same time, type dialogue[3]
        if (dialogueIndex == 3) {
            continueBtn.setEnabled(false);
            setSceneBackground("assets/Backgrounds/NGELogin2.png");
            loginPopup.setVisible(true);          // shown simultaneously with bg swap
            startTyping(dialogues[3], null);      // continueBtn stays locked
            return;
        }

        if (dialogueIndex >= dialogues.length) {
            continueBtn.setEnabled(false);
            goToSelection();
        }
    }

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

    private JButton createButton(String text, int x, int y, int w, int h) {
        JButton btn = new JButton(text);
        btn.setBounds(x, y, w, h);
        return btn;
    }

    /**
     * Image button with normal/hover swap. Falls back to text if assets missing.
     */
    private JButton createImageButton(String normalPath, String hoverPath,
                                      int x, int y, int w, int h, String fallbackText) {
        JButton btn = new JButton();
        btn.setBounds(x, y, w, h);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        java.net.URL normalUrl = getClass().getResource(normalPath);
        java.net.URL hoverUrl  = getClass().getResource(hoverPath);

        if (normalUrl != null) {
            ImageIcon normalIcon = new ImageIcon(
                    new ImageIcon(normalUrl).getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH));
            ImageIcon hoverIcon = hoverUrl != null
                    ? new ImageIcon(new ImageIcon(hoverUrl).getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH))
                    : normalIcon;
            btn.setIcon(normalIcon);
            btn.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override public void mouseEntered(java.awt.event.MouseEvent e) { btn.setIcon(hoverIcon);  }
                @Override public void mouseExited (java.awt.event.MouseEvent e) { btn.setIcon(normalIcon); }
            });
        } else {
            btn.setText(fallbackText);
            btn.setForeground(Color.WHITE);
            btn.setContentAreaFilled(true);
            System.out.println("[GameScreen] Missing button asset: " + normalPath);
        }
        return btn;
    }

    // ─── Post-selection dialogue screen ──────────────────────────────────────

    private String[] postDialogues;

    private void buildPostSelectDialogues(HeroDefinition hero) {
        String heroLine;
        String weaponLine;
        String armorLine;

        switch (hero.name) {
            case "Kael Saint Laurent" -> {
                heroLine   = "⚔️ You have chosen KAEL SAINT LAURENT, the valiant Swordsman!\n\n" + hero.backstory;
                weaponLine = "🗡️ The gods bestow upon you your starting gear...\n\n" +
                        "The Old Broadsword rests firmly in your grasp, its blade marked\n" +
                        "by the scars of past battles.";
                armorLine  = "🛡️ The Leather Guard settles on your shoulders — worn, but reliable.\n\n" +
                        "You are ready. The forest of World 1 awaits.";
            }
            case "Karl Clover Dior IV" -> {
                heroLine   = "🏹 You have chosen KARL CLOVER DIOR IV, the swift Archer!\n\n" + hero.backstory;
                weaponLine = "🏹 The gods bestow upon you your starting gear...\n\n" +
                        "The Shortbow hums softly — each arrow you notch feels like an\n" +
                        "extension of your will.";
                armorLine  = "🛡️ The Leather Guard settles on your shoulders — worn, but reliable.\n\n" +
                        "You are ready. The forest of World 1 awaits.";
            }
            case "Simon Versace" -> {
                heroLine   = "🌟 You have chosen SIMON VERSACE, the arcane Mage!\n\n" + hero.backstory;
                weaponLine = "🔮 The gods bestow upon you your starting gear...\n\n" +
                        "The Wooden Staff pulses faintly, whispering secrets\n" +
                        "of forgotten spells.";
                armorLine  = "🛡️ The Leather Guard shimmers with faint runes — fragile,\n" +
                        "yet brimming with arcane energy.\n\n" +
                        "You are ready. The forest of World 1 awaits.";
            }
            default -> {
                heroLine   = "You have chosen: " + hero.name + ".\n\n" + hero.backstory;
                weaponLine = "✨ The gods bestow upon you your starting gear...";
                armorLine  = "You are ready. The forest of World 1 awaits.";
            }
        }

        postDialogues = new String[]{ heroLine, weaponLine, armorLine };
    }

    private JPanel buildPostSelectScreen() {
        JPanel screen = new JPanel(null);
        screen.setBackground(new Color(28, 26, 44));
        screen.setPreferredSize(new Dimension(1280, 720));

        JPanel bg = new JPanel();
        bg.setBounds(0, 0, 1280, 520);
        bg.setBackground(new Color(20, 18, 35));
        screen.add(bg);

        postDialogueBox = new JTextArea();
        postDialogueBox.setBounds(60, 530, 1000, 140);
        postDialogueBox.setBackground(new Color(30, 28, 50));
        postDialogueBox.setForeground(Color.WHITE);
        postDialogueBox.setFont(new Font("Serif", Font.PLAIN, 16));
        postDialogueBox.setEditable(false);
        postDialogueBox.setLineWrap(true);
        postDialogueBox.setWrapStyleWord(true);
        postDialogueBox.setBorder(BorderFactory.createLineBorder(new Color(100, 80, 180), 2));
        screen.add(postDialogueBox);

        postContinueBtn = new JButton("▶  Continue");
        postContinueBtn.setBounds(1100, 610, 140, 40);
        postContinueBtn.setBackground(new Color(70, 50, 120));
        postContinueBtn.setForeground(Color.WHITE);
        postContinueBtn.setFocusPainted(false);
        postContinueBtn.addActionListener(e -> continuePostDialogue());
        screen.add(postContinueBtn);

        return screen;
    }

    private Timer postTypingTimer;

    private void startPostTyping() {
        if (postDialogues == null || postDialogueIndex >= postDialogues.length) return;
        if (postTypingTimer != null && postTypingTimer.isRunning()) postTypingTimer.stop();

        String text = postDialogues[postDialogueIndex];
        postDialogueBox.setText("");
        int[] ci = {0};

        postTypingTimer = new Timer(25, null);
        postTypingTimer.addActionListener(e -> {
            if (ci[0] < text.length()) {
                postDialogueBox.append(String.valueOf(text.charAt(ci[0]++)));
            } else {
                postTypingTimer.stop();
            }
        });
        postTypingTimer.start();
    }

    private void continuePostDialogue() {
        if (postTypingTimer != null && postTypingTimer.isRunning()) {
            postTypingTimer.stop();
            postDialogueBox.setText(postDialogues[postDialogueIndex]);
            return;
        }
        postDialogueIndex++;
        if (postDialogueIndex < postDialogues.length) {
            startPostTyping();
        } else {
            postDialogueIndex = 0;
            w1DialogueIndex   = 0;
            goToWorld1Intro();
            startW1Typing();
        }
    }

    // ─── World 1 intro dialogue screen ───────────────────────────────────────

    private static final String[] WORLD1_DIALOGUES = {
            "You wake up gasping for air. The world is drained of color.\n\n" +
                    "You are lying on a bed of gray moss in a dead forest. The trees are skeletal\n" +
                    "giants, stripped to bone-white wood. A cold mist coils around your ankles,\n" +
                    "and silence presses from every side — watching, waiting.",

            "A heavy bell tolls in the distance...\n\"Dong... Dong...\"",

            "From the mist steps a figure cloaked in tattered robes.\n" +
                    "He leans heavily on a staff. As he lifts his hood, you jolt back —\n" +
                    "the face is familiar. It looks exactly like your professor, Khai.\n" +
                    "But his eyes are weary, holding the weight of centuries.",

            "🟢 \"Be calm, Traveler. In this realm, I am known as Khai the Gray.\"\n\n" +
                    "\"We suffer because an evil Necromancer has corrupted these lands.\n" +
                    "He has drained the nature itself. We must find the Three Stones of Life\n" +
                    "that hold this reality together. Only then will your path home reveal itself.\"\n\n" +
                    "🟢 Khai fades back into the mist. A low growl vibrates through the ground...\n\n" +
                    "⚔️  Three Rotfang Wolves emerge from the tree line. Their glowing red eyes\n" +
                    "fixate on you. They do not hunt for food — they hunt to kill."
    };

    private JPanel buildWorld1IntroScreen() {
        JPanel screen = new JPanel(null);
        screen.setBackground(new Color(15, 12, 28));
        screen.setPreferredSize(new Dimension(1280, 720));

        JPanel bg = new JPanel(null);
        bg.setBounds(0, 0, 1280, 520);
        bg.setBackground(new Color(10, 8, 22));
        screen.add(bg);

        JLabel worldLabel = new JLabel("— WORLD 1 : THE FOREST OF ENDINGS —", SwingConstants.CENTER);
        worldLabel.setBounds(0, 220, 1280, 50);
        worldLabel.setForeground(new Color(140, 110, 220));
        worldLabel.setFont(new Font("Serif", Font.BOLD | Font.ITALIC, 26));
        bg.add(worldLabel);

        w1DialogueBox = new JTextArea();
        w1DialogueBox.setBounds(60, 530, 1000, 150);
        w1DialogueBox.setBackground(new Color(20, 16, 38));
        w1DialogueBox.setForeground(Color.WHITE);
        w1DialogueBox.setFont(new Font("Serif", Font.PLAIN, 16));
        w1DialogueBox.setEditable(false);
        w1DialogueBox.setLineWrap(true);
        w1DialogueBox.setWrapStyleWord(true);
        w1DialogueBox.setBorder(BorderFactory.createLineBorder(new Color(80, 55, 160), 2));
        screen.add(w1DialogueBox);

        w1ContinueBtn = new JButton("▶  Continue");
        w1ContinueBtn.setBounds(1100, 650, 140, 40);
        w1ContinueBtn.setBackground(new Color(60, 40, 110));
        w1ContinueBtn.setForeground(Color.WHITE);
        w1ContinueBtn.setFocusPainted(false);
        w1ContinueBtn.addActionListener(e -> continueW1Dialogue());
        screen.add(w1ContinueBtn);

        return screen;
    }

    private void startW1Typing() {
        if (w1DialogueIndex >= WORLD1_DIALOGUES.length) return;
        if (w1TypingTimer != null && w1TypingTimer.isRunning()) w1TypingTimer.stop();

        String text = WORLD1_DIALOGUES[w1DialogueIndex];
        w1DialogueBox.setText("");
        int[] ci = {0};

        w1TypingTimer = new Timer(25, null);
        w1TypingTimer.addActionListener(e -> {
            if (ci[0] < text.length()) {
                w1DialogueBox.append(String.valueOf(text.charAt(ci[0]++)));
            } else {
                w1TypingTimer.stop();
            }
        });
        w1TypingTimer.start();
    }

    private void continueW1Dialogue() {
        if (w1TypingTimer != null && w1TypingTimer.isRunning()) {
            w1TypingTimer.stop();
            w1DialogueBox.setText(WORLD1_DIALOGUES[w1DialogueIndex]);
            return;
        }
        w1DialogueIndex++;
        if (w1DialogueIndex < WORLD1_DIALOGUES.length) {
            startW1Typing();
        } else {
            w1DialogueIndex = 0;
            goToBattle();
        }
    }
}