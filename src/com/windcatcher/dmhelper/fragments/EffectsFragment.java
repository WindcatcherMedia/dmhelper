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
import com.windcatcher.dmhelper.SQLite.tables.EffectsTable;
import com.windcatcher.dmhelper.fragments.dialogs.CreateEffectFragment;

public class EffectsFragment extends CursorListFragment {

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
		initMenu(R.menu.effects_selected, R.menu.effects);

		setHasOptionsMenu(true);

		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_simple_list, container, false);

		ListView list = (ListView) rootView.findViewById(R.id.base_list);

		Cursor c = getCursor();

		SimpleCursorAdapter adapter = new SimpleCursorAdapter(getActivity(), R.layout.list_item_base_two_line, c, new String[] { EffectsTable.COLUMN_NAME.getName(), EffectsTable.COLUMN_DESCRIPTION.getName() }, new int[] { R.id.list_item_base_line_one, R.id.list_item_base_line_two }, SimpleCursorAdapter.NO_SELECTION);

		initList(list, adapter);

		return rootView;
	}

	@Override
	protected Cursor getCursor() { 
		return EffectsTable.query(GameSQLDataSource.getDatabase(getActivity()));
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case R.id.effects_new:
			newEffect();
			return true;
		case R.id.effects_remove:
			removeEffect();
			return true;
		case R.id.effects_edit:
			editEffect();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	// ===========================================================
	// TODO Methods
	// ===========================================================
	
	private void newEffect(){
		CreateEffectFragment eff = CreateEffectFragment.newInstance(null, this);
		eff.show(getFragmentManager(), "dialog");
	}

	private void editEffect(){
		CreateEffectFragment eff = CreateEffectFragment.newInstance(null, this, getSelectedRowID());
		eff.show(getFragmentManager(), "dialog");
		
		resetSelection();
	}

	private void removeEffect(){
		// TODO confirm remove dialog!

		EffectsTable.removeEffect(GameSQLDataSource.getDatabase(getActivity()), getSelectedRowID());

		resetSelection();
		refreshList();
	}
}
