package com.example.poultrysense.activities;

import com.example.poultrysense.R;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LihatProfilActivity extends AppCompatActivity {

    private ImageView btnBack, imgProfile;
    private TextView txtNamaProfil, txtEmailProfil;
    private TextView txtInfoNama, txtInfoEmail, txtStatusAkun, txtTanggalDibuat;

    private FirebaseAuth mAuth;
    private SharedPreferences profilePrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lihat_profil);

        mAuth = FirebaseAuth.getInstance();
        profilePrefs = getSharedPreferences("PROFILE_PREFS", MODE_PRIVATE);

        btnBack = findViewById(R.id.btn_back_profil);
        imgProfile = findViewById(R.id.img_detail_profile);

        txtNamaProfil = findViewById(R.id.txt_detail_nama);
        txtEmailProfil = findViewById(R.id.txt_detail_email);
        txtInfoNama = findViewById(R.id.txt_info_nama);
        txtInfoEmail = findViewById(R.id.txt_info_email);
        txtStatusAkun = findViewById(R.id.txt_status_akun);
        txtTanggalDibuat = findViewById(R.id.txt_tanggal_akun);

        tampilkanDataUser();
        tampilkanFotoProfil();

        btnBack.setOnClickListener(v -> finish());
    }

    private void tampilkanDataUser() {
        FirebaseUser user = mAuth.getCurrentUser();
        String emailKey = (user != null && user.getEmail() != null) ? user.getEmail() : "default";

        if (user != null) {
            String namaFirebase = user.getDisplayName();
            String namaLocal = profilePrefs.getString("profile_name_" + emailKey, null);
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

            String nama;
            if (namaLocal != null && !namaLocal.trim().isEmpty()) {
                nama = namaLocal;
            } else if (mamName != null && !mamName.trim().isEmpty()) {
                nama = mamName;
            } else if (namaFirebase != null && !namaFirebase.trim().isEmpty()) {
                nama = namaFirebase;
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
}