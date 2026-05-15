package com.example.poultrysense.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.poultrysense.R;
import com.example.poultrysense.adapters.HistoryAdapter;
import com.example.poultrysense.models.HistoryPakan;
import com.example.poultrysense.utils.HistoryManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RiwayatActivity extends AppCompatActivity implements HistoryAdapter.OnHistoryClickListener {

    private ImageView navHome, navHistory, navNotif, navProfile;
    private androidx.cardview.widget.CardView navPakan;

    private TextView tabSemua, tabManual, tabOtomatis;
    private List<TextView> allTabs;
    private String currentTab = "Semua";

    // View untuk Ringkasan Mingguan
    private TextView txtTotalPakanMinggu, txtPersenPakanMinggu;
    private final double MAX_STOK_KG = 50.0;

    // History Components
    private RecyclerView rvHistory;
    private HistoryAdapter adapter;
    private HistoryManager historyManager;
    private Spinner spinnerBulan;
    private TextView txtEmpty;
    private List<HistoryPakan> displayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayat);

        historyManager = new HistoryManager(this);

        // UI Binding
        navHome = findViewById(R.id.nav_home);
        navHistory = findViewById(R.id.nav_history);
        navNotif = findViewById(R.id.nav_notif);
        navProfile = findViewById(R.id.nav_profile);
        navPakan = findViewById(R.id.nav_pakan);

        tabSemua = findViewById(R.id.tab_semua);
        tabManual = findViewById(R.id.tab_manual);
        tabOtomatis = findViewById(R.id.tab_otomatis);
        
        txtTotalPakanMinggu = findViewById(R.id.txt_total_pakan_minggu);
        txtPersenPakanMinggu = findViewById(R.id.txt_persen_pakan_minggu);
        
        rvHistory = findViewById(R.id.rv_history);
        spinnerBulan = findViewById(R.id.spinner_bulan);
        txtEmpty = findViewById(R.id.txt_empty_history);

        allTabs = new ArrayList<>();
        allTabs.add(tabSemua);
        allTabs.add(tabManual);
        allTabs.add(tabOtomatis);

        setupRecyclerView();
        setupTabs();
        setupBottomNavigation();
        setupMonthFilter();
        
        // Tambahkan data contoh jika kosong untuk pertama kali
        checkAndAddSampleData();
        
        updateUI();
    }

    private void setupRecyclerView() {
        adapter = new HistoryAdapter(displayList, this);
        rvHistory.setLayoutManager(new LinearLayoutManager(this));
        rvHistory.setAdapter(adapter);
    }

    private void setupMonthFilter() {
        List<String> months = historyManager.getAvailableMonths();
        if (months.isEmpty()) {
            months.add("Tidak ada data");
        }
        
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, months);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBulan.setAdapter(spinnerAdapter);

        spinnerBulan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateUI();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void updateUI() {
        String selectedMonth = spinnerBulan.getSelectedItem() != null ? spinnerBulan.getSelectedItem().toString() : "";
        List<HistoryPakan> filtered = historyManager.getFullHistory();
        
        // Filter by Month
        List<HistoryPakan> monthFiltered = new ArrayList<>();
        for (HistoryPakan h : filtered) {
            if (h.getBulan().equals(selectedMonth) || selectedMonth.equals("Tidak ada data")) {
                monthFiltered.add(h);
            }
        }

        // Filter by Tab
        displayList.clear();
        for (HistoryPakan h : monthFiltered) {
            if (currentTab.equals("Semua") || h.getTipe().equals(currentTab)) {
                displayList.add(h);
            }
        }

        adapter.updateList(displayList);
        txtEmpty.setVisibility(displayList.isEmpty() ? View.VISIBLE : View.GONE);
        
        updateRingkasanMingguan(monthFiltered);
    }

    private void updateRingkasanMingguan(List<HistoryPakan> currentList) {
        double totalTerpakaiGram = 0;
        for (HistoryPakan h : currentList) {
            totalTerpakaiGram += h.getJumlahGram();
        }
        
        double totalTerpakaiKg = totalTerpakaiGram / 1000.0;
        double persentase = (totalTerpakaiKg / MAX_STOK_KG) * 100;

        if (txtTotalPakanMinggu != null) {
            txtTotalPakanMinggu.setText(String.format(Locale.getDefault(), "%.1f", totalTerpakaiKg));
        }

        if (txtPersenPakanMinggu != null) {
            txtPersenPakanMinggu.setText(String.format(Locale.getDefault(), "%.0f%%", persentase));
        }
    }

    private void setupTabs() {
        tabSemua.setOnClickListener(v -> {
            currentTab = "Semua";
            updateTabAppearance(tabSemua);
            updateUI();
        });

        tabManual.setOnClickListener(v -> {
            currentTab = "Manual";
            updateTabAppearance(tabManual);
            updateUI();
        });

        tabOtomatis.setOnClickListener(v -> {
            currentTab = "Otomatis";
            updateTabAppearance(tabOtomatis);
            updateUI();
        });
    }

    @Override
    public void onDeleteClick(HistoryPakan history) {
        new AlertDialog.Builder(this)
                .setTitle("Hapus Riwayat")
                .setMessage("Apakah Anda yakin ingin menghapus riwayat ini?")
                .setPositiveButton("Hapus", (dialog, which) -> {
                    historyManager.deleteHistory(history.getId());
                    Toast.makeText(this, "Riwayat dihapus", Toast.LENGTH_SHORT).show();
                    updateUI();
                    // Update spinner if month is gone
                    setupMonthFilter();
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    private void checkAndAddSampleData() {
        if (historyManager.getFullHistory().isEmpty()) {
            historyManager.addHistory(new HistoryPakan("1", "Manual", 200, "25 Maret 2026 - 09:00 PM", "Maret 2026"));
            historyManager.addHistory(new HistoryPakan("2", "Otomatis", 500, "26 Maret 2026 - 10:00 PM", "Maret 2026"));
            historyManager.addHistory(new HistoryPakan("3", "Manual", 300, "10 Februari 2026 - 08:00 AM", "Februari 2026"));
            setupMonthFilter();
        }
    }

    private void updateTabAppearance(TextView selectedTab) {
        for (TextView tab : allTabs) {
            if (tab == selectedTab) {
                tab.setBackgroundResource(R.drawable.bg_tab_active);
                tab.setTextColor(ContextCompat.getColor(this, R.color.teal_primary));
                tab.setTypeface(null, android.graphics.Typeface.BOLD);
            } else {
                tab.setBackgroundResource(R.drawable.bg_tab_inactive);
                tab.setTextColor(ContextCompat.getColor(this, R.color.text_gray));
                tab.setTypeface(null, android.graphics.Typeface.NORMAL);
            }
        }
    }

    private void setupBottomNavigation() {
        if (navHome != null) navHome.setOnClickListener(v -> navigateTo(DashboardActivity.class));
        if (navNotif != null) navNotif.setOnClickListener(v -> navigateTo(NotificationActivity.class));
        if (navPakan != null) navPakan.setOnClickListener(v -> navigateTo(MenuPakanActivity.class));
        if (navHistory != null) navHistory.setOnClickListener(v -> navigateTo(RiwayatActivity.class));
        if (navProfile != null) navProfile.setOnClickListener(v -> navigateTo(AkunActivity.class));
    }

    private void navigateTo(Class<?> targetActivity) {
        if (this.getClass() == targetActivity) return;
        Intent intent = new Intent(this, targetActivity);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }
}
