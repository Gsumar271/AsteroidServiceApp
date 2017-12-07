package com.eugenesumaryev.myapipractice02;

import android.widget.RemoteViewsService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

public class AsteroidRemoteViewsService extends RemoteViewsService {


    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new EarthquakeRemoteViewsFactory(getApplicationContext());
    }

    class EarthquakeRemoteViewsFactory implements RemoteViewsFactory {

        private Context context;

        public EarthquakeRemoteViewsFactory(Context context) {
            this.context = context;
        }

        private Cursor c;

        private Cursor executeQuery() {
            String[] projection = new String[] {
                    AsteroidProvider.KEY_ID,
                    AsteroidProvider.KEY_MAGNITUDE,
                    AsteroidProvider.KEY_NAME
            };


            Context appContext = getApplicationContext();
            SharedPreferences prefs =
                    PreferenceManager.getDefaultSharedPreferences(appContext);

            int minimumMagnitude =
                    Integer.parseInt(prefs.getString(PreferencesActivity.PREF_MIN_MAG_INDEX, "3"));

            String where = AsteroidProvider.KEY_MAGNITUDE + " > " + minimumMagnitude;

            return context.getContentResolver().query(AsteroidProvider.CONTENT_URI,
                    projection, where, null, null);
        }
        public void onCreate() {
            c = executeQuery();
        }

        public void onDataSetChanged() {
            c = executeQuery();
        }

        public void onDestroy() {
            c.close();
        }

        public int getCount() {
            if (c != null)
                return c.getCount();
            else
                return 0;
        }

        public long getItemId(int index) {
            if (c != null)
                return c.getLong(c.getColumnIndex(AsteroidProvider.KEY_ID));
            else
                return index;
        }

        public RemoteViews getViewAt(int index) {
            // Move the Cursor to the required index.
            c.moveToPosition(index);

            // Extract the values for the current cursor row.
            int idIdx = c.getColumnIndex(AsteroidProvider.KEY_ID);
            int magnitudeIdx = c.getColumnIndex(AsteroidProvider.KEY_MAGNITUDE);
            int detailsIdx = c.getColumnIndex(AsteroidProvider.KEY_NAME);

            String id = c.getString(idIdx);
            String magnitude = c.getString(magnitudeIdx);
            String details = c.getString(detailsIdx);

            // Create a new Remote Views object and use it to populate the
            // layout used to represent each asteroid in the list.
            RemoteViews rv = new RemoteViews(context.getPackageName(),
                    R.layout.asteroid_widget);

            rv.setTextViewText(R.id.widget_magnitude, magnitude);
            rv.setTextViewText(R.id.widget_details, details);

            // Create the fill-in Intent that adds the URI for the current item
            // to the template Intent.
            Intent fillInIntent = new Intent();
            Uri uri = Uri.withAppendedPath(AsteroidProvider.CONTENT_URI, id);
            fillInIntent.setData(uri);

            rv.setOnClickFillInIntent(R.id.widget_magnitude, fillInIntent);
            rv.setOnClickFillInIntent(R.id.widget_details, fillInIntent);

            return rv;
        }


        public int getViewTypeCount() {
            return 1;
        }

        public boolean hasStableIds() {
            return true;
        }

        public RemoteViews getLoadingView() {
            return null;
        }
    }


}
