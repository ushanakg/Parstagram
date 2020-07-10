package com.example.parstagram.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.parstagram.EndlessRecyclerViewScrollListener;
import com.example.parstagram.GridActivity;
import com.example.parstagram.Post;
import com.example.parstagram.PostGridAdapter;
import com.example.parstagram.databinding.ActivityGridBinding;

import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    private ActivityGridBinding gridBinding;
    private PostGridAdapter adapter;
    private List<Post> allPosts;
    private GridLayoutManager layoutManager;
    private EndlessRecyclerViewScrollListener scrollListener;
    private ParseUser user;

    private static final String TAG = "ProfileFragment";

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        gridBinding = ActivityGridBinding.inflate(LayoutInflater.from(getContext()), container, false);

        return gridBinding.getRoot();
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // set up layout
        user = ParseUser.getCurrentUser();

        allPosts = new ArrayList<>();

        adapter = new PostGridAdapter(getContext(), allPosts);
        gridBinding.rvPosts.setAdapter(adapter);

        layoutManager = new GridLayoutManager(getContext(), 3);
        gridBinding.rvPosts.setLayoutManager(layoutManager);

        gridBinding.swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshTimeline();
            }
        });
        // Configure the refreshing colors
        gridBinding.swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                GridActivity.queryPosts(page, user, adapter);
            }
        };
        gridBinding.rvPosts.addOnScrollListener(scrollListener);

        //populate data into view
        /*ParseFile profile = postClicked.getProfile();
        if (profile != null) {
            Glide.with(this).load(profile.getUrl()).into(gridBinding.ivProfile);
        }*/
        gridBinding.tvUsername.setText(user.getUsername());

        // fill in all the posts
        GridActivity.queryPosts(0, user, adapter);
    }


    private void refreshTimeline() {
        adapter.clear();
        GridActivity.queryPosts(0, user, adapter);
        gridBinding.swipeContainer.setRefreshing(false);
    }
}
