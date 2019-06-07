package me.sieric.thehat.activities.offlineGame;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

import me.sieric.thehat.logic.DBManager;
import me.sieric.thehat.logic.game.OfflineGame;
import me.sieric.thehat.logic.GameHolder;
import me.sieric.thehat.R;
import me.sieric.thehat.logic.data.Word;

/**
 * Activity to manage players
 * Provides changing players number and renaming players
 */
public class ManagePlayersActivity extends AppCompatActivity {

    private TextView playersRealNumberView;
    private Switch squareSwitch;

    private int playersNumber = 6;
    private ArrayList<String> playersNames;
    private ArrayAdapter<String> adapter;

    private boolean isSquare = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_game_manage_players);

        playersRealNumberView = findViewById(R.id.realPlayersNumberView);
        Button nextButton = findViewById(R.id.nextButton);
        squareSwitch = findViewById(R.id.squareSwitch);
        ListView playersList = findViewById(R.id.playersList);
        Button lessButton = findViewById(R.id.lessButton);
        Button moreButton = findViewById(R.id.moreButton);

        playersRealNumberView.setText(String.valueOf(playersNumber));
        squareSwitch.setChecked(false);

        playersNames = new ArrayList<>();
        playersNames.add(GameHolder.name);
        for (int i = 1; i < playersNumber; i++) {
            playersNames.add(getString(R.string.player) + (i + 1));
        }

        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_expandable_list_item_1, playersNames);

        playersList.setOnItemClickListener(new PlayersRenamingOnClickListener());
        playersList.setAdapter(adapter);

        squareSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> isSquare = isChecked);

        /* Decreases players number */
        lessButton.setOnClickListener(v -> {
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
        });

        /* Increases players number */
        moreButton.setOnClickListener(v -> {
            if (playersNumber < 12) {
                playersNumber++;
                playersNames.add(getString(R.string.player) + playersNumber);
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
        });

        nextButton.setOnClickListener(v -> {
            Toast toast = Toast.makeText(ManagePlayersActivity.this, getString(R.string.press_longer), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        });
        nextButton.setOnLongClickListener(new GoNextOnLongClickListener());
    }

    /**
     * Opens dialog to rename player
     */
    private class PlayersRenamingOnClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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
        }
    }

    /**
     * Starts game
     * (Creates new game with random words from chosen dictionary)
     */
    private class GoNextOnLongClickListener implements View.OnLongClickListener {

        @Override
        public boolean onLongClick(View v) {
            DBManager dbManager = new DBManager(ManagePlayersActivity.this);
            int wordsPerPlayer = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(ManagePlayersActivity.this).getString("words_per_player", "10"));
            GameHolder.name = PreferenceManager.getDefaultSharedPreferences(ManagePlayersActivity.this).getString("name", "kek");
            ArrayList<Word> words = dbManager.getWordsList(GameHolder.dictId);
            Collections.shuffle(words);
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
        }
    }
}
