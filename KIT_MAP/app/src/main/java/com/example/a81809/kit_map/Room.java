package com.example.a81809.kit_map;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.TextView;

public class Room {
    private TextView roomTextView;
    private Context context;
    private float xper;
    private float yper;
    private float x;
    private float y;
    private int room_num;
    private final int textSize = (int)(MainActivity.screenSize.x*0.006);

    private int width;
    private int height;
    private String name;

    public Room(Context context, FrameLayout layout, DatabaseRead database, int building_number, int floor, int room_num,
                final Point mapImageSize, final Point mapImageLocation) {
        this.context = context;
        this.room_num = room_num;
        String[] info = database.getRoomInfo(building_number, floor, room_num);
        this.name = info[0];
        this.xper = Float.parseFloat(info[1]);
        this.yper = Float.parseFloat(info[2]);
        roomTextView = new TextView(context);
        layout.addView(roomTextView);
        if(name!=null) {
            String str[] = name.split(",");
            if (str.length == 1)
                roomTextView.setText(building_number + "-" + room_num + "\n" + str[0]);
            else
                roomTextView.setText(building_number + "-" + room_num + "\n" + str[0] + "\n" + str[1]);
        }else
            roomTextView.setText(building_number+"-"+room_num);
        roomTextView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        roomTextView.setBackgroundResource(R.drawable.marukado);
        roomTextView.setTextSize(textSize);
        roomTextView.setTextColor(Color.rgb(0,0,0));
        roomTextView.setTypeface(Typeface.DEFAULT_BOLD);
        roomTextView.setGravity(1);

        ViewTreeObserver vto = roomTextView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Room.this.width = Room.this.roomTextView.getWidth();
                Room.this.height = Room.this.roomTextView.getHeight();
                Room.this.x = mapImageLocation.x + (float) mapImageSize.x * Room.this.xper - Room.this.width / 2;
                Room.this.y = mapImageLocation.y + (float) mapImageSize.y * Room.this.yper - Room.this.height / 2;
                setRoomLocation(Room.this.x, Room.this.y);
                Room.this.roomTextView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    public void onScroll(Point mapImageSize, Point mapImageLocation, float moveX, float moveY) {
        this.x = this.x - moveX;
        this.y = this.y - moveY;
        setRoomLocation(this.x, this.y);
    }

    public void onScale(Point mapImageSize, Point mapImageLocation) {
        this.width = roomTextView.getWidth();
        this.height = roomTextView.getHeight();
        this.x = mapImageLocation.x + (float) mapImageSize.x * this.xper - this.width / 2;
        this.y = mapImageLocation.y + (float) mapImageSize.y * this.yper - this.height / 2;
        setRoomLocation(this.x, this.y);
    }

    public void setRoomLocation(float x, float y) {
        roomTextView.setX(x);
        roomTextView.setY(y);
    }
    public void showActionBar(){
        this.y=this.y-MainActivity.actionBarSize.y;
        roomTextView.setY(this.y);
    }
    public void hideActionBar(){
        this.y=this.y+MainActivity.actionBarSize.y;
        roomTextView.setY(this.y);
    }
    public void removeView(FrameLayout layout){
        layout.removeView(roomTextView);
    }

    public void removeRoomResource(){
        roomTextView=null;
        context=null;
        xper=0;
        yper=0;
        x=0;
        y=0;
        room_num=0;
        width=0;
        height=0;
        name=null;
    }
}
