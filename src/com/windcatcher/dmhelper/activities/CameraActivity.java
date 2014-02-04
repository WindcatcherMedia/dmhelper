package com.windcatcher.dmhelper.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.windcatcher.dmhelper.R;
import com.windcatcher.dmhelper.fragments.MyCameraFragment;

public class CameraActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_base_fragment);
		
		String creatureName = getIntent().getStringExtra(ARGS_NAME);
		
		MyCameraFragment fragment = MyCameraFragment.newInstance(this, creatureName, ARGS_TYPE_CREATURE);
		
		getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
	}

	// ===========================================================
	// TODO Fields
	// ===========================================================

	// ===========================================================
	// TODO Constants
	// ===========================================================

	public static final String DATA_PATH = "path";
	
	public static final int ARGS_TYPE_CREATURE = 1;
	
	public static final String ARGS_NAME = "name";
	public static final String ARGS_TYPE = "type";
	
	// ===========================================================
	// TODO Inherited Methods
	// ===========================================================

	// ===========================================================
	// TODO Methods
	// ===========================================================
}
