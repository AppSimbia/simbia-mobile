package com.germinare.simbia_mobile.ui.features.home.fragments.feed.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.germinare.simbia_mobile.R;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostHolder> {

    private final List<Post> posts;
    private final Consumer<Post> onClickPost;

    public PostAdapter(List<Post> posts, Consumer<Post> onClickPost){
        this.posts = posts;
        this.onClickPost = onClickPost;
    }

    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.post_layout,
                parent,
                false
        );
        return new PostHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostHolder holder, int position) {
        Post post = posts.get(position);

        holder.title.setText(post.getTitle());
        holder.price.setText("R$ "+ post.getPrice());
        holder.quantity.setText(post.getQuantity());

        Glide.with(holder.image.getContext())
                .load(post.getUrlImage())
                .into(holder.image);

        Glide.with(holder.industry.getContext())
                .load(post.getUrlIndustry())
                .into(holder.industry);

        holder.cardPost.setOnClickListener(V -> onClickPost.accept(post));
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
