package com.example.spin;


import com.example.spin.SQLitem;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
/*import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;*/
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;


public class Flashcard extends Activity {
	
	/**
	 * Creates a Flashcard activity
	 * 
	 * */
	
	
	public static final String START_NUMBER = "start";
	public static final String END_NUMBER = "end";
	
	public static final String KEY_INDEX = "index";
	public static final String TAG = "FlashcardActivity";
	
	public static final int IMAGE_TAG = 1;
	
	private TextView mQuestionTextView, mAnswerTextView, mPageNumTextView;
	private ImageButton mNextButton, mPrevButton, mFavButton;
	private boolean mRepeatable = false;
	
	/*
	 * private ViewPager mPager;
	 * private ViewPagerAdapter mPagerAdapter; 
	*/
	
	private int mCurrentIndex = 0;
	private int mStartingIndex = 0;
	
	private SQLitem[] mItemBank = new SQLitem[] {
			new SQLitem("QUESTION_1", "ANSWER_1", 0),
			new SQLitem("QUESTION_2", "ANSWER_2", 0),
			new SQLitem("QUESTION_3", "ANSWER_3", 0),
			new SQLitem("QUESTION_4", "ANSWER_4", 0),
			new SQLitem("QUESTION_5", "ANSWER_5", 0),
			new SQLitem("QUESTION_6", "ANSWER_6", 0),
			new SQLitem("QUESTION_7", "ANSWER_7", 0),
			new SQLitem("QUESTION_8", "ANSWER_8", 0),
			new SQLitem("QUESTION_9", "ANSWER_9", 0),
			new SQLitem("QUESTION_10", "ANSWER_10", 0),
			new SQLitem("QUESTION_11", "ANSWER_11", 0),
			new SQLitem("QUESTION_12", "ANSWER_12", 0),
	};
	
	private int mEndingIndex;
	
	private void updateCard(){
		/**
		 * Updates a SQL item from a specified Database table and its
		 * sequence number
		 *  */
		
		mRepeatable = false;
		int repeat=mItemBank[mCurrentIndex].getRepeat();
		if(repeat==0){
			mFavButton.setImageResource(R.drawable.favourite);
		}
		else{
			mFavButton.setImageResource(R.drawable.fav);
		}
		String question = mItemBank[mCurrentIndex].getQuestion();
		mQuestionTextView.setText(question);
		
		String answer = mItemBank[mCurrentIndex].getAnswer();
		mAnswerTextView.setText(answer);
		
		String pageNumber = ((Integer)(mCurrentIndex+1)).toString() + " of " + ((Integer)(mEndingIndex+1)).toString();
		mPageNumTextView.setText(pageNumber);
		
		((View)findViewById(R.id.main_activity_card_face)).setVisibility(View.VISIBLE);
		((View)findViewById(R.id.main_activity_card_back)).setVisibility(View.GONE);
	}

	private void flipCard() {
		
		/**
		 * defines the views needed to animate
		 * starts the animation
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
		
		Intent intent = getIntent();
		mStartingIndex = intent.getIntExtra(START_NUMBER, 0);
		mEndingIndex = intent.getIntExtra(END_NUMBER, 0); //mItemBank.length-1
		
		mCurrentIndex = mStartingIndex;
		
		mQuestionTextView = (TextView)findViewById(R.id.textFront);
		mAnswerTextView = (TextView)findViewById(R.id.textBack);
		mPageNumTextView = (TextView)findViewById(R.id.pageNumber);
		
		mFavButton = (ImageButton)findViewById(R.id.repeatButton);
		mFavButton.setImageResource(R.drawable.favourite);
		mFavButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(!mRepeatable){
					mFavButton.setImageResource(R.drawable.fav);
					mRepeatable = !mRepeatable;
					
				}else{
					mFavButton.setImageResource(R.drawable.favourite);
					mRepeatable = !mRepeatable; 
				}
				int repeat=mItemBank[mCurrentIndex].getRepeat();
				repeat=1-repeat;
				mItemBank[mCurrentIndex].setRepeat(repeat);
			}
		});
		
		mNextButton = (ImageButton)findViewById(R.id.next_button);
		mNextButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mCurrentIndex == mEndingIndex) {
					startActivity(new Intent(Flashcard.this, MainActivity.class));
				} else {
					mCurrentIndex++;
					invalidateOptionsMenu();
					updateCard(); 
				}
			}
		});
		
		mPrevButton = (ImageButton)findViewById(R.id.prev_button);
		mPrevButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mCurrentIndex==mStartingIndex){
					mPrevButton.setEnabled(false);
					mPrevButton.setEnabled(true);
			}else{
				mCurrentIndex--;
				invalidateOptionsMenu();
				updateCard();}
			}
		});
		
	/*	if(savedInstanceState != null){
			mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0); 
		} */
		updateCard();

	}
	
/*	@Override
	public void onSaveInstanceState(Bundle savedInstanceState){
		super.onSaveInstanceState(savedInstanceState);
		Log.i(TAG, "onSaveInstanceState");
		savedInstanceState.putInt(KEY_INDEX, mCurrentIndex);
	} */
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.flashcard, menu);
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
		if(mCurrentIndex == mEndingIndex){
			
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
 * package com.bignerdranch.android.geoquiz;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CheatActivity extends Activity {
	
	public static final String EXTRA_ANSWER_IS_TRUE = "com.bignerdranch.android.geoquiz.answer_is_true";
	public static final String EXTRA_ANSWER_SHOWN = "com.bignerdranch.android.geoquiz.answer_shown";
	
	private boolean mAnswerIsTrue;
	private TextView mAnswerTextView;
	private Button mShowAnswer;
	
	private boolean mCheater;
	
	private void setAnswerShownResult(boolean isAnswerShown) {
		Intent data = new Intent();
		data.putExtra(EXTRA_ANSWER_SHOWN, isAnswerShown);
		setResult(RESULT_OK, data);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cheat);
		
		setAnswerShownResult(false);
		
		mCheater = false;
		
		mAnswerIsTrue = getIntent().getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false);
		
		mAnswerTextView = (TextView)findViewById(R.id.answerTextView);
		
		mShowAnswer = (Button)findViewById(R.id.showAnswerButton);
		mShowAnswer.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mAnswerIsTrue == true)
					mAnswerTextView.setText(R.string.true_button);
				else
					mAnswerTextView.setText(R.string.false_button);
				setAnswerShownResult(true);
				
				mCheater = true;
			}
		});
		
		if(savedInstanceState != null)
			mCheater = savedInstanceState.getBoolean(EXTRA_ANSWER_SHOWN, false);
		setAnswerShownResult(mCheater);
		
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState){
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putBoolean(EXTRA_ANSWER_SHOWN, mCheater);
	}
	
}
*/
