package com.example.a81809.myapplication;

import android.content.Context;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.ArrayList;

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
        int[] range = database.getFacilityRange(building_number,floor,id);
        this.x= range[0];
        this.y=range[1];
        if(type==0)
            image.setImageResource(R.drawable.printer_icon);
        else
            image.setImageResource(R.drawable.vending_machine_icon);
    }
    private void setImage(){

    }

    public void onScroll(float moveX,float moveY){

    }
}
