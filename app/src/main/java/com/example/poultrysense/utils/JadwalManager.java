package com.example.poultrysense.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.poultrysense.models.JadwalPakan;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class JadwalManager {
    private static final String PREF_NAME = "JadwalPrefs";
    private static final String KEY_JADWAL = "ListJadwal";
    private SharedPreferences pref;
    private Context mContext;

    public JadwalManager(Context context) {
        this.mContext = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveJadwal(JadwalPakan jadwal) {
        List<JadwalPakan> list = getListJadwal();
        boolean exists = false;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId().equals(jadwal.getId())) {
                list.set(i, jadwal);
                exists = true;
                break;
            }
        }
        if (!exists) {
            list.add(jadwal);
        }
        saveList(list);

        if (jadwal.isAktif()) {
            scheduleAlarm(mContext, jadwal);
        } else {
            cancelAlarm(mContext, jadwal.getId());
        }
    }

    public List<JadwalPakan> getListJadwal() {
        List<JadwalPakan> list = new ArrayList<>();
        String json = pref.getString(KEY_JADWAL, "[]");
        try {
            JSONArray array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                list.add(new JadwalPakan(
                        obj.getString("id"),
                        obj.getString("jam"),
                        obj.getString("menit"),
                        obj.getString("label"),
                        obj.getBoolean("aktif")
                ));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void deleteJadwal(String id) {
        cancelAlarm(mContext, id);
        List<JadwalPakan> list = getListJadwal();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId().equals(id)) {
                list.remove(i);
                break;
            }
        }
        saveList(list);
    }

    private void saveList(List<JadwalPakan> list) {
        JSONArray array = new JSONArray();
        try {
            for (JadwalPakan j : list) {
                JSONObject obj = new JSONObject();
                obj.put("id", j.getId());
                obj.put("jam", j.getJam());
                obj.put("menit", j.getMenit());
                obj.put("label", j.getLabel());
                obj.put("aktif", j.isAktif());
                array.put(obj);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        pref.edit().putString(KEY_JADWAL, array.toString()).apply();
    }

    public void scheduleAlarm(Context context, JadwalPakan jadwal) {
        android.app.AlarmManager alarmManager = (android.app.AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        android.content.Intent intent = new android.content.Intent(context, PakanAlarmReceiver.class);
        intent.putExtra("jadwal_id", jadwal.getId());
        intent.putExtra("jadwal_label", jadwal.getLabel());

        int requestCode = jadwal.getId().hashCode();
        android.app.PendingIntent pendingIntent = android.app.PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                android.app.PendingIntent.FLAG_UPDATE_CURRENT | android.app.PendingIntent.FLAG_IMMUTABLE
        );

        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        try {
            int jam = Integer.parseInt(jadwal.getJam());
            int menit = Integer.parseInt(jadwal.getMenit());
            calendar.set(java.util.Calendar.HOUR_OF_DAY, jam);
            calendar.set(java.util.Calendar.MINUTE, menit);
            calendar.set(java.util.Calendar.SECOND, 0);
            calendar.set(java.util.Calendar.MILLISECOND, 0);

            if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
                calendar.add(java.util.Calendar.DATE, 1);
            }

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExact(android.app.AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                } else {
                    alarmManager.set(android.app.AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                }
            } else {
                alarmManager.setExact(android.app.AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cancelAlarm(Context context, String jadwalId) {
        android.app.AlarmManager alarmManager = (android.app.AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        android.content.Intent intent = new android.content.Intent(context, PakanAlarmReceiver.class);
        int requestCode = jadwalId.hashCode();
        android.app.PendingIntent pendingIntent = android.app.PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                android.app.PendingIntent.FLAG_UPDATE_CURRENT | android.app.PendingIntent.FLAG_IMMUTABLE
        );
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }
}
