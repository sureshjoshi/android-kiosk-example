package com.sureshjoshi.android.kioskexample.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;

public class AppVersion implements Comparable<AppVersion> {

    private String mVersion;

    public final String get() {
        return mVersion;
    }

    public AppVersion(String version) {
        if (version == null) {
            throw new IllegalArgumentException("Version can not be null");
        }

        if (!version.matches("[0-9]+(\\.[0-9]+)*")) {
            throw new IllegalArgumentException("Invalid version format");
        }

        mVersion = version;
    }

    @Override
    public int compareTo(@NonNull AppVersion another) {
        String[] thisParts = mVersion.split("\\.");
        String[] thatParts = another.get().split("\\.");
        int length = Math.max(thisParts.length, thatParts.length);
        for (int i = 0; i < length; i++) {
            int thisPart = i < thisParts.length ? Integer.parseInt(thisParts[i]) : 0;
            int thatPart = i < thatParts.length ? Integer.parseInt(thatParts[i]) : 0;

            if (thisPart < thatPart) {
                return -1;
            }

            if (thisPart > thatPart) {
                return 1;
            }
        }
        return 0;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }

        if (that == null) {
            return false;
        }

        if (getClass() != that.getClass()) {
            return false;
        }

        return compareTo((AppVersion) that) == 0;
    }

    public static String getApplicationVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "";
        }
    }
}