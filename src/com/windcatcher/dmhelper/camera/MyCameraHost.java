package com.windcatcher.dmhelper.camera;

import android.content.Context;
import android.hardware.Camera.ShutterCallback;

import com.commonsware.cwac.camera.SimpleCameraHost;

public class MyCameraHost extends SimpleCameraHost {

	protected MyCameraHost(Context ctxt) {
		super(ctxt);
	}

	// ===========================================================
	// TODO Fields
	// ===========================================================

	private ShutterCallback mCallback;

	// ===========================================================
	// TODO Constants
	// ===========================================================

	// ===========================================================
	// TODO Inherited Methods
	// ===========================================================

	@Override
	public boolean useSingleShotMode() {
		return true;
	}

	@Override
	public ShutterCallback getShutterCallback() {
		return null;
	}

	@Override
	public void saveImage(byte[] image) {
		super.saveImage(image);
		mCallback.onShutter();
	}

	// ===========================================================
	// TODO Methods
	// ===========================================================

	public void setShutterCallback(ShutterCallback callback){
		mCallback = callback;
	}
}
