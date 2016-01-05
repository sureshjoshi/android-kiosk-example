package com.sureshjoshi.android.kioskexample;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import com.sureshjoshi.android.kioskexample.utils.AppVersion;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class MainActivity extends Activity {

    private static final File DOWNLOAD_DIRECTORY = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

    @Bind(R.id.button_toggle_kiosk)
    Button mButton;

    @OnClick(R.id.button_toggle_kiosk)
    void toggleKioskMode() {
        enableKioskMode(!mIsKioskEnabled);
    }

    @OnClick(R.id.button_check_update)
    public void checkForUpdate() {
        // Look in downloaded directory, assuming it's called to "kiosk-x.y.z.apk"
        File files[] = DOWNLOAD_DIRECTORY.listFiles((dir, filename) -> {
            String lowerFilename = filename.toLowerCase();
            return lowerFilename.endsWith(".apk") && lowerFilename.contains("kiosk-");
        });

        if (files == null) {
            Timber.d("No files in downloads directory");
            return;
        }

        String applicationVersion = AppVersion.getApplicationVersion(this);

        // Figure out if the APK is newer than the current one
        // Base this off of filename convention
        for (File file : files) {
            String fileVersion = file.getName().substring(6, 11);
            Timber.d("Current filename is: %s, with version: %s", file.getName(), fileVersion);

            AppVersion appVersion = new AppVersion(fileVersion);
            int result = appVersion.compareTo(new AppVersion(AppVersion.getApplicationVersion(this)));
            if (result >= 1) {
                Timber.d("Application %s is older than %s", applicationVersion, fileVersion);
                final Intent installIntent = new Intent(Intent.ACTION_VIEW);
                installIntent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(installIntent);
                break;
            } else if (result == 0) {
                Timber.d("Application %s is same as %s", applicationVersion, fileVersion);
            } else {
                Timber.d("Application %s is newer than %s", applicationVersion, fileVersion);
            }
        }
    }


    @Bind(R.id.webview)
    public WebView mWebView;

    private View mDecorView;
    private DevicePolicyManager mDpm;
    private boolean mIsKioskEnabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        ComponentName deviceAdmin = new ComponentName(this, AdminReceiver.class);
        mDpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        if (!mDpm.isAdminActive(deviceAdmin)) {
            Toast.makeText(getApplicationContext(), getString(R.string.not_device_admin), Toast.LENGTH_SHORT).show();
        }

        if (mDpm.isDeviceOwnerApp(getPackageName())) {
            mDpm.setLockTaskPackages(deviceAdmin, new String[]{getPackageName()});
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.not_device_owner), Toast.LENGTH_SHORT).show();
        }

        mDecorView = getWindow().getDecorView();

        mWebView.loadUrl("http://www.vicarasolutions.com/");
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideSystemUI();
    }

    // This snippet hides the system bars.
    private void hideSystemUI() {
        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
        mDecorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    private void enableKioskMode(boolean enabled) {
        try {
            if (enabled) {
                if (mDpm.isLockTaskPermitted(this.getPackageName())) {
                    startLockTask();
                    mIsKioskEnabled = true;
                    mButton.setText(getString(R.string.exit_kiosk_mode));
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.kiosk_not_permitted), Toast.LENGTH_SHORT).show();
                }
            } else {
                stopLockTask();
                mIsKioskEnabled = false;
                mButton.setText(getString(R.string.enter_kiosk_mode));
            }
        } catch (Exception e) {
            // TODO: Log and handle appropriately
        }
    }
}
