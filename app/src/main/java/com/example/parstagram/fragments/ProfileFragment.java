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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.parstagram.EndlessRecyclerViewScrollListener;
import com.example.parstagram.Post;
import com.example.parstagram.PostGridAdapter;

import com.example.parstagram.databinding.FragmentProfileBinding;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding profileBinding;
    private PostGridAdapter adapter;
    private List<Post> allPosts;
    private GridLayoutManager layoutManager;
    private EndlessRecyclerViewScrollListener scrollListener;
    private ParseUser user;

    private static final String TAG = "ProfileFragment";

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param user
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(ParseUser user) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putParcelable("User", user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            this.user = getArguments().getParcelable("User");
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        profileBinding = FragmentProfileBinding.inflate(LayoutInflater.from(getContext()), container, false);

        return profileBinding.getRoot();
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // set up layout
        allPosts = new ArrayList<>();

        adapter = new PostGridAdapter(getContext(), allPosts);
        profileBinding.rvPosts.setAdapter(adapter);

        layoutManager = new GridLayoutManager(getContext(), 3);
        profileBinding.rvPosts.setLayoutManager(layoutManager);

        profileBinding.swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshTimeline();
            }
        });
        // Configure the refreshing colors
        profileBinding.swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                queryPosts(page, user, adapter);
            }
        };
        profileBinding.rvPosts.addOnScrollListener(scrollListener);

        //populate data into view
        ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);
        query.whereEqualTo("objectId", user.getObjectId());
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                ParseFile profile = objects.get(0).getParseFile("profilePhoto");
                if (profile != null) {
                    Glide.with(getContext()).load(profile.getUrl()).transform(new RoundedCorners(150)).into(profileBinding.ivProfile);
                }
            }
        });

        profileBinding.tvUsername.setText(user.getUsername());

        // fill in all the posts
        queryPosts(0, user, adapter);
    }


    private void refreshTimeline() {
        adapter.clear();
        queryPosts(0, user, adapter);
        profileBinding.swipeContainer.setRefreshing(false);
    }

    public static void queryPosts(int page, ParseUser user, final PostGridAdapter adapter) {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.whereEqualTo(Post.KEY_USER, user);
        query.setLimit(20);
        query.setSkip(page * 20);
        query.addDescendingOrder(Post.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "issue with getting posts", e);
                    return;
                }

                adapter.addAll(posts);
            }
        });
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        profileBinding = null;
    }
}
