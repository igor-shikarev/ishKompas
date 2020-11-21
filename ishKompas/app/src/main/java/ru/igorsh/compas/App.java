package ru.igorsh.compas;

import android.app.Application;
import android.util.Log;
import ru.igorsh.compas.models.Model;

public class App extends Application {
    private Model mModel;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("ish", "create App");
        this.mModel = new Model(this);
    }
}
