package com.windcatcher.dmhelper.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.windcatcher.dmhelper.R;
import com.windcatcher.dmhelper.fragments.dialogs.LoadGameFragment;
import com.windcatcher.dmhelper.fragments.dialogs.NewGameFragment;

public class FrontMenuActivity extends FragmentActivity implements OnClickListener{

	// ===========================================================
	// TODO Fields
	// ===========================================================

	// ===========================================================
	// TODO Constants
	// ===========================================================

	private static final int NEW = 0, LOAD = 1;
	
	// ===========================================================
	// TODO Inherited Methods
	// ===========================================================
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mainmenu);
		
		// set up the buttons
		// new game
		Button newgame = (Button)findViewById(R.id.main_newgame);
		newgame.setOnClickListener(this);
		
		Button loadGame = (Button)findViewById(R.id.main_loadgame);
		loadGame.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.main_newgame:
			new NewGameFragment(this).show(getSupportFragmentManager(), "NewGame");
			break;
		case R.id.main_loadgame:
			new LoadGameFragment(this).show(getSupportFragmentManager(), "LoadGame");
			break;
		}
	}
	
	// ===========================================================
	// TODO Methods
	// ===========================================================
	
}
