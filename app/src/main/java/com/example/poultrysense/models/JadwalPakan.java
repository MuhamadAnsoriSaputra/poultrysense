package com.example.poultrysense.models;

public class JadwalPakan {
    private String id;
    private String jam;
    private String menit;
    private String label;
    private boolean aktif;

    public JadwalPakan(String id, String jam, String menit, String label, boolean aktif) {
        this.id = id;
        this.jam = jam;
        this.menit = menit;
        this.label = label;
        this.aktif = aktif;
    }

    public String getId() { return id; }
    public String getJam() { return jam; }
    public String getMenit() { return menit; }
    public String getLabel() { return label; }
    public boolean isAktif() { return aktif; }
    public void setAktif(boolean aktif) { this.aktif = aktif; }

    public String getWaktuFormatted() {
        return String.format("%02d:%02d", Integer.parseInt(jam), Integer.parseInt(menit));
    }
}
