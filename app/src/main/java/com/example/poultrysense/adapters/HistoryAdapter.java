package com.example.poultrysense.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.poultrysense.R;
import com.example.poultrysense.models.HistoryPakan;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private List<HistoryPakan> historyList;
    private OnHistoryClickListener listener;
    private boolean selectionMode = false;

    public interface OnHistoryClickListener {
        void onLongClick(HistoryPakan history, int position);
        void onItemClick(HistoryPakan history, int position);
    }

    public HistoryAdapter(List<HistoryPakan> historyList, OnHistoryClickListener listener) {
        this.historyList = historyList;
        this.listener = listener;
    }

    public void setSelectionMode(boolean mode) {
        this.selectionMode = mode;
        notifyDataSetChanged();
    }

    public boolean isSelectionMode() {
        return selectionMode;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history_pakan, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HistoryPakan history = historyList.get(position);
        holder.txtTitle.setText("Pemberian pakan");
        holder.txtTipe.setText(history.getTipe() + " - " + history.getJumlahGram() + "g");
        holder.txtTime.setText(history.getWaktu());

        if (selectionMode) {
            holder.imgSelect.setVisibility(View.VISIBLE);
            holder.imgSelect.setImageResource(history.isSelected() ? R.drawable.ic_circle_checked : R.drawable.ic_circle_unchecked);
        } else {
            holder.imgSelect.setVisibility(View.GONE);
        }

        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onLongClick(history, position);
            }
            return true;
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(history, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public void updateList(List<HistoryPakan> newList) {
        this.historyList = newList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle, txtTipe, txtTime;
        ImageView imgSelect;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txt_title_history);
            txtTipe = itemView.findViewById(R.id.txt_tipe_history);
            txtTime = itemView.findViewById(R.id.txt_time_history);
            imgSelect = itemView.findViewById(R.id.img_select_history);
        }
    }
}
