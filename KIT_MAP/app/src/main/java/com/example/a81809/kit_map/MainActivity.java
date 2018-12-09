package com.example.a81809.kit_map;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.SearchView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private Image image;
    private Room[] rooms;
    private Facility[] faclities;
    private UIManager uiManager;
    private FrameLayout parent_layout;
    private GestureDetector mGestureDetector;
    private ScaleGestureDetector mScaleGestureDetector;
    public  DatabaseRead database;

    public static Point screenSize;
    public static Point actionBarSize;
    public static int building_number;
    public static int floor;
    private Point focusRange;
    private boolean touchFlg = true;
    private boolean isMapMode = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = new DatabaseRead(getApplication(), "database.db");
        parent_layout = findViewById(R.id.parent_layout);
        building_number = 0;
        floor = 0;
        screenSize = new Point(0, 0);
        Display display = this.getWindowManager().getDefaultDisplay();
        display.getSize(screenSize);

        changeFloor();
        uiManager= new UIManager(getApplication(),parent_layout);
        removeUI();
        UIManager.upButton.setOnClickListener(upButtonClickListener);
        UIManager.downButton.setOnClickListener(downButtonClickListener);

        //タッチイベント
        parent_layout.setOnTouchListener(mTouchEventListener);
        //スクロールイベント
        mGestureDetector = new GestureDetector(this, mGestureListener);
        //スケールイベント
        mScaleGestureDetector = new ScaleGestureDetector(this, mScaleGestureListener);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.b23_1:
                if (building_number != 23 || floor != 1) {
                    building_number = 23;
                    floor = 1;
                    removeViews();
                    changeFloor();
                }
                return true;
            case R.id.b23_2:
                if (building_number != 23 || floor != 2) {
                    building_number = 23;
                    floor = 2;
                    removeViews();
                    changeFloor();
                }
                return true;
            case R.id.b23_3:
                if (building_number != 23 || floor != 3) {
                    building_number = 23;
                    floor = 3;
                    removeViews();
                    changeFloor();
                }
                return true;
        }
        return false;
    }

    private void changeFloor() {
        image = new Image(getApplication(), parent_layout, database, building_number, floor);
        int[] room_numbers = database.getRoomNumbers(building_number, floor);
        rooms = new Room[room_numbers.length];
        for (int i = 0; i < room_numbers.length; i++)
            rooms[i] = new Room(getApplication(), parent_layout, database, building_number, floor,
                    room_numbers[i], image.getImageSize(), image.getImageLocation());
        int[] facility_numbers = database.getFacilityNumber(building_number, floor);
        faclities = new Facility[facility_numbers.length];
        for (int i = 0; i < facility_numbers.length; i++)
            faclities[i] = new Facility(getApplication(), parent_layout, database, building_number, floor,
                    facility_numbers[i], image.getImageSize(), image.getImageLocation());
    }

    private void removeViews() {
        image.removeView(parent_layout);
        for (Room room : rooms) room.removeView(parent_layout);
        for (Facility facility : faclities) facility.removeView(parent_layout);
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
//                    if (touchFlg) {
//                        touchFlg = false;
//                        hideActionBar();
//                    }
                    image.onScroll(distanceX, distanceY);
                    for (int i = 0; i < faclities.length; i++)
                        faclities[i].onScroll(image.getImageSize(), image.getImageLocation(), distanceX, distanceY);
                    for (int i = 0; i < rooms.length; i++)
                        rooms[i].onScroll(image.getImageSize(), image.getImageLocation(), distanceX, distanceY);
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
                    Bitmap bitmap = getViewCapture(parent_layout);
                    int color = bitmap.getPixel((int) motionEvent.getX(), (int) motionEvent.getY());
                    if (building_number == 0) {
                        switch (color) {
                            case -12199: //8
                                building_number = 8;
                                floor = 1;
                                removeViews();
                                changeFloor();
                                if(touchFlg) setUI();
                                break;
                            case -1055568: //23
                                building_number = 23;
                                floor = 1;
                                removeViews();
                                changeFloor();
                                if(touchFlg) setUI();
                                break;
                            case -12457: //5
                                building_number = 5;
                                floor = 1;
                                removeViews();
                                changeFloor();
                                if(touchFlg) setUI();
                                break;
                            case -12713: //3
                                building_number = 3;
                                floor = 1;
                                removeViews();
                                changeFloor();
                                if(touchFlg) setUI();
                                break;
                            default:
                                if (touchFlg) {
                                    touchFlg = false;
                                    hideActionBar();
                                } else {
                                    touchFlg = true;
                                    showActionBar();
                                }
                                break;
                        }
                    } else if (color == -2687049) {
                        building_number = 0;
                        floor = 0;
                        removeViews();
                        changeFloor();
                        if(touchFlg)
                            removeUI();
                    } else {
                        if (touchFlg) {
                            touchFlg = false;
                            hideActionBar();
                            removeUI();
                        } else {
                            touchFlg = true;
                            showActionBar();
                            setUI();
                        }
                    }
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
//                    if (touchFlg) {
//                        touchFlg = false;
//                        hideActionBar();
//                    }
                    float factor = scaleGestureDetector.getScaleFactor();
                    if (focusRange.x == 0 && focusRange.y == 0)
                        focusRange.set((int) scaleGestureDetector.getFocusX(), (int) scaleGestureDetector.getFocusY());
                    image.onScale(focusRange.x, focusRange.y, factor);
                    for (int i = 0; i < faclities.length; i++)
                        faclities[i].onScale(image.getImageSize(), image.getImageLocation());
                    for (int i = 0; i < rooms.length; i++)
                        rooms[i].onScale(image.getImageSize(), image.getImageLocation());
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

    private void hideActionBar() {
        getSupportActionBar().hide();
//        image.hideActionBar();
//        for (Facility facility:faclities) facility.hideActionBar();
//        for (Room room : rooms) room.hideActionBar();

    }

    private void showActionBar() {
        getSupportActionBar().show();
//        image.showActionBar();
//        for (Facility faclity : faclities) faclity.showActionBar();
//        for (Room room : rooms) room.showActionBar();
    }

    //スクリーンショットの撮影
    private Bitmap getViewCapture(View view) {
        view.setDrawingCacheEnabled(true);
        Bitmap cache = view.getDrawingCache();
        if (cache != null) {
            Bitmap screenShot = Bitmap.createBitmap(cache);
            view.setDrawingCacheEnabled(false);
            return screenShot;
        } else {
            return null;
        }
    }

    private View.OnClickListener upButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int numberOfFloor =database.getNumberOfFloor(building_number);
            if(floor < numberOfFloor){
                floor++;
                removeViews();
                changeFloor();
            }
        }
    };
    private View.OnClickListener downButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(floor!=1){
                floor--;
                removeViews();
                changeFloor();
            }
        }
    };
    private void setUI(){
       uiManager.setUI(parent_layout);
    }
    private void removeUI(){
        uiManager.removeUI(parent_layout);
    }

}
