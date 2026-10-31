package git.artdeell.mojo.update;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import git.artdeell.mojo.BuildConfig;
import git.artdeell.mojo.MojoApplication;
import git.artdeell.mojo.R;

/**
 * Checks the GitHub release of Anime Launcher for a newer build, downloads it inside the app
 * and hands it to the system installer. No browser is involved.
 */
public final class AnimeUpdater {
    private static final String RELEASE_API = "https://api.github.com/repos/aamirplayzz/MojoLauncher/releases/tags/latest";
    private static final Pattern BUILD_PATTERN = Pattern.compile("build:\\s*(\\d+)");
    private static boolean sCheckedThisRun = false;

    private AnimeUpdater() {}

    /** Check once per app start, silently when nothing new is found. */
    public static void checkOnStart(Activity activity) {
        if(sCheckedThisRun) return;
        sCheckedThisRun = true;
        check(activity, false);
    }

    public static void check(Activity activity, boolean userInitiated) {
        MojoApplication.sExecutorService.execute(() -> {
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(RELEASE_API).openConnection();
                connection.setRequestProperty("User-Agent", "AnimeLauncher-Updater");
                connection.setRequestProperty("Accept", "application/vnd.github+json");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(15000);
                if(connection.getResponseCode() != 200) throw new Exception("HTTP " + connection.getResponseCode());
                String text = readAll(connection.getInputStream());
                JSONObject release = new JSONObject(text);
                Matcher matcher = BUILD_PATTERN.matcher(release.optString("body", ""));
                int latest = matcher.find() ? Integer.parseInt(matcher.group(1)) : -1;
                JSONArray assets = release.optJSONArray("assets");
                String url = null;
                if(assets != null) {
                    for(int i = 0; i < assets.length(); i++) {
                        JSONObject asset = assets.getJSONObject(i);
                        if(asset.optString("name").endsWith(".apk")) {
                            url = asset.getString("browser_download_url");
                            break;
                        }
                    }
                }
                final String apkUrl = url;
                final int latestBuild = latest;
                activity.runOnUiThread(() -> {
                    if(activity.isFinishing()) return;
                    if(apkUrl != null && latestBuild > BuildConfig.VERSION_CODE) {
                        showUpdateDialog(activity, apkUrl, latestBuild);
                    } else if(userInitiated) {
                        Toast.makeText(activity, R.string.anime_update_latest, Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Exception e) {
                if(userInitiated) activity.runOnUiThread(() ->
                        Toast.makeText(activity, R.string.anime_update_failed, Toast.LENGTH_LONG).show());
            }
        });
    }

    private static void showUpdateDialog(Activity activity, String apkUrl, int build) {
        new AlertDialog.Builder(activity)
                .setTitle(R.string.anime_update_available)
                .setMessage(activity.getString(R.string.anime_update_message, build))
                .setPositiveButton(R.string.anime_update_now, (d, w) -> download(activity, apkUrl))
                .setNegativeButton(R.string.anime_update_later, null)
                .show();
    }

    private static void download(Activity activity, String apkUrl) {
        final ProgressBar bar = new ProgressBar(activity, null, android.R.attr.progressBarStyleHorizontal);
        bar.setMax(100);
        bar.setPadding(48, 32, 48, 8);
        final AlertDialog dialog = new AlertDialog.Builder(activity)
                .setTitle(R.string.anime_update_downloading)
                .setView(bar)
                .setCancelable(false)
                .show();
        MojoApplication.sExecutorService.execute(() -> {
            try {
                File dir = new File(activity.getCacheDir(), "updates");
                if(!dir.isDirectory() && !dir.mkdirs()) throw new Exception("No cache dir");
                File apk = new File(dir, "AnimeLauncher.apk");
                HttpURLConnection connection = (HttpURLConnection) new URL(apkUrl).openConnection();
                connection.setRequestProperty("User-Agent", "AnimeLauncher-Updater");
                connection.setInstanceFollowRedirects(true);
                connection.setConnectTimeout(15000);
                connection.setReadTimeout(30000);
                if(connection.getResponseCode() != 200) throw new Exception("HTTP " + connection.getResponseCode());
                long total = connection.getContentLengthLong();
                try (InputStream in = connection.getInputStream(); OutputStream out = new FileOutputStream(apk)) {
                    byte[] buffer = new byte[64 * 1024];
                    long done = 0;
                    int read;
                    int lastPercent = -1;
                    while((read = in.read(buffer)) != -1) {
                        out.write(buffer, 0, read);
                        done += read;
                        if(total > 0) {
                            final int percent = (int) (done * 100 / total);
                            if(percent != lastPercent) {
                                lastPercent = percent;
                                activity.runOnUiThread(() -> bar.setProgress(percent));
                            }
                        }
                    }
                }
                activity.runOnUiThread(() -> {
                    dialog.dismiss();
                    install(activity, apk);
                });
            } catch (Exception e) {
                activity.runOnUiThread(() -> {
                    dialog.dismiss();
                    Toast.makeText(activity, R.string.anime_update_failed, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private static void install(Activity activity, File apk) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !activity.getPackageManager().canRequestPackageInstalls()) {
            Toast.makeText(activity, R.string.anime_update_allow_install, Toast.LENGTH_LONG).show();
            Intent settings = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:" + activity.getPackageName()));
            activity.startActivity(settings);
            return;
        }
        Uri uri = FileProvider.getUriForFile(activity, activity.getPackageName() + ".updates", apk);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    private static String readAll(InputStream in) throws Exception {
        try (InputStream stream = in) {
            java.io.ByteArrayOutputStream bytes = new java.io.ByteArrayOutputStream();
            byte[] buffer = new byte[8192];
            int read;
            while((read = stream.read(buffer)) != -1) bytes.write(buffer, 0, read);
            return bytes.toString("UTF-8");
        }
    }
}
