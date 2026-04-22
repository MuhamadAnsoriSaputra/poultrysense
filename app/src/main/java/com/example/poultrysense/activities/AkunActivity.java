package com.example.poultrysense.activities;

import com.example.poultrysense.R;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.poultrysense.utils.SessionManager; // Tambahkan ini
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AkunActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private SessionManager sessionManager; // Tambahkan ini agar sama dengan Dashboard
    private TextView txtNama, txtEmail;
    private LinearLayout btnLogout;
    private ImageView navHome, navHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_akun);

        // Inisialisasi Firebase & Session sesuai Dashboard
        mAuth = FirebaseAuth.getInstance();
        sessionManager = new SessionManager(this);

        FirebaseUser user = mAuth.getCurrentUser();

        txtNama = findViewById(R.id.txt_nama_akun);
        txtEmail = findViewById(R.id.txt_email_akun);
        btnLogout = findViewById(R.id.btn_logout);
        navHome = findViewById(R.id.nav_home);
        navHistory = findViewById(R.id.nav_history);

        if (user != null) {
            txtEmail.setText(user.getEmail());
            String name = user.getDisplayName();
            txtNama.setText((name != null && !name.isEmpty()) ? name : "User PoultrySense");
        }

        // --- LOGIKA KLIK ASLI ANDA YANG DISESUAIKAN DENGAN DASHBOARD ---

        btnLogout.setOnClickListener(v -> {
            // Memanggil dialog logout agar sama dengan dashboard
            showLogoutDialog();
        });

        navHome.setOnClickListener(v -> {
            Intent intent = new Intent(AkunActivity.this, DashboardActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
        });

        if (navHistory != null) {
            navHistory.setOnClickListener(v -> {
                Intent intent = new Intent(AkunActivity.this, RiwayatActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            });
        }
    }

    // Mengambil fungsi showLogoutDialog persis dari Dashboard Anda
    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Apakah Anda yakin ingin keluar?")
                .setPositiveButton("Ya", (dialog, which) -> {
                    mAuth.signOut();
                    sessionManager.logoutUser(); // Menggunakan session manager Anda
                    Intent intent = new Intent(this, LoginActivity.class);
                    // Flag ini yang memastikan balik ke LOGIN, bukan ke HOME
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Batal", null)
                .show();
    }
}