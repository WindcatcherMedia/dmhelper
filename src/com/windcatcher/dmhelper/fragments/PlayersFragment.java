package com.windcatcher.dmhelper.fragments;

import android.content.Intent;
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
import com.windcatcher.dmhelper.SQLite.tables.PlayersTable;
import com.windcatcher.dmhelper.activities.PopupFragmentActivity;

public class PlayersFragment extends CursorListFragment {

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
		initMenu(R.menu.players_selected, R.menu.players);

		setHasOptionsMenu(true);

		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_simple_list, container, false);

		ListView list = (ListView) rootView.findViewById(R.id.base_list);

		Cursor c = getCursor();

		SimpleCursorAdapter adapter = new SimpleCursorAdapter(getActivity(), R.layout.list_item_base_two_line, c, new String[] { PlayersTable.COLUMN_NAME.getName(), PlayersTable.COLUMN_PLAYER_NAME.getName() }, new int[] { R.id.list_item_base_line_one, R.id.list_item_base_line_two }, SimpleCursorAdapter.NO_SELECTION);

		initList(list, adapter);

		return rootView;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case R.id.menu_players_new:
			newPlayer();
			return true;
		case R.id.menu_players_remove:
			removePlayer();
			return true;
		case R.id.menu_players_edit:
			editPlayer();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected Cursor getCursor() {
		return PlayersTable.query(GameSQLDataSource.getDatabase(getActivity()));
	}

	// ===========================================================
	// TODO Methods
	// ===========================================================

	private void newPlayer(){
		Intent i = new Intent(getActivity(), PopupFragmentActivity.class);
		i.putExtra(PopupFragmentActivity.SCREEN_TO_POP, PopupFragmentActivity.SCREEN_CREATE_PLAYER);
		startActivity(i);
	}

	private void editPlayer(){
		long rowID = getSelectedRowID();

		resetSelection();

		// bring up the create creatures screen with a row argument
		Intent intent = new Intent(getActivity(), PopupFragmentActivity.class);
		intent.putExtra(PopupFragmentActivity.SCREEN_TO_POP, PopupFragmentActivity.SCREEN_CREATE_PLAYER);
		intent.putExtra(PopupFragmentActivity.ARGS_ROW, rowID);
		getActivity().startActivity(intent);
	}

	private void removePlayer(){
		// TODO confirm remove dialog!

		PlayersTable.removePlayer(GameSQLDataSource.getDatabase(getActivity()), getSelectedRowID());

		resetSelection();
		refreshList();
	}
}
