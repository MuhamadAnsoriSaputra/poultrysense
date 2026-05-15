package com.example.poultrysense.activities;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.example.poultrysense.R;
import com.example.poultrysense.models.HistoryPakan;
import com.example.poultrysense.utils.HistoryManager;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class BeriPakanActivity extends AppCompatActivity {

    private ImageView btnBack;
    private CardView btnActionPakan;
    private TextView txtStatus;
    private ProgressBar progressBar;
    private boolean isFeeding = false;
    private HistoryManager historyManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beri_pakan);

        historyManager = new HistoryManager(this);

        btnBack = findViewById(R.id.btn_back_pakan);
        btnActionPakan = findViewById(R.id.btn_action_pakan);
        txtStatus = findViewById(R.id.txt_status_feeding);
        progressBar = findViewById(R.id.progress_feeding);

        btnBack.setOnClickListener(v -> finish());

        btnActionPakan.setOnClickListener(v -> {
            if (!isFeeding) {
                startFeeding();
            }
        });
    }

    private void startFeeding() {
        isFeeding = true;
        btnActionPakan.setEnabled(false);
        btnActionPakan.setAlpha(0.7f);
        
        txtStatus.setText("Sedang Mengeluarkan Pakan...");
        txtStatus.setTextColor(android.graphics.Color.parseColor("#F59E0B")); // Orange
        progressBar.setVisibility(View.VISIBLE);

        // Simulasi pengeluaran pakan selama 3 detik
        new Handler().postDelayed(() -> {
            saveToHistory();
            stopFeeding();
        }, 3000);
    }

    private void saveToHistory() {
        SimpleDateFormat sdfWaktu = new SimpleDateFormat("dd MMMM yyyy - hh:mm a", new Locale("id", "ID"));
        SimpleDateFormat sdfBulan = new SimpleDateFormat("MMMM yyyy", new Locale("id", "ID"));
        Date now = new Date();
        
        String id = UUID.randomUUID().toString();
        String waktu = sdfWaktu.format(now);
        String bulan = sdfBulan.format(now);
        
        // Misal porsi manual standar 200g
        HistoryPakan newHistory = new HistoryPakan(id, "Manual", 200, waktu, bulan);
        historyManager.addHistory(newHistory);
    }

    private void stopFeeding() {
        isFeeding = false;
        btnActionPakan.setEnabled(true);
        btnActionPakan.setAlpha(1.0f);
        
        txtStatus.setText("Pakan Berhasil Dikeluarkan!");
        txtStatus.setTextColor(getResources().getColor(R.color.teal_primary));
        progressBar.setVisibility(View.GONE);

        Toast.makeText(this, "Ayam Anda sudah diberi pakan!", Toast.LENGTH_SHORT).show();

        // Reset status setelah 2 detik
        new Handler().postDelayed(() -> {
            if (!isFeeding) txtStatus.setText("Sistem Siap");
        }, 2000);
    }
}
