package com.example.falldetector.database;

public class Ride {

    int _id;
    String _howLongWasRide;
    String _howWasRide;
    String _howWasWeather;

    public Ride() {}

    public Ride(int id, String howLongWasRide, String howWasRide, String howWasWeather) {
        this._id = id;
        this._howLongWasRide = howLongWasRide;
        this._howWasRide = howWasRide;
        this._howWasWeather = howWasWeather;
    }

    public Ride(String howLongWasRide, String howWasRide, String howWasWeather) {
        this._howLongWasRide = howLongWasRide;
        this._howWasRide = howWasRide;
        this._howWasWeather = howWasWeather;
    }

    public int getId() {
        return this._id;
    }

    public void setId(int _id) {
        this._id = _id;
    }

    public String getHowLongWasRide() {
        return this._howLongWasRide;
    }

    public void setHowLongWasRide(String _howLongWasRide) {
        this._howLongWasRide = _howLongWasRide;
    }

    public String getHowWasRide() {
        return this._howWasRide;
    }

    public void setHowWasRide(String _howWasRide) {
        this._howWasRide = _howWasRide;
    }

    public String getHowWasWeather() {
        return this._howWasWeather;
    }

    public void setHowWasWeather(String _howWasWeather) {
        this._howWasWeather = _howWasWeather;
    }
}
