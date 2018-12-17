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
import java.util.ArrayList;

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

    private float beforeX;
    private float beforeY;

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

        beforeX = -1;

        mode = 0;
        myView = findViewById(R.id.my_view);

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
        } else if(mode==2){
            menuMode.setTitle("ROOM");
        }else{
            menuMode.setTitle("GET ROAD ID");
        }
        return true;
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
                str += "======== road mode ========\nis_x(bool),start_x(%),start_y(%),length(%)\n";
                invalidateOptionsMenu();
                myView.resetRoads();
                firsttap = true;
                return true;
            case R.id.facility_mode:
                mode = 1;
                str += "======== facility mode ========\nx(%),y(%)\n";
                invalidateOptionsMenu();
                myView.resetRoads();
                firsttap = true;

                return true;
            case R.id.room_mode:
                mode = 2;
                str += "======== room mode ========\nx(%),y(%)\n";
                invalidateOptionsMenu();
                myView.resetRoads();
                firsttap = true;

                return true;
            case R.id.get_roadId_mode:
                mode =3;
                invalidateOptionsMenu();
                myView.resetRoads();

                float [] roadX =database.getRoad_x(building_number, floor);
                float [] roadY = database.getRoad_y(building_number, floor);
                float [] length =database.getRoadLength(building_number, floor);
                boolean [] isXDir=database.getRoad_xDir(building_number, floor);
                float startX[] = new float[roadX.length];
                float startY[] = new float[roadX.length];
                float endX[] = new float[roadX.length];
                float endY[] = new float[roadX.length];
                for(int i=0;i<startX.length;i++){
                    startX[i]=x+roadX[i]*width;
                    startY[i]=y+roadY[i]*height;
                    if(isXDir[i]){
                        endX[i]=x+(roadX[i]+length[i])*width;
                        endY[i]=y+roadY[i]*height;
                    }else{
                        endX[i]=x+roadX[i]*width;
                        endY[i]=y+(roadY[i]+length[i])*height;
                    }
                    myView.drawLine(startX[i],startY[i],endX[i],endY[i]);
                }

                firsttap = true;

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
        myView.resetRoads();
        firsttap = true;
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
                            firstRange[0] = motionEvent.getX();
                            firstRange[1] = motionEvent.getY();
                            if (beforeX != -1) {
                                if (Math.abs(firstRange[0] - beforeX) < 15) {
                                    firstRange[0] = beforeX;
                                } else {
                                    beforeX = firstRange[0];
                                }
                                if (Math.abs(firstRange[1] - beforeY) < 15) {
                                    firstRange[1] = beforeY;
                                } else {
                                    beforeY = firstRange[1];
                                }
                            }
                            float[] startX = myView.getStartX();
                            float[] startY = myView.getStartY();
                            float[] endX = myView.getEndX();
                            float[] endY = myView.getEndY();
                            for (int i = 0; i < startX.length; i++) {
                                if (Math.abs(startX[i] - firstRange[0]) <15 && Math.abs(startY[i] -firstRange[1]) <15){
                                    firstRange[0]=startX[i];
                                    firstRange[1]=startY[i];
                                    break;
                                }else if(Math.abs(endX[i] - firstRange[0]) <15 && Math.abs(endY[i] -firstRange[1]) <15){
                                    firstRange[0]=endX[i];
                                    firstRange[1]=endY[i];
                                }
                            }

                            firsttap = false;
                        } else {
                            secondRange[0] = motionEvent.getX();
                            secondRange[1] = motionEvent.getY();

                            float x = secondRange[0] - firstRange[0];
                            float y = secondRange[1] - firstRange[1];
                            //x方向への移動
                            if (Math.abs(y) / Math.abs(x) <= 1) {
                                if (firstRange[0] < secondRange[0]) {
                                    secondRange[1] = firstRange[1];
                                } else {
                                    float tmp[] = firstRange;
                                    firstRange = secondRange;
                                    secondRange = tmp;
                                    firstRange[1] = secondRange[1];
                                }
                            }
                            //y方向への移動
                            else {
                                if (firstRange[1] < secondRange[1]) {
                                    secondRange[0] = firstRange[0];
                                } else {
                                    float tmp[] = firstRange;
                                    firstRange = secondRange;
                                    secondRange = tmp;
                                    firstRange[0] = secondRange[0];
                                }
                            }

                            beforeX = firstRange[0];
                            beforeY = firstRange[1];

                            float startX = (firstRange[0] - mapImageView.getX()) / mapImageView.getWidth();
                            float startY = (firstRange[1] - mapImageView.getY()) / mapImageView.getHeight();
                            float endX = (secondRange[0] - mapImageView.getX()) / mapImageView.getWidth();
                            float endY = (secondRange[1] - mapImageView.getY()) / mapImageView.getHeight();


                            float length = (secondRange[0] - firstRange[0]) / mapImageView.getWidth();

                            boolean is_xDirection = true;
                            if (startX == endX) {
                                is_xDirection = !is_xDirection;
                                length = (secondRange[1] - firstRange[1]) / mapImageView.getHeight();
                            }


                            str += is_xDirection + "," + startX + "," + startY + "," + length + "\n";
                            myView.drawLine(firstRange[0], firstRange[1], secondRange[0], secondRange[1]);
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
                    case 3:
                        if(firsttap) {
                            firstRange[0] = motionEvent.getX();
                            firstRange[1] = motionEvent.getY();
                            firsttap=!firsttap;
                        }else{
                            secondRange[0]=motionEvent.getX();
                            secondRange[1]=motionEvent.getY();
                            float[] startX = myView.getStartX();
                            float[] startY=myView.getStartY();
                            float[] endX=myView.getEndX();
                            float[] endY=myView.getEndY();
                            ArrayList<Integer> result = new ArrayList<Integer>();
                            for(int i=0;i<startX.length;i++){
                                if(Math.abs(startX[i]-firstRange[0])<30 && Math.abs(startY[i]-firstRange[1])<30 &&
                                        Math.abs(endX[i]-secondRange[0])<30 && Math.abs(endY[i]-secondRange[1])<30){
                                    result.add(i+1);
                                }
                            }
                            Toast toast;
                            if(result.size()==0){
                                toast=Toast.makeText(MainActivity.this,"search road id : Nothing Found",Toast.LENGTH_LONG);
                            }else if(result.size()==1){
                                toast=Toast.makeText(MainActivity.this,"search road id : Found! road id is:  "+result.get(0),Toast.LENGTH_LONG);
                            }else{
                                toast=Toast.makeText(MainActivity.this,"search road id : Multiple road found..  ",Toast.LENGTH_LONG);
                            }
                            toast.show();

                            firsttap=!firsttap;
                        }
                }
            }
            return true;
        }
    };
}
