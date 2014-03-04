package com.windcatcher.dmhelper.adapters;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.windcatcher.dmhelper.R;
import com.windcatcher.dmhelper.SQLite.tables.EncountersTable;

public class CombatAdapter extends CursorAdapter {

	public CombatAdapter(Context context, Cursor c) {
		super(context, c, false);
		mContext = context;
	}

	// ===========================================================
	// TODO Fields
	// ===========================================================

	private Context mContext;

	private int mCurrentTurnPosition = -1;

	// ===========================================================
	// TODO Constants
	// ===========================================================

	// ===========================================================
	// TODO Inherited Methods
	// ===========================================================

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		return inflater.inflate(R.layout.list_item_edit_encounters_creatures, parent, false);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		// grab all of the data
		String playerName = cursor.getString(EncountersTable.VIEW_READ_COLUMN_PLAYERNAME.getNum());
		String creatureName = cursor.getString(EncountersTable.VIEW_READ_COLUMN_CREATURENAME.getNum());
		int hp = cursor.getInt(EncountersTable.VIEW_READ_COLUMN_HP.getNum());
		int maxHP = cursor.getInt(EncountersTable.VIEW_READ_COLUMN_MAXHP.getNum());
		int init = cursor.getInt(EncountersTable.VIEW_READ_COLUMN_INIT.getNum());
		int effectsActive;
		try {
			effectsActive = new JSONArray(cursor.getString(EncountersTable.VIEW_READ_COLUMN_EFFECTS.getNum())).length();
		} catch (JSONException e) {
			effectsActive = 0;
			e.printStackTrace();
		}

		// set the name
		TextView t = (TextView)view.findViewById(R.id.list_item_base_line_one);
		// is this a player or creature
		if(playerName.equals("blank")){			
			t.setText(creatureName);
		}else{
			t.setText(playerName);
		}

		// set the hp
		t = (TextView)view.findViewById(R.id.list_item_base_line_two);
		t.setText(hp + "/" + maxHP + " --- Init: " + init);
		
		// set the number of effects active
		t = (TextView)view.findViewById(R.id.list_item_base_line_two_right);
		t.setText("Effects: " + effectsActive);
		
		// check to see if it should be highlighted because it's the current turn
		View root = view.findViewById(R.id.list_item_root);
		if(cursor.getPosition() == mCurrentTurnPosition){
			root.setBackgroundColor(R.color.list_turn_highlight);
		}else{
			root.setBackgroundResource(0);
		}
	}

	// ===========================================================
	// TODO Methods
	// ===========================================================

	public void setCurrentTurn(int turn){
		mCurrentTurnPosition = turn;
		notifyDataSetChanged();
	}
	
}
