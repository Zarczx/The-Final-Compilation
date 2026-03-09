package GameGUI;

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

        backgrounds = new BufferedImage[3];

        try{
            backgrounds[0] = ImageIO.read(getClass().getResource("/assets/titleScreen/title_screen.jpg"));
            backgrounds[1] = ImageIO.read(getClass().getResource("/assets/titleScreen/title_screen2.png"));
            backgrounds[2] = ImageIO.read(getClass().getResource("/assets/titleScreen/title_screen3.jpg"));
        }catch(IOException e){
            e.printStackTrace();
        }

        // animation timer (change frame every 400ms)
        timer = new Timer(400, e -> {
            frame++;
            if(frame >= backgrounds.length){
                frame = 0;
            }
            repaint();
        });

        timer.start();

        setLayout(new GridBagLayout());

        JLabel title = new JLabel("The Final Compilation");
        title.setFont(new Font("Arial", Font.BOLD, 60));
        title.setForeground(Color.WHITE);

        JButton startBtn = new JButton("Start");

        startBtn.addActionListener(e -> {
            // get the JFrame that contains this panel
            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);

            // remove the current TitleScreen panel
            topFrame.getContentPane().removeAll();

            // add the new GameScreen panel
            topFrame.getContentPane().add(new GameScreen());

            // refresh the JFrame
            topFrame.revalidate();
            topFrame.repaint();
        });

        JButton optionBtn = new JButton("Options");
        JButton exitBtn = new JButton("Exit");

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(10,10,10,10);

        gbc.gridy = 0;
        add(title, gbc);

        gbc.gridy = 1;
        add(startBtn, gbc);

        gbc.gridy = 2;
        add(optionBtn, gbc);

        gbc.gridy = 3;
        add(exitBtn, gbc);

        exitBtn.addActionListener(e -> System.exit(0));
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);

        if(backgrounds[frame] != null){
            g.drawImage(backgrounds[frame], 0, 0, getWidth(), getHeight(), null);
        }
    }

}