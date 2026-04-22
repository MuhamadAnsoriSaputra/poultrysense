package com.example.poultrysense.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
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

        if (navHome != null) {
            navHome.setOnClickListener(v -> {
                Intent intent = new Intent(RiwayatActivity.this, DashboardActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            });
        }

        if (navNotif != null) {
            navNotif.setOnClickListener(v -> {
                Toast.makeText(this, "Notifikasi", Toast.LENGTH_SHORT).show();
            });
        }

        if (navProfile != null) {
            navProfile.setOnClickListener(v -> {
                Intent intent = new Intent(RiwayatActivity.this, AkunActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            });
        }
    }
}