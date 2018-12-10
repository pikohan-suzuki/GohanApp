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
        image.setLayoutParams(new FrameLayout.LayoutParams(50,50));
    }
    public void setLocationIcon(FrameLayout layout,boolean locationShowing){
        if(!locationShowing) layout.addView(image);

    }
    public void removeLocationIcon(FrameLayout layout){
        layout.removeView(image);
    }
}
