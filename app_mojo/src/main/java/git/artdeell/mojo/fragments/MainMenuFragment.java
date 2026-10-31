package git.artdeell.mojo.fragments;

import static git.artdeell.mojo.Tools.openPath;
import static git.artdeell.mojo.Tools.shareLog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.kdt.mcgui.ProgressLayout;
import com.kdt.mcgui.mcVersionSpinner;

import git.artdeell.mojo.CustomControlsActivity;
import git.artdeell.mojo.R;

import git.artdeell.mojo.Tools;
import git.artdeell.mojo.contracts.OpenDocumentWithExtension;
import git.artdeell.mojo.extra.ExtraConstants;
import git.artdeell.mojo.extra.ExtraCore;
import git.artdeell.mojo.instances.Instance;
import git.artdeell.mojo.instances.Instances;
import git.artdeell.mojo.utils.FileUtils;

import java.io.File;

public class MainMenuFragment extends Fragment {
    public static final String TAG = "MainMenuFragment";

    private mcVersionSpinner mVersionSpinner;

    private final ActivityResultLauncher<Object> mModInstallerLauncher =
            registerForActivityResult(new OpenDocumentWithExtension("jar"), (data)->{
                if(data != null) Tools.launchModInstaller(requireContext(), data);
            });

    public MainMenuFragment(){
        super(R.layout.fragment_launcher);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Button mNewsButton = view.findViewById(R.id.news_button);
        Button mDiscordButton = view.findViewById(R.id.social_media_button);
        Button mCustomControlButton = view.findViewById(R.id.custom_control_button);
        Button mInstallJarButton = view.findViewById(R.id.install_jar_button);
        Button mShareLogsButton = view.findViewById(R.id.share_logs_button);
        Button mOpenDirectoryButton = view.findViewById(R.id.open_files_button);

        ImageButton mEditProfileButton = view.findViewById(R.id.edit_profile_button);
        Button mPlayButton = view.findViewById(R.id.play_button);
        mVersionSpinner = view.findViewById(R.id.mc_version_spinner);

        if(mNewsButton != null) {
            mNewsButton.setOnClickListener(v -> openWiki());
            mNewsButton.setOnLongClickListener((v)->{
                openGamepadMapper();
                return true;
            });
        }
        if(mDiscordButton != null) mDiscordButton.setOnClickListener(v -> openSocial());
        if(mCustomControlButton != null) mCustomControlButton.setOnClickListener(v -> openCustomControls());
        if(mInstallJarButton != null) mInstallJarButton.setOnClickListener(v -> runInstallerWithConfirmation());
        if(mShareLogsButton != null) mShareLogsButton.setOnClickListener((v) -> shareLog(requireContext()));
        if(mOpenDirectoryButton != null) mOpenDirectoryButton.setOnClickListener((v)-> openGameDirectory(v.getContext()));

        mEditProfileButton.setOnClickListener(v -> mVersionSpinner.openProfileEditor(requireActivity()));
        mPlayButton.setOnClickListener(v -> ExtraCore.setValue(ExtraConstants.LAUNCH_GAME, true));

        bindSidebar(view);
    }

    private void bindSidebar(View view) {
        View navVersions = view.findViewById(R.id.nav_versions);
        View navSettings = view.findViewById(R.id.nav_settings);
        View navTools = view.findViewById(R.id.nav_tools);
        View navAbout = view.findViewById(R.id.nav_about);
        if(navVersions != null) navVersions.setOnClickListener(v -> mVersionSpinner.performClick());
        if(navSettings != null) navSettings.setOnClickListener(v -> {
            View settingsButton = requireActivity().findViewById(R.id.setting_button);
            if(settingsButton != null) settingsButton.performClick();
        });
        if(navTools != null) {
            navTools.setOnClickListener(v -> showToolsDialog());
            navTools.setOnLongClickListener(v -> {
                openGamepadMapper();
                return true;
            });
        }
        if(navAbout != null) navAbout.setOnClickListener(v -> new AlertDialog.Builder(requireContext())
                .setTitle(R.string.anime_about_title)
                .setMessage(R.string.anime_about_body)
                .setPositiveButton(R.string.anime_close, null)
                .show());
    }

    private void showToolsDialog() {
        final String[] items = new String[]{
                getString(R.string.mcl_tab_wiki),
                getString(R.string.mcl_button_social_media),
                getString(R.string.mcl_option_customcontrol),
                getString(R.string.main_install_jar_file),
                getString(R.string.main_share_logs),
                getString(R.string.mcl_button_open_directory)
        };
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.anime_tools_title)
                .setItems(items, (dialog, which) -> {
                    switch (which) {
                        case 0: openWiki(); break;
                        case 1: openSocial(); break;
                        case 2: openCustomControls(); break;
                        case 3: runInstallerWithConfirmation(); break;
                        case 4: shareLog(requireContext()); break;
                        case 5: openGameDirectory(requireContext()); break;
                    }
                })
                .show();
    }

    private void openWiki() {
        Tools.openURL(requireActivity(), Tools.URL_HOME);
    }

    private void openSocial() {
        Tools.openURL(requireActivity(), getString(R.string.social_media_invite));
    }

    private void openCustomControls() {
        startActivity(new Intent(requireContext(), CustomControlsActivity.class));
    }

    private void openGamepadMapper() {
        Tools.swapFragment(requireActivity(), GamepadMapperFragment.class, GamepadMapperFragment.TAG, null);
    }

    private void openGameDirectory(Context context) {
        Instance instance = Instances.loadSelectedInstance();
        if(instance == null) {
            Toast.makeText(context, R.string.no_instance, Toast.LENGTH_LONG).show();
            return;
        }
        File gameDirectory = instance.getGameDirectory();
        if(FileUtils.ensureDirectorySilently(gameDirectory)) {
            openPath(context, gameDirectory, false);
        }else {
            Toast.makeText(context, R.string.gamedir_open_failed, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ExtraCore.setValue(ExtraConstants.REFRESH_ACCOUNT_SPINNER, true);
    }

    private void runInstallerWithConfirmation() {
        if(!ProgressLayout.ensureNoOngoingTasks(requireContext())) return;
        mModInstallerLauncher.launch(null);
    }
}
