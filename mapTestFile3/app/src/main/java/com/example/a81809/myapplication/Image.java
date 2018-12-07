package com.example.a81809.myapplication;

import android.content.Context;

import android.widget.FrameLayout;
import android.widget.ImageView;

public class Image {
    private ImageView mapImage;
    private Context context;
    private int width;
    private int height;
    private float x;
    private float y;

    public Image(Context context, FrameLayout layout, DatabaseRead database) {
        this.context = context;
        mapImage = new ImageView(this.context);
        setImage(0,0,database);
        layout.addView(mapImage);
    }

    public void setImage(int building_number,int floor,DatabaseRead database) {
        if(building_number==0) {
            mapImage.setImageResource(R.drawable.school_map);
        }else{
            int imageNum = database.getImageResource(building_number,floor);
            mapImage.setImageResource(imageNum);
        }
            mapImage.setBackgroundResource(R.drawable.border);

        setImageInit(building_number,floor,database);
    }

    private void setImageInit(int building_number, int floor,DatabaseRead database) {
        if(building_number==0){
            this.width = 1203;
            this.height = 1017;
        }else {
            String[] record = database.getFloorImageSize(building_number, floor);
            this.width = Integer.parseInt(record[0]);
            this.height = Integer.parseInt(record[1]);
        }
            setFillCenter();
    }

    private void setFillCenter() {
        double factor;
        if (MainActivity.screenSize.x / width < MainActivity.screenSize.y / height)
            factor = (float)MainActivity.screenSize.x / this.width;
         else
            factor =(float)MainActivity.screenSize.y/this.height;
        this.width = (int) (width * factor);
        this.height = (int) (height * factor);
        mapImage.setLayoutParams(new FrameLayout.LayoutParams(this.width, this.height));
        this.x = (MainActivity.screenSize.x - this.width) / 2;
        this.y = (MainActivity.screenSize.y - this.height) / 2;
        mapImage.setX(this.x);
        mapImage.setY(this.y);
    }

    public void onScroll(float moveX,float moveY){
        this.x = this.x-moveX;
        this.y = this.y - moveY;
        mapImage.setX(this.x);
        mapImage.setY(this.y);
    }
    public void onScale(float focusX,float focusY,float factor){
        this.x=(focusX-this.x)-(focusX-this.x)*factor;
        this.y=(focusY-this.y)-(focusY-this.y)*factor;
        this.width=(int)(this.width*factor);
        this.height=(int)(this.height*factor);
        mapImage.setX(this.x);
        mapImage.setY(this.y);
        mapImage.setLayoutParams(new FrameLayout.LayoutParams(this.width,this.height));
    }

}
