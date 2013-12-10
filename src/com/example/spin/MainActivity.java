package com.example.spin;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;


public class MainActivity extends Activity {

	private RadioGroup difficulty;
	private RadioButton setdifficulty;
	private Button StartQuiz;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
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
