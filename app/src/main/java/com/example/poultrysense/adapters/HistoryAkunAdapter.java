package com.example.poultrysense.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.poultrysense.R;
import com.example.poultrysense.utils.HistoryAkunManager;

import java.util.List;

public class HistoryAkunAdapter extends RecyclerView.Adapter<HistoryAkunAdapter.ViewHolder> {

    private List<HistoryAkunManager.HistoryItem> historyList;

    public HistoryAkunAdapter(List<HistoryAkunManager.HistoryItem> historyList) {
        this.historyList = historyList;
    }

    public void updateData(List<HistoryAkunManager.HistoryItem> newHistoryList) {
        this.historyList = newHistoryList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history_akun, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HistoryAkunManager.HistoryItem item = historyList.get(position);

        holder.txtTitle.setText(item.title);
        holder.txtTime.setText(item.timestamp);
        holder.txtDevice.setText(item.device);
        holder.txtLocation.setText(item.location);

        if ("login".equals(item.type)) {
            holder.imgIcon.setImageResource(R.drawable.ic_login);
        } else {
            holder.imgIcon.setImageResource(R.drawable.ic_ubah_profil);
        }
    }

    @Override
    public int getItemCount() {
        return historyList != null ? historyList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgIcon;
        TextView txtTitle, txtTime, txtDevice, txtLocation;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgIcon = itemView.findViewById(R.id.img_history_icon);
            txtTitle = itemView.findViewById(R.id.txt_history_title);
            txtTime = itemView.findViewById(R.id.txt_history_time);
            txtDevice = itemView.findViewById(R.id.txt_history_device);
            txtLocation = itemView.findViewById(R.id.txt_history_location);
        }
    }
}
