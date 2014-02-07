package com.windcatcher.dmhelper.SQLite.tables;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.windcatcher.dmhelper.SQLite.Column;

public class PlayersTable{

	// ===========================================================
	// TODO Fields
	// ===========================================================

	// ===========================================================
	// TODO Constants
	// ===========================================================
	
	public static final Column COLUMN_ID = new Column("_id", 0),
			COLUMN_NAME = new Column("name", 1),
			COLUMN_HP = new Column("hp", 2),
			COLUMN_PLAYER_NAME = new Column("playerNme", 3);
	
	public static final String TABLE_NAME = "players";
	
	// ===========================================================
	// TODO Inherited Methods
	// ===========================================================
	
	// ===========================================================
	// TODO Methods
	// ===========================================================
	
	public static void createPlayerTable(SQLiteDatabase database){
		// create the player table
		database.execSQL("CREATE TABLE players " +
				"(" + COLUMN_ID + " integer primary key autoincrement, " +
				COLUMN_NAME + " text not null, " +
				COLUMN_HP + " INTEGER NOT NULL, " +
				COLUMN_PLAYER_NAME + " text NOT NULL)");
		// create a blank entry to account for encounter rows
		// a row for a creature will have 0 for the player column
		addPlayer(database, "blank", "blank");
	}
	
	public static long addPlayer(SQLiteDatabase database, String name, String playerName){
		ContentValues values = new ContentValues();
		values.put(COLUMN_NAME.getName(), name);
		values.put(COLUMN_HP.getName(), 0);
		values.put(COLUMN_PLAYER_NAME.getName(), playerName);
		long rowID = database.insert(TABLE_NAME, null, values);
		return rowID;
	}
	
	public static void removePlayer(SQLiteDatabase database, long rowID){
		database.delete(TABLE_NAME, COLUMN_ID + " = " + rowID, null);
	}
	
	public static int getPlayerCount(SQLiteDatabase database){
		Cursor c = query(database);
		
		int count = c.getCount();
		
		c.close();
		
		return count;
	}
	
	public static boolean exists(SQLiteDatabase database, String name){
		Cursor c = query(database);
		while(c.moveToNext()){
			if(c.getString(COLUMN_NAME.getNum()).equalsIgnoreCase(name)){	
				c.close();
				return true;
			}
		}
		c.close();
		return false;
	}
	
	// ===========================================================
	// TODO Base SQL Statements
	// ===========================================================
	
	public static void drop(SQLiteDatabase database){
		database.execSQL("DROP TABLE " + TABLE_NAME);
	}
	
	public static void update(SQLiteDatabase database, long id, ContentValues values){
		database.update(TABLE_NAME, values, COLUMN_ID + " = " + id, null);
	}
	
	public static Cursor query(SQLiteDatabase database, long id){
		Cursor c = database.query(TABLE_NAME, null, COLUMN_ID + " = ?", new String[] { id +"" }, null, null, null);
		return c;
	}
	
	public static Cursor query(SQLiteDatabase database){
		Cursor c = database.query(TABLE_NAME, null, COLUMN_ID + " > 1", null, null, null, null);
		return c;
	}
	
}
