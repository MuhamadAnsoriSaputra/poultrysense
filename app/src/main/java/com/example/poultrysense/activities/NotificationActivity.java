package com.example.poultrysense.activities;

import com.example.poultrysense.R;
import com.example.poultrysense.models.NotificationItem;
import com.example.poultrysense.utils.NotifManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {

    private TextView tabSemua, tabPakan, tabPeringatan, tabLaporan;
    private List<TextView> allTabs;
    private String currentTab = "Semua";

    private LinearLayout containerNotifikasi;
    private ImageView btnRefresh;
    private NotifManager notifManager;

    // Selection Components
    private View layoutTopNormal, layoutTopSelection, layoutSelectionBar;
    private TextView txtSelectCount;
    private ImageView btnCancelSelect, btnSelectAllTop, btnHapusTerpilih;
    private boolean selectionMode = false;
    private boolean isSelectAll = false;
    private List<NotificationItem> currentFilteredList = new ArrayList<>();
    private List<NotificationItem> allNotificationsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        notifManager = new NotifManager(this);
        allNotificationsList = notifManager.getNotifications();

        // 1. Inisialisasi View Filter
        tabSemua = findViewById(R.id.tab_semua);
        tabPakan = findViewById(R.id.tab_pakan);
        tabPeringatan = findViewById(R.id.tab_peringatan);
        tabLaporan = findViewById(R.id.tab_laporan);
        containerNotifikasi = findViewById(R.id.container_notifikasi);
        btnRefresh = findViewById(R.id.btn_refresh_notif);

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
            btnHapusTerpilih.setOnClickListener(v -> deleteSelectedNotifications());
        }

        allTabs = new ArrayList<>();
        allTabs.add(tabSemua);
        allTabs.add(tabPakan);
        allTabs.add(tabPeringatan);
        allTabs.add(tabLaporan);

        // 2. Inisialisasi Bottom Navigation
        ImageView navHome = findViewById(R.id.nav_home);
        ImageView navHistory = findViewById(R.id.nav_history);
        ImageView navProfile = findViewById(R.id.nav_profile);
        androidx.cardview.widget.CardView navPakan = findViewById(R.id.nav_pakan);

        // 3. Logika Klik Tab
        tabSemua.setOnClickListener(v -> {
            currentTab = "Semua";
            updateTabStyle(tabSemua);
            exitSelectionMode();
            loadNotifications();
        });

        tabPakan.setOnClickListener(v -> {
            currentTab = "Pakan";
            updateTabStyle(tabPakan);
            exitSelectionMode();
            loadNotifications();
        });

        tabPeringatan.setOnClickListener(v -> {
            currentTab = "Peringatan";
            updateTabStyle(tabPeringatan);
            exitSelectionMode();
            loadNotifications();
        });

        tabLaporan.setOnClickListener(v -> {
            currentTab = "Laporan";
            updateTabStyle(tabLaporan);
            exitSelectionMode();
            loadNotifications();
        });

        if (btnRefresh != null) {
            btnRefresh.setOnClickListener(v -> {
                allNotificationsList = notifManager.getNotifications();
                loadNotifications();
                Toast.makeText(this, "Notifikasi diperbarui", Toast.LENGTH_SHORT).show();
            });
        }

        // 4. Navigasi
        setupBottomNavigation(navHome, findViewById(R.id.nav_notif), navPakan, navHistory, navProfile);
    }

    @Override
    protected void onResume() {
        super.onResume();
        allNotificationsList = notifManager.getNotifications();
        loadNotifications();
    }

    private void loadNotifications() {
        if (containerNotifikasi == null) return;
        containerNotifikasi.removeAllViews();

        currentFilteredList.clear();

        for (NotificationItem item : allNotificationsList) {
            if (!item.isHidden()) {
                if (currentTab.equals("Semua") || item.getKategori().equals(currentTab)) {
                    currentFilteredList.add(item);
                }
            }
        }

        if (currentFilteredList.isEmpty()) {
            TextView txtEmpty = new TextView(this);
            txtEmpty.setText("Belum ada notifikasi");
            txtEmpty.setTextColor(ContextCompat.getColor(this, R.color.text_gray));
            txtEmpty.setGravity(android.view.Gravity.CENTER);
            txtEmpty.setPadding(0, 50, 0, 0);
            containerNotifikasi.addView(txtEmpty);
            return;
        }

        LayoutInflater inflater = LayoutInflater.from(this);
        for (NotificationItem item : currentFilteredList) {
            View view = inflater.inflate(R.layout.item_notification, containerNotifikasi, false);

            ImageView imgIcon = view.findViewById(R.id.img_notif_icon);
            TextView txtJudul = view.findViewById(R.id.txt_notif_judul);
            TextView txtKategori = view.findViewById(R.id.txt_notif_kategori);
            TextView txtPesan = view.findViewById(R.id.txt_notif_pesan);
            TextView txtWaktu = view.findViewById(R.id.txt_notif_waktu);
            ImageView imgSelectNotif = view.findViewById(R.id.img_select_notif);

            txtJudul.setText(item.getJudul());
            txtKategori.setText(item.getKategori());
            txtPesan.setText(item.getPesan());
            txtWaktu.setText(item.getWaktu());

            if (item.getKategori().equals("Peringatan")) {
                imgIcon.setImageResource(R.drawable.ic_nav_notification);
                txtKategori.setTextColor(android.graphics.Color.RED);
            } else if (item.getKategori().equals("Laporan")) {
                imgIcon.setImageResource(R.drawable.ic_nav_history);
                txtKategori.setTextColor(ContextCompat.getColor(this, R.color.teal_primary));
            } else {
                imgIcon.setImageResource(R.drawable.logo_app);
                txtKategori.setTextColor(ContextCompat.getColor(this, R.color.teal_primary));
            }

            if (selectionMode) {
                imgSelectNotif.setVisibility(View.VISIBLE);
                imgSelectNotif.setImageResource(item.isSelected() ? R.drawable.ic_circle_checked : R.drawable.ic_circle_unchecked);
            } else {
                imgSelectNotif.setVisibility(View.GONE);
            }

            view.setOnLongClickListener(v -> {
                if (!selectionMode) {
                    selectionMode = true;
                    item.setSelected(true);
                    updateSelectionUI();
                    loadNotifications();
                }
                return true;
            });

            view.setOnClickListener(v -> {
                if (selectionMode) {
                    item.setSelected(!item.isSelected());
                    updateSelectionUI();
                    loadNotifications();
                }
            });

            containerNotifikasi.addView(view);
        }
    }

    private void updateSelectionUI() {
        int count = 0;
        for (NotificationItem n : currentFilteredList) {
            if (n.isSelected()) count++;
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
        for (NotificationItem n : currentFilteredList) {
            n.setSelected(false);
        }
        selectionMode = false;
        if (layoutTopNormal != null) layoutTopNormal.setVisibility(View.VISIBLE);
        if (layoutTopSelection != null) layoutTopSelection.setVisibility(View.GONE);
        if (layoutSelectionBar != null) layoutSelectionBar.setVisibility(View.GONE);
        loadNotifications();
    }

    private void toggleSelectAll() {
        isSelectAll = !isSelectAll;
        for (NotificationItem n : currentFilteredList) {
            n.setSelected(isSelectAll);
        }
        updateSelectionUI();
        loadNotifications();
    }

    private void deleteSelectedNotifications() {
        List<NotificationItem> toDelete = new ArrayList<>();
        for (NotificationItem n : currentFilteredList) {
            if (n.isSelected()) {
                toDelete.add(n);
            }
        }

        if (toDelete.isEmpty()) return;

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Hapus Notifikasi")
                .setMessage("Hapus " + toDelete.size() + " notifikasi terpilih?")
                .setPositiveButton("Hapus", (dialog, which) -> {
                    for (NotificationItem n : toDelete) {
                        notifManager.hideNotification(n.getId());
                    }
                    Toast.makeText(this, toDelete.size() + " notifikasi dihapus", Toast.LENGTH_SHORT).show();
                    selectionMode = false;
                    if (layoutTopNormal != null) layoutTopNormal.setVisibility(View.VISIBLE);
                    if (layoutTopSelection != null) layoutTopSelection.setVisibility(View.GONE);
                    if (layoutSelectionBar != null) layoutSelectionBar.setVisibility(View.GONE);
                    allNotificationsList = notifManager.getNotifications();
                    loadNotifications();
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    private void setupBottomNavigation(ImageView navHome, ImageView navNotif, androidx.cardview.widget.CardView navPakan, ImageView navHistory, ImageView navProfile) {
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

    private void updateTabStyle(TextView selectedTab) {
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
}