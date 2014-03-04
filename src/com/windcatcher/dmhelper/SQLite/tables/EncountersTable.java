package com.windcatcher.dmhelper.SQLite.tables;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.windcatcher.dmhelper.GlobalConfig;
import com.windcatcher.dmhelper.SQLite.Column;

public class EncountersTable {

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
	VIEW_READ_COLUMN_INIT_MOD = new Column("initMod", 6),
	VIEW_READ_COLUMN_EFFECTS = new Column("effects", 7);

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
	 * @param encounter_id
	 * @return
	 */
	public static Cursor getRunningEncounterInfo(SQLiteDatabase database, long encounter_id){
		Cursor c = database.rawQuery(
				"select e." + COLUMN_ID + " as " + COLUMN_ID + "," +
				" p." + PlayersTable.COLUMN_NAME + " as " + VIEW_READ_COLUMN_PLAYERNAME + "," +
				" c." + CreaturesTable.COLUMN_NAME + " as " + VIEW_READ_COLUMN_CREATURENAME + "," +
				" e." + COLUMN_INIT + " as " + VIEW_READ_COLUMN_INIT + "," +
				" e." + COLUMN_HP + " as " + VIEW_READ_COLUMN_HP + "," +
				" e." + COLUMN_MAX_HP + " as " + VIEW_READ_COLUMN_MAXHP  + "," +
				" c." + CreaturesTable.COLUMN_INIT_MOD + " as " + VIEW_READ_COLUMN_INIT_MOD + "," +
				" e." + COLUMN_EFFECTS + " as " + VIEW_READ_COLUMN_EFFECTS +
				" from " + TABLE_NAME + " as e" +
				" join " + PlayersTable.TABLE_NAME + " as p on e." + COLUMN_PLAYER + " = p." + PlayersTable.COLUMN_ID  +
				" join " + CreaturesTable.TABLE_NAME + " as c on e." + COLUMN_CREATURE + " = c." + CreaturesTable.COLUMN_ID +
				" where " + COLUMN_ENCOUNTER_ID + " = " + encounter_id +
				" order by e.init DESC", null);
		return c;
	}

	/**
	 * Gets information used in editing encounters. Only Creatures and not bothering with max hp and init
	 * Column names are prefixed with VIEW_EDIT
	 * @param database
	 * @param encounter_id
	 * @return
	 */
	public static Cursor getEditEncounterInfo(SQLiteDatabase database, long encounter_id){
		String querey = 
				"select e." + COLUMN_ID + " as " + COLUMN_ID + "," +
				" c." + CreaturesTable.COLUMN_NAME + " as " + VIEW_EDIT_COLUMN_CREATURENAME +
				" e." + COLUMN_HP + " as " + VIEW_EDIT_COLUMN_HP + "," +
				" c." + CreaturesTable.COLUMN_INIT_MOD + " as " + VIEW_EDIT_COLUMN_INIT_MOD +
				" from " + TABLE_NAME + " as e" +
				" join " + CreaturesTable.TABLE_NAME + " as c on e." + COLUMN_CREATURE + " = c." + CreaturesTable.COLUMN_ID +
				" where e." + COLUMN_ENCOUNTER_ID + " = " + encounter_id + " AND e." + COLUMN_CREATURE + " > 1";

		Cursor c = database.rawQuery(querey, null);
		return c;
	}

	public static void deleteEncounter(SQLiteDatabase database, long id){
		// remove the encounter rows
		database.delete(TABLE_NAME, COLUMN_ENCOUNTER_ID + " = " + id, null);
	}

	public static void addCreature(SQLiteDatabase database, long encounterID, long creatureID){
		// create an empty json array
		JSONArray array = new JSONArray();
		
		// set the values
		ContentValues values = new ContentValues();
		values.put(COLUMN_ENCOUNTER_ID.getName(), encounterID);
		values.put(COLUMN_CREATURE.getName(), creatureID);
		values.put(COLUMN_EFFECTS.getName(), array.toString());
		
		// insert a row to enter the encounter ID and creature ID
		long rowID = database.insert(TABLE_NAME, null, values);
		
		// use the returned rowID to update the hp and maxHP of said creature from the creature table
		database.execSQL("UPDATE " + TABLE_NAME + " SET " + COLUMN_HP + " = (SELECT " + CreaturesTable.COLUMN_HP + " FROM " + CreaturesTable.TABLE_NAME + " WHERE " + CreaturesTable.COLUMN_ID + " = " + creatureID + " ), " + COLUMN_MAX_HP + " = (SELECT " + CreaturesTable.COLUMN_HP + " FROM " + CreaturesTable.TABLE_NAME + " WHERE " + CreaturesTable.COLUMN_ID + " = " + creatureID + " ) WHERE " + COLUMN_ID + " = " + rowID);
	}

	public static void addPlayer(SQLiteDatabase database, long encounterID, long playerID, int init){
		// create an empty json array
		JSONArray array = new JSONArray();
		
		ContentValues values = new ContentValues();
		values.put(COLUMN_ENCOUNTER_ID.getName(), encounterID);
		values.put(COLUMN_PLAYER.getName(), playerID);
		values.put(COLUMN_INIT.getName(), init);
		values.put(COLUMN_EFFECTS.getName(), array.toString());
		
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
	public static void removeAllCreaturesOfType(SQLiteDatabase database, long creatureID){
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

	public static void changeHP(SQLiteDatabase database, long rowID, int hpChange){
		Cursor c = queryExact(database, rowID);

		c.moveToFirst();

		int currentHp = c.getInt(COLUMN_HP.getNum());
		int maxHP = c.getInt(COLUMN_MAX_HP.getNum());

		c.close();

		int newHP;

		if(currentHp + hpChange > maxHP){
			newHP = maxHP;
		}else if(currentHp + hpChange < 0){
			newHP = 0;
		}else{
			newHP = currentHp + hpChange;
		}

		ContentValues values = new ContentValues();
		values.put(COLUMN_HP.getName(), newHP);

		update(database, rowID, values);
	}
	
	public static void addEffect(SQLiteDatabase database, long rowID, long effectID){
		// grab the current effects to add it
		Cursor c = queryExact(database, rowID);
		c.moveToFirst();
		
		String effects = c.getString(COLUMN_EFFECTS.getNum());
		
		JSONArray effectsArray;
		try {
			effectsArray = new JSONArray(effects);
			effectsArray.put(effectID);
		} catch (JSONException e) {
			effectsArray = new JSONArray();
			e.printStackTrace();
		}
		
		c.close();
		
		ContentValues values = new ContentValues();
		values.put(COLUMN_EFFECTS.getName(), effectsArray.toString());
		update(database, rowID, values);
	}
	
	public static void removeEffect(SQLiteDatabase database, long rowID, long position){
		// grab the current effects
		Cursor c = queryExact(database, rowID);
		c.moveToFirst();
		
		String effects = c.getString(COLUMN_EFFECTS.getNum());
		
		JSONArray effectsArray;
		try {
			effectsArray = new JSONArray(effects);
			// remove the effect at the given position
			effectsArray = GlobalConfig.remove(position, effectsArray);
		} catch (JSONException e) {
			effectsArray = new JSONArray();
			e.printStackTrace();
		}
		
		c.close();
		
		// update the database
		ContentValues values = new ContentValues();
		values.put(COLUMN_EFFECTS.getName(), effectsArray.toString());
		update(database, rowID, values);
	}

	// ===========================================================
	// TODO Base SQL Statements
	// ===========================================================

	public static int update(SQLiteDatabase database, long id, ContentValues values){
		int count = database.update(TABLE_NAME, values, COLUMN_ID + " = " + id, null);
		return count;
	}

	/**
	 * Gets information from a specific encounter.
	 * 
	 * @param database
	 * @param encounter_id
	 * @return All creatures/players for an encounter_id
	 */
	public static Cursor query(SQLiteDatabase database, long encounter_id){
		return database.query(TABLE_NAME, null, COLUMN_ENCOUNTER_ID + " = " + encounter_id, null, null, null, null);
	}

	/**
	 * Gets direct information from a specific row in the table.
	 * 
	 * @param database
	 * @param rowID
	 * @return Single row from encounters table
	 */
	public static Cursor queryExact(SQLiteDatabase database, long rowID){
		return database.query(TABLE_NAME, null, COLUMN_ID + " = " + rowID, null, null, null, null);
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
