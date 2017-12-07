package com.asteroidserviceapp;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;


public class AsteroidDialog extends DialogFragment {
	
   private static String DIALOG_STRING = "DIALOG_STRING";
   
   public static AsteroidDialog newInstance(Context context, Asteroid asteroid) {
	    
	    // Create a new Fragment instance with the specified 
	    // parameters.
	    AsteroidDialog fragment = new AsteroidDialog();
	    Bundle args = new Bundle();

	    String asteroidText = "Name: "+ asteroid.getName() + "\n" + "Magnitude " + asteroid.getMagnitude() +
	                       "\n" + asteroid.getDiameter() + "\n" +
	                       asteroid.getMiss_distance();

	    
	    args.putString(DIALOG_STRING, asteroidText);
	    fragment.setArguments(args);


	    return fragment;
   }

   
   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState) {
     
     View view = inflater.inflate(R.layout.asteroid_details, container, false);

     String title = getArguments().getString(DIALOG_STRING);
     TextView tv = (TextView)view.findViewById(R.id.asteroidDetailsTextView);
     tv.setText(title);
   
     return view;
   }
   
   @Override
   public Dialog onCreateDialog(Bundle savedInstanceState) {
     Dialog dialog = super.onCreateDialog(savedInstanceState);
     dialog.setTitle("Asteroid Details");
     return dialog;
   }


}
