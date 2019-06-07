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
import me.sieric.thehat.logic.game.OnlineGame;
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

        squareSwitch = findViewById(R.id.squareSwitch);
        Button addWordsButton = findViewById(R.id.addWordsButton);
        Button startButton = findViewById(R.id.startGameButton);

        if (!GameHolder.isCreator) {
            squareSwitch.setVisibility(View.INVISIBLE);
            squareSwitch.setClickable(false);
            startButton.setVisibility(View.INVISIBLE);
            startButton.setClickable(false);
        }

        playersListView.setOnItemClickListener((parent, view, position, id) -> {
            if (!GameHolder.isCreator) {
                return;
            }
            System.out.println("kek");
            if (playersPerm.contains(position)) {
                playersPerm.clear();
                for (int i = 0; i < playersColorIds.size(); i++) {
                    playersColorIds.set(i, -1);
                }
            } else {
                playersColorIds.set(position, playersPerm.size() / (squareSwitch.isChecked() ? 1 : 2));
                playersPerm.add(position);
            }
            adapter.notifyDataSetChanged();
        });

        squareSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            for (int i = 0; i < playersColorIds.size(); i++) {
                playersColorIds.set(i, -1);
            }
            playersPerm.clear();
            adapter.notifyDataSetChanged();
        });

        addWordsButton.setOnClickListener(v -> {
            Intent intent = new Intent(ManagePlayersActivity.this, DictionaryListActivity.class);
            startActivity(intent);
        });

        startButton.setOnClickListener(v -> {
            Toast toast = Toast.makeText(ManagePlayersActivity.this, getString(R.string.press_longer), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        });
        startButton.setOnLongClickListener(v -> {
            if (onlineGame.getPlayers().size() < 2) {
                Toast toast = Toast.makeText(ManagePlayersActivity.this, "Too few players", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return true;
            }
            if (playersPerm.size() != playersColorIds.size()) {
                Toast toast = Toast.makeText(ManagePlayersActivity.this, "Set players' order", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return true;
            }
            task.cancel();
            updateWords();
            if (!squareSwitch.isChecked()) {
                ArrayList<Integer> newPlayersPerm = new ArrayList<>();
                for (int i = 0; i < playersColorIds.size() / 2; i++) {
                    newPlayersPerm.add(playersPerm.get(i * 2));
                }
                for (int i = 0; i < playersColorIds.size() / 2; i++) {
                    newPlayersPerm.add(playersPerm.get(i * 2 + 1));
                }
                playersPerm = newPlayersPerm;
            }
            NetworkManager.startGame(GameHolder.gameId, playersPerm, squareSwitch.isChecked());
            GameHolder.game = onlineGame;
            Toast toast = Toast.makeText(ManagePlayersActivity.this, getString(R.string.game_started_message), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            Intent intent = new Intent(ManagePlayersActivity.this, MenuActivity.class);
            startActivity(intent);
            return true;
        });

        task = new UpdateStatusTask();
        timer.scheduleAtFixedRate(task, 0, TIME_STEP);
    }

    private class UpdateStatusTask extends TimerTask {

        @Override
        public void run() {
            NetworkManager.gameStatus(GameHolder.gameId, onlineGameStatus -> {
                onlineGame.setStatus(onlineGameStatus);
                runOnUiThread(() -> {
                    if (onlineGameStatus.getGameStatus().equals(OnlineGame.Status.GameStatus.RUNNING)) {
                        task.cancel();
                        updateWords();
                        updatePlayers();
                        Intent intent = new Intent(ManagePlayersActivity.this, MenuActivity.class);
                        startActivity(intent);
                        return;
                    }
                    if (onlineGameStatus.getPlayersNumber() > onlineGame.getPlayers().size()) {
                        updatePlayers();
                    }
                    wordsNumberView.setText(String.format(getString(R.string.words_number_format), onlineGameStatus.getWordsNumber()));
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
            });
        });
    }

    private void updatePlayers() {
        NetworkManager.allPlayers(GameHolder.gameId, playersNames -> {
            runOnUiThread(() -> {
                if (playersNames.size() > onlineGame.getPlayers().size()) {
                    onlineGame.getPlayers().clear();
                    playersPerm.clear();
                    playersColorIds.clear();
                    for (int i = 0; i < playersNames.size(); i++) {
                        playersColorIds.add(-1);
                        onlineGame.getPlayers().add(new Player(playersNames.get(i)));
                    }
                    adapter.notifyDataSetChanged();
                    if (playersNames.size() % 2 == 1) {
                        squareSwitch.setChecked(true);
                        squareSwitch.setClickable(false);
                    } else {
                        squareSwitch.setClickable(true);
                    }
                }
            });
        });
    }

    private class PlayersNamesAdapter extends ArrayAdapter<Player> {
        PlayersNamesAdapter(Context context, ArrayList<Player> players) {
            super(context, 0, players);
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @NotNull
        @Override
        public View getView(int position, View convertView, @NotNull ViewGroup parent) {
            Player player = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.player_name, parent, false);
            }
            convertView.setBackgroundColor(getItemColor(playersColorIds.get(position), playersColorIds.size() / (squareSwitch.isChecked() ? 1 : 2)));
            TextView nameView = convertView.findViewById(R.id.nameView);
            TextView idView = convertView.findViewById(R.id.idView);
            assert player != null;
            nameView.setText(player.getName());
            if (playersColorIds.get(position) == -1) {
                idView.setText("");
            } else {
                idView.setText(String.valueOf(playersColorIds.get(position) + 1));
            }
            return convertView;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private int getItemColor(int id, int number) {
        if (id == -1) {
            return getColor(R.color.backgroundWhite);
        }
        if (number == 1) {
            return getColor(R.color.lightItemColor);
        }
        float percent = (float) (1 - (id * 1.0 / (number - 1)));

        Color light = Color.valueOf(getColor(R.color.lightItemColor));
        Color dark = Color.valueOf(getColor(R.color.darkItemColor));
        return Color.valueOf((light.red() * percent + dark.red() * (1 - percent)), (light.green() * percent + dark.green() * (1 - percent)), (light.blue() * percent + dark.blue() * (1 - percent))).toArgb();
    }

    @Override
    public void onBackPressed() {
        // nothing
    }
}
