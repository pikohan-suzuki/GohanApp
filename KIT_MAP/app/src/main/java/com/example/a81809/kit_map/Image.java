package com.example.a81809.kit_map;

import android.content.Context;

import android.graphics.Point;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class Image {
    private ImageView mapImage;
    private Context context;
    private int width;
    private int height;
    private float x;
    private float y;

    public Image(Context context, FrameLayout layout, DatabaseRead database,int building_number,int floor) {
        this.context = context;
        mapImage = new ImageView(this.context);
        setImage(building_number, floor, database);
        layout.addView(mapImage);
    }

    public void setImage(int building_number, int floor, DatabaseRead database) {
        if (building_number == 0) {
            mapImage.setImageResource(R.drawable.school_map);
        } else {
            int imageNum = database.getImageResource(building_number, floor);
            mapImage.setImageResource(imageNum);
        }
        mapImage.setBackgroundResource(R.drawable.border);

        setImageInit(building_number, floor, database);
    }

    private void setImageInit(int building_number, int floor, DatabaseRead database) {
        if (building_number == 0) {
            this.width = 1203;
            this.height = 1017;
        } else {
            String[] record = database.getFloorImageSize(building_number, floor);
            this.width = Integer.parseInt(record[0]);
            this.height = Integer.parseInt(record[1]);
        }
        setFillCenter();
    }

    private void setFillCenter() {
        double factor;
        if (MainActivity.screenSize.x / width < MainActivity.screenSize.y / height)
            factor = (float) MainActivity.screenSize.x / this.width;
        else
            factor = (float) MainActivity.screenSize.y / this.height;
        this.width = (int) (width * factor);
        this.height = (int) (height * factor);
        mapImage.setLayoutParams(new FrameLayout.LayoutParams(this.width, this.height));
        this.x = (MainActivity.screenSize.x - this.width) / 2;
        this.y = (MainActivity.screenSize.y - this.height) / 2;
        mapImage.setX(this.x);
        mapImage.setY(this.y);
    }

    public void onScroll(float moveX, float moveY) {
        this.x = this.x - moveX;
        this.y = this.y - moveY;
        mapImage.setX(this.x);
        mapImage.setY(this.y);
    }

    public void onScale(float focusX, float focusY, float factor) {

        float xProportionOfImage = (focusX - this.x) / this.width;
        float yProportionOfImage = (focusY - this.y) / this.height;
        float xspan;
        float yspan;
        if (factor > 1) {
            xspan = width / 50 * factor;
            yspan = height / 50 * factor;
        } else if (factor < 1) {
            xspan = -width / (50 * factor);
            yspan = -height / (50 * factor);
        } else {
            xspan = 0;
            yspan = 0;
        }
        if (this.width + xspan < MainActivity.screenSize.x * 2 && this.height + yspan < MainActivity.screenSize.y * 2
                && (this.width + xspan >= MainActivity.screenSize.x * 0.7 || this.height + yspan >= MainActivity.screenSize.y * 0.7)) {
            this.width = (int) (this.width + xspan);
            this.height = (int) (this.height + yspan);
            this.x = focusX - this.width * xProportionOfImage;
            this.y = focusY - this.height * yProportionOfImage;
            mapImage.setX(this.x);
            mapImage.setY(this.y);
            mapImage.setLayoutParams(new FrameLayout.LayoutParams(this.width, this.height));
        }
    }

    public Point getImageSize() {
        return new Point(this.width, this.height);
    }
    public Point getImageLocation(){
        return new Point((int)this.x,(int)this.y);
    }
    public void showActionBar(){
        this.y=this.y-MainActivity.actionBarSize.y;
        mapImage.setY(this.y);
    }
    public void hideActionBar(){
        this.y=this.y+MainActivity.actionBarSize.y;
        mapImage.setY(this.y);
    }
}
