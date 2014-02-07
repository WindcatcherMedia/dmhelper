package com.windcatcher.dmhelper.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.windcatcher.dmhelper.R;
import com.windcatcher.dmhelper.SQLite.GameSQLDataSource;
import com.windcatcher.dmhelper.SQLite.tables.PlayersTable;
import com.windcatcher.dmhelper.dialogs.QuickDialogs;
import com.windcatcher.dmhelper.dialogs.QuickDialogs.FieldNames;

public class CreatePlayerFragment extends Fragment {
	
	private CreatePlayerFragment(){}

	// ===========================================================
	// TODO Fields
	// ===========================================================
	
	private EditText mCharName;
	private EditText mPlayerName;
	
	// ===========================================================
	// TODO Constants
	// ===========================================================

	public static final String EXISTING_ROW_ID = "existing";
	
	// ===========================================================
	// TODO Inherited Methods
	// ===========================================================

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// inflate the view
		View v = inflater.inflate(R.layout.fragment_create_player, null); 

		// grab the edit text references
		mCharName = (EditText)v.findViewById(R.id.create_character_name);
		mPlayerName = (EditText)v.findViewById(R.id.create_player_name);



		// check for existing row argument which signifies that this is modifying an existing creature
		int existingRow = getArguments().getInt(EXISTING_ROW_ID);
		if(existingRow != -1){
			// row exists, grab the information and populate the fields
			Cursor c = PlayersTable.query(GameSQLDataSource.getDatabase(getActivity()), existingRow);
			if(c.moveToFirst()){
				mCharName.setText(c.getString(PlayersTable.COLUMN_NAME.getNum()));
				mPlayerName.setText(c.getString(PlayersTable.COLUMN_PLAYER_NAME.getNum()));
			}
		}
		
		return v;
	}


	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.create_player, menu);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case R.id.menu_create_player_save:
			savePlayer();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	// ===========================================================
	// TODO Methods
	// ===========================================================
	
	public static CreatePlayerFragment newInstance(long existingRow){
		CreatePlayerFragment frag = new CreatePlayerFragment();
		
		Bundle args = new Bundle();
		args.putLong(EXISTING_ROW_ID, existingRow);
		
		frag.setArguments(args);
		
		return frag;
	}
	
	private void savePlayer(){
		// sanitize
		final String name = mCharName.getText().toString();
		if(name == null || name.isEmpty()){
			QuickDialogs.showFieldNotNullDialog(getActivity(), FieldNames.Name);
			return;
		}
		final String playerName = mPlayerName.getText().toString();
		if(playerName == null || playerName.isEmpty()){
			QuickDialogs.showFieldNotNullDialog(getActivity(), FieldNames.Name);
			return;
		}
		
		// check for existing
		if(PlayersTable.exists(GameSQLDataSource.getDatabase(getActivity()), name)){
			QuickDialogs.showNameExistsDialog(getActivity());
			return;
		}
		
		// save the player
		PlayersTable.addPlayer(GameSQLDataSource.getDatabase(getActivity()), name, playerName);
		
		getActivity().finish();
	}
}
