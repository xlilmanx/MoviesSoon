package com.l.moviessoon;

import java.io.Serializable;


public class MovieInfo implements Serializable {

    private int id;
    private String poster;
    private String title;
    private String release;
    private String overview;
    private String rating;
    private String rating_count;

    public MovieInfo(int id, String poster, String title, String release, String overview, String rating, String rating_count) {
        super();
        this.id = id;
        this.poster = poster;
        this.title = title;
        this.release = release;
        this.overview = overview;
        this.rating = rating;
        this.rating_count = rating_count;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRelease() {
        return release;
    }

    public void setRelease(String release) {
        this.release = release;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getRatingCount() {
        return rating_count;
    }

    public void setRatingCount(String rating_count) {
        this.rating_count = rating_count;
    }

}


