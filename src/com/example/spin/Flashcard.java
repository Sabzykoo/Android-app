package com.example.spin;


import java.util.List;

import com.example.spin.R;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class Flashcard extends Activity {
	
	public static final String ARG_PAGE = "page";
	
	private ViewPager mPager;
	private ViewPagerAdapter mPagerAdapter;
	private int mPageNumber;
	
	
	private int BackArray[] = {R.string.backText };
	private int FrontArray[] = {R.string.frontText };
	
	private void flipCard() {
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
		
		// Set the ViewPager adapter
	    mPagerAdapter = new ViewPagerAdapter(this, BackArray);
	    mPager = (ViewPager) findViewById(R.id.pager);
	    mPager.setAdapter(mPagerAdapter);
	 //   mPager.setCurrentItem(0);
	    
	    
	    
	    mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
	    	
	    	@Override
	    	public void onPageSelected(int position){
	    		invalidateOptionsMenu();
	    		
	    	}
	    });
		
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.flashcard, menu); // Inflate the menu; this adds items to the action bar if it is present.
		
		menu.findItem(R.id.action_previous).setEnabled(mPager.getCurrentItem() > 0);
		
		//Add either a "next" of "finish" button to the action bar, depending on which page is currently selected.
		MenuItem item = menu.add(Menu.NONE, R.id.action_next, Menu.NONE,
				(mPager.getCurrentItem() == mPagerAdapter.getCount() - 1)
				? R.string.action_finish
				: R.string.action_next);
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		return true;
	}

}
