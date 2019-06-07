package me.sieric.thehat.activities.onlineGame;

import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import me.sieric.thehat.R;
import me.sieric.thehat.logic.GameHolder;
import me.sieric.thehat.logic.NetworkManager;
import me.sieric.thehat.logic.game.OnlineGame;

/**
 * Online game creation activity
 * Provides creating or joining game with chosen id
 */
public class CreateJoinActivity extends AppCompatActivity {

    private EditText gameIdText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_game_create_join);

        Button createButton = findViewById(R.id.createButton);
        Button joinButton = findViewById(R.id.joinButton);
        gameIdText = findViewById(R.id.gameIdText);

        GameHolder.gameType = GameHolder.GameType.ONLINE;
        GameHolder.name = PreferenceManager.getDefaultSharedPreferences(this).getString("name", "Sasha");

        createButton.setOnClickListener(new CreateClickListener());
        joinButton.setOnClickListener(new JoinClickListener());
    }

    /**
     * Sends request to create game with entered game id
     * If succeed, starts {@link ManagePlayersActivity}
     */
    private class CreateClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            String gameId = gameIdText.getText().toString();
            NetworkManager.createGame(gameId, ok ->
                    runOnUiThread(() -> {
                        if (!ok) {
                            Toast toast = Toast.makeText(CreateJoinActivity.this, getString(R.string.game_already_created_message), Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        } else {
                            Toast toast = Toast.makeText(CreateJoinActivity.this, getString(R.string.game_created_message), Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            GameHolder.gameId = gameId;
                            NetworkManager.addPlayer(gameId, GameHolder.name, id -> runOnUiThread(() -> {
                                GameHolder.playerId = id;
                                System.out.println(id);
                                GameHolder.isCreator = true;
                                Intent intent = new Intent(CreateJoinActivity.this, ManagePlayersActivity.class);
                                startActivity(intent);
                            }));
                        }
                    }));
        }
    }

    /**
     * Sends request to join game with entered game id
     * If succeed, starts {@link ManagePlayersActivity}
     */
    private class JoinClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            String gameId = gameIdText.getText().toString();
            NetworkManager.gameStatus(gameId, onlineGameStatus -> runOnUiThread(() -> {
                OnlineGame.Status.GameStatus status = onlineGameStatus.getGameStatus();
                if (status.equals(OnlineGame.Status.GameStatus.CREATED)) {
                    Toast toast = Toast.makeText(CreateJoinActivity.this, "Successfully joined the game", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    GameHolder.gameId = gameId;
                    NetworkManager.addPlayer(gameId, GameHolder.name, id -> runOnUiThread(() -> {
                        GameHolder.playerId = id;
                        System.out.println(id);
                        GameHolder.isCreator = false;
                        Intent intent = new Intent(CreateJoinActivity.this, ManagePlayersActivity.class);
                        startActivity(intent);
                    }));

                } else if (status.equals(OnlineGame.Status.GameStatus.RUNNING)) {
                    Toast toast = Toast.makeText(CreateJoinActivity.this, "Game already started", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } else {
                    Toast toast = Toast.makeText(CreateJoinActivity.this, "Game doesn't exists", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }));
        }
    }
}
