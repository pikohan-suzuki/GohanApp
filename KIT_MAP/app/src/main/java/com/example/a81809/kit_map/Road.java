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

    private ArrayList<Float> startXper;
    private ArrayList<Float> startYper;
    private ArrayList<Float> length;
    private ArrayList<Boolean> is_xDir;

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
                Log.d("debug", "llllllllllll" + startX.get(i) + "startY:" + startY.get(i) + "endX:" + endX.get(i) + "endY:" + endY.get(i));
                canvas.drawLine(startX.get(i), startY.get(i), endX.get(i), endY.get(i), paint);
            }
        }
    }

    public void setInfo(float[] startX, float[] startY, float[] length, boolean[] is_xDir, Point mapImageSize, Point mapImageLocation) {
        resetRoads();
        for (int i = 0; i < startX.length; i++) {
            this.startXper.add(startX[i]);
            this.startYper.add(startY[i]);
            this.length.add(length[i]);
            this.is_xDir.add(is_xDir[i]);

            this.startX.add(mapImageLocation.x + startXper.get(i) * mapImageSize.x);
            this.startY.add(mapImageLocation.y + startYper.get(i) * mapImageSize.y);
            if (is_xDir[i]) {
                this.endX.add(mapImageLocation.x + (startXper.get(i) + this.length.get(i)) * mapImageSize.x);
                this.endY.add(mapImageLocation.y + startYper.get(i) * mapImageSize.y);
            } else {
                this.endX.add(mapImageLocation.x + startXper.get(i) * mapImageSize.x);
                this.endY.add(mapImageLocation.y + (startYper.get(i) + this.length.get(i)) * mapImageSize.y);
            }
        }
        invalidate();
    }

    public void resetRoads() {
        startXper = new ArrayList<Float>();
        startYper = new ArrayList<Float>();
        length = new ArrayList<Float>();
        is_xDir = new ArrayList<Boolean>();

        startX = new ArrayList<Float>();
        startY = new ArrayList<Float>();
        endX = new ArrayList<Float>();
        endY = new ArrayList<Float>();
        invalidate();
    }

    public void onScroll(Point mapImageSize, Point mapImageLocation, float moveX, float moveY) {
        for (int i = 0; i < this.startX.size(); i++) {
            startX.set(i, startX.get(i) - moveX);
            startY.set(i, startY.get(i) - moveY);
            endX.set(i, endX.get(i) - moveX);
            endY.set(i, endY.get(i) - moveY);
        }
        invalidate();
    }

    public void onScale(Point mapImageSize, Point mapImageLocation) {
        for (int i = 0; i < startX.size(); i++) {
            this.startX.set(i,mapImageLocation.x + startXper.get(i) * mapImageSize.x);
            this.startY.set(i,mapImageLocation.y + startYper.get(i) * mapImageSize.y);
            if (is_xDir.get(i)) {
                this.endX.set(i,mapImageLocation.x + (startXper.get(i) + this.length.get(i)) * mapImageSize.x);
                this.endY.set(i,mapImageLocation.y + startYper.get(i) * mapImageSize.y);
            } else {
                this.endX.set(i,mapImageLocation.x + startXper.get(i) * mapImageSize.x);
                this.endY.set(i,mapImageLocation.y + (startYper.get(i) + this.length.get(i)) * mapImageSize.y);
            }
        }
        invalidate();
    }
}