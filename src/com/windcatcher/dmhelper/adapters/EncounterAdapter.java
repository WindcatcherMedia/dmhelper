package com.windcatcher.dmhelper.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.windcatcher.dmhelper.R;
import com.windcatcher.dmhelper.SQLite.GameSQLDataSource;
import com.windcatcher.dmhelper.SQLite.tables.EncountersTable;
import com.windcatcher.dmhelper.SQLite.tables.GameTable;

public class EncounterAdapter extends CursorAdapter {
	
	public EncounterAdapter(Context context, Cursor c) {
		super(context, c, false);
		mContext = context;
	}
	
	// ===========================================================
	// TODO Fields
	// ===========================================================

	private Context mContext;
	
	// ===========================================================
	// TODO Constants
	// ===========================================================

	// ===========================================================
	// TODO Inherited Methods
	// ===========================================================

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		return inflater.inflate(R.layout.list_item_base_two_line, parent, false);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		int rowID = cursor.getInt(GameTable.COLUMN_ID.getNum());
		// set the list item info from the cursor
		// set the encounter title
		TextView t = (TextView)view.findViewById(R.id.list_item_base_line_one);
		t.setText(String.format(t.getText().toString(), rowID + ""));
		
		// set the creature count
		int count = EncountersTable.getCreatureCount(GameSQLDataSource.getDatabase(mContext), rowID);
		t = (TextView)view.findViewById(R.id.list_item_base_line_two);
		t.setText(String.format(t.getText().toString(), count +""));
	}

	// ===========================================================
	// TODO Methods
	// ===========================================================
	
}
