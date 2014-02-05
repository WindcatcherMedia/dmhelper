package com.windcatcher.dmhelper.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.windcatcher.dmhelper.R;
import com.windcatcher.dmhelper.SQLite.GameSQLDataSource;
import com.windcatcher.dmhelper.SQLite.tables.EncounterTable;
import com.windcatcher.dmhelper.fragments.CombatFragment;
import com.windcatcher.dmhelper.fragments.CreateCreatureFragment;
import com.windcatcher.dmhelper.fragments.CreatePlayerFragment;
import com.windcatcher.dmhelper.fragments.EditEncounterFragment;

public class PopupFragmentActivity extends FragmentActivity {


	// ===========================================================
	// TODO Fields
	// ===========================================================

	CombatFragment mCombatFrag;

	// ===========================================================
	// TODO Constants
	// ===========================================================

	public static final String SCREEN_TO_POP = "screen";

	public static final String ARGS_ROW = "encRowID";

	public static final int
	SCREEN_CREATE_CREATURE = 0,
	SCREEN_EDIT_ENCOUNTER = 1,
	SCREEN_COMBAT = 2,
	SCREEN_CREATE_PLAYER = 3;


	// ===========================================================
	// TODO Inherited Methods
	// ===========================================================

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_base_fragment);

		Intent intent = getIntent();

		int screenToPop = intent.getIntExtra(SCREEN_TO_POP, -1);
		long rowID = intent.getLongExtra(ARGS_ROW, -1);

		Fragment frag;
		switch(screenToPop){
		case SCREEN_CREATE_CREATURE:
			frag = CreateCreatureFragment.newInstance(rowID);
			break;
		case SCREEN_CREATE_PLAYER:
			frag = CreatePlayerFragment.newInstance(rowID);
			break;
		case SCREEN_COMBAT:
			frag = mCombatFrag = CombatFragment.newInstance(rowID);
			break;
		case SCREEN_EDIT_ENCOUNTER:
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

	@Override
	public void onBackPressed() {
		// check for the combat fragment being visible, run the close dialog
		if(mCombatFrag != null){
			closeCombat(mCombatFrag.getEncounterRowID());
			return;
		}
		super.onBackPressed();
	}

	// ===========================================================
	// TODO Methods
	// ===========================================================

	private void closeCombat(final long rowID){
		// create an alert dialog to see if the user wants to reset the encounter or save it's state
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.dialog_reset_combat)
		.setPositiveButton(R.string.dialog_okay, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// reset the encounter
				EncounterTable.resetEncounter(GameSQLDataSource.getDatabase(PopupFragmentActivity.this), rowID);

				// null out the fragment reference to avoid leaking the activity
				mCombatFrag = null;

				// close the activity
				finish();
			}
		})
		.setNegativeButton(R.string.dialog_no, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// null out the fragment reference to avoid leaking the activity
				mCombatFrag = null;

				// close the activity
				finish();
			}
		});
		builder.create().show();
	}


}
