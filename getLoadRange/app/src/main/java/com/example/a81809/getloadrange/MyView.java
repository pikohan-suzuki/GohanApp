package com.example.a81809.getloadrange;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class MyView extends View {
    private Paint paint;

    private float X1,X2,Y1,Y2;

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.argb(0, 255, 255, 255));
        paint.setColor(Color.argb(255,0,0,200));
        canvas.drawLine(X1,Y1,X2,Y2,paint);
    }

    public void setLine(float x1,float y1,float x2,float y2,float imageX,float imageY){
        X1=x1+imageX;
        X2=x2+imageX;
        Y1=y1+imageY;
        Y2=y2+imageY;
    }
}