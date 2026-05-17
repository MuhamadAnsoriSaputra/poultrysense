package com.example.poultrysense.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
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

public class JadwalPakanActivity extends AppCompatActivity implements JadwalAdapter.OnJadwalInteractionListener {

    private RecyclerView rvJadwal;
    private JadwalAdapter adapter;
    private JadwalManager manager;
    private FloatingActionButton fabTambah;

    // Top Bar Components
    private RelativeLayout layoutTopNormal;
    private RelativeLayout layoutTopSelection;
    private ImageView btnBack;
    private ImageView btnCancelSelect;
    private TextView txtSelectCount;
    private ImageView btnSelectAllTop;

    // Bottom Bar Components
    private LinearLayout layoutSelectionBar;
    private ImageView btnHapusTerpilih;
    private boolean isAllSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jadwal_pakan);

        manager = new JadwalManager(this);
        rvJadwal = findViewById(R.id.rv_jadwal);
        fabTambah = findViewById(R.id.fab_tambah_jadwal);

        layoutTopNormal = findViewById(R.id.layout_top_normal);
        layoutTopSelection = findViewById(R.id.layout_top_selection);
        btnBack = findViewById(R.id.btn_back_jadwal);
        btnCancelSelect = findViewById(R.id.btn_cancel_select);
        txtSelectCount = findViewById(R.id.txt_select_count);
        btnSelectAllTop = findViewById(R.id.btn_select_all_top);

        layoutSelectionBar = findViewById(R.id.layout_selection_bar);
        btnHapusTerpilih = findViewById(R.id.btn_hapus_terpilih);

        setupRecyclerView();

        fabTambah.setOnClickListener(v -> showTambahJadwalSheet());
        
        btnBack.setOnClickListener(v -> finish());

        btnCancelSelect.setOnClickListener(v -> {
            if (adapter != null) {
                adapter.setSelectionMode(false);
            }
        });

        btnSelectAllTop.setOnClickListener(v -> {
            if (adapter != null) {
                isAllSelected = !isAllSelected;
                adapter.selectAll(isAllSelected);
            }
        });

        btnHapusTerpilih.setOnClickListener(v -> {
            if (adapter != null) {
                List<JadwalPakan> selected = adapter.getSelectedItems();
                if (selected.isEmpty()) {
                    Toast.makeText(this, "Pilih jadwal yang ingin dihapus", Toast.LENGTH_SHORT).show();
                    return;
                }
                new AlertDialog.Builder(this)
                        .setTitle("Hapus Jadwal Terpilih")
                        .setMessage("Apakah Anda yakin ingin menghapus " + selected.size() + " jadwal pakan?")
                        .setPositiveButton("Hapus", (dialog, which) -> {
                            for (JadwalPakan j : selected) {
                                manager.deleteJadwal(j.getId());
                            }
                            adapter.setSelectionMode(false);
                            adapter.updateData(manager.getListJadwal());
                            Toast.makeText(this, selected.size() + " jadwal berhasil dihapus", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Batal", null)
                        .show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (adapter != null && adapter.isSelectionMode()) {
            adapter.setSelectionMode(false);
        } else {
            super.onBackPressed();
        }
    }

    private void setupRecyclerView() {
        List<JadwalPakan> list = manager.getListJadwal();
        adapter = new JadwalAdapter(list, manager, this);
        rvJadwal.setLayoutManager(new LinearLayoutManager(this));
        rvJadwal.setAdapter(adapter);
    }

    @Override
    public void onJadwalClick(JadwalPakan jadwal) {
        showEditJadwalSheet(jadwal);
    }

    @Override
    public void onSelectionModeChange(boolean isSelectionMode, int selectedCount) {
        if (isSelectionMode) {
            layoutTopNormal.setVisibility(View.GONE);
            layoutTopSelection.setVisibility(View.VISIBLE);
            layoutSelectionBar.setVisibility(View.VISIBLE);
            fabTambah.setVisibility(View.GONE);
            
            txtSelectCount.setText(selectedCount + " item terpilih");
            isAllSelected = (selectedCount == adapter.getItemCount() && selectedCount > 0);
            btnSelectAllTop.setImageResource(isAllSelected ? R.drawable.ic_circle_checked : R.drawable.ic_select_all_lines);
        } else {
            layoutTopNormal.setVisibility(View.VISIBLE);
            layoutTopSelection.setVisibility(View.GONE);
            layoutSelectionBar.setVisibility(View.GONE);
            fabTambah.setVisibility(View.VISIBLE);
            isAllSelected = false;
        }
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

    private void showEditJadwalSheet(JadwalPakan jadwal) {
        BottomSheetDialog sheet = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        View view = getLayoutInflater().inflate(R.layout.layout_tambah_jadwal, null);
        sheet.setContentView(view);

        TimePicker timePicker = view.findViewById(R.id.time_picker);
        EditText etLabel = view.findViewById(R.id.et_label_jadwal);
        Button btnSimpan = view.findViewById(R.id.btn_simpan_jadwal);
        ImageView btnClose = view.findViewById(R.id.btn_close_sheet);

        TextView txtTitle = view.findViewById(R.id.txt_sheet_title);
        if (txtTitle != null) {
            txtTitle.setText("Edit Jadwal Pakan");
        }
        btnSimpan.setText("Selesai");

        timePicker.setIs24HourView(true);
        try {
            timePicker.setHour(Integer.parseInt(jadwal.getJam()));
            timePicker.setMinute(Integer.parseInt(jadwal.getMenit()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        etLabel.setText(jadwal.getLabel());

        btnClose.setOnClickListener(v -> sheet.dismiss());

        btnSimpan.setOnClickListener(v -> {
            String jam = String.valueOf(timePicker.getHour());
            String menit = String.valueOf(timePicker.getMinute());
            String label = etLabel.getText().toString().trim();

            if (label.isEmpty()) label = "Jadwal Pakan";

            jadwal.setJam(jam);
            jadwal.setMenit(menit);
            jadwal.setLabel(label);
            jadwal.setAktif(true);

            manager.saveJadwal(jadwal);
            adapter.updateData(manager.getListJadwal());
            sheet.dismiss();
            Toast.makeText(this, "Jadwal berhasil diperbarui", Toast.LENGTH_SHORT).show();
        });

        sheet.show();
    }
}
