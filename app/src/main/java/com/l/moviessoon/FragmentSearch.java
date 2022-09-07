package com.l.moviessoon;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class FragmentSearch extends Fragment {

    View rootView;
    ListView listview;
    ListViewAdapter adapter;
    View footerView;
    int current_page = 1;
    int total_pages = 1;
    String search;

    AsyncTask RetrieveMovieList;

    public FragmentSearch() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_coming_soon, container, false);
        final Context context = getActivity();

        listview = rootView.findViewById(R.id.lv_coming_soon);

        footerView = inflater.inflate(R.layout.lv_footer, null);
        listview.addFooterView(footerView);

        adapter = new ListViewAdapter(context, new ArrayList<MovieInfo>());
        listview.setAdapter(adapter);

        listview.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {

                if (current_page < total_pages) {

                    current_page += 1;

                    RetrieveMovieList = new RetrieveMovieList(getActivity(), rootView, current_page, "search").execute("search/movie", "&query=" + search);

                    return true;

                } else {

                    return false;

                }
            }
        });

        UpdateSearchText();

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                MainActivity MainActivity = (MainActivity) context;
                MainActivity.searchView.clearFocus();
                MainActivity.searchView.onActionViewCollapsed();
                MainActivity.title_text.setVisibility(View.VISIBLE);
                MovieInfo MovieInfo = adapter.getItem(i);
                MainActivity.showDetailedFragment(MovieInfo.getId());


            }
        });

        return rootView;
    }

    public void UpdateListView(List<MovieInfo> movieList, int pages) {

        if (current_page == 1) {


        } else {

            adapter.setItemList(movieList);

        }
        adapter.replaceItemList(movieList);
        adapter.notifyDataSetChanged();

        footerView.setVisibility(View.GONE);

        total_pages = pages;

    }

    public void StartRetrieveTask() {

        footerView.setVisibility(View.VISIBLE);

    }

    public void UpdateSearchText() {

        current_page = 1;
        search = getArguments().getString("search");
        search = search.replaceAll(" ", "%20");

        if (RetrieveMovieList != null) {
            cancelAsyncTask();
        }

        if (search.length() > 0) {
            RetrieveMovieList = new RetrieveMovieList(getActivity(), rootView, current_page, "search").execute("search/movie", "&query=" + search);
        }
    }

    public void cancelAsyncTask() {

        RetrieveMovieList.cancel(true);

    }

}
