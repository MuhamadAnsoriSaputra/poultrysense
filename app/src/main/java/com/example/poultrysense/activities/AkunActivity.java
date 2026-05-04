package com.example.poultrysense.activities;

import com.example.poultrysense.R;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.poultrysense.utils.SessionManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AkunActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private SessionManager sessionManager;

    private TextView txtNama, txtEmail, menuLihatProfil, menuUbahProfil;
    private LinearLayout btnLogout;
    private ImageView navHome, navHistory, imgProfileAkun;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_akun);

        mAuth = FirebaseAuth.getInstance();
        sessionManager = new SessionManager(this);

        txtNama = findViewById(R.id.txt_nama_akun);
        txtEmail = findViewById(R.id.txt_email_akun);
        menuLihatProfil = findViewById(R.id.menu_lihat_profil);
        menuUbahProfil = findViewById(R.id.menu_ubah_profil);
        btnLogout = findViewById(R.id.btn_logout);
        navHome = findViewById(R.id.nav_home);
        navHistory = findViewById(R.id.nav_history);
        imgProfileAkun = findViewById(R.id.img_profile_akun);

        tampilkanNamaProfilAkun();
        tampilkanFotoProfilAkun();

        menuLihatProfil.setOnClickListener(v -> {
            Intent intent = new Intent(AkunActivity.this, LihatProfilActivity.class);
            startActivity(intent);
        });

        menuUbahProfil.setOnClickListener(v -> {
            Intent intent = new Intent(AkunActivity.this, UbahProfilActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
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

    @Override
    protected void onResume() {
        super.onResume();

        tampilkanNamaProfilAkun();
        tampilkanFotoProfilAkun();
    }

    private void tampilkanNamaProfilAkun() {
        SharedPreferences profilePrefs = getSharedPreferences("PROFILE_PREFS", MODE_PRIVATE);
        FirebaseUser user = mAuth.getCurrentUser();

        String namaFirebase = null;
        String email = null;

        if (user != null) {
            namaFirebase = user.getDisplayName();
            email = user.getEmail();
        }

        String namaLocal = profilePrefs.getString("profile_name", null);

        String nama;
        if (namaFirebase != null && !namaFirebase.trim().isEmpty()) {
            nama = namaFirebase;
        } else if (namaLocal != null && !namaLocal.trim().isEmpty()) {
            nama = namaLocal;
        } else {
            nama = "User PoultrySense";
        }

        txtNama.setText(nama);

        if (email != null && !email.trim().isEmpty()) {
            txtEmail.setText(email);
        } else {
            txtEmail.setText("Email tidak tersedia");
        }
    }

    private void tampilkanFotoProfilAkun() {
        SharedPreferences profilePrefs = getSharedPreferences("PROFILE_PREFS", MODE_PRIVATE);
        String savedUri = profilePrefs.getString("profile_photo_uri", null);

        if (savedUri != null && !savedUri.isEmpty()) {
            try {
                imgProfileAkun.setImageURI(Uri.parse(savedUri));
            } catch (Exception e) {
                imgProfileAkun.setImageResource(R.drawable.profil);
            }
        } else {
            imgProfileAkun.setImageResource(R.drawable.profil);
        }
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Apakah Anda yakin ingin keluar?")
                .setPositiveButton("Ya", (dialog, which) -> {
                    mAuth.signOut();
                    sessionManager.logoutUser();

                    Intent intent = new Intent(AkunActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Batal", null)
                .show();
    }
}