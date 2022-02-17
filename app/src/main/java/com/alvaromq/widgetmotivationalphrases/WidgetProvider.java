package com.alvaromq.widgetmotivationalphrases;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.RemoteViews;

import com.alvaromq.widgetmotivationalphrases.database.DbHelper;

/**
 * Implementation of App Widget functionality.
 */
public class WidgetProvider extends AppWidgetProvider {

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

        views.setOnClickPendingIntent(R.id.appwidget_text, pendingIntent);


        Intent intentRefresh = new Intent(context, WidgetProvider.class);
        intentRefresh.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        PendingIntent pendingSync = PendingIntent.getBroadcast(context,0, intentRefresh, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.btnRefresh,pendingSync);

        // Instruct the widget manager to update the widget
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
        }
    }
}