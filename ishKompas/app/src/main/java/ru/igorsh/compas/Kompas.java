package ru.igorsh.compas;

import android.content.Context;
import android.graphics.*;
import android.os.Bundle;
import android.view.*;
import android.widget.RelativeLayout;
import androidx.fragment.app.Fragment;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import ru.igorsh.compas.events.GpsEvent;

public class Kompas extends Fragment {
    private DrawView mDrawView = null;

    public Kompas() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_kompas, container, false);
        RelativeLayout panel = (RelativeLayout)v.findViewById(R.id.ltKompasCanvas);
        mDrawView = new DrawView(getActivity());
        panel.addView(mDrawView);
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
    public void onGpsStatusEvent(GpsEvent event) {
        if (mDrawView != null) {
            mDrawView.setBearing(event.bearing);
            mDrawView.invalidate();
        }
    }

    class DrawView extends View {
        private static final float TEXT_SIZE = 24;
        private static final float ARROW_SIZE = 48;
        private final Paint mPaint;
        private final Path mPath;
        private final Matrix mMatrix;
        private float mBearing;

        public DrawView(Context context) {
            super(context);
            mPaint = new Paint();
            mPath = new Path();
            mMatrix = new Matrix();
        }

        public void setBearing(float bearing) {
            mBearing = bearing;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            float cx = this.getWidth() / 2;
            float cy = this.getHeight() / 2;
            float rd = Math.min(cx, cy) - 20;

            mPaint.setStrokeWidth(2);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setTextSize(TEXT_SIZE);
            mPaint.setColor(getResources().getColor(R.color.colorText));

            // рисуем фон компаса
            canvas.drawCircle(cx, cy, rd, mPaint);
            canvas.drawText(getContext().getString(R.string.frg_kompas_n), cx - TEXT_SIZE / 2, cy - rd + TEXT_SIZE, mPaint);
            canvas.drawText(getContext().getString(R.string.frg_kompas_e), cx + rd - TEXT_SIZE, cy + TEXT_SIZE / 2, mPaint);
            canvas.drawText(getContext().getString(R.string.frg_kompas_s), cx - TEXT_SIZE / 2, cy + rd - TEXT_SIZE / 2, mPaint);
            canvas.drawText(getContext().getString(R.string.frg_kompas_w), cx - rd + TEXT_SIZE / 2, cy + TEXT_SIZE / 2, mPaint);

            // рисуем стрелку компаса
            mPath.reset();
            mPath.moveTo(cx - ARROW_SIZE / 2, cy - 2);
            mPath.lineTo(cx, cy - rd + ARROW_SIZE);
            mPath.lineTo(cx + ARROW_SIZE / 2, cy - 2);
            mPath.lineTo(cx - ARROW_SIZE / 2, cy - 2);
            mPath.close();
            mMatrix.reset();
            mMatrix.postRotate(mBearing, cx, cy);
            mPath.transform(mMatrix);
            mPaint.setColor(Color.BLUE);
            mPaint.setStrokeWidth(1);
            mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            canvas.drawPath(mPath, mPaint);

            mPath.reset();
            mPath.moveTo(cx - ARROW_SIZE / 2, cy + 2);
            mPath.lineTo(cx, cy + rd - ARROW_SIZE);
            mPath.lineTo(cx + ARROW_SIZE / 2, cy + 2);
            mPath.lineTo(cx - ARROW_SIZE / 2, cy + 2);
            mPath.close();
            mMatrix.reset();
            mMatrix.postRotate(mBearing, cx, cy);
            mPath.transform(mMatrix);
            mPaint.setColor(Color.RED);
            mPaint.setStrokeWidth(1);
            mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            canvas.drawPath(mPath, mPaint);
        }
    }
}