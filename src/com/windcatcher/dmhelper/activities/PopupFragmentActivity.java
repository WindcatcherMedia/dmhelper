package com.windcatcher.dmhelper.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.windcatcher.dmhelper.R;
import com.windcatcher.dmhelper.fragments.CreateCreatureFragment;
import com.windcatcher.dmhelper.fragments.EditEncounterFragment;

public class PopupFragmentActivity extends FragmentActivity {


	// ===========================================================
	// TODO Fields
	// ===========================================================

	// ===========================================================
	// TODO Constants
	// ===========================================================

	public static final String SCREEN_TO_POP = "screen";

	public static final String ARGS_EDIT_ENCOUNTER_ROW = "encRowID";

	public static final int
	SCREEN_CREATE_CREATURE = 0,
	SCREEN_EDIT_ENCOUNTER = 1;
	

	// ===========================================================
	// TODO Inherited Methods
	// ===========================================================

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_base_fragment);

		Intent intent = getIntent();
		
		int screenToPop = intent.getIntExtra(SCREEN_TO_POP, -1);

		Fragment frag;
		
		switch(screenToPop){
		case SCREEN_CREATE_CREATURE:
			frag = new CreateCreatureFragment();
			break;
		case SCREEN_EDIT_ENCOUNTER:
			long rowID = intent.getLongExtra(ARGS_EDIT_ENCOUNTER_ROW, -1);
			frag = EditEncounterFragment.newInstance(rowID);
			break;
		default:
			throw new IllegalArgumentException("No screen specified to pop up");
		}
		
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.replace(R.id.container, frag);
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		
		ft.commit(); 
	}

	// ===========================================================
	// TODO Methods
	// ===========================================================
}
