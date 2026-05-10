package com.example.poultrysense.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.poultrysense.R;

public class RiwayatActivity extends AppCompatActivity {

    private ImageView navHome, navHistory, navNotif, navProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayat);

        navHome = findViewById(R.id.nav_home);
        navHistory = findViewById(R.id.nav_history);
        navNotif = findViewById(R.id.nav_notif);
        navProfile = findViewById(R.id.nav_profile);

        // --- LOGIKA KLIK ASLI ANDA ---
        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
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
}