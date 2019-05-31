package me.sieric.thehat.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import me.sieric.thehat.logic.GameHolder;
import me.sieric.thehat.R;

public class OfflineGameMenuActivity extends AppCompatActivity {

    private TextView wordNumber;
    private TextView playerA;
    private TextView playerB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_game_menu);

        Button playButton = findViewById(R.id.playButton);
        Button exitButton = findViewById(R.id.exitButton);

        wordNumber = findViewById(R.id.wordNumber);

        playButton.setOnLongClickListener(v -> {
            Intent intent = new Intent(OfflineGameMenuActivity.this, GameCountdownActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
            return true;
        });

        View.OnClickListener onClickListenerPlayAndExitButton = v -> {
            Toast toast = Toast.makeText(OfflineGameMenuActivity.this, getString(R.string.press_longer), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        };

        playButton.setOnClickListener(onClickListenerPlayAndExitButton);
        exitButton.setOnClickListener(onClickListenerPlayAndExitButton);

        playerA = findViewById(R.id.PlayerA);
        playerB = findViewById(R.id.PlayerB);

        exitButton.setOnLongClickListener(v -> {
            Intent intent = new Intent(OfflineGameMenuActivity.this, GameStatisticsActivity.class);
            startActivity(intent);
            return true;
        });
    }

        @Override
        protected void onStart() {
            super.onStart();

            if (GameHolder.game.getNumberOfUnfinishedWords() == 0) {
                Intent intent = new Intent(OfflineGameMenuActivity.this, GameStatisticsActivity.class);
                startActivity(intent);
            } else {
                wordNumber.setText(String.format(getString(R.string.words_remaining_format),  GameHolder.game.getNumberOfUnfinishedWords()));
                playerA.setText(GameHolder.game.getPlayersName(GameHolder.game.getFirstPlayer()));
                playerB.setText(GameHolder.game.getPlayersName(GameHolder.game.getSecondPlayer()));
            }
        }
}
