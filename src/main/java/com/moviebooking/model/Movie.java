package com.moviebooking.model;

import java.time.LocalDate;

public class Movie {
    private String movieId;
    private String title;
    private String genre;
    private LocalDate releaseDate;
    private int duration; // in minutes
    private String imageName;
    private double ticketPrice;

    public Movie() {}

    public Movie(String title, String genre, LocalDate releaseDate, int duration, String imageName, double ticketPrice) {
        this.title = title;
        this.genre = genre;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.imageName = imageName;
        this.ticketPrice = ticketPrice;
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
        // Update movieId when title changes
        this.movieId = title.substring(0, Math.min(title.length(), 6)).toUpperCase();
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

    public double getTicketPrice() {
        return ticketPrice;
    }

    public void setTicketPrice(double ticketPrice) {
        this.ticketPrice = ticketPrice;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }


}