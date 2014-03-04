package com.windcatcher.dmhelper.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import com.windcatcher.dmhelper.R;

/**
 * This is a simple wrapper class for simple dialogs.
 * 
 * @author Tyler
 *
 */
public class QuickDialogs {

	// ===========================================================
	// TODO Fields
	// ===========================================================

	// ===========================================================
	// TODO Constants
	// ===========================================================

	public enum FieldNames { Name, Init, HP, Description, Damage };

	public enum EntryType { String, Number };

	// ===========================================================
	// TODO Inherited Methods
	// ===========================================================

	// ===========================================================
	// TODO Methods
	// ===========================================================

	private static void showInputDialog(Context c, int titleID, int messageID, EntryType type, final IInputCallback listener){
		// Create a textview for the entry
		final EditText et = new EditText(c);
		switch(type){
		case Number:
			et.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
			break;
		case String:
			et.setInputType(EditorInfo.TYPE_CLASS_TEXT);
			break;
		default:
			throw new IllegalArgumentException("Must supply an EntryType for the input");
		}
		
		AlertDialog.Builder builder = new AlertDialog.Builder(c);
		
		builder.setTitle(titleID);
		if(messageID > -1){
			builder.setMessage(messageID);
		}
		builder.setView(et)
		.setPositiveButton(R.string.okay, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				listener.onInput(et.getText().toString());
			}
		})
		.setNegativeButton(R.string.close, null);
		
		builder.create().show();
	}

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
		showTripleOptionDialog(c, R.string.picture_choose, -1, R.string.gallery, R.string.camera, R.string.cancel, new OnClickListener() {

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
		showDoubleOptionDialog(c, R.string.create_creature_overwrite_picture_title, R.string.create_creature_overwrite_picture, R.string.overwrite, R.string.cancel,
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
			messageID = R.string.hp_init_need_numbers;
			break;
		case Name:
			messageID = R.string.hp_init_need_numbers;
			break;
		case Damage:
			messageID = R.string.damage_need_numbers;
			break;
		}
		showSingleOptionDialog(c, R.string.wrong_input, messageID, R.string.close, null);
	}

	public static void showFieldNotNullDialog(Context c, FieldNames fn){
		int messageID = -1;
		switch(fn){
		case HP:
			messageID = R.string.hp_not_null;
			break;
		case Init:
			messageID = R.string.init_not_null;
			break;
		case Name:
			messageID = R.string.name_not_null;
			break;
		case Damage:
			messageID = R.string.damage_not_null;
			break;
		case Description:
			messageID = R.string.description_not_null;
			break;
		}
		showSingleOptionDialog(c, R.string.wrong_input, messageID, R.string.close, null);
	}

	public static void showNoImageDialog(Context c, final INoPictureCallback callback){
		showDoubleOptionDialog(c, R.string.no_image_title, R.string.no_image, R.string.yes, R.string.no
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
		showDoubleOptionDialog(c, R.string.quit_title, R.string.quit_or_back, R.string.main_menu, R.string.close_app, 
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

	public static void showNameExistsDialog(Context c){
		showSingleOptionDialog(c, R.string.name_exists_title, R.string.name_exists, R.string.close, null);
	}

	public static void showHarmDialog(Context c, IInputCallback callback){
		showInputDialog(c, R.string.harm_title, -1, EntryType.Number, callback);
	}

	public static void showHealDialog(Context c, IInputCallback callback){
		showInputDialog(c, R.string.heal_title, -1, EntryType.Number, callback);
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

	public interface IChangeHPCallback{

		public void onHPChange(int change);
	}

	public interface IInputCallback{

		public void onInput(String input);
	}
	
	public interface INewEffectSelectCallback{
		
		public void onNewEffectCreated(long effectID);
	}
}
