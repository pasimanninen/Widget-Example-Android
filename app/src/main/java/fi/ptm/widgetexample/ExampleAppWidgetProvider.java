package fi.ptm.widgetexample;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.widget.RemoteViews;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by pasi on 14/10/15.
 */
public class ExampleAppWidgetProvider extends AppWidgetProvider {
    private final String REFRESH = "fi.ptm.widgetexample.REFRESH";
    static final String PREF_TEXT_COLOR  = "backgroundcolor";

    // called to update the App Widget at intervals defined by the updatePeriodMillis
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d("EXAMPLE WIDGET", "Update: " + appWidgetIds[0]);
        // get widget layout
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.example_appwidget);

        // start timer to update widget - uncomment to test timer based update
        //Timer timer = new Timer();
        //timer.scheduleAtFixedRate(new MyTime(context, appWidgetManager), 1, 1000);

        // update all widget instances
        for (int i=0;i<appWidgetIds.length;i++) {
            // A) add intent to start main activity
            Intent activityIntent = new Intent(context, MainActivity.class);
            activityIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent activityPendIntent = PendingIntent.getActivity(context, appWidgetIds[i], activityIntent, 0);
            views.setOnClickPendingIntent(R.id.activityButton, activityPendIntent);

            // R) add refresh intent
            Intent refreshIntent = new Intent(REFRESH);
            PendingIntent refreshPendIntent = PendingIntent.getBroadcast(context, 0, refreshIntent, 0);
            views.setOnClickPendingIntent(R.id.refreshButton, refreshPendIntent);

            // C) add intent to start configure activity
            Intent configureIntent = new Intent(context, ConfigureActivity.class);
            configureIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            configureIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent configurePendIntent = PendingIntent.getActivity(context, appWidgetIds[i], configureIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.configureButton, configurePendIntent);

            // check text colors for textviews and show time
            SharedPreferences sharedPreferences = ExampleAppWidgetProvider.getSharedPreferencesForAppWidget(context, appWidgetIds[i]);
            int textColor = sharedPreferences.getInt(PREF_TEXT_COLOR, Color.WHITE);
            views.setTextColor(R.id.date, textColor);
            views.setTextColor(R.id.time, textColor);
            showDateAndTime(views);

            // update widget instance
            appWidgetManager.updateAppWidget(appWidgetIds[i], views);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v("EXAMPLE WIDGET", "action=" + intent.getAction());
        if (REFRESH.equals(intent.getAction())) {
            // show date and time
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.example_appwidget);
            showDateAndTime(remoteViews);
            // update view
            ComponentName thisWidget = new ComponentName(context, ExampleAppWidgetProvider.class);
            AppWidgetManager manager = AppWidgetManager.getInstance(context);
            manager.updateAppWidget(thisWidget, remoteViews);
        }
        super.onReceive(context, intent);
    }

    // method shows date and time in TextViews
    private void showDateAndTime(RemoteViews views) {
        // date and time formats
        DateFormat timeFormat = SimpleDateFormat.getTimeInstance(SimpleDateFormat.MEDIUM, Locale.getDefault());
        DateFormat dateFormat = SimpleDateFormat.getDateInstance(SimpleDateFormat.MEDIUM,Locale.getDefault());
        views.setTextViewText(R.id.date, "date:" + dateFormat.format(new Date()));
        views.setTextViewText(R.id.time, "time:" + timeFormat.format(new Date()));
    }

    // method returns unique shared preferences to each app widget id's
    public static SharedPreferences getSharedPreferencesForAppWidget(Context context, int appWidgetId) {
        String name = context.getPackageName() + "_preferences_" + appWidgetId;
        return context.getSharedPreferences(name,0);
    }

    // update with own timer
    private class MyTime extends TimerTask {
        RemoteViews remoteViews;
        AppWidgetManager appWidgetManager;
        ComponentName thisWidget;
        DateFormat timeFormat = SimpleDateFormat.getTimeInstance(SimpleDateFormat.MEDIUM, Locale.getDefault());
        DateFormat dateFormat = SimpleDateFormat.getDateInstance(SimpleDateFormat.MEDIUM,Locale.getDefault());

        public MyTime(Context context, AppWidgetManager appWidgetManager) {
            this.appWidgetManager = appWidgetManager;
            remoteViews = new RemoteViews(context.getPackageName(), R.layout.example_appwidget);
            thisWidget = new ComponentName(context, ExampleAppWidgetProvider.class);
        }

        @Override
        public void run() {
            showDateAndTime(remoteViews);
            appWidgetManager.updateAppWidget(thisWidget, remoteViews);
        }
    }
}
