package com.example.parstagram;


import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.parstagram.databinding.ItemPostBinding;
import com.example.parstagram.fragments.DetailsFragment;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    private Context context;
    private List<Post> posts;


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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ItemPostBinding holderBinding;

        public ViewHolder(@NonNull ItemPostBinding binding) {
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
            ParseFile profile = post.getUser().getParseFile("profilePhoto");
            if (profile != null) {
                Glide.with(context).load(profile.getUrl()).transform(new RoundedCorners(90)).into(holderBinding.ivProfile);
            } else {
                holderBinding.ivProfile.setImageResource(R.drawable.ic_baseline_person_24);
            }
            holderBinding.tvDescription.setText(post.getDescription());
            holderBinding.tvUsername.setText(post.getUser().getUsername());
            holderBinding.tvRelativeTimeAgo.setText(post.getRelativeTimeAgo());

            if (post.likedBy(ParseUser.getCurrentUser())) {
                holderBinding.ivLike.setImageResource(R.drawable.ufi_heart_active);
                holderBinding.ivLike.setColorFilter(Color.parseColor("#e95950"));
            } else {
                holderBinding.ivLike.setImageResource(R.drawable.ufi_heart);
                holderBinding.ivLike.setColorFilter(Color.parseColor("#000000"));
            }
        }

        @Override
        public void onClick(View view) {
            // open detailed fragment
            FragmentManager fm = ((MainActivity) context).getSupportFragmentManager();
            DetailsFragment fragment = DetailsFragment.newInstance(posts.get(getAdapterPosition()));
            fm.beginTransaction().replace(R.id.flContainer, fragment, "DetailsFragment").commit();

        }
    }
}
