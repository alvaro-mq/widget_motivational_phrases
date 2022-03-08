package com.alvaromq.widgetmotivationalphrases;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class PhraseItem extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phrase_item);

        Intent intent = this.getIntent();
        if (intent != null) {
            String description =  intent.getStringExtra("description");
            String author = intent.getStringExtra("author");
            Log.v("tag", description);
            TextView tvNickAvatar = findViewById(R.id.tvNickAvatarItem);
            TextView tvDescription = findViewById(R.id.tvDescriptionItem);
            TextView tvAuthor = findViewById(R.id.tvAuthorItem);
            tvDescription.setText(description);
            tvNickAvatar.setText(Utils.generateNickAvatar(author));
            tvAuthor.setText(author);
        }
    }
}