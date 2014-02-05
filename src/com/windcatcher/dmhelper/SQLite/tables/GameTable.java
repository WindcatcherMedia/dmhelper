package com.windcatcher.dmhelper.SQLite.tables;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.windcatcher.dmhelper.SQLite.Column;

public class GameTable{

	// ===========================================================
	// TODO Fields
	// ===========================================================

	// ===========================================================
	// TODO Constants
	// ===========================================================

	public static final Column COLUMN_ID = new Column("_id", 0),
			COLUMN_ACTIVE = new Column("active", 1),
			COLUMN_RUNNING = new Column("running", 2),
			COLUMN_TURN = new Column("turn", 3);
	
	public static final String TABLE_NAME = "game";
	
	// ===========================================================
	// TODO Inherited Methods
	// ===========================================================

	// ===========================================================
	// TODO Methods
	// ===========================================================
	
	public static void createGameTable(SQLiteDatabase database){
		// create the player table
		database.execSQL("CREATE TABLE game (" +
				COLUMN_ID + " integer primary key autoincrement, " +
				COLUMN_ACTIVE + " INTEGER DEFAULT 1," +
				COLUMN_RUNNING + " INTEGER DEFAULT 0," +
				COLUMN_TURN + " INTEGER DEFAULT 0)");
	}
	
	public static long createNewEncounter(SQLiteDatabase database){
		ContentValues values = new ContentValues();
		values.put(COLUMN_ACTIVE.getName(), 1);
		return database.insert(TABLE_NAME, null, values);
	}
	
	public static void deleteEncounter(SQLiteDatabase database, long rowID){
		// de-activate the row in the game table
		ContentValues values = new ContentValues();
		values.put(COLUMN_ACTIVE.getName(), 0);
		database.update(TABLE_NAME, values, COLUMN_ID + " = " + rowID, null);
		// remove rows from the encounter table
		// TODO EncounterTable.deleteEncounter(database, rowID);
	}
	
	public static boolean isRunning(SQLiteDatabase database, long rowID){
		Cursor c = database.query(TABLE_NAME, null, COLUMN_ID + " = " + rowID, null, null, null, null);
		
		c.moveToFirst();
		
		boolean running = c.getInt(COLUMN_RUNNING.getNum()) == 1;
		
		c.close();
		
		return running;
	}
	
	public static void setRunning(SQLiteDatabase database, long rowID, boolean isRunning){
		ContentValues values = new ContentValues();
		values.put(COLUMN_RUNNING.getName(), (isRunning) ? 1 : 0);
		values.put(COLUMN_TURN.getName(), 0);
		update(database, rowID, values);
	}
	
	public static void setTurn(SQLiteDatabase database, long rowID, int turn){
		ContentValues values = new ContentValues();
		values.put(COLUMN_TURN.getName(), turn);
		update(database, rowID, values);
	}
	
	// ===========================================================
	// TODO Base SQL Statements
	// ===========================================================
	
	public static void drop(SQLiteDatabase database){
		database.execSQL("DROP TABLE " + TABLE_NAME);
	}
	
	public static void update(SQLiteDatabase database, long rowID, ContentValues values){
		database.update(TABLE_NAME, values, COLUMN_ID + " = " + rowID, null);
	}
	
	public static Cursor query(SQLiteDatabase database, long rowID){
		Cursor c = database.query(TABLE_NAME, null, COLUMN_ID + " = " + rowID, null, null, null, null);
		return c;
	}
	
	public static Cursor query(SQLiteDatabase database){
		Cursor c = database.query(TABLE_NAME, null, COLUMN_ACTIVE + " = 1", null, null, null, null);
		return c;
	}
}
