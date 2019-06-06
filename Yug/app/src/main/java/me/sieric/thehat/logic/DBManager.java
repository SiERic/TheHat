package me.sieric.thehat.logic;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.sieric.thehat.R;
import me.sieric.thehat.logic.data.Dictionary;
import me.sieric.thehat.logic.data.Word;

public class DBManager extends SQLiteOpenHelper {

    private static final String TAG = DBManager.class.getSimpleName();
    private static final String DICTIONARIES = "Dictionaries";
    private static final String WORDS = "Words";
    private static final String DICTIONARY_WORDS = "DictionaryWords";
    private static final String ID = "_id";
    private static final String NAME = "name";
    private static final String WORD = "word";
    private static final String TIME = "time";
    private static final String COUNT = "count";
    private static final String DICT_ID = "dictId";
    private static final String WORD_ID = "wordId";

    public DBManager(Context context) {
        super(context, "DB", null, 1);
        LocalDictionary.resources = context.getResources();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + DICTIONARIES + " ("
                + ID + " integer primary key,"
                + NAME + " text" + ");");
        db.execSQL("create table " + WORDS + " ("
                + ID + " integer primary key,"
                + WORD + " text,"
                + TIME + " integer,"
                + COUNT + " integer" + ");");
        db.execSQL("create table " + DICTIONARY_WORDS + " ("
                + DICT_ID + " integer, "
                + WORD_ID + " integer" + ");");

        for (LocalDictionary localDictionary : localDictList) {
            Log.d(TAG, localDictionary.name);
            System.out.println(localDictionary.name);
            addDictionary(localDictionary.name, localDictionary.getWordList(), db);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public long addDictionary(String dictName, List<String> words) {
        SQLiteDatabase db = getWritableDatabase();
        return addDictionary(dictName, words, db);
    }

    private long addDictionary(String dictName, List<String> words, SQLiteDatabase db) {
        ContentValues newDict = new ContentValues();
        newDict.put(NAME, dictName);
        long dictId = db.insert(DICTIONARIES, null, newDict);

        for (String word : words) {
            addWord(word, dictId, db);
        }
        return dictId;
    }

    public void removeDictionary(long dictId) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(DICTIONARIES, ID + " = ?", new String[] {String.valueOf(dictId)});
        db.delete(DICTIONARY_WORDS, DICT_ID + " = ?", new String[] {String.valueOf(dictId)});
    }

    public void renameDictionary(long dictId, String newName) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues dict = new ContentValues();
        dict.put(NAME, newName);
        db.update(DICTIONARIES, dict, ID + " = ?", new String[] {String.valueOf(dictId)});
    }

    public void addWord(String word, long dictId) {
        SQLiteDatabase db = getWritableDatabase();
        addWord(word, dictId, db);
    }

    private void addWord(String word, long dictId, SQLiteDatabase db) {
        ContentValues newWord = new ContentValues();
        newWord.put(WORD, word);
        newWord.put(TIME, 0);
        newWord.put(COUNT, 0);
        long wordId = db.insert(WORDS, null, newWord);
        if (wordId == -1) {
            Cursor cursor = db.query(WORDS, null, WORD + " = ?", new String[] {word}, null, null, null);
            Log.d(TAG, String.valueOf(cursor.getColumnIndex(ID)));
            wordId = cursor.getLong(cursor.getColumnIndex(ID));
            cursor.close();
        }
        ContentValues newDictWord = new ContentValues();
        newDictWord.put(DICT_ID, dictId);
        newDictWord.put(WORD_ID, wordId);
        db.insert(DICTIONARY_WORDS, null, newDictWord);
    }

    public void removeWord(long wordId, long dictId) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(DICTIONARY_WORDS, WORD_ID + " = ?" + " AND " + DICT_ID + " = ?", new String[] {String.valueOf(wordId), String.valueOf(dictId)});
    }

    public List<Dictionary> getDictionariesList() {
        List<Dictionary> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(DICTIONARIES, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                list.add(new Dictionary(cursor.getLong(cursor.getColumnIndex(ID)), cursor.getString(cursor.getColumnIndex(NAME))));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public ArrayList<Word> getWordsList(long dictId) {
        ArrayList<Word> list = new ArrayList<>();

        String localWordId = "localWordId";
        String localWord = "localWord";

        String sqlQuery = "SELECT " + WORDS + "." + ID + " AS " + localWordId + ", " + WORDS + "." + WORD + " AS " + localWord +
                " FROM " + DICTIONARY_WORDS + " INNER JOIN " + WORDS + " ON " + WORDS + "." + ID + " = " + DICTIONARY_WORDS + "." + WORD_ID +
                " WHERE " + DICTIONARY_WORDS + "." + DICT_ID + " = ?" + ";";

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(sqlQuery, new String[] {String.valueOf(dictId)});

        if (cursor.moveToFirst()) {
            int wordIdIndex = cursor.getColumnIndex(localWordId);
            int wordIndex = cursor.getColumnIndex(localWord);
            do {
                long wordId = cursor.getLong(wordIdIndex);
                String word = cursor.getString(wordIndex);
                list.add(new Word(wordId, word));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    static class LocalDictionary {

        private ArrayList<String> wordList = null;
        private String name;
        private int id;

        LocalDictionary(String name, int id) {
            this.name = name;
            this.id = id;
        }

        private ArrayList<String> getWordList() {
            if (wordList == null) {
                wordList = new ArrayList<>();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(resources.openRawResource(id)))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        wordList.add(line);
                    }
                    Collections.shuffle(wordList);
                } catch (Exception ignored) {}
            }
            return wordList;
        }

        private static Resources resources;
    }

    private static ArrayList<LocalDictionary> localDictList = new ArrayList<>();

    static {
        localDictList.add(new LocalDictionary("Easy English nouns", R.raw.english_easy));
        localDictList.add(new LocalDictionary("Russian nouns", R.raw.russian_all));
        localDictList.add(new LocalDictionary("English nouns", R.raw.english_nouns));
        localDictList.add(new LocalDictionary("Esperanto nouns", R.raw.esperanto_nouns));
        localDictList.add(new LocalDictionary("Most common French nouns", R.raw.most_common_french_nouns));
        localDictList.add(new LocalDictionary("German nouns", R.raw.most_common_german_nouns));
        localDictList.add(new LocalDictionary("Japanese words", R.raw.japanese_words));
    }
}
