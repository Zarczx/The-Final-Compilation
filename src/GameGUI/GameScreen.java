package GameGUI;

import GameGUI.HeroData.HeroDefinition;
import GameGUI.HeroData.EnemyDefinition;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

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
    private static final String SCREEN_INTRO        = "intro";
    private static final String SCREEN_SELECTION    = "selection";
    private static final String SCREEN_POST_SELECT  = "postSelect";
    private static final String SCREEN_WORLD1_INTRO = "world1Intro";
    private static final String SCREEN_BATTLE       = "battle";

    // ─── Layout ───────────────────────────────────────────────────────────────
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel     cardPanel  = new JPanel(cardLayout);

    // ─── Sub-panels ───────────────────────────────────────────────────────────
    private HeroSelectionPanel heroSelectionPanel;
    private BattlePanel        battlePanel;
    private JPanel             postSelectPanel;
    private JPanel             world1IntroPanel;

    // ─── Post-selection dialogue widgets ─────────────────────────────────────
    private JTextArea postDialogueBox;
    private JButton   postContinueBtn;
    private int       postDialogueIndex = 0;

    private boolean inPendingDialogue = false;

    // ─── World 1 intro dialogue widgets ──────────────────────────────────────
    private JTextArea w1DialogueBox;
    private JButton   w1ContinueBtn;
    private int       w1DialogueIndex  = 0;
    private Timer     w1TypingTimer;

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

    // ─── World 1 Spawn Sequence ───────────────────────────────────────────────

    /** One "wave" of enemies — mirrors a for-loop group in World1.java. */
    private record EnemyWave(String enemyKey, int count, String encounterEmoji,
                             String betweenMsg, String[] groupClearDialogue) {}

    // ─── World wave tables ────────────────────────────────────────────────────
    // Each world's enemy groups, mirroring the for-loops in World1/2/3.java.
    // Add World 2 & 3 enemies to HeroData.ENEMIES when those worlds are ready.

    private static final EnemyWave[] WORLD1_WAVES = {
            new EnemyWave("Rotfang Wolf", 3, "🐺",
                    "Another wolf snarls and steps forward!",
                    new String[]{
                            "🎉 The last of the Rotfang Wolves collapses.\n\n" +
                                    "The adrenaline cools, but the forest feels no safer.\n" +
                                    "You bandage your wounds and collect what little the wolves carried.",
                            "The path narrows. The mist becomes thick — the air grows icy.\n\n" +
                                    "Shadows detach from the trees, twisting into vague human-like shapes\n" +
                                    "that flicker in and out of existence.\n\n" +
                                    "👻  SHADE SPRITES — lost souls, now jealous of your life.\n\n" +
                                    "🎯 Objective: Defeat 2 Shade Sprites!"
                    }),
            new EnemyWave("Shade Sprite", 2, "👻",
                    "The mist swirls — another soul screams into existence!",
                    new String[]{
                            "🌌 With a final shriek, the sprites disperse like fog in the wind.\n\n" +
                                    "The whispering in your mind finally stops.\n" +
                                    "You feel your strength returning.",
                            "You step over a massive tree root. Suddenly... the root moves.\n\n" +
                                    "🌲 Two DREADBARK TREANTS pull free from the soil.\n" +
                                    "Corrupted by decay, their hollow eyes burn with green necrotic fire.\n\n" +
                                    "🎯 Objective: Defeat 2 Dreadbark Treants!"
                    }),
            new EnemyWave("Dreadbark Treant", 2, "🌳",
                    "The ground quakes — the second ancient giant lumbers forward!",
                    new String[]{
                            "🍃 The Treants collapse. Where they fall, small green sprouts rise from the ash.\n\n" +
                                    "You emerge covered in dust, but victorious.",
                            "The canopy darkens. A stench of rotting meat washes over you.\n\n" +
                                    "🦇 Four CARRION BATS, each the size of a man, dive-bomb from above.\n" +
                                    "Their fangs drip with venom.\n\n" +
                                    "🎯 Objective: Defeat 4 Carrion Bats!"
                    }),
            new EnemyWave("Carrion Bat", 4, "🦇",
                    "Another screech echoes above — the swarm continues!",
                    new String[]{
                            "💨 The last bat crashes down. The forest grows quiet.\n\n" +
                                    "The stench of decay lifts. The path ahead is clear.",
                            "The mist thins, revealing a clearing in pale ghostly moonlight.\n\n" +
                                    "🌕 From behind a great blackened oak steps a massive stag —\n" +
                                    "twelve feet tall. Its antlers glow with white fire.\n\n" +
                                    "🟢 \"Free him,\" Khai's voice echoes. \"Break the Necromancer's chains.\"\n\n" +
                                    "⚠️  MINI-BOSS: THE HOLLOW STAG\n🎯 Objective: Defeat The Hollow Stag!"
                    }),
            new EnemyWave("The Hollow Stag", 1, "🦌", "",
                    new String[]{
                            "✅ 🏆  MINI-BOSS DEFEATED!\n\n" +
                                    "🌟 The Hollow Stag staggers. The white fire in its antlers flickers and dies.\n" +
                                    "It dissolves into particles of pure light.",
                            "You reach out and grasp the light. It solidifies into the FIRST STONE.\n\n" +
                                    "It pulses with quiet power, driving away the chill of the dead forest.\n\n" +
                                    "✨  You have cleansed the forest.\n\n— WORLD 1 COMPLETE —"
                    })
    };

    private int      currentWaveIndex      = 0;
    private int      currentEnemyInWave    = 0;
    private String[] pendingDialogues      = null;
    private int      pendingDialogueIdx    = 0;
    private Runnable afterPendingDialogue  = null;

    // ─── World 2 waves ────────────────────────────────────────────────────────
    // Fill in enemyKey names once you have World2Enemy files.
    // Add matching EnemyDefinition entries to HeroData.ENEMIES.
    private static final EnemyWave[] WORLD2_WAVES = {
            new EnemyWave("Storm Hawk", 3, "🦅",
                    "Another hawk dives from the clouds!",
                    new String[]{
                            "⚡ The last Storm Hawk plummets into the valley floor.\n\n" +
                                    "The thunder overhead seems to ease slightly,\n" +
                                    "as if the sky itself is catching its breath.",
                            "The fog thickens. From within the clouds, a low roar reverberates.\n\n" +
                                    "🐉 A RIDGE WYVERN descends, its wings crackling with lightning.\n\n" +
                                    "🎯 Objective: Defeat 2 Ridge Wyverns!"
                    }),
            new EnemyWave("Ridge Wyvern", 2, "🐉",
                    "The second Wyvern crashes down from above!",
                    new String[]{
                            "⚡ The Wyverns collapse, their lightning fading.\n\n" +
                                    "The storm above begins to thin — but the air still crackles\n" +
                                    "with dangerous energy.",
                            "The valley narrows into a canyon. Deep within, a massive silhouette\n" +
                                    "stirs behind a veil of storm clouds.\n\n" +
                                    "🌩️ MINI-BOSS: THE TEMPEST COLOSSUS\n🎯 Objective: Defeat The Tempest Colossus!"
                    }),
            new EnemyWave("The Tempest Colossus", 1, "🌩️", "",
                    new String[]{
                            "✅ 🏆  MINI-BOSS DEFEATED!\n\n" +
                                    "🌟 The Tempest Colossus staggers and dissolves into a cascade\n" +
                                    "of crackling lightning bolts that fade into sparks.",
                            "The sparks coalesce into the SECOND STONE.\n\n" +
                                    "It hums with electric energy, warm against your palm.\n\n" +
                                    "✨  The storm valley is calmed.\n\n— WORLD 2 COMPLETE —"
                    })
    };

    // ─── World 3 waves ────────────────────────────────────────────────────────
    // Fill in enemyKey names once you have World3Enemy files.
    private static final EnemyWave[] WORLD3_WAVES = {
            new EnemyWave("Bone Archer", 4, "💀",
                    "Another skeletal archer rises from the shadows!",
                    new String[]{
                            "💀 The last Bone Archer crumbles to dust.\n\n" +
                                    "The silence of the Necromancer's realm is suffocating.\n" +
                                    "Every shadow feels alive.",
                            "The ground cracks open. Black smoke pours from the fissures,\n" +
                                    "taking the shape of towering figures.\n\n" +
                                    "👹 SHADOW GOLEMS — animated by the Necromancer's will.\n\n" +
                                    "🎯 Objective: Defeat 2 Shadow Golems!"
                    }),
            new EnemyWave("Shadow Golem", 2, "👹",
                    "The second Shadow Golem lumbers forward from the darkness!",
                    new String[]{
                            "👹 The Golems shatter. The black smoke dissipates\n" +
                                    "but the air still reeks of necrotic energy.",
                            "A throne room materialises from the void ahead.\n" +
                                    "On it sits a robed figure — skeletal hands curled around a staff\n" +
                                    "of pure darkness.\n\n" +
                                    "🟢 \"So you made it,\" Khai whispers behind you.\n" +
                                    "\"This is it. The Necromancer himself.\"\n\n" +
                                    "💀 FINAL BOSS: THE LICH VAROS\n🎯 Objective: Defeat The Lich Varos!"
                    }),
            new EnemyWave("The Lich Varos", 1, "🧿", "",
                    new String[]{
                            "✅ 🏆  FINAL BOSS DEFEATED!\n\n" +
                                    "🌟 The Lich Varos screams — a sound that tears through\n" +
                                    "every dimension at once. His form collapses inward\n" +
                                    "and implodes into nothing.",
                            "The Third Stone falls from where his heart was.\n\n" +
                                    "All three Stones pulse together, reweaving the fabric of reality.\n\n" +
                                    "✨  The Necromancer is gone. The realm begins to heal.\n\n— WORLD 3 COMPLETE —"
                    })
    };

    // ─── Active world tracking ────────────────────────────────────────────────
    private EnemyWave[] currentWaves    = WORLD1_WAVES;
    private int         currWorldLevel  = 1;

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

        postSelectPanel = buildPostSelectScreen();
        cardPanel.add(postSelectPanel, SCREEN_POST_SELECT);

        world1IntroPanel = buildWorld1IntroScreen();
        cardPanel.add(world1IntroPanel, SCREEN_WORLD1_INTRO);

        battlePanel = new BattlePanel();
        battlePanel.setOnReturnToSelection(this::goToSelection);
        battlePanel.setOnRestartBattle(this::restartBattle);
        cardPanel.add(battlePanel, SCREEN_BATTLE);

        add(cardPanel, BorderLayout.CENTER);

        // Start the intro
        startTyping(dialogues[dialogueIndex], null);
    }

    // ─── Screen navigation ────────────────────────────────────────────────────

    private void goToIntro()        { cardLayout.show(cardPanel, SCREEN_INTRO);        }
    private void goToSelection()    { cardLayout.show(cardPanel, SCREEN_SELECTION);    }
    private void goToPostSelect()   { cardLayout.show(cardPanel, SCREEN_POST_SELECT);  }
    private void goToWorld1Intro()  { cardLayout.show(cardPanel, SCREEN_WORLD1_INTRO); }

    private void goToBattle() {
        if (confirmedHero == null) return;
        EnemyDefinition enemy = getCurrentWaveEnemy();
        if (enemy == null) return;

        EnemyWave wave  = currentWaves[currentWaveIndex];
        int enemyNum    = currentEnemyInWave + 1;

        battlePanel.startBattle(confirmedHero, enemy);
        battlePanel.setBattleAnnouncement(
                wave.encounterEmoji() + "  " + enemy.name
                        + "  (" + enemyNum + " / " + wave.count() + ")"
        );
        cardLayout.show(cardPanel, SCREEN_BATTLE);
    }

    /**
     * Called by BattlePanel when the player wins a fight.
     * Mirrors the post-battle flow in World1/2/3.java's for-loop body.
     */
    public void onEnemyDefeated() {
        EnemyWave wave = currentWaves[currentWaveIndex];
        currentEnemyInWave++;

        if (currentEnemyInWave < wave.count()) {
            // More enemies in this wave — show "next enemy" message then fight
            showPendingDialogue(
                    new String[]{ wave.encounterEmoji() + "  " + wave.betweenMsg() },
                    this::goToBattle
            );
        } else {
            // Wave cleared — show group-clear dialogue then advance
            currentEnemyInWave = 0;
            currentWaveIndex++;
            if (currentWaveIndex < currentWaves.length) {
                // Next wave in the same world
                showPendingDialogue(wave.groupClearDialogue(), this::goToBattle);
            } else {
                // All waves in this world cleared — transition to next world
                showPendingDialogue(wave.groupClearDialogue(), this::onWorldComplete);
            }
        }
    }

    /** Returns the EnemyDefinition for the current wave enemy. */
    private EnemyDefinition getCurrentWaveEnemy() {
        if (currentWaveIndex >= currentWaves.length) return null;
        String key = currentWaves[currentWaveIndex].enemyKey();
        return HeroData.ENEMIES.stream()
                .filter(e -> e.name.equals(key))
                .findFirst()
                .orElse(null); // fallback
    }

    private void restartBattle() {
        // Reset to beginning of World 1
        currentWaves       = WORLD1_WAVES;
        currWorldLevel     = 1;
        currentWaveIndex   = 0;
        currentEnemyInWave = 0;
        goToBattle();
    }

    /**
     * Called after all waves in the current world are cleared.
     * Mirrors StoryEngine's transitionToWorld2() / transitionToWorld3() pattern.
     */
    private void onWorldComplete() {
        if (currWorldLevel == 1) {
            currWorldLevel = 2;
            currentWaves       = WORLD2_WAVES;
            currentWaveIndex   = 0;
            currentEnemyInWave = 0;

            // Mirrors StoryEngine.transitionToWorld2() dialogue
            showPendingDialogue(new String[]{
                    "The forest around you shudders — not in pain, but in relief.\n\n" +
                            "Gray bark cracks to reveal rich brown wood. The ash on the ground\n" +
                            "blooms into lush green moss. The corruption fades, leaving behind\n" +
                            "faint sparks of life glowing in the air.",

                    "🟢 \"This forest is saved. Life is beautiful,\" Sir Khai murmurs,\n" +
                            "watching a small flower bloom.\n\n" +
                            "\"But our journey is far from over. Two more Stones remain…\n" +
                            "and darkness gathers ahead.\"\n\n" +
                            "A path parts through the trees, winding toward a valley\n" +
                            "shrouded in thick fog and the sound of distant thunder.\n\n" +
                            "⚡ — WORLD 2 : THE STORMING VALLEY — ⚡"
            }, this::goToBattle);

        } else if (currWorldLevel == 2) {
            currWorldLevel = 3;
            currentWaves       = WORLD3_WAVES;
            currentWaveIndex   = 0;
            currentEnemyInWave = 0;

            // Mirrors StoryEngine.transitionToWorld3() dialogue
            showPendingDialogue(new String[]{
                    "The Second Stone vibrates violently in your grasp,\n" +
                            "reacting to Khai's presence.\n\n" +
                            "Sir Khai's staff ignites with a brilliant SILVER FLAME,\n" +
                            "cutting through the castle's gloom.\n" +
                            "Outside, the sky begins to twist unnaturally.",

                    "🟢 \"The final trial awaits,\" Khai says quietly,\n" +
                            "looking toward the dark peaks.\n\n" +
                            "\"Beyond that storm lies a realm where even light cannot survive...\n" +
                            "That is where the Last Stone is kept.\n" +
                            "And where the Necromancer waits.\"\n\n" +
                            "💀 — WORLD 3 : THE NECROMANCER'S REALM — 💀"
            }, this::goToBattle);

        } else {
            // All three worlds complete — epilogue
            showPendingDialogue(new String[]{
                    "🌟 THE FINAL COMPILATION — COMPLETE.\n\n" +
                            "The Necromancer's power crumbles. The three Stones of Life\n" +
                            "pulse together, reweaving the fabric of reality.\n\n" +
                            "A rift tears open before you — on the other side,\n" +
                            "the warm light of a classroom flickers into view.",

                    "You step through.\n\n" +
                            "The monitor reads:   100 / 100.\n\n" +
                            "Professor Khai looks up from his desk and smiles.\n\n" +
                            "✨ Thank you for playing — The Final Compilation."
            }, this::goToSelection);
        }
    }

    // ─── Pending dialogue queue (reuses world1IntroPanel) ────────────────────

    /**
     * Shows a sequence of dialogue lines on the world1IntroPanel,
     * then runs the callback when all lines are acknowledged.
     * Reuses the existing w1DialogueBox / w1ContinueBtn / typing system.
     */
    private void showPendingDialogue(String[] lines, Runnable onFinished) {
        inPendingDialogue = true;
        pendingDialogues     = lines;
        pendingDialogueIdx   = 0;
        afterPendingDialogue = onFinished;
        cardLayout.show(cardPanel, SCREEN_WORLD1_INTRO);
        startPendingTyping();
    }

    private Timer pendingTypingTimer;

    private void startPendingTyping() {
        if (pendingDialogues == null || pendingDialogueIdx >= pendingDialogues.length) return;
        if (pendingTypingTimer != null && pendingTypingTimer.isRunning()) pendingTypingTimer.stop();

        String text = pendingDialogues[pendingDialogueIdx];
        w1DialogueBox.setText("");
        int[] ci = {0};

        pendingTypingTimer = new Timer(22, null);
        pendingTypingTimer.addActionListener(e -> {
            if (ci[0] < text.length()) {
                w1DialogueBox.append(String.valueOf(text.charAt(ci[0]++)));
            } else {
                pendingTypingTimer.stop();
            }
        });
        pendingTypingTimer.start();

        // Redirect the continue button to advance pending dialogues
        for (var l : w1ContinueBtn.getActionListeners()) w1ContinueBtn.removeActionListener(l);
        w1ContinueBtn.addActionListener(e -> advancePendingDialogue());
    }

    private void advancePendingDialogue() {
        // First click while typing → skip to end
        if (pendingTypingTimer != null && pendingTypingTimer.isRunning()) {
            pendingTypingTimer.stop();
            w1DialogueBox.setText(pendingDialogues[pendingDialogueIdx]);
            return;
        }

        pendingDialogueIdx++;
        if (pendingDialogueIdx < pendingDialogues.length) {
            startPendingTyping();
        } else {
            // All pending lines done — restore normal w1 listener and run callback
            for (var l : w1ContinueBtn.getActionListeners()) w1ContinueBtn.removeActionListener(l);
            w1ContinueBtn.addActionListener(e2 -> continueW1Dialogue());
            pendingDialogues = null;
            inPendingDialogue = false;
            if (afterPendingDialogue != null) {
                Runnable cb = afterPendingDialogue;
                afterPendingDialogue = null;
                cb.run();
            }
        }
    }

    // ─── Hero confirmed callback ──────────────────────────────────────────────

    /**
     * Called by HeroSelectionPanel when the player clicks "Enter the Arena".
     * Mirrors the old Kael button listener + fightBtn listener flow.
     */
    private void onHeroConfirmed(HeroDefinition hero) {
        this.confirmedHero = hero;
        // Reset all world tracking for a fresh run
        currentWaves       = WORLD1_WAVES;
        currWorldLevel     = 1;
        currentWaveIndex   = 0;
        currentEnemyInWave = 0;
        pendingDialogues   = null;

        // Build the post-selection dialogues for this specific hero
        buildPostSelectDialogues(hero);

        // Go to the post-selection screen (backstory + weapon gift)
        goToPostSelect();
        postDialogueIndex = 0;
        startPostTyping();
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

        // Fight → start the battle with the already-confirmed hero
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
            goToSelection(); // Player picks hero first; battleChoicePopup shown after
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

    // ─── Post-selection dialogue screen ──────────────────────────────────────

    /** Lines of dialogue shown after a hero is confirmed, before World 1 begins. */
    private String[] postDialogues;

    /**
     * Populates postDialogues based on the chosen hero — mirrors StoryEngine's
     * chooseCharacter() backstory + weapon-gift text for each character.
     */
    private void buildPostSelectDialogues(HeroDefinition hero) {
        String heroLine;
        String weaponLine;
        String armorLine;

        switch (hero.name) {
            case "Kael Saint Laurent" -> {
                heroLine  = "⚔️ You have chosen KAEL SAINT LAURENT, the valiant Swordsman!\n\n" +
                        hero.backstory;
                weaponLine = "🗡️ The gods bestow upon you your starting gear...\n\n" +
                        "The Old Broadsword rests firmly in your grasp, its blade marked\n" +
                        "by the scars of past battles.";
                armorLine  = "🛡️ The Leather Guard settles on your shoulders — worn, but reliable.\n\n" +
                        "You are ready. The forest of World 1 awaits.";
            }
            case "Karl Clover Dior IV" -> {
                heroLine  = "🏹 You have chosen KARL CLOVER DIOR IV, the swift Archer!\n\n" +
                        hero.backstory;
                weaponLine = "🏹 The gods bestow upon you your starting gear...\n\n" +
                        "The Shortbow hums softly — each arrow you notch feels like an\n" +
                        "extension of your will.";
                armorLine  = "🛡️ The Leather Guard settles on your shoulders — worn, but reliable.\n\n" +
                        "You are ready. The forest of World 1 awaits.";
            }
            case "Simon Versace" -> {
                heroLine  = "🌟 You have chosen SIMON VERSACE, the arcane Mage!\n\n" +
                        hero.backstory;
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

        postDialogues = new String[] { heroLine, weaponLine, armorLine };
    }

    /**
     * Builds the post-selection card — a full-screen dialogue panel styled like
     * the intro screen, used for backstory + weapon-gift sequence.
     */
    private JPanel buildPostSelectScreen() {
        JPanel screen = new JPanel(null);
        screen.setBackground(new Color(28, 26, 44));
        screen.setPreferredSize(new Dimension(1280, 720));

        // Dark scenic background
        JPanel bg = new JPanel();
        bg.setBounds(0, 0, 1280, 520);
        bg.setBackground(new Color(20, 18, 35));
        screen.add(bg);

        // Dialogue box — same style as intro
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

        // Continue button
        postContinueBtn = new JButton("▶  Continue");
        postContinueBtn.setBounds(1100, 610, 140, 40);
        postContinueBtn.setBackground(new Color(70, 50, 120));
        postContinueBtn.setForeground(Color.WHITE);
        postContinueBtn.setFocusPainted(false);
        postContinueBtn.addActionListener(e -> continuePostDialogue());
        screen.add(postContinueBtn);

        return screen;
    }

    /** Tracks the currently running post-selection typing timer so it can be stopped. */
    private Timer postTypingTimer;

    /** Kicks off typing animation for the current postDialogueIndex line. */
    private void startPostTyping() {
        if (postDialogues == null || postDialogueIndex >= postDialogues.length) return;

        // Stop any previously running timer before starting a new one
        if (postTypingTimer != null && postTypingTimer.isRunning()) {
            postTypingTimer.stop();
        }

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

    /**
     * Advances through post-selection dialogues.
     * If still typing, first click skips to end of current line.
     * After the last line, transitions to World 1 (SCREEN_BATTLE).
     */
    private void continuePostDialogue() {
        // If still typing, skip to end of current line instead of advancing
        if (postTypingTimer != null && postTypingTimer.isRunning()) {
            postTypingTimer.stop();
            postDialogueBox.setText(postDialogues[postDialogueIndex]);
            return;
        }

        postDialogueIndex++;
        if (postDialogueIndex < postDialogues.length) {
            startPostTyping();
        } else {
            // All backstory + weapon-gift lines shown — enter World 1 intro dialogue
            postDialogueIndex = 0;
            w1DialogueIndex = 0;
            goToWorld1Intro();
            startW1Typing();
        }
    }

    // ─── World 1 intro dialogue screen ───────────────────────────────────────

    /** Dialogue lines from World1.java's opening story sequence, before combat begins. */
    private static final String[] WORLD1_DIALOGUES = {
            "You wake up gasping for air. The world is drained of color.\n\n" +
                    "You are lying on a bed of gray moss in a dead forest. The trees are skeletal\n" +
                    "giants, stripped to bone-white wood. A cold mist coils around your ankles,\n" +
                    "and silence presses from every side — watching, waiting.",

            "A heavy bell tolls in the distance...\n" +
                    "\"Dong... Dong...\"",

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

    /**
     * Builds the World 1 intro card — same style as the post-select screen.
     * Shows the opening story sequence from World1.java before the first battle.
     */
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

    /** Kicks off typing animation for the current w1DialogueIndex line. */
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

    /**
     * Advances through World 1 intro dialogues.
     * First click while typing skips to end of current line.
     * After the last line, transitions to the actual battle (SCREEN_BATTLE).
     */
    private void continueW1Dialogue() {
        if(inPendingDialogue)return;
        if (w1TypingTimer != null && w1TypingTimer.isRunning()) {
            w1TypingTimer.stop();
            w1DialogueBox.setText(WORLD1_DIALOGUES[w1DialogueIndex]);
            return;
        }

        w1DialogueIndex++;
        if (w1DialogueIndex < WORLD1_DIALOGUES.length) {
            startW1Typing();
        } else {
            // World 1 intro complete — now enter the actual battle
            w1DialogueIndex = 0;
            goToBattle();
        }
    }
}