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

    public interface OnLawClickListener {
        void onLawClick(String url);
    }

    private List<LawResponse> lawList = new ArrayList<>();
    private final OnLawClickListener listener;

    public LawAdapter(List<LawResponse> initialList, OnLawClickListener listener) {
        if (initialList != null) this.lawList = initialList;
        this.listener = listener;
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
        return new LawViewHolder(view, listener);
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

        private final TextView tvTitle, tvEmenta, tvArea, tvAssunto;
        private final View btnVerDetalhes;

        public LawViewHolder(@NonNull View itemView, OnLawClickListener listener) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_law_title);
            tvEmenta = itemView.findViewById(R.id.tv_law_ementa);
            tvArea = itemView.findViewById(R.id.tv_law_area);
            tvAssunto = itemView.findViewById(R.id.tv_law_assunto);
            btnVerDetalhes = itemView.findViewById(R.id.btn_view_details);

            btnVerDetalhes.setOnClickListener(v -> {
                if (listener != null && getBindingAdapterPosition() != RecyclerView.NO_POSITION) {
                    LawResponse law = (LawResponse) v.getTag();
                    listener.onLawClick(law.getLink());
                }
            });
        }

        public void bind(LawResponse law) {
            String title = String.format("%s nº %s/%d", law.getDocumento(), law.getNumero(), law.getAno());
            tvTitle.setText(title);
            tvEmenta.setText(law.getEmenta());
            tvArea.setText("Área: " + law.getArea());
            tvAssunto.setText("Assunto: " + law.getAssunto());

            btnVerDetalhes.setTag(law);
        }
    }
}

