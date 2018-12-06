package com.example.a81809.myapplication;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DatabaseRead database = new DatabaseRead(getApplication(),"test.db");
//        String str = database.getBuildingName();
        String str = database.getId();
        Log.d("debug","pppppppppp"+str);
    }
}
