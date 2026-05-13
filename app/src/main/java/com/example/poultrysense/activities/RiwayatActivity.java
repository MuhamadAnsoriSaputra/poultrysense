package com.example.poultrysense.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.example.poultrysense.R;

public class RiwayatActivity extends AppCompatActivity {

    private ImageView navHome, navHistory, navNotif, navProfile;
    private androidx.cardview.widget.CardView navPakan;

    private TextView tabSemua, tabManual, tabOtomatis;
    private LinearLayout itemManual, itemOtomatis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayat);

        navHome = findViewById(R.id.nav_home);
        navHistory = findViewById(R.id.nav_history);
        navNotif = findViewById(R.id.nav_notif);
        navProfile = findViewById(R.id.nav_profile);
        navPakan = findViewById(R.id.nav_pakan);

        tabSemua = findViewById(R.id.tab_semua);
        tabManual = findViewById(R.id.tab_manual);
        tabOtomatis = findViewById(R.id.tab_otomatis);
        itemManual = findViewById(R.id.item_manual);
        itemOtomatis = findViewById(R.id.item_otomatis);

        setupTabs();
        setupBottomNavigation();
    }

    private void setupTabs() {
        tabSemua.setOnClickListener(v -> {
            updateTabAppearance(tabSemua, tabManual, tabOtomatis);
            itemManual.setVisibility(View.VISIBLE);
            itemOtomatis.setVisibility(View.VISIBLE);
        });

        tabManual.setOnClickListener(v -> {
            updateTabAppearance(tabManual, tabSemua, tabOtomatis);
            itemManual.setVisibility(View.VISIBLE);
            itemOtomatis.setVisibility(View.GONE);
        });

        tabOtomatis.setOnClickListener(v -> {
            updateTabAppearance(tabOtomatis, tabSemua, tabManual);
            itemManual.setVisibility(View.GONE);
            itemOtomatis.setVisibility(View.VISIBLE);
        });
    }

    private void updateTabAppearance(TextView activeTab, TextView inactiveTab1, TextView inactiveTab2) {
        activeTab.setTextColor(ContextCompat.getColor(this, R.color.teal_primary));
        activeTab.setTypeface(null, android.graphics.Typeface.BOLD);

        inactiveTab1.setTextColor(ContextCompat.getColor(this, R.color.text_gray));
        inactiveTab1.setTypeface(null, android.graphics.Typeface.NORMAL);

        inactiveTab2.setTextColor(ContextCompat.getColor(this, R.color.text_gray));
        inactiveTab2.setTypeface(null, android.graphics.Typeface.NORMAL);
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