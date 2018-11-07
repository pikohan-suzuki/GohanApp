package com.example.a81809.kit_map;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private GestureDetector mGestureDetector;
    private ScaleGestureDetector mScaleGestureDetector;

    private FrameLayout drawer_layout;
    private ImageView mImageView;   //マップを表示するイメージビュー
    private Button center_button;   //画像を元に戻すボタン
    private Button up_button;
    private Button down_button;
    private TextView buildingtextView;
    private TextView floorTextView;
    private TextView room1[] = new TextView[10];
    private LinearLayout info_layout;
    private Button goto_button;
    private Button info_button;
    private LinearLayout info_sideBar;
    private ImageView room_image;
    private Button bSearchButton;
    private EditText searchEditText;
    private ListView forecastListView;
    private ImageView locationImageView;

    private int imageWidth;         //画像の現在の幅
    private int imageHeight;        //画像の現在の高さ
    private float maxImageWidth;    //最大の画像幅
    private float maxImageHeight;   //最大の画像の高さ
    private float minImageWidth;    //最小の画像の幅
    private float minImageHeight;   //最小の画像の高さ
    private float defaultX;         //画像のデフォルトx座標
    private float defaultY;         //画像のデフォルトy座標
    private float defaultHeight;
    private float defaultWidth;
    private boolean infoFlag = false;       //infoポップアップが表示されているかのフラグ
    private boolean roomInfoFlag = false;    //roomInfoサイドバーが表示されているかのフラグ
    private boolean searchFlag = false;     //searchEditTextが表示されているかのフラグ
    private float defaultRoomTextSize;
    private int numberOfRooms;

    private FusedLocationProviderClient fusedLocationClient;
    private SettingsClient settingsClient;
    private LocationSettingsRequest locationSettingsRequest;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private Location location;

    private String lastUpdateTime;
    private Boolean requestingLocationUpdates;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;

    private int k;


    private final double imageLongitude = 0.000858; //x
    private final double imageLatitude = 0.000379; //y
    private final double imageTopLongitude = 36.531387;
    private final double imageLeftLatitude = 136.629138083022;
    private boolean firstFlg =true;
    private double firstLatitude;
    private double firstLongitude;


    private ArrayAdapter<String> arrayAdapter;


    private FrameLayout.LayoutParams frameLayoutParams;
    private LinearLayout.LayoutParams linearLayoutParams;


    private int building = 0;           //建物番号
    private int floor = 0;              //階層番号
    private int[] floorImage = {R.drawable.b23_1, R.drawable.b23_2, R.drawable.b23_3, R.drawable.b23_4, R.drawable.b23_5};
    private float[][][] roomRange = {{{0.5f, 0.25f}, {0.01f, 0.5f}, {0.45f, 0.8f}, {0.75f, 0.25f}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}},
            {{0.5f, 0.5f}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}},
            {{0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}},
            {{0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}},
            {{0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}}};
    private String[][] roomName = {{"23-4\nコミュニケーション\nスタジオ", "23-101\n学生ステーション", "23-106\nパフォーミング\nスタジオ", "23-102\nコラボレーション\nスタジオ", "", "", "", "", "", ""},
            {"テスト\n2階", "", "", "", "", "", "", "", "", ""},
            {"", "", "", "", "", "", "", "", "", ""},
            {"", "", "", "", "", "", "", "", "", ""},
            {"", "", "", "", "", "", "", "", "", ""}};
    private String[][] searchRoomName = {{"コミュニケーションスタジオ 23-104", "学生ステーション 23-101", "パフォーミングスタジオ 23-106", "コラボレーションスタジオ 23-102", "", "", "", "", "", ""},
            {"テスト2階 23-299", "", "", "", "", "", "", "", "", ""},
            {"", "", "", "", "", "", "", "", "", ""},
            {"", "", "", "", "", "", "", "", "", ""},
            {"", "", "", "", "", "", "", "", "", ""}};
    private String[] buildingName = {"23", "", "", "", "", "", "", "", "", ""};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(getApplication(), checkPermissionActivity.class);
        startActivity(intent);

        drawer_layout = findViewById(R.id.drawer_layout);
        mImageView = findViewById(R.id.droid_Image);
        center_button = findViewById(R.id.center_button);
        up_button = findViewById(R.id.up_button);
        down_button = findViewById(R.id.down_button);
        buildingtextView = findViewById(R.id.building_textView);
        floorTextView = findViewById(R.id.floor_textView);
        info_layout = findViewById(R.id.info_layout);
        info_button = findViewById(R.id.info_button);
        goto_button = findViewById(R.id.goTo_button);
        info_sideBar = findViewById(R.id.info_sideBar);
        room_image = findViewById(R.id.roomImageView);
        bSearchButton = findViewById(R.id.b_search_button);
        searchEditText = findViewById(R.id.search_editText);
        forecastListView = findViewById(R.id.search_forecast);
        locationImageView = findViewById(R.id.location);

        info_layout.setVisibility(View.INVISIBLE);
        info_sideBar.setVisibility(View.GONE);
        searchEditText.setVisibility(View.GONE);

        mImageView.setImageResource(floorImage[0]);

        fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(this);
        settingsClient = LocationServices.getSettingsClient(this);
        createLocationCallback();
        createLocationRequest();
        buildLocationSettingsRequest();
        startLocationUpdates();
        numberOfRooms = 10;
        for (int j = 0; j < 10; j++) {
            if (!(roomName[floor][j] != "" && searchRoomName[floor][j] != "" && roomRange[floor][j][0] != 0.f)) {
                numberOfRooms = j;
                break;
            }
        }

        for (int i = 0; i < 10; i++) {
            room1[i] = new TextView(this);
            if (i < numberOfRooms) {
                room1[i].setText(roomName[floor][i]);
                drawer_layout.addView(room1[i], new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                room1[i].setClickable(true);
                room1[i].setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                room1[i].setBackgroundResource(R.drawable.marukado);
            }
        }

        for (k = 0; k < numberOfRooms; k++) {
            room1[k].setOnClickListener(onTextViewClickListener);
        }


        /* Touch event */
        drawer_layout.setOnTouchListener(mTouchEventLister);
        mGestureDetector = new GestureDetector(this, mGestureListener);
        mScaleGestureDetector = new ScaleGestureDetector(this, mScaleGestureListener);


        //＋ボタンのクリックイベント
        center_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frameLayoutParams = new FrameLayout.LayoutParams((int) defaultWidth, (int) defaultHeight);
                mImageView.setLayoutParams(frameLayoutParams);
                mImageView.setX(defaultX);
                mImageView.setY(defaultY);

                for (int i = 0; i < numberOfRooms; i++) {
                    float textMarginX = defaultX + defaultWidth * roomRange[floor][i][0];
                    float textMarginY = defaultY + defaultHeight * roomRange[floor][i][1];
                    room1[i].setX(textMarginX);
                    room1[i].setY(textMarginY);
                    room1[i].setTextSize(defaultRoomTextSize);
                }
                if (infoFlag) {
                    info_layout.setVisibility(View.INVISIBLE);
                    infoFlag = false;
                }
            }
        });
        //▲ボタンのクリックイベント
        up_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (floor < 4) {
                    floor++;
                    mImageView.setImageResource(floorImage[floor]);
                    setImageInfo();
                    setRoomName();
                }
            }
        });

        down_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (floor > 0) {
                    floor--;
                    mImageView.setImageResource(floorImage[floor]);
                    setImageInfo();
                    setRoomName();
                }
            }
        });


        info_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                info_sideBar.setVisibility(View.VISIBLE);
                linearLayoutParams = new LinearLayout.LayoutParams((int) (maxImageWidth / 3), (int) maxImageHeight / 3);
                room_image.setLayoutParams(linearLayoutParams);
                roomInfoFlag = true;
            }
        });

        goto_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });

        bSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchEditText.setVisibility(View.VISIBLE);
                searchFlag = true;
            }
        });

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("debug", "text: " + s + " start: " + start + " before: " + before + " count: " + count);
                List<String> rooms = new ArrayList<String>();
                for (int i = 0; i < 5; i++) {
                    for (int j = 0; j < 10; j++) {
                        if (searchRoomName[i][j].contains(s) && searchRoomName[i][j] != "") {
                            rooms.add(rooms.size(), searchRoomName[i][j]);
                        }
                    }
                }
                String str[] = rooms.toArray(new String[rooms.size()]);
                createArrayList(str);
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d("debug", "afterTextChanged::" + "text: " + s);
                if (s.length() == 0) {
                    Log.d("debug", "afterTextChanged:: null");
                    String str[] = new String[0];
                    createArrayList(str);
                }
            }
        });

        //リスト項目が選択された時のイベントー
        forecastListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("debug", parent.getAdapter().getItem(position) + "aaaa");
                loop:
                for (int i = 0; i < 5; i++) {
                    for (int j = 0; j < 10; j++) {
                        if (searchRoomName[i][j] == parent.getAdapter().getItem(position)) {
                            floor = i;
                            setImageInfo();
                            setRoomName();
                            room1[j].callOnClick();
                            searchEditText.setText(String.valueOf(parent.getAdapter().getItem(position)));
                            String str[] = new String[0];
                            createArrayList(str);
                            break loop;
                        }
                    }
                }
            }
        });
        //リスト項目が長押しされた時のイベント
        forecastListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                return false;
            }
        });

    }

    private View.OnClickListener onTextViewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Log.d("debug", "TextTapped: " + v);
            float X_moveRange = (v.getX() - (maxImageWidth - v.getWidth()) / 2);
            float Y_moveRange = (v.getY() - (maxImageHeight - v.getHeight()) / 2);
            mImageView.setX(mImageView.getX() - X_moveRange);
            mImageView.setY(mImageView.getY() - Y_moveRange);
            for (int i = 0; i < numberOfRooms; i++) {
                Log.d("debug", "room1: X:" + room1[i].getX() + " maxImageWidth: " + maxImageWidth + " v.Width: " + v.getWidth() + " v.X: " + v.getX());
                room1[i].setX(room1[i].getX() - X_moveRange);
                room1[i].setY(room1[i].getY() - Y_moveRange);

            }

            info_layout.setX(v.getX() + (v.getWidth() - info_layout.getWidth()) / 2);
            info_layout.setY(v.getY() - info_layout.getHeight() - 10);
            Log.d("debug", "roomX: " + v.getX() + " roomWidth: " + v.getWidth() + " infoWidth: " + info_layout.getWidth());
            info_layout.setVisibility(View.VISIBLE);
            infoFlag = true;

        }
    };

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display disp = wm.getDefaultDisplay();
        Point realSize = new Point();
        disp.getRealSize(realSize);
        int realScreenWidth = realSize.x;
        int realScreenHeight = realSize.y;

        maxImageHeight = realScreenHeight;
        maxImageWidth = realScreenWidth;
        minImageHeight = realScreenHeight * 0.25f;
        minImageWidth = realScreenWidth * 0.25f;
        defaultHeight = mImageView.getHeight();
        defaultWidth = mImageView.getWidth();
        defaultRoomTextSize = 14*1280/maxImageWidth;
        setImageInfo();

        for (int i = 0; i < numberOfRooms; i++) {
            float textMarginX = defaultX + defaultWidth * roomRange[floor][i][0];
            float textMarginY = defaultY + defaultHeight * roomRange[floor][i][1];
            room1[i].setX(textMarginX);
            room1[i].setY(textMarginY);
            room1[i].setTextSize(defaultRoomTextSize);
        }
    }

    private void createArrayList(String[] array) {
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, array);
        forecastListView.setAdapter(arrayAdapter);
    }

    private void setImageInfo() {
        imageWidth = mImageView.getWidth();
        imageHeight = mImageView.getHeight();
        mImageView.setX((maxImageWidth - imageWidth) / 2);
        mImageView.setY((maxImageHeight - imageHeight) / 2.5f);
        defaultX = mImageView.getX();
        defaultY = mImageView.getY();
        floorTextView.setText("F" + (floor + 1));
    }

    private void setRoomName() {
        numberOfRooms = 10;
        for (int j = 0; j < 10; j++) {
            if (!(roomName[floor][j] != "" && searchRoomName[floor][j] != "" && roomRange[floor][j][0] != 0.f)) {
                numberOfRooms = j;
                break;
            }
        }
        for (int i = 0; i < 10; i++) {
            if (i < numberOfRooms) {
                room1[i].setText(roomName[floor][i]);
                float textMarginX = defaultX + defaultWidth * roomRange[floor][i][0];
                float textMarginY = defaultY + defaultHeight * roomRange[floor][i][1];
                room1[i].setX(textMarginX);
                room1[i].setY(textMarginY);
                room1[i].setTextSize(defaultRoomTextSize);
            } else {
                room1[i].setText("");
            }
        }
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

    //屋内か屋外かの判断
    private boolean isOutdoor(int color) {
        ColorDrawable colorDrawable = (ColorDrawable) drawer_layout.getBackground();
        if (color == (colorDrawable.getColor())) {
            return true;
        } else {
            return false;
        }
    }

    private View.OnTouchListener mTouchEventLister = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent e) {
            mScaleGestureDetector.onTouchEvent(e);
            mGestureDetector.onTouchEvent(e);
            if (infoFlag) {
                info_layout.setVisibility(View.INVISIBLE);
                infoFlag = false;
            }
            if (roomInfoFlag) {
                info_sideBar.setVisibility(View.GONE);
                roomInfoFlag = false;
            }
            if (searchFlag) {
                searchEditText.setVisibility(View.GONE);
                searchEditText.setText("");
                searchFlag = false;
                String array[] = new String[0];
                createArrayList(array);
            }
            Log.d("debug", "onTouch");

            return true;
        }
    };

    private ScaleGestureDetector.OnScaleGestureListener mScaleGestureListener
            = new ScaleGestureDetector.OnScaleGestureListener() {

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            Log.d("debug", "on Scale Begin");

            return true;
        }

        public boolean onScale(ScaleGestureDetector detector) {
            mGestureDetector.setIsLongpressEnabled(false);
            Log.d("debug", "on Scale");
            float factor = detector.getScaleFactor();
            if (factor < 2.0) {
                imageHeight = mImageView.getHeight();
                imageWidth = mImageView.getWidth();
                if (imageHeight * factor < maxImageHeight && imageWidth * factor < maxImageWidth
                        && imageHeight * factor > minImageHeight && imageWidth * factor > minImageWidth) {
                    frameLayoutParams = new FrameLayout.LayoutParams((int) (imageWidth * factor), (int) (imageHeight * factor));
                    mImageView.setLayoutParams(frameLayoutParams);
                    for (int i = 0; i < numberOfRooms; i++) {
                        float textMarginX = mImageView.getX() + mImageView.getWidth() * roomRange[floor][i][0] * factor;
                        float textMarginY = mImageView.getY() + mImageView.getHeight() * roomRange[floor][i][1] * factor;
                        room1[i].setX(textMarginX);
                        room1[i].setY(textMarginY);
                        if (Math.abs(mImageView.getWidth() - defaultWidth) < 25) {
                            room1[i].setTextSize(defaultRoomTextSize);
                        } else {
                            room1[i].setTextSize(defaultRoomTextSize * mImageView.getWidth() / defaultWidth);
                        }
                    }
                }
            }
            mGestureDetector.setIsLongpressEnabled(true);
            return true;
        }

        public void onScaleEnd(ScaleGestureDetector detector) {
            Log.d("debug", "on Scale End.");

        }
    };

    private GestureDetector.OnGestureListener mGestureListener =
            new GestureDetector.OnGestureListener() {
                @Override
                public boolean onDown(MotionEvent motionEvent) {
                    Log.d("debug", "onDown");
                    return false;
                }

                @Override
                public void onShowPress(MotionEvent motionEvent) {
                    Log.d("debug", "onShowPress");
                }

                @Override
                public boolean onSingleTapUp(MotionEvent motionEvent) {
                    Log.d("debug", "onSingleTapUp");
                    Bitmap capture = getViewCapture(drawer_layout);
                    if (capture != null) {
                        int coughtColor = capture.getPixel((int) motionEvent.getX(), (int) motionEvent.getY());
                        if (isOutdoor(coughtColor)) {
                            Toast toast = Toast.makeText(MainActivity.this, "屋外です。", Toast.LENGTH_SHORT);
                            setContentView(R.layout.outdoor_map);

                            toast.show();
                        } else {
                            Toast toast = Toast.makeText(MainActivity.this, "屋内です。", Toast.LENGTH_SHORT);
                            toast.show();
                        }

                    } else {
                        Log.d("debug", "getBitmapColor: failed.");
                    }
                    return false;
                }

                @Override
                public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float distanceX, float distanceY) {
                    Log.d("debug", "onScroll");
                    mImageView.setX(mImageView.getX() - distanceX * 0.5f);
                    mImageView.setY(mImageView.getY() - distanceY * 0.5f);
                    locationImageView.setX(locationImageView.getX() - distanceX * 0.5f);
                    locationImageView.setY(locationImageView.getY() - distanceY * 0.5f);
                    for (int i = 0; i < numberOfRooms; i++) {
                        room1[i].setX(room1[i].getX() - distanceX * 0.5f);
                        room1[i].setY(room1[i].getY() - distanceY * 0.5f);
                    }
                    Log.d("debug", "x:" + mImageView.getX() + " v:" + distanceX + " setY:" + mImageView.getY() +
                            " b1:" + distanceY + "before_x:" + motionEvent1.getX() + " after_x:" + motionEvent1.getY());
                    return false;
                }

                @Override
                public void onLongPress(MotionEvent motionEvent) {
                    Log.d("debug", "onLongPress");
                }

                @Override
                public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
                    Log.d("debug", "onFling");
                    return false;
                }
            };

    // locationのコールバックを受け取る
    private void createLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                location = locationResult.getLastLocation();

                lastUpdateTime = DateFormat.getTimeInstance().format(new Date());
                updateLocationUI();
            }
        };
    }

    private void updateLocationUI() {
        // getLastLocation()からの情報がある場合のみ
        if (location != null) {

            if(firstFlg){
                firstFlg=false;
                firstLatitude=location.getLatitude();
                firstLongitude=location.getLongitude();
                Toast toast = Toast.makeText(this,"firstLocationChanged.",Toast.LENGTH_SHORT);
                toast.show();
                locationImageView.setX((maxImageWidth-locationImageView.getWidth())/2);
                locationImageView.setY((maxImageHeight-locationImageView.getHeight())/2);
            }else{
//                double getX = mImageView.getX();
//                double loca = location.getLatitude();
//                double Wid = mImageView.getHeight();
                double marginX = mImageView.getX() -((location.getLongitude() - firstLongitude)*mImageView.getWidth()/imageLongitude);
                double marginY = mImageView.getY() -((location.getLatitude()-firstLatitude)*mImageView.getHeight()/imageLatitude);
                Toast toast = Toast.makeText(this,"lati:"+location.getLatitude()+"long:"+location.getLongitude(),Toast.LENGTH_SHORT);
                toast.show();
                mImageView.setX((float)marginX);
                mImageView.setY((float)marginY);
                firstLatitude=location.getLatitude();
                firstLongitude=location.getLongitude();
            }

        }
    }

    private void createLocationRequest() {
        locationRequest = new LocationRequest();

        locationRequest.setPriority(
                LocationRequest.PRIORITY_HIGH_ACCURACY);

        // 単位：msec
        locationRequest.setInterval(1000);
        // このインターバル時間は正確です。これより早いアップデートはしません。
        // 単位：msec
        locationRequest.setFastestInterval(1000);

    }

    // 端末で測位できる状態か確認する。wifi, GPSなどがOffになっているとエラー情報のダイアログが出る
    private void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder =
                new LocationSettingsRequest.Builder();

        builder.addLocationRequest(locationRequest);
        locationSettingsRequest = builder.build();
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.i("debug", "User agreed to make required location settings changes.");
                        // Nothing to do. startLocationupdates() gets called in onResume again.
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.i("debug", "User chose not to make required location settings changes.");
                        requestingLocationUpdates = false;
                        break;
                }
                break;
        }
    }

    // FusedLocationApiによるlocation updatesをリクエスト
    private void startLocationUpdates() {
        // Begin by checking if the device has the necessary location settings.
        settingsClient.checkLocationSettings(locationSettingsRequest)
                .addOnSuccessListener(this,
                        new OnSuccessListener<LocationSettingsResponse>() {
                            @Override
                            public void onSuccess(
                                    LocationSettingsResponse locationSettingsResponse) {
                                Log.i("debug", "All location settings are satisfied.");

                                // パーミッションの確認
                                if (ActivityCompat.checkSelfPermission(
                                        MainActivity.this,
                                        Manifest.permission.ACCESS_FINE_LOCATION) !=
                                        PackageManager.PERMISSION_GRANTED
                                        && ActivityCompat.checkSelfPermission(
                                        MainActivity.this,
                                        Manifest.permission.ACCESS_COARSE_LOCATION) !=
                                        PackageManager.PERMISSION_GRANTED) {

                                    // TODO: Consider calling
                                    //    ActivityCompat#requestPermissions
                                    // here to request the missing permissions, and then overriding
                                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                    //                                          int[] grantResults)
                                    // to handle the case where the user grants the permission. See the documentation
                                    // for ActivityCompat#requestPermissions for more details.
                                    return;
                                }
                                fusedLocationClient.requestLocationUpdates(
                                        locationRequest, locationCallback, Looper.myLooper());

                            }
                        })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i("debug", "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings ");
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(
                                            MainActivity.this,
                                            REQUEST_CHECK_SETTINGS);

                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i("debug", "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e("debug", errorMessage);
                                Toast.makeText(MainActivity.this,
                                        errorMessage, Toast.LENGTH_LONG).show();

                                requestingLocationUpdates = false;
                        }

                    }
                });

        requestingLocationUpdates = true;
    }

    private void stopLocationUpdates() {
        if (!requestingLocationUpdates) {
            Log.d("debug", "stopLocationUpdates: " +
                    "updates never requested, no-op.");


            return;
        }

        fusedLocationClient.removeLocationUpdates(locationCallback)
                .addOnCompleteListener(this,
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                requestingLocationUpdates = false;
                            }
                        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        // バッテリー消費を鑑みLocation requestを止める
        stopLocationUpdates();
    }
//    @Override
//    protected void onResume(){
//        startLocationUpdates();
//    }
}
