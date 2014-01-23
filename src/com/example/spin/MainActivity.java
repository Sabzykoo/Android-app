package com.example.spin;

import java.util.ArrayList;

import com.example.spin.R;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


public class MainActivity extends Activity {
	
	private EditText mMinEditText, mMaxEditText;
	private Button mStartButton, downloadButton, deleteButton;

	private Database myDatabase;
	private boolean mChosenSpin = false;
	private String mTable;
	ArrayList<String> tables = new ArrayList<String>();
	
	private int mStartPoint;
	private int mEndPoint;
	String minimum = "Not defined";
	String maximum = "Not defined";
	MediaPlayer mp;
	
	private boolean CheckStartingPoint(Intent intent){
		int gringo;
		if(mMaxEditText.getText().toString().equals("")){
			gringo =  Integer.parseInt(mMaxEditText.getHint().toString());}
		else {
			gringo = Integer.parseInt(mMaxEditText.getText().toString()); }
		
		if(mMinEditText.getText().toString().equals("")){
			mStartPoint = Integer.parseInt(mMinEditText.getHint().toString()); } 
		else {
			mStartPoint = Integer.parseInt(mMinEditText.getText().toString()); }
		
		if(mStartPoint < 1 || mStartPoint > gringo){
			Toast.makeText(MainActivity.this, R.string.toast_num_start, Toast.LENGTH_LONG).show(); 
			return false; }
		else {
			intent.putExtra(Flashcard.START_NUMBER,(int)(mStartPoint-1));
			return true;
		}
		
	}
	
	private boolean CheckEndingPoint(Intent intent){
		int gringo = Integer.parseInt(mMaxEditText.getHint().toString());
		if(mMaxEditText.getText().toString().equals("")){
			mEndPoint = gringo; }
		else {
			mEndPoint = Integer.parseInt(mMaxEditText.getText().toString()); }
		
		if(mEndPoint < mStartPoint || mEndPoint > gringo){
			Toast.makeText(MainActivity.this, R.string.toast_num_end, Toast.LENGTH_LONG).show();
			return false; }
		else{
			intent.putExtra(Flashcard.END_NUMBER, (int)(mEndPoint-1)); 
			return true;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mp = MediaPlayer.create(getBaseContext(),
				R.raw.button);
				
		myDatabase = new Database(MainActivity.this);
		
				Cursor c = myDatabase.showAllTables();
				if (c.moveToFirst())
		        {
					c.moveToNext();
					while(!c.isAfterLast()){
						myDatabase.defineTable(c.getString(0));
						if(myDatabase.countItems()>0){
							tables.add(c.getString(0));
						}
						myDatabase.closeDatabase();
			           c.moveToNext();
			        }
		        }
		       if (tables.size() == 0)
		        {
		            tables.add("Download sets");
		        }
		       

		Spinner spinner = (Spinner) findViewById(R.id.spinnerCategory);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, 
                android.R.layout.simple_spinner_item, tables); 
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		 
		spinner.setOnItemSelectedListener(new OnItemSelectedListener(){
	        
			@Override
	        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				
				Object selected = parent.getItemAtPosition(pos);
				if(selected.toString().equalsIgnoreCase("Download sets")){
					mChosenSpin=false;
					minimum = "Not defined";
					maximum = "Not defined";
				}
				else{
					mChosenSpin=true;
					mTable = selected.toString();
					myDatabase.defineTable(selected.toString());
					int max=myDatabase.countItems();
					myDatabase.closeDatabase();
					maximum=String.valueOf(max);
					minimum=String.valueOf(1);
					mEndPoint = max;
					
					
				}
				mMinEditText.setHint(minimum);
				mMaxEditText.setHint(maximum);
	        }

	        @Override
	        public void onNothingSelected(AdapterView<?> arg0){	
	        }
	    });
		
		mMinEditText = (EditText) findViewById(R.id.minNumber);
		mMinEditText.setHint(minimum);
		
		mMaxEditText = (EditText) findViewById(R.id.maxNumber);
		mMaxEditText.setHint(maximum);
		
		
		
		downloadButton=(Button)findViewById(R.id.downloadButton);
		downloadButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mp.start();
				Intent intent = new Intent(MainActivity.this, CramFetcher.class);
				startActivity(intent);
			}
		});
		
		mStartButton = (Button)findViewById(R.id.startButton); //setting reference for the "START" button
		mStartButton.setOnClickListener(new View.OnClickListener(){ //creating a listener object

			@Override
			public void onClick(View v){
				mp.start();
				if(mChosenSpin){
					Intent intent = new Intent(MainActivity.this, Flashcard.class);
					if(CheckStartingPoint(intent)){
						if(CheckEndingPoint(intent)){
						intent.putExtra(Flashcard.TABLES, mTable);	
						startActivity(intent); }
						}
					} else {
						Toast.makeText(MainActivity.this, R.string.toast_text, Toast.LENGTH_LONG).show(); }
				}
		});
		
		deleteButton=(Button)findViewById(R.id.deleteButton);
		deleteButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mp.start();
				Intent intent = new Intent(MainActivity.this, DeleteSets.class);
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
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	    	case R.id.action_poweroff:
	    		Intent i = new Intent("end");
                sendBroadcast(i);
                moveTaskToBack(true);
                return true;
	    	default:
	            return super.onOptionsItemSelected(item);
	    }
	}
}