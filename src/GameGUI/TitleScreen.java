package GameGUI;

import GameGUI.engine.GameScreen;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;

public class TitleScreen extends JPanel {

    BufferedImage[] backgrounds;
    int frame = 0;

    Timer timer;

    public TitleScreen(){

        backgrounds = new BufferedImage[26];

        try{
            backgrounds[0]  = ImageIO.read(getClass().getResource("/assets/titleScreen/NewTitleScreen_1.png"));
            backgrounds[1]  = ImageIO.read(getClass().getResource("/assets/titleScreen/NewTitleScreen_2.png"));
            backgrounds[2]  = ImageIO.read(getClass().getResource("/assets/titleScreen/NewTitleScreen_3.png"));
            backgrounds[3]  = ImageIO.read(getClass().getResource("/assets/titleScreen/NewTitleScreen_4.png"));
            backgrounds[4]  = ImageIO.read(getClass().getResource("/assets/titleScreen/NewTitleScreen_5.png"));
            backgrounds[5]  = ImageIO.read(getClass().getResource("/assets/titleScreen/NewTitleScreen_6.png"));
            backgrounds[6]  = ImageIO.read(getClass().getResource("/assets/titleScreen/NewTitleScreen_7.png"));
            backgrounds[7]  = ImageIO.read(getClass().getResource("/assets/titleScreen/NewTitleScreen_8.png"));
            backgrounds[8]  = ImageIO.read(getClass().getResource("/assets/titleScreen/NewTitleScreen_9.png"));
            backgrounds[9]  = ImageIO.read(getClass().getResource("/assets/titleScreen/NewTitleScreen_10.png"));
            backgrounds[10] = ImageIO.read(getClass().getResource("/assets/titleScreen/NewTitleScreen_11.png"));
            backgrounds[11] = ImageIO.read(getClass().getResource("/assets/titleScreen/NewTitleScreen_12.png"));
            backgrounds[12] = ImageIO.read(getClass().getResource("/assets/titleScreen/NewTitleScreen_13.png"));
            backgrounds[13] = ImageIO.read(getClass().getResource("/assets/titleScreen/NewTitleScreen_14.png"));
            backgrounds[14] = ImageIO.read(getClass().getResource("/assets/titleScreen/NewTitleScreen_15.png"));
            backgrounds[15] = ImageIO.read(getClass().getResource("/assets/titleScreen/NewTitleScreen_16.png"));
            backgrounds[16] = ImageIO.read(getClass().getResource("/assets/titleScreen/NewTitleScreen_17.png"));
            backgrounds[17] = ImageIO.read(getClass().getResource("/assets/titleScreen/NewTitleScreen_18.png"));
            backgrounds[18] = ImageIO.read(getClass().getResource("/assets/titleScreen/NewTitleScreen_19.png"));
            backgrounds[19] = ImageIO.read(getClass().getResource("/assets/titleScreen/NewTitleScreen_20.png"));
            backgrounds[20] = ImageIO.read(getClass().getResource("/assets/titleScreen/NewTitleScreen_21.png"));
            backgrounds[21] = ImageIO.read(getClass().getResource("/assets/titleScreen/NewTitleScreen_22.png"));
            backgrounds[22] = ImageIO.read(getClass().getResource("/assets/titleScreen/NewTitleScreen_23.png"));
            backgrounds[23] = ImageIO.read(getClass().getResource("/assets/titleScreen/NewTitleScreen_24.png"));
            backgrounds[24] = ImageIO.read(getClass().getResource("/assets/titleScreen/NewTitleScreen_25.png"));
            backgrounds[25] = ImageIO.read(getClass().getResource("/assets/titleScreen/NewTitleScreen_26.png"));
        }catch(IOException e){
            e.printStackTrace();
        }

        timer = new Timer(120, e -> {  // ~12fps — smooth animated feel
            frame = (frame + 1) % backgrounds.length;
            repaint();
        });

        timer.start();


        setLayout(new GridBagLayout());

        // Logo image — scale proportionally to target width of 500px
        JLabel title = new JLabel();
        java.net.URL logoUrl = getClass().getResource("/assets/GUIButtons/TheFinalCompilationLogo.png");
        if (logoUrl != null) {
            ImageIcon raw = new ImageIcon(logoUrl);
            int origW = raw.getIconWidth();
            int origH = raw.getIconHeight();
            int targetW = 500;
            int targetH = (origH * targetW) / origW;
            Image scaled = raw.getImage().getScaledInstance(targetW, targetH, Image.SCALE_SMOOTH);
            title.setIcon(new ImageIcon(scaled));
        } else {
            title.setText("The Final Compilation");
            title.setFont(new Font("Arial", Font.BOLD, 60));
            title.setForeground(Color.WHITE);
        }

        // ── Image buttons ──────────────────────────────────────────────────
        JButton startBtn  = makeImageButton("/assets/GUIButtons/StartButton.png",  "Start",   235);
        JButton optionBtn = makeImageButton("/assets/GUIButtons/OptionsButton.png", "Options", 263);
        JButton exitBtn   = makeImageButton("/assets/GUIButtons/ExitButton.png",    "Exit",    235);

        startBtn.addActionListener(e -> {
            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            topFrame.getContentPane().removeAll();
            topFrame.getContentPane().add(new GameScreen());
            topFrame.revalidate();
            topFrame.repaint();
        });

        exitBtn.addActionListener(e -> System.exit(0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;

        gbc.insets = new Insets(10, 10, 20, 10);
        gbc.gridy = 0; add(title,     gbc);

        gbc.insets = new Insets(0, 10, 5, 10);
        gbc.gridy = 1; add(startBtn,  gbc);
        gbc.insets = new Insets(-60, 10, 5, 10);
        gbc.gridy = 2; add(optionBtn, gbc);
        gbc.gridy = 3; add(exitBtn,   gbc);
    }

    /** Creates a transparent image button. Falls back to text if asset missing. */
    private JButton makeImageButton(String path, String fallback) {
        return makeImageButton(path, fallback, 210);
    }

    private JButton makeImageButton(String path, String fallback, int targetW) {
        JButton btn = new JButton();
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        java.net.URL url = getClass().getResource(path);
        if (url != null) {
            ImageIcon raw = new ImageIcon(url);
            int origW = raw.getIconWidth();
            int origH = raw.getIconHeight();
            int targetH = (origW > 0) ? (origH * targetW / origW) : 60;
            Image scaled = raw.getImage().getScaledInstance(targetW, targetH, Image.SCALE_SMOOTH);
            btn.setIcon(new ImageIcon(scaled));
        } else {
            btn.setText(fallback);
            btn.setFont(new Font("Arial", Font.BOLD, 18));
            btn.setForeground(Color.WHITE);
            btn.setContentAreaFilled(true);
            System.out.println("[TitleScreen] Missing: " + path);
        }
        return btn;
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        if(backgrounds[frame] != null){
            g.drawImage(backgrounds[frame], 0, 0, getWidth(), getHeight(), null);
        }
    }
}