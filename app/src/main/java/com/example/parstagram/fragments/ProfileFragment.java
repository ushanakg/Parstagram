package com.example.parstagram.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

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
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding profileBinding;
    private PostGridAdapter adapter;
    private List<Post> allPosts;
    private GridLayoutManager layoutManager;
    private EndlessRecyclerViewScrollListener scrollListener;
    private ParseUser user;

    private final static int PICK_PHOTO_CODE = 2703;
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

        if (user.getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {

            profileBinding.ivProfile.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent event) {
                    if(event.getAction() == MotionEvent.ACTION_UP) {
                        // release
                        profileBinding.ivProfile.clearColorFilter();
                        onPickPhoto(view);
                        return false;

                    } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        // pressed
                        profileBinding.ivProfile.setColorFilter(Color.parseColor("#fccc63"));
                        return true;
                    }

                    return false;
                }
            });
        }

        loadProfilePic();
        profileBinding.tvUsername.setText(user.getUsername());


        profileBinding.pbLoading.setVisibility(ProgressBar.VISIBLE);
        // fill in all the posts
        queryPosts(0, user, adapter);
    }

    private void loadProfilePic() {
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
    }

    // Trigger gallery selection for a photo
    public void onPickPhoto(View view) {
        // Create intent for picking a photo from the gallery
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            // Bring up gallery to select a photo
            startActivityForResult(intent, PICK_PHOTO_CODE);
        }
    }

    public Bitmap loadFromUri(Uri photoUri) {
        Bitmap image = null;
        try {
            // check version of Android on device
            if(Build.VERSION.SDK_INT > 27){
                // on newer versions of Android, use the new decodeBitmap method
                ImageDecoder.Source source = ImageDecoder.createSource(getContext().getContentResolver(), photoUri);
                image = ImageDecoder.decodeBitmap(source);
            } else {
                // support older versions of Android by using getBitmap
                image = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), photoUri);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((data != null) && requestCode == PICK_PHOTO_CODE) {
            Uri photoUri = data.getData();

            // Load the image located at photoUri into selectedImage
            Bitmap selectedImage = loadFromUri(photoUri);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            selectedImage.compress(Bitmap.CompressFormat.JPEG, 50, stream);
            byte[] bitmapBytes = stream.toByteArray();

            user.put("profilePhoto", new ParseFile(bitmapBytes));
            user.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Toast.makeText(getContext(), "Updated!", Toast.LENGTH_SHORT).show();
                        loadProfilePic();
                    } else {
                        Log.e(TAG, "updating profile pic failed", e);
                    }
                }
            });
            // Load the selected image into a preview
            loadProfilePic();
        }
    }


    private void refreshTimeline() {
        adapter.clear();
        queryPosts(0, user, adapter);
        profileBinding.swipeContainer.setRefreshing(false);
    }

    public void queryPosts(int page, ParseUser user, final PostGridAdapter adapter) {
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
                profileBinding.pbLoading.setVisibility(ProgressBar.INVISIBLE);
                adapter.addAll(posts);
            }
        });
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        profileBinding = null;
    }
}
