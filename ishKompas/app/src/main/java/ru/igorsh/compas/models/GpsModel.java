package ru.igorsh.compas.models;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;

import static android.content.Context.LOCATION_SERVICE;

import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;
import androidx.core.app.ActivityCompat;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import ru.igorsh.compas.R;
import ru.igorsh.compas.events.*;

public class GpsModel {
    private static final long MIN_TIME = 1000 * 10;
    private static final float MIN_DISTANCE = 10;

    private final LocationManager mLocationManager;
    private final LocationListener mLocationListener;
    private final Context mContext;
    private Location mPrevLocation = null;
    private float mDistance = 0;
    private float mDeltaDistance = 0;
    private int mStatus = 0;

    public GpsModel(Context context) {
        Log.d("ish", "create GpsModel");
        mContext = context;
        mLocationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                doLocationChanged(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.d("gps", String.valueOf(status));
                mStatus = status;
                sendGpsStatusEvent();
            }

            @Override
            public void onProviderDisabled(String provider) {
                //;
            }

            @SuppressLint("MissingPermission")
            @Override
            public void onProviderEnabled(String provider) {
                doLocationChanged(mLocationManager.getLastKnownLocation(provider));
            }
        };

        EventBus.getDefault().register(this);

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("gps", "gps_not_permitted");
            EventBus.getDefault().post(new GpsStatusEvent(mContext.getString(R.string.gps_not_permitted)));
        } else {
            Log.d("gps", "gps_permitted");
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, mLocationListener);
        }
    }

    private void doLocationChanged(Location location) {
        if (location == null)
            return;
        if (mPrevLocation != null) {
            mDeltaDistance = location.distanceTo(mPrevLocation) / 1000;
            mDistance += mDeltaDistance;
            EventBus.getDefault().post(new GpsDeltaDistEvent(mDeltaDistance));
        }

        mPrevLocation = location;

        // send events
        sendGpsEvent(location);
    }

    private void sendGpsEvent(Location location) {
        if (location == null)
            return;
        EventBus.getDefault().post(new GpsEvent(
                  Location.convert(location.getLatitude(), Location.FORMAT_DEGREES)
                , Location.convert(location.getLongitude(), Location.FORMAT_DEGREES)
                , mDistance
                , (location.getSpeed() * 3600 / 1000)
                , location.getBearing()));
    }

    private void sendGpsStatusEvent() {
        if (mStatus == LocationProvider.TEMPORARILY_UNAVAILABLE) {
            EventBus.getDefault().post(new GpsStatusEvent(mContext.getString(R.string.gps_disable)));
        } else
        if (mStatus == LocationProvider.AVAILABLE) {
            EventBus.getDefault().post(new GpsStatusEvent(mContext.getString(R.string.gps_enable)));
        } else {
            EventBus.getDefault().post(new GpsStatusEvent(mContext.getString(R.string.gps_not_permitted)));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void onGpsRefreshEvent(GpsRefreshEvent event) {
        sendGpsEvent(mPrevLocation);
        sendGpsStatusEvent();
    }
}
