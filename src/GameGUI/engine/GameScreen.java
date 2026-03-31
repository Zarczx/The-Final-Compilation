package GameGUI.engine;

import GameGUI.model.HeroData;
import GameGUI.ui.BattlePanel;
import GameGUI.ui.HeroSelectionPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class GameScreen extends JPanel {

    private static final String SCREEN_INTRO ="intro";
    private static final String SCREEN_SELECTION ="selection";
    private static final String SCREEN_POST_SELECT ="postSelect";
    private static final String SCREEN_WORLD1_INTRO ="world1Intro";
    private static final String SCREEN_BATTLE ="battle";

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cardPanel = new JPanel(cardLayout);

    private HeroSelectionPanel heroSelectionPanel;
    private BattlePanel battlePanel;
    private JPanel postSelectPanel;
    private JPanel world1IntroPanel;

    private JTextArea postDialogueBox;
    private JButton postContinueBtn;
    private int postDialogueIndex = 0;

    private JTextArea w1DialogueBox;
    private JButton w1ContinueBtn;
    private int w1DialogueIndex = 0;
    private Timer w1TypingTimer;

    JTextArea dialogueBox;
    JButton continueBtn, menuBtn, backBtn, exitBtn;

    JPanel loginPopup, examPopup;
    JTextField usernameField;
    JPasswordField passwordField;
    JButton loginStartBtn, loginExitBtn;
    JButton examStartBtn, examExitBtn;

    JPanel battleChoicePopup;
    JButton fightBtn, runBtn;

    Timer typingTimer;
    int dialogueIndex = 0;
    int charIndex = 0;

    private JLabel sceneBgLabel;
    private JLabel postBgLabel;
    private JLabel postWeaponLabel;
    private JLabel postArmorLabel;
    private JLabel w1WorldLabel;
    private Timer world1FadeTimer;
    private int[] world1FadeAlpha;
    private JPanel world1FadeOverlay;
    private JPanel world1SceneBg;
    private float[] world1SceneAlpha;
    private float[] world1WorldAlpha;
    private JLabel world1KhaiLabel;
    private float[] world1KhaiAlpha = {0f};
    private HeroData.HeroDefinition confirmedHero = null;
    private boolean pendingSelection = false;

    // ★ Tracks which inter-enemy dialogue group is currently active
    private int w1CurrentInterIndex = -1;

    // ★ Background to show BEFORE each inter-dialogue chunk [interIndex][chunkIndex]
    //   null = keep current background, String = switch to that image path
    private static final String[][] WORLD1_INTER_BG = {
            // Index 0: After Rotfang Wolves → before Shade Sprites
            {
                    null,                                                                   // chunk 0: "The path narrows..." — keep current bg
                    "/assets/Backgrounds/World1BattleBackgroundFog.jpeg",                   // chunk 1: "The air grows icy..."
                    null,                                                                   // chunk 2: "Shadows detach..."
                    "/assets/Backgrounds/World1BattleBackgroundShadeSprites1.jpeg"          // chunk 3: "SHADE SPRITES."
            },
            // Index 1: After Shade Sprites → before Dreadbark Treants
            { null, null },
            // Index 2: After Dreadbark Treants → before Carrion Bats
            { null, null },
            // Index 3: After Carrion Bats → before Hollow Stag
            { null, null }
    };

    String[] dialogues = {
            "It's just another Tuesday, you come in for your Java examination.",

            "You walk in and Professor Khai greets you warmly as you sit before\n"+
                    "the CodeChum login screen.",

            "You place your hands on the keyboard and login... L15Y07W....",

            "Enter your Username, password, and click Sign in.",
    };

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

    private void goToIntro() { cardLayout.show(cardPanel, SCREEN_INTRO); }

    public void skipToWorld1(HeroData.HeroDefinition hero) {
        this.confirmedHero = hero;
        w1DialogueIndex = 0;
        if (typingTimer != null) typingTimer.stop();
        if (w1WorldLabel != null) {
            w1WorldLabel.setVisible(true);
            w1WorldLabel.setForeground(Color.WHITE);
        }
        if (world1KhaiLabel != null) {
            world1KhaiLabel.setIcon(null);
            world1KhaiAlpha[0] = 0f;
            world1KhaiLabel.putClientProperty("prevImage", null);
            world1KhaiLabel.putClientProperty("prevAlpha", 0f);
        }
        if (world1SceneAlpha != null) world1SceneAlpha[0] = 1.0f;
        if (world1WorldAlpha != null) world1WorldAlpha[0] = 0.0f;
        goToWorld1Intro();
    }

    private void goToSelection() { cardLayout.show(cardPanel, SCREEN_SELECTION); }
    private void goToPostSelect() { cardLayout.show(cardPanel, SCREEN_POST_SELECT); }
    private void goToWorld1Intro() {
        cardLayout.show(cardPanel, SCREEN_WORLD1_INTRO);

        if (world1SceneBg != null) {
            world1SceneAlpha[0] = 1.0f;
            world1WorldAlpha[0] = 0.0f;
            world1SceneBg.repaint();
        }

        delay(1500, () -> {
            Timer crossfade = new Timer(16, null);
            crossfade.addActionListener(ev -> {
                if (world1SceneBg == null) { crossfade.stop(); return; }
                world1SceneAlpha[0] = Math.max(0f, world1SceneAlpha[0] - 0.02f);
                world1WorldAlpha[0] = Math.min(1f, world1WorldAlpha[0] + 0.02f);
                world1SceneBg.repaint();
                if (world1SceneAlpha[0] <= 0f && world1WorldAlpha[0] >= 1f) {
                    crossfade.stop();
                    startW1Typing();
                }
            });
            crossfade.start();
        });
    }

    private void goToBattle() {
        if (confirmedHero == null) return;
        cardLayout.show(cardPanel, SCREEN_BATTLE);
        battlePanel.setOnEnemyGroupDefeated((nextGroupIndex, resumeFight) -> {
            showInterEnemyDialogue(nextGroupIndex - 1, resumeFight);
        });
        battlePanel.startEnemySequence(
                confirmedHero,
                HeroData.WORLD1_ENEMIES,
                () -> goToSelection()
        );
    }

    // ════════════════════════════════════════════════════════════════════════
    // ★ INTER-ENEMY DIALOGUE — shown between enemy groups
    //   Now tracks w1CurrentInterIndex and switches backgrounds per chunk
    // ════════════════════════════════════════════════════════════════════════
    private void showInterEnemyDialogue(int interIndex, Runnable resumeFight) {
        if (interIndex < 0 || interIndex >= WORLD1_INTER_DIALOGUES.length) {
            resumeFight.run(); return;
        }
        w1ResumeAfterDialogue = resumeFight;
        w1InInterDialogue = true;
        w1CurrentInterIndex = interIndex; // ★ track which group we're in
        w1InterChunks = WORLD1_INTER_DIALOGUES[interIndex];
        w1InterChunkIndex = 0;

        if (world1KhaiLabel != null) {
            world1KhaiLabel.setIcon(null);
            world1KhaiAlpha[0] = 0f;
            world1KhaiLabel.putClientProperty("prevImage", null);
            world1KhaiLabel.putClientProperty("prevAlpha", 0f);
            world1KhaiLabel.repaint();
        }
        if (world1SceneAlpha != null) world1SceneAlpha[0] = 0f;
        if (world1WorldAlpha != null) world1WorldAlpha[0] = 1.0f;
        if (world1SceneBg != null) world1SceneBg.repaint();
        if (w1WorldLabel != null) w1WorldLabel.setVisible(false);

        cardLayout.show(cardPanel, SCREEN_WORLD1_INTRO);
        w1ContinueBtn.setEnabled(true);
        typeInterChunk();
    }

    // ════════════════════════════════════════════════════════════════════════
    // ★ TYPE INTER CHUNK — switches background before typing each chunk
    // ════════════════════════════════════════════════════════════════════════
    private void typeInterChunk() {
        if (w1InterChunks == null || w1InterChunkIndex >= w1InterChunks.length) {
            finishInterDialogue(); return;
        }
        if (w1TypingTimer != null && w1TypingTimer.isRunning()) w1TypingTimer.stop();

        // ★ Switch background if one is defined for this chunk
        if (w1CurrentInterIndex >= 0
                && w1CurrentInterIndex < WORLD1_INTER_BG.length
                && w1InterChunkIndex < WORLD1_INTER_BG[w1CurrentInterIndex].length) {
            String bg = WORLD1_INTER_BG[w1CurrentInterIndex][w1InterChunkIndex];
            if (bg != null) setW1InterBackground(bg);
        }

        String text = w1InterChunks[w1InterChunkIndex];
        w1DialogueBox.setText("");
        w1ContinueBtn.setEnabled(false);
        int[] ci = {0};
        w1TypingTimer = new Timer(25, null);
        w1TypingTimer.addActionListener(e -> {
            if (ci[0] < text.length()) {
                w1DialogueBox.append(String.valueOf(text.charAt(ci[0]++)));
            } else {
                w1TypingTimer.stop();
                w1ContinueBtn.setEnabled(true);
            }
        });
        w1TypingTimer.start();
    }

    // ════════════════════════════════════════════════════════════════════════
    // ★ SET INTER BACKGROUND — switches World1 scene to a new image instantly
    // ════════════════════════════════════════════════════════════════════════
    private void setW1InterBackground(String path) {
        if (world1SceneBg == null || world1WorldAlpha == null) return;
        try {
            java.net.URL url = getClass().getResource(path);
            if (url == null) {
                System.out.println("[W1 Inter] Missing bg: " + path); return;
            }
            java.awt.image.BufferedImage newImg = javax.imageio.ImageIO.read(url);
            // Store as interBg client property so paintComponent can draw it
            world1SceneBg.putClientProperty("interBg", newImg);
            // Make sure scene alpha is 0 and world alpha is 1 so interBg layer shows
            world1SceneAlpha[0] = 0f;
            world1WorldAlpha[0] = 1f;
            world1SceneBg.repaint();
        } catch (Exception ex) {
            System.out.println("[W1 Inter] bg error: " + ex.getMessage());
        }
    }

    private void finishInterDialogue() {
        w1InInterDialogue = false;
        w1CurrentInterIndex = -1; // ★ reset
        w1DialogueBox.setText("");
        Runnable resume = w1ResumeAfterDialogue;
        w1ResumeAfterDialogue = null;
        cardLayout.show(cardPanel, SCREEN_BATTLE);
        if (resume != null) resume.run();
    }

    private void restartBattle() { goToBattle(); }

    private HeroData.EnemyDefinition pickEnemy() {
        var enemies = HeroData.WORLD1_ENEMIES;
        return enemies.get(new Random().nextInt(enemies.size()));
    }

    private void onHeroConfirmed(HeroData.HeroDefinition hero) {
        this.confirmedHero = hero;
        buildPostSelectDialogues(hero);

        if (postBgLabel != null) {
            String imgPath = switch (hero.name) {
                case"Kael Saint Laurent"->"/assets/KaelAssets/KaelIntro.png";
                case"Karl Clover Dior IV"->"/assets/KarlAssets/KarlIntro.png";
                case"Simon Versace"->"/assets/SimonAssets/SimonIntro.png";
                default -> null;
            };
            if (imgPath != null) {
                java.net.URL url = getClass().getResource(imgPath);
                if (url != null) {
                    ImageIcon raw = new ImageIcon(url);
                    Image scaled = raw.getImage().getScaledInstance(1280, 520, Image.SCALE_SMOOTH);
                    postBgLabel.setIcon(new ImageIcon(scaled));
                    postBgLabel.setText("");
                } else {
                    postBgLabel.setIcon(null);
                    System.out.println("[GameScreen] Missing post-select bg:"+ imgPath);
                }
            } else {
                postBgLabel.setIcon(null);
            }
        }

        postDialogueIndex = 0;
        if (postWeaponLabel != null) { postWeaponLabel.setVisible(false); postWeaponLabel.setIcon(null); }
        if (postArmorLabel != null) { postArmorLabel.setVisible(false); postArmorLabel.setIcon(null); }
        goToPostSelect();
        startPostTyping();
    }

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

    private void setSceneBackground(String resourcePath) {
        if (sceneBgLabel == null) return;
        String absPath = resourcePath.startsWith("/") ? resourcePath :"/"+ resourcePath;
        java.net.URL url = getClass().getResource(absPath);
        if (url != null) {
            ImageIcon raw = new ImageIcon(url);
            Image scaled = raw.getImage().getScaledInstance(1280, 520, Image.SCALE_SMOOTH);
            sceneBgLabel.setIcon(new ImageIcon(scaled));
            sceneBgLabel.setText("");
        } else {
            sceneBgLabel.setIcon(null);
            sceneBgLabel.setText("<html><center><font color='#ff6666'> Missing:<br>"
                    + resourcePath + "</font></center></html>");
            System.out.println("[GameScreen] Asset not found:"+ absPath);
        }
        sceneBgLabel.repaint();
    }

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

        JLabel theBackground = new JLabel();
        theBackground.setBounds(0, 0, 1280, 720);
        theBackground.setOpaque(true);
        theBackground.setBackground(new Color(40, 40, 60));
        java.net.URL theBgUrl = getClass().getResource("/assets/Backgrounds/TheBackground.png");
        if (theBgUrl != null) {
            Image theBgScaled = new ImageIcon(theBgUrl).getImage().getScaledInstance(1280, 720, Image.SCALE_SMOOTH);
            theBackground.setIcon(new ImageIcon(theBgScaled));
        }

        sceneBgLabel = new JLabel();
        sceneBgLabel.setBounds(0, 0, 1280, 520);
        sceneBgLabel.setHorizontalAlignment(SwingConstants.CENTER);
        sceneBgLabel.setVerticalAlignment(SwingConstants.CENTER);
        sceneBgLabel.setBackground(new Color(30, 28, 50));
        sceneBgLabel.setOpaque(false);

        JLabel dialogueBgLabel = new JLabel();
        dialogueBgLabel.setBounds(-40, 453, 1053, 343);
        java.net.URL dialogueBgUrl = getClass().getResource("/assets/GUIButtons/DialogueBox.png");
        if (dialogueBgUrl != null) {
            ImageIcon rawDb = new ImageIcon(dialogueBgUrl);
            Image scaledDb = rawDb.getImage().getScaledInstance(1053, 343, Image.SCALE_SMOOTH);
            dialogueBgLabel.setIcon(new ImageIcon(scaledDb));
        }

        dialogueBox = new JTextArea();
        dialogueBox.setBounds(104, 541, 900, 100);
        dialogueBox.setEditable(false);
        dialogueBox.setLineWrap(true);
        dialogueBox.setWrapStyleWord(true);
        try {
            java.io.InputStream fontStream = getClass().getResourceAsStream("/assets/AssetFont/Pixelari.ttf");
            if (fontStream != null) {
                Font pixelFont = Font.createFont(Font.TRUETYPE_FONT, fontStream).deriveFont(Font.BOLD, 16f);
                dialogueBox.setFont(pixelFont);
            } else {
                System.out.println("[GameScreen] Missing font: /assets/AssetFont/Pixelari.ttf");
                dialogueBox.setFont(new Font("Dialog", Font.BOLD, 16));
            }
        } catch (Exception ex) {
            System.out.println("[GameScreen] Font load error:"+ ex.getMessage());
            dialogueBox.setFont(new Font("Dialog", Font.BOLD, 16));
        }
        dialogueBox.setOpaque(false);
        dialogueBox.setBackground(new Color(0, 0, 0, 0));
        dialogueBox.setForeground(Color.BLACK);
        dialogueBox.setBorder(BorderFactory.createEmptyBorder(30, 70, 30, 20));

        continueBtn = createImageButton(
                "/assets/GUIButtons/Continue.png","/assets/GUIButtons/ContinueHover.png",
                964, 554, 154, 64,"Continue");
        menuBtn = createImageButton(
                "/assets/GUIButtons/Menu.png","/assets/GUIButtons/MenuHover.png",
                1103, 555, 148, 58,"Menu", 0);
        backBtn = createImageButton(
                "/assets/GUIButtons/Back.png","/assets/GUIButtons/BackHover.png",
                970, 613, 140, 50,"Back", 20);
        exitBtn = createImageButton(
                "/assets/GUIButtons/Exit.png","/assets/GUIButtons/ExitHover.png",
                1108, 613, 140, 50,"Exit", 19);

        exitBtn.addActionListener(e -> System.exit(0));
        continueBtn.addActionListener(e -> continueDialogue());

        loginPopup = buildLoginPopup();
        examPopup = buildExamPopup();
        battleChoicePopup = buildBattleChoicePopup();

        layeredPane.setBounds(0, 0, 1280, 720);
        layeredPane.add(theBackground,     JLayeredPane.FRAME_CONTENT_LAYER);
        layeredPane.add(sceneBgLabel,      JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(dialogueBgLabel,   JLayeredPane.PALETTE_LAYER);
        layeredPane.add(dialogueBox,       JLayeredPane.MODAL_LAYER);
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
            ImageIcon raw = new ImageIcon(signInUrl);
            Image scaled = raw.getImage().getScaledInstance(235, 23, Image.SCALE_SMOOTH);
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

    private JPanel buildExamPopup() {
        JPanel popup = new JPanel(null);
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
            ImageIcon raw = new ImageIcon(startUrl);
            Image scaled = raw.getImage().getScaledInstance(182, 59, Image.SCALE_SMOOTH);
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
            continueBtn.setEnabled(false);
            runLightsSequence();
        });

        return popup;
    }

    private void runLightsSequence() {
        setSceneBackground("assets/Backgrounds/NGEBeforeLights.png");
        delay(1000, () -> {
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
                delay(1000, () -> {
                    startTyping("Screen flickers...", () -> {
                        delay(1000, () -> {
                            setSceneBackground("assets/Backgrounds/NGEBeforeLights2.png");
                            startTyping("Everything goes silent.", () -> {
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
                                        startTyping(
                                                "The screen glitches... \n"+
                                                        "And the world turns to black as the room seems to wrap around you.",
                                                () -> {
                                                    setSceneBackground("assets/Backgrounds/NGEBeforeLights6.png");
                                                    startTyping(
                                                            "When you come to your senses, you're no longer in the lab.\n"+
                                                                    "You wake up in an unfamiliar place.",
                                                            () -> {
                                                                pendingSelection = true;
                                                                continueBtn.setEnabled(true);
                                                            }
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

    private void delay(int ms, Runnable action) {
        Timer t = new Timer(ms, null);
        t.setRepeats(false);
        t.addActionListener(e -> { t.stop(); action.run(); });
        t.start();
    }

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

    private void continueDialogue() {
        if (pendingSelection) {
            pendingSelection = false;
            continueBtn.setEnabled(false);
            goToSelection();
            return;
        }

        if (typingTimer != null && typingTimer.isRunning()) {
            typingTimer.stop();
            dialogueBox.setText(dialogues[Math.min(dialogueIndex, dialogues.length - 1)]);
            return;
        }

        dialogueIndex++;

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

        if (dialogueIndex == 3) {
            continueBtn.setEnabled(false);
            setSceneBackground("assets/Backgrounds/NGELogin2.png");
            loginPopup.setVisible(true);
            startTyping(dialogues[3], null);
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

    private JButton createImageButton(String normalPath, String hoverPath,
                                      int x, int y, int w, int h, String fallbackText) {
        return createImageButton(normalPath, hoverPath, x, y, w, h, fallbackText, 10);
    }

    private JButton createImageButton(String normalPath, String hoverPath,
                                      int x, int y, int w, int h, String fallbackText, int hoverOffset) {
        JButton btn = new JButton();
        btn.setBounds(x, y, w, h);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        java.net.URL normalUrl = getClass().getResource(normalPath);
        java.net.URL hoverUrl = getClass().getResource(hoverPath);

        if (normalUrl != null) {
            ImageIcon normalIcon = new ImageIcon(
                    new ImageIcon(normalUrl).getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH));
            ImageIcon hoverIcon = hoverUrl != null
                    ? new ImageIcon(new ImageIcon(hoverUrl).getImage().getScaledInstance(w + hoverOffset, h + hoverOffset, Image.SCALE_SMOOTH))
                    : normalIcon;
            btn.setIcon(normalIcon);
            btn.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override public void mouseEntered(java.awt.event.MouseEvent e) { btn.setIcon(hoverIcon); }
                @Override public void mouseExited (java.awt.event.MouseEvent e) { btn.setIcon(normalIcon); }
            });
        } else {
            btn.setText(fallbackText);
            btn.setForeground(Color.WHITE);
            btn.setContentAreaFilled(true);
            System.out.println("[GameScreen] Missing button asset:"+ normalPath);
        }
        return btn;
    }

    private String[] postDialogues;

    private void buildPostSelectDialogues(HeroData.HeroDefinition hero) {
        String heroLine;
        String weaponLine;
        String armorLine;

        switch (hero.name) {
            case"Kael Saint Laurent"-> {
                heroLine ="You have chosen KAEL SAINT LAURENT, the valiant Swordsman!\n\n"+ hero.backstory;
                weaponLine ="The gods bestow upon you your starting gear...\n\n"+
                        "The Old Broadsword rests firmly in your grasp, its blade marked\n"+
                        "by the scars of past battles.";
                armorLine ="The Leather Guard settles on your shoulders worn, but reliable.\n\n"+
                        "You are ready. The forest of World 1 awaits.";
            }
            case"Karl Clover Dior IV"-> {
                heroLine ="You have chosen KARL CLOVER DIOR IV, the swift Archer!\n\n"+ hero.backstory;
                weaponLine ="The gods bestow upon you your starting gear...\n\n"+
                        "The Shortbow hums softly each arrow you notch feels like an\n"+
                        "extension of your will.";
                armorLine ="The Leather Guard settles on your shoulders worn, but reliable.\n\n"+
                        "You are ready. The forest of World 1 awaits.";
            }
            case"Simon Versace"-> {
                heroLine ="You have chosen SIMON VERSACE, the arcane Mage!\n\n"+ hero.backstory;
                weaponLine ="The gods bestow upon you your starting gear...\n\n"+
                        "The Wooden Staff pulses faintly, whispering secrets\n"+
                        "of forgotten spells.";
                armorLine ="The Leather Guard shimmers with faint runes fragile,\n"+
                        "yet brimming with arcane energy.\n\n"+
                        "You are ready. The forest of World 1 awaits.";
            }
            default -> {
                heroLine ="You have chosen:"+ hero.name + ".\n\n"+ hero.backstory;
                weaponLine ="The gods bestow upon you your starting gear...";
                armorLine ="You are ready. The forest of World 1 awaits.";
            }
        }

        postDialogues = new String[]{ heroLine, weaponLine, armorLine };
    }

    private JPanel buildPostSelectScreen() {
        JLayeredPane layeredPane = new JLayeredPane();

        JPanel wrapper = new JPanel(null) {
            @Override public void doLayout() {
                super.doLayout();
                layeredPane.setBounds(0, 0, getWidth(), getHeight());
            }
        };
        wrapper.setBackground(new Color(40, 40, 60));
        wrapper.setLayout(null);
        wrapper.setPreferredSize(new Dimension(1280, 720));

        JLabel theBackground = new JLabel();
        theBackground.setBounds(0, 0, 1280, 720);
        theBackground.setOpaque(true);
        theBackground.setBackground(new Color(40, 40, 60));
        java.net.URL theBgUrl2 = getClass().getResource("/assets/Backgrounds/TheBackground.png");
        if (theBgUrl2 != null) {
            Image theBgScaled2 = new ImageIcon(theBgUrl2).getImage().getScaledInstance(1280, 720, Image.SCALE_SMOOTH);
            theBackground.setIcon(new ImageIcon(theBgScaled2));
        }

        postBgLabel = new JLabel();
        postBgLabel.setBounds(0, 0, 1280, 520);
        postBgLabel.setOpaque(false);
        postBgLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        postBgLabel.setVerticalAlignment(javax.swing.SwingConstants.CENTER);

        postWeaponLabel = new JLabel();
        postWeaponLabel.setBounds(30, 60, 300, 420);
        postWeaponLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        postWeaponLabel.setVerticalAlignment(javax.swing.SwingConstants.CENTER);
        postWeaponLabel.setVisible(false);

        postArmorLabel = new JLabel();
        postArmorLabel.setBounds(950, 60, 300, 420);
        postArmorLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        postArmorLabel.setVerticalAlignment(javax.swing.SwingConstants.CENTER);
        postArmorLabel.setVisible(false);

        JLabel postDialogueBgLabel = new JLabel();
        postDialogueBgLabel.setBounds(-40, 453, 1053, 343);
        java.net.URL postDbUrl = getClass().getResource("/assets/GUIButtons/DialogueBox.png");
        if (postDbUrl != null) {
            Image postDbScaled = new ImageIcon(postDbUrl).getImage().getScaledInstance(1053, 343, Image.SCALE_SMOOTH);
            postDialogueBgLabel.setIcon(new ImageIcon(postDbScaled));
        }

        postDialogueBox = new JTextArea();
        postDialogueBox.setBounds(104, 541, 900, 100);
        postDialogueBox.setEditable(false);
        postDialogueBox.setLineWrap(true);
        postDialogueBox.setWrapStyleWord(true);
        try {
            java.io.InputStream fs = getClass().getResourceAsStream("/assets/AssetFont/Pixelari.ttf");
            if (fs != null) {
                Font pf = Font.createFont(Font.TRUETYPE_FONT, fs).deriveFont(Font.BOLD, 16f);
                postDialogueBox.setFont(pf);
            } else {
                postDialogueBox.setFont(new Font("Dialog", Font.BOLD, 16));
            }
        } catch (Exception ex) {
            postDialogueBox.setFont(new Font("Dialog", Font.BOLD, 16));
        }
        postDialogueBox.setOpaque(false);
        postDialogueBox.setBackground(new Color(0, 0, 0, 0));
        postDialogueBox.setForeground(Color.BLACK);
        postDialogueBox.setBorder(BorderFactory.createEmptyBorder(30, 70, 30, 20));

        postContinueBtn = createImageButton(
                "/assets/GUIButtons/Continue.png", "/assets/GUIButtons/ContinueHover.png",
                964, 554, 154, 64, "Continue");
        postContinueBtn.addActionListener(e -> continuePostDialogue());

        JButton postMenuBtn = createImageButton(
                "/assets/GUIButtons/Menu.png", "/assets/GUIButtons/MenuHover.png",
                1103, 555, 148, 58, "Menu", 0);
        JButton postBackBtn = createImageButton(
                "/assets/GUIButtons/Back.png", "/assets/GUIButtons/BackHover.png",
                970, 613, 140, 50, "Back", 20);
        JButton postExitBtn = createImageButton(
                "/assets/GUIButtons/Exit.png", "/assets/GUIButtons/ExitHover.png",
                1108, 613, 140, 50, "Exit", 19);
        postExitBtn.addActionListener(e -> System.exit(0));

        layeredPane.setBounds(0, 0, 1280, 720);
        layeredPane.add(theBackground,       JLayeredPane.FRAME_CONTENT_LAYER);
        layeredPane.add(postBgLabel,         JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(postWeaponLabel,     JLayeredPane.PALETTE_LAYER);
        layeredPane.add(postArmorLabel,      JLayeredPane.PALETTE_LAYER);
        layeredPane.add(postDialogueBgLabel, JLayeredPane.PALETTE_LAYER);
        layeredPane.add(postDialogueBox,     JLayeredPane.MODAL_LAYER);
        layeredPane.add(postContinueBtn,     JLayeredPane.PALETTE_LAYER);
        layeredPane.add(postMenuBtn,         JLayeredPane.PALETTE_LAYER);
        layeredPane.add(postBackBtn,         JLayeredPane.PALETTE_LAYER);
        layeredPane.add(postExitBtn,         JLayeredPane.PALETTE_LAYER);

        wrapper.add(layeredPane);
        return wrapper;
    }

    private Timer postTypingTimer;
    private String[] postChunks;
    private int postChunkIndex = 0;

    private String[] splitIntoChunks(String text) {
        String[] lines = text.split("\n");
        java.util.List<String> chunks = new java.util.ArrayList<>();
        StringBuilder chunk = new StringBuilder();
        int count = 0;
        for (String line : lines) {
            if (chunk.length() > 0) chunk.append("\n");
            chunk.append(line);
            count++;
            if (count >= 2) {
                chunks.add(chunk.toString());
                chunk = new StringBuilder();
                count = 0;
            }
        }
        if (chunk.length() > 0) chunks.add(chunk.toString());
        return chunks.toArray(new String[0]);
    }

    private void startPostTyping() {
        if (postDialogues == null || postDialogueIndex >= postDialogues.length) return;
        if (postTypingTimer != null && postTypingTimer.isRunning()) postTypingTimer.stop();

        postChunks = splitIntoChunks(postDialogues[postDialogueIndex]);
        postChunkIndex = 0;
        typePostChunk();
    }

    private void typePostChunk() {
        if (postChunks == null || postChunkIndex >= postChunks.length) return;
        if (postTypingTimer != null && postTypingTimer.isRunning()) postTypingTimer.stop();

        String text = postChunks[postChunkIndex];
        postDialogueBox.setText("");
        int[] ci = {0};

        postTypingTimer = new Timer(25, null);
        postTypingTimer.addActionListener(e -> {
            if (ci[0] < text.length()) {
                postDialogueBox.append(String.valueOf(text.charAt(ci[0]++)));
            } else {
                postTypingTimer.stop();
                if (postChunkIndex < postChunks.length - 1) {
                    postContinueBtn.setEnabled(false);
                    delay(1000, () -> {
                        postChunkIndex++;
                        postContinueBtn.setEnabled(true);
                        typePostChunk();
                    });
                }
            }
        });
        postTypingTimer.start();
    }

    private void continuePostDialogue() {
        if (postTypingTimer != null && postTypingTimer.isRunning()) {
            postTypingTimer.stop();
            if (postChunks != null && postChunkIndex < postChunks.length) {
                postDialogueBox.setText(postChunks[postChunkIndex]);
            }
            return;
        }
        postDialogueIndex++;
        if (postDialogueIndex < postDialogues.length) {
            if (postDialogueIndex == 1) showPostWeapon();
            if (postDialogueIndex == 2) showPostArmor();
            startPostTyping();
        } else {
            postWeaponLabel.setVisible(false);
            postArmorLabel.setVisible(false);
            postWeaponLabel.setIcon(null);
            postArmorLabel.setIcon(null);
            postDialogueIndex = 0;
            w1DialogueIndex = 0;
            goToWorld1Intro();
        }
    }

    private void showPostWeapon() {
        if (confirmedHero == null || postWeaponLabel == null) return;
        String path = switch (confirmedHero.name) {
            case"Kael Saint Laurent"->"/assets/SwordAssets/OldBroadSword.png";
            case"Karl Clover Dior IV"->"/assets/BowAssets/WoodenBow.png";
            case"Simon Versace"->"/assets/StaffAssets/WoodenStaff.png";
            default -> null;
        };
        loadPostItemImage(postWeaponLabel, path);
    }

    private void showPostArmor() {
        if (postArmorLabel == null) return;
        loadPostItemImage(postArmorLabel,"/assets/ArmorAssets/LeatherGuard.png");
    }

    private void loadPostItemImage(JLabel label, String path) {
        if (path == null) { label.setVisible(false); return; }
        java.net.URL url = getClass().getResource(path);
        if (url != null) {
            ImageIcon raw = new ImageIcon(url);
            int origW = raw.getIconWidth();
            int origH = raw.getIconHeight();
            int targetH = 380;
            int targetW = (origH > 0) ? (origW * targetH / origH) : 200;
            Image scaled = raw.getImage().getScaledInstance(targetW, targetH, Image.SCALE_SMOOTH);
            label.setIcon(new ImageIcon(scaled));
            label.setVisible(true);
            Container parent = label.getParent();
            if (parent != null) {
                parent.setComponentZOrder(label, 0);
                parent.repaint();
            }
        } else {
            System.out.println("[GameScreen] Missing post-select item:"+ path);
            label.setVisible(false);
        }
    }

    // Dialogues shown BETWEEN enemy groups
    private static final String[][] WORLD1_INTER_DIALOGUES = {
            // After Rodtfang Wolves (index 0) → before Shade Sprites
            {
                    "The path narrows. The mist becomes so thick\nyou can barely see your hand in front of your face.",
                    "The air grows icy. The silence is broken by a sound like static,\nor perhaps whispering voices overlapping until they become noise.",
                    "Shadows detach themselves from the trees.\nThey twist and contort, forming vague human-like shapes.",
                    "SHADE SPRITES.\nThey are the lost souls of travelers who died in this woods, now jealous of your life."
            },
            // After Shade Sprites (index 1) → before Dreadbark Treants
            {
                    "The whispering finally stops.\nThe mist recedes, revealing faint lights hovering among the dead trees.",
                    "The ground shudders beneath your feet.\nAncient roots crack through the soil."
            },
            // After Dreadbark Treants (index 2) → before Carrion Bats
            {
                    "The Treants collapse in a shower of rotting bark.\nWhere they fall, small green sprouts push through the ash.",
                    "A foul stench drifts down from above.\nSomething vast circles in the dead canopy overhead."
            },
            // After Carrion Bats (index 3) → before Hollow Stag
            {
                    "The last bat crashes into the earth.\nThe forest holds its breath.",
                    "Ahead, pale moonlight breaks through the canopy.\nA clearing opens — and within it, something stirs."
            }
    };

    private static final String[] WORLD1_DIALOGUES = {
            "You wake up gasping for air. The world is drained of color.",
            "You are lying on a bed of gray moss in a dead forest. The trees are skeletal\n"+
                    "giants, stripped to bone-white wood. A cold mist coils around your ankles,\n"+
                    "and silence presses from every side watching, waiting.",
            "A heavy bell tolls in the distance...\n\"Dong... Dong...\"",
            "From the mist steps a figure cloaked in tattered robes.\n"+
                    "He leans heavily on a staff. As he lifts his hood, you jolt back \n"+
                    "the face is familiar. It looks exactly like your professor, Khai.\n"+
                    "But his eyes are weary, holding the weight of centuries.",
            "\"Be calm, Traveler. In this realm, I am known as Khai the Gray.\"\n\n"+
                    "\"We suffer because an evil Necromancer has corrupted these lands.\n"+
                    "He has drained the nature itself. We must find the Three Stones of Life\n"+
                    "that hold this reality together. Only then will your path home reveal itself.\"",
            "Khai fades back into the mist.",
            "Three Rodtfang Wolves emerge from the tree line.",
            "Their glowing red eyes",
            "fixate on you. They do not hunt for food they hunt to kill."
    };

    private JPanel buildWorld1IntroScreen() {
        JLayeredPane layeredPane = new JLayeredPane();

        JPanel wrapper = new JPanel(null) {
            @Override public void doLayout() {
                super.doLayout();
                layeredPane.setBounds(0, 0, getWidth(), getHeight());
            }
        };
        wrapper.setBackground(new Color(40, 40, 60));
        wrapper.setLayout(null);
        wrapper.setPreferredSize(new Dimension(1280, 720));

        JLabel w1TheBg = new JLabel();
        w1TheBg.setBounds(0, 0, 1280, 720);
        w1TheBg.setOpaque(true);
        w1TheBg.setBackground(new Color(40, 40, 60));
        java.net.URL w1BgUrl = getClass().getResource("/assets/Backgrounds/TheBackground.png");
        if (w1BgUrl != null) {
            Image w1BgScaled = new ImageIcon(w1BgUrl).getImage().getScaledInstance(1280, 720, Image.SCALE_SMOOTH);
            w1TheBg.setIcon(new ImageIcon(w1BgScaled));
        }

        final float[] sceneAlpha = {1.0f};
        final float[] worldAlpha = {0.0f};

        java.awt.image.BufferedImage lights6Img = null;
        java.awt.image.BufferedImage world1Img  = null;
        try {
            java.net.URL l6url = getClass().getResource("/assets/Backgrounds/NGEBeforeLights6.png");
            java.net.URL w1url = getClass().getResource("/assets/Backgrounds/World1Background.png");
            if (l6url != null) lights6Img = javax.imageio.ImageIO.read(l6url);
            if (w1url  != null) world1Img  = javax.imageio.ImageIO.read(w1url);
        } catch (Exception ex) { System.out.println("[W1] Image load error: " + ex.getMessage()); }

        final java.awt.image.BufferedImage fLights6 = lights6Img;
        final java.awt.image.BufferedImage fWorld1  = world1Img;

        // ★ Scene background panel — also handles interBg switching via client property
        JPanel w1SceneBg = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                if (fLights6 != null && sceneAlpha[0] > 0) {
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, sceneAlpha[0]));
                    g2.drawImage(fLights6, 0, 0, getWidth(), getHeight(), null);
                }
                if (fWorld1 != null && worldAlpha[0] > 0) {
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, worldAlpha[0]));
                    g2.drawImage(fWorld1, 0, 0, getWidth(), getHeight(), null);
                }
                // ★ Draw inter-enemy background on top if set and World1Bg is showing
                Object interBg = getClientProperty("interBg");
                if (interBg instanceof java.awt.image.BufferedImage
                        && worldAlpha[0] >= 1f && sceneAlpha[0] <= 0f) {
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                    g2.drawImage((java.awt.image.BufferedImage) interBg, 0, 0, getWidth(), getHeight(), null);
                }
                g2.dispose();
            }
        };
        w1SceneBg.setBounds(0, 0, 1280, 520);
        w1SceneBg.setOpaque(false);

        world1SceneBg    = w1SceneBg;
        world1SceneAlpha = sceneAlpha;
        world1WorldAlpha = worldAlpha;

        w1WorldLabel = new JLabel("WORLD 1 : THE FOREST OF SILENCE", SwingConstants.CENTER);
        w1WorldLabel.setBounds(0, 220, 1280, 50);
        w1WorldLabel.setForeground(Color.WHITE);
        w1WorldLabel.setFont(new Font("Serif", Font.BOLD | Font.ITALIC, 26));

        JLabel w1DialogueBgLabel = new JLabel();
        w1DialogueBgLabel.setBounds(-40, 453, 1053, 343);
        java.net.URL w1DbUrl = getClass().getResource("/assets/GUIButtons/DialogueBox.png");
        if (w1DbUrl != null) {
            Image w1DbScaled = new ImageIcon(w1DbUrl).getImage().getScaledInstance(1053, 343, Image.SCALE_SMOOTH);
            w1DialogueBgLabel.setIcon(new ImageIcon(w1DbScaled));
        }

        w1DialogueBox = new JTextArea();
        w1DialogueBox.setBounds(104, 541, 900, 100);
        w1DialogueBox.setEditable(false);
        w1DialogueBox.setLineWrap(true);
        w1DialogueBox.setWrapStyleWord(true);
        try {
            java.io.InputStream w1fs = getClass().getResourceAsStream("/assets/AssetFont/Pixelari.ttf");
            if (w1fs != null) {
                Font w1pf = Font.createFont(Font.TRUETYPE_FONT, w1fs).deriveFont(Font.BOLD, 19f);
                w1DialogueBox.setFont(w1pf);
            } else {
                w1DialogueBox.setFont(new Font("Dialog", Font.BOLD, 16));
            }
        } catch (Exception ex) {
            w1DialogueBox.setFont(new Font("Dialog", Font.BOLD, 16));
        }
        w1DialogueBox.setOpaque(false);
        w1DialogueBox.setBackground(new Color(0, 0, 0, 0));
        w1DialogueBox.setForeground(Color.BLACK);
        w1DialogueBox.setBorder(BorderFactory.createEmptyBorder(30, 70, 30, 20));

        w1ContinueBtn = createImageButton(
                "/assets/GUIButtons/Continue.png", "/assets/GUIButtons/ContinueHover.png",
                964, 554, 154, 64, "Continue");
        w1ContinueBtn.addActionListener(e -> continueW1Dialogue());

        JButton w1MenuBtn = createImageButton(
                "/assets/GUIButtons/Menu.png", "/assets/GUIButtons/MenuHover.png",
                1103, 555, 148, 58, "Menu", 0);
        JButton w1BackBtn = createImageButton(
                "/assets/GUIButtons/Back.png", "/assets/GUIButtons/BackHover.png",
                970, 613, 140, 50, "Back", 20);
        JButton w1ExitBtn = createImageButton(
                "/assets/GUIButtons/Exit.png", "/assets/GUIButtons/ExitHover.png",
                1108, 613, 140, 50, "Exit", 19);
        w1ExitBtn.addActionListener(e -> System.exit(0));

        JLabel w1KhaiPanel = new JLabel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                Object prevImg = getClientProperty("prevImage");
                Object prevA   = getClientProperty("prevAlpha");
                if (prevImg instanceof Image && prevA instanceof Float && (Float)prevA > 0f) {
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (Float)prevA));
                    g2.drawImage((Image)prevImg, 0, 0, getWidth(), getHeight(), null);
                }
                if (getIcon() != null && world1KhaiAlpha[0] > 0f) {
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.min(1f, world1KhaiAlpha[0])));
                    g2.drawImage(((ImageIcon)getIcon()).getImage(), 0, 0, getWidth(), getHeight(), null);
                }
                g2.dispose();
            }
        };
        w1KhaiPanel.setBounds(0, 0, 1280, 520);
        w1KhaiPanel.setOpaque(false);
        world1KhaiLabel = w1KhaiPanel;

        layeredPane.setBounds(0, 0, 1280, 720);
        layeredPane.add(w1TheBg,          JLayeredPane.FRAME_CONTENT_LAYER);
        layeredPane.add(w1SceneBg,        JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(w1KhaiPanel,      JLayeredPane.PALETTE_LAYER);
        layeredPane.add(w1WorldLabel,     JLayeredPane.PALETTE_LAYER);
        layeredPane.add(w1DialogueBgLabel,JLayeredPane.PALETTE_LAYER);
        layeredPane.add(w1DialogueBox,    JLayeredPane.MODAL_LAYER);
        layeredPane.add(w1ContinueBtn,    JLayeredPane.PALETTE_LAYER);
        layeredPane.add(w1MenuBtn,        JLayeredPane.PALETTE_LAYER);
        layeredPane.add(w1BackBtn,        JLayeredPane.PALETTE_LAYER);
        layeredPane.add(w1ExitBtn,        JLayeredPane.PALETTE_LAYER);

        wrapper.add(layeredPane);
        return wrapper;
    }

    private String[] w1Chunks;
    private int w1ChunkIndex = 0;
    private Runnable w1ResumeAfterDialogue = null;
    private boolean w1InInterDialogue = false;
    private int w1InterDialogueIndex = 0;
    private String[] w1InterChunks;
    private int w1InterChunkIndex = 0;

    private void startW1Typing() {
        if (w1DialogueIndex >= WORLD1_DIALOGUES.length) return;
        if (w1TypingTimer != null && w1TypingTimer.isRunning()) w1TypingTimer.stop();

        w1Chunks = splitIntoChunks3(WORLD1_DIALOGUES[w1DialogueIndex]);
        w1ChunkIndex = 0;
        typeW1Chunk();
    }

    private String[] splitIntoChunks3(String text) {
        String[] lines = text.split("\n");
        java.util.List<String> chunks = new java.util.ArrayList<>();
        StringBuilder chunk = new StringBuilder();
        int count = 0;
        for (String line : lines) {
            if (chunk.length() > 0) chunk.append("\n");
            chunk.append(line);
            count++;
            if (count >= 3) {
                chunks.add(chunk.toString());
                chunk = new StringBuilder();
                count = 0;
            }
        }
        if (chunk.length() > 0) chunks.add(chunk.toString());
        return chunks.toArray(new String[0]);
    }

    private void typeW1Chunk() {
        if (w1Chunks == null || w1ChunkIndex >= w1Chunks.length) return;
        if (w1TypingTimer != null && w1TypingTimer.isRunning()) w1TypingTimer.stop();

        String text = w1Chunks[w1ChunkIndex];
        w1DialogueBox.setText("");
        int[] ci = {0};

        w1TypingTimer = new Timer(25, null);
        w1TypingTimer.addActionListener(e -> {
            if (ci[0] < text.length()) {
                w1DialogueBox.append(String.valueOf(text.charAt(ci[0]++)));
            } else {
                w1TypingTimer.stop();
                if (w1ChunkIndex < w1Chunks.length - 1) {
                    w1ContinueBtn.setEnabled(false);
                    delay(1000, () -> {
                        w1ChunkIndex++;
                        w1ContinueBtn.setEnabled(true);
                        typeW1Chunk();
                    });
                }
            }
        });
        w1TypingTimer.start();
    }

    private void continueW1Dialogue() {
        if (w1TypingTimer != null && w1TypingTimer.isRunning()) {
            w1TypingTimer.stop();
            if (w1InInterDialogue && w1InterChunks != null && w1InterChunkIndex < w1InterChunks.length) {
                w1DialogueBox.setText(w1InterChunks[w1InterChunkIndex]);
            } else if (w1Chunks != null && w1ChunkIndex < w1Chunks.length) {
                w1DialogueBox.setText(w1Chunks[w1ChunkIndex]);
            }
            return;
        }

        if (w1InInterDialogue) {
            w1InterChunkIndex++;
            if (w1InterChunkIndex < w1InterChunks.length) {
                typeInterChunk();
            } else {
                finishInterDialogue();
            }
            return;
        }

        if (w1DialogueIndex == 1 && w1WorldLabel != null) {
            fadeW1Label();
            delay(1000, () -> crossfadeW1ToKhai("/assets/Backgrounds/SilhouetteSirKhai.png", () -> {}));
        }

        if (w1DialogueIndex == 2) {
            transitionW1Scene("/assets/Backgrounds/SilhouetteSirKhai2.jpg");
        }

        if (w1DialogueIndex == 3) {
            try {
                java.net.URL u = getClass().getResource("/assets/Backgrounds/SilhouetteSirKhai3.png");
                if (u != null) {
                    world1KhaiLabel.putClientProperty("prevImage", null);
                    world1KhaiLabel.putClientProperty("prevAlpha", 0f);
                    world1KhaiLabel.setIcon(new ImageIcon(new ImageIcon(u).getImage().getScaledInstance(1280, 520, Image.SCALE_SMOOTH)));
                    world1KhaiAlpha[0] = 1.0f;
                    world1KhaiLabel.repaint();
                }
            } catch (Exception ex) { System.out.println("[W1] SirKhai3 error: " + ex.getMessage()); }
        }

        if (w1DialogueIndex == 4) {
            w1ContinueBtn.setEnabled(false);
            crossfadeKhaiToKhai("/assets/Backgrounds/SilhouetteSirKhai2.jpg", () -> {
                crossfadeKhaiToKhai("/assets/Backgrounds/SilhouetteSirKhai.png", () -> {
                    delay(1000, () -> {
                        world1KhaiAlpha[0] = 0f;
                        world1KhaiLabel.repaint();
                        world1WorldAlpha[0] = 1.0f;
                        world1SceneBg.repaint();
                        w1DialogueIndex = 5;
                        w1ContinueBtn.setEnabled(true);
                        startW1Typing();
                    });
                });
            });
            return;
        }

        if (w1DialogueIndex == 6) {
            w1ContinueBtn.setEnabled(false);
            crossfadeKhaiToKhai("/assets/Backgrounds/World1RodtfangWolf1.png", () -> {
                delay(1000, () -> {
                    try {
                        java.net.URL u = getClass().getResource("/assets/Backgrounds/World1RodtfangWolf2.png");
                        if (u != null) {
                            world1KhaiLabel.putClientProperty("prevImage", null);
                            world1KhaiLabel.putClientProperty("prevAlpha", 0f);
                            world1KhaiLabel.setIcon(new ImageIcon(new ImageIcon(u).getImage().getScaledInstance(1280, 520, Image.SCALE_SMOOTH)));
                            world1KhaiAlpha[0] = 1.0f;
                            world1KhaiLabel.repaint();
                        }
                    } catch (Exception ex) { System.out.println("[W1] Wolf2 error: " + ex.getMessage()); }
                    delay(1000, () -> {
                        w1DialogueIndex = 7;
                        w1ContinueBtn.setEnabled(true);
                        startW1Typing();
                    });
                });
            });
            return;
        }

        if (w1DialogueIndex == 7) {
            try {
                java.net.URL u = getClass().getResource("/assets/Backgrounds/World1RodtfangWolf3.png");
                if (u != null) {
                    world1KhaiLabel.putClientProperty("prevImage", null);
                    world1KhaiLabel.putClientProperty("prevAlpha", 0f);
                    world1KhaiLabel.setIcon(new ImageIcon(new ImageIcon(u).getImage().getScaledInstance(1280, 520, Image.SCALE_SMOOTH)));
                    world1KhaiAlpha[0] = 1.0f;
                    world1KhaiLabel.repaint();
                }
            } catch (Exception ex) { System.out.println("[W1] Wolf3 error: " + ex.getMessage()); }
        }

        w1DialogueIndex++;
        if (w1DialogueIndex < WORLD1_DIALOGUES.length) {
            startW1Typing();
        } else {
            w1DialogueIndex = 0;
            goToBattle();
        }
    }

    private void fadeW1Label() {
        if (w1WorldLabel == null) return;
        final float[] a = {1.0f};
        JLabel lbl = w1WorldLabel;
        Timer t = new Timer(16, null);
        t.addActionListener(e -> {
            a[0] = Math.max(0f, a[0] - 0.011f);
            lbl.setForeground(new Color(1f, 1f, 1f, a[0]));
            if (a[0] <= 0f) { ((Timer)e.getSource()).stop(); lbl.setVisible(false); }
        });
        t.start();
    }

    private void crossfadeKhaiToKhai(String path, Runnable onDone) {
        try {
            java.net.URL url = getClass().getResource(path);
            if (url == null) { System.out.println("[W1] Missing: " + path); if (onDone != null) onDone.run(); return; }
            java.awt.image.BufferedImage newImg = javax.imageio.ImageIO.read(url);
            final Image nextImage = newImg.getScaledInstance(1280, 520, Image.SCALE_SMOOTH);
            final Image prevImage = world1KhaiLabel.getIcon() != null
                    ? ((ImageIcon) world1KhaiLabel.getIcon()).getImage() : null;
            final float[] prevAlpha = {world1KhaiAlpha[0]};
            final float[] newAlpha  = {0f};
            world1KhaiLabel.setIcon(new ImageIcon(nextImage));
            world1KhaiLabel.putClientProperty("prevImage", prevImage);
            world1KhaiLabel.putClientProperty("prevAlpha", prevAlpha[0]);
            world1KhaiAlpha[0] = 0f;
            Timer fade = new Timer(16, null);
            fade.addActionListener(ev -> {
                prevAlpha[0] = Math.max(0f, prevAlpha[0] - 0.02f);
                newAlpha[0]  = Math.min(1f, newAlpha[0]  + 0.02f);
                world1KhaiLabel.putClientProperty("prevAlpha", prevAlpha[0]);
                world1KhaiAlpha[0] = newAlpha[0];
                world1KhaiLabel.repaint();
                if (prevAlpha[0] <= 0f && newAlpha[0] >= 1f) {
                    ((Timer)ev.getSource()).stop();
                    world1KhaiLabel.putClientProperty("prevImage", null);
                    world1KhaiLabel.putClientProperty("prevAlpha", 0f);
                    if (onDone != null) onDone.run();
                }
            });
            fade.start();
        } catch (Exception ex) { System.out.println("[W1] crossfadeKhaiToKhai error: " + ex.getMessage()); if (onDone != null) onDone.run(); }
    }

    private void crossfadeW1ToKhai(String path, Runnable onDone) {
        if (world1SceneBg == null) return;
        try {
            java.net.URL khaiUrl = getClass().getResource(path);
            if (khaiUrl == null) { System.out.println("[W1] Missing: " + path); return; }
            java.awt.image.BufferedImage khaiImg = javax.imageio.ImageIO.read(khaiUrl);
            final java.awt.image.BufferedImage fKhai = khaiImg;
            final float[] khaiAlpha = {0f};
            final float[] currentAlpha = {1.0f};
            Timer fade = new Timer(16, null);
            fade.addActionListener(kev -> {
                currentAlpha[0] = Math.max(0f, currentAlpha[0] - 0.02f);
                khaiAlpha[0]    = Math.min(1f, khaiAlpha[0]    + 0.02f);
                world1SceneAlpha[0] = 0f;
                world1WorldAlpha[0] = currentAlpha[0];
                if (world1KhaiLabel != null) {
                    world1KhaiLabel.setIcon(new ImageIcon(fKhai.getScaledInstance(1280, 520, Image.SCALE_SMOOTH)));
                    world1KhaiLabel.putClientProperty("prevImage", null);
                    world1KhaiLabel.putClientProperty("prevAlpha", 0f);
                    world1KhaiAlpha[0] = khaiAlpha[0];
                    world1KhaiLabel.repaint();
                }
                world1SceneBg.repaint();
                if (currentAlpha[0] <= 0f && khaiAlpha[0] >= 1f) {
                    ((Timer)kev.getSource()).stop();
                    world1WorldAlpha[0] = 0f;
                    world1SceneBg.repaint();
                    if (onDone != null) onDone.run();
                }
            });
            fade.start();
        } catch (Exception ex) { System.out.println("[W1] crossfadeW1ToKhai error: " + ex.getMessage()); }
    }

    private void transitionW1Scene(String path) {
        delay(1000, () -> {
            if (world1KhaiLabel == null) return;
            try {
                java.net.URL url = getClass().getResource(path);
                if (url == null) { System.out.println("[W1] Missing: " + path); return; }
                java.awt.image.BufferedImage newImg = javax.imageio.ImageIO.read(url);
                final Image nextImage = newImg.getScaledInstance(1280, 520, Image.SCALE_SMOOTH);
                final Image prevImage = world1KhaiLabel.getIcon() != null
                        ? ((ImageIcon) world1KhaiLabel.getIcon()).getImage() : null;
                final float[] prevAlpha = {1.0f};
                final float[] newAlpha  = {0f};
                world1KhaiLabel.setIcon(new ImageIcon(nextImage));
                world1KhaiLabel.putClientProperty("prevImage", prevImage);
                world1KhaiLabel.putClientProperty("prevAlpha", 1.0f);
                world1KhaiAlpha[0] = 0f;
                Timer fade = new Timer(16, null);
                fade.addActionListener(ev -> {
                    prevAlpha[0] = Math.max(0f, prevAlpha[0] - 0.02f);
                    newAlpha[0]  = Math.min(1f, newAlpha[0]  + 0.02f);
                    world1KhaiLabel.putClientProperty("prevAlpha", prevAlpha[0]);
                    world1KhaiAlpha[0] = newAlpha[0];
                    world1KhaiLabel.repaint();
                    if (prevAlpha[0] <= 0f && newAlpha[0] >= 1f) {
                        ((Timer)ev.getSource()).stop();
                        world1KhaiLabel.putClientProperty("prevImage", null);
                        world1KhaiLabel.putClientProperty("prevAlpha", 0f);
                    }
                });
                fade.start();
            } catch (Exception ex) {
                System.out.println("[W1] Scene transition error: " + ex.getMessage());
            }
        });
    }
}