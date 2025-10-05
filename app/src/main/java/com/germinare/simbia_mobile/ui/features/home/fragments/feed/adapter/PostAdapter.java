package com.germinare.simbia_mobile.ui.features.home.fragments.feed.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.germinare.simbia_mobile.R;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostHolder> {

    private final List<Post> posts;

    public PostAdapter(List<Post> posts){
        this.posts = posts;
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
        Log.d("teste", post.getTittle());

        holder.tittle.setText(post.getTittle());
        holder.price.setText("R$ "+ post.getPrice());
        holder.quantity.setText(post.getQuantity());

        Glide.with(holder.image.getContext())
                .load(post.getUrlImage())
                .into(holder.image);

        Glide.with(holder.industry.getContext())
                .load(post.getUrlIndustry())
                .into(holder.industry);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public static class PostHolder extends RecyclerView.ViewHolder{

        TextView tittle, price, quantity;
        ImageView image;
        ShapeableImageView industry;

        public PostHolder(@NonNull View itemView) {
            super(itemView);
            this.tittle = itemView.findViewById(R.id.tx_tittle_post);
            this.price = itemView.findViewById(R.id.tx_preco_post);
            this.quantity = itemView.findViewById(R.id.tx_qntd_post);
            this.image = itemView.findViewById(R.id.image_post);
            this.industry = itemView.findViewById(R.id.image_industry_post);
        }
    }
}
