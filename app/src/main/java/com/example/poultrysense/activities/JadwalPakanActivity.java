package com.example.poultrysense.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.poultrysense.R;
import com.example.poultrysense.adapters.JadwalAdapter;
import com.example.poultrysense.models.JadwalPakan;
import com.example.poultrysense.utils.JadwalManager;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;
import java.util.UUID;

public class JadwalPakanActivity extends AppCompatActivity {

    private RecyclerView rvJadwal;
    private JadwalAdapter adapter;
    private JadwalManager manager;
    private FloatingActionButton fabTambah;
    private ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jadwal_pakan);

        manager = new JadwalManager(this);
        rvJadwal = findViewById(R.id.rv_jadwal);
        fabTambah = findViewById(R.id.fab_tambah_jadwal);
        btnBack = findViewById(R.id.btn_back_jadwal);

        setupRecyclerView();

        fabTambah.setOnClickListener(v -> showTambahJadwalSheet());
        btnBack.setOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        List<JadwalPakan> list = manager.getListJadwal();
        adapter = new JadwalAdapter(list, manager);
        rvJadwal.setLayoutManager(new LinearLayoutManager(this));
        rvJadwal.setAdapter(adapter);
    }

    private void showTambahJadwalSheet() {
        BottomSheetDialog sheet = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        View view = getLayoutInflater().inflate(R.layout.layout_tambah_jadwal, null);
        sheet.setContentView(view);

        TimePicker timePicker = view.findViewById(R.id.time_picker);
        EditText etLabel = view.findViewById(R.id.et_label_jadwal);
        Button btnSimpan = view.findViewById(R.id.btn_simpan_jadwal);
        ImageView btnClose = view.findViewById(R.id.btn_close_sheet);

        timePicker.setIs24HourView(true);

        btnClose.setOnClickListener(v -> sheet.dismiss());

        btnSimpan.setOnClickListener(v -> {
            String jam = String.valueOf(timePicker.getHour());
            String menit = String.valueOf(timePicker.getMinute());
            String label = etLabel.getText().toString().trim();

            if (label.isEmpty()) label = "Jadwal Pakan";

            JadwalPakan baru = new JadwalPakan(
                    UUID.randomUUID().toString(),
                    jam,
                    menit,
                    label,
                    true
            );

            manager.saveJadwal(baru);
            adapter.updateData(manager.getListJadwal());
            sheet.dismiss();
            Toast.makeText(this, "Jadwal berhasil ditambahkan", Toast.LENGTH_SHORT).show();
        });

        sheet.show();
    }
}
