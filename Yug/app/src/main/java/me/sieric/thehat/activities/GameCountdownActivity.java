package me.sieric.thehat.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import me.sieric.thehat.R;

public class GameCountdownActivity extends AppCompatActivity {

    private Timer timer;
    private TextView count;

    private boolean fl = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_game_start);

        count = findViewById(R.id.Count);
        count.setText(R.string.bid_three);

        timer = new Timer();
        timer.schedule(new FirstTimerTask(), 1000);

    }

    private class FirstTimerTask extends TimerTask {
        @Override
        public void run() {
            runOnUiThread(() -> {
                count.setText(R.string.bid_two);
                timer.schedule(new SecondTimerTask(), 1000);
            });
        }
    }

    private class SecondTimerTask extends TimerTask {
        @Override
        public void run() {
            runOnUiThread(() -> {
                count.setText(getString(R.string.big_one));
                timer.schedule(new ThirdTimerTask(), 500);
            });
        }
    }

    private class ThirdTimerTask extends TimerTask {
        @Override
        public void run() {
            fl = true;
            runOnUiThread(() -> {
                Intent intent = new Intent(GameCountdownActivity.this, GameActivity.class);
                startActivity(intent);
            });
        }
    }

    protected void onResume() {
        super.onResume();
        if (fl) {
            GameCountdownActivity.this.finish();
        }
    }
}
