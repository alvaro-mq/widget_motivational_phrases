package com.alvaromq.widgetmotivationalphrases;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;

import com.alvaromq.widgetmotivationalphrases.database.Configuration;

public class UpdateService extends IntentService {
    private static String ACTION_EVENT = "ACTION_EVENT";
    /**
     * @param name
     * @deprecated
     */
    public UpdateService(String name) {
        super(name);
    }

    public UpdateService() {
        super("UpdateService");
    }

    public static void startActionUpdateWidget(Context context) {
        Intent intent = new Intent(context, UpdateService.class);
        intent.setAction(ACTION_EVENT);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_EVENT.equals(action)) {
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
                int[] appWidgetId = appWidgetManager.getAppWidgetIds(new ComponentName(this, WidgetProvider.class));
                Log.v("tag", String.valueOf(appWidgetId.length));
                WidgetProvider.updateAppWidgets(this, appWidgetManager, appWidgetId);
            }
        }
    }
}
