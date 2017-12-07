package com.asteroidserviceapp;

import android.app.DialogFragment;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.util.Date;


public class AsteroidListFragment extends ListFragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    SimpleCursorAdapter adapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Create a new Adapter and bind it to the List View
        adapter = new SimpleCursorAdapter(getActivity(),
                android.R.layout.simple_list_item_1, null,
                new String[] { AsteroidProvider.KEY_NAME },
                new int[] { android.R.id.text1 }, 0);
        setListAdapter(adapter);

        getLoaderManager().initLoader(0, null, this);

        refreshAsteroids();

    }


    private static final String TAG = "EARTHQUAKE";
    private Handler handler = new Handler();

    public void refreshAsteroids() {

        handler.post(new Runnable() {
            public void run() {
                getLoaderManager().restartLoader(0, null, AsteroidListFragment.this);
            }
        });

        getActivity().startService(new Intent(getActivity(),
                AsteroidUpdateService.class));

    }

    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = new String[] {
                AsteroidProvider.KEY_ID,
                AsteroidProvider.KEY_NAME
        };

        MainActivity asteroidActivity = (MainActivity)getActivity();
        String where = AsteroidProvider.KEY_MAGNITUDE + " > " +
                asteroidActivity.minimumMagnitude;

        CursorLoader loader = new CursorLoader(getActivity(),
                AsteroidProvider.CONTENT_URI, projection, where, null, null);

        return loader;
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        adapter.swapCursor(cursor);
    }

    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        ContentResolver cr = getActivity().getContentResolver();

        Cursor result =
                cr.query(ContentUris.withAppendedId(AsteroidProvider.CONTENT_URI, id),
                        null, null, null, null);

        if (result.moveToFirst()) {

            String name =
                    result.getString(
                            result.getColumnIndex(AsteroidProvider.KEY_NAME));

            int magnitude =
                    result.getInt(
                            result.getColumnIndex(AsteroidProvider.KEY_MAGNITUDE));

            double diameter =
                    result.getDouble(
                            result.getColumnIndex(AsteroidProvider.KEY_DIAMETER));

            int miss_dist =
                    result.getInt(
                            result.getColumnIndex(AsteroidProvider.KEY_MISS_DIST));


            Asteroid asteroid = new Asteroid(name, magnitude, diameter, miss_dist);

            DialogFragment newFragment = AsteroidDialog.newInstance(getActivity(), asteroid);
            newFragment.show(getFragmentManager(), "dialog");
        }
    }



}
