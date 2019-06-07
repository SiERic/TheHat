package me.sieric.thehat.activities.game;

import android.content.Context;
import android.graphics.Color;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import me.sieric.thehat.logic.game.Game;
import me.sieric.thehat.logic.GameHolder;
import me.sieric.thehat.R;
import me.sieric.thehat.logic.data.Word;

/**
 * Activity, that shows the words to player
 */
public class GameActivity extends AppCompatActivity {

    private TextView remainingTimeView;
    private TextView currentWordView;

    private Timer timer;
    private int time;
    private int beginningTimeOfCurrentWord;
    private Word currentWord;
    private Game game;
    private int explanationTime;
    private TimerTask currentTask;

    private final int SECOND = (int)TimeUnit.SECONDS.toMillis(1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        remainingTimeView = findViewById(R.id.remainingTime);
        currentWordView = findViewById(R.id.currentWord);

        Button okButton = findViewById(R.id.okButton);
        Button errorButton = findViewById(R.id.errorButton);
        Button skipButton = findViewById(R.id.skipButton);

        game = GameHolder.game;
        time = 0;
        beginningTimeOfCurrentWord = 0;

        explanationTime = Integer.parseInt(Objects.requireNonNull(
                PreferenceManager.getDefaultSharedPreferences(this).getString("explanation_time", "20")));
        remainingTimeView.setText(getBeautifulTime(explanationTime));

        timer = new Timer();
        currentTask = new First20SecondsTimerTask();
        timer.schedule(currentTask, SECOND);

        currentWord = game.getNextWord();
        currentWordView.setText(currentWord.getWord());

        okButton.setOnClickListener(v -> {
            game.setWordAsGuessed(time - beginningTimeOfCurrentWord + 1);

            if (time >= explanationTime || game.getNumberOfUnfinishedWords() == 0) {
                currentTask.cancel();
                game.doPhase();
                finish();
            } else {
                if (GameHolder.gameType == GameHolder.GameType.ONE_TO_OTHERS) {
                    time = 0;
                    remainingTimeView.setText(getBeautifulTime(explanationTime));
                }
                currentWord = game.getNextWord();
                currentWordView.setText(currentWord.getWord());
                beginningTimeOfCurrentWord = time;
            }
        });

        errorButton.setOnLongClickListener(v -> {
            currentTask.cancel();
            game.setWordAsFailed(time - beginningTimeOfCurrentWord + 1);
            game.doPhase();
            finish();
            return true;
        });

        skipButton.setOnLongClickListener(v -> {
            currentTask.cancel();
            game.setWordAsSkipped(time - beginningTimeOfCurrentWord + 1);
            game.doPhase();
            finish();
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
                remainingTimeView.setText(getBeautifulTime(explanationTime - time));
                if (time < explanationTime) {
                    currentTask = new First20SecondsTimerTask();
                    timer.schedule(currentTask, SECOND);
                } else {
                    Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    v.vibrate(SECOND / 3);
                    currentTask = new Next3SecondsTimerTask();
                    timer.schedule(currentTask, SECOND);
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
                remainingTimeView.setText(getBeautifulTime(time - explanationTime));
                if (time < explanationTime + 3) {
                    currentTask = new Next3SecondsTimerTask();
                    timer.schedule(currentTask, SECOND);
                } else {
                    Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    v.vibrate(SECOND);
                    currentTask = new LastTimerTask();
                    timer.scheduleAtFixedRate(currentTask, SECOND, SECOND);
                }
            });
        }
    }

    private class LastTimerTask extends TimerTask {
        @Override
        public void run() {
            time++;
            runOnUiThread(() -> remainingTimeView.setTextColor(Color.GRAY));
            remainingTimeView.setText(getBeautifulTime(time - explanationTime));
        }
    }

    private String getBeautifulTime(int time) {
        return String.format(getString(R.string.time_format), time / 60, time % 60);
    }

    @Override
    public void onBackPressed() {
        // nothing
    }

}
