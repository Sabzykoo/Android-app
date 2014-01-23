package com.example.spin;


import com.example.spin.SQLitem;
import com.example.spin.Database;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Iterator;
import java.util.List;

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
	
	private TextView mQuestionTextView, mAnswerTextView, mPageNumTextView;
	private ImageButton mNextButton, mPrevButton, mFavButton;
	private boolean mRepeatable = false;
	
	private String mDeckTable;
	
	private int mCurrentIndex;
	private int mStartingIndex;
	
	private List<SQLitem> table;
	private SQLitem[] mItemBank;
	
	private int mEndingIndex;

	private Database myDatabase;
	
	private void updateCard(int cardPosition){
		
		/**
		 * Updates a SQL item from a specified Database table and its
		 * sequence number
		 *  */
		

		int repeat = mItemBank[cardPosition].getRepeat();
		if(repeat == 0){
			mFavButton.setImageResource(R.drawable.favourite);
			mRepeatable=false;
		}
		else{
			mFavButton.setImageResource(R.drawable.fav);
			mRepeatable=true;
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
	
	//turns the card
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
		
	
	myDatabase.closeDatabase();
	Cursor c = myDatabase.showAllTables();
	int ok=0;
	if (c.moveToFirst()) {
		c.moveToNext();
		while(!c.isAfterLast()){
			if(c.getString(0).equalsIgnoreCase("favorite")){
				ok=1;
			}
			c.moveToNext();
		}
	}
	if(ok==0){
		myDatabase.defineTable("favorite");
		myDatabase.createTable();
	}
		myDatabase.closeDatabase();
		
		mQuestionTextView = (TextView)findViewById(R.id.textFront);
		mAnswerTextView = (TextView)findViewById(R.id.textBack);
		mPageNumTextView = (TextView)findViewById(R.id.pageNumber);
		
		mFavButton = (ImageButton)findViewById(R.id.repeatButton);
		mFavButton.setImageResource(R.drawable.favourite);
		mFavButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mDeckTable.equalsIgnoreCase("favorite")){
					//do nothing since it is favorite
				}
				else{
					int repeat=mItemBank[mCurrentIndex].getRepeat();
					repeat=1-repeat;
					mItemBank[mCurrentIndex].setRepeat(repeat);
					myDatabase.defineTable(mDeckTable);
					myDatabase.updateItem(mItemBank[mCurrentIndex]);
					if(!mRepeatable){
						mFavButton.setImageResource(R.drawable.fav);
						myDatabase.defineTable("favorite");
						myDatabase.addItem(mItemBank[mCurrentIndex]);
						
					}else{
						mFavButton.setImageResource(R.drawable.favourite);
						myDatabase.defineTable("favorite");
						myDatabase.removeItem(mItemBank[mCurrentIndex].getQuestion());
					}
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
		
		updateCard(mCurrentIndex);

	}
	
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
	        	Intent back = new Intent(Flashcard.this,MainActivity.class);
	        	startActivity(back);
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
	public void onConfigurationChanged(Configuration newConfig) {
	    super.onConfigurationChanged(newConfig);

	    // Checks the orientation of the screen
	    if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
	        
	    } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
	       
	    }
	}
	
}