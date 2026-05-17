package com.example.poultrysense.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.poultrysense.R;
import com.example.poultrysense.utils.NotificationAkunManager;

import java.util.List;

public class NotificationAkunAdapter extends RecyclerView.Adapter<NotificationAkunAdapter.ViewHolder> {

    private List<NotificationAkunManager.NotifItem> notifList;

    public NotificationAkunAdapter(List<NotificationAkunManager.NotifItem> notifList) {
        this.notifList = notifList;
    }

    public void updateData(List<NotificationAkunManager.NotifItem> newNotifList) {
        this.notifList = newNotifList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notif_akun, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NotificationAkunManager.NotifItem item = notifList.get(position);

        holder.txtTitle.setText(item.title);
        holder.txtTime.setText(item.timestamp);
        holder.txtMessage.setText(item.message);

        if ("keamanan".equals(item.type)) {
            holder.imgIcon.setImageResource(R.drawable.ic_login);
        } else {
            holder.imgIcon.setImageResource(R.drawable.ic_ubah_profil);
        }
    }

    @Override
    public int getItemCount() {
        return notifList != null ? notifList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgIcon;
        TextView txtTitle, txtTime, txtMessage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgIcon = itemView.findViewById(R.id.img_notif_icon);
            txtTitle = itemView.findViewById(R.id.txt_notif_title);
            txtTime = itemView.findViewById(R.id.txt_notif_time);
            txtMessage = itemView.findViewById(R.id.txt_notif_message);
        }
    }
}
