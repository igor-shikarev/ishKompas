package ru.igorsh.compas;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.widget.*;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.muddzdev.styleabletoast.StyleableToast;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import ru.igorsh.compas.events.GpsRefreshEvent;
import ru.igorsh.compas.events.GpsStatusEvent;
import ru.igorsh.compas.events.GpsEvent;

import static android.content.Context.CLIPBOARD_SERVICE;

public class Summary extends Fragment {
    private TextView tvStatus;
    private TextView tvLat;
    private TextView tvLot;
    private TextView tvBearing;
    private TextView tvSpeed;
    private TextView tvSumDist;
    private Button btnCopy;
    private ProgressBar pbLoader;

    public Summary() {
        // Required empty public constructor
    }

    private void startLoader() {
        pbLoader.setIndeterminate(true);
        pbLoader.setVisibility(View.VISIBLE);
    }

    private void stopLoader() {
        pbLoader.setIndeterminate(false);
        pbLoader.setVisibility(View.GONE);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_summary, container, false);

        tvStatus = (TextView)v.findViewById(R.id.tvSummaryStatus);
        tvLat = (TextView)v.findViewById(R.id.tvSummaryLat);
        tvLot = (TextView)v.findViewById(R.id.tvSummaryLon);
        tvBearing = (TextView)v.findViewById(R.id.tvSummaryBearing);
        tvSpeed = (TextView)v.findViewById(R.id.tvSummarySpeed);
        tvSumDist = (TextView)v.findViewById(R.id.tvSummarySumDist);
        btnCopy = (Button)v.findViewById(R.id.btnSummaryCopy);
        pbLoader = (ProgressBar)v.findViewById(R.id.pbSummaryLoader);

        btnCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StringBuilder text = new StringBuilder();

                TextView tvText = (TextView)v.findViewById(R.id.lblSummaryCaption);
                text.append(tvText.getText() + "\n");
                tvText = (TextView)v.findViewById(R.id.lblSummaryLat);
                text.append(tvText.getText() + ": ");
                tvText = (TextView)v.findViewById(R.id.tvSummaryLat);
                text.append(tvText.getText() + "\n");
                tvText = (TextView)v.findViewById(R.id.lblSummaryLot);
                text.append(tvText.getText() + ": ");
                tvText = (TextView)v.findViewById(R.id.tvSummaryLon);
                text.append(tvText.getText() + "\n");
                tvText = (TextView)v.findViewById(R.id.lblSummaryBearing);
                text.append(tvText.getText() + ": ");
                tvText = (TextView)v.findViewById(R.id.tvSummaryBearing);
                text.append(tvText.getText() + "\n");
                tvText = (TextView)v.findViewById(R.id.lblSummarySpeed);
                text.append(tvText.getText() + ": ");
                tvText = (TextView)v.findViewById(R.id.tvSummarySpeed);
                text.append(tvText.getText() + "\n");
                tvText = (TextView)v.findViewById(R.id.lblSummarySumDist);
                text.append(tvText.getText() + ": ");
                tvText = (TextView)v.findViewById(R.id.tvSummarySumDist);
                text.append(tvText.getText() + "\n");

                Context context = getActivity();
                ClipboardManager clipboard = (ClipboardManager)context.getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(null, text);
                clipboard.setPrimaryClip(clip);
                //Toast.makeText(context, R.string.info_copied, Toast.LENGTH_LONG).show();
                StyleableToast.makeText(context, getResources().getString(R.string.info_copied), Toast.LENGTH_LONG, R.style.AppToast).show();
            }
        });

        startLoader();
        EventBus.getDefault().post(new GpsRefreshEvent());
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

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void onGpsStatusEvent(GpsStatusEvent event) {
        tvStatus.setText(event.status);
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void onGpsSummaryEvent(GpsEvent event) {
        stopLoader();
        tvLat.setText(event.latitude);
        tvLot.setText(event.longitude);
        tvBearing.setText(String.format("%1$.2f",event.bearing));
        tvSpeed.setText(String.format("%1$.2f", event.speed));
        tvSumDist.setText(String.format("%1$.2f", event.distance));
    }
}