package com.example.poultrysense.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.poultrysense.R;
import com.example.poultrysense.adapters.NotificationAkunAdapter;
import com.example.poultrysense.utils.NotificationAkunManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class NotificationAkunActivity extends AppCompatActivity {

    private RecyclerView rvNotifAkun;
    private TextView txtEmpty, tabSemua, tabKeamanan, tabSistem;
    private ImageView btnBack, btnRefresh, navHome, navNotif, navHistory, navProfile;
    private CardView navPakan;

    private NotificationAkunAdapter adapter;
    private List<NotificationAkunManager.NotifItem> allNotifList = new ArrayList<>();
    private String currentFilter = "semua";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_akun);

        initViews();
        setupListeners();
        loadNotifData();
        setupBottomNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNotifData();
    }

    private void initViews() {
        rvNotifAkun = findViewById(R.id.rv_notif_akun);
        txtEmpty = findViewById(R.id.txt_empty_notif_akun);
        tabSemua = findViewById(R.id.tab_semua);
        tabKeamanan = findViewById(R.id.tab_keamanan);
        tabSistem = findViewById(R.id.tab_sistem);
        btnBack = findViewById(R.id.btn_back);
        btnRefresh = findViewById(R.id.btn_refresh_notif);

        navHome = findViewById(R.id.nav_home);
        navNotif = findViewById(R.id.nav_notif);
        navPakan = findViewById(R.id.nav_pakan);
        navHistory = findViewById(R.id.nav_history);
        navProfile = findViewById(R.id.nav_profile);

        rvNotifAkun.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NotificationAkunAdapter(new ArrayList<>());
        rvNotifAkun.setAdapter(adapter);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnRefresh.setOnClickListener(v -> {
            loadNotifData();
            Toast.makeText(this, "Data notifikasi diperbarui", Toast.LENGTH_SHORT).show();
        });

        tabSemua.setOnClickListener(v -> setFilter("semua"));
        tabKeamanan.setOnClickListener(v -> setFilter("keamanan"));
        tabSistem.setOnClickListener(v -> setFilter("sistem"));
    }

    private void loadNotifData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String emailKey = (user != null && user.getEmail() != null) ? user.getEmail() : "default";

        allNotifList = NotificationAkunManager.getNotifications(this, emailKey);
        applyFilter();
    }

    private void setFilter(String filter) {
        currentFilter = filter;
        updateTabStyles();
        applyFilter();
    }

    private void updateTabStyles() {
        tabSemua.setBackgroundResource(currentFilter.equals("semua") ? R.drawable.bg_tab_active : R.drawable.bg_tab_inactive);
        tabSemua.setTextColor(currentFilter.equals("semua") ? getResources().getColor(R.color.teal_primary) : getResources().getColor(R.color.text_gray));

        tabKeamanan.setBackgroundResource(currentFilter.equals("keamanan") ? R.drawable.bg_tab_active : R.drawable.bg_tab_inactive);
        tabKeamanan.setTextColor(currentFilter.equals("keamanan") ? getResources().getColor(R.color.teal_primary) : getResources().getColor(R.color.text_gray));

        tabSistem.setBackgroundResource(currentFilter.equals("sistem") ? R.drawable.bg_tab_active : R.drawable.bg_tab_inactive);
        tabSistem.setTextColor(currentFilter.equals("sistem") ? getResources().getColor(R.color.teal_primary) : getResources().getColor(R.color.text_gray));
    }

    private void applyFilter() {
        List<NotificationAkunManager.NotifItem> filteredList = new ArrayList<>();
        for (NotificationAkunManager.NotifItem item : allNotifList) {
            if (currentFilter.equals("semua") || item.type.equals(currentFilter)) {
                filteredList.add(item);
            }
        }

        adapter.updateData(filteredList);

        if (filteredList.isEmpty()) {
            txtEmpty.setVisibility(View.VISIBLE);
            rvNotifAkun.setVisibility(View.GONE);
        } else {
            txtEmpty.setVisibility(View.GONE);
            rvNotifAkun.setVisibility(View.VISIBLE);
        }
    }

    private void setupBottomNavigation() {
        if (navHome != null) navHome.setOnClickListener(v -> navigateTo(DashboardActivity.class));
        if (navNotif != null) navNotif.setOnClickListener(v -> navigateTo(NotificationActivity.class));
        if (navPakan != null) navPakan.setOnClickListener(v -> navigateTo(MenuPakanActivity.class));
        if (navHistory != null) navHistory.setOnClickListener(v -> navigateTo(RiwayatActivity.class));
        if (navProfile != null) navProfile.setOnClickListener(v -> navigateTo(AkunActivity.class));
    }

    private void navigateTo(Class<?> targetActivity) {
        if (this.getClass() == targetActivity) return;
        Intent intent = new Intent(this, targetActivity);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }
}
