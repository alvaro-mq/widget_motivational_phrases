package com.alvaromq.widgetmotivationalphrases.database;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.util.logging.Logger;

public class DbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "db_mp2.db";
    private static final String TABLE_PHRASES = "phrases";

    public DbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        try {
            Log.v("tag", "init create table");
            sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_PHRASES + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, DESCRIPTION TEXT NOT NULL, AUTHOR TEXT NOT NULL)");
            Log.v("tag", "init insert data");
            sqLiteDatabase.execSQL("INSERT INTO " + TABLE_PHRASES + "(ID, DESCRIPTION, AUTHOR) VALUES(1, 'Cuanta vida te esta costando tu sueldo?', 'Anonimo')");

            sqLiteDatabase.execSQL("INSERT INTO " + TABLE_PHRASES + "(ID, DESCRIPTION, AUTHOR) VALUES(2, 'Si estas buscando a la persona que cambiara tu vida echale una mirada al espejo', 'Anonimo')");

            sqLiteDatabase.execSQL("INSERT INTO " + TABLE_PHRASES + "(ID, DESCRIPTION, AUTHOR) VALUES(3, 'Si al principio no tienes exito, llamalo version 1.0', 'Anonimo')");

            sqLiteDatabase.execSQL("INSERT INTO " + TABLE_PHRASES + "(ID, DESCRIPTION, AUTHOR) VALUES(4, 'Y sin darte cuenta, vives en el trabajo y vas de visita a tu casa...', 'Anonimo')");

            sqLiteDatabase.execSQL("INSERT INTO " + TABLE_PHRASES + "(ID, DESCRIPTION, AUTHOR) VALUES(5, 'Cuando entendamos que no es un dia mas sino un dia menos. Empezaremos a valorar lo que realmente importa.', 'Anonimo')");

            sqLiteDatabase.execSQL("INSERT INTO " + TABLE_PHRASES + "(ID, DESCRIPTION, AUTHOR) VALUES(6, 'A veces no hay proxima vez. A veces no hay segundas oportunidades. A veces es ahora o nunca', 'Bob Marley')");

            sqLiteDatabase.execSQL("INSERT INTO " + TABLE_PHRASES + "(ID, DESCRIPTION, AUTHOR) VALUES(7, 'Te pasas la vida esperando que pase algo y al final, lo unico que pasa es la vida.', 'Juan Arevalo')");

            sqLiteDatabase.execSQL("INSERT INTO " + TABLE_PHRASES + "(ID, DESCRIPTION, AUTHOR) VALUES(8, 'No es falta de tiempo, es falta de interes, porque cuando la gente quiere, siempre habra tiempo.', 'Anonimo')");

            sqLiteDatabase.execSQL("INSERT INTO " + TABLE_PHRASES + "(ID, DESCRIPTION, AUTHOR) VALUES(9, 'Algunos quieren o suenan que algo ocurra otros hacen que suceda.', 'Anonimo')");

            sqLiteDatabase.execSQL("INSERT INTO " + TABLE_PHRASES + "(ID, DESCRIPTION, AUTHOR) VALUES(10, 'Que prefieres, el dolor de hoy y la recompensa de manana o la excusa de hoy y el arrepentimiento de manana.', 'Anonimo')");

            sqLiteDatabase.execSQL("INSERT INTO " + TABLE_PHRASES + "(ID, DESCRIPTION, AUTHOR) VALUES(11, 'Si te da miedo caer, aun no mereces volar.', 'Anonimo')");

            sqLiteDatabase.execSQL("INSERT INTO " + TABLE_PHRASES + "(ID, DESCRIPTION, AUTHOR) VALUES(12, 'Intentalo una y otra vez hasta que el miedo te tenga miedo.', 'Anonimo')");

            sqLiteDatabase.execSQL("INSERT INTO " + TABLE_PHRASES + "(ID, DESCRIPTION, AUTHOR) VALUES(13, 'Colecciona momentos, no cosas.', 'Anonimo')");

            sqLiteDatabase.execSQL("INSERT INTO " + TABLE_PHRASES + "(ID, DESCRIPTION, AUTHOR) VALUES(14, 'Las cosas no se dicen se hacen porque al hacerlas se dicen solas', 'Woody Allen')");

            sqLiteDatabase.execSQL("INSERT INTO " + TABLE_PHRASES + "(ID, DESCRIPTION, AUTHOR) VALUES(15, 'No permitas que alguien que ha enterrado su sueno entierre el tuyo.', 'Anonimo')");

            sqLiteDatabase.execSQL("INSERT INTO " + TABLE_PHRASES + "(ID, DESCRIPTION, AUTHOR) VALUES(16, 'Los suenos no se cumplen, se trabajan.', 'Anonimo')");

            sqLiteDatabase.execSQL("INSERT INTO " + TABLE_PHRASES + "(ID, DESCRIPTION, AUTHOR) VALUES(17, 'Si estas buscando a la persona que cambiara tu vida, echale una mirada al espejo.', 'Anonimo')");

            sqLiteDatabase.execSQL("INSERT INTO " + TABLE_PHRASES + "(ID, DESCRIPTION, AUTHOR) VALUES(18, 'No importa quien eres, de donde vienes. La capacidad de triunfar cominza con tigo. Siempre.', 'Oprah Winfrey')");

            sqLiteDatabase.execSQL("INSERT INTO " + TABLE_PHRASES + "(ID, DESCRIPTION, AUTHOR) VALUES(19, 'Si estas buscando a la persona que cambiara tu vida, echale una mirada al espejo.', 'Anonimo')");

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
    public String getRandomPhrase() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT ID, DESCRIPTION, AUTHOR FROM " + TABLE_PHRASES + " ORDER BY RANDOM() LIMIT 1", null);
        String description = "";
        if (cursor != null) {
            cursor.moveToFirst();
            description = cursor.getString(cursor.getColumnIndex("DESCRIPTION"));
        }
        return description;
    }
}
