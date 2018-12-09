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

    private float X1,X2,Y1,Y2;
    private ArrayList<Float> startX;
    private ArrayList<Float> startY;
    private ArrayList<Float> endX;
    private ArrayList<Float> endY;



    public MyView(Context context) {
        super(context);
        paint = new Paint();
        paint.setColor(Color.argb(255,0,0,200));
        paint.setStrokeWidth(10);
        setWillNotDraw(false);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.argb(0, 255, 255, 255));

        canvas.drawLine(X1,Y1,X2,Y2,paint);

//        canvas.drawLine(1,1,100,100,paint);
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event){
//
//    }
    public void drawLine(float x1,float y1,float x2,float y2){
//        startX.add(x1);
//        startY.add(y1);
//        endX.add(x2);
//        endY.add(y2);
        X1=x1;
        X2=x2;
        Y1=y1;
        Y2=y2;
        invalidate();
    }
}