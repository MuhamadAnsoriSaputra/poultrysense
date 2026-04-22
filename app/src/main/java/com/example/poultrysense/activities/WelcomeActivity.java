package com.example.poultrysense.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.example.poultrysense.R;
import com.google.firebase.auth.FirebaseAuth; // Tambahkan ini

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // --- CEK STATUS LOGIN DISINI (SEBELUM SETCONTENTVIEW) ---
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            // Jika user sudah login, langsung ke Dashboard
            Intent intent = new Intent(WelcomeActivity.this, DashboardActivity.class);
            startActivity(intent);
            finish(); // Tutup WelcomeActivity agar tidak bisa di-back
            return; // Berhenti agar kode di bawah tidak dijalankan
        }

        setContentView(R.layout.activity_welcome);

        LinearLayout btnContinue = findViewById(R.id.btnContinue);

        btnContinue.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}