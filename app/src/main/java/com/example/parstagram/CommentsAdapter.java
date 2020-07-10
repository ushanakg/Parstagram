package com.example.parstagram;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parstagram.databinding.ItemCommentBinding;

import java.util.ArrayList;
import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {

    private ItemCommentBinding binding;
    private ArrayList<String> allComments;
    private Context context;

    public CommentsAdapter(Context context, ArrayList<String> comments) {
        this.context = context;
        this.allComments = comments;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ItemCommentBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public int getItemCount() {
        Log.i("CommentsAdapter", "size: " + allComments.size());
        return allComments.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String comment = allComments.get(position);
        holder.bind(comment);
    }

    // Clean all elements of the recycler
    public void clear() {
        allComments.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<String> list) {
        allComments.addAll(list);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ItemCommentBinding commentBinding;

        public ViewHolder(@NonNull ItemCommentBinding binding) {
            super(binding.getRoot());
            this.commentBinding = binding;
        }

        public void bind(String comment) {
            Log.i("CommentsAdapter", "Comment bound to viewholder");
            commentBinding.tvComment.setText(comment);
        }
    }
}
