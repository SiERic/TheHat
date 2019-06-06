package me.sieric.thehat.activities.offlineGame;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import me.sieric.thehat.activities.game.CountdownActivity;
import me.sieric.thehat.activities.game.StatisticsActivity;
import me.sieric.thehat.logic.GameHolder;
import me.sieric.thehat.R;

public class MenuActivity extends AppCompatActivity {

    private TextView wordNumber;
    private TextView firstPlayer;
    private TextView secondPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_game_menu);

        Button playButton = findViewById(R.id.playButton);
        Button exitButton = findViewById(R.id.exitButton);

        wordNumber = findViewById(R.id.wordsNumberView);

        playButton.setOnLongClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, CountdownActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
            return true;
        });

        View.OnClickListener onClickListenerPlayAndExitButton = v -> {
            Toast toast = Toast.makeText(MenuActivity.this, getString(R.string.press_longer), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        };

        playButton.setOnClickListener(onClickListenerPlayAndExitButton);
        exitButton.setOnClickListener(onClickListenerPlayAndExitButton);

        firstPlayer = findViewById(R.id.firstPlayerView);
        secondPlayer = findViewById(R.id.secondPlayerView);

        exitButton.setOnLongClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, StatisticsActivity.class);
            startActivity(intent);
            return true;
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (GameHolder.game.getNumberOfUnfinishedWords() == 0) {
            Intent intent = new Intent(MenuActivity.this, StatisticsActivity.class);
            startActivity(intent);
        } else {
            wordNumber.setText(String.format(getString(R.string.words_remaining_format),  GameHolder.game.getNumberOfUnfinishedWords()));
            firstPlayer.setText(GameHolder.game.getPlayersName(GameHolder.game.getFirstPlayer()));
            secondPlayer.setText(GameHolder.game.getPlayersName(GameHolder.game.getSecondPlayer()));
        }
    }

    @Override
    public void onBackPressed() {
        // nothing
    }

}
