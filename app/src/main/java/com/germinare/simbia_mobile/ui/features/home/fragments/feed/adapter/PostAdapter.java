package com.germinare.simbia_mobile.ui.features.home.fragments.feed.adapter;

import static android.view.View.GONE;

import android.util.Log;
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

public class PostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Post> posts;
    private final Consumer<Post> onClickPost;
    private final boolean defaultPost;

    private static final List<String> MEASURE_UNITS = Arrays.asList(
            "Kg", "Litro", "Metro", "Unidade"
    );


    public PostAdapter(List<Post> posts, Consumer<Post> onClickPost){
        this.posts = posts;
        this.onClickPost = onClickPost;
        this.defaultPost = true;
    }

    public PostAdapter(List<Post> posts, Consumer<Post> onClickPost, boolean defaultPost){
        this.posts = posts;
        this.onClickPost = onClickPost;
        this.defaultPost = defaultPost;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (defaultPost){
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.post_layout,
                    parent,
                    false
            );
            return new PostHolder(view);
        }else{
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.post_carrosel_layout,
                    parent,
                    false
            );
            return new PostPagerAdapter.PostCarroselHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Post post = posts.get(position);

        if (holder instanceof PostHolder) {
            PostHolder h = (PostHolder) holder;
            h.title.setText(post.getTitle());
            h.price.setText("R$ " + String.format("%.2f", post.getPrice()));
            h.quantity.setText(post.getQuantity() + " " + MEASURE_UNITS.get(post.getMeasureUnit() - 1));
            Glide.with(h.image.getContext())
                    .load(post.getUrlImage())
                    .into(h.image);

            Glide.with(h.industry.getContext())
                    .load(post.getUrlIndustry())
                    .into(h.industry);

            h.cardPost.setOnClickListener(V -> onClickPost.accept(post));
        } else {
            PostPagerAdapter.PostCarroselHolder h = (PostPagerAdapter.PostCarroselHolder) holder;
            h.title.setText(post.getTitle());
            h.subtitle.setText("R$ " + String.format("%.2f", post.getPrice()) + " | " +
                    post.getQuantity() + " " + MEASURE_UNITS.get(post.getMeasureUnit() - 1));
            Glide.with(h.image.getContext())
                    .load(post.getUrlImage())
                    .into(h.image);

            Glide.with(h.industry.getContext())
                    .load(post.getUrlIndustry())
                    .into(h.industry);
            h.dots.setVisibility(GONE);

            ViewGroup.LayoutParams params = h.cardPost.getLayoutParams();
            params.height = (int) (300 * h.itemView.getContext().getResources().getDisplayMetrics().density);
            h.cardPost.setLayoutParams(params);
            h.cardPost.setOnClickListener(v -> onClickPost.accept(post));
        }
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public static class PostHolder extends RecyclerView.ViewHolder{
        TextView title, price, quantity;
        ImageView image;
        ShapeableImageView industry;
        CardView cardPost;

        public PostHolder(@NonNull View itemView) {
            super(itemView);
            this.title = itemView.findViewById(R.id.tx_title_post);
            this.price = itemView.findViewById(R.id.tx_preco_post);
            this.quantity = itemView.findViewById(R.id.tx_qntd_post);
            this.image = itemView.findViewById(R.id.image_post);
            this.industry = itemView.findViewById(R.id.image_industry_post);
            this.cardPost = itemView.findViewById(R.id.card_post);
        }
    }
}
