package ru.igorsh.compas.events;

public class GpsDistEvent {
    public final float dist0;
    public final float dist1;
    public final float dist2;

    public GpsDistEvent(float dist0, float dist1, float dist2) {
        this.dist0 = dist0;
        this.dist1 = dist1;
        this.dist2 = dist2;
    }
}
