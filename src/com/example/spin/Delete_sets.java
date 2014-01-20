package com.example.spin;

import java.util.ArrayList;
import java.util.HashMap;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.Button;
import android.widget.TextView;

public class Delete_sets extends Activity {

	ArrayList<HashMap<String, String>> contactList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cram);
		
		String str_cram= "Delete sets from database";
		((TextView)findViewById (R.id.mainCram)).setText (str_cram);
		
		str_cram="Delete";
		((Button)findViewById (R.id.buttonCram)).setText (str_cram);
		
		contactList = new ArrayList<HashMap<String, String>>();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.delete_sets, menu);
		return true;
	}

}
