package GameGUI.model.system;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LeaderboardManager {

    // Target folder and file path
    private static final String LEADERBOARD_DIR = "leaderboard/";
    private static final String FILE_PATH = LEADERBOARD_DIR + "leaderboard.json";

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final int MAX_ENTRIES = 10;

    // Automatically creates the leaderboard folder if it is missing
    private static void ensureDirectoryExists() {
        File dir = new File(LEADERBOARD_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public static void addEntry(String name, long timeMillis) {
        ensureDirectoryExists();
        List<LeaderboardEntry> leaderboard = loadLeaderboard();

        leaderboard.add(new LeaderboardEntry(name, timeMillis));
        Collections.sort(leaderboard); // Sort fastest to slowest

        // Truncate to top 10
        if (leaderboard.size() > MAX_ENTRIES) {
            leaderboard = leaderboard.subList(0, MAX_ENTRIES);
        }

        saveLeaderboard(leaderboard);
    }

    public static List<LeaderboardEntry> loadLeaderboard() {
        ensureDirectoryExists();
        File file = new File(FILE_PATH);
        if (!file.exists()) return new ArrayList<>();

        try (Reader reader = new FileReader(file)) {
            Type listType = new TypeToken<ArrayList<LeaderboardEntry>>(){}.getType();
            List<LeaderboardEntry> data = GSON.fromJson(reader, listType);
            return data != null ? data : new ArrayList<>();
        } catch (IOException e) {
            System.err.println("Failed to load leaderboard data: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private static void saveLeaderboard(List<LeaderboardEntry> data) {
        ensureDirectoryExists();
        try (Writer writer = new FileWriter(FILE_PATH)) {
            GSON.toJson(data, writer);
        } catch (IOException e) {
            System.err.println("Failed to save leaderboard data: " + e.getMessage());
        }
    }
}