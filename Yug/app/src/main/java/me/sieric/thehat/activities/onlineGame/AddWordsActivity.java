package me.sieric.thehat.activities.onlineGame;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import me.sieric.thehat.R;
import me.sieric.thehat.logic.DBManager;
import me.sieric.thehat.logic.GameHolder;
import me.sieric.thehat.logic.NetworkManager;
import me.sieric.thehat.logic.data.Word;

/**
 * Activity to add words from chosen dictionary to the online game
 */
public class AddWordsActivity extends AppCompatActivity {

    private ArrayList<Boolean> isChosen;
    private WordsAdapter adapter;
    private ArrayList<Word> words;

    private TextView wordsNumberView;
    private int wordsNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_game_add_words);

        wordsNumberView = findViewById(R.id.wordsNumberView);
        Button exitButton = findViewById(R.id.exitButton);
        Button addButton = findViewById(R.id.addButton);

        DBManager dbManager = new DBManager(this);
        ListView wordListView = findViewById(R.id.wordsListView);
        words = dbManager.getWordsList(GameHolder.dictId);
        isChosen = new ArrayList<>(words.size());
        for (int i = 0; i < words.size(); i++) {
            isChosen.add(false);
        }
        adapter = new WordsAdapter(this, words);
        wordsNumber = 0;
        setWordsNumber();
        wordListView.setAdapter(adapter);

        exitButton.setOnClickListener(v -> {
            Toast toast = Toast.makeText(AddWordsActivity.this, getString(R.string.press_longer), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show(); });
        exitButton.setOnLongClickListener(v -> {
            AddWordsActivity.this.onBackPressed();
            return true; });

        addButton.setOnClickListener(v -> {
            Toast toast = Toast.makeText(AddWordsActivity.this, getString(R.string.press_longer), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show(); });

        addButton.setOnLongClickListener(v -> {
            List<String> newWords = new ArrayList<>();
            for (int i = 0; i < words.size(); i++) {
                if (isChosen.get(i)) {
                    newWords.add(words.get(i).getWord());
                }
            }
            NetworkManager.addWords(GameHolder.gameId, newWords);
            AddWordsActivity.this.onBackPressed();
            return true;
        });

    }

    /**
     * Words adapter
     * Has a checkbox to add word to the game
     */
    private class WordsAdapter extends ArrayAdapter<Word> {
        WordsAdapter(Context context, ArrayList<Word> words) {
            super(context, 0, words);
        }

        @NotNull
        @Override
        public View getView(int position, View convertView, @NotNull ViewGroup parent) {
            Word word = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.words_managment, parent, false);
            }

            if (isChosen.get(position)) {
                convertView.setBackgroundColor(getColor(R.color.newWordColor));
            } else {
                convertView.setBackgroundColor(getColor(R.color.backgroundWhite));
            }
            TextView wordView = convertView.findViewById(R.id.wordView);
            CheckBox isChosenView = convertView.findViewById(R.id.isChosenBox);

            isChosenView.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked != isChosen.get(position)) {
                    wordsNumber += (isChecked ? 1 : -1);
                    setWordsNumber();
                    isChosen.set(position, isChecked);
                    adapter.notifyDataSetChanged();
                }
            });

            assert word != null;
            wordView.setText(word.getWord());
            isChosenView.setChecked(isChosen.get(position));

            return convertView;
        }
    }

    private void setWordsNumber() {
        wordsNumberView.setText(String.format(getString(R.string.format_words_number), wordsNumber));
    }
}
