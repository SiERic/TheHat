package me.sieric.thehat.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import me.sieric.thehat.R;
import me.sieric.thehat.activities.offlineGame.DictionaryListActivity;
import me.sieric.thehat.activities.onlineGame.CreateJoinActivity;
import me.sieric.thehat.activities.settings.SettingsActivity;
import me.sieric.thehat.logic.GameHolder;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        Button oneToOthersButton = findViewById(R.id.oneToOthersButton);
        Button offlineGameButton = findViewById(R.id.offlineGameButton);
        Button onlineGameButton = findViewById(R.id.onlineGameButton);
        Button settingsButton = findViewById(R.id.settingsButton);
        Button manageDictionariesButton = findViewById(R.id.manageDictionariesButton);

        oneToOthersButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, DictionaryListActivity.class);
            GameHolder.gameType = GameHolder.GameType.ONE_TO_OTHERS;
            startActivity(intent);
        });

        offlineGameButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, DictionaryListActivity.class);
            GameHolder.gameType = GameHolder.GameType.OFFLINE;
            startActivity(intent);
        });

        settingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

        manageDictionariesButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, me.sieric.thehat.activities.dictionaryManagement.DictionaryListActivity.class);
            startActivity(intent);
        });

        onlineGameButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CreateJoinActivity.class);
            startActivity(intent);
        });
    }

}
