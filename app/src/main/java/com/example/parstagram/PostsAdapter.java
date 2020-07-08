package com.example.parstagram;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.parstagram.databinding.FragmentPostBinding;
import com.example.parstagram.databinding.ItemPostBinding;
import com.parse.ParseFile;

import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    private Context context;
    List<Post> posts;


    public PostsAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPostBinding binding = ItemPostBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post p = posts.get(position);
        holder.bind(p);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ItemPostBinding holderBinding;

        public ViewHolder(@NonNull ItemPostBinding binding) {
            super(binding.getRoot());

            holderBinding = binding;
        }

        public void bind(Post post) {
            ParseFile image = post.getImage();
            if (image != null) {
                Glide.with(context).load(image.getUrl()).into(holderBinding.ivPhoto);
            }

            holderBinding.tvDescription.setText(post.getDescription());
            holderBinding.tvUsername.setText(post.getUser().getUsername());

        }
    }
}
