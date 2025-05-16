package com.moviebooking.model;

import java.time.LocalDate;

public class Movie {
    private String movieId;
    private String title;
    private String genre;
    private LocalDate releaseDate;
    private int duration;
    private String imageName;

    public Movie() {}

    public Movie(String title, String genre, LocalDate releaseDate, int duration, String imageName) {
        this.title = title;
        this.genre = genre;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.imageName = imageName;
        this.movieId = title.substring(0, Math.min(title.length(), 6)).toUpperCase();
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
}