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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


public class MainActivity extends Activity {
	
	private EditText mMinEditText, mMaxEditText;
	private Button mStartButton,downloadButton;
	private String tableName;
	private Database myDatabase;
	private boolean mChoosenSpin = true;
	ArrayList<String> tables = new ArrayList<String>();
	private int mTableIndex;
	
	private int mStartPoint;
	private int mEndPoint;
	
	
	private boolean CheckStartingPoint(Intent intent){
		if(mMinEditText.getText().toString().equals("")){
			mStartPoint = Integer.parseInt(mMinEditText.getHint().toString()); } 
		else {
			mStartPoint = Integer.parseInt(mMinEditText.getText().toString()); }
		
		if(mStartPoint < 0 || mStartPoint > 12){
			Toast.makeText(MainActivity.this, R.string.toast_num_start, Toast.LENGTH_LONG).show(); 
			return false; }
		else {
			intent.putExtra(Flashcard.START_NUMBER,(int)(mStartPoint-1));
			return true;
		}
		
	}
	
	private boolean CheckEndingPoint(Intent intent){
		if(mMaxEditText.getText().toString().equals("")){
			mEndPoint = Integer.parseInt(mMaxEditText.getHint().toString()); }
		else {
			mEndPoint = Integer.parseInt(mMaxEditText.getText().toString()); }
		
		if(mEndPoint < 1 || mEndPoint > 12){
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
		
		File f = new File("/data/data/com/example/spin/shared_prefs/MyPref.xml");
		
		if (f.exists()) {
			//do nothing
			} else {
				SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
				Editor editor = pref.edit();
				editor.putString("token", null); // Storing string
				editor.putLong("expiry", 0); // Storing long
				editor.commit(); // commit changes
			}
		
		myDatabase = new Database(MainActivity.this);
		
		
		
		/* 	// here you can see how to define table
		 	myDatabase.defineTable("Test");
		  	// here you can see how to define a row
		  	SQLitem item = new SQLitem("What is the biggest land by region", "Russia", 1);
		  	// here you can see how to insert a row into defined table
			MainActivity.this.myDatabase.addItem(item); */
		
		Cursor c = myDatabase.showAllTables();
		if (c.moveToFirst()) {
			c.moveToNext();
			while(!c.isAfterLast()) {
				tables.add(c.getString(0));
				c.moveToNext();
			}
		}
				
		if (tables.size() == 0) {
			tables.add("Download sets");
		}
		
		Spinner spinner = (Spinner) findViewById(R.id.spinnerCategory);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, 
                android.R.layout.simple_spinner_item, tables);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		
		mMinEditText = (EditText) findViewById(R.id.minNumber);
		mMinEditText.setHint("1");
		
		mMaxEditText = (EditText) findViewById(R.id.maxNumber);
		mMaxEditText.setHint("12");
		
		
		
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
				if(mChoosenSpin){
					Intent intent = new Intent(MainActivity.this, Flashcard.class);
					if(CheckEndingPoint(intent) && CheckStartingPoint(intent))
						startActivity(intent); 
				//	else if(!CheckEndingPoint(intent) && !CheckStartingPoint(intent))
				//		Toast.makeText(MainActivity.this, R.string.toast_both, Toast.LENGTH_LONG).show();
					} else {
						Toast.makeText(MainActivity.this, R.string.toast_text, Toast.LENGTH_LONG).show(); }
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

/*
package com.bignerdranch.android.geoquiz;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


public class QuizActivity extends Activity {
	
	private static final String TAG = "QuizActivity";
	private static final String KEY_INDEX = "index";

	private Button mCheatButton;
	private Button mTrueButton;
	private Button mFalseButton;
	private ImageButton mNextButton, mPrevButton;
	private TextView mQuestionTextView;
	private TrueFalse[] mQuestionBank = new TrueFalse[] {
			new TrueFalse(R.string.question_oceans, true),
			new TrueFalse(R.string.question_mideast, true),
			new TrueFalse(R.string.question_africa, true),
			new TrueFalse(R.string.question_americas, false),
			new TrueFalse(R.string.question_asia, true),
	};

		private int mCurrentIndex = 0;
		
		private boolean mIsCheater;
		
		private boolean cheatField[] = new boolean[mQuestionBank.length]; 
		
		@Override
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
				if (data == null) {
					return;
					}
				mIsCheater = data.getBooleanExtra(CheatActivity.EXTRA_ANSWER_SHOWN, false);
		}
		
		private void updateQuestion(){
			int question = mQuestionBank[mCurrentIndex].getQuestion();
			mQuestionTextView.setText(question);
		}

		
		private void checkAnswer(boolean userPressedTrue){
			boolean answerIsTrue = mQuestionBank[mCurrentIndex].isTrueQuestion();
			cheatField[mCurrentIndex] = mIsCheater;
			int messageResId = 0;
			if(mIsCheater){
				messageResId = R.string.judgment_toast;
			} else {
				if(userPressedTrue == answerIsTrue)
					messageResId = R.string.correct_toast; 
				else
					messageResId = R.string.incorrect_toast;
				}
			Toast.makeText(QuizActivity.this, messageResId, Toast.LENGTH_SHORT).show();
		}

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			Log.d(TAG, "onCreate(Bundle) called");
			setContentView(R.layout.activity_quiz);	
			
			mQuestionTextView = (TextView)findViewById(R.id.question_text_view);
			
			//updateQuestion();
			mQuestionTextView.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
					updateQuestion();
				}
			});
			
			mTrueButton = (Button)findViewById(R.id.true_button); //setting reference for the "true" button
			mTrueButton.setOnClickListener(new View.OnClickListener(){ //creating a listener object
				@Override
				public void onClick(View v){
					checkAnswer(true);
					}
			});
			
			mFalseButton = (Button)findViewById(R.id.false_button); // setting reference for the "false" button
			mFalseButton.setOnClickListener(new View.OnClickListener(){ //creating a listener object
				@Override
				public void onClick(View v){
					checkAnswer(false);
					}
			});
			
			mNextButton = (ImageButton)findViewById(R.id.next_button);
			mNextButton.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
					if(cheatField[mCurrentIndex] == true)
						mIsCheater = true;
					else
						mIsCheater = false;
					updateQuestion();
				}
			});
			
			mPrevButton = (ImageButton)findViewById(R.id.prev_button);
			mPrevButton.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					mCurrentIndex = (mCurrentIndex-1) % mQuestionBank.length;
					if(cheatField[mCurrentIndex] == true)
						mIsCheater = true;
					else
						mIsCheater = false;
					updateQuestion();
					
				}
			});
			
			mCheatButton = (Button)findViewById(R.id.cheat_button);
			mCheatButton.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(QuizActivity.this, CheatActivity.class);
					boolean answerIsTrue = mQuestionBank[mCurrentIndex].isTrueQuestion();
					intent.putExtra(CheatActivity.EXTRA_ANSWER_IS_TRUE, answerIsTrue);
					startActivityForResult(intent, 0);
					
				}
			});
			
			if(savedInstanceState != null){
				mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
				mIsCheater = savedInstanceState.getBoolean(KEY_INDEX, false);
			}
			
			updateQuestion();
		}
		
		@Override
		public void onSaveInstanceState(Bundle savedInstanceState){
			super.onSaveInstanceState(savedInstanceState);
			Log.i(TAG, "onSaveInstanceState");
			savedInstanceState.putInt(KEY_INDEX, mCurrentIndex);
			savedInstanceState.putBoolean(KEY_INDEX, mIsCheater);
		}

		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
			// Inflate the menu; this adds items to the action bar if it is present.
			getMenuInflater().inflate(R.menu.quiz, menu);
			return true;
		}
		
		@Override
		public void onStart(){
			super.onStart();
			Log.d(TAG, "onStart() called");
		}
		
		@Override
		public void onPause(){
			super.onPause();
			Log.d(TAG, "onPause() called");
		}
		
		@Override
		public void onResume(){
			super.onResume();
			Log.d(TAG, "onResume() called");
		}
		
		@Override
		public void onStop(){
			super.onStop();
			Log.d(TAG, "onStop() called");
		}
		
		@Override
		public void onDestroy(){
			super.onDestroy();
			Log.d(TAG, "onDestroy() called");
		}

	}

	/*public void updateQuestion() {
	Log.d(TAG, "Updating question text for question #" + mCurrentIndex, new Exception());
	int question = mAnswerKey[mCurrentIndex].getQuestion();
	mQuestionTextView.setText(question);
	}*/
