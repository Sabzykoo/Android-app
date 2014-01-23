package com.example.spin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.content.Intent;
import android.database.Cursor;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.app.ListActivity;

public class DeleteSets extends ListActivity {
	
	/**
	 * displays all downloaded tables
	 * deletes selected tables
	 * and returns to main activity
	 * */
	
	ArrayList<String> tables = new ArrayList<String>();
	List<Model> list = new ArrayList<Model>();
	private Database myDatabase;
	private Button mDelete;
	MediaPlayer mp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cram);
		String str_cram= "Delete sets from database";
		((TextView)findViewById(R.id.mainCram)).setText(str_cram);
		
		str_cram="Delete";
		((Button)findViewById(R.id.buttonCram)).setText(str_cram);
		
		myDatabase = new Database(DeleteSets.this);
		Cursor c = myDatabase.showAllTables();
		int br=0;
		if (c.moveToFirst()){
			c.moveToNext();
		
			while(!c.isAfterLast()) {
				// Model for each set
				String id=String.valueOf(br);
				myDatabase.defineTable(c.getString(0));
				List<SQLitem> query = myDatabase.getAllItems();
				String number = String.valueOf(query.size());
				String questions= "Number of questions in set: "+number;
				list.add(get(id,c.getString(0),questions));
				br++;
				c.moveToNext();
			}
		} 
		
		if (br == 0) {
    	   Toast.makeText(DeleteSets.this, "You need to download sets!",
	          	     Toast.LENGTH_LONG).show();
			DeleteSets.this.finish();
		}
		mp = MediaPlayer.create(getBaseContext(),
				R.raw.checkbox_sound);
		ArrayAdapter<Model> adapter = new InteractiveArrayAdapter(this,
		        list,mp);
		    setListAdapter(adapter);
		    mp = MediaPlayer.create(getBaseContext(),
					R.raw.button);
       mDelete = (Button)findViewById(R.id.buttonCram); //setting reference for the "START" button
       mDelete.setOnClickListener(new View.OnClickListener(){ //creating a listener object
			
			@Override
			public void onClick(View v){
				mp.start();
				Iterator<Model> iterate =list.iterator();
				while(iterate.hasNext()){
					Model next = iterate.next();
					if(next.isSelected()){
						String table=next.getName();
						String value =iterate(table);
						if(value.equalsIgnoreCase("Not found")){
							Toast.makeText(DeleteSets.this,
									"Table "+table+" not found!",
									Toast.LENGTH_SHORT).show();
						}
						else{
							myDatabase.defineTable(value);
							myDatabase.clearItems();
						}
					}
					
				}
				DeleteSets.this.finish();
				Intent back= new Intent(DeleteSets.this,MainActivity.class);
				startActivity(back);
			}
		});
       
	}
	private Model get(String id,String s,String q) {
        return new Model(id,s,q);
      }
	
	private String iterate(String string){
		Cursor c = myDatabase.showAllTables();
		if (c.moveToFirst()){
			c.moveToNext();
		
			while(!c.isAfterLast()) {
				if(c.getString(0).equalsIgnoreCase(string)){
					return c.getString(0);
				}
				c.moveToNext();
			}
		}
		return "Not found"; 
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.delete_sets, menu);
		return true;
	}

}
