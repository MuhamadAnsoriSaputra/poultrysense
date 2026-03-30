package com.example.poultrysense.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.poultrysense.R;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private EditText etEmail, etPassword, etConfirmPassword;
    private Button btnDaftar;
    private TextView tvLogin;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        etEmail = findViewById(R.id.etEmailSignup);
        etPassword = findViewById(R.id.etPasswordSignup);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnDaftar = findViewById(R.id.btnDaftar);
        tvLogin = findViewById(R.id.tvLogin);

        btnDaftar.setOnClickListener(v -> {
            registerUser();
        });

        tvLogin.setOnClickListener(v -> {
            finish();
        });
    }

    private void registerUser() {
        String email = etEmail.getText().toString().trim();
        String pass = etPassword.getText().toString().trim();
        String confirmPass = etConfirmPassword.getText().toString().trim();

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
                Toast.makeText(this, "Berhasil Daftar! Silakan Login", Toast.LENGTH_SHORT).show();
                finish(); // Kembali ke halaman LoginActivity
            } else {
                Toast.makeText(this, "Gagal Daftar: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}