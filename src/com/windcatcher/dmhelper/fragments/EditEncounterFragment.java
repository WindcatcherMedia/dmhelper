package com.windcatcher.dmhelper.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.windcatcher.dmhelper.R;
import com.windcatcher.dmhelper.SQLite.GameSQLDataSource;
import com.windcatcher.dmhelper.SQLite.tables.EncounterTable;
import com.windcatcher.dmhelper.dialogs.CreaturePickFragment;

public class EditEncounterFragment extends CursorListFragment {

	private EditEncounterFragment(){}

	// ===========================================================
	// TODO Fields
	// ===========================================================

	private long mEncounterRowID;
	private static int mFragmentCount = 0;

	private static EditEncounterFragment activeInstance;

	// ===========================================================
	// TODO Constants
	// ===========================================================

	private static final int CREATURES_LOADER = 0;

	private static final String ARGS_ROW_ID = "rowid";

	// ===========================================================
	// TODO Inherited Methods
	// ===========================================================

	@Override
	public void onCreate(Bundle savedInstanceState) {
		mEncounterRowID = getArguments().getLong(ARGS_ROW_ID);

		initMenu(R.menu.encounter_edit_selected, R.menu.encounter_edit);
		
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_edit_encounter, container, false);

		ListView list = (ListView) rootView.findViewById(R.id.edit_encounter_creature_list);

		Cursor c = getCursor();

		int count = c.getCount();

		SimpleCursorAdapter adapter = new SimpleCursorAdapter(getActivity(), R.layout.list_item_edit_encounters_creatures, c, new String[] { EncounterTable.VIEW_EDIT_COLUMN_CREATURENAME.getName(), EncounterTable.VIEW_EDIT_COLUMN_HP.getName() }, new int[] { R.id.list_item_base_line_one, R.id.list_item_base_line_two}, SimpleCursorAdapter.NO_SELECTION);


		initList(list, adapter);


		return rootView;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case R.id.menu_encounter_edit_add:
			addCreature();
			return true;
		case R.id.menu_encounter_edit_remove:
			removeCreature();
			return true;
		case R.id.menu_encounter_edit_edit:
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * This method will load up the new cursor data for the list
	 */

	@Override
	protected Cursor getCursor() {
		return EncounterTable.getEditEncounterInfo(GameSQLDataSource.getDatabase(getActivity()), mEncounterRowID);
	}

	// ===========================================================
	// TODO Methods
	// ===========================================================

	private void addCreature(){
		new CreaturePickFragment(this, mEncounterRowID).show(getFragmentManager(), null);
		
		refreshList();
	}

	private void editCreature(){

	}

	private void removeCreature(){
		EncounterTable.removeCreatures(GameSQLDataSource.getDatabase(getActivity()), getSelectedRowID());

		refreshList();
		
		resetSelection();
	}

	public static EditEncounterFragment newInstance(long rowID){
		EditEncounterFragment frag = new EditEncounterFragment();

		Bundle args = new Bundle();
		args.putLong(ARGS_ROW_ID, rowID);

		frag.setArguments(args);

		return frag;
	}
}
