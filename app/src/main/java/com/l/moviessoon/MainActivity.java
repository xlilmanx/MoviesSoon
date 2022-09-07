package com.l.moviessoon;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubePlayer;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    public YouTubePlayer youTubePlayer;
    public boolean isYouTubePlayerFullScreen;
    FragmentComingSoon comingsoonFragment;
    FragmentFavorites favoritesFragment;
    FragmentInTheaters intheatersFragment;
    FragmentDetailedMovieInfo detailedmovieinfoFragment;
    FragmentSearch searchFragment;
    FragmentYoutube youtubeFragment;
    ImageLoader imageLoader;
    int currentid = 0;
    Timer timer;
    BottomNavigationView navigation;
    TextView title_text;
    SearchView searchView;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            if (currentid == 5) {
                getSupportFragmentManager().beginTransaction().remove(youtubeFragment).commit();
            }
            switch (item.getItemId()) {
                case R.id.in_theaters:
                    if (currentid != item.getItemId()) {
                        currentid = item.getItemId();

                        title_text.setText("Now Playing");
                        searchView.clearFocus();
                        searchView.onActionViewCollapsed();
                        title_text.setVisibility(View.VISIBLE);
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("initial", true);
                        intheatersFragment.setArguments(bundle);

                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.frame, intheatersFragment, "INTHEATERS")
                                .addToBackStack(null)
                                .commit();
                    }
                    return true;
                case R.id.coming_soon:
                    if (currentid != item.getItemId()) {
                        currentid = item.getItemId();

                        if (comingsoonFragment == null) {
                            comingsoonFragment = buildFragmentComingSoon();
                        }

                        title_text.setText("Coming Soon");
                        searchView.clearFocus();
                        searchView.onActionViewCollapsed();
                        title_text.setVisibility(View.VISIBLE);
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("initial", true);
                        comingsoonFragment.setArguments(bundle);

                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.frame, comingsoonFragment, "COMINGSOON")
                                .addToBackStack(null)
                                .commit();
                    }
                    return true;
                case R.id.favorites:
                    if (currentid != item.getItemId()) {

                        if (favoritesFragment == null) {
                            favoritesFragment = buildFragmentFavorites();
                        }

                        title_text.setText("Favorites");
                        searchView.clearFocus();
                        searchView.onActionViewCollapsed();
                        title_text.setVisibility(View.VISIBLE);
                        currentid = item.getItemId();
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.frame, favoritesFragment, "FAVORITES")
                                .addToBackStack(null)
                                .commit();
                    }
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                //.diskCacheSize(10 * 1024 * 1024)
                .tasksProcessingOrder(QueueProcessingType.LIFO).build();

        imageLoader = ImageLoader.getInstance();
        imageLoader.init(config);

        intheatersFragment = buildFragmentInTheaters();
        Bundle bundle = new Bundle();
        bundle.putBoolean("initial", true);
        intheatersFragment.setArguments(bundle);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame, intheatersFragment, "INTHEATERS")
                .commit();


        searchView = findViewById(R.id.search_view);

        int searchPlateId = searchView.getContext().getResources().getIdentifier("android:id/search_plate", null, null);
        searchView.findViewById(searchPlateId).setBackgroundResource(android.R.color.transparent);
        /**
         final int searchBarId = searchView.getContext().getResources().getIdentifier("android:id/search_bar", null, null);
         LinearLayout searchBar = searchView.findViewById(searchBarId);
         searchBar.setLayoutTransition(new LayoutTransition());
         */
        title_text = findViewById(R.id.app_title);
        title_text.setText("Now Playing");

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                title_text.setVisibility(View.GONE);
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {

                title_text.setVisibility(View.VISIBLE);

                return false;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (searchView.hasFocus()) {
                    if (searchFragment == null) {
                        searchFragment = buildFragmentSearch();
                    }

                    title_text.setText("Search");
                    Bundle bundle = new Bundle();
                    bundle.putString("search", s);
                    searchFragment.setArguments(bundle);

                    currentid = 4;

                    if (timer != null) {
                        timer.cancel();
                    }
                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {

                            if (getSupportFragmentManager().findFragmentById(R.id.frame) != searchFragment) {

                                getSupportFragmentManager()
                                        .beginTransaction()
                                        .replace(R.id.frame, searchFragment, "SEARCH")
                                        .addToBackStack(null)
                                        .commit();

                            } else {

                                searchFragment.UpdateSearchText();

                            }
                        }
                    }, 500);
                }

                return true;
            }
        });

    }

    public void showDetailedFragment(int id) {

        if (detailedmovieinfoFragment == null) {
            detailedmovieinfoFragment = buildFragmentDetailedMovieInfo();
        }

        Bundle bundle = new Bundle();
        bundle.putInt("id", id);
        detailedmovieinfoFragment.setArguments(bundle);

        currentid = 3;

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame, detailedmovieinfoFragment, "DETAIL")
                .addToBackStack(null)
                .commit();

    }

    public void showYoutube(String id) {

        if (youtubeFragment == null) {
            youtubeFragment = buildFragmentYoutube();
        }

        Bundle bundle = new Bundle();
        bundle.putString("id", id);
        youtubeFragment.setArguments(bundle);

        currentid = 5;

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_youtube, youtubeFragment, "YOUTUBE")
                .commit();

    }

    private FragmentComingSoon buildFragmentComingSoon() {
        return new FragmentComingSoon();
    }

    private FragmentFavorites buildFragmentFavorites() {
        return new FragmentFavorites();
    }

    private FragmentInTheaters buildFragmentInTheaters() {
        return new FragmentInTheaters();
    }

    private FragmentDetailedMovieInfo buildFragmentDetailedMovieInfo() {
        return new FragmentDetailedMovieInfo();
    }

    private FragmentSearch buildFragmentSearch() {
        return new FragmentSearch();
    }

    private FragmentYoutube buildFragmentYoutube() {
        return new FragmentYoutube();
    }

    @Override
    public void onBackPressed() {
        if (youTubePlayer != null && isYouTubePlayerFullScreen) {
            youTubePlayer.setFullscreen(false);
        } else {
            if (currentid == 5) {
                getSupportFragmentManager().beginTransaction().remove(youtubeFragment).commit();
                currentid = 3;
            } else {

                FragmentManager manager = getSupportFragmentManager();
                if (manager.getBackStackEntryCount() > 0) {
                    super.onBackPressed();
                    Fragment currentFragment = manager.findFragmentById(R.id.frame);
                    if (currentFragment instanceof FragmentInTheaters) {
                        title_text.setText("Now Playing");
                        navigation.getMenu().getItem(0).setChecked(true);
                    } else if (currentFragment instanceof FragmentComingSoon) {
                        title_text.setText("Coming Soon");
                        navigation.getMenu().getItem(1).setChecked(true);
                    } else if (currentFragment instanceof FragmentFavorites) {
                        title_text.setText("Favorites");
                        navigation.getMenu().getItem(2).setChecked(true);
                    }

                } else {

                    super.onBackPressed();

                }
            }

        }
    }

}
