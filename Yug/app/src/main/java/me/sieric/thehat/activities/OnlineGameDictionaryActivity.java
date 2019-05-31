package me.sieric.thehat.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import me.sieric.thehat.R;
import me.sieric.thehat.logic.DBManager;
import me.sieric.thehat.logic.Dictionary;
import me.sieric.thehat.logic.GameHolder;

public class OnlineGameDictionaryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_game_dictionary);

        ListView dictListView = findViewById(R.id.dictListView);

        DBManager dbManager = new DBManager(this);

        List<Dictionary> dictList = dbManager.getDictionariesList();
        ArrayList<String> dictNames = new ArrayList<>(dictList.size());
        for (int i = 0; i < dictList.size(); i++) {
            dictNames.add(dictList.get(i).getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, dictNames);

        dictListView.setAdapter(adapter);

        dictListView.setOnItemClickListener((parent, itemClicked, position, id) -> {
            GameHolder.dictId = dictList.get(position).getId();
            Intent intent;
            intent = new Intent(OnlineGameDictionaryActivity.this, OnlineGameManageWordsActivity.class);
            startActivity(intent);
        });

    }
}
