package com.example.poultrysense.activities;

import com.example.poultrysense.R;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LihatProfilActivity extends AppCompatActivity {

    private ImageView btnBack, imgProfile;
    private FrameLayout layoutPilihFoto;

    private TextView txtNamaProfil, txtEmailProfil;
    private TextView txtInfoNama, txtInfoEmail, txtStatusAkun, txtTanggalDibuat;
    private Button btnUbahNama;

    private FirebaseAuth mAuth;
    private SharedPreferences profilePrefs;

    private ActivityResultLauncher<String[]> pilihFotoLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lihat_profil);

        mAuth = FirebaseAuth.getInstance();
        profilePrefs = getSharedPreferences("PROFILE_PREFS", MODE_PRIVATE);

        btnBack = findViewById(R.id.btn_back_profil);
        imgProfile = findViewById(R.id.img_detail_profile);
        layoutPilihFoto = findViewById(R.id.layout_pilih_foto);

        txtNamaProfil = findViewById(R.id.txt_detail_nama);
        txtEmailProfil = findViewById(R.id.txt_detail_email);
        txtInfoNama = findViewById(R.id.txt_info_nama);
        txtInfoEmail = findViewById(R.id.txt_info_email);
        txtStatusAkun = findViewById(R.id.txt_status_akun);
        txtTanggalDibuat = findViewById(R.id.txt_tanggal_akun);
        btnUbahNama = findViewById(R.id.btn_ubah_nama);

        pilihFotoLauncher = registerForActivityResult(
                new ActivityResultContracts.OpenDocument(),
                uri -> {
                    if (uri != null) {
                        try {
                            getContentResolver().takePersistableUriPermission(
                                    uri,
                                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                            );

                            profilePrefs.edit()
                                    .putString("profile_photo_uri", uri.toString())
                                    .apply();

                            imgProfile.setImageURI(uri);
                            Toast.makeText(this, "Foto profil berhasil diganti", Toast.LENGTH_SHORT).show();

                        } catch (Exception e) {
                            Toast.makeText(this, "Gagal mengambil foto profil", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        tampilkanDataUser();
        tampilkanFotoProfil();

        btnBack.setOnClickListener(v -> finish());

        layoutPilihFoto.setOnClickListener(v -> {
            pilihFotoLauncher.launch(new String[]{"image/*"});
        });

        btnUbahNama.setOnClickListener(v -> {
            tampilkanDialogUbahNama();
        });
    }

    private void tampilkanDataUser() {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            String namaFirebase = user.getDisplayName();
            String namaLocal = profilePrefs.getString("profile_name", null);
            String email = user.getEmail();

            String nama;

            if (namaFirebase != null && !namaFirebase.trim().isEmpty()) {
                nama = namaFirebase;
            } else if (namaLocal != null && !namaLocal.trim().isEmpty()) {
                nama = namaLocal;
            } else {
                nama = "User PoultrySense";
            }

            if (email == null || email.trim().isEmpty()) {
                email = "Email tidak tersedia";
            }

            txtNamaProfil.setText(nama);
            txtEmailProfil.setText(email);

            txtInfoNama.setText(nama);
            txtInfoEmail.setText(email);

            txtStatusAkun.setText("Aktif");

            if (user.getMetadata() != null) {
                long createdAt = user.getMetadata().getCreationTimestamp();

                String tanggal = new SimpleDateFormat("dd MMMM yyyy", new Locale("id", "ID"))
                        .format(new Date(createdAt));

                txtTanggalDibuat.setText(tanggal);
            } else {
                txtTanggalDibuat.setText("Tidak tersedia");
            }
        } else {
            txtNamaProfil.setText("User PoultrySense");
            txtEmailProfil.setText("Email tidak tersedia");
            txtInfoNama.setText("User PoultrySense");
            txtInfoEmail.setText("Email tidak tersedia");
            txtStatusAkun.setText("Tidak aktif");
            txtTanggalDibuat.setText("Tidak tersedia");
        }
    }

    private void tampilkanDialogUbahNama() {
        EditText inputNama = new EditText(this);
        inputNama.setHint("Masukkan nama profil");

        String namaSekarang = txtInfoNama.getText().toString();
        inputNama.setText(namaSekarang);
        inputNama.setSelection(inputNama.getText().length());

        new AlertDialog.Builder(this)
                .setTitle("Ubah Nama Profil")
                .setMessage("Masukkan nama yang ingin ditampilkan di profil.")
                .setView(inputNama)
                .setPositiveButton("Simpan", (dialog, which) -> {
                    String namaBaru = inputNama.getText().toString().trim();

                    if (namaBaru.isEmpty()) {
                        Toast.makeText(this, "Nama tidak boleh kosong", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    simpanNamaProfil(namaBaru);
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    private void simpanNamaProfil(String namaBaru) {
        FirebaseUser user = mAuth.getCurrentUser();

        profilePrefs.edit()
                .putString("profile_name", namaBaru)
                .apply();

        txtNamaProfil.setText(namaBaru);
        txtInfoNama.setText(namaBaru);

        if (user != null) {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(namaBaru)
                    .build();

            user.updateProfile(profileUpdates)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(this, "Nama profil berhasil diperbarui", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Nama tersimpan lokal, tapi gagal update ke Firebase", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(this, "Nama profil berhasil disimpan", Toast.LENGTH_SHORT).show();
        }
    }

    private void tampilkanFotoProfil() {
        String savedUri = profilePrefs.getString("profile_photo_uri", null);

        if (savedUri != null && !savedUri.isEmpty()) {
            try {
                imgProfile.setImageURI(Uri.parse(savedUri));
            } catch (Exception e) {
                imgProfile.setImageResource(R.drawable.profil);
            }
        } else {
            imgProfile.setImageResource(R.drawable.profil);
        }
    }
}