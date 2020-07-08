package com.example.parstagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.parstagram.databinding.ActivityMainBinding;
import com.example.parstagram.fragments.ComposeFragment;
import com.example.parstagram.fragments.PostsFragment;
import com.example.parstagram.fragments.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private ActivityMainBinding mainBinding;
    private final FragmentManager fragmentManager = getSupportFragmentManager();

    private Menu menu;

    private MenuItem action_compose;
    private MenuItem action_home;
    private MenuItem action_profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());

        ActionBar ab = getSupportActionBar();
        ab.setDisplayShowTitleEnabled(false);
        ab.setDisplayUseLogoEnabled(true);
        ab.setDisplayShowHomeEnabled(true);
        ab.setIcon(R.drawable.padded_white_logo);

        menu = mainBinding.bottomNavigation.getMenu();
        action_compose = menu.findItem(R.id.action_compose);
        action_home = menu.findItem(R.id.action_home);
        action_profile = menu.findItem(R.id.action_profile);

        mainBinding.bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment fragment;
                resetIcons();
                switch (menuItem.getItemId()) {
                    case R.id.action_compose:
                        action_compose.setIcon(R.drawable.instagram_new_post_filled_24);
                        fragment = new ComposeFragment();
                        break;
                    case R.id.action_profile:
                        action_profile.setIcon(R.drawable.instagram_user_filled_24);
                        fragment = new ProfileFragment();
                        break;
                    case R.id.action_home:
                    default:
                        action_home.setIcon(R.drawable.instagram_home_filled_24);
                        fragment = new PostsFragment();
                        break;
                }
                fragmentManager.beginTransaction().replace(mainBinding.flContainer.getId(), fragment, "RANDOMTAG").commit();
                return true;
            }
        });

        mainBinding.bottomNavigation.setSelectedItemId(R.id.action_home);
    }

    private void resetIcons() {
        action_profile.setIcon(getDrawable(R.drawable.instagram_user_outline_24));
        action_compose.setIcon(R.drawable.instagram_new_post_outline_24);
        action_home.setIcon(R.drawable.instagram_home_outline_24);

    }
}