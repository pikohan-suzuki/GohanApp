package com.example.a81809.kit_map;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
    private Button searchButton;
    private ArrayAdapter<String> locaAdapter;
    private ArrayAdapter<String> destAdapter;
    private ArrayList<String> locaSearchResult;
    private ArrayList<String> destSearchResult;
    private String[] locaInfo;
    private String[] destInfo;
    private String[][] rooms;

    private DatabaseRead database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

       database = new DatabaseRead(getApplication(), "database.db");
        rooms = database.getSearchRoom();

        parent_layout = findViewById(R.id.search_parent_layout);
        parent_layout.setOnClickListener(layout_clickListener);
        locaListView = new ListView(this);
        destListView = new ListView(this);
        searchButton=findViewById(R.id.search_route_button);
        searchButton.setOnClickListener(searchButton_clickListener);
        locaInfo = new String[3];
        destInfo=new String[3];

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
                locaSearchResult = new ArrayList<String>();
                int numberOfForecast = 0;
                for (int i = 0; i < rooms.length; i++) {
                    if(s.length()!=0) {
                        if (rooms[i][0].contains(s) || rooms[i][1].contains(s) || rooms[i][2].contains(s) ||
                                (rooms[i][0] + "-" + rooms[i][1] + " " + rooms[i][2]).contains(s)) {
                            if(rooms[i][2].contains("号館"))
                                locaSearchResult.add(rooms[i][2]);
                            else
                                locaSearchResult.add(rooms[i][0] + "-" + rooms[i][1] + " " + rooms[i][2]);
                            numberOfForecast++;
                        }
                    }
            }
            locaAdapter = new ArrayAdapter<>(SearchActivity.this, android.R.layout.simple_list_item_1, locaSearchResult);
                locaListView.setAdapter(locaAdapter);
                locaListView.setBackgroundColor(Color.argb(255,240,240,250));
                if (numberOfForecast < 6)
                    locaListView.setLayoutParams(new FrameLayout.LayoutParams(locaEditText.getWidth(), ViewGroup.LayoutParams.WRAP_CONTENT));
                else
                        locaListView.setLayoutParams(new FrameLayout.LayoutParams(locaEditText.getWidth(), locaEditText.getHeight()*4));
                locaListView.setX(locaEditText.getX());
                locaListView.setY(locaEditText.getY() + locaEditText.getHeight());

                if (parent_layout.indexOfChild(locaListView) == -1)
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
                destSearchResult = new ArrayList<String>();
                int numberOfForecast = 0;
                for (int i = 0; i < rooms.length; i++) {
                    if(s.length()!=0) {
                        if (rooms[i][0].contains(s) || rooms[i][1].contains(s) || rooms[i][2].contains(s) ||
                                (rooms[i][0] + "-" + rooms[i][1] + " " + rooms[i][2]).contains(s)) {
                            if(rooms[i][2].contains("号館"))
                                destSearchResult.add(rooms[i][2]);
                            else
                                destSearchResult.add(rooms[i][0] + "-" + rooms[i][1] + " " + rooms[i][2]);
                            numberOfForecast++;
                        }
                    }
                }
                destAdapter = new ArrayAdapter<>(SearchActivity.this, android.R.layout.simple_list_item_1, destSearchResult);
                destListView.setAdapter(destAdapter);
                destListView.setBackgroundColor(Color.argb(255,240,240,250));
                if (numberOfForecast < 6)
                    destListView.setLayoutParams(new FrameLayout.LayoutParams(destEditText.getWidth(), ViewGroup.LayoutParams.WRAP_CONTENT));
                else
                    destListView.setLayoutParams(new FrameLayout.LayoutParams(destEditText.getWidth(), destEditText.getHeight()*4));
                destListView.setX(destEditText.getX());
                destListView.setY(destEditText.getY() + destEditText.getHeight());

                if (parent_layout.indexOfChild(destListView) == -1)
                    parent_layout.addView(destListView);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        locaListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                locaEditText.setText(locaSearchResult.get(position));
                locaInfo = locaSearchResult.get(position).split("-| ");
                parent_layout.removeView(locaListView);
            }
        });
        destListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                destEditText.setText(destSearchResult.get(position));
                destInfo = destSearchResult.get(position).split("-| ");
                parent_layout.removeView(destListView);
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
    private View.OnClickListener layout_clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            locaEditText.clearFocus();
            destEditText.clearFocus();
            parent_layout.removeView(destListView);
            parent_layout.removeView(locaListView);
        }
    };
    private View.OnClickListener searchButton_clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            if (locaInfo!=null & destInfo!=null){
                int[] start = database.getRoadIdFromRoom(locaInfo[0],locaInfo[1]);
                int[] dest=database.getRoadIdFromRoom(destInfo[0],destInfo[1]);
                Intent intent = new Intent();
                intent.putExtra("startBuildingNumber", start[0]);
                intent.putExtra("startFloor", start[1]);
                intent.putExtra("startRoadId", start[2]);
                intent.putExtra("destBuildingNumber", dest[0]);
                intent.putExtra("destFloor", dest[1]);
                intent.putExtra("destRoadId", dest[2]);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        }
    };

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

