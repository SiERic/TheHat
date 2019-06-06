package me.sieric.thehat.activities.onlineGame;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import me.sieric.thehat.R;
import me.sieric.thehat.logic.DBManager;
import me.sieric.thehat.logic.data.Dictionary;
import me.sieric.thehat.logic.GameHolder;

public class DictionaryListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_game_dictionary_list);

        Button okButton = findViewById(R.id.okButton);
        okButton.setOnClickListener(v -> DictionaryListActivity.this.onBackPressed());

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
            intent = new Intent(DictionaryListActivity.this, AddWordsActivity.class);
            startActivity(intent);
        });

    }
}
