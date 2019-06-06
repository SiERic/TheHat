package me.sieric.thehat.activities.game;

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
import me.sieric.thehat.activities.MainActivity;
import me.sieric.thehat.logic.games.Game;
import me.sieric.thehat.logic.GameHolder;
import me.sieric.thehat.logic.data.Player;
import me.sieric.thehat.logic.data.Team;
import me.sieric.thehat.logic.data.Word;

public class StatisticsActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_statistics);

        Button exitButton = findViewById(R.id.exitButton);

        View.OnClickListener onClickListenerExitButton = v -> {
            Toast toast = Toast.makeText(StatisticsActivity.this, getString(R.string.press_longer), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        };

        View.OnLongClickListener onLongClickListenerExitButton = v -> {
            Intent intent = new Intent(StatisticsActivity.this, MainActivity.class);
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

        Game game = GameHolder.game;

        if (game.isSquare()) {
            ArrayList<Player> players = game.getPlayersStats();
            players.add(0, new Player(""));
            PlayerResultsAdapter adapter = new PlayerResultsAdapter(this, players);
            playersListView.setAdapter(adapter);
        } else {
            ArrayList<Team> teams = game.getTeamsStats();
            teams.add(0, new Team("", "", 0, 0));
            TeamResultsAdapter adapter = new TeamResultsAdapter(this, teams);
            playersListView.setAdapter(adapter);
        }
        ArrayList<Word> words = game.getWordsStats();
        words.add(0, new Word(0, "!"));
        WordStatsAdapter wordsStatsAdapter = new WordStatsAdapter(this, words);
        wordsListView.setAdapter(wordsStatsAdapter);
    }

    private class PlayerResultsAdapter extends ArrayAdapter<Player> {
        PlayerResultsAdapter(Context context, ArrayList<Player> players) {
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
                assert player != null;
                nameView.setText(player.getName());
                scoreView.setText(String.valueOf(player.getExplained() + player.getGuessed()));
                explainedView.setText(String.valueOf(player.getExplained()));
                guessedView.setText(String.valueOf(player.getGuessed()));
            }

            return convertView;
        }
    }

    private class TeamResultsAdapter extends ArrayAdapter<Team> {
        TeamResultsAdapter(Context context, ArrayList<Team> teams) {
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
                assert team != null;
                nameView.setText(String.format(getString(R.string.team_names_format), team.getFirstPlayerName(), team.getSecondPlayerName()));
                scoreView.setText(String.valueOf(team.getFirstPlayerExplained() + team.getSecondPlayerExplained()));
                explainedView.setText(String.format(getString(R.string.team_results_format), team.getFirstPlayerExplained(), team.getSecondPlayerExplained()));
            }

            return convertView;
        }
    }

    private class WordStatsAdapter extends ArrayAdapter<Word> {
        WordStatsAdapter(Context context, ArrayList<Word> words) {
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
                assert word != null;
                wordView.setText(word.getWord());
                timeView.setText(String.valueOf(word.getTime()));
                isGuessedView.setText(word.getStatus().toStatsString());
            }
            return convertView;
        }
    }

    @Override
    public void onBackPressed() {
        // nothing
    }
}
