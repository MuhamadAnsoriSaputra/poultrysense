package com.example.poultrysense.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.poultrysense.R;
import com.example.poultrysense.models.JadwalPakan;
import com.example.poultrysense.utils.JadwalManager;
import com.google.android.material.switchmaterial.SwitchMaterial;
import java.util.ArrayList;
import java.util.List;

public class JadwalAdapter extends RecyclerView.Adapter<JadwalAdapter.ViewHolder> {

    private List<JadwalPakan> list;
    private JadwalManager manager;
    private boolean isSelectionMode = false;
    private OnJadwalInteractionListener listener;

    public interface OnJadwalInteractionListener {
        void onJadwalClick(JadwalPakan jadwal);
        void onSelectionModeChange(boolean isSelectionMode, int selectedCount);
    }

    public JadwalAdapter(List<JadwalPakan> list, JadwalManager manager, OnJadwalInteractionListener listener) {
        this.list = list;
        this.manager = manager;
        this.listener = listener;
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

        if (isSelectionMode) {
            holder.switchJadwal.setVisibility(View.GONE);
            holder.imgSelectJadwal.setVisibility(View.VISIBLE);
            holder.imgSelectJadwal.setImageResource(jadwal.isSelected() ? R.drawable.ic_circle_checked : R.drawable.ic_circle_unchecked);
        } else {
            holder.switchJadwal.setVisibility(View.VISIBLE);
            holder.imgSelectJadwal.setVisibility(View.GONE);
        }

        holder.switchJadwal.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isSelectionMode && buttonView.isPressed()) {
                jadwal.setAktif(isChecked);
                manager.saveJadwal(jadwal);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (isSelectionMode) {
                jadwal.setSelected(!jadwal.isSelected());
                notifyItemChanged(position);
                if (listener != null) {
                    listener.onSelectionModeChange(isSelectionMode, getSelectedCount());
                }
            } else {
                if (listener != null) {
                    listener.onJadwalClick(jadwal);
                }
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (!isSelectionMode) {
                isSelectionMode = true;
                jadwal.setSelected(true);
                notifyDataSetChanged();
                if (listener != null) {
                    listener.onSelectionModeChange(isSelectionMode, getSelectedCount());
                }
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void updateData(List<JadwalPakan> newList) {
        this.list = newList;
        if (!isSelectionMode) {
            for (JadwalPakan j : list) j.setSelected(false);
        }
        notifyDataSetChanged();
    }

    public boolean isSelectionMode() { return isSelectionMode; }

    public void setSelectionMode(boolean mode) {
        this.isSelectionMode = mode;
        if (!mode) {
            for (JadwalPakan j : list) j.setSelected(false);
        }
        notifyDataSetChanged();
        if (listener != null) {
            listener.onSelectionModeChange(isSelectionMode, getSelectedCount());
        }
    }

    public void selectAll(boolean select) {
        for (JadwalPakan j : list) j.setSelected(select);
        notifyDataSetChanged();
        if (listener != null) {
            listener.onSelectionModeChange(isSelectionMode, getSelectedCount());
        }
    }

    public int getSelectedCount() {
        int count = 0;
        for (JadwalPakan j : list) {
            if (j.isSelected()) count++;
        }
        return count;
    }

    public List<JadwalPakan> getSelectedItems() {
        List<JadwalPakan> selected = new ArrayList<>();
        for (JadwalPakan j : list) {
            if (j.isSelected()) selected.add(j);
        }
        return selected;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtJam, txtLabel;
        SwitchMaterial switchJadwal;
        ImageView imgSelectJadwal;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtJam = itemView.findViewById(R.id.txt_jam_jadwal);
            txtLabel = itemView.findViewById(R.id.txt_label_jadwal);
            switchJadwal = itemView.findViewById(R.id.switch_jadwal);
            imgSelectJadwal = itemView.findViewById(R.id.img_select_jadwal);
        }
    }
}
