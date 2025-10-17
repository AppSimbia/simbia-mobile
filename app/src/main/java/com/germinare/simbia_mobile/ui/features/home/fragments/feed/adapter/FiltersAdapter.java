package com.germinare.simbia_mobile.ui.features.home.fragments.feed.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.germinare.simbia_mobile.R;

import java.util.HashMap;
import java.util.Map;

public class FiltersAdapter extends RecyclerView.Adapter<FiltersAdapter.FilterHolder> {

    private final Context ctx;
    private final String[] filters;
    private final Map<String, Boolean> mapFilters = new HashMap<>();
    private final boolean interactible;
    private int selectedPosition = RecyclerView.NO_POSITION;

    public FiltersAdapter(Context ctx, String[] filters){
        this.ctx = ctx;
        this.filters = filters;
        this.interactible = true;

        for (String filter : filters){
            mapFilters.put(filter, Boolean.FALSE);
        }
    }

    public FiltersAdapter(Context ctx, String[] filters, boolean interactible){
        this.ctx = ctx;
        this.filters = filters;
        this.interactible = interactible;

        for (String filter : filters){
            mapFilters.put(filter, Boolean.FALSE);
        }
    }

    @NonNull
    @Override
    public FilterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.filter_layout,
                parent,
                false
        );
        return new FilterHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FilterHolder holder, int position) {
        String filter = filters[position];
        holder.filter.setText(filter);

        boolean isSelected = position == selectedPosition;

        if (isSelected) {
            holder.card.setCardBackgroundColor(ContextCompat.getColor(ctx, R.color.md_theme_primary));
            holder.filter.setTextColor(ContextCompat.getColor(ctx, R.color.md_theme_inverseOnSurface));
        } else {
            holder.card.setCardBackgroundColor(Color.TRANSPARENT);
            holder.filter.setTextColor(ContextCompat.getColor(ctx, R.color.md_theme_primary));
        }

        if (interactible) {
            holder.card.setOnClickListener(v -> {
                int oldPosition = selectedPosition;

                if (selectedPosition == position) {
                    selectedPosition = RecyclerView.NO_POSITION;
                    notifyItemChanged(position);
                } else {
                    selectedPosition = position;
                    notifyItemChanged(oldPosition);
                    notifyItemChanged(selectedPosition);
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        return filters.length;
    }

    public static class FilterHolder extends RecyclerView.ViewHolder {

        TextView filter;
        CardView card;

        public FilterHolder(@NonNull View itemView) {
            super(itemView);
            this.filter = itemView.findViewById(R.id.filter);
            this.card = itemView.findViewById(R.id.card_filter);
        }
    }
}
