package com.moviebooking.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Showtime {
    private String showtimeId;
    private String movieId;
    private String theatreId;
    private String screenId;
    private LocalDate date;
    private LocalTime time;

    public Showtime() {}

    public Showtime(String showtimeId, String movieId, String theatreId, String screenId, LocalDate date, LocalTime time) {
        this.showtimeId = showtimeId;
        this.movieId = movieId;
        this.theatreId = theatreId;
        this.screenId = screenId;
        this.date = date;
        this.time = time;
    }

    public String getShowtimeId() {
        return showtimeId;
    }

    public void setShowtimeId(String showtimeId) {
        this.showtimeId = showtimeId;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public String getTheatreId() {
        return theatreId;
    }

    public void setTheatreId(String theatreId) {
        this.theatreId = theatreId;
    }

    public String getScreenId() {
        return screenId;
    }

    public void setScreenId(String screenId) {
        this.screenId = screenId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }
}
