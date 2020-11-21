package ru.igorsh.compas.events;

public class GpsEvent {
    public final String latitude;
    public final String longitude;
    public final float distance;
    public final float speed;
    public final float bearing;

    public GpsEvent(String latitude, String longitude, float distance, float speed, float bearing) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.distance = distance;
        this.speed = speed;
        this.bearing = bearing;
    }
}
