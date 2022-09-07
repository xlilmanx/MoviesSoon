package com.l.moviessoon;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class FragmentInTheaters extends Fragment {

    View rootView;
    ListView listview;
    ListViewAdapter adapter;
    View headerView;
    View footerView;
    int current_page = 1;
    int total_pages = 1;
    String currentDate;
    String endDate;
    Boolean initial;
    Timer timer;
    Boolean timer_run = false;
    String release_type = "";
    String sort_by = "";

    public FragmentInTheaters() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        initial = getArguments().getBoolean("initial");
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_coming_soon, container, false);
        final Context context = getActivity();

        listview = rootView.findViewById(R.id.lv_coming_soon);

        headerView = inflater.inflate(R.layout.lv_header, null);
        listview.addHeaderView(headerView);

        Spinner spinner_release_type = headerView.findViewById(R.id.spinner_release_type);
        Spinner spinner_sort_by = headerView.findViewById(R.id.spinner_sort_by);

        release_type = "3|2";
        sort_by = "popularity.desc";

        spinner_release_type.setSelection(0);
        spinner_sort_by.setSelection(0);

        spinner_release_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                switch (i) {
                    case 0:
                        release_type = "3|2";
                        break;
                    case 1:
                        release_type = "3";
                        break;
                    case 2:
                        release_type = "2";
                        break;
                }

                if (!initial) {
                    initial = true;
                    current_page = 1;
                    adapter.clearMovieList();
                    adapter.notifyDataSetChanged();
                    new RetrieveMovieList(getActivity(), rootView, current_page, "in_theaters").execute("discover/movie", "&primary_release_date.lte=" + currentDate + "&primary_release_date.gte=" + endDate + "&with_release_type=" + release_type + "&sort_by=" + sort_by);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinner_sort_by.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                switch (i) {
                    case 0:
                        sort_by = "popularity.desc";
                        break;
                    case 1:
                        sort_by = "popularity.asc";
                        break;
                    case 2:
                        sort_by = "primary_release_date.desc";
                        break;
                    case 3:
                        sort_by = "primary_release_date.asc";
                        break;
                    case 4:
                        sort_by = "vote_average.desc";
                        break;
                    case 5:
                        sort_by = "vote_average.asc";
                        break;
                }
                if (!initial) {
                    initial = true;
                    current_page = 1;
                    adapter.clearMovieList();
                    adapter.notifyDataSetChanged();
                    new RetrieveMovieList(getActivity(), rootView, current_page, "in_theaters").execute("discover/movie", "&primary_release_date.lte=" + currentDate + "&primary_release_date.gte=" + endDate + "&with_release_type=" + release_type + "&sort_by=" + sort_by);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        footerView = inflater.inflate(R.layout.lv_footer, null);
        listview.addFooterView(footerView);
        footerView.setVisibility(View.GONE);

        adapter = new ListViewAdapter(context, new ArrayList<MovieInfo>());
        listview.setAdapter(adapter);

        timer = new Timer();

        listview.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {

                if (current_page < total_pages && !initial && !timer_run) {

                    timer_run = true;
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            timer_run = false;
                        }
                    }, 1000);

                    current_page += 1;

                    //new RetrieveMovieList(getActivity(), rootView, current_page, "in_theaters").execute("movie/now_playing", "");
                    new RetrieveMovieList(getActivity(), rootView, current_page, "in_theaters").execute("discover/movie", "&primary_release_date.lte=" + currentDate + "&primary_release_date.gte=" + endDate + "&with_release_type=" + release_type + "&sort_by=" + sort_by);

                    return true;

                } else {

                    return false;

                }
            }
        });

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                MovieInfo MovieInfo = adapter.getItem(i - 1);
                MainActivity MainActivity = (MainActivity) context;
                MainActivity.showDetailedFragment(MovieInfo.getId());
                MainActivity.searchView.clearFocus();
                MainActivity.searchView.onActionViewCollapsed();
                MainActivity.title_text.setVisibility(View.VISIBLE);

            }
        });

        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        currentDate = sdf.format(currentTime);

        Calendar c = Calendar.getInstance();
        c.setTime(currentTime);
        c.add(Calendar.MONTH, -2);

        endDate = sdf.format(c.getTime());

        current_page = 1;

        new RetrieveMovieList(getActivity(), rootView, current_page, "in_theaters").execute("discover/movie", "&primary_release_date.lte=" + currentDate + "&primary_release_date.gte=" + endDate + "&with_release_type=" + release_type + "&sort_by=" + sort_by);

        return rootView;
    }

    public void UpdateListView(List<MovieInfo> movieList, int pages) {

        adapter.setItemList(movieList);
        adapter.notifyDataSetChanged();

        footerView.setVisibility(View.GONE);
        rootView.findViewById(R.id.progressBar).setVisibility(View.GONE);

        total_pages = pages;

        initial = false;

    }

    public void StartRetrieveTask() {

        if (current_page == 1) {
            rootView.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        } else {
            footerView.setVisibility(View.VISIBLE);
        }

    }

}
