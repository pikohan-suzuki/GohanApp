package com.example.a81809.getloadrange;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

        ImageView mapImageView;
//        TextView now;
//        MyView myView;
        int loadnumber;
        boolean firsttap;

        float firstRange[];
        float secondRange[];

        Point viewMaxSize;

        String str;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapImageView= findViewById(R.id.mapImageView);
//        Button fileButton = findViewById(R.id.file);
//        Button selectImageButton = findViewById(R.id.selectImage);
//        Button clearButton = findViewById(R.id.clear_button);
//        Button saveButton = findViewById(R.id.save_button);
//        now = findViewById(R.id.now);
//        myView = findViewById(R.id.my_view);
//        final Spinner buildingSpinner = findViewById(R.id.building_spinner);
//        final Spinner floorSpinner = findViewById(R.id.floor_spinner);

        String buildingList[] = {"1","2","3","4","5","6","7","8","9","21","23","24","lc","other"};
        String floorList[] ={"1","2","3","4","5","6","7","8","9","10","11","12","13","14"};
        firstRange = new float[2];
        secondRange=new float[2];

        loadnumber =0;
        firsttap=true;
        str =" id  x(%)  y(%)  x_flg  length(%)\n";

//        ArrayAdapter<String> buildingAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,buildingList);
//        ArrayAdapter<String> floorAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,floorList);
//        buildingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        floorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        buildingSpinner.setAdapter(buildingAdapter);
//        floorSpinner.setAdapter(floorAdapter);

        mapImageView.setOnTouchListener(new imageTouchListener());

//        saveButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(loadnumber==0){
//                    str+= buildingSpinner.getSelectedItem() +","+floorSpinner.getSelectedItem()+"\n";
//                }
//                str += loadnumber +","+firstRange[0]/mapImageView.getWidth()+","+firstRange[1]/mapImageView.getHeight() + ",";
//                if(firstRange[0]!=secondRange[0]) {
//                    str += "1";
//                    str += "," + (secondRange[0] - firstRange[0]) / mapImageView.getWidth() + "\n";
//                }else {
//                    str += "0";
//                    str += "," + (secondRange[1] - firstRange[1]) / mapImageView.getHeight() + "\n";
//                }
//                loadnumber++;
//            }
//        });

        //結果閲覧
//        fileButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this,ResultActivity.class);
//                intent.putExtra("str",str);
//                startActivity(intent);
//            }
//        });


        //画像選択
//        selectImageButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this,SelectImageActivity.class);
//                int requestCode =1001;
//                startActivityForResult(intent,requestCode);
//            }
//        });

    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(mapImageView.getWidth() !=0) {
            FrameLayout r = (FrameLayout) findViewById(R.id.parent_layout);
            viewMaxSize = getViewSize(r);
            changeImageSize(mapImageView);
        }
    }
    public static Point getViewSize(View View){
        Point point = new Point(0, 0);
        point.set(View.getWidth(), View.getHeight());
        return point;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    public void changeImageSize(View v){
        float facter =1;
        if(viewMaxSize.x/getViewSize(v).x >= viewMaxSize.y/getViewSize(v).y)
            facter=getViewSize(mapImageView).y/viewMaxSize.y;
        else
            facter=getViewSize(v).x/viewMaxSize.x;
        FrameLayout.LayoutParams frameLayoutParams;
        frameLayoutParams = new FrameLayout.LayoutParams((int)(getViewSize(v).x*facter) ,(int)(getViewSize(v).y*facter));
//        frameLayoutParams = new FrameLayout.LayoutParams(500 ,500);
        mapImageView.setLayoutParams(frameLayoutParams);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){

            case R.id.file:
                Intent intent1 = new Intent(MainActivity.this,ResultActivity.class);
                intent1.putExtra("str",str);
                startActivity(intent1);
                return true;
            case R.id.selectImage:
                Intent intent2 = new Intent(MainActivity.this,SelectImageActivity.class);
                int requestCode =1001;
                startActivityForResult(intent2,requestCode);
                return true;
            case R.id.clear:
                return true;
//            case R.id.save:
//                return true;
        }
        return false;
    }
    public void onActivityResult( int requestCode, int resultCode, Intent intent )
    {
        // startActivityForResult()の際に指定した識別コードとの比較
        if( requestCode == 1001 ){

            // 返却結果ステータスとの比較
            if( resultCode == Activity.RESULT_OK ){
                // 返却されてきたintentから値を取り出す
                int imageAddress = intent.getIntExtra("address",0);
                if(imageAddress!=0){
                    setImage(imageAddress);
                }
            }
        }
    }

    public void setImage(int address){
        mapImageView.setImageResource(address);
        Log.d("debug",";;;;;;;;;"+mapImageView.getWidth()+"+"+mapImageView.getHeight()+"+"+mapImageView.getX()+"+"+mapImageView.getY());
        firsttap=true;
//        now.setText("1");
        try {
            wait(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        changeImageSize(mapImageView);
    }

    public class imageTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            Log.d("debug","touched: x="+event.getX()+" y:="+event.getY() );
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if(firsttap){
                        firstRange[0]=event.getX()-mapImageView.getX();
                        firstRange[1]=event.getY()-mapImageView.getY();
//                        now.setText("2");
                        firsttap=false;
                    }else{
                        secondRange[0]=event.getX()-mapImageView.getX();
                        secondRange[1]=event.getY()-mapImageView.getY();

                        float x= secondRange[0]-firstRange[0];
                        float y = secondRange[1]-firstRange[1];
                        //x方向への移動
                        if(Math.abs(y)/Math.abs(x)<=1){
                            if(firstRange[0]<secondRange[0]){
                                secondRange[1]=firstRange[1];
                            }else{
                                float tmp[] = firstRange;
                                firstRange=secondRange;
                                secondRange=tmp;
                                secondRange[1]=firstRange[1];
                            }
                        }
                        //y方向への移動
                        else{
                            if(firstRange[1]<secondRange[1]){
                                secondRange[0]=firstRange[0];
                            }else{
                                float tmp[] = firstRange;
                                firstRange=secondRange;
                                secondRange=tmp;
                                secondRange[0]=firstRange[0];
                            }
                        }
//                        myView.setLine(firstRange[0],firstRange[1],secondRange[0],secondRange[1],mapImageView.getX(),mapImageView.getY());
//                        now.setText("1");
                        firsttap=true;
                    }
                    break;
                case MotionEvent.ACTION_UP:

                    break;
                case MotionEvent.ACTION_CANCEL:
                    break;
            }
            return false;
        }
    }
}
