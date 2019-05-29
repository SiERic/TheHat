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

        View.OnLongClickListener onLongClickListenerPlayButton = v -> {
            Intent intent = new Intent(OfflineGameMenuActivity.this, GameCountdownActivity.class);
            startActivity(intent);
            return true;
        };

        playButton.setOnLongClickListener(onLongClickListenerPlayButton);

        View.OnClickListener onClickListenerPlayAndExitButton = v -> {
            Toast toast = Toast.makeText(OfflineGameMenuActivity.this, getString(R.string.press_longer), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        };

        playButton.setOnClickListener(onClickListenerPlayAndExitButton);
        exitButton.setOnClickListener(onClickListenerPlayAndExitButton);

        playerA = findViewById(R.id.PlayerA);
        playerB = findViewById(R.id.PlayerB);

        playerA.setTextSize(24);
        playerB.setTextSize(24);

        View.OnLongClickListener onLongClickListenerExitButton = v -> {
            Intent intent = new Intent(OfflineGameMenuActivity.this, GameStatisticsActivity.class);
            startActivity(intent);
            return true;
        };

        exitButton.setOnLongClickListener(onLongClickListenerExitButton);
    }

        @Override
        protected void onStart() {
            super.onStart();

            if (GameHolder.game.getNumberOfUnguessedWords() == 0) {
                Intent intent = new Intent(OfflineGameMenuActivity.this, GameStatisticsActivity.class);
                startActivity(intent);
            } else {
                GameHolder.game.increasePhase();
                wordNumber.setText(String.format(getString(R.string.words_remaining_format),  GameHolder.game.getNumberOfUnguessedWords()));
                playerA.setText(GameHolder.game.getCurrentPlayerAName());
                playerB.setText(GameHolder.game.getCurrentPlayerBName());
            }
        }
}
