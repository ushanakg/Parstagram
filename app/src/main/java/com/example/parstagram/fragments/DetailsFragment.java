package com.example.parstagram.fragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.parstagram.CommentsAdapter;
import com.example.parstagram.MainActivity;
import com.example.parstagram.Post;
import com.example.parstagram.R;
import com.example.parstagram.databinding.ActivityMainBinding;
import com.example.parstagram.databinding.FragmentDetailsBinding;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailsFragment extends Fragment {

    private static final String TAG = "DetailsFragment";
    private FragmentDetailsBinding detailsBinding;
    private Post post;
    private CommentsAdapter adapter;
    private ArrayList<String> comments;

    public DetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param post
     * @return A new instance of fragment DetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DetailsFragment newInstance(Post post) {
        DetailsFragment fragment = new DetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable("Post", post);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.post = getArguments().getParcelable("Post");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        detailsBinding = FragmentDetailsBinding.inflate(inflater, container, false);
        return detailsBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        comments =  post.getComments();

        Log.i(TAG, "Comments: " + comments.size());

        adapter = new CommentsAdapter(getContext(), comments);
        detailsBinding.rvComments.setAdapter(adapter);
        detailsBinding.rvComments.setLayoutManager(new LinearLayoutManager(getContext()));

        detailsBinding.rvComments.setNestedScrollingEnabled(false);

        detailsBinding.swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshComments();
            }
        });
        // Configure the refreshing colors
        detailsBinding.swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        bindData();

        // how to open fragment from fragment
        detailsBinding.llUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // open profile fragment
                if (post.getUser().getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
                    ((MainActivity) getActivity()).bnm.setSelectedItemId(R.id.action_profile);
                } else {
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    ProfileFragment fragment = ProfileFragment.newInstance(post.getUser());
                    fm.beginTransaction().replace(R.id.flContainer, fragment, "ProfileFragment").commit();
                }
            }
        });

        detailsBinding.ivLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final boolean liked = post.toggleLike(ParseUser.getCurrentUser());
                post.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Log.e(TAG, "like did not succeed", e);
                        } else {
                            fillLikeView(liked);
                        }
                    }
                });
            }
        });

        detailsBinding.btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comment = detailsBinding.etComment.getText().toString();
                post.addComment(ParseUser.getCurrentUser(), comment);
                post.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Log.e(TAG, "Issue with sending comment", e);
                        } else {
                            detailsBinding.etComment.setText("");
                            Toast.makeText(getContext(), "Sent!", Toast.LENGTH_SHORT).show();
                            refreshComments();
                        }
                    }
                });
            }
        });
    }

    private void bindData() {
        ParseFile image = post.getImage();
        if (image != null) {
            Glide.with(this).load(image.getUrl()).into(detailsBinding.ivPhoto);
        }
        ParseFile profile = post.getUser().getParseFile("profilePhoto");
        if (profile != null) {
            Glide.with(this).load(profile.getUrl()).transform(new RoundedCorners(90)).into(detailsBinding.ivProfile);
        }
        detailsBinding.tvDescription.setText(post.getDescription());
        detailsBinding.tvUsername.setText(post.getUser().getUsername());
        detailsBinding.tvTimestamp.setText(post.getDatePosted() + " at " + post.getTimePosted());

        fillLikeView(post.likedBy(ParseUser.getCurrentUser()));
        detailsBinding.tvLikedBy.setText("Liked by " + post.getNumLikes());
    }

    private void refreshComments() {
        adapter.clear();
        adapter.addAll(post.getComments());
        detailsBinding.swipeContainer.setRefreshing(false);
    }

    private void fillLikeView(boolean liked) {
        if (liked) {
            detailsBinding.ivLike.setImageResource(R.drawable.ufi_heart_active);
            detailsBinding.ivLike.setColorFilter(Color.parseColor("#e95950"));
        } else {
            detailsBinding.ivLike.setImageResource(R.drawable.ufi_heart);
            detailsBinding.ivLike.setColorFilter(Color.parseColor("#000000"));
        }
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        detailsBinding = null;
    }
}