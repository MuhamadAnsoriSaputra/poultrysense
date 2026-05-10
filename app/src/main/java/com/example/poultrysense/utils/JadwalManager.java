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

    public JadwalManager(Context context) {
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
}
