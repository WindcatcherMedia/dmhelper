package com.windcatcher.dmhelper.SQLite;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * This class holds all of the references and helper classes to the game data stored in the databases.<br>
 * Use getSavedGames for a list of the current games that exist and create a new {@link GlobalSQLDataSource} <br>
 * to open or create a new game
 * 
 * 
 * @author Tyler
 *
 */
public class GlobalSQLDataSource extends SQLiteOpenHelper{

	public GlobalSQLDataSource(Activity activity, CursorFactory factory, int version) {
		super(activity, "global", factory, version);

		instance = this;
	}

	// ===========================================================
	// TODO Fields
	// ===========================================================

	private SQLiteDatabase mDatabase;

	private static GlobalSQLDataSource instance;

	
	// ===========================================================
	// TODO Constants
	// ===========================================================

	private static int VERSION = 0;

	// ===========================================================
	// TODO Inherited Methods
	// ===========================================================


	@Override
	public void onCreate(SQLiteDatabase db) {
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}


	// ===========================================================
	// TODO Methods
	// ===========================================================
	
	private void init() throws SQLiteException{
		mDatabase = this.getWritableDatabase();
	}

	public static GlobalSQLDataSource getInstance(){
		return instance;
	}
}
