package GameGUI.ui;

import GameGUI.model.entity.Combatant;
import GameGUI.model.system.SaveData;
import GameGUI.model.system.SaveManager;

import javax.swing.*;
import java.awt.*;
import java.util.function.BiConsumer;

import java.io.File;

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
    public SaveSlotDialog(Window parent, boolean isSaveMode, Combatant heroToSave, int currentWorld, int seqIndex, int fightIndex, BiConsumer<Combatant, SaveData> onLoadAction) {
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
            slotsPanel.add(createSlot(i, isSaveMode, heroToSave, currentWorld, seqIndex, fightIndex, onLoadAction));
        }

        add(slotsPanel, BorderLayout.CENTER);
    }

    private JPanel createSlot(int slotNum, boolean isSaveMode, Combatant heroToSave, int currentWorld, int seqIndex, int fightIndex, BiConsumer<Combatant, SaveData> onLoadAction) {
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

        // Disable the load button if the slot is empty
        if (!isSaveMode && data == null) {
            actionBtn.setEnabled(false);
        }

        actionBtn.addActionListener(e -> {
            if (isSaveMode) {
                // RUN SAVE LOGIC
                boolean success = SaveManager.saveGame(heroToSave, slotNum, currentWorld, seqIndex, fightIndex);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Game successfully saved to Slot " + slotNum + "!", "Saved", JOptionPane.INFORMATION_MESSAGE);
                    dispose(); // Close window
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
}