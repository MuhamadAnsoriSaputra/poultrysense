package com.example.poultrysense.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.poultrysense.R;
import com.example.poultrysense.adapters.HistoryAkunAdapter;
import com.example.poultrysense.utils.HistoryAkunManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class RiwayatAkunActivity extends AppCompatActivity {

    private ImageView btnBack;
    private TextView tabSemua, tabLogin, tabUbahProfil, txtEmpty;
    private RecyclerView rvHistory;
    private HistoryAkunAdapter adapter;
    private List<HistoryAkunManager.HistoryItem> allHistoryList;
    private String currentFilter = "semua"; // "semua", "login", "ubah_profil"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayat_akun);

        btnBack = findViewById(R.id.btn_back);
        tabSemua = findViewById(R.id.tab_semua);
        tabLogin = findViewById(R.id.tab_login);
        tabUbahProfil = findViewById(R.id.tab_ubah_profil);
        txtEmpty = findViewById(R.id.txt_empty_history_akun);
        rvHistory = findViewById(R.id.rv_history_akun);

        rvHistory.setLayoutManager(new LinearLayoutManager(this));

        btnBack.setOnClickListener(v -> finish());

        tabSemua.setOnClickListener(v -> setFilter("semua"));
        tabLogin.setOnClickListener(v -> setFilter("login"));
        tabUbahProfil.setOnClickListener(v -> setFilter("ubah_profil"));

        loadHistoryData();
        setupBottomNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadHistoryData();
    }

    private void loadHistoryData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String emailKey = (user != null && user.getEmail() != null) ? user.getEmail() : "default";

        allHistoryList = HistoryAkunManager.getHistory(this, emailKey);
        applyFilter();
    }

    private void setFilter(String filter) {
        currentFilter = filter;
        updateTabStyles();
        applyFilter();
    }

    private void updateTabStyles() {
        tabSemua.setBackgroundResource(R.drawable.bg_tab_inactive);
        tabSemua.setTextColor(getResources().getColor(R.color.text_gray));
        tabSemua.setTypeface(null, android.graphics.Typeface.NORMAL);

        tabLogin.setBackgroundResource(R.drawable.bg_tab_inactive);
        tabLogin.setTextColor(getResources().getColor(R.color.text_gray));
        tabLogin.setTypeface(null, android.graphics.Typeface.NORMAL);

        tabUbahProfil.setBackgroundResource(R.drawable.bg_tab_inactive);
        tabUbahProfil.setTextColor(getResources().getColor(R.color.text_gray));
        tabUbahProfil.setTypeface(null, android.graphics.Typeface.NORMAL);

        if ("semua".equals(currentFilter)) {
            tabSemua.setBackgroundResource(R.drawable.bg_tab_active);
            tabSemua.setTextColor(getResources().getColor(R.color.teal_primary));
            tabSemua.setTypeface(null, android.graphics.Typeface.BOLD);
        } else if ("login".equals(currentFilter)) {
            tabLogin.setBackgroundResource(R.drawable.bg_tab_active);
            tabLogin.setTextColor(getResources().getColor(R.color.teal_primary));
            tabLogin.setTypeface(null, android.graphics.Typeface.BOLD);
        } else if ("ubah_profil".equals(currentFilter)) {
            tabUbahProfil.setBackgroundResource(R.drawable.bg_tab_active);
            tabUbahProfil.setTextColor(getResources().getColor(R.color.teal_primary));
            tabUbahProfil.setTypeface(null, android.graphics.Typeface.BOLD);
        }
    }

    private void applyFilter() {
        List<HistoryAkunManager.HistoryItem> filteredList = new ArrayList<>();
        if ("semua".equals(currentFilter)) {
            filteredList.addAll(allHistoryList);
        } else {
            for (HistoryAkunManager.HistoryItem item : allHistoryList) {
                if (currentFilter.equals(item.type)) {
                    filteredList.add(item);
                }
            }
        }

        if (adapter == null) {
            adapter = new HistoryAkunAdapter(filteredList);
            rvHistory.setAdapter(adapter);
        } else {
            adapter.updateData(filteredList);
        }

        if (filteredList.isEmpty()) {
            txtEmpty.setVisibility(View.VISIBLE);
            rvHistory.setVisibility(View.GONE);
        } else {
            txtEmpty.setVisibility(View.GONE);
            rvHistory.setVisibility(View.VISIBLE);
        }
    }

    private void setupBottomNavigation() {
        findViewById(R.id.nav_home).setOnClickListener(v -> navigateTo(DashboardActivity.class));
        findViewById(R.id.nav_notif).setOnClickListener(v -> navigateTo(NotificationActivity.class));
        findViewById(R.id.nav_pakan).setOnClickListener(v -> navigateTo(MenuPakanActivity.class));
        findViewById(R.id.nav_history).setOnClickListener(v -> navigateTo(RiwayatActivity.class));
        findViewById(R.id.nav_profile).setOnClickListener(v -> navigateTo(AkunActivity.class));
    }

    private void navigateTo(Class<?> targetActivity) {
        if (this.getClass() == targetActivity) return;
        Intent intent = new Intent(this, targetActivity);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }
}
