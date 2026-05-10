package GameGUI;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.geom.RoundRectangle2D;
import javax.imageio.ImageIO;
import java.io.IOException;

public class CreditsScreen extends JPanel {

    BufferedImage[] backgrounds;
    int frame = 0;
    Timer timer;

    public CreditsScreen(JPanel returnPanel) {

        // ── Animated background (reuse title screen frames) ──────────────
        backgrounds = new BufferedImage[26];
        try {
            for (int i = 0; i < 26; i++) {
                backgrounds[i] = ImageIO.read(
                        getClass().getResource("/assets/titleScreen/NewTitleScreen_" + (i + 1) + ".png"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        timer = new Timer(120, e -> {
            frame = (frame + 1) % backgrounds.length;
            repaint();
        });
        timer.start();

        setLayout(new GridBagLayout());
        setOpaque(false);

        // ── Brown parchment panel ─────────────────────────────────────────
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Outer dark border
                g2.setColor(new Color(60, 30, 10));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 24, 24));

                // Inner parchment gradient
                GradientPaint parchment = new GradientPaint(
                        0, 0, new Color(210, 160, 90),
                        0, getHeight(), new Color(170, 110, 50));
                g2.setPaint(parchment);
                g2.fill(new RoundRectangle2D.Float(4, 4, getWidth() - 8, getHeight() - 8, 20, 20));

                // Subtle inner shadow / vignette
                g2.setColor(new Color(100, 50, 10, 60));
                g2.setStroke(new BasicStroke(6f));
                g2.draw(new RoundRectangle2D.Float(6, 6, getWidth() - 12, getHeight() - 12, 18, 18));

                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // ── Title label ───────────────────────────────────────────────────
        JLabel heading = styledLabel("✦  CREDITS  ✦", 28, new Color(255, 240, 180), Font.BOLD);
        heading.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(heading);
        card.add(Box.createVerticalStrut(6));

        // Divider
        card.add(divider());
        card.add(Box.createVerticalStrut(18));

        // ── Project Managers ──────────────────────────────────────────────
        card.add(sectionLabel("Project Managers"));
        card.add(Box.createVerticalStrut(6));
        card.add(nameLabel("Arcenal, John Michael"));
        card.add(nameLabel("Solinap, Karl Cyrus"));

        card.add(Box.createVerticalStrut(20));

        // ── Members ───────────────────────────────────────────────────────
        card.add(sectionLabel("Members"));
        card.add(Box.createVerticalStrut(6));
        card.add(nameLabel("Escobañas, Lance Angel"));
        card.add(nameLabel("Belino, Simon"));

        card.add(Box.createVerticalStrut(20));

        // ── Special Thanks ────────────────────────────────────────────────
        card.add(sectionLabel("Special Thanks"));
        card.add(Box.createVerticalStrut(6));

        JLabel thanksNote = styledLabel("Our professor who guided us with this project:", 13,
                new Color(240, 210, 140), Font.ITALIC);
        thanksNote.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(thanksNote);
        card.add(Box.createVerticalStrut(4));
        card.add(nameLabel("Kenn Migan Vincent Gumonan"));

        card.add(Box.createVerticalStrut(24));

        // Divider
        card.add(divider());
        card.add(Box.createVerticalStrut(18));

        // ── Back button ───────────────────────────────────────────────────
        JButton backBtn = makeImageButton(
                "/assets/GUIButtons/ContinueButton.png",   // reuse any button asset you have
                "/assets/GUIButtons/ContinueButtonHover.png",
                "◀  Back", 200, 60);

        backBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        backBtn.addActionListener(e -> {
            timer.stop();
            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            TitleScreen titleScreen = (TitleScreen) returnPanel;
            titleScreen.startAnimation(); // restart the title animation
            topFrame.getContentPane().removeAll();
            topFrame.getContentPane().add(returnPanel);
            topFrame.revalidate();
            topFrame.repaint();
        });
        card.add(backBtn);

        // ── Place card in centre of screen ────────────────────────────────
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx  = 0;
        gbc.gridy  = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        add(card, gbc);
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private JLabel styledLabel(String text, int size, Color color, int style) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Serif", style, size));
        lbl.setForeground(color);
        return lbl;
    }

    private JLabel sectionLabel(String text) {
        JLabel lbl = styledLabel(text, 16, new Color(255, 230, 120), Font.BOLD);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        return lbl;
    }

    private JLabel nameLabel(String text) {
        JLabel lbl = styledLabel(text, 14, new Color(255, 245, 200), Font.PLAIN);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        return lbl;
    }

    private JSeparator divider() {
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(120, 70, 20, 180));
        sep.setBackground(new Color(255, 200, 100, 80));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        return sep;
    }

    private JButton makeImageButton(String normalPath, String hoverPath,
                                    String fallback, int w, int h) {
        JButton btn = new JButton();
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        Dimension size = new Dimension(w, h);
        btn.setPreferredSize(size);
        btn.setMinimumSize(size);
        btn.setMaximumSize(size);

        java.net.URL normalUrl = getClass().getResource(normalPath);
        java.net.URL hoverUrl  = hoverPath != null ? getClass().getResource(hoverPath) : null;

        if (normalUrl != null) {
            ImageIcon normalIcon = new ImageIcon(
                    new ImageIcon(normalUrl).getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH));
            ImageIcon hoverIcon  = hoverUrl != null
                    ? new ImageIcon(new ImageIcon(hoverUrl).getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH))
                    : normalIcon;

            btn.setIcon(normalIcon);
            btn.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                    btn.setIcon(hoverIcon);
                    utils.SoundUtil.play("HoverSound.wav");
                }
                @Override public void mouseExited(java.awt.event.MouseEvent e) {
                    btn.setIcon(normalIcon);
                }
            });
            btn.addActionListener(e -> utils.SoundUtil.play("SelectSound.wav"));
        } else {
            // Fallback: styled brown text button
            btn.setText(fallback);
            btn.setFont(new Font("Serif", Font.BOLD, 16));
            btn.setForeground(new Color(255, 240, 180));
            btn.setContentAreaFilled(true);
            btn.setBackground(new Color(100, 55, 15));
        }
        return btn;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgrounds != null && backgrounds[frame] != null) {
            g.drawImage(backgrounds[frame], 0, 0, getWidth(), getHeight(), null);
        }
    }
}