package com.windcatcher.dmhelper.SQLite.tables;

import com.windcatcher.dmhelper.SQLite.Column;

public class CombatTable {

	// ===========================================================
	// TODO Fields
	// ===========================================================

	// ===========================================================
	// TODO Constants
	// ===========================================================
	
	public static final Column COLUMN_ID = new Column("_id", 0),
			COLUMN_NAME = new Column("name", 1),
			COLUMN_HP = new Column("hp", 2);
			
	public static final String TABLE_NAME = "combats";
	// ===========================================================
	// TODO Inherited Methods
	// ===========================================================

	// ===========================================================
	// TODO Methods
	// ===========================================================
}
