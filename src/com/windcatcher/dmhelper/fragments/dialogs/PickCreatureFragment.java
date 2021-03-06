package com.windcatcher.dmhelper.fragments.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.SimpleCursorAdapter;

import com.windcatcher.dmhelper.R;
import com.windcatcher.dmhelper.SQLite.GameSQLDataSource;
import com.windcatcher.dmhelper.SQLite.tables.CreaturesTable;
import com.windcatcher.dmhelper.SQLite.tables.EncountersTable;
import com.windcatcher.dmhelper.fragments.EditEncounterFragment;

public class PickCreatureFragment extends DialogFragment{
	
	public PickCreatureFragment(EditEncounterFragment fragment, long encounterID){
		mParentFragment = fragment;
		mEncounterID = encounterID;
	}

	// ===========================================================
	// TODO Fields
	// ===========================================================
	
	private EditEncounterFragment mParentFragment;
	private long mEncounterID;
	
	// ===========================================================
	// TODO Constants
	// ===========================================================

	// ===========================================================
	// TODO Inherited Methods
	// ===========================================================
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.pick_creature);

        Cursor c = CreaturesTable.query(GameSQLDataSource.getDatabase(getActivity()));
        
        // do the list items
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(getActivity(), R.layout.list_item_edit_encounters_creatures, c, new String[] { CreaturesTable.COLUMN_NAME.getName(), CreaturesTable.COLUMN_HP.getName() }, new int[] { R.id.list_item_base_line_one, R.id.list_item_base_line_two}, SimpleCursorAdapter.NO_SELECTION);

        builder.setAdapter(adapter, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// get the ID of the selected creature
				Cursor c = CreaturesTable.query(GameSQLDataSource.getDatabase(getActivity()));
				c.moveToPosition(which);
				
				int creatureID = c.getInt(CreaturesTable.COLUMN_ID.getNum());
				
				c.close();
				
				// add this creature and refresh the creature list.
				EncountersTable.addCreature(GameSQLDataSource.getDatabase(getActivity()), mEncounterID, creatureID);
				mParentFragment.refreshList();
			}
		});
        // Create the AlertDialog object and return it
        return builder.create();
    }

	// ===========================================================
	// TODO Methods
	// ===========================================================
}
