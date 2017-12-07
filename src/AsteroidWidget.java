package com.asteroidserviceapp;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;

public class AsteroidWidget extends AppWidgetProvider {
	
	public void updateAsteroid(Context context, AppWidgetManager appWidgetManager,
                            int[] appWidgetIds) {
		
		Cursor lastAsteroid;
	    ContentResolver cr = context.getContentResolver();
	    lastAsteroid = cr.query(AsteroidProvider.CONTENT_URI,
	              null, null, null, null);
	    
	    String magnitude = "--";
	    String details = "-- None --";
	    
	    if (lastAsteroid != null) {
	      try {
	        if (lastAsteroid.moveToFirst()) {
	          int magColumn = lastAsteroid.getColumnIndexOrThrow(AsteroidProvider.KEY_MAGNITUDE);
	          int detailsColumn = lastAsteroid.getColumnIndexOrThrow(AsteroidProvider.KEY_NAME);
	          
	          magnitude = lastAsteroid.getString(magColumn);
	          details = lastAsteroid.getString(detailsColumn);
	        }
	      }
	      finally {
	        lastAsteroid.close();
	      }
	    }
	    
	    final int N = appWidgetIds.length;
	    for (int i = 0; i < N; i++) {
	      int appWidgetId = appWidgetIds[i];
	      RemoteViews views = new RemoteViews(context.getPackageName(),
	                                          R.layout.asteroid_widget);
	      views.setTextViewText(R.id.widget_magnitude, magnitude);
	      views.setTextViewText(R.id.widget_details, details);
	      appWidgetManager.updateAppWidget(appWidgetId, views);
	    }
		
	}
	
	public void updateAsteroid(Context context) {
	    ComponentName thisWidget = new ComponentName(context,
	                                                 AsteroidWidget.class);
	    AppWidgetManager appWidgetManager =
	       AppWidgetManager.getInstance(context);
	    int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
	    updateAsteroid(context, appWidgetManager, appWidgetIds);
	}
	
	@Override
	public void onUpdate(Context context,
	                     AppWidgetManager appWidgetManager,
	                     int[] appWidgetIds) {
		
		// Create a Pending Intent that will open the main Activity.
	    Intent intent = new Intent(context, MainActivity.class);
	    PendingIntent pendingIntent =
	      PendingIntent.getActivity(context, 0, intent, 0);

	    // Apply the On Click Listener to both Text Views.
	    RemoteViews views = new RemoteViews(context.getPackageName(),
	                                        R.layout.asteroid_widget);

	    views.setOnClickPendingIntent(R.id.widget_magnitude, pendingIntent);
	    views.setOnClickPendingIntent(R.id.widget_details, pendingIntent);

	    // Notify the App Widget Manager to update the 
	    appWidgetManager.updateAppWidget(appWidgetIds, views);
		
		
		// Update the Widget UI with the latest Asteroid details.
	    updateAsteroid(context, appWidgetManager, appWidgetIds);
	}
	
	@Override
	public void onReceive(Context context, Intent intent){
		super.onReceive(context, intent);

	   if (AsteroidUpdateService.ASTEROID_REFRESHED.equals(intent.getAction()))
	      updateAsteroid(context);
	}
	
	

}
