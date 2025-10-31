package com.germinare.simbia_mobile.ui.features.home.fragments.challenges;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.germinare.simbia_mobile.R;
import com.germinare.simbia_mobile.data.api.model.mongo.SolutionResponse;

import java.util.ArrayList;
import java.util.List;

public class SolutionAdapter extends RecyclerView.Adapter<SolutionAdapter.SolutionViewHolder> {

    private List<SolutionResponse> solutions;

    // Você pode adicionar um listener aqui se quiser que o clique no card leve a uma tela de detalhes da solução
    // private OnSolutionClickListener listener;

    public SolutionAdapter(List<SolutionResponse> solutions) {
        this.solutions = solutions != null ? solutions : new ArrayList<>();
    }

    @NonNull
    @Override
    public SolutionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_solution_suggestion, parent, false);
        return new SolutionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SolutionViewHolder holder, int position) {
        SolutionResponse solution = solutions.get(position);
        holder.bind(solution);
    }

    @Override
    public int getItemCount() {
        return solutions.size();
    }

    /**
     * Atualiza a lista de soluções e notifica o RecyclerView.
     */
    public void updateList(List<SolutionResponse> newSolutions) {
        this.solutions.clear();
        if (newSolutions != null) {
            this.solutions.addAll(newSolutions);
        }
        notifyDataSetChanged();
    }


    static class SolutionViewHolder extends RecyclerView.ViewHolder {

        ImageView ivAuthorLogo;
        TextView tvAuthorName;
        TextView tvSolutionTitle;
        TextView tvSolutionShortText;
        TextView tvViewDetails;

        public SolutionViewHolder(@NonNull View itemView) {
            super(itemView);
            // Mapeie os IDs do seu item_solution_suggestion.xml
            ivAuthorLogo = itemView.findViewById(R.id.iv_author_logo);
            tvAuthorName = itemView.findViewById(R.id.tv_author_name);
            tvSolutionTitle = itemView.findViewById(R.id.tv_solution_title);
            tvSolutionShortText = itemView.findViewById(R.id.tv_solution_short_text);
        }

        public void bind(SolutionResponse solution) {
            // Preenche os dados
            tvSolutionTitle.setText(solution.getTitle());
            tvSolutionShortText.setText(solution.getText());

            // O logo (ivAuthorLogo) geralmente requer uma biblioteca de carregamento de imagem (como Glide ou Picasso)
            // e uma URL para ser carregado. Por enquanto, ele usa o logo padrão.

            // Lógica para clique no item (se necessário)
            // tvViewDetails.setOnClickListener(v -> { ... });
        }
    }
}