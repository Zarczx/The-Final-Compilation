package GameGUI.engine;

import GameGUI.model.HeroData;
import GameGUI.model.HeroData.HeroDefinition;
import GameGUI.ui.BattlePanel;
import GameGUI.ui.HeroSelectionPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GameScreen extends JPanel {

    private static final String SCREEN_INTRO ="intro";
    private static final String SCREEN_SELECTION ="selection";
    private static final String SCREEN_POST_SELECT ="postSelect";
    private static final String SCREEN_WORLD1_INTRO ="world1Intro";
    private static final String SCREEN_WORLD2_INTRO ="world2Intro";
    private static final String SCREEN_WORLD3_INTRO ="world3Intro";
    private static final String SCREEN_BATTLE ="battle";
    private static final String SCREEN_SHOP = "shop";

    private GameGUI.ui.MagicShopPanel magicShopPanel;

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cardPanel = new JPanel(cardLayout);

    private HeroSelectionPanel heroSelectionPanel;
    private BattlePanel battlePanel;
    private JPanel postSelectPanel;
    private JPanel world1IntroPanel;
    private JPanel world2IntroPanel;
    private JPanel world3IntroPanel;

    private JTextArea postDialogueBox;
    private JButton postContinueBtn;
    private int postDialogueIndex = 0;

    private JTextArea w1DialogueBox;
    private JButton w1ContinueBtn;
    private int w1DialogueIndex = 0;
    private Timer w1TypingTimer;

    // World 2 intro screen fields
    private JTextArea w2DialogueBox;
    private JButton w2ContinueBtn;
    private int w2DialogueIndex = 0;
    private Timer w2TypingTimer;
    private String[] w2Chunks;
    private int w2ChunkIndex = 0;
    private JLabel w2WorldLabel;
    private JLabel w2KhaiLabel;
    private float[] w2KhaiAlpha = {0f};
    private JPanel w2SceneBg;
    private float[] w2SceneAlpha;
    private float[] w2WorldAlpha;

    // World 3 intro screen fields
    private JTextArea w3DialogueBox;
    private JButton w3ContinueBtn;
    private int w3DialogueIndex = 0;
    private Timer w3TypingTimer;
    private String[] w3Chunks;
    private int w3ChunkIndex = 0;
    private JLabel w3WorldLabel;
    private JLabel w3KhaiLabel;
    private float[] w3KhaiAlpha = {0f};
    private JPanel w3SceneBg;
    private float[] w3SceneAlpha;
    private float[] w3WorldAlpha;

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
    private HeroDefinition confirmedHero = null;
    private boolean pendingSelection = false;
    private int currentWorld = 1;

    private javax.swing.Timer pendingMusicTimer = null;

    private void switchMusic(String fileName, float volume) {
        if (pendingMusicTimer != null && pendingMusicTimer.isRunning()) {
            pendingMusicTimer.stop();
            pendingMusicTimer = null;
        }
        utils.SoundUtil.stopLoop();
        utils.SoundUtil.playLoop(fileName, volume);
    }


    private void playBattleMusic() {
        switchMusic("BattleBackgroundMusic.wav", 0.7f);
    }

    private void playWorldMusic() {
        if (currentWorld == 1) {
            switchMusic("TheForestOfSilence.wav", 0.5f);
        } else if (currentWorld == 2) {
            switchMusic("World2BackgroundMusic.wav", 0.5f);
        } else {
            switchMusic("TheForestOfSilence.wav", 0.5f);
        }
    }

    private void showWorld1TransitionImage(String fileName) {
        showW1InterBg("/assets/Backgrounds/" + fileName);
    }

    private void typeW1TransitionText(String text, Runnable onDone) {
        w1DialogueBox.setText("");
        w1ContinueBtn.setEnabled(false);
        int[] ci = {0};
        if (w1TypingTimer != null && w1TypingTimer.isRunning()) w1TypingTimer.stop();
        w1TypingTimer = new Timer(25, null);
        w1TypingTimer.addActionListener(e -> {
            if (ci[0] < text.length()) {
                w1DialogueBox.append(String.valueOf(text.charAt(ci[0]++)));
            } else {
                w1TypingTimer.stop();
                w1ContinueBtn.setEnabled(true);
                for (ActionListener l : w1ContinueBtn.getActionListeners()) {
                    w1ContinueBtn.removeActionListener(l);
                }
                w1ContinueBtn.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent ev) {
                        w1ContinueBtn.setEnabled(false);
                        w1ContinueBtn.removeActionListener(this);
                        w1ContinueBtn.addActionListener(e2 -> continueW1Dialogue());
                        if (onDone != null) onDone.run();
                    }
                });
            }
        });
        w1TypingTimer.start();
    }

    private void playW1TimedSequence(String[] imageFiles, int durationMs, Runnable onDone) {
        if (imageFiles == null || imageFiles.length == 0) {
            if (onDone != null) onDone.run();
            return;
        }
        final int[] idx = {0};
        Runnable[] step = {null};
        step[0] = () -> {
            if (idx[0] >= imageFiles.length) {
                if (onDone != null) onDone.run();
                return;
            }
            showWorld1TransitionImage(imageFiles[idx[0]]);
            idx[0]++;
            delay(durationMs, step[0]);
        };
        step[0].run();
    }


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

        world2IntroPanel = buildWorld2IntroScreen();
        cardPanel.add(world2IntroPanel, SCREEN_WORLD2_INTRO);

        world3IntroPanel = buildWorld3IntroScreen();
        cardPanel.add(world3IntroPanel, SCREEN_WORLD3_INTRO);

        battlePanel = new BattlePanel();
        battlePanel.setOnReturnToSelection(this::goToSelection);
        battlePanel.setOnRestartBattle(this::restartBattle);
        cardPanel.add(battlePanel, SCREEN_BATTLE);

        magicShopPanel = new GameGUI.ui.MagicShopPanel();
        magicShopPanel.setOnLeaveShop(this::startWorld3Transition);
        cardPanel.add(magicShopPanel, SCREEN_SHOP);

        add(cardPanel, BorderLayout.CENTER);

        setSceneBackground("assets/Backgrounds/NGEBackground.png");
        startTyping(dialogues[dialogueIndex], null);
    }

    // =========================================================================
    //  DEV TOOLS / TESTER WARPS
    // =========================================================================

    public void enableDevTools() {
        battlePanel.enableDevTools();
    }

    public void debugSkipToWorld1Battle(HeroDefinition hero) {
        this.confirmedHero = hero;
        this.currentWorld = 1;
        if (typingTimer != null) typingTimer.stop();
        playWorldMusic();
        goToWorld1Intro();
    }

    public void debugSkipToWorld2(HeroDefinition hero) {
        this.confirmedHero = hero;
        this.currentWorld = 2;
        if (typingTimer != null) typingTimer.stop();
        playWorldMusic();

        // Paint cardPanel black immediately before any card switch
        cardPanel.setBackground(Color.BLACK);
        cardPanel.setOpaque(true);

        // Also hide the current intro screen's scene image to kill NGEBackground
        if (sceneBgLabel != null) {
            sceneBgLabel.setIcon(null);
            sceneBgLabel.setOpaque(true);
            sceneBgLabel.setBackground(Color.BLACK);
        }

        goToWorld2Intro();
    }

    public void debugSkipToWorld3(HeroDefinition hero) {
        this.confirmedHero = hero;
        this.currentWorld = 3;
        if (typingTimer != null) typingTimer.stop();
        startWorld3Transition();
    }

    public void debugSkipToFinalBoss(HeroDefinition hero) {
        this.confirmedHero = hero;
        this.currentWorld = 3;
        if (typingTimer != null) typingTimer.stop();
        startFinalBossTransition();
    }

    public void debugSkipToShop(HeroDefinition hero) {
        this.confirmedHero = hero;
        this.currentWorld = 2;
        if (typingTimer != null) typingTimer.stop();

        GameGUI.model.Combatant dummy = GameGUI.model.HeroFactory.createHero(hero);
        dummy.soulShards = 999;

        magicShopPanel.loadPlayer(dummy);
        cardLayout.show(cardPanel, SCREEN_SHOP);
    }

    public void debugSkipToHollowStag(HeroDefinition hero) {
        this.confirmedHero = hero;
        this.currentWorld = 1;
        if (typingTimer != null) typingTimer.stop();

        // Find the Hollow Stag entry from WORLD1_ENEMIES and wrap it in a single-item list
        java.util.List<GameGUI.model.HeroData.EnemyDefinition> stagOnly =
                HeroData.WORLD1_ENEMIES.stream()
                        .filter(e -> e.name.equals("The Hollow Stag"))
                        .collect(java.util.stream.Collectors.toList());

        cardLayout.show(cardPanel, SCREEN_BATTLE);
        battlePanel.setOnEnemyGroupDefeated((nextGroupIndex, resumeFight) -> resumeFight.run());
        battlePanel.startEnemySequence(hero, stagOnly, this::startWorld2Transition);
    }

    public void debugSkipToEnemy(HeroData.HeroDefinition hero, String enemyName, int currentWorld) {
        this.confirmedHero = hero;
        this.currentWorld = currentWorld;
        if (typingTimer != null) typingTimer.stop();

        // Search all enemy lists for the matching enemy
        java.util.List<HeroData.EnemyDefinition> allEnemies = new java.util.ArrayList<>();
        allEnemies.addAll(HeroData.WORLD1_ENEMIES);
        allEnemies.addAll(HeroData.WORLD2_ENEMIES);
        allEnemies.addAll(HeroData.WORLD3_ENEMIES);

        java.util.List<HeroData.EnemyDefinition> match = allEnemies.stream()
                .filter(e -> e.name.equals(enemyName))
                .collect(java.util.stream.Collectors.toList());

        if (match.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Enemy not found: " + enemyName);
            return;
        }

        // Set correct background and music before starting
        if (currentWorld == 2) {
            battlePanel.setBattleBackground("/assets/Backgrounds/World2Battle1Background.png");
        }
        utils.SoundUtil.stopLoop();
        utils.SoundUtil.playLoop("BattleBackgroundMusic.wav", 0.7f);

        cardLayout.show(cardPanel, SCREEN_BATTLE);
        battlePanel.setOnEnemyGroupDefeated((nextGroupIndex, resumeFight) -> {
            if (currentWorld == 1) showInterEnemyDialogue(nextGroupIndex - 1, resumeFight);
            else if (currentWorld == 2) showWorld2InterDialogue(nextGroupIndex - 1, resumeFight);
            else if (currentWorld == 3) showWorld3InterDialogue(nextGroupIndex - 1, resumeFight);
            else resumeFight.run();
        });
        battlePanel.startEnemySequence(hero, match, () -> {
            if (currentWorld == 1) {
                playWorldMusic();
                startWorld2Transition();
            } else if (currentWorld == 2) {
                playWorldMusic();
                magicShopPanel.loadPlayer(battlePanel.getCurrentHero());
                cardLayout.show(cardPanel, SCREEN_SHOP);
            } else if (currentWorld == 3) {
                playWorldMusic();
                startFinalBossTransition();
            }
        });
    }

    private void goToIntro() { cardLayout.show(cardPanel, SCREEN_INTRO); }

    public void skipToWorld1(HeroDefinition hero) {
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
        utils.SoundUtil.stopLoop(); // stop immediately before switching
        cardLayout.show(cardPanel, SCREEN_BATTLE);
        playBattleMusic();

        battlePanel.setOnEnemyGroupDefeated((nextGroupIndex, resumeFight) -> {
            if (currentWorld == 1) {
                showInterEnemyDialogue(nextGroupIndex - 1, resumeFight);
            } else if (currentWorld == 2) {
                showWorld2InterDialogue(nextGroupIndex - 1, resumeFight);
            } else if (currentWorld == 3) {
                showWorld3InterDialogue(nextGroupIndex - 1, resumeFight);
            } else {
                resumeFight.run();
            }
        });

        if (currentWorld == 1) {
            battlePanel.startEnemySequence(
                    confirmedHero,
                    HeroData.WORLD1_ENEMIES,
                    () -> {
                        playWorldMusic();
                        startWorld2Transition();
                    }
            );
        } else if (currentWorld == 2) {
            battlePanel.setBattleBackground("/assets/Backgrounds/World2Battle1Background.png");
            battlePanel.startEnemySequence(
                    confirmedHero,
                    HeroData.WORLD2_ENEMIES,
                    () -> {
                        playWorldMusic();
                        magicShopPanel.loadPlayer(battlePanel.getCurrentHero());
                        cardLayout.show(cardPanel, SCREEN_SHOP);
                    }
            );
        }else if (currentWorld == 3) {
            battlePanel.startEnemySequence(
                    confirmedHero,
                    HeroData.WORLD3_ENEMIES,
                    () -> {
                        playWorldMusic();
                        startFinalBossTransition();
                    }
            );
        }
    }

    private void showInterEnemyDialogue(int interIndex, Runnable resumeFight) {
        if (interIndex < 0 || interIndex >= WORLD1_INTER_DIALOGUES.length) {
            resumeFight.run(); return;
        }
        w1ResumeAfterDialogue = resumeFight;
        w1InterDialogueIndex = interIndex;
        w1InInterDialogue = true;
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

        w1DialogueBox.setText("");
        cardLayout.show(cardPanel, SCREEN_WORLD1_INTRO);

        w1ContinueBtn.setEnabled(true);
        typeInterChunk();
    }

    private void typeInterChunk() {
        if (w1InterChunks == null || w1InterChunkIndex >= w1InterChunks.length) {
            finishInterDialogue(); return;
        }
        if (w1TypingTimer != null && w1TypingTimer.isRunning()) w1TypingTimer.stop();

        // ★ INTER-DIALOGUE 0 logic
        if (w1InterDialogueIndex == 0 && w1InterChunkIndex == 1) {
            w1ContinueBtn.setEnabled(false);
            showW1InterBg("/assets/Backgrounds/World1BattleBackgroundFog.png");
            delay(1500, () -> {
                showW1InterBg("/assets/Backgrounds/World1BattleBackgroundFogHands.png");
                delay(1500, () -> {
                    showW1InterBg("/assets/Backgrounds/World1BattleBackgroundFog.png");
                    w1ContinueBtn.setEnabled(true);
                    typeW1InterChunkNow();
                });
            });
            return;
        }

        if (w1InterDialogueIndex == 0 && w1InterChunkIndex == 2) {
            showW1InterBg("/assets/Backgrounds/World1BattleBackgroundShadowsDetached.png");
        }

        if (w1InterDialogueIndex == 0 && w1InterChunkIndex == 3) {
            showW1InterBg("/assets/Backgrounds/World1ShadeSprite.png");
        }

        // ★ INTER-DIALOGUE 1 logic
        // After chunk 0 ("The whispering finally stops"), play chunk 1 normally
        // After chunk 1 ("The ground shudders"), show World1Cracks.png then type
        if (w1InterDialogueIndex == 1 && w1InterChunkIndex == 1) {
            showW1InterBg("/assets/Backgrounds/World1Cracks.png");
            typeW1InterChunkNow();
            return;
        }

        // After chunk 1 is done and continue pressed, show treant image then type chunk 2
        if (w1InterDialogueIndex == 1 && w1InterChunkIndex == 2) {
            showW1InterBg("/assets/Backgrounds/World1DreadBarkTreants.png");
            typeW1InterChunkNow();
            return;
        }

        // ★ INTER-DIALOGUE 2 logic
        if (w1InterDialogueIndex == 2 && w1InterChunkIndex == 0) {
            showW1InterBg("/assets/Backgrounds/World1DreadbarkTreantsDefeated.png");
            typeW1InterChunkNow();
            return;
        }

        if (w1InterDialogueIndex == 2 && w1InterChunkIndex == 1) {
            w1ContinueBtn.setEnabled(false);
            showW1InterBg("/assets/Backgrounds/World1CarrionBatsCircle.png");
            delay(1500, () -> {
                showW1InterBg("/assets/Backgrounds/World1CarrionBatsSpread.png");
                w1ContinueBtn.setEnabled(true);
                typeW1InterChunkNow();
            });
            return;
        }

        if (w1InterDialogueIndex == 2 && w1InterChunkIndex == 2) {
            showW1InterBg("/assets/Backgrounds/World1CarrionBats.png");
            typeW1InterChunkNow();
            return;
        }

        // ★ INTER-DIALOGUE 3 logic
        if (w1InterDialogueIndex == 3 && w1InterChunkIndex == 0) {
            showW1InterBg("/assets/Backgrounds/World1CarrionBatsDefeat.png");
            typeW1InterChunkNow();
            return;
        }

        if (w1InterDialogueIndex == 3 && w1InterChunkIndex == 1) {
            showW1InterBg("/assets/Backgrounds/World1Moonlight.png");
            typeW1InterChunkNow();
            return;
        }

        if (w1InterDialogueIndex == 3 && w1InterChunkIndex == 2) {
            showW1InterBg("/assets/Backgrounds/World1HollowStagEncounter.png");
            typeW1InterChunkNow();
            return;
        }

        if (w1InterDialogueIndex == 3 && w1InterChunkIndex == 3) {
            w1ContinueBtn.setEnabled(false);
            showW1InterBg("/assets/Backgrounds/World1HollowStagEncounter2.png");
            delay(1500, () -> {
                showW1InterBg("/assets/Backgrounds/World1HollowStagEncounter3.png");
                w1ContinueBtn.setEnabled(true);
                typeW1InterChunkNow();
            });
            return;
        }

        if (w1InterDialogueIndex == 3 && w1InterChunkIndex == 4) {
            typeW1InterChunkNow();
            return;
        }

        if (w1InterDialogueIndex == 3 && w1InterChunkIndex == 5) {
            showW1InterBg("/assets/Backgrounds/World1HollowStag.png");
            typeW1InterChunkNow();
            return;
        }

        typeW1InterChunkNow();
    }

    private void showW1InterBg(String path) {
        if (world1KhaiLabel == null) return;
        java.net.URL url = getClass().getResource(path);
        if (url == null) return;
        world1KhaiLabel.setIcon(new ImageIcon(
                new ImageIcon(url).getImage().getScaledInstance(1280, 520, Image.SCALE_SMOOTH)));
        world1KhaiLabel.putClientProperty("prevImage", null);
        world1KhaiLabel.putClientProperty("prevAlpha", 0f);
        world1KhaiAlpha[0] = 1.0f;
        world1KhaiLabel.repaint();
    }

    private void typeW1InterChunkNow() {
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

    private void finishInterDialogue() {
        w1InInterDialogue = false;
        w1DialogueBox.setText("");
        Runnable resume = w1ResumeAfterDialogue;
        w1ResumeAfterDialogue = null;
        cardLayout.show(cardPanel, SCREEN_BATTLE);
        playBattleMusic();
        if (resume != null) resume.run();
    }

    private void restartBattle() { goToBattle(); }

    private void onHeroConfirmed(HeroDefinition hero) {
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
                dialogueBox.setFont(new Font("Dialog", Font.BOLD, 16));
            }
        } catch (Exception ex) {
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
        }
        popup.add(examStartBtn);

        examStartBtn.addActionListener(e -> {
            examPopup.setVisible(false);
            continueBtn.setEnabled(false);
            utils.SoundUtil.fadeOutLoop(1500);
            Timer startWorld1Music = new Timer(1600, null); // ← 1600ms, after fade completes
            startWorld1Music.setRepeats(false);
            startWorld1Music.addActionListener(ev -> {
                startWorld1Music.stop();
                switchMusic("TheForestOfSilence.wav", 0.5f);
            });
            startWorld1Music.start();
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
        }
        return btn;
    }

    private String[] postDialogues;

    private void buildPostSelectDialogues(HeroDefinition hero) {
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
            label.setVisible(false);
        }
    }

    private void startWorld2Transition() {
        currentWorld = 2;
        switchMusic("TheForestOfSilence.wav", 0.5f);

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
        w1ContinueBtn.setEnabled(false);

        typeW1TransitionText("Sir Khai's staff strikes the scorched earth with a resonant hum.", () ->
                playW1TimedSequence(
                        new String[]{
                                "World1Transition.png",
                                "World1Transition2.png",
                                "World1Transition3.png",
                                "World1Transition4.png"
                        },
                        2000,
                        () -> typeW1TransitionText(
                                "The forest around you shudders — not in pain, but in relief.\nGray bark cracks to reveal rich brown wood.",
                                () -> typeW1TransitionText(
                                        "The ash on the ground blooms into lush green moss.\nThe corruption fades, leaving behind faint sparks of life glowing in the air.",
                                        () -> {
                                            showWorld1TransitionImage("World1Transition5.png");
                                            typeW1TransitionText(
                                                    "\"This forest is saved. Life is beautiful,\"\nSir Khai murmurs, watching a small flower bloom.",
                                                    () -> typeW1TransitionText(
                                                            "\"But our journey is far from over.\nTwo more Stones remain… and darkness gathers ahead.\"",
                                                            () -> playW1TimedSequence(
                                                                    new String[]{
                                                                            "World1Transition6.png",
                                                                            "World1Transition7.png",
                                                                            "World1Transition8.png"
                                                                    },
                                                                    2000,
                                                                    () -> typeW1TransitionText(
                                                                            "A path begins to part through the trees, leading out of the forest...\nIt winds toward a valley shrouded in a wall of thick fog.",
                                                                            () -> playW1TimedSequence(
                                                                                    new String[]{
                                                                                            "World1Transition9.png",
                                                                                            "World1Transition10.png",
                                                                                            "World1Transition11.png"
                                                                                    },
                                                                                    2000,
                                                                                    () -> typeW1TransitionText(
                                                                                            "The sound of distant thunder echoes ahead.",
                                                                                            () -> {
                                                                                                utils.SoundUtil.stopLoop();
                                                                                                utils.SoundUtil.playLoop("World2BackgroundMusic.wav", 0.5f);
                                                                                                fadeToBlackThenWorld2();
                                                                                            }
                                                                                    )
                                                                            )
                                                                    )
                                                            )
                                                    )
                                            );
                                        }
                                )
                        )
                )
        );
    }

    private void fadeToBlackThenWorld2() {
        JPanel blackOverlay = new JPanel() {
            final float[] alpha = {0f};
            {
                setOpaque(false);
                setBounds(0, 0, 1280, 720);
                Timer fadeIn = new Timer(16, null);
                fadeIn.addActionListener(ev -> {
                    alpha[0] = Math.min(1f, alpha[0] + 0.025f);
                    repaint();
                    if (alpha[0] >= 1f) {
                        fadeIn.stop();
                        // Fully black — now switch screens
                        goToWorld2Intro();
                        // Remove this overlay after a short delay
                        // (goToWorld2Intro will show its own crossfade from black)
                        Timer remove = new Timer(100, e2 -> {
                            Container p = this.getParent();
                            if (p != null) { p.remove(this); p.repaint(); }
                        });
                        remove.setRepeats(false);
                        remove.start();
                    }
                });
                fadeIn.start();
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha[0]));
                g2.setColor(Color.BLACK);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };

        // Add on top of everything using GameScreen itself
        add(blackOverlay);
        setComponentZOrder(blackOverlay, 0);
        revalidate();
        repaint();// Add on top of cardPanel

    }

    private void goToWorld2Intro() {
        switchMusic("World2BackgroundMusic.wav", 0.5f);
        w2DialogueIndex = 0;

        // ── Step 1: slam a black panel over cardPanel immediately to kill any flash ──
        JPanel instantBlack = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(Color.BLACK);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        instantBlack.setBounds(0, 0, 1280, 720);
        instantBlack.setOpaque(false);
        cardPanel.add(instantBlack);
        cardPanel.setComponentZOrder(instantBlack, 0);
        cardPanel.revalidate();
        cardPanel.repaint();

        // ── Step 2: setup World 2 screen state while hidden behind black ──
        if (w2WorldLabel != null) {
            w2WorldLabel.setVisible(true);
            w2WorldLabel.setForeground(Color.WHITE);
        }
        if (w2KhaiLabel != null) {
            java.net.URL t12url = getClass().getResource("/assets/Backgrounds/World1Transition12.png");
            if (t12url != null) {
                w2KhaiLabel.setIcon(new ImageIcon(
                        new ImageIcon(t12url).getImage().getScaledInstance(1280, 520, Image.SCALE_SMOOTH)));
            } else {
                w2KhaiLabel.setIcon(null);
            }
            w2KhaiAlpha[0] = 0f;
            w2KhaiLabel.putClientProperty("prevImage", null);
            w2KhaiLabel.putClientProperty("prevAlpha", 0f);
        }
        if (w2SceneAlpha != null) w2SceneAlpha[0] = 0f;
        if (w2WorldAlpha != null) w2WorldAlpha[0] = 0f;
        if (w2SceneBg != null) {
            w2SceneBg.setOpaque(true);
            w2SceneBg.setBackground(Color.BLACK);
            w2SceneBg.repaint();
        }

        // Switch card while still hidden behind instantBlack
        cardLayout.show(cardPanel, SCREEN_WORLD2_INTRO);

        // ── Step 3: hold on black, then slowly fade in World1Transition12.png ──
        delay(1000, () -> {
            // Remove the instant black cover now that we're on the right card
            cardPanel.remove(instantBlack);
            cardPanel.revalidate();
            cardPanel.repaint();

            // Slow fade in — 0.008f per tick at 16ms = ~2 seconds to full opacity
            Timer fadeIn = new Timer(16, null);
            fadeIn.addActionListener(ev -> {
                if (w2KhaiLabel == null) { fadeIn.stop(); return; }
                w2KhaiAlpha[0] = Math.min(1f, w2KhaiAlpha[0] + 0.008f);
                w2KhaiLabel.repaint();
                if (w2KhaiAlpha[0] >= 1f) {
                    fadeIn.stop();
                    // Hold on image for a moment, then fade title and start dialogue
                    delay(800, () -> fadeW2Label());
                    delay(1600, () -> startW2Typing());
                }
            });
            fadeIn.start();
        });
    }

    private void fadeW2Label() {
        if (w2WorldLabel == null) return;
        final float[] a = {1.0f};
        JLabel lbl = w2WorldLabel;
        Timer t = new Timer(16, null);
        t.addActionListener(e -> {
            a[0] = Math.max(0f, a[0] - 0.011f);
            lbl.setForeground(new Color(1f, 1f, 1f, a[0]));
            if (a[0] <= 0f) { ((Timer)e.getSource()).stop(); lbl.setVisible(false); }
        });
        t.start();
    }

    private void startW2Typing() {
        if (w2DialogueIndex >= WORLD2_DIALOGUES.length) return;
        if (w2TypingTimer != null && w2TypingTimer.isRunning()) w2TypingTimer.stop();
        w2Chunks = splitIntoChunks3(WORLD2_DIALOGUES[w2DialogueIndex]);
        w2ChunkIndex = 0;
        typeW2Chunk();
    }

    private void typeW2Chunk() {
        if (w2Chunks == null || w2ChunkIndex >= w2Chunks.length) return;
        if (w2TypingTimer != null && w2TypingTimer.isRunning()) w2TypingTimer.stop();
        String text = w2Chunks[w2ChunkIndex];
        w2DialogueBox.setText("");
        int[] ci = {0};
        w2TypingTimer = new Timer(25, null);
        w2TypingTimer.addActionListener(e -> {
            if (ci[0] < text.length()) {
                w2DialogueBox.append(String.valueOf(text.charAt(ci[0]++)));
            } else {
                w2TypingTimer.stop();
                if (w2ChunkIndex < w2Chunks.length - 1) {
                    w2ContinueBtn.setEnabled(false);
                    delay(1000, () -> { w2ChunkIndex++; w2ContinueBtn.setEnabled(true); typeW2Chunk(); });
                } else {
                    w2ContinueBtn.setEnabled(true);
                }
            }
        });
        w2TypingTimer.start();
    }

    private void continueW2Dialogue() {
        if (w2TypingTimer != null && w2TypingTimer.isRunning()) {
            w2TypingTimer.stop();
            if (w2Chunks != null && w2ChunkIndex < w2Chunks.length)
                w2DialogueBox.setText(w2Chunks[w2ChunkIndex]);
            return;
        }
        if (w2InInterDialogue) {
            w2InterChunkIndex++;
            if (w2InterChunkIndex < w2InterChunks.length) typeW2InterChunk();
            else finishW2InterDialogue();
            return;
        }

        if (w2DialogueIndex == 0) {
            showW2SceneImage("/assets/Backgrounds/World1Transition13.png");
        } else if (w2DialogueIndex == 5) {
            showW2SceneImage("/assets/Backgrounds/World1Transition14.png");
        } else if (w2DialogueIndex == 6) {
            showW2SceneImage("/assets/Backgrounds/World1Transition15.png");
        } else if (w2DialogueIndex == 9) {
            w2ContinueBtn.setEnabled(false);
            playW2TimedSequence(
                    new String[]{"World1Transition16.png", "World1Transition17.png"},
                    2000,
                    () -> {
                        w2DialogueIndex = 10;
                        w2ContinueBtn.setEnabled(true);
                        startW2Typing();
                    }
            );
            return;
        } else if (w2DialogueIndex == 10) {
            w2ContinueBtn.setEnabled(false);
            playW2TimedSequence(
                    new String[]{"World1Transition18.png", "World1Transition19.png"},
                    2000,
                    () -> {
                        w2DialogueIndex = 11;
                        w2ContinueBtn.setEnabled(true);
                        startW2Typing();
                    }
            );
            return;
        } else if (w2DialogueIndex == 11) {
            showW2SceneImage("/assets/Backgrounds/World1Transition20.png");
        } else if (w2DialogueIndex == 12) {
            showW2SceneImage("/assets/Backgrounds/World2Battle1Background.png");
        }

        w2DialogueIndex++;
        if (w2DialogueIndex < WORLD2_DIALOGUES.length) {
            startW2Typing();
        } else {
            w2DialogueIndex = 0;
            goToBattle();
        }
    }

    private boolean w2InInterDialogue = false;
    private Runnable w2ResumeAfterDialogue = null;
    private String[] w2InterChunks;
    private int w2InterChunkIndex = 0;

    private void showWorld2InterDialogue(int interIndex, Runnable resumeFight) {
        if (interIndex < 0 || interIndex >= WORLD2_INTER_DIALOGUES.length) {
            resumeFight.run(); return;
        }
        w2ResumeAfterDialogue = resumeFight;
        w2InInterDialogue = true;
        w2InterChunks = WORLD2_INTER_DIALOGUES[interIndex];
        w2InterChunkIndex = 0;

        if (w2KhaiLabel != null) {
            w2KhaiLabel.setIcon(null);
            w2KhaiAlpha[0] = 0f;
            w2KhaiLabel.putClientProperty("prevImage", null);
            w2KhaiLabel.putClientProperty("prevAlpha", 0f);
            w2KhaiLabel.repaint();
        }
        if (w2SceneAlpha != null) w2SceneAlpha[0] = 0f;
        if (w2WorldAlpha != null) w2WorldAlpha[0] = 1.0f;
        if (w2SceneBg != null) w2SceneBg.repaint();
        if (w2WorldLabel != null) w2WorldLabel.setVisible(false);

        cardLayout.show(cardPanel, SCREEN_WORLD2_INTRO);
        w2ContinueBtn.setEnabled(true);
        typeW2InterChunk();
    }

    private void typeW2InterChunk() {
        if (w2InterChunks == null || w2InterChunkIndex >= w2InterChunks.length) {
            finishW2InterDialogue(); return;
        }
        if (w2TypingTimer != null && w2TypingTimer.isRunning()) w2TypingTimer.stop();
        String text = w2InterChunks[w2InterChunkIndex];
        w2DialogueBox.setText("");
        w2ContinueBtn.setEnabled(false);
        int[] ci = {0};
        w2TypingTimer = new Timer(25, null);
        w2TypingTimer.addActionListener(e -> {
            if (ci[0] < text.length()) {
                w2DialogueBox.append(String.valueOf(text.charAt(ci[0]++)));
            } else {
                w2TypingTimer.stop();
                w2ContinueBtn.setEnabled(true);
            }
        });
        w2TypingTimer.start();
    }

    private void finishW2InterDialogue() {
        w2InInterDialogue = false;
        w2DialogueBox.setText("");
        Runnable resume = w2ResumeAfterDialogue;
        w2ResumeAfterDialogue = null;
        cardLayout.show(cardPanel, SCREEN_BATTLE);
        playBattleMusic();
        if (resume != null) resume.run();
    }

    private void showW2SceneImage(String path) {
        if (w2KhaiLabel == null) return;
        java.net.URL url = getClass().getResource(path);
        if (url == null) return;
        w2KhaiLabel.setIcon(new ImageIcon(
                new ImageIcon(url).getImage().getScaledInstance(1280, 520, Image.SCALE_SMOOTH)));
        w2KhaiLabel.putClientProperty("prevImage", null);
        w2KhaiLabel.putClientProperty("prevAlpha", 0f);
        w2KhaiAlpha[0] = 1.0f;
        w2KhaiLabel.repaint();
    }

    private void playW2TimedSequence(String[] imageFiles, int durationMs, Runnable onDone) {
        if (imageFiles == null || imageFiles.length == 0) {
            if (onDone != null) onDone.run();
            return;
        }
        final int[] idx = {0};
        Runnable[] step = {null};
        step[0] = () -> {
            if (idx[0] >= imageFiles.length) {
                if (onDone != null) onDone.run();
                return;
            }
            showW2SceneImage("/assets/Backgrounds/" + imageFiles[idx[0]]);
            idx[0]++;
            delay(durationMs, step[0]);
        };
        step[0].run();
    }


    // ─── Dialogues ──────────────────────────────────────────────

    private static final String[][] WORLD2_INTER_DIALOGUES = {
            {
                    "The last Vermin goes still. The stench of rot clings to your clothes.",
                    "Further in, you hear a rhythmic chanting — low, guttural, wrong.\n" +
                            "The shadows ahead pulse with violet light.",
                    "FORSAKEN CULTISTS.\nThey have surrendered their souls for power. Now they serve the darkness without question."
            },
            {
                    "The chanting dies. The cultists crumple — their pact finally, mercifully broken.",
                    "The silence that follows is not peaceful.\nSomething low and wet breathes in the dark ahead.",
                    "Two shapes detach from the fog.\nBlight Hounds — their bodies wrong, their eyes hollow, driven by hunger and rot."
            },
            {
                    "The Hounds collapse. Their corruption bleeds into the mud beneath them.",
                    "Khai places a hand on your shoulder.\n\"The town garrison has fallen. What marches ahead were once its defenders.\"",
                    "GHOUL FOOTMEN — four of them.\nThey were soldiers once. Now they obey a master who does not care if they survive."
            },
            {
                    "The last Footman falls with a hollow clatter. The silence after is heavy.",
                    "From somewhere deep in the town, a door of iron groans open.\n" +
                            "Something massive steps through — chains dragging on stone.",
                    "\"The Black Jailer,\" Khai breathes. \"He keeps the Second Stone locked away.\"\n" +
                            "\"No one who has faced him has ever walked free.\""
            },
            {
                    "The Black Jailer staggers. His chains go slack — the first time they have ever rested.",
                    "His iron mask cracks. Behind it: a face that was once human.\n\"Free…\" he rasps. Then he is still.",
                    "A slow clap echoes through the hall. A figure descends a crumbling staircase.",
                    "LUTHER VON.\nThe Corrupted King. He who let this town rot from within while he sat on his throne.\n" +
                            "\"Impressive,\" he says. \"Now face something worthy of that title.\""
            }
    };

    private static final String[] WORLD2_DIALOGUES = {
            "You emerge from the forest's edge, \nbreathing as the clean air turns heavy and sour.",
            "Ahead lies a town, huddled against the gray sky.\nRelief surges for a moment — until the wind changes.",
            "It carries the copper scent of blood and the sickly sweet smell of rot.",
            "You walk through the broken gates. The mud is thick and black.",
            "This place is diseased. The buildings lean like dying men.",
            "The silence is broken only by wet, hacking coughs.",
            "Khai stops, his face twisting in grief.\n\"Look at them,\" he whispers.",
            "\"Children cough in alleys. \nHollow-eyed guards demand bribes just to look the other way.\"",
            "\"This town used to be the epitome of peace and unity,\" \nKhai continues, gripping his staff.",
            "\"Now, every face tells the same story:\nsomething has poisoned the very heart of this world.\"",
            "A scratching sound echoes from the gutters.\nThe shadows near your feet begin to move.",
            "The stench of rot suddenly intensifies.\nFrom the sewers and piles of filth, three PLAGUE VERMINS scuttle out.",
            "They hiss — claws dripping with venomous filth. \nThey do not flee. They charge."
    };

    private static final String[][] WORLD1_INTER_DIALOGUES = {
            {
                    "The path narrows. The mist becomes so thick\nyou can barely see your hand in front of your face.",
                    "The air grows icy. The silence is broken by a sound like static,\nor perhaps whispering voices overlapping until they become noise.",
                    "Shadows detach themselves from the trees.\nThey twist and contort, forming vague human-like shapes.",
                    "SHADE SPRITES.\nThey are the lost souls of travelers who died in this woods,\n now jealous of your life."
            },
            {
                    "The whispering finally stops.\nThe mist recedes, revealing faint lights hovering among the dead trees.",
                    "The ground shudders beneath your feet.\nAncient roots crack through the soil.",
                    "Two DREADBARK TREANTS pull themselves free from the earth."
            },
            {
                    "The Treants collapse in a shower of rotting bark.\nWhere they fall, small green sprouts push through the ash.",
                    "A foul stench drifts down from above.\nSomething vast circles in the dead canopy overhead.",
                    "Four CARRION BATS, each the size of a man, \ndive-bomb from the dead branches above."
            },
            {
                    "The last bat crashes into the earth.\nThe forest holds its breath.",
                    "Ahead, pale moonlight breaks through the canopy.\nA clearing opens - and within it, something stirs.",
                    "The trees twist around a great blackened oak.\nFrom behind it steps a massive stag, twelve feet tall at the shoulder.",
                    "Its antlers are cracked and glow with faint white fire.\nIts eyes burn not with anger, but with an ancient, crushing sadness.",
                    "Once a noble guardian of this forest, the Hollow Stag is corrupted by the darkness. Its hooves scorch the ground. \nIt lowers its head, seeking peace through battle.",
                    "Free him. Khai's voice echoes in your mind.\n\"Break the chains of the Necromancer.\""
            },
    };

    private static final String[] WORLD1_DIALOGUES = {
            "You wake up gasping for air. The world is drained of color.",          // index 0
            "You are lying on a bed of gray moss in a dead forest.\nThe trees are skeletal giants, stripped to bone-white wood.", // index 1
            "A cold mist coils around your ankles,\nand silence presses from every side watching, waiting.", // index 2
            "A heavy bell tolls in the distance...\n\"Dong... Dong...\"",           // index 3
            "From the mist steps a figure cloaked in tattered robes.\nHe leans heavily on a staff. As he lifts his hood, you jolt back,\nthe face is familiar. It looks exactly like your professor, Khai.", // index 4
            "But his eyes are weary, holding the weight of centuries.",             // index 5
            "\"Be calm, Traveler. In this realm, I am known as Khai the Gray.\"\n"+
                    "\"We suffer because an evil Necromancer has corrupted these lands.\n"+
                    "He has drained the nature itself. We must find the Three Stones of Life\n"+
                    "that hold this reality together. \nOnly then will your path home reveal itself.\"", // index 6
            "Khai fades back into the mist.",                                       // index 7
            "Three Rodtfang Wolves emerge from the tree line.",                     // index 8
            "Their glowing red eyes—",                                              // index 9
            "—fixate on you. They do not hunt for food — they hunt to kill."        // index 10
    };
    // ─── WORLD 3 DIALOGUES ──────────────────────────────────────────────
    private static final String[] WORLD3_DIALOGUES = {
            "You have been travelling for days, leaving the green world far behind.\n" +
                    "You have reached a land where not even a glimmer of life can survive.",
            "The earth here has turned to black glass. Ash falls like snow, coating your armor in gray dust.\n" +
                    "Rivers of molten fire carve through the rock, lighting the underbelly of the dark clouds.",
            "At the center of this desolation, rising higher than the mountains...\n" +
                    "Stands a spire of twisted obsidian, piercing the storm itself.\n" +
                    "THE NECROMANCER'S TOWER.",
            "\"We are here,\" Khai whispers, his voice barely audible over the roaring wind.\n" +
                    "\"The source of the rot. The end of the path.\"",
            "The ground beneath you becomes uncomfortably hot. The cracks in the rock begin to glow.\n" +
                    "Molten magma bubbles to the surface!",
            "From the fire, shape-less forms pull themselves together.\n" +
                    "FLAME REVENANTS rise, their bodies flickering with ember and hatred.\n" +
                    "They scream without mouths, a sound like burning timber."
    };

    private static final String[][] WORLD3_INTER_DIALOGUES = {
            {
                    "You steel yourself and look up at the Tower.",
                    "You begin the ascent. The air thickens with suffocating magic.\n" +
                            "Each step you take hums with a pulse from the Stones you carry, as if they are calling out.",
                    "The air grows cold, despite the rivers of lava flowing nearby.\n" +
                            "A hollow chanting fills the chamber, vibrating in your bones.",
                    "From the shadows of the obsidian pillars, figures draped in tattered robes emerge.\n" +
                            "BONE WARLOCKS.",
                    "They raise staffs made of spine and skull, chanting forbidden incantations\n" +
                            "to twist the very life force from your body."
            },
            {
                    "A deep, rhythmic thumping echoes through the cavern. Boom... Boom...\n" +
                            "Lava geysers burst upward, spraying molten rock against the walls.",
                    "Massive shadows rise from behind the curtain of fire.\n" +
                            "OBSIDIAN CRUSHERS emerge — molten giants forged from living stone and fury.",
                    "Their skin is black rock, their veins flow with lava,\n" +
                            "and they look at you as nothing more than dust to be swept away."
            },
            {
                    "Halfway up the winding stairs, you find something etched into the obsidian wall.\n" +
                            "It is a mural, ancient and jagged.",
                    "It shows a hooded figure holding three glowing stones high above a kneeling crowd.\n" +
                            "Beneath it, carved in a language that looks chillingly familiar, is a single phrase:\n" +
                            "\"TO TEACH IS TO CONTROL.\"",
                    "A shiver runs down your spine that has nothing to do with the cold.\n" +
                            "You climb higher into the spire. The air grows thin and impossibly cold.",
                    "Suddenly, stone cracks with a sharp snap!\n" +
                            "Perched on the obsidian ledges above, grim stone statues shed their rocky skin and shriek as they dive.",
                    "SOULFLAYER GARGOYLES take flight.\n" +
                            "Their wings block out the red lightning, and their eyes burn with hunger for the living."
            },
            {
                    "You reach the penultimate landing. The heat here is unbearable.\n" +
                            "The stone beneath your boots is soft, almost melting.",
                    "A towering figure steps from the magma falls blocking the path.\n" +
                            "ZYRRYL, Warden of the Shattered Tower.",
                    "His armor is forged from cursed steel and hardened lava.\n" +
                            "He drags a massive greatsword that glows white-hot."
            }
    };

    private static final String[] KHAI_BETRAYAL_DIALOGUE = {
            "With a heavy crash, Zyrryl, the Tower Warden, falls to the ground.\n" +
                    "You catch your breath. You hold the final Stone of Life.",
            "Sir Khai steps forward. His staff is no longer wood—it is blazing with chaotic energy.\n" +
                    "\"Finally.\"",
            "\"You’ve served well, my student.\n" +
                    "Who better to collect the Stones of Life than one who trusts their teacher blindly?\"",
            "\"I have guided you not to save this land... but to claim its power.\n" +
                    "I have been waiting for a vessel like you for a millennium.\"",
            "The air around him turns black. His weary eyes are gone, replaced by burning voids.",
            "\"I wish to bring chaos not only to this land, but to all lands beyond.\n" +
                    "The Necromancer you sought... The one who brings the end of worlds...\"",
            "...IS ME!!!!!!!!!"
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
        } catch (Exception ex) {}

        final java.awt.image.BufferedImage fLights6 = lights6Img;
        final java.awt.image.BufferedImage fWorld1  = world1Img;

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

        // index 2 — fade world label, show SilhouetteSirKhai
        if (w1DialogueIndex == 2 && w1WorldLabel != null) {
            fadeW1Label();
            delay(1000, () -> crossfadeW1ToKhai("/assets/Backgrounds/SilhouetteSirKhai.png", () -> {}));
        }

        // index 3 — SilhouetteSirKhai2
        if (w1DialogueIndex == 3) {
            transitionW1Scene("/assets/Backgrounds/SilhouetteSirKhai2.jpg");
        }

        // index 4 — SilhouetteSirKhai3
        if (w1DialogueIndex == 4) {
            try {
                java.net.URL u = getClass().getResource("/assets/Backgrounds/SilhouetteSirKhai3.png");
                if (u != null) {
                    world1KhaiLabel.putClientProperty("prevImage", null);
                    world1KhaiLabel.putClientProperty("prevAlpha", 0f);
                    world1KhaiLabel.setIcon(new ImageIcon(new ImageIcon(u).getImage().getScaledInstance(1280, 520, Image.SCALE_SMOOTH)));
                    world1KhaiAlpha[0] = 1.0f;
                    world1KhaiLabel.repaint();
                }
            } catch (Exception ex) {}
        }

        // index 5 — "But his eyes are weary" — DO NOTHING, image stays as SilhouetteSirKhai3

        // index 6 — "Be calm Traveler" — press continue → crossfade sequence → World1Background
        if (w1DialogueIndex == 6) {
            w1ContinueBtn.setEnabled(false);
            crossfadeKhaiToKhai("/assets/Backgrounds/SilhouetteSirKhai2.jpg", () -> {
                crossfadeKhaiToKhai("/assets/Backgrounds/SilhouetteSirKhai.png", () -> {
                    delay(1000, () -> {
                        world1KhaiAlpha[0] = 0f;
                        world1KhaiLabel.setIcon(null);
                        world1KhaiLabel.putClientProperty("prevImage", null);
                        world1KhaiLabel.putClientProperty("prevAlpha", 0f);
                        world1KhaiLabel.repaint();
                        world1WorldAlpha[0] = 1.0f;
                        world1SceneBg.repaint();
                        w1DialogueIndex = 7;
                        w1ContinueBtn.setEnabled(true);
                        startW1Typing();
                    });
                });
            });
            return;
        }

        // index 7 — "Khai fades back into the mist" — forest already showing, nothing to do
        // index 8 — "Three Rodtfang Wolves..." — Wolf1 crossfade
        if (w1DialogueIndex == 8) {
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
                    } catch (Exception ex) {}
                    delay(1000, () -> {
                        w1DialogueIndex = 9;
                        w1ContinueBtn.setEnabled(true);
                        startW1Typing();
                    });
                });
            });
            return;
        }

        // index 9 — Wolf3 image
        if (w1DialogueIndex == 9) {
            try {
                java.net.URL u = getClass().getResource("/assets/Backgrounds/World1RodtfangWolf3.png");
                if (u != null) {
                    world1KhaiLabel.putClientProperty("prevImage", null);
                    world1KhaiLabel.putClientProperty("prevAlpha", 0f);
                    world1KhaiLabel.setIcon(new ImageIcon(new ImageIcon(u).getImage().getScaledInstance(1280, 520, Image.SCALE_SMOOTH)));
                    world1KhaiAlpha[0] = 1.0f;
                    world1KhaiLabel.repaint();
                }
            } catch (Exception ex) {}
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
            if (url == null) { if (onDone != null) onDone.run(); return; }
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
        } catch (Exception ex) { if (onDone != null) onDone.run(); }
    }

    private void crossfadeW1ToKhai(String path, Runnable onDone) {
        if (world1SceneBg == null) return;
        try {
            java.net.URL khaiUrl = getClass().getResource(path);
            if (khaiUrl == null) { return; }
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
        } catch (Exception ex) {}
    }

    private void transitionW1Scene(String path) {
        delay(1000, () -> {
            if (world1KhaiLabel == null) return;
            try {
                java.net.URL url = getClass().getResource(path);
                if (url == null) { return; }
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
            } catch (Exception ex) { }
        });
    }

    private JPanel buildWorld2IntroScreen() {
        JLayeredPane layeredPane = new JLayeredPane();

        JPanel wrapper = new JPanel(null) {
            @Override public void doLayout() {
                super.doLayout();
                layeredPane.setBounds(0, 0, getWidth(), getHeight());
            }
        };
        wrapper.setBackground(new Color(20, 10, 30));
        wrapper.setLayout(null);
        wrapper.setPreferredSize(new Dimension(1280, 720));

        JLabel w2TheBg = new JLabel();
        w2TheBg.setBounds(0, 0, 1280, 720);
        w2TheBg.setOpaque(true);
        w2TheBg.setBackground(new Color(20, 10, 30));
        java.net.URL w2BgUrl = getClass().getResource("/assets/Backgrounds/TheBackground.png");
        if (w2BgUrl != null) {
            w2TheBg.setIcon(new ImageIcon(
                    new ImageIcon(w2BgUrl).getImage().getScaledInstance(1280, 720, Image.SCALE_SMOOTH)));
        }

        final float[] sceneAlpha = {1.0f};
        final float[] worldAlpha = {0.0f};

        java.awt.image.BufferedImage darkFrameImg = null;
        java.awt.image.BufferedImage world2Img    = null;
        try {
            java.net.URL darkUrl = getClass().getResource("/assets/Backgrounds/NGEBeforeLights6.png");
            java.net.URL w2url   = getClass().getResource("/assets/Backgrounds/World2Background.png");
            if (darkUrl != null) darkFrameImg = javax.imageio.ImageIO.read(darkUrl);
            if (w2url   != null) world2Img    = javax.imageio.ImageIO.read(w2url);
        } catch (Exception ex) { }

        final java.awt.image.BufferedImage fDark   = darkFrameImg;
        final java.awt.image.BufferedImage fWorld2 = world2Img;

        JPanel w2Scene = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                // Always fill black first so there is never a transparent/flicker frame
                g2.setColor(Color.BLACK);
                g2.fillRect(0, 0, getWidth(), getHeight());
                if (fDark != null && sceneAlpha[0] > 0) {
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, sceneAlpha[0]));
                    g2.drawImage(fDark, 0, 0, getWidth(), getHeight(), null);
                }
                if (fWorld2 != null && worldAlpha[0] > 0) {
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, worldAlpha[0]));
                    g2.drawImage(fWorld2, 0, 0, getWidth(), getHeight(), null);
                }
                g2.dispose();
            }
        };
        w2Scene.setBounds(0, 0, 1280, 520);
        w2Scene.setOpaque(true);
        w2Scene.setBackground(Color.BLACK);

        w2SceneBg    = w2Scene;
        w2SceneAlpha = sceneAlpha;
        w2WorldAlpha = worldAlpha;

        w2WorldLabel = new JLabel("WORLD 2 : THE DECAYING TOWN", SwingConstants.CENTER);
        w2WorldLabel.setBounds(0, 220, 1280, 50);
        w2WorldLabel.setForeground(Color.WHITE);
        w2WorldLabel.setFont(new Font("Serif", Font.BOLD | Font.ITALIC, 26));

        JLabel w2KhaiPanel = new JLabel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                Object prevImg = getClientProperty("prevImage");
                Object prevA   = getClientProperty("prevAlpha");
                if (prevImg instanceof Image && prevA instanceof Float && (Float)prevA > 0f) {
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (Float)prevA));
                    g2.drawImage((Image)prevImg, 0, 0, getWidth(), getHeight(), null);
                }
                if (getIcon() != null && w2KhaiAlpha[0] > 0f) {
                    g2.setComposite(AlphaComposite.getInstance(
                            AlphaComposite.SRC_OVER, Math.min(1f, w2KhaiAlpha[0])));
                    g2.drawImage(((ImageIcon)getIcon()).getImage(), 0, 0, getWidth(), getHeight(), null);
                }
                g2.dispose();
            }
        };
        w2KhaiPanel.setBounds(0, 0, 1280, 520);
        w2KhaiPanel.setOpaque(false);
        w2KhaiLabel = w2KhaiPanel;

        JLabel w2DialogueBgLabel = new JLabel();
        w2DialogueBgLabel.setBounds(-40, 453, 1053, 343);
        java.net.URL w2DbUrl = getClass().getResource("/assets/GUIButtons/DialogueBox.png");
        if (w2DbUrl != null) {
            w2DialogueBgLabel.setIcon(new ImageIcon(
                    new ImageIcon(w2DbUrl).getImage().getScaledInstance(1053, 343, Image.SCALE_SMOOTH)));
        }

        w2DialogueBox = new JTextArea();
        w2DialogueBox.setBounds(104, 541, 900, 100);
        w2DialogueBox.setEditable(false);
        w2DialogueBox.setLineWrap(true);
        w2DialogueBox.setWrapStyleWord(true);
        try {
            java.io.InputStream fs = getClass().getResourceAsStream("/assets/AssetFont/Pixelari.ttf");
            if (fs != null) {
                Font pf = Font.createFont(Font.TRUETYPE_FONT, fs).deriveFont(Font.BOLD, 19f);
                w2DialogueBox.setFont(pf);
            } else {
                w2DialogueBox.setFont(new Font("Dialog", Font.BOLD, 16));
            }
        } catch (Exception ex) {
            w2DialogueBox.setFont(new Font("Dialog", Font.BOLD, 16));
        }
        w2DialogueBox.setOpaque(false);
        w2DialogueBox.setBackground(new Color(0, 0, 0, 0));
        w2DialogueBox.setForeground(Color.BLACK);
        w2DialogueBox.setBorder(BorderFactory.createEmptyBorder(30, 70, 30, 20));

        w2ContinueBtn = createImageButton(
                "/assets/GUIButtons/Continue.png", "/assets/GUIButtons/ContinueHover.png",
                964, 554, 154, 64, "Continue");
        w2ContinueBtn.addActionListener(e -> continueW2Dialogue());

        JButton w2MenuBtn = createImageButton(
                "/assets/GUIButtons/Menu.png", "/assets/GUIButtons/MenuHover.png",
                1103, 555, 148, 58, "Menu", 0);
        JButton w2BackBtn = createImageButton(
                "/assets/GUIButtons/Back.png", "/assets/GUIButtons/BackHover.png",
                970, 613, 140, 50, "Back", 20);
        JButton w2ExitBtn = createImageButton(
                "/assets/GUIButtons/Exit.png", "/assets/GUIButtons/ExitHover.png",
                1108, 613, 140, 50, "Exit", 19);
        w2ExitBtn.addActionListener(e -> System.exit(0));

        layeredPane.setBounds(0, 0, 1280, 720);
        layeredPane.add(w2TheBg,           JLayeredPane.FRAME_CONTENT_LAYER);
        layeredPane.add(w2Scene,           JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(w2KhaiPanel,       JLayeredPane.PALETTE_LAYER);
        layeredPane.add(w2WorldLabel,      JLayeredPane.PALETTE_LAYER);
        layeredPane.add(w2DialogueBgLabel, JLayeredPane.PALETTE_LAYER);
        layeredPane.add(w2DialogueBox,     JLayeredPane.MODAL_LAYER);
        layeredPane.add(w2ContinueBtn,     JLayeredPane.PALETTE_LAYER);
        layeredPane.add(w2MenuBtn,         JLayeredPane.PALETTE_LAYER);
        layeredPane.add(w2BackBtn,         JLayeredPane.PALETTE_LAYER);
        layeredPane.add(w2ExitBtn,         JLayeredPane.PALETTE_LAYER);

        wrapper.add(layeredPane);
        return wrapper;
    }

    // =========================================================================
    //  BUILD WORLD 3 INTRO SCREEN
    // =========================================================================
    private JPanel buildWorld3IntroScreen() {
        JLayeredPane layeredPane = new JLayeredPane();

        JPanel wrapper = new JPanel(null) {
            @Override public void doLayout() {
                super.doLayout();
                layeredPane.setBounds(0, 0, getWidth(), getHeight());
            }
        };
        wrapper.setBackground(new Color(30, 10, 10));
        wrapper.setLayout(null);
        wrapper.setPreferredSize(new Dimension(1280, 720));

        JLabel w3TheBg = new JLabel();
        w3TheBg.setBounds(0, 0, 1280, 720);
        w3TheBg.setOpaque(true);
        w3TheBg.setBackground(new Color(30, 10, 10));
        java.net.URL w3BgUrl = getClass().getResource("/assets/Backgrounds/TheBackground.png");
        if (w3BgUrl != null) {
            w3TheBg.setIcon(new ImageIcon(
                    new ImageIcon(w3BgUrl).getImage().getScaledInstance(1280, 720, Image.SCALE_SMOOTH)));
        }

        final float[] sceneAlpha = {1.0f};
        final float[] worldAlpha = {0.0f};

        java.awt.image.BufferedImage darkFrameImg = null;
        java.awt.image.BufferedImage world3Img    = null;
        try {
            java.net.URL darkUrl = getClass().getResource("/assets/Backgrounds/NGEBeforeLights6.png");
            java.net.URL w3url   = getClass().getResource("/assets/Backgrounds/World3Background.png");
            if (darkUrl != null) darkFrameImg = javax.imageio.ImageIO.read(darkUrl);
            if (w3url   != null) world3Img    = javax.imageio.ImageIO.read(w3url);
        } catch (Exception ex) { }

        final java.awt.image.BufferedImage fDark   = darkFrameImg;
        final java.awt.image.BufferedImage fWorld3 = world3Img;

        JPanel w3Scene = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();

                g2.setColor(new Color(30, 10, 10));
                g2.fillRect(0, 0, getWidth(), getHeight());

                if (fDark != null && sceneAlpha[0] > 0) {
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, sceneAlpha[0]));
                    g2.drawImage(fDark, 0, 0, getWidth(), getHeight(), null);
                }
                if (fWorld3 != null && worldAlpha[0] > 0) {
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, worldAlpha[0]));
                    g2.drawImage(fWorld3, 0, 0, getWidth(), getHeight(), null);
                }
                g2.dispose();
            }
        };
        w3Scene.setBounds(0, 0, 1280, 520);
        w3Scene.setOpaque(false);

        w3SceneBg    = w3Scene;
        w3SceneAlpha = sceneAlpha;
        w3WorldAlpha = worldAlpha;

        w3WorldLabel = new JLabel("WORLD 3 : THE NECROMANCER'S TOWER", SwingConstants.CENTER);
        w3WorldLabel.setBounds(0, 220, 1280, 50);
        w3WorldLabel.setForeground(Color.WHITE);
        w3WorldLabel.setFont(new Font("Serif", Font.BOLD | Font.ITALIC, 26));

        JLabel w3KhaiPanel = new JLabel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                Object prevImg = getClientProperty("prevImage");
                Object prevA   = getClientProperty("prevAlpha");
                if (prevImg instanceof Image && prevA instanceof Float && (Float)prevA > 0f) {
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (Float)prevA));
                    g2.drawImage((Image)prevImg, 0, 0, getWidth(), getHeight(), null);
                }
                if (getIcon() != null && w3KhaiAlpha[0] > 0f) {
                    g2.setComposite(AlphaComposite.getInstance(
                            AlphaComposite.SRC_OVER, Math.min(1f, w3KhaiAlpha[0])));
                    g2.drawImage(((ImageIcon)getIcon()).getImage(), 0, 0, getWidth(), getHeight(), null);
                }
                g2.dispose();
            }
        };
        w3KhaiPanel.setBounds(0, 0, 1280, 520);
        w3KhaiPanel.setOpaque(false);
        w3KhaiLabel = w3KhaiPanel;

        JLabel w3DialogueBgLabel = new JLabel();
        w3DialogueBgLabel.setBounds(-40, 453, 1053, 343);
        java.net.URL w3DbUrl = getClass().getResource("/assets/GUIButtons/DialogueBox.png");
        if (w3DbUrl != null) {
            w3DialogueBgLabel.setIcon(new ImageIcon(
                    new ImageIcon(w3DbUrl).getImage().getScaledInstance(1053, 343, Image.SCALE_SMOOTH)));
        }

        w3DialogueBox = new JTextArea();
        w3DialogueBox.setBounds(104, 541, 900, 100);
        w3DialogueBox.setEditable(false);
        w3DialogueBox.setLineWrap(true);
        w3DialogueBox.setWrapStyleWord(true);
        try {
            java.io.InputStream fs = getClass().getResourceAsStream("/assets/AssetFont/Pixelari.ttf");
            if (fs != null) {
                Font pf = Font.createFont(Font.TRUETYPE_FONT, fs).deriveFont(Font.BOLD, 19f);
                w3DialogueBox.setFont(pf);
            } else {
                w3DialogueBox.setFont(new Font("Dialog", Font.BOLD, 16));
            }
        } catch (Exception ex) {
            w3DialogueBox.setFont(new Font("Dialog", Font.BOLD, 16));
        }
        w3DialogueBox.setOpaque(false);
        w3DialogueBox.setBackground(new Color(0, 0, 0, 0));
        w3DialogueBox.setForeground(Color.BLACK);
        w3DialogueBox.setBorder(BorderFactory.createEmptyBorder(30, 70, 30, 20));

        w3ContinueBtn = createImageButton(
                "/assets/GUIButtons/Continue.png", "/assets/GUIButtons/ContinueHover.png",
                964, 554, 154, 64, "Continue");
        w3ContinueBtn.addActionListener(e -> continueW3Dialogue());

        JButton w3MenuBtn = createImageButton(
                "/assets/GUIButtons/Menu.png", "/assets/GUIButtons/MenuHover.png",
                1103, 555, 148, 58, "Menu", 0);
        JButton w3BackBtn = createImageButton(
                "/assets/GUIButtons/Back.png", "/assets/GUIButtons/BackHover.png",
                970, 613, 140, 50, "Back", 20);
        JButton w3ExitBtn = createImageButton(
                "/assets/GUIButtons/Exit.png", "/assets/GUIButtons/ExitHover.png",
                1108, 613, 140, 50, "Exit", 19);
        w3ExitBtn.addActionListener(e -> System.exit(0));

        layeredPane.setBounds(0, 0, 1280, 720);
        layeredPane.add(w3TheBg,           JLayeredPane.FRAME_CONTENT_LAYER);
        layeredPane.add(w3Scene,           JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(w3KhaiPanel,       JLayeredPane.PALETTE_LAYER);
        layeredPane.add(w3WorldLabel,      JLayeredPane.PALETTE_LAYER);
        layeredPane.add(w3DialogueBgLabel, JLayeredPane.PALETTE_LAYER);
        layeredPane.add(w3DialogueBox,     JLayeredPane.MODAL_LAYER);
        layeredPane.add(w3ContinueBtn,     JLayeredPane.PALETTE_LAYER);
        layeredPane.add(w3MenuBtn,         JLayeredPane.PALETTE_LAYER);
        layeredPane.add(w3BackBtn,         JLayeredPane.PALETTE_LAYER);
        layeredPane.add(w3ExitBtn,         JLayeredPane.PALETTE_LAYER);

        wrapper.add(layeredPane);
        return wrapper;
    }

    // =========================================================================
    //  WORLD 3 TRANSITION & LOGIC
    // =========================================================================

    private void startWorld3Transition() {
        utils.SoundUtil.stopLoop();                                    // ← ADD THIS
        utils.SoundUtil.playLoop("TheForestOfSilence.wav", 0.5f);     // ← ADD THIS
        currentWorld = 3;
        w3DialogueIndex = 0;
        if (w3WorldLabel != null) {
            w3WorldLabel.setVisible(true);
            w3WorldLabel.setForeground(Color.WHITE);
        }
        if (w3KhaiLabel != null) {
            w3KhaiLabel.setIcon(null);
            w3KhaiAlpha[0] = 0f;
            w3KhaiLabel.putClientProperty("prevImage", null);
            w3KhaiLabel.putClientProperty("prevAlpha", 0f);
        }
        if (w3SceneAlpha != null) w3SceneAlpha[0] = 1.0f;
        if (w3WorldAlpha != null) w3WorldAlpha[0] = 0.0f;
        if (w3SceneBg != null) w3SceneBg.repaint();

        cardLayout.show(cardPanel, SCREEN_WORLD3_INTRO);

        delay(800, () -> {
            Timer crossfade = new Timer(16, null);
            crossfade.addActionListener(ev -> {
                if (w3SceneBg == null) { crossfade.stop(); return; }
                w3SceneAlpha[0] = Math.max(0f, w3SceneAlpha[0] - 0.02f);
                w3WorldAlpha[0] = Math.min(1f, w3WorldAlpha[0] + 0.02f);
                w3SceneBg.repaint();
                if (w3SceneAlpha[0] <= 0f && w3WorldAlpha[0] >= 1f) {
                    crossfade.stop();
                    delay(600, () -> fadeW3Label());
                    delay(1400, () -> startW3Typing());
                }
            });
            crossfade.start();
        });
    }

    private void fadeW3Label() {
        if (w3WorldLabel == null) return;
        final float[] a = {1.0f};
        JLabel lbl = w3WorldLabel;
        Timer t = new Timer(16, null);
        t.addActionListener(e -> {
            a[0] = Math.max(0f, a[0] - 0.011f);
            lbl.setForeground(new Color(1f, 1f, 1f, a[0]));
            if (a[0] <= 0f) { ((Timer)e.getSource()).stop(); lbl.setVisible(false); }
        });
        t.start();
    }

    private void startW3Typing() {
        if (w3DialogueIndex >= WORLD3_DIALOGUES.length) return;
        if (w3TypingTimer != null && w3TypingTimer.isRunning()) w3TypingTimer.stop();
        w3Chunks = splitIntoChunks3(WORLD3_DIALOGUES[w3DialogueIndex]);
        w3ChunkIndex = 0;
        typeW3Chunk();
    }

    private void typeW3Chunk() {
        if (w3Chunks == null || w3ChunkIndex >= w3Chunks.length) return;
        if (w3TypingTimer != null && w3TypingTimer.isRunning()) w3TypingTimer.stop();
        String text = w3Chunks[w3ChunkIndex];
        w3DialogueBox.setText("");
        int[] ci = {0};
        w3TypingTimer = new Timer(25, null);
        w3TypingTimer.addActionListener(e -> {
            if (ci[0] < text.length()) {
                w3DialogueBox.append(String.valueOf(text.charAt(ci[0]++)));
            } else {
                w3TypingTimer.stop();
                if (w3ChunkIndex < w3Chunks.length - 1) {
                    w3ContinueBtn.setEnabled(false);
                    delay(1000, () -> { w3ChunkIndex++; w3ContinueBtn.setEnabled(true); typeW3Chunk(); });
                } else {
                    w3ContinueBtn.setEnabled(true);
                }
            }
        });
        w3TypingTimer.start();
    }

    private void continueW3Dialogue() {
        if (w3TypingTimer != null && w3TypingTimer.isRunning()) {
            w3TypingTimer.stop();
            if (w3Chunks != null && w3ChunkIndex < w3Chunks.length)
                w3DialogueBox.setText(w3Chunks[w3ChunkIndex]);
            return;
        }
        if (w3InInterDialogue) {
            w3InterChunkIndex++;
            if (w3InterChunkIndex < w3InterChunks.length) typeW3InterChunk();
            else finishW3InterDialogue();
            return;
        }
        w3DialogueIndex++;
        if (w3DialogueIndex < WORLD3_DIALOGUES.length) {
            startW3Typing();
        } else {
            w3DialogueIndex = 0;
            goToBattle();
        }
    }

    private boolean w3InInterDialogue = false;
    private Runnable w3ResumeAfterDialogue = null;
    private String[] w3InterChunks;
    private int w3InterChunkIndex = 0;

    private void showWorld3InterDialogue(int interIndex, Runnable resumeFight) {
        if (interIndex < 0 || interIndex >= WORLD3_INTER_DIALOGUES.length) {
            resumeFight.run(); return;
        }
        w3ResumeAfterDialogue = resumeFight;
        w3InInterDialogue = true;
        w3InterChunks = WORLD3_INTER_DIALOGUES[interIndex];
        w3InterChunkIndex = 0;

        if (w3KhaiLabel != null) {
            w3KhaiLabel.setIcon(null);
            w3KhaiAlpha[0] = 0f;
            w3KhaiLabel.putClientProperty("prevImage", null);
            w3KhaiLabel.putClientProperty("prevAlpha", 0f);
            w3KhaiLabel.repaint();
        }
        if (w3SceneAlpha != null) w3SceneAlpha[0] = 0f;
        if (w3WorldAlpha != null) w3WorldAlpha[0] = 1.0f;
        if (w3SceneBg != null) w3SceneBg.repaint();
        if (w3WorldLabel != null) w3WorldLabel.setVisible(false);

        cardLayout.show(cardPanel, SCREEN_WORLD3_INTRO);
        w3ContinueBtn.setEnabled(true);
        typeW3InterChunk();
    }

    private void typeW3InterChunk() {
        if (w3InterChunks == null || w3InterChunkIndex >= w3InterChunks.length) {
            finishW3InterDialogue(); return;
        }
        if (w3TypingTimer != null && w3TypingTimer.isRunning()) w3TypingTimer.stop();
        String text = w3InterChunks[w3InterChunkIndex];
        w3DialogueBox.setText("");
        w3ContinueBtn.setEnabled(false);
        int[] ci = {0};
        w3TypingTimer = new Timer(25, null);
        w3TypingTimer.addActionListener(e -> {
            if (ci[0] < text.length()) {
                w3DialogueBox.append(String.valueOf(text.charAt(ci[0]++)));
            } else {
                w3TypingTimer.stop();
                w3ContinueBtn.setEnabled(true);
            }
        });
        w3TypingTimer.start();
    }

    private void finishW3InterDialogue() {
        w3InInterDialogue = false;
        w3DialogueBox.setText("");
        Runnable resume = w3ResumeAfterDialogue;
        w3ResumeAfterDialogue = null;
        cardLayout.show(cardPanel, SCREEN_BATTLE);
        playBattleMusic(); // ← this was missing, unlike finishW2InterDialogue
        if (resume != null) resume.run();
    }

    private void startFinalBossTransition() {
        cardLayout.show(cardPanel, SCREEN_WORLD3_INTRO);

        if (w3SceneBg != null) {
            w3SceneBg.setBackground(Color.BLACK);
            w3SceneBg.setOpaque(true);
            w3SceneBg.repaint();
        }
        if (w3WorldLabel != null) w3WorldLabel.setVisible(false);
        if (w3KhaiLabel != null) w3KhaiLabel.setIcon(null);

        w3ContinueBtn.setEnabled(false);

        final int[] idx = {0};
        Runnable[] typeNext = {null};

        typeNext[0] = () -> {
            if (idx[0] >= KHAI_BETRAYAL_DIALOGUE.length) {
                w3ContinueBtn.setEnabled(false);
                cardLayout.show(cardPanel, SCREEN_BATTLE);
                battlePanel.startEnemySequence(
                        confirmedHero,
                        GameGUI.model.HeroData.FINAL_BOSS_SEQUENCE,
                        () -> System.out.println("GAME OVER - YOU BEAT THE GAME!")
                );
                return;
            }

            String text = KHAI_BETRAYAL_DIALOGUE[idx[0]];
            w3DialogueBox.setText("");

            if (idx[0] == 6) {
                w3DialogueBox.setForeground(Color.RED);
                w3DialogueBox.setFont(w3DialogueBox.getFont().deriveFont(28f));

                Point origin = getLocation();
                Timer shakeTimer = new Timer(50, ev -> {
                    int dx = (Math.random() > 0.5 ? 5 : -5);
                    int dy = (Math.random() > 0.5 ? 5 : -5);
                    setLocation(origin.x + dx, origin.y + dy);
                });
                shakeTimer.start();
                delay(1000, () -> {
                    shakeTimer.stop();
                    setLocation(origin);
                });
            } else {
                w3DialogueBox.setForeground(Color.WHITE);
            }

            int[] ci = {0};
            if (w3TypingTimer != null && w3TypingTimer.isRunning()) w3TypingTimer.stop();
            final Runnable next = typeNext[0];

            w3TypingTimer = new Timer(35, e -> {
                if (ci[0] < text.length()) {
                    w3DialogueBox.append(String.valueOf(text.charAt(ci[0]++)));
                } else {
                    w3TypingTimer.stop();
                    idx[0]++;
                    w3ContinueBtn.setEnabled(true);

                    for (ActionListener l : w3ContinueBtn.getActionListeners()) w3ContinueBtn.removeActionListener(l);
                    w3ContinueBtn.addActionListener(ev -> {
                        w3ContinueBtn.setEnabled(false);
                        for (ActionListener l : w3ContinueBtn.getActionListeners()) w3ContinueBtn.removeActionListener(l);
                        next.run();
                    });
                }
            });
            w3TypingTimer.start();
        };

        typeNext[0].run();
    }
}