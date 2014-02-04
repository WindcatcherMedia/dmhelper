package com.windcatcher.dmhelper.camera;

import java.io.File;

import com.windcatcher.dmhelper.GlobalConfig;

import android.content.Context;
import android.os.Environment;

public class CreatureCameraHost extends MyCameraHost {

	public CreatureCameraHost(Context ctxt, String creatureName) {
		super(ctxt);
		mCreatureName = creatureName;
	}

	// ===========================================================
	// TODO Fields
	// ===========================================================

	private String mCreatureName;
	
	// ===========================================================
	// TODO Constants
	// ===========================================================

	// ===========================================================
	// TODO Inherited Methods
	// ===========================================================
	
	@Override
	public File getPhotoPath() {
		return getFilePath(mCreatureName);
	}
	
	// ===========================================================
	// TODO Methods
	// ===========================================================
	
	public static File getFilePath(String creatureName){
		final File root = new File(Environment.getExternalStorageDirectory() + File.separator + GlobalConfig.SAVED_PICTURES_ROOT + File.separator + GlobalConfig.SAVED_PICTURES_CREATURES + File.separator);
		String fileName = "Creature" + creatureName + ".jpg";
		File path = new File(root, fileName);
		
		return path;
	}
}
