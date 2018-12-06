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

        try {
            InputStream fp = getApplication().getAssets().open("asdf.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        DatabaseOpenHelper helper = new DatabaseOpenHelper(getApplicationContext(),"test.db", null, 1);
        SQLiteDatabase db = helper.getReadableDatabase();
        Toast toast = Toast.makeText(this,searchData(db),Toast.LENGTH_SHORT);
    }

    public String searchData(SQLiteDatabase db) {
        // Cursorを確実にcloseするために、try{}～finally{}にする
        Cursor cursor = null;
        try {
            //SQL文
            String sql = "SELECT * FROM test";

            //SQL文の実行
//            cursor = db.query("test", new String[]{"id", "name"}, null, null, null, null, null, null);
            cursor = db.rawQuery(sql,new String[]{});
            // 検索結果をcursorから読み込んで返す
            return readCursor(cursor);
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            // Cursorを忘れずにcloseする
            if (cursor != null) {
                cursor.close();
            }
        }
    }
    private String readCursor(Cursor cursor ){
        //カーソル開始位置を先頭にする
        cursor.moveToFirst();
        String str="";
        for (int i = 1; i <= cursor.getCount(); i++) {
            //SQL文の結果から、必要な値を取り出す
            str += cursor.getString(0);
            cursor.moveToNext();
        }
        return str;
    }
}
