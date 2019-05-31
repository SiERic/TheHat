package me.sieric.thehat.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import me.sieric.thehat.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        Button trainingModeButton = findViewById(R.id.trainingModeButton);
        Button offlineGameButton = findViewById(R.id.offlineGameButton);
        Button onlineGameButton = findViewById(R.id.onlineGameButton);
        Button settingsButton = findViewById(R.id.settingsButton);
        Button manageDictionariesButton = findViewById(R.id.manageDictionariesButton);

        trainingModeButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, DictionaryChoiceActivity.class);
            intent.putExtra("offlineGame", "training");
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
        });

        offlineGameButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, DictionaryChoiceActivity.class);
            intent.putExtra("offlineGame", "offline");
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
        });

        settingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
        });

        manageDictionariesButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, DictionaryListActivity.class);
            startActivity(intent);
        });

        onlineGameButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, OnlineGameChooseActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
        });
    }

}