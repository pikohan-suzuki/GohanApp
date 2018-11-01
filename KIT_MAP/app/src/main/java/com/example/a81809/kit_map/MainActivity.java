package com.example.a81809.kit_map;

import android.graphics.Color;
import android.graphics.PointF;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private GestureDetector mGestureDetector;
    private ScaleGestureDetector mScaleDetector;

    private ImageView imageView;
    private TextView x_range;
    private TextView y_range;
    private TextView distance;
    private FrameLayout.LayoutParams layoutParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGestureDetector = new GestureDetector(this, mGestureListener);
        mScaleDetector = new ScaleGestureDetector(this, scaleGestureListener);

//        imageView = findViewById(R.id.map_image);
        x_range = findViewById(R.id.range_x);
        y_range=findViewById(R.id.range_y);
        distance=findViewById(R.id.distance);

    }



    //モーションイベントを検知するリスナー
    private GestureDetector.SimpleOnGestureListener mGestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            // ダブルタップ時の処理.
            return true;
        }


    };

    //スケーリング変換を検知するリスナー
    private ScaleGestureDetector.SimpleOnScaleGestureListener scaleGestureListener = new ScaleGestureDetector.SimpleOnScaleGestureListener() {

        private PointF mLastPoint = new PointF();

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            mLastPoint.set(detector.getCurrentSpanX() / 2.f, detector.getCurrentSpanY() / 2.f);
            return true;
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float factor = detector.getScaleFactor();
            float x = detector.getCurrentSpanX() / 2.f;
            float y = detector.getCurrentSpanY() / 2.f;
            boolean useFitScale = false;

            float diffX = x - mLastPoint.x;
            float diffY = y - mLastPoint.y;

            x_range.setText(String.valueOf(diffX));
            y_range.setText(String.valueOf(diffY));
            mLastPoint.set(x, y);

            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
        }
    };
}
