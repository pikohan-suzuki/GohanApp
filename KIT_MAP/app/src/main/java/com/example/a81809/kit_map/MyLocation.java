package com.example.a81809.kit_map;

import android.content.Context;
import android.graphics.Point;
import android.location.Location;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class MyLocation {
    private ImageView image;
    private Context context;
    private float x;
    private float y;
    private double top_latitude;
    private double left_longitude;
    private double latitude;
    private double longitude;
    private static final int imageSize= 50;
    public MyLocation(Context context){
        this.context =context;
        image=new ImageView(this.context);
        image.setImageResource(R.drawable.location);
        image.setLayoutParams(new FrameLayout.LayoutParams(imageSize,imageSize));
    }
    public void changeFloor(double[] range){
        this.top_latitude=range[1];
        this.left_longitude=range[0];
        this.latitude=range[3];
        this.longitude=range[2];
    }
    public boolean setLocationIcon(FrameLayout layout, boolean locationShowing, Location location,Point mapLocation,Point mapSize){
        if(location!=null) {
            if (location.getLongitude() > left_longitude && location.getLongitude() < left_longitude + longitude &&
                    location.getLatitude() < top_latitude && location.getLatitude() > top_latitude - latitude) {
                if (!locationShowing) {
                    layout.addView(image);
                    locationShowing = true;
                }
                this.x = (float) (mapLocation.x + (location.getLongitude() - left_longitude) * mapSize.x / longitude - imageSize / 2);
                this.y = (float) (mapLocation.y + (top_latitude - location.getLatitude()) * mapSize.y / latitude - imageSize / 2);
                setImage();
            }

        }
        return locationShowing;
    }
    public void removeLocationIcon(FrameLayout layout){
        layout.removeView(image);
    }

//    public void onScroll(float moveX, float moveY) {
//        this.x = this.x - moveX;
//        this.y = this.y - moveY;
//        setLocationIcon();
//    }
//
//    public void onScale(Location location,Point mapSize, Point mapLocation,double top_latitude,double left_longitude,double latitude,double longitude) {
//        this.x=(float)(mapLocation.x+(location.getLongitude()-left_longitude)*mapSize.x/longitude-imageSize/2);
//        this.y=(float)(mapLocation.y+(top_latitude-location.getLatitude())*mapSize.y/latitude-imageSize/2);
//        setLocationIcon();
//    }
    public void setImage(){
        image.setX(x);
        image.setY(y);
    }
}
