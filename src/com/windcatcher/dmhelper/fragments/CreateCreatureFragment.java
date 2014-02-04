package com.windcatcher.dmhelper.fragments;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.MediaColumns;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.windcatcher.dmhelper.GlobalConfig;
import com.windcatcher.dmhelper.R;
import com.windcatcher.dmhelper.SQLite.GameSQLDataSource;
import com.windcatcher.dmhelper.SQLite.tables.CreaturesTable;
import com.windcatcher.dmhelper.activities.CameraActivity;
import com.windcatcher.dmhelper.camera.CreatureCameraHost;
import com.windcatcher.dmhelper.dialogs.QuickDialogs;
import com.windcatcher.dmhelper.dialogs.QuickDialogs.FieldNames;
import com.windcatcher.dmhelper.dialogs.QuickDialogs.INoPictureCallback;
import com.windcatcher.dmhelper.dialogs.QuickDialogs.IPictureChoiceCallback;
import com.windcatcher.dmhelper.dialogs.QuickDialogs.IPictureOverwriteCallback;

public class CreateCreatureFragment extends Fragment implements IPictureChoiceCallback, IPictureOverwriteCallback{

	public CreateCreatureFragment(){}

	// ===========================================================
	// TODO Fields
	// ===========================================================

	private EditText mName, mHP, mInit;
	private String mImagePath;

	private CursorListFragment mFragment = null;

	private static Uri outputImageURI;

	private static CreateCreatureFragment activeInstance;
	private boolean mActivated = false;

	// ===========================================================
	// TODO Constants
	// ===========================================================

	public static final int GET_PICTURE_CODE = 1928;

	// ===========================================================
	// TODO Inherited Methods
	// ===========================================================

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);		
	}

	@Override
	public void onDestroy(){
		super.onDestroy();
		activeInstance = null;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// inflate the view
		View v = inflater.inflate(R.layout.fragment_create_creature, null); 

		// grab the edit text references
		mName = (EditText)v.findViewById(R.id.create_creature_name);
		mHP = (EditText)v.findViewById(R.id.create_creature_hp);
		mInit = (EditText)v.findViewById(R.id.create_creature_init);

		// set the set image button
		Button b = (Button)v.findViewById(R.id.create_creature_set_image);
		b.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// choose camera/gallery to get the picture
				QuickDialogs.showPictureChooserDialog(getActivity(), CreateCreatureFragment.this);

			}
		});



		return v;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.create_creature, menu);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case R.id.menu_create_creature_save:
			saveCreature();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onCamera() {
		// check for an empty name
		String creatureName = mName.getText().toString();
		if(creatureName == null || creatureName.isEmpty()){
			QuickDialogs.showFieldNotNullDialog(getActivity(), FieldNames.Name);
			return;
		}

		// check for an existing picture for this creature name
		File image = CreatureCameraHost.getFilePath(creatureName);
		if(image.exists()){
			Intent i = new Intent(new Intent(getActivity(), CameraActivity.class));
			i.putExtra(CameraActivity.ARGS_NAME, mName.getText().toString());
			startActivityForResult(i, GET_PICTURE_CODE);
			activeInstance = this;
			// now return because the callback will bring up the next dialog if necessary
			return;
		}
		
		Intent i = new Intent(new Intent(getActivity(), CameraActivity.class));
		i.putExtra(CameraActivity.ARGS_NAME, mName.getText().toString());
		startActivityForResult(i, GET_PICTURE_CODE);
		activeInstance = this;
	}

	@Override
	public void onGallery() {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(Intent.createChooser(intent,
				"Select Picture..."), GET_PICTURE_CODE);
		activeInstance = this;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == CreateCreatureFragment.GET_PICTURE_CODE && resultCode == Activity.RESULT_OK){
			String path;
			// check for gallery intent
			Uri selectedImage = data.getData();
			if(selectedImage != null){
				path = getPath(selectedImage);
				
				CreateCreatureFragment.getActiveInstance().setImagePath(path);
			}
			//nope, was the camera
			path = data.getStringExtra(CameraActivity.DATA_PATH);
			if(path != null){
				CreateCreatureFragment.getActiveInstance().setImagePath(path);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onOverwrite() {
		// choose camera/gallery to get the picture
		QuickDialogs.showPictureChooserDialog(getActivity(), CreateCreatureFragment.this);
	}

	// ===========================================================
	// TODO Methods
	// ===========================================================

	private void saveCreature(){
		// TODO check for duplicate creature names

		// sanity checks
		final String name = mName.getText().toString();
		if(name == null || name.isEmpty()){
			QuickDialogs.showFieldNotNullDialog(getActivity(), FieldNames.Name);
			return;
		}
		String hpS = mHP.getText().toString();
		if(hpS == null || name.isEmpty()){
			QuickDialogs.showFieldNotNullDialog(getActivity(), FieldNames.HP);
			return;
		}
		String initS = mInit.getText().toString();
		if(initS == null || name.isEmpty()){
			QuickDialogs.showFieldNotNullDialog(getActivity(), FieldNames.Init);
			return;
		}

		final int hp;
		final int init;
		try{
			hp = Integer.valueOf(hpS);
			init = Integer.valueOf(initS);
		}catch(NumberFormatException e){
			QuickDialogs.showFieldWrongInputDialog(getActivity(), FieldNames.HP);
			return;
		}

		// not needed for sanity, but check if they want to add a picture
		if(mImagePath == null || mImagePath.isEmpty()){
			QuickDialogs.showNoImageDialog(getActivity(), new INoPictureCallback() {

				@Override
				public void onNo() {
					CreaturesTable.addCreature(GameSQLDataSource.getDatabase(getActivity()), name, hp, init, mImagePath);
					getActivity().onBackPressed();
				}

				@Override
				public void onChangedMind() {
					// choose camera/gallery to get the picture
					QuickDialogs.showPictureChooserDialog(getActivity(), CreateCreatureFragment.this);

				}
			});
			return; // return because continuing depends on dialog input
		}

		// whew, input sanitized. Add it to the database
		CreaturesTable.addCreature(GameSQLDataSource.getDatabase(getActivity()), name, hp, init, mImagePath);
		getActivity().onBackPressed();
	}

	public void setImagePath(String path){
		// set the path
		mImagePath = path;
		// null the instance
		activeInstance = null;
		GlobalConfig.log("Path to image: " + path);
	}

	public static CreateCreatureFragment getActiveInstance(){
		return activeInstance;
	}

	public static Uri getOutputImageURI(){
		return outputImageURI;
	}
	
	public String getPath(Uri uri) {
	    String[] projection = {  MediaColumns.DATA};
	    Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);
	    if(cursor != null) {
	        //HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
	        //THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
	        cursor.moveToFirst();
	        int columnIndex = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
	        String filePath = cursor.getString(columnIndex);
	        cursor.close();
	        return filePath;
	    }
	    else 
	        return uri.getPath();               // FOR OI/ASTRO/Dropbox etc
	}
}
