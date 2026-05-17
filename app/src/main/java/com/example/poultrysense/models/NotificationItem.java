package com.example.poultrysense.models;

public class NotificationItem {
    private String id;
    private String kategori; // "Sistem" or "Promosi"
    private String judul;
    private String pesan;
    private String waktu;

    private boolean hidden = false;

    public NotificationItem(String id, String kategori, String judul, String pesan, String waktu) {
        this(id, kategori, judul, pesan, waktu, false);
    }

    public NotificationItem(String id, String kategori, String judul, String pesan, String waktu, boolean hidden) {
        this.id = id;
        this.kategori = kategori;
        this.judul = judul;
        this.pesan = pesan;
        this.waktu = waktu;
        this.hidden = hidden;
    }

    public String getId() { return id; }
    public String getKategori() { return kategori; }
    public String getJudul() { return judul; }
    public String getPesan() { return pesan; }
    public String getWaktu() { return waktu; }
    public boolean isHidden() { return hidden; }
    public void setHidden(boolean hidden) { this.hidden = hidden; }

    private boolean selected = false;
    public boolean isSelected() { return selected; }
    public void setSelected(boolean selected) { this.selected = selected; }
}
