package com.example.a81809.kit_map;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

public class Room {
    private TextView roomTextView;
    private Context context;
    private float x;
    private float y;
    private int room_num;
    private int textSize;
    private int width;
    private int height;
    private String name;

    public Room(Context context, FrameLayout layout, DatabaseRead database, int building_number, int floor, int room_num) {
        this.context = context;
        this.room_num = room_num;
        String[] info = database.getRoomInfo(building_number, floor, room_num);
        this.name = info[0];
        this.x = Float.parseFloat(info[1]);
        this.y = Float.parseFloat(info[2]);
        roomTextView = new TextView(context);
        layout.addView(roomTextView);
        String str[] = name.split(",");
        if (str.length == 1)
            roomTextView.setText(building_number + "-" + room_num + "\n" + str[0]);
        else
            roomTextView.setText(building_number + "-" + room_num + "\n" + str[0] + "\n" + str[1]);
        roomTextView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        roomTextView.setBackgroundResource(R.drawable.marukado);
        roomTextView.setGravity(1);


    }

    public void changeRange() {
        if (width == 0 && height == 0) {
            this.width = roomTextView.getWidth();
            this.height = roomTextView.getHeight();
        }
        this.x = MainActivity.image.getImageLocation().x + MainActivity.image.getImageRange().x * this.x - this.width / 2;
        this.y = MainActivity.image.getImageLocation().y + MainActivity.image.getImageRange().y * this.y - this.height / 2;
        roomTextView.setX(this.x);
        roomTextView.setY(this.y);
    }

    public void onScroll(float moveX, float moveY) {
        this.x = this.x - moveX;
        this.y = this.y - moveY;
        roomTextView.setX(this.x);
        roomTextView.setY(this.y);
    }
}
