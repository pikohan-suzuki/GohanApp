package com.example.a81809.kit_map;

import android.content.Context;
import android.graphics.Point;
import android.location.Location;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class MyLocation {
    private ImageView image;
    private Context context;
    private static final int imageSize= 50;
    public MyLocation(Context context){
        this.context =context;
        image=new ImageView(this.context);
        image.setImageResource(R.drawable.location);
        image.setLayoutParams(new FrameLayout.LayoutParams(imageSize,imageSize));
    }
    public void setLocationIcon(FrameLayout layout, boolean locationShowing, Location location,Point mapLocation,Point mapSize,
                                double top_latitude,double left_longitude,double latitude,double longitude){
        if(!locationShowing) layout.addView(image);
            image.setX((float)(mapLocation.x+(location.getLongitude()-left_longitude)*mapSize.x/longitude-imageSize/2));
            image.setY((float)(mapLocation.y+(top_latitude-location.getLatitude())*mapSize.y/latitude-imageSize/2));
    }
    public void removeLocationIcon(FrameLayout layout){
        layout.removeView(image);
    }
}
