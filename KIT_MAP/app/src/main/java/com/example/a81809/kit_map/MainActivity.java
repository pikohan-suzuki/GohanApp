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
import java.util.ArrayList;
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
    public DatabaseRead database;

    public static Point screenSize;
    public static Point actionBarSize;
    public static int building_number;
    public static int floor;
    private Point focusRange;
    private boolean touchFlg = true;
    private boolean isSearchMode = false;
    private boolean locationShowing = false;

    private String lastUpdateTime;


    private FusedLocationProviderClient fusedLocationClient;
    private SettingsClient settingsClient;
    private LocationSettingsRequest locationSettingsRequest;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private Location location;

    private Boolean requestingLocationUpdates;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private int priority = 0;

    private int[][] search_route;
//    private int[] search_route_id;

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

        road = findViewById(R.id.my_view);

        changeFloor();

        uiManager = new UIManager(getApplication(), parent_layout);
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

            case R.id.search:
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                int requestCode = 1001;
                startActivityForResult(intent, requestCode);
                break;

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
                    parent_layout.removeView(road);
                    road.onScroll(image.getImageSize(), image.getImageLocation(), distanceX, distanceY);
                    parent_layout.addView(road);
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
                                if (touchFlg) setUI();
                                break;
                            case -1055568: //23
                                building_number = 23;
                                floor = 1;
                                removeViews();
                                changeFloor();
                                if (touchFlg) setUI();
                                break;
                            case -12457: //5
                                building_number = 5;
                                floor = 1;
                                removeViews();
                                changeFloor();
                                if (touchFlg) setUI();
                                break;
                            case -12713: //3
                                building_number = 3;
                                floor = 1;
                                removeViews();
                                changeFloor();
                                if (touchFlg) setUI();
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
                        if (touchFlg)
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

                    float factor = scaleGestureDetector.getScaleFactor();
                    if (focusRange.x == 0 && focusRange.y == 0)
                        focusRange.set((int) scaleGestureDetector.getFocusX(), (int) scaleGestureDetector.getFocusY());
                    image.onScale(focusRange.x, focusRange.y, factor);
                    for (int i = 0; i < faclities.length; i++)
                        faclities[i].onScale(image.getImageSize(), image.getImageLocation());
                    for (int i = 0; i < rooms.length; i++)
                        rooms[i].onScale(image.getImageSize(), image.getImageLocation());
                    parent_layout.removeView(road);
                    road.onScale(image.getImageSize(), image.getImageLocation());
                    parent_layout.addView(road);
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

    private void setLocationIcon() {
        float[] range = image.getimageRange();
        if (location != null) {
            if (location.getLongitude() > range[0] && location.getLongitude() < range[0] + range[2]
                    && location.getLatitude() < range[1] && location.getLatitude() > range[1] - range[3]) {
                myLocation.setLocationIcon(parent_layout, locationShowing);
                if (!locationShowing) locationShowing = !locationShowing;
                Toast toast = Toast.makeText(this, "Added", Toast.LENGTH_SHORT);
                toast.show();
            } else if (locationShowing) {
                myLocation.removeLocationIcon(parent_layout);
                locationShowing = !locationShowing;
                Toast toast = Toast.makeText(this, "removed", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    private View.OnClickListener upButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int numberOfFloor = database.getNumberOfFloor(building_number);
            if (floor < numberOfFloor) {
                floor++;
                removeViews();
                changeFloor();
            }
        }
    };
    private View.OnClickListener downButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (floor != 1) {
                floor--;
                removeViews();
                changeFloor();
            }
        }
    };

    private void setUI() {
        uiManager.setUI(parent_layout);
    }

    private void removeUI() {
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

        parent_layout.removeView(road);

        int [] roadId = database.getRoadId(building_number,floor);
        float [] roadX =database.getRoad_x(building_number, floor);
        float [] roadY = database.getRoad_y(building_number, floor);
        float [] length =database.getRoadLength(building_number, floor);
        boolean [] isXDir=database.getRoad_xDir(building_number, floor);
        if(isSearchMode) {
            ArrayList<Float> x = new ArrayList<Float>();
            ArrayList<Float> y = new ArrayList<Float>();
            ArrayList<Float> len = new ArrayList<Float>();
            ArrayList<Boolean> isX = new ArrayList<Boolean>();
            ArrayList<Integer> asdf = new ArrayList<Integer>();
            for(int i=0;i<roadId.length;i++){
                for(int j=0;j<search_route.length;j++){
                    if(roadId[i]==search_route[j][2]){
                        if(building_number==search_route[j][0] && floor==search_route[j][1]){
                            x.add(roadX[i]);
                            y.add(roadY[i]);
                            len.add(length[i]);
                            isX.add(isXDir[i]);
                        }
                    }
                }
            }
//            for(int i=0;i<search_route_id.length;i++) asdf.add(search_route_id[i]);
//            for(int i=0;i<roadId.length;i++){
//                if(asdf.contains(roadId[i]) && search_route[i][0]==building_number && search_route[i][1] == floor){
//                    x.add(roadX[i]);
//                    y.add(roadY[i]);
//                    len.add(length[i]);
//                    isX.add(isXDir[i]);
//                }
//            }
            roadX = new float[x.size()];
            roadY=new float[x.size()];
            length=new float[x.size()];
            isXDir=new boolean[x.size()];
            for(int i=0;i<x.size();i++){
                roadX[i]=x.get(i);
                roadY[i]=y.get(i);
                length[i]=len.get(i);
                isXDir[i]=isX.get(i);
            }
        }
        road.setInfo(roadX, roadY, length, isXDir, image.getImageSize(), image.getImageLocation());
        parent_layout.addView(road);
        setLocationIcon();
    }

    private void removeViews() {
        image.removeView(parent_layout);
        for (Room room : rooms) room.removeView(parent_layout);
        for (Facility facility : faclities) facility.removeView(parent_layout);
    }

//    private void resetRoute() {
//        routeBuilding = new ArrayList<Integer>();
//        routeFloor = new ArrayList<Integer>();
//        routeId = new ArrayList<Integer>();
//    }

    private int[][] search_route_inFloor(int[][] connectTable, float[] roadLength, int startBuildingNumber, int startFloor, int startRoadId, int destRoadId) {
        float[] distanceToRoad = new float[roadLength.length];
        int[] comeFrom = new int[roadLength.length];
        ArrayList<Integer> unsettledRoad = new ArrayList<Integer>();
        ArrayList<Integer> calculatingRoad = new ArrayList<Integer>();
        for (int i = 0; i < distanceToRoad.length; i++) unsettledRoad.add(i + 1);
        unsettledRoad.remove(unsettledRoad.indexOf(startRoadId));
        for (int i = 0; i < distanceToRoad.length; i++) distanceToRoad[i] = 9999;
        distanceToRoad[startRoadId - 1] = 0;
        int location =startRoadId;

        while(unsettledRoad.indexOf(destRoadId)!=-1){
            for(int i=0;i<connectTable.length;i++){
                if(connectTable[i][0]==location){
                    if(unsettledRoad.indexOf(connectTable[i][1])!=-1){
                        if(distanceToRoad[connectTable[i][1]-1] > distanceToRoad[connectTable[i][0]-1]+roadLength[connectTable[i][1]-1]){
                            if(distanceToRoad[connectTable[i][1]-1]==9999)
                                calculatingRoad.add(connectTable[i][1]);
                            distanceToRoad[connectTable[i][1]-1]=distanceToRoad[connectTable[i][0]-1]+roadLength[connectTable[i][1]-1];
                            comeFrom[connectTable[i][1]-1]=location;
                        }
                    }
                }else if(connectTable[i][0] >location) break;
            }
            Log.d("debug","pppppp"+calculatingRoad);
            Log.d("debug","oooooo"+unsettledRoad);
            float min = 9999;
            int id = 0;
            for(int calcId: calculatingRoad){
                if(distanceToRoad[calcId-1] <min){
                    min = distanceToRoad[calcId-1];
                    id = calcId;
                }
            }
            if(id!=0) {
                unsettledRoad.remove(unsettledRoad.indexOf(id));
                calculatingRoad.remove(calculatingRoad.indexOf(id));
                location = id;
            }else{
                Log.d("debug","the end");
            }
        }

        ArrayList<Integer> resultRoute = new ArrayList<Integer>();

        int now = destRoadId;
        while(now!=0){
            resultRoute.add(now);
            now=comeFrom[now-1];
        }
        int[][] result = new int[resultRoute.size()][3];
        for(int i=0;i<resultRoute.size();i++) {
            result[i][0] = startBuildingNumber;
            result[i][1] = startFloor;
            result[i][2] = resultRoute.get(resultRoute.size() - 1 - i);
        }
        return result;
    }
    private int[][] searchRouteInBuilding(int[][] connectTable, ArrayList<ArrayList<Float>> roadLength, int startBuildingNumber, int startFloor, int startRoadId, int destFloor, int destRoadId){
        ArrayList<ArrayList<Float>> distanceToRoad = new ArrayList<ArrayList<Float>>();
        for(ArrayList<Float> list : roadLength){
            ArrayList<Float> maxList = new ArrayList<Float>();
            for(float value : list){
                maxList.add((float) 9999);
            }
            distanceToRoad.add(maxList);
        }
        


        int[] comeFrom = new int[roadLength.length];
        ArrayList<Integer> unsettledRoad = new ArrayList<Integer>();
        ArrayList<Integer> calculatingRoad = new ArrayList<Integer>();
        for (int i = 0; i < distanceToRoad.length; i++) unsettledRoad.add(i + 1);
        unsettledRoad.remove(unsettledRoad.indexOf(startRoadId));
        for (int i = 0; i < distanceToRoad.length; i++) distanceToRoad[i] = 9999;
        distanceToRoad[startRoadId - 1] = 0;
        int location =startRoadId;
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

    public void onActivityResult(int requestCode,
                                 int resultCode, Intent intent) {
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
            default:
                if (resultCode == Activity.RESULT_OK) {
                    int startBuildingNumber = intent.getIntExtra("startBuildingNumber", 0);
                    int startFloor = intent.getIntExtra("startFloor", 0);
                    int startRoadId = intent.getIntExtra("startRoadId", 0);
                    int destBuildingNumber = intent.getIntExtra("destBuildingNumber", 0);
                    int destFloor = intent.getIntExtra("destFloor", 0);
                    int destRoadId = intent.getIntExtra("destRoadId", 0);
                    if (startBuildingNumber == destBuildingNumber) {
                        if (startFloor == destFloor) {
                            int[][] connectTable = database.getFloorDir(startBuildingNumber, startFloor);
                            float[] roadLength = database.getRoadLength(startBuildingNumber, startFloor);
                            int[][] resultRoute = search_route_inFloor(connectTable, roadLength, startBuildingNumber, startFloor, startRoadId,destRoadId);
                            search_route=new int[resultRoute.length][3];
                            search_route=resultRoute;
                            isSearchMode=true;
                            removeViews();
                            changeFloor();
                        }else{
                            int[][] connectTable = database.getBuildingDir(startBuildingNumber);
                            int numberOfFloor = database.getNumberOfFloor(startBuildingNumber);
                            ArrayList<ArrayList<Float>> roadLength = database.getBuildingRoadLength(startBuildingNumber,numberOfFloor);
                            int[][] resultRoute = searchRouteInBuilding(connectTable, roadLength, startBuildingNumber, startFloor,startRoadId,destFloor,destRoadId);
                        }
                    }
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
