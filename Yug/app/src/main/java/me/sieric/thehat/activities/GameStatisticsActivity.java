package me.sieric.thehat.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
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
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import me.sieric.thehat.R;
import me.sieric.thehat.logic.GameHolder;
import me.sieric.thehat.logic.NetworkManager;
import me.sieric.thehat.logic.Player;
import me.sieric.thehat.logic.Team;
import me.sieric.thehat.logic.Word;

public class GameStatisticsActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_game_statistics);

        Button exitButton = findViewById(R.id.exitButton);

        View.OnClickListener onClickListenerExitButton = v -> {
            Toast toast = Toast.makeText(GameStatisticsActivity.this, getString(R.string.press_longer), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        };

        View.OnLongClickListener onLongClickListenerExitButton = v -> {
            Intent intent = new Intent(GameStatisticsActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return true;
        };
        exitButton.setOnClickListener(onClickListenerExitButton);
        exitButton.setOnLongClickListener(onLongClickListenerExitButton);

        TabHost tabHost = findViewById(R.id.tabHost);

        tabHost.setup();

        TabHost.TabSpec tabSpec = tabHost.newTabSpec("players");
        tabSpec.setContent(R.id.playersTab);
        tabSpec.setIndicator(getString(R.string.players_in_statistics));
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("words");
        tabSpec.setContent(R.id.wordsTab);
        tabSpec.setIndicator(getString(R.string.words_in_statistics));
        tabHost.addTab(tabSpec);

        tabHost.setCurrentTab(0);

        ListView playersListView = findViewById(R.id.playersView);
        ListView wordsListView = findViewById(R.id.wordsView);

        if (GameHolder.isOffline) {
            if (GameHolder.game.isSquare()) {
                ArrayList<Player> players = GameHolder.game.getPlayersInOrder();
                players.add(0, new Player(""));
                PlayerResultsAdapter adapter = new PlayerResultsAdapter(this, players);
                playersListView.setAdapter(adapter);
            } else {
                ArrayList<Team> teams = GameHolder.game.getTeamsInOrder();
                teams.add(0, new Team("", "", 0, 0));
                TeamResultsAdapter adapter = new TeamResultsAdapter(this, teams);
                playersListView.setAdapter(adapter);
            }
            ArrayList<Word> words = GameHolder.game.getWordsStats();
            words.add(0, new Word(0, "!"));
            WordStatsAdapter wordsStatsAdapter = new WordStatsAdapter(this, words);
            wordsListView.setAdapter(wordsStatsAdapter);
        } else {
            NetworkManager.playersStats(GameHolder.gameId, players -> {
                runOnUiThread(() -> {
                    GameHolder.onlineGame.setPlayers(players);
                    if (GameHolder.onlineGame.isSquare()) {
                        ArrayList<Player> players1 = GameHolder.onlineGame.getPlayersInOrder();
                        players1.add(0, new Player(""));
                        PlayerResultsAdapter adapter = new PlayerResultsAdapter(this, players1);
                        playersListView.setAdapter(adapter);
                    } else {
                        ArrayList<Team> teams = GameHolder.onlineGame.getTeamsInOrder();
                        teams.add(0, new Team("", "", 0, 0));
                        TeamResultsAdapter adapter = new TeamResultsAdapter(this, teams);
                        playersListView.setAdapter(adapter);
                    }
                });
            });
            NetworkManager.wordsStats(GameHolder.gameId, words -> {
                runOnUiThread(() -> {
                    for (int i = 0; i < words.size(); i++) {
                        GameHolder.onlineGame.getWords().get(i).time = words.get(i).time;
                        GameHolder.onlineGame.getWords().get(i).status = words.get(i).status;
                    }
                    ArrayList<Word> words1 = GameHolder.onlineGame.getWordsStats();
                    words1.add(0, new Word(0, "!"));
                    WordStatsAdapter wordsStatsAdapter = new WordStatsAdapter(this, words1);
                    wordsListView.setAdapter(wordsStatsAdapter);
                });
            });
        }
    }

    private class PlayerResultsAdapter extends ArrayAdapter<Player> {
        public PlayerResultsAdapter(Context context, ArrayList<Player> players) {
            super(context, 0, players);
        }

        @NotNull
        @Override
        public View getView(int position, View convertView, @NotNull ViewGroup parent) {
            Player player = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.player_results, parent, false);
            }
            TextView placeView = convertView.findViewById(R.id.placeView);
            TextView nameView = convertView.findViewById(R.id.nameView);
            TextView scoreView = convertView.findViewById(R.id.scoreView);
            TextView explainedView = convertView.findViewById(R.id.explainedView);
            TextView guessedView = convertView.findViewById(R.id.guessedView);

            if (position == 0) {
                placeView.setText(getString(R.string.place));
                nameView.setText(getString(R.string.player));
                scoreView.setText(getString(R.string.all));
                explainedView.setText("->");
                guessedView.setText("<-");
            } else {
                placeView.setText(String.valueOf(position));
                nameView.setText(player.name);
                scoreView.setText(String.valueOf(player.explained + player.guessed));
                explainedView.setText(String.valueOf(player.explained));
                guessedView.setText(String.valueOf(player.guessed));
            }

            return convertView;
        }
    }

    private class TeamResultsAdapter extends ArrayAdapter<Team> {
        public TeamResultsAdapter(Context context, ArrayList<Team> teams) {
            super(context, 0, teams);
        }

        @NotNull
        @Override
        public View getView(int position, View convertView, @NotNull ViewGroup parent) {
            Team team = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.team_results, parent, false);
            }
            TextView placeView = convertView.findViewById(R.id.placeView);
            TextView nameView = convertView.findViewById(R.id.nameView);
            TextView scoreView = convertView.findViewById(R.id.scoreView);
            TextView explainedView = convertView.findViewById(R.id.explainedView);

            if (position == 0) {
                placeView.setText(getString(R.string.place));
                nameView.setText(getString(R.string.players_in_statistics));
                scoreView.setText(getString(R.string.all));
                explainedView.setText("->");
            } else {
                placeView.setText(String.valueOf(position));
                nameView.setText(team.nameA + '\n' + team.nameB);
                scoreView.setText(String.valueOf(team.explainedA + team.explainedB));
                explainedView.setText(String.valueOf(team.explainedA) + '\n' + String.valueOf(team.explainedB));
            }

            return convertView;
        }
    }

    private class WordStatsAdapter extends ArrayAdapter<Word> {
        public WordStatsAdapter(Context context, ArrayList<Word> words) {
            super(context, 0, words);
        }

        @NotNull
        @Override
        public View getView(int position, View convertView, @NotNull ViewGroup parent) {
            Word word = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.word_stats, parent, false);
            }
            TextView wordView = convertView.findViewById(R.id.wordView);
            TextView timeView = convertView.findViewById(R.id.timeView);
            TextView isGuessedView = convertView.findViewById(R.id.isGuessedView);

            if (position == 0) {
                wordView.setText(getString(R.string.word));
                timeView.setText(getString(R.string.time));
                isGuessedView.setText("?");
            } else {
                wordView.setText(word.word);
                timeView.setText(String.valueOf(word.time));
                isGuessedView.setText(word.status.toMyString());
            }
            return convertView;
        }
    }
}
