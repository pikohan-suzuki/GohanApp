package com.example.a81809.kit_map;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.ArrayList;

public class Facility {
    private ImageView image;
    private Context context;
    private int type;
    private float x;
    private float y;
    private int width;
    private int height;
    public  Facility(Context context,FrameLayout layout ,DatabaseRead database, int building_number, int floor, int id){
        this.context=context;
        this.image = new ImageView(this.context);
        this.type = database.getFacilityType(building_number,floor,id);
        float[] range = database.getFacilityRange(building_number,floor,id);
        this.x= range[0];
        this.y=range[1];
        if(type==0)
            image.setImageResource(R.drawable.printer_icon);
        else
            image.setImageResource(R.drawable.vending_machine_icon);
        layout.addView(this.image);
        this.width=144;
        this.height=144;
        image.setLayoutParams(new FrameLayout.LayoutParams(this.width,this.height));
    }
    public void changeRange(){
        if(width==0&&height==0){
            this.width=image.getWidth();
            this.height=image.getHeight();
        }
        this.x= MainActivity.image.getImageLocation().x + MainActivity.image.getImageRange().x * this.x-this.width/2;
        this.y=MainActivity.image.getImageLocation().y + MainActivity.image.getImageRange().y * this.y-this.height/2;
        image.setX(this.x);
        image.setY(this.y);
    }
    private void setImage(){

    }

    public void onScroll(float moveX,float moveY){
        this.x = this.x - moveX;
        this.y = this.y - moveY;
        image.setX(this.x);
        image.setY(this.y);
    }
}
