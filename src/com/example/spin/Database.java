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
		private static String DATABASE_TABLE;
		
		// constructor to instantiate open helper
		public Database(Context context, String table) {
			
			DATABASE_TABLE = table;
			
			myDBOpenHelper = new MyDBOpenHelper(context,
					MyDBOpenHelper.DATABASE_NAME, null,
					MyDBOpenHelper.DATABASE_VERSION);
			
		}
		
		public void closeDatabase() {
			myDBOpenHelper.close();
		}
		
		private class MyDBOpenHelper extends SQLiteOpenHelper{

			private static final String DATABASE_NAME = "flashcards.db";
			private static final int DATABASE_VERSION = 1;
			
			// SQL Statement to create a new database.
			private final String DATABASE_CREATE = "create table "
					+ DATABASE_TABLE + " (" + KEY_ID
					+ " integer primary key autoincrement, " + KEY_QUESTION_COLUMN
					+ " integer, " + KEY_ANSWER_COLUMN + " text not null, "
					+ KEY_REPEAT_COLUMN + " integer);";
			
			public MyDBOpenHelper(Context context, String name,
					CursorFactory factory, int version) {
				super(context, name, factory, version);
			}

			@Override
			public void onCreate(SQLiteDatabase db) {
				db.execSQL(DATABASE_CREATE);
			}

			@Override
			public void onUpgrade(SQLiteDatabase db, int oldVersion,
					int newVersion) {
				clear(db);
			}
			public void clear(SQLiteDatabase db) {
				// drop the old table and create a new one
				db.execSQL("DROP TABLE " + DATABASE_TABLE);
				// create a new one
				onCreate(db);
			}
			
		}
	
}
