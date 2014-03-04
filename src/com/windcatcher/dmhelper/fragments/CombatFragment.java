package com.windcatcher.dmhelper.fragments;

import java.io.File;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.windcatcher.dmhelper.GlobalConfig;
import com.windcatcher.dmhelper.R;
import com.windcatcher.dmhelper.SQLite.GameSQLDataSource;
import com.windcatcher.dmhelper.SQLite.tables.CreaturesTable;
import com.windcatcher.dmhelper.SQLite.tables.EncountersTable;
import com.windcatcher.dmhelper.SQLite.tables.GameTable;
import com.windcatcher.dmhelper.SQLite.tables.PlayersTable;
import com.windcatcher.dmhelper.activities.GameActivity;
import com.windcatcher.dmhelper.adapters.CombatAdapter;
import com.windcatcher.dmhelper.dialogs.QuickDialogs;
import com.windcatcher.dmhelper.dialogs.QuickDialogs.IInputCallback;
import com.windcatcher.dmhelper.fragments.dialogs.CreateEffectFragment;
import com.windcatcher.dmhelper.fragments.dialogs.CreateEffectFragment.ICreateEffectCallback;
import com.windcatcher.dmhelper.fragments.dialogs.PickEffectFragment;
import com.windcatcher.dmhelper.fragments.dialogs.PickEffectFragment.IEffectSelectCallback;
import com.windcatcher.dmhelper.views.TurnView;

public class CombatFragment extends CursorListFragment {

	private CombatFragment(){
		
	}

	// ===========================================================
	// TODO Fields
	// ===========================================================

	private TurnView mTurnIndicator;
	private long mEncounterRowID;
	
	private TextView mTurn, mRound;


	// ===========================================================
	// TODO Constants
	// ===========================================================

	private static final String ARGS_ROW_ID = "rowid";

	// ===========================================================
	// TODO Inherited Methods
	// ===========================================================

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// store the encounter row ID and then initialize the encounter
		mEncounterRowID = getArguments().getLong(ARGS_ROW_ID);
		initEncounter();

		initMenu(R.menu.combat_selected, R.menu.combat);

		super.onCreate(savedInstanceState);
	}
	
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_combat, container, false);

		mTurnIndicator = (TurnView) view.findViewById(R.id.combat_turnindicator);
		mTurn = (TextView) view.findViewById(R.id.combat_turn);
		mRound = (TextView) view.findViewById(R.id.combat_round);

		ListView list = (ListView) view.findViewById(R.id.base_list);

		Cursor c = getCursor();

		// grab the initiatives from the cursor
		ArrayList<Short> turns = new ArrayList<Short>();
		while(c.moveToNext()){
			short turn = c.getShort(EncountersTable.VIEW_READ_COLUMN_INIT.getNum());
			turns.add(turn);
		}
		// init the turnView
		mTurnIndicator.init(turns);

		CombatAdapter adapter = new CombatAdapter(getActivity(), c);
		// find out what the current turn is
		Cursor gameCursor = GameTable.query(GameSQLDataSource.getDatabase(getActivity()), mEncounterRowID);
		int currentTurn, currentRound;
		if(gameCursor.moveToFirst()){
			// get the current turn
			currentTurn = gameCursor.getInt(GameTable.COLUMN_TURN.getNum());
			currentRound = gameCursor.getInt(GameTable.COLUMN_ROUND.getNum());
			// highlight the current combatant and set the indicator
			adapter.setCurrentTurn(currentTurn);
			int currentInit = mTurnIndicator.setCurrentTurnCount(currentTurn);
			// set the text views
			mTurn.setText(currentInit + "");
			mRound.setText(currentRound + "");
		}

		gameCursor.close();

		initList(list, adapter);

		list.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
			                               int position, long id) {
				String path = CreaturesTable.getCreatureImagePathFromEncounter(GameSQLDataSource.getDatabase(getActivity()), id);
				if(path != null){					
					Intent intent = new Intent();
					intent.setAction(android.content.Intent.ACTION_VIEW);
					intent.setDataAndType(Uri.fromFile(new File(path)), "image/jpg");
					startActivity(intent);
				}else{
					return false;
				}

				return true;
			}
		});

		return view;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case R.id.combat_harm:
			harm();
			return true;
		case R.id.combat_heal:
			heal();
			return true;
		case R.id.combat_previous:
			previousTurn();
			return true;
		case R.id.combat_remove_effect:
			removeEffect();
			return true;
		case R.id.combat_add_effect:
			addEffect();
			return true;
		case R.id.combat_next:
			nextTurn();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected Cursor getCursor() {
		return EncountersTable.getRunningEncounterInfo(GameSQLDataSource.getDatabase(getActivity()), mEncounterRowID);
	}

	// ===========================================================
	// TODO Methods
	// ===========================================================

	private void harm(){
		// grab the selected ID
		final long rowID = getSelectedRowID();

		// reset the selection
		resetSelection();

		// show the dialog
		QuickDialogs.showHarmDialog(getActivity(), new IInputCallback() {

			@Override
			public void onInput(String input) {
				int damage;
				// convert the string to int
				try{
					damage = Integer.valueOf(input);
				}catch(NumberFormatException e){
					// TODO display incorrect input dialog
					damage = 0;
				}

				// deal dat damage!
				EncountersTable.changeHP(GameSQLDataSource.getDatabase(getActivity()), rowID, -damage);

				refreshList();
			}
		});
	}

	private void heal(){
		// grab the selected ID
		final long rowID = getSelectedRowID();

		// reset the selection
		resetSelection();

		// show the dialog
		QuickDialogs.showHealDialog(getActivity(), new IInputCallback() {

			@Override
			public void onInput(String input) {
				int damage;
				// convert the string to int
				try{
					damage = Integer.valueOf(input);
				}catch(NumberFormatException e){
					// TODO display incorrect input dialog
					damage = 0;
				}

				// heal it up!
				EncountersTable.changeHP(GameSQLDataSource.getDatabase(getActivity()), rowID, damage);	

				refreshList();
			}
		});
	}

	private void addEffect(){
		final long rowID = getSelectedRowID();
		// show the picking effect dialog
		PickEffectFragment frag = PickEffectFragment.newInstance(new IEffectSelectCallback() {
			
			@Override
			public void onEffectSelected(long effectID) {
				EncountersTable.addEffect(GameSQLDataSource.getDatabase(getActivity()), rowID, effectID);
				refreshList();
			}

			@Override
			public void onNewEffectSelected() {
				// if the new effect option is selected, this dialog will be dismissed and the new effect dialog will be shown
				CreateEffectFragment newFrag = CreateEffectFragment.newInstance(new ICreateEffectCallback() {
					
					@Override
					public void onSaveEffect(long effectID) {
						// when the new effect is created, add it to the selected creature
						EncountersTable.addEffect(GameSQLDataSource.getDatabase(getActivity()), rowID, effectID);
						refreshList();
					}
				}, null);
				
				newFrag.show(getFragmentManager(), null);
			}
		});
		
		frag.show(getFragmentManager(), null);
		resetSelection();
	}

	private void removeEffect(){
		final long rowID = getSelectedRowID();
		
		resetSelection();
		
		PickEffectFragment frag = PickEffectFragment.newInstance(rowID, new IEffectSelectCallback() {
			
			@Override
			public void onNewEffectSelected() {}
			
			@Override
			public void onEffectSelected(long effectID) {
				EncountersTable.removeEffect(GameSQLDataSource.getDatabase(getActivity()), rowID, effectID - 1);
			}
		});
		
		frag.show(getFragmentManager(), null);
	}

	private void previousTurn(){
		int currentTurnPosition = mTurnIndicator.previousTurn();
		if(currentTurnPosition > -1){
			CombatAdapter adapter = (CombatAdapter) getList().getAdapter();
			adapter.setCurrentTurn(currentTurnPosition);
			GameTable.setTurn(GameSQLDataSource.getDatabase(getActivity()), mEncounterRowID, currentTurnPosition);
		}
	}

	private void nextTurn(){
		// iterate the turn on the indicator and grab the current turn
		int[] currentTurnInfo = mTurnIndicator.nextTurn();  
		if(currentTurnInfo[TurnView.TURN_POSITION] > -1){
			// highlight the current player
			CombatAdapter adapter = (CombatAdapter) getList().getAdapter();
			adapter.setCurrentTurn(currentTurnInfo[TurnView.TURN_POSITION]);
			// change the turn in the game table
			GameTable.setTurn(GameSQLDataSource.getDatabase(getActivity()), mEncounterRowID, currentTurnInfo[TurnView.TURN_POSITION]);
			// set the label
			mTurn.setText(currentTurnInfo[TurnView.TURN_INIT] + "");
			// increment the round if necessary
			if(currentTurnInfo[TurnView.TURN_POSITION] == 0){
				int round = GameTable.addRound(GameSQLDataSource.getDatabase(getActivity()), mEncounterRowID);
				mRound.setText(round + "");
			}
		}
	}

	private void initEncounter(){
		SQLiteDatabase database = GameSQLDataSource.getDatabase(getActivity());
		// check to see if the encounter is already running
		boolean isRunning = GameTable.isRunning(database, mEncounterRowID);

		if(!isRunning){	
			// roll initatives for monsters
			// grab all of the monsters in the encounter
			Cursor c = EncountersTable.getEditEncounterInfo(database, mEncounterRowID);
			// check for 0 creatures
			if(c.getCount() == 0){
				c.close();
				return; // just back out of the initalization
			}
			// cycle through them and set their init
			while(c.moveToNext()){
				// get the row id of the encounter entry
				int creatureRowID = c.getInt(EncountersTable.COLUMN_ID.getNum());
				// roll a d20 and add the init modifier
				int initMod = c.getInt(EncountersTable.VIEW_EDIT_COLUMN_INIT_MOD.getNum());
				int init = GlobalConfig.RANDY.nextInt(20) + 1 + initMod;
				ContentValues values = new ContentValues();
				values.put(EncountersTable.COLUMN_INIT.getName(), init);
				// update the table
				EncountersTable.update(database, creatureRowID, values);
			}

			c.close();		
			// add the players to the mix
			c = PlayersTable.query(database);
			if(c.moveToFirst())
				do{
					// get the player id
					int playerID = c.getInt(PlayersTable.COLUMN_ID.getNum());
					// TODO get proper inits from dialogs
					// add it to the encounter
					EncountersTable.addPlayer(database, mEncounterRowID, playerID, GlobalConfig.RANDY.nextInt(20) + 1);
				}while(c.moveToNext());

			GameTable.setRunning(database, mEncounterRowID, true);
		}
	}

	public static CombatFragment newInstance(long encounterRowID){
		CombatFragment frag = new CombatFragment();

		Bundle args = new Bundle();
		args.putLong(ARGS_ROW_ID, encounterRowID);

		frag.setArguments(args);

		return frag;
	}

	public long getEncounterRowID(){
		return mEncounterRowID;
	}

	/**
	 * 
	 * @return Will return true if the fragment de-activated fully
	 */
	public void onBack(){
		// create an alert dialog to see if the user wants to reset the encounter or save it's state
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.reset_combat)
		.setPositiveButton(R.string.okay, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// reset the encounter
				EncountersTable.resetEncounter(GameSQLDataSource.getDatabase(getActivity()), mEncounterRowID);

				// remove the navigation bar from the main activity
				GameActivity act = (GameActivity) getActivity();
				act.enableNavigation();

				act.finish();
			}
		})
		.setNegativeButton(R.string.no, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// remove the navigation bar from the main activity
				GameActivity act = (GameActivity) getActivity();
				act.enableNavigation();

				act.finish();
			}
		});
		builder.create().show();
	}

}
