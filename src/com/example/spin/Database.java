package com.example.spin;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class Database {

	// the index (key) column name
		public static final String KEY_ID = "ID";

		// names of columns
		public static final String KEY_QUESTION_COLUMN = "ITEM_QUESTION_COLUMN";
		public static final String KEY_ANSWER_COLUMN = "ITEM_ANSWER_COLUMN";
		public static final String KEY_REPEAT_COLUMN = "ITEM_COLUMN_COLUMN";

		private MyDBOpenHelper myDBOpenHelper;
		
		// constructor to instantiate open helper
		public Database(Context context) {
			
		}
		
		private class MyDBOpenHelper extends SQLiteOpenHelper{

			public MyDBOpenHelper(Context context, String name,
					CursorFactory factory, int version) {
				super(context, name, factory, version);
			}

			@Override
			public void onCreate(SQLiteDatabase db) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onUpgrade(SQLiteDatabase db, int oldVersion,
					int newVersion) {
				// TODO Auto-generated method stub
				
			}
			
		}
	
}
