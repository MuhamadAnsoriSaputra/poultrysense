package com.example.poultrysense.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.net.Uri;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.poultrysense.R;
import com.example.poultrysense.utils.SessionManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DashboardActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private SessionManager sessionManager;

    private ImageView imgProfile;
    private LinearLayout menuRiwayat, menuNotifikasi;
    private ImageView navHome, navNotif, navHistory, navProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // 1. Inisialisasi Firebase & Session
        mAuth = FirebaseAuth.getInstance();
        sessionManager = new SessionManager(this);

        imgProfile = findViewById(R.id.img_profile);
        menuRiwayat = findViewById(R.id.menu_riwayat);
        menuNotifikasi = findViewById(R.id.menu_notifikasi);

        navHome = findViewById(R.id.nav_home);
        navNotif = findViewById(R.id.nav_notif);
        navHistory = findViewById(R.id.nav_history);
        navProfile = findViewById(R.id.nav_profile);

        if (menuNotifikasi != null)
            menuNotifikasi.setOnClickListener(v -> navigateTo(NotificationActivity.class));
        if (menuRiwayat != null)
            menuRiwayat.setOnClickListener(v -> navigateTo(RiwayatActivity.class));

        LinearLayout btnProfileSwitcher = findViewById(R.id.btn_profile_switcher);
        if (btnProfileSwitcher != null)
            btnProfileSwitcher.setOnClickListener(v -> showProfileMenu(btnProfileSwitcher));
        else if (imgProfile != null)
            imgProfile.setOnClickListener(v -> showProfileMenu(imgProfile));

        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        if (navHome != null)
            navHome.setOnClickListener(v -> navigateTo(DashboardActivity.class));
        if (navNotif != null)
            navNotif.setOnClickListener(v -> navigateTo(NotificationActivity.class));
        if (navHistory != null)
            navHistory.setOnClickListener(v -> navigateTo(RiwayatActivity.class));
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
        tampilkanDataProfil();
    }

    private void tampilkanDataProfil() {
        android.content.SharedPreferences profilePrefs = getSharedPreferences("PROFILE_PREFS", MODE_PRIVATE);
        FirebaseUser user = mAuth.getCurrentUser();

        // Tampilkan Nama
        String namaFirebase = (user != null) ? user.getDisplayName() : null;
        String namaLocal = profilePrefs.getString("profile_name", null);
        String email = (user != null) ? user.getEmail() : null;

        // Fallback: ambil bagian sebelum '@' dari email
        String emailPrefix = "User";
        if (email != null && email.contains("@")) {
            emailPrefix = email.substring(0, email.indexOf('@'));
        }

        String nama;
        if (namaFirebase != null && !namaFirebase.trim().isEmpty()) {
            nama = namaFirebase;
        } else if (namaLocal != null && !namaLocal.trim().isEmpty()) {
            nama = namaLocal;
        } else {
            nama = emailPrefix;
        }

        android.widget.TextView txtHiUser = findViewById(R.id.txt_hi_user);
        if (txtHiUser != null) {
            txtHiUser.setText("Hi " + nama + ",");
        }

        // Tampilkan Foto
        String savedUri = profilePrefs.getString("profile_photo_uri", null);
        if (savedUri != null && !savedUri.isEmpty() && imgProfile != null) {
            try {
                imgProfile.setImageURI(android.net.Uri.parse(savedUri));
            } catch (Exception e) {
                imgProfile.setImageResource(R.drawable.profil);
            }
        } else if (imgProfile != null) {
            imgProfile.setImageResource(R.drawable.profil);
        }

        // Pastikan akun aktif selalu ada di MultiAccountManager
        if (user != null && user.getEmail() != null) {
            com.example.poultrysense.utils.MultiAccountManager mam = new com.example.poultrysense.utils.MultiAccountManager(
                    this);
            java.util.List<com.example.poultrysense.utils.MultiAccountManager.SavedAccount> accs = mam
                    .getSavedAccounts();

            boolean found = false;
            for (com.example.poultrysense.utils.MultiAccountManager.SavedAccount acc : accs) {
                if (acc.email.equals(user.getEmail())) {
                    // Update nama & foto jika berubah
                    acc.name = nama;
                    acc.photoUri = savedUri != null ? savedUri : "";
                    mam.saveAccount(acc.email, acc.password, acc.name, acc.photoUri);
                    found = true;
                    break;
                }
            }

            // Jika belum ada (sesi lama sebelum fitur ini), tambahkan dengan password
            // kosong
            if (!found) {
                mam.saveAccount(user.getEmail(), "", nama, savedUri != null ? savedUri : "");
            }
        }
    }

    // --- FUNGSI MENU PROFIL & AUTH ---

    private void showProfileMenu(android.view.View anchor) {
        com.google.android.material.bottomsheet.BottomSheetDialog bottomSheetDialog = new com.google.android.material.bottomsheet.BottomSheetDialog(
                this);
        android.view.View view = getLayoutInflater().inflate(R.layout.layout_bottom_sheet_accounts, null);
        bottomSheetDialog.setContentView(view);

        LinearLayout containerAccounts = view.findViewById(R.id.container_accounts);
        LinearLayout btnTambahAkun = view.findViewById(R.id.btn_tambah_akun);

        com.example.poultrysense.utils.MultiAccountManager mam = new com.example.poultrysense.utils.MultiAccountManager(
                this);
        java.util.List<com.example.poultrysense.utils.MultiAccountManager.SavedAccount> accounts = mam
                .getSavedAccounts();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        for (com.example.poultrysense.utils.MultiAccountManager.SavedAccount acc : accounts) {
            android.view.View itemAccount = getLayoutInflater().inflate(R.layout.item_account_switch, null);
            android.widget.TextView txtName = itemAccount.findViewById(R.id.txt_account_name);
            android.widget.TextView txtEmail = itemAccount.findViewById(R.id.txt_account_email);
            ImageView imgAccProfile = itemAccount.findViewById(R.id.img_account_profile);
            ImageView imgActive = itemAccount.findViewById(R.id.img_active_indicator);

            txtName.setText(acc.name);
            txtEmail.setVisibility(android.view.View.GONE);

            if (acc.photoUri != null && !acc.photoUri.isEmpty()) {
                try {
                    imgAccProfile.setImageURI(android.net.Uri.parse(acc.photoUri));
                } catch (Exception e) {
                    imgAccProfile.setImageResource(R.drawable.profil);
                }
            }

            if (currentUser != null && acc.email.equals(currentUser.getEmail())) {
                imgActive.setVisibility(android.view.View.VISIBLE);
            }

            itemAccount.setOnClickListener(v -> {
                bottomSheetDialog.dismiss();
                if (currentUser != null && acc.email.equals(currentUser.getEmail())) {
                    return; // Sudah aktif
                }

                // Jika password kosong (sesi lama), arahkan ke login manual
                if (acc.password == null || acc.password.isEmpty()) {
                    mAuth.signOut();
                    sessionManager.logoutUser();
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.putExtra("prefill_email", acc.email);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                    return;
                }

                android.app.ProgressDialog pd = new android.app.ProgressDialog(this);
                pd.setMessage("Beralih ke " + acc.name + "...");
                pd.show();

                mAuth.signInWithEmailAndPassword(acc.email, acc.password).addOnCompleteListener(task -> {
                    pd.dismiss();
                    if (task.isSuccessful()) {
                        sessionManager.createLoginSession(acc.email);
                        startActivity(new Intent(this, DashboardActivity.class));
                        finish();
                        overridePendingTransition(0, 0);
                    } else {
                        Toast.makeText(this, "Gagal beralih akun. Silakan login manual.", Toast.LENGTH_SHORT).show();
                        mAuth.signOut();
                        sessionManager.logoutUser();
                        startActivity(new Intent(this, LoginActivity.class));
                        finish();
                    }
                });
            });

            containerAccounts.addView(itemAccount);
        }

        btnTambahAkun.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            mAuth.signOut();
            sessionManager.logoutUser();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        bottomSheetDialog.show();
    }

}