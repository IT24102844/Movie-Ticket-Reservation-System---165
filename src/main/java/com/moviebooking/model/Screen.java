package com.moviebooking.model;

public class Screen {
    private String screenId;
    private String theatreId;
    private String screenName;
    private int totalSeats;
    private double ticketPrice;

    public Screen() {}

    public Screen(String screenId, String theatreId, String screenName, int totalSeats, double ticketPrice) {
        this.screenId = screenId;
        this.theatreId = theatreId;
        this.screenName = screenName;
        this.totalSeats = totalSeats;
        this.ticketPrice = ticketPrice;
    }

    public String getScreenId() {
        return screenId;
    }

    public void setScreenId(String screenId) {
        this.screenId = screenId;
    }

    public String getTheatreId() {
        return theatreId;
    }

    public void setTheatreId(String theatreId) {
        this.theatreId = theatreId;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }

    public double getTicketPrice() {
        return ticketPrice;
    }

    public void setTicketPrice(double ticketPrice) {
        this.ticketPrice = ticketPrice;
    }
}