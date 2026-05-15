package com.example.poultrysense.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.poultrysense.R;
import com.google.firebase.auth.FirebaseAuth;

public class LupaPasswordActivity extends AppCompatActivity {

    private EditText etEmail;
    private Button btnKirim;
    private ImageView btnBack;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lupa_password);

        mAuth = FirebaseAuth.getInstance();

        etEmail = findViewById(R.id.etEmailLupa);
        btnKirim = findViewById(R.id.btnKirimEmail);
        btnBack = findViewById(R.id.btn_back_lupa);

        btnBack.setOnClickListener(v -> finish());

        btnKirim.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();

            if (email.isEmpty()) {
                etEmail.setError("Email tidak boleh kosong");
                return;
            }

            // Firebase built-in password reset
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(LupaPasswordActivity.this, 
                                    "Instruksi pengaturan ulang sandi telah dikirim ke email Anda", 
                                    Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            Toast.makeText(LupaPasswordActivity.this, 
                                    "Gagal mengirim email: " + task.getException().getMessage(), 
                                    Toast.LENGTH_LONG).show();
                        }
                    });
        });
    }
}
