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

import com.alvaromq.widgetmotivationalphrases.R;

import java.io.IOException;
import java.util.ArrayList;

public class DbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "db_mp2.db";
    private static final String TABLE_PHRASES = "phrases";
    private static final String TABLE_CONFIGURATIONS = "configurations";
    private static final String TABLE_TYPES = "types";
    private final Context fContext;

    public DbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        fContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        try {
            Log.v("tag", "init create table configurations");
            sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_CONFIGURATIONS + "(LANGUAGE TEXT NOT NULL, TYPE TEXT NOT NULL)");

            Log.v("tag", "init create table types");
            sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_TYPES + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, DESCRIPTION_SP TEXT NOT NULL, DESCRIPTION_EN TEXT NOT NULL)");

            Log.v("tag", "init create table phrases");
            sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_PHRASES + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, DESCRIPTION_SP TEXT NOT NULL, DESCRIPTION_EN TEXT NOT NULL, AUTHOR TEXT NOT NULL)");

            Log.v("tag", "init insert data");
            seedsTypes(sqLiteDatabase);
            seedsConfigurations(sqLiteDatabase);
            seedsPhrases(sqLiteDatabase);
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

    public void seedsConfigurations(SQLiteDatabase sqLiteDatabase) {
        ContentValues values = new ContentValues();
        values.put("LANGUAGE", "SP");
        values.put("TYPE", "1");
        sqLiteDatabase.insert(TABLE_CONFIGURATIONS, null, values);
    }

    public void seedsPhrases(SQLiteDatabase sqLiteDatabase) {
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
    }

    public void seedsTypes(SQLiteDatabase sqLiteDatabase) {
        ContentValues values = new ContentValues();
        Resources res = fContext.getResources();

        String[] types = res.getStringArray(R.array.types_array);
        for (String type: types) {
            String[] parts = type.split("\\|");
            values.put("ID", parts[0]);
            values.put("DESCRIPTION_EN", parts[1]);
            values.put("DESCRIPTION_SP", parts[2]);
            sqLiteDatabase.insert(TABLE_TYPES, null, values);
        }
    }

    @SuppressLint("Range")
    public Phrase getRandomPhrase(String language) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT ID, DESCRIPTION_SP, DESCRIPTION_EN, AUTHOR FROM " + TABLE_PHRASES + " ORDER BY RANDOM() LIMIT 1", null);
        Phrase phrase = new Phrase();
        if (cursor != null) {
            cursor.moveToFirst();
            int id = cursor.getInt(cursor.getColumnIndex("ID"));
            String nameColumnDescription = language.equals("SP") ? "DESCRIPTION_SP" : "DESCRIPTION_EN";
            String description = " “" + cursor.getString(cursor.getColumnIndex(nameColumnDescription)) + "” ";
            String author = cursor.getString(cursor.getColumnIndex("AUTHOR"));
            phrase.setId(id);
            phrase.setDescription(description);
            phrase.setAuthor(author);
        }
        return phrase;
    }

    @SuppressLint("Range")
    public Configuration getConfigurations() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT LANGUAGE, TYPE FROM " + TABLE_CONFIGURATIONS, null);
        String language = "";
        String type = "";
        Configuration configuration = new Configuration();
        if (cursor != null) {
            cursor.moveToFirst();
            language = cursor.getString(cursor.getColumnIndex("LANGUAGE"));
            type = cursor.getString(cursor.getColumnIndex("TYPE"));
            configuration.setLanguage(language);
            configuration.setType(type);
        }
        return configuration;
    }

    public boolean updateConfiguration(String column, String value) {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("UPDATE " + TABLE_CONFIGURATIONS + " SET " + column + " = '" + value +"'");
        return true;
    }

    @SuppressLint("Range")
    public ArrayList<Type> getTypes() {
        ArrayList<Type> types = new ArrayList<Type>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT ID, DESCRIPTION_EN, DESCRIPTION_SP FROM " + TABLE_TYPES, null);
        while(cursor.moveToNext()) {
            Type type = new Type();
            type.setId(cursor.getInt(cursor.getColumnIndex("ID")));
            type.setDescriptionEn(cursor.getString(cursor.getColumnIndex("DESCRIPTION_EN")));
            type.setDescriptionSp(cursor.getString(cursor.getColumnIndex("DESCRIPTION_SP")));
            types.add(type);
        }
        return types;
    }
}
