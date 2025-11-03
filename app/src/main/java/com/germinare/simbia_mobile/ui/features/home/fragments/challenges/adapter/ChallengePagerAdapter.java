package com.germinare.simbia_mobile.ui.features.home.fragments.challenges.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.germinare.simbia_mobile.R;

import java.util.List;
import java.util.function.Consumer;

public class ChallengePagerAdapter extends RecyclerView.Adapter<ChallengePagerAdapter.ChallengeViewHolder> {

    private final List<Challenge> challengeList;
    private final Consumer<Challenge> onClick;
    private final boolean isPager;

    public ChallengePagerAdapter(List<Challenge> challengeList, boolean isPager, Consumer<Challenge> onClick) {
        this.challengeList = challengeList;
        this.onClick = onClick;
        this.isPager = isPager;
    }

    @NonNull
    @Override
    public ChallengeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (isPager) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_challenge_pager, parent, false);
        }else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_challenge, parent, false);
        }
        return new ChallengeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChallengeViewHolder holder, int position) {
        Challenge challenge = challengeList.get(position);
        holder.bind(challenge, onClick);
    }

    @Override
    public int getItemCount() {
        return challengeList != null ? challengeList.size() : 0;
    }

    public void updateList(List<Challenge> newList) {
        challengeList.clear();
        challengeList.addAll(newList);
        notifyDataSetChanged();
    }

    static class ChallengeViewHolder extends RecyclerView.ViewHolder {
        TextView tvBrandName;
        TextView tvChallengeTitle;
        TextView tvChallengeDescription;
        TextView tvSuggestSolution;
        ImageView ivLogo;
        public ChallengeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBrandName = itemView.findViewById(R.id.tv_brand_name);
            tvChallengeTitle = itemView.findViewById(R.id.tv_challenge_title);
            tvChallengeDescription = itemView.findViewById(R.id.tv_challenge_description);
            tvSuggestSolution = itemView.findViewById(R.id.tv_suggest_solution);
            ivLogo = itemView.findViewById(R.id.iv_logo);
        }

        public void bind(final Challenge challenge, final Consumer<Challenge> onClick) {
            tvBrandName.setText(challenge.getIndustryName() != null ? challenge.getIndustryName() : "Indústria");
            tvChallengeTitle.setText(challenge.getTitle());
            tvChallengeDescription.setText(challenge.getText());

            Glide.with(ivLogo.getContext())
                    .load(challenge.getIndustryImage())
                    .into(ivLogo);

            View clickArea = tvSuggestSolution.getParent() instanceof ViewGroup
                    ? (ViewGroup) tvSuggestSolution.getParent()
                    : itemView;

            clickArea.setOnClickListener(v -> {
                if (onClick != null) onClick.accept(challenge);
            });
        }
    }
}
