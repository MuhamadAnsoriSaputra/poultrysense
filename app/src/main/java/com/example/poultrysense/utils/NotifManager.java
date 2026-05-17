package com.example.poultrysense.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import com.example.poultrysense.R;
import com.example.poultrysense.activities.NotificationActivity;
import com.example.poultrysense.models.NotificationItem;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class NotifManager {
    private static final String PREF_NAME = "NotifPrefs";
    private static final String KEY_NOTIF = "ListNotif";
    private SharedPreferences pref;

    public NotifManager(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void addNotification(NotificationItem item) {
        List<NotificationItem> list = getNotifications();
        list.add(0, item); // Add to top
        saveList(list);
    }

    public List<NotificationItem> getNotifications() {
        List<NotificationItem> list = new ArrayList<>();
        String json = pref.getString(KEY_NOTIF, "[]");
        try {
            JSONArray array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                list.add(new NotificationItem(
                        obj.getString("id"),
                        obj.getString("kategori"),
                        obj.getString("judul"),
                        obj.getString("pesan"),
                        obj.getString("waktu"),
                        obj.optBoolean("hidden", false)
                ));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void deleteNotification(String id) {
        List<NotificationItem> list = getNotifications();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId().equals(id)) {
                list.remove(i);
                break;
            }
        }
        saveList(list);
    }

    public void hideNotification(String id) {
        List<NotificationItem> list = getNotifications();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId().equals(id)) {
                list.get(i).setHidden(true);
                break;
            }
        }
        saveList(list);
    }

    private void saveList(List<NotificationItem> list) {
        JSONArray array = new JSONArray();
        try {
            for (NotificationItem n : list) {
                JSONObject obj = new JSONObject();
                obj.put("id", n.getId());
                obj.put("kategori", n.getKategori());
                obj.put("judul", n.getJudul());
                obj.put("pesan", n.getPesan());
                obj.put("waktu", n.getWaktu());
                obj.put("hidden", n.isHidden());
                array.put(obj);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        pref.edit().putString(KEY_NOTIF, array.toString()).apply();
    }

    public void showSystemNotification(Context context, String title, String message) {
        String channelId = "poultrysense_general_channel";
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "PoultrySense Notifikasi",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Saluran notifikasi umum PoultrySense");
            notificationManager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(context, NotificationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                (int) System.currentTimeMillis(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.logo_app)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }
}
