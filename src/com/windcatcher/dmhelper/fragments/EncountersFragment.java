package com.windcatcher.dmhelper.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;

import com.windcatcher.dmhelper.R;
import com.windcatcher.dmhelper.SQLite.GameSQLDataSource;
import com.windcatcher.dmhelper.SQLite.tables.GameTable;
import com.windcatcher.dmhelper.activities.PopupFragmentActivity;
import com.windcatcher.dmhelper.adapters.EncounterAdapter;

public class EncountersFragment extends CursorListFragment {

	// ===========================================================
	// TODO Fields
	// ===========================================================

	// ===========================================================
	// TODO Constants
	// ===========================================================

	private static final int ENCOUNTERS_LOADER = 0;

	// ===========================================================
	// TODO Inherited Methods
	// ===========================================================

	@Override
	public void onCreate(Bundle savedInstanceState) {
		initMenu(R.menu.encounters_selected, R.menu.encounters);
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_simple_list, container, false);

		ListView list = (ListView) rootView.findViewById(R.id.base_list);

		Cursor c = getCursor();
		
		EncounterAdapter adapter = new EncounterAdapter(getActivity(), c);

		initList(list, adapter);


		return rootView;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case R.id.menu_encounters_delete:
			deleteEncounter(getSelectedRowID());
			return true;
		case R.id.menu_encounters_edit:
			editEncounter();
			return true;
		case R.id.menu_encounters_new:
			createEncounter();
			return true;
		case R.id.menu_encounters_run:
			runEncounter();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected Cursor getCursor() {
		return GameTable.query(GameSQLDataSource.getDatabase(getActivity()));
	}

	// ===========================================================
	// TODO Methods
	// ===========================================================
	
	private void runEncounter(){
		// grab the rowID
		long rowID = getSelectedRowID();
		
		// transition to combat
		Intent intent = new Intent(getActivity(), PopupFragmentActivity.class);
		intent.putExtra(PopupFragmentActivity.ARGS_ROW, rowID);
		intent.putExtra(PopupFragmentActivity.SCREEN_TO_POP, PopupFragmentActivity.SCREEN_COMBAT);
		getActivity().startActivity(intent);
	}

	private void editEncounter(){
		// grab the rowID
		long rowID = getSelectedRowID();

		// reset the selection
		resetSelection();

		// bring up the edit encounter screen
		Intent intent = new Intent(getActivity(), PopupFragmentActivity.class);
		intent.putExtra(PopupFragmentActivity.ARGS_ROW, rowID);
		intent.putExtra(PopupFragmentActivity.SCREEN_TO_POP, PopupFragmentActivity.SCREEN_EDIT_ENCOUNTER);
		getActivity().startActivity(intent);
	}

	private void createEncounter(){
		// create a new blank encounter
		GameTable.createNewEncounter(GameSQLDataSource.getDatabase(getActivity()));

		// re-load encounter data in to a new cursor and refresh the list
		refreshList();
	}

	private void deleteEncounter(long rowID){
		// delete the encounter
		GameTable.deleteEncounter(GameSQLDataSource.getDatabase(getActivity()), rowID);

		// reset the selection and invalidate the menu
		resetSelection();

		// re-load encounter data in to a new cursor and refresh the list
		refreshList();
	}

	private int getItemRowID(AdapterView<?> view, int position){
		// grab the cursor from the listview
		Cursor c = ((CursorAdapter)view.getAdapter()).getCursor();
		// move to the selected position
		c.moveToPosition(position);
		// grab the actual rowID of the item
		return c.getInt(GameTable.COLUMN_ID.getNum());
	}
}
