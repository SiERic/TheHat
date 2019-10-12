package me.sieric.thehat.logic;

import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import me.sieric.thehat.logic.data.Player;
import me.sieric.thehat.logic.data.Word;
import me.sieric.thehat.logic.game.OnlineGame;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Network manager
 * Helps to communicate with the server
 */
public class NetworkManager {

    private static final String TAG = NetworkManager.class.getName();

    private static final String SERVER_URL = "https://the-hat-simple.herokuapp.com";

    private static JSONObject getJsonData(Response response) throws IOException, JSONException {
        return new JSONObject(response.body().string());
    }

    private static final MediaType MEDIA_JSON = MediaType.get("application/json; charset=utf-8");

    /**
     * Sends a request to create a new game
     * Gets true, if game doesn't exists, false otherwise
     * @param gameId game id
     * @param action action to be done after response received
     */
    public static void createGame(String gameId, Consumer<Boolean> action) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(SERVER_URL + "/create_game/" + gameId)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e(TAG, "request failure", e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    JSONObject data = getJsonData(response);
                    boolean ok = "OK".equals(data.getString("status"));
                    action.accept(ok);
                } catch (JSONException e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }
        });
    }

    /**
     * Send a request to get game status
     * Gets game status ({@link OnlineGame.Status}
     * @param gameId game id
     * @param action action to be done after response received
     */
    public static void gameStatus(String gameId, Consumer<OnlineGame.Status> action) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(SERVER_URL + "/game_status/" + gameId)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e(TAG, "request failure", e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    JSONObject data = getJsonData(response);
                    try {
                        String gameStatus = data.getString("game_status");
                        int wordsNumber = data.getInt("words_number");
                        int playersNumber = data.getInt("players_number");
                        System.out.println(OnlineGame.Status.GameStatus.valueOf(gameStatus));
                        if (OnlineGame.Status.GameStatus.valueOf(gameStatus) != OnlineGame.Status.GameStatus.RUNNING) {
                            action.accept(new OnlineGame.Status(gameStatus, wordsNumber, playersNumber));
                            return;
                        }
                        int firstPlayer = data.getInt("first_player");
                        int secondPlayer = data.getInt("second_player");
                        int finishedWords = data.getInt("finished_words");
                        boolean isSquare = data.getBoolean("is_square");
                        action.accept(new OnlineGame.Status(gameStatus, wordsNumber, playersNumber, firstPlayer,
                                secondPlayer, finishedWords, isSquare));
                    } catch (Exception e) {
                        action.accept(new OnlineGame.Status(OnlineGame.Status.GameStatus.DOES_NOT_EXISTS.toString(), 0, 0));
                    }
                } catch (JSONException e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }
        });
    }

    /**
     * Sends a post request to add new words to the game "hat"
     * @param gameId game id
     * @param words list of words to add
     */
    public static void addWords(String gameId, List<String> words) {
        JSONObject jsonData = new JSONObject(Collections.singletonMap("words", new JSONArray(words)));

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(SERVER_URL + "/add_words/" + gameId)
                .post(RequestBody.create(MEDIA_JSON, jsonData.toString()))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
            }
        });
    }

    /**
     * Sends a request to add new player to the game
     * Gets player id
     * @param gameId gameId
     * @param name player name
     * @param action action to be done after response received
     */
    public static void addPlayer(String gameId, String name, Consumer<Integer> action) {
        JSONObject jsonData = new JSONObject();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(SERVER_URL + "/add_player/" + gameId + "/" + name)
                .post(RequestBody.create(MEDIA_JSON, jsonData.toString()))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e(TAG, "request failure", e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    JSONObject data = getJsonData(response);
                    int playerId = data.getInt("player_id");
                    action.accept(playerId);
                } catch (JSONException e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }
        });
    }

    /**
     * Sends a request to get all players list
     * Gets list of players names
     * @param gameId game id
     * @param action action to be done after response received
     */
    public static void allPlayers(String gameId, Consumer<ArrayList<String> > action) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(SERVER_URL + "/all_players/" + gameId)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e(TAG, "request failure", e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    JSONObject data = getJsonData(response);
                    ArrayList<String> players = new ArrayList<>();
                    JSONArray jsonPlayers = data.getJSONArray("players");
                    for (int i = 0; i < jsonPlayers.length(); i++) {
                        JSONObject player = jsonPlayers.getJSONObject(i);
                        players.add(player.getString("name"));
                    }
                    action.accept(players);
                } catch (JSONException e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }
        });
    }

    /**
     * Sends a request to get all game words
     * Gets list of words (strings)
     * @param gameId game id
     * @param action action to be done after response received
     */
    public static void allWords(String gameId, Consumer<ArrayList<String> > action) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(SERVER_URL + "/all_words/" + gameId)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e(TAG, "request failure", e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    JSONObject data = getJsonData(response);
                    ArrayList<String> words = new ArrayList<>();
                    JSONArray jsonWords = data.getJSONArray("words");
                    for (int i = 0; i < jsonWords.length(); i++) {
                        JSONObject word = jsonWords.getJSONObject(i);
                        words.add(word.getString("word"));
                    }
                    action.accept(words);
                } catch (JSONException e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }
        });
    }

    /**
     * Sends a post request to start a game
     * Sets game status to RUNNING
     * @param gameId game id
     * @param playersPermutation permutation of players (to play in right pair e.g.)
     * @param isSquare "is square" game flag
     */
    public static void startGame(String gameId, ArrayList<Integer> playersPermutation, boolean isSquare) {
        OkHttpClient client = new OkHttpClient();
        JSONObject jsonData = new JSONObject();
        try {
            jsonData.put("players", new JSONArray(playersPermutation));
            jsonData.put("is_square", isSquare);
        } catch (JSONException ignored) {

        }

        Request request = new Request.Builder()
                .url(SERVER_URL + "/start_game/" + gameId)
                .post(RequestBody.create(MEDIA_JSON, jsonData.toString()))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e(TAG, "request failure", e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {

            }
        });
    }

    /**
     * Sends a request to get new finished words
     * Gets list of ids of all finished (after finishedSinceId) words
     * @param gameId game id
     * @param finishedSinceId id of last known finished word
     * @param action action to be done after response received
     */
    public static void finishedWords(String gameId, int finishedSinceId, Consumer<List<Integer>> action) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(SERVER_URL + "/finished_word/" + gameId + "/" + finishedSinceId)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e(TAG, "request failure", e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    JSONObject data = getJsonData(response);
                    ArrayList<Integer> ids = new ArrayList<>();
                    JSONArray jsonIds = data.getJSONArray("finished_ids");
                    for (int i = 0; i < jsonIds.length(); i++) {
                        ids.add(jsonIds.getInt(i));
                    }
                    action.accept(ids);
                } catch (JSONException e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }
        });
    }

    /**
     * Sends a post request to do game phase
     * Sends data about explained (including skipped and failed) words and explanation time
     * @param gameId game id
     * @param words words shown during last explanation
     */
    public static void doPhase(String gameId, List<Word> words) {

        JSONArray wordsArray = new JSONArray();

        for (int i = 0; i < words.size(); i++) {
            JSONObject word = new JSONObject();
            try {
                word.put("id", words.get(i).getWordId());
                word.put("time", words.get(i).getTime());
                word.put("status", words.get(i).getStatus().toString());
                wordsArray.put(word);
            } catch (JSONException ignored) {}
        }

        JSONObject jsonData = new JSONObject(Collections.singletonMap("words", wordsArray));

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(SERVER_URL + "/do_phase/" + gameId)
                .post(RequestBody.create(MEDIA_JSON, jsonData.toString()))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
            }
        });
    }

    /**
     * Sends request to get words statistics
     * Gets words list with full information (time and status)
     * @param gameId game id
     * @param action action to be done after response received
     */
    public static void wordsStats(String gameId, Consumer<ArrayList<Word>> action) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(SERVER_URL + "/words_stats/" + gameId)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e(TAG, "request failure", e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    JSONObject data = getJsonData(response);
                    ArrayList<Word> words = new ArrayList<>();
                    JSONArray jsonStats = data.getJSONArray("stats");
                    for (int i = 0; i < jsonStats.length(); i++) {
                        JSONObject word = jsonStats.getJSONObject(i);
                        words.add(new Word(i, "", word.getInt("time"), Word.Status.valueOf(word.getString("status"))));
                    }
                    action.accept(words);
                } catch (JSONException e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }
        });
    }

    /**
     * Sends a request to get players statistics
     * Gets players list with full information (name, explained and guessed words number)
     * @param gameId game id
     * @param action action to be done after response received
     */
    public static void playersStats(String gameId, Consumer<ArrayList<Player>> action) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(SERVER_URL + "/players_stats/" + gameId)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e(TAG, "request failure", e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    JSONObject data = getJsonData(response);
                    ArrayList<Player> players = new ArrayList<>();
                    JSONArray jsonStats = data.getJSONArray("stats");
                    for (int i = 0; i < jsonStats.length(); i++) {
                        JSONObject player = jsonStats.getJSONObject(i);
                        players.add(new Player(player.getString("name"), player.getInt("guessed"), player.getInt("explained")));
                    }
                    action.accept(players);
                } catch (JSONException e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }
        });
    }

    /**
     * Sends a post request to finish the game
     * Sets game status to FINISHED
     * @param gameId game id
     */
    public static void finishGame(String gameId) {
        OkHttpClient client = new OkHttpClient();
        JSONObject jsonData = new JSONObject();

        Request request = new Request.Builder()
                .url(SERVER_URL + "/finish_game/" + gameId)
                .post(RequestBody.create(MEDIA_JSON, jsonData.toString()))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e(TAG, "request failure", e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {

            }
        });
    }
}
