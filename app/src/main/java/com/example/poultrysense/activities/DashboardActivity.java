package com.example.poultrysense.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.PopupMenu;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.poultrysense.R;
import com.example.poultrysense.utils.SessionManager;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DashboardActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private SessionManager sessionManager;
    private ShapeableImageView imgProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Inisialisasi Firebase & Session
        mAuth = FirebaseAuth.getInstance();
        sessionManager = new SessionManager(this);

        // Inisialisasi View
        imgProfile = findViewById(R.id.imgProfile);

        // Klik Foto Profil
        imgProfile.setOnClickListener(v -> showProfileMenu());
    }

    private void showProfileMenu() {
        PopupMenu popup = new PopupMenu(this, imgProfile);

        // Menambah item menu
        popup.getMenu().add("Ganti Foto");
        popup.getMenu().add("Hapus Akun");
        popup.getMenu().add("Logout");

        popup.setOnMenuItemClickListener(item -> {
            String title = item.getTitle().toString();
            if (title.equals("Ganti Foto")) {
                Toast.makeText(this, "Fitur Ganti Foto", Toast.LENGTH_SHORT).show();
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
                    startActivity(new Intent(this, LoginActivity.class));
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
                                Toast.makeText(this, "Gagal menghapus akun", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .setNegativeButton("Batal", null)
                .show();
    }
}