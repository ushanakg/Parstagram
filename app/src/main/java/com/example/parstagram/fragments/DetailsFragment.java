package com.example.parstagram.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.parstagram.MainActivity;
import com.example.parstagram.Post;
import com.example.parstagram.R;
import com.example.parstagram.databinding.ActivityMainBinding;
import com.example.parstagram.databinding.FragmentDetailsBinding;
import com.parse.ParseFile;
import com.parse.ParseUser;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailsFragment extends Fragment {

    private static final String TAG = "DetailsFragment";
    private FragmentDetailsBinding detailsBinding;
    private Post post;

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
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        detailsBinding = null;
    }
}