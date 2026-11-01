package git.artdeell.mojo.utils;

import android.util.Log;

import androidx.annotation.NonNull;

import git.artdeell.mojo.Tools;
import git.artdeell.mojo.prefs.LauncherPreferences;

import java.io.File;
import java.io.IOException;

/**
 * Applies light in-game options to an instance once, so weak phones start with settings
 * that are cheap to render. The user can change every option in the game afterwards,
 * the profile is not applied again for the same instance.
 */
public final class PerformanceProfile {
    private static final String TAG = "PerformanceProfile";
    private static final String MARKER_NAME = ".anime_perf_profile_v1";

    private PerformanceProfile() {}

    /** Values that only lower the load. An option the user already set lower is kept. */
    private static final String[][] LOWER_IS_BETTER = {
            {"renderDistance", "4"},
            {"simulationDistance", "5"},
    };

    private static final String[][] FORCED = {
            {"particles", "2"},
            {"graphicsMode", "0"},
            {"fancyGraphics", "false"},
            {"entityShadows", "false"},
            {"biomeBlendRadius", "0"},
            {"mipmapLevels", "0"},
            {"enableVsync", "false"},
            {"maxFps", "260"},
            {"bobView", "false"},
            {"entityDistanceScaling", "0.5"},
    };

    public static void applyIfNeeded(@NonNull File gameDir) {
        if(!LauncherPreferences.PREF_ANIME_PERF_PROFILE) return;
        File marker = new File(gameDir, MARKER_NAME);
        if(marker.exists()) return;
        try {
            MCOptionUtils.load(gameDir.getAbsolutePath());
            for(String[] option : LOWER_IS_BETTER) {
                int target = Integer.parseInt(option[1]);
                int current = GameOptionsUtils.parseIntDefault(MCOptionUtils.get(option[0]), Integer.MAX_VALUE);
                if(current > target) MCOptionUtils.set(option[0], option[1]);
            }
            for(String[] option : FORCED) {
                MCOptionUtils.set(option[0], option[1]);
            }
            MCOptionUtils.save();
            Tools.write(marker.getAbsolutePath(), "1");
        } catch (IOException | RuntimeException e) {
            Log.w(TAG, "Could not apply the performance profile", e);
        }
    }
}
