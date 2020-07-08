package com.example.parstagram.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.parstagram.Post;
import com.example.parstagram.PostsAdapter;
import com.example.parstagram.databinding.FragmentPostsBinding;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;


public class PostsFragment extends Fragment {


    private static final String TAG = "PostFragment";
    private FragmentPostsBinding postsBinding;
    protected PostsAdapter adapter;
    protected List<Post> allPosts;


    public PostsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        allPosts = new ArrayList<>();
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        adapter = new PostsAdapter(getContext(), allPosts);


        // Inflate the layout for this fragment
        postsBinding = FragmentPostsBinding.inflate(inflater, container, false);
        View view = postsBinding.getRoot();

        postsBinding.rvPosts.setAdapter(adapter);
        postsBinding.rvPosts.setLayoutManager(llm);
        //postsBinding.tvTest.setText("New Text");
        queryPosts();

        return view;

        //return inflater.inflate(R.layout.fragment_post, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    protected void queryPosts() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.setLimit(20);
        query.addDescendingOrder(Post.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "issue with getting posts", e);
                    return;
                }

                for (Post p : posts) {
                    // to be filled
                    Log.i(TAG, p.getDescription());
                }

                //postsBinding.rvPosts.setAdapter(adapter);
                allPosts.addAll(posts);
                adapter.notifyDataSetChanged();
            }
        });
    }
}