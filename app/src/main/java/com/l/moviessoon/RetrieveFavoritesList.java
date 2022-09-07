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


public class RetrieveFavoritesList extends AsyncTask<String, Void, String> {

    private static String API_URL = "https://api.themoviedb.org/3/movie/";

    private int page = 1;
    private String fragment;
    private WeakReference<Context> mContext;
    private WeakReference<View> mView;


    public RetrieveFavoritesList(Context context, View view) {

        this.mContext = new WeakReference<>(context);
        this.mView = new WeakReference<>(view);

    }

    @Override
    protected void onPreExecute() {
        Context contextRef = mContext.get();

        if (contextRef != null) {

            ((MainActivity) contextRef).favoritesFragment.StartRetrieveTask();

        }
    }

    @Override
    protected String doInBackground(String... params) {

        try {
            String API_KEY = ((MainActivity) mContext.get()).getString(R.string.api_key);
            URL url = new URL(API_URL + params[0] + "?api_key=" + API_KEY + "&language=en-US&append_to_response=releases");
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
                    JSONObject releases = object.getJSONObject("releases");
                    JSONArray release_country = releases.getJSONArray("countries");
                    JSONObject release_US = release_country.getJSONObject(0);

                    int id = object.getInt("id");
                    String poster = object.getString("poster_path");
                    String title = object.getString("title");
                    String release = release_US.getString("release_date");
                    String rating = object.getString("vote_average");
                    String rating_count = object.getString("vote_count");
                    String overview = object.getString("overview");

                    MovieInfo movieInfo = new MovieInfo(id, poster, title, release, overview, rating, rating_count);

                    ((MainActivity) contextRef).favoritesFragment.UpdateListView(movieInfo);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}


