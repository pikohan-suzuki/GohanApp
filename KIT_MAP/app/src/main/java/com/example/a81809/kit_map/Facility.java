package com.example.a81809.kit_map;

import android.content.Context;
import android.graphics.Point;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class Facility {
    private ImageView image;
    private Context context;
    private int type;
    private float xper;
    private float yper;
    private float x;
    private float y;
    private float widthProportion;
    private float heightProportion;
    private final int defaultImageSize =144;
    public  Facility(Context context,FrameLayout layout ,DatabaseRead database, int building_number, int floor, int id,
                     Point mapImageSize,Point mapImageLocation){
        this.context=context;
        this.image = new ImageView(this.context);
        this.type = database.getFacilityType(building_number,floor,id);
        float[] range = database.getFacilityRange(building_number,floor,id);
        this.xper = range[0];
        this.yper =range[1];
        if(type==0)
            image.setImageResource(R.drawable.printer_icon);
        else
            image.setImageResource(R.drawable.vending_machine_icon);
        layout.addView(this.image);
        image.setLayoutParams(new FrameLayout.LayoutParams(this.defaultImageSize,this.defaultImageSize));

        widthProportion=defaultImageSize/(float)mapImageSize.x;
        heightProportion=defaultImageSize/(float)mapImageSize.y;
        this.x= mapImageLocation.x + (float)mapImageSize.x * this.xper -(float)mapImageSize.x*widthProportion/2;
        this.y=mapImageLocation.y +(float)+mapImageSize.y * this.yper -(float)mapImageSize.y*heightProportion/2;
        setImageLocation(x,y);
    }
    private void setImage(){

    }

    public void onScroll(Point mapImageSize,Point mapImageLocation,float moveX,float moveY){
        this.x=this.x-moveX;
        this.y=this.y-moveY;
        setImageLocation(x,y);
    }
    public void onScale(Point mapImageSize,Point mapImageLocation){
        image.setLayoutParams(new FrameLayout.LayoutParams((int)(mapImageSize.x*widthProportion),(int)(mapImageSize.y*heightProportion)));
        this.x =mapImageLocation.x+(float)mapImageSize.x*this.xper -(float)mapImageSize.x*widthProportion/2;
        this.y = mapImageLocation.y+(float)mapImageSize.y*this.yper -(float)mapImageSize.y*heightProportion/2;
        setImageLocation(x,y);
    }
    private void setImageLocation(float x, float y){
        image.setX(x);
        image.setY(y);
    }
    public void showActionBar(){
        this.y=this.y-MainActivity.actionBarSize.y;
        image.setY(this.y);
    }
    public void hideActionBar(){
        this.y=this.y+MainActivity.actionBarSize.y;
        image.setY(this.y);
    }
}
