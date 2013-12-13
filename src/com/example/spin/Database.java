package com.example.spin;

import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
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
		private String DATABASE_CREATE;
		private MyDBOpenHelper myDBOpenHelper;
		private static String DATABASE_TABLE;
		
		// constructor to instantiate open helper
		public Database(Context context) {
			
			myDBOpenHelper = new MyDBOpenHelper(context,
					MyDBOpenHelper.DATABASE_NAME, null,
					MyDBOpenHelper.DATABASE_VERSION);
		}
		
		public void defineDatabase(String table){
			
			DATABASE_TABLE = table;
			DATABASE_CREATE = "create table "
					+ DATABASE_TABLE + " (" + KEY_ID
					+ " integer primary key autoincrement, " + KEY_QUESTION_COLUMN
					+ " text not null, " + KEY_ANSWER_COLUMN + " text not null, "
					+ KEY_REPEAT_COLUMN + " integer);";
			
		}
		
		public Cursor showAllTables(){
			SQLiteDatabase DB = myDBOpenHelper.getWritableDatabase();
			Cursor c = DB.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
	        c.moveToFirst();
			return c;
	    }
		
		public void closeDatabase() {
			myDBOpenHelper.close();
			DATABASE_TABLE="";
		}
		
		// add a new item
		public void addItem(SQLitem item) {
			// create a new row of values to insert
			ContentValues newValues = new ContentValues();

			// assign values for each row
			newValues.put(KEY_QUESTION_COLUMN, item.getQuestion());
			newValues.put(KEY_ANSWER_COLUMN, item.getAnswer());
			newValues.put(KEY_REPEAT_COLUMN, item.getRepeat());

			// Insert the row into your table
			SQLiteDatabase db = myDBOpenHelper.getWritableDatabase();
			db.insert(DATABASE_TABLE, null, newValues);
		}

		// get a single item
		public SQLitem getItem(int id) {
			SQLiteDatabase db = myDBOpenHelper.getReadableDatabase();

			// defined arguments
			String[] result_columns = new String[] { KEY_QUESTION_COLUMN,
					KEY_ANSWER_COLUMN, KEY_REPEAT_COLUMN };
			String where = KEY_ID + "=?";
			String[] where_args = new String[] { String.valueOf(id) };

			// undefined arguments
			String groupBy = null;
			String having = null;
			String order = null;

			Cursor cursor = db.query(DATABASE_TABLE, result_columns,
					where, where_args, groupBy, having, order);
			if (cursor != null)
				cursor.moveToFirst();

			SQLitem item = new SQLitem(cursor.getString(0), cursor.getString(1),
					cursor.getInt(2));

			// return item
			return item;
		}

		// get all accessible items
		public List<SQLitem> getAllItems() {
			List<SQLitem> itemList = new LinkedList<SQLitem>();

			// select all items
			String selectQuery = "SELECT  * FROM " + DATABASE_TABLE;

			SQLiteDatabase db = myDBOpenHelper.getReadableDatabase();
			Cursor cursor = db.rawQuery(selectQuery, null);

			// looping through all rows and adding to list
			if (cursor.moveToFirst()) {
				do {
					SQLitem item = new SQLitem(cursor.getString(1), cursor.getString(2),
							cursor.getInt(3));
					//add item to list
					itemList.add(item);
				} while (cursor.moveToNext());
			}

			// return item list
			return itemList;
		}
		
		//clear all items
		public void clearItems() {
			SQLiteDatabase db = myDBOpenHelper.getWritableDatabase();
			myDBOpenHelper.clear(db);
		}

		
		private class MyDBOpenHelper extends SQLiteOpenHelper{

			private static final String DATABASE_NAME = "flashcards.db";
			private static final int DATABASE_VERSION = 1;

			// SQL Statement to create a new database.
			
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
