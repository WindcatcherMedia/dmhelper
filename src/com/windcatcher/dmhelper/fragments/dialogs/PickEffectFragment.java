package com.windcatcher.dmhelper.fragments.dialogs;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

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
import com.windcatcher.dmhelper.SQLite.tables.EffectsTable;
import com.windcatcher.dmhelper.SQLite.tables.EncountersTable;

public class PickEffectFragment extends DialogFragment {

	/**
	 * Block this fragment from being instantiated. Make sure that it is only created with newInstance()
	 */
	private PickEffectFragment(){}

	// ===========================================================
	// TODO Fields
	// ===========================================================

	private static IEffectSelectCallback mCallback;

	// ===========================================================
	// TODO Constants
	// ===========================================================

	private static final String ARGS_ENCOUNTER_ROW_ID = "rowid";


	// ===========================================================
	// TODO Inherited Methods
	// ===========================================================

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.pick_effect);

		long encounterRowID = getArguments().getLong(ARGS_ENCOUNTER_ROW_ID);
		// grab either a list of the saved effects, or the effects on a creature
		Cursor c;
		SimpleCursorAdapter adapter;
		if(encounterRowID > -1){
			// grab the effects on the creature
			c = EncountersTable.getRunningEncounterInfo(GameSQLDataSource.getDatabase(getActivity()), encounterRowID);
			c.moveToFirst();
			try {
				// grab the json array of effects
				JSONArray effectsJson = new JSONArray(c.getString(EncountersTable.VIEW_READ_COLUMN_EFFECTS.getNum()));
				// close up the previous cursor
				c.close();

				// store the effects in a list of long values
				int count = effectsJson.length();
				ArrayList<Long> effectsList = new ArrayList<Long>();
				for(int i = 0; i < count; i ++){
					effectsList.add(effectsJson.getLong(i));
				}

				// use the list to get a cursor from the effects table
				c = EffectsTable.query(GameSQLDataSource.getDatabase(getActivity()), effectsList);

			} catch (JSONException e) {
				c.close();

				c = null;
				e.printStackTrace(); 
			}

			adapter = new SimpleCursorAdapter(getActivity(), R.layout.list_item_edit_encounters_creatures, c, new String[] { EffectsTable.COLUMN_NAME.getName(), EffectsTable.COLUMN_DESCRIPTION.getName() }, new int[] { R.id.list_item_base_line_one, R.id.list_item_base_line_two}, SimpleCursorAdapter.NO_SELECTION);
		}else{
			c = EffectsTable.query(GameSQLDataSource.getDatabase(getActivity()));
			adapter = new SimpleCursorAdapter(getActivity(), R.layout.list_item_edit_encounters_creatures, c, new String[] { EffectsTable.COLUMN_NAME.getName(), EffectsTable.COLUMN_DESCRIPTION.getName() }, new int[] { R.id.list_item_base_line_one, R.id.list_item_base_line_two}, SimpleCursorAdapter.NO_SELECTION);
		}


		builder.setAdapter(adapter, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// grab the effect ID from the adapter
				long effect = which;

				if(mCallback != null){
					mCallback.onEffectSelected(effect);
				}
				mCallback = null;
			}
		})
		.setNegativeButton(R.string.close, null);
		if(encounterRowID < 0){
			builder.setNeutralButton(R.string.new_effect, new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					if(mCallback != null){
						mCallback.onNewEffectSelected();
					}
					mCallback = null;
				}
			});
		}
		return builder.create();
	}

	// ===========================================================
	// TODO Methods
	// ===========================================================

	/**
	 * Creates a dialog displaying the effects that are currently on a creature
	 * @param rowID Row ID of a creature in an Encounter
	 * @param callback
	 * @return
	 */
	public static PickEffectFragment newInstance(long rowID, IEffectSelectCallback callback){
		mCallback = callback;

		PickEffectFragment frag = new PickEffectFragment();

		Bundle args = new Bundle();
		args.putLong(ARGS_ENCOUNTER_ROW_ID, rowID);
		frag.setArguments(args);

		return frag;
	}

	/**
	 * Creates a dialog displaying all of the effects saved in the database
	 * @param callback
	 * @return
	 */
	public static PickEffectFragment newInstance(IEffectSelectCallback callback){
		mCallback = callback;

		PickEffectFragment frag = new PickEffectFragment();

		Bundle args = new Bundle();
		args.putLong(ARGS_ENCOUNTER_ROW_ID, -1);
		frag.setArguments(args);

		return frag;
	}

	// ===========================================================
	// TODO Interface
	// ===========================================================

	public interface IEffectSelectCallback{

		public void onEffectSelected(long effectID);

		public void onNewEffectSelected();
	}
}
