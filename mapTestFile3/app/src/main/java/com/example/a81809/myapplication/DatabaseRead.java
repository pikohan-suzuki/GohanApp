package com.example.a81809.myapplication;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseRead {
    SQLiteDatabase db;
    Cursor cursor;

    public DatabaseRead(Context context,String databaseName){
        DatabaseOpenHelper helper = new DatabaseOpenHelper(context,"database.db", null, 1);
        db = helper.getReadableDatabase();
    }
    private String searchData(String sql){
        cursor = null;
        try {
            cursor = db.rawQuery(sql,new String[]{});
            return readData(cursor);
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
    private String readData(Cursor cursor){
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

    public String getBuildingName(){
        String sql ="SELECT * FROM building";
        return searchData(sql);
    }
}
