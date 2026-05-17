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

public class NotificationAkunManager {

    private static final String PREF_NAME = "NotifAkunPrefs";

    public static class NotifItem {
        public String id;
        public String emailKey;
        public String type; // "keamanan" or "sistem"
        public String title;
        public String message;
        public String timestamp;
        public long timeMillis;

        public NotifItem(String id, String emailKey, String type, String title, String message, String timestamp, long timeMillis) {
            this.id = id;
            this.emailKey = emailKey;
            this.type = type;
            this.title = title;
            this.message = message;
            this.timestamp = timestamp;
            this.timeMillis = timeMillis;
        }
    }

    public static void addNotification(Context context, String emailKey, String type, String title, String message) {
        if (emailKey == null || emailKey.trim().isEmpty()) {
            emailKey = "default";
        }
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String key = "notif_" + emailKey;
        String jsonString = pref.getString(key, "[]");

        JSONArray jsonArray;
        try {
            jsonArray = new JSONArray(jsonString);
        } catch (JSONException e) {
            jsonArray = new JSONArray();
        }

        long currentTimeMillis = System.currentTimeMillis();
        String timestamp = new SimpleDateFormat("dd MMMM yyyy - hh:mm a", Locale.getDefault()).format(new Date(currentTimeMillis));

        try {
            // Mencegah duplikasi pesan yang sama persis di waktu berdekatan
            if (jsonArray.length() > 0) {
                JSONObject lastObj = jsonArray.getJSONObject(jsonArray.length() - 1);
                if (lastObj.optString("title").equals(title) && lastObj.optString("message").equals(message)) {
                    return;
                }
            }

            JSONObject newItem = new JSONObject();
            newItem.put("id", String.valueOf(currentTimeMillis));
            newItem.put("emailKey", emailKey);
            newItem.put("type", type);
            newItem.put("title", title);
            newItem.put("message", message);
            newItem.put("timestamp", timestamp);
            newItem.put("timeMillis", currentTimeMillis);

            jsonArray.put(newItem);
            pref.edit().putString(key, jsonArray.toString()).apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static List<NotifItem> getNotifications(Context context, String emailKey) {
        if (emailKey == null || emailKey.trim().isEmpty()) {
            emailKey = "default";
        }
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String key = "notif_" + emailKey;
        String jsonString = pref.getString(key, "[]");

        JSONArray jsonArray;
        try {
            jsonArray = new JSONArray(jsonString);
        } catch (JSONException e) {
            jsonArray = new JSONArray();
        }

        // Jika belum ada notifikasi sama sekali, tambahkan pesan sambutan default agar tidak kosong
        if (jsonArray.length() == 0) {
            long ts = System.currentTimeMillis() - 3600000; // 1 jam lalu
            String tsStr = new SimpleDateFormat("dd MMMM yyyy - hh:mm a", Locale.getDefault()).format(new Date(ts));
            try {
                JSONObject welcomeObj = new JSONObject();
                welcomeObj.put("id", String.valueOf(ts));
                welcomeObj.put("emailKey", emailKey);
                welcomeObj.put("type", "sistem");
                welcomeObj.put("title", "Selamat Datang di PoultrySense");
                welcomeObj.put("message", "Sistem pemantauan kandang dan keamanan akun Anda telah aktif sepenuhnya.");
                welcomeObj.put("timestamp", tsStr);
                welcomeObj.put("timeMillis", ts);

                jsonArray.put(welcomeObj);
                pref.edit().putString(key, jsonArray.toString()).apply();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        List<NotifItem> list = new ArrayList<>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                NotifItem item = new NotifItem(
                        obj.optString("id", ""),
                        obj.optString("emailKey", emailKey),
                        obj.optString("type", "sistem"),
                        obj.optString("title", ""),
                        obj.optString("message", ""),
                        obj.optString("timestamp", ""),
                        obj.optLong("timeMillis", 0)
                );
                list.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Collections.sort(list, (a, b) -> Long.compare(b.timeMillis, a.timeMillis));
        return list;
    }
}
