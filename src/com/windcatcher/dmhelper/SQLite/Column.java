package com.windcatcher.dmhelper.SQLite;

public class Column {
	
	public Column(String name, int num){
		mColumnName = name;
		mColumnNum = num;
	}

	// ===========================================================
	// TODO Fields
	// ===========================================================

	private String mColumnName;
	private int mColumnNum;
	
	// ===========================================================
	// TODO Constants
	// ===========================================================

	// ===========================================================
	// TODO Inherited Methods
	// ===========================================================

	public String toString(){
		return getName();
	}
	
	// ===========================================================
	// TODO Methods
	// ===========================================================
	
	public String getName(){
		return mColumnName;
	}
	
	public int getNum(){
		return mColumnNum;
	}
}
