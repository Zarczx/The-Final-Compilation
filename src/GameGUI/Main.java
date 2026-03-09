package GameGUI;


import javax.swing.*;
import java.awt.*;

public class Main {
    private static CardLayout cardLayout = new CardLayout();
    private static JPanel mainPanel = new JPanel(cardLayout);

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("The Final Compilation");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1280, 720);
            frame.setResizable(false);
            frame.setLocationRelativeTo(null);

            // 1. Add the screens to the card container
            mainPanel.add(new TitleScreen(), "TITLE");
            mainPanel.add(new GameScreen(), "GAME");

            frame.add(mainPanel);

            // Start on the Title Screen
            cardLayout.show(mainPanel, "TITLE");

            frame.setVisible(true);
        });
    }

    // Helper method to switch to the game
    public static void startGame() {
        cardLayout.show(mainPanel, "GAME");
    }
}