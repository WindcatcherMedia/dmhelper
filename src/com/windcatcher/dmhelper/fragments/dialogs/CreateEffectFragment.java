package com.windcatcher.dmhelper.fragments.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RadioButton;

import com.windcatcher.dmhelper.R;
import com.windcatcher.dmhelper.SQLite.GameSQLDataSource;
import com.windcatcher.dmhelper.SQLite.tables.EffectsTable;
import com.windcatcher.dmhelper.dialogs.QuickDialogs;
import com.windcatcher.dmhelper.dialogs.QuickDialogs.FieldNames;
import com.windcatcher.dmhelper.fragments.CursorListFragment;

public class CreateEffectFragment extends DialogFragment {

	private CreateEffectFragment(){}

	// ===========================================================
	// TODO Fields
	// ===========================================================

	public static ICreateEffectCallback mCallback;

	private EditText mName, mDescription, mDamage;
	private RadioButton mStart, mEnd;
	private CheckBox mDealsDamage;
	
	private static CursorListFragment mParent;

	// ===========================================================
	// TODO Constants
	// ===========================================================

	private static final String ARG_ROWID = "rowid";

	// ===========================================================
	// TODO Inherited Methods
	// ===========================================================


	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

		View v = inflater.inflate(R.layout.dialog_new_effect, null);

		mName = (EditText)v.findViewById(R.id.new_effect_name);
		mDescription = (EditText)v.findViewById(R.id.new_effect_description);
		mDamage = (EditText)v.findViewById(R.id.new_effect_damage);

		mStart = (RadioButton)v.findViewById(R.id.new_effect_trigger_begin);
		mEnd = (RadioButton)v.findViewById(R.id.new_effect_trigger_end);

		mDealsDamage = (CheckBox)v.findViewById(R.id.new_effect_deals_damage);
		mDealsDamage.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					mDamage.setVisibility(View.VISIBLE);
				}else{
					mDamage.setVisibility(View.GONE);
				}
			}
		});

		// populate the information if there is an existing row ID
		long existingRowId = getArguments().getLong(ARG_ROWID);
		if(existingRowId > -1){
			Cursor c = EffectsTable.queryExact(GameSQLDataSource.getDatabase(getActivity()), existingRowId);
			if(c.moveToFirst()){

				mName.setText(c.getString(EffectsTable.COLUMN_NAME.getNum()));
				mDescription.setText(c.getString(EffectsTable.COLUMN_DESCRIPTION.getNum()));
				int dealsDamage = c.getInt(EffectsTable.COLUMN_DEALS_DAMAGE.getNum());
				if(dealsDamage == EffectsTable.DAMAGE_YES){
					mDealsDamage.setChecked(true);
					mDamage.setText(c.getString(EffectsTable.COLUMN_DESCRIPTION.getNum()));				
				}else{
					mDealsDamage.setChecked(false);
				}
				int trigger = c.getInt(EffectsTable.COLUMN_TRIGGER_TYPE.getNum());
				if(trigger == EffectsTable.TRIGGER_START){
					mStart.setChecked(true);
				}else{
					mEnd.setChecked(true);
				}
			}
		}


		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.new_effect)
		.setView(v)
		.setPositiveButton(R.string.save, null)
		.setNegativeButton(R.string.cancel, null);
		// Create the AlertDialog object and return it
		return builder.create();
	}
	
	public void onDestroy(){
		super.onDestroy();
		
		mParent = null;
	}

	@Override
	public void onStart(){
		super.onStart();

		AlertDialog d = (AlertDialog)getDialog();
		if(d != null){
			Button positiveButton = (Button) d.getButton(Dialog.BUTTON_POSITIVE);
			// override the positive button to not dismiss if the input is not sanitized
			positiveButton.setOnClickListener(new View.OnClickListener(){

				@Override
				public void onClick(View v){
					// sanitize inputs
					String name = mName.getText().toString();
					if(name == null || name.isEmpty()){
						QuickDialogs.showFieldNotNullDialog(getActivity(), FieldNames.Name);
						return;
					}
					String description = mDescription.getText().toString();
					if(description == null || description.isEmpty()){
						QuickDialogs.showFieldNotNullDialog(getActivity(), FieldNames.Description);
						return;
					}
					int damage = -1;
					if(mDealsDamage.isChecked()){
						try {
							damage = Integer.valueOf(mDamage.getText().toString());
						}catch(NumberFormatException e){
							QuickDialogs.showFieldWrongInputDialog(getActivity(), FieldNames.HP);
							return;
						}
					}
					int triggerType;
					if(mStart.isChecked()){
						triggerType = EffectsTable.TRIGGER_START;
					}else{
						triggerType = EffectsTable.TRIGGER_END;
					}

					int dealsDamage;
					if(mDealsDamage.isChecked()){
						dealsDamage = EffectsTable.DAMAGE_YES;
					}else{
						dealsDamage = EffectsTable.DAMAGE_NO;
					}

					long existingRowID = getArguments().getLong(ARG_ROWID);
					if(existingRowID > -1){
						ContentValues values = new ContentValues();
						values.put(EffectsTable.COLUMN_NAME.getName(), name);
						values.put(EffectsTable.COLUMN_DESCRIPTION.getName(), description);
						values.put(EffectsTable.COLUMN_TRIGGER_TYPE.getName(), triggerType);
						values.put(EffectsTable.COLUMN_DEALS_DAMAGE.getName(), dealsDamage);
						if(dealsDamage == EffectsTable.DAMAGE_YES){
							values.put(EffectsTable.COLUMN_DAMAGE.getName(), damage);
						}else{
							values.put(EffectsTable.COLUMN_DAMAGE.getName(), damage);
						}
						EffectsTable.update(GameSQLDataSource.getDatabase(getActivity()), existingRowID, values);
					}else{
						long newRowID = EffectsTable.addNewEffect(GameSQLDataSource.getDatabase(getActivity()), name, description, triggerType, 0, dealsDamage, damage);
						if(mCallback != null){
							mCallback.onSaveEffect(newRowID);
						}
					}

					// refresh the list if the parent exists
					if(mParent != null){
						mParent.refreshList();
					}
					
					dismiss();
				}
			});
		}
	}

	// ===========================================================
	// TODO Methods
	// ===========================================================

	public static CreateEffectFragment newInstance(ICreateEffectCallback callback, CursorListFragment parent){
		return newInstance(callback, parent, -1);
	}

	public static CreateEffectFragment newInstance(ICreateEffectCallback callback, CursorListFragment parent, long rowID){
		mCallback = callback;
		mParent = parent;

		CreateEffectFragment frag = new CreateEffectFragment();

		Bundle args = new Bundle();
		args.putLong(ARG_ROWID, rowID);

		frag.setArguments(args);

		return frag;
	}

	// ===========================================================
	// TODO Interfaces
	// ===========================================================

	public interface ICreateEffectCallback{

		public void onSaveEffect(long effectID);

	}
}
