package me.sieric.thehat.activities.onlineGame;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import me.sieric.thehat.R;
import me.sieric.thehat.activities.game.CountdownActivity;
import me.sieric.thehat.activities.game.StatisticsActivity;
import me.sieric.thehat.logic.game.Game;
import me.sieric.thehat.logic.GameHolder;
import me.sieric.thehat.logic.NetworkManager;
import me.sieric.thehat.logic.game.OnlineGame;

public class MenuActivity extends AppCompatActivity {

    private TextView wordsNumberView;
    private TextView playerAView;
    private TextView playerBView;
    private Timer timer;
    private TimerTask task;
    private final int TIME_STEP = 2000;
    private Button playButton;
    private OnlineGame game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_menu);

        playButton = findViewById(R.id.playButton);
        Button exitButton = findViewById(R.id.exitButton);

        playerAView = findViewById(R.id.firstPlayerView);
        playerBView = findViewById(R.id.secondPlayerView);

        wordsNumberView = findViewById(R.id.wordsNumberView);

        game = (OnlineGame) GameHolder.game;

        timer = new Timer();
        task = new UpdateTask();
        timer.scheduleAtFixedRate(task, 0, TIME_STEP);

        if (!GameHolder.isCreator) {
            exitButton.setVisibility(View.INVISIBLE);
            exitButton.setClickable(false);
        }

        playButton.setOnClickListener(v -> {
            Toast toast = Toast.makeText(MenuActivity.this, getString(R.string.press_longer), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        });
        playButton.setOnLongClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, CountdownActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
            return true;
        });

        exitButton.setOnClickListener(v -> {
            Toast toast = Toast.makeText(MenuActivity.this, getString(R.string.press_longer), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        });
        exitButton.setOnLongClickListener(v -> {
            task.cancel();
            NetworkManager.finishGame(GameHolder.gameId);
            Intent intent = new Intent(MenuActivity.this, StatisticsActivity.class);
            startActivity(intent);
            return true;
        });
    }

    private void updateView() {
        wordsNumberView.setText(String.format(getString(R.string.words_remaining_format),  game.getNumberOfUnfinishedWords()));

        playerAView.setText(game.getPlayerName(game.getFirstPlayer()));
        playerBView.setText(game.getPlayerName(game.getSecondPlayer()));

        if (game.getFirstPlayer() == GameHolder.playerId) {
            playButton.setVisibility(View.VISIBLE);
            playButton.setClickable(true);
        } else {
            playButton.setVisibility(View.INVISIBLE);
            playButton.setClickable(false);
        }
    }

    private void updateWords() {
        NetworkManager.finishedWords(GameHolder.gameId, 0, finishedIds -> {
            for (int i = 0; i < finishedIds.size(); i++) {
                game.setWordAsFinished(finishedIds.get(i));
            }
        });
    }

    private class UpdateTask extends TimerTask {

        @Override
        public void run() {
            NetworkManager.gameStatus(GameHolder.gameId, onlineGameStatus -> {
                runOnUiThread(() -> {
                    if (onlineGameStatus.getGameStatus() == OnlineGame.Status.GameStatus.FINISHED) {
                        System.out.println(onlineGameStatus.getGameStatus().toString());
                        task.cancel();
                        Intent intent = new Intent(MenuActivity.this, StatisticsActivity.class);
                        startActivity(intent);
                        return;
                    }
                    game.setStatus(onlineGameStatus);
                    if (onlineGameStatus.getFinishedWords() > (game.getWordsNumber() - game.getNumberOfUnfinishedWords())) {
                        updateWords();
                    }
                    if (game.getNumberOfUnfinishedWords() == 0) {
                        task.cancel();
                        NetworkManager.finishGame(GameHolder.gameId);
                        Intent intent = new Intent(MenuActivity.this, StatisticsActivity.class);
                        startActivity(intent);
                    }
                    updateView();
                });
            });
        }
    }

    @Override
    public void onBackPressed() {
        // nothing
    }
}
