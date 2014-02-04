package com.windcatcher.dmhelper.activities;

import com.windcatcher.dmhelper.R;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class EditEncounterActivity extends FragmentActivity {

	// ===========================================================
	// TODO Fields
	// ===========================================================

	// ===========================================================
	// TODO Constants
	// ===========================================================

	// ===========================================================
	// TODO Inherited Methods
	// ===========================================================

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_encounter);

		// Set up the action bar
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);

	}

	// ===========================================================
	// TODO Methods
	// ===========================================================
}
