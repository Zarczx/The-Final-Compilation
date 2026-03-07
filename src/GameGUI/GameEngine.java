package GameGUI;

import characters.*;
import characters.Character;
import inventory.Armor;
import inventory.Bow;
import inventory.Staff;
import inventory.Sword;
import utils.SoundUtil;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import static java.awt.Component.LEFT_ALIGNMENT;

public class GameEngine {

    // =========================================================================
    //  PALETTE  (from Game.java)
    // =========================================================================
    static final Color BG          = new Color(8,   10,  18);
    static final Color BG_PANEL    = new Color(14,  17,  28);
    static final Color BG_CARD     = new Color(20,  25,  42);
    static final Color BORDER      = new Color(45,  60, 105);
    static final Color BORDER_HOT  = new Color(80, 120, 200);
    static final Color GOLD        = new Color(220, 175,  70);
    static final Color CYAN        = new Color( 70, 195, 215);
    static final Color RED         = new Color(210,  65,  65);
    static final Color GREEN       = new Color( 65, 185, 110);
    static final Color TEXT        = new Color(205, 210, 225);
    static final Color TEXT_DIM    = new Color(110, 120, 148);
    static final Color SCAN_LINE   = new Color(0, 0, 0, 28);
    static final Color MAGENTA     = new Color(190,  80, 200);
    static final Color C_HP        = new Color(200,  60,  60);
    static final Color C_ENERGY    = new Color( 60, 140, 220);
    static final Color C_HP_BG     = new Color( 60,  20,  20);
    static final Color C_ENERGY_BG = new Color( 20,  40,  80);

    // =========================================================================
    //  FONTS  (from Game.java)
    // =========================================================================
    static final Font MONO_SM  = new Font("Courier New", Font.PLAIN,  11);
    static final Font MONO_MD  = new Font("Courier New", Font.PLAIN,  14);
    static final Font MONO_LG  = new Font("Courier New", Font.BOLD,   16);
    static final Font MONO_XL  = new Font("Courier New", Font.BOLD,   20);
    static final Font SERIF_MD = new Font("Georgia",     Font.PLAIN,  15);
    static final Font SERIF_LG = new Font("Georgia",     Font.ITALIC, 17);
    static final Font UI_SM    = new Font("Segoe UI",    Font.PLAIN,  12);
    static final Font UI_MD    = new Font("Segoe UI",    Font.PLAIN,  14);

    // =========================================================================
    //  TEXT STYLE KEYS  (for the story JTextPane)
    // =========================================================================
    static final String S_NORMAL  = "normal";
    static final String S_STORY   = "story";
    static final String S_EFFECT  = "effect";
    static final String S_TITLE   = "title";
    static final String S_CYAN    = "cyan";
    static final String S_GOLD    = "gold";
    static final String S_GREEN   = "green";
    static final String S_RED     = "red";
    static final String S_MAGENTA = "magenta";
    static final String S_DIM     = "dim";

    // =========================================================================
    //  STATE
    // =========================================================================
    private Character player;
    private Character selectedTemp;
    private static int currWorldLevel = 1;
    public static int getCurrWorldLevel() { return currWorldLevel; }

    // =========================================================================
    //  WINDOW  &  CARD LAYOUT
    // =========================================================================
    private JFrame     window;
    private CardPanel  cards;

    // Screens
    private TitleScreen      titleScreen;
    private StoryScreen      storyScreen;
    private LoginScreen      loginScreen;
    private ExamScreen       examScreen;
    private CharSelectScreen charSelectScreen;
    private GameScreen       gameScreen;       // world/adventure screen

    // =========================================================================
    //  ENTRY POINT
    // =========================================================================
    public void start() {
        SwingUtilities.invokeLater(() -> {
            buildWindow();
            SoundUtil.playLoop("intro1.wav", 0.1f);
            SoundUtil.playLoop2Delayed("intro2.wav", 0.1f, 2);
            showScreen("TITLE");
        });
    }

    // =========================================================================
    //  WINDOW CONSTRUCTION
    // =========================================================================
    private void buildWindow() {
        window = new JFrame("The Final Compilation — OOP Adventure");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setSize(1100, 760);
        window.setMinimumSize(new Dimension(900, 600));
        window.setLocationRelativeTo(null);
        window.getContentPane().setBackground(BG);
        window.setLayout(new BorderLayout());

        cards = new CardPanel();
        window.add(cards, BorderLayout.CENTER);

        // Build all screens once
        titleScreen      = new TitleScreen(this);
        storyScreen      = new StoryScreen(this);
        loginScreen      = new LoginScreen(this);
        examScreen       = new ExamScreen(this);
        charSelectScreen = new CharSelectScreen(this);
        gameScreen       = new GameScreen(this);

        cards.add(titleScreen,      "TITLE");
        cards.add(storyScreen,      "STORY");
        cards.add(loginScreen,      "LOGIN");
        cards.add(examScreen,       "EXAM");
        cards.add(charSelectScreen, "CHARSELECT");
        cards.add(gameScreen,       "GAME");

        window.setVisible(true);
    }

    public void showScreen(String name) {
        SwingUtilities.invokeLater(() -> {
            cards.show(name);
            window.revalidate();
            window.repaint();
        });
    }

    public void showAlert(String msg) {
        JOptionPane.showMessageDialog(window, msg, "Alert", JOptionPane.WARNING_MESSAGE);
    }

    // ── Non-blocking delay (replaces PrintUtil.pause) ─────────────────────────
    public void delay(int ms, Runnable next) {
        Timer t = new Timer(ms, e -> next.run());
        t.setRepeats(false);
        t.start();
    }

    // =========================================================================
    //  SCREEN: TITLE
    // =========================================================================
    static class TitleScreen extends BaseScreen {
        TitleScreen(GameEngine engine) {
            setLayout(new GridBagLayout());

            JPanel center = new JPanel();
            center.setOpaque(false);
            center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

            JTextArea title = monoArea(
                    "╔═══════════════════════════════════════════════════════════════════════════════════════════════════════╗\n" +
                            "\n" +
                            "      ░▀█▀░█░█░█▀▀░░░█▀▀░█░█░█▀█░█▄█░░░▀█▀░█░█░█▀█░▀█▀░░░█▀█░█▀▀░█░█░█▀▀░█▀▄░░░█▀▄░█▀▀░█▀▀░█▀█░█▀█\n" +
                            "      ░░█░░█▀█░█▀▀░░░█▀▀░▄▀▄░█▀█░█░█░░░░█░░█▀█░█▀█░░█░░░░█░█░█▀▀░▀▄▀░█▀▀░█▀▄░░░█▀▄░█▀▀░█░█░█▀█░█░█\n" +
                            "      ░░▀░░▀░▀░▀▀▀░░░▀▀▀░▀░▀░▀░▀░▀░▀░░░░▀░░▀░▀░▀░▀░░▀░░░░▀░▀░▀▀▀░░▀░░▀▀▀░▀░▀░░░▀▀░░▀▀▀░▀▀▀░▀░▀░▀░▀\n" +
                            "\n" +
                            "╚═══════════════════════════════════════════════════════════════════════════════════════════════════════╝",
                    CYAN, MONO_SM
            );
            title.setAlignmentX(CENTER_ALIGNMENT);

            JLabel subtitle = styledLabel("OBJECT ORIENTED PROGRAMMING ADVENTURE", GOLD, MONO_LG);
            subtitle.setAlignmentX(CENTER_ALIGNMENT);

            JLabel tagline = styledLabel("Press START to begin your journey...", TEXT_DIM, SERIF_MD);
            tagline.setAlignmentX(CENTER_ALIGNMENT);

            GlowButton startBtn = new GlowButton("▶   START", GOLD);
            startBtn.setAlignmentX(CENTER_ALIGNMENT);
            startBtn.setMaximumSize(new Dimension(220, 52));
            startBtn.addActionListener(e -> engine.showScreen("STORY"));

            center.add(Box.createVerticalGlue());
            center.add(title);
            center.add(Box.createVerticalStrut(24));
            center.add(subtitle);
            center.add(Box.createVerticalStrut(10));
            center.add(tagline);
            center.add(Box.createVerticalStrut(40));
            center.add(startBtn);
            center.add(Box.createVerticalGlue());

            add(center);
        }
    }

    // =========================================================================
    //  SCREEN: STORY (intro scene)
    // =========================================================================
    static class StoryScreen extends BaseScreen {
        StoryScreen(GameEngine engine) {
            JPanel content = new JPanel();
            content.setOpaque(false);
            content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
            content.setBorder(new EmptyBorder(60, 120, 40, 120));

            JTextArea story = new JTextArea(
                    "💡  It's just another Tuesday. You come in for your Java examination.\n\n" +
                            "Professor Khai greets you warmly as you sit before the CodeChum login\n" +
                            "screen, nerves heavy, focus sharp. You place your hands on the keyboard\n" +
                            "and type your credentials...   L15Y07W....  ⌨️"
            );
            story.setFont(SERIF_LG);
            story.setForeground(TEXT);
            story.setBackground(new Color(0, 0, 0, 0));
            story.setOpaque(false);
            story.setEditable(false);
            story.setFocusable(false);
            story.setLineWrap(true);
            story.setWrapStyleWord(true);
            story.setAlignmentX(CENTER_ALIGNMENT);

            JTextArea monitor = monoArea(
                    "        :@@@@@@@@@@@@@@@@@@@@@@@@@@@%\n" +
                            "        -%                         -@\n" +
                            "        -%                         -@   #@@@@@@@@@-\n" +
                            "        -%                         -@   @#......:@+\n" +
                            "        -%                         -@   @@@@@@@@@@+\n" +
                            "        -@@@@@@@@@@@@@@@@@@@@@@@@@@@@   @@@@@@@@@@+\n" +
                            "                 +%@@@@@@@@#-           @@@@%+@@@@+\n" +
                            "                  .-==++=-:             @@@@#-@@@@+",
                    CYAN, MONO_SM
            );
            monitor.setAlignmentX(CENTER_ALIGNMENT);

            GlowButton next = new GlowButton(">>>   Continue", CYAN);
            next.setAlignmentX(CENTER_ALIGNMENT);
            next.setMaximumSize(new Dimension(200, 46));
            next.addActionListener(e -> engine.showScreen("LOGIN"));

            content.add(Box.createVerticalGlue());
            content.add(story);
            content.add(Box.createVerticalStrut(30));
            content.add(monitor);
            content.add(Box.createVerticalStrut(30));
            content.add(next);
            content.add(Box.createVerticalGlue());

            add(content, BorderLayout.CENTER);
        }
    }

    // =========================================================================
    //  SCREEN: LOGIN
    // =========================================================================
    static class LoginScreen extends BaseScreen {
        JTextField     userField;
        JPasswordField passField;

        LoginScreen(GameEngine engine) {
            setLayout(new GridBagLayout());

            JPanel card = new CardBackground();
            card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
            card.setPreferredSize(new Dimension(460, 480));
            card.setBorder(new EmptyBorder(36, 48, 36, 48));

            JLabel header = styledLabel("🖥  CODECHUM LOGIN", CYAN, MONO_LG);
            header.setAlignmentX(CENTER_ALIGNMENT);

            JSeparator sep = new JSeparator();
            sep.setForeground(BORDER);
            sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

            JTextArea art = monoArea(
                    "   :@@@@@@@@@@@@@@@@@@@@@@@:   \n" +
                            "   -%                     -@   \n" +
                            "   -%   ▓▓▓▓▓▓▓▓▓▓▓▓▓▓   -@   \n" +
                            "   -%   ▓▓▓▓▓▓▓▓▓▓▓▓▓▓   -@   \n" +
                            "   -@@@@@@@@@@@@@@@@@@@@@@@@   \n" +
                            "          +%@@@@@@#-           ",
                    CYAN, MONO_SM
            );
            art.setAlignmentX(CENTER_ALIGNMENT);

            JLabel userLbl = fieldLabel("👤  Username");
            userField = styledField("Enter username...");

            JLabel passLbl = fieldLabel("🔑  Password");
            passField = new JPasswordField();
            passField.setFont(MONO_MD);
            passField.setForeground(TEXT);
            passField.setBackground(new Color(12, 15, 26));
            passField.setCaretColor(CYAN);
            passField.setBorder(fieldBorder());
            passField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

            GlowButton loginBtn = new GlowButton("🔑   Login", GOLD);
            loginBtn.setAlignmentX(CENTER_ALIGNMENT);
            loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
            loginBtn.addActionListener(e -> {
                String u = userField.getText().trim();
                String p = new String(passField.getPassword()).trim();
                if (u.isBlank() || u.equals("Enter username...")) { engine.showAlert("❌ Username cannot be empty!"); return; }
                if (p.isBlank()) { engine.showAlert("❌ Password cannot be empty!"); return; }
                engine.showScreen("EXAM");
            });

            card.add(header);
            card.add(Box.createVerticalStrut(10));
            card.add(sep);
            card.add(Box.createVerticalStrut(16));
            card.add(art);
            card.add(Box.createVerticalStrut(20));
            card.add(userLbl);
            card.add(Box.createVerticalStrut(6));
            card.add(userField);
            card.add(Box.createVerticalStrut(14));
            card.add(passLbl);
            card.add(Box.createVerticalStrut(6));
            card.add(passField);
            card.add(Box.createVerticalStrut(24));
            card.add(loginBtn);

            add(card);
        }
    }

    // =========================================================================
    //  SCREEN: EXAM
    // =========================================================================
    static class ExamScreen extends BaseScreen {
        ExamScreen(GameEngine engine) {
            setLayout(new GridBagLayout());

            JPanel card = new CardBackground();
            card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
            card.setPreferredSize(new Dimension(520, 380));
            card.setBorder(new EmptyBorder(36, 48, 36, 48));

            JTextArea header = monoArea(
                    "╔═════════════════════════════════════════════╗\n" +
                            "     OOP1  Final Exam  —  Batch 1 · G1        \n" +
                            "╚═════════════════════════════════════════════╝",
                    CYAN, MONO_MD
            );
            header.setAlignmentX(CENTER_ALIGNMENT);

            JPanel statsRow = new JPanel(new GridLayout(1, 2, 20, 0));
            statsRow.setOpaque(false);
            statsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
            statsRow.add(statCard("Overall Score",  "0 / 100",  GOLD));
            statsRow.add(statCard("Time Remaining", "01:00:00", RED));

            JLabel prompt = styledLabel("Are you ready to begin?", TEXT_DIM, SERIF_MD);
            prompt.setAlignmentX(CENTER_ALIGNMENT);

            JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
            btnRow.setOpaque(false);
            btnRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));

            GlowButton startBtn = new GlowButton("▶   Start Exam", GOLD);
            startBtn.setPreferredSize(new Dimension(180, 46));
            startBtn.addActionListener(e -> {
                SoundUtil.stopLoop();
                SoundUtil.stopLoop2();
                engine.showScreen("CHARSELECT");
                engine.startExamTransition();
            });

            GlowButton cancelBtn = new GlowButton("✖   Cancel", RED);
            cancelBtn.setPreferredSize(new Dimension(140, 46));
            cancelBtn.addActionListener(e -> System.exit(0));

            btnRow.add(startBtn);
            btnRow.add(cancelBtn);

            card.add(header);
            card.add(Box.createVerticalStrut(24));
            card.add(statsRow);
            card.add(Box.createVerticalStrut(24));
            card.add(prompt);
            card.add(Box.createVerticalStrut(20));
            card.add(btnRow);

            add(card);
        }

        private JPanel statCard(String label, String value, Color accent) {
            JPanel p = new JPanel();
            p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
            p.setBackground(new Color(10, 12, 22));
            p.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(accent, 1),
                    new EmptyBorder(10, 16, 10, 16)
            ));
            JLabel lbl = styledLabel(label, TEXT_DIM, UI_SM); lbl.setAlignmentX(CENTER_ALIGNMENT);
            JLabel val = styledLabel(value, accent, MONO_XL);  val.setAlignmentX(CENTER_ALIGNMENT);
            p.add(lbl);
            p.add(Box.createVerticalStrut(4));
            p.add(val);
            return p;
        }
    }

    // =========================================================================
    //  SCREEN: CHARACTER SELECT
    // =========================================================================
    static class CharSelectScreen extends BaseScreen {
        private JTextArea detailArea;

        CharSelectScreen(GameEngine engine) {
            setLayout(new BorderLayout(0, 0));

            // Top bar
            JPanel topBar = new JPanel(new FlowLayout(FlowLayout.CENTER));
            topBar.setBackground(BG_PANEL);
            topBar.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, BORDER));
            topBar.setPreferredSize(new Dimension(0, 48));
            topBar.add(styledLabel("⚔   Choose Your Hero", GOLD, MONO_LG));
            add(topBar, BorderLayout.NORTH);

            // Center detail area
            detailArea = new JTextArea(
                    "\n\n\n" +
                            "      Select a character below to preview their stats,\n" +
                            "      skills, and backstory.\n\n" +
                            "      Your choice will shape the entire journey ahead..."
            );
            detailArea.setFont(SERIF_MD);
            detailArea.setBackground(BG);
            detailArea.setForeground(TEXT);
            detailArea.setEditable(false);
            detailArea.setFocusable(false);
            detailArea.setMargin(new Insets(24, 32, 24, 32));
            detailArea.setLineWrap(true);
            detailArea.setWrapStyleWord(true);

            JScrollPane scroll = new JScrollPane(detailArea);
            scroll.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, BORDER));
            scroll.getViewport().setBackground(BG);
            scroll.setBackground(BG);
            styleScrollBar(scroll);
            add(scroll, BorderLayout.CENTER);

            // Bottom panel
            JPanel bottomPanel = new JPanel();
            bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
            bottomPanel.setBackground(BG_PANEL);
            bottomPanel.setBorder(new EmptyBorder(12, 24, 14, 24));

            // Hero choice row
            JPanel heroRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
            heroRow.setOpaque(false);

            String[][] heroes = {
                    {"1", "⚔  Kael",  "Swordsman"},
                    {"2", "🏹  Karl",  "Archer"},
                    {"3", "✨  Simon", "Mage"},
                    {"4", "🧪  Null",  "Test"}
            };
            for (String[] h : heroes) {
                int id = Integer.parseInt(h[0]);
                HeroButton btn = new HeroButton(h[1], h[2]);
                btn.addActionListener(e -> engine.previewCharacter(id, detailArea));
                heroRow.add(btn);
            }

            // Confirm row
            JPanel confirmRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
            confirmRow.setOpaque(false);

            GlowButton confirmBtn = new GlowButton("✅   Confirm Selection", GREEN);
            confirmBtn.setPreferredSize(new Dimension(240, 46));
            confirmBtn.addActionListener(e -> engine.confirmCharacter());

            GlowButton backBtn = new GlowButton("↩   Back", TEXT_DIM);
            backBtn.setPreferredSize(new Dimension(140, 46));
            backBtn.addActionListener(e -> engine.showScreen("EXAM"));

            confirmRow.add(confirmBtn);
            confirmRow.add(backBtn);

            bottomPanel.add(heroRow);
            bottomPanel.add(Box.createVerticalStrut(10));
            bottomPanel.add(confirmRow);

            add(bottomPanel, BorderLayout.SOUTH);
        }

        private void styleScrollBar(JScrollPane sp) {
            sp.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
                @Override protected void configureScrollBarColors() { thumbColor = BORDER; trackColor = BG_PANEL; }
                @Override protected JButton createDecreaseButton(int o) { return zeroBtn(); }
                @Override protected JButton createIncreaseButton(int o) { return zeroBtn(); }
                JButton zeroBtn() { JButton b = new JButton(); b.setPreferredSize(new Dimension(0,0)); return b; }
            });
        }
    }

    // =========================================================================
    //  SCREEN: GAME  (world / adventure — replaces the old JTextPane display)
    // =========================================================================
    static class GameScreen extends BaseScreen {
        // HUD
        private JLabel    hudName;
        private BarWidget hpBar;
        private BarWidget energyBar;

        // Story display
        private JTextPane      display;
        private StyledDocument doc;

        // Button bar
        private JPanel buttonBar;

        GameScreen(GameEngine engine) {
            setLayout(new BorderLayout(0, 0));

            // ── HUD (top) ──────────────────────────────────────────────────
            JPanel hud = new JPanel(new FlowLayout(FlowLayout.LEFT, 18, 8));
            hud.setBackground(BG_PANEL);
            hud.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, BORDER));
            hud.setPreferredSize(new Dimension(0, 52));

            hudName   = new JLabel("No Hero");
            hudName.setFont(MONO_MD);
            hudName.setForeground(GOLD);

            hpBar     = new BarWidget("HP",     C_HP,     C_HP_BG,     100, 100);
            energyBar = new BarWidget("Energy", C_ENERGY, C_ENERGY_BG, 100, 100);

            hud.add(hudName);
            hud.add(Box.createHorizontalStrut(20));
            hud.add(hpBar);
            hud.add(Box.createHorizontalStrut(12));
            hud.add(energyBar);
            add(hud, BorderLayout.NORTH);

            // ── Story display (center) ─────────────────────────────────────
            display = new JTextPane();
            display.setEditable(false);
            display.setBackground(BG);
            display.setFont(MONO_MD);
            display.setBorder(new EmptyBorder(16, 24, 16, 24));
            doc = display.getStyledDocument();

            registerStyles(display);

            JScrollPane scroll = new JScrollPane(display);
            scroll.setBorder(BorderFactory.createEmptyBorder());
            scroll.getViewport().setBackground(BG);
            scroll.setBackground(BG);
            scroll.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
                @Override protected void configureScrollBarColors() { thumbColor = BORDER; trackColor = BG_PANEL; }
                @Override protected JButton createDecreaseButton(int o) { return zeroBtn(); }
                @Override protected JButton createIncreaseButton(int o) { return zeroBtn(); }
                JButton zeroBtn() { JButton b = new JButton(); b.setPreferredSize(new Dimension(0,0)); return b; }
            });
            add(scroll, BorderLayout.CENTER);

            // ── Button bar (bottom) ────────────────────────────────────────
            buttonBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 10));
            buttonBar.setBackground(BG_PANEL);
            buttonBar.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, BORDER));
            buttonBar.setPreferredSize(new Dimension(0, 64));
            add(buttonBar, BorderLayout.SOUTH);
        }

        // ── Write helpers ─────────────────────────────────────────────────
        void write(String text, String style) {
            SwingUtilities.invokeLater(() -> {
                try {
                    Style s = display.getStyle(style);
                    if (s == null) s = display.getStyle(S_NORMAL);
                    doc.insertString(doc.getLength(), text + "\n", s);
                    display.setCaretPosition(doc.getLength());
                } catch (BadLocationException e) { e.printStackTrace(); }
            });
        }
        void write(String text) { write(text, S_NORMAL); }

        void clearDisplay() {
            SwingUtilities.invokeLater(() -> {
                try { doc.remove(0, doc.getLength()); }
                catch (BadLocationException e) { e.printStackTrace(); }
            });
        }

        void setButtons(String[] labels, Color[] colors, Runnable[] actions) {
            SwingUtilities.invokeLater(() -> {
                buttonBar.removeAll();
                for (int i = 0; i < labels.length; i++) {
                    final Runnable action = actions[i];
                    GlowButton btn = new GlowButton(labels[i], colors[i]);
                    btn.setPreferredSize(new Dimension(btn.getPreferredSize().width + 28, 42));
                    btn.addActionListener(e -> action.run());
                    buttonBar.add(btn);
                }
                buttonBar.revalidate();
                buttonBar.repaint();
            });
        }

        void showContinueButton(Runnable next) {
            setButtons(new String[]{"Continue  →"}, new Color[]{GOLD}, new Runnable[]{next});
        }

        void updateHUD(Character player) {
            SwingUtilities.invokeLater(() -> {
                hudName.setText("⚔  " + player.getName());
                hpBar.update(player.getHp(), player.getMaxHP());
                energyBar.update(player.getEnergy(), player.getMaxEnergy());
                revalidate(); repaint();
            });
        }

        private static void registerStyles(JTextPane pane) {
            reg(pane, S_NORMAL,  TEXT,    MONO_MD,  false, false);
            reg(pane, S_STORY,   TEXT,    SERIF_LG, false, false);
            reg(pane, S_EFFECT,  GOLD,    MONO_LG,  true,  false);
            reg(pane, S_TITLE,   CYAN,    MONO_LG,  true,  false);
            reg(pane, S_CYAN,    CYAN,    MONO_MD,  false, false);
            reg(pane, S_GOLD,    GOLD,    MONO_MD,  false, false);
            reg(pane, S_GREEN,   GREEN,   SERIF_LG, false, true);
            reg(pane, S_RED,     RED,     MONO_MD,  false, false);
            reg(pane, S_MAGENTA, MAGENTA, MONO_MD,  false, false);
            reg(pane, S_DIM,     TEXT_DIM,MONO_SM,  false, false);
        }

        private static void reg(JTextPane p, String name, Color fg, Font font, boolean bold, boolean italic) {
            Style s = p.addStyle(name, StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE));
            StyleConstants.setForeground(s, fg);
            StyleConstants.setFontFamily(s, font.getFamily());
            StyleConstants.setFontSize(s, font.getSize());
            StyleConstants.setBold(s, bold || (font.getStyle() & Font.BOLD) != 0);
            StyleConstants.setItalic(s, italic || (font.getStyle() & Font.ITALIC) != 0);
        }
    }

    // =========================================================================
    //  STORY ENGINE LOGIC  (all write/delay calls go through gameScreen)
    // =========================================================================

    // ── Convenience forwarders ────────────────────────────────────────────────
    private void write(String text, String style) { gameScreen.write(text, style); }
    private void write(String text)               { gameScreen.write(text); }
    private void clearDisplay()                   { gameScreen.clearDisplay(); }
    private void setButtons(String[] l, Color[] c, Runnable[] a) { gameScreen.setButtons(l, c, a); }
    private void showContinueButton(Runnable next) { gameScreen.showContinueButton(next); }

    // ── Exam flash transition (played while CHARSELECT shows) ─────────────────
    public void startExamTransition() {
        // Visual glitch scene plays in background before char select is used
        // (nothing to do here unless you want a cutscene overlay)
    }

    // ── CHARACTER PREVIEW  (called from CharSelectScreen hero buttons) ─────────
    public void previewCharacter(int choice, JTextArea detailArea) {
        selectedTemp = switch (choice) {
            case 1 -> new Kael();
            case 2 -> new Karl();
            case 3 -> new Simon();
            case 4 -> new Null();
            default -> null;
        };
        if (selectedTemp == null) return;

        String[] banners = {
                "        ▄█   ▄█▄    ▄████████    ▄████████   ▄█\n" +
                        "       ███ ▄███▀   ███    ███   ███    ███  ███\n" +
                        "       ███▐██▀     ███    ███   ███    █▀   ███\n" +
                        "       ▄█████▀      ███    ███  ▄███▄▄▄     ███\n" +
                        "      ▀▀█████▄    ▀███████████ ▀▀███▀▀▀     ███\n" +
                        "       ███▐██▄     ███    ███   ███    █▄   ███\n" +
                        "       ███ ▀███▄   ███    ███   ███    ███  ███▌    ▄\n" +
                        "       ███   ▀█▀   ███    █▀    ██████████  █████▄▄██",

                "          ▄█   ▄█▄    ▄████████    ▄████████   ▄█\n" +
                        "         ███ ▄███▀   ███    ███   ███    ███  ███\n" +
                        "         ███▐██▀     ███    ███   ███    ███  ███\n" +
                        "        ▄█████▀      ███    ███  ▄███▄▄▄▄██▀  ███\n" +
                        "        ▀▀█████▄    ▀███████████ ▀▀███▀▀▀▀▀   ███\n" +
                        "          ███▐██▄     ███    ███ ▀███████████ ███\n" +
                        "          ███ ▀███▄   ███    ███   ███    ███ ███▌    ▄\n" +
                        "          ███   ▀█▀   ███    █▀    ███    ███ █████▄▄██",

                "        ▄████████  ▄█    ▄▄▄▄███▄▄▄▄    ▄██████▄  ███▄▄▄▄\n" +
                        "       ███    ███ ███  ▄██▀▀▀███▀▀▀██▄ ███    ███ ███▀▀▀██▄\n" +
                        "       ███    █▀  ███▌ ███   ███   ███ ███    ███ ███   ███\n" +
                        "      ▀███████████ ███▌ ███   ███   ███ ███    ███ ███   ███\n" +
                        "               ███ ███  ███   ███   ███ ███    ███ ███   ███\n" +
                        "        ▄█    ███ ███  ███   ███   ███ ███    ███ ███   ███\n" +
                        "      ▄████████▀  █▀    ▀█   ███   █▀   ▀██████▀   ▀█   █▀",

                "                 ███▄▄▄▄   ███    █▄   ▄█        ▄█\n" +
                        "                ███▀▀▀██▄ ███    ███ ███       ███\n" +
                        "                ███   ███ ███    ███ ███       ███\n" +
                        "                ███   ███ ███    ███ ███       ███\n" +
                        "                ███   ███ ███    ███ ███▌    ▄ ███▌    ▄\n" +
                        "                 ▀█   █▀  ████████▀  █████▄▄██ █████▄▄██"
        };

        String[] arts = { KAEL_ART, KARL_ART, SIMON_ART, NULL_ART };

        String backstory = selectedTemp.showBackstory();
        String bs = (backstory != null && !backstory.isBlank()) ? backstory : "(No backstory available)";

        detailArea.setFont(MONO_SM);
        detailArea.setText(
                banners[choice - 1] + "\n\n" +
                        arts[choice - 1] + "\n\n" +
                        "━━━  Stats  ━━━\n" +
                        "Name     : " + selectedTemp.getName()       + "\n" +
                        "Class    : " + selectedTemp.getClassType()  + "\n" +
                        "HP       : " + selectedTemp.getMaxHP()      + "\n" +
                        "Energy   : " + selectedTemp.getMaxEnergy()  + "  (" + selectedTemp.getEnergyName() + ")\n" +
                        "Base ATK : " + selectedTemp.getBaseAttack() + "\n" +
                        "Base DEF : " + selectedTemp.getBaseDefense()+ "\n\n" +
                        "━━━  Backstory  ━━━\n" +
                        bs
        );
        detailArea.setCaretPosition(0);
    }

    // ── CONFIRM CHARACTER ──────────────────────────────────────────────────────
    public void confirmCharacter() {
        if (selectedTemp == null) {
            showAlert("Please select a character first!");
            return;
        }
        this.player = selectedTemp;

        switch (player.getClassType()) {
            case "Archer" -> player.getInventory().setEquippedWeapon(Bow.WOODEN_BOW);
            case "Mage"   -> player.getInventory().setEquippedWeapon(Staff.WOODEN_STAFF);
            default       -> player.getInventory().setEquippedWeapon(Sword.OLD_BROADSWORD);
        }
        player.getInventory().setEquippedArmor(Armor.LEATHER_GUARD);
        player.recalculateBuffs();
        SoundUtil.playLoop("charSelect.wav", 0.1f);

        showScreen("GAME");
        gameScreen.clearDisplay();

        String emoji = switch (player.getClassType()) {
            case "Archer" -> "🏹"; case "Mage" -> "🧙"; default -> "⚔️";
        };
        write("┌────────────────────────────────────────────────────┐", S_CYAN);
        write("  " + emoji + "  You have chosen " + player.getName().toUpperCase() + "!", S_CYAN);
        write("└────────────────────────────────────────────────────┘\n", S_CYAN);

        delay(800, () -> {
            write("✨ The gods bestow upon you your starting gear...", S_EFFECT);
            delay(800, () -> {
                String wep = switch (player.getClassType()) {
                    case "Archer" -> "🏹 *The --" + player.getInventory().getEquippedWeapon().getName() + "-- hums softly as you draw the string.*";
                    case "Mage"   -> "🔮 *The --" + player.getInventory().getEquippedWeapon().getName() + "-- pulses faintly, whispering forgotten spells.*";
                    default       -> "🗡️ *The --" + player.getInventory().getEquippedWeapon().getName() + "-- rests firmly in your grasp.*";
                };
                write(wep, S_GOLD);
                delay(800, () -> {
                    write("🛡️ *The --" + player.getInventory().getEquippedArmor().getName() + "-- fits perfectly, worn yet dependable.*", S_GOLD);
                    delay(800, () -> {
                        String close = switch (player.getClassType()) {
                            case "Archer" -> "🌿 The forest seems to watch over you as your path unfolds...";
                            case "Mage"   -> "💫 Magic stirs in the air around you as your journey begins...";
                            default       -> "⚡ Strength surges through your veins as your journey begins...";
                        };
                        write(close, S_EFFECT);
                        if (player.getClassType().equals("Archer"))
                            write("✨ *You are granted a --Magic Quiver--, arrows that regenerate through ancient magic.*", S_GOLD);
                        gameScreen.updateHUD(player);
                        SoundUtil.stopLoop();
                        delay(1200, this::beginWorld1);
                    });
                });
            });
        });
    }

    // =========================================================================
    //  WORLD 1
    // =========================================================================
    private void beginWorld1() {
        currWorldLevel = 1;
        clearDisplay();
        write("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━", S_TITLE);
        write("                      WORLD 1 BEGINS                   ", S_TITLE);
        write("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n", S_TITLE);
        write("The forest stretches endlessly before you...", S_STORY);
        write("The air is thick with an ancient silence.\n", S_STORY);
        showAdventureButtons();
        // TODO: World1 w1 = new World1(); w1.startScene(player, this, () -> transitionToWorld2());
    }

    // =========================================================================
    //  WORLD 2 TRANSITION
    // =========================================================================
    public void transitionToWorld2() {
        currWorldLevel = 2;
        clearDisplay();
        write("─".repeat(55), S_DIM);
        write("Sir Khai's staff strikes the scorched earth with a resonant hum.", S_EFFECT);
        delay(1000, () -> {
            write("", S_NORMAL);
            write("           * .   * .      *", S_GREEN);
            write("       .      _\\|/_   * _\\|/_      .", S_GREEN);
            write("      * /|\\     .   /|\\        *", S_GREEN);
            write("    .    _\\|/_  |  _\\|/_    |    _\\|/_", S_GREEN);
            write("          /|\\   |   /|\\     |     /|\\", S_GREEN);
            write("   ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n", S_GREEN);
            delay(800, () -> {
                write("The forest around you shudders — not in pain, but in relief.", S_STORY);
                write("Gray bark cracks to reveal rich brown wood, ash blooms into lush green moss.", S_STORY);
                write("The corruption fades, leaving faint sparks of life glowing in the air.\n", S_STORY);
                delay(2000, () -> {
                    write("\"This forest is saved. Life is beautiful,\" Sir Khai murmurs.", S_GREEN);
                    write("\"But our journey is far from over. Two more Stones remain… and darkness gathers ahead.\"\n", S_GREEN);
                    delay(1500, () -> {
                        write("A path parts through the trees, leading out of the forest...", S_EFFECT);
                        write("It winds toward a valley shrouded in thick fog and distant thunder.\n", S_EFFECT);
                        showContinueButton(this::beginWorld2);
                    });
                });
            });
        });
    }

    private void beginWorld2() {
        clearDisplay();
        write("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━", S_TITLE);
        write("                      WORLD 2 BEGINS                   ", S_TITLE);
        write("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n", S_TITLE);
        showAdventureButtons();
        // TODO: World2 w2 = new World2(); w2.startScene(player, this, () -> transitionToWorld3());
    }

    // =========================================================================
    //  WORLD 3 TRANSITION
    // =========================================================================
    public void transitionToWorld3() {
        currWorldLevel = 3;
        clearDisplay();
        write("─".repeat(55), S_DIM);
        write("The Second Stone vibrates violently, reacting to Khai's presence.", S_EFFECT);
        delay(1000, () -> {
            write("Sir Khai's staff ignites with a brilliant SILVER FLAME.", S_STORY);
            write("Outside, the sky begins to twist unnaturally.\n", S_STORY);
            delay(1500, () -> {
                write("              .     :             .   ", S_CYAN);
                write("           / \\    |    |        _   ", S_CYAN);
                write("   |   |    STORM  RISING   /     | ", S_CYAN);
                write("___|___|___________________|_______|___\n", S_CYAN);
                delay(1000, () -> {
                    write("Thunder rumbles across a storm-choked horizon.", S_EFFECT);
                    write("The air grows colder — as if something ancient is waking.\n", S_EFFECT);
                    delay(2000, () -> {
                        write("\"The final trial awaits,\" Khai says quietly.", S_GREEN);
                        write("\"Beyond that storm lies a realm where even light cannot survive...\"", S_GREEN);
                        write("\"That is where the Last Stone is kept. And where the Necromancer waits.\"\n", S_GREEN);
                        showContinueButton(this::beginWorld3);
                    });
                });
            });
        });
    }

    private void beginWorld3() {
        clearDisplay();
        write("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━", S_TITLE);
        write("                      WORLD 3 BEGINS                   ", S_TITLE);
        write("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n", S_TITLE);
        showAdventureButtons();
        // TODO: World3 w3 = new World3(); w3.startScene(player, this, () -> beginWorld3Final());
    }

    // =========================================================================
    //  ADVENTURE BUTTONS
    // =========================================================================
    public void showAdventureButtons() {
        setButtons(
                new String[]  {"⚔  Fight",       "🎒  Inventory",      "📊  Stats",        "✖  Quit"},
                new Color[]   { RED,               GOLD,                  CYAN,               TEXT_DIM},
                new Runnable[]{ this::handleFight,  this::handleInventory, this::handleStats,  this::confirmQuit}
        );
    }

    private void handleFight()     { write("\n⚔️  Entering combat...", S_EFFECT); /* TODO: launch BattleScreen */ }
    private void handleInventory() { write("\n🎒  Opening inventory...", S_EFFECT); /* TODO: show inventory */ }
    private void handleStats() {
        if (player == null) return;
        clearDisplay();
        write("━━━  " + player.getName() + " — Stats  ━━━\n", S_TITLE);
        write("Name     : " + player.getName(),           S_GOLD);
        write("Class    : " + player.getClassType(),      S_GOLD);
        write("Level    : " + player.getLevel(),          S_NORMAL);
        write("HP       : " + player.getHp() + " / " + player.getMaxHP(), S_NORMAL);
        write("Energy   : " + player.getEnergy() + " / " + player.getMaxEnergy() + "  (" + player.getEnergyName() + ")", S_NORMAL);
        write("Attack   : " + player.getAttack()  + "  (Base " + player.getBaseAttack()  + ")", S_NORMAL);
        write("Defense  : " + player.getDefense() + "  (Base " + player.getBaseDefense() + ")", S_NORMAL);
        write("Weapon   : " + (player.getWeapon() != null ? player.getWeapon().getName() : "None"), S_GOLD);
        write("Armor    : " + (player.getArmor()  != null ? player.getArmor().getName()  : "None"), S_GOLD);
        write("", S_NORMAL);
        showAdventureButtons();
    }

    // =========================================================================
    //  QUIT
    // =========================================================================
    private void confirmQuit() {
        int r = JOptionPane.showConfirmDialog(window,
                "To confirm exit, type the exact phrase in the next dialog.",
                "Quit Game", JOptionPane.YES_NO_OPTION);
        if (r != JOptionPane.YES_OPTION) return;

        String phrase = JOptionPane.showInputDialog(window,
                "Type exactly:\nOBJECT ORIENTED PROGRAMMING", "Confirm Exit", JOptionPane.PLAIN_MESSAGE);

        if ("OBJECT ORIENTED PROGRAMMING".equals(phrase)) {
            SoundUtil.stopLoop();
            SoundUtil.stopLoop2();
            write("\n═════════════════════════════════", S_GOLD);
            write("     Thank you for playing!      ", S_GOLD);
            write("    Your journey ends here...    ", S_GOLD);
            write("═════════════════════════════════\n", S_GOLD);
            delay(1500, () -> System.exit(0));
        } else if (phrase != null) {
            write("❌ Incorrect phrase. Returning to game...", S_RED);
        }
    }

    public static void quitGame() {
        int c = JOptionPane.showConfirmDialog(null, "Quit the game?", "Exit", JOptionPane.YES_NO_OPTION);
        if (c == JOptionPane.YES_OPTION) System.exit(0);
    }

    // =========================================================================
    //  ASCII ART
    // =========================================================================
    private static final String KAEL_ART =
            "                             .\n" +
                    "     .                      *#@*:\n" +
                    "      =*                  -%@#  =#\n" +
                    "        =@=                #@@##*\n" +
                    "           +%-           =@@@@@@@=\n" +
                    "              +%=       :%@@@%%@@@@:\n" +
                    "                -#*+# :%%+#@@@@@%@@@@@*=:\n" +
                    "                   #*@*::*%@@@@@@@@@@@@@%#=\n" +
                    "                       ++-#@@%@@@@@@@@@@@@@@@@*:\n" +
                    "                        =%@@@%@@@@@@@@@@@@@@@@@@@%.\n" +
                    "                       #@@@@@@@@%%@@@@@@@@@@@@#=.:%+\n" +
                    "                      %@@@@@@@@@@@%@@@@@@@@@%##.\n" +
                    "                     =@@@@@@@@@@%@@@@@@@@@@@@.\n" +
                    "                      @@@@@@@@@@@@@@@@%%#@#:\n" +
                    "                      @#  =. %@-:+@@%@%    .:.\n" +
                    "                      @=        . +@=\n" +
                    "                     *@=           :%\n" +
                    "                .%@@@@@@%#*-        ##\n" +
                    "                  #@@@@@@@@@@@@@@@@@@@@%\n" +
                    "                 :*@@@@@@@@@@@@@@@@@@@@@@*\n" +
                    "                    :-==++*******++=-:.";

    private static final String KARL_ART =
            "                                    +\n" +
                    "                                      +  -\n" +
                    "                         =-  *@@+      .  +- . .\n" +
                    "                         :-  #%#@:        +:    .\n" +
                    "                         .*%%@#%#*        .     .  .\n" +
                    "              :.:     .##%%%@%#*#%%@@@%#+-%*    ...\n" +
                    "               ::.       %@@@@%#%%=       .     :\n" +
                    "                 .     +@@@@%%@@#%%:      ==    .\n" +
                    "                     +@@@@@@%%@@%#@*    . =-  .\n" +
                    "                  -#@@@@@@@%%%%%###@+     -\n" +
                    "                +%@@@@@@@@@%%%@@@%%#@#  .\n" +
                    "              =%@@@@@@@@@@%@%%%@%#@%%@#*\n" +
                    "             *#@@@@@@@@@@@%%@%@@%%%%#%@@%*\n" +
                    "            +##@@@@@@@@@@%@@@%@@@%@@#%@@@@#-\n" +
                    "            -#    #@@@@@%@@@@%@@@%@@@@@@@@@#+\n" +
                    "             -    #@@@@@%@@@%%@#%%@@@@@@@@%#*\n" +
                    "                   -%@%=#@@@%%@@#%%@@@@@    *\n" +
                    "                        %#.=@@%%    %=\n" +
                    "                       -%.          -%\n" +
                    "                    :=+@@@@@@@@@@@@@@@@*+-\n" +
                    "                 =%@@@%%@@@@@@@@@@@@@@@%@@%#-\n" +
                    "                      .:=#%%%##%%%##*=:.";

    private static final String SIMON_ART =
            "                         .\n" +
                    "                          -@*\n" +
                    "                           %@*\n" +
                    "                         .*##+.\n" +
                    "             :         =%@@%####=\n" +
                    "         = @#=:          *%%@@*          .-\n" +
                    "          -*-+         :#%##@##*       .=.\n" +
                    "             +        *%%%+#%-@%%*.  .#%-\n" +
                    "             ::      *####%%%%#%@@%%@++\n" +
                    "              :     +#@@%@@@@@@%@#%@@#@.\n" +
                    "               +=+%@@@@%%@@@@%%%@@%.:*%\n" +
                    "               =##@@@%%%%#******#@@%\n" +
                    "                -##@*@%#*#%#%*+%%@@@%\n" +
                    "                 =+:.%#%@%%%@+@##%@@@@+\n" +
                    "                  . *@%#@%@@@#%%%%@@@@@#:\n" +
                    "                  - @@@%@@%@@@@@@@@@@@@@@#+-\n" +
                    "                   #@@%@@@#@@%@@@@@@@@@@@@@%#\n" +
                    "                    @@#@@@@*@@@@@@%%@@%@@@@@%@-\n" +
                    "                     =%@@@@@@@*@@@%#=@@-      :\n" +
                    "                      *@@@@@@@@%##@@@@@#+\n" +
                    "                      @@@@@@@@@@@@@@@%%%:\n" +
                    "                 :=+####%@@@@@@@@@%%%##*+=-";

    private static final String NULL_ART =
            "                   .=:                             :-.\n" +
                    "                   -@%%%#*+-.               .-+*##%%@-\n" +
                    "                   -%**#####%**@%%%%##%%%%++###*###%@-\n" +
                    "                   -@%%#####%**%#+=+*##%@@+*%####%%%@-\n" +
                    "           .%#-    -@%@%##%##**%%###%%#%%@++%%%%%%%%@-     .+%#.\n" +
                    "           :%%%%%*..+#%@@@%#%**%%%%@@@@#%%++%%%%@@%*+..+%%%%%%%:\n" +
                    "           :%%#=*#@*%%%%%%##+:-+=+%@@@@%==::+#%%##%%%*###*+##%%:\n" +
                    "           :%%#+*%@*%%#+*####+*##%@@@@@@%#++###%%##%%*%%###%%%%:\n" +
                    "           :%%%#*%%*%###%@@%%+*@@@@@@@@@@@%###%@@###%*%%%%%%%%%:\n" +
                    "           :%%#%%+#*%%%%%%@@#%@@@@@@@@@@@@@@%#@@#*##%*%%###*##%:\n" +
                    "            =+++***=#####*%@@@@#%@@@@@@@@%#@@@@#*####+*****+++=\n" +
                    "           .+*+=-::.......:%@#:.*@@@@@@@@=.-#@*......\n" +
                    "           :@@@@%#*##%@%*%%%#%##%@@@@@@@@##%@%%@%###%@-\n" +
                    "           :@@@@@@@%%%%@%%%%%%%%@@@@@@@@@@%@@@@@@@@%@@%.\n" +
                    "           :@@@@@@@@@@@@=     :%@@@%:.#@@@@=     .%@@@@@@@#\n" +
                    "           :@@@@@@@@@@#:      *@@@#.   =@@@%.    .%@@@@@@%\n" +
                    "           :@@@@@@@@@*       :@@@*      -@@@+    .%@@@@@@.\n" +
                    "           :@@@@@@@@@+       #@@#.       +@@%    .%@@@@@+\n" +
                    "           :@@@@@@@@@@@*.   *@#:          .*@#   .%@%@%@%:\n" +
                    "           :@@@@@@@@%%%%%%%#@@%#*+*######%#%%@#*#####@@@#%.";

    // =========================================================================
    //  SHARED INNER COMPONENTS  (ported from Game.java)
    // =========================================================================

    // ── CardPanel ─────────────────────────────────────────────────────────────
    static class CardPanel extends JPanel {
        private final CardLayout layout = new CardLayout();
        CardPanel() { setLayout(layout); setBackground(BG); }
        void show(String name) { layout.show(this, name); }
    }

    // ── BaseScreen (scanline effect) ──────────────────────────────────────────
    static class BaseScreen extends JPanel {
        BaseScreen() { setBackground(BG); setLayout(new BorderLayout()); }

        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(SCAN_LINE);
            for (int y = 0; y < getHeight(); y += 3)
                g2.drawLine(0, y, getWidth(), y);
        }
    }

    // ── GlowButton ────────────────────────────────────────────────────────────
    static class GlowButton extends JButton {
        private final Color accent;
        private boolean hover = false;

        GlowButton(String text, Color accent) {
            super(text);
            this.accent = accent;
            setFont(MONO_MD);
            setForeground(accent);
            setBackground(BG_CARD);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { hover = true;  repaint(); }
                public void mouseExited (MouseEvent e) { hover = false; repaint(); }
            });
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            RoundRectangle2D r = new RoundRectangle2D.Float(1, 1, getWidth()-2, getHeight()-2, 8, 8);
            g2.setColor(hover ? new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 35) : new Color(20, 25, 42));
            g2.fill(r);
            if (hover) {
                g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 60));
                g2.setStroke(new BasicStroke(3f));
                g2.draw(r);
            }
            g2.setColor(hover ? accent : BORDER);
            g2.setStroke(new BasicStroke(1.5f));
            g2.draw(r);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ── HeroButton ────────────────────────────────────────────────────────────
    static class HeroButton extends JPanel {
        private boolean hover = false;
        private final String name, role;
        private final java.util.List<ActionListener> listeners = new java.util.ArrayList<>();

        HeroButton(String name, String role) {
            this.name = name; this.role = role;
            setPreferredSize(new Dimension(160, 68));
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { hover = true;  repaint(); }
                public void mouseExited (MouseEvent e) { hover = false; repaint(); }
                public void mouseClicked(MouseEvent e) {
                    ActionEvent ae = new ActionEvent(HeroButton.this, ActionEvent.ACTION_PERFORMED, "");
                    listeners.forEach(l -> l.actionPerformed(ae));
                }
            });
        }

        public void addActionListener(ActionListener l) { listeners.add(l); }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color border = hover ? GOLD : BORDER;
            RoundRectangle2D r = new RoundRectangle2D.Float(1, 1, getWidth()-2, getHeight()-2, 10, 10);
            g2.setColor(hover ? new Color(220, 175, 70, 25) : BG_CARD);
            g2.fill(r);
            g2.setColor(border);
            g2.setStroke(new BasicStroke(hover ? 2f : 1f));
            g2.draw(r);
            g2.setFont(MONO_MD); g2.setColor(hover ? GOLD : TEXT);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(name, (getWidth() - fm.stringWidth(name)) / 2, 30);
            g2.setFont(UI_SM); g2.setColor(TEXT_DIM);
            fm = g2.getFontMetrics();
            g2.drawString(role, (getWidth() - fm.stringWidth(role)) / 2, 48);
            g2.dispose();
        }
    }

    // ── CardBackground ────────────────────────────────────────────────────────
    static class CardBackground extends JPanel {
        CardBackground() { setOpaque(false); }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            RoundRectangle2D r = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16);
            g2.setColor(BG_CARD); g2.fill(r);
            g2.setColor(BORDER);  g2.setStroke(new BasicStroke(1.5f)); g2.draw(r);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ── BarWidget (HUD hp/energy bars) ────────────────────────────────────────
    static class BarWidget extends JPanel {
        private final String label;
        private final Color fill, bg;
        private int cur, max;

        BarWidget(String label, Color fill, Color bg, int cur, int max) {
            this.label = label; this.fill = fill; this.bg = bg;
            this.cur = cur; this.max = max;
            setPreferredSize(new Dimension(200, 36)); setOpaque(false);
        }

        void update(int cur, int max) { this.cur = cur; this.max = max; repaint(); }

        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int barY = getHeight()/2, barH = 10, barW = getWidth() - 60;
            g2.setFont(MONO_SM); g2.setColor(TEXT_DIM); g2.drawString(label, 0, barY - 2);
            g2.setColor(bg);   g2.fillRoundRect(0, barY+2, barW, barH, 6, 6);
            int fillW = max > 0 ? (int)((cur/(float)max)*barW) : 0;
            g2.setColor(fill); g2.fillRoundRect(0, barY+2, fillW, barH, 6, 6);
            g2.setColor(BORDER); g2.setStroke(new BasicStroke(1f)); g2.drawRoundRect(0, barY+2, barW, barH, 6, 6);
            g2.setFont(MONO_SM); g2.setColor(TEXT); g2.drawString(cur+"/"+max, barW+6, barY+10);
            g2.dispose();
        }
    }

    // =========================================================================
    //  STATIC FACTORY HELPERS  (from Game.java)
    // =========================================================================
    static JLabel styledLabel(String text, Color color, Font font) {
        JLabel l = new JLabel(text); l.setFont(font); l.setForeground(color); return l;
    }

    static JLabel fieldLabel(String text) {
        JLabel l = new JLabel(text); l.setFont(UI_MD); l.setForeground(TEXT_DIM);
        l.setAlignmentX(LEFT_ALIGNMENT); return l;
    }

    static JTextField styledField(String placeholder) {
        JTextField f = new JTextField();
        f.setFont(MONO_MD); f.setForeground(TEXT);
        f.setBackground(new Color(12, 15, 26));
        f.setCaretColor(CYAN);
        f.setBorder(fieldBorder());
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        f.setText(placeholder); f.setForeground(TEXT_DIM);
        f.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (f.getText().equals(placeholder)) { f.setText(""); f.setForeground(TEXT); }
            }
            public void focusLost(FocusEvent e) {
                if (f.getText().isBlank()) { f.setText(placeholder); f.setForeground(TEXT_DIM); }
            }
        });
        return f;
    }

    static Border fieldBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(6, 10, 6, 10)
        );
    }

    static JTextArea monoArea(String text, Color color, Font font) {
        JTextArea a = new JTextArea(text);
        a.setFont(font); a.setForeground(color);
        a.setBackground(new Color(0, 0, 0, 0));
        a.setOpaque(false); a.setEditable(false); a.setFocusable(false);
        return a;
    }
}