package com.eugenesumaryev.myapipractice02;

import android.os.Bundle;
import android.preference.PreferenceActivity;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Switch;


/*
public class PreferencesActivity extends PreferenceActivity {
	

	  public static final String PREF_AUTO_UPDATE = "PREF_AUTO_UPDATE";
	  public static final String PREF_MIN_MAG = "PREF_MIN_MAG";
	  public static final String PREF_UPDATE_FREQ = "PREF_UPDATE_FREQ";
      public static final String USER_PREFERENCE = "USER_PREFERENCE";

      public static final String PREF_MIN_MAG_INDEX = "PREF_MIN_MAG_INDEX";
      public static final String PREF_UPDATE_FREQ_INDEX = "PREF_UPDATE_FREQ_INDEX";

 
	
	@SuppressWarnings("deprecation")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    addPreferencesFromResource(R.xml.userpreferences);


		
}
    
 
}
*/



 public class PreferencesActivity extends Activity {
	
	CheckBox autoUpdate;
	Spinner updateFreqSpinner;
	Spinner magnitudeSpinner;	
	
	public static final String USER_PREFERENCE = "USER_PREFERENCE";
	public static final String PREF_AUTO_UPDATE = "PREF_AUTO_UPDATE";
	public static final String PREF_MIN_MAG_INDEX = "PREF_MIN_MAG_INDEX";
    public static final String PREF_UPDATE_FREQ_INDEX = "PREF_UPDATE_FREQ_INDEX";

    SharedPreferences prefs;


	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.preferences);
      //  addPreferencesFromResource(R.layout.preferences);


	    updateFreqSpinner = (Spinner)findViewById(R.id.spinner_update_freq);
	    magnitudeSpinner = (Spinner)findViewById(R.id.spinner_quake_mag);
	    autoUpdate = (CheckBox)findViewById(R.id.checkbox_auto_update);

        Log.v("PreferenceActivity:", "Preferences" );
	
	populateSpinners();
	
	Context context = getApplicationContext();
    prefs = PreferenceManager.getDefaultSharedPreferences(context);
    updateUIFromPreferences();
    
    Button okButton = (Button) findViewById(R.id.okButton);
    okButton.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {
        savePreferences();
        PreferencesActivity.this.setResult(RESULT_OK);
        finish();
      }
    });

    Button cancelButton = (Button) findViewById(R.id.cancelButton);
    cancelButton.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {
        PreferencesActivity.this.setResult(RESULT_CANCELED);
        finish();
      }
    });
	

	}
    


private void savePreferences() {
	// int updateIndex = updateFreqSpinner.getSelectedItemPosition();
    // int updateIndex;

    int updateIndex =  updateFreqSpinner.getSelectedItemPosition();


    /* switch(checkUpdateIndex) {
         case 0:
             updateIndex = 1;
             break;
         case 1:
             updateIndex = 5;
             break;
         case 2:
             updateIndex = 10;
             break;
         case 3:
             updateIndex = 15;
             break;
         case 4:
             updateIndex = 60;
             break;
         default:
             updateIndex = 15;
             break;
     }
     */

	 int minMagIndex = magnitudeSpinner.getSelectedItemPosition();
    // int minMagIndex = 3;
	 boolean autoUpdateChecked = autoUpdate.isChecked();

	 Editor editor = prefs.edit();
	 editor.putBoolean(PREF_AUTO_UPDATE, autoUpdateChecked);
	 editor.putInt(PREF_UPDATE_FREQ_INDEX, updateIndex);
	 editor.putInt(PREF_MIN_MAG_INDEX, minMagIndex);

    // Log.v("savepreferencescheck:", String.valueOf(autoUpdateChecked) );
   //  Log.v("updatefrmprferencindx:", String.valueOf(updateIndex) );
    // Log.v("updtfrmprefernsmimg:", String.valueOf(minMagIndex) );

	 editor.commit();
	
    }
 
 

    private void updateUIFromPreferences() {
	    boolean autoUpChecked = prefs.getBoolean(PREF_AUTO_UPDATE, false);
	    int updateFreqIndex = prefs.getInt(PREF_UPDATE_FREQ_INDEX, 2);
	    int minMagIndex = prefs.getInt(PREF_MIN_MAG_INDEX, 0);

	    updateFreqSpinner.setSelection(updateFreqIndex);
	    magnitudeSpinner.setSelection(minMagIndex);
	    autoUpdate.setChecked(autoUpChecked);
	  }

	
	private void populateSpinners() { 
		// Populate the update frequency spinner
	    ArrayAdapter<CharSequence> fAdapter;
	    fAdapter = ArrayAdapter.createFromResource(this, R.array.update_freq_options,
	                                             android.R.layout.simple_spinner_item);
	    int spinner_dd_item = android.R.layout.simple_spinner_dropdown_item;
	    fAdapter.setDropDownViewResource(spinner_dd_item);
	    updateFreqSpinner.setAdapter(fAdapter);
	    // Populate the minimum magnitude spinner
	    ArrayAdapter<CharSequence> mAdapter;
	    mAdapter = ArrayAdapter.createFromResource(this,
	      R.array.magnitude_options,
	      android.R.layout.simple_spinner_item);
	    mAdapter.setDropDownViewResource(spinner_dd_item);
	    magnitudeSpinner.setAdapter(mAdapter);
	}
}


