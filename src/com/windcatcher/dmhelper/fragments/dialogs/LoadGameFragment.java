package com.windcatcher.dmhelper.fragments.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.windcatcher.dmhelper.GlobalConfig;
import com.windcatcher.dmhelper.R;
import com.windcatcher.dmhelper.SQLite.GameSQLDataSource;

public class LoadGameFragment extends DialogFragment{

	public LoadGameFragment(Activity activity){
		mActivity = activity;
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
		// get the list of games
		final String[] games = formatGameList(GameSQLDataSource.getSavedGames());
		// format the game list

		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.load_gamename)
		.setNegativeButton(R.string.cancel, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// cancelled
			}
		})
		.setItems(games, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// grab the game name
				String gameName = games[which];
				// store it in the shared preferences for the next load
				Editor prefs = getActivity().getSharedPreferences(GlobalConfig.PREFS, Activity.MODE_PRIVATE).edit();
				prefs.putString(GlobalConfig.LOADED_GAME, gameName);
				
				// load up the game.
				GameSQLDataSource.openGame(getActivity(), gameName);
					// close the main menu
				mActivity.finish();
				mActivity = null;
			
			}
		});
		// Create the AlertDialog object and return it
		return builder.create();
	}

	// ===========================================================
	// TODO Methods
	// ===========================================================

	private String[] formatGameList(String[] input){
		String[] output = new String[input.length];
		for(int i = 0; i < input.length; i++){
			output[i] = input[i].substring(0, input[i].length() - 3);
		}
		return output;
	}
}
