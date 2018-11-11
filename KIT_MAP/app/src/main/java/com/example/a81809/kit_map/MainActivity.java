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
import android.os.Build;
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
import android.view.ViewTreeObserver;
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

import java.io.File;
import java.sql.Time;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private GestureDetector mGestureDetector;
    private ScaleGestureDetector mScaleGestureDetector;

    private FrameLayout drawer_layout;  //メインのレイアウト
    private ImageView mImageView;   //マップを表示するイメージビュー
    private Button center_button;   //画像を元に戻すボタン
    private Button up_button;       //▲ボタン
    private Button down_button;     //▼ボタン
    private TextView buildingtextView;  //建物名を表示しているテキスト
    private TextView floorTextView;     //階層を表示しているテキスト
    private TextView room1[] = new TextView[10];    //部屋名を表示するテキスト配列
    private LinearLayout info_layout;   //部屋名をタップしたときに出てくるポップアップ
    private Button goto_button;         //ここにいくのボタン
    private Button info_button;         //INFOボタン
    private LinearLayout info_sideBar;  //部屋情報を表示するサイドバー
    private ImageView room_image;       //部屋の画像
    private Button bSearchButton;       //🔎ボタン
    private EditText searchEditText;    //検索の入力
    private ListView forecastListView;  // 予測検索のリストビュー
    private ImageView locationImageView;    //現在地の◎ボタン（屋内)
    private ImageView outdoorImageView;

    private int imageWidth;         //画像の現在の幅
    private int imageHeight;        //画像の現在の高さ
    private float maxImageWidth;    //最大の画像幅
    private float maxImageHeight;   //最大の画像の高さ
    private float minImageWidth;    //最小の画像の幅
    private float minImageHeight;   //最小の画像の高さ
    private float defaultX;         //画像のデフォルトx座標
    private float defaultY;         //画像のデフォルトy座標
    private float defaultHeight;    //画像のデフォルトheight
    private float defaultWidth;     //画像のデフォルトwidth
    private boolean infoFlag = false;       //infoポップアップが表示されているかのフラグ
    private boolean roomInfoFlag = false;    //roomInfoサイドバーが表示されているかのフラグ
    private boolean searchFlag = false;     //searchEditTextが表示されているかのフラグ
    private float defaultRoomTextSize;      //部屋名のデフォルトテキストサイズ
    private int numberOfRooms;              //現在表示している階層の部屋の数

    //位置情報取得用の変数
    private FusedLocationProviderClient fusedLocationClient;
    private SettingsClient settingsClient;
    private LocationSettingsRequest locationSettingsRequest;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private Location location;

    private String lastUpdateTime;
    private Boolean requestingLocationUpdates;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;

    private ViewTreeObserver.OnGlobalLayoutListener mGlobalLayoutListener;

    private int k;  //部屋をしている添え字（変更予定）


    private final double imageLongitude = 0.000858; //x　経度(変更予定）
    private final double imageLatitude = 0.000379; //y　緯度（変更予定）
    private final double imageLeftLongitude = 136.6291380; //23号館の西側の経度  136.629138083022
    private final double imageTopLatitude = 36.531387;  //23号館の北側の緯度
    private boolean firstFlg = true;    //初めて自位置情報を取得することを判断するフラグ
    private double firstLatitude;       //初期値の経度
    private double firstLongitude;      //初期値の緯度
    private double latestLatitude;      //最新の経度
    private double latestLongitude;     //最新の緯度

    //予測検索表示用のアダプター
    private ArrayAdapter<String> arrayAdapter;

    //画像の大きさ変更用の変数
    private FrameLayout.LayoutParams frameLayoutParams;
    private LinearLayout.LayoutParams linearLayoutParams;


    private int building = 1;           //建物番号
    private int floor = 0;              //階層番号
    //フロアの画像配列
    private int[][] floorImage = {{R.drawable.school_map},{R.drawable.b23_1, R.drawable.b23_2, R.drawable.b23_3, R.drawable.b23_4, R.drawable.b23_5}};
    //部屋の位置(画像端からの%)
    private float[][][][] roomRange = {{{{0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}},
            {{0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}},
            {{0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}},
            {{0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}},
            {{0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}}},
            {{{0.5f, 0.25f}, {0.01f, 0.5f}, {0.45f, 0.8f}, {0.75f, 0.25f}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}},
                    {{0.5f, 0.5f}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}},
                    {{0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}},
                    {{0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}},
                    {{0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}}}};
    //部屋の名前
    private String[][][] roomName = {{{"", "", "", "", "", "", "", "", "", ""},
            {"", "", "", "", "", "", "", "", "", ""},
            {"", "", "", "", "", "", "", "", "", ""},
            {"", "", "", "", "", "", "", "", "", ""},
            {"", "", "", "", "", "", "", "", "", ""}},
            {{"23-4\nコミュニケーション\nスタジオ", "23-101\n学生ステーション", "23-106\nパフォーミング\nスタジオ", "23-102\nコラボレーション\nスタジオ", "", "", "", "", "", ""},
                    {"テスト\n2階", "", "", "", "", "", "", "", "", ""},
                    {"", "", "", "", "", "", "", "", "", ""},
                    {"", "", "", "", "", "", "", "", "", ""},
                    {"", "", "", "", "", "", "", "", "", ""}}};
    //部屋の名前（検索用）
    private String[][][] searchRoomName = {{{"", "", "", "", "", "", "", "", "", ""},
            {"", "", "", "", "", "", "", "", "", ""},
            {"", "", "", "", "", "", "", "", "", ""},
            {"", "", "", "", "", "", "", "", "", ""},
            {"", "", "", "", "", "", "", "", "", ""}},
            {{"コミュニケーションスタジオ 23-104", "学生ステーション 23-101", "パフォーミングスタジオ 23-106", "コラボレーションスタジオ 23-102", "", "", "", "", "", ""},
                    {"テスト2階 23-299", "", "", "", "", "", "", "", "", ""},
                    {"", "", "", "", "", "", "", "", "", ""},
                    {"", "", "", "", "", "", "", "", "", ""},
                    {"", "", "", "", "", "", "", "", "", ""}}};

    //建物の名前
    private String[] buildingName = {"", "23", "", "", "", "", "", "", "", "", ""};

    private double[] buildingLeftLongitude = {136.626368, 136.6291380};
    private double[] buildingTopLatitude = {36.532157, 36.531387};
    private double[] buildingLongitude = {0.004656, 0.000858};
    private double[] buildingLatitude = {0.002556, 0.000379};
    private double[][] floorAltitude = {{0}, {56.599998, 60.9, 65.3, 69.2, 73.3}};
    //屋外用の変数
    private ImageView outdoor_locationImageView;    //現在地の◎ボタン(屋外)
    private double outImageLongitute = 0.001324;
    private double outImageLatitude = 0.000585;
    private double outLeftLongitute = 136.628923583022;
    private double outTopLatitude = 36.5314899181035;

    private double xRangeMargin;
    private double yRangeMargin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(getApplication(), checkPermissionActivity.class);
        startActivity(intent);

        mImageView = findViewById(R.id.indoor_image);
        drawer_layout = findViewById(R.id.drawer_layout);
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

        fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(this);
        settingsClient = LocationServices.getSettingsClient(this);
        createLocationCallback();
        createLocationRequest();
        buildLocationSettingsRequest();
        startLocationUpdates();

        mImageView.setImageResource(floorImage[building][0]);
        outdoorImageView=findViewById(R.id.outdoor_image);
        outdoorImageView.setImageResource(R.drawable.school_map);
        numberOfRooms = 10;
        for (int j = 0; j < 10; j++) {
            if (!(roomName[building][floor][j] != "" && searchRoomName[building][floor][j] != "" && roomRange[building][floor][j][0] != 0.f)) {
                numberOfRooms = j;
                break;
            }
        }

        for (int i = 0; i < 10; i++) {
            room1[i] = new TextView(this);
            if (i < numberOfRooms) {
                room1[i].setText(roomName[building][floor][i]);
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

                locationImageView.setX(((maxImageWidth - locationImageView.getWidth()) / 2));
                locationImageView.setY((maxImageHeight - locationImageView.getHeight()) / 2);

                for (int i = 0; i < numberOfRooms; i++) {
                    float textMarginX = defaultX + defaultWidth * roomRange[building][floor][i][0];
                    float textMarginY = defaultY + defaultHeight * roomRange[building][floor][i][1];
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
                    changeFloor();
                }
            }
        });

        down_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (floor > 0) {
                    floor--;
                    changeFloor();
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
                        if (searchRoomName[building][i][j].contains(s) && searchRoomName[building][i][j] != "") {
                            rooms.add(rooms.size(), searchRoomName[building][i][j]);
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



        mGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                removeOnGlobalLayoutListener(mImageView.getViewTreeObserver(), mGlobalLayoutListener);
            }
        };
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

        maxImageHeight = drawer_layout.getHeight();
        maxImageWidth = drawer_layout.getWidth();
        minImageHeight = drawer_layout.getHeight() * 0.25f;
        minImageWidth = drawer_layout.getWidth() * 0.25f;
        defaultHeight = mImageView.getHeight();
        defaultWidth = mImageView.getWidth();
        defaultRoomTextSize = 14 * 1280 / maxImageWidth;
        setImageInfo();

        for (int i = 0; i < numberOfRooms; i++) {
            float textMarginX = defaultX + defaultWidth * roomRange[building][floor][i][0];
            float textMarginY = defaultY + defaultHeight * roomRange[building][floor][i][1];
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
            if (!(roomName[building][floor][j] != "" && searchRoomName[building][floor][j] != "" && roomRange[building][floor][j][0] != 0.f)) {
                numberOfRooms = j;
                break;
            }
        }
        for (int i = 0; i < 10; i++) {
            if (i < numberOfRooms) {
                room1[i].setText(roomName[building][floor][i]);
                float textMarginX = defaultX + defaultWidth * roomRange[building][floor][i][0];
                float textMarginY = defaultY + defaultHeight * roomRange[building][floor][i][1];
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
//        ColorDrawable colorDrawable = (ColorDrawable) drawer_layout.getBackground();
        if (color == 0) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isIndoor(int color) {
        if (color == -1055568) {
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
                    //画像を拡大
                    frameLayoutParams = new FrameLayout.LayoutParams((int) (imageWidth * factor), (int) (imageHeight * factor));
                    mImageView.setLayoutParams(frameLayoutParams);
                    //現在地を変更
                    locationImageView.setX(mImageView.getX() + (locationImageView.getX() - mImageView.getX() + (locationImageView.getWidth() * mImageView.getWidth() / defaultWidth)) * factor - locationImageView.getWidth() * mImageView.getWidth() / defaultWidth);//
                    locationImageView.setY(mImageView.getY() + (locationImageView.getY() - mImageView.getY() + (locationImageView.getHeight() * mImageView.getHeight() / defaultHeight)) * factor - locationImageView.getHeight() * mImageView.getHeight() / defaultHeight);//
                    for (int i = 0; i < numberOfRooms; i++) {
                        float textMarginX = mImageView.getX() + mImageView.getWidth() * roomRange[building][floor][i][0] * factor;
                        float textMarginY = mImageView.getY() + mImageView.getHeight() * roomRange[building][floor][i][1] * factor;
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

            if (firstFlg) {
                firstFlg = false;
                firstLatitude = location.getLatitude();
                firstLongitude = location.getLongitude();
                latestLatitude = firstLatitude;
                latestLongitude = firstLongitude;
                Toast toast = Toast.makeText(this, "firstLocationChanged.", Toast.LENGTH_SHORT);
                toast.show();
                locationImageView.setX((maxImageWidth - locationImageView.getWidth()) / 2);
                locationImageView.setY((maxImageHeight - locationImageView.getHeight()) / 2);
            } else {
//            double marginX = ((buildingLeftLongitude[building] + buildingLongitude[building] / 2 - location.getLongitude()) * mImageView.getWidth() / buildingLongitude[building]);
//            double marginY = ((buildingTopLatitude[building] - buildingLatitude[building] / 2 - location.getLatitude()) * mImageView.getHeight() / buildingLatitude[building]);
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                double marginX;
                double marginY;
                if(building!=0) {
                    marginX = ((location.getLongitude() - buildingLeftLongitude[building]) * mImageView.getWidth() / buildingLongitude[building]);
                    marginY = ((buildingTopLatitude[building] - location.getLatitude()) * mImageView.getHeight() / buildingLatitude[building]);
                }else{
                    double xx = outdoorImageView.getWidth();
                    marginX = ((location.getLongitude() - buildingLeftLongitude[building]) * 3158 / buildingLongitude[building]);
                    marginY = ((buildingTopLatitude[building] - location.getLatitude()) * 2670 / buildingLatitude[building]);
                }
                Toast toast = Toast.makeText(this, "lati:" + location.getLatitude() + "long:" + location.getLongitude(), Toast.LENGTH_SHORT);
            toast.show();
            double dd = locationImageView.getX();
            double ddd=locationImageView.getWidth();
            mImageView.setX((float)(-marginX+maxImageWidth/2+locationImageView.getWidth()/2) +locationImageView.getX()-maxImageWidth/2);
            mImageView.setY((float)(-marginY+maxImageHeight/2+locationImageView.getHeight()/2)+ locationImageView.getY() +locationImageView.getHeight()/2 - maxImageHeight/2);
//            mImageView.setX((float) marginX + ((locationImageView.getX() - maxImageWidth / 2) + locationImageView.getWidth() / 2) + (maxImageWidth - mImageView.getWidth()) / 2);
//            mImageView.setY((float) -marginY + ((locationImageView.getY() - maxImageHeight / 2) + locationImageView.getHeight() / 2) + (maxImageHeight - mImageView.getHeight()) / 2);
            for (int i = 0; i < numberOfRooms; i++) {
//                room1[i].setX((float) (marginX + ((locationImageView.getX() - maxImageWidth / 2) + locationImageView.getWidth() / 2) + (maxImageWidth - mImageView.getWidth()) / 2) + roomRange[building][floor][i][0] * mImageView.getWidth());
//                room1[i].setY((float) (-marginY + ((locationImageView.getY() - maxImageHeight / 2) + locationImageView.getHeight() / 2) + (maxImageHeight - mImageView.getHeight()) / 2) + roomRange[building][floor][i][1] * mImageView.getHeight());
                room1[i].setX((mImageView.getX()+ mImageView.getWidth()*roomRange[building][floor][i][0]));
                room1[i].setY((mImageView.getY()+ mImageView.getHeight()*roomRange[building][floor][i][1]));
            }
            latestLatitude = location.getLatitude();
            latestLongitude = location.getLongitude();
            if (building != 0) {
                if (location.getAltitude() >= floorAltitude[building][0]) {
                    if (location.getAltitude() > floorAltitude[building][floorAltitude[building].length - 1]) {
                        floor = floorAltitude[building].length - 1;
                        changeFloor();
                    } else {
                        for (int i = 1; i < floorAltitude[building].length; i++) {
                            if (location.getAltitude() < floorAltitude[building][i]) {
                                floor = i - 1;
                                changeFloor();
                                break;
                            }
                        }
                    }
                }
            }
            if (!(floor != 0 && building != 0)) {
                Bitmap capture = getViewCapture(mImageView);
                if (capture != null) {
//                    double x = (locationImageView.getX() - ((float) marginX + ((locationImageView.getX() - maxImageWidth / 2) + locationImageView.getWidth() / 2) + (maxImageWidth - mImageView.getWidth()) / 2) + locationImageView.getWidth() / 2);
//                    double y = (locationImageView.getY() - ((float) -marginY + ((locationImageView.getY() - maxImageHeight / 2) + locationImageView.getHeight() / 2) + (maxImageHeight - mImageView.getHeight()) / 2) + locationImageView.getHeight() / 2);
                    double x = marginX;
                    double y = marginY;
                    if (x > 0 && x < mImageView.getWidth() && y > 0 && y < mImageView.getHeight()) {

                        int coughtColor = capture.getPixel((int) x, (int) y);
                        Log.d("debug", "coughtColor" + coughtColor);
                        if (building != 0) {
                            if (isOutdoor(coughtColor)) {
                                goToOutdoor(location.getLongitude(), location.getLatitude());
                            }
                        } else {
                            if (isIndoor(coughtColor)) {
//                                goToRoom(coughtColor, location.getLongitude(), location.getLatitude());
                            }
                        }

                    } else if(building!=0) {
                        goToOutdoor(location.getLongitude(), location.getLatitude());
                    }
                } else {
                    Log.d("debug", "getBitmapColor: failed.");
                }
                }
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

    private void goToOutdoor( double longitude, double latitude) {

        mImageView.setImageResource(R.drawable.school_map);
        mImageView.getViewTreeObserver().addOnGlobalLayoutListener(mGlobalLayoutListener);
        building = 0;
        floor = 0;
        setImageInfo();
        setRoomName();
        try {
            TimeUnit.MILLISECONDS.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void goToRoom(int color, double longitude, double latitude) {
        switch (color) {
            case -1055568:
                building = 1;
                floor = 0;
                mImageView.setImageResource(floorImage[building][floor]);
                setImageInfo();
                setRoomName();
                try {
                    TimeUnit.MILLISECONDS.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
        }
    }

    private void changeFloor() {
        mImageView.setImageResource(floorImage[building][floor]);

        try {
            TimeUnit.MILLISECONDS.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        setImageInfo();
        setRoomName();
    }




    private static void removeOnGlobalLayoutListener(ViewTreeObserver observer, ViewTreeObserver.OnGlobalLayoutListener listener) {
        if (observer == null) {
            return ;
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            observer.removeGlobalOnLayoutListener(listener);
        } else {
            observer.removeOnGlobalLayoutListener(listener);
        }
    }
}
