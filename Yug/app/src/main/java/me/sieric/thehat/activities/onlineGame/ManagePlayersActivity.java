package me.sieric.thehat.activities.onlineGame;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import me.sieric.thehat.R;
import me.sieric.thehat.logic.GameHolder;
import me.sieric.thehat.logic.NetworkManager;
import me.sieric.thehat.logic.games.OnlineGame;
import me.sieric.thehat.logic.games.OnlineGameStatus;
import me.sieric.thehat.logic.data.Player;
import me.sieric.thehat.logic.data.Word;

public class ManagePlayersActivity extends AppCompatActivity {

    private TextView wordsNumberView;
    private ListView playersListView;

    private Timer timer = new Timer();

    private OnlineGame onlineGame;
    private ArrayList<String> playersNames;

    private ArrayAdapter<String> adapter;

    private final int TIME_STEP = 2000;

    private TimerTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_game_manage_players);

        onlineGame = new OnlineGame();

        wordsNumberView = findViewById(R.id.wordsNumberView);

        playersListView = findViewById(R.id.playersListView);
        playersNames = new ArrayList<>();
        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_expandable_list_item_1, playersNames);
        playersListView.setAdapter(adapter);

        task = new UpdateStatusTask();
        timer.scheduleAtFixedRate(task, 0, TIME_STEP);

        Button addWordsButton = findViewById(R.id.addWordsButton);
        addWordsButton.setOnClickListener(v -> {
            task.cancel();
            Intent intent = new Intent(ManagePlayersActivity.this, DictionaryListActivity.class);
            startActivity(intent);
        });

        Button startButton = findViewById(R.id.startGameButton);
        startButton.setOnClickListener(v -> {
            Toast toast = Toast.makeText(ManagePlayersActivity.this, getString(R.string.press_longer), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show(); });
        startButton.setOnLongClickListener(v -> {
            if (!GameHolder.isCreator) {
                Toast toast = Toast.makeText(ManagePlayersActivity.this, getString(R.string.wrong_start_game_message), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            } else {
                if (playersNames.size() < 2) {
                    Toast toast = Toast.makeText(ManagePlayersActivity.this, "Too few players", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return true;
                }
                task.cancel();
                ArrayList<Integer> playersPerm = new ArrayList<>();
                for (int i = 0; i < playersNames.size(); i++) {
                    playersPerm.add(i);
                }
                NetworkManager.startGame(GameHolder.gameId, playersPerm, true);
                ArrayList<Player> players = new ArrayList<>();
                for (int i = 0; i < playersNames.size(); i++) {
                    players.add(new Player(playersNames.get(i)));
                }
                NetworkManager.allWords(GameHolder.gameId, words -> {
                    runOnUiThread(() -> {
                        ArrayList<Word> words_ = new ArrayList<>();
                        for (int i = 0; i < words.size(); i++) {
                            words_.add(new Word(-1, words.get(i)));
                            System.out.println("!!!!!!!!!  " + words.get(i));
                        }
                        onlineGame.setWords(words_);
                        onlineGame.setPlayers(players);
                        GameHolder.game = onlineGame;
                        Toast toast = Toast.makeText(ManagePlayersActivity.this, getString(R.string.game_started_message), Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        Intent intent = new Intent(ManagePlayersActivity.this, MenuActivity.class);
                        startActivity(intent);
                    });
                });
            }
            return true;
        });
    }

    private class UpdateStatusTask extends TimerTask {

        @Override
        public void run() {
            NetworkManager.gameStatus(GameHolder.gameId, onlineGameStatus -> {
                runOnUiThread(() -> {
                    if (onlineGameStatus.getGameStatus().equals(OnlineGameStatus.GameStatus.RUNNING)) {
                        task.cancel();
                        ArrayList<Player> players = new ArrayList<>();
                        for (int i = 0; i < playersNames.size(); i++) {
                            players.add(new Player(playersNames.get(i)));
                        }
                        NetworkManager.allPlayers(GameHolder.gameId, playersNames_ -> {
                            runOnUiThread(() -> {
                                playersNames.clear();
                                playersNames.addAll(playersNames_);
                            });
                        });
                        NetworkManager.allWords(GameHolder.gameId, words -> {
                            runOnUiThread(() -> {
                                ArrayList<Word> words_ = new ArrayList<>();
                                for (int i = 0; i < words.size(); i++) {
                                    words_.add(new Word(-1, words.get(i)));
                                }
                                onlineGame.setWords(words_);
                                onlineGame.setPlayers(players);
                                Intent intent = new Intent(ManagePlayersActivity.this, MenuActivity.class);
                                startActivity(intent);
                            });
                        });
                    }
                    wordsNumberView.setText(onlineGameStatus.getWordsNumber());
                });
            });
            NetworkManager.allPlayers(GameHolder.gameId, playersNames_ -> {
                runOnUiThread(() -> {
                    playersNames.clear();
                    playersNames.addAll(playersNames_);
                    adapter.notifyDataSetChanged();
                });
            });
        }
    }
}