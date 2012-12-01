package org.proof.recorder.fragment.dialog;

import java.util.Calendar;

import org.proof.recorder.R;
import org.proof.recorder.utils.DateUtils;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public class SearchByDates extends SherlockFragmentActivity{
	
	private final static String TAG = "CUSTOM_SEARCH_MODULE_DATES";
	
	private Context mContext;
	
	private int mDay, mMonth, mYear;
	
	private Intent mIntentForResult;
	
	private CheckBox mPreciseDateChoice;
	private CheckBox mPeriodDateChoice;
	
	private CheckBox mStartingDate;
	private CheckBox mEndingDate;
	
	private TextView txtStartingDateOrPrecise, txtEndingDate;	
	
	private Button btnValidateDates;
	
	private DatePickerDialog mDatePickDiag;
	private Calendar mCal;
	
	private boolean mPrecise, mStarting, mEnding;

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
	
	private final OnDateSetListener odsl = new OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker arg0, int year, int month,
				int dayOfMonth) {

			Log.i(TAG, "The date is " + dayOfMonth + "/" + month + "/"
					+ year + " pointeur(ref): " + arg0);

			mDay = dayOfMonth;
			mMonth = month + 1;
			mYear = year;
			
			String mSqlDate = mYear + "-" + mMonth + "-" + mDay;
			
			String selectedDate = getString(R.string.search_selected_date) + ": " + DateUtils.reOrderDate(mContext, mSqlDate);

			if(mPrecise) {
				
				try {
					mIntentForResult.removeExtra("mStartingDate");
					mIntentForResult.removeExtra("endingDate");
				}
				catch(Exception e) {
					
				}			
				
				txtStartingDateOrPrecise.setVisibility(TextView.INVISIBLE);
				txtStartingDateOrPrecise.setText(selectedDate);
				txtStartingDateOrPrecise.setVisibility(TextView.VISIBLE);
				mIntentForResult.putExtra("preciseDate", mSqlDate);
			}
			else if (mStarting) {
				
				try {
					mIntentForResult.removeExtra("preciseDate");
				}
				catch(Exception e) {
					
				}
				
				txtStartingDateOrPrecise.setVisibility(TextView.INVISIBLE);
				txtStartingDateOrPrecise.setText(selectedDate);
				txtStartingDateOrPrecise.setVisibility(TextView.VISIBLE);
				mIntentForResult.putExtra("startingDate", mSqlDate);
			}
			
			else if (mEnding) {
				
				try {
					mIntentForResult.removeExtra("preciseDate");
				}
				catch(Exception e) {
					
				}
				
				txtEndingDate.setVisibility(TextView.INVISIBLE);
				txtEndingDate.setText(selectedDate);
				txtEndingDate.setVisibility(TextView.VISIBLE);
				mIntentForResult.putExtra("endingDate", mSqlDate);
			}
			
			else {
				Log.v(TAG, "NONE of mPrecise | mStarting | mEnding");
			}
			
			
		}

	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_by_dates);
		
		mContext = this;
		
		mIntentForResult = new Intent();
		
		mCal = Calendar.getInstance();
		mDatePickDiag = new DatePickerDialog(
				SearchByDates.this, odsl, mCal.get(Calendar.YEAR), mCal
						.get(Calendar.MONTH), mCal
						.get(Calendar.DAY_OF_MONTH));

		mDatePickDiag.setButton(DialogInterface.BUTTON_NEGATIVE,
				getString(R.string.alert_dialog_cancel),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int which) {
						if (which == DialogInterface.BUTTON_NEGATIVE) {
							
							dialog.dismiss();
							if(mPrecise) {
								mPreciseDateChoice.setChecked(false);
							}
							else if (mStarting) {
								mStartingDate.setChecked(false);
							}
							
							else if (mEnding) {
								mEndingDate.setChecked(false);
							}
							
							else {
								Log.v(TAG, "NONE of mPrecise | mStarting | mEnding");
							}
						}
					}
				});
		
		mDatePickDiag.setCancelable(false);
		
		mPreciseDateChoice = (CheckBox) findViewById(R.id.preciseDate);
		mPeriodDateChoice = (CheckBox) findViewById(R.id.periodDate);
		
		mStartingDate = (CheckBox) findViewById(R.id.startingDate);
		mEndingDate = (CheckBox) findViewById(R.id.endingDate);
		
		txtStartingDateOrPrecise = (TextView) findViewById(R.id.txtStartingDateOrPrecise);
		txtEndingDate = (TextView) findViewById(R.id.txtEndingDate);
		
		btnValidateDates = (Button) findViewById(R.id.btnValidateDates);
		
		mPeriodDateChoice.setOnCheckedChangeListener( new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked) {
					mStartingDate.setVisibility(CheckBox.VISIBLE);
					mEndingDate.setVisibility(CheckBox.VISIBLE);
					
					mStartingDate.setChecked(false);
					mEndingDate.setChecked(false);
					
					mPreciseDateChoice.setEnabled(false);
				}
				else {
					txtStartingDateOrPrecise.setVisibility(TextView.GONE);
					txtEndingDate.setVisibility(TextView.GONE);
					
					mStartingDate.setVisibility(CheckBox.GONE);
					mEndingDate.setVisibility(CheckBox.GONE);
					
					mPreciseDateChoice.setEnabled(true);
				}
				
			}
		});
		
		mPreciseDateChoice.setOnCheckedChangeListener( new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				
				if(isChecked) {	
					mPeriodDateChoice.setEnabled(false);
					mPrecise = true;
					mStarting = false;
					mEnding = false;
					mDatePickDiag.show();				
				}
				else {
					txtStartingDateOrPrecise.setVisibility(TextView.GONE);
					mPeriodDateChoice.setEnabled(true);
				}
			}
		});
		
		mStartingDate.setOnCheckedChangeListener( new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				
				if(isChecked) {
					mPrecise = false;
					mStarting = true;
					mEnding = false;
					mDatePickDiag.show();				
				}
				else
					txtStartingDateOrPrecise.setVisibility(TextView.GONE);
			}
		});
		
		mEndingDate.setOnCheckedChangeListener( new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				
				if(isChecked) {		
					mPrecise = false;
					mStarting = false;
					mEnding = true;
					mDatePickDiag.show();				
				}
				else
					txtEndingDate.setVisibility(TextView.GONE);
			}
		});
		
		btnValidateDates.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				int RESULT;
				
				if (mPeriodDateChoice.isChecked() && 
						!(mStartingDate.isChecked() && mEndingDate.isChecked())
					){
					
					if (!mStartingDate.isChecked()) {
						txtStartingDateOrPrecise.setText("Veuillez sélectionner une date!");
						txtStartingDateOrPrecise.setVisibility(TextView.VISIBLE);
					}
					else if (!mEndingDate.isChecked()) {
						txtEndingDate.setText("Veuillez sélectionner une date!");
						txtEndingDate.setVisibility(TextView.VISIBLE);
					}
					else {}
					return;
				}
				
				if(mPreciseDateChoice.isChecked() || mPeriodDateChoice.isChecked())	{
					RESULT = RESULT_OK;					
				}
				else {
					RESULT = RESULT_CANCELED;
				}
				setResult(RESULT, mIntentForResult);				
				onBackPressed();
			}
		});
		
		
	}
}
