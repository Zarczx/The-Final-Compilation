package GameGUI.engine;

import GameGUI.model.entity.HeroData;
import GameGUI.model.entity.HeroData.HeroDefinition;
import GameGUI.model.entity.Combatant;
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
    private static final String SCREEN_PREFI = "prefi";

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
        if (currentWorld == 2) switchMusic("BattleBackgroundMusic2.WAV", 0.7f);
        else if (currentWorld == 3) switchMusic("BattleBackgroundMusic3.WAV", 0.7f);
        else switchMusic("BattleBackgroundMusic.wav", 0.7f);
    }

    private void playWorldMusic() {
        if (currentWorld == 1) {
            switchMusic("TheForestOfSilence.wav", 0.5f);
        } else if (currentWorld == 2) {
            switchMusic("World2BackgroundMusic.wav", 0.5f);
        } else {
            switchMusic("World3BackgroundMusic.WAV", 0.5f);
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

        battlePanel.setOnOpenSaveScreen(this::openSaveScreen);
        battlePanel.setOnPromptSaveAndExit(this::promptSaveAndExit);

        cardPanel.add(battlePanel, SCREEN_BATTLE);

        magicShopPanel = new GameGUI.ui.MagicShopPanel();
        magicShopPanel.setOnLeaveShop(this::showPostShopDialogue);
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

        cardPanel.setBackground(Color.BLACK);
        cardPanel.setOpaque(true);

        if (sceneBgLabel != null) {
            sceneBgLabel.setIcon(null);
            sceneBgLabel.setOpaque(true);
            sceneBgLabel.setBackground(Color.BLACK);
        }

        goToWorld2Intro();
        this.currentWorld = 2;
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
        battlePanel.setBattleBackground("/assets/Backgrounds/NecroBackground.png");
        startFinalBossTransition();
    }

    public void debugSkipToShop(HeroData.HeroDefinition heroDef) {
        this.confirmedHero = heroDef;
        this.currentWorld = 2;
        if (typingTimer != null) typingTimer.stop();

        // 1. Create the test hero
        Combatant realHero = GameGUI.model.HeroFactory.createHero(heroDef);
        realHero.soulShards = 999;

        // 2. INJECT the hero into BattlePanel so Shop upgrades carry over to battles!
        battlePanel.setCurrentHero(realHero);

        magicShopPanel.loadPlayer(realHero);
        cardLayout.show(cardPanel, SCREEN_SHOP);
    }

    // Use this for the GameTester button
    public void debugSkipToPrefiEncounter(HeroData.HeroDefinition heroDef) {
        this.confirmedHero = heroDef;
        this.currentWorld = 3;
        if (typingTimer != null) typingTimer.stop();

        // 1. Create the test hero
        Combatant realHero = GameGUI.model.HeroFactory.createHero(heroDef);

        // 2. INJECT the hero into BattlePanel so it uses THIS exact object for the Final Boss!
        battlePanel.setCurrentHero(realHero);

        // 3. Start the encounter
        startPrefiEncounter(realHero);
    }

    // This handles the actual transition and logic
    private void startPrefiEncounter(Combatant player) {
        switchMusic("PrefinalMusic.WAV", 0.6f);

        GameGUI.ui.PrefiEncounterGUI prefiPanel = new GameGUI.ui.PrefiEncounterGUI(player, () -> {
            // This runs when the player passes or fails the encounter!
            playWorldMusic();
            startFinalBossTransition(); // Move on to Khai's betrayal
        });

        cardPanel.add(prefiPanel, SCREEN_PREFI);
        cardLayout.show(cardPanel, SCREEN_PREFI);
    }

    public void debugSkipToHollowStag(HeroDefinition hero) {
        this.confirmedHero = hero;
        this.currentWorld = 1;
        if (typingTimer != null) typingTimer.stop();

        java.util.List<HeroData.EnemyDefinition> stagOnly =
                HeroData.WORLD1_ENEMIES.stream()
                        .filter(e -> e.name.equals("The Hollow Stag"))
                        .collect(java.util.stream.Collectors.toList());

        cardLayout.show(cardPanel, SCREEN_BATTLE);
        playBattleMusic();
        battlePanel.setOnEnemyGroupDefeated((nextGroupIndex, resumeFight) -> resumeFight.run());
        battlePanel.startEnemySequence(hero, stagOnly, this::startWorld2Transition);
    }

    public void debugSkipToEnemy(HeroData.HeroDefinition hero, String enemyName, int currentWorld) {
        this.confirmedHero = hero;
        this.currentWorld = currentWorld;
        if (typingTimer != null) typingTimer.stop();

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

        if (currentWorld == 1) {
            String bg = switch (enemyName) {
                case "Rotfang Wolf"    -> "/assets/Backgrounds/World1BattleBackground.png";
                case "Shade Sprite"    -> "/assets/Backgrounds/World1BattleBackground2.png";
                case "Dreadbark Treant"-> "/assets/Backgrounds/World1BattleBackground3.png";
                case "Carrion Bat"     -> "/assets/Backgrounds/World1BattleBackground4.png";
                case "The Hollow Stag" -> "/assets/Backgrounds/World1BattleBackground5.png";
                default                -> "/assets/Backgrounds/World1BattleBackground.png";
            };
            battlePanel.setBattleBackground(bg);
        } else if (currentWorld == 2) {
            String bg = switch (enemyName) {
                case "Plague Vermin"    -> "/assets/Backgrounds/World2Battle1Background.png";
                case "Forsaken Cultist" -> "/assets/Backgrounds/World2BattleBackground2.png";
                case "Blight Hound"     -> "/assets/Backgrounds/World2BattleBackground3.png";
                case "Ghoul Footman"    -> "/assets/Backgrounds/World2BattleBackground4.png";
                case "The Black Jailer" -> "/assets/Backgrounds/World2BattleBackground5.png";
                case "Luther Von"       -> "/assets/Backgrounds/World2BattleBackgroundLast.png";
                default                 -> "/assets/Backgrounds/World2Battle1Background.png";
            };
            battlePanel.setBattleBackground(bg);
        } else if (currentWorld == 3) {
            String bg = switch (enemyName) {
                case "Flame Revenant"      -> "/assets/Backgrounds/World3BG9.png";
                case "Bone Warlock"        -> "/assets/Backgrounds/World3BG15.5.png";
                case "Obsidian Crusher"    -> "/assets/Backgrounds/World3BG21.5.png";
                case "Soulflayer Gargoyle" -> "/assets/Backgrounds/World3BG27.png";
                case "Zyrryl"              -> "/assets/Backgrounds/World3BG30.5.png";
                case "Khai the Gray"   -> "/assets/Backgrounds/NecroBackground.png";
                default                    -> "/assets/Backgrounds/World3BG9.png";
            };
            battlePanel.setBattleBackground(bg);
        }

        utils.SoundUtil.stopLoop();
        playBattleMusic();

        cardLayout.show(cardPanel, SCREEN_BATTLE);
        battlePanel.setOnEnemyGroupDefeated((nextGroupIndex, resumeFight) -> {
            if (currentWorld == 1) {
                showInterEnemyDialogue(nextGroupIndex - 1, resumeFight);
            } else if (currentWorld == 2) {
                if (nextGroupIndex == 1) {
                    battlePanel.setBattleBackground("/assets/Backgrounds/World2BattleBackground2.png");
                }
                showWorld2InterDialogue(nextGroupIndex - 1, resumeFight);
            } else if (currentWorld == 3) {
                showWorld3InterDialogue(nextGroupIndex - 1, resumeFight);
            } else {
                resumeFight.run();
            }
        });

        battlePanel.startEnemySequence(hero, match, () -> {
            if (currentWorld == 1) {
                playWorldMusic();
                startWorld2Transition();
            } else if (currentWorld == 2) {
                playWorldMusic();
                showKingVictoryDialogue();
            } else if (currentWorld == 3) {
                // ★ PREFI ENCOUNTER LAUNCHES HERE ★
                startPrefiEncounter(battlePanel.getCurrentHero());
            }
        });
    }

    private void showPostShopDialogue() {
        showWorld2InterDialogue(WORLD2_INTER_DIALOGUES.length - 1, this::startWorld3Transition);
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
        utils.SoundUtil.stopSFX(); // ← ADD THIS LINE
        utils.SoundUtil.stopLoop();
        cardLayout.show(cardPanel, SCREEN_BATTLE);
        playBattleMusic();

        battlePanel.setOnEnemyGroupDefeated((nextGroupIndex, resumeFight) -> {
            if (currentWorld == 1) {
                showInterEnemyDialogue(nextGroupIndex - 1, resumeFight);
            } else if (currentWorld == 2) {
                if (nextGroupIndex == 1) {
                    battlePanel.setBattleBackground("/assets/Backgrounds/World2BattleBackground2.png");
                }
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
                        showKingVictoryDialogue();
                    }
            );
        } else if (currentWorld == 3) {
            battlePanel.setBattleBackground("/assets/Backgrounds/World3BG9.png");
            battlePanel.startEnemySequence(
                    confirmedHero,
                    HeroData.WORLD3_ENEMIES,
                    () -> {
                        // ★ PREFI ENCOUNTER LAUNCHES HERE ★
                        startPrefiEncounter(battlePanel.getCurrentHero());
                    }
            );
        }
    }

    private void showKingVictoryDialogue() {
        String[] kingVictory = {
                "FINAL VICTORY - BOSS DEFEATED!\n" +
                        "The King screams as the corruption tears free — he crumbles to dust,\n" +
                        "leaving only his rusted crown and a pulsing stone upon the throne.",
                "His breath rattles. A faint glimmer of humanity returns to his hollow eyes.",
                "\"It's… It's you!!! NO!!!…\"",
                "He screams with his dying breath as his body crumbles into dust.",
                "He leaves behind only a pile of ash… and confusion about who he was referring to.",
                "You lift the SECOND STONE from the ash, feeling its dark energy pulse in your hands.",
                "The room falls into silence, whispering secrets of the past.",
                "As you step forward, a strange pull brushes against your soul.\n" +
                        "The world itself feels like it is shifting around you.",
                "Something… or someone… is calling to you.",
                "A BRILLIANT FLASH LIGHTS UP THE ROOM!",
                "From the shattered shadows, a glowing arcane doorway forms before you.",
                "A calm, ancient voice echoes:\n" +
                        "\"Hero… you are granted one chance to reshape your fate.\"",
                "\"I appear only to those who have conquered great darkness.\"\n" +
                        "\"Stock up now — once you leave, I will vanish forever.\""
        };

        w2InInterDialogue = true;
        w2InterChunks = kingVictory;
        w2InterChunkIndex = 0;
        w2ResumeAfterDialogue = () -> {
            magicShopPanel.loadPlayer(battlePanel.getCurrentHero());
            switchMusic("MagicShopMusic.WAV", 0.5f);
            cardLayout.show(cardPanel, SCREEN_SHOP);
        };

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

    private void showInterEnemyDialogue(int interIndex, Runnable resumeFight) {
        if (interIndex < 0 || interIndex >= WORLD1_INTER_DIALOGUES.length) {
            resumeFight.run(); return;
        }
        playWorldMusic();
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
            utils.SoundUtil.play("WhisperingSound.WAV");
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

        if (w1InterDialogueIndex == 1 && w1InterChunkIndex == 1) {
            utils.SoundUtil.play("EarthquakeSound.WAV");
            showW1InterBg("/assets/Backgrounds/World1Cracks.png");
            typeW1InterChunkNow();
            return;
        }

        if (w1InterDialogueIndex == 1 && w1InterChunkIndex == 2) {
            showW1InterBg("/assets/Backgrounds/World1DreadBarkTreants.png");
            typeW1InterChunkNow();
            return;
        }

        if (w1InterDialogueIndex == 2 && w1InterChunkIndex == 0) {
            showW1InterBg("/assets/Backgrounds/World1DreadbarkTreantsDefeated.png");
            typeW1InterChunkNow();
            return;
        }

        if (w1InterDialogueIndex == 2 && w1InterChunkIndex == 1) {
            w1ContinueBtn.setEnabled(false);
            utils.SoundUtil.play("BatSound.WAV");
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
        utils.SoundUtil.stopSFX(); // ★ ADD THIS
        playBattleMusic();
        if (resume != null) resume.run();
    }

    private void restartBattle() { goToBattle(); }

    private void onHeroConfirmed(HeroDefinition hero) {
        battlePanel.setCurrentHero(null);

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
        dialogueBgLabel.setBounds(-15, 468, 1053, 300);
        java.net.URL dialogueBgUrl = getClass().getResource("/assets/GUIButtons/DialogueBox.png");
        if (dialogueBgUrl != null) {
            ImageIcon rawDb = new ImageIcon(dialogueBgUrl);
            Image scaledDb = rawDb.getImage().getScaledInstance(990, 180, Image.SCALE_SMOOTH);
            dialogueBgLabel.setIcon(new ImageIcon(scaledDb));
        }

        dialogueBox = new JTextArea();
        dialogueBox.setBounds(2, 541, 900, 100);
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
                964, 554, 140, 64,"Continue");
        menuBtn = createImageButton(
                "/assets/GUIButtons/Menu.png","/assets/GUIButtons/MenuHover.png",
                1103, 555, 148, 58,"Menu", 0);
        backBtn = createImageButton(
                "/assets/GUIButtons/Save.png","/assets/GUIButtons/SaveHover.png",
                962, 613, 145, 50,"Back", 0);
        exitBtn = createImageButton(
                "/assets/GUIButtons/Exit.png","/assets/GUIButtons/ExitHover.png",
                1104, 613, 145, 50,"Exit", 0);

        exitBtn.addActionListener(e -> promptSaveAndExit());
        backBtn.addActionListener(e -> openSaveScreen());
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
            utils.SoundUtil.play("NGEGlitchSound2.WAV");
            utils.SoundUtil.fadeOutLoop(1500);
            Timer startWorld1Music = new Timer(1600, null);
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
            utils.SoundUtil.play("NGETypingSound2.WAV");
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
                @Override public void mouseEntered(java.awt.event.MouseEvent e) { btn.setIcon(hoverIcon); utils.SoundUtil.play("HoverSound.wav"); }
                @Override public void mouseExited (java.awt.event.MouseEvent e) { btn.setIcon(normalIcon); }
            });
            btn.addActionListener(e -> utils.SoundUtil.play("SelectSound2.wav"));
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
        postDialogueBgLabel.setBounds(-15, 468, 1053, 300);
        java.net.URL postDbUrl = getClass().getResource("/assets/GUIButtons/DialogueBox.png");
        if (postDbUrl != null) {
            Image postDbScaled = new ImageIcon(postDbUrl).getImage().getScaledInstance(990, 180, Image.SCALE_SMOOTH);
            postDialogueBgLabel.setIcon(new ImageIcon(postDbScaled));
        }

        postDialogueBox = new JTextArea();
        postDialogueBox.setBounds(2, 541, 900, 100);
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
                964, 554, 140, 64, "Continue");
        postContinueBtn.addActionListener(e -> continuePostDialogue());

        JButton postMenuBtn = createImageButton(
                "/assets/GUIButtons/Menu.png", "/assets/GUIButtons/MenuHover.png",
                1103, 555, 148, 58, "Menu", 0);
        JButton postBackBtn = createImageButton(
                "/assets/GUIButtons/Save.png", "/assets/GUIButtons/SaveHover.png",
                962, 613, 145, 50, "Back", 0);
        JButton postExitBtn = createImageButton(
                "/assets/GUIButtons/Exit.png", "/assets/GUIButtons/ExitHover.png",
                1104, 613, 145, 50, "Exit", 0);
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
                        goToWorld2Intro();
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

        add(blackOverlay);
        setComponentZOrder(blackOverlay, 0);
        revalidate();
        repaint();
    }

    private void goToWorld2Intro() {
        this.currentWorld = 2;
        switchMusic("World2BackgroundMusic.wav", 0.5f);
        w2DialogueIndex = 0;

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

        cardLayout.show(cardPanel, SCREEN_WORLD2_INTRO);

        delay(1000, () -> {
            cardPanel.remove(instantBlack);
            cardPanel.revalidate();
            cardPanel.repaint();

            Timer fadeIn = new Timer(16, null);
            fadeIn.addActionListener(ev -> {
                if (w2KhaiLabel == null) { fadeIn.stop(); return; }
                w2KhaiAlpha[0] = Math.min(1f, w2KhaiAlpha[0] + 0.008f);
                w2KhaiLabel.repaint();
                if (w2KhaiAlpha[0] >= 1f) {
                    fadeIn.stop();
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
            utils.SoundUtil.play("RatHissing.WAV");
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
        playWorldMusic();
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

    // =========================================================================
    //  WORLD 2 INTER-DIALOGUE TYPING
    // =========================================================================
    private void typeW2InterChunk() {
        if (w2InterChunks == null || w2InterChunkIndex >= w2InterChunks.length) {
            finishW2InterDialogue(); return;
        }
        if (w2TypingTimer != null && w2TypingTimer.isRunning()) w2TypingTimer.stop();

        int interIdx = resolveW2InterIndex();

        // ── INTER 0: after Plague Vermin ──────────────────────────────────────
        if (interIdx == 0) {
            switch (w2InterChunkIndex) {
                case 0 -> { showW2InterBg("/assets/Backgrounds/PlagueVerminDead.png"); typeW2InterChunkNow(); return; }
                case 1 -> { showW2InterBg("/assets/Backgrounds/PlagueVerminDead2.png"); typeW2InterChunkNow(); return; }
                case 2 -> { showW2InterBg("/assets/Backgrounds/WanderAround.png"); typeW2InterChunkNow(); return; }
                case 3 -> {
                    w2ContinueBtn.setEnabled(false);
                    showW2InterBg("/assets/Backgrounds/World2Beggar.png");
                    delay(2000, () -> { showW2InterBg("/assets/Backgrounds/World2Beggar2.png"); w2ContinueBtn.setEnabled(true); typeW2InterChunkNow(); });
                    return;
                }
                case 4 -> { showW2InterBg("/assets/Backgrounds/World2Beggar3.png"); typeW2InterChunkNow(); return; }
                case 5 -> { /* stay on World2Beggar3.png */ typeW2InterChunkNow(); return; }
                case 6 -> { showW2InterBg("/assets/Backgrounds/WanderAround.png"); typeW2InterChunkNow(); return; }
                case 7 -> { utils.SoundUtil.play("CultSound.WAV"); showW2InterBg("/assets/Backgrounds/World2Chapel.png"); typeW2InterChunkNow(); return; }
                case 8 -> {
                    // Show World2Chapel2.png, let player read, then on continue:
                    // ForsakenCultist.png 2s → ForsakenCultist2.png, then type
                    w2ContinueBtn.setEnabled(false);
                    showW2InterBg("/assets/Backgrounds/World2Chapel2.png");
                    w2ContinueBtn.setEnabled(true);
                    for (ActionListener l : w2ContinueBtn.getActionListeners()) w2ContinueBtn.removeActionListener(l);
                    w2ContinueBtn.addActionListener(new java.awt.event.ActionListener() {
                        @Override public void actionPerformed(java.awt.event.ActionEvent ev) {
                            w2ContinueBtn.removeActionListener(this);
                            w2ContinueBtn.addActionListener(e2 -> continueW2Dialogue());
                            w2ContinueBtn.setEnabled(false);
                            showW2InterBg("/assets/Backgrounds/World2ForsakenCultist.png");
                            delay(2000, () -> { showW2InterBg("/assets/Backgrounds/World2ForsakenCultist2.png"); w2ContinueBtn.setEnabled(true); typeW2InterChunkNow(); });
                        }
                    });
                    return;
                }
            }
        }

        // ── INTER 1: after Forsaken Cultists ─────────────────────────────────
        if (interIdx == 1) {
            switch (w2InterChunkIndex) {
                case 0 -> {
                    // Immediately show ForsakenCultistDead.png, then type
                    showW2InterBg("/assets/Backgrounds/World2ForsakenCultistDead.png");
                    typeW2InterChunkNow();
                    return;
                }
                case 1 -> {
                    // On continue: show World2ExitChapel.png for 2s → World2AfterChapel.png, then type
                    w2ContinueBtn.setEnabled(false);
                    showW2InterBg("/assets/Backgrounds/World2ExitChapel.png");
                    delay(2000, () -> {
                        showW2InterBg("/assets/Backgrounds/World2AfterChapel.png");
                        w2ContinueBtn.setEnabled(true);
                        typeW2InterChunkNow();
                    });
                    return;
                }
                case 2 -> {
                    utils.SoundUtil.play("DogGrowlSound.WAV");
                    // Stay on World2AfterChapel.png, just type
                    typeW2InterChunkNow();
                    return;
                }
                case 3 -> {
                    // Show World2BlighthoundSilhouette.png for 2s → World2BlightHoundEncounter.png, then type
                    w2ContinueBtn.setEnabled(false);
                    showW2InterBg("/assets/Backgrounds/World2BlightHoundSilhouette.png");
                    delay(2000, () -> {
                        showW2InterBg("/assets/Backgrounds/World2BlightHoundEncounter.png");
                        w2ContinueBtn.setEnabled(true);
                        typeW2InterChunkNow();
                    });
                    return;
                }
            }
        }

        // ── INTER 2: after Blight Hounds ─────────────────────────────────────
        if (interIdx == 2) {
            switch (w2InterChunkIndex) {
                case 0 -> {
                    // Immediately: World2BlightHoundDefeated.png for 2s → World2BattleBlackCastleGate.png
                    w2ContinueBtn.setEnabled(false);
                    showW2InterBg("/assets/Backgrounds/World2BlightHoundDefeated.png");
                    delay(2000, () -> {
                        showW2InterBg("/assets/Backgrounds/World2BlackCastleGate.png");
                        w2ContinueBtn.setEnabled(true);
                        typeW2InterChunkNow();
                    });
                    return;
                }
                case 1 -> {
                    // Stay on World2BattleBlackCastleGate.png
                    typeW2InterChunkNow();
                    return;
                }
                case 2 -> {
                    // Show World2BlackCastleDoor.png
                    utils.SoundUtil.play("LightningDoorSound.WAV");
                    showW2InterBg("/assets/Backgrounds/World2BlackCastleDoor.png");
                    typeW2InterChunkNow();
                    return;
                }
                case 3 -> {
                    // Show World2BlackCastleInside.png
                    showW2InterBg("/assets/Backgrounds/World2BlackCastleInside.png");
                    typeW2InterChunkNow();
                    return;
                }
                case 4 -> {
                    // Show World2GhoulFootman.png
                    showW2InterBg("/assets/Backgrounds/World2GhoulFootman.png");
                    typeW2InterChunkNow();
                    return;
                }
            }
        }

        // ── INTER 3: after Ghoul Footmen ─────────────────────────────────────
        if (interIdx == 3) {
            switch (w2InterChunkIndex) {
                case 0 -> {
                    // Immediately: World2GhoulFootmanDefeated.png
                    showW2InterBg("/assets/Backgrounds/World2GhoulFootmanDefeated.png");
                    typeW2InterChunkNow();
                    return;
                }
                case 1 -> {
                    // Show World2BlackCastleDescend.png
                    showW2InterBg("/assets/Backgrounds/World2BlackCastleDescend.png");
                    typeW2InterChunkNow();
                    return;
                }
                case 2 -> {
                    // Show World2BlackJailer.png
                    utils.SoundUtil.play("DraggingWater.WAV");
                    showW2InterBg("/assets/Backgrounds/World2BlackJailer.png");
                    typeW2InterChunkNow();
                    return;
                }
                case 3 -> {
                    // Show World2BlackJailer2.png
                    utils.SoundUtil.play("DraggingWater.WAV");
                    showW2InterBg("/assets/Backgrounds/World2BlackJailer2.png");
                    typeW2InterChunkNow();
                    return;
                }
                case 4 -> {
                    // Show World2BlackJailer3.png
                    showW2InterBg("/assets/Backgrounds/World2BlackJailer3.png");
                    typeW2InterChunkNow();
                    return;
                }
            }
        }

        // ── INTER 4: after Black Jailer ───────────────────────────────────────
        if (interIdx == 4) {
            switch (w2InterChunkIndex) {
                case 0 -> {
                    // Immediately: World2BlackJailerDefeated.png 2s → World2BlackJailerDefeated2.png 2s → World2BlackJailerDefeated3.png
                    w2ContinueBtn.setEnabled(false);
                    showW2InterBg("/assets/Backgrounds/World2BlackJailerDefeated.png");
                    delay(2000, () -> {
                        showW2InterBg("/assets/Backgrounds/World2BlackJailerDefeated2.png");
                        delay(2000, () -> {
                            showW2InterBg("/assets/Backgrounds/World2BlackJailerDefeated3.png");
                            w2ContinueBtn.setEnabled(true);
                            typeW2InterChunkNow();
                        });
                    });
                    return;
                }
                case 1 -> {
                    // Stay on World2BlackJailerDefeated3.png
                    typeW2InterChunkNow();
                    return;
                }
                case 2 -> {
                    // Show World2Key.png for 2s → World2Key2.png
                    w2ContinueBtn.setEnabled(false);
                    showW2InterBg("/assets/Backgrounds/World2Key.png");
                    delay(2000, () -> {
                        showW2InterBg("/assets/Backgrounds/World2Key2.png");
                        w2ContinueBtn.setEnabled(true);
                        typeW2InterChunkNow();
                    });
                    return;
                }
                case 3 -> {
                    // Show World2Ascend.png
                    showW2InterBg("/assets/Backgrounds/World2Ascend.png");
                    typeW2InterChunkNow();
                    return;
                }
                case 4 -> {
                    // Show World2ThroneDoor.png
                    showW2InterBg("/assets/Backgrounds/World2ThroneDoor.png");
                    typeW2InterChunkNow();
                    return;
                }
                case 5 -> {
                    // Show World2Throne.png
                    showW2InterBg("/assets/Backgrounds/World2Throne.png");
                    typeW2InterChunkNow();
                    return;
                }
                case 6 -> {
                    // Stay on World2Throne.png
                    typeW2InterChunkNow();
                    return;
                }
                case 7 -> {
                    // Show World2Throne2.png
                    showW2InterBg("/assets/Backgrounds/World2Throne2.png");
                    typeW2InterChunkNow();
                    return;
                }
                case 8 -> {
                    // Show World2Throne3.png immediately, then type
                    showW2InterBg("/assets/Backgrounds/World2Throne3.png");
                    typeW2InterChunkNow();
                    return;
                }
                case 9 -> {
                    // Stay on World2Throne3.png
                    typeW2InterChunkNow();
                    return;
                }
            }
        }

        // Default fallback (e.g. inter 5 / king-victory / shop outro)
        // ── KING VICTORY / Magic Shop transition dialogue ─────────────────────
        if (interIdx == -1) {
            switch (w2InterChunkIndex) {
                case 0 -> {
                    // "FINAL VICTORY..." — TransitionMagicShop.png
                    showW2InterBg("/assets/Backgrounds/TransitionMagicShop.png");
                    typeW2InterChunkNow();
                    return;
                }
                case 1 -> {
                    // "His breath rattles..." — TransitionMagicShop2.png
                    showW2InterBg("/assets/Backgrounds/TransitionMagicShop2.png");
                    typeW2InterChunkNow();
                    return;
                }
                case 2 -> {
                    // "It's… It's you!!!" — TransitionMagicShop3.png
                    showW2InterBg("/assets/Backgrounds/TransitionMagicShop3.png");
                    typeW2InterChunkNow();
                    return;
                }
                case 3 -> {
                    // "He screams..." — TransitionMagicShop4.png
                    showW2InterBg("/assets/Backgrounds/TransitionMagicShop4.png");
                    typeW2InterChunkNow();
                    return;
                }
                case 4 -> {
                    // "He leaves behind only a pile of ash..." — stay on TransitionMagicShop4.png
                    typeW2InterChunkNow();
                    return;
                }
                case 5 -> {
                    // "You lift the SECOND STONE..." — TransitionMagicShop5 (2s) → TransitionMagicShop6
                    w2ContinueBtn.setEnabled(false);
                    showW2InterBg("/assets/Backgrounds/TransitionMagicShop5.png");
                    delay(2000, () -> {
                        showW2InterBg("/assets/Backgrounds/TransitionMagicShop6.png");
                        w2ContinueBtn.setEnabled(true);
                        typeW2InterChunkNow();
                    });
                    return;
                }
                case 6 -> {
                    // "The room falls into silence..." — TransitionMagicShop7.png
                    showW2InterBg("/assets/Backgrounds/TransitionMagicShop7.png");
                    typeW2InterChunkNow();
                    return;
                }
                case 7 -> {
                    // "As you step forward..." — TransitionMagicShop8.png
                    utils.SoundUtil.play("TeleportMagicShop.WAV");
                    showW2InterBg("/assets/Backgrounds/TransitionMagicShop8.png");
                    typeW2InterChunkNow();
                    return;
                }
                case 8 -> {
                    // "Something… or someone…" — TransitionMagicShop9.png
                    showW2InterBg("/assets/Backgrounds/TransitionMagicShop9.png");
                    typeW2InterChunkNow();
                    return;
                }
                case 9 -> {
                    // "A BRILLIANT FLASH..." — TransitionMagicShop10.png|
                    utils.SoundUtil.play("LightSound.WAV");
                    showW2InterBg("/assets/Backgrounds/TransitionMagicShop10.png");
                    typeW2InterChunkNow();
                    return;
                }
                case 10 -> {
                    // "From the shattered shadows..." — TransitionMagicShop11.png
                    showW2InterBg("/assets/Backgrounds/TransitionMagicShop11.png");
                    typeW2InterChunkNow();
                    return;
                }
                case 11 -> {
                    // "A calm, ancient voice echoes..." — TransitionMagicShop12.png
                    showW2InterBg("/assets/Backgrounds/TransitionMagicShop12.png");
                    typeW2InterChunkNow();
                    return;
                }
                case 12 -> {
                    // "I appear only to those..." — TransitionMagicShop13.png
                    showW2InterBg("/assets/Backgrounds/TransitionMagicShop13.png");
                    typeW2InterChunkNow();
                    return;
                }
            }
        }

        // ── INTER 5: after Magic Shop ─────────────────────────────────────────
        if (interIdx == 5) {
            switch (w2InterChunkIndex) {
                case 0 -> {
                    showW2InterBg("/assets/Backgrounds/World2TransitionWorld3_1.png");
                    typeW2InterChunkNow();
                    return;
                }
                case 1 -> {
                    showW2InterBg("/assets/Backgrounds/World2TransitionWorld3_2.png");
                    typeW2InterChunkNow();
                    return;
                }
                case 2 -> {
                    showW2InterBg("/assets/Backgrounds/World2TransitionWorld3_3.png");
                    typeW2InterChunkNow();
                    return;
                }
                case 3 -> {
                    // default black screen — clear the image
                    if (w2KhaiLabel != null) {
                        w2KhaiLabel.setIcon(null);
                        w2KhaiAlpha[0] = 0f;
                        w2KhaiLabel.putClientProperty("prevImage", null);
                        w2KhaiLabel.putClientProperty("prevAlpha", 0f);
                        w2KhaiLabel.repaint();
                    }
                    typeW2InterChunkNow();
                    return;
                }
            }
        }

        // Default fallback (e.g. inter 5 / shop outro)
        typeW2InterChunkNow();
    }

    /**
     * Resolves which WORLD2_INTER_DIALOGUES index the current w2InterChunks belongs to.
     * Returns -1 if not matched (e.g. king-victory dialogue).
     */
    private int resolveW2InterIndex() {
        if (w2InterChunks == null) return -1;
        for (int i = 0; i < WORLD2_INTER_DIALOGUES.length; i++) {
            if (w2InterChunks == WORLD2_INTER_DIALOGUES[i]) return i;
        }
        return -1;
    }

    /** Shows an image in the w2KhaiLabel layer (same as showW2SceneImage but for inter-dialogues). */
    private void showW2InterBg(String path) {
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

    /** Types the current w2InterChunks[w2InterChunkIndex] text with the typewriter effect. */
    private void typeW2InterChunkNow() {
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

        utils.SoundUtil.stopSFX(); // ★ ADD THIS — stops any one-shot sound before battle music starts

        int interIdx = resolveW2InterIndex();
        if (interIdx == 3) switchMusic("World2BlackJailer.WAV", 0.7f);
        else if (interIdx == 4) switchMusic("World2LutherSound.WAV", 0.7f);
        else playBattleMusic();

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
            // ── INTER 0: after Plague Vermin ─────────────────────────────────────
            {
                    // chunk 0 — PlagueVerminDead.png (auto-shown immediately)
                    "The last Vermin goes still.\nThe stench of rot clings to your clothes.",
                    // chunk 1 — PlagueVerminDead2.png
                    "The last Vermin goes still.\nThe stench of rot clings to your clothes.",
                    // chunk 2 — WanderAround.png
                    "You wander through the town's crumbling streets.\nThe air is thick with despair.",
                    // chunk 3 — World2Beggar.png (2s) → World2Beggar2.png
                    "You stop to help a beggar, offering a small kindness in a cruel world.\nHe grabs your wrist, his eyes wide with fear.",
                    // chunk 4 — World2Beggar3.png
                    "\"Beware the Black Castle,\" he rasps.\n\"The Corrupted King hoards the Second Stone there.\"",
                    // chunk 5 — still World2Beggar3.png
                    "\"But the real master... is the Necromancer.\"\n\"He is a phantom who rules from the shadows.\"",
                    // chunk 6 — WanderAround.png
                    "Heeding the beggar's warning, you make your way\nto a ruined chapel at the edge of town.",
                    // chunk 7 — World2Chapel.png
                    "Inside, the air hums with dark energy.",
                    // chunk 8 — World2Chapel2.png → continue → World2ForsakenCultist.png (2s) → World2ForsakenCultist2.png
                    "Two FORSAKEN CULTISTS stand before a defiled altar.\nThey turn slowly, their eyes glowing with fanatic light.",
            },
            // ── INTER 1: after Forsaken Cultists ────────────────────────────────
            {
                    // chunk 0 — ForsakenCultistDead.png (auto-shown immediately)
                    "The chanting dies.\nThe cultists crumple — their pact finally, mercifully broken.",
                    // chunk 1 — World2ExitChapel.png (2s) → World2AfterChapel.png
                    "You leave the ruined chapel and head toward the castle outskirts.\nThe air grows heavy with sulfur.",
                    // chunk 2 — image stays
                    "The silence that follows is not peaceful.\nSomething low and wet breathes in the dark ahead.",
                    // chunk 3 — World2BlighthoundSlihouette.png (2s) → World2BlightHoundEncounter.png
                    "Two shapes detach from the fog.\nBlight Hounds — hollow-eyed, driven by hunger and rot."
            },
            // ── INTER 2: after Blight Hounds ─────────────────────────────────────
            {
                    // chunk 0 — World2BlightHoundDefeated.png (2s) → World2BattleBlackCastleGate.png (auto)
                    "The Hounds collapse.\nTheir corruption bleeds into the mud beneath them.",
                    // chunk 1 — image stays
                    "You stand before the towering iron gates.\nThe metal is cold to the touch.",
                    // chunk 2 — World2BlackCastleDoor.png
                    "Thunder cracks overhead as you push open the heavy gates.",
                    // chunk 3 — World2BlackCastleInside.png
                    "Inside, the halls are silent except for the scrape of metal on stone.\nFigures lurch from the darkness — once knights, now twisted by plague.",
                    // chunk 4 — World2GhoulFootman.png
                    "Two GHOUL FOOTMEN emerge.\nTheir armor is cracked, their eyes bleed darkness."
            },
            // ── INTER 3: after Ghoul Footmen ─────────────────────────────────────
            {
                    // chunk 0 — World2GhoulFootmanDefeated.png (auto-shown immediately)
                    "The last Footman falls with a hollow clatter.\nThe silence after is heavy.",
                    // chunk 1 — World2BlackCastleDescend.png
                    "You descend into the castle's damp underbelly.\nThe air grows cold.",
                    // chunk 2 — World2BlackJailer.png
                    "The sound of dripping water is drowned out by the heavy dragging of iron.",
                    // chunk 3 — World2BlackJailer2.png
                    "Clank... Drag... Clank...",
                    // chunk 4 — World2BlackJailer3.png
                    "In the flickering torchlight, a massive figure blocks the path.\nThe Black Jailer steps from the shadows, his face hidden behind an iron mask.",
            },
            // ── INTER 4: after Black Jailer ───────────────────────────────────────
            {
                    // chunk 0 — World2BlackJailerDefeated.png (2s) → World2BlackJailerDefeated2.png (2s) → World2BlackJailerDefeated3.png (auto)
                    "The Black Jailer drops to his knees.\nHis iron mask falls away to reveal nothing but ash.",
                    // chunk 1 — stay on World2BlackJailerDefeated3.png
                    "The chains that bound the dungeon fall silent.\nYou have broken his tyranny.",
                    // chunk 2 — World2Key.png (2s) → World2Key2.png
                    "The chains that bound the dungeon fall silent.\nYou have broken his tyranny.",
                    // chunk 3 — World2Ascend.png
                    "You ascend the spiral staircase.\nThe air grows thin and smells of ancient dust.",
                    // chunk 4 — World2ThroneDoor.png
                    "At the top, the massive doors to the Throne Room stand slightly a far.",
                    // chunk 5 — World2Throne.png
                    "In the center of the room, on a throne of jagged iron, sits the King.\nHe is slumped forward, his body fused to the chair by the corruption.",
                    // chunk 6 — stay on World2Throne.png
                    "Embedded in his rusted crown, pulsating with a sickly green light, is the SECOND STONE.",
                    // chunk 7 — World2Throne2.png
                    "The King slowly lifts his head.\nHis eyes are hollow voids.",
                    // chunk 8 — World2Throne3.png
                    "\"YOU DARE CHALLENGE MY AUTHORITY?!\"",
                    // chunk 9 — stay on World2Throne3.png
                    "\"YOUR SKULL WILL BECOME BUT ANOTHER TROPHY IN MY HALLS!\""
            },
            // ── INTER 5: after Magic Shop ─────────────────────────────────────────
            {
                    "🌟 The glow of the Magic Shop fades, leaving only silence behind.",
                    "The doorway vanishes as suddenly as it appeared.\nYou stand alone in the quiet halls of the castle.",
                    "Whatever choices you made within… will echo in the battles to come.\nThe castle seems to hold its breath.",
                    "The choices you've made, the treasures you've claimed…\nall will shape the path ahead."
            },
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
            "You wake up gasping for air. The world is drained of color.",
            "You are lying on a bed of gray moss in a dead forest.\nThe trees are skeletal giants, stripped to bone-white wood.",
            "A cold mist coils around your ankles,\nand silence presses from every side watching, waiting.",
            "A heavy bell tolls in the distance...\n\"Dong... Dong...\"",
            "From the mist steps a figure cloaked in tattered robes.\nHe leans heavily on a staff. As he lifts his hood, you jolt back,\nthe face is familiar. It looks exactly like your professor, Khai.",
            "But his eyes are weary, holding the weight of centuries.",
            "\"Be calm, Traveler. In this realm, I am known as Khai the Gray.\"\n" +
                    "\"We suffer because an evil Necromancer has corrupted these lands.\n" +
                    "He has drained the nature itself.\"",
            "\"We must find the Three Stones of Life that hold this reality together.\n" +
                    "Only then will your path home reveal itself.\"",
            "Khai fades back into the mist.",
            "Three Rodtfang Wolves emerge from the tree line.",
            "Their glowing red eyes——fixate on you.",
            "They do not hunt for food — they hunt to kill."
    };

    // ─── WORLD 3 DIALOGUES ──────────────────────────────────────────────
    private static final String[] WORLD3_DIALOGUES = {
            "You have been travelling for days, leaving the green world far behind.\n" +
                    "You have reached a land where not even a glimmer of life can survive.",         // index 0 — BG1 shown on entry
            "The earth here has turned to black glass. Ash falls like snow, coating your armor in gray dust.\n" +
                    "Rivers of molten fire carve through the rock.", // index 1 — show BG2 before this
            "At the center of this desolation, rising higher than the mountains...\n" +
                    "Stands a spire of twisted obsidian, piercing the storm itself.\n" +
                    "THE NECROMANCER'S TOWER.",                                                      // index 2 — show BG3→BG4 before this
            "\"We are here,\" Khai whispers, his voice barely audible over the roaring wind.\n" +
                    "\"The source of the rot. The end of the path.\"",                               // index 3 — show BG5→BG6 before this
            "The ground beneath you becomes uncomfortably hot. The cracks in the rock begin to glow.",  // index 4 — BG6 stays
            "From the fire, shape-less forms pull themselves together.\n" +
                    "FLAME REVENANTS rise, their bodies flickering with ember and hatred.\n" +
                    "They scream without mouths, a sound like burning timber."                       // index 5 — show BG7→BG8 before this
    };

    private static final String[][] WORLD3_INTER_DIALOGUES = {
            // ── INTER 0: after Flame Revenants ──────────────────────────────────────
            {
                    // chunk 0 — BG10 shown immediately
                    "You steel yourself and look up at the Tower.",
                    // chunk 1 — BG11 (2s) → BG12
                    "You begin the ascent. The air thickens with suffocating magic.\n" +
                            "Each step you take hums with a pulse from the Stones you carry, as if they are calling out.",
                    // chunk 2 — BG13
                    "The air grows cold, despite the rivers of lava flowing nearby.\n" +
                            "A hollow chanting fills the chamber, vibrating in your bones.",
                    // chunk 3 — BG14
                    "From the shadows of the obsidian pillars, figures draped in tattered robes emerge." +
                            " BONE WARLOCKS.",
                    // chunk 4 — BG15.5 (battle bg set separately)
                    "They raise staffs made of spine and skull, chanting forbidden incantations\n" +
                            "to twist the very life force from your body."
            },
            // ── INTER 1: after Bone Warlocks ─────────────────────────────────────────
            {
                    // chunk 0 — BG15 (2s) → BG16 (2s) → BG17 (2s) → BG18 (2s) → BG19
                    "A deep, rhythmic thumping echoes through the cavern. Boom... Boom...\n" +
                            "Lava geysers burst upward, spraying molten rock against the walls.",
                    // chunk 1 — BG20
                    "Massive shadows rise from behind the curtain of fire.\n" +
                            "OBSIDIAN CRUSHERS emerge — molten giants forged from living stone and fury.",
                    // chunk 2 — BG21
                    "Their skin is black rock, their veins flow with lava,\n" +
                            "and they look at you as nothing more than dust to be swept away."
            },
            // ── INTER 2: after Obsidian Crushers ─────────────────────────────────────
            {
                    // chunk 0 — BG22
                    "Halfway up the winding stairs, you find something etched into the obsidian wall.\n" +
                            "It is a mural, ancient and jagged.",
                    // chunk 1 — BG22 stays
                    "It shows a hooded figure holding three glowing stones high above a kneeling crowd." +
                            " Beneath it, carved in a language that looks chillingly familiar, is a single phrase: " +
                            "\"TO TEACH IS TO CONTROL.\"",
                    // chunk 2 — BG23
                    "A shiver runs down your spine that has nothing to do with the cold.\n" +
                            "You climb higher into the spire. The air grows thin and impossibly cold.",
                    // chunk 3 — BG24 (2s) → BG25
                    "Suddenly, stone cracks with a sharp snap!\n" +
                            "Perched on the obsidian ledges above, grim stone statues shed their rocky skin and shriek as they dive.",
                    // chunk 4 — BG26
                    "SOULFLAYER GARGOYLES take flight.\n" +
                            "Their wings block out the red lightning, and their eyes burn with hunger for the living."
            },
            // ── INTER 3: after Soulflayer Gargoyles ───────────────────────────────────
            {
                    // chunk 0 — BG28
                    "You reach the penultimate landing. The heat here is unbearable.\n" +
                            "The stone beneath your boots is soft, almost melting.",
                    // chunk 1 — BG29
                    "A towering figure steps from the magma falls blocking the path.\n" +
                            "ZYRRYL, Warden of the Shattered Tower.",
                    // chunk 2 — BG29 stays
                    "His armor is forged from cursed steel and hardened lava.\n" +
                            "He drags a massive greatsword that glows white-hot."
            }
    };

    private static final String[] KHAI_BETRAYAL_DIALOGUE = {
            // index 0 — BG30
            "With a heavy crash, Zyrryl, the Tower Warden, falls to the ground.\n" +
                    "You catch your breath. You hold the final Stone of Life.",
            // index 1 — BG31 (2s) → BG32
            "Sir Khai steps forward. His staff is no longer wood—it is blazing with chaotic energy." +
                    "\"Finally.\"",
            // index 2 — BG33
            "\"You've served well, my student.\n" +
                    "Who better to collect the Stones of Life than one who trusts their teacher blindly?\"",
            // index 3 — BG34
            "\"I have guided you not to save this land... but to claim its power.\n" +
                    "I have been waiting for a vessel like you for a millennium.\"",
            // index 4 — BG35
            "The air around him turns black. His weary eyes are gone, replaced by burning voids.",
            // index 5 — BG35 stays
            "\"I wish to bring chaos not only to this land, but to all lands beyond.\n" +
                    "The Necromancer you sought... The one who brings the end of worlds...\"",
            // index 6 — BG36, RED text + shake
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
        w1DialogueBgLabel.setBounds(-15, 468, 1053, 300);
        java.net.URL w1DbUrl = getClass().getResource("/assets/GUIButtons/DialogueBox.png");
        if (w1DbUrl != null) {
            Image w1DbScaled = new ImageIcon(w1DbUrl).getImage().getScaledInstance(990, 180, Image.SCALE_SMOOTH);
            w1DialogueBgLabel.setIcon(new ImageIcon(w1DbScaled));
        }

        w1DialogueBox = new JTextArea();
        w1DialogueBox.setBounds(2, 541, 900, 100);
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
                964, 554, 140, 64, "Continue");
        w1ContinueBtn.addActionListener(e -> continueW1Dialogue());

        JButton w1MenuBtn = createImageButton(
                "/assets/GUIButtons/Menu.png", "/assets/GUIButtons/MenuHover.png",
                1103, 555, 148, 58, "Menu", 0);
        JButton w1BackBtn = createImageButton(
                "/assets/GUIButtons/Save.png", "/assets/GUIButtons/SaveHover.png",
                962, 613, 145, 50, "Back", 0);
        JButton w1ExitBtn = createImageButton(
                "/assets/GUIButtons/Exit.png", "/assets/GUIButtons/ExitHover.png",
                1104, 613, 145, 50, "Exit", 0);
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
        if (w1DialogueIndex == 3) utils.SoundUtil.play("BellSound.WAV");

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

        if (w1DialogueIndex == 2 && w1WorldLabel != null) {
            fadeW1Label();
            delay(1000, () -> crossfadeW1ToKhai("/assets/Backgrounds/SilhouetteSirKhai.png", () -> {}));
        }

        if (w1DialogueIndex == 3) {
            transitionW1Scene("/assets/Backgrounds/SilhouetteSirKhai2.jpg");
        }

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

        if (w1DialogueIndex == 7) {
            w1ContinueBtn.setEnabled(false);
            utils.SoundUtil.play("KhaiVanishSound.WAV");
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
                        w1DialogueIndex = 8;
                        w1ContinueBtn.setEnabled(true);
                        startW1Typing();
                    });
                });
            });
            return;
        }

        if (w1DialogueIndex == 8) {
            w1ContinueBtn.setEnabled(false);
            utils.SoundUtil.play("BushSound.WAV");
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
            if (w1DialogueIndex == 9)  utils.SoundUtil.play("WolfEncounter.wav");  // ★ "Three wolves emerge"
            if (w1DialogueIndex == 10) utils.SoundUtil.play("WolfGlaring.WAV");     // ★ "Glowing red eyes"
        } else {
            w1DialogueIndex = 0;
            goToBattle();
        }

        if (w1DialogueIndex == 11) utils.SoundUtil.play("WolfAttack.wav"); // ★ "They hunt to kill"
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

        w2WorldLabel = new JLabel("WORLD 2 : THE DECAYING TOWN ", SwingConstants.CENTER);
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
        w2DialogueBgLabel.setBounds(-15, 468, 1053, 300);
        java.net.URL w2DbUrl = getClass().getResource("/assets/GUIButtons/DialogueBox.png");
        if (w2DbUrl != null) {
            w2DialogueBgLabel.setIcon(new ImageIcon(
                    new ImageIcon(w2DbUrl).getImage().getScaledInstance(990, 180, Image.SCALE_SMOOTH)));
        }

        w2DialogueBox = new JTextArea();
        w2DialogueBox.setBounds(2, 541, 900, 100);
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
                964, 554, 140, 64, "Continue");
        w2ContinueBtn.addActionListener(e -> continueW2Dialogue());

        JButton w2MenuBtn = createImageButton(
                "/assets/GUIButtons/Menu.png", "/assets/GUIButtons/MenuHover.png",
                1103, 555, 148, 58, "Menu", 0);
        JButton w2BackBtn = createImageButton(
                "/assets/GUIButtons/Save.png", "/assets/GUIButtons/SaveHover.png",
                962, 613, 145, 50, "Back", 0);
        JButton w2ExitBtn = createImageButton(
                "/assets/GUIButtons/Exit.png", "/assets/GUIButtons/ExitHover.png",
                1104, 613, 145, 50, "Exit", 0);
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
                // Always fill black first — covers TheBackground.png
                g2.setColor(Color.BLACK);
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
        w3Scene.setOpaque(true);   // ← change false to true
        w3Scene.setBackground(Color.BLACK);

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
        w3DialogueBgLabel.setBounds(-15, 468, 1053, 300);
        java.net.URL w3DbUrl = getClass().getResource("/assets/GUIButtons/DialogueBox.png");
        if (w3DbUrl != null) {
            w3DialogueBgLabel.setIcon(new ImageIcon(
                    new ImageIcon(w3DbUrl).getImage().getScaledInstance(990, 180, Image.SCALE_SMOOTH)));
        }

        w3DialogueBox = new JTextArea();
        w3DialogueBox.setBounds(2, 541, 900, 100);
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
                964, 554, 140, 64, "Continue");
        w3ContinueBtn.addActionListener(e -> continueW3Dialogue());

        JButton w3MenuBtn = createImageButton(
                "/assets/GUIButtons/Menu.png", "/assets/GUIButtons/MenuHover.png",
                1103, 555, 148, 58, "Menu", 0);
        JButton w3BackBtn = createImageButton(
                "/assets/GUIButtons/Save.png", "/assets/GUIButtons/SaveHover.png",
                962, 613, 145, 50, "Back", 0);
        JButton w3ExitBtn = createImageButton(
                "/assets/GUIButtons/Exit.png", "/assets/GUIButtons/ExitHover.png",
                1104, 613, 145, 50, "Exit", 0);
        w3ExitBtn.addActionListener(e -> System.exit(0));

        layeredPane.setBounds(0, 0, 1280, 720);
        layeredPane.add(w3TheBg,           JLayeredPane.FRAME_CONTENT_LAYER);
        layeredPane.add(w3Scene,           JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(w3KhaiPanel,       JLayeredPane.PALETTE_LAYER);
        layeredPane.add(w3WorldLabel,      JLayeredPane.MODAL_LAYER);      // ← raise this
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
        utils.SoundUtil.stopLoop();
        utils.SoundUtil.playLoop("World3BackgroundMusic.WAV", 0.5f);
        currentWorld = 3;
        w3DialogueIndex = 0;

        if (w3WorldLabel != null) {
            w3WorldLabel.setVisible(true);
            w3WorldLabel.setForeground(Color.WHITE);
        }
        if (w3KhaiLabel != null) {
            java.net.URL bg1url = getClass().getResource("/assets/Backgrounds/World3BG1.png");
            if (bg1url != null) {
                w3KhaiLabel.setIcon(new ImageIcon(
                        new ImageIcon(bg1url).getImage().getScaledInstance(1280, 520, Image.SCALE_SMOOTH)));
            }
            w3KhaiAlpha[0] = 0f;
            w3KhaiLabel.putClientProperty("prevImage", null);
            w3KhaiLabel.putClientProperty("prevAlpha", 0f);
        }
        if (w3SceneAlpha != null) w3SceneAlpha[0] = 0f;
        if (w3WorldAlpha != null) w3WorldAlpha[0] = 0f;
        if (w3SceneBg != null) {
            w3SceneBg.setOpaque(true);
            w3SceneBg.setBackground(Color.BLACK);
            w3SceneBg.repaint();
        }

        cardLayout.show(cardPanel, SCREEN_WORLD3_INTRO);

        // Hold on black + label for 1.5s, then fade in BG1
        delay(1500, () -> {
            Timer fadeIn = new Timer(16, null);
            fadeIn.addActionListener(ev -> {
                if (w3KhaiLabel == null) { fadeIn.stop(); return; }
                w3KhaiAlpha[0] = Math.min(1f, w3KhaiAlpha[0] + 0.008f);
                w3KhaiLabel.repaint();
                w3SceneBg.repaint();
                if (w3KhaiAlpha[0] >= 1f) {
                    fadeIn.stop();
                    // Once BG1 fully fades in, fade out the label then start typing
                    fadeW3Label();
                    delay(1000, () -> startW3Typing());
                }
            });
            fadeIn.start();
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

        if (w3DialogueIndex == 5) {
            utils.SoundUtil.play("BurningSound.WAV");
        }

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

        if (w3DialogueIndex == 1) {
            // After "earth turned to black glass" — show BG2 then type dialogue[2]
            showW3SceneImage("/assets/Backgrounds/World3BG2.png");
        } else if (w3DialogueIndex == 2) {
            // After "THE NECROMANCER'S TOWER" — BG3 2s → BG4 2s then type dialogue[3]
            w3ContinueBtn.setEnabled(false);
            showW3SceneImage("/assets/Backgrounds/World3BG3.png");
            delay(2000, () -> {
                showW3SceneImage("/assets/Backgrounds/World3BG4.png");
                delay(2000, () -> {
                    w3DialogueIndex = 3;
                    w3ContinueBtn.setEnabled(true);
                    startW3Typing();
                });
            });
            return;
        } else if (w3DialogueIndex == 3) {
            // After "We are here" — BG5 2s then type dialogue[4]
            w3ContinueBtn.setEnabled(false);
            showW3SceneImage("/assets/Backgrounds/World3BG5.png");
            delay(2000, () -> {
                w3DialogueIndex = 4;
                w3ContinueBtn.setEnabled(true);
                startW3Typing();
            });
            return;
        } else if (w3DialogueIndex == 4) {
            // After "cracks in the rock begin to glow" — BG7 2s → BG8 then type dialogue[5]
            w3ContinueBtn.setEnabled(false);
            showW3SceneImage("/assets/Backgrounds/World3BG7.png");
            delay(2000, () -> {
                showW3SceneImage("/assets/Backgrounds/World3BG8.png");
                w3DialogueIndex = 5;
                w3ContinueBtn.setEnabled(true);
                startW3Typing();
            });
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
        playWorldMusic();
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

        int interIdx = resolveW3InterIndex();

        // ── INTER 0: after Flame Revenants ───────────────────────────────────────
        if (interIdx == 0) {
            switch (w3InterChunkIndex) {
                case 0 -> { showW3InterBg("/assets/Backgrounds/World3BG10.png"); typeW3InterChunkNow(); return; }
                case 1 -> {
                    w3ContinueBtn.setEnabled(false);
                    showW3InterBg("/assets/Backgrounds/World3BG11.png");
                    delay(2000, () -> {
                        showW3InterBg("/assets/Backgrounds/World3BG12.png");
                        w3ContinueBtn.setEnabled(true);
                        typeW3InterChunkNow();
                    });
                    return;
                }
                case 2 -> { showW3InterBg("/assets/Backgrounds/World3BG13.png"); typeW3InterChunkNow(); return; }
                case 3 -> { showW3InterBg("/assets/Backgrounds/World3BG14.png"); typeW3InterChunkNow(); return; }
                case 4 -> { /* BG14 stays for battle lead-in */ typeW3InterChunkNow(); return; }
            }
        }

        // ── INTER 1: after Bone Warlocks ─────────────────────────────────────────
        if (interIdx == 1) {
            switch (w3InterChunkIndex) {
                case 0 -> {
                    // BG15 → BG16 → BG17 → BG18 → BG19 timed sequence then type
                    w3ContinueBtn.setEnabled(false);
                    showW3InterBg("/assets/Backgrounds/World3BG15.png");
                    delay(2000, () -> {
                        showW3InterBg("/assets/Backgrounds/World3BG16.png");
                        delay(2000, () -> {
                            showW3InterBg("/assets/Backgrounds/World3BG17.png");
                            delay(2000, () -> {
                                showW3InterBg("/assets/Backgrounds/World3BG18.png");
                                delay(2000, () -> {
                                    delay(1500, () -> utils.SoundUtil.play("ExplosionSound.WAV"));
                                    showW3InterBg("/assets/Backgrounds/World3BG19.png");
                                    w3ContinueBtn.setEnabled(true);
                                    typeW3InterChunkNow();
                                });
                            });
                        });
                    });
                    return;
                }
                case 1 -> { showW3InterBg("/assets/Backgrounds/World3BG20.png"); typeW3InterChunkNow(); return; }
                case 2 -> { showW3InterBg("/assets/Backgrounds/World3BG21.png"); typeW3InterChunkNow(); return; }
            }
        }

        // ── INTER 2: after Obsidian Crushers ─────────────────────────────────────
        if (interIdx == 2) {
            switch (w3InterChunkIndex) {
                case 0 -> { showW3InterBg("/assets/Backgrounds/World3BG22.png"); typeW3InterChunkNow(); return; }
                case 1 -> { /* BG22 stays */ typeW3InterChunkNow(); return; }
                case 2 -> { showW3InterBg("/assets/Backgrounds/World3BG23.png"); typeW3InterChunkNow(); return; }
                case 3 -> {
                    w3ContinueBtn.setEnabled(false);
                    utils.SoundUtil.play("CrackingSound.WAV");
                    showW3InterBg("/assets/Backgrounds/World3BG24.png");
                    delay(2000, () -> {
                        showW3InterBg("/assets/Backgrounds/World3BG25.png");
                        w3ContinueBtn.setEnabled(true);
                        typeW3InterChunkNow();
                    });
                    return;
                }
                case 4 -> { showW3InterBg("/assets/Backgrounds/World3BG26.png"); typeW3InterChunkNow(); return; }
            }
        }

        // ── INTER 3: after Soulflayer Gargoyles ──────────────────────────────────
        if (interIdx == 3) {
            switch (w3InterChunkIndex) {
                case 0 -> { showW3InterBg("/assets/Backgrounds/World3BG28.png"); typeW3InterChunkNow(); return; }
                case 1 -> { showW3InterBg("/assets/Backgrounds/World3BG29.png"); typeW3InterChunkNow(); return; }
                case 2 -> { /* BG29 stays */ typeW3InterChunkNow(); return; }
            }
        }

        typeW3InterChunkNow();
    }


    private void finishW3InterDialogue() {
        w3InInterDialogue = false;
        w3DialogueBox.setText("");
        Runnable resume = w3ResumeAfterDialogue;
        w3ResumeAfterDialogue = null;
        int interIdx = resolveW3InterIndex();
        String battleBg = switch (interIdx) {
            case 0 -> "/assets/Backgrounds/World3BG15.5.png";
            case 1 -> "/assets/Backgrounds/World3BG21.5.png";
            case 2 -> "/assets/Backgrounds/World3BG27.png";
            case 3 -> "/assets/Backgrounds/World3BG30.5.png";
            default -> "/assets/Backgrounds/World3BG9.png";
        };
        battlePanel.setBattleBackground(battleBg);
        utils.SoundUtil.stopSFX(); // ★ ADD THIS
        if (interIdx == 3) switchMusic("ZyrrylSound.WAV", 0.7f);
        else playBattleMusic();
        cardLayout.show(cardPanel, SCREEN_BATTLE);
        if (resume != null) resume.run();
    }

    /** Shows an image in the w3KhaiLabel layer for main dialogue scenes. */
    private void showW3SceneImage(String path) {
        if (w3KhaiLabel == null) return;
        java.net.URL url = getClass().getResource(path);
        if (url == null) return;
        w3KhaiLabel.setIcon(new ImageIcon(
                new ImageIcon(url).getImage().getScaledInstance(1280, 520, Image.SCALE_SMOOTH)));
        w3KhaiLabel.putClientProperty("prevImage", null);
        w3KhaiLabel.putClientProperty("prevAlpha", 0f);
        w3KhaiAlpha[0] = 1.0f;
        w3KhaiLabel.repaint();
    }

    /** Types the current w3InterChunks[w3InterChunkIndex] text with the typewriter effect. */
    private void typeW3InterChunkNow() {
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

    /** Shows an image in the w3KhaiLabel layer for inter-dialogue scenes. */
    private void showW3InterBg(String path) {
        if (w3KhaiLabel == null) return;
        java.net.URL url = getClass().getResource(path);
        if (url == null) return;
        w3KhaiLabel.setIcon(new ImageIcon(
                new ImageIcon(url).getImage().getScaledInstance(1280, 520, Image.SCALE_SMOOTH)));
        w3KhaiLabel.putClientProperty("prevImage", null);
        w3KhaiLabel.putClientProperty("prevAlpha", 0f);
        w3KhaiAlpha[0] = 1.0f;
        w3KhaiLabel.repaint();
    }

    /** Resolves which WORLD3_INTER_DIALOGUES index w3InterChunks belongs to. Returns -1 if not matched. */
    private int resolveW3InterIndex() {
        if (w3InterChunks == null) return -1;
        for (int i = 0; i < WORLD3_INTER_DIALOGUES.length; i++) {
            if (w3InterChunks == WORLD3_INTER_DIALOGUES[i]) return i;
        }
        return -1;
    }

    private void startFinalBossTransition() {
        switchMusic("KhaiBetrayal.WAV", 0.7f);
        cardLayout.show(cardPanel, SCREEN_WORLD3_INTRO);

        if (w3SceneBg != null) {
            w3SceneBg.setBackground(Color.BLACK);
            w3SceneBg.setOpaque(true);
            w3SceneBg.repaint();
        }
        if (w3WorldLabel != null) w3WorldLabel.setVisible(false);

        w3ContinueBtn.setEnabled(false);

        final int[] idx = {0};
        Runnable[] typeNext = {null};

        typeNext[0] = () -> {
            if (idx[0] >= KHAI_BETRAYAL_DIALOGUE.length) {
                w3ContinueBtn.setEnabled(false);
                switchMusic("FinalBossBattleMusic.WAV", 0.8f);
                cardLayout.show(cardPanel, SCREEN_BATTLE);
                battlePanel.startEnemySequence(
                        confirmedHero,
                        HeroData.FINAL_BOSS_SEQUENCE,
                        this::showKhaiVictoryDialogue
                );
                return;
            }

            switch (idx[0]) {
                case 0 -> showW3SceneImage("/assets/Backgrounds/World3BG30.png");
                case 1 -> {
                    // BG31 for 2s then BG32, then type line 1
                    showW3SceneImage("/assets/Backgrounds/World3BG31.png");
                    idx[0]++;  // advance to 2 NOW before the delay
                    delay(2000, () -> {
                        showW3SceneImage("/assets/Backgrounds/World3BG32.png");
                        typeKhaiLine(1, typeNext[0]);  // hardcode line 1
                    });
                    return;
                }
                case 2 -> showW3SceneImage("/assets/Backgrounds/World3BG33.png");
                case 3 -> showW3SceneImage("/assets/Backgrounds/World3BG34.png");
                case 4 -> showW3SceneImage("/assets/Backgrounds/World3BG35.png");
                case 5 -> { /* BG35 stays */ }
                case 6 -> showW3SceneImage("/assets/Backgrounds/World3BG36.png");
            }

            typeKhaiLine(idx[0]++, typeNext[0]);
        };

        typeNext[0].run();
    }

    private void showKhaiVictoryDialogue() {
        switchMusic("BeforeBGMusic.wav", 0.5f);
        cardLayout.show(cardPanel, SCREEN_WORLD3_INTRO);
        if (w3SceneBg != null) {
            w3SceneBg.setBackground(Color.BLACK);
            w3SceneBg.setOpaque(true);
            w3SceneBg.repaint();
        }
        if (w3WorldLabel != null) w3WorldLabel.setVisible(false);
        if (w3KhaiLabel != null) {
            w3KhaiLabel.setIcon(null);
            w3KhaiAlpha[0] = 0f;
            w3KhaiLabel.putClientProperty("prevImage", null);
            w3KhaiLabel.putClientProperty("prevAlpha", 0f);
            w3KhaiLabel.repaint();
        }

        showW3SceneImage("/assets/Backgrounds/W3Epilogue1.png");
        typeVictoryLine(0, () -> {
            w3ContinueBtn.setEnabled(false);
            String[] autoSeq = {
                    "/assets/Backgrounds/W3Epilogue2.png",
                    "/assets/Backgrounds/W3Epilogue3.png",
                    "/assets/Backgrounds/W3Epilogue4.png",
                    "/assets/Backgrounds/W3Epilogue5.png",
                    "/assets/Backgrounds/W3Epilogue6.png",
                    "/assets/Backgrounds/W3Epilogue7.png",
                    "/assets/Backgrounds/W3Epilogue8.png",
                    "/assets/Backgrounds/W3Epilogue9.png",
                    "/assets/Backgrounds/W3Epilogue10.png",
            };
            final int[] si = {0};
            Runnable[] stepSeq = {null};
            stepSeq[0] = () -> {
                if (si[0] < autoSeq.length) {
                    showW3SceneImage(autoSeq[si[0]++]);
                    delay(2000, stepSeq[0]);
                } else {
                    showW3SceneImage("/assets/Backgrounds/W3Epilogue11.png");
                    w3DialogueBox.setText("");
                    w3ContinueBtn.setEnabled(true);
                    for (ActionListener l : w3ContinueBtn.getActionListeners())
                        w3ContinueBtn.removeActionListener(l);
                    w3ContinueBtn.addActionListener(new ActionListener() {
                        @Override public void actionPerformed(ActionEvent e) {
                            w3ContinueBtn.removeActionListener(this);
                            w3ContinueBtn.setEnabled(false);
                            showW3SceneImage("/assets/Backgrounds/W3Epilogue12.png");
                            w3DialogueBox.setText("");
                            w3ContinueBtn.setEnabled(true);
                            for (ActionListener l2 : w3ContinueBtn.getActionListeners())
                                w3ContinueBtn.removeActionListener(l2);
                            w3ContinueBtn.addActionListener(new ActionListener() {
                                @Override public void actionPerformed(ActionEvent e2) {
                                    w3ContinueBtn.removeActionListener(this);
                                    w3ContinueBtn.setEnabled(false);
                                    typeVictoryLine(3, () ->
                                            typeVictoryLine(4, () ->
                                                    typeVictoryLine(5, () -> {
                                                        w3ContinueBtn.setEnabled(false);
                                                        showW3SceneImage("/assets/Backgrounds/W3Epilogue13.png");
                                                        delay(2000, () -> {
                                                            showW3SceneImage("/assets/Backgrounds/W3Epilogue14.png");
                                                            typeVictoryLine(6, () -> System.exit(0));
                                                        });
                                                    })
                                            )
                                    );
                                }
                            });
                        }
                    });
                }
            };
            stepSeq[0].run();
        });
    }

    private static final String[] KHAI_VICTORY_DIALOGUE = {
            // index 0 — Epilogue1 shown immediately, then type this
            "With your last strike, Khai staggers.\n" +
                    "His form unravels into smoke and stars.",
            // index 1 — Epilogue2→3→4→5→6→7→8→9→10 auto-sequence, then Epilogue11, then type this (no text, just continue trigger)
            "",
            // index 2 — Epilogue12 shown, then type this
            "",
            // index 3 — type this
            "You jolt awake.\n" +
                    "You're back in the lab. The CodeChum exam screen stares back at you.",
            // index 4
            "The timer blinks: 00:00:01.\n" +
                    "The exam has already ended.",
            // index 5
            "You conquered a world, fought betrayal, toppled a king —\n" +
                    "yet here, in reality, you didn't even answer a single problem.",
            // index 6 — Epilogue13 2s → Epilogue14, then type this
            "And for a split second, when you glance at your professor across the room...\n" +
                    "you swear his eyes flash violet.",
    };

    /** Types one line of the Khai betrayal dialogue then enables the continue button. */
    private void typeKhaiLine(int lineIdx, Runnable onContinue) {
        typeFromArray(KHAI_BETRAYAL_DIALOGUE, lineIdx, onContinue);
    }

    private void typeVictoryLine(int lineIdx, Runnable onContinue) {
        typeFromArray(KHAI_VICTORY_DIALOGUE, lineIdx, onContinue);
    }

    private void typeFromArray(String[] source, int lineIdx, Runnable onContinue) {
        if (lineIdx >= source.length) return;
        String text = source[lineIdx];
        w3DialogueBox.setText("");
        w3DialogueBox.setForeground(Color.WHITE);
        try {
            java.io.InputStream fs = getClass().getResourceAsStream("/assets/AssetFont/Pixelari.ttf");
            if (fs != null) w3DialogueBox.setFont(Font.createFont(Font.TRUETYPE_FONT, fs).deriveFont(Font.BOLD, 19f));
        } catch (Exception ignored) {}

        // Special red shake for betrayal line index 6 only
        if (source == KHAI_BETRAYAL_DIALOGUE && lineIdx == 6) {
            w3DialogueBox.setForeground(Color.RED);
            try { w3DialogueBox.setFont(w3DialogueBox.getFont().deriveFont(28f)); } catch (Exception ignored) {}
            Point origin = getLocation();
            Timer shakeTimer = new Timer(50, ev -> {
                int dx = (Math.random() > 0.5 ? 5 : -5);
                int dy = (Math.random() > 0.5 ? 5 : -5);
                setLocation(origin.x + dx, origin.y + dy);
            });
            shakeTimer.start();
            delay(1000, () -> { shakeTimer.stop(); setLocation(origin); });
        }

        int[] ci = {0};
        if (w3TypingTimer != null && w3TypingTimer.isRunning()) w3TypingTimer.stop();
        final Runnable next = onContinue;
        w3TypingTimer = new Timer(35, e -> {
            if (ci[0] < text.length()) {
                w3DialogueBox.append(String.valueOf(text.charAt(ci[0]++)));
            } else {
                w3TypingTimer.stop();
                w3ContinueBtn.setEnabled(true);
                for (ActionListener l : w3ContinueBtn.getActionListeners()) w3ContinueBtn.removeActionListener(l);
                w3ContinueBtn.addActionListener(ev -> {
                    w3ContinueBtn.setEnabled(false);
                    for (ActionListener l : w3ContinueBtn.getActionListeners()) w3ContinueBtn.removeActionListener(l);
                    if (next != null) next.run();
                });
            }
        });
        w3TypingTimer.start();
    }

    public void loadSavedGame(GameGUI.model.entity.Combatant loadedHero, GameGUI.model.system.SaveData data) {
        if (typingTimer != null) typingTimer.stop();

        for (GameGUI.model.entity.HeroData.HeroDefinition def : GameGUI.model.entity.HeroData.HEROES) {
            if (def.name.equals(loadedHero.name)) {
                this.confirmedHero = def;
                break;
            }
        }

        this.currentWorld = data.currentWorld;
        battlePanel.setCurrentHero(loadedHero);
        utils.SoundUtil.stopLoop();
        cardLayout.show(cardPanel, SCREEN_BATTLE);

        // ★ FIX: Wire up inter-enemy dialogues, same as goToBattle()
        battlePanel.setOnEnemyGroupDefeated((nextGroupIndex, resumeFight) -> {
            if (currentWorld == 1) {
                showInterEnemyDialogue(nextGroupIndex - 1, resumeFight);
            } else if (currentWorld == 2) {
                if (nextGroupIndex == 1) {
                    battlePanel.setBattleBackground("/assets/Backgrounds/World2BattleBackground2.png");
                }
                showWorld2InterDialogue(nextGroupIndex - 1, resumeFight);
            } else if (currentWorld == 3) {
                showWorld3InterDialogue(nextGroupIndex - 1, resumeFight);
            } else {
                resumeFight.run();
            }
        });

        if (currentWorld == 1) {
            utils.SoundUtil.playLoop("World1BGMusic.wav", 0.5f);
            battlePanel.resumeEnemySequence(
                    confirmedHero,
                    GameGUI.model.entity.HeroData.WORLD1_ENEMIES,
                    data.savedEnemySequenceIndex,
                    data.savedEnemyFightIndex,
                    () -> {
                        playWorldMusic();
                        startWorld2Transition();
                    }
            );
            restoreEnemyHp(data);

        } else if (currentWorld == 2) {
            utils.SoundUtil.playLoop("World2BGMusic.wav", 0.5f);
            battlePanel.resumeEnemySequence(
                    confirmedHero,
                    GameGUI.model.entity.HeroData.WORLD2_ENEMIES,
                    data.savedEnemySequenceIndex,
                    data.savedEnemyFightIndex,
                    () -> {
                        playWorldMusic();
                        showKingVictoryDialogue();
                    }
            );
            restoreEnemyHp(data);

        } else if (currentWorld == 3) {
            utils.SoundUtil.playLoop("World3BGMusic.wav", 0.5f);
            battlePanel.resumeEnemySequence(
                    confirmedHero,
                    GameGUI.model.entity.HeroData.WORLD3_ENEMIES,
                    data.savedEnemySequenceIndex,
                    data.savedEnemyFightIndex,
                    () -> {
                        startPrefiEncounter(battlePanel.getCurrentHero());
                    }
            );
            restoreEnemyHp(data);
        }
    }

    private void restoreEnemyHp(GameGUI.model.system.SaveData data) {
        if (data.savedEnemyCurrentHp <= 0 || data.savedEnemyMaxHp <= 0) return;
        GameGUI.model.entity.Combatant enemy = battlePanel.getCurrentEnemy();
        if (enemy == null) return;
        // Only restore if the saved enemy matches the one that spawned (same max HP)
        if (enemy.maxHp == data.savedEnemyMaxHp) {
            enemy.currentHp = data.savedEnemyCurrentHp;
            System.out.println("★ Enemy HP restored: " + enemy.currentHp + "/" + enemy.maxHp);
        }
    }

    private void openSaveScreen() {
        // Prevent saving if the game hasn't fully started
        if (battlePanel == null || battlePanel.getCurrentHero() == null) {
            showStyledCannotSaveDialog();
            return;
        }

        // Open the dialog in Save Mode (isSaveMode = true)
        GameGUI.ui.SaveSlotDialog saveDialog = new GameGUI.ui.SaveSlotDialog(
                SwingUtilities.getWindowAncestor(this),
                true,
                battlePanel.getCurrentHero(),
                battlePanel.getCurrentEnemy(),
                currentWorld,
                battlePanel.getEnemySequenceIndex(),
                battlePanel.getEnemyFightIndex(),
                null
        );
        saveDialog.setVisible(true);
    }

    private void showStyledCannotSaveDialog() {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Cannot Save", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setUndecorated(true);
        dialog.setSize(360, 180);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout(10, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(15, 13, 25));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(new Color(120, 80, 10));
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 20, 20);
            }
        };
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 24, 16, 24));

        JLabel titleLabel = new JLabel("Cannot Save", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Georgia", Font.BOLD, 18));
        titleLabel.setForeground(new Color(200, 160, 40));

        JLabel msg = new JLabel("You haven't started your journey yet!", SwingConstants.CENTER);
        msg.setFont(new Font("Georgia", Font.PLAIN, 13));
        msg.setForeground(new Color(210, 200, 180));

        JButton ok = new JButton("OK");
        ok.setFont(new Font("Georgia", Font.BOLD, 13));
        ok.setForeground(new Color(200, 160, 40));
        ok.setBackground(new Color(30, 28, 45));
        ok.setFocusPainted(false);
        ok.setBorderPainted(true);
        ok.setBorder(BorderFactory.createLineBorder(new Color(120, 80, 10), 2));
        ok.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        ok.setPreferredSize(new Dimension(80, 32));
        ok.addActionListener(e -> dialog.dispose());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setOpaque(false);
        btnPanel.add(ok);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(msg, BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);
        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }

    private void promptSaveAndExit() {
        if (battlePanel == null || battlePanel.getCurrentHero() == null) {
            System.exit(0);
        }

        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Exit Game", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setUndecorated(true);
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout(10, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(15, 13, 25));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(new Color(120, 80, 10));
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 20, 20);
            }
        };
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 24, 16, 24));

        JLabel titleLabel = new JLabel("Exit Game", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Georgia", Font.BOLD, 18));
        titleLabel.setForeground(new Color(200, 160, 40));

        JLabel msg = new JLabel("Do you want to save your progress before exiting?", SwingConstants.CENTER);
        msg.setFont(new Font("Georgia", Font.PLAIN, 13));
        msg.setForeground(new Color(210, 200, 180));

        JButton yesBtn = new JButton("Save & Exit");
        JButton noBtn  = new JButton("Exit");
        JButton cancelBtn = new JButton("Cancel");

        for (JButton btn : new JButton[]{yesBtn, noBtn, cancelBtn}) {
            btn.setFont(new Font("Georgia", Font.BOLD, 13));
            btn.setForeground(new Color(200, 160, 40));
            btn.setBackground(new Color(30, 28, 45));
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createLineBorder(new Color(120, 80, 10), 2));
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.setPreferredSize(new Dimension(110, 32));
        }

        yesBtn.addActionListener(e -> { dialog.dispose(); openSaveScreen(); });
        noBtn.addActionListener(e ->  { dialog.dispose(); System.exit(0); });
        cancelBtn.addActionListener(e -> dialog.dispose());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        btnPanel.setOpaque(false);
        btnPanel.add(yesBtn);
        btnPanel.add(noBtn);
        btnPanel.add(cancelBtn);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(msg, BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);
        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }


}