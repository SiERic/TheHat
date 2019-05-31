package me.sieric.thehat.activities.game;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import me.sieric.thehat.logic.games.Game;
import me.sieric.thehat.logic.GameHolder;
import me.sieric.thehat.R;
import me.sieric.thehat.logic.data.Word;

public class GameActivity extends AppCompatActivity {

    private TextView remainingTimeView;
    private TextView currentWordView;

    private Timer timer;
    private int time;
    private int beginningTimeOfCurrentWord;
    private Word currentWord;
    private Game game;

    private final int SECOND = (int)TimeUnit.SECONDS.toMillis(1);

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

        game = GameHolder.game;
        time = 0;
        beginningTimeOfCurrentWord = 0;
        remainingTimeView.setText(getBeautifulTime(time));

        timer = new Timer();
        timer.schedule(new First20SecondsTimerTask(), SECOND);

        currentWord = game.getNextWord();
        currentWordView.setText(currentWord.getWord());

        okButton.setOnClickListener(v -> {
            game.setWordAsGuessed(time - beginningTimeOfCurrentWord + 1);

            if (time >= 20 || game.getNumberOfUnfinishedWords() == 0) {
                game.doPhase();
                GameActivity.this.onBackPressed();
            } else {
                currentWord = game.getNextWord();
                currentWordView.setText(currentWord.getWord());
                beginningTimeOfCurrentWord = time;
            }
        });

        errorButton.setOnLongClickListener(v -> {
            game.setWordAsFailed(time - beginningTimeOfCurrentWord + 1);
            game.doPhase();
            GameActivity.this.onBackPressed();
            return true;
        });

        skipButton.setOnLongClickListener(v -> {
            game.setWordAsSkipped(time - beginningTimeOfCurrentWord + 1);
            game.doPhase();
            GameActivity.this.onBackPressed();
            return true;
        });

        View.OnClickListener onClickListenerErrorAndSkipButton = v -> {
            Toast toast = Toast.makeText(GameActivity.this, getString(R.string.press_longer), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        };
        errorButton.setOnClickListener(onClickListenerErrorAndSkipButton);
        skipButton.setOnClickListener(onClickListenerErrorAndSkipButton);
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
        return String.format(getString(R.string.time_format), time / 60, time % 60);
    }

}
