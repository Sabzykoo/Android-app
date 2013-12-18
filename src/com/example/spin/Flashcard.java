package com.example.spin;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

public class Flashcard extends Activity {
	
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
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.flashcard, menu);
		return true;
	}

}
