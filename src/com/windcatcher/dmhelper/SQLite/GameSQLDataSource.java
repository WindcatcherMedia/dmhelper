package com.windcatcher.dmhelper.SQLite;

import java.io.File;
import java.io.FilenameFilter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.windcatcher.dmhelper.GlobalConfig;
import com.windcatcher.dmhelper.SQLite.tables.CreaturesTable;
import com.windcatcher.dmhelper.SQLite.tables.EffectsTable;
import com.windcatcher.dmhelper.SQLite.tables.EncountersTable;
import com.windcatcher.dmhelper.SQLite.tables.GameTable;
import com.windcatcher.dmhelper.SQLite.tables.PlayersTable;
import com.windcatcher.dmhelper.activities.GameActivity;

/**
 * This class holds all of the references and helper classes to the game data stored in the databases.<br>
 * Use getSavedGames for a list of the current games that exist and create a new {@link GameSQLDataSource} <br>
 * to open or create a new game
 * 
 * 
 * @author Tyler
 *
 */
public class GameSQLDataSource extends SQLiteOpenHelper{

	private GameSQLDataSource(Context context, String name, int version) {
		super(context, name + ".db", null, version);
	}

	// ===========================================================
	// TODO Fields
	// ===========================================================

	private SQLiteDatabase mDatabase;

	public static GameSQLDataSource instance;

	
	// ===========================================================
	// TODO Constants
	// ===========================================================
	
	private static String DB_PATH = "/data/data/com.windcatcher.dmhelper/databases/";

	private static int VERSION = 19;


	// ===========================================================
	// TODO Inherited Methods
	// ===========================================================


	@Override
	public void onCreate(SQLiteDatabase db) {
		// create the base tables
		PlayersTable.createPlayerTable(db);
		GameTable.createGameTable(db);
		CreaturesTable.createCreaturesTable(db);
		EncountersTable.createEncounterTable(db);
		EffectsTable.createEffectsTable(db);
		
		// add some default data
		addDefaultData(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// wipe the old data
		PlayersTable.drop(db);
		GameTable.drop(db);
		CreaturesTable.drop(db);
		EncountersTable.drop(db);
		EffectsTable.drop(db);
		// start anew
		onCreate(db);
	}
	
	@Override
	public void close(){
		mDatabase.close();
	}


	// ===========================================================
	// TODO Methods
	// ===========================================================
	
	private void addDefaultData(SQLiteDatabase database){
		CreaturesTable.addCreature(database, "Goblin", 12, 5, null);
		CreaturesTable.addCreature(database, "Orc", 20, 2, null);
		CreaturesTable.addCreature(database, "Tommygun", 15, 3, null);
	}
	
	private void init() throws SQLiteException{
		mDatabase = this.getWritableDatabase();
		instance = this;
	}

	public static GameSQLDataSource getInstance(Context c){
		if(instance == null){
			// the app garbage collected the instance, re-load
			openGame(c);
		}
		return instance;
	}
	
	public static SQLiteDatabase getDatabase(Context c){
		return getInstance(c).mDatabase;
	}

	/**
	 * Opens a game. If it is a new game, a new database will be created and initialized, if it is not a new game all of the data will
	 * be loaded.
	 * @param activity
	 * @param gameName Campaign to create/load.
	 */
	public static void openGame(Activity activity, String gameName){
		// create the database object
		GameSQLDataSource dataSource = new GameSQLDataSource(activity, gameName, VERSION);
		
		// initialize, catch error
		try{
			dataSource.init();
		}catch(SQLiteException e){
			e.printStackTrace();
		}
		
		// start up the game activity
		activity.startActivity(new Intent(activity, GameActivity.class));
	}
	
	/**
	 * Opens a database. Used for JUnit testing only.
	 * @param c
	 * @param gameName Campaign to create/load.
	 */
	public static void openGame(Context c, String gameName){
		// create the database object
		GameSQLDataSource dataSource = new GameSQLDataSource(c, gameName, VERSION);
		
		// initialize, catch error
		try{
			dataSource.init();
		}catch(SQLiteException e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Opens a database. No game name given, load stored game name
	 * @param c
	 * @param gameName Campaign to create/load.
	 */
	public static void openGame(Context c){		
		SharedPreferences prefs = c.getSharedPreferences(GlobalConfig.PREFS, Activity.MODE_PRIVATE);
		String gameName = prefs.getString(GlobalConfig.LOADED_GAME, null);
		
		// create the database object
		GameSQLDataSource dataSource = new GameSQLDataSource(c, gameName, VERSION);
		
		// initialize, catch error
		try{
			dataSource.init();
		}catch(SQLiteException e){
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @return Array of all of the current saved games.
	 */
	public static String[] getSavedGames(){
		File dir = new File(DB_PATH);

		String[] files = dir.list(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String filename) {
				if(filename.contains("journal") || filename.contains("global")){
					return false;
				}
				return true;
			}
		});

		return files;
	}
}
