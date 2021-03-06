package com.asteroidserviceapp;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.LiveFolders;
import android.text.TextUtils;
import android.util.Log;

import java.util.HashMap;


@SuppressWarnings("deprecation")
public class AsteroidProvider extends ContentProvider {
	
	public static final Uri CONTENT_URI =
		      Uri.parse("content://com.asteroidprovider/asteroids");
	
	public static final Uri LIVE_FOLDER_URI =
		      Uri.parse("content://com.provider.asteroid/live_folder");
	
	
	
	//Column Names
	public static final String KEY_ID = "_id";
	public static final String KEY_NAME = "name";
	public static final String KEY_MAGNITUDE = "magnitude";
	public static final String KEY_DIAMETER = "diameter";
	public static final String KEY_MISS_DIST = "miss_distance";

	AsteroidDatabaseHelper dbHelper;
	@Override
	  public boolean onCreate() {
	    Context context = getContext();

	    dbHelper = new AsteroidDatabaseHelper(context,
	      AsteroidDatabaseHelper.DATABASE_NAME, null,
	      AsteroidDatabaseHelper.DATABASE_VERSION);

	    return true;
	  }


    private static final HashMap<String, String> SEARCH_PROJECTION_MAP;
	  static {
	    SEARCH_PROJECTION_MAP = new HashMap<String, String>();
	    SEARCH_PROJECTION_MAP.put(SearchManager.SUGGEST_COLUMN_TEXT_1, KEY_ID +
	      " AS " + SearchManager.SUGGEST_COLUMN_TEXT_1);
	    SEARCH_PROJECTION_MAP.put("_id", KEY_ID +
	      " AS " + "_id");
	  }
	  
	  
	static final HashMap<String, String> LIVE_FOLDER_PROJECTION;
	  static {
	    LIVE_FOLDER_PROJECTION = new HashMap<String, String>();
	    LIVE_FOLDER_PROJECTION.put(LiveFolders._ID,
	                               KEY_ID + " AS " + LiveFolders._ID);
	    LIVE_FOLDER_PROJECTION.put(LiveFolders.NAME,
	                               KEY_MAGNITUDE + " AS " + LiveFolders.NAME);
	    LIVE_FOLDER_PROJECTION.put(LiveFolders.DESCRIPTION,
	                               KEY_DIAMETER + " AS " + LiveFolders.DESCRIPTION);
	  }

	
	//Create the constants used to differentiate between the different URI
	//requests.
	private static final int ASTEROIDS = 1;
	private static final int ASTEROID_ID = 2;
	private static final int SEARCH = 3;
	private static final int LIVE_FOLDER = 4;
	  
	private static final UriMatcher uriMatcher;
	  
	//Allocate the UriMatcher object, where a URI ending in 'ateroids' will
	//correspond to a request for all asteroids, and 'asteroids' with a
	//trailing '/[rowID]' will represent a single asteroid row.
	static {
	 uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	 uriMatcher.addURI("com.asteroidprovider", "asteroids", ASTEROIDS);
	 uriMatcher.addURI("com.asteroidprovider", "asteroids/#", ASTEROID_ID);
	 uriMatcher.addURI("com.provider.asteroid", "live_folder", LIVE_FOLDER);
	 uriMatcher.addURI("com.asteroidprovider",
		SearchManager.SUGGEST_URI_PATH_QUERY, SEARCH);
     uriMatcher.addURI("com.asteroidprovider",
		SearchManager.SUGGEST_URI_PATH_QUERY + "/*", SEARCH);
     uriMatcher.addURI("com.asteroidprovider",
		SearchManager.SUGGEST_URI_PATH_SHORTCUT, SEARCH);
     uriMatcher.addURI("com.asteroidprovider",
		SearchManager.SUGGEST_URI_PATH_SHORTCUT + "/*", SEARCH);

     
	 
	}
	  
	@Override
	public String getType(Uri uri) {
	 switch (uriMatcher.match(uri)) {
	   case ASTEROIDS|LIVE_FOLDER: return "vnd.android.cursor.dir/vnd.asteroid";
	 //case QUAKES: return "vnd.android.cursor.dir/vnd.asteroid";
	   case ASTEROID_ID: return "vnd.android.cursor.item/vnd.asteroid";
	   case SEARCH: return SearchManager.SUGGEST_MIME_TYPE;
	   
	   default: throw new IllegalArgumentException("Unsupported URI: " + uri);
	 }
	}
 
   @Override
   public Cursor query(Uri uri,
                       String[] projection,
                       String selection,
                       String[] selectionArgs,
                       String sort) {

	   SQLiteDatabase database = dbHelper.getWritableDatabase();

	   SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

	   qb.setTables(AsteroidDatabaseHelper.ASTEROID_TABLE);

	   // If this is a row query, limit the result set to the passed in row.
	   switch (uriMatcher.match(uri)) {
	   case ASTEROID_ID: qb.appendWhere(KEY_ID + "=" + uri.getPathSegments().get(1));
	   				  break;
	   case SEARCH:   qb.appendWhere(KEY_MAGNITUDE + " LIKE \"%" +
                         uri.getPathSegments().get(1) + "%\"");
                         qb.setProjectionMap(SEARCH_PROJECTION_MAP);
                      break;
	   case LIVE_FOLDER : qb.setProjectionMap(LIVE_FOLDER_PROJECTION);
	   				  break;
	   				  
	   default      : break;
	 }

	 // If no sort order is specified, sort by magnitude
	 String orderBy;
	 if (TextUtils.isEmpty(sort)) {
		 orderBy = KEY_MAGNITUDE;
	 } else {
		 orderBy = sort;
	 }

	 // Apply the query to the underlying database.
	 Cursor c = qb.query(database,
			 			 projection,
			 			 selection, selectionArgs,
			 			 null, null,
			 			 orderBy);

	 // Register the contexts ContentResolver to be notified if
	 // the cursor result set changes.
	 c.setNotificationUri(getContext().getContentResolver(), uri);

	 // Return a cursor to the query result.
	 return c;
   }

   
   @Override
   public Uri insert(Uri _uri, ContentValues _initialValues) {
     SQLiteDatabase database = dbHelper.getWritableDatabase();
     
     // Insert the new row. The call to database.insert will return the row number
     // if it is successful.
     long rowID = database.insert(
       AsteroidDatabaseHelper.ASTEROID_TABLE, "asteroid", _initialValues);

     // Return a URI to the newly inserted row on success.
     if (rowID > 0) {
       Uri uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
       getContext().getContentResolver().notifyChange(uri, null);
       return uri;
     }
     
     throw new SQLException("Failed to insert row into " + _uri);
   }

   
   @Override
   public int delete(Uri uri, String where, String[] whereArgs) {
     SQLiteDatabase database = dbHelper.getWritableDatabase();
     
     int count;
     switch (uriMatcher.match(uri)) {
       case ASTEROIDS:
         count = database.delete(
           AsteroidDatabaseHelper.ASTEROID_TABLE, where, whereArgs);
         break;
       case ASTEROID_ID:
         String segment = uri.getPathSegments().get(1);
         count = database.delete(AsteroidDatabaseHelper.ASTEROID_TABLE,
                 KEY_ID + "=" 
                 + segment
                 + (!TextUtils.isEmpty(where) ? " AND ("
                 + where + ')' : ""), whereArgs);
         break;

       default: throw new IllegalArgumentException("Unsupported URI: " + uri);
     }

     getContext().getContentResolver().notifyChange(uri, null);
     return count;
   }

  

   @Override
   public int update(Uri uri, ContentValues values,
                     String where, String[] whereArgs) {
     SQLiteDatabase database = dbHelper.getWritableDatabase();
     
     int count;
     switch (uriMatcher.match(uri)) {
       case ASTEROIDS:
         count = database.update(AsteroidDatabaseHelper.ASTEROID_TABLE,
                                 values, where, whereArgs);
         break;
       case ASTEROID_ID:
         String segment = uri.getPathSegments().get(1);
         count = database.update(AsteroidDatabaseHelper.ASTEROID_TABLE,
                                 values, KEY_ID
                                   + "=" + segment
                                   + (!TextUtils.isEmpty(where) ? " AND ("
                                   + where + ')' : ""), whereArgs);
         break;
       default: throw new IllegalArgumentException("Unknown URI " + uri);
     }

     getContext().getContentResolver().notifyChange(uri, null);
     return count;
   }

   
	
	
   //Helper class for opening, creating, and managing database version control 
   private static class AsteroidDatabaseHelper extends SQLiteOpenHelper {
	  
	  private static final String TAG = "AsteroidProvider";
	  
	  private static final String DATABASE_NAME = "asteroids.db";
      private static final int DATABASE_VERSION = 1;
	  private static final String ASTEROID_TABLE = "asteroids";
	  
	  private static final String DATABASE_CREATE =
	    "create table " + ASTEROID_TABLE + " ("
	    + KEY_ID + " integer primary key autoincrement, "
	    + KEY_NAME + " TEXT, "
	    + KEY_DIAMETER + " FLOAT, "
	    + KEY_MISS_DIST + " INTEGER, "
	    + KEY_MAGNITUDE + " INTEGER);";
	  
	  // The underlying database
	  private SQLiteDatabase earthquakeDB;
	  
	  public AsteroidDatabaseHelper(Context context, String name,
                                      CursorFactory factory, int version) {
	    super(context, name, factory, version);
	  }
	  
	  @Override
	  public void onCreate(SQLiteDatabase db) {
	    db.execSQL(DATABASE_CREATE);
	  }
	  
	  @Override
	  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	    Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
	               + newVersion + ", which will destroy all old data");
	   
	    db.execSQL("DROP TABLE IF EXISTS " + ASTEROID_TABLE);
	    onCreate(db);
	  }
   }
}
