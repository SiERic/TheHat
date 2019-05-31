package me.sieric.thehat.activities.offlineGame;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import me.sieric.thehat.logic.DBManager;
import me.sieric.thehat.logic.games.OfflineGame;
import me.sieric.thehat.logic.GameHolder;
import me.sieric.thehat.R;
import me.sieric.thehat.logic.data.Word;

public class ManagePlayersActivity extends AppCompatActivity {

    private TextView playersRealNumberView;
    private Button nextButton;
    private Switch squareSwitch;
    private ListView playersList;
    private Button lessButton;
    private Button moreButton;

    private int playersNumber = 6;
    private int wordsPerPlayer = 10;
    private ArrayList<String> playersNames;

    private boolean isSquare = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_offline_game_manage_players);

        playersRealNumberView = findViewById(R.id.realPlayersNumberView);
        nextButton = findViewById(R.id.nextButton);
        squareSwitch = findViewById(R.id.squareSwitch);
        playersList = findViewById(R.id.playersList);
        lessButton = findViewById(R.id.lessButton);
        moreButton = findViewById(R.id.moreButton);

        playersRealNumberView.setText(String.valueOf(playersNumber));
        squareSwitch.setChecked(false);

        playersNames = new ArrayList<>();
        for (int i = 0; i < playersNumber; i++) {
            playersNames.add(getString(R.string.player) + String.valueOf(i + 1));
        }

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_expandable_list_item_1, playersNames);

        playersList.setOnItemClickListener((adapter1, v, position, arg3) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(ManagePlayersActivity.this);
            builder.setTitle(getString(R.string.rename));
            final View customLayout = getLayoutInflater().inflate(R.layout.changing_dialog, null);
            builder.setView(customLayout);
            builder.setPositiveButton(getString(R.string.ok), (dialog, arg1) -> {
                EditText editText = customLayout.findViewById(R.id.editText);
                playersNames.set(position, editText.getText().toString());
                adapter.notifyDataSetChanged();
            });
            builder.setNegativeButton(getString(R.string.cancel), (dialog, arg1) -> {
                //
            });
            AlertDialog alert = builder.create();
            alert.show();
        });


        View.OnClickListener lessButtonOnClickListener = v -> {
            if (playersNumber > 2) {
                playersNumber--;
                playersNames.remove(playersNumber);
                adapter.notifyDataSetChanged();
                playersRealNumberView.setText(String.valueOf(playersNumber));
                if (playersNumber % 2 == 1) {
                    squareSwitch.setChecked(true);
                    squareSwitch.setClickable(false);
                    isSquare = true;
                } else {
                    squareSwitch.setClickable(true);
                }
            }
        };

        lessButton.setOnClickListener(lessButtonOnClickListener);

        View.OnClickListener moreButtonOnClickListener = v -> {
            if (playersNumber < 99) {
                playersNumber++;
                playersNames.add(getString(R.string.player) + String.valueOf(playersNumber));
                adapter.notifyDataSetChanged();
                playersRealNumberView.setText(String.valueOf(playersNumber));
                if (playersNumber % 2 == 1) {
                    squareSwitch.setChecked(true);
                    squareSwitch.setClickable(false);
                    isSquare = true;
                } else {
                    squareSwitch.setClickable(true);
                }
            }
        };

        moreButton.setOnClickListener(moreButtonOnClickListener);

        playersList.setAdapter(adapter);

        squareSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> isSquare = isChecked);

        View.OnClickListener onClickListenerNextButton = v -> {
            Toast toast = Toast.makeText(ManagePlayersActivity.this, getString(R.string.press_longer), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        };

        View.OnLongClickListener onLongClickListenerNextButton = v -> {
            DBManager dbManager = new DBManager(this);
            ArrayList<Word> words = dbManager.getWordsList(GameHolder.dictId);
            if (words.size() < playersNumber * wordsPerPlayer) {
                Toast toast = Toast.makeText(ManagePlayersActivity.this, "There are too few words in the dictionary", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            } else {
                GameHolder.game = new OfflineGame(playersNumber, playersNames, new ArrayList<>(words.subList(0, playersNumber * wordsPerPlayer)), isSquare);
                Intent intent = new Intent(ManagePlayersActivity.this, MenuActivity.class);
                startActivity(intent);
            }
            return true;
        };

        nextButton.setOnClickListener(onClickListenerNextButton);
        nextButton.setOnLongClickListener(onLongClickListenerNextButton);
    }

}
