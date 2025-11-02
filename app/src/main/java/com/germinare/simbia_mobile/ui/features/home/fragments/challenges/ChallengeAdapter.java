package com.germinare.simbia_mobile.ui.features.home.fragments.challenges;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.germinare.simbia_mobile.R;
import com.germinare.simbia_mobile.data.api.model.mongo.ChalengeResponse;
import com.germinare.simbia_mobile.data.api.model.postgres.IndustryResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChallengeAdapter extends RecyclerView.Adapter<ChallengeAdapter.ChallengeViewHolder> {

    public interface OnChallengeClickListener {
        void onSuggestSolutionClick(ChalengeResponse challenge);
        void onClick(ChalengeResponse challenge);
    }

    private final Map<Long, IndustryResponse> industryMap;
    private final OnChallengeClickListener listener;
    private final List<ChalengeResponse> challenges = new ArrayList<>();

    public ChallengeAdapter(Map<Long, IndustryResponse> industryMap, OnChallengeClickListener listener) {
        this.industryMap = industryMap;
        this.listener = listener;
    }

    public void setChallenges(List<ChalengeResponse> list) {
        challenges.clear();
        if (list != null) {
            challenges.addAll(list);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChallengeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_challenge, parent, false);
        return new ChallengeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChallengeViewHolder holder, int position) {
        ChalengeResponse challenge = challenges.get(position);
        holder.bind(challenge);
    }

    @Override
    public int getItemCount() {
        return challenges.size();
    }

    class ChallengeViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvTitle;
        private final TextView tvCompany;
        private final ImageView ivCompanyLogo;
        private final TextView tvDescription;

        public ChallengeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_challenge_title);
            tvDescription = itemView.findViewById(R.id.tv_challenge_description);
            tvCompany = itemView.findViewById(R.id.tv_brand_name);
            ivCompanyLogo = itemView.findViewById(R.id.iv_logo);
        }

        public void bind(ChalengeResponse challenge) {
            tvTitle.setText(challenge.getTitle());

            Long employeeId = challenge.getIdEmployeeQuestion();
            if (employeeId != null && industryMap.containsKey(employeeId)) {
                IndustryResponse industry = industryMap.get(employeeId);
                tvCompany.setText(industry.getIndustryName());
                if (industry.getImage() != null) {
                    Glide.with(ivCompanyLogo.getContext())
                            .load(industry.getImage())
                            .placeholder(R.drawable.carrosel_mock)
                            .into(ivCompanyLogo);
                } else {
                    ivCompanyLogo.setImageResource(R.drawable.carrosel_mock);
                }
            } else {
                tvCompany.setText("Empresa desconhecida");
                ivCompanyLogo.setImageResource(R.drawable.carrosel_mock);
            }

            itemView.setOnClickListener(v -> listener.onClick(challenge));
            itemView.findViewById(R.id.tv_suggest_solution)
                    .setOnClickListener(v -> listener.onSuggestSolutionClick(challenge));
        }
    }
}
