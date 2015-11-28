package com.sureshjoshi.android.kioskexample;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends Activity {

    @Bind(R.id.button_toggle_kiosk)
    public Button mButton;

    @OnClick(R.id.button_toggle_kiosk)
    public void toggleKioskMode() {
        enableKioskMode(!mIsKioskEnabled);
    }

    @OnClick(R.id.button_check_update)
    public void checkForUpdate() {

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
            Toast.makeText(this, getString(R.string.not_device_admin), Toast.LENGTH_SHORT).show();
        }

        if (mDpm.isDeviceOwnerApp(getPackageName())) {
            mDpm.setLockTaskPackages(deviceAdmin, new String[]{getPackageName()});
        } else {
            Toast.makeText(this, getString(R.string.not_device_owner), Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(this, getString(R.string.kiosk_not_permitted), Toast.LENGTH_SHORT).show();
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
