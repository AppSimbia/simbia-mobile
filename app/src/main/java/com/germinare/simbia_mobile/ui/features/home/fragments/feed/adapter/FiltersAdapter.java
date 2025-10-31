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
import com.germinare.simbia_mobile.data.api.cache.PostgresCache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FiltersAdapter extends RecyclerView.Adapter<FiltersAdapter.FilterHolder> {

    private final Context ctx;
    private final PostgresCache postgresCache;
    private final List<String> filters;
    private final Map<String, Boolean> mapFilters = new HashMap<>();
    private final boolean interactible;
    private int selectedPosition = RecyclerView.NO_POSITION;

    public FiltersAdapter(Context ctx, List<String> filters){
        this.ctx = ctx;
        this.postgresCache = PostgresCache.getInstance();
        this.filters = filters;
        this.interactible = true;

        for (String filter : filters){
            mapFilters.put(filter, Boolean.FALSE);
        }
    }

    public FiltersAdapter(Context ctx, List<String> filters, boolean interactible){
        this.ctx = ctx;
        this.postgresCache = PostgresCache.getInstance();
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
        final int positionE = holder.getAbsoluteAdapterPosition();
        String filter = filters.get(positionE);
        holder.filter.setText(filter);

        boolean isSelected = positionE == selectedPosition;

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

                postgresCache.setPostsFiltered(
                        postgresCache.getPosts().stream()
                                .filter(post -> post.getProductCategory().getCategoryName().equals(filter))
                                .collect(Collectors.toList())
                );

                if (selectedPosition == positionE) {
                    selectedPosition = RecyclerView.NO_POSITION;
                    postgresCache.setPostsFiltered(postgresCache.getPosts());
                    notifyItemChanged(positionE);
                } else {
                    selectedPosition = positionE;
                    notifyItemChanged(oldPosition);
                    notifyItemChanged(selectedPosition);
                }
            });
        } else {
            holder.card.setCardBackgroundColor(ContextCompat.getColor(ctx, R.color.md_theme_primary));
            holder.filter.setTextColor(ContextCompat.getColor(ctx, R.color.md_theme_inverseOnSurface));
        }
    }


    @Override
    public int getItemCount() {
        return filters.size();
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
