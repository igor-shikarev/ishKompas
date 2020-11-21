package ru.igorsh.compas.models;

import android.content.Context;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import ru.igorsh.compas.events.*;

public class GpsDistModel {
    private final Context mContext;
    private final float[] mDist = {0, 0, 0};
    private final boolean[] mDistEnable = {false, false, false};

    public GpsDistModel(Context context) {
        mContext = context;
        EventBus.getDefault().register(this);
    }

    private void sendDistEvent() {
        EventBus.getDefault().post(new GpsDistEvent(mDist[0], mDist[1], mDist[2]));
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void onGpsResetDistEvent(GpsResetDistEvent event) {
        mDist[event.idx] = 0;
        sendDistEvent();
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void onGpsStopDistEvent(GpsStopDistEvent event) {
        mDistEnable[event.idx] = false;
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void onGpsStartDistEvent(GpsStartDistEvent event) {
        mDistEnable[event.idx] = true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void onGpsRefreshEvent(GpsRefreshDistEvent event) {
        sendDistEvent();
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void onGpsEvent(GpsDeltaDistEvent event) {
        if (mDistEnable[0]) mDist[0] += event.distance;
        if (mDistEnable[1]) mDist[1] += event.distance;
        if (mDistEnable[2]) mDist[2] += event.distance;
        sendDistEvent();
    }

}
