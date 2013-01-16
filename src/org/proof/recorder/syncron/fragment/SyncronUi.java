package org.proof.recorder.syncron.fragment;

import java.io.FileNotFoundException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import org.proof.recorder.R;
import org.proof.recorder.Settings;
import org.proof.recorder.database.models.RecordRPC;
import org.proof.recorder.database.support.ProofDataBase;
import org.proof.recorder.personnal.provider.PersonnalProofContentProvider;
import org.proof.recorder.syncron.OperationBatchController;
import org.proof.recorder.syncron.OperationBatchTelePhone;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;

public class SyncronUi extends SherlockFragmentActivity {
	static ProgressBar progDialog;
	private static final String TAG = "SyncronUi"; //$NON-NLS-1$
	protected int mProgressStatus = 0;
	static DialogFragment newFragment;
	static TextView tv;
	Fragment prev;
	public static ArrayList<RecordRPC> records = new ArrayList<RecordRPC>();
	static TextView tvTEL;
	static TextView tvRESTORE;
	static ListView mListView;
	static RadioButton rButton;

	private static int checksum;
	private static int saveTel;
	private static int saveTelNot;
	private static int saveVoice;
	private static int saveVoiceNot;
	private static int saveUnionOPCODES;

	private static int restoreTel;
	private static int restoreTelNot;
	private static int restoreVoice;
	private static int restoreVoiceNot;
	private static int restoreUnionOPCODES;

	private static boolean justTCheck;

	private static String STARTOP;

	private static String op;
	private static int position;
	private static int[] OPTEST = { 1000 };
	private static int[] OPSAVE = { 0, 0, 0, 0, 0 };
	private static int[] OPRESTORE = { 0, 0, 0, 0 };
	private static boolean isOPSAVE;
	private static Context context;

	public static int opCodes(String opcode, boolean finnishCount, int pos) {
		/*
		 * if op is for upload data to server
		 */
		if (justTCheck) {
			storeOpCodes(opcode, pos);
			checksum = OPTEST[0];
			if (Settings.isDebug())
				Log.e(TAG,
						"It's justTCheck whith a store code of :{" + checksum + "}"); //$NON-NLS-1$ //$NON-NLS-2$
			return checksum;

		}
		if (isOPSAVE == true) {
			storeOpCodes(opcode, pos);
			if (finnishCount == false) {
				return 0;
			} else {

				if (OPSAVE.length == 5) {
					checksum = OPSAVE[0];
					saveTel = OPSAVE[1];
					saveTelNot = OPSAVE[2];
					saveVoice = OPSAVE[3];
					saveVoiceNot = OPSAVE[4];
					saveUnionOPCODES = checksum + saveTel + saveTelNot
							+ saveVoice + saveVoiceNot;
					if (Settings.isDebug()) {
						Log.e(TAG, "saveTel return code : " + saveTel); //$NON-NLS-1$
						Log.e(TAG, "saveTelNot return code : " + saveTelNot); //$NON-NLS-1$
						Log.e(TAG, "saveVoice return code : " + saveVoice); //$NON-NLS-1$
						Log.e(TAG, "saveVoiceNot return code : " + saveVoiceNot); //$NON-NLS-1$
						Log.e(TAG, "saveUnionOPCODES return code : " //$NON-NLS-1$
								+ saveUnionOPCODES);
					}
					return saveUnionOPCODES;
				} else {
					return 666;
				}

			}

			/*
			 * if op is for download data form server
			 */
		} else if (isOPSAVE == false) {
			storeOpCodes(opcode, pos);
			if (finnishCount == false) {
				return 0;
			} else {
				if (OPRESTORE.length == 4) {
					restoreTel = OPRESTORE[0];
					restoreTelNot = OPRESTORE[1];
					restoreVoice = OPRESTORE[2];
					restoreVoiceNot = OPRESTORE[3];
					restoreUnionOPCODES = restoreTel + restoreTelNot
							+ restoreVoice + restoreVoiceNot;
					if (Settings.isDebug()) {
						Log.e(TAG, "restoreTel return code : " + restoreTel); //$NON-NLS-1$
						Log.e(TAG, "restoreTelNot return code : " //$NON-NLS-1$
								+ restoreTelNot);
						Log.e(TAG, "restoreVoice return code : " + restoreVoice); //$NON-NLS-1$
						Log.e(TAG, "restoreVoiceNot return code : " //$NON-NLS-1$
								+ restoreVoiceNot);
						Log.e(TAG, "restoreunionOPCODES return code : " //$NON-NLS-1$
								+ restoreUnionOPCODES);
					}
					return restoreUnionOPCODES;
				} else {
					return 666;
				}

			}

		}
		return 0;

	}

	private static String comprehensiveOpCode(int union) {
		/*
		 * PLUSDEREC = 5 PLUSDEVOICE = 50 MOINSDEREC = 500 MOINSDEVOICE = 5000
		 */
		String msg = getAppContext().getString(
				Messages.getStringResource(getAppContext(), "SyncronUi_13")); //$NON-NLS-1$
		if (justTCheck) {
			if (checksum == 1) {
				msg += getAppContext().getString(
						Messages.getStringResource(getAppContext(),
								"SyncronUi_14")); //$NON-NLS-1$
			} else if (checksum == 0) {
				msg += getAppContext().getString(
						Messages.getStringResource(getAppContext(),
								"SyncronUi_15")); //$NON-NLS-1$

			} else if (checksum == 3) {
				msg += getAppContext().getString(
						Messages.getStringResource(getAppContext(),
								"SyncronUi_16")); //$NON-NLS-1$
				if (Settings.isDebug())
					Log.e(TAG,
							getAppContext().getString(
									Messages.getStringResource(getAppContext(),
											"SyncronUi_17"))); //$NON-NLS-1$
			} else if (checksum == 1000) {
				if (Settings.isDebug())
					Log.e(TAG,
							getAppContext().getString(
									Messages.getStringResource(getAppContext(),
											"SyncronUi_18"))); //$NON-NLS-1$
			} else if (checksum == 5) {
				msg += getAppContext().getString(
						Messages.getStringResource(getAppContext(),
								"SyncronUi_55"));
				if (Settings.isDebug())
					Log.e(TAG, "" + checksum);
			} else if (checksum == 50) {
				msg += getAppContext().getString(
						Messages.getStringResource(getAppContext(),
								"SyncronUi_56"));
				if (Settings.isDebug())
					Log.e(TAG, "" + checksum);
			} else if (checksum == 500) {
				msg += getAppContext().getString(
						Messages.getStringResource(getAppContext(),
								"SyncronUi_57"));
				if (Settings.isDebug())
					Log.e(TAG, "" + checksum);
			} else if (checksum == 5000) {
				msg += getAppContext().getString(
						Messages.getStringResource(getAppContext(),
								"SyncronUi_58"));
				if (Settings.isDebug())
					Log.e(TAG, "" + checksum);
			} else if (checksum > 5 && checksum < 56) {
				msg += getAppContext().getString(
						Messages.getStringResource(getAppContext(),
								"SyncronUi_59"));
				if (Settings.isDebug())
					Log.e(TAG, "" + checksum);
			} else if (checksum > 56 && checksum < 5556) {
				msg += getAppContext().getString(
						Messages.getStringResource(getAppContext(),
								"SyncronUi_60"));
				if (Settings.isDebug())
					Log.e(TAG, "" + checksum);
			}
			return msg;
		}
		if (isOPSAVE == true) {
			if (checksum == 1) {
				msg += getAppContext().getString(
						Messages.getStringResource(getAppContext(),
								"SyncronUi_19")); //$NON-NLS-1$

			} else if (checksum == 3) {
				msg += getAppContext().getString(
						Messages.getStringResource(getAppContext(),
								"SyncronUi_20")); //$NON-NLS-1$
				if (Settings.isDebug())
					Log.e(TAG,
							getAppContext().getString(
									Messages.getStringResource(getAppContext(),
											"SyncronUi_21"))); //$NON-NLS-1$

			} else {
				msg += getAppContext().getString(
						Messages.getStringResource(getAppContext(),
								"SyncronUi_22")); //$NON-NLS-1$
			}
			if (union < 400) {
				if (saveTel == 50 || saveTel < 100) {
					msg += getAppContext().getString(
							Messages.getStringResource(getAppContext(),
									"SyncronUi_23")); //$NON-NLS-1$
				}
				if (saveTelNot == 50 || saveTelNot < 100) {
					msg += getAppContext().getString(
							Messages.getStringResource(getAppContext(),
									"SyncronUi_24")); //$NON-NLS-1$
				}
				if (saveVoice == 50 || saveVoice < 100) {
					msg += getAppContext().getString(
							Messages.getStringResource(getAppContext(),
									"SyncronUi_25")); //$NON-NLS-1$
				}
				if (saveVoiceNot == 50 || saveVoiceNot < 100) {

					msg += getAppContext().getString(
							Messages.getStringResource(getAppContext(),
									"SyncronUi_26")); //$NON-NLS-1$
				}

				if (saveTel == 100) {
					msg += getAppContext().getString(
							Messages.getStringResource(getAppContext(),
									"SyncronUi_27")); //$NON-NLS-1$
				}
				if (saveTelNot == 100) {
					msg += getAppContext().getString(
							Messages.getStringResource(getAppContext(),
									"SyncronUi_28")); //$NON-NLS-1$
				}
				if (saveVoice == 100) {
					msg += getAppContext().getString(
							Messages.getStringResource(getAppContext(),
									"SyncronUi_29"));//$NON-NLS-1$
				}
				if (saveVoiceNot == 100) {

					msg += getAppContext().getString(
							Messages.getStringResource(getAppContext(),
									"SyncronUi_30")); //$NON-NLS-1$
				}
			} else if (union == 400) {

				if (saveTel == 100) {
					msg += getAppContext().getString(
							Messages.getStringResource(getAppContext(),
									"SyncronUi_31")); //$NON-NLS-1$
				}
				if (saveTelNot == 100) {
					msg += getAppContext().getString(
							Messages.getStringResource(getAppContext(),
									"SyncronUi_32"));//$NON-NLS-1$
				}
				if (saveVoice == 100) {
					msg += getAppContext().getString(
							Messages.getStringResource(getAppContext(),
									"SyncronUi_33")); //$NON-NLS-1$
				}
				if (saveVoiceNot == 100) {

					msg += getAppContext().getString(
							Messages.getStringResource(getAppContext(),
									"SyncronUi_34")); //$NON-NLS-1$
				}

				return getAppContext().getString(
						Messages.getStringResource(getAppContext(),
								"SyncronUi_35")); //$NON-NLS-1$

			} else if (union > 400 && union <= 800) {

				return getAppContext().getString(
						Messages.getStringResource(getAppContext(),
								"SyncronUi_36")); //$NON-NLS-1$

			} else if (union > 800) {

				if (saveTel == 300) {
					msg += getAppContext().getString(
							Messages.getStringResource(getAppContext(),
									"SyncronUi_37"));//$NON-NLS-1$
				}
				if (saveTelNot == 300) {
					msg += getAppContext().getString(
							Messages.getStringResource(getAppContext(),
									"SyncronUi_38")); //$NON-NLS-1$
				}
				if (saveVoice == 300) {
					msg += getAppContext().getString(
							Messages.getStringResource(getAppContext(),
									"SyncronUi_39")); //$NON-NLS-1$
				}
				if (saveVoiceNot == 300) {

					msg += getAppContext().getString(
							Messages.getStringResource(getAppContext(),
									"SyncronUi_40")); //$NON-NLS-1$
				}
			}
		} else if (isOPSAVE == false) {
			if (union < 400) {
				if (restoreTel == 50 || restoreTel < 100) {
					msg += getAppContext().getString(
							Messages.getStringResource(getAppContext(),
									"SyncronUi_41")); //$NON-NLS-1$
				}
				if (restoreTelNot == 50 || restoreTelNot < 100) {
					msg += getAppContext().getString(
							Messages.getStringResource(getAppContext(),
									"SyncronUi_42")); //$NON-NLS-1$
				}
				if (restoreVoice == 50 || restoreVoice < 100) {
					msg += getAppContext().getString(
							Messages.getStringResource(getAppContext(),
									"SyncronUi_43"));//$NON-NLS-1$
				}
				if (restoreVoiceNot == 50 || restoreVoiceNot < 100) {

					msg += getAppContext().getString(
							Messages.getStringResource(getAppContext(),
									"SyncronUi_44")); //$NON-NLS-1$
				}
			} else if (union == 400) {

				return getAppContext().getString(
						Messages.getStringResource(getAppContext(),
								"SyncronUi_45")); //$NON-NLS-1$

			} else if (union > 800) {

				if (restoreTel == 300) {
					msg += getAppContext().getString(
							Messages.getStringResource(getAppContext(),
									"SyncronUi_46")); //$NON-NLS-1$
				}
				if (restoreTelNot == 300) {
					msg += getAppContext().getString(
							Messages.getStringResource(getAppContext(),
									"SyncronUi_47")); //$NON-NLS-1$
				}
				if (restoreVoice == 300) {
					msg += getAppContext().getString(
							Messages.getStringResource(getAppContext(),
									"SyncronUi_48")); //$NON-NLS-1$
				}
				if (restoreVoiceNot == 300) {

					msg += getAppContext().getString(
							Messages.getStringResource(getAppContext(),
									"SyncronUi_49"));//$NON-NLS-1$
				}
			}

		}
		return msg;

	}

	public static void storeOpCodes(String opcode, int pos) {
		if (justTCheck) {
			OPTEST[pos] = Integer.parseInt(opcode);

		} else {
			if (isOPSAVE == true && opcode != "") { //$NON-NLS-1$
				OPSAVE[pos] = Integer.parseInt(opcode);
			} else if (isOPSAVE == false && opcode != "") { //$NON-NLS-1$
				OPRESTORE[pos] = Integer.parseInt(opcode);
			}
		}

	}

	public static enum bouton {

		BUTTONTEL, BUTTONRESTORE;
		/*
		 * 
		 */
		private boolean valeur;

		public boolean getValue() {
			return valeur;
		}

		bouton() {
			this.valeur = false;
		}

		public void setValue(boolean value) {
			this.valeur = value;
		}

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		SyncronUi.context = getApplicationContext();
		justTCheck = true;
		popSyncList();
		if (Settings.isDebug())
			Log.i(TAG, "Debug enable"); //$NON-NLS-1$

		setContentView(R.layout.syncronbasefragment);
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		FragmentManager fd = getSupportFragmentManager();
		prev = fd.findFragmentByTag("dialog"); //$NON-NLS-1$
		if (prev == null) {
			newFragment = new MyDialogFragment();
			ft.add(R.id.SyncronOne, newFragment);
			ft.commit();
		}
		STARTOP = getResources().getString(R.string.STARTOP);
		tvTEL = (TextView) findViewById(R.id.syncronTextTEL);

		rButton = (RadioButton) findViewById(R.id.radioButton1);
		tvTEL.setText(getAppContext().getString(
				Messages.getStringResource(getAppContext(), "SyncronUi_54"))); //$NON-NLS-1$

		rButton.setChecked(false);
		rButton.setEnabled(false);
		rButton.setVisibility(View.INVISIBLE);

		ImageButton buttonTEL = (ImageButton) findViewById(R.id.syncronTEL);
		buttonTEL.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SyncronUi.bouton.BUTTONTEL.setValue(true);
				SyncronUi.bouton.BUTTONRESTORE.setValue(false);
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							showDialog();
						} catch (NoSuchAlgorithmException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}).start();

			}
		});
		
		ImageButton buttonRESTORE = (ImageButton) findViewById(R.id.syncronRESTORE);
		buttonRESTORE.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				justTCheck = false;
				SyncronUi.bouton.BUTTONTEL.setValue(false);
				SyncronUi.bouton.BUTTONRESTORE.setValue(true);
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							showDialog();
						} catch (NoSuchAlgorithmException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}).start();

			}
		});

	}

	public void popSyncList() {
		Uri urlEntreeNonSync = Uri.withAppendedPath(
				PersonnalProofContentProvider.CONTENT_URI, "record_sync");
		Cursor c = getContentResolver().query(urlEntreeNonSync, null, null,
				null, null);
		while (c.moveToNext()) {
			String id = c.getString(c
					.getColumnIndex(ProofDataBase.COLUMNRECODINGAPP_ID));

			RecordRPC RecordRCP = new RecordRPC(id, c.getString(c
					.getColumnIndex(ProofDataBase.COLUMN_TELEPHONE)),
					c.getString(c
							.getColumnIndex(ProofDataBase.COLUMN_CONTRACT_ID)),
					c.getString(c
							.getColumnIndex(ProofDataBase.COLUMN_TIMESTAMP)),
					c.getString(c.getColumnIndex(ProofDataBase.COLUMN_FILE)),
					c.getString(c.getColumnIndex(ProofDataBase.COLUMN_SENS)),
					c.getString(c.getColumnIndex(ProofDataBase.COLUMN_TAILLE)),
					c.getString(c.getColumnIndex(ProofDataBase.COLUMN_HTIME)),
					1, "",null);
			records.add(RecordRCP);
			Log.e(TAG, "RECORDRPCSYNC");

		}

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	public static Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) throws NullPointerException {
			/*
			 * communication interThread
			 */

			Log.e(TAG, "Taille du handler syncronui" + msg.getData().size()); //$NON-NLS-1$
			op = msg.getData().getString("opmsg"); //$NON-NLS-1$
			position = msg.getData().getInt("pos"); //$NON-NLS-1$
			int total = msg.getData().getInt("total"); //$NON-NLS-1$
			progDialog.setProgress(total);
			if (position == 50) {

				tv.setText(msg.getData().getString("Hmsg") + "\r\n"); //$NON-NLS-1$ //$NON-NLS-2$
				String finalmessage = comprehensiveOpCode(opCodes(op, true, 0));
				tvTEL.append(finalmessage);
				justTCheck = false;
				Handler fandler = new Handler();
				fandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						hideDialog();
					}
				}, 500);
				return;
			}
			/*
			 * 
			 */
			boolean ABORT_OP = msg.getData().getBoolean("ABORT_OP"); //$NON-NLS-1$
			if (ABORT_OP == true) {
				tv.setText(msg.getData().getString(
						"PAS DE CONNEXION POSSIBLE AU SERVEUR") //$NON-NLS-1$
						+ "\r\n"); //$NON-NLS-1$
				Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						hideDialog();
					}
				}, 2000);
			}

			if (Settings.isDebug())
				Log.i(TAG, "OBJ == " + total); //$NON-NLS-1$

			tv.setText(msg.getData().getString("Hmsg") + "\r\n"); //$NON-NLS-1$ //$NON-NLS-2$
			if (op != null) {
				if (!op.equals("B")) { //$NON-NLS-1$
					opCodes(op, false, position);
				}
			}
			if (msg.getData().getInt("ENDOP") == 1) { //$NON-NLS-1$
				if (op != null) {
					if (!op.equals("B")) { //$NON-NLS-1$
						String finalmessage = comprehensiveOpCode(opCodes(op,
								true, position));

						tvTEL.setText(finalmessage);
						rButton.setVisibility(View.VISIBLE);
						rButton.setChecked(true);

					}
				} else {
					tvTEL.setText(msg.getData().getString("Hmsg"));
				}

				Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						hideDialog();
					}
				}, 2000);

			}

		}
	};

	static void hideDialog() {
		try {
			newFragment.dismiss();
		} catch (NullPointerException e) {
			Log.e(TAG, "NullPointerException on fragment cant dimiss it " //$NON-NLS-1$
					+ e.toString());

		} catch (Exception e) {

			Log.e(TAG,
					"UnKnow Error on fragment cant dimiss it " + e.toString()); //$NON-NLS-1$
		}
	}

	void showDialog() throws NoSuchAlgorithmException, FileNotFoundException {
		// Create the fragment and show it as a dialog.
		newFragment = new MyDialogFragment();
		newFragment.show(getSupportFragmentManager(), "dialog"); //$NON-NLS-1$
		if (!justTCheck) {
			callXML();
		} else {
			new OperationBatchController(getApplicationContext(), handler,
					true, true);
		}
	}

	private void callXML() throws NoSuchAlgorithmException,
			FileNotFoundException {
		if (Settings.isDebug()) {
			Log.i(TAG,
					"BOUTONTELSYNC : " + SyncronUi.bouton.BUTTONTEL.getValue()); //$NON-NLS-1$
			Log.i(TAG, "BUTTONRESTORE : " //$NON-NLS-1$
					+ SyncronUi.bouton.BUTTONRESTORE.getValue());
		}

		if (SyncronUi.bouton.BUTTONTEL.getValue() == true) {

			doSyncSAVE();

		} else if (SyncronUi.bouton.BUTTONRESTORE.getValue() == true) {

			doSyncRESTORE();

		}
	}

	public static Context getAppContext() {
		return SyncronUi.context;
	}

	public static Handler handlerFTP = new Handler() {
		@Override
		public void handleMessage(Message msg) throws NullPointerException {
			Log.e(TAG, "Taille du handler " + TAG + " " + msg.getData().size()); //$NON-NLS-1$ //$NON-NLS-2$
			boolean FIN = msg.getData().getBoolean("downloadFIN"); //$NON-NLS-1$
			String message = msg.getData().getString("HUMANIZE"); //$NON-NLS-1$
			Log.e(TAG,
					"Opération retour success:{" + FIN + "}message:{" + message + "}"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			tvTEL.append("\r\n" + message + "\r\n");
		}
	};

	private void doSyncSAVE() throws NoSuchAlgorithmException,
			FileNotFoundException {
		/*
		 * isOPSAVE == TRUE
		 */
		isOPSAVE = true;
		/*
		 * cascade des methodes à partir de OperationBatchTelePhone
		 */
		new OperationBatchController(getApplicationContext(), handler, true,
				false);

	}

	private void doSyncRESTORE() throws NoSuchAlgorithmException,
			FileNotFoundException {
		/*
		 * isOPSAVE == FALSE
		 */
		isOPSAVE = false;

		/*
		 * cascade des methodes à partir de OperationBatchTelePhone
		 */
		new OperationBatchTelePhone(getApplicationContext(), true, handler,
				true);

	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		// Save the user’s current game state

		// Always call the superclass so it can save the view hierarchy state
		super.onSaveInstanceState(savedInstanceState);
	}

	public static class MyDialogFragment extends SherlockDialogFragment {
		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);

		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			View v = inflater.inflate(R.layout.syncronprogressfragment,
					container, false);
			progDialog = (ProgressBar) v.findViewById(R.id.PGB);
			progDialog.setMax(100);
			tv = (TextView) v.findViewById(R.id.inline_text);
			tv.setText(STARTOP);
			return v;

		}

		@Override
		public void onCancel(DialogInterface dialog) {
			getActivity().finish();
		}
		// Handler on the main (UI) thread that will receive messages from the
		// second thread and update the progress.

	}

	public Context getActivity() {
		// TODO Auto-generated method stub
		return this.getApplicationContext();
	}

}
