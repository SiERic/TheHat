package me.sieric.thehat.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import me.sieric.thehat.R;
import me.sieric.thehat.logic.GameHolder;
import me.sieric.thehat.logic.NetworkManager;
import me.sieric.thehat.logic.OnlineGameStatus;

public class OnlineGameChooseActivity extends AppCompatActivity {
    private Button createButton;
    private Button joinButton;
    private EditText gameIdText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_game_choose);

        createButton = findViewById(R.id.createButton);
        joinButton = findViewById(R.id.joinButton);
        gameIdText = findViewById(R.id.gameIdText);

        createButton.setOnClickListener(v -> {
            String gameId = gameIdText.getText().toString();
            NetworkManager.createGame(gameId, ok ->
                    runOnUiThread(() -> {
                        if (!ok) {
                            Toast toast = Toast.makeText(OnlineGameChooseActivity.this, getString(R.string.game_already_created_message), Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        } else {
                            Toast toast = Toast.makeText(OnlineGameChooseActivity.this, getString(R.string.game_created_message), Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            GameHolder.gameId = gameId;
                            NetworkManager.addPlayer(gameId, GameHolder.name, id -> {
                                runOnUiThread(() -> {
                                    GameHolder.playerId = id;
                                    System.out.println(id);
                                    GameHolder.isCreator = true;
                                    Intent intent = new Intent(OnlineGameChooseActivity.this, OnlineGameManagePlayersActivity.class);
                                    startActivity(intent);
                                });
                            });
                        }
                    }));
        });

        joinButton.setOnClickListener(v -> {
            String gameId = gameIdText.getText().toString();
            NetworkManager.gameStatus(gameId, onlineGameStatus -> runOnUiThread(() -> {
                OnlineGameStatus.GameStatus status = onlineGameStatus.getGameStatus();
                if (status.equals(OnlineGameStatus.GameStatus.CREATED)) {
                    Toast toast = Toast.makeText(OnlineGameChooseActivity.this, "Successfully joined the offlineGame", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    GameHolder.gameId = gameId;
                    NetworkManager.addPlayer(gameId, GameHolder.name, id -> {
                        runOnUiThread(() -> {
                            GameHolder.playerId = id;
                            System.out.println(id);
                            GameHolder.isCreator = false;
                            Intent intent = new Intent(OnlineGameChooseActivity.this, OnlineGameManagePlayersActivity.class);
                            startActivity(intent);
                        });
                    });

                } else if (status.equals(OnlineGameStatus.GameStatus.RUNNING)) {
                    Toast toast = Toast.makeText(OnlineGameChooseActivity.this, "OfflineGame already started", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } else {
                    Toast toast = Toast.makeText(OnlineGameChooseActivity.this, "OfflineGame doesn't exists", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }));
        });
    }
}
