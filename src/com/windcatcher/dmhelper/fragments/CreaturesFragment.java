package com.windcatcher.dmhelper.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.windcatcher.dmhelper.R;
import com.windcatcher.dmhelper.SQLite.GameSQLDataSource;
import com.windcatcher.dmhelper.SQLite.tables.CreaturesTable;
import com.windcatcher.dmhelper.activities.PopupFragmentActivity;

public class CreaturesFragment extends CursorListFragment {

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
	public void onCreate(Bundle savedInstanceState) {
		initMenu(R.menu.creatures_selected, R.menu.creatures);
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_simple_list, container, false);

		ListView list = (ListView)rootView.findViewById(R.id.base_list);

		Cursor c = getCursor();

		SimpleCursorAdapter adapter = new SimpleCursorAdapter(getActivity(), R.layout.list_item_edit_encounters_creatures, c, new String[] { CreaturesTable.COLUMN_NAME.getName() }, new int[] { R.id.list_item_base_line_one }, SimpleCursorAdapter.NO_SELECTION);

		initList(list, adapter);

		return rootView;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case R.id.menu_creatures_new:
			newCreature();
			return true;
		case R.id.menu_creatures_edit:
			editCreature();
			return true;
		case R.id.menu_creatures_remove:
			removeCreature();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected Cursor getCursor() {
		return CreaturesTable.query(GameSQLDataSource.getDatabase(getActivity()));
	}
	// ===========================================================
	// TODO Methods
	// ===========================================================

	private void newCreature(){
		// bring up the create creatures screen
		Intent intent = new Intent(getActivity(), PopupFragmentActivity.class);
		intent.putExtra(PopupFragmentActivity.SCREEN_TO_POP, PopupFragmentActivity.SCREEN_CREATE_CREATURE);
		getActivity().startActivity(intent);
	}

	private void editCreature(){
		long rowID = getSelectedRowID();
		
		resetSelection();
		
		// bring up the create creatures screen with a row argument
		Intent intent = new Intent(getActivity(), PopupFragmentActivity.class);
		intent.putExtra(PopupFragmentActivity.SCREEN_TO_POP, PopupFragmentActivity.SCREEN_CREATE_CREATURE);
		intent.putExtra(PopupFragmentActivity.ARGS_ROW, rowID);
		getActivity().startActivity(intent);
	}

	private void removeCreature(){
		CreaturesTable.deleteCreature(GameSQLDataSource.getDatabase(getActivity()), getSelectedRowID());

		resetSelection();

		refreshList();
	}
}
