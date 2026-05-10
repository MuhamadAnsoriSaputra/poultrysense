package com.example.poultrysense.activities;

import com.example.poultrysense.R;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {

    private TextView tabSemua, tabSistem, tabPromosi;
    private List<TextView> allTabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        // 1. Inisialisasi View Filter
        tabSemua = findViewById(R.id.tab_semua);
        tabSistem = findViewById(R.id.tab_sistem);
        tabPromosi = findViewById(R.id.tab_promosi);

        allTabs = new ArrayList<>();
        allTabs.add(tabSemua);
        allTabs.add(tabSistem);
        allTabs.add(tabPromosi);

        // 2. Inisialisasi Bottom Navigation
        ImageView navHome = findViewById(R.id.nav_home);
        ImageView navHistory = findViewById(R.id.nav_history);
        ImageView navProfile = findViewById(R.id.nav_profile);

        // 3. Logika Klik Tab
        for (TextView tab : allTabs) {
            tab.setOnClickListener(v -> {
                updateTabStyle((TextView) v);
                Toast.makeText(this, "Filter: " + ((TextView) v).getText(), Toast.LENGTH_SHORT).show();
            });
        }

        // 4. Navigasi
        setupBottomNavigation(navHome, findViewById(R.id.nav_notif), navHistory, navProfile);
    }

    private void setupBottomNavigation(ImageView navHome, ImageView navNotif, ImageView navHistory, ImageView navProfile) {
        if (navHome != null) navHome.setOnClickListener(v -> navigateTo(DashboardActivity.class));
        if (navNotif != null) navNotif.setOnClickListener(v -> navigateTo(NotificationActivity.class));
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

    private void updateTabStyle(TextView selectedTab) {
        for (TextView tab : allTabs) {
            if (tab == selectedTab) {
                tab.setBackgroundResource(R.drawable.bg_tab_active);
                tab.setTextColor(ContextCompat.getColor(this, R.color.teal_primary));
                tab.setTypeface(null, android.graphics.Typeface.BOLD);
            } else {
                tab.setBackgroundResource(R.drawable.bg_tab_inactive);
                tab.setTextColor(ContextCompat.getColor(this, R.color.text_gray));
                tab.setTypeface(null, android.graphics.Typeface.NORMAL);
            }
        }
    }
}