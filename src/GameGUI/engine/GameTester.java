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
        // 1. Pop up a Dev Menu before launching the UI
        String[] options = {
                "Start Normally",
                "Warp to World 2",
                "Warp to Magic Shop",
                "Warp to World 3",
                "Warp to Final Boss"
        };

        int choice = JOptionPane.showOptionDialog(null,
                "Where do you want to warp?",
                "Developer Test Menu",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]);

        if (choice == -1) {
            System.exit(0); // User closed the window
        }

        // 2. Build the main game window (similar to GameEngine)
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

        // 3. Grab a default hero for testing (Karl)
        HeroData.HeroDefinition testHero = HeroData.HEROES.get(1);

        // 4. Execute the chosen warp
        switch (choice) {
            case 0 -> System.out.println("Starting normally..."); // Does nothing, plays intro
            case 1 -> mainScreen.debugSkipToWorld2(testHero);
            case 2 -> mainScreen.debugSkipToShop(testHero);
            case 3 -> mainScreen.debugSkipToWorld3(testHero);
            case 4 -> mainScreen.debugSkipToFinalBoss(testHero);
        }
    }
}