package com.windcatcher.dmhelper.fragments;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera.ShutterCallback;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.commonsware.cwac.camera.CameraFragment;
import com.commonsware.cwac.camera.CameraView;
import com.windcatcher.dmhelper.R;
import com.windcatcher.dmhelper.activities.CameraActivity;
import com.windcatcher.dmhelper.camera.CreatureCameraHost;
import com.windcatcher.dmhelper.camera.MyCameraHost;


public class MyCameraFragment extends CameraFragment {

	private MyCameraFragment(Activity activity){
		mActivity = activity;
	}

	// ===========================================================
	// TODO Fields
	// ===========================================================

	Activity mActivity;

	// ===========================================================
	// TODO Constants
	// ===========================================================


	// ===========================================================
	// TODO Inherited Methods
	// ===========================================================

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.camera, menu);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		int type = getArguments().getInt(CameraActivity.ARGS_TYPE);
		String creatureName = getArguments().getString(CameraActivity.ARGS_NAME, null);
		final String filePath;

		MyCameraHost host;
		switch(type){
		case 1:
			host = new CreatureCameraHost(getActivity(), creatureName);
			filePath = CreatureCameraHost.getFilePath(creatureName).getPath();
			break;
		default:
			throw new IllegalArgumentException("No camera host type declared. Use CameraActivity.ARGS_TYPE_... as argument");
		}

		ShutterCallback callback = new ShutterCallback() {

			@Override
			public void onShutter() {
				Intent i = new Intent();
				i.putExtra(CameraActivity.DATA_PATH, filePath);
				mActivity.setResult(Activity.RESULT_OK, i);
				mActivity.finish();
			}
		};
		
		host.setShutterCallback(callback);

		setHost(host);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View v = super.onCreateView(inflater, container, savedInstanceState);

		CameraView cv = getCameraView();
		if(cv != null){
			cv.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					autoFocus();
				}
			});
		}

		return v;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.camera:
			takePicture();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	// ===========================================================
	// TODO Methods
	// ===========================================================

	public static MyCameraFragment newInstance(Activity activitiy, String creatureName, int type){
		MyCameraFragment frag = new MyCameraFragment(activitiy);

		Bundle args = new Bundle();
		args.putString(CameraActivity.ARGS_NAME, creatureName);
		args.putInt(CameraActivity.ARGS_TYPE, type);

		frag.setArguments(args);

		return frag;
	}
}
