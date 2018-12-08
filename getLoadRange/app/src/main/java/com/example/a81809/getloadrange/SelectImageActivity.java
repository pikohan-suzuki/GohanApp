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
import android.widget.Spinner;
import android.widget.Toast;

import java.lang.reflect.Field;

public class SelectImageActivity extends AppCompatActivity {
    private Spinner buildingSpinner;
    private Spinner floorSpinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_image);

        buildingSpinner = findViewById(R.id.building_spinner);
        floorSpinner = findViewById(R.id.floor_spinner);
        String buildingList[] = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "21", "23", "24", "100"};
        String floorList[] = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14"};
        final ArrayAdapter<String> buildingAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, buildingList);
        final ArrayAdapter<String> floorAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, floorList);
        buildingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        floorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        buildingSpinner.setAdapter(buildingAdapter);
        floorSpinner.setAdapter(floorAdapter);

        Button return_button = findViewById(R.id.return_button);
        Button select_button = findViewById(R.id.select_button);

        return_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("building_name", 999);
                intent.putExtra("floor",999);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });

        select_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("building_name", Integer.parseInt((String) buildingSpinner.getSelectedItem()));
                intent.putExtra("floor",Integer.parseInt(String.valueOf(floorSpinner.getSelectedItem())));
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });

    }
}
