package com.example.poultrysense.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.google.android.material.bottomsheet.BottomSheetDialog;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class RiwayatActivity extends AppCompatActivity implements HistoryAdapter.OnHistoryClickListener {

    private ImageView navHome, navHistory, navNotif, navProfile;
    private androidx.cardview.widget.CardView navPakan;

    private TextView tabSemua, tabManual, tabOtomatis;
    private List<TextView> allTabs;
    private String currentTab = "Semua";

    // Filter Waktu Kreatif
    private LinearLayout containerFilterWaktu;
    private TextView txtFilterWaktu;
    private String currentFilterMode = "ALL"; // ALL, DATE, MONTH
    private String currentFilterValue = "";

    // View untuk Ringkasan
    private TextView txtTotalPakanMinggu;

    // History Components
    private RecyclerView rvHistory;
    private HistoryAdapter adapter;
    private HistoryManager historyManager;
    private TextView txtEmpty;
    private List<HistoryPakan> displayList = new ArrayList<>();

    // Selection Components
    private View layoutTopNormal, layoutTopSelection, layoutSelectionBar;
    private TextView txtSelectCount;
    private ImageView btnCancelSelect, btnSelectAllTop, btnHapusTerpilih;
    private boolean isSelectAll = false;

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
        
        rvHistory = findViewById(R.id.rv_history);
        containerFilterWaktu = findViewById(R.id.container_filter_waktu);
        txtFilterWaktu = findViewById(R.id.txt_filter_waktu);
        txtEmpty = findViewById(R.id.txt_empty_history);

        layoutTopNormal = findViewById(R.id.layout_top_normal);
        layoutTopSelection = findViewById(R.id.layout_top_selection);
        layoutSelectionBar = findViewById(R.id.layout_selection_bar);
        txtSelectCount = findViewById(R.id.txt_select_count);
        btnCancelSelect = findViewById(R.id.btn_cancel_select);
        btnSelectAllTop = findViewById(R.id.btn_select_all_top);
        btnHapusTerpilih = findViewById(R.id.btn_hapus_terpilih);

        if (btnCancelSelect != null) {
            btnCancelSelect.setOnClickListener(v -> exitSelectionMode());
        }
        if (btnSelectAllTop != null) {
            btnSelectAllTop.setOnClickListener(v -> toggleSelectAll());
        }
        if (btnHapusTerpilih != null) {
            btnHapusTerpilih.setOnClickListener(v -> deleteSelectedHistories());
        }

        allTabs = new ArrayList<>();
        allTabs.add(tabSemua);
        allTabs.add(tabManual);
        allTabs.add(tabOtomatis);

        setupRecyclerView();
        setupTabs();
        setupBottomNavigation();
        setupFilterWaktu();
        
        ImageView btnRefresh = findViewById(R.id.btn_refresh_riwayat);
        if (btnRefresh != null) {
            btnRefresh.setOnClickListener(v -> {
                updateUI();
                Toast.makeText(this, "Riwayat diperbarui", Toast.LENGTH_SHORT).show();
            });
        }

        updateUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    private void setupRecyclerView() {
        adapter = new HistoryAdapter(displayList, this);
        rvHistory.setLayoutManager(new LinearLayoutManager(this));
        rvHistory.setAdapter(adapter);
    }

    private void setupFilterWaktu() {
        if (containerFilterWaktu != null) {
            containerFilterWaktu.setOnClickListener(v -> showFilterWaktuDialog());
        }
    }

    private void showFilterWaktuDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_filter_waktu, null);
        bottomSheetDialog.setContentView(view);

        LinearLayout btnPilihTanggal = view.findViewById(R.id.btn_pilih_tanggal);
        LinearLayout btnPilihBulan = view.findViewById(R.id.btn_pilih_bulan);
        LinearLayout btnSemuaWaktu = view.findViewById(R.id.btn_semua_waktu);

        btnPilihTanggal.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(this, (dateView, year, month, dayOfMonth) -> {
                Calendar selected = Calendar.getInstance();
                selected.set(year, month, dayOfMonth);
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", new Locale("id", "ID"));
                currentFilterValue = sdf.format(selected.getTime());
                currentFilterMode = "DATE";
                txtFilterWaktu.setText("Tanggal: " + currentFilterValue);
                updateUI();
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
        });

        btnPilihBulan.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            List<String> months = new ArrayList<>();
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdfMonth = new SimpleDateFormat("MMMM yyyy", new Locale("id", "ID"));
            for (int i = 0; i < 12; i++) {
                months.add(sdfMonth.format(cal.getTime()));
                cal.add(Calendar.MONTH, -1);
            }
            for (String hm : historyManager.getAvailableMonths()) {
                if (!months.contains(hm)) {
                    months.add(hm);
                }
            }
            String[] monthArray = months.toArray(new String[0]);
            new AlertDialog.Builder(this)
                    .setTitle("Pilih Bulan & Tahun")
                    .setItems(monthArray, (dialogMonth, whichMonth) -> {
                        currentFilterValue = monthArray[whichMonth];
                        currentFilterMode = "MONTH";
                        txtFilterWaktu.setText("Bulan: " + currentFilterValue);
                        updateUI();
                    }).show();
        });

        btnSemuaWaktu.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            currentFilterMode = "ALL";
            currentFilterValue = "";
            txtFilterWaktu.setText("Semua Waktu");
            updateUI();
        });

        bottomSheetDialog.show();
    }

    private void updateUI() {
        List<HistoryPakan> fullList = historyManager.getFullHistory();
        
        // 1. Filter by Time Mode (ALL, DATE, MONTH)
        List<HistoryPakan> timeFiltered = new ArrayList<>();
        for (HistoryPakan h : fullList) {
            if (currentFilterMode.equals("ALL")) {
                timeFiltered.add(h);
            } else if (currentFilterMode.equals("DATE")) {
                if (h.getWaktu().startsWith(currentFilterValue) || h.getWaktu().contains(currentFilterValue)) {
                    timeFiltered.add(h);
                }
            } else if (currentFilterMode.equals("MONTH")) {
                if (h.getBulan().equals(currentFilterValue)) {
                    timeFiltered.add(h);
                }
            }
        }

        // 2. Filter by Tab (Semua, Manual, Otomatis)
        displayList.clear();
        for (HistoryPakan h : timeFiltered) {
            if (!h.isHidden()) {
                if (currentTab.equals("Semua") || h.getTipe().equals(currentTab)) {
                    displayList.add(h);
                }
            }
        }

        adapter.updateList(displayList);
        if (txtEmpty != null) {
            txtEmpty.setVisibility(displayList.isEmpty() ? View.VISIBLE : View.GONE);
        }
        
        // Perbarui ringkasan total pakan berdasarkan data yang difilter oleh waktu (termasuk yang disembunyikan)
        updateRingkasanMingguan(timeFiltered);
    }

    private void updateRingkasanMingguan(List<HistoryPakan> currentFilteredList) {
        double totalTerpakaiGram = 0;
        for (HistoryPakan h : currentFilteredList) {
            totalTerpakaiGram += h.getJumlahGram();
        }
        
        double totalTerpakaiKg = totalTerpakaiGram / 1000.0;

        if (txtTotalPakanMinggu != null) {
            txtTotalPakanMinggu.setText(String.format(Locale.getDefault(), "%.2f", totalTerpakaiKg));
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
    public void onLongClick(HistoryPakan history, int position) {
        if (!adapter.isSelectionMode()) {
            adapter.setSelectionMode(true);
            history.setSelected(true);
            updateSelectionUI();
        }
    }

    @Override
    public void onItemClick(HistoryPakan history, int position) {
        if (adapter.isSelectionMode()) {
            history.setSelected(!history.isSelected());
            adapter.notifyItemChanged(position);
            updateSelectionUI();
        }
    }

    private void updateSelectionUI() {
        int count = 0;
        for (HistoryPakan h : displayList) {
            if (h.isSelected()) count++;
        }

        if (count == 0) {
            exitSelectionMode();
            return;
        }

        if (layoutTopNormal != null) layoutTopNormal.setVisibility(View.GONE);
        if (layoutTopSelection != null) layoutTopSelection.setVisibility(View.VISIBLE);
        if (layoutSelectionBar != null) layoutSelectionBar.setVisibility(View.VISIBLE);

        if (txtSelectCount != null) {
            txtSelectCount.setText(count + " item terpilih");
        }
    }

    private void exitSelectionMode() {
        for (HistoryPakan h : displayList) {
            h.setSelected(false);
        }
        adapter.setSelectionMode(false);
        if (layoutTopNormal != null) layoutTopNormal.setVisibility(View.VISIBLE);
        if (layoutTopSelection != null) layoutTopSelection.setVisibility(View.GONE);
        if (layoutSelectionBar != null) layoutSelectionBar.setVisibility(View.GONE);
    }

    private void toggleSelectAll() {
        isSelectAll = !isSelectAll;
        for (HistoryPakan h : displayList) {
            h.setSelected(isSelectAll);
        }
        adapter.notifyDataSetChanged();
        updateSelectionUI();
    }

    private void deleteSelectedHistories() {
        List<HistoryPakan> toDelete = new ArrayList<>();
        for (HistoryPakan h : displayList) {
            if (h.isSelected()) {
                toDelete.add(h);
            }
        }

        if (toDelete.isEmpty()) return;

        new AlertDialog.Builder(this)
                .setTitle("Hapus Riwayat")
                .setMessage("Hapus " + toDelete.size() + " riwayat terpilih?")
                .setPositiveButton("Hapus", (dialog, which) -> {
                    for (HistoryPakan h : toDelete) {
                        historyManager.hideHistory(h.getId());
                    }
                    Toast.makeText(this, toDelete.size() + " riwayat dihapus", Toast.LENGTH_SHORT).show();
                    exitSelectionMode();
                    updateUI();
                })
                .setNegativeButton("Batal", null)
                .show();
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
