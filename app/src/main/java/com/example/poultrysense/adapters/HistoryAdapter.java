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

    public interface OnHistoryClickListener {
        void onDeleteClick(HistoryPakan history);
    }

    public HistoryAdapter(List<HistoryPakan> historyList, OnHistoryClickListener listener) {
        this.historyList = historyList;
        this.listener = listener;
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

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(history);
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
        ImageView btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txt_title_history);
            txtTipe = itemView.findViewById(R.id.txt_tipe_history);
            txtTime = itemView.findViewById(R.id.txt_time_history);
            btnDelete = itemView.findViewById(R.id.btn_delete_history);
        }
    }
}
