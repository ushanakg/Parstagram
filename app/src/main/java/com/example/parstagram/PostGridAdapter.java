package com.example.parstagram;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.parstagram.databinding.ItemPostBinding;
import com.example.parstagram.databinding.ItemPostGridBinding;
import com.parse.ParseFile;

import java.util.List;

public class PostGridAdapter extends RecyclerView.Adapter<PostGridAdapter.GridViewHolder> {

    private Context context;
    private List<Post> posts;


    public PostGridAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;

    }

    @NonNull
    @Override
    public GridViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPostGridBinding holderBinding = ItemPostGridBinding.inflate(LayoutInflater.from(context), parent, false);
        return new GridViewHolder(holderBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull GridViewHolder holder, int position) {
        Post p = posts.get(position);
        holder.bind(p);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }


    // Clean all elements of the recycler
    public void clear() {
        posts.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Post> list) {
        posts.addAll(list);
        notifyDataSetChanged();
    }

    public class GridViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public static final String POST_DETAILS_KEY = "post";
        ItemPostGridBinding holderBinding;

        public GridViewHolder(@NonNull ItemPostGridBinding binding) {
            super(binding.getRoot());

            holderBinding = binding;
            binding.getRoot().setOnClickListener(this);
        }

        public void bind(Post post) {
            Glide.with(context).clear(holderBinding.ivPhoto);

            ParseFile image = post.getImage();
            if (image != null) {
                Glide.with(context).load(image.getUrl()).into(holderBinding.ivPhoto);
            }

        }

        @Override
        public void onClick(View view) {
            // open detailed post activity
            Intent i = new Intent(context, PostDetailsActivity.class);
            i.putExtra(POST_DETAILS_KEY, posts.get(getAdapterPosition()));
            context.startActivity(i);
        }
    }
}
