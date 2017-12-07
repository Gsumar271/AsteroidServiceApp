package com.eugenesumaryev.myapipractice02;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AsteroidAlarmReceiver extends BroadcastReceiver {
	
	public static final String ACTION_REFRESH_ASTEROID_ALARM =
		      "com.asteroid.ACTION_REFRESH_ASTEROID_ALARM";
	
	@Override
	public void onReceive(Context context, Intent intent) {
      Intent startIntent = new Intent(context, AsteroidUpdateService.class);
      context.startService(startIntent);
    }

}
