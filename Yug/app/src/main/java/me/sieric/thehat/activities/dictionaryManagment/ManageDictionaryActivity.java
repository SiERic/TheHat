package me.sieric.thehat.activities.dictionaryManagment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import me.sieric.thehat.R;
import me.sieric.thehat.logic.DBManager;
import me.sieric.thehat.logic.GameHolder;
import me.sieric.thehat.logic.data.Word;
import me.sieric.thehat.ocrreader.OcrCaptureActivity;

public class ManageDictionaryActivity extends AppCompatActivity {
    private static final int RC_OCR_CAPTURE = 9003;

    private ArrayList<Boolean> isChosen;
    private ArrayList<Boolean> isNew;
    private WordsAdapter adapter;
    private ArrayList<Long> replacedWords = new ArrayList<>();
    private ArrayList<Word> words;

    private TextView wordsNumberView;
    private int wordsNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_dictionary);

        wordsNumberView = findViewById(R.id.wordsNumberView);

        DBManager dbManager = new DBManager(this);
        ListView wordListView = findViewById(R.id.wordsListView);
        words = dbManager.getWordsList(GameHolder.dictId);
        isChosen = new ArrayList<>(words.size());
        isNew = new ArrayList<>(words.size());
        for (int i = 0; i < words.size(); i++) {
            isChosen.add(true);
            isNew.add(false);
        }
        adapter = new WordsAdapter(this, words);
        wordsNumber = words.size();
        setWordsNumber();
        wordListView.setAdapter(adapter);

        Button exitButton = findViewById(R.id.exitButton);

        exitButton.setOnClickListener(v -> {
                    Toast toast = Toast.makeText(ManageDictionaryActivity.this, getString(R.string.press_longer), Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show(); });
        exitButton.setOnLongClickListener(v -> {
            Intent intent = new Intent(ManageDictionaryActivity.this, DictionaryListActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return true; });

        Button saveButton = findViewById(R.id.saveButton);

        saveButton.setOnClickListener(v -> {
            Toast toast = Toast.makeText(ManageDictionaryActivity.this, getString(R.string.press_longer), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show(); });
        saveButton.setOnLongClickListener(v -> {
            for (int i = 0; i < isChosen.size(); i++) {
                if (!isChosen.get(i) && words.get(i).getWordId() != -1) {
                    dbManager.remove(words.get(i).getWordId(), GameHolder.dictId);
                }
                if (isChosen.get(i) && words.get(i).getWordId() == -1) {
                    dbManager.add(words.get(i).getWord(), GameHolder.dictId);
                }
            }
            for (long wordId : replacedWords) {
                if (wordId != -1) {
                    dbManager.remove(wordId, GameHolder.dictId);
                }
            }
            Intent intent = new Intent(ManageDictionaryActivity.this, DictionaryListActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return true; });

        Button addWordButton = findViewById(R.id.addWordButton);
        addWordButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(ManageDictionaryActivity.this);
            builder.setTitle("New word");
            final View customLayout = getLayoutInflater().inflate(R.layout.changing_dialog, null);
            builder.setView(customLayout);
            builder.setPositiveButton(getString(R.string.ok), (dialog, arg1) -> {
                EditText editText = customLayout.findViewById(R.id.editText);
                words.add(0, new Word(-1, editText.getText().toString()));
                isChosen.add(0, true);
                isNew.add(0, true);
                wordsNumber++;
                setWordsNumber();
                adapter.notifyDataSetChanged();
            });
            builder.setNegativeButton(getString(R.string.cancel), (dialog, arg1) -> {
                //
            });
            AlertDialog alert = builder.create();
            alert.show();
        });

        Button addFromCameraButton = findViewById(R.id.addFromCameraButton);
        addFromCameraButton.setOnClickListener(v -> {
            Intent intent = new Intent(ManageDictionaryActivity.this, OcrCaptureActivity.class);
            intent.putExtra(OcrCaptureActivity.AutoFocus, true);
            intent.putExtra(OcrCaptureActivity.UseFlash, false);

            startActivityForResult(intent, RC_OCR_CAPTURE);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_OCR_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    String[] detections = data.getStringArrayExtra(OcrCaptureActivity.Detections);
                    for (String detection : detections) {
                        String[] detectedWords = detection.split(" ");
                        for (String detectedWord : detectedWords) {
                            detectedWord = detectedWord.trim();
                            if (detectedWord.isEmpty()) {
                                continue;
                            }
                            words.add(0, new Word(-1, detectedWord));
                            isChosen.add(0, true);
                            isNew.add(0, true);
                            wordsNumber++;
                        }

                    }
                    setWordsNumber();
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }

    private class WordsAdapter extends ArrayAdapter<Word> {
        private WordsAdapter(Context context, ArrayList<Word> words) {
            super(context, 0, words);
        }

        @NotNull
        @Override
        public View getView(int position, View convertView, @NotNull ViewGroup parent) {
            Word word = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.words_managment, parent, false);
            }

            convertView.setOnLongClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(ManageDictionaryActivity.this);
                builder.setTitle("Change word");
                final View customLayout = getLayoutInflater().inflate(R.layout.changing_dialog, null);
                builder.setView(customLayout);
                builder.setPositiveButton(getString(R.string.ok), (dialog, arg1) -> {
                    EditText editText = customLayout.findViewById(R.id.editText);
                    replacedWords.add(words.get(position).getWordId());
                    words.set(position, new Word(-1, editText.getText().toString()));
                    isChosen.set(position, true);
                    isNew.set(position, true);
                    adapter.notifyDataSetChanged();
                });
                builder.setNegativeButton(getString(R.string.cancel), (dialog, arg1) -> {
                    //
                });
                AlertDialog alert = builder.create();
                alert.show();
                return true;
            });
            if (isNew.get(position)) {
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
                    isNew.set(position, true);
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