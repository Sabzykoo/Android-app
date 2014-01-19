package com.example.spin;


import com.example.spin.SQLitem;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
/*import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;*/
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;


public class Flashcard extends Activity {
	
	/**
	 * Creates a Flashcard activty
	 * */
	
	
	private static final String TAG = "FlashcardActivty";
	private static final String KEY_INDEX = "index";
	
	public static final String ARG_PAGE = "page";
	//boolean flag;
	private TextView mQuestionTextView, mAnswerTextView, mPageNumTextView;
	private ImageButton mNextButton, mPrevButton;
	/*private ViewPager mPager;
	private ViewPagerAdapter mPagerAdapter; */
	
/*	private View rootLayout = (View) findViewById(R.id.main_activity_root);
	private View cardFace = (View) findViewById(R.id.main_activity_card_face);
	private View cardBack = (View) findViewById(R.id.main_activity_card_back);*/
	private int mCurrentIndex = 0;
	
	private SQLitem[] mItemBank = new SQLitem[] {
			new SQLitem("el gringo", "grrrrr", 0),
			new SQLitem("el nino come naranjas", "desde mi balcom lo siento", 0),
	};
	
	private void updateCard(){
		/**
		 * Updates a SQL item from a specified Database table and its
		 * sequence number
		 *  */
		
		String question = mItemBank[mCurrentIndex].getQuestion();
		mQuestionTextView.setText(question);
		
		String answer = mItemBank[mCurrentIndex].getAnswer();
		mAnswerTextView.setText(answer);
		
		String pageNumber = ((Integer)(mCurrentIndex+1)).toString() + " of " + ((Integer)mItemBank.length).toString();
		mPageNumTextView.setText(pageNumber);
		
	//	((View)findViewById(R.id.main_activity_card_face)).setVisibility(View.VISIBLE);
		
	}


	private void flipCard() {
		
		/**
		 * @see FlipAnimation
		 * */
		
		View rootLayout = (View) findViewById(R.id.main_activity_root);
		View cardFace = (View) findViewById(R.id.main_activity_card_face);
		View cardBack = (View) findViewById(R.id.main_activity_card_back);
		
	    FlipAnimation flipAnimation = new FlipAnimation(cardFace, cardBack);

	    if (cardFace.getVisibility() == View.GONE) {
	        flipAnimation.reverse();
	    }
	    rootLayout.startAnimation(flipAnimation);
	}
	
	
	public void onCardClick(View view) {
	      flipCard();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_flashcard);
		
		mQuestionTextView = (TextView)findViewById(R.id.textFront);
		mAnswerTextView = (TextView)findViewById(R.id.textBack);
		mPageNumTextView = (TextView)findViewById(R.id.pageNumber);
		
		mNextButton = (ImageButton)findViewById(R.id.next_button);
		mNextButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mCurrentIndex++;
				invalidateOptionsMenu();
				updateCard();
			}
		});
		
		mPrevButton = (ImageButton)findViewById(R.id.prev_button);
		mPrevButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mCurrentIndex--;
				invalidateOptionsMenu();
				updateCard();
			}
		});
		
		updateCard();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.flashcard, menu); // Inflate the menu; this adds items to the action bar if it is present.
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_previous:
	        	mCurrentIndex--;
	        	invalidateOptionsMenu();
	        	updateCard();
	            return true;
	        case R.id.action_next:
	        	mCurrentIndex++;
	        	invalidateOptionsMenu();
	        	updateCard();
	            return true;
	        case R.id.action_finish:
	        	startActivity(new Intent(Flashcard.this, MainActivity.class));
	        	return false;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu){
		if(mCurrentIndex == mItemBank.length-1){
			
			menu.removeItem(R.id.action_next);
			MenuItem item = menu.add(Menu.NONE, R.id.action_finish, Menu.NONE, R.string.action_finish);
			item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		}
	
		if(mCurrentIndex==0){
			menu.findItem(R.id.action_previous).setEnabled(false);
		}
	
		return super.onPrepareOptionsMenu(menu);	
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
