package com.example.a81809.kit_map;

import android.content.Context;
import android.widget.FrameLayout;
import android.widget.TextView;

public class Room {
    private TextView roomTextView;
    private Context context;
    private float x;
    private float y;
    private int room_num;
    private String name;
    public Room(Context context, FrameLayout layout, DatabaseRead database, int building_number, int floor, int room_num){
        this.context=context;
        this.room_num=room_num;
        String[] info = database.getRoomInfo(building_number,floor,room_num);
        this.name=info[0];
        this.x= Float.parseFloat(info[1]);
        this.y=Float.parseFloat(info[2]);
        roomTextView=new TextView(context);
    }
    public void onScroll(float moveX,float moveY){

    }
}
