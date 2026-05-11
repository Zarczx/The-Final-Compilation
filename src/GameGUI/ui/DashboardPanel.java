package GameGUI.ui;

import GameGUI.model.system.LeaderboardEntry;
import GameGUI.model.system.LeaderboardManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DashboardPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private Runnable onMainMenu; // Callback to return to menu

    public DashboardPanel(Runnable onMainMenu) {
        this.onMainMenu = onMainMenu;
        setLayout(new BorderLayout());
        setBackground(new Color(12, 10, 22));

        JLabel title = new JLabel("HALL OF CHAMPIONS", SwingConstants.CENTER);
        title.setFont(new Font("Monospaced", Font.BOLD, 36));
        title.setForeground(new Color(255, 215, 0)); // Gold
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(title, BorderLayout.NORTH);

        // Set up the table
        String[] columns = {"Rank", "Player", "Time", "Date Completed"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        table = new JTable(tableModel);
        table.setBackground(new Color(22, 20, 38));
        table.setForeground(Color.WHITE);
        table.setFont(new Font("Monospaced", Font.PLAIN, 16));
        table.setRowHeight(30);
        table.getTableHeader().setBackground(new Color(40, 35, 60));
        table.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(new Color(12, 10, 22));
        add(scrollPane, BorderLayout.CENTER);

        JButton menuBtn = new JButton("Return to Menu");
        menuBtn.addActionListener(e -> onMainMenu.run());
        add(menuBtn, BorderLayout.SOUTH);
    }

    // Call this right before switching CardLayout to this screen
    public void refreshData() {
        tableModel.setRowCount(0); // Clear old data
        List<LeaderboardEntry> data = LeaderboardManager.loadLeaderboard();

        int rank = 1;
        for (LeaderboardEntry entry : data) {
            tableModel.addRow(new Object[]{
                    rank++,
                    entry.playerName,
                    entry.formattedTime,
                    entry.completionDate
            });
        }
    }
}