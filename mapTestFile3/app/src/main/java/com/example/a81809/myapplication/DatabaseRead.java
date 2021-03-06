package com.example.a81809.myapplication;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class DatabaseRead {
    private SQLiteDatabase db;
    private Cursor cursor;

    public DatabaseRead(Context context,String databaseName){
        DatabaseOpenHelper helper = new DatabaseOpenHelper(context,databaseName, null, 1);
        db = helper.getReadableDatabase();
    }
    private ArrayList<String> searchData(String sql,String[] where){
        cursor = null;
        try {
            cursor = db.rawQuery(sql,where);
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
    private ArrayList<String> readData(Cursor cursor){
        //カーソル開始位置を先頭にする
        cursor.moveToFirst();
        ArrayList<String> str=new ArrayList<String>();
        for (int i = 1; i <= cursor.getCount(); i++) {
            //SQL文の結果から、必要な値を取り出す
            str.add( cursor.getString(0));
            cursor.moveToNext();
        }
        return str;
    }

    public ArrayList<String> getBuildingName(){
        String sql ="SELECT * FROM room";
        return searchData(sql,new String[] {});
    }
    public String[] getFloorImageSize(int building_number,int floor){
        String[] str = new String[2];
        String sql ="SELECT width FROM floor WHERE building_number= ? AND floor = ?";
        String[] where = {String.valueOf(building_number),String.valueOf(floor)};
        ArrayList<String> list= searchData(sql,where);
        str[0]=list.get(0);
        sql ="SELECT height FROM floor WHERE building_number= ? AND floor = ?";
        list=searchData(sql,where);
        str[1]=list.get(0);
        return str;
    }
    public int getImageResource(int building_number,int floor){
        String sql = "SELECT image FROM floor WHERE building_number= ? AND floor = ?";
        String[] where = {String.valueOf(building_number),String.valueOf(floor)};
        ArrayList<String> list = searchData(sql,where);
        return Integer.parseInt(list.get(0));
    }

    public  int getFacilityType(int building_number,int floor,int id){
        String sql = "SELECT type FROM facility WHERE building_number= ? AND floor = ? AND id = ?";
        String[] where = {String.valueOf(building_number),String.valueOf(floor),String.valueOf(id)};
        ArrayList<String > list = searchData(sql,where);
        return Integer.parseInt(list.get(0));
    }
    public int[] getFacilityRange(int building_number,int floor,int id) {
        int[] range = new int[2];
        String sql = "SELECT x FROM facility WHERE building_number= ? AND floor = ? AND id = ?";
        String[] where = {String.valueOf(building_number), String.valueOf(floor), String.valueOf(id)};
        ArrayList<String> list = searchData(sql, where);
        range[0] = Integer.parseInt(list.get(0));
        sql = "SELECT height FROM floor WHERE building_number= ? AND floor = ? AND id = ?";
        list = searchData(sql, where);
        range[1] = Integer.parseInt(list.get(0));
        return range;
    }
    public String[] getRoomNumbers(int building_number,int floor){
        String sql ="SELECT room_number FROM room WHERE building_number = ? AND floor = ? ";
        String[] where ={String.valueOf(building_number),String.valueOf(floor)};
        ArrayList<String> list = searchData(sql, where);
        return (String[]) list.toArray();
    }
    public String[] getRoomInfo(int building_number,int floor,int room_id){
        String[] info = new String[3];
        String sql = "SELECT name FROM room WHERE building_number = ? AND floor = ? AND room_id =?";
        String[] where ={String.valueOf(building_number),String.valueOf(floor),String.valueOf(room_id)};
        ArrayList<String> list = searchData(sql,where);
        info[0] = list.get(0);
        sql = "SELECT x FROM room WHERE building_number = ? AND floor = ? AND room_id =?";
        list = searchData(sql,where);
        info[1]=list.get(0);
        sql = "SELECT y FROM room WHERE building_number = ? AND floor = ? AND room_id =?";
        list = searchData(sql,where);
        info[2]=list.get(0);
        return info;
    }
}
