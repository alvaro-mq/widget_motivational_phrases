package com.alvaromq.widgetmotivationalphrases;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import com.alvaromq.widgetmotivationalphrases.database.Configuration;
import com.alvaromq.widgetmotivationalphrases.database.DbHelper;
import com.alvaromq.widgetmotivationalphrases.database.Phrase;
import com.alvaromq.widgetmotivationalphrases.database.Type;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.navigation.ui.AppBarConfiguration;

import com.alvaromq.widgetmotivationalphrases.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private MaterialButtonToggleGroup buttonToggleGroup;
    private TextView tvDescription;
    private TextView tvAuthor;
    private TextView tvNick;
    private TextView tvAvatarNick;

    @SuppressLint("WrongThread")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        DbHelper dbHelper = new DbHelper(MainActivity.this);
        Configuration configuration = dbHelper.getConfigurations();
        String language = configuration.getLanguage();
        String[] typesKeys = configuration.getType().split(",");

        buttonToggleGroup = (MaterialButtonToggleGroup) findViewById(R.id.toggleButtonGroup);
        setCheckedButtonGroup(language);

        LinearLayout layout = findViewById(R.id.groupCheckbox);
        ArrayList<Type> types = dbHelper.getTypes();
        for (Type type: types) {
            CheckBox checkBox = makeCheckbox(type, typesKeys);
            layout.addView(checkBox);
        }

        Intent intent = getIntent();
        String action = intent.getAction();
        Phrase phrase = makePhrase(intent);
        Log.v("tag", action);
        if (intent.getStringExtra("phrase") != null) {
            setDefaultConfiguration(phrase);
            if (WidgetProvider.ACTION_SHARE_MAIN_ACTIVITY.equals(action)) {
                View view = getWindow().getDecorView().findViewById(R.id.llPhrase);
                view.post(() -> takeScreenshot(view));
            }
        } else {
            Phrase randomPhrase = dbHelper.getRandomPhrase(language, configuration.getType());
            setDefaultConfiguration(randomPhrase);
        }

        // ToogleButton for language
        buttonToggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            validateConfigurationLanguage(checkedId, isChecked);
        });
    }

    private void takeScreenshot(View view){
        view.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);

        String filePath = Environment.getExternalStorageDirectory()+"/Download/"+ Calendar.getInstance().getTime().toString()+".jpg";
        Log.v("tag", filePath);
        File fileScreenshot = new File(filePath);
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(fileScreenshot);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        shareImage(bitmap);
    }

    private void shareImage(Bitmap bitmap) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/jpeg");
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Title", null);
        Uri uri = Uri.parse(path);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        this.startActivity(Intent.createChooser(intent, "Select"));
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
        UpdateService.startActionUpdateWidget(MainActivity.this);
    }

    private void saveConfigurationType(String type, boolean isChecked) {
        DbHelper db = new DbHelper(MainActivity.this);
        String types = db.getConfigurations().getType();
        Log.v("tag", types);
        String[] keys = types.split(",");
        Set setKeys = new HashSet(Arrays.asList(keys));
        if (isChecked == true) {
            setKeys.add(type);
        } else {
            setKeys.remove(type);
        }
        String typeSave = TextUtils.join(",", setKeys.toArray());
        Log.v("tag", typeSave);
        db.updateConfiguration("TYPE", typeSave);
        UpdateService.startActionUpdateWidget(MainActivity.this);
    }

    private void setDefaultConfiguration(Phrase phrase) {
        tvDescription = (TextView) findViewById(R.id.tvPhraseActivity);
        tvDescription.setText(phrase.getDescription());

        tvAuthor = (TextView) findViewById(R.id.tvAuthorActivity);
        tvAuthor.setText(phrase.getAuthor());

        tvNick = (TextView) findViewById(R.id.tvNickActivity);
        tvNick.setText(Utils.generateNick(phrase.getAuthor()));

        tvAvatarNick = (TextView) findViewById(R.id.tvNickAvatarActivity);
        tvAvatarNick.setText(Utils.generateNickAvatar(phrase.getAuthor()));
    }

    private void validateConfigurationLanguage(int checkedId, boolean isChecked) {
        if (isChecked == false) {
            MaterialButtonToggleGroup groupButton = findViewById(R.id.toggleButtonGroup);
            groupButton.check(R.id.btnEnglish == checkedId ? R.id.btnSpanish : R.id.btnEnglish);
            saveConfigurationLanguage(R.id.btnEnglish == checkedId ? "SP" : "EN");
        } else {
            saveConfigurationLanguage(R.id.btnEnglish == checkedId ? "EN" : "SP");
        }
    }

    private Phrase makePhrase(Intent intent) {
        Phrase phrase = new Phrase();
        phrase.setDescription(intent.getStringExtra("phrase"));
        phrase.setAuthor(intent.getStringExtra("author"));
        return phrase;
    }

    private CheckBox makeCheckbox(Type type, String[] typesKeys) {
        CheckBox checkBox = new CheckBox(this);
        Locale current = getResources().getConfiguration().locale;
        String language = current.getLanguage();
        String description = language.equals("es") ? type.getDescriptionSp() : type.getDescriptionEn();
        checkBox.setText(description);
        checkBox.setPadding(0,0,0,0);
        boolean isChecked = Arrays.asList(typesKeys).indexOf(String.valueOf(type.getId())) >= 0;
        checkBox.setChecked(isChecked);
        checkBox.setId(View.generateViewId());
        checkBox.setTag(type.getId());
        checkBox.setOnClickListener(view -> {
            boolean checked = ((CheckBox) view).isChecked();
            saveConfigurationType(String.valueOf(view.getTag()), checked);
        });
        return checkBox;
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
        if (id == R.id.itemListPhrases) {
            startActivity(new Intent(this, PhrasesList.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}