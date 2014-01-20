package com.example.spin;

import java.io.File;
import java.util.ArrayList;

import com.example.spin.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
//import android.widget.Spinner;


public class MainActivity extends Activity {
	

	private Button mStartButton,downloadButton,deleteButton;
	private boolean mChoosen;
	private String tableName;
	private Database myDatabase;
	private boolean mPressedRB = true, mChoosenSpin = true;
	ArrayList<String> tables = new ArrayList<String>();
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		File f = new File(
				"/data/data/com/example/spin/shared_prefs/MyPref.xml");
				if (f.exists()){
					//do nothing
				}
				else{
					SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
				    Editor editor = pref.edit();
				    editor.putString("token", null); // Storing string
					editor.putLong("expiry", 0); // Storing long
					editor.commit(); // commit changes
				}
		
		myDatabase = new Database(MainActivity.this);
		
		/*myDatabase.defineTable("Test"); //here you can see how to define table
		SQLitem item = new SQLitem("What is the biggest land by region", "Russia", 1); //here you can see how to define a row
		MainActivity.this.myDatabase.addItem(item);*/ //here you can see how to insert a row into defined table
				Cursor c = myDatabase.showAllTables();
				if (c.moveToFirst())
		        {
					c.moveToNext();
					while(!c.isAfterLast()){
			           tables.add(c.getString(0));
			           c.moveToNext();
			        }
		        }
		       if (tables.size() == 0)
		        {
		            tables.add("Download sets");
		        }
		Spinner spinner = (Spinner) findViewById(R.id.spinnerCategory);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, 
                android.R.layout.simple_spinner_item,
                tables );
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		downloadButton=(Button)findViewById(R.id.downloadButton);
		downloadButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, CramFetcher.class);
				startActivity(intent);
			}
		});
		mStartButton = (Button)findViewById(R.id.startButton); //setting reference for the "START" button
		mStartButton.setOnClickListener(new View.OnClickListener(){ //creating a listener object
			
			@Override
			public void onClick(View v){
				if(mPressedRB && mChoosenSpin){
					Intent intent = new Intent(MainActivity.this, Flashcard.class);
					startActivity(intent);
					}
				else
					Toast.makeText(MainActivity.this, R.string.toast_text, Toast.LENGTH_LONG).show();
				}
		});
		deleteButton=(Button)findViewById(R.id.deleteButton);
		deleteButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, Delete_sets.class);
				startActivity(intent);
			}
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		//saved token
	    SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
	    Editor editor = pref.edit();
		super.onDestroy();
		editor.remove("token"); // will delete key name
		editor.remove("expiry"); // will delete key email
		editor.commit(); // commit changes
	}
	
}
