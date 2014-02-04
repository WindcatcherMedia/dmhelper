package com.windcatcher.dmhelper.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

import com.windcatcher.dmhelper.R;

public class QuickDialogs {

	// ===========================================================
	// TODO Fields
	// ===========================================================

	// ===========================================================
	// TODO Constants
	// ===========================================================

	public enum FieldNames { Name, Init, HP };

	// ===========================================================
	// TODO Inherited Methods
	// ===========================================================

	// ===========================================================
	// TODO Methods
	// ===========================================================

	/**
	 * Shows a simple dialog with one option and a message
	 * 
	 * @param c
	 * @param titleID
	 * @param buttonID
	 * @param listener
	 */
	private static void showSingleOptionDialog(Context c, int titleID, int messageID, int buttonID, OnClickListener listener){
		AlertDialog.Builder builder = new AlertDialog.Builder(c);
		builder.setTitle(titleID)
		.setNegativeButton(buttonID, listener);
		if(messageID > -1){
			builder.setMessage(messageID);
		}

		builder.create().show();
	}

	/**
	 * Shows a dialog with two options and a message
	 * @param c
	 * @param titleID
	 * @param posButtonID
	 * @param negButtonID
	 * @param posListener
	 * @param negListener
	 */
	private static void showDoubleOptionDialog(Context c, int titleID, int messageID, int posButtonID, int negButtonID, OnClickListener posListener, OnClickListener negListener){
		AlertDialog.Builder builder = new AlertDialog.Builder(c);
		builder.setTitle(titleID)
		.setPositiveButton(posButtonID, posListener)
		.setNegativeButton(negButtonID, negListener);
		if(messageID > -1){
			builder.setMessage(messageID);
		}

		builder.create().show();
	}

	/**
	 * Shows a simple dialog with 3 options and a message
	 * @param c
	 * @param titleID
	 * @param posButtonID
	 * @param neutralButtonID
	 * @param negButtonID
	 * @param posListener
	 * @param neutralListener
	 * @param negListener
	 */
	private static void showTripleOptionDialog(Context c, int titleID, int messageID, int posButtonID, int neutralButtonID, int negButtonID, OnClickListener posListener, OnClickListener neutralListener, OnClickListener negListener){
		AlertDialog.Builder builder = new AlertDialog.Builder(c);
		builder.setTitle(titleID)
		.setPositiveButton(posButtonID, posListener)
		.setNeutralButton(neutralButtonID, neutralListener)
		.setNegativeButton(negButtonID, negListener);
		if(messageID > -1){
			builder.setMessage(messageID);
		}

		builder.create().show();
	}

	public static void showPictureChooserDialog(Context c, final IPictureChoiceCallback choiceCallback){
		showTripleOptionDialog(c, R.string.dialog_picture_choose, -1, R.string.dialog_gallery, R.string.dialog_camera, R.string.dialog_cancel, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(choiceCallback != null){
					choiceCallback.onGallery();
				}
			}
		}, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(choiceCallback != null){
					choiceCallback.onCamera();
				}
			}
		}, null);
	}

	public static void showPictureOverwriteDialog(Context c, final IPictureOverwriteCallback callback){
		showDoubleOptionDialog(c, R.string.create_creature_overwrite_picture_title, R.string.create_creature_overwrite_picture, R.string.dialog_overwrite, R.string.dialog_cancel,
				new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(callback != null){
					callback.onOverwrite();
				}
			}
		}, null);
	}

	@SuppressWarnings("incomplete-switch")
	public static void showFieldWrongInputDialog(Context c, FieldNames fn){
		int messageID = -1;
		switch(fn){
		case Init:
			messageID = R.string.dialog_hp_init_need_numbers;
			break;
		case Name:
			messageID = R.string.dialog_hp_init_need_numbers;
			break;
		}
		showSingleOptionDialog(c, R.string.dialog_wrong_input, messageID, R.string.dialog_close, null);
	}

	public static void showFieldNotNullDialog(Context c, FieldNames fn){
		int messageID = -1;
		switch(fn){
		case HP:
			messageID = R.string.dialog_hp_not_null;
			break;
		case Init:
			messageID = R.string.dialog_init_not_null;
			break;
		case Name:
			messageID = R.string.dialog_name_not_null;
			break;
		}
		showSingleOptionDialog(c, R.string.dialog_wrong_input, messageID, R.string.dialog_close, null);
	}

	public static void showNoImageDialog(Context c, final INoPictureCallback callback){
		showDoubleOptionDialog(c, R.string.dialog_no_image_title, R.string.dialog_no_image, R.string.dialog_yes, R.string.dialog_no
				, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(callback != null){
					callback.onChangedMind();
				}
			}
		}, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(callback != null){
					callback.onNo();
				}
			}
		});
	}

	public static void showQuitDialog(Context c, final IQuitCallback callback){
		showDoubleOptionDialog(c, R.string.dialog_quit_title, R.string.dialog_quit_or_back, R.string.dialog_main_menu, R.string.dialog_close_app, 
				new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				callback.onQuitToMenu();
			}
		}, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				callback.onQuitApp();
			}
		});
	}

	// ===========================================================
	// TODO Interfaces
	// ===========================================================

	public interface IQuitCallback{

		public void onQuitApp();

		public void onQuitToMenu();
	}

	public interface INoPictureCallback{

		/**
		 * User has chosen to select a picture after all
		 */
		public void onChangedMind();

		/**
		 * User doesn't want to add a picture
		 */
		public void onNo();
	}

	public interface IPictureChoiceCallback{
		/**
		 * User has chosen to get the picture from the camera
		 */
		public void onCamera();

		/**
		 * User has chosen to get the picture from a gallery app
		 */
		public void onGallery();
	}

	public interface IPictureOverwriteCallback{

		/**
		 * User has chosen to overwrite existing picture
		 */
		public void onOverwrite();
	}
}
