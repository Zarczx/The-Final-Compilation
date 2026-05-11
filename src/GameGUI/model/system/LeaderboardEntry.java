package GameGUI.model.system;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LeaderboardEntry implements Comparable<LeaderboardEntry> {
    public String playerName;
    public long timeMillis;
    public String formattedTime;
    public String completionDate;

    public LeaderboardEntry(String playerName, long timeMillis) {
        this.playerName = playerName;
        this.timeMillis = timeMillis;
        this.formattedTime = formatTime(timeMillis);
        this.completionDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
    }

    private String formatTime(long millis) {
        long seconds = (millis / 1000) % 60;
        long minutes = (millis / (1000 * 60)) % 60;
        long hours = millis / (1000 * 60 * 60);

        if (hours > 0) return String.format("%dh %02dm %02ds", hours, minutes, seconds);
        return String.format("%02dm %02ds", minutes, seconds);
    }

    @Override
    public int compareTo(LeaderboardEntry other) {
        // Sorts fastest time to the top
        return Long.compare(this.timeMillis, other.timeMillis);
    }
}