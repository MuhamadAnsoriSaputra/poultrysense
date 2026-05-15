package com.example.poultrysense.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.poultrysense.models.HistoryPakan;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class HistoryManager {
    private static final String PREF_NAME = "HistoryPrefs";
    private static final String KEY_HISTORY = "ListHistory";
    private SharedPreferences pref;

    public HistoryManager(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void addHistory(HistoryPakan history) {
        List<HistoryPakan> list = getFullHistory();
        list.add(0, history); // Add to the top
        saveList(list);
    }

    public List<HistoryPakan> getFullHistory() {
        List<HistoryPakan> list = new ArrayList<>();
        String json = pref.getString(KEY_HISTORY, "[]");
        try {
            JSONArray array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                list.add(new HistoryPakan(
                        obj.getString("id"),
                        obj.getString("tipe"),
                        obj.getInt("jumlahGram"),
                        obj.getString("waktu"),
                        obj.getString("bulan")
                ));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<HistoryPakan> getHistoryByMonth(String monthYear) {
        List<HistoryPakan> filtered = new ArrayList<>();
        for (HistoryPakan h : getFullHistory()) {
            if (h.getBulan().equals(monthYear)) {
                filtered.add(h);
            }
        }
        return filtered;
    }

    public List<String> getAvailableMonths() {
        List<String> months = new ArrayList<>();
        for (HistoryPakan h : getFullHistory()) {
            if (!months.contains(h.getBulan())) {
                months.add(h.getBulan());
            }
        }
        return months;
    }

    public void deleteHistory(String id) {
        List<HistoryPakan> list = getFullHistory();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId().equals(id)) {
                list.remove(i);
                break;
            }
        }
        saveList(list);
    }

    private void saveList(List<HistoryPakan> list) {
        JSONArray array = new JSONArray();
        try {
            for (HistoryPakan h : list) {
                JSONObject obj = new JSONObject();
                obj.put("id", h.getId());
                obj.put("tipe", h.getTipe());
                obj.put("jumlahGram", h.getJumlahGram());
                obj.put("waktu", h.getWaktu());
                obj.put("bulan", h.getBulan());
                array.put(obj);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        pref.edit().putString(KEY_HISTORY, array.toString()).apply();
    }
}
