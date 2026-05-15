package com.example.poultrysense.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.poultrysense.R;
import com.example.poultrysense.utils.SessionManager;

public class RiwayatAkunActivity extends AppCompatActivity {

    private TextView txtLastLoginTime, txtLastLoginLocation, txtLastProfileUpdate;
    private ImageView btnBack;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayat_akun);

        sessionManager = new SessionManager(this);
        
        btnBack = findViewById(R.id.btn_back);
        txtLastLoginTime = findViewById(R.id.txt_last_login_time);
        txtLastLoginLocation = findViewById(R.id.txt_last_login_location);
        txtLastProfileUpdate = findViewById(R.id.txt_last_profile_update);

        btnBack.setOnClickListener(v -> finish());

        displayAccountHistory();
        setupBottomNavigation();
    }

    private void displayAccountHistory() {
        // Ambil data login dari SessionManager
        long lastLoginTimestamp = sessionManager.getLastLoginTimestamp();
        long oneMonthInMillis = 30L * 24 * 60 * 60 * 1000;
        long currentTime = System.currentTimeMillis();

        // Riwayat login hanya tampil jika kurang dari 1 bulan
        if (currentTime - lastLoginTimestamp < oneMonthInMillis && lastLoginTimestamp != 0) {
            txtLastLoginTime.setText(sessionManager.getLastLoginTime());
            txtLastLoginLocation.setText(sessionManager.getLastLoginLocation());
        } else {
            txtLastLoginTime.setText("Tidak ada riwayat (1 bulan terakhir)");
            txtLastLoginLocation.setText("-");
        }

        // Ambil data update profil dari SharedPreferences PROFILE_PREFS (Simpan Selamanya)
        SharedPreferences profilePrefs = getSharedPreferences("PROFILE_PREFS", MODE_PRIVATE);
        String lastUpdate = profilePrefs.getString("last_profile_update", "Belum pernah diubah");
        txtLastProfileUpdate.setText(lastUpdate);
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
