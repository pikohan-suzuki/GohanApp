package com.example.a81809.myapplication;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.gesture.Gesture;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private Image image;
    private FrameLayout parent_layout;
    private DatabaseRead database;
    public static Point screenSize;
    public static int building_number;
    public static int floor;
    private GestureDetector mGestureDetector;
    private ScaleGestureDetector mScaleGestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = new DatabaseRead(getApplication(),"database.db");
        parent_layout = findViewById(R.id.parent_layout);
        building_number=0;
        floor=0;

        //タッチイベント
        parent_layout.setOnTouchListener(mTouchEventListener);
        //スクロールイベント
        mGestureDetector = new GestureDetector(this,mGestureListener);
        //スケールイベント
        mScaleGestureDetector = new ScaleGestureDetector(this,mScaleGestureListener);

    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if(screenSize==null){
            screenSize=getViewSize(parent_layout);
            image = new Image(getApplication(),parent_layout,database);
        }
//        image.scale();
    }

    private Point getViewSize(View v){
        Point point =new Point(0,0);
        point.set(v.getWidth(),v.getHeight());
        return point;
    }

    private View.OnTouchListener mTouchEventListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mScaleGestureDetector.onTouchEvent(motionEvent);
            mGestureDetector.onTouchEvent(motionEvent);
            return true;
        }
    };

    private GestureDetector.OnGestureListener mGestureListener =
            new GestureDetector.OnGestureListener() {
                @Override
                public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
                    image.onScroll(v,v1);
                    return false;
                }
                @Override
                public boolean onDown(MotionEvent motionEvent) {
                    return false;
                }
                @Override
                public void onShowPress(MotionEvent motionEvent) {

                }
                @Override
                public boolean onSingleTapUp(MotionEvent motionEvent) {
                    return false;
                }
                @Override
                public void onLongPress(MotionEvent motionEvent) {

                }
                @Override
                public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
                    return false;
                }
            };

    private ScaleGestureDetector.OnScaleGestureListener mScaleGestureListener =
            new ScaleGestureDetector.OnScaleGestureListener() {
                @Override
                public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
                    float focusX = scaleGestureDetector.getFocusX();
                    float focusY = scaleGestureDetector.getFocusY();
                    float factor = scaleGestureDetector.getScaleFactor();
                    image.onScale(focusX,focusY,factor);
                    return false;
                }
                @Override
                public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
                    return true;
                }
                @Override
                public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {

                }
            };
}
