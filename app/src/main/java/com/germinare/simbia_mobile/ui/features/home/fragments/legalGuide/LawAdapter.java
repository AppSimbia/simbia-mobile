package com.germinare.simbia_mobile.ui.features.home.fragments.legalGuide;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.germinare.simbia_mobile.R;
import com.germinare.simbia_mobile.data.api.model.integration.LawResponse;

import java.util.ArrayList;
import java.util.List;

public class LawAdapter extends RecyclerView.Adapter<LawAdapter.LawViewHolder> {

    private List<LawResponse> lawList = new ArrayList<>();

    public LawAdapter(List<LawResponse> initialList) {
        if (initialList != null) {
            this.lawList = initialList;
        }
    }

    public void submitList(List<LawResponse> newList) {
        lawList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LawViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_law, parent, false);
        return new LawViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LawViewHolder holder, int position) {
        LawResponse law = lawList.get(position);
        holder.bind(law);
    }

    @Override
    public int getItemCount() {
        return lawList.size();
    }

    public static class LawViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvTitle;
        private final TextView tvEmenta;
        private final TextView tvArea;
        private final TextView tvAssunto;

        public LawViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_law_title);
            tvEmenta = itemView.findViewById(R.id.tv_law_ementa);
            tvArea = itemView.findViewById(R.id.tv_law_area);
            tvAssunto = itemView.findViewById(R.id.tv_law_assunto);
        }

        public void bind(LawResponse law) {
            String title = String.format("%s nº %s/%d", law.getDocumento(), law.getNumero(), law.getAno());
            tvTitle.setText(title);

            tvEmenta.setText(law.getEmenta());

            tvArea.setText(String.format("Área: %s", law.getArea()));
            tvAssunto.setText(String.format("Assunto: %s", law.getAssunto()));
        }
    }
}