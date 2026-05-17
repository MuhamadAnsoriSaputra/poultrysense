package com.example.poultrysense.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import com.example.poultrysense.R;
import com.example.poultrysense.activities.NotificationActivity;
import com.example.poultrysense.models.HistoryPakan;
import com.example.poultrysense.models.JadwalPakan;
import com.example.poultrysense.models.NotificationItem;
import com.google.firebase.database.FirebaseDatabase;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class PakanAlarmReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "pakan_alarm_channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        String jadwalId = intent.getStringExtra("jadwal_id");
        String jadwalLabel = intent.getStringExtra("jadwal_label");

        if (jadwalId == null) return;

        JadwalManager jm = new JadwalManager(context);
        List<JadwalPakan> list = jm.getListJadwal();
        JadwalPakan activeJadwal = null;

        for (JadwalPakan j : list) {
            if (j.getId().equals(jadwalId) && j.isAktif()) {
                activeJadwal = j;
                break;
            }
        }

        // Jika jadwal sudah dimatikan atau dihapus, batalkan eksekusi
        if (activeJadwal == null) return;

        // 1. Picu Firebase Realtime Database (Buka Katup Pakan)
        FirebaseDatabase.getInstance().getReference().child("beri_pakan").setValue(true);

        // 2. Catat ke Riwayat Pakan (Otomatis)
        SimpleDateFormat sdfWaktu = new SimpleDateFormat("dd MMMM yyyy - hh:mm a", new Locale("id", "ID"));
        SimpleDateFormat sdfBulan = new SimpleDateFormat("MMMM yyyy", new Locale("id", "ID"));
        String waktuStr = sdfWaktu.format(new Date());
        String bulanStr = sdfBulan.format(new Date());

        HistoryManager hm = new HistoryManager(context);
        hm.addHistory(new HistoryPakan(
                UUID.randomUUID().toString(),
                "Otomatis",
                200, // Takaran otomatis 200 gram
                waktuStr,
                bulanStr
        ));

        // 3. Catat ke Notifikasi Internal Aplikasi
        NotifManager nm = new NotifManager(context);
        String judulNotif = "Pakan Otomatis Berhasil Diberikan!";
        String pesanNotif = "Waktunya makan! Ayam kesayangan Anda telah diberi pakan secara otomatis sesuai jadwal (" + jadwalLabel + "). Nyam nyam!";
        nm.addNotification(new NotificationItem(
                UUID.randomUUID().toString(),
                "Pakan",
                judulNotif,
                pesanNotif,
                waktuStr
        ));

        // 4. Tampilkan Notifikasi Sistem Android (Muncul di Bar HP)
        showAndroidNotification(context, judulNotif, pesanNotif);

        // 5. Jadwalkan ulang alarm untuk besok hari pada jam yang sama
        jm.scheduleAlarm(context, activeJadwal);
    }

    private void showAndroidNotification(Context context, String title, String message) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Jadwal Pakan Otomatis",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Notifikasi saat pakan otomatis diberikan");
            notificationManager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(context, NotificationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo_app)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }
}
