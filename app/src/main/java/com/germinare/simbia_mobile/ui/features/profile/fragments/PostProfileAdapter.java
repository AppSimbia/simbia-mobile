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
import com.google.android.material.imageview.ShapeableImageView; // Adicionado para ShapeableImageView

import java.util.List;

public class PostProfileAdapter extends RecyclerView.Adapter<PostProfileAdapter.PostViewHolder> {

    private List<Post> posts;

    public PostProfileAdapter(List<Post> posts) {
        this.posts = posts;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 🎯 CORREÇÃO 1: O layout deve ser 'item_profile_post' (ou o nome que você deu ao XML do item).
        // Assumindo que você usa 'R.layout.item_profile_post' para o XML que você enviou:
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_layout, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = posts.get(position);

        // 🎯 CORREÇÃO 2: Usa 'holder.imagePost' (variável mapeada corretamente no ViewHolder).
        // Carrega a imagem principal do post
        if (post.getUrlImage() != null && !post.getUrlImage().isEmpty()) {
            Glide.with(holder.imagePost.getContext())
                    .load(post.getUrlImage())
                    .placeholder(R.drawable.photo_default)
                    .into(holder.imagePost);
        } else {
            holder.imagePost.setImageResource(R.drawable.photo_default);
        }

        // Preenche os outros campos conforme seus IDs no XML
        holder.txTitlePost.setText(post.getTitle());

        // Se você precisar carregar a imagem da indústria (image_industry_post):
        /* if (post.getUrlIndustryImage() != null && holder.imageIndustryPost != null) {
            Glide.with(holder.imageIndustryPost.getContext())
                .load(post.getUrlIndustryImage())
                .into(holder.imageIndustryPost);
        }
        */
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

        // ❌ Removida: ivPostImage. Você tinha duas variáveis para o mesmo propósito.

        // ✅ Corrigidas (conforme seu XML):
        ImageView imagePost; // Corresponde a R.id.image_post
        TextView txTitlePost; // Corresponde a R.id.tx_title_post
        TextView txPrecoPost; // Corresponde a R.id.tx_preco_post
        TextView txQntdPost; // Corresponde a R.id.tx_qntd_post
        ShapeableImageView imageIndustryPost; // Corresponde a R.id.image_industry_post

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);

            // Mapeamento corrigido dos IDs do XML
            imagePost = itemView.findViewById(R.id.image_post);
            txTitlePost = itemView.findViewById(R.id.tx_title_post);
            txPrecoPost = itemView.findViewById(R.id.tx_preco_post);
            txQntdPost = itemView.findViewById(R.id.tx_qntd_post);

            // Adicionado o mapeamento da ShapeableImageView
            imageIndustryPost = itemView.findViewById(R.id.image_industry_post);
        }
    }
}