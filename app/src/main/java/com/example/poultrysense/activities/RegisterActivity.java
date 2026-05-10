package com.example.poultrysense.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.poultrysense.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;

public class RegisterActivity extends AppCompatActivity {

    private EditText etNama, etEmail, etPassword, etConfirmPassword;
    private Button btnDaftar;
    private TextView tvLogin;
    private ImageView imgShowPassword, imgShowConfirmPassword;
    private FirebaseAuth mAuth;
    private boolean isPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        etNama = findViewById(R.id.etNama);
        etEmail = findViewById(R.id.etEmailSignup);
        etPassword = findViewById(R.id.etPasswordSignup);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnDaftar = findViewById(R.id.btnDaftar);
        tvLogin = findViewById(R.id.tvLogin);
        imgShowPassword = findViewById(R.id.imgShowPassword);
        imgShowConfirmPassword = findViewById(R.id.imgShowConfirmPassword);

        btnDaftar.setOnClickListener(v -> registerUser());
        tvLogin.setOnClickListener(v -> finish());

        imgShowPassword.setOnClickListener(v -> {
            isPasswordVisible = !isPasswordVisible;
            if (isPasswordVisible) {
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                imgShowPassword.setImageResource(R.drawable.ic_eye);
            } else {
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                imgShowPassword.setImageResource(R.drawable.ic_eye_off);
            }
            etPassword.setSelection(etPassword.getText().length());
        });

        imgShowConfirmPassword.setOnClickListener(v -> {
            isConfirmPasswordVisible = !isConfirmPasswordVisible;
            if (isConfirmPasswordVisible) {
                etConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                imgShowConfirmPassword.setImageResource(R.drawable.ic_eye);
            } else {
                etConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                imgShowConfirmPassword.setImageResource(R.drawable.ic_eye_off);
            }
            etConfirmPassword.setSelection(etConfirmPassword.getText().length());
        });
    }

    private void registerUser() {
        String nama = etNama.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String pass = etPassword.getText().toString().trim();
        String confirmPass = etConfirmPassword.getText().toString().trim();

        if (nama.isEmpty()) {
            etNama.setError("Nama wajib diisi");
            return;
        }
        if (email.isEmpty()) {
            etEmail.setError("Email wajib diisi");
            return;
        }
        if (pass.isEmpty()) {
            etPassword.setError("Password wajib diisi");
            return;
        }
        if (pass.length() < 6) {
            etPassword.setError("Password minimal 6 karakter");
            return;
        }
        if (!pass.equals(confirmPass)) {
            etConfirmPassword.setError("Password tidak cocok");
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Simpan nama ke profil Firebase
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(nama)
                        .build();
                
                if (mAuth.getCurrentUser() != null) {
                    mAuth.getCurrentUser().updateProfile(profileUpdates);
                }

                // Simpan akun ke MultiAccountManager
                com.example.poultrysense.utils.MultiAccountManager mam = new com.example.poultrysense.utils.MultiAccountManager(this);
                mam.saveAccount(email, pass, nama, "");

                // Langsung login & masuk Dashboard
                com.example.poultrysense.utils.SessionManager sessionManager = new com.example.poultrysense.utils.SessionManager(this);
                sessionManager.createLoginSession(email);

                Toast.makeText(this, "Berhasil Daftar! Selamat datang, " + nama + "!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, DashboardActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Gagal Daftar: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}