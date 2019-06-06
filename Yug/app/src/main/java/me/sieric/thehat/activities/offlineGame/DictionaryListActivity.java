package me.sieric.thehat.activities.offlineGame;

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
import me.sieric.thehat.logic.data.Dictionary;
import me.sieric.thehat.logic.games.OfflineGame;
import me.sieric.thehat.logic.GameHolder;
import me.sieric.thehat.logic.data.Word;

public class DictionaryListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_game_dictionary_list);

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
            Intent intent;
            if (GameHolder.gameType == GameHolder.GameType.ONE_TO_OTHERS) {
                ArrayList<Word> words = dbManager.getWordsList(GameHolder.dictId);
                GameHolder.game = new OfflineGame(words);
                intent = new Intent(DictionaryListActivity.this, MenuActivity.class);
            } else /*if (gameType.equals("offline"))*/ {
                intent = new Intent(DictionaryListActivity.this, ManagePlayersActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            }
            startActivity(intent);
        });

    }
}
