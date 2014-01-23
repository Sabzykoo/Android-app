package com.example.spin;

import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.widget.Toast;

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
		private SQLiteDatabase DB = null;
		
		// constructor to instantiate open helper
		public Database(Context context) {
			
			myDBOpenHelper = new MyDBOpenHelper(context,
					MyDBOpenHelper.DATABASE_NAME, null,
					MyDBOpenHelper.DATABASE_VERSION);
			
			try{
				open();
			}
			catch(SQLException e)
			{
				Toast.makeText(context, "Error opening the database",
						   Toast.LENGTH_LONG).show();
			}
		}
		
		private void open() throws SQLException {
		    //If connection to db is not open then will open connection
		    if ((DB == null) || (!DB.isOpen())) {
		        DB = myDBOpenHelper.getReadableDatabase();
		    }
		}
		
		public void defineTable(String table){
			
			/**
			 * defines table
			 * */
			
			DATABASE_TABLE = "["+table+"]";
			DATABASE_CREATE = "create table "
					+ DATABASE_TABLE + " (" + KEY_ID
					+ " integer primary key autoincrement, " + KEY_QUESTION_COLUMN
					+ " text not null, " + KEY_ANSWER_COLUMN + " text not null, "
					+ KEY_REPEAT_COLUMN + " integer);";
		}
		
		public void createTable(){
			DB.execSQL(DATABASE_CREATE);
		}
		
		public Cursor showAllTables(){
			Cursor c = DB.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name != 'sqlite_sequence'", null);
			return c;
	    }
		
		public void closeDatabase() {
			DATABASE_TABLE="";
		}
		
		public void addItem(SQLitem item) {
			
			/**
			 * adds a new SQL item
			 * creates a new row of values to insert
			 * assigns values for each row
			 * Inserts the row into table
			 * 
			 * @param SQLitem	SQL item
			 * 
			 * 
			 * */
			
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

		public boolean updateItem(SQLitem item){
			SQLiteDatabase db = myDBOpenHelper.getWritableDatabase();
			int deleted=0;
			String where = KEY_QUESTION_COLUMN + "=?";
			String[] where_args = new String[] { "'"+String.valueOf(item.getQuestion())+"'" };
			ContentValues value= new ContentValues();
			value.put(KEY_QUESTION_COLUMN, item.getQuestion());
			value.put(KEY_ANSWER_COLUMN, item.getAnswer());
			value.put(KEY_REPEAT_COLUMN, item.getRepeat());
			try{
				deleted=db.update(DATABASE_TABLE, value , where, where_args);
			}
			catch(SQLiteException e){
				e.getStackTrace();
			}
			if(deleted>1){
				return false;
			}
			return true;
		}
		
		public boolean removeItem(String id){
			/**
			 * deletes a single SQL item
			 * @param id
			 * @return boolean 
			 * 
			 * */
			SQLiteDatabase db = myDBOpenHelper.getWritableDatabase();
			int deleted=0;
			String where = KEY_QUESTION_COLUMN + "=?";
			String[] where_args = new String[] { "'"+String.valueOf(id)+"'" };
			try{
				deleted=db.delete(DATABASE_TABLE, where, where_args);
			}
			catch(SQLiteException e){
				e.getStackTrace();
			}
			if(deleted>1){
				return false;
			}
			return true;
		}
		
		public SQLitem getItem(int id) {
			/**
			 * gets a single SQL item
			 * @param id
			 * @return item 
			 * 
			 * */
			
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

			return item;
		}

		
		public List<SQLitem> getAllItems() {
			/**
			 * gets all accessible items by selecting them as looping
			 * through all rows and adding to list
			 * 
			 * @return List<SQLitem>	returns a list of SQL items from a specific table
			 *  */
			
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
		
		
		public int countItems(){
			String selectQuery = "SELECT  * FROM " + DATABASE_TABLE;

			SQLiteDatabase db = myDBOpenHelper.getReadableDatabase();
			Cursor cursor = db.rawQuery(selectQuery, null);
			int br=0;
			if (cursor.moveToFirst()) {
				do {
					br++;
				} while (cursor.moveToNext());
			}

			// return number of items
			return br;
		}
		
		//clear all items
		public void clearItems() {
			SQLiteDatabase db = myDBOpenHelper.getWritableDatabase();
			myDBOpenHelper.clear(db);
		}

		
		private class MyDBOpenHelper extends SQLiteOpenHelper{

			private static final String DATABASE_NAME = "flashcards.db";
			private static final int DATABASE_VERSION = 1;
			
			public MyDBOpenHelper(Context context, String name,
					CursorFactory factory, int version) {
				super(context, name, factory, version);
			}
			@Override
			public void onCreate(SQLiteDatabase db) {
				
			}

			@Override
			public void onUpgrade(SQLiteDatabase db, int oldVersion,
					int newVersion) {
				clear(db);
			}
			public void clear(SQLiteDatabase db) {
				// drop the old table
				db.execSQL("DROP TABLE " + DATABASE_TABLE);
			}
			
		}
	
}
