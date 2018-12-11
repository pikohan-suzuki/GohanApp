package com.example.a81809.kit_map;

import android.content.Context;
import android.graphics.PixelFormat;
import android.support.v4.view.MarginLayoutParamsCompat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class UIManager {

    private LinearLayout layout;
    public static Button upButton;
    public static Button downButton;
    private Context context;
    private int buttonSize;

    public UIManager(Context context,FrameLayout parent_layout) {
        this.context = context;
        layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setHorizontalGravity(Gravity.RIGHT);
        layout.setVerticalGravity(Gravity.BOTTOM);
        buttonSize = (int) (MainActivity.screenSize.x / 10);

        upButton = new Button(context);
        upButton.setText("▲");
        upButton.setTextSize(50);

        upButton.setBackgroundResource(R.drawable.round_button);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(buttonSize,buttonSize);
        ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams)params;
        marginParams.setMargins(0,0,0,20);
//        upButton.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
//        upButton.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        upButton.setLayoutParams(marginParams);
//        layout.addView(upButton);

        downButton = new Button(context);
        downButton.setText("▼");
        downButton.setTextSize(50);
        downButton.setBackgroundResource(R.drawable.round_button);
//        downButton.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
//        downButton.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        downButton.setLayoutParams(marginParams);
//        layout.addView(downButton);
//
//        parent_layout.addView(layout);
    }

    public void hideButton(WindowManager mWindowManager) {
        mWindowManager.removeViewImmediate(layout);
    }

    public void showButton(WindowManager mWindowManager,WindowManager.LayoutParams layoutParams) {
        mWindowManager.addView(layout, layoutParams);
    }
    public void removeUI( FrameLayout parent_layout){
        layout.removeView(upButton);
        layout.removeView(downButton);
        parent_layout.removeView(layout);
    }
    public void setUI( FrameLayout parent_layout ){
        parent_layout.addView(layout);
        layout.addView(upButton);
        layout.addView(downButton);
    }
}
