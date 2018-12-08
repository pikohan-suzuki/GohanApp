package com.example.a81809.kit_map;

import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity {
    public static Image image;
    private Room[] rooms;
    private Facility[] faclities;
    private FrameLayout parent_layout;
    private DatabaseRead database;
    private GestureDetector mGestureDetector;
    private ScaleGestureDetector mScaleGestureDetector;

    public static Point screenSize;
    public static int building_number;
    public static int floor;
    private Point focusRange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = new DatabaseRead(getApplication(), "database.db");
        parent_layout = findViewById(R.id.parent_layout);
        building_number = 0;
        floor = 0;

        //タッチイベント
        parent_layout.setOnTouchListener(mTouchEventListener);
        //スクロールイベント
        mGestureDetector = new GestureDetector(this, mGestureListener);
        //スケールイベント
        mScaleGestureDetector = new ScaleGestureDetector(this, mScaleGestureListener);

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        if (screenSize == null) {
            screenSize = getViewSize(parent_layout);
            image = new Image(getApplication(), parent_layout, database);
            int[] room_numbers = database.getRoomNumbers(23, 1);
            rooms = new Room[room_numbers.length];
            for (int i = 0; i < room_numbers.length; i++)
                rooms[i] = new Room(getApplication(), parent_layout, database, 23, 1, room_numbers[i]);
            int[] facility_numbers = database.getFacilityNumber(23,1);
            faclities=new Facility[facility_numbers.length];
            for(int i=0;i<facility_numbers.length;i++)
                faclities[i]=new Facility(getApplication(),parent_layout,database,23,1,facility_numbers[i]);
            for (int i = 0; i < room_numbers.length; i++)
                rooms[i].changeRange();
            for(int i=0;i<facility_numbers.length;i++)
                faclities[i].changeRange();
        }

//        image.scale();
    }

    private Point getViewSize(View v) {
        Point point = new Point(0, 0);
        point.set(v.getWidth(), v.getHeight());
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
                public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float distanceX, float distanceY) {
                    image.onScroll(distanceX, distanceY);
                    for(int i=0;i<faclities.length;i++)
                        faclities[i].onScroll(distanceX,distanceY);
                    for (int i = 0; i < rooms.length; i++)
                        rooms[i].onScroll(distanceX,distanceY);
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
                    float factor = scaleGestureDetector.getScaleFactor();
                    if (focusRange.x == 0 && focusRange.y == 0)
                        focusRange.set((int) scaleGestureDetector.getFocusX(), (int) scaleGestureDetector.getFocusY());
                    image.onScale(focusRange.x, focusRange.y, factor);
                    return false;
                }

                @Override
                public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
                    focusRange = new Point(0, 0);
                    return true;
                }

                @Override
                public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {
                }
            };
}
