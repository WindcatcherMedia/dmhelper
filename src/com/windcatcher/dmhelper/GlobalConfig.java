package com.windcatcher.dmhelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

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
	
	public static JSONArray remove(final long idx, final JSONArray from) {
	    final List<JSONObject> objs = asList(from);
	    objs.remove(idx);

	    final JSONArray ja = new JSONArray();
	    for (final JSONObject obj : objs) {
	        ja.put(obj);
	    }

	    return ja;
	}

	public static List<JSONObject> asList(final JSONArray ja) {
	    final int len = ja.length();
	    final ArrayList<JSONObject> result = new ArrayList<JSONObject>(len);
	    for (int i = 0; i < len; i++) {
	        final JSONObject obj = ja.optJSONObject(i);
	        if (obj != null) {
	            result.add(obj);
	        }
	    }
	    return result;
	}
}
