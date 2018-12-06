package com.example.a81809.getloadrange;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.lang.reflect.Field;

public class SelectImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_image);

        ListView listView = findViewById(R.id.listView);
        Button returnButton =findViewById(R.id.return_button);

        Field[] fields = R.drawable.class.getFields();
        final int list[] = new int[100];
        String nameList[] = new String[100];
        int i = 0;
        for (Field field : fields) {
            try {
                String name = field.getName();
                int id = (Integer) field.get(name);
                list[i] = id;
                nameList[i]=name;
                i++;
            } catch (Exception e) {
                Log.d("debug", "Exception happened!");
            }
        }

        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("address",0);
                setResult(Activity.RESULT_OK,intent);
                finish();
            }
        });

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,nameList);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.putExtra("address",list[position]);
                setResult(Activity.RESULT_OK,intent);
                finish();
            }
        });

    }

}
