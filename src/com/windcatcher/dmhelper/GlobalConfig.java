package com.windcatcher.dmhelper;

import java.util.Random;

import android.util.Log;

public class GlobalConfig {

	// ===========================================================
	// TODO Fields
	// ===========================================================

	// ===========================================================
	// TODO Constants
	// ===========================================================

	public static final Random RANDY = new Random();

	private static final String LOG_TAG = "DM Manager";

	public static final String PREFS = "prefs";
	public static final String LOADED_GAME = "gameloaded";

	public static final String 
	SAVED_PICTURES_ROOT = "Dungeon Manager",
	SAVED_PICTURES_CREATURES = "Creatures";


	// ===========================================================
	// TODO Inherited Methods
	// ===========================================================

	// ===========================================================
	// TODO Methods
	// ===========================================================

	public static void log(String message){
		Log.w(LOG_TAG, message);
	}
}
