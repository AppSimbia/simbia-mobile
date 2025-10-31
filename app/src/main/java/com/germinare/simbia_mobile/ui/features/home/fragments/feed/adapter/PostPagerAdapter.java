package com.germinare.simbia_mobile.ui.features.home.fragments.feed.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.germinare.simbia_mobile.R;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class PostPagerAdapter extends RecyclerView.Adapter<PostPagerAdapter.PostCarroselHolder> {

    private final List<Post> posts;
    private final Consumer<Post> onClickPost;

    private static final List<String> MEASURE_UNITS = Arrays.asList(
            "Kg", "Litro", "Metro", "Unidade"
    );

    public PostPagerAdapter(List<Post> posts, Consumer<Post> onClickPost) {
        this.posts = posts;
        this.onClickPost = onClickPost;
    }

    @NonNull
    @Override
    public PostCarroselHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_carrosel_layout, parent, false);
        return new PostCarroselHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostCarroselHolder holder, int position) {
        Post post = posts.get(position);

        holder.title.setText(post.getTitle());
        holder.subtitle.setText("R$ " + String.format("%.2f", post.getPrice()) + " | " +
                post.getQuantity() + " " + MEASURE_UNITS.get(post.getMeasureUnit() - 1));

        Glide.with(holder.image.getContext())
                .load(post.getUrlImage())
                .into(holder.image);

        Glide.with(holder.industry.getContext())
                .load(post.getUrlIndustry())
                .into(holder.industry);
        updateDots(holder, position);

        holder.cardPost.setOnClickListener(v -> onClickPost.accept(post));
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    private void updateDots(PostCarroselHolder holder, int position) {
        int dotsCount = holder.dots.getChildCount();

        if (dotsCount == 0) return;
        int activeIndex = position % dotsCount;

        for (int i = 0; i < dotsCount; i++) {
            View dot = holder.dots.getChildAt(i);
            dot.setBackgroundResource(
                    i == activeIndex ? R.drawable.dot_active : R.drawable.dot_inactive
            );
        }
    }

    public static class PostCarroselHolder extends RecyclerView.ViewHolder {
        TextView title, subtitle;
        ImageView image;
        ShapeableImageView industry;
        CardView cardPost;
        LinearLayout dots;

        public PostCarroselHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tx_carrossel_title);
            subtitle = itemView.findViewById(R.id.tx_carrossel_subtitle);
            image = itemView.findViewById(R.id.image_carrossel_post);
            industry = itemView.findViewById(R.id.image_industry_post);
            cardPost = itemView.findViewById(R.id.card_post_carrosel);
            dots = itemView.findViewById(R.id.layout_dots);
        }
    }
}
