package ru.igorsh.compas.models;

import android.content.Context;

public class Model {
    private final GpsModel mGpsModel;
    private final GpsDistModel mGpsDistModel;

    public Model(Context context) {
        mGpsModel = new GpsModel(context);
        mGpsDistModel = new GpsDistModel(context);
    }

}
