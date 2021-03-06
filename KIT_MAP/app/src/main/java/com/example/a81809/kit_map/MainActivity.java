package com.example.a81809.kit_map;

import android.Manifest;
import android.graphics.Color;
import android.support.v7.widget.SearchView;
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
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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

    private SearchView mSearchView;

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

    private String[][] search_rooms;
    private ArrayList<String> roomSearchResult;
    private ArrayAdapter searchRoomAdapter;
    private ListView serachRoomListView;

    public static int selectingRoomNum;
    public static LinearLayout roomPopUpLayout;
    public static  TextView roomInfoTextView;
    public static  TextView goToRoomTextView;
    public static  LinearLayout roomInfoLayout;
    public static ImageView roomImageView;
    public static TextView roomDescriptionTextView;

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
        roomPopUpLayout = findViewById(R.id.room_menu);
        roomInfoTextView = findViewById(R.id.room_info);
        goToRoomTextView = findViewById(R.id.go_to_room);
        roomInfoLayout = findViewById(R.id.room_info_layout);
        roomImageView = findViewById(R.id.room_image);
        roomDescriptionTextView = findViewById(R.id.room_description);
        parent_layout.removeView(roomInfoLayout);
        parent_layout.removeView(roomPopUpLayout);

        MainActivity.roomInfoTextView.setOnClickListener(roomInfoClickListener);
        MainActivity.goToRoomTextView.setOnClickListener(goToRoomClickListener);

        road = findViewById(R.id.my_view);
        myLocation = new MyLocation(getApplication());
        changeFloor();

        uiManager = new UIManager(getApplication(), parent_layout);
        UIManager.upButton.setOnClickListener(upButtonClickListener);
        UIManager.downButton.setOnClickListener(downButtonClickListener);

        search_rooms=database.getSearchRoom();
        serachRoomListView=new ListView(this);

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

        mSearchView = (SearchView) menu.findItem(R.id.searchView).getActionView();
        mSearchView.setOnQueryTextListener(queryTextListener);
        return true;
    }
    private SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String s) {
            Toast toast = Toast.makeText(MainActivity.this,"success",Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }

        @Override
        public boolean onQueryTextChange(String s) {

            roomSearchResult = new ArrayList<String>();
            int numberOfForecast = 0;
            for (int i = 0; i < search_rooms.length; i++) {
                if(s.length()!=0) {
                    if (search_rooms[i][0].contains(s) || search_rooms[i][1].contains(s) || search_rooms[i][2].contains(s) ||
                            (search_rooms[i][0] + "-" + search_rooms[i][1] + " " + search_rooms[i][2]).contains(s)) {
                        if(search_rooms[i][2].contains("号館"))
                            roomSearchResult.add(search_rooms[i][2]);
                        else
                            roomSearchResult.add(search_rooms[i][0] + "-" + search_rooms[i][1] + " " + search_rooms[i][2]);
                        numberOfForecast++;
                    }
                }
            }
            searchRoomAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, roomSearchResult);
            serachRoomListView.setAdapter(searchRoomAdapter);
            serachRoomListView.setBackgroundColor(Color.argb(255,240,240,250));
            if (numberOfForecast < 6)
                serachRoomListView.setLayoutParams(new FrameLayout.LayoutParams(screenSize.x/3, ViewGroup.LayoutParams.WRAP_CONTENT));
            else
                serachRoomListView.setLayoutParams(new FrameLayout.LayoutParams(screenSize.x/3, screenSize.y/4));
            serachRoomListView.setX(screenSize.x/2-screenSize.x/6);
            serachRoomListView.setY(parent_layout.getY());

            if (parent_layout.indexOfChild(serachRoomListView) == -1)
                parent_layout.addView(serachRoomListView);
            return false;
        }

    };

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
                    parent_layout.removeView(roomPopUpLayout);
                    image.onScroll(distanceX, distanceY);
                    for (int i = 0; i < faclities.length; i++)
                        faclities[i].onScroll(image.getImageSize(), image.getImageLocation(), distanceX, distanceY);
                    for (int i = 0; i < rooms.length; i++)
                        rooms[i].onScroll(image.getImageSize(), image.getImageLocation(), distanceX, distanceY);
                    parent_layout.removeView(road);
                    road.onScroll(image.getImageSize(), image.getImageLocation(), distanceX, distanceY);
                    parent_layout.addView(road);
                    locationShowing=myLocation.setLocationIcon(parent_layout,locationShowing,location,image.getImageLocation(),image.getImageSize());
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
                    parent_layout.removeView(roomInfoLayout);
                    int color = bitmap.getPixel((int) motionEvent.getX(), (int) motionEvent.getY());
                    Log.d("debug","color : "+color);
                    if(parent_layout.indexOfChild(roomPopUpLayout)==-1) {
                        if (building_number == 0) {
                            switch (color) {
                                case -142749: //1
                                    building_number = 1;
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
                                case -538540: //2
                                    building_number = 2;
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
                                case -143009: //6
                                    building_number = 6;
                                    floor = 1;
                                    removeViews();
                                    changeFloor();
                                    if (touchFlg) setUI();
                                    break;
                                case -12196: //7
                                    building_number = 7;
                                    floor = 1;
                                    removeViews();
                                    changeFloor();
                                    if (touchFlg) setUI();
                                    break;
                                case -12199: //8
                                    building_number = 8;
                                    floor = 1;
                                    removeViews();
                                    changeFloor();
                                    if (touchFlg) setUI();
                                    break;
                                case -77473: //21
                                    building_number = 21;
                                    floor = 1;
                                    removeViews();
                                    changeFloor();
                                    if (touchFlg) setUI();
                                    break;
                                case -11937: //23
                                    building_number = 23;
                                    floor = 1;
                                    removeViews();
                                    changeFloor();
                                    if (touchFlg) setUI();
                                    break;
                                case -11938: //24
                                    building_number = 24;
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
                        } else if (color == -2293834) {
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
                    }else {
                        parent_layout.removeView(roomPopUpLayout);
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
                    parent_layout.removeView(roomPopUpLayout);
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
                    locationShowing=myLocation.setLocationIcon(parent_layout,locationShowing,location,image.getImageLocation(),image.getImageSize());
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


    }

    private void showActionBar() {
        getSupportActionBar().show();

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
        destroyViews();
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


        if(isSearchMode) {
            int [] roadId = database.getRoadId(building_number,floor);
            float [] roadX =database.getRoad_x(building_number, floor);
            float [] roadY = database.getRoad_y(building_number, floor);
            float [] length =database.getRoadLength(building_number, floor);
            boolean [] isXDir=database.getRoad_xDir(building_number, floor);
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
            road.setInfo(roadX, roadY, length, isXDir, image.getImageSize(), image.getImageLocation());
        }
        parent_layout.addView(road);
        double[] range = database.getFloorRangeSize(building_number,floor);
        myLocation.changeFloor(range);
        myLocation.removeLocationIcon(parent_layout);
        locationShowing=false;
        locationShowing=myLocation.setLocationIcon(parent_layout,locationShowing,location, image.getImageLocation(),image.getImageSize());
    }

    private void removeViews() {
        image.removeView(parent_layout);
        for (Room room : rooms) room.removeView(parent_layout);
        for (Facility facility : faclities) facility.removeView(parent_layout);
    }

    private void destroyViews() {
        if (image != null)
            image.removeImageResource();
        if (rooms != null)
            for (Room room : rooms) {
                if (room != null) {
                    room.removeRoomResource();
                }
            }
        if(faclities!=null){
            for(Facility facility:faclities){
                if(facility!=null){
                    facility.removeFacilityResource();
                }
            }
        }

    }



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
                }
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
    private int[][] searchRouteInBuilding(int[][] connectTable, float[][] roadLength, int startBuildingNumber, int startFloor, int startRoadId, int destFloor, int destRoadId){
        float[][] distanceToRoad = new float[roadLength.length][roadLength[0].length];
        for(int i=0;i<distanceToRoad.length;i++){
            for(int j=0;j<distanceToRoad[i].length;j++){
                if(roadLength[i][j]!=-1)
                    distanceToRoad[i][j]=9999;
                else
                    distanceToRoad[i][j]=-1;
            }
        }
        distanceToRoad[startFloor-1][startRoadId-1]=0;
        int[][][]  comeFrom = new int[roadLength.length][roadLength[0].length][2];
        int[][] roadStatus = new int[roadLength.length][roadLength[0].length];  //0:out of target 1:calculating 2:confirmed -1:empty
        for(int i=0;i<distanceToRoad.length;i++){
            for(int j=0;j<distanceToRoad[0].length;j++){
                if(roadLength[i][j]!=-1)
                    roadStatus[i][j]=0;
                else
                    roadStatus[i][j]=-1;
            }
        }
        roadStatus[startFloor-1][startRoadId]=2;
        int[] location ={startFloor,startRoadId};

        while(roadStatus[destFloor-1][destRoadId-1]!=2){
            for(int i=0;i<connectTable.length;i++){
                if(location[0]==connectTable[i][0] && location[1] == connectTable[i][1]){
                    if(roadStatus[connectTable[i][2]-1][connectTable[i][3]-1] != 2){
                        if(distanceToRoad[connectTable[i][2]-1][connectTable[i][3]-1]>
                                distanceToRoad[location[0]-1][location[1]-1]+roadLength[connectTable[i][2]-1][connectTable[i][3]-1]){
                            distanceToRoad[connectTable[i][2]-1][connectTable[i][3]-1]=distanceToRoad[location[0]-1][location[1]-1]+roadLength[connectTable[i][2]-1][connectTable[i][3]-1];
                            comeFrom[connectTable[i][2]-1][connectTable[i][3]-1][0]=location[0];
                            comeFrom[connectTable[i][2]-1][connectTable[i][3]-1][1]=location[1];
                            roadStatus[connectTable[i][2]-1][connectTable[i][3]-1]=1;
                        }
                    }
                }
            }
            int[] next={0,0};
            float min=9999;
            for(int i=0;i<distanceToRoad.length;i++){
                for(int j=0;j<distanceToRoad[i].length;j++){
                    if(roadStatus[i][j]==1 && min > distanceToRoad[i][j]){
                        min=distanceToRoad[i][j];
                        next[0]=i+1;
                        next[1]=j+1;
                    }
                }
            }
            roadStatus[next[0]-1][next[1]-1]=2;
            location[0]=next[0];
            location[1]=next[1];
        }
        ArrayList<ArrayList<Integer>> resultRoute = new ArrayList<ArrayList<Integer>>();
        int[] now={destFloor,destRoadId};
        while(now[0]!=0){
            ArrayList<Integer> list = new ArrayList<Integer>();
            list.add(now[0]);
            list.add(now[1]);
            resultRoute.add(list);
            int next[] = {comeFrom[now[0]-1][now[1]-1][0],comeFrom[now[0]-1][now[1]-1][1]};
            now=next;
        }
        int[][] result = new int[resultRoute.size()][3];
        for(int i=0;i<resultRoute.size();i++){
            result[i][0]=startBuildingNumber;
            result[i][1]=resultRoute.get(i).get(0);
            result[i][2]=resultRoute.get(i).get(1);
        }
        return result;
    }

    private int[][] searchRouteOutDoor(int startBuildingNumber, int startFloor, int startRoadId,int destBuildingNumber,int destFloor, int destRoadId){

        int[][] buildingToRoad= database.getBuildingToRoad();
        int outDoorStartRoadId=1;
        int outDoorDestRoadId=1;
        for(int i =0; i<buildingToRoad.length;i++){
            if(buildingToRoad[i][0]==startBuildingNumber){
                outDoorStartRoadId=buildingToRoad[i][1];
                break;
            }
        }
        for(int i =0; i<buildingToRoad.length;i++){
            if(buildingToRoad[i][0]==destBuildingNumber){
                outDoorDestRoadId=buildingToRoad[i][1];
                break;
            }
        }
        int[][] connectTable = database.getFloorDir(0, 0);
        float[] roadLength = database.getRoadLength(0, 0);
        int[][] outDoorResult = search_route_inFloor(connectTable,roadLength,0,0,outDoorStartRoadId,outDoorDestRoadId);

        int[][] entrance = database.getEntrance();
        ArrayList<int[]> resultRoute = new ArrayList<>();
        Boolean inBuilding;

        int entranceBuiding_number;
        int entranceFloor;
        int entranceRoadId;
        int exitBuiding_number;
        int exitFloor = 0;
        int exitRoadId=0;
        if(startBuildingNumber==0) {
            inBuilding = false;
            entranceBuiding_number = 0;
            entranceFloor=0;
            entranceRoadId=startRoadId;
        }else {
            inBuilding = true;
            entranceBuiding_number=startBuildingNumber;
            entranceFloor=startFloor;
            entranceRoadId=startRoadId;
        }


        for(int i=0;i<outDoorResult.length;i++){
            if(inBuilding){
                boolean flg =false;
                for(int j=0;j<entrance.length;j++){
                    if(outDoorResult[i][2]==entrance[j][1]){
                        flg=true;
                        exitBuiding_number=entranceBuiding_number;
                        exitFloor=1;
                        exitRoadId= entrance[j][2];
                        break;
                    }
                }
                if(flg){
                    inBuilding=false;
                    int[][] buildingConnectTable = database.getBuildingDir(entranceBuiding_number);
                    int numberOfFloor = database.getNumberOfFloor(entranceBuiding_number);
                    int maxNumberOfRoad = database.getMaxNumberOfRoad(entranceBuiding_number);
                    float[][] buildingRoadLength = database.getBuildingRoadLength(entranceBuiding_number,numberOfFloor,maxNumberOfRoad);
                    int[][] buildingResult=searchRouteInBuilding(buildingConnectTable,buildingRoadLength,entranceBuiding_number,entranceFloor,entranceRoadId,exitFloor,exitRoadId);
                    for(int j=0;j<buildingResult.length;j++){
                        resultRoute.add(buildingResult[j]);
                    }
                }else if(!flg && i==outDoorResult.length-1){
                    exitBuiding_number=destBuildingNumber;
                    exitFloor=destFloor;
                    exitRoadId=destRoadId;
                    int[][] buildingConnectTable = database.getBuildingDir(entranceBuiding_number);
                    int numberOfFloor = database.getNumberOfFloor(entranceBuiding_number);
                    int maxNumberOfRoad = database.getMaxNumberOfRoad(entranceBuiding_number);
                    float[][] buildingRoadLength = database.getBuildingRoadLength(entranceBuiding_number,numberOfFloor,maxNumberOfRoad);
                    int[][] buildingResult=searchRouteInBuilding(buildingConnectTable,buildingRoadLength,entranceBuiding_number,entranceFloor,entranceRoadId,exitFloor,exitRoadId);
                    for(int j=0;j<buildingResult.length;j++){
                        resultRoute.add(buildingResult[j]);
                    }
                }
            }else{
                boolean flg = false;
                for(int j=0;j<entrance.length;j++){
                    if(outDoorResult[i][2]==entrance[j][1]){
                        flg=true;
                        inBuilding=true;
                        entranceBuiding_number=entrance[j][0];
                        entranceFloor=1;
                        entranceRoadId=entrance[j][2];
                        break;
                    }
                }
                if (!flg) {
                    resultRoute.add(outDoorResult[i]);
                }
            }
        }

        int[][] result = new int[resultRoute.size()][3];
        for(int i=0;i<resultRoute.size();i++){
            for(int j=0;j<3;j++){
                result[i][j]=resultRoute.get(i)[j];
            }
        }
        return result;

//        float[][] distanceToRoad = new float[roadLength.length][roadLength[0].length];
//        for(int i=0;i<distanceToRoad.length;i++){
//            for(int j=0;j<distanceToRoad[i].length;j++){
//                if(roadLength[i][j]!=-1)
//                    distanceToRoad[i][j]=9999;
//                else
//                    distanceToRoad[i][j]=-1;
//            }
//        }
//        distanceToRoad[startFloor-1][startRoadId-1]=0;
//        int[][][]  comeFrom = new int[roadLength.length][roadLength[0].length][2];
//        int[][] roadStatus = new int[roadLength.length][roadLength[0].length];  //0:out of target 1:calculating 2:confirmed -1:empty
//        for(int i=0;i<distanceToRoad.length;i++){
//            for(int j=0;j<distanceToRoad[0].length;j++){
//                if(roadLength[i][j]!=-1)
//                    roadStatus[i][j]=0;
//                else
//                    roadStatus[i][j]=-1;
//            }
//        }
//        roadStatus[startFloor-1][startRoadId]=2;
//        int[] location ={startFloor,startRoadId};
//
//        while(roadStatus[destFloor-1][destRoadId-1]!=2){
//            for(int i=0;i<connectTable.length;i++){
//                if(location[0]==connectTable[i][0] && location[1] == connectTable[i][1]){
//                    if(roadStatus[connectTable[i][2]-1][connectTable[i][3]-1] != 2){
//                        if(distanceToRoad[connectTable[i][2]-1][connectTable[i][3]-1]>
//                                distanceToRoad[location[0]-1][location[1]-1]+roadLength[connectTable[i][2]-1][connectTable[i][3]-1]){
//                            distanceToRoad[connectTable[i][2]-1][connectTable[i][3]-1]=distanceToRoad[location[0]-1][location[1]-1]+roadLength[connectTable[i][2]-1][connectTable[i][3]-1];
//                            comeFrom[connectTable[i][2]-1][connectTable[i][3]-1][0]=location[0];
//                            comeFrom[connectTable[i][2]-1][connectTable[i][3]-1][1]=location[1];
//                            roadStatus[connectTable[i][2]-1][connectTable[i][3]-1]=1;
//                        }
//                    }
//                }
//            }
//            int[] next={0,0};
//            float min=9999;
//            for(int i=0;i<distanceToRoad.length;i++){
//                for(int j=0;j<distanceToRoad[i].length;j++){
//                    if(roadStatus[i][j]==1 && min > distanceToRoad[i][j]){
//                        min=distanceToRoad[i][j];
//                        next[0]=i+1;
//                        next[1]=j+1;
//                    }
//                }
//            }
//            roadStatus[next[0]-1][next[1]-1]=2;
//            location[0]=next[0];
//            location[1]=next[1];
//        }
//        ArrayList<ArrayList<Integer>> resultRoute = new ArrayList<ArrayList<Integer>>();
//        int[] now={destFloor,destRoadId};
//        while(now[0]!=0){
//            ArrayList<Integer> list = new ArrayList<Integer>();
//            list.add(now[0]);
//            list.add(now[1]);
//            resultRoute.add(list);
//            int next[] = {comeFrom[now[0]-1][now[1]-1][0],comeFrom[now[0]-1][now[1]-1][1]};
//            now=next;
//        }
//        int[][] result = new int[resultRoute.size()][3];
//        for(int i=0;i<resultRoute.size();i++){
//            result[i][0]=startBuildingNumber;
//            result[i][1]=resultRoute.get(i).get(0);
//            result[i][2]=resultRoute.get(i).get(1);
//        }
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
        locationShowing=myLocation.setLocationIcon(parent_layout,locationShowing,location,image.getImageLocation(),image.getImageSize());
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
                            int maxNumberOfRoad = database.getMaxNumberOfRoad(startBuildingNumber);
                            float[][] roadLength = database.getBuildingRoadLength(startBuildingNumber,numberOfFloor,maxNumberOfRoad);
                            int[][] resultRoute = searchRouteInBuilding(connectTable, roadLength, startBuildingNumber, startFloor,startRoadId,destFloor,destRoadId);
                            search_route=new int[resultRoute.length][3];
                            search_route=resultRoute;
                            isSearchMode=true;
                            removeViews();
                            changeFloor();
                        }
                    }else{
                        int[][] resultRoute= searchRouteOutDoor(startBuildingNumber,startFloor,startRoadId,destBuildingNumber,destFloor,destRoadId);
                        search_route=new int[resultRoute.length][3];
                        search_route=resultRoute;
                        isSearchMode=true;
                        removeViews();
                        changeFloor();
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
    public View.OnClickListener roomInfoClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String[] roomInfo = database.getRoomInfo(building_number,selectingRoomNum);
            roomDescriptionTextView.setText("Wifi : "+roomInfo[0]+"\n"+"情報コンセント : "+roomInfo[1]);
            parent_layout.addView(roomInfoLayout);
            parent_layout.removeView(roomPopUpLayout);
        }
    };
    public View.OnClickListener goToRoomClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(MainActivity.this, SearchActivity.class);
            int requestCode = 1001;
            intent.putExtra("building_number",building_number);
//            intent.putExtra("floor",floor);
            intent.putExtra("room_number",selectingRoomNum);
            startActivityForResult(intent, requestCode);
        }
    };
}
