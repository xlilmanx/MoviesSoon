package com.l.moviessoon;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.io.InputStream;
import java.util.List;


public class ListViewAdapter extends ArrayAdapter<MovieInfo> {

    private final Context context;
    private List<MovieInfo> movieList;


    public ListViewAdapter(Context context, List<MovieInfo> itemList) {
        super(context, -1);
        this.context = context;
        this.movieList = itemList;
        // this.symbols = symbols;

    }

    public List<MovieInfo> getItemList() {
        return movieList;
    }

    public void setItemList(List<MovieInfo> movieList) {
        this.movieList.addAll(movieList);
    }

    public void addItemList(MovieInfo movieInfo) {
        this.movieList.add(movieInfo);
    }

    public void replaceItemList(List<MovieInfo> movieList) {
        this.movieList = movieList;
    }

    public void clearMovieList() {
        this.movieList.clear();
    }

    public int getCount() {
        if (movieList != null)
            return movieList.size();
        return 0;
    }

    public MovieInfo getItem(int position) {
        if (movieList != null)
            return movieList.get(position);
        return null;
    }

    public long getItemId(int position) {
        if (movieList != null)
            return movieList.get(position).hashCode();
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        MovieInfo movie_info = movieList.get(position);

        View rowView = inflater.inflate(R.layout.lv_layout, parent, false);

        ImageView lv_poster = rowView.findViewById(R.id.movie_poster_image);
        TextView lv_title = rowView.findViewById(R.id.movie_title);
        TextView lv_release = rowView.findViewById(R.id.movie_release);
        TextView lv_overview = rowView.findViewById(R.id.movie_overview);
        TextView lv_rating = rowView.findViewById(R.id.movie_rating);
        TextView lv_rating_count = rowView.findViewById(R.id.movie_rating_count);

        //new DownloadImageTask(lv_poster)
        //        .execute("https://image.tmdb.org/t/p/w154" + movie_info.getPoster());


        MainActivity MainActivity = (MainActivity) context;

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                //.cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        if (!movie_info.getPoster().equals("null")) {
            MainActivity.imageLoader.displayImage("https://image.tmdb.org/t/p/w154" + movie_info.getPoster(), lv_poster, options);
        }

        lv_title.setText(movie_info.getTitle());
        lv_release.setText(movie_info.getRelease());
        //lv_overview.setText(movie_info.getOverview());
        lv_rating.setText(movie_info.getRating());
        lv_rating_count.setText("(" + movie_info.getRatingCount() + ")");

        return rowView;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                bmImage.setImageBitmap(result);
            }
        }
    }

}
