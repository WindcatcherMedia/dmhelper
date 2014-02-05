package com.windcatcher.dmhelper.activities;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.view.KeyEvent;
import android.view.Menu;
import android.widget.ArrayAdapter;

import com.windcatcher.dmhelper.GlobalConfig;
import com.windcatcher.dmhelper.R;
import com.windcatcher.dmhelper.dialogs.QuickDialogs;
import com.windcatcher.dmhelper.dialogs.QuickDialogs.IQuitCallback;
import com.windcatcher.dmhelper.fragments.CreaturesFragment;
import com.windcatcher.dmhelper.fragments.EncountersFragment;
import com.windcatcher.dmhelper.fragments.PlayersFragment;

public class GameActivity extends FragmentActivity implements
ActionBar.OnNavigationListener, OnBackStackChangedListener {

	// ===========================================================
	// TODO Fields
	// ===========================================================

	private boolean mEditEncountersOpen = false;
	private long mEditEncounterRowID;

	private ArrayAdapter<String> mNavigationAdapter;

	// ===========================================================
	// TODO Constants
	// ===========================================================

	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * current dropdown position.
	 */
	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

	public static final String FRAGMENT_EDIT_ENCOUNTER = "edit_encounter";

	// ===========================================================
	// TODO Inherited Methods
	// ===========================================================

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_base_fragment);

		// Set up the action bar to show a dropdown list.
		final ActionBar actionBar = getActionBar();

		// Set up the array adapter
		String[] titles = getResources().getStringArray(R.array.game_nav);
		mNavigationAdapter = new ArrayAdapter<String>(actionBar.getThemedContext(), android.R.layout.simple_list_item_1, android.R.id.text1, titles);

		enableNavigation();
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		// Restore the previously serialized current dropdown position.
		if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
			getActionBar().setSelectedNavigationItem(savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// Serialize the current dropdown position.
		outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getActionBar().getSelectedNavigationIndex());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.game, menu);
		return true;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		GlobalConfig.log("Destroyed");
	}

	@Override
	public boolean onNavigationItemSelected(int position, long id) {
		// When the given dropdown item is selected, show its contents in the
		// container view.
		Fragment fragment;
		switch(position){
		default:
			fragment = new EncountersFragment();
			break;
		case 1:
			fragment = new PlayersFragment();
			break;
		case 2:
			fragment = new CreaturesFragment();
			break;
		}
		Bundle args = new Bundle();
		fragment.setArguments(args);
		getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
		return true;
	}

	@Override
	public void onBackStackChanged() {

	}

		

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(event.getKeyCode() == KeyEvent.KEYCODE_BACK){
			// check for encounters being the visible navigation item
			int current = getActionBar().getSelectedNavigationIndex();
			if(current != 0){
				getActionBar().setSelectedNavigationItem(0);
			}else{
				QuickDialogs.showQuitDialog(this, new IQuitCallback() {
					
					@Override
					public void onQuitToMenu() {
						startActivity(new Intent(GameActivity.this, FrontMenuActivity.class));
						finish();
					}
					
					@Override
					public void onQuitApp() {
						finish();
					}
				});
			}
			
			return true;
		}
		
		return super.onKeyDown(keyCode, event);
	}

	


	// ===========================================================
	// TODO Methods
	// ===========================================================

	public void enableNavigation(){
		// Set up the action bar to show a dropdown list.
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

		// Set up the dropdown list navigation in the action bar.
		actionBar.setListNavigationCallbacks(mNavigationAdapter, this);
		
	}

	public void disableNavigation(){
		// Set up the action bar to show a dropdown list.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
	}


}
