package com.example.parstagram;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.parstagram.databinding.ActivityPostDetailsBinding;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class PostDetailsActivity extends AppCompatActivity {

    private static final String TAG = "PostDetailsActivity";
    ActivityPostDetailsBinding detailsBinding;
    Post post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        detailsBinding = ActivityPostDetailsBinding.inflate(getLayoutInflater());
        setContentView(detailsBinding.getRoot());

        post = getIntent().getParcelableExtra(PostsAdapter.ViewHolder.POST_DETAILS_KEY);


        ActionBar ab = getSupportActionBar();
        ab.setDisplayShowTitleEnabled(false);
        ab.setDisplayUseLogoEnabled(true);
        ab.setDisplayShowHomeEnabled(true);
        ab.setIcon(R.drawable.padded_white_logo);

        ParseFile image = post.getImage();
        if (image != null) {
            Glide.with(this).load(image.getUrl()).into(detailsBinding.ivPhoto);
        }

        detailsBinding.tvDescription.setText(post.getDescription());
        detailsBinding.tvUsername.setText(post.getUser().getUsername());
        detailsBinding.tvTimestamp.setText(post.getDatePosted() + " at " + post.getTimePosted());

        detailsBinding.llUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(PostDetailsActivity.this, GridActivity.class);
                i.putExtra(PostsAdapter.ViewHolder.POST_DETAILS_KEY, post);
                startActivity(i);
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