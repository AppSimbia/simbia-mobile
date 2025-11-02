package com.germinare.simbia_mobile.ui.features.profile.fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.germinare.simbia_mobile.R;
import com.germinare.simbia_mobile.ui.features.home.fragments.feed.adapter.Post;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

public class PostProfileAdapter extends RecyclerView.Adapter<PostProfileAdapter.PostViewHolder> {

    private List<Post> posts;

    public PostProfileAdapter(List<Post> posts) {
        this.posts = posts;
    }

    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Usar o novo layout com image_industry_post
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = posts.get(position);

        // Imagem principal do post
        Glide.with(holder.imagePost.getContext())
                .load(post.getUrlImage())
                .placeholder(R.drawable.photo_default)
                .into(holder.imagePost);

        holder.txTitlePost.setText(post.getTitle());

        if (post.getPrice() != null) {
            holder.txPrecoPost.setText("R$ " + String.format("%.2f", post.getPrice()));
        } else {
            holder.txPrecoPost.setText("R$ 0,00");
        }

        if (post.getQuantity() != null && post.getMeasureUnit() != null) {
            String[] units = {"Kg", "Litro", "Metro", "Unidade"};
            int unitIndex = post.getMeasureUnit() - 1;
            if (unitIndex >= 0 && unitIndex < units.length) {
                holder.txQntdPost.setText(post.getQuantity() + " " + units[unitIndex]);
            } else {
                holder.txQntdPost.setText(post.getQuantity().toString());
            }
        } else {
            holder.txQntdPost.setText("-");
        }

        if (holder.imageIndustryPost != null) {
            if (post.getUrlIndustry() != null && !post.getUrlIndustry().isEmpty()) {
                Glide.with(holder.imageIndustryPost.getContext())
                        .load(post.getUrlIndustry())
                        .placeholder(R.drawable.photo_default)
                        .into(holder.imageIndustryPost);
            } else {
                holder.imageIndustryPost.setImageResource(R.drawable.photo_default);
            }
        }
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public void updatePosts(List<Post> newPosts) {
        this.posts.clear();
        this.posts.addAll(newPosts);
        notifyDataSetChanged();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView imagePost;
        TextView txTitlePost, txPrecoPost, txQntdPost;
        ShapeableImageView imageIndustryPost;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            imagePost = itemView.findViewById(R.id.image_post);
            txTitlePost = itemView.findViewById(R.id.tx_title_post);
            txPrecoPost = itemView.findViewById(R.id.tx_preco_post);
            txQntdPost = itemView.findViewById(R.id.tx_qntd_post);
            imageIndustryPost = itemView.findViewById(R.id.image_industry_post);
        }
    }
}
