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

import java.util.Timer;
import java.util.TimerTask;

import me.sieric.thehat.R;
import me.sieric.thehat.logic.GameHolder;
import me.sieric.thehat.logic.NetworkManager;
import me.sieric.thehat.logic.OnlineGameStatus;

public class OnlineGameMenuActivity extends AppCompatActivity {

    private TextView wordsNumberView;
    private TextView playerAView;
    private TextView playerBView;
    private OnlineGameStatus status;
    private Timer timer;
    private TimerTask task;
    private final int TIME_STEP = 2000;
    private Button playButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_online_game_menu);

        playButton = findViewById(R.id.playButton);
        Button exitButton = findViewById(R.id.exitButton);

        playerAView = findViewById(R.id.playerAView);
        playerBView = findViewById(R.id.playerBView);

        wordsNumberView = findViewById(R.id.wordNumber);

        timer = new Timer();
        task = new UpdateTask();
        timer.scheduleAtFixedRate(task, 0, TIME_STEP);

        playButton.setOnClickListener(v -> {
            if (GameHolder.onlineGame.getPlayerA() != GameHolder.playerId) {
                return;
            }
            Toast toast = Toast.makeText(OnlineGameMenuActivity.this, getString(R.string.press_longer), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        });
        playButton.setOnLongClickListener(v -> {
            if (GameHolder.onlineGame.getPlayerA() != GameHolder.playerId) {
                return true;
            }
            Intent intent = new Intent(OnlineGameMenuActivity.this, GameCountdownActivity.class);
            startActivity(intent);
            return true;
        });

        if (!GameHolder.isCreator) {
            exitButton.setVisibility(View.INVISIBLE);
        }

        exitButton.setOnClickListener(v -> {
            Toast toast = Toast.makeText(OnlineGameMenuActivity.this, getString(R.string.press_longer), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        });
        exitButton.setOnLongClickListener(v -> {
            if (!GameHolder.isCreator) {
                return true;
            }
            task.cancel();
            System.out.println("Lolkek");
            NetworkManager.finishGame(GameHolder.gameId);
            Intent intent = new Intent(OnlineGameMenuActivity.this, GameStatisticsActivity.class);
            startActivity(intent);
            return true;
        });
    }

    private void updateView() {
        wordsNumberView.setText(String.format(getString(R.string.words_remaining_format),  GameHolder.onlineGame.getWordsNumber() - status.getFinishedWords()));

        playerAView.setText(GameHolder.onlineGame.getPlayers().get(status.getFirstPlayer()).name);
        playerBView.setText(GameHolder.onlineGame.getPlayers().get(status.getSecondPlayer()).name);

        if (GameHolder.onlineGame.getPlayerA() == GameHolder.playerId) {
            playButton.setVisibility(View.VISIBLE);
        } else {
            playButton.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        System.out.println(GameHolder.onlineGame.getWords());
        System.out.println(GameHolder.onlineGame.getUnfinishedWordsIds());
        if (GameHolder.onlineGame.getNumberOfUnguessedWords() == 0) {
            task.cancel();
            System.out.println("Kekekoko");
            NetworkManager.finishGame(GameHolder.gameId);
            Intent intent = new Intent(OnlineGameMenuActivity.this, GameStatisticsActivity.class);
            startActivity(intent);
        }
    }

    private void updateWords() {
        NetworkManager.finishedWords(GameHolder.gameId, status.getFinishedWords() - (GameHolder.onlineGame.getWordsNumber() - GameHolder.onlineGame.getUnfinishedWordsIds().size()), finishedIds -> {
            for (int i = 0; i < finishedIds.size(); i++) {
                GameHolder.onlineGame.getUnfinishedWordsIds().remove(finishedIds.get(i));
            }
        });
    }

    private class UpdateTask extends TimerTask {

        @Override
        public void run() {
            NetworkManager.gameStatus(GameHolder.gameId, onlineGameStatus -> {
                runOnUiThread(() -> {
                    if (onlineGameStatus.getGameStatus() == OnlineGameStatus.GameStatus.FINISHED) {
                        System.out.println(onlineGameStatus.getGameStatus().toString());
                        task.cancel();
                        Intent intent = new Intent(OnlineGameMenuActivity.this, GameStatisticsActivity.class);
                        startActivity(intent);
                        return;
                    }
                    status = onlineGameStatus;
                    GameHolder.onlineGame.setPlayerA(status.getFirstPlayer());
                    GameHolder.onlineGame.setPlayerB(status.getSecondPlayer());
                    if (status.getFinishedWords() > (GameHolder.onlineGame.getWordsNumber() - GameHolder.onlineGame.getUnfinishedWordsIds().size())) {
                        updateWords();
                    }
                    updateView();
                });
            });
        }
    }
}
