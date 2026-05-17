package com.example.poultrysense.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.net.Uri;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.poultrysense.R;
import com.example.poultrysense.utils.SessionManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DashboardActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private SessionManager sessionManager;

    private ImageView imgProfile;
    private LinearLayout menuRiwayat, menuNotifikasi;
    private ImageView navHome, navNotif, navHistory, navProfile;
    private androidx.cardview.widget.CardView navPakan;
    private LinearLayout menuJadwal, menuPakan;
    private android.widget.TextView txtJadwalAktif;

    private android.widget.TextView txtKonsumsiGram, txtSisaGram, txtStatusPakanMonitor, txtHariIni;
    private android.widget.ProgressBar progressRingPakan;
    private android.widget.TextView txtPersentasePakan;
    private final int MAX_CAPACITY_GRAM = 5000; // Kapasitas Wadah 5kg
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // 1. Inisialisasi Firebase & Session
        mAuth = FirebaseAuth.getInstance();
        sessionManager = new SessionManager(this);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        imgProfile = findViewById(R.id.img_profile);
        menuRiwayat = findViewById(R.id.menu_riwayat);
        menuNotifikasi = findViewById(R.id.menu_notifikasi);

        navHome = findViewById(R.id.nav_home);
        navNotif = findViewById(R.id.nav_notif);
        navHistory = findViewById(R.id.nav_history);
        navProfile = findViewById(R.id.nav_profile);
        navPakan = findViewById(R.id.nav_pakan);
        menuJadwal = findViewById(R.id.menu_jadwal);
        menuPakan = findViewById(R.id.menu_pakan);
        txtJadwalAktif = findViewById(R.id.txt_jadwal_aktif);

        // Inisialisasi UI Monitoring
        txtKonsumsiGram = findViewById(R.id.txt_konsumsi_gram);
        txtSisaGram = findViewById(R.id.txt_sisa_gram);
        txtStatusPakanMonitor = findViewById(R.id.txt_status_pakan_monitor);
        txtHariIni = findViewById(R.id.txt_hari_ini);
        progressRingPakan = findViewById(R.id.progress_ring_pakan);
        txtPersentasePakan = findViewById(R.id.txt_persentase_pakan);

        if (menuNotifikasi != null)
            menuNotifikasi.setOnClickListener(v -> navigateTo(NotificationActivity.class));
        if (menuRiwayat != null)
            menuRiwayat.setOnClickListener(v -> navigateTo(RiwayatActivity.class));
        if (menuJadwal != null)
            menuJadwal.setOnClickListener(v -> navigateTo(JadwalPakanActivity.class));
        if (menuPakan != null)
            menuPakan.setOnClickListener(v -> navigateTo(BeriPakanActivity.class));

        LinearLayout btnProfileSwitcher = findViewById(R.id.btn_profile_switcher);
        if (btnProfileSwitcher != null)
            btnProfileSwitcher.setOnClickListener(v -> showProfileMenu(btnProfileSwitcher));
        else if (imgProfile != null)
            imgProfile.setOnClickListener(v -> showProfileMenu(imgProfile));

        setupBottomNavigation();

        // 2. Mendengarkan perubahan sisa_pakan dari Firebase Realtime Database
        mDatabase.child("sisa_pakan").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Integer sisa = dataSnapshot.getValue(Integer.class);
                    if (sisa != null) {
                        updateMonitoringDashboard(sisa);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Silently ignore or log
            }
        });
    }

    /**
     * Logika Monitoring Pakan (IoT Ready)
     * @param percentageRemaining Persentase sisa pakan dari sensor ultrasonik (0-100)
     */
    private void updateMonitoringDashboard(double percentageRemaining) {
        // 1. Logika Estimasi Sisa Pakan (Gram & Persen) dari Ketinggian Sensor Ultrasonik
        // Sesuai rumus: (Persen / 100) * Kapasitas Maksimal (5kg / 5000g)
        double currentGrams = (percentageRemaining / 100.0) * MAX_CAPACITY_GRAM;
        
        // 2. Logika Konsumsi Harian Perkiraan (Berdasarkan Frekuensi Beri Pakan Hari Ini x 200 Gram)
        com.example.poultrysense.utils.HistoryManager hm = new com.example.poultrysense.utils.HistoryManager(this);
        java.util.List<com.example.poultrysense.models.HistoryPakan> fullHistory = hm.getFullHistory();
        int countPakanHariIni = 0;
        String todayIndoStr = new java.text.SimpleDateFormat("dd MMMM yyyy", new java.util.Locale("id", "ID")).format(new java.util.Date());

        for (com.example.poultrysense.models.HistoryPakan h : fullHistory) {
            if (h.getWaktu().startsWith(todayIndoStr)) {
                countPakanHariIni++;
            }
        }

        // Asumsi 1 kali beri pakan = 200 gram
        double consumptionGrams = countPakanHariIni * 200.0;

        // 3. Logika Asli (Selisih Pakan Berkurang) untuk Notifikasi & Laporan Harian Kemarin
        android.content.SharedPreferences dailyPrefs = getSharedPreferences("DAILY_CONSUMPTION_PREFS", MODE_PRIVATE);
        String todayStr = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(new java.util.Date());
        String savedDate = dailyPrefs.getString("recorded_date", "");
        
        float initialPercentage;
        if (!savedDate.equals(todayStr)) {
            // Hari baru! Ambil data awal kemarin untuk dihitung konsumsi kemarin
            float oldInitialPercentage = dailyPrefs.getFloat("initial_percentage", 100f);
            if (!savedDate.isEmpty()) {
                double konsumsiKemarinGram = ((oldInitialPercentage - percentageRemaining) / 100.0) * MAX_CAPACITY_GRAM;
                if (konsumsiKemarinGram < 0) konsumsiKemarinGram = 0;

                com.example.poultrysense.utils.NotifManager nm = new com.example.poultrysense.utils.NotifManager(this);
                String judulNotif = "Laporan Konsumsi Pakan Harian";
                String pesanNotif = "Total konsumsi pakan ayam Anda pada tanggal " + savedDate + " diperkirakan sebanyak " + Math.round(konsumsiKemarinGram) + "g (berkurang sekitar " + Math.round(oldInitialPercentage - percentageRemaining) + "% dari wadah).";
                
                nm.addNotification(new com.example.poultrysense.models.NotificationItem(
                        java.util.UUID.randomUUID().toString(),
                        "Laporan",
                        judulNotif,
                        pesanNotif,
                        new java.text.SimpleDateFormat("dd MMMM yyyy - hh:mm a", new java.util.Locale("id", "ID")).format(new java.util.Date())
                ));
                nm.showSystemNotification(this, judulNotif, pesanNotif);
            }

            // Simpan persentase saat ini sebagai persentase awal hari ini
            initialPercentage = (float) percentageRemaining;
            dailyPrefs.edit()
                .putString("recorded_date", todayStr)
                .putFloat("initial_percentage", initialPercentage)
                .apply();
        } else {
            // Hari yang sama, ambil persentase awal yang sudah disimpan tadi pagi
            initialPercentage = dailyPrefs.getFloat("initial_percentage", 100f);
            if (percentageRemaining > initialPercentage) {
                initialPercentage = (float) percentageRemaining;
                dailyPrefs.edit().putFloat("initial_percentage", initialPercentage).apply();
            }
        }

        double initialGrams = (initialPercentage / 100.0) * MAX_CAPACITY_GRAM;
        double pakanBerkurangGram = initialGrams - currentGrams;
        if (pakanBerkurangGram < 0) pakanBerkurangGram = 0;

        // Cek 1: Notifikasi ketika pakan berkurang mencapai 1000 gram hari ini
        boolean notifDecreaseSent = dailyPrefs.getBoolean("notif_decrease_sent_" + todayStr, false);
        if (!notifDecreaseSent && pakanBerkurangGram >= 1000) {
            com.example.poultrysense.utils.NotifManager nm = new com.example.poultrysense.utils.NotifManager(this);
            String judulNotif = "Informasi Pakan Berkurang";
            String pesanNotif = "Pakan di wadah telah berkurang sekitar " + Math.round(pakanBerkurangGram) + "g hari ini. Sisa pakan saat ini diperkirakan " + Math.round(currentGrams) + "g (" + Math.round(percentageRemaining) + "%).";
            
            nm.addNotification(new com.example.poultrysense.models.NotificationItem(
                    java.util.UUID.randomUUID().toString(),
                    "Peringatan",
                    judulNotif,
                    pesanNotif,
                    new java.text.SimpleDateFormat("dd MMMM yyyy - hh:mm a", new java.util.Locale("id", "ID")).format(new java.util.Date())
            ));
            nm.showSystemNotification(this, judulNotif, pesanNotif);
            dailyPrefs.edit().putBoolean("notif_decrease_sent_" + todayStr, true).apply();
        }

        // Cek 2: Notifikasi ketika sisa pakan menyentuh level kritis <= 20%
        boolean notifLowSent = dailyPrefs.getBoolean("notif_low_sent_" + todayStr, false);
        if (!notifLowSent && percentageRemaining <= 20) {
            com.example.poultrysense.utils.NotifManager nm = new com.example.poultrysense.utils.NotifManager(this);
            String judulNotif = "Peringatan Stok Pakan Menipis!";
            String pesanNotif = "Sisa pakan di wadah diperkirakan tinggal " + Math.round(currentGrams) + "g (" + Math.round(percentageRemaining) + "%). Segera lakukan pengisian ulang stok pakan!";
            
            nm.addNotification(new com.example.poultrysense.models.NotificationItem(
                    java.util.UUID.randomUUID().toString(),
                    "Peringatan",
                    judulNotif,
                    pesanNotif,
                    new java.text.SimpleDateFormat("dd MMMM yyyy - hh:mm a", new java.util.Locale("id", "ID")).format(new java.util.Date())
            ));
            nm.showSystemNotification(this, judulNotif, pesanNotif);
            dailyPrefs.edit().putBoolean("notif_low_sent_" + todayStr, true).apply();
        }

        // 4. Update UI Dashboard
        int persentaseInt = (int) Math.round(percentageRemaining);
        if (progressRingPakan != null) {
            progressRingPakan.setProgress(persentaseInt);
        }
        if (txtPersentasePakan != null) {
            txtPersentasePakan.setText(persentaseInt + "%");
        }
        if (txtKonsumsiGram != null) {
            txtKonsumsiGram.setText(String.format("%,.0f g", consumptionGrams).replace(",", "."));
        }
        if (txtSisaGram != null) {
            txtSisaGram.setText(String.format("%,.0f g", currentGrams).replace(",", "."));
        }

        // 5. Update Status Warna
        if (txtStatusPakanMonitor != null) {
            if (percentageRemaining > 40) {
                txtStatusPakanMonitor.setText("Cukup");
                txtStatusPakanMonitor.setTextColor(getResources().getColor(R.color.teal_primary));
            } else if (percentageRemaining > 15) {
                txtStatusPakanMonitor.setText("Menipis");
                txtStatusPakanMonitor.setTextColor(android.graphics.Color.parseColor("#F59E0B")); // Warna Orange/Amber
            } else {
                txtStatusPakanMonitor.setText("Hampir Habis");
                txtStatusPakanMonitor.setTextColor(android.graphics.Color.RED);
            }
        }

        // 6. Update Tanggal Hari Ini
        if (txtHariIni != null) {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("EEEE, d MMMM", new java.util.Locale("id", "ID"));
            txtHariIni.setText(sdf.format(new java.util.Date()));
        }
    }

    private void setupBottomNavigation() {
        if (navHome != null)
            navHome.setOnClickListener(v -> navigateTo(DashboardActivity.class));
        if (navNotif != null)
            navNotif.setOnClickListener(v -> navigateTo(NotificationActivity.class));
        if (navPakan != null)
            navPakan.setOnClickListener(v -> navigateTo(MenuPakanActivity.class));
        if (navHistory != null)
            navHistory.setOnClickListener(v -> navigateTo(RiwayatActivity.class));
        if (navProfile != null)
            navProfile.setOnClickListener(v -> navigateTo(AkunActivity.class));
    }

    private void navigateTo(Class<?> targetActivity) {
        if (this.getClass() == targetActivity)
            return;
        Intent intent = new Intent(this, targetActivity);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        tampilkanDataProfil();
        tampilkanJadwalAktif();
        tampilkanGrafikMingguan();
    }

    private void tampilkanGrafikMingguan() {
        LinearLayout container = findViewById(R.id.chart_mingguan_container);
        if (container == null) return;
        container.removeAllViews();

        com.example.poultrysense.utils.HistoryManager hm = new com.example.poultrysense.utils.HistoryManager(this);
        java.util.List<com.example.poultrysense.models.HistoryPakan> historyList = hm.getFullHistory();

        // Siapkan kalender untuk 7 hari dalam minggu ini (Senin - Minggu)
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(java.util.Calendar.DAY_OF_WEEK, java.util.Calendar.MONDAY);
        
        java.text.SimpleDateFormat sdfParse = new java.text.SimpleDateFormat("dd MMMM yyyy", new java.util.Locale("id", "ID"));
        java.text.SimpleDateFormat sdfCompare = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());

        String[] hariLabels = {"Sen", "Sel", "Rab", "Kam", "Jum", "Sab", "Min"};
        int[] frekuensi = new int[7];

        // Hitung tanggal yyyy-MM-dd untuk Senin-Minggu minggu ini
        String[] targetDates = new String[7];
        for (int i = 0; i < 7; i++) {
            targetDates[i] = sdfCompare.format(cal.getTime());
            cal.add(java.util.Calendar.DATE, 1);
        }

        // Cocokkan riwayat pakan (1 kali gerak servo = 1 kali tercatat di history)
        for (com.example.poultrysense.models.HistoryPakan h : historyList) {
            try {
                String waktuStr = h.getWaktu();
                if (waktuStr.contains("-")) {
                    waktuStr = waktuStr.substring(0, waktuStr.indexOf('-')).trim();
                }
                java.util.Date d = sdfParse.parse(waktuStr);
                if (d != null) {
                    String historyDate = sdfCompare.format(d);
                    for (int i = 0; i < 7; i++) {
                        if (targetDates[i].equals(historyDate)) {
                            frekuensi[i]++;
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                // Ignore parse error
            }
        }

        // Cari nilai maksimum untuk skala tinggi bar (minimal 5 agar bar tidak terlalu tinggi jika frekuensi cuma 1)
        int maxFreq = 5;
        for (int f : frekuensi) {
            if (f > maxFreq) maxFreq = f;
        }

        // Buat UI Bar secara dinamis
        for (int i = 0; i < 7; i++) {
            int count = frekuensi[i];

            LinearLayout col = new LinearLayout(this);
            col.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f));
            col.setOrientation(LinearLayout.VERTICAL);
            col.setGravity(android.view.Gravity.BOTTOM | android.view.Gravity.CENTER_HORIZONTAL);

            // TextView Jumlah
            android.widget.TextView txtCount = new android.widget.TextView(this);
            txtCount.setText(count + "x");
            txtCount.setTextSize(12);
            txtCount.setGravity(android.view.Gravity.CENTER_HORIZONTAL);
            txtCount.setTypeface(null, count > 0 ? android.graphics.Typeface.BOLD : android.graphics.Typeface.NORMAL);
            txtCount.setTextColor(count > 0 ? getResources().getColor(R.color.text_black) : getResources().getColor(R.color.text_gray));
            col.addView(txtCount);

            // Bar View
            android.view.View bar = new android.view.View(this);
            int heightDp = count > 0 ? (int) ((count / (float) maxFreq) * 90) : 4; // 4dp untuk batas bawah jika 0
            int heightPx = (int) android.util.TypedValue.applyDimension(android.util.TypedValue.COMPLEX_UNIT_DIP, heightDp, getResources().getDisplayMetrics());
            int widthPx = (int) android.util.TypedValue.applyDimension(android.util.TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics());

            LinearLayout.LayoutParams barParams = new LinearLayout.LayoutParams(widthPx, heightPx);
            barParams.setMargins(0, 8, 0, 8);
            bar.setLayoutParams(barParams);

            android.graphics.drawable.GradientDrawable barBg = new android.graphics.drawable.GradientDrawable();
            barBg.setColor(count > 0 ? getResources().getColor(R.color.teal_primary) : android.graphics.Color.parseColor("#E0E0E0"));
            barBg.setCornerRadii(new float[]{16f, 16f, 16f, 16f, 0f, 0f, 0f, 0f});
            bar.setBackground(barBg);
            col.addView(bar);

            // TextView Hari Label
            android.widget.TextView txtHari = new android.widget.TextView(this);
            txtHari.setText(hariLabels[i]);
            txtHari.setTextSize(12);
            txtHari.setGravity(android.view.Gravity.CENTER_HORIZONTAL);
            txtHari.setTextColor(getResources().getColor(R.color.text_gray));
            col.addView(txtHari);

            container.addView(col);
        }
    }

    private void tampilkanJadwalAktif() {
        com.example.poultrysense.utils.JadwalManager jm = new com.example.poultrysense.utils.JadwalManager(this);
        java.util.List<com.example.poultrysense.models.JadwalPakan> list = jm.getListJadwal();
        
        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (com.example.poultrysense.models.JadwalPakan j : list) {
            if (j.isAktif()) {
                if (count > 0) sb.append("\n");
                sb.append(j.getWaktuFormatted()).append(" - ").append(j.getLabel());
                count++;
            }
        }

        if (txtJadwalAktif != null) {
            if (count > 0) {
                txtJadwalAktif.setText(sb.toString());
            } else {
                txtJadwalAktif.setText("Belum ada jadwal aktif");
            }
        }
    }

    private void tampilkanDataProfil() {
        android.content.SharedPreferences profilePrefs = getSharedPreferences("PROFILE_PREFS", MODE_PRIVATE);
        FirebaseUser user = mAuth.getCurrentUser();
        String emailKey = (user != null && user.getEmail() != null) ? user.getEmail() : "default";

        String namaFirebase = (user != null) ? user.getDisplayName() : null;
        String namaLocal = profilePrefs.getString("profile_name_" + emailKey, null);
        String email = (user != null) ? user.getEmail() : null;

        String emailPrefix = "User";
        if (email != null && email.contains("@")) {
            emailPrefix = email.substring(0, email.indexOf('@'));
        }

        String mamName = null;
        String mamPhoto = null;
        if (email != null) {
            com.example.poultrysense.utils.MultiAccountManager mam = new com.example.poultrysense.utils.MultiAccountManager(this);
            for (com.example.poultrysense.utils.MultiAccountManager.SavedAccount acc : mam.getSavedAccounts()) {
                if (acc.email.equals(email)) {
                    mamName = acc.name;
                    mamPhoto = acc.photoUri;
                    break;
                }
            }
        }

        String nama;
        if (namaLocal != null && !namaLocal.trim().isEmpty()) {
            nama = namaLocal;
        } else if (mamName != null && !mamName.trim().isEmpty()) {
            nama = mamName;
        } else if (namaFirebase != null && !namaFirebase.trim().isEmpty()) {
            nama = namaFirebase;
        } else {
            nama = emailPrefix;
        }

        android.widget.TextView txtHiUser = findViewById(R.id.txt_hi_user);
        if (txtHiUser != null) {
            txtHiUser.setText("Hi " + nama + ",");
        }

        String savedUri = profilePrefs.getString("profile_photo_uri_" + emailKey, null);
        if (savedUri == null || savedUri.isEmpty()) {
            savedUri = mamPhoto;
        }

        if (savedUri != null && !savedUri.isEmpty() && imgProfile != null) {
            try {
                imgProfile.setImageURI(android.net.Uri.parse(savedUri));
            } catch (Exception e) {
                imgProfile.setImageResource(R.drawable.profil);
            }
        } else if (imgProfile != null) {
            imgProfile.setImageResource(R.drawable.profil);
        }

        if (user != null && user.getEmail() != null) {
            com.example.poultrysense.utils.MultiAccountManager mam = new com.example.poultrysense.utils.MultiAccountManager(this);
            java.util.List<com.example.poultrysense.utils.MultiAccountManager.SavedAccount> accs = mam.getSavedAccounts();

            boolean found = false;
            for (com.example.poultrysense.utils.MultiAccountManager.SavedAccount acc : accs) {
                if (acc.email.equals(user.getEmail())) {
                    acc.name = nama;
                    acc.photoUri = savedUri != null ? savedUri : "";
                    mam.saveAccount(acc.email, acc.password, acc.name, acc.photoUri);
                    found = true;
                    break;
                }
            }

            if (!found) {
                mam.saveAccount(user.getEmail(), "", nama, savedUri != null ? savedUri : "");
            }
        }
    }

    // --- FUNGSI MENU PROFIL & AUTH ---

    private void showProfileMenu(android.view.View anchor) {
        com.google.android.material.bottomsheet.BottomSheetDialog bottomSheetDialog = new com.google.android.material.bottomsheet.BottomSheetDialog(
                this);
        android.view.View view = getLayoutInflater().inflate(R.layout.layout_bottom_sheet_accounts, null);
        bottomSheetDialog.setContentView(view);

        LinearLayout containerAccounts = view.findViewById(R.id.container_accounts);
        LinearLayout btnTambahAkun = view.findViewById(R.id.btn_tambah_akun);

        com.example.poultrysense.utils.MultiAccountManager mam = new com.example.poultrysense.utils.MultiAccountManager(
                this);
        java.util.List<com.example.poultrysense.utils.MultiAccountManager.SavedAccount> accounts = mam
                .getSavedAccounts();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        for (com.example.poultrysense.utils.MultiAccountManager.SavedAccount acc : accounts) {
            android.view.View itemAccount = getLayoutInflater().inflate(R.layout.item_account_switch, null);
            android.widget.TextView txtName = itemAccount.findViewById(R.id.txt_account_name);
            android.widget.TextView txtEmail = itemAccount.findViewById(R.id.txt_account_email);
            ImageView imgAccProfile = itemAccount.findViewById(R.id.img_account_profile);
            ImageView imgActive = itemAccount.findViewById(R.id.img_active_indicator);

            txtName.setText(acc.name);
            txtEmail.setVisibility(android.view.View.GONE);

            if (acc.photoUri != null && !acc.photoUri.isEmpty()) {
                try {
                    imgAccProfile.setImageURI(android.net.Uri.parse(acc.photoUri));
                } catch (Exception e) {
                    imgAccProfile.setImageResource(R.drawable.profil);
                }
            }

            if (currentUser != null && acc.email.equals(currentUser.getEmail())) {
                imgActive.setVisibility(android.view.View.VISIBLE);
            }

            itemAccount.setOnClickListener(v -> {
                bottomSheetDialog.dismiss();
                if (currentUser != null && acc.email.equals(currentUser.getEmail())) {
                    return; // Sudah aktif
                }

                // Jika password kosong (sesi lama), arahkan ke login manual
                if (acc.password == null || acc.password.isEmpty()) {
                    mAuth.signOut();
                    sessionManager.logoutUser();
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.putExtra("prefill_email", acc.email);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                    return;
                }

                android.app.ProgressDialog pd = new android.app.ProgressDialog(this);
                pd.setMessage("Beralih ke " + acc.name + "...");
                pd.show();

                mAuth.signInWithEmailAndPassword(acc.email, acc.password).addOnCompleteListener(task -> {
                    pd.dismiss();
                    if (task.isSuccessful()) {
                        sessionManager.createLoginSession(acc.email);
                        com.example.poultrysense.utils.HistoryAkunManager.addHistory(this, acc.email, "login", "Beralih Akun Berhasil", android.os.Build.MODEL, "Indonesia");
                        com.example.poultrysense.utils.NotificationAkunManager.addNotification(this, acc.email, "keamanan", "Beralih Akun Berhasil", "Sesi login dialihkan ke akun ini dari perangkat " + android.os.Build.MODEL + ".");
                        startActivity(new Intent(this, DashboardActivity.class));
                        finish();
                        overridePendingTransition(0, 0);
                    } else {
                        Toast.makeText(this, "Gagal beralih akun. Silakan login manual.", Toast.LENGTH_SHORT).show();
                        mAuth.signOut();
                        sessionManager.logoutUser();
                        startActivity(new Intent(this, LoginActivity.class));
                        finish();
                    }
                });
            });

            containerAccounts.addView(itemAccount);
        }

        btnTambahAkun.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            mAuth.signOut();
            sessionManager.logoutUser();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        bottomSheetDialog.show();
    }

}