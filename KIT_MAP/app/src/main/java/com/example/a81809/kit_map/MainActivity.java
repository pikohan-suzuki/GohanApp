package com.example.a81809.kit_map;

import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
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

    private FrameLayout drawer_layout;
    private ImageView mImageView;   //マップを表示するイメージビュー
    private Button center_button;   //画像を元に戻すボタン
    private Button up_button;
    private Button down_button;
    private TextView buildingtextView;
    private TextView floorTextView;
    private TextView room0;
    private LinearLayout info_layout;
    private Button goto_button;
    private Button info_button;

    private int imageWidth;         //画像の現在の幅
    private int imageHeight;        //画像の現在の高さ
    private float maxImageWidth;    //最大の画像幅
    private float maxImageHeight;   //最大の画像の高さ
    private float minImageWidth;    //最小の画像の幅
    private float minImageHeight;   //最小の画像の高さ
    private float defaultX;         //画像のデフォルトx座標
    private float defaultY;         //画像のデフォルトy座標
    private boolean infoFlag=false;       //infoポップアップが表示されているかのフラグ

    private FrameLayout.LayoutParams layoutParams;


    private int building = 23;           //建物番号
    private int floor = 0;              //階層番号
    private int[] floorImage = {R.drawable.b23_1, R.drawable.b23_2, R.drawable.b23_3, R.drawable.b23_4, R.drawable.b23_5};
    private float[][] roomRange ={{0.5f,0.25f}};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawer_layout = findViewById(R.id.drawer_layout);
        mImageView = findViewById(R.id.droid_Image);
        center_button = findViewById(R.id.center_button);
        up_button = findViewById(R.id.up_button);
        down_button = findViewById(R.id.down_button);
        buildingtextView = findViewById(R.id.building_textView);
        floorTextView = findViewById(R.id.floor_textView);
        room0 = findViewById(R.id.room0);
        info_layout =findViewById(R.id.info_layout);
        info_button = findViewById(R.id.info_button);
        goto_button = findViewById(R.id.goTo_button);

        info_layout.setVisibility(View.INVISIBLE);

        mImageView.setImageResource(floorImage[0]);

        /* Touch event */
        drawer_layout.setOnTouchListener(mTouchEventLister);
        mGestureDetector = new GestureDetector(this, mGestureListener);
        mScaleGestureDetector = new ScaleGestureDetector(this, mScaleGestureListener);


        //＋ボタンのクリックイベント
        center_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mImageView.setImageResource(floorImage[floor]);
                mImageView.setX(defaultX);
                mImageView.setY(defaultY);
            }
        });

        up_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (floor < 4) {
                    floor++;
                    mImageView.setImageResource(floorImage[floor]);
                    setImageInfo();
                }
            }
        });

        down_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (floor > 0) {
                    floor--;
                    mImageView.setImageResource(floorImage[floor]);
                    setImageInfo();
                }
            }
        });

        room0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("debug","コミュニケーションスタジオ");

                info_layout.setX(room0.getX()+(room0.getWidth() - info_layout.getWidth())/2);
                info_layout.setY(room0.getY()-info_layout.getHeight()-10);
                Log.d("debug","roomX: " +room0.getX()+ " roomWidth: " + room0.getWidth() + " infoWidth: " + info_layout.getWidth());
                info_layout.setVisibility(View.VISIBLE);
                infoFlag=true;
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
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
        setImageInfo();
        float textMarginX = defaultX + imageWidth*roomRange[0][0];
        float textMarginY = defaultY + imageHeight*roomRange[0][1];

        room0.setX(textMarginX);
        room0.setY(textMarginY);

    }

    private void setImageInfo() {
        imageWidth = mImageView.getWidth();
        imageHeight = mImageView.getHeight();
        mImageView.setX((maxImageWidth - imageWidth) / 2);
        mImageView.setY((maxImageHeight - imageHeight) / 2.5f);
        defaultX = mImageView.getX();
        defaultY = mImageView.getY();
        floorTextView.setText("F" + (floor + 1));
    }

    private View.OnTouchListener mTouchEventLister = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent e) {
            mScaleGestureDetector.onTouchEvent(e);
            mGestureDetector.onTouchEvent(e);
            if(infoFlag){
                info_layout.setVisibility(View.INVISIBLE);
            }
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
            if (factor < 2.0) {

                imageHeight = mImageView.getHeight();
                imageWidth = mImageView.getWidth();
                 if (imageHeight * factor < maxImageHeight && imageWidth * factor < maxImageWidth
                        && imageHeight * factor > minImageHeight && imageWidth * factor > minImageWidth) {
                    layoutParams = new FrameLayout.LayoutParams((int) (imageWidth * factor), (int) (imageHeight * factor));
                    mImageView.setLayoutParams(layoutParams);
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
                    mImageView.setX(mImageView.getX() - distanceX * 0.5f);
                    mImageView.setY(mImageView.getY() - distanceY * 0.5f);
                    room0.setX(room0.getX() - distanceX * 0.5f);
                    room0.setY(room0.getY()- distanceY *0.5f);
                    Log.d("debug", "x:" + mImageView.getX() + " v:" + distanceX + " setY:" + mImageView.getY() +
                            " b1:" + distanceY + "before_x:" + motionEvent1.getX() + " after_x:" + motionEvent1.getY());
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
