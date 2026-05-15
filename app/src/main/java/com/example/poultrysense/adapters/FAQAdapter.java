package com.example.poultrysense.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.poultrysense.R;
import com.example.poultrysense.models.FAQItem;
import java.util.List;

public class FAQAdapter extends RecyclerView.Adapter<FAQAdapter.ViewHolder> {

    private List<FAQItem> faqList;

    public FAQAdapter(List<FAQItem> faqList) {
        this.faqList = faqList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_faq, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FAQItem item = faqList.get(position);
        holder.txtQuestion.setText(item.getQuestion());
        holder.txtAnswer.setText(item.getAnswer());

        boolean isExpanded = item.isExpanded();
        holder.txtAnswer.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.imgArrow.setRotation(isExpanded ? 90 : 0);

        holder.itemView.setOnClickListener(v -> {
            item.setExpanded(!item.isExpanded());
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return faqList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtQuestion, txtAnswer;
        ImageView imgArrow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtQuestion = itemView.findViewById(R.id.txt_faq_question);
            txtAnswer = itemView.findViewById(R.id.txt_faq_answer);
            imgArrow = itemView.findViewById(R.id.img_faq_arrow);
        }
    }
}
