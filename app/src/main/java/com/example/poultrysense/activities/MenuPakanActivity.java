package com.example.poultrysense.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.example.poultrysense.R;

public class MenuPakanActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_pakan);

        CardView cardJadwal = findViewById(R.id.card_jadwal_pakan);
        CardView cardManual = findViewById(R.id.card_pakan_manual);

        cardJadwal.setOnClickListener(v -> {
            startActivity(new Intent(this, JadwalPakanActivity.class));
        });

        cardManual.setOnClickListener(v -> {
            startActivity(new Intent(this, BeriPakanActivity.class));
        });

        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        ImageView navHome = findViewById(R.id.nav_home);
        ImageView navNotif = findViewById(R.id.nav_notif);
        ImageView navHistory = findViewById(R.id.nav_history);
        ImageView navProfile = findViewById(R.id.nav_profile);
        androidx.cardview.widget.CardView navPakan = findViewById(R.id.nav_pakan);

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
