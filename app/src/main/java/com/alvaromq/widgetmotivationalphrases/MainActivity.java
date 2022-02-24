package com.alvaromq.widgetmotivationalphrases;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import com.alvaromq.widgetmotivationalphrases.database.DbHelper;
import com.alvaromq.widgetmotivationalphrases.Configuration;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;

import androidx.core.content.FileProvider;
import androidx.navigation.ui.AppBarConfiguration;

import com.alvaromq.widgetmotivationalphrases.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private MaterialButtonToggleGroup buttonToggleGroup;

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

        buttonToggleGroup = (MaterialButtonToggleGroup) findViewById(R.id.toggleButtonGroup);
        setCheckedButtonGroup(language);


        Intent intent = getIntent();
        String phrase = intent.getStringExtra("phrase");
        String author = intent.getStringExtra("author");
        String nick = intent.getStringExtra("nick");
        String avatarNick = intent.getStringExtra("nickAvatar");
        if (phrase != null) {
            Log.v("tag", phrase);
            TextView tvDescription = (TextView) findViewById(R.id.tvPhraseActivity);
            tvDescription.setText(phrase);

            TextView tvAuthor = (TextView) findViewById(R.id.tvAuthorActivity);
            tvAuthor.setText(author);

            TextView tvNick = (TextView) findViewById(R.id.tvNickActivity);
            tvNick.setText(nick);

            TextView tvAvatarNick = (TextView) findViewById(R.id.tvNickAvatarActivity);
            tvAvatarNick.setText(avatarNick);

            View view = getWindow().getDecorView().findViewById(R.id.llPhrase);

            view.post(new Runnable() {
                @Override
                public void run() {
                    takeScreenshot(view);
                }
            });
        }

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