package com.windcatcher.dmhelper;


import android.database.Cursor;
import android.os.AsyncTask;

import com.windcatcher.dmhelper.AsyncCursorLoader.IAsyncCursorCallback;

public class AsyncCursorLoader extends AsyncTask<IAsyncCursorCallback, Integer, Cursor> {

	// ===========================================================
	// TODO Fields
	// ===========================================================

	IAsyncCursorCallback[] _params;
	
	// ===========================================================
	// Inherited Methods
	// ===========================================================

	@Override
	protected Cursor doInBackground(IAsyncCursorCallback... params) {
		this._params = params;
		
		Cursor c = params[0].loadCursor();

		return c;
	}
	
	@Override
	protected void onPostExecute(Cursor result) {
		_params[0].onCursorLoaded(result);
    }
	
	public interface IAsyncCursorCallback {
		// ===========================================================
		// TODO Methods
		// ===========================================================

		public abstract Cursor loadCursor();
		
		public abstract void onCursorLoaded(Cursor c);
		
	}
}
