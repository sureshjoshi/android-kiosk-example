package com.sureshjoshi.android.kioskexample;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by sureshjoshi on 15-06-28.
 */
public class AdminReceiver extends DeviceAdminReceiver {

    private void showToast(Context context,String text) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onEnabled(Context context, Intent intent) {
        showToast(context, context.getString(R.string.device_admin_enabled));
    }

    @Override
    public CharSequence onDisableRequested(Context context, Intent intent) {
        return context.getString(R.string.device_admin_warning);
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        showToast(context, context.getString(R.string.device_admin_disabled));
    }

    @Override
    public void onLockTaskModeEntering(Context context, Intent intent,
                                       String pkg) {
        showToast(context, context.getString(R.string.kiosk_mode_enabled));
    }

    @Override
    public void onLockTaskModeExiting(Context context, Intent intent) {
        showToast(context, context.getString(R.string.kiosk_mode_disabled));
    }
}