package com.example.poultrysense.utils;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryAkunManager {

    private static final String PREF_NAME = "HistoryAkunPrefs";

    public static class HistoryItem {
        public String id;
        public String emailKey;
        public String type; // "login" or "ubah_profil"
        public String title;
        public String timestamp;
        public long timeMillis;
        public String device;
        public String location;

        public HistoryItem(String id, String emailKey, String type, String title, String timestamp, long timeMillis, String device, String location) {
            this.id = id;
            this.emailKey = emailKey;
            this.type = type;
            this.title = title;
            this.timestamp = timestamp;
            this.timeMillis = timeMillis;
            this.device = device;
            this.location = location;
        }
    }

    private static JSONArray migrateLegacyIfNeeded(Context context, SharedPreferences pref, String emailKey, String key, JSONArray jsonArray) {
        boolean migrated = pref.getBoolean("migrated_" + emailKey, false);
        if (!migrated) {
            try {
                SharedPreferences sessionHist = context.getSharedPreferences("AccountHistory", Context.MODE_PRIVATE);
                String lastLoginTime = sessionHist.getString("last_login_time", "");
                String lastLoginDevice = sessionHist.getString("last_login_location", android.os.Build.MODEL);
                long lastLoginTs = sessionHist.getLong("last_login_timestamp", System.currentTimeMillis() - 60000); // 1 menit lalu agar tidak bentrok

                if (!lastLoginTime.isEmpty()) {
                    // Cek apakah sudah ada di jsonArray
                    boolean exists = false;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        if (obj.optString("timestamp").equals(lastLoginTime) && obj.optString("type").equals("login")) {
                            exists = true;
                            break;
                        }
                    }
                    if (!exists) {
                        JSONObject loginObj = new JSONObject();
                        loginObj.put("id", String.valueOf(lastLoginTs));
                        loginObj.put("emailKey", emailKey);
                        loginObj.put("type", "login");
                        loginObj.put("title", "Login Berhasil");
                        loginObj.put("timestamp", lastLoginTime);
                        loginObj.put("timeMillis", lastLoginTs);
                        loginObj.put("device", lastLoginDevice);
                        loginObj.put("location", "Indonesia");
                        jsonArray.put(loginObj);
                    }
                }

                SharedPreferences profilePrefs = context.getSharedPreferences("PROFILE_PREFS", Context.MODE_PRIVATE);
                String lastProfileUpdate = profilePrefs.getString("last_profile_update_" + emailKey, profilePrefs.getString("last_profile_update", ""));
                if (!lastProfileUpdate.isEmpty() && !lastProfileUpdate.equals("Belum pernah diubah")) {
                    boolean exists = false;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        if (obj.optString("timestamp").equals(lastProfileUpdate) && obj.optString("type").equals("ubah_profil")) {
                            exists = true;
                            break;
                        }
                    }
                    if (!exists) {
                        long profileTs = System.currentTimeMillis() - 120000; // 2 menit lalu
                        JSONObject profObj = new JSONObject();
                        profObj.put("id", String.valueOf(profileTs));
                        profObj.put("emailKey", emailKey);
                        profObj.put("type", "ubah_profil");
                        profObj.put("title", "Profil Diperbarui");
                        profObj.put("timestamp", lastProfileUpdate);
                        profObj.put("timeMillis", profileTs);
                        profObj.put("device", android.os.Build.MODEL);
                        profObj.put("location", "Indonesia");
                        jsonArray.put(profObj);
                    }
                }

                pref.edit().putString(key, jsonArray.toString()).putBoolean("migrated_" + emailKey, true).apply();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonArray;
    }

    public static void addHistory(Context context, String emailKey, String type, String title, String device, String location) {
        if (emailKey == null || emailKey.trim().isEmpty()) {
            emailKey = "default";
        }
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String key = "history_" + emailKey;
        String jsonString = pref.getString(key, "[]");

        JSONArray jsonArray;
        try {
            jsonArray = new JSONArray(jsonString);
        } catch (JSONException e) {
            jsonArray = new JSONArray();
        }

        jsonArray = migrateLegacyIfNeeded(context, pref, emailKey, key, jsonArray);

        long currentTimeMillis = System.currentTimeMillis();
        String timestamp = new SimpleDateFormat("dd MMMM yyyy - hh:mm a", Locale.getDefault()).format(new Date(currentTimeMillis));

        // Mencegah duplikasi item yang sama persis dalam waktu yang berdekatan (misal spam beralih akun)
        try {
            if (jsonArray.length() > 0) {
                JSONObject lastObj = jsonArray.getJSONObject(jsonArray.length() - 1);
                if (lastObj.optString("type").equals(type) &&
                    lastObj.optString("title").equals(title) &&
                    lastObj.optString("timestamp").equals(timestamp)) {
                    // Sudah ada riwayat yang sama persis di menit yang sama, tidak perlu ditambahkan lagi
                    return;
                }
            }

            JSONObject newItem = new JSONObject();
            newItem.put("id", String.valueOf(currentTimeMillis));
            newItem.put("emailKey", emailKey);
            newItem.put("type", type);
            newItem.put("title", title);
            newItem.put("timestamp", timestamp);
            newItem.put("timeMillis", currentTimeMillis);
            newItem.put("device", device != null ? device : android.os.Build.MODEL);
            newItem.put("location", location != null ? location : "Indonesia");

            jsonArray.put(newItem);
            pref.edit().putString(key, jsonArray.toString()).apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static List<HistoryItem> getHistory(Context context, String emailKey) {
        if (emailKey == null || emailKey.trim().isEmpty()) {
            emailKey = "default";
        }
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String key = "history_" + emailKey;
        String jsonString = pref.getString(key, "[]");

        JSONArray jsonArray;
        try {
            jsonArray = new JSONArray(jsonString);
        } catch (JSONException e) {
            jsonArray = new JSONArray();
        }

        jsonArray = migrateLegacyIfNeeded(context, pref, emailKey, key, jsonArray);

        List<HistoryItem> list = new ArrayList<>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                HistoryItem item = new HistoryItem(
                        obj.optString("id", ""),
                        obj.optString("emailKey", emailKey),
                        obj.optString("type", "login"),
                        obj.optString("title", "Aktivitas Akun"),
                        obj.optString("timestamp", ""),
                        obj.optLong("timeMillis", 0),
                        obj.optString("device", android.os.Build.MODEL),
                        obj.optString("location", "Indonesia")
                );
                list.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Sort descending by timeMillis
        Collections.sort(list, (a, b) -> Long.compare(b.timeMillis, a.timeMillis));
        return list;
    }
}
