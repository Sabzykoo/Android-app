package com.example.spin;


import com.example.spin.SQLitem;
import com.example.spin.Database;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Flashcard extends Activity{
	
	/**
	 * Creates a Flashcard activity
	 * 
	 * */
	
	
	public static final String START_NUMBER = "start";
	public static final String END_NUMBER = "end";
	public static final String TABLES = "tables";
	
	public static final String KEY_INDEX = "index";
	public static final String TAG = "FlashcardActivity";
	
	public static final int IMAGE_TAG = 1;
	private static final int RESULT_CLOSE_ALL = 0;
	
	private TextView mQuestionTextView, mAnswerTextView, mPageNumTextView;
	private ImageButton mNextButton, mPrevButton, mFavButton;
	private boolean mRepeatable = false;
	
	private String mDeckTable;
	
	/*
	 * private ViewPager mPager;
	 * private ViewPagerAdapter mPagerAdapter; 
	*/
	
	private int mCurrentIndex = 0;
	private int mStartingIndex = 0;
	
	private List<SQLitem> table;
	private SQLitem[] mItemBank;
	
	private int mEndingIndex;

	private Database myDatabase;

	public static int randPosition(int min, int max) {
		
		/**
		 * Returns a pseudo-random number between min and max, inclusive.
		 * The difference between min and max can be at most
		 * <code>Integer.MAX_VALUE - 1</code>.
		 *
		 * @param min Minimum value
		 * @param max Maximum value.  Must be greater than min.
		 * @return Integer between min and max, inclusive.
		 * @see java.util.Random#nextInt(int)
		 */

	    // Usually this can be a field rather than a method variable
	    Random rand = new Random();
	    // nextInt is normally exclusive of the top value,
	    // so add 1 to make it inclusive
	    int randomNum = rand.nextInt((max - (min+1)) + 1) + min;

	    return randomNum;
	}
	
	
	private void insertCard(int first, int position){
		if(position == mCurrentIndex){
			updateCard(first);
		}
		
	}
	
	private void doIt(int favIndex){
	if(mItemBank[favIndex].getRepeat() == 1){
		int pos = randPosition(favIndex, mEndingIndex); //+1
		insertCard(favIndex, pos);}
	}
	
	private void updateCard(int cardPosition){
		
		/**
		 * Updates a SQL item from a specified Database table and its
		 * sequence number
		 *  */
		

		mRepeatable = false; //?
		int repeat = mItemBank[cardPosition].getRepeat();
		if(repeat == 0){
			mFavButton.setImageResource(R.drawable.favourite);
		}
		else{
			mFavButton.setImageResource(R.drawable.fav);
		}
		String question = mItemBank[cardPosition].getQuestion();
		mQuestionTextView.setText(question);
		
		String answer = mItemBank[cardPosition].getAnswer();
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
		mEndingIndex = intent.getIntExtra(END_NUMBER, 0);
		mDeckTable = intent.getStringExtra(TABLES);
		
		myDatabase = new Database(Flashcard.this);
		myDatabase.defineTable(mDeckTable);
		table = myDatabase.getAllItems();
		
		mItemBank = new SQLitem[table.size()];
		mCurrentIndex = mStartingIndex;
		int br = 0;
		Iterator<SQLitem> iterate = table.iterator();
		while(iterate.hasNext()){
			SQLitem table = iterate.next();
			mItemBank[br++]=table;
			}
		
	
	myDatabase.defineTable("favorite");
	Cursor c = myDatabase.showAllTables();
	if (c.moveToFirst()) {
		c.moveToNext();
		while(!c.isAfterLast()){
			if(!c.getString(0).equalsIgnoreCase("favorite")){
				myDatabase.createTable();
			}
			c.moveToNext();
			}
		}
		
		
		if(mItemBank[mCurrentIndex].getRepeat() == 1){
			doIt(mCurrentIndex);
		}
		
		mQuestionTextView = (TextView)findViewById(R.id.textFront);
		mAnswerTextView = (TextView)findViewById(R.id.textBack);
		mPageNumTextView = (TextView)findViewById(R.id.pageNumber);
		
		mFavButton = (ImageButton)findViewById(R.id.repeatButton);
		mFavButton.setImageResource(R.drawable.favourite);
		mFavButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int repeat=mItemBank[mCurrentIndex].getRepeat();
				repeat=1-repeat;
				mItemBank[mCurrentIndex].setRepeat(repeat);
				myDatabase.defineTable(mDeckTable);
				myDatabase.updateItem(mItemBank[mCurrentIndex]);
				if(!mRepeatable){
					mFavButton.setImageResource(R.drawable.fav);
					myDatabase.defineTable("favorites");
					myDatabase.addItem(mItemBank[mCurrentIndex]);
					mRepeatable = !mRepeatable;
					
				}else{
					mFavButton.setImageResource(R.drawable.favourite);
					myDatabase.defineTable("favorites");
					myDatabase.removeItem(mItemBank[mCurrentIndex].getQuestion());
					mRepeatable = !mRepeatable; 
				}
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
					updateCard(mCurrentIndex); 
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
				updateCard(mCurrentIndex);}
			}
		});
		
	/*	if(savedInstanceState != null){
			mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0); 
		} */
		updateCard(mCurrentIndex);

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
	        	updateCard(mCurrentIndex);
	            return true;
	        case R.id.action_next:
	        	mCurrentIndex++;
	        	invalidateOptionsMenu();
	        	updateCard(mCurrentIndex);
	            return true;
	        case R.id.action_finish:
	        	Flashcard.this.finish();
	        	return true;
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
	
	@Override
	protected void onDestroy() {
		Flashcard.this.finish();
		super.onDestroy();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    
		switch(resultCode)
	    {
	    case RESULT_CLOSE_ALL:
	        setResult(RESULT_CLOSE_ALL);
	        onDestroy();
	        finish();
	    }
	    super.onActivityResult(requestCode, resultCode, data);
	}

}