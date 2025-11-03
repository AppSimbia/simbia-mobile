package com.germinare.simbia_mobile.ui.features.home.fragments.challenges.adapter;

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

    private final String authorName;
    private final String authorLogoUrl;

    public SolutionAdapter(List<SolutionResponse> solutions, String authorName, String authorLogoUrl) {
        this.solutions = solutions != null ? solutions : new ArrayList<>();
        this.authorName = authorName;
        this.authorLogoUrl = authorLogoUrl;
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

        public SolutionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSolutionTitle = itemView.findViewById(R.id.tv_solution_title);
            tvSolutionShortText = itemView.findViewById(R.id.tv_solution_short_text);
        }

        public void bind(SolutionResponse solution) {
            tvSolutionTitle.setText(solution.getTitle());
            tvSolutionShortText.setText(solution.getText());
        }
    }
}