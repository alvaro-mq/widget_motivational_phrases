package com.alvaromq.widgetmotivationalphrases;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.alvaromq.widgetmotivationalphrases.database.Configuration;
import com.alvaromq.widgetmotivationalphrases.database.DbHelper;
import com.alvaromq.widgetmotivationalphrases.database.Phrase;

/**
 * Implementation of App Widget functionality.
 */
public class WidgetProvider extends AppWidgetProvider {

    public static String ACTION_SHARE = "ACTION_SHARE";
    public static String ACTION_OPEN_MAIN_ACTIVITY = "ACTION_OPEN_MAIN_ACTIVITY";
    public static String ACTION_SHARE_MAIN_ACTIVITY = "ACTION_SHARE_MAIN_ACTIVITY";
    public static String ACTION_COPY = "ACTION_COPY";

    private static int idPhrase;
    public static void updateAppWidgets(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId: appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, true);
        }
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, Boolean isEventMainActivity) {
        DbHelper dbHelper = new DbHelper(context);
        Configuration configuration = dbHelper.getConfigurations();
        String language = configuration.getLanguage();
        String idTypes = configuration.getType();
        Phrase phrase = isEventMainActivity != null && idPhrase > 0 ? dbHelper.getPhraseForId(idPhrase, language) : dbHelper.getRandomPhrase(language, idTypes);
        idPhrase = phrase.getId();

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_provider);
        views.setTextViewText(R.id.tvDescription, phrase.getDescription());
        views.setTextViewText(R.id.tvAuthor, phrase.getAuthor());
        views.setTextViewText(R.id.tvNick, Utils.generateNick(phrase.getAuthor()));
        views.setTextViewText(R.id.tvNickAvatar, Utils.generateNickAvatar(phrase.getAuthor()));
        // Create an intent to launch MainActivity
        PendingIntent pendingIntentMainActivity = makeIntentForMainActivity(context, phrase, ACTION_OPEN_MAIN_ACTIVITY);
        views.setOnClickPendingIntent(R.id.tvDescription, pendingIntentMainActivity);

        // Create an intent to Refresh phrase
        PendingIntent pendingIntentRefresh = makeIntentRefresh(context);
        views.setOnClickPendingIntent(R.id.btnRefresh, pendingIntentRefresh);

        // Instruct the widget manager to update the widget

        // Create an intent to launch MainActivity share
        PendingIntent pendingIntentMainActivityShare = makeIntentForMainActivity(context, phrase, ACTION_SHARE_MAIN_ACTIVITY);
        views.setOnClickPendingIntent(R.id.btnShare, pendingIntentMainActivityShare);

        Intent intentCopy = new Intent(context, WidgetProvider.class);
        intentCopy.putExtra("phrase", phrase.getDescription());
        intentCopy.putExtra("author", phrase.getAuthor());
        intentCopy.setAction(ACTION_COPY);
        PendingIntent pendingCopy = PendingIntent.getBroadcast(context, 0, intentCopy, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.btnCopy, pendingCopy);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, null);
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
        } else if (ACTION_COPY.equals(intent.getAction())) {
            String text = intent.getStringExtra("phrase") + " -" + intent.getStringExtra("author");
            Log.v("tag", text);
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("text",  text);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(context,R.string.clipboard, Toast.LENGTH_SHORT).show();
        }
    }

    private static PendingIntent makeIntentRefresh(Context context) {
        Intent intentRefresh = new Intent(context, WidgetProvider.class);
        intentRefresh.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        PendingIntent pendingSync = PendingIntent.getBroadcast(context,0, intentRefresh, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingSync;
    }

    private static PendingIntent makeIntentForMainActivity(Context context, Phrase phrase, String action) {
        Intent intentActivityMain = new Intent(context, MainActivity.class);
        intentActivityMain.setAction(action);
        intentActivityMain.putExtra("phrase", phrase.getDescription());
        intentActivityMain.putExtra("author", phrase.getAuthor());
        PendingIntent pendingIntent2 = PendingIntent.getActivity(context, 0, intentActivityMain, PendingIntent.FLAG_UPDATE_CURRENT);
        return  pendingIntent2;
    }
}