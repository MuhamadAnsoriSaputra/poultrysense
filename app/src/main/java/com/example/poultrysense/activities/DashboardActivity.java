package com.example.poultrysense.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.poultrysense.R;
import com.example.poultrysense.utils.SessionManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DashboardActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private SessionManager sessionManager;

    // Deklarasi View
    private ImageView imgProfile;
    private LinearLayout menuRiwayat, menuNotifikasi; // Tambahan menuNotifikasi
    private ImageView navHome, navNotif, navHistory, navProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // 1. Inisialisasi Firebase & Session
        mAuth = FirebaseAuth.getInstance();
        sessionManager = new SessionManager(this);

        // 2. Inisialisasi View (ID sesuai XML terbaru)
        imgProfile = findViewById(R.id.img_profile);
        menuRiwayat = findViewById(R.id.menu_riwayat);
        menuNotifikasi = findViewById(R.id.menu_notifikasi); // ID baru dari XML

        navHome = findViewById(R.id.nav_home);
        navNotif = findViewById(R.id.nav_notif);
        navHistory = findViewById(R.id.nav_history);
        navProfile = findViewById(R.id.nav_profile);

        // --- LOGIKA NAVIGASI NOTIFIKASI ---

        // Klik pada Icon Notifikasi di Kartu Menu (Tengah)
        if (menuNotifikasi != null) {
            menuNotifikasi.setOnClickListener(v -> goToNotification());
        }

        // Klik pada Icon Notifikasi di Bottom Bar
        if (navNotif != null) {
            navNotif.setOnClickListener(v -> goToNotification());
        }

        // --- LOGIKA NAVIGASI LAINNYA ---

        if (navHistory != null) {
            navHistory.setOnClickListener(v -> goToRiwayat());
        }

        if (menuRiwayat != null) {
            menuRiwayat.setOnClickListener(v -> goToRiwayat());
        }

        if (navHome != null) {
            navHome.setOnClickListener(v -> {
                Toast.makeText(this, "Anda sedang di Beranda", Toast.LENGTH_SHORT).show();
            });
        }

        if (imgProfile != null) {
            imgProfile.setOnClickListener(v -> showProfileMenu());
        }

        if (navProfile != null) {
            navProfile.setOnClickListener(v -> {
                Intent intent = new Intent(DashboardActivity.this, AkunActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                overridePendingTransition(0, 0);
            });
        }
    }

    // --- FUNGSI HELPER NAVIGASI ---

    private void goToNotification() {
        Intent intent = new Intent(DashboardActivity.this, NotificationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    private void goToRiwayat() {
        Intent intent = new Intent(DashboardActivity.this, RiwayatActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    // --- FUNGSI MENU PROFIL & AUTH ---

    private void showProfileMenu() {
        PopupMenu popup = new PopupMenu(this, imgProfile);
        popup.getMenu().add("Ganti Foto");
        popup.getMenu().add("Hapus Akun");
        popup.getMenu().add("Logout");

        popup.setOnMenuItemClickListener(item -> {
            String title = item.getTitle().toString();
            if (title.equals("Ganti Foto")) {
                Toast.makeText(this, "Fitur Ganti Foto segera hadir", Toast.LENGTH_SHORT).show();
            } else if (title.equals("Hapus Akun")) {
                showDeleteAccountDialog();
            } else if (title.equals("Logout")) {
                showLogoutDialog();
            }
            return true;
        });
        popup.show();
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Apakah Anda yakin ingin keluar?")
                .setPositiveButton("Ya", (dialog, which) -> {
                    mAuth.signOut();
                    sessionManager.logoutUser();
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    private void showDeleteAccountDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Hapus Akun")
                .setMessage("Tindakan ini permanen. Yakin?")
                .setPositiveButton("Hapus", (dialog, which) -> {
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        user.delete().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                sessionManager.logoutUser();
                                startActivity(new Intent(this, LoginActivity.class));
                                finish();
                            } else {
                                Toast.makeText(this, "Gagal menghapus akun. Silakan login ulang.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .setNegativeButton("Batal", null)
                .show();
    }
}