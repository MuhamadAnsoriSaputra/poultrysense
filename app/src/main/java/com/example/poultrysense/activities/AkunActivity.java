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

    private TextView txtNama, txtEmail, menuLihatProfil, menuUbahProfil, menuRiwayatAkun, menuNotifikasi, menuPusatBantuan, menuFAQ, menuSetelan;
    private LinearLayout btnLogout;
    private ImageView navHome, navHistory, imgProfileAkun;
    private androidx.cardview.widget.CardView navPakan;

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
        menuRiwayatAkun = findViewById(R.id.menu_riwayat_akun);
        menuNotifikasi = findViewById(R.id.menu_notifikasi);
        menuPusatBantuan = findViewById(R.id.menu_pusat_bantuan);
        menuFAQ = findViewById(R.id.menu_faq);
        menuSetelan = findViewById(R.id.menu_setelan);
        
        btnLogout = findViewById(R.id.btn_logout);
        navHome = findViewById(R.id.nav_home);
        navHistory = findViewById(R.id.nav_history);
        imgProfileAkun = findViewById(R.id.img_profile_akun);

        tampilkanNamaProfilAkun();
        tampilkanFotoProfilAkun();

        menuLihatProfil.setOnClickListener(v -> startActivity(new Intent(AkunActivity.this, LihatProfilActivity.class)));
        menuUbahProfil.setOnClickListener(v -> startActivity(new Intent(AkunActivity.this, UbahProfilActivity.class)));
        ImageView btnEditProfilIcon = findViewById(R.id.btn_edit_profil_icon);
        if (btnEditProfilIcon != null) {
            btnEditProfilIcon.setOnClickListener(v -> startActivity(new Intent(AkunActivity.this, UbahProfilActivity.class)));
        }
        if (imgProfileAkun != null) {
            imgProfileAkun.setOnClickListener(v -> startActivity(new Intent(AkunActivity.this, UbahProfilActivity.class)));
        }
        menuRiwayatAkun.setOnClickListener(v -> startActivity(new Intent(AkunActivity.this, RiwayatAkunActivity.class)));
        menuNotifikasi.setOnClickListener(v -> startActivity(new Intent(AkunActivity.this, NotificationAkunActivity.class)));
        
        // Link to new Help Center and FAQ
        menuPusatBantuan.setOnClickListener(v -> startActivity(new Intent(AkunActivity.this, PusatBantuanActivity.class)));
        menuFAQ.setOnClickListener(v -> startActivity(new Intent(AkunActivity.this, FAQActivity.class)));
        
        if (menuSetelan != null) {
            menuSetelan.setOnClickListener(v -> showThemeDialog());
        }

        TextView menuHapusAkun = findViewById(R.id.menu_hapus_akun);
        if (menuHapusAkun != null) {
            menuHapusAkun.setOnClickListener(v -> showDeleteAccountDialog());
        }

        btnLogout.setOnClickListener(v -> showLogoutDialog());

        ImageView navNotif = findViewById(R.id.nav_notif);
        navPakan = findViewById(R.id.nav_pakan);
        setupBottomNavigation(navHome, navNotif, navPakan, navHistory);
    }

    private void setupBottomNavigation(ImageView navHome, ImageView navNotif, androidx.cardview.widget.CardView navPakan, ImageView navHistory) {
        if (navHome != null)
            navHome.setOnClickListener(v -> navigateTo(DashboardActivity.class));
        if (navNotif != null)
            navNotif.setOnClickListener(v -> navigateTo(NotificationActivity.class));
        if (navPakan != null)
            navPakan.setOnClickListener(v -> navigateTo(MenuPakanActivity.class));
        if (navHistory != null)
            navHistory.setOnClickListener(v -> navigateTo(RiwayatActivity.class));
        ImageView navProfile = findViewById(R.id.nav_profile);
        if (navProfile != null)
            navProfile.setOnClickListener(v -> navigateTo(AkunActivity.class));
    }

    private void navigateTo(Class<?> targetActivity) {
        if (this.getClass() == targetActivity)
            return;
        Intent intent = new Intent(this, targetActivity);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        overridePendingTransition(0, 0);
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
        String emailKey = (user != null && user.getEmail() != null) ? user.getEmail() : "default";

        String namaFirebase = (user != null) ? user.getDisplayName() : null;
        String namaLocal = profilePrefs.getString("profile_name_" + emailKey, null);
        String email = (user != null) ? user.getEmail() : null;

        String emailPrefix = "User";
        if (email != null && email.contains("@")) {
            emailPrefix = email.substring(0, email.indexOf('@'));
        }

        String mamName = null;
        if (email != null) {
            com.example.poultrysense.utils.MultiAccountManager mam = new com.example.poultrysense.utils.MultiAccountManager(this);
            for (com.example.poultrysense.utils.MultiAccountManager.SavedAccount acc : mam.getSavedAccounts()) {
                if (acc.email.equals(email)) {
                    mamName = acc.name;
                    break;
                }
            }
        }

        String nama;
        if (namaLocal != null && !namaLocal.trim().isEmpty()) {
            nama = namaLocal;
        } else if (mamName != null && !mamName.trim().isEmpty()) {
            nama = mamName;
        } else if (namaFirebase != null && !namaFirebase.trim().isEmpty()) {
            nama = namaFirebase;
        } else {
            nama = emailPrefix;
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
        FirebaseUser user = mAuth.getCurrentUser();
        String emailKey = (user != null && user.getEmail() != null) ? user.getEmail() : "default";

        String savedUri = profilePrefs.getString("profile_photo_uri_" + emailKey, null);
        if (savedUri == null || savedUri.isEmpty()) {
            if (user != null && user.getEmail() != null) {
                com.example.poultrysense.utils.MultiAccountManager mam = new com.example.poultrysense.utils.MultiAccountManager(this);
                for (com.example.poultrysense.utils.MultiAccountManager.SavedAccount acc : mam.getSavedAccounts()) {
                    if (acc.email.equals(user.getEmail())) {
                        savedUri = acc.photoUri;
                        break;
                    }
                }
            }
        }

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

    private void showDeleteAccountDialog() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null || user.getEmail() == null) return;
        String email = user.getEmail();

        new AlertDialog.Builder(this)
                .setTitle("Hapus Akun")
                .setMessage("Apakah Anda yakin ingin menghapus akun ini (" + email + ") secara permanen dari perangkat?")
                .setPositiveButton("Ya", (dialog, which) -> {
                    com.example.poultrysense.utils.MultiAccountManager mam = new com.example.poultrysense.utils.MultiAccountManager(this);
                    mam.removeAccount(email);

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

    private void showThemeDialog() {
        String[] options = {"Gelap (Dark Mode)", "Terang (Light Mode)", "Mengikuti Sistem (Default)"};
        SharedPreferences prefs = getSharedPreferences("SETTINGS_PREFS", MODE_PRIVATE);
        int currentMode = prefs.getInt("night_mode", androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        
        int checkedItem = 2; // Mengikuti Sistem
        if (currentMode == androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES) checkedItem = 0;
        else if (currentMode == androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO) checkedItem = 1;

        new com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
                .setTitle("Pilih Tema Aplikasi")
                .setIcon(R.drawable.ic_setting)
                .setSingleChoiceItems(options, checkedItem, (dialog, which) -> {
                    int selectedNightMode = androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
                    if (which == 0) selectedNightMode = androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES;
                    else if (which == 1) selectedNightMode = androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO;

                    prefs.edit().putInt("night_mode", selectedNightMode).apply();
                    androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(selectedNightMode);
                    dialog.dismiss();
                })
                .setNegativeButton("Batal", null)
                .show();
    }
}