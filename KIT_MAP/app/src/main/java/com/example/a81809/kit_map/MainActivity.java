package com.example.a81809.kit_map;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.location.Location;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.FrameLayout;
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
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private Image image;
    private Room[] rooms;
    private Facility[] faclities;
    private UIManager uiManager;
    private Road road;
    private MyLocation myLocation;
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
    private boolean locationShowing = false;


    private FusedLocationProviderClient fusedLocationClient;
    private SettingsClient settingsClient;
    private LocationSettingsRequest locationSettingsRequest;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private Location location;

    private String lastUpdateTime;
    private Boolean requestingLocationUpdates;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private int priority = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(getApplication(), CheckPermission.class);
        startActivity(intent);

        database = new DatabaseRead(getApplication(), "database.db");
        parent_layout = findViewById(R.id.parent_layout);
        building_number = 0;
        floor = 0;
        screenSize = new Point(0, 0);
        Display display = this.getWindowManager().getDefaultDisplay();
        display.getSize(screenSize);

        road =findViewById(R.id.my_view);

        changeFloor();
        uiManager= new UIManager(getApplication(),parent_layout);
        UIManager.upButton.setOnClickListener(upButtonClickListener);
        UIManager.downButton.setOnClickListener(downButtonClickListener);

        myLocation = new MyLocation(getApplication());

        //タッチイベント
        parent_layout.setOnTouchListener(mTouchEventListener);
        //スクロールイベント
        mGestureDetector = new GestureDetector(this, mGestureListener);
        //スケールイベント
        mScaleGestureDetector = new ScaleGestureDetector(this, mScaleGestureListener);





        fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(this);
        settingsClient = LocationServices.getSettingsClient(this);

        priority = 0;

        createLocationCallback();
        createLocationRequest();
        buildLocationSettingsRequest();


//        // 測位開始
//        Button buttonStart = (Button) findViewById(R.id.button_start);
//        buttonStart.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
                startLocationUpdates();
//            }
//        });

//        // 測位終了
//        Button buttonStop = (Button) findViewById(R.id.button_stop);
//        buttonStop.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                stopLocationUpdates();
//            }
//        });
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
                    road.onScroll(image.getImageSize(),image.getImageLocation(),distanceX,distanceY);
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

    private void setLocationIcon(){
        float[] range = image.getimageRange();
        if(location!=null) {
            if (location.getLongitude() > range[0] && location.getLongitude() < range[0] + range[2]
                    && location.getLatitude() < range[1] && location.getLatitude() > range[1] - range[3]) {
                myLocation.setLocationIcon(parent_layout,locationShowing);
                if(!locationShowing)    locationShowing=!locationShowing;
                Toast toast = Toast.makeText(this,"Added",Toast.LENGTH_SHORT);
                toast.show();
            } else if(locationShowing) {
                myLocation.removeLocationIcon(parent_layout);
                locationShowing=!locationShowing;
                Toast toast = Toast.makeText(this,"removed",Toast.LENGTH_SHORT);
                toast.show();
            }
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

        road.drawLine(database.getRoad_x(building_number,floor),database.getRoad_y(building_number,floor),
                database.getRoadLength(building_number,floor),database.getRoad_xDir(building_number,floor),image.getImageSize().x,image.getImageSize().y,image.getImageLocation().x,image.getImageLocation().y);
        setLocationIcon();
    }

    private void removeViews() {
        image.removeView(parent_layout);
        for (Room room : rooms) room.removeView(parent_layout);
        for (Facility facility : faclities) facility.removeView(parent_layout);
    }



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
        setLocationIcon();
    }

    private void createLocationRequest() {
        locationRequest = new LocationRequest();

        if (priority == 0) {
            // 高い精度の位置情報を取得したい場合
            // インターバルを例えば5000msecに設定すれば
            // マップアプリのようなリアルタイム測位となる
            // 主に精度重視のためGPSが優先的に使われる
            locationRequest.setPriority(
                    LocationRequest.PRIORITY_HIGH_ACCURACY);

        } else if (priority == 1) {
            // バッテリー消費を抑えたい場合、精度は100mと悪くなる
            // 主にwifi,電話網での位置情報が主となる
            // この設定の例としては　setInterval(1時間)、setFastestInterval(1分)
            locationRequest.setPriority(
                    LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        } else if (priority == 2) {
            // バッテリー消費を抑えたい場合、精度は10kmと悪くなる
            locationRequest.setPriority(
                    LocationRequest.PRIORITY_LOW_POWER);

        } else {
            // 受け身的な位置情報取得でアプリが自ら測位せず、
            // 他のアプリで得られた位置情報は入手できる
            locationRequest.setPriority(
                    LocationRequest.PRIORITY_NO_POWER);
        }

        // アップデートのインターバル期間設定
        // このインターバルは測位データがない場合はアップデートしません
        // また状況によってはこの時間よりも長くなることもあり
        // 必ずしも正確な時間ではありません
        // 他に同様のアプリが短いインターバルでアップデートしていると
        // それに影響されインターバルが短くなることがあります。
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
}
