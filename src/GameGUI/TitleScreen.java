package GameGUI;

import GameGUI.engine.GameScreen;
import GameGUI.model.entity.Combatant;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;

public class TitleScreen extends JPanel {

    BufferedImage[] backgrounds;
    int frame = 0;

    Timer timer;

    private GameGUI.engine.GameScreen gameScreen;

    public void setGameScreen(GameGUI.engine.GameScreen gameScreen) {
        this.gameScreen = gameScreen;
    }

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

        timer = new Timer(120, e -> {
            frame = (frame + 1) % backgrounds.length;
            repaint();
        });
        timer.start();

        setLayout(new GridBagLayout());

        // ── Logo ──────────────────────────────────────────────────────────
        JLabel title = new JLabel();
        java.net.URL logoUrl = getClass().getResource("/assets/GUIButtons/TheFinalCompilationLogo.png");
        if (logoUrl != null) {
            ImageIcon raw = new ImageIcon(logoUrl);
            int origW  = raw.getIconWidth();
            int origH  = raw.getIconHeight();
            int targetW = 470;
            int targetH = (origW > 0) ? (origH * targetW / origW) : 200;
            title.setIcon(new ImageIcon(
                    raw.getImage().getScaledInstance(targetW, targetH, Image.SCALE_SMOOTH)));
        } else {
            title.setText("The Final Compilation");
            title.setFont(new Font("Arial", Font.BOLD, 60));
            title.setForeground(Color.WHITE);
        }

        // ── Uniform button size for all four buttons ───────────────────────
        final int BTN_W = 200;
        final int BTN_H = 70;

        JButton continueBtn = makeImageButton(
                "/assets/GUIButtons/ContinueButton.png",
                "/assets/GUIButtons/ContinueButtonHover.png",
                "Continue", BTN_W, BTN_H);

        JButton startBtn = makeImageButton(
                "/assets/GUIButtons/StartButton.png",
                "/assets/GUIButtons/StartButtonHover.png",
                "Start", BTN_W, BTN_H);

        JButton creditsBtn = makeImageButton(
                "/assets/GUIButtons/CreditsButton.png",
                "/assets/GUIButtons/CreditsButtonHover.png",
                "Credits", BTN_W, BTN_H);

        JButton exitBtn = makeImageButton(
                "/assets/GUIButtons/ExitButton.png",
                "/assets/GUIButtons/ExitButtonHover.png",
                "Exit", BTN_W, BTN_H);

        // ── Actions ───────────────────────────────────────────────────────
        startBtn.addActionListener(e -> {
            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            topFrame.getContentPane().removeAll();
            topFrame.getContentPane().add(new GameScreen());
            topFrame.revalidate();
            topFrame.repaint();
        });

        continueBtn.addActionListener(e -> {
            // Open the SaveSlotDialog in LOAD Mode
            GameGUI.ui.SaveSlotDialog loadDialog = new GameGUI.ui.SaveSlotDialog(
                    SwingUtilities.getWindowAncestor(this),
                    false,
                    null,
                    null,
                    1,
                    0,  // ★ ADDED: Dummy seqIndex for Load Mode
                    0,  // ★ ADDED: Dummy fightIndex for Load Mode
                    (GameGUI.model.entity.Combatant loadedHero, GameGUI.model.system.SaveData data) -> {
                        timer.stop(); // Stop the Title Screen animations

                        // 1. Create the brand new GameScreen!
                        GameGUI.engine.GameScreen myGame = new GameGUI.engine.GameScreen();

                        // 2. Inject the veteran hero and world into it!
                        myGame.loadSavedGame(loadedHero, data);

                        // 3. Swap the window to show the GameScreen instead of the Title Screen!
                        JFrame window = (JFrame) SwingUtilities.getWindowAncestor(this);
                        window.getContentPane().removeAll();
                        window.getContentPane().add(myGame);
                        window.revalidate();
                        window.repaint();
                    }
            );
            loadDialog.setVisible(true);
        });

        exitBtn.addActionListener(e -> System.exit(0));

        // ── Layout ────────────────────────────────────────────────────────
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx  = 0;
        gbc.fill   = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;

        // Logo — large top inset pushes it well down from the top edge
        gbc.insets = new Insets(2, 10, 2, 10);
        gbc.gridy = 0;
        add(title, gbc);

        // Buttons — small equal gap so all four fit comfortably on screen
        gbc.insets = new Insets(5, 10, 5, 10);

        gbc.gridy = 1; add(startBtn,    gbc);
        gbc.gridy = 2; add(continueBtn, gbc);
        gbc.gridy = 3; add(creditsBtn,  gbc);
        gbc.gridy = 4; add(exitBtn,     gbc);
    }

    /**
     * Creates a transparent image button scaled to exactly (w x h).
     * Swaps to the hover icon on mouse-enter, reverts on mouse-exit.
     */
    private JButton makeImageButton(String normalPath, String hoverPath,
                                    String fallback, int w, int h) {
        JButton btn = new JButton();
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Lock every button to the exact same size so GridBagLayout can't distort them
        Dimension size = new Dimension(w, h);
        btn.setPreferredSize(size);
        btn.setMinimumSize(size);
        btn.setMaximumSize(size);

        java.net.URL normalUrl = getClass().getResource(normalPath);
        java.net.URL hoverUrl  = (hoverPath != null) ? getClass().getResource(hoverPath) : null;

        if (normalUrl != null) {
            ImageIcon normalIcon = new ImageIcon(
                    new ImageIcon(normalUrl).getImage()
                            .getScaledInstance(w, h, Image.SCALE_SMOOTH));

            ImageIcon hoverIcon = (hoverUrl != null)
                    ? new ImageIcon(new ImageIcon(hoverUrl).getImage()
                    .getScaledInstance(w, h, Image.SCALE_SMOOTH))
                    : normalIcon;

            btn.setIcon(normalIcon);
            btn.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override public void mouseEntered(java.awt.event.MouseEvent e) { btn.setIcon(hoverIcon); utils.SoundUtil.play("HoverSound.wav"); }
                @Override public void mouseExited (java.awt.event.MouseEvent e) { btn.setIcon(normalIcon); }
            });
            btn.addActionListener(e -> utils.SoundUtil.play("SelectSound.wav"));
        } else {
            btn.setText(fallback);
            btn.setFont(new Font("Arial", Font.BOLD, 18));
            btn.setForeground(Color.WHITE);
            btn.setContentAreaFilled(true);
            System.out.println("[TitleScreen] Missing: " + normalPath);
        }
        return btn;
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        if (backgrounds[frame] != null){
            g.drawImage(backgrounds[frame], 0, 0, getWidth(), getHeight(), null);
        }
    }


}