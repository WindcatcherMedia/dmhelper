package com.windcatcher.dmhelper;

import java.lang.reflect.Field;

import android.view.ViewConfiguration;


public class Application extends android.app.Application {

	@Override
	public void onCreate() {
		super.onCreate();
		try {
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
			if(menuKeyField != null) {
				menuKeyField.setAccessible(true);
				menuKeyField.setBoolean(config, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// ===========================================================
	// TODO Fields
	// ===========================================================

	// ===========================================================
	// TODO Constants
	// ===========================================================

	// ===========================================================
	// TODO Inherited Methods
	// ===========================================================

	// ===========================================================
	// TODO Methods
	// ===========================================================
}
