package com.example.a81809.kit_map;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class DatabaseRead {
    private SQLiteDatabase db;
    private Cursor cursor;

    public DatabaseRead(Context context, String databaseName) {
        DatabaseOpenHelper helper = new DatabaseOpenHelper(context, databaseName, null, 1);
        db = helper.getReadableDatabase();
    }

    private ArrayList<String> searchData(String sql, String[] where) {
        cursor = null;
        try {
            cursor = db.rawQuery(sql, where);
            return readData(cursor);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            // Cursorを忘れずにcloseする
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private ArrayList<String> readData(Cursor cursor) {
        //カーソル開始位置を先頭にする
        cursor.moveToFirst();
        ArrayList<String> str = new ArrayList<>();
        for (int i = 1; i <= cursor.getCount(); i++) {
            //SQL文の結果から、必要な値を取り出す
            str.add(cursor.getString(0));
            cursor.moveToNext();
        }
        return str;
    }

    public ArrayList<String> getBuildingName() {
        String sql = "SELECT * FROM room";
        return searchData(sql, new String[]{});
    }

    public String[] getFloorImageSize(int building_number, int floor) {
        String[] str = new String[2];
        String sql = "SELECT width FROM floor WHERE building_number= ? AND floor = ?";
        String[] where = {String.valueOf(building_number), String.valueOf(floor)};
        ArrayList<String> list = searchData(sql, where);
        str[0] = list.get(0);
        sql = "SELECT height FROM floor WHERE building_number= ? AND floor = ?";
        list = searchData(sql, where);
        str[1] = list.get(0);
        return str;
    }

    public String getImageResource(int building_number, int floor) {
        String sql = "SELECT image FROM floor WHERE building_number= ? AND floor = ?";
        String[] where = {String.valueOf(building_number), String.valueOf(floor)};
        ArrayList<String> list = searchData(sql, where);
        return list.get(0);
    }

    public int getFacilityType(int building_number, int floor, int id) {
        String sql = "SELECT type FROM facility WHERE building_number= ? AND floor = ? AND facility_number = ?";
        String[] where = {String.valueOf(building_number), String.valueOf(floor), String.valueOf(id)};
        ArrayList<String> list = searchData(sql, where);
        return Integer.parseInt(list.get(0));
    }

    public float[] getFacilityRange(int building_number, int floor, int id) {
        float[] range = new float[2];
        String sql = "SELECT x FROM facility WHERE building_number= ? AND floor = ? AND facility_number = ?";
        String[] where = {String.valueOf(building_number), String.valueOf(floor), String.valueOf(id)};
        ArrayList<String> list = searchData(sql, where);
        range[0] = Float.parseFloat(list.get(0));
        sql = "SELECT y FROM facility WHERE building_number= ? AND floor = ? AND facility_number = ?";
        list = searchData(sql, where);
        range[1] = Float.parseFloat(list.get(0));
        return range;
    }

    public int[] getRoomNumbers(int building_number, int floor) {
        String sql = "SELECT room_number FROM room WHERE building_number = ? AND floor = ? ";
        String[] where = {String.valueOf(building_number), String.valueOf(floor)};
        ArrayList<String> list = searchData(sql, where);
        int[] result = new int[list.size()];
        for (int i = 0; i < list.size(); i++)
            result[i] = Integer.parseInt(list.get(i));
        return result;
    }

    public String[] getRoomInfo(int building_number, int floor, int room_id) {
        String[] info = new String[3];
        String sql = "SELECT name FROM room WHERE building_number = ? AND floor = ? AND room_number =?";
        String[] where = {String.valueOf(building_number), String.valueOf(floor), String.valueOf(room_id)};

        ArrayList<String> list = searchData(sql, where);
        Log.d("debug", "aaaaaaaaaaaaa" + list);
        info[0] = list.get(0);
        sql = "SELECT x FROM room WHERE building_number = ? AND floor = ? AND room_number =?";
        list = searchData(sql, where);
        info[1] = list.get(0);
        sql = "SELECT y FROM room WHERE building_number = ? AND floor = ? AND room_number =?";
        list = searchData(sql, where);
        info[2] = list.get(0);
        return info;
    }

    public int[] getFacilityNumber(int building_number, int floor) {
        String sql = "SELECT facility_number FROM facility WHERE building_number = ? AND floor = ? ";
        String[] where = {String.valueOf(building_number), String.valueOf(floor)};
        ArrayList<String> list = searchData(sql, where);
        int[] result = new int[list.size()];
        for (int i = 0; i < list.size(); i++)
            result[i] = Integer.parseInt(list.get(i));
        return result;
    }

    public int getNumberOfFloor(int building_number) {
        String sql = "SELECT COUNT(*) FROM floor WHERE building_number = ?";
        String[] where = {String.valueOf(building_number)};
        ArrayList<String> list = searchData(sql, where);
        int result = Integer.parseInt(list.get(0));
        return result;
    }

    public float[] getFloorRangeSize(int building_number) {
        String sql = "SELECT left_longitude FROM building WHERE building_number = ?";
        String[] where = {String.valueOf(building_number)};
        float[] result = new float[4];
        ArrayList<String> list = searchData(sql, where);
        result[0] = Float.parseFloat(list.get(0));
        sql = "SELECT top_latitude FROM building WHERE building_number = ?";
        list = searchData(sql, where);
        result[1] = Float.parseFloat(list.get(0));
        sql = "SELECT longitude FROM building WHERE building_number = ?";
        list = searchData(sql, where);
        result[2] = Float.parseFloat(list.get(0));
        sql = "SELECT latitude FROM building WHERE building_number = ?";
        list = searchData(sql, where);
        result[3] = Float.parseFloat(list.get(0));
        return result;
    }

    public float[] getRoad_x(int building_number, int floor) {
        String sql = "SELECT x FROM road WHERE building_number = ? AND floor = ?";
        String[] where = {String.valueOf(building_number), String.valueOf(floor)};
        ArrayList<String> list = searchData(sql, where);
        float[] result = new float[list.size()];
        for (int i = 0; i < list.size(); i++) result[i] = Float.parseFloat(list.get(i));
        return result;
    }

    public float[] getRoad_y(int building_number, int floor) {
        String sql = "SELECT y FROM road WHERE building_number = ? AND floor = ?";
        String[] where = {String.valueOf(building_number), String.valueOf(floor)};
        ArrayList<String> list = searchData(sql, where);
        float[] result = new float[list.size()];
        for (int i = 0; i < list.size(); i++) result[i] = Float.parseFloat(list.get(i));
        return result;
    }

    public float[] getRoadLength(int building_number, int floor) {
        String sql = "SELECT length FROM road WHERE building_number = ? AND floor = ?";
        String[] where = {String.valueOf(building_number), String.valueOf(floor)};
        ArrayList<String> list = searchData(sql, where);
        float[] result = new float[list.size()];
        for (int i = 0; i < list.size(); i++) result[i] = Float.parseFloat(list.get(i));
        return result;
    }

    public boolean[] getRoad_xDir(int building_number, int floor) {
        String sql = "SELECT is_xDirection FROM road WHERE building_number = ? AND floor = ?";
        String[] where = {String.valueOf(building_number), String.valueOf(floor)};
        ArrayList<String> list = searchData(sql, where);
        boolean[] result = new boolean[list.size()];
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).equals("0"))
                result[i] = false;
            else
                result[i] = true;
        }

        return result;
    }

    public int[][] getFloorDir(int building_number, int floor) {
        String sql = "SELECT from_road_id FROM road_connect WHERE from_building_number= ? AND to_building_number= ? AND from_floor = ? AND to_floor= ?";
        String[] where = {String.valueOf(building_number),String.valueOf(building_number), String.valueOf(floor),String.valueOf(floor)};
        ArrayList<String> list = searchData(sql, where);
        int result[][] = new int[list.size()][2];
        for (int i = 0; i < list.size(); i++) result[i][0] = Integer.parseInt(list.get(i));
        sql = "SELECT to_road_id FROM road_connect WHERE from_building_number= ? AND to_building_number= ? AND from_floor = ? AND to_floor= ?" ;
        list = searchData(sql, where);
        for (int i = 0; i < list.size(); i++) result[i][1] = Integer.parseInt(list.get(i));
        return result;
    }
    public int[] getRoadId(int building_number,int floor){
        String sql = "SELECT road_id FROM road WHERE building_number = ? AND floor= ?";
        String[] where = {String.valueOf(building_number), String.valueOf(floor)};
        ArrayList<String> list = searchData(sql, where);
        int[] result = new int[list.size()];
        for(int i=0;i<list.size();i++){
            result[i] = Integer.parseInt(list.get(i));
        }
        return result;
    }
    public int[][] getBuildingDir(int building_number){
        String sql = "SELECT from_floor FROM road_connect WHERE from_building_number= ? AND to_building_number= ?";
        String[] where = {String.valueOf(building_number),String.valueOf(building_number)};
        ArrayList<String> list = searchData(sql, where);
        int result[][] = new int[list.size()][4];
        for (int i = 0; i < list.size(); i++) result[i][0] = Integer.parseInt(list.get(i));
        sql = "SELECT from_road_id FROM road_connect WHERE from_building_number= ? AND to_building_number= ?";
        list = searchData(sql, where);
        for (int i = 0; i < list.size(); i++) result[i][1] = Integer.parseInt(list.get(i));
        sql = "SELECT to_floor FROM road_connect WHERE from_building_number= ? AND to_building_number= ?";
        list = searchData(sql, where);
        for (int i = 0; i < list.size(); i++) result[i][2] = Integer.parseInt(list.get(i));
        sql = "SELECT to_road_id FROM road_connect WHERE from_building_number= ? AND to_building_number= ?";
        list = searchData(sql, where);
        for (int i = 0; i < list.size(); i++) result[i][3] = Integer.parseInt(list.get(i));
        return result;
    }
    public float[][] getBuildingRoadLength(int building_number,int numberOfFloor,int maxNumberOfRoad){
        String sql = "SELECT length FROM road WHERE building_number= ? AND floor = ?";
        float[][] result = new float[numberOfFloor][maxNumberOfRoad];
        ArrayList<String> list;
        for(int i=0;i<numberOfFloor;i++){
            String[] where = {String.valueOf(building_number),String.valueOf(i+1)};
            list = searchData(sql,where);
            for(int j=0;j<maxNumberOfRoad;j++){
                if(j<list.size())
                    result[i][j] = Float.parseFloat(list.get(j));
                else
                    result[i][j] = -1;
            }
        }
        return result;
    }
    public int getMaxNumberOfRoad(int building_nubmer){
        String sql ="SELECT COUNT(*) FROM road WHERE building_number = ? GROUP BY floor";
        String[] where ={String.valueOf(building_nubmer)};
        ArrayList<String> list = searchData(sql, where);
        int result = 0;
        for(String value: list){
            if(result< Integer.parseInt(value))
                result = Integer.parseInt(value);
        }
        return result;
    }
}
