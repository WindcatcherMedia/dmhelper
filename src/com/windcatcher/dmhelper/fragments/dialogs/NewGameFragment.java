package com.windcatcher.dmhelper.fragments.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.EditText;

import com.windcatcher.dmhelper.R;
import com.windcatcher.dmhelper.SQLite.GameSQLDataSource;

public class NewGameFragment extends DialogFragment{

	public NewGameFragment(Activity fragment){
		mActivity = fragment;
	}
	
	// ===========================================================
	// TODO Fields
	// ===========================================================
	
	private Activity mActivity;
	
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
        builder.setTitle(R.string.new_gamename);
        // create a text view to name the game with
        final EditText tv = new EditText(getActivity());
        
        builder.setView(tv)
        .setPositiveButton(R.string.okay, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// grab the game name and fire it back to the main menu activity
				// TODO input checking
				String gameName = tv.getText().toString();
				GameSQLDataSource.openGame(getActivity(), gameName);
				
				// close the main menu
				mActivity.finish();
				mActivity = null;
			}
		})
		.setNegativeButton(R.string.cancel, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// cancelled
			}
		});
        // Create the AlertDialog object and return it
        return builder.create();
    }

	// ===========================================================
	// TODO Methods
	// ===========================================================
}
