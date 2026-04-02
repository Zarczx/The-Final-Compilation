package GameGUI.engine;

import GameGUI.model.HeroData;
import javax.swing.*;
import java.awt.*;

/**
 * GameTester — A standalone launcher for developers.
 * Bypasses the main menu and intro to test specific worlds and features instantly.
 */
public class GameTester {

    public static void main(String[] args) {
        // 1. Build custom dev menu dialog
        JDialog dialog = new JDialog((Frame) null, "Developer Test Menu", true);
        dialog.setLayout(new BorderLayout());
        dialog.setResizable(false);

        JLabel title = new JLabel("WHERE DO YOU WANT TO WARP?", SwingConstants.CENTER);
        title.setFont(new Font("Monospaced", Font.BOLD, 14));
        title.setForeground(new Color(220, 200, 120));
        title.setBackground(new Color(20, 18, 36));
        title.setOpaque(true);
        title.setBorder(BorderFactory.createEmptyBorder(16, 20, 12, 20));
        dialog.add(title, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(20, 18, 36));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(6, 16, 16, 16));

        int[] chosen = {-1};

        // Helper to build a section
        class Section {
            void add(JPanel parent, String sectionTitle, String[] btnLabels, int startIndex) {
                JLabel lbl = new JLabel("  " + sectionTitle);
                lbl.setFont(new Font("Monospaced", Font.BOLD, 11));
                lbl.setForeground(new Color(140, 125, 95));
                lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
                lbl.setBorder(BorderFactory.createEmptyBorder(10, 0, 4, 0));
                parent.add(lbl);

                JPanel row = null;
                for (int i = 0; i < btnLabels.length; i++) {
                    if (i % 5 == 0) {
                        row = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
                        row.setBackground(new Color(20, 18, 36));
                        row.setAlignmentX(Component.LEFT_ALIGNMENT);
                        parent.add(row);
                    }
                    final int idx = startIndex + i;
                    JButton btn = new JButton(btnLabels[i]);
                    btn.setFont(new Font("Monospaced", Font.BOLD, 11));
                    btn.setForeground(Color.WHITE);
                    btn.setBackground(new Color(38, 35, 60));
                    btn.setFocusPainted(false);
                    btn.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(new Color(80, 70, 110), 1),
                            BorderFactory.createEmptyBorder(6, 10, 6, 10)));
                    btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    btn.addMouseListener(new java.awt.event.MouseAdapter() {
                        public void mouseEntered(java.awt.event.MouseEvent e) {
                            btn.setBackground(new Color(70, 60, 110));
                        }
                        public void mouseExited(java.awt.event.MouseEvent e) {
                            btn.setBackground(new Color(38, 35, 60));
                        }
                    });
                    btn.addActionListener(e -> {
                        chosen[0] = idx;
                        dialog.dispose();
                    });
                    row.add(btn);
                }
            }
        }
        Section sec = new Section();

        // ── GENERAL ──
        sec.add(mainPanel, "GENERAL", new String[]{
                "Start Normally"
        }, 0);

        // ── WORLD 1 ──
        sec.add(mainPanel, "WORLD 1", new String[]{
                "Full W1 Run",
                "Rotfang Wolf",
                "Shade Sprite",
                "Dreadbark Treant",
                "Carrion Bat",
                "Hollow Stag (Boss)"
        }, 1);

        // ── WORLD 2 ──
        sec.add(mainPanel, "WORLD 2", new String[]{
                "Full W2 Run",
                "Plague Vermin",
                "Forsaken Cultist",
                "Blight Hound",
                "Ghoul Footman",
                "Black Jailer",
                "Luther Von (Boss)"
        }, 7);

        // ── WORLD 3 ──
        sec.add(mainPanel, "WORLD 3", new String[]{
                "Full W3 Run",
                "Flame Revenant",
                "Bone Warlock",
                "Obsidian Crusher",
                "Soulflayer Gargoyle",
                "Zyrryl (Mini-Boss)"
        }, 14);

        // ── MISC ──
        sec.add(mainPanel, "MISC", new String[]{
                "Magic Shop",
                "Final Boss (Khai)"
        }, 20);

        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);

        if (chosen[0] == -1) System.exit(0);

        // 2. Build the main game window
        JFrame window = new JFrame("The Final Compilation — TEST MODE");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.getContentPane().setPreferredSize(new Dimension(1280, 720));
        window.pack();
        window.setLocationRelativeTo(null);
        window.setResizable(false);
        window.setLayout(new BorderLayout());

        GameScreen mainScreen = new GameScreen();
        window.add(mainScreen, BorderLayout.CENTER);
        window.setVisible(true);

        // 3. Grab a default hero for testing (Kael)
        HeroData.HeroDefinition testHero = HeroData.HEROES.get(0);

        // 4. Track if we land in a battle so we can enable dev tools
        final boolean[] battleStarted = {false};

        // 5. Execute the chosen warp
        switch (chosen[0]) {
            case 0  -> System.out.println("Starting normally...");

            // World 1
            case 1  -> { mainScreen.debugSkipToWorld1Battle(testHero);                          battleStarted[0] = true; }
            case 2  -> { mainScreen.debugSkipToEnemy(testHero, "Rotfang Wolf",        1);       battleStarted[0] = true; }
            case 3  -> { mainScreen.debugSkipToEnemy(testHero, "Shade Sprite",        1);       battleStarted[0] = true; }
            case 4  -> { mainScreen.debugSkipToEnemy(testHero, "Dreadbark Treant",    1);       battleStarted[0] = true; }
            case 5  -> { mainScreen.debugSkipToEnemy(testHero, "Carrion Bat",         1);       battleStarted[0] = true; }
            case 6  -> { mainScreen.debugSkipToHollowStag(testHero);                            battleStarted[0] = true; }

            // World 2
            case 7  -> { mainScreen.debugSkipToWorld2(testHero);                                battleStarted[0] = true; }
            case 8  -> { mainScreen.debugSkipToEnemy(testHero, "Plague Vermin",       2);       battleStarted[0] = true; }
            case 9  -> { mainScreen.debugSkipToEnemy(testHero, "Forsaken Cultist",    2);       battleStarted[0] = true; }
            case 10 -> { mainScreen.debugSkipToEnemy(testHero, "Blight Hound",        2);       battleStarted[0] = true; }
            case 11 -> { mainScreen.debugSkipToEnemy(testHero, "Ghoul Footman",       2);       battleStarted[0] = true; }
            case 12 -> { mainScreen.debugSkipToEnemy(testHero, "Black Jailer",        2);       battleStarted[0] = true; }
            case 13 -> { mainScreen.debugSkipToEnemy(testHero, "Luther Von",          2);       battleStarted[0] = true; }

            // World 3
            case 14 -> { mainScreen.debugSkipToWorld3(testHero);                                battleStarted[0] = true; }
            case 15 -> { mainScreen.debugSkipToEnemy(testHero, "Flame Revenant",      3);       battleStarted[0] = true; }
            case 16 -> { mainScreen.debugSkipToEnemy(testHero, "Bone Warlock",        3);       battleStarted[0] = true; }
            case 17 -> { mainScreen.debugSkipToEnemy(testHero, "Obsidian Crusher",    3);       battleStarted[0] = true; }
            case 18 -> { mainScreen.debugSkipToEnemy(testHero, "Soulflayer Gargoyle", 3);       battleStarted[0] = true; }
            case 19 -> { mainScreen.debugSkipToEnemy(testHero, "Zyrryl",              3);       battleStarted[0] = true; }

            // Misc
            case 20 -> mainScreen.debugSkipToShop(testHero);
            case 21 -> { mainScreen.debugSkipToFinalBoss(testHero);                             battleStarted[0] = true; }
        }

        // 6. Enable dev tools for any battle warp
        if (battleStarted[0]) {
            mainScreen.enableDevTools();
        }
    }
}