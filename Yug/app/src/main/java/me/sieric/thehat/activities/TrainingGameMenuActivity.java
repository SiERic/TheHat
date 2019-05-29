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

import me.sieric.thehat.logic.Game;
import me.sieric.thehat.logic.GameHolder;
import me.sieric.thehat.R;

public class TrainingGameMenuActivity extends AppCompatActivity {

    private TextView wordNumber;
    private TextView playerA;
    private TextView playerB;
    private Button playButton;
    private Button exitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_game_menu);

        wordNumber = findViewById(R.id.wordNumber);
        playerA = findViewById(R.id.PlayerA);
        playerB = findViewById(R.id.PlayerB);
        playButton = findViewById(R.id.playButton);
        exitButton = findViewById(R.id.exitButton);

        View.OnLongClickListener onLongClickListenerPlayButton = v -> {
            Intent intent = new Intent(TrainingGameMenuActivity.this, GameCountdownActivity.class);
            startActivity(intent);
            return true;
        };

        playButton.setOnLongClickListener(onLongClickListenerPlayButton);

        View.OnClickListener onClickListenerPlayAndExitButton = v -> {
            Toast toast = Toast.makeText(TrainingGameMenuActivity.this, getString(R.string.press_longer), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        };

        playButton.setOnClickListener(onClickListenerPlayAndExitButton);
        exitButton.setOnClickListener(onClickListenerPlayAndExitButton);

        View.OnLongClickListener onLongClickListenerExitButton = v -> {
            Intent intent = new Intent(TrainingGameMenuActivity.this, GameStatisticsActivity.class);
            startActivity(intent);
            return true;
        };

        exitButton.setOnLongClickListener(onLongClickListenerExitButton);

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (GameHolder.game.getNumberOfUnguessedWords() == 0) {
            Intent intent = new Intent(TrainingGameMenuActivity.this, GameStatisticsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            GameHolder.game.increasePhase();
            wordNumber.setText(getString(R.string.words_remaining) + ": "+ GameHolder.game.getNumberOfUnguessedWords());
            playerA.setText(GameHolder.game.getCurrentPlayerAName());
            playerB.setText(GameHolder.game.getCurrentPlayerBName());
        }
    }
}
