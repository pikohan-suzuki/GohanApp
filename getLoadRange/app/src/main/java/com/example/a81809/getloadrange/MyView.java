package com.example.a81809.getloadrange;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

public class MyView extends View {
    private Paint paint;

    private float X1, X2, Y1, Y2;
    private ArrayList<Float> startX;
    private ArrayList<Float> startY;
    private ArrayList<Float> endX;
    private ArrayList<Float> endY;


    public MyView(Context context,AttributeSet attr) {
        super(context,attr);
        paint = new Paint();
        paint.setColor(Color.argb(255, 0, 0, 200));
        paint.setStrokeWidth(10);
        resetRoads();
//        setWillNotDraw(false);
    }


    @Override
    protected void onDraw(Canvas canvas) {
//        canvas.drawColor(Color.argb(0, 255, 255, 255));
    if(startX!=null) {
        for (int i = 0; i < startX.size(); i++) {
            canvas.drawLine(startX.get(i), startY.get(i), endX.get(i), endY.get(i), paint);
        }
    }
//        canvas.drawLine(X1,Y1,X2,Y2,paint);


//        canvas.drawLine(1, 1, 100, 100, paint);
    }

    public void drawLine(float x1, float y1, float x2, float y2) {
        startX.add(x1);
        startY.add(y1);
        endX.add(x2);
        endY.add(y2);
        invalidate();
    }
    public void resetRoads(){
        startX = new ArrayList<Float>();
        startY = new ArrayList<Float>();
        endX = new ArrayList<Float>();
        endY = new ArrayList<Float>();
    }
}