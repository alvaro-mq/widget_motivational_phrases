package com.alvaromq.widgetmotivationalphrases;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RemoteViews;

import com.alvaromq.widgetmotivationalphrases.database.DbHelper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Implementation of App Widget functionality.
 */
public class WidgetProvider extends AppWidgetProvider {

    public String ACTION_SHARE = "ACTION_SHARE";
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        DbHelper dbHelper = new DbHelper(context);
        Phrase phrase = dbHelper.getRandomPhrase();


        // Create an intent to launch MainActivity
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0 );

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_provider);
        views.setTextViewText(R.id.appwidget_text, phrase.getDescription());
        views.setTextViewText(R.id.tvPhrase, phrase.getAuthor());
        views.setTextViewText(R.id.tvNick, generateNick(phrase.getAuthor()));
        views.setTextViewText(R.id.tvNickAvatar, generateNickAvatar(phrase.getAuthor()));
        views.setOnClickPendingIntent(R.id.appwidget_text, pendingIntent);


        Intent intentRefresh = new Intent(context, WidgetProvider.class);
        intentRefresh.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        PendingIntent pendingSync = PendingIntent.getBroadcast(context,0, intentRefresh, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.btnRefresh, pendingSync);

        // Instruct the widget manager to update the widget

        Intent intentActivityMain = new Intent(context, MainActivity.class);
        intentActivityMain.putExtra("phrase", phrase.getDescription());
        intentActivityMain.putExtra("author", phrase.getAuthor());
        intentActivityMain.putExtra("nick", generateNick(phrase.getAuthor()));
        intentActivityMain.putExtra("nickAvatar", generateNickAvatar(phrase.getAuthor()));
        PendingIntent pendingIntent2 = PendingIntent.getActivity(context, 0, intentActivityMain, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.btnShare, pendingIntent2);

        /*Intent intentShare = new Intent(context, WidgetProvider.class);
        intentShare.putExtra("phrase", phrase.getDescription());
        intentShare.setAction(ACTION_SHARE);
        PendingIntent pendingShare = PendingIntent.getBroadcast(context, 0, intentShare, PendingIntent.FLAG_UPDATE_CURRENT);

        PendingIntent.getActivity(context, 0, intentShare, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.btnShare, pendingShare);*/

        appWidgetManager.updateAppWidget(appWidgetId, views);

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }


    @SuppressLint("ResourceType")
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        super.onReceive(context, intent);
        Log.v("tag", intent.getAction());
        if (AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals(intent.getAction())) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName watchWidget = new ComponentName(context, WidgetProvider.class);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                    watchWidget);
            onUpdate(context, appWidgetManager, appWidgetIds);
        } else if (ACTION_SHARE.equals(intent.getAction())) {
            String phrase = intent.getStringExtra("phrase");
            Log.v("tag", "share");
            Intent sendIntent = new Intent();
            sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, phrase);
            sendIntent.setType("text/plain");

            Intent shareIntent = Intent.createChooser(sendIntent, null);
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            context.startActivity(shareIntent);
        }
    }

    private static String generateNick(String name) {
        return "@" + name.toLowerCase().replaceAll(" ", "_");
    }

    private static String generateNickAvatar(String name) {
        return name.toUpperCase().substring(0, 2);
    }
}