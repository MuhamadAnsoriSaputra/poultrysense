package com.example.poultrysense.activities;

import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.poultrysense.R;
import com.example.poultrysense.adapters.FAQAdapter;
import com.example.poultrysense.models.FAQItem;
import java.util.ArrayList;
import java.util.List;

public class FAQActivity extends AppCompatActivity {

    private RecyclerView rvFaq;
    private FAQAdapter adapter;
    private List<FAQItem> faqList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);

        ImageView btnBack = findViewById(R.id.btn_back_faq);
        btnBack.setOnClickListener(v -> finish());

        rvFaq = findViewById(R.id.rv_faq);
        faqList = new ArrayList<>();

        prepareFAQData();

        adapter = new FAQAdapter(faqList);
        rvFaq.setLayoutManager(new LinearLayoutManager(this));
        rvFaq.setAdapter(adapter);
    }

    private void prepareFAQData() {
        // Akun
        faqList.add(new FAQItem("Bagaimana cara mendaftar akun baru?", 
                "Anda dapat mendaftar dengan menekan 'Daftar sekarang' di halaman login, lalu masukkan nama, email, dan kata sandi Anda."));
        faqList.add(new FAQItem("Bagaimana cara mengubah nama atau foto profil?", 
                "Masuk ke menu Akun, pilih 'Ubah Profil'. Di sana Anda bisa mengganti nama dan mengunggah foto profil baru."));
        faqList.add(new FAQItem("Apakah data saya aman jika saya logout?", 
                "Ya, data akun dan riwayat Anda tersimpan dengan aman di sistem kami dan akan muncul kembali saat Anda login."));

        // Kata Sandi
        faqList.add(new FAQItem("Apa yang harus saya lakukan jika lupa kata sandi?", 
                "Klik 'Lupa kata sandi?' pada halaman login. Kami akan mengirimkan instruksi pengaturan ulang ke email Anda."));
        faqList.add(new FAQItem("Bagaimana kriteria kata sandi yang aman?", 
                "Gunakan minimal 6 karakter dengan kombinasi huruf dan angka agar akun Anda lebih terlindungi."));

        // Google
        faqList.add(new FAQItem("Apakah saya bisa login menggunakan akun Google?", 
                "Ya, PoultrySense mendukung login cepat menggunakan Google. Cukup tekan tombol 'Login dengan Google' di halaman login."));
        faqList.add(new FAQItem("Kenapa saya tidak bisa login dengan Google?", 
                "Pastikan perangkat Anda terhubung ke internet dan layanan Google Play Service dalam keadaan aktif."));

        // Pakan
        faqList.add(new FAQItem("Bagaimana cara menghubungkan sensor pakan?", 
                "Pastikan alat IoT Anda aktif dan terhubung ke jaringan Wi-Fi yang sama dengan ponsel Anda. Aplikasi akan mendeteksi sensor secara otomatis."));
        faqList.add(new FAQItem("Bagaimana cara mengatur jadwal pakan otomatis?", 
                "Masuk ke menu 'Jadwal Pakan' di Dashboard, tekan ikon tambah (+), lalu tentukan jam dan menit pemberian pakan."));
        faqList.add(new FAQItem("Berapa kapasitas maksimal wadah pakan yang didukung?", 
                "Saat ini aplikasi mendukung pemantauan wadah dengan kapasitas maksimal hingga 5kg (5000 gram)."));
    }
}
