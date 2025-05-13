package com.moviebooking.model;

public class Theatre {
    private String theatreId;
    private String name;
    private String location;
    private int seatCapacity;

    public Theatre() {}

    public Theatre(String theatreId, String name, String location, int seatCapacity) {
        this.theatreId = theatreId;
        this.name = name;
        this.location = location;
        this.seatCapacity = seatCapacity;
    }

    public String getTheatreId() {
        return theatreId;
    }

    public void setTheatreId(String theatreId) {
        this.theatreId = theatreId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getSeatCapacity() {
        return seatCapacity;
    }

    public void setSeatCapacity(int seatCapacity) {
        this.seatCapacity = seatCapacity;
    }
}