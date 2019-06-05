package me.sieric.thehat.activities.onlineGame;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

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

    private PlayersNamesAdapter adapter;
    private final int TIME_STEP = 2000;

    private TimerTask task;
    private Switch squareSwitch;
    private ArrayList<Integer> playersPerm = new ArrayList<>();
    private ArrayList<Integer> playersColorIds = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_game_manage_players);

        onlineGame = new OnlineGame();

        wordsNumberView = findViewById(R.id.wordsNumberView);

        playersListView = findViewById(R.id.playersListView);
        adapter = new PlayersNamesAdapter(ManagePlayersActivity.this, onlineGame.getPlayers());
        playersListView.setAdapter(adapter);

        task = new UpdateStatusTask();
        timer.scheduleAtFixedRate(task, 0, TIME_STEP);
        squareSwitch = findViewById(R.id.squareSwitch);

        NetworkManager.addPlayer(GameHolder.gameId, "Lol", (id) -> {});
        NetworkManager.addPlayer(GameHolder.gameId, "Kek", (id) -> {});
        NetworkManager.addPlayer(GameHolder.gameId, "Aidarbek", (id) -> {});

        if (!GameHolder.isCreator) {
            squareSwitch.setVisibility(View.INVISIBLE);
            squareSwitch.setEnabled(false);
        }

        playersListView.setOnItemClickListener((parent, view, position, id) -> {
            System.out.println("kek");
            Toast toast = Toast.makeText(ManagePlayersActivity.this, "Kek", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            if (playersPerm.contains(position)) {
                playersPerm.clear();
                adapter.notifyDataSetChanged();
            } else {
                view.setBackgroundColor(getItemColor(playersPerm.size(), onlineGame.getPlayers().size()));
                playersPerm.add(position);
            }
        });

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
                if (onlineGame.getPlayers().size() < 2) {
                    Toast toast = Toast.makeText(ManagePlayersActivity.this, "Too few players", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return true;
                }
                task.cancel();
                updateWords();
//                NetworkManager.startGame(GameHolder.gameId, playersPerm, true);

                GameHolder.game = onlineGame;
                Toast toast = Toast.makeText(ManagePlayersActivity.this, getString(R.string.game_started_message), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                Intent intent = new Intent(ManagePlayersActivity.this, MenuActivity.class);
                startActivity(intent);
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
                        NetworkManager.allPlayers(GameHolder.gameId, playersNames -> {
                            runOnUiThread(() -> {

                            });
                        });
                        updateWords();
                    }
                    if (onlineGameStatus.getPlayersNumber() > onlineGame.getPlayers().size()) {
                        updatePlayers();
                    }
                    wordsNumberView.setText(String.format(getString(R.string.words_number_format), onlineGameStatus.getWordsNumber()));
                    onlineGame.setStatus(onlineGameStatus);
                });
            });
        }
    }

    private void updateWords() {
        NetworkManager.allWords(GameHolder.gameId, words -> {
            runOnUiThread(() -> {
                ArrayList<Word> words_ = new ArrayList<>();
                for (int i = 0; i < words.size(); i++) {
                    words_.add(new Word(-1, words.get(i)));
                }
                onlineGame.setWords(words_);
                Intent intent = new Intent(ManagePlayersActivity.this, MenuActivity.class);
                startActivity(intent);
            });
        });
    }

    private void updatePlayers() {
        NetworkManager.allPlayers(GameHolder.gameId, playersNames -> {
            runOnUiThread(() -> {
                if (playersNames.size() > onlineGame.getPlayers().size()) {
                    onlineGame.getPlayers().clear();
                    playersPerm.clear();
                    playersColorIds = new ArrayList<>(playersNames.size());
                    for (int i = 0; i < playersNames.size(); i++) {
                        onlineGame.getPlayers().add(new Player(playersNames.get(i), i));
                    }
                    adapter.notifyDataSetChanged();
                }
            });
        });
    }

    private class PlayersNamesAdapter extends ArrayAdapter<Player> {
        PlayersNamesAdapter(Context context, ArrayList<Player> players) {
            super(context, 0, players);
        }

        @NotNull
        @Override
        public View getView(int position, View convertView, @NotNull ViewGroup parent) {
            Player player = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.player_name, parent, false);
            }
            convertView.setBackgroundColor(getColor(R.color.backgroundWhite));
            TextView nameView = convertView.findViewById(R.id.nameView);
            assert player != null;
            nameView.setText(player.getName());
            return convertView;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private int getItemColor(int id, int number) {
        if (number == 1) {
            return getColor(R.color.lightItemColor);
        }
        float percent = (float) (1 - (id * 1.0 / (number - 1)));
        Color light = Color.valueOf(getColor(R.color.lightItemColor));
        Color dark = Color.valueOf(getColor(R.color.darkItemColor));
        return Color.valueOf((light.red() * percent + dark.red() * (1 - percent)), (light.green() * percent + dark.green() * (1 - percent)), (light.blue() * percent + dark.blue() * (1 - percent))).toArgb();
    }
}
