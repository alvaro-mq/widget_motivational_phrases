package com.alvaromq.widgetmotivationalphrases.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.alvaromq.widgetmotivationalphrases.Phrase;
import com.alvaromq.widgetmotivationalphrases.R;

import java.io.IOException;
import java.util.logging.Logger;

public class DbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "db_mp2.db";
    private static final String TABLE_PHRASES = "phrases";
    private final Context fContext;

    public DbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        fContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        try {
            Log.v("tag", "init create table");
            sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_PHRASES + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, DESCRIPTION_SP TEXT NOT NULL, DESCRIPTION_EN TEXT NOT NULL, AUTHOR TEXT NOT NULL)");
           
            ContentValues values = new ContentValues();
            Resources res = fContext.getResources();

            String[] phrases = res.getStringArray(R.array.phrases_array);
            for (String phrase: phrases) {
                String[] parts = phrase.split("\\|");
                values.put("DESCRIPTION_EN", parts[0]);
                values.put("DESCRIPTION_SP", parts[1]);
                values.put("AUTHOR", parts[2]);
                sqLiteDatabase.insert(TABLE_PHRASES, null, values);
            }
            Log.v("tag", "finish insert data");
        } catch (SQLiteException e) {
            Log.e("tag", e.getMessage());
            try {
                throw new IOException(e);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE " + TABLE_PHRASES);
        onCreate(sqLiteDatabase);
    }

    @SuppressLint("Range")
    public Phrase getRandomPhrase() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT ID, DESCRIPTION_SP, AUTHOR FROM " + TABLE_PHRASES + " ORDER BY RANDOM() LIMIT 1", null);
        String description = "";
        String author = "";
        Phrase phrase = new Phrase();
        if (cursor != null) {
            cursor.moveToFirst();
            description = "\"" + cursor.getString(cursor.getColumnIndex("DESCRIPTION_SP"))+"\"";
            author = cursor.getString(cursor.getColumnIndex("AUTHOR"));
            phrase.setAuthor(author);
            phrase.setDescription(description);
        }
        return phrase;
    }
}
