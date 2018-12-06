package com.example.a81809.myapplication;

import android.content.Context;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class Facility {
    private ImageView image;
    private Context context;
    private int type;
    private int x;
    private int y;
    public  Facility(Context context,FrameLayout layout ,DatabaseRead database, int building_number, int floor, int id){
        this.context=context;
        this.image = new ImageView(this.context);
        this.type = database.getFacilityType(building_number,floor,id);
        
    }
}
