package com.example.spin;

import java.lang.reflect.Array;

import android.app.Activity;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;


public class MainActivity extends Activity {

	private String tableName;
	private Database myDatabase;
	private RadioGroup difficulty;
	private RadioButton setdifficulty;
	private Button StartQuiz;
	private Menu todoItems;
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
	    
	    Spinner spinner = (Spinner) findViewById(R.id.spinnerMain);
	    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
	            R.array.spinner_choices, android.R.layout.simple_spinner_item);
	    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    spinner.setAdapter(adapter);
	    
		difficulty = (RadioGroup) findViewById(R.id.radioDifficulty);
		StartQuiz= (Button) findViewById(R.id.buttonStart);
		StartQuiz.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//get user choices
				int selectedDifficulty = difficulty.getCheckedRadioButtonId();
				setdifficulty=(RadioButton) findViewById(selectedDifficulty);
				Toast.makeText(MainActivity.this, setdifficulty.getText(), Toast.LENGTH_SHORT).show();
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