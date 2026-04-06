package GameGUI;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            utils.SoundUtil.playLoop("BeforeBGMusic.wav", 0.6f); // ADD THIS
            JFrame frame = new JFrame("The Final Compilation");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1280, 720);
            frame.setResizable(false);
            frame.setLocationRelativeTo(null);

            // TitleScreen handles its own transition to GameScreen
            frame.getContentPane().add(new TitleScreen());

            frame.setVisible(true);
        });
    }
}