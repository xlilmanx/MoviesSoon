package com.l.moviessoon;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashSet;
import java.util.Set;

public class FragmentDetailedMovieInfo extends Fragment {

    View rootView;
    Set<String> favorites;
    Boolean isFavorite = false;
    TextView favoritestext;
    ImageView favoritesimage;


    public FragmentDetailedMovieInfo() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_detailed_movie_info, container, false);

        final SharedPreferences prefs = getActivity().getSharedPreferences("com.l.moviessoon", 0);
        favorites = prefs.getStringSet("favorites", new HashSet<String>());


        final LinearLayout container_favorites = rootView.findViewById(R.id.container_favorites);
        favoritestext = rootView.findViewById(R.id.addfavorites);
        favoritesimage = rootView.findViewById(R.id.favorite_button);

        container_favorites.setOnClickListener(new View.OnClickListener() {

                                                   @Override
                                                   public void onClick(View v) {
                                                       favorites = prefs.getStringSet("favorites", new HashSet<String>());
                                                       Set<String> updatefavorites = new HashSet<>(favorites);
                                                       int id = getArguments().getInt("id");

                                                       if (isFavorite) {
                                                           updatefavorites.remove(Integer.toString(id));
                                                           favoritestext.setText("Add to Favorites");
                                                           favoritestext.setTextColor(Color.WHITE);
                                                           favoritesimage.setColorFilter(Color.WHITE);
                                                           isFavorite = false;
                                                       } else {
                                                           updatefavorites.add(Integer.toString(id));
                                                           favoritestext.setText("Remove from Favorites");
                                                           favoritestext.setTextColor(Color.RED);
                                                           favoritesimage.setColorFilter(Color.RED);
                                                           isFavorite = true;
                                                       }

                                                       prefs.edit().putStringSet("favorites", updatefavorites).apply();

                                                   }
                                               }
        );

        updateMovieDetails();


        return rootView;
    }


    public void updateMovieDetails() {

        int id = getArguments().getInt("id");
        new RetrieveMovieDetails(getActivity(), rootView).execute(Integer.toString(id));

        for (String favorites : favorites) {
            if (favorites.equals(Integer.toString(id))) {
                isFavorite = true;
                break;
            }
        }

        favoritestext = rootView.findViewById(R.id.addfavorites);
        favoritesimage = rootView.findViewById(R.id.favorite_button);
        if (isFavorite) {
            favoritestext.setText("Remove from Favorites");
            favoritestext.setTextColor(Color.RED);
            favoritesimage.setColorFilter(Color.RED);
        } else {
            favoritestext.setText("Add to Favorites");
            favoritestext.setTextColor(Color.WHITE);
            favoritesimage.setColorFilter(Color.WHITE);
        }

    }

}
