package GameGUI.ui;

import GameGUI.model.entity.base.Combatant;
import GameGUI.model.system.SaveData;
import GameGUI.model.system.SaveManager;

import javax.swing.*;
import java.awt.*;
import java.util.function.BiConsumer;

public class SaveSlotDialog extends JDialog {

    private static final Color BG_DARK = new Color(15, 15, 15);
    private static final Color PANEL_DARK = new Color(25, 25, 25);
    private static final Color GOLD_MUTED = new Color(160, 140, 90);
    private static final Color TEXT_LIGHT = new Color(210, 210, 200);

    /**
     * @param isSaveMode true if opening to Save, false if opening to Load.
     * @param heroToSave The living Combatant you want to save (pass null if loading).
     * @param currentWorld The current world number (pass 1 if loading).
     * @param onLoadAction A callback function that runs when a game is successfully loaded!
     */
    public SaveSlotDialog(Window parent, boolean isSaveMode, Combatant heroToSave, Combatant enemyToSave, int currentWorld, int seqIndex, int fightIndex, BiConsumer<Combatant, SaveData> onLoadAction) {
        super(parent, isSaveMode ? "Save Game" : "Load Game", ModalityType.APPLICATION_MODAL);
        setSize(500, 450);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(BG_DARK);
        setLayout(new BorderLayout());

        JLabel title = new JLabel(isSaveMode ? "Select Save Slot" : "Select Game to Load", SwingConstants.CENTER);
        title.setFont(new Font("Georgia", Font.BOLD, 22));
        title.setForeground(GOLD_MUTED);
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 15, 0));
        add(title, BorderLayout.NORTH);

        JPanel slotsPanel = new JPanel(new GridLayout(3, 1, 15, 15));
        slotsPanel.setBackground(BG_DARK);
        slotsPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));

        for (int i = 1; i <= 3; i++) {
            slotsPanel.add(createSlot(i, isSaveMode, heroToSave, enemyToSave, currentWorld, seqIndex, fightIndex, onLoadAction));
        }

        add(slotsPanel, BorderLayout.CENTER);
    }

    private JPanel createSlot(int slotNum, boolean isSaveMode, Combatant heroToSave, Combatant enemyToSave, int currentWorld, int seqIndex, int fightIndex, BiConsumer<Combatant, SaveData> onLoadAction) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PANEL_DARK);
        panel.setBorder(BorderFactory.createLineBorder(GOLD_MUTED, 1));

        // Read the file to see if a save exists here
        SaveData data = SaveManager.loadGame(slotNum);

        JLabel info = new JLabel();
        info.setForeground(TEXT_LIGHT);
        info.setFont(new Font("Georgia", Font.PLAIN, 14));
        info.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        if (data == null) {
            info.setText("<html><b style='color:#a08c5a; font-size:16px;'>Slot " + slotNum + "</b><br><font color='gray'>Empty Slot</font></html>");
        } else {
            info.setText("<html><b style='color:#a08c5a; font-size:16px;'>Slot " + slotNum + "</b> &nbsp;&nbsp;&nbsp; <font color='gray'>" + data.timestamp + "</font>" +
                    "<br><b>" + data.heroName + "</b> (Level " + data.level + ")" +
                    "<br>World " + data.currentWorld + "</html>");
        }
        panel.add(info, BorderLayout.CENTER);

        JButton actionBtn = new JButton(isSaveMode ? "Save" : "Load");
        actionBtn.setFont(new Font("Georgia", Font.BOLD, 14));
        actionBtn.setBackground(new Color(40, 40, 40));
        actionBtn.setForeground(GOLD_MUTED);
        actionBtn.setFocusPainted(false);
        actionBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        actionBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { utils.SoundUtil.play("HoverSound.wav"); }
        });
        actionBtn.addActionListener(e -> utils.SoundUtil.play("SelectSound.wav"));

        // Disable the load button if the slot is empty
        if (!isSaveMode && data == null) {
            actionBtn.setEnabled(false);
        }

        actionBtn.addActionListener(e -> {
            if (isSaveMode) {
                // RUN SAVE LOGIC
                boolean success = SaveManager.saveGame(heroToSave, enemyToSave, slotNum, currentWorld, seqIndex, fightIndex);
                if (success) {
                    showStyledMessage("Game saved to Slot " + slotNum + "!", "Saved", false);
                    dispose();
                }
            } else {
                // RUN LOAD LOGIC
                Combatant loadedHero = SaveManager.reconstructHero(data);
                if (loadedHero != null && onLoadAction != null) {
                    dispose(); // Close window
                    onLoadAction.accept(loadedHero, data); // ★ Pass the WHOLE SaveData file back!
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to load save file.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Wrapper to size the button nicely
        JPanel btnPanel = new JPanel(new GridBagLayout());
        btnPanel.setBackground(PANEL_DARK);
        btnPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 15));

        // ★ CREATE THE DELETE BUTTON (Only if a save file exists)
        if (data != null) {
            JButton deleteBtn = new JButton("Delete");
            deleteBtn.setPreferredSize(new Dimension(80, 40));
            deleteBtn.setBackground(new Color(140, 40, 40)); // Dark Red
            deleteBtn.setForeground(Color.WHITE);
            deleteBtn.setFocusPainted(false);
            deleteBtn.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override public void mouseEntered(java.awt.event.MouseEvent e) { utils.SoundUtil.play("HoverSound.wav"); }
            });
            deleteBtn.addActionListener(e -> utils.SoundUtil.play("SelectSound.wav"));

            deleteBtn.addActionListener(e -> {
                int choice = JOptionPane.showConfirmDialog(
                        this,
                        "Are you sure you want to delete Slot " + slotNum + "?",
                        "Confirm Delete",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

                if (choice == JOptionPane.YES_OPTION) {
                    GameGUI.model.system.SaveManager.deleteGame(slotNum);
                    dispose(); // Close the menu so the UI refreshes properly
                }
            });
            btnPanel.add(deleteBtn);
        }

        actionBtn.setPreferredSize(new Dimension(100, 40));
        btnPanel.add(actionBtn);

        panel.add(btnPanel, BorderLayout.EAST);

        return panel;


    }

    private void showStyledMessage(String message, String title, boolean isError) {
        JDialog dialog = new JDialog(this, title, true);
        dialog.setUndecorated(true);
        dialog.setSize(320, 160);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout(10, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(25, 25, 25));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(new Color(160, 140, 90));
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 20, 20);
            }
        };
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Georgia", Font.BOLD, 18));
        titleLabel.setForeground(new Color(160, 140, 90));

        JLabel msg = new JLabel(message, SwingConstants.CENTER);
        msg.setFont(new Font("Georgia", Font.PLAIN, 14));
        msg.setForeground(isError ? new Color(255, 80, 80) : new Color(210, 210, 200));

        JButton ok = new JButton("OK");
        ok.setFont(new Font("Georgia", Font.BOLD, 13));
        ok.setForeground(new Color(160, 140, 90));
        ok.setBackground(new Color(40, 40, 40));
        ok.setFocusPainted(false);
        ok.setBorder(BorderFactory.createLineBorder(new Color(160, 140, 90), 2));
        ok.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        ok.setPreferredSize(new Dimension(80, 32));
        ok.addActionListener(e -> { utils.SoundUtil.play("SelectSound.wav"); dialog.dispose(); });
        ok.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { utils.SoundUtil.play("HoverSound.wav"); }
        });

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setOpaque(false);
        btnPanel.add(ok);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(msg, BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);
        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }
}