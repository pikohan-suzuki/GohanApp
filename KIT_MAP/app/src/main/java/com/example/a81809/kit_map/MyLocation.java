package com.example.a81809.kit_map;

import android.content.Context;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class MyLocation {
    private ImageView image;
    private Context context;
    public MyLocation(Context context){
        this.context =context;
        image=new ImageView(this.context);
        image.setImageResource(R.drawable.location);
    }
    public void setLocationIcon(FrameLayout layout){
        layout.addView(image);
    }
    public void removeLocationIcon(FrameLayout layout){
        layout.removeView(image);
    }
}
