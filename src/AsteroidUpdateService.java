package com.eugenesumaryev.myapipractice02;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


public class AsteroidUpdateService extends IntentService {
	

    public static String TAG = "ASTEROID_UPDATE_SERVICE";
    
    private Notification.Builder asteroidNotificationBuilder;
    public static final int NOTIFICATION_ID = 1;


    public AsteroidUpdateService() {
        super("AsteroidUpdateService");
    }

    public AsteroidUpdateService(String name) {
        super(name);
    }

    
    public static String ASTEROIDS_REFRESHED =
    	      "com.asteroid.ASTEROIDS_REFRESHED";
    

    @Override
    protected void onHandleIntent(Intent intent) {
      refreshAsteroids();
      sendBroadcast(new Intent(ASTEROIDS_REFRESHED));
      

      Context context = getApplicationContext();
      AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
      ComponentName earthquakeWidget =
        new ComponentName(context, AsteroidListWidget.class);
      int[] appWidgetIds = appWidgetManager.getAppWidgetIds(earthquakeWidget);
     
      appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds,
        R.id.widget_list_view);
    }

	
	@Override
	public IBinder onBind(Intent intent){
		return null;
	}
	
	private void addNewAsteroid(Asteroid _asteroid) {
	    ContentResolver cr = getContentResolver();

	    // Construct a where clause to make sure we don't already have this
	    // asteroid in the provider.
	    String w = AsteroidProvider.KEY_NAME + " = " + _asteroid.getName();

	    // If the asteroid is new, insert it into the provider.
	    Cursor query = cr.query(AsteroidProvider.CONTENT_URI, null, w, null, null);
	    
	    if (query.getCount()==0) {
	      ContentValues values = new ContentValues();

	      values.put(AsteroidProvider.KEY_NAME, _asteroid.getName());
	      values.put(AsteroidProvider.KEY_DIAMETER, _asteroid.getDiameter());
	      values.put(AsteroidProvider.KEY_MAGNITUDE, _asteroid.getMagnitude());
	      values.put(AsteroidProvider.KEY_MISS_DIST, _asteroid.getMiss_distance());

			// Trigger a notification.
	      broadcastNotification(_asteroid);
	      
	      // Add the new quake to the Earthquake provider.
	      cr.insert(AsteroidProvider.CONTENT_URI, values);
	    }
	    query.close();
	  }


	public void refreshAsteroids() {

		int objCount;

		URL url;
		try {
			String asteroidFeed = getString(R.string.asteroid_feed);
			url = new URL(asteroidFeed);

			URLConnection connection;
			connection = url.openConnection();

			HttpURLConnection httpConnection = (HttpURLConnection) connection;
			int responseCode = httpConnection.getResponseCode();

			if (responseCode == HttpURLConnection.HTTP_OK) {
				InputStream in = httpConnection.getInputStream();

				// Parse the asteroid feed.
				JSNParser parser = new JSNParser();
				JSONObject obj = (JSONObject) parser.readJSONObject(in);


				// Clear the old earthquakes
				//  asteroids.clear();

				// Get a list of each asteroid entry.
				JSONObject neo = (JSONObject) obj.get("near_earth_objects");


				//get all the necessary dates
				Iterator<String> allDates = neo.keys();

				while (allDates.hasNext()){
					//get each individual date
					JSONArray date = (JSONArray)neo.get(allDates.next());

					//for each item in each date
					for (objCount = 0; objCount < date.length(); objCount++){

						JSONObject asteroidObject = (JSONObject) date.get(objCount);


						String _id = asteroidObject.getString("neo_reference_id");
						int mag = asteroidObject.getInt("absolute_magnitude_h");

						JSONObject diamObject = (JSONObject)asteroidObject.get("estimated_diameter");
						JSONObject milesObject = (JSONObject)diamObject.get( "miles");
						double diameter = milesObject.getDouble("estimated_diameter_min");

						JSONArray close_data = (JSONArray) asteroidObject.get("close_approach_data");
						JSONObject close_data_obj= (JSONObject) close_data.get(0);
						JSONObject missObject = (JSONObject)close_data_obj.get("miss_distance");
						int miss_distance = missObject.getInt("miles");


						final Asteroid asteroid = new Asteroid(_id, mag, diameter, miss_distance);

						addNewAsteroid(asteroid);
					}

				}

			}

		} catch (MalformedURLException e) {
			Log.d(TAG, "MalformedURLException", e);
		} catch (IOException e) {
			Log.d(TAG, "IOException", e);
		} catch (JSONException e) {
			Log.e(TAG, "JSON Exception", e);
		} finally {
		}
	}


	  private AlarmManager alarmManager;
	  private PendingIntent alarmIntent;

	  
	  @Override
	  public void onCreate() {
	    /*
	     * updateTimer = new Timer("earthquakeUpdates");
	     * */
		super.onCreate();
		  alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

		  String ALARM_ACTION =
	         AsteroidAlarmReceiver.ACTION_REFRESH_ASTEROID_ALARM;
		  Intent intentToFire = new Intent(ALARM_ACTION);
		  alarmIntent =
			 PendingIntent.getBroadcast(this, 0, intentToFire, 0);
		  
		  asteroidNotificationBuilder = new Notification.Builder(this);
		  asteroidNotificationBuilder
		     .setAutoCancel(true)
		     .setTicker("Asteroid detected")
		     .setSmallIcon(R.drawable.notification_icon);


	  
	  }
	  
	  
	 
	private void broadcastNotification(Asteroid _asteroid) {
		Intent startActivityIntent = new Intent(this, MainActivity.class);
	    PendingIntent launchIntent =
	    		PendingIntent.getActivity(this, 0, startActivityIntent, 0);

		asteroidNotificationBuilder
		    .setContentIntent(launchIntent)
		    .setWhen(System.currentTimeMillis())
            .setContentTitle("M:" + _asteroid.getMagnitude())
    	    .setContentText("D: " + _asteroid.getDiameter());
		
		if (_asteroid.getMagnitude() > 6) {
		   Uri ringURI =
		     RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

	      asteroidNotificationBuilder.setSound(ringURI);
		}
		
		double vibrateLength = 100* Math.exp(0.53*_asteroid.getMagnitude());
	    long[] vibrate = new long[] {100, 100, (long)vibrateLength };
	    asteroidNotificationBuilder.setVibrate(vibrate);
	    
	    int color;
	    if (_asteroid.getMagnitude() < 5.4)
	      color = Color.GREEN;
	    else if (_asteroid.getMagnitude() < 6)
	      color = Color.YELLOW;
	    else
	      color = Color.RED;

	    asteroidNotificationBuilder.setLights(
	      color, 
	      (int)vibrateLength, 
	      (int)vibrateLength);
		
		NotificationManager notificationManager
	      = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

	    notificationManager.notify(NOTIFICATION_ID,
	      asteroidNotificationBuilder.getNotification());
	    
	    
	  
	  }



}
