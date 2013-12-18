package com.example.spin;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
//import android.widget.Spinner;
import android.widget.Toast;


public class MainActivity extends Activity {
	
	private Button mStartButton;
	private RadioButton mRadioB;
	private RadioGroup mRadioDiffGroup;
	private String tableName;
	private Database myDatabase;
//	private int mDifficulty, mCategory;
	private boolean mPressedRB = true, mChoosenSpin = true;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		myDatabase = new Database(MainActivity.this);
				
				/*Cursor c = myDatabase.showAllTables();
				if (c.moveToFirst())
		        {
		        do{
		           todoItems.add(c.getString(0));
		
		           }while (c.moveToNext());
		        }
		        if (todoItems.size() == 0)
		        {
		            todoItems.add("No flashcards");
		
		        }*/
		    
		    Spinner spinner = (Spinner) findViewById(R.id.spinnerCategory);
		    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
		            R.array.spinner_choices, android.R.layout.simple_spinner_item);
		    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		    spinner.setAdapter(adapter);
		
		mRadioDiffGroup = (RadioGroup) findViewById(R.id.radioDifficulty);
		int selectedId = mRadioDiffGroup.getCheckedRadioButtonId();
		mRadioB = (RadioButton) findViewById(selectedId);
		
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
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	

}
