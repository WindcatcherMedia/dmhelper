package com.windcatcher.dmhelper.SQLite.tables;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.windcatcher.dmhelper.SQLite.Column;

public class EncounterTable {

	// ===========================================================
	// TODO Fields
	// ===========================================================

	// ===========================================================
	// TODO Constants
	// ===========================================================

	public static final String TABLE_NAME = "encounter";

	public static final Column 
	COLUMN_ID = new Column("_id", 0),
	COLUMN_ENCOUNTER_ID = new Column("encounter_id", 1),
	COLUMN_CREATURE = new Column("creature_id", 2),
	COLUMN_PLAYER = new Column("player_id", 3),
	COLUMN_MAX_HP = new Column("maxhp", 4),
	COLUMN_HP = new Column("hp", 5),
	COLUMN_EFFECTS = new Column("effects", 6), // JSON encoded text array
	COLUMN_INIT = new Column("init", 7);

	public static final Column 
	VIEW_READ_COLUMN_PLAYERNAME = new Column("pName", 1),
	VIEW_READ_COLUMN_CREATURENAME = new Column("cName", 2),
	VIEW_READ_COLUMN_INIT = new Column("init", 3),
	VIEW_READ_COLUMN_HP = new Column("hp", 4),
	VIEW_READ_COLUMN_MAXHP = new Column("max", 5),
	VIEW_READ_COLUMN_INIT_MOD = new Column("initMod", 6);

	public static final Column
	VIEW_EDIT_COLUMN_CREATURENAME = new Column("cName", 1),
	VIEW_EDIT_COLUMN_HP = new Column("hp", 2),
	VIEW_EDIT_COLUMN_INIT_MOD = new Column("initMod", 3);

	// ===========================================================
	// TODO Methods
	// ===========================================================
	
	public static void createEncounterTable(SQLiteDatabase database){
		// create the table with the name as the id
		database.execSQL("CREATE TABLE encounter (" +
				"_id integer primary key autoincrement, " +
				"encounter_id INTEGER not null, " +
				"creature_id INTEGER default 1, " +
				"player_id INTEGER default 1, " +
				"maxhp INTEGER, " +
				"hp INTEGER, " +
				"effects TEXT DEFAULT '[]', " +
				"init INTEGER," +
				"name TEXT)");
	}
	
	public static void resetEncounter(SQLiteDatabase database, long encounter_id){
		// delete all of the player records from the encounter
		database.delete(TABLE_NAME, COLUMN_ENCOUNTER_ID + " = " + encounter_id + " AND " + COLUMN_PLAYER + " > 1", null);
		
		// set the encounter as not running
		GameTable.setRunning(database, encounter_id, false);
	}

	public static void addToEncounter(SQLiteDatabase database, ContentValues values, int encounter_id){
		values.put(COLUMN_ENCOUNTER_ID.getName(), encounter_id);
		database.insert(TABLE_NAME, null, values);
	}

	/**
	 * Get's player readable information from the encounters. Combatants listed by their name as given by their ID relationships.
	 * Column names are prefixed with VIEW_READ
	 * @param database
	 * @param rowID
	 * @return
	 */
	public static Cursor getRunningEncounterInfo(SQLiteDatabase database, long rowID){
		Cursor c = database.rawQuery("select e." + COLUMN_ID + " as " + COLUMN_ID + ", p." + PlayersTable.COLUMN_NAME + " as " + VIEW_READ_COLUMN_PLAYERNAME + ", c." + CreaturesTable.COLUMN_NAME + " as " + VIEW_READ_COLUMN_CREATURENAME + ", e." + COLUMN_INIT + " as " + VIEW_READ_COLUMN_INIT + ", e." + COLUMN_HP + " as " + VIEW_READ_COLUMN_HP + ", e." + COLUMN_MAX_HP + " as " + VIEW_READ_COLUMN_MAXHP  + ", c." + CreaturesTable.COLUMN_INIT_MOD + " as " + VIEW_READ_COLUMN_INIT_MOD + " " +
				"from " + TABLE_NAME + " as e " +
				"join " + PlayersTable.TABLE_NAME + " as p on e." + COLUMN_PLAYER + " = p." + PlayersTable.COLUMN_ID + " " +
				"join " + CreaturesTable.TABLE_NAME + " as c on e." + COLUMN_CREATURE + " = c." + CreaturesTable.COLUMN_ID + " " +
				"where " + COLUMN_ENCOUNTER_ID + " = " + rowID +
				" order by e.init DESC", null);
		return c;
	}

	/**
	 * Gets information used in editing encounters. Only Creatures and not bothering with max hp and init
	 * Column names are prefixed with VIEW_EDIT
	 * @param database
	 * @param rowID
	 * @return
	 */
	public static Cursor getEditEncounterInfo(SQLiteDatabase database, long rowID){
		String querey = "select e." + COLUMN_ID + " as " + COLUMN_ID + ", c." + CreaturesTable.COLUMN_NAME + " as " + VIEW_EDIT_COLUMN_CREATURENAME + ", e." + COLUMN_HP + " as " + VIEW_EDIT_COLUMN_HP + ", c." + CreaturesTable.COLUMN_INIT_MOD + " as " + VIEW_EDIT_COLUMN_INIT_MOD + " " +
				"from " + TABLE_NAME + " as e " +
				"join " + CreaturesTable.TABLE_NAME + " as c on e." + COLUMN_CREATURE + " = c." + CreaturesTable.COLUMN_ID + " " +
				"where e." + COLUMN_ENCOUNTER_ID + " = " + rowID + " AND e." + COLUMN_CREATURE + " > 1";
		
		Cursor c = database.rawQuery(querey, null);
		return c;
	}

	public static void deleteEncounter(SQLiteDatabase database, long id){
		// remove the encounter rows
		database.delete(TABLE_NAME, COLUMN_ENCOUNTER_ID + " = " + id, null);
	}

	public static void addCreature(SQLiteDatabase database, long encounterID, long creatureID){
		ContentValues values = new ContentValues();
		values.put(COLUMN_ENCOUNTER_ID.getName(), encounterID);
		values.put(COLUMN_CREATURE.getName(), creatureID);
		// insert a row to enter the encounter ID and creature ID
		long rowID = database.insert(TABLE_NAME, null, values);
		// use the returned rowID to update the hp and maxHP of said creature from the creature table
		database.execSQL("UPDATE " + TABLE_NAME + " SET " + COLUMN_HP + " = (SELECT " + CreaturesTable.COLUMN_HP + " FROM " + CreaturesTable.TABLE_NAME + " WHERE " + CreaturesTable.COLUMN_ID + " = " + creatureID + " ), " + COLUMN_MAX_HP + " = (SELECT " + CreaturesTable.COLUMN_HP + " FROM " + CreaturesTable.TABLE_NAME + " WHERE " + CreaturesTable.COLUMN_ID + " = " + creatureID + " ) WHERE " + COLUMN_ID + " = " + rowID);
	}
	
	public static void addPlayer(SQLiteDatabase database, long encounterID, long playerID, int init){
		ContentValues values = new ContentValues();
		values.put(COLUMN_ENCOUNTER_ID.getName(), encounterID);
		values.put(COLUMN_PLAYER.getName(), playerID);
		values.put(COLUMN_INIT.getName(), init);
		// insert a row to enter the encounter ID and creature ID
		long rowID = database.insert(TABLE_NAME, null, values);
		// use the returned rowID to update the hp and maxHP of said creature from the creature table
		database.execSQL("UPDATE " + TABLE_NAME + " SET " + COLUMN_HP + " = (SELECT " + PlayersTable.COLUMN_HP + " FROM " + PlayersTable.TABLE_NAME + " WHERE " + PlayersTable.COLUMN_ID + " = " + playerID + " ), " + COLUMN_MAX_HP + " = (SELECT " + PlayersTable.COLUMN_HP + " FROM " + PlayersTable.TABLE_NAME + " WHERE " + PlayersTable.COLUMN_ID + " = " + playerID + " ) WHERE " + COLUMN_ID + " = " + rowID);
	}
	
	public static void removeCreatures(SQLiteDatabase database, long rowID){
		database.delete(TABLE_NAME, COLUMN_ID + " = " + rowID, null);
	}
	
	/**
	 * Removes all creatures of this type from all encounters.
	 * @param database
	 * @param creatureID
	 */
	public static void removeAllCreatures(SQLiteDatabase database, long creatureID){
		database.delete(TABLE_NAME, COLUMN_CREATURE + " = " + creatureID, null);
	}

	public static int getCreatureCount(SQLiteDatabase database, long encounterID){
		Cursor c = database.query(TABLE_NAME, new String[] {COLUMN_ENCOUNTER_ID.getName()}, COLUMN_ENCOUNTER_ID + " = " + encounterID, null, null, null, null);
		//Cursor c = database.rawQuery("SELECT " + COLUMN_ID + " from " + TABLE_NAME + " where " + COLUMN_ID + " = " + rowID, null);

		int count = c.getCount();

		return count;
	}
	
	public static boolean hasPlayers(SQLiteDatabase database, long encounterID){
		Cursor c = database.query(TABLE_NAME, null, COLUMN_ENCOUNTER_ID + " = " + encounterID + " and " + COLUMN_PLAYER + " > 1", null, null, null, null);
		
		boolean hasPlayers = c.getCount() > 0;
		
		c.close();
		
		return hasPlayers;
	}

	public static void update(SQLiteDatabase database, String table, int id, ContentValues values){
		database.update(table, values, COLUMN_ID + " = " + id, null);
	}

	/**
	 * Gets direct information from a specific row in an encounter. Combatants listed by their ID.
	 * 
	 * @param database
	 * @param encounter_id
	 * @return
	 */
	public static Cursor query(SQLiteDatabase database, long encounter_id){
		return database.query(TABLE_NAME, null, COLUMN_ENCOUNTER_ID + " = " + encounter_id, null, null, null, null);
	}

	/**
	 * Gets direct information from the encounter. Combatants listed by their ID.
	 * 
	 * @param database
	 * @return 
	 */
	public static Cursor query(SQLiteDatabase database){
		return database.query(TABLE_NAME, null, null, null, null, null, null);
	}

	public static void drop(SQLiteDatabase database){
		database.execSQL("DROP TABLE " + TABLE_NAME);
	}
}
