package me.sieric.thehat.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import me.sieric.thehat.R;
import me.sieric.thehat.logic.DBManager;
import me.sieric.thehat.logic.Dictionary;
import me.sieric.thehat.logic.OfflineGame;
import me.sieric.thehat.logic.GameHolder;
import me.sieric.thehat.logic.Word;

public class DictionaryChoiceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_dictionary_choose);

        ListView dictListView = findViewById(R.id.dictList);

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
            String gameType = getIntent().getStringExtra("offlineGame");
            Intent intent;
            if (gameType.equals("training")) {
                ArrayList<Word> words = dbManager.getWordsList(GameHolder.dictId);
                GameHolder.game = new OfflineGame(words);
                intent = new Intent(DictionaryChoiceActivity.this, OfflineGameMenuActivity.class);
                //intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            } else /*if (gameType.equals("offline"))*/ {
                intent = new Intent(DictionaryChoiceActivity.this, ManagePlayersActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            }
            startActivity(intent);
        });

    }
}
