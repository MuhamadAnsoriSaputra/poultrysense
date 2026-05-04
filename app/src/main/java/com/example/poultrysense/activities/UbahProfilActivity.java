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
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class UbahProfilActivity extends AppCompatActivity {

    private ImageView btnBack, imgProfile;
    private FrameLayout layoutPilihFoto;
    private EditText edtNamaProfil;
    private TextView txtEmailProfil;
    private Button btnSimpanProfil;

    private FirebaseAuth mAuth;
    private SharedPreferences profilePrefs;

    private Uri selectedImageUri = null;
    private ActivityResultLauncher<String[]> pilihFotoLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubah_profil);

        mAuth = FirebaseAuth.getInstance();
        profilePrefs = getSharedPreferences("PROFILE_PREFS", MODE_PRIVATE);

        btnBack = findViewById(R.id.btn_back_ubah_profil);
        imgProfile = findViewById(R.id.img_ubah_profile);
        layoutPilihFoto = findViewById(R.id.layout_pilih_foto);
        edtNamaProfil = findViewById(R.id.edt_nama_profil);
        txtEmailProfil = findViewById(R.id.txt_email_ubah_profil);
        btnSimpanProfil = findViewById(R.id.btn_simpan_profil);

        pilihFotoLauncher = registerForActivityResult(
                new ActivityResultContracts.OpenDocument(),
                uri -> {
                    if (uri != null) {
                        try {
                            getContentResolver().takePersistableUriPermission(
                                    uri,
                                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                            );

                            selectedImageUri = uri;
                            imgProfile.setImageURI(uri);

                        } catch (Exception e) {
                            Toast.makeText(this, "Gagal memilih foto", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        tampilkanDataAwal();
        tampilkanFotoProfil();

        btnBack.setOnClickListener(v -> finish());

        layoutPilihFoto.setOnClickListener(v -> {
            pilihFotoLauncher.launch(new String[]{"image/*"});
        });

        btnSimpanProfil.setOnClickListener(v -> {
            simpanPerubahanProfil();
        });
    }

    private void tampilkanDataAwal() {
        FirebaseUser user = mAuth.getCurrentUser();

        String namaLocal = profilePrefs.getString("profile_name", null);

        if (user != null) {
            String namaFirebase = user.getDisplayName();
            String email = user.getEmail();

            if (namaFirebase != null && !namaFirebase.trim().isEmpty()) {
                edtNamaProfil.setText(namaFirebase);
            } else if (namaLocal != null && !namaLocal.trim().isEmpty()) {
                edtNamaProfil.setText(namaLocal);
            } else {
                edtNamaProfil.setText("User PoultrySense");
            }

            if (email != null && !email.trim().isEmpty()) {
                txtEmailProfil.setText(email);
            } else {
                txtEmailProfil.setText("Email tidak tersedia");
            }
        } else {
            edtNamaProfil.setText("User PoultrySense");
            txtEmailProfil.setText("Email tidak tersedia");
        }

        edtNamaProfil.setSelection(edtNamaProfil.getText().length());
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

    private void simpanPerubahanProfil() {
        String namaBaru = edtNamaProfil.getText().toString().trim();

        if (namaBaru.isEmpty()) {
            Toast.makeText(this, "Nama tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences.Editor editor = profilePrefs.edit();
        editor.putString("profile_name", namaBaru);

        if (selectedImageUri != null) {
            editor.putString("profile_photo_uri", selectedImageUri.toString());
        }

        editor.apply();

        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(namaBaru)
                    .build();

            user.updateProfile(profileUpdates)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(this, "Profil berhasil diperbarui", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Profil tersimpan lokal, tapi gagal update Firebase", Toast.LENGTH_SHORT).show();
                        finish();
                    });
        } else {
            Toast.makeText(this, "Profil berhasil disimpan", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}