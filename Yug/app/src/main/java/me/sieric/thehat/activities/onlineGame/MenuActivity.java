package me.sieric.thehat.activities.onlineGame;

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
import me.sieric.thehat.activities.game.CountdownActivity;
import me.sieric.thehat.activities.game.StatisticsActivity;
import me.sieric.thehat.logic.games.Game;
import me.sieric.thehat.logic.GameHolder;
import me.sieric.thehat.logic.NetworkManager;
import me.sieric.thehat.logic.games.OnlineGame;
import me.sieric.thehat.logic.games.OnlineGameStatus;

public class MenuActivity extends AppCompatActivity {

    private TextView wordsNumberView;
    private TextView playerAView;
    private TextView playerBView;
    private OnlineGameStatus status;
    private Timer timer;
    private TimerTask task;
    private final int TIME_STEP = 2000;
    private Button playButton;
    private Game game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_game_menu);

        playButton = findViewById(R.id.playButton);
        Button exitButton = findViewById(R.id.exitButton);

        playerAView = findViewById(R.id.firstPlayerView);
        playerBView = findViewById(R.id.secondPlayerView);

        wordsNumberView = findViewById(R.id.wordsNumberView);

        game = GameHolder.game;

        timer = new Timer();
        task = new UpdateTask();
        timer.scheduleAtFixedRate(task, 0, TIME_STEP);

        playButton.setOnClickListener(v -> {
            if (game.getFirstPlayer() != GameHolder.playerId) {
                return;
            }
            Toast toast = Toast.makeText(MenuActivity.this, getString(R.string.press_longer), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        });
        playButton.setOnLongClickListener(v -> {
            if (game.getFirstPlayer() != GameHolder.playerId) {
                return true;
            }
            Intent intent = new Intent(MenuActivity.this, CountdownActivity.class);
            startActivity(intent);
            return true;
        });

        if (!GameHolder.isCreator) {
            exitButton.setVisibility(View.INVISIBLE);
        }

        exitButton.setOnClickListener(v -> {
            Toast toast = Toast.makeText(MenuActivity.this, getString(R.string.press_longer), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        });
        exitButton.setOnLongClickListener(v -> {
            if (!GameHolder.isCreator) {
                return true;
            }
            task.cancel();
//            System.out.println("Lolkek");
            NetworkManager.finishGame(GameHolder.gameId);
            Intent intent = new Intent(MenuActivity.this, StatisticsActivity.class);
            startActivity(intent);
            return true;
        });
    }

    private void updateView() {
        wordsNumberView.setText(String.format(getString(R.string.words_remaining_format),  game.getNumberOfUnfinishedWords()));

        playerAView.setText(game.getPlayersName(game.getFirstPlayer()));
        playerBView.setText(game.getPlayersName(game.getSecondPlayer()));

        if (game.getFirstPlayer() == GameHolder.playerId) {
            playButton.setVisibility(View.VISIBLE);
        } else {
            playButton.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
//        System.out.println(game.getWords());
//        System.out.println(GameHolder.onlineGame.getUnfinishedWordsIds());
        if (game.getNumberOfUnfinishedWords() == 0) {
            task.cancel();
//            System.out.println("Kekekoko");
            NetworkManager.finishGame(GameHolder.gameId);
            Intent intent = new Intent(MenuActivity.this, StatisticsActivity.class);
            startActivity(intent);
        }
    }

    private void updateWords() {
        NetworkManager.finishedWords(GameHolder.gameId, status.getFinishedWords() - (game.getWordsNumber() - game.getNumberOfUnfinishedWords()), finishedIds -> {
            for (int i = 0; i < finishedIds.size(); i++) {
                ((OnlineGame) game).setWordAsFinished(finishedIds.get(i));
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
                        Intent intent = new Intent(MenuActivity.this, StatisticsActivity.class);
                        startActivity(intent);
                        return;
                    }
                    status = onlineGameStatus;
                    ((OnlineGame) game).setStatus(status);
                    if (status.getFinishedWords() > (game.getWordsNumber() - game.getNumberOfUnfinishedWords())) {
                        updateWords();
                    }
                    updateView();
                });
            });
        }
    }
}
