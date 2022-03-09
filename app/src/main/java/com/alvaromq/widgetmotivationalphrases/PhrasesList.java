package com.alvaromq.widgetmotivationalphrases;

import android.content.Intent;
import android.os.Bundle;
import android.widget.AdapterView;

import androidx.appcompat.app.AppCompatActivity;

import com.alvaromq.widgetmotivationalphrases.database.ConfigurationModel;
import com.alvaromq.widgetmotivationalphrases.database.DbHelper;
import com.alvaromq.widgetmotivationalphrases.database.Phrase;
import com.alvaromq.widgetmotivationalphrases.databinding.ActivityPhrasesListBinding;

import java.util.ArrayList;

public class PhrasesList extends AppCompatActivity {
    private ActivityPhrasesListBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityPhrasesListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        DbHelper dbHelper = new DbHelper(PhrasesList.this);
        ConfigurationModel configuration = dbHelper.getConfigurations();
        String language = configuration.getLanguage();
        ArrayList<Phrase> phrases = dbHelper.getPhraseAll(language);
        /*for (Phrase phrase: phrases) {

        }*/

        ListAdapter listAdapter = new ListAdapter(PhrasesList.this, phrases);
        binding.lvPhrases.setAdapter(listAdapter);
        binding.lvPhrases.setClickable(true);
        binding.lvPhrases.setOnItemClickListener((AdapterView.OnItemClickListener) (adapterView, view, i, l) -> {
            Phrase phrase = (Phrase) listAdapter.getItem(i);
            Intent intent = new Intent(PhrasesList.this, PhraseItem.class);
            intent.putExtra("description", phrase.getDescription());
            intent.putExtra("author", phrase.getAuthor());
            startActivity(intent);
        });
    }
}