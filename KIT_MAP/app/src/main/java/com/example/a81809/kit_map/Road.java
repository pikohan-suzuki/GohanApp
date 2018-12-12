package com.example.a81809.kit_map;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;

public class Road extends View {
    private Paint paint;

    private ArrayList<Float> startX;
    private ArrayList<Float> startY;
    private ArrayList<Float> endX;
    private ArrayList<Float> endY;


    public Road(Context context, AttributeSet attr) {
        super(context, attr);
        paint = new Paint();
        paint.setColor(Color.argb(255, 0, 0, 200));
        paint.setStrokeWidth(5);
        resetRoads();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if (startX != null) {
            for (int i = 0; i < startX.size(); i++) {
                Log.d("debug","llllllllllll"+startX.get(i)+"startY:"+startY.get(i)+"endX:"+endX.get(i) +"endY:"+endY.get(i));
                canvas.drawLine(startX.get(i), startY.get(i), endX.get(i), endY.get(i), paint);
            }
        }
    }

    public void drawLine(float[] startX, float[] startY, float[] length, boolean[] is_xDir, int imageWidth, int imageHeight,int imageX,int imageY) {
        resetRoads();
        for(int i=0;i<startX.length;i++){
            this.startX.add(imageX+startX[i]*imageWidth);
            this.startY.add(imageY+startY[i]*imageHeight);
            if(is_xDir[i]){
                this.endX.add(imageX+(startX[i]+length[i])*imageWidth);
                this.endY.add(imageY+startY[i]*imageHeight);
            }else{
                this.endX.add(imageX+startX[i]*imageWidth);
                this.endY.add(imageY+(startY[i]+length[i])*imageHeight);
            }
        }
        invalidate();
    }

    public void resetRoads() {
        startX = new ArrayList<Float>();
        startY = new ArrayList<Float>();
        endX = new ArrayList<Float>();
        endY = new ArrayList<Float>();
        invalidate();
    }

    public void onScroll(Point mapImageSize, Point mapImageLocation, float moveX, float moveY){
        resetRoads();
        for(int i=0;i<this.startX.size();i++){
            startX.set(i,startX.get(i)-moveX);
            startY.set(i,startY.get(i)-moveY);
            endX.set(i,endX.get(i)-moveX);
            endY.set(i,endY.get(i)-moveY);
        }
    }
}