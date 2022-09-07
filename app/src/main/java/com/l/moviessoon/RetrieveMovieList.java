package com.l.moviessoon;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class RetrieveMovieList extends AsyncTask<String, Void, String> {

    private static String API_URL = "https://api.themoviedb.org/3/";
    List<MovieInfo> movieList = new ArrayList();
    private int page = 1;
    private String fragment;
    private WeakReference<Context> mContext;
    private WeakReference<View> mView;

    public RetrieveMovieList(Context context, View view, int page, String fragment) {

        this.mContext = new WeakReference<>(context);
        this.mView = new WeakReference<>(view);
        this.page = page;
        this.fragment = fragment;

    }

    @Override
    protected void onPreExecute() {
        Context contextRef = mContext.get();

        if (contextRef != null) {
            switch (fragment) {
                case "coming_soon":
                    ((MainActivity) contextRef).comingsoonFragment.StartRetrieveTask();
                    break;
                case "in_theaters":
                    ((MainActivity) contextRef).intheatersFragment.StartRetrieveTask();
                    break;
                case "search":
                    ((MainActivity) contextRef).searchFragment.StartRetrieveTask();
                    break;
            }
        }
    }

    @Override
    protected String doInBackground(String... params) {

        try {
            String API_KEY = ((MainActivity) mContext.get()).getString(R.string.api_key);
            URL url = new URL(API_URL + params[0] + "?api_key=" + API_KEY + "&language=en-US&region=US&page=" + page + params[1]);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                bufferedReader.close();
                return stringBuilder.toString();
            } finally {
                urlConnection.disconnect();
            }
        } catch (Exception e) {
            return null;
        }

    }

    @Override
    protected void onPostExecute(String result) {

        if (result != null && !isCancelled()) {

            Context contextRef = mContext.get();

            if (contextRef != null) {

                try {
                    JSONObject object = (JSONObject) new JSONTokener(result).nextValue();
                    JSONArray results = object.getJSONArray("results");
                    String pages_string = object.getString("total_pages");
                    int pages = Integer.parseInt(pages_string);

                    for (int i = 0; i < results.length(); i++) {

                        JSONObject current_result = results.getJSONObject(i);
                        int id = current_result.getInt("id");
                        String poster = current_result.getString("poster_path");
                        String title = current_result.getString("title");
                        String release = current_result.getString("release_date");
                        String overview = current_result.getString("overview");
                        String rating = current_result.getString("vote_average");
                        String rating_count = current_result.getString("vote_count");

                        MovieInfo movieInfo = new MovieInfo(id, poster, title, release, overview, rating, rating_count);

                        movieList.add(movieInfo);

                    }

                    switch (fragment) {
                        case "coming_soon":
                            ((MainActivity) contextRef).comingsoonFragment.UpdateListView(movieList, pages);
                            break;
                        case "in_theaters":
                            ((MainActivity) contextRef).intheatersFragment.UpdateListView(movieList, pages);
                            //((MainActivity) contextRef).intheatersFragment.adapter.setItemList(movieList);
                            // ((MainActivity) contextRef).intheatersFragment.adapter.notifyDataSetChanged();
                            break;
                        case "search":
                            ((MainActivity) contextRef).searchFragment.UpdateListView(movieList, pages);
                            break;
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}


