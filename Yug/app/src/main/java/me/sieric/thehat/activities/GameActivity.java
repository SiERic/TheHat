package me.sieric.thehat.activities;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import me.sieric.thehat.logic.Game;
import me.sieric.thehat.logic.GameHolder;
import me.sieric.thehat.R;
import me.sieric.thehat.logic.NetworkManager;
import me.sieric.thehat.logic.OnlineGame;
import me.sieric.thehat.logic.Word;

public class GameActivity extends AppCompatActivity {

    private TextView remainingTimeView;
    private TextView currentWordView;

    private Timer timer;
    private int time;
    private int beginningTimeOfCurrentWord;
    private Word currentWord;

    private final int SECOND = (int) TimeUnit.SECONDS.toMillis(1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_game);

        remainingTimeView = findViewById(R.id.remainingTime);
        currentWordView = findViewById(R.id.currentWord);

        Button okButton = findViewById(R.id.okButton);
        Button errorButton = findViewById(R.id.errorButton);
        Button skipButton = findViewById(R.id.skipButton);

        time = 0;
        beginningTimeOfCurrentWord = 0;
        remainingTimeView.setText(getBeautifulTime(time));
        if (!GameHolder.isOffline) {
            GameHolder.onlineGame.setPhaseWords(new ArrayList<>());
        }

        timer = new Timer();
        timer.schedule(new First20SecondsTimerTask(), SECOND);

        if (GameHolder.isOffline) {
            currentWord = GameHolder.game.getNextWord();
        } else {
            currentWord = GameHolder.onlineGame.getNextWord();
        }
        currentWordView.setText(currentWord.word);

        View.OnClickListener onClickListenerOkButton = v -> {
            if (GameHolder.isOffline) {
                GameHolder.game.setWordAsGuessed(time - beginningTimeOfCurrentWord + 1);
            } else {
                GameHolder.onlineGame.setWordAsGuessed(time - beginningTimeOfCurrentWord + 1);
            }

            int numberOfUnguessedWords;
            if (GameHolder.isOffline) {
                numberOfUnguessedWords = GameHolder.game.getNumberOfUnguessedWords();
            } else {
                numberOfUnguessedWords = GameHolder.onlineGame.getNumberOfUnguessedWords();
            }

            if (time >= 20 || numberOfUnguessedWords == 0) {
                sentPhaseData();
                GameActivity.this.onBackPressed();
            } else {
                if (GameHolder.isOffline) {
                    currentWord = GameHolder.game.getNextWord();
                } else {
                    currentWord = GameHolder.onlineGame.getNextWord();
                }
                currentWordView.setText(currentWord.word);
                currentWordView.setText(currentWord.word);
                beginningTimeOfCurrentWord = time;
            }
        };
        okButton.setOnClickListener(onClickListenerOkButton);

        View.OnLongClickListener onLongClickListenerButton = v -> {
            if (GameHolder.isOffline) {
                GameHolder.game.setWordAsFailed(time - beginningTimeOfCurrentWord + 1);
            } else {
                GameHolder.onlineGame.setWordAsFailed(time - beginningTimeOfCurrentWord + 1);
            }
            sentPhaseData();
            GameActivity.this.onBackPressed();
            return true;
        };
        errorButton.setOnLongClickListener(onLongClickListenerButton);

        View.OnLongClickListener onLongClickListenerSkipButton = v -> {
            if (GameHolder.isOffline) {
                GameHolder.game.setWordAsSkipped(time - beginningTimeOfCurrentWord + 1);
            } else {
                GameHolder.onlineGame.setWordAsSkipped(time - beginningTimeOfCurrentWord + 1);
            }
            sentPhaseData();
            GameActivity.this.onBackPressed();
            return true;
        };
        skipButton.setOnLongClickListener(onLongClickListenerSkipButton);

        View.OnClickListener onClickListenerErrorAndSkipButton = v -> {
            Toast toast = Toast.makeText(GameActivity.this, getString(R.string.press_longer), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        };
        errorButton.setOnClickListener(onClickListenerErrorAndSkipButton);
        skipButton.setOnClickListener(onClickListenerErrorAndSkipButton);
    }

    private void sentPhaseData() {
        if (GameHolder.isOffline) {
            return;
        }
        NetworkManager.doPhase(GameHolder.gameId, GameHolder.onlineGame.getPhaseWords());
    }

    private class First20SecondsTimerTask extends TimerTask {
        @Override
        public void run() {
            time++;
            runOnUiThread(() -> {
                remainingTimeView.setText(getBeautifulTime(20 - time));
                if (time < 20) {
                    timer.schedule(new First20SecondsTimerTask(), SECOND);
                } else {
                    timer.schedule(new Next3SecondsTimerTask(), SECOND);
                }
            });
        }
    }

    private class Next3SecondsTimerTask extends TimerTask {
        @Override
        public void run() {
            time++;
            runOnUiThread(() -> {
                remainingTimeView.setTextColor(Color.DKGRAY);
                remainingTimeView.setText(getBeautifulTime(time - 20));
                if (time < 23) {
                    timer.schedule(new Next3SecondsTimerTask(), SECOND);
                } else {
                    timer.schedule(new LastTimerTask(), SECOND);
                }
            });
        }
    }

    private class LastTimerTask extends TimerTask {
        @Override
        public void run() {
            time++;
            runOnUiThread(() -> remainingTimeView.setTextColor(Color.GRAY));
            remainingTimeView.setText(getBeautifulTime(time - 20));
            timer.schedule(new LastTimerTask(), SECOND);
        }
    }

    private String getBeautifulTime(int time) {
        return String.format("%02d:%02d", time / 60, time % 60);
    }


}
