package me.sieric.thehat.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.sieric.thehat.R;
import me.sieric.thehat.logic.DBManager;
import me.sieric.thehat.logic.Dictionary;
import me.sieric.thehat.logic.GameHolder;

public class DictionaryListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_dictionary_list);

        ListView dictListView = findViewById(R.id.dictListView);
        DBManager dbManager = new DBManager(this);

        List<Dictionary> dictList = dbManager.getDictionariesList();
        ArrayList<String> dictNames = new ArrayList<>(dictList.size());
        for (int i = 0; i < dictList.size(); i++) {
            dictNames.add(dictList.get(i).getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, dictNames);

        dictListView.setAdapter(adapter);

        dictListView.setOnItemClickListener((parent, itemClicked, position, id) -> {
            GameHolder.dictId = dictList.get(position).getId();
            Intent intent = new Intent(DictionaryListActivity.this, ManageDictionaryActivity.class);
            startActivity(intent);
        });

        dictListView.setOnItemLongClickListener((parent, itemClicked, position, id) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(DictionaryListActivity.this);
            builder.setTitle("Rename");
            final View customLayout = getLayoutInflater().inflate(R.layout.changing_dialog, null);
            builder.setView(customLayout);
            builder.setPositiveButton(getString(R.string.ok), (dialog, arg1) -> {
                EditText editText = customLayout.findViewById(R.id.editText);
                String newName = editText.getText().toString();
                dbManager.renameDictionary(GameHolder.dictId, newName);
                dictList.get(position).setName(newName);
                dictNames.set(position, newName);
                adapter.notifyDataSetChanged();
            });
            builder.setNegativeButton(getString(R.string.cancel), (dialog, arg1) -> {
                //
            });
            builder.setNeutralButton("Delete", (dialog, arg1) -> {
                AlertDialog.Builder builder2 = new AlertDialog.Builder(DictionaryListActivity.this);
                builder2.setTitle(String.format("Delete \"%s\" dictionary?", dictList.get(position).getName()));
                final View customLayout2 = getLayoutInflater().inflate(R.layout.asking_dialog, null);
                builder2.setView(customLayout2);
                builder2.setPositiveButton(getString(R.string.ok), (dialog2, arg2) -> {
                    Dictionary removedDict = dictList.get(position);
                    dbManager.removeDictionary(removedDict.getId());
                    dictList.remove(position);
                    dictNames.remove(position);
                    adapter.notifyDataSetChanged();
                    Toast toast = Toast.makeText(DictionaryListActivity.this, String.format("Dictionary \"%s\" was successfully removed", removedDict.getName()), Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                });
                builder2.setNegativeButton(getString(R.string.cancel), (dialog2, arg2) -> {
                    //
                });
                AlertDialog alert2 = builder2.create();
                alert2.show();
            });
            AlertDialog alert = builder.create();
            alert.show();
            return true;
        });

        Button exitButton = findViewById(R.id.exitButton);
        exitButton.setOnClickListener(v -> {
            Intent intent = new Intent(DictionaryListActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });

        Button newButton = findViewById(R.id.addButton);
        newButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(DictionaryListActivity.this);
            builder.setTitle("Dictionary name");
            final View customLayout = getLayoutInflater().inflate(R.layout.changing_dialog, null);
            builder.setView(customLayout);
            builder.setPositiveButton(getString(R.string.ok), (dialog, arg1) -> {
                EditText editText = customLayout.findViewById(R.id.editText);
                GameHolder.dictId = dbManager.addDictionary(editText.getText().toString(), Collections.emptyList());
                Intent intent = new Intent(DictionaryListActivity.this, ManageDictionaryActivity.class);
                startActivity(intent);
            });
            builder.setNegativeButton(getString(R.string.cancel), (dialog, arg1) -> {
                //
            });
            AlertDialog alert = builder.create();
            alert.show();
        });
    }
}
