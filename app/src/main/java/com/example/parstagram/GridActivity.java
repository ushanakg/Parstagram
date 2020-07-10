package com.example.parstagram;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.parstagram.databinding.ActivityGridBinding;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class GridActivity extends AppCompatActivity {

    private static final String TAG = "GridActivity";
    private ActivityGridBinding gridBinding;
    private PostGridAdapter adapter;
    private List<Post> allPosts;
    private GridLayoutManager layoutManager;
    private EndlessRecyclerViewScrollListener scrollListener;
    private ParseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gridBinding = ActivityGridBinding.inflate(getLayoutInflater());
        setContentView(gridBinding.getRoot());

        // set up layout
        Post p = getIntent().getParcelableExtra(PostsAdapter.ViewHolder.POST_DETAILS_KEY);
        user = p.getUser();

        allPosts = new ArrayList<>();

        adapter = new PostGridAdapter(this, allPosts);
        gridBinding.rvPosts.setAdapter(adapter);

        layoutManager = new GridLayoutManager(this, 3);
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
                queryPosts(page, user, adapter);
            }
        };
        gridBinding.rvPosts.addOnScrollListener(scrollListener);

        //populate data into view
        Glide.with(this).clear(gridBinding.ivProfile);
        ParseFile profile = user.getParseFile("profilePhoto");
        if (profile != null) {
            Glide.with(this).load(profile.getUrl()).transform(new RoundedCorners(150)).into(gridBinding.ivProfile);
        }
        gridBinding.tvUsername.setText(user.getUsername());

        // fill in all the posts
        queryPosts(0, user, adapter);
    }




    private void refreshTimeline() {
        adapter.clear();
        queryPosts(0, user, adapter);
        gridBinding.swipeContainer.setRefreshing(false);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the main_menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case R.id.logout:
                ParseUser.logOut();

                // direct user back to login screen
                Intent i = new Intent(this, LoginActivity.class);
                startActivity(i);
                finish();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }
}