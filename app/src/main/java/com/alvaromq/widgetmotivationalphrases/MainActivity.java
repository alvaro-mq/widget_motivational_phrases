package com.alvaromq.widgetmotivationalphrases;

import android.os.Bundle;

import com.alvaromq.widgetmotivationalphrases.database.DbHelper;
import com.alvaromq.widgetmotivationalphrases.Configuration;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import androidx.navigation.ui.AppBarConfiguration;

import com.alvaromq.widgetmotivationalphrases.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private MaterialButtonToggleGroup buttonToggleGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);


        DbHelper dbHelper = new DbHelper(MainActivity.this);
        Configuration configuration = dbHelper.getConfigurations();
        String language = configuration.getLanguage();
        // Toast.makeText(language, Toast.LENGTH_LONG);
        buttonToggleGroup = (MaterialButtonToggleGroup) findViewById(R.id.toggleButtonGroup);
        setCheckedButtonGroup(language);

        // ToogleButton for language

        buttonToggleGroup.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if (isChecked) {
                    if (checkedId == R.id.btnEnglish) {
                        Log.v("tag", "EN");
                        saveConfigurationLanguage("EN");
                    }
                    if (checkedId == R.id.btnSpanish) {
                        Log.v("tag", "SP");
                        saveConfigurationLanguage("SP");
                    }
                }
            }
        });
    }


    private void setCheckedButtonGroup(String language) {
        MaterialButton button;
        if (language.equals("EN")) {
            button = buttonToggleGroup.findViewById(R.id.btnEnglish);
        } else {
            button = buttonToggleGroup.findViewById(R.id.btnSpanish);
        }
        button.setChecked(true);
    }
    private void saveConfigurationLanguage(String language) {
        DbHelper db = new DbHelper(MainActivity.this);
        db.updateConfiguration("LANGUAGE", language);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}