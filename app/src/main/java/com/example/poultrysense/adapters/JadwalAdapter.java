package com.example.poultrysense.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.poultrysense.R;
import com.example.poultrysense.models.JadwalPakan;
import com.example.poultrysense.utils.JadwalManager;
import com.google.android.material.switchmaterial.SwitchMaterial;
import java.util.List;

public class JadwalAdapter extends RecyclerView.Adapter<JadwalAdapter.ViewHolder> {

    private List<JadwalPakan> list;
    private JadwalManager manager;

    public JadwalAdapter(List<JadwalPakan> list, JadwalManager manager) {
        this.list = list;
        this.manager = manager;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_jadwal, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        JadwalPakan jadwal = list.get(position);
        holder.txtJam.setText(jadwal.getWaktuFormatted());
        holder.txtLabel.setText(jadwal.getLabel());
        holder.switchJadwal.setChecked(jadwal.isAktif());

        holder.switchJadwal.setOnCheckedChangeListener((buttonView, isChecked) -> {
            jadwal.setAktif(isChecked);
            manager.saveJadwal(jadwal);
        });

        holder.itemView.setOnLongClickListener(v -> {
            // Logika hapus jadwal bisa ditambahkan di sini
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void updateData(List<JadwalPakan> newList) {
        this.list = newList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtJam, txtLabel;
        SwitchMaterial switchJadwal;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtJam = itemView.findViewById(R.id.txt_jam_jadwal);
            txtLabel = itemView.findViewById(R.id.txt_label_jadwal);
            switchJadwal = itemView.findViewById(R.id.switch_jadwal);
        }
    }
}
