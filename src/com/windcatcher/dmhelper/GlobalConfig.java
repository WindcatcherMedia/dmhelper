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
	
	public static JSONArray remove(final int idx, final JSONArray from) {
	    final List<Long> objs = asListLong(from);
	    objs.remove(idx);

	    final JSONArray ja = new JSONArray(objs);

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

	public static List<Long> asListLong(final JSONArray ja) {
	    final int len = ja.length();
	    final ArrayList<Long> result = new ArrayList<Long>(len);
	    for (int i = 0; i < len; i++) {
	        final long obj = ja.optLong(i, -1);
	        if (obj != -1) {
	            result.add(obj);
	        }
	    }
	    return result;
	}
}
