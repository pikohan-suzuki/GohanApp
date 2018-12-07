package com.example.a81809.myapplication;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    Image image;
    FrameLayout parent_layout;
    DatabaseRead database;
    public static Point screenSize;
    public static int building_number;
    public static int floor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = new DatabaseRead(getApplication(),"database.db");
        parent_layout = findViewById(R.id.parent_layout);
        building_number=0;
        floor=0;



    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if(screenSize==null){
            screenSize=getViewSize(parent_layout);
            image = new Image(getApplication(),parent_layout,database);
        }
//        image.scale();
    }

    private Point getViewSize(View v){
        Point point =new Point(0,0);
        point.set(v.getWidth(),v.getHeight());
        return point;
    }
}
