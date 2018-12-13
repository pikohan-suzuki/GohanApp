package com.example.a81809.kit_map;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class SearchActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Button testButton = findViewById(R.id.testtest);
        testButton.setOnClickListener(buttonClickListener);

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private View.OnClickListener buttonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int startBuildingNumber;
            int startFloor;
            int startRoadId;
            int destBuildNumber;
            int destFloor;
            int destRoadId;
            startBuildingNumber=23;
            startFloor=1;
            startRoadId=1;
            destBuildNumber=23;
            destFloor=1;
            destRoadId=20;

            Intent intent = new Intent();
            intent.putExtra("startBuildingNumber",startBuildingNumber);
            intent.putExtra("startFloor",startFloor);
            intent.putExtra("startRoadId",startRoadId);
            intent.putExtra("destBuildingNumber",destBuildNumber);
            intent.putExtra("destFloor",destFloor);
            intent.putExtra("destRoadId",destRoadId);
            setResult(Activity.RESULT_OK,intent);
            finish();
        }
    };
}
