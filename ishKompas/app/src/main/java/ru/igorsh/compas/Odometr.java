package ru.igorsh.compas;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import ru.igorsh.compas.events.*;

import java.util.Arrays;

public class Odometr extends Fragment {
    private TextView tvDist0;
    private TextView tvDist1;
    private TextView tvDist2;
    private Button btnReset0;
    private Button btnReset1;
    private Button btnReset2;
    private Button btnStartStop0;
    private Button btnStartStop1;
    private Button btnStartStop2;

    public Odometr() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v =  inflater.inflate(R.layout.fragment_odometr, container, false);

        tvDist0 = (TextView)v.findViewById(R.id.tvOdmDist0);
        tvDist1 = (TextView)v.findViewById(R.id.tvOdmDist1);
        tvDist2 = (TextView)v.findViewById(R.id.tvOdmDist2);
        btnReset0 = (Button)v.findViewById(R.id.btnOdmReset0);
        btnReset1 = (Button)v.findViewById(R.id.btnOdmReset1);
        btnReset2 = (Button)v.findViewById(R.id.btnOdmReset2);
        btnStartStop0 = (Button)v.findViewById(R.id.btnOdmStartStop0);
        btnStartStop1 = (Button)v.findViewById(R.id.btnOdmStartStop1);
        btnStartStop2 = (Button)v.findViewById(R.id.btnOdmStartStop2);

        View.OnClickListener doResetClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int[] ids = {R.id.btnOdmReset0, R.id.btnOdmReset1, R.id.btnOdmReset2};
                int idx = Arrays.binarySearch(ids, v.getId());
                EventBus.getDefault().post(new GpsResetDistEvent(idx));
            }
        };

        View.OnClickListener doStartStopClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int[] ids = {R.id.btnOdmStartStop0, R.id.btnOdmStartStop1, R.id.btnOdmStartStop2};
                int idx = Arrays.binarySearch(ids, v.getId());

                Button btn = (Button)v;
                if (btn.getText() == v.getResources().getString(R.string.icon_start)) {
                    btn.setText(R.string.icon_stop);
                    EventBus.getDefault().post(new GpsStartDistEvent(idx));
                } else {
                    btn.setText(R.string.icon_start);
                    EventBus.getDefault().post(new GpsStopDistEvent(idx));
                }

            }
        };

        btnReset0.setOnClickListener(doResetClick);
        btnReset1.setOnClickListener(doResetClick);
        btnReset2.setOnClickListener(doResetClick);
        btnStartStop0.setOnClickListener(doStartStopClick);
        btnStartStop1.setOnClickListener(doStartStopClick);
        btnStartStop2.setOnClickListener(doStartStopClick);

        if (savedInstanceState != null) {
            btnStartStop0.setText(savedInstanceState.getCharSequence("StartStop0"));
            btnStartStop1.setText(savedInstanceState.getCharSequence("StartStop1"));
            btnStartStop2.setText(savedInstanceState.getCharSequence("StartStop2"));
        }

        EventBus.getDefault().post(new GpsRefreshDistEvent());
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharSequence("StartStop0", btnStartStop0.getText());
        outState.putCharSequence("StartStop1", btnStartStop1.getText());
        outState.putCharSequence("StartStop2", btnStartStop2.getText());
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void onGpsRefreshEvent(GpsDistEvent event) {
        tvDist0.setText(String.format("%1$.3f", event.dist0));
        tvDist1.setText(String.format("%1$.3f", event.dist1));
        tvDist2.setText(String.format("%1$.3f", event.dist2));
    }
}