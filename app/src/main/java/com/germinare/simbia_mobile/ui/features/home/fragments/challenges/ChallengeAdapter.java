package com.germinare.simbia_mobile.ui.features.home.fragments.challenges; // Pacote sugerido

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.germinare.simbia_mobile.R; // Necessário para acessar R.layout.item_challenge
import com.germinare.simbia_mobile.data.api.model.mongo.ChalengeResponse;

import java.util.List;

public class ChallengeAdapter extends RecyclerView.Adapter<ChallengeAdapter.ChallengeViewHolder> {

    private List<ChalengeResponse> challengeList;
    private OnChallengeClickListener listener;

    /**
     * Interface para lidar com cliques no item "Sugerir Solução".
     * O ChallengesFragment deve implementar esta interface.
     */
    public interface OnChallengeClickListener {
        void onSuggestSolutionClick(ChalengeResponse challenge);
    }

    public ChallengeAdapter(List<ChalengeResponse> challengeList, OnChallengeClickListener listener) {
        this.challengeList = challengeList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ChallengeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Infla o layout do item de lista: item_challenge.xml
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_challenge, parent, false);
        return new ChallengeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChallengeViewHolder holder, int position) {
        ChalengeResponse challenge = challengeList.get(position);
        holder.bind(challenge, listener);
    }

    @Override
    public int getItemCount() {
        return challengeList.size();
    }

    /**
     * Método para atualizar a lista de desafios, chamado após a requisição da API.
     */
    public void updateList(List<ChalengeResponse> newList) {
        this.challengeList = newList;
        notifyDataSetChanged();
    }

    /**
     * ViewHolder para segurar as referências das views no item_challenge.xml.
     */
    static class ChallengeViewHolder extends RecyclerView.ViewHolder {
        TextView tvBrandName;
        TextView tvChallengeTitle;
        TextView tvChallengeDescription;
        TextView tvSuggestSolution;
        // Os outros componentes (ImageViews) são opcionais se não forem manipulados

        public ChallengeViewHolder(@NonNull View itemView) {
            super(itemView);
            // Mapeamento das views do item_challenge.xml
            tvBrandName = itemView.findViewById(R.id.tv_brand_name);
            tvChallengeTitle = itemView.findViewById(R.id.tv_challenge_title);
            tvChallengeDescription = itemView.findViewById(R.id.tv_challenge_description);
            tvSuggestSolution = itemView.findViewById(R.id.tv_suggest_solution);
            // tvSuggestSolution e ivArrow estão dentro de um ConstraintLayout que é clicável no seu XML
        }

        public void bind(final ChalengeResponse challenge, final OnChallengeClickListener listener) {
            // Preenche os dados do desafio
            // Note: O tv_brand_name deve ser preenchido com o nome do funcionário,
            // mas aqui usamos o ID como placeholder, pois o nome não está no ChalengeResponse.
            tvBrandName.setText("ID Questionador: " + challenge.getIdEmployeeQuestion());
            tvChallengeTitle.setText(challenge.getTitle());
            tvChallengeDescription.setText(challenge.getText());

            // Configura o listener de clique na área "Sugerir Solução"
            // O ideal é que a área de clique seja o elemento pai do tv_suggest_solution e iv_arrow
            View clickArea = tvSuggestSolution.getParent() instanceof ViewGroup ? (ViewGroup) tvSuggestSolution.getParent() : itemView;

            clickArea.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onSuggestSolutionClick(challenge);
                }
            });
        }
    }
}