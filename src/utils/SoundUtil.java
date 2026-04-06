package utils;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

public class SoundUtil {

    private static final String BASE_PATH = "src/audioGUI/";

    private static Clip bgmClip;
    private static Clip bgmClip2;

    // ---------- ONE-SHOT SFX ----------

    public static void play(String relativePath) {
        play(relativePath, 1.0f);
    }

    public static void play(String relativePath, float volume) {
        String path = BASE_PATH + relativePath;

        try {
            File soundFile = new File(path);
            if (!soundFile.exists()) {
                soundFile = new File(System.getProperty("user.dir") + "/" + path);
            }
            if (!soundFile.exists()) {
                System.out.println("Sound file not found: " + soundFile.getAbsolutePath());
                return;
            }

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            setVolume(clip, volume);
            clip.start();

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.out.println("Error playing sound (" + path + "): " + e.getMessage());
        }
    }

    // ---------- LOOPING BGM ----------

    public static void playLoop(String relativePath) {
        playLoop(relativePath, 1.0f);
    }

    public static void playLoop(String relativePath, float volume) {
        stopLoop();

        String path = BASE_PATH + relativePath;

        try {
            File soundFile = new File(path);
            if (!soundFile.exists()) {
                soundFile = new File(System.getProperty("user.dir") + "/" + path);
            }
            if (!soundFile.exists()) {
                System.out.println("BGM file not found: " + soundFile.getAbsolutePath());
                return;
            }

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
            bgmClip = AudioSystem.getClip();
            bgmClip.open(audioStream);
            setVolume(bgmClip, volume);
            bgmClip.loop(Clip.LOOP_CONTINUOUSLY);

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.out.println("Error playing BGM (" + path + "): " + e.getMessage());
        }
    }

    public static void stopLoop() {
        if (bgmClip != null) {
            if (bgmClip.isRunning()) bgmClip.stop();
            bgmClip.close();
            bgmClip = null;
        }
    }

    // ---------- VOLUME HELPER ----------

    private static void setVolume(Clip clip, float volume) {
        if (volume < 0f) volume = 0f;
        if (volume > 1f) volume = 1f;

        if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            FloatControl gain = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float safe = (volume <= 0.0001f) ? 0.0001f : volume;
            float dB = (float) (20.0 * Math.log10(safe));
            gain.setValue(dB);
        }
    }

    // ---------- DELAY SFX ----------

    public static void playDelayed(String relativePath, int delayMs) {
        new Thread(() -> {
            try { Thread.sleep(delayMs); } catch (InterruptedException ignored) {}
            play(relativePath);
        }).start();
    }

    public static void playDelayed(String relativePath, int delayMs, float volume) {
        new Thread(() -> {
            try { Thread.sleep(delayMs); } catch (InterruptedException ignored) {}
            play(relativePath, volume);
        }).start();
    }

    // ---------- LOOPING BGM 2 ----------

    public static void playLoop2(String relativePath, float volume) {
        stopLoop2();

        String path = BASE_PATH + relativePath;

        try {
            File soundFile = new File(path);
            if (!soundFile.exists()) {
                soundFile = new File(System.getProperty("user.dir") + "/" + path);
            }
            if (!soundFile.exists()) {
                System.out.println("BGM2 file not found: " + soundFile.getAbsolutePath());
                return;
            }

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
            bgmClip2 = AudioSystem.getClip();
            bgmClip2.open(audioStream);
            setVolume(bgmClip2, volume);
            bgmClip2.loop(Clip.LOOP_CONTINUOUSLY);

        } catch (Exception e) {
            System.out.println("Error in playLoop2: " + e.getMessage());
        }
    }

    public static void stopLoop2() {
        if (bgmClip2 != null) {
            if (bgmClip2.isRunning()) bgmClip2.stop();
            bgmClip2.close();
            bgmClip2 = null;
        }
    }

    public static void playLoop2Delayed(String relativePath, float volume, int delayMs) {
        new Thread(() -> {
            try { Thread.sleep(delayMs); } catch (InterruptedException ignored) {}
            playLoop2(relativePath, volume);
        }).start();
    }

    public static void fadeOutLoop(int durationMs) {
        if (bgmClip == null || !bgmClip.isRunning()) return;
        final Clip clipToFade = bgmClip;
        float[] volume = {1.0f};
        int steps = 50;
        int stepDelay = durationMs / steps;
        javax.swing.Timer fadeTimer = new javax.swing.Timer(stepDelay, null);
        fadeTimer.addActionListener(e -> {
            volume[0] -= 1.0f / steps;
            if (volume[0] <= 0f) {
                fadeTimer.stop();
                clipToFade.stop();
                clipToFade.close();
                bgmClip = null;
            } else {
                setVolume(clipToFade, volume[0]);
            }
        });
        fadeTimer.start();
    }
}