package com.example.a81809.kit_map;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    private FrameLayout parent_layout;
    private EditText locaEditText;
    private EditText destEditText;
    private ListView locaListView;
    private ListView destListView;
    private ArrayAdapter<String> locaAdapter;
    private ArrayAdapter<String> destAdapter;
    private String[][] rooms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        DatabaseRead database = new DatabaseRead(getApplication(), "database.db");
        rooms = database.getSearchRoom();

        parent_layout=findViewById(R.id.search_parent_layout);
        locaListView = new ListView(this);
        destListView=new ListView(this);

        Button testButton = findViewById(R.id.testtest);
        testButton.setOnClickListener(buttonClickListener);

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        locaEditText = findViewById(R.id.loca_editText);
        destEditText = findViewById(R.id.dest_editText);
        locaEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ArrayList<String> searchResult = new ArrayList<String>();
                for (int i = 0; i < rooms.length; i++) {
                    if (rooms[i][0].contains(s) || rooms[i][1].contains(s) || rooms[i][2].contains(s)||(rooms[i][0] + "-" + rooms[i][1] + " " + rooms[i][2]).contains(s))
                        searchResult.add(rooms[i][0] + "-" + rooms[i][1] + " " + rooms[i][2]);
                }
                locaListView.setX(locaEditText.getX());
                locaListView.setY(locaEditText.getY()+locaEditText.getHeight());
               locaAdapter = new ArrayAdapter<>(SearchActivity.this, android.R.layout.simple_list_item_1, searchResult);
                locaListView.setAdapter(locaAdapter);
                if(parent_layout.indexOfChild(locaListView)==-1)
                    parent_layout.addView(locaListView);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        destEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
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
            startBuildingNumber = 23;
            startFloor = 1;
            startRoadId = 1;
            destBuildNumber = 23;
            destFloor = 5;
            destRoadId = 18;

            Intent intent = new Intent();
            intent.putExtra("startBuildingNumber", startBuildingNumber);
            intent.putExtra("startFloor", startFloor);
            intent.putExtra("startRoadId", startRoadId);
            intent.putExtra("destBuildingNumber", destBuildNumber);
            intent.putExtra("destFloor", destFloor);
            intent.putExtra("destRoadId", destRoadId);
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    };
}

