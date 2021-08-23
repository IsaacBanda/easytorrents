package com.intelisoft.easytorrents.model;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Technophile on 5/25/17.
 */

public class Movie implements Serializable {
    private  String largeCoverImageUrl;
    private  String mediumCoverImageUrl;
    private String smallCoverImageUrl;
    private int id;
    private String url;
    private String title;
    private String year;
    private String rating,runtime;
    private String[] genres;
    private String summary, descriptionFull;
    private String language;
    private String state;
    private ArrayList<Torrent> torrents;
    private String bgImageUrl;
    private String bgImageOriginalURL;
    private String screenshot1URL;
    private String screenshot2URL;
    private String screenshot3URL;
    private ArrayList<Cast> cast = new ArrayList<>();
    private ArrayList<Movie> suggestedMovies = new ArrayList<>();

    public ArrayList<Cast> getCast() {
        Log.i("getCast", "Size: "+cast.size());
        return cast;
    }

    public void addCast(Cast cast) {
        this.cast.add(cast);
    }

    public Movie setCast(ArrayList<Cast> cast) {
        this.cast = cast;
        return  this;
    }

    public ArrayList<Movie> getSuggestedMovies() {
        return suggestedMovies;
    }

    public void setSuggestedMovies(ArrayList<Movie> suggestedMovies) {
        this.suggestedMovies = suggestedMovies;
    }

    public Movie setScreenshot1(String screenshot1) {
        this.screenshot1URL = screenshot1;
        return this;
    }

    public String getScreenshot1URL() {
        return screenshot1URL;
    }

    public Movie setScreenshot2(String screenshot2) {
        this.screenshot2URL = screenshot2;
        return this;
    }

    public String getScreenshot2URL() {
        return screenshot2URL;
    }

    public Movie setScreenshot3(String screenshot3) {
        this.screenshot3URL = screenshot3;
        return this;
    }

    public String getScreenshot3URL() {
        return screenshot3URL;
    }


    public static  class Torrent implements Serializable {
        private String url, runtime, quality, seeds, peers;
        private String size, dateUploaded, title;

        public Torrent(String title, String url, String quality, String size, String dateUploaded, String seeds, String peers, String runtime) {
            this.url = url;
            this.quality = quality;
            this.size = size;
            this.dateUploaded = dateUploaded;
            this.seeds = seeds;this.peers = peers;
            this.runtime = runtime;
            this.title = title;
        }

        public Torrent(String title, String url, String size) {
            this(title, url, "",size, "","", "", "" );
        }

        public String getPeers() {
            return peers;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getRuntime() {
            return runtime;
        }

        public void setRuntime(String runtime) {
            this.runtime = runtime;
        }

        public void setPeers(String peers) {
            this.peers = peers;
        }

        public String getSeeds() {
            return seeds;
        }

        public void setSeeds(String seeds) {
            this.seeds = seeds;
        }

        public String getUrl() {
            return url;
        }


        public String getQuality() {
            return quality;
        }

        public String getSize() {
            return size;
        }

        public String getDateUploaded() {
            return dateUploaded;
        }
    }

    public Movie() {

    }

    public Movie(int id, String url, String title, String year, String rating, String runtime,
                 String[] genres, String summary, String descriptionFull, String language,
                 String bgImage, String bgImageOriginal, String smallCoverImage,
                 String mediumCoverImage, String largeCoverImage, String state, ArrayList<Torrent> torrents) {
        this.id = id;
        this.url = url;
        this.title = title;
        this.year = year;
        this.rating = rating;
        this.runtime = runtime;
        this.genres = genres;
        this.summary = summary;
        this.descriptionFull = descriptionFull;
        this.language = language;
        this.bgImageUrl = bgImage;
        this.bgImageOriginalURL = bgImageOriginal;
        this.smallCoverImageUrl = smallCoverImage;
        this.mediumCoverImageUrl = mediumCoverImage;
        this.largeCoverImageUrl = largeCoverImage;
        this.state = state;
        this.torrents = torrents;
    }

    public int getId() {
        return id;
    }

    public Movie setId(int id) {
        this.id = id; return this;
    }

    public String getLargeCoverImageUrl() {
        return largeCoverImageUrl;
    }

    public Movie setLargeCoverImageUrl(String largeCoverImageUrl) {
        this.largeCoverImageUrl = largeCoverImageUrl;
        return this;
    }

    public String getMediumCoverImageUrl() {
        return mediumCoverImageUrl;
    }

    public Movie setMediumCoverImageUrl(String mediumCoverImageUrl) {
        this.mediumCoverImageUrl = mediumCoverImageUrl;
        return this;
    }

    public String getSmallCoverImageUrl() {
        return smallCoverImageUrl;
    }

    public Movie setSmallCoverImageUrl(String smallCoverImageUrl) {
        this.smallCoverImageUrl = smallCoverImageUrl;
        return this;
    }

    public String getBgImageUrl() {
        return bgImageUrl;
    }


    public String getBgImageOriginalURL() {
        return bgImageOriginalURL;
    }

    public Movie setBgImageOriginalURL(String bgImageOriginalURL) {
        this.bgImageOriginalURL = bgImageOriginalURL;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public Movie setUrl(String url) {
        this.url = url; return this;
    }

    public String getTitle() {
        return title;
    }

    public Movie setTitle(String title) {
        this.title = title; return this;
    }

    public String getYear() {
        return year;
    }

    public Movie setYear(String year) {
        this.year = year; return this;
    }

    public String getRating() {
        return rating;
    }

    public Movie setRating(String rating) {
        this.rating = rating; return this;
    }

    public String getRuntime() {
        return runtime;
    }

    public Movie setRuntime(String runtime) {
        this.runtime = runtime; return this;
    }

    public String getGenres() {
        String s = "";

        for (String genre : genres)
            s +=""+ genre.replace("[", "").replace("]", "").replace("\"", "").replaceFirst(",", "") + ", ";
        int index = s.lastIndexOf(",");

        return s.substring(0, index);
    }

    public Movie setGenres(String[] genres) {
        this.genres = genres; return this;
    }

    public String getSummary() {
        return summary;
    }

    public Movie setSummary(String summary) {
        this.summary = summary; return this;
    }

    public String getDescriptionFull() {
        return descriptionFull;
    }

    public Movie setDescriptionFull(String descriptionFull) {
        this.descriptionFull = descriptionFull; return this;
    }

    public String getLanguage() {
        return language;
    }

    public Movie setLanguage(String language) {
        this.language = language;
        return this;
    }

    public String getBgImageURL() {
        return bgImageUrl;
    }

    public Movie setBgImageURL(String url) {
        this.bgImageUrl = url;
        return this;
    }

    public String getBgImageOriginal() {
        return bgImageOriginalURL;
    }



    public String getState() {
        return state;
    }

    public Movie setState(String state) {
        this.state = state;
        return this;
    }

    public ArrayList<Torrent> getTorrents() {
        return torrents;
    }

    public Movie setTorrents(ArrayList<Torrent> torrents) {
        this.torrents = torrents;
        return this;
    }
}
