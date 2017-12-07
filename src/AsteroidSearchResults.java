package com.asteroidserviceapp;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.SimpleCursorAdapter;


public class AsteroidSearchResults extends ListActivity implements
	LoaderManager.LoaderCallbacks<Cursor>{
	
	private SimpleCursorAdapter adapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	   super.onCreate(savedInstanceState);
	   
	   
	    // Create a new adapter and bind it to the List View
	    adapter = new SimpleCursorAdapter(this,
	      android.R.layout.simple_list_item_1, null,
	      new String[] { AsteroidProvider.KEY_NAME },
	      new int[] { android.R.id.text1 }, 0);
	    setListAdapter(adapter);
	    
	    // Initiate the Cursor Loader
	    getLoaderManager().initLoader(0, null, this);
	    
	    // Get the launch Intent
	    parseIntent(getIntent());

	}
	
	@Override
	protected void onNewIntent(Intent intent) {
	   super.onNewIntent(intent);
	   parseIntent(getIntent());
	}

	
	private static String QUERY_EXTRA_KEY = "QUERY_EXTRA_KEY";
		
	private void parseIntent(Intent intent) {
	  // If the Activity was started to service a Search request,
	  // extract the search query.
	  if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
	    String searchQuery = intent.getStringExtra(SearchManager.QUERY);

	  // Perform the search, passing in the search query as an argument
	  // to the Cursor Loader
	  Bundle args = new Bundle();
	  args.putString(QUERY_EXTRA_KEY, searchQuery);
	      
	  // Restart the Cursor Loader to execute the new query.
	  getLoaderManager().restartLoader(0, args, this);
	  }

		
	}

	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
	  String query =  "0";
	    
	  if (args != null) {
	      // Extract the search query from the arguments.
	      query = args.getString(QUERY_EXTRA_KEY);
	   }
	  // Construct the new query in the form of a Cursor Loader.
	  String[] projection = { AsteroidProvider.KEY_ID,
	      AsteroidProvider.KEY_NAME };
	  String where = AsteroidProvider.KEY_NAME
	                     + " LIKE \"%" + query + "%\"";
	  String[] whereArgs = null;
	  String sortOrder = AsteroidProvider.KEY_NAME + " COLLATE LOCALIZED ASC";

	  // Create the new Cursor loader.
	  return new CursorLoader(this, AsteroidProvider.CONTENT_URI,
	           projection, where, whereArgs,
	           sortOrder);
	}
   
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
	    // Replace the result Cursor displayed by the Cursor Adapter with
	    // the new result set.
	    adapter.swapCursor(cursor);
	}

	public void onLoaderReset(Loader<Cursor> loader) {
	    // Remove the existing result Cursor from the List Adapter.
	    adapter.swapCursor(null);
	}

	

}
