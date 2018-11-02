package com.example.a81809.kit_map;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private GestureDetector mGestureDetector;
    private ScaleGestureDetector mScaleGestureDetector;
    private ImageView mImageView;
    private Button center_button;

    private Bitmap bitmap1;
    private Bitmap bitmap3;
    private int imageWidth;
    private int imageHeight;
    private float maxImageWidth;
    private float maxImageHeight;
    private float minImageWidth;
    private float minImageHeight;
    private float defaultX;
    private float defaultY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageView = findViewById(R.id.droid_Image);
        center_button = findViewById(R.id.center_button);
        bitmap1 = BitmapFactory.decodeResource(getResources(), R.drawable.droid);
        bitmap3 = bitmap1;


        /* Touch event */
        mImageView.setOnTouchListener(mTouchEventLister);
        mGestureDetector = new GestureDetector(this, mGestureListener);
        mScaleGestureDetector = new ScaleGestureDetector(this, mScaleGestureListener);

        center_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mImageView.setImageBitmap(bitmap1);
                mImageView.setX(defaultX);
                mImageView.setY(defaultY);
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        imageWidth = mImageView.getWidth();
        imageHeight = mImageView.getHeight();
        defaultX = mImageView.getX();
        defaultY = mImageView.getY();

        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display disp = wm.getDefaultDisplay();

        Point realSize = new Point();
        disp.getRealSize(realSize);

        int realScreenWidth = realSize.x;
        int realScreenHeight = realSize.y;
        maxImageHeight = realScreenHeight;
        maxImageWidth = realScreenWidth;
        minImageHeight = realScreenHeight * 0.25f;
        minImageWidth = realScreenWidth * 0.25f;
    }


    private View.OnTouchListener mTouchEventLister = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent e) {
            mScaleGestureDetector.onTouchEvent(e);
            mGestureDetector.onTouchEvent(e);
            Log.d("debug", "onTouch");

            return true;
        }
    };


    private ScaleGestureDetector.OnScaleGestureListener mScaleGestureListener
            = new ScaleGestureDetector.OnScaleGestureListener() {

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            Log.d("debug", "on Scale Begin");

            return true;
        }

        public boolean onScale(ScaleGestureDetector detector) {
            mGestureDetector.setIsLongpressEnabled(false);
            Log.d("debug", "on Scale");
            float factor = detector.getScaleFactor();
            Matrix matrix = new Matrix();
            matrix.preScale(factor, factor);
            if (factor < 2.0) {
                imageHeight = bitmap3.getHeight();
                imageWidth = bitmap3.getWidth();
                if (Math.abs(imageHeight * factor - bitmap1.getHeight()) < 25 &&
                        Math.abs(imageWidth * factor - bitmap1.getWidth()) < 25) {
                    mImageView.setImageBitmap(bitmap1);
                } else if (imageHeight * factor < maxImageHeight && imageWidth * factor < maxImageWidth
                        && imageHeight * factor > minImageHeight && imageWidth * factor > minImageWidth) {

                    bitmap3 = Bitmap.createBitmap(bitmap3, 0, 0,
                            imageWidth, imageHeight, matrix, true);
                    // drawableに変換
                    Drawable drawable = new BitmapDrawable(getResources(), bitmap3);
                    mImageView.setImageDrawable(drawable);
                }
                mGestureDetector.setIsLongpressEnabled(true);
            }
            return true;
        }

        public void onScaleEnd(ScaleGestureDetector detector) {
            Log.d("debug", "on Scale End.");

        }
    };

    private GestureDetector.OnGestureListener mGestureListener =
            new GestureDetector.OnGestureListener() {
                @Override
                public boolean onDown(MotionEvent motionEvent) {
                    Log.d("debug", "onDown");
                    return false;
                }

                @Override
                public void onShowPress(MotionEvent motionEvent) {
                    Log.d("debug", "onShowPress");
                }

                @Override
                public boolean onSingleTapUp(MotionEvent motionEvent) {
                    Log.d("debug", "onSingleTapUp");
                    return false;
                }

                @Override
                public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float distanceX, float distanceY) {
                    Log.d("debug", "onScroll");
                    mImageView.setX(mImageView.getX()-distanceX*0.5f);
                    mImageView.setY(mImageView.getY()-distanceY*0.5f);
                    Log.d("debug","x:"+mImageView.getX() + " v:" +distanceX+" setY:" + mImageView.getY() +
                    " b1:" + distanceY +"before_x:" + motionEvent1.getX() +" after_x:"+motionEvent1.getY());
                    return false;
                }

                @Override
                public void onLongPress(MotionEvent motionEvent) {
                    Log.d("debug", "onLongPress");
                }

                @Override
                public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
                    Log.d("debug", "onFling");
                    return false;
                }
            };
}
