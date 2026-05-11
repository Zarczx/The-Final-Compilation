package GameGUI.ui;

import GameGUI.model.system.LeaderboardEntry;
import GameGUI.model.system.LeaderboardManager;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

public class DashboardPanel extends JPanel {

    // ── Palette ────────────────────────────────────────────────────────────────
    private static final Color BG_DEEP       = new Color(12,  10,  22);
    private static final Color BG_HEADER     = new Color(16,  14,  30);
    private static final Color BG_STATS      = new Color(20,  18,  36);
    private static final Color BG_STAT_CELL  = new Color(26,  24,  44);
    private static final Color BG_TABLE      = new Color(22,  20,  38);
    private static final Color BG_ROW_HOVER  = new Color(32,  30,  52);
    private static final Color BG_FOOTER     = new Color(18,  16,  32);
    private static final Color BORDER_LINE   = new Color(50,  46,  80);

    private static final Color GOLD          = new Color(255, 215,   0);
    private static final Color SILVER        = new Color(176, 190, 197);
    private static final Color BRONZE        = new Color(205, 127,  50);

    private static final Color TEXT_PRIMARY  = new Color(232, 230, 240);
    private static final Color TEXT_MUTED    = new Color(130, 126, 160);
    private static final Color TEXT_HINT     = new Color( 80,  76, 110);
    private static final Color ACCENT_GREEN  = new Color( 76, 175,  80);
    private static final Color BADGE_GREEN_BG= new Color( 15,  42,  26);

    // Avatar palette — cycles through players
    private static final Color[][] AVATAR_COLORS = {
            { new Color(61, 46,  0),   new Color(255, 215,   0) },
            { new Color(26, 32, 48),   new Color(144, 202, 249) },
            { new Color(26, 40, 24),   new Color(165, 214, 167) },
            { new Color(42, 26, 42),   new Color(206, 147, 216) },
            { new Color(42, 26, 26),   new Color(239, 154, 154) },
            { new Color(20, 36, 40),   new Color(128, 222, 234) },
            { new Color(40, 32, 16),   new Color(255, 204, 128) },
    };

    // ── State ──────────────────────────────────────────────────────────────────
    private final Runnable onMainMenu;
    private DefaultTableModel tableModel;
    private JTable table;

    // Stat labels updated in refreshData()
    private JLabel statRunsVal;
    private JLabel statBestVal;
    private JLabel statPlayersVal;

    // ── Constructor ────────────────────────────────────────────────────────────
    public DashboardPanel(Runnable onMainMenu) {
        this.onMainMenu = onMainMenu;
        setLayout(new BorderLayout());
        setBackground(BG_DEEP);

        add(buildNorthPanel(),  BorderLayout.NORTH);
        add(buildCenterPanel(), BorderLayout.CENTER);
        add(buildFooterPanel(), BorderLayout.SOUTH);
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  NORTH — header + stats bar
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel buildNorthPanel() {
        JPanel north = new JPanel(new BorderLayout());
        north.setOpaque(false);
        north.add(buildHeaderPanel(), BorderLayout.NORTH);
        north.add(buildStatsBar(),    BorderLayout.SOUTH);
        return north;
    }

    private JPanel buildHeaderPanel() {
        JPanel header = new JPanel(new GridBagLayout());
        header.setBackground(BG_HEADER);
        header.setBorder(BorderFactory.createCompoundBorder(
                new BottomBorder(BORDER_LINE),
                BorderFactory.createEmptyBorder(24, 32, 20, 32)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor    = GridBagConstraints.CENTER;

        // Trophy emoji label
        JLabel trophy = new JLabel("🏆", SwingConstants.CENTER);
        trophy.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 30));
        gbc.insets = new Insets(0, 0, 6, 0);
        header.add(trophy, gbc);

        // Title
        JLabel title = new JLabel("Hall of Champions", SwingConstants.CENTER);
        title.setFont(new Font("Dialog", Font.BOLD, 26));
        title.setForeground(GOLD);
        gbc.insets = new Insets(0, 0, 4, 0);
        header.add(title, gbc);

        // Subtitle
        JLabel sub = new JLabel("Top completion times across all runs", SwingConstants.CENTER);
        sub.setFont(new Font("Dialog", Font.PLAIN, 13));
        sub.setForeground(TEXT_MUTED);
        gbc.insets = new Insets(0, 0, 0, 0);
        header.add(sub, gbc);

        return header;
    }

    private JPanel buildStatsBar() {
        JPanel bar = new JPanel(new GridLayout(1, 3, 1, 0));
        bar.setBackground(BORDER_LINE); // 1-px gaps between cells
        bar.setBorder(new BottomBorder(BORDER_LINE));

        statRunsVal    = new JLabel("—", SwingConstants.CENTER);
        statBestVal    = new JLabel("—", SwingConstants.CENTER);
        statPlayersVal = new JLabel("—", SwingConstants.CENTER);

        bar.add(makeStatCell(statRunsVal,    "Total runs"));
        bar.add(makeStatCell(statBestVal,    "Best time"));
        bar.add(makeStatCell(statPlayersVal, "Players"));
        return bar;
    }

    private JPanel makeStatCell(JLabel valueLabel, String labelText) {
        JPanel cell = new JPanel(new GridBagLayout());
        cell.setBackground(BG_STAT_CELL);
        cell.setBorder(BorderFactory.createEmptyBorder(14, 12, 14, 12));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor    = GridBagConstraints.CENTER;

        valueLabel.setFont(new Font("Dialog", Font.BOLD, 18));
        valueLabel.setForeground(TEXT_PRIMARY);
        gbc.insets = new Insets(0, 0, 3, 0);
        cell.add(valueLabel, gbc);

        JLabel lbl = new JLabel(labelText.toUpperCase(), SwingConstants.CENTER);
        lbl.setFont(new Font("Dialog", Font.PLAIN, 11));
        lbl.setForeground(TEXT_HINT);
        gbc.insets = new Insets(0, 0, 0, 0);
        cell.add(lbl, gbc);

        return cell;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  CENTER — leaderboard table
    // ══════════════════════════════════════════════════════════════════════════
    private JScrollPane buildCenterPanel() {
        String[] columns = { "Rank", "Player", "Time", "Date Completed" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
            @Override public Class<?> getColumnClass(int c) {
                return c == 0 ? Integer.class : String.class;
            }
        };

        table = new JTable(tableModel);
        table.setBackground(BG_TABLE);
        table.setForeground(TEXT_PRIMARY);
        table.setFont(new Font("Dialog", Font.PLAIN, 14));
        table.setRowHeight(52);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(BG_ROW_HOVER);
        table.setSelectionForeground(TEXT_PRIMARY);
        table.setFocusable(false);
        table.setRowSelectionAllowed(true);

        // Header
        JTableHeader header = table.getTableHeader();
        header.setBackground(BG_STATS);
        header.setForeground(TEXT_MUTED);
        header.setFont(new Font("Dialog", Font.BOLD, 11));
        header.setPreferredSize(new Dimension(header.getWidth(), 38));
        header.setReorderingAllowed(false);
        header.setBorder(new BottomBorder(BORDER_LINE));
        header.setDefaultRenderer(new HeaderRenderer());

        // Column widths
        TableColumnModel cm = table.getColumnModel();
        cm.getColumn(0).setPreferredWidth(64);
        cm.getColumn(0).setMaxWidth(64);
        cm.getColumn(1).setPreferredWidth(280);
        cm.getColumn(2).setPreferredWidth(120);
        cm.getColumn(3).setPreferredWidth(160);

        // Custom cell renderers
        cm.getColumn(0).setCellRenderer(new RankRenderer());
        cm.getColumn(1).setCellRenderer(new PlayerRenderer());
        cm.getColumn(2).setCellRenderer(new TimeRenderer());
        cm.getColumn(3).setCellRenderer(new DateRenderer());

        // Row-separator painting via prepareRenderer override
        table.setDefaultRenderer(String.class, new SeparatorRenderer());

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(BG_TABLE);
        scroll.setBackground(BG_TABLE);
        return scroll;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  SOUTH — footer bar
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel buildFooterPanel() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(BG_FOOTER);
        footer.setBorder(BorderFactory.createCompoundBorder(
                new TopBorder(BORDER_LINE),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));

        JButton btn = new RoundButton("← Return to Menu");
        btn.addActionListener(e -> onMainMenu.run());
        footer.add(btn, BorderLayout.WEST);

        JLabel hint = new JLabel("Updated live on completion");
        hint.setFont(new Font("Dialog", Font.PLAIN, 12));
        hint.setForeground(TEXT_HINT);
        footer.add(hint, BorderLayout.EAST);

        return footer;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  PUBLIC API — call before showing this panel
    // ══════════════════════════════════════════════════════════════════════════
    public void refreshData() {
        tableModel.setRowCount(0);
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

        updateStats(data);
    }

    private void updateStats(List<LeaderboardEntry> data) {
        statRunsVal.setText(String.valueOf(data.size()));

        if (!data.isEmpty()) {
            statBestVal.setText(data.get(0).formattedTime);
        } else {
            statBestVal.setText("—");
        }

        long uniquePlayers = data.stream()
                .map(e -> e.playerName)
                .distinct()
                .count();
        statPlayersVal.setText(String.valueOf(uniquePlayers));
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  CELL RENDERERS
    // ══════════════════════════════════════════════════════════════════════════

    /** Shared base — applies alternating row bg + bottom separator. */
    private static class BaseRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(
                JTable t, Object val, boolean sel, boolean focus, int row, int col) {
            super.getTableCellRendererComponent(t, val, sel, focus, row, col);
            setOpaque(true);
            if (sel) {
                setBackground(BG_ROW_HOVER);
            } else {
                setBackground(row % 2 == 0 ? BG_TABLE : new Color(26, 24, 42));
            }
            setBorder(new RowSeparatorBorder(BORDER_LINE));
            return this;
        }
    }

    /** Rank column — gold/silver/bronze badges for top 3. */
    private static class RankRenderer extends JPanel implements javax.swing.table.TableCellRenderer {
        private int rank;
        private boolean selected;

        RankRenderer() { setOpaque(true); }

        @Override
        public Component getTableCellRendererComponent(
                JTable t, Object val, boolean sel, boolean focus, int row, int col) {
            rank = (val instanceof Integer) ? (Integer) val : row + 1;
            selected = sel;
            setBackground(sel ? BG_ROW_HOVER : (row % 2 == 0 ? BG_TABLE : new Color(26, 24, 42)));
            setBorder(new RowSeparatorBorder(BORDER_LINE));
            return this;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int cx = getWidth() / 2;
            int cy = getHeight() / 2;
            int r  = 13;

            Color bg, fg;
            if (rank == 1) { bg = new Color(61, 46,  0);  fg = GOLD;   }
            else if (rank == 2) { bg = new Color(26, 32, 48); fg = SILVER; }
            else if (rank == 3) { bg = new Color(46, 26, 10); fg = BRONZE; }
            else {
                g2.setFont(new Font("Dialog", Font.PLAIN, 13));
                g2.setColor(TEXT_MUTED);
                String s = String.valueOf(rank);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(s, cx - fm.stringWidth(s) / 2, cy + fm.getAscent() / 2 - 1);
                g2.dispose();
                return;
            }

            g2.setColor(bg);
            g2.fillOval(cx - r, cy - r, r * 2, r * 2);

            g2.setFont(new Font("Dialog", Font.BOLD, 12));
            g2.setColor(fg);
            String s = String.valueOf(rank);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(s, cx - fm.stringWidth(s) / 2, cy + fm.getAscent() / 2 - 1);
            g2.dispose();
        }
    }

    /** Player column — avatar circle + name + optional NEW badge. */
    private static class PlayerRenderer extends JPanel implements javax.swing.table.TableCellRenderer {
        private String name = "";
        private boolean isNewest = false;
        private int rowIndex;
        private boolean selected;

        PlayerRenderer() {
            setOpaque(true);
            setLayout(null); // manual layout in paintComponent
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable t, Object val, boolean sel, boolean focus, int row, int col) {
            name     = val != null ? val.toString() : "";
            isNewest = (row == 0);
            rowIndex = row;
            selected = sel;
            setBackground(sel ? BG_ROW_HOVER : (row % 2 == 0 ? BG_TABLE : new Color(26, 24, 42)));
            setBorder(new RowSeparatorBorder(BORDER_LINE));
            return this;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int avatarR = 15;
            int ax = 16, ay = getHeight() / 2 - avatarR;

            // Avatar circle
            Color[] pal = AVATAR_COLORS[rowIndex % AVATAR_COLORS.length];
            g2.setColor(pal[0]);
            g2.fillOval(ax, ay, avatarR * 2, avatarR * 2);

            // Initials
            String initials = initials(name);
            g2.setFont(new Font("Dialog", Font.BOLD, 11));
            g2.setColor(pal[1]);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(initials,
                    ax + avatarR - fm.stringWidth(initials) / 2,
                    ay + avatarR + fm.getAscent() / 2 - 2);

            // Player name
            int nx = ax + avatarR * 2 + 10;
            g2.setFont(new Font("Dialog", Font.PLAIN, 14));
            g2.setColor(TEXT_PRIMARY);
            g2.drawString(name, nx, getHeight() / 2 + g2.getFontMetrics().getAscent() / 2 - 1);

            // NEW badge
            if (isNewest) {
                int badgeX = nx + g2.getFontMetrics().stringWidth(name) + 8;
                int badgeY = getHeight() / 2 - 8;
                int bw = 36, bh = 16;
                g2.setColor(BADGE_GREEN_BG);
                g2.fillRoundRect(badgeX, badgeY, bw, bh, 4, 4);
                g2.setFont(new Font("Dialog", Font.BOLD, 10));
                g2.setColor(ACCENT_GREEN);
                FontMetrics bfm = g2.getFontMetrics();
                g2.drawString("NEW",
                        badgeX + bw / 2 - bfm.stringWidth("NEW") / 2,
                        badgeY + bh / 2 + bfm.getAscent() / 2 - 2);
            }

            g2.dispose();
        }

        private static String initials(String name) {
            if (name == null || name.isEmpty()) return "?";
            String[] parts = name.trim().split("\\s+");
            if (parts.length >= 2)
                return ("" + parts[0].charAt(0) + parts[1].charAt(0)).toUpperCase();
            return name.length() >= 2
                    ? name.substring(0, 2).toUpperCase()
                    : name.substring(0, 1).toUpperCase();
        }
    }

    /** Time column — monospaced, primary colour. */
    private static class TimeRenderer extends BaseRenderer {
        @Override
        public Component getTableCellRendererComponent(
                JTable t, Object val, boolean sel, boolean focus, int row, int col) {
            super.getTableCellRendererComponent(t, val, sel, focus, row, col);
            setText(val != null ? val.toString() : "");
            setFont(new Font("Monospaced", Font.PLAIN, 13));
            setForeground(TEXT_PRIMARY);
            setHorizontalAlignment(RIGHT);
            setBorder(BorderFactory.createCompoundBorder(
                    new RowSeparatorBorder(BORDER_LINE),
                    BorderFactory.createEmptyBorder(0, 0, 0, 16)));
            return this;
        }
    }

    /** Date column — muted, right-aligned. */
    private static class DateRenderer extends BaseRenderer {
        @Override
        public Component getTableCellRendererComponent(
                JTable t, Object val, boolean sel, boolean focus, int row, int col) {
            super.getTableCellRendererComponent(t, val, sel, focus, row, col);
            setText(val != null ? val.toString() : "");
            setFont(new Font("Dialog", Font.PLAIN, 13));
            setForeground(TEXT_MUTED);
            setHorizontalAlignment(RIGHT);
            setBorder(BorderFactory.createCompoundBorder(
                    new RowSeparatorBorder(BORDER_LINE),
                    BorderFactory.createEmptyBorder(0, 0, 0, 16)));
            return this;
        }
    }

    /** Fallback renderer that draws row separators on string columns. */
    private static class SeparatorRenderer extends BaseRenderer {
        @Override
        public Component getTableCellRendererComponent(
                JTable t, Object val, boolean sel, boolean focus, int row, int col) {
            super.getTableCellRendererComponent(t, val, sel, focus, row, col);
            setText(val != null ? val.toString() : "");
            setFont(new Font("Dialog", Font.PLAIN, 14));
            setForeground(TEXT_PRIMARY);
            return this;
        }
    }

    /** Table header renderer — uppercase, small, muted. */
    private static class HeaderRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(
                JTable t, Object val, boolean sel, boolean focus, int row, int col) {
            super.getTableCellRendererComponent(t, val, sel, focus, row, col);
            setText(val != null ? val.toString().toUpperCase() : "");
            setFont(new Font("Dialog", Font.BOLD, 11));
            setForeground(TEXT_MUTED);
            setBackground(BG_STATS);
            setOpaque(true);
            int align = (col == 2 || col == 3) ? RIGHT : (col == 0 ? CENTER : LEFT);
            setHorizontalAlignment(align);
            setBorder(BorderFactory.createCompoundBorder(
                    new BottomBorder(BORDER_LINE),
                    BorderFactory.createEmptyBorder(0, col == 0 ? 0 : 8, 0, col == 2 || col == 3 ? 16 : 8)));
            return this;
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  CUSTOM BUTTON
    // ══════════════════════════════════════════════════════════════════════════
    private static class RoundButton extends JButton {
        RoundButton(String text) {
            super(text);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setFont(new Font("Dialog", Font.PLAIN, 13));
            setForeground(TEXT_PRIMARY);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(BorderFactory.createEmptyBorder(7, 16, 7, 16));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color bg = getModel().isPressed()
                    ? new Color(50, 46, 80)
                    : getModel().isRollover()
                    ? new Color(40, 36, 64)
                    : new Color(30, 28, 50);
            g2.setColor(bg);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
            g2.setColor(BORDER_LINE);
            g2.setStroke(new BasicStroke(0.5f));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  UTILITY BORDERS
    // ══════════════════════════════════════════════════════════════════════════
    private static class BottomBorder extends AbstractBorder {
        private final Color color;
        BottomBorder(Color c) { this.color = c; }
        @Override public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            g.setColor(color);
            g.drawLine(x, y + h - 1, x + w, y + h - 1);
        }
        @Override public Insets getBorderInsets(Component c) { return new Insets(0, 0, 1, 0); }
    }

    private static class TopBorder extends AbstractBorder {
        private final Color color;
        TopBorder(Color c) { this.color = c; }
        @Override public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            g.setColor(color);
            g.drawLine(x, y, x + w, y);
        }
        @Override public Insets getBorderInsets(Component c) { return new Insets(1, 0, 0, 0); }
    }

    private static class RowSeparatorBorder extends AbstractBorder {
        private final Color color;
        RowSeparatorBorder(Color c) { this.color = c; }
        @Override public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            g.setColor(color);
            g.drawLine(x, y + h - 1, x + w, y + h - 1);
        }
        @Override public Insets getBorderInsets(Component c) { return new Insets(0, 8, 1, 0); }
    }
}