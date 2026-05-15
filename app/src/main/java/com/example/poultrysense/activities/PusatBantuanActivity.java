package com.example.poultrysense.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.example.poultrysense.R;

public class PusatBantuanActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pusat_bantuan);

        ImageView btnBack = findViewById(R.id.btn_back_bantuan);
        btnBack.setOnClickListener(v -> finish());

        CardView btnWA = findViewById(R.id.btn_contact_wa);
        CardView btnEmail = findViewById(R.id.btn_contact_email);

        btnWA.setOnClickListener(v -> {
            String url = "https://api.whatsapp.com/send?phone=628123456789"; // Ganti dengan nomor asli
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        });

        btnEmail.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:support@poultrysense.id"));
            intent.putExtra(Intent.EXTRA_SUBJECT, "Bantuan Aplikasi PoultrySense");
            startActivity(intent);
        });
    }
}
