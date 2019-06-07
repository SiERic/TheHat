package me.sieric.thehat.activities.game;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import me.sieric.thehat.R;

/**
 * Activity to show countdown before game
 * 3 ... 2 ... 1 ... Play!
 */
public class CountdownActivity extends AppCompatActivity {

    private Timer timer;
    private TextView countDownView;

    private final int SECOND = (int) TimeUnit.SECONDS.toMillis(1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_countdown);

        countDownView = findViewById(R.id.countDownView);
        countDownView.setText(R.string.bid_three);

        timer = new Timer();
        timer.schedule(new FirstTimerTask(), SECOND);

    }

    private class FirstTimerTask extends TimerTask {
        @Override
        public void run() {
            runOnUiThread(() -> {
                countDownView.setText(R.string.bid_two);
                timer.schedule(new SecondTimerTask(), SECOND);
            });
        }
    }

    private class SecondTimerTask extends TimerTask {
        @Override
        public void run() {
            runOnUiThread(() -> {
                countDownView.setText(getString(R.string.big_one));
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(SECOND / 2);
                timer.schedule(new ThirdTimerTask(), SECOND / 2);
            });
        }
    }

    private class ThirdTimerTask extends TimerTask {
        @Override
        public void run() {
            runOnUiThread(() -> {
                Intent intent;
                intent = new Intent(CountdownActivity.this, GameActivity.class);
                startActivity(intent);
            });
        }
    }
}
