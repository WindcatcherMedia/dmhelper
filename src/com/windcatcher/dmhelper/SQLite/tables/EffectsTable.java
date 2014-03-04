package com.windcatcher.dmhelper.SQLite.tables;

import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.windcatcher.dmhelper.SQLite.Column;

public class EffectsTable {

	// ===========================================================
	// TODO Fields
	// ===========================================================

	public static final Column COLUMN_ID = new Column("_id", 0),
			COLUMN_NAME = new Column("name", 1),
			COLUMN_DESCRIPTION = new Column("description", 2),
			COLUMN_TRIGGER_TYPE = new Column("trigger", 3),
			COLUMN_NOTIFICATION_ACTIVE = new Column("notificationActive", 4),
			COLUMN_DEALS_DAMAGE = new Column("dealsDamage", 5),
			COLUMN_DAMAGE = new Column("damage", 6);

	public static final String TABLE_NAME = "effects";

	// ===========================================================
	// TODO Constants
	// ===========================================================

	public static final int TRIGGER_START = 0, TRIGGER_END = 1;
	public static final int DAMAGE_NO = 0, DAMAGE_YES = 1;

	// ===========================================================
	// TODO Inherited Methods
	// ===========================================================

	// ===========================================================
	// TODO Methods
	// ===========================================================

	public static void createEffectsTable(SQLiteDatabase database){
		// create the table with the name as the id
		database.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
				COLUMN_ID + " integer primary key autoincrement, " +
				COLUMN_NAME + " text not null, " +
				COLUMN_DESCRIPTION + " text not null, " +
				COLUMN_TRIGGER_TYPE + " INTEGER not null, " +
				COLUMN_NOTIFICATION_ACTIVE + " integer not null, " +
				COLUMN_DEALS_DAMAGE + " INTEGER not null, " +
				COLUMN_DAMAGE + " INTEGER)");
	}
	
	public static long addNewEffect(SQLiteDatabase database, String name, String description, int triggerType, int notificationActive, int dealsDamage, int damage){
		// put together the values
		ContentValues values = new ContentValues();
		values.put(COLUMN_NAME.getName(), name);
		values.put(COLUMN_DESCRIPTION.getName(), description);
		values.put(COLUMN_TRIGGER_TYPE.getName(), triggerType);
		values.put(COLUMN_NOTIFICATION_ACTIVE.getName(), notificationActive);
		values.put(COLUMN_DEALS_DAMAGE.getName(), dealsDamage);
		if(dealsDamage == DAMAGE_YES){
			values.put(COLUMN_DAMAGE.getName(), damage);
		}
		
		// add it to the database
		return database.insert(TABLE_NAME, null, values);
	}
	
	public static void removeEffect(SQLiteDatabase database, long rowID){
		database.delete(TABLE_NAME, COLUMN_ID + " = " + rowID, null);
	}
	
	public static Cursor query(SQLiteDatabase database, List<Long> rowIDs){
		String query;
		query = "Select * from " + TABLE_NAME + " where " + COLUMN_ID + " = " + rowIDs.get(0);
		for(Long l: rowIDs){
			query += " OR " + COLUMN_ID + " = " + l;
		}
		
		Cursor c = database.rawQuery(query, null);
		
		
		return c;
	}

	// ===========================================================
	// TODO Base SQL Statements
	// ===========================================================

	public static int update(SQLiteDatabase database, long id, ContentValues values){
		int count = database.update(TABLE_NAME, values, COLUMN_ID + " = " + id, null);
		return count;
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
