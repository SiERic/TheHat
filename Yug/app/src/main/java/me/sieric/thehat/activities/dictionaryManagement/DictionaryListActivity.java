package me.sieric.thehat.activities.dictionaryManagement;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.sieric.thehat.R;
import me.sieric.thehat.activities.MainActivity;
import me.sieric.thehat.logic.DBManager;
import me.sieric.thehat.logic.data.Dictionary;
import me.sieric.thehat.logic.GameHolder;

/**
 * Activity for dictionary management
 * Provides creating, renaming and removing dictionaries via dialog
 * Opens {@link ManageDictionaryActivity} to add/remove words
 */
public class DictionaryListActivity extends AppCompatActivity {

    private DBManager dbManager;
    private List<Dictionary> dictList;
    private ArrayList<String> dictNames;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictionary_list);

        ListView dictListView = findViewById(R.id.dictListView);
        Button exitButton = findViewById(R.id.exitButton);
        Button newButton = findViewById(R.id.addButton);

        dbManager = new DBManager(this);
        dictList = dbManager.getDictionariesList();
        dictNames = new ArrayList<>(dictList.size());
        for (int i = 0; i < dictList.size(); i++) {
            dictNames.add(dictList.get(i).getName());
        }
        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, dictNames);

        dictListView.setAdapter(adapter);

        dictListView.setOnItemClickListener((parent, itemClicked, position, id) -> {
            GameHolder.dictId = dictList.get(position).getId();
            Intent intent = new Intent(DictionaryListActivity.this, ManageDictionaryActivity.class);
            startActivity(intent);
        });
        dictListView.setOnItemLongClickListener(new RenameRemoveOnLongClickListener());

        newButton.setOnClickListener(new AddNewOnClickListener());

        exitButton.setOnClickListener(v -> {
            Intent intent = new Intent(DictionaryListActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });
    }

    /**
     * Opens dialog to rename/remove dictionary
     */
    private class RenameRemoveOnLongClickListener implements AdapterView.OnItemLongClickListener {

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            AlertDialog.Builder builder = new AlertDialog.Builder(DictionaryListActivity.this);
            builder.setTitle(getString(R.string.rename));
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
            builder.setNeutralButton(getString(R.string.delete), (dialog, arg1) -> {
                AlertDialog.Builder builder2 = new AlertDialog.Builder(DictionaryListActivity.this);
                builder2.setTitle(String.format(getString(R.string.delete_dict_ask_format), dictList.get(position).getName()));
                final View customLayout2 = getLayoutInflater().inflate(R.layout.asking_dialog, null);
                builder2.setView(customLayout2);
                builder2.setPositiveButton(getString(R.string.ok), (dialog2, arg2) -> {
                    Dictionary removedDict = dictList.get(position);
                    dbManager.removeDictionary(removedDict.getId());
                    dictList.remove(position);
                    dictNames.remove(position);
                    adapter.notifyDataSetChanged();
                    Toast toast = Toast.makeText(DictionaryListActivity.this,
                            String.format("Dictionary \"%s\" was successfully removed", removedDict.getName()), Toast.LENGTH_SHORT);
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
        }
    }

    /**
     * Opens dialog to create new dictionary
     */
    private class AddNewOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(DictionaryListActivity.this);
            builder.setTitle(getString(R.string.dict_name));
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
        }
    }
}


