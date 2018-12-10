package com.example.a81809.getloadrange;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Time;

public class MainActivity extends AppCompatActivity {

    ImageView mapImageView;
    FrameLayout parent_layout;
    Point viewMaxSize;
    MyView myView;
    private DatabaseRead database;
    private int building_number;
    private int floor;

    String str;
    private int mode;

    private int width;
    private int height;
    private float x;
    private float y;

    private long beforeTouch = 0;

    float firstRange[];
    float secondRange[];
    int loadnumber;
    boolean firsttap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapImageView = findViewById(R.id.mapImageView);
        parent_layout = findViewById(R.id.parent_layout);

        database = new DatabaseRead(getApplication(), "database.db");

        firstRange = new float[2];
        secondRange = new float[2];

        mode = 0;
        myView = findViewById(R.id.my_view);
//        setContentView(myView);

        loadnumber = 0;
        firsttap = true;
        str = "======== road mode ========\n";

        parent_layout.setOnTouchListener(mTouchEventListener);

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (mapImageView.getWidth() != 0) {
            viewMaxSize = getViewSize(parent_layout);
        }
    }

    public static Point getViewSize(View View) {
        Point point = new Point(0, 0);
        point.set(View.getWidth(), View.getHeight());
        return point;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        MenuItem menuMode = menu.findItem(R.id.mode);
        if (mode == 0) {
            menuMode.setTitle("ROAD");
        } else if (mode == 1) {
            menuMode.setTitle("FACILITY");
        } else {
            menuMode.setTitle("ROOM");
        }
        return true;
    }

    public void changeImageSize(View v) {
        float facter = 1;
        if (viewMaxSize.x / getViewSize(v).x >= viewMaxSize.y / getViewSize(v).y)
            facter = getViewSize(mapImageView).y / viewMaxSize.y;
        else
            facter = getViewSize(v).x / viewMaxSize.x;
        FrameLayout.LayoutParams frameLayoutParams;
        frameLayoutParams = new FrameLayout.LayoutParams((int) (getViewSize(v).x * facter), (int) (getViewSize(v).y * facter));
        mapImageView.setLayoutParams(frameLayoutParams);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.file:
                Intent intent1 = new Intent(MainActivity.this, ResultActivity.class);
                intent1.putExtra("str", str);
                startActivity(intent1);
                return true;
            case R.id.selectImage:
                Intent intent2 = new Intent(MainActivity.this, SelectImageActivity.class);
                int requestCode = 1001;
                startActivityForResult(intent2, requestCode);
                return true;
            case R.id.clear:
                return true;
            case R.id.road_mode:
                mode = 0;
                str += "======== road mode ========\nid,start_x(%),start_y(%),is_x(bool),length(%)\n";
                invalidateOptionsMenu();
                return true;
            case R.id.facility_mode:
                mode = 1;
                str += "======== facility mode ========\nx(%),y(%)\n";
                invalidateOptionsMenu();
                return true;
            case R.id.room_mode:
                mode = 2;
                str += "======== room mode ========\nx(%),y(%)\n";
                invalidateOptionsMenu();
                return true;
        }
        return false;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        // startActivityForResult()の際に指定した識別コードとの比較
        if (requestCode == 1001) {

            // 返却結果ステータスとの比較
            if (resultCode == Activity.RESULT_OK) {
                // 返却されてきたintentから値を取り出す
                building_number = intent.getIntExtra("building_name", 0);
                floor = intent.getIntExtra("floor", 0);
                if (building_number != 999) {
                    setImage(building_number, floor);
                }
            }
        }
    }

    public void setImage(int building_number, int floor) {
        String[] imageSize = database.getFloorImageSize(building_number, floor);
        String name = database.getImageResource(building_number, floor);
        int id = getResources().getIdentifier(name, "drawable", getPackageName());
        mapImageView.setImageResource(id);
        width = Integer.parseInt(imageSize[0]);
        height = Integer.parseInt(imageSize[1]);
        float factor;
        if ((float) viewMaxSize.x / width < (float) viewMaxSize.y / height) {
            factor = (float) viewMaxSize.x / width;
        } else {
            factor = (float) viewMaxSize.y / height;
        }
        width = (int) (width * factor);
        height = (int) (height * factor);
        x = (viewMaxSize.x - width) / 2;
        y = (viewMaxSize.y - height) / 2;
        mapImageView.setLayoutParams(new FrameLayout.LayoutParams(width, height));
        mapImageView.setX(x);
        mapImageView.setY(y);
        str += String.valueOf(building_number) + "," + String.valueOf(floor) + "\n";
    }

    private View.OnTouchListener mTouchEventListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getEventTime() - beforeTouch > 300) {
                beforeTouch = motionEvent.getEventTime();

                float xper;
                float yper;
                switch (mode) {
                    case 0:
                        if (firsttap) {
//                            firstRange[0] = motionEvent.getX() - mapImageView.getX();
//                            firstRange[1] = motionEvent.getY() - mapImageView.getY();
                            firstRange[0] = motionEvent.getX();
                            firstRange[1] = motionEvent.getY();
//                        now.setText("2");
                            firsttap = false;
                        } else {
                            secondRange[0] = motionEvent.getX();
                            secondRange[1] = motionEvent.getY();
//                            secondRange[0] = motionEvent.getX() - mapImageView.getX();
//                            secondRange[1] = motionEvent.getY() - mapImageView.getY();
//
//                            float x = secondRange[0] - firstRange[0];
//                            float y = secondRange[1] - firstRange[1];
//                            //x方向への移動
//                            if (Math.abs(y) / Math.abs(x) <= 1) {
//                                if (firstRange[0] < secondRange[0]) {
//                                    secondRange[1] = firstRange[1];
//                                } else {
//                                    float tmp[] = firstRange;
//                                    firstRange = secondRange;
//                                    secondRange = tmp;
//                                    secondRange[1] = firstRange[1];
//                                }
//                            }
//                            //y方向への移動
//                            else {
//                                if (firstRange[1] < secondRange[1]) {
//                                    secondRange[0] = firstRange[0];
//                                } else {
//                                    float tmp[] = firstRange;
//                                    firstRange = secondRange;
//                                    secondRange = tmp;
//                                    secondRange[0] = firstRange[0];
//                                }
//                            }
                            myView.drawLine(firstRange[0], firstRange[1], secondRange[0], secondRange[1]);
                            //                        now.setText("1");
                            firsttap = true;

                        }
                        return true;
                    case 1:
                        xper = (motionEvent.getX() - x) / width;
                        yper = (motionEvent.getY() - y) / height;
                        str += xper + "," + yper + "\n";
                        return true;
                    case 2:
                        xper = (motionEvent.getX() - x) / width;
                        yper = (motionEvent.getY() - y) / height;
                        str += xper + "," + yper + "\n";
                        return true;
                }
            }
            return true;
        }
    };

//    public class imageTouchListener implements View.OnTouchListener {
//        @Override
//        public boolean onTouch(View v, MotionEvent event) {
//            switch (event.getAction()) {
//                case MotionEvent.ACTION_DOWN:
//                    switch (mode) {
//                        case 0:
//                            if (firsttap) {
//                                firstRange[0] = event.getX() - mapImageView.getX();
//                                firstRange[1] = event.getY() - mapImageView.getY();
////                        now.setText("2");
//                                firsttap = false;
//                            } else {
//                                secondRange[0] = event.getX() - mapImageView.getX();
//                                secondRange[1] = event.getY() - mapImageView.getY();
//
//                                float x = secondRange[0] - firstRange[0];
//                                float y = secondRange[1] - firstRange[1];
//                                //x方向への移動
//                                if (Math.abs(y) / Math.abs(x) <= 1) {
//                                    if (firstRange[0] < secondRange[0]) {
//                                        secondRange[1] = firstRange[1];
//                                    } else {
//                                        float tmp[] = firstRange;
//                                        firstRange = secondRange;
//                                        secondRange = tmp;
//                                        secondRange[1] = firstRange[1];
//                                    }
//                                }
//                                //y方向への移動
//                                else {
//                                    if (firstRange[1] < secondRange[1]) {
//                                        secondRange[0] = firstRange[0];
//                                    } else {
//                                        float tmp[] = firstRange;
//                                        firstRange = secondRange;
//                                        secondRange = tmp;
//                                        secondRange[0] = firstRange[0];
//                                    }
//                                }
////                        myView.setLine(firstRange[0],firstRange[1],secondRange[0],secondRange[1],mapImageView.getX(),mapImageView.getY());
////                        now.setText("1");
//                                firsttap = true;
//
//                            }
//                            break;
//                        case 1:
//                            break;
//                        case 2:
//
//                    }
//                    break;
//                case MotionEvent.ACTION_UP:
//
//                    break;
//                case MotionEvent.ACTION_CANCEL:
//                    break;
//            }
//            return false;
//        }
//    }
}
