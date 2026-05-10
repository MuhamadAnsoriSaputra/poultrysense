package com.example.poultrysense.utils;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MultiAccountManager {

    private static final String PREF_NAME = "MultiAccountPrefs";
    private static final String KEY_ACCOUNTS = "SavedAccounts";
    private SharedPreferences pref;

    public MultiAccountManager(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveAccount(String email, String password, String name, String photoUri) {
        List<SavedAccount> accounts = getSavedAccounts();
        
        // Remove if exists to update it
        for (int i = 0; i < accounts.size(); i++) {
            if (accounts.get(i).email.equals(email)) {
                accounts.remove(i);
                break;
            }
        }

        accounts.add(new SavedAccount(email, password, name, photoUri));
        saveAccountsList(accounts);
    }

    public List<SavedAccount> getSavedAccounts() {
        List<SavedAccount> accountList = new ArrayList<>();
        String json = pref.getString(KEY_ACCOUNTS, "[]");
        try {
            JSONArray array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                accountList.add(new SavedAccount(
                        obj.getString("email"),
                        obj.getString("password"),
                        obj.optString("name", "User"),
                        obj.optString("photoUri", "")
                ));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return accountList;
    }

    private void saveAccountsList(List<SavedAccount> accounts) {
        JSONArray array = new JSONArray();
        try {
            for (SavedAccount acc : accounts) {
                JSONObject obj = new JSONObject();
                obj.put("email", acc.email);
                obj.put("password", acc.password);
                obj.put("name", acc.name);
                obj.put("photoUri", acc.photoUri);
                array.put(obj);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        pref.edit().putString(KEY_ACCOUNTS, array.toString()).apply();
    }

    public void removeAccount(String email) {
        List<SavedAccount> accounts = getSavedAccounts();
        for (int i = 0; i < accounts.size(); i++) {
            if (accounts.get(i).email.equals(email)) {
                accounts.remove(i);
                break;
            }
        }
        saveAccountsList(accounts);
    }

    public static class SavedAccount {
        public String email;
        public String password;
        public String name;
        public String photoUri;

        public SavedAccount(String email, String password, String name, String photoUri) {
            this.email = email;
            this.password = password;
            this.name = name;
            this.photoUri = photoUri;
        }
    }
}
