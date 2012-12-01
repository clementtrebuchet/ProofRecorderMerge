package org.proof.recorder.fragment.dialog;

import org.proof.recorder.R;
import org.proof.recorder.fragment.search.SearchResult;
import org.proof.recorder.utils.DateUtils;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public class Search extends SherlockFragmentActivity {

	private final static String TAG = "CUSTOM_SEARCH_MODULE";
	private final static String BR = "\n";
	private final static int DATES_PICKED = 100;
	
	private static Context mContext;

	private CheckBox mSearchByDate;
	private TextView mSelectedDate;
	private ProgressBar mPgBar;
	private Button mBtnSearch;

	private RadioButton mRBtnVoices;
	private RadioButton mRBtnCalls;

	private AutoCompleteTextView mAocSearchText;
	private boolean mDatePicked = false;
	
	private static Intent mDataResult;
	private static String mPhone = null;

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);
		
		mContext = this;		
		mDataResult = new Intent(Search.this, SearchResult.class);

		mAocSearchText = (AutoCompleteTextView) findViewById(R.id.aocSearchText);
		
		mRBtnVoices = (RadioButton) findViewById(R.id.rbVoices);
		mRBtnCalls = (RadioButton) findViewById(R.id.rbCalls);
		mSearchByDate = (CheckBox) findViewById(R.id.cbByDateSearch);
		mSelectedDate = (TextView) findViewById(R.id.selected_date);
		mPgBar = (ProgressBar) findViewById(R.id.pgBarSearch);
		mBtnSearch = (Button) findViewById(R.id.btnSearch);

		mRBtnCalls.setChecked(true);
		mSelectedDate.setVisibility(TextView.GONE);	
		
		mAocSearchText.setText("");		
		
		Bundle extras = getIntent().getExtras();
		try {
			mPhone = extras.getString("phone");
		}
		catch(Exception e) {
			mPhone = "";
		}
		
		mAocSearchText.setText(mPhone);
		
		mRBtnCalls.setOnCheckedChangeListener(new OnCheckedChangeListener(
				) {			
			@Override
			public void onCheckedChanged(CompoundButton ref, boolean isSelected) {
				Log.i(TAG, "@ref: " + ref + " isSelected: " + isSelected);
				
				int mInputType = mAocSearchText.getInputType();
				mAocSearchText.setText("");
				
				if (mPhone != "" && mPhone != null) {
					mAocSearchText.setText(mPhone);
				}				
				
				if(!isSelected && mInputType != InputType.TYPE_CLASS_TEXT) {
					mAocSearchText.setInputType(InputType.TYPE_CLASS_TEXT);
				}
				else if (isSelected && mInputType != InputType.TYPE_CLASS_PHONE) {
					mAocSearchText.setInputType(InputType.TYPE_CLASS_PHONE);
				}
				else { // should not happened
					mAocSearchText.setInputType(InputType.TYPE_CLASS_TEXT);
				}
			}
		});		
		
		
		mAocSearchText.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
				Log.i(TAG, "TextView: " + arg0 + " int: " + arg1 + " KeyEvent: " + arg2);				
				return false;
			}
		});
		
		mAocSearchText.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
				mAocSearchText.setText("");
				
				if (mPhone != "" && mPhone != null) {
					mAocSearchText.setText(mPhone);
				}
				if(mRBtnCalls.isChecked())
					mAocSearchText.setInputType(InputType.TYPE_CLASS_PHONE);
				else
					mAocSearchText.setInputType(InputType.TYPE_CLASS_TEXT);
				return false;
			}
		});

		mSearchByDate.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					mDatePicked = false;					
					Intent intent = new Intent(Search.this, SearchByDates.class);					
					startActivityForResult(intent, DATES_PICKED);
				}
				else {
					mSelectedDate.setText("");
					mSelectedDate.setVisibility(TextView.GONE);
				}
			}
		});

		mBtnSearch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				onSearch();
			}
			
		});
	}

	private void onSearch() {

		String recap = "";
		String search = mAocSearchText.getText().toString();
		
		if (search.equals("")){
			return;
		}
		
		mDataResult.putExtra("mQuery", search);	

		mPgBar.setVisibility(ProgressBar.VISIBLE);

		if (mDatePicked && mSearchByDate.isChecked()) {			
			recap += "PAR DATE: OUI" + BR;
			mDataResult.putExtra("mByDate", true);
		} else {
			recap += "PAR DATE: NON" + BR;
			mDataResult.putExtra("mByDate", false);
		}

		recap += "RECHERCHE: " + search + BR;

		if (mRBtnVoices.isChecked()) {
			recap += "VOIX: OUI" + BR;
			Log.v(TAG, recap);
			mDataResult.putExtra("mCalls", false);
			mDataResult.putExtra("mVoices", true);
			
		} else if (mRBtnCalls.isChecked()) {
			recap += "APPELS: OUI" + BR;
			Log.v(TAG, recap);
			mDataResult.putExtra("mVoices", false);
			mDataResult.putExtra("mCalls", true);
			
		} else {
			throw new IllegalStateException("");
		}
		
		mPgBar.setVisibility(ProgressBar.INVISIBLE);	
		
		startActivity(mDataResult);
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
	
	protected void onActivityResult(int requestCode, int resultCode,
            Intent data) {
        if (requestCode == DATES_PICKED) {
            if (resultCode == RESULT_OK) {
            	
            	boolean mPreciseDate, mStartingDate, mEndingDate, mAtLeastOne;
            	String mData = "", tmp = "";            	
            	
                try
                {
                	tmp = data.getStringExtra("preciseDate");                	
                	if(tmp != null)
                	{
                		mData += tmp;
                		mPreciseDate = true;
                		mDataResult.putExtra("preciseDate", tmp);
                		mDataResult.putExtra("mPreciseDate", true);
                		mDataResult.putExtra("mPeriodDate", false);
                	}
                	else
                		mPreciseDate = false;
                	
                	Log.i(TAG, mData);
                }
                catch (Exception e) {
                	Log.e(TAG, "data.getStringExtra(\"preciseDate\") INEXISTANT!");
                	mPreciseDate = false;
                }
                
                try
                {
                	tmp = data.getStringExtra("startingDate");                	
                	if(tmp != null)
                	{
                		mData += tmp + BR;
                		mStartingDate = true;
                		mDataResult.putExtra("startingDate", tmp);
                		mDataResult.putExtra("mPreciseDate", false);
                		mDataResult.putExtra("mPeriodDate", true);
                	}
                	else
                		mStartingDate = false;
                	
                	Log.i(TAG, mData);
                }
                catch (Exception e) {
                	Log.e(TAG, "data.getStringExtra(\"startingDate\") INEXISTANT!");
                	mStartingDate = false;
                }
                
                try
                {
                	tmp = data.getStringExtra("endingDate");
                	if(tmp != null)
                	{
                		mData += tmp;
                		mEndingDate = true;
                		mDataResult.putExtra("endingDate", tmp);
                	}
                	else
                		mEndingDate = false;
                	
                	Log.i(TAG, mData);
                	
                }
                catch (Exception e) {
                	Log.e(TAG, "data.getStringExtra(\"endingDate\") INEXISTANT!");
                	mEndingDate = false;
                }
                
                mAtLeastOne = mPreciseDate || mStartingDate || mEndingDate;
                
                if(!mAtLeastOne) {
                	mSearchByDate.setChecked(false);
                	mSelectedDate.setVisibility(TextView.GONE);
                }
                else
                {
                	mSearchByDate.setChecked(true);
                	mSelectedDate.setVisibility(TextView.INVISIBLE);                	
                	mDatePicked = true;
                	
                	String mDate  = "NOT INITIALIZED!";
                	
                	try {
                		String[] parts = mData.split(BR);
                		mDate  = "Période sélectionnée: " + BR;
                		mDate 		+= "Date de début: " + DateUtils.reOrderDate(mContext, parts[0]) + BR;
                		mDate 		+= "Date de fin: " + DateUtils.reOrderDate(mContext, parts[1]);
                	}
                	catch (Exception e) {
                		mDate  = "Date sélectionnée: " + DateUtils.reOrderDate(mContext, mData);
                	}
                	
                	Log.v(TAG, "startingDate: " + mDate);
                	
                	mSelectedDate.setText(mDate);
                	mSelectedDate.setVisibility(TextView.VISIBLE);
                }
            }
            
            else if(resultCode == RESULT_CANCELED) {
            	mSearchByDate.setChecked(false);
            	mSelectedDate.setVisibility(TextView.GONE);
            }
        }
    }
}
