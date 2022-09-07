package com.l.moviessoon;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class FragmentFavorites extends Fragment {

    View rootView;
    ListView listview;
    ListViewAdapter adapter;
    View footerView;
    ArrayList<AsyncTask> AsyncTaskList = new ArrayList<>();

    public FragmentFavorites() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_favorites, container, false);
        final Context context = getActivity();

        listview = rootView.findViewById(R.id.lv_favorites);


        adapter = new ListViewAdapter(context, new ArrayList<MovieInfo>());
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                MovieInfo MovieInfo = adapter.getItem(i);
                MainActivity MainActivity = (MainActivity) context;
                MainActivity.showDetailedFragment(MovieInfo.getId());
                MainActivity.searchView.clearFocus();
                MainActivity.searchView.onActionViewCollapsed();
                MainActivity.title_text.setVisibility(View.VISIBLE);

            }
        });

        UpdateFavorites();

        return rootView;
    }

    public void UpdateFavorites() {

        SharedPreferences prefs = getActivity().getSharedPreferences("com.l.moviessoon", 0);
        Set<String> set = prefs.getStringSet("favorites", new HashSet<String>());
        AsyncTaskList = new ArrayList<>();

        adapter.clearMovieList();
        adapter.notifyDataSetChanged();

        if (!set.isEmpty()) {

            rootView.findViewById(R.id.favorites_error).setVisibility(View.GONE);

            for (String s : set) {
                new RetrieveFavoritesList(getActivity(), rootView).execute(s);
            }

        } else {

            rootView.findViewById(R.id.favorites_error).setVisibility(View.VISIBLE);

        }

    }

    public void UpdateListView(MovieInfo movieInfo) {

        adapter.addItemList(movieInfo);
        adapter.notifyDataSetChanged();

        Boolean Loading = false;

        for (int i = 0; i < AsyncTaskList.size(); i++) {
            if (AsyncTaskList.get(i).getStatus() != AsyncTask.Status.FINISHED) {

                Loading = true;
                break;

            }
        }

        if (!Loading) {
            rootView.findViewById(R.id.progressBar).setVisibility(View.GONE);
        }

    }

    public void StartRetrieveTask() {

        rootView.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);

    }

}
