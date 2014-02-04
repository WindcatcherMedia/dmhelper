package com.windcatcher.dmhelper.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CursorAdapter;
import android.widget.ListView;

import com.windcatcher.dmhelper.AsyncCursorLoader.IAsyncCursorCallback;
import com.windcatcher.dmhelper.AsyncCursorLoader;
import com.windcatcher.dmhelper.R;

public abstract class CursorListFragment extends Fragment implements OnItemClickListener, IAsyncCursorCallback {
	

	// ===========================================================
	// TODO Fields
	// ===========================================================

	private ListView mList;

	private int mSelectedListPosition = -1;
	private long mSelectedCursorRowID = -1;
	
	private int mMenuUnselected;
	private int mMenuSelected;
	
	private boolean mListInitialized = false;
	private boolean mMenuInitialized = false;

	// ===========================================================
	// TODO Constants
	// ===========================================================

	// ===========================================================
	// TODO Inherited Methods
	// ===========================================================

	@Override
	public void onCreate(Bundle savedInstanceState) {
		if(!mMenuInitialized){
			throw new Error("Menu not initialized!!");
		}
		setHasOptionsMenu(true);
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if(!mListInitialized){
			throw new Error("List not initialized!");
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		
		refreshList();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// store the selected position, or de-select if it's already selected.
		if(mSelectedListPosition == position){
			mSelectedListPosition = -1;
			mSelectedCursorRowID = -1;
			mList.setItemChecked(position, false);
		}else{
			// store the selected list position
			mSelectedListPosition = position;
			// store the selected item's rowID
			mSelectedCursorRowID = id;
		}
		// refresh the options menu.
		getActivity().invalidateOptionsMenu();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		if(mSelectedListPosition >= 0){
			inflater.inflate(mMenuSelected, menu);
		}else{
			inflater.inflate(mMenuUnselected, menu);
		}
	}
	
	@Override
	public void onCursorLoaded(Cursor c){
		CursorAdapter adapter = (CursorAdapter)mList.getAdapter();
		adapter.changeCursor(c);
		mList.setAdapter(adapter);
	}
	
	@Override
	public Cursor loadCursor() {
		return getCursor();
	}

	// ===========================================================
	// TODO Methods
	// ===========================================================

	public void refreshList(){
		// re-load data in to a new cursor and refresh the list
		new AsyncCursorLoader().execute(this);
	}
	
	protected abstract Cursor getCursor();
	
	protected void initMenu(int menuSelected, int menuUnselected){
		mMenuSelected = menuSelected;
		mMenuUnselected = menuUnselected;
		
		mMenuInitialized = true;
	}
	
	protected void initList(ListView list, CursorAdapter adapter){
		mList = list;
		mList.setOnItemClickListener(this);
		mList.setSelector(R.drawable.list_highlight);
		mList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

		mList.setAdapter(adapter);
		
		mListInitialized = true;
	}
	
	protected ListView getList(){
		return mList;
	}
	
	protected int getSelectedPosition(){
		return mSelectedListPosition;
	}
	
	protected long getSelectedRowID(){
		return mSelectedCursorRowID;
	}

	protected void resetSelection(){
		mSelectedCursorRowID = -1;
		mSelectedListPosition = -1;
		getActivity().invalidateOptionsMenu();
	}
}
