package com.example.poultrysense.models;

public class HistoryPakan {
    private String id;
    private String tipe; // "Manual" or "Otomatis"
    private int jumlahGram;
    private String waktu; // Format: "dd MMMM yyyy - hh:mm a"
    private String bulan; // Format: "MMMM yyyy" for filtering

    private boolean hidden = false;

    public HistoryPakan(String id, String tipe, int jumlahGram, String waktu, String bulan) {
        this(id, tipe, jumlahGram, waktu, bulan, false);
    }

    public HistoryPakan(String id, String tipe, int jumlahGram, String waktu, String bulan, boolean hidden) {
        this.id = id;
        this.tipe = tipe;
        this.jumlahGram = jumlahGram;
        this.waktu = waktu;
        this.bulan = bulan;
        this.hidden = hidden;
    }

    public String getId() { return id; }
    public String getTipe() { return tipe; }
    public int getJumlahGram() { return jumlahGram; }
    public String getWaktu() { return waktu; }
    public String getBulan() { return bulan; }
    public boolean isHidden() { return hidden; }
    public void setHidden(boolean hidden) { this.hidden = hidden; }

    private boolean selected = false;
    public boolean isSelected() { return selected; }
    public void setSelected(boolean selected) { this.selected = selected; }
}
