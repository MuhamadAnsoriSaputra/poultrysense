package com.example.poultrysense.utils;

import android.content.Context;
import android.content.SharedPreferences;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SessionManager {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context context;

    private static final String PREF_NAME = "PoultrySenseSession";
    private static final String IS_LOGIN = "IsLoggedIn";
    public static final String KEY_EMAIL = "email";
    
    // Pref untuk Riwayat Akun (Persistent)
    private static final String HISTORY_PREF_NAME = "AccountHistory";

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, 0);
        editor = pref.edit();
    }

    public void createLoginSession(String email) {
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_EMAIL, email);
        editor.commit();

        // Simpan waktu login terakhir ke Riwayat Akun (Persistent)
        SharedPreferences historyPref = context.getSharedPreferences(HISTORY_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor hEditor = historyPref.edit();
        
        String currentTime = new SimpleDateFormat("dd MMMM yyyy - hh:mm a", Locale.getDefault()).format(new Date());
        hEditor.putString("last_login_time", currentTime);
        hEditor.putString("last_login_location", android.os.Build.MODEL);
        hEditor.putLong("last_login_timestamp", System.currentTimeMillis());
        hEditor.apply();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }

    public void logoutUser() {
        // Hanya hapus session login, jangan hapus riwayat akun
        editor.clear();
        editor.commit();
    }
    
    public String getLastLoginTime() {
        SharedPreferences historyPref = context.getSharedPreferences(HISTORY_PREF_NAME, Context.MODE_PRIVATE);
        return historyPref.getString("last_login_time", "Belum pernah login");
    }
    
    public String getLastLoginLocation() {
        SharedPreferences historyPref = context.getSharedPreferences(HISTORY_PREF_NAME, Context.MODE_PRIVATE);
        return historyPref.getString("last_login_location", "Tidak diketahui");
    }

    public long getLastLoginTimestamp() {
        SharedPreferences historyPref = context.getSharedPreferences(HISTORY_PREF_NAME, Context.MODE_PRIVATE);
        return historyPref.getLong("last_login_timestamp", 0);
    }
}
