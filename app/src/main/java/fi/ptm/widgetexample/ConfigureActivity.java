package fi.ptm.widgetexample;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RemoteViews;
import android.widget.Toast;

/**
 * Created by pasi on 14/10/15.
 */
public class ConfigureActivity extends Activity {
    private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private int textColor = Color.WHITE;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the view layout resource to use.
        setContentView(R.layout.activity_configure);

        // Find the widget id from the intent.
        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                mAppWidgetId = bundle.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
            }
        }

        // If they gave us an intent without the widget id, just bail
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            Toast.makeText(getBaseContext(), "INVALID_APPWIDGET_ID = " + mAppWidgetId, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    // called from button, save selected color to shared preferences
    public void saveTextColor(View view) {
        // save selected color to preferences
        SharedPreferences sharedPreferences = ExampleAppWidgetProvider.getSharedPreferencesForAppWidget(getApplicationContext(), mAppWidgetId);
        SharedPreferences.Editor prefs = sharedPreferences.edit();
        prefs.putInt(ExampleAppWidgetProvider.PREF_TEXT_COLOR, textColor);
        prefs.commit();
        // configure widget
        Context context = getApplicationContext();
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        RemoteViews views = new RemoteViews(context.getPackageName(),R.layout.example_appwidget);
        views.setTextColor(R.id.date, textColor);
        views.setTextColor(R.id.time, textColor);
        appWidgetManager.updateAppWidget(mAppWidgetId, views);
        // close activity
        finish();
    }

    // following method handles the click event for radio buttons
    public void onRadioButtonClicked(View view) {
        // Is the button now checked
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radioWhite:
                if (checked) textColor = Color.WHITE;
                break;
            case R.id.radioRed:
                if (checked) textColor = Color.RED;
                break;
            case R.id.radioGreen:
                if (checked) textColor = Color.GREEN;
                break;
            case R.id.radioBlue:
                if (checked) textColor = Color.BLUE;
                break;
        }
    }
}
