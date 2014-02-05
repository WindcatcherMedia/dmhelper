package com.windcatcher.dmhelper.SQLite.tables;

import com.windcatcher.dmhelper.SQLite.Column;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CreaturesTable{

	// ===========================================================
	// TODO Fields
	// ===========================================================

	// ===========================================================
	// TODO Constants
	// ===========================================================

	public static final Column COLUMN_ID = new Column("_id", 0),
	COLUMN_NAME = new Column("name", 1),
	COLUMN_HP = new Column("hp", 2),
	COLUMN_INIT_MOD = new Column("initmod", 3),
	COLUMN_IMAGE = new Column("imgpath", 4);
	
	public static final String TABLE_NAME = "creatures";
	
	// ===========================================================
	// TODO Inherited Methods
	// ===========================================================

	// ===========================================================
	// TODO Methods
	// ===========================================================
	
	public static void createCreaturesTable(SQLiteDatabase database){
		database.execSQL("CREATE TABLE creatures (" +
				COLUMN_ID + " integer primary key autoincrement," +
				COLUMN_NAME + " text not null," +
				COLUMN_HP + " INTEGER NOT NULL," +
				COLUMN_INIT_MOD + " INTEGER NOT NULL," +
				COLUMN_IMAGE + " text)");
		
		addCreature(database, "blank", 0, 0, null);
	}
	
	public static void deleteCreature(SQLiteDatabase database, long id){
		database.delete(TABLE_NAME, COLUMN_ID + " = " + id, null);
		// remove from all encounters
		EncounterTable.removeAllCreatures(database, id);
	}
	
	public static long addCreature(SQLiteDatabase database, String name, int hp, int initMod, String path){
		ContentValues values = new ContentValues();
		values.put(COLUMN_NAME.getName(), name);
		values.put(COLUMN_HP.getName(), hp);
		values.put(COLUMN_INIT_MOD.getName(), initMod);
		if(path != null){
			values.put(COLUMN_IMAGE.getName(), path);
		}
		long rowID = database.insert(TABLE_NAME, null, values);
		return rowID;
	}
	
	public static String getCreatureImagePathFromEncounter(SQLiteDatabase database, long encounterID){
		String querey = "select c." + COLUMN_IMAGE + " " +
				"from " + EncounterTable.TABLE_NAME + " as e " +
				"join " + TABLE_NAME + " as c on e." + EncounterTable.COLUMN_CREATURE + " = c." + CreaturesTable.COLUMN_ID + " " +
				"where e." + EncounterTable.COLUMN_ID + " = " + encounterID;
		
		Cursor c = database.rawQuery(querey, null);
		
		String path = null;
		if(c.moveToFirst()){
			c.moveToFirst();
			path = c.getString(0);
		}
		
		c.close();
		
		return path;
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
	
	public static void update(SQLiteDatabase database, int id, ContentValues values){
		database.update(TABLE_NAME, values, COLUMN_ID + " = " + id, null);
	}
	
	public static Cursor query(SQLiteDatabase database, long rowID){
		Cursor c = database.query(TABLE_NAME, null, COLUMN_ID + " = ?", new String[] { rowID +"" }, null, null, null);
		return c;
	}
	
	public static Cursor query(SQLiteDatabase database){
		Cursor c = database.query(TABLE_NAME, null, COLUMN_ID + " > 1", null, null, null, null);
		return c;
	}
}
