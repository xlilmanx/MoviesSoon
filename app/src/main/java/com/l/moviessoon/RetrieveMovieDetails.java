package com.l.moviessoon;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;


public class RetrieveMovieDetails extends AsyncTask<String, Void, String> {

    private static String API_URL = "https://api.themoviedb.org/3/movie/";

    private WeakReference<Context> mContext;
    private WeakReference<View> mView;


    public RetrieveMovieDetails(Context context, View view) {

        this.mContext = new WeakReference<>(context);
        this.mView = new WeakReference<>(view);

        if (mView.get() != null) {
            mView.get().findViewById(R.id.container_base).setVisibility(View.GONE);
            mView.get().findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected String doInBackground(String... params) {

        try {
            String API_KEY = ((MainActivity) mContext.get()).getString(R.string.api_key);
            URL url = new URL(API_URL + params[0] + "?api_key=" + API_KEY + "&language=en-US&append_to_response=releases,credits,videos,similar,reviews");
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

        if (result != null) {

            final Context contextRef = mContext.get();
            View viewRef = mView.get();

            if (viewRef != null && contextRef != null) {
                try {
                    JSONObject object = (JSONObject) new JSONTokener(result).nextValue();
                    JSONObject releases = object.getJSONObject("releases");
                    JSONArray release_country = releases.getJSONArray("countries");
                    JSONObject release_US = release_country.getJSONObject(0);
                    for (int i = 0; i < release_country.length(); i++) {
                        if (release_country.getJSONObject(i).getString("iso_3166_1").equals("US")) {
                            release_US = release_country.getJSONObject(i);
                            break;
                        }
                    }
                    JSONArray genres = object.getJSONArray("genres");
                    JSONObject credits = object.getJSONObject("credits");
                    JSONArray crew = credits.getJSONArray("crew");
                    JSONArray cast = credits.getJSONArray("cast");
                    JSONObject videos = object.getJSONObject("videos");
                    JSONArray videos_results = videos.getJSONArray("results");

                    int id = object.getInt("id");
                    String backdrop = object.getString("backdrop_path");
                    String poster = object.getString("poster_path");
                    String title = object.getString("title");
                    String release = release_US.getString("release_date");
                    String content_rating = release_US.getString("certification");
                    String runtime = object.getString("runtime");
                    String rating = object.getString("vote_average");
                    String rating_count = object.getString("vote_count");
                    String tagline = object.getString("tagline");
                    String overview = object.getString("overview");

                    ImageView iv_backdrop = viewRef.findViewById(R.id.movie_backdrop);
                    ImageView iv_poster = viewRef.findViewById(R.id.movie_poster_image);
                    TextView tv_title = viewRef.findViewById(R.id.movie_title);
                    TextView tv_release = viewRef.findViewById(R.id.movie_release);
                    TextView tv_content_rating = viewRef.findViewById(R.id.movie_content_rating);
                    TextView tv_runtime = viewRef.findViewById(R.id.movie_runtime);
                    TextView tv_genres = viewRef.findViewById(R.id.movie_genres);
                    TextView tv_rating = viewRef.findViewById(R.id.movie_rating);
                    TextView tv_rating_count = viewRef.findViewById(R.id.movie_rating_count);
                    TextView tv_tagline = viewRef.findViewById(R.id.movie_tagline);
                    TextView tv_overview = viewRef.findViewById(R.id.movie_overview);

                    MainActivity MainActivity = (MainActivity) contextRef;

                    DisplayImageOptions options = new DisplayImageOptions.Builder()
                            .cacheInMemory(true)
                            //.cacheOnDisk(true)
                            .considerExifParams(true)
                            .bitmapConfig(Bitmap.Config.RGB_565)
                            .build();

                    if (!backdrop.equals("null")) {
                        MainActivity.imageLoader.displayImage("https://image.tmdb.org/t/p/w1280" + backdrop, iv_backdrop, options);
                    }

                    if (!poster.equals("null")) {
                        MainActivity.imageLoader.displayImage("https://image.tmdb.org/t/p/w185" + poster, iv_poster, options);
                    }

                    MainActivity.title_text.setText(title);
                    tv_title.setText(title);
                    tv_release.setText(release);
                    if (!content_rating.equals("")) {
                        tv_content_rating.setText(content_rating);
                    } else {
                        tv_content_rating.setText("N/A");
                    }

                    tv_runtime.setText(runtime + " min");
                    for (int i = 0; i < genres.length(); i++) {
                        tv_genres.append(genres.getJSONObject(i).getString("name"));
                        if (i < genres.length() - 1) {
                            tv_genres.append(", ");
                        }
                    }
                    tv_rating.setText(rating + "/10");
                    tv_rating_count.setText("(" + rating_count + " votes)");
                    tv_tagline.setText(tagline);
                    tv_overview.setText(overview);


                    for (int i = 0; i < crew.length(); i++) {

                        String job = crew.getJSONObject(i).getString("job");

                        if (job.equals("Director")) {
                            LinearLayout containerDirector = viewRef.findViewById(R.id.container_director);
                            LayoutInflater inflater = LayoutInflater.from(contextRef);
                            View inflatedLayout = inflater.inflate(R.layout.layout_cast, null, false);
                            containerDirector.addView(inflatedLayout);

                            TextView cast_title = inflatedLayout.findViewById(R.id.movie_cast_title);
                            ImageView cast_image = inflatedLayout.findViewById(R.id.movie_cast_image);
                            TextView cast_name = inflatedLayout.findViewById(R.id.movie_cast_name);

                            cast_title.setText(crew.getJSONObject(i).getString("job"));

                            if (!crew.getJSONObject(i).getString("profile_path").equals("null")) {
                                MainActivity.imageLoader.displayImage("https://image.tmdb.org/t/p/w185" + crew.getJSONObject(i).getString("profile_path"), cast_image, options);
                            }

                            if (!crew.getJSONObject(i).getString("name").equals("null")) {
                                cast_name.setText(crew.getJSONObject(i).getString("name"));
                            }
                        } else if (job.contains("Writer") || job.contains("Screenplay") || job.contains("Novel") || job.contains("Comic")) {
                            LinearLayout containerWriters = viewRef.findViewById(R.id.container_writers);
                            LayoutInflater inflater = LayoutInflater.from(contextRef);
                            View inflatedLayout = inflater.inflate(R.layout.layout_cast, null, false);
                            containerWriters.addView(inflatedLayout);

                            TextView cast_title = inflatedLayout.findViewById(R.id.movie_cast_title);
                            ImageView cast_image = inflatedLayout.findViewById(R.id.movie_cast_image);
                            TextView cast_name = inflatedLayout.findViewById(R.id.movie_cast_name);

                            cast_title.setText(crew.getJSONObject(i).getString("job"));

                            if (!crew.getJSONObject(i).getString("profile_path").equals("null")) {
                                MainActivity.imageLoader.displayImage("https://image.tmdb.org/t/p/w185" + crew.getJSONObject(i).getString("profile_path"), cast_image, options);
                            }

                            if (!crew.getJSONObject(i).getString("name").equals("null")) {
                                cast_name.setText(crew.getJSONObject(i).getString("name"));
                            }
                        }
                    }

                    LinearLayout containerCrew = viewRef.findViewById(R.id.container_rest_crew);
                    Button bt_crew = new Button(contextRef);
                    bt_crew.setText(" + ");

                    LinearLayout.LayoutParams layoutParams_crew = new LinearLayout.LayoutParams(100,
                            100);
                    layoutParams_crew.setMargins(0, -60, 0, 0);
                    setAddMoreOnClickCrew(bt_crew, viewRef, crew, contextRef);
                    containerCrew.addView(bt_crew, layoutParams_crew);

                    LinearLayout containerCast = viewRef.findViewById(R.id.container_cast);
                    for (int i = 0; i < cast.length() && i < 15; i++) {
                        LayoutInflater inflater = LayoutInflater.from(contextRef);
                        View inflatedLayout = inflater.inflate(R.layout.layout_cast, null, false);
                        containerCast.addView(inflatedLayout);

                        TextView cast_title = inflatedLayout.findViewById(R.id.movie_cast_title);
                        ImageView cast_image = inflatedLayout.findViewById(R.id.movie_cast_image);
                        TextView cast_name = inflatedLayout.findViewById(R.id.movie_cast_name);

                        if (!cast.getJSONObject(i).getString("character").equals("null")) {
                            cast_title.setText(cast.getJSONObject(i).getString("character"));
                        }

                        if (!cast.getJSONObject(i).getString("profile_path").equals("null")) {
                            MainActivity.imageLoader.displayImage("https://image.tmdb.org/t/p/w185" + cast.getJSONObject(i).getString("profile_path"), cast_image, options);
                        }

                        if (!cast.getJSONObject(i).getString("name").equals("null")) {
                            cast_name.setText(cast.getJSONObject(i).getString("name"));
                        }
                    }

                    if (cast.length() > 15) {

                        Button bt = new Button(contextRef);
                        bt.setText(" + ");

                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(100,
                                100);
                        layoutParams.setMargins(0, -60, 0, 0);
                        setAddMoreOnClick(bt, viewRef, cast, contextRef);
                        containerCast.addView(bt, layoutParams);

                    }

                    LinearLayout containerVideos = viewRef.findViewById(R.id.container_videos);
                    for (int i = 0; i < videos_results.length(); i++) {
                        LayoutInflater inflater = LayoutInflater.from(contextRef);
                        View inflatedLayout = inflater.inflate(R.layout.layout_video, null, false);
                        containerVideos.addView(inflatedLayout);

                        TextView video_title = inflatedLayout.findViewById(R.id.movie_video_title);
                        ImageView video_image = inflatedLayout.findViewById(R.id.movie_video_image);

                        if (!videos_results.getJSONObject(i).getString("name").equals("null")) {
                            video_title.setText(videos_results.getJSONObject(i).getString("name"));
                        }

                        if (!videos_results.getJSONObject(i).getString("key").equals("null")) {
                            MainActivity.imageLoader.displayImage("https://i.ytimg.com/vi/" + videos_results.getJSONObject(i).getString("key") + "/hqdefault.jpg", video_image, options);
                        }

                        setOnClick(inflatedLayout, videos_results.getJSONObject(i).getString("key"), contextRef);

                    }

                    Animation fadeInAnimation = AnimationUtils.loadAnimation(contextRef, R.anim.fade_in_anim);
                    viewRef.findViewById(R.id.container_base).startAnimation(fadeInAnimation);
                    viewRef.findViewById(R.id.container_base).setVisibility(View.VISIBLE);
                    viewRef.findViewById(R.id.progressBar).setVisibility(View.GONE);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void setOnClick(View v, final String id, final Context contextRef) {
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
/**
 Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
 Intent webIntent = new Intent(Intent.ACTION_VIEW,
 Uri.parse("http://www.youtube.com/watch?v=" + id));
 try {
 contextRef.startActivity(appIntent);
 } catch (ActivityNotFoundException ex) {
 contextRef.startActivity(webIntent);
 }
 */
                MainActivity MainActivity = (MainActivity) contextRef;
                MainActivity.showYoutube(id);

            }
        });
    }

    private void setAddMoreOnClick(Button bt, final View viewRef, final JSONArray cast, final Context contextRef) {
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    MainActivity MainActivity = (MainActivity) contextRef;

                    DisplayImageOptions options = new DisplayImageOptions.Builder()
                            .cacheInMemory(true)
                            //.cacheOnDisk(true)
                            .considerExifParams(true)
                            .bitmapConfig(Bitmap.Config.RGB_565)
                            .build();

                    LinearLayout containerCast = viewRef.findViewById(R.id.container_cast);
                    for (int i = 15; i < cast.length(); i++) {
                        LayoutInflater inflater = LayoutInflater.from(contextRef);
                        View inflatedLayout = inflater.inflate(R.layout.layout_cast, null, false);
                        containerCast.addView(inflatedLayout);

                        TextView cast_title = inflatedLayout.findViewById(R.id.movie_cast_title);
                        ImageView cast_image = inflatedLayout.findViewById(R.id.movie_cast_image);
                        TextView cast_name = inflatedLayout.findViewById(R.id.movie_cast_name);

                        if (!cast.getJSONObject(i).getString("character").equals("null")) {
                            cast_title.setText(cast.getJSONObject(i).getString("character"));
                        }

                        if (!cast.getJSONObject(i).getString("profile_path").equals("null")) {
                            MainActivity.imageLoader.displayImage("https://image.tmdb.org/t/p/w185" + cast.getJSONObject(i).getString("profile_path"), cast_image, options);
                        }

                        if (!cast.getJSONObject(i).getString("name").equals("null")) {
                            cast_name.setText(cast.getJSONObject(i).getString("name"));
                        }
                    }

                    v.setVisibility(View.GONE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setAddMoreOnClickCrew(Button bt, final View viewRef, final JSONArray crew, final Context contextRef) {
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    MainActivity MainActivity = (MainActivity) contextRef;

                    DisplayImageOptions options = new DisplayImageOptions.Builder()
                            .cacheInMemory(true)
                            //.cacheOnDisk(true)
                            .considerExifParams(true)
                            .bitmapConfig(Bitmap.Config.RGB_565)
                            .build();

                    LinearLayout containerCrew = viewRef.findViewById(R.id.container_rest_crew);
                    for (int i = 0; i < crew.length(); i++) {

                        String job = crew.getJSONObject(i).getString("job");

                        if (!job.equals("Director") && !job.contains("Writer") && !job.contains("Screenplay") && !job.contains("Novel") && !job.contains("Comic")) {
                            LayoutInflater inflater = LayoutInflater.from(contextRef);
                            View inflatedLayout = inflater.inflate(R.layout.layout_cast, null, false);
                            containerCrew.addView(inflatedLayout);

                            TextView cast_title = inflatedLayout.findViewById(R.id.movie_cast_title);
                            ImageView cast_image = inflatedLayout.findViewById(R.id.movie_cast_image);
                            TextView cast_name = inflatedLayout.findViewById(R.id.movie_cast_name);

                            cast_title.setText(crew.getJSONObject(i).getString("job"));

                            if (!crew.getJSONObject(i).getString("profile_path").equals("null")) {
                                MainActivity.imageLoader.displayImage("https://image.tmdb.org/t/p/w185" + crew.getJSONObject(i).getString("profile_path"), cast_image, options);
                            }

                            if (!crew.getJSONObject(i).getString("name").equals("null")) {
                                cast_name.setText(crew.getJSONObject(i).getString("name"));
                            }
                        }
                    }
                    v.setVisibility(View.GONE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}


