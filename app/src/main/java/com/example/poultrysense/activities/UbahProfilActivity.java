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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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

        TextView btnHapusPP = findViewById(R.id.btn_hapus_pp);
        if (btnHapusPP != null) {
            btnHapusPP.setOnClickListener(v -> hapusFotoProfil());
        }

        btnSimpanProfil.setOnClickListener(v -> {
            simpanPerubahanProfil();
        });
    }

    private void hapusFotoProfil() {
        FirebaseUser user = mAuth.getCurrentUser();
        String emailKey = (user != null && user.getEmail() != null) ? user.getEmail() : "default";

        selectedImageUri = null;
        imgProfile.setImageResource(R.drawable.profil);

        SharedPreferences.Editor editor = profilePrefs.edit();
        editor.putString("profile_photo_uri_" + emailKey, "");
        editor.apply();

        if (user != null && user.getEmail() != null) {
            com.example.poultrysense.utils.MultiAccountManager mam = new com.example.poultrysense.utils.MultiAccountManager(this);
            java.util.List<com.example.poultrysense.utils.MultiAccountManager.SavedAccount> accs = mam.getSavedAccounts();
            for (com.example.poultrysense.utils.MultiAccountManager.SavedAccount acc : accs) {
                if (acc.email.equals(user.getEmail())) {
                    acc.photoUri = "";
                    mam.saveAccount(acc.email, acc.password, acc.name, acc.photoUri);
                    break;
                }
            }
        }

        com.example.poultrysense.utils.HistoryAkunManager.addHistory(this, emailKey, "ubah_profil", "Foto Profil Dihapus", android.os.Build.MODEL, "Indonesia");
        com.example.poultrysense.utils.NotificationAkunManager.addNotification(this, emailKey, "sistem", "Foto Profil Dihapus", "Foto profil Anda telah dikembalikan ke bawaan.");
        Toast.makeText(this, "Foto profil berhasil dihapus", Toast.LENGTH_SHORT).show();
    }

    private void tampilkanDataAwal() {
        FirebaseUser user = mAuth.getCurrentUser();
        String emailKey = (user != null && user.getEmail() != null) ? user.getEmail() : "default";

        String namaLocal = profilePrefs.getString("profile_name_" + emailKey, null);

        if (user != null) {
            String namaFirebase = user.getDisplayName();
            String email = user.getEmail();

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

            if (namaLocal != null && !namaLocal.trim().isEmpty()) {
                edtNamaProfil.setText(namaLocal);
            } else if (mamName != null && !mamName.trim().isEmpty()) {
                edtNamaProfil.setText(mamName);
            } else if (namaFirebase != null && !namaFirebase.trim().isEmpty()) {
                edtNamaProfil.setText(namaFirebase);
            } else {
                if (email != null && email.contains("@")) {
                    edtNamaProfil.setText(email.substring(0, email.indexOf('@')));
                } else {
                    edtNamaProfil.setText("User");
                }
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

        FirebaseUser user = mAuth.getCurrentUser();
        String emailKey = (user != null && user.getEmail() != null) ? user.getEmail() : "default";

        SharedPreferences.Editor editor = profilePrefs.edit();
        editor.putString("profile_name_" + emailKey, namaBaru);

        if (selectedImageUri != null) {
            editor.putString("profile_photo_uri_" + emailKey, selectedImageUri.toString());
        }

        String lastUpdate = new SimpleDateFormat("dd MMMM yyyy - hh:mm a", Locale.getDefault()).format(new Date());
        editor.putString("last_profile_update_" + emailKey, lastUpdate);

        editor.apply();

        if (user != null && user.getEmail() != null) {
            com.example.poultrysense.utils.MultiAccountManager mam = new com.example.poultrysense.utils.MultiAccountManager(this);
            java.util.List<com.example.poultrysense.utils.MultiAccountManager.SavedAccount> accs = mam.getSavedAccounts();
            for (com.example.poultrysense.utils.MultiAccountManager.SavedAccount acc : accs) {
                if (acc.email.equals(user.getEmail())) {
                    acc.name = namaBaru;
                    if (selectedImageUri != null) {
                        acc.photoUri = selectedImageUri.toString();
                    }
                    mam.saveAccount(acc.email, acc.password, acc.name, acc.photoUri);
                    break;
                }
            }
        }

        com.example.poultrysense.utils.HistoryAkunManager.addHistory(this, emailKey, "ubah_profil", "Profil Diperbarui (" + namaBaru + ")", android.os.Build.MODEL, "Indonesia");
        com.example.poultrysense.utils.NotificationAkunManager.addNotification(this, emailKey, "sistem", "Profil Diperbarui", "Informasi profil Anda berhasil diperbarui menjadi " + namaBaru + ".");

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