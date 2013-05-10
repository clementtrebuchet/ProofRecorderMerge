package org.proof.recorder.fragment.dialog;

import java.util.Calendar;

import org.proof.recorder.R;
import org.proof.recorder.bases.activity.ProofFragmentActivity;
import org.proof.recorder.utils.DateUtils;
import org.proof.recorder.utils.Log.Console;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.TextView;

public class SearchByDates extends ProofFragmentActivity {
	
	private Context mContext;
	
	private int mDay, mMonth, mYear;
	
	private Intent mIntentForResult;
	
	private CheckBox mPreciseDateChoice;
	private CheckBox mPeriodDateChoice;
	
	private CheckBox mStartingDate;
	private CheckBox mEndingDate;
	
	private TextView txtStartingDateOrPrecise, txtEndingDate;	
	
	private Button btnValidateDates;
	
	private DatePickerDialog mDatePickDiag = null;
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

			Console.print_debug("The date is " + dayOfMonth + "/" + month + "/"
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
				
				txtStartingDateOrPrecise.setVisibility(View.INVISIBLE);
				txtStartingDateOrPrecise.setText(selectedDate);
				txtStartingDateOrPrecise.setVisibility(View.VISIBLE);
				mIntentForResult.putExtra("preciseDate", mSqlDate);
			}
			else if (mStarting) {
				
				try {
					mIntentForResult.removeExtra("preciseDate");
				}
				catch(Exception e) {
					
				}
				
				txtStartingDateOrPrecise.setVisibility(View.INVISIBLE);
				txtStartingDateOrPrecise.setText(selectedDate);
				txtStartingDateOrPrecise.setVisibility(View.VISIBLE);
				mIntentForResult.putExtra("startingDate", mSqlDate);
			}
			
			else if (mEnding) {
				
				try {
					mIntentForResult.removeExtra("preciseDate");
				}
				catch(Exception e) {
					
				}
				
				txtEndingDate.setVisibility(View.INVISIBLE);
				txtEndingDate.setText(selectedDate);
				txtEndingDate.setVisibility(View.VISIBLE);
				mIntentForResult.putExtra("endingDate", mSqlDate);
			}
			
			else {
				Console.print_debug("NONE of mPrecise | mStarting | mEnding");
			}
			
			
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_by_dates);
		Console.setTagName(this.getClass().getSimpleName());
		mContext = this;
		
		mIntentForResult = new Intent();
		
		mCal = Calendar.getInstance();
		
		if(mDatePickDiag == null) {
			mDatePickDiag = new DatePickerDialog(
					SearchByDates.this, odsl, mCal.get(Calendar.YEAR), mCal
							.get(Calendar.MONTH), mCal
							.get(Calendar.DAY_OF_MONTH));

			mDatePickDiag.setButton(DialogInterface.BUTTON_NEGATIVE,
					getString(R.string.alert_dialog_cancel),
					new DialogInterface.OnClickListener() {
						@Override
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
									Console.print_debug("NONE of mPrecise | mStarting | mEnding");
								}
							}
						}
					});
			
			mDatePickDiag.setCancelable(false);
		}		
		
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
					mStartingDate.setVisibility(View.VISIBLE);
					mEndingDate.setVisibility(View.VISIBLE);
					
					mStartingDate.setChecked(false);
					mEndingDate.setChecked(false);
					
					mPreciseDateChoice.setEnabled(false);
				}
				else {
					txtStartingDateOrPrecise.setVisibility(View.GONE);
					txtEndingDate.setVisibility(View.GONE);
					
					mStartingDate.setVisibility(View.GONE);
					mEndingDate.setVisibility(View.GONE);
					
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
					
					if(mDatePickDiag != null & !mDatePickDiag.isShowing()) {
						mDatePickDiag.show();
					}										
				}
				else {
					txtStartingDateOrPrecise.setVisibility(View.GONE);
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
					if(mDatePickDiag != null & !mDatePickDiag.isShowing()) {
						mDatePickDiag.show();
					}				
				}
				else
					txtStartingDateOrPrecise.setVisibility(View.GONE);
			}
		});
		
		mEndingDate.setOnCheckedChangeListener( new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				
				if(isChecked) {		
					mPrecise = false;
					mStarting = false;
					mEnding = true;
					if(mDatePickDiag != null & !mDatePickDiag.isShowing()) {
						mDatePickDiag.show();
					}				
				}
				else
					txtEndingDate.setVisibility(View.GONE);
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
						txtStartingDateOrPrecise.setVisibility(View.VISIBLE);
					}
					else if (!mEndingDate.isChecked()) {
						txtEndingDate.setText("Veuillez sélectionner une date!");
						txtEndingDate.setVisibility(View.VISIBLE);
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
