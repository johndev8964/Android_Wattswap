package com.citrusbug.wattswap.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.citrusbug.wattswap.R;
import com.citrusbug.wattswap.bean.Survey;
import com.citrusbug.wattswap.util.Constant;
import com.citrusbug.wattswap.util.DatabaseHandler;
import com.citrusbug.wattswap.util.DropboxHelper;
import com.citrusbug.wattswap.util.ImageUtil;
import com.citrusbug.wattswap.util.NetworkUtil;
import com.citrusbug.wattswap.util.Pref;
import com.citrusbug.wattswap.util.NetworkUtil.NewSurvey;

@SuppressLint({ "CutPasteId", "ClickableViewAccessibility" })
public class AddNewSurveyActivity extends Activity implements OnTouchListener,OnDateSetListener {
	
	RelativeLayout progressLayout;
	//TextView object declaration used to access UI lable 
	private TextView titleTextView;
	private TextView cancelTextView;
	private TextView saveTextView;
	private TextView deleteTextView;

	//EditText object declaration used to access EditText Value 
	EditText editText_SurveyName;
	EditText editText_Add_Line1;
	EditText editText_Add_Line2;
	EditText editText_City;
	EditText editText_Zip;
	EditText editText_CName;
	EditText editText_Phone;
	EditText editText_Email;
	EditText editText_Note;
	EditText editText_Scheduled;
	EditText editText_Howmanyunits;
	EditText editText_Buildingtype;
	EditText editText_refferedby;
	EditText editText_totalsquarefootage;
	EditText editText_utilitycmpny;
	EditText editText_accountnum;
	TextView remove_Image;
	ImageView cameraImageView;
	String sid;
	Spinner spinner_State;

	Bitmap thumbnail;
	String finaleImagePath = null;
	String[] statesArray = null;
	ContentValues values;
	private int year;
	private int month;
	private int day;
	static final int DATE_PICKER_ID = 1111;
	
	ArrayAdapter<String> imageOptionAdapter;
	private static final int PICK_FROM_CAMERA = 1;
	private static final int PICK_FROM_FILE = 2;
	private Uri mImageCaptureUri;
	//private DbxAccountManager mDbxAcctMgr;
	
	File dirFile;
    
	DatabaseHandler dbHandler;
	Context mContext;
	Survey surveyItem;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_addnewsurvey);
		
		FontChangeCrawler fontChanger = new FontChangeCrawler(getAssets(), "gothic.ttf");
	    fontChanger.replaceFonts((ViewGroup)this.findViewById(android.R.id.content));
		mContext = this;
		Constant.CONTEXT = mContext;
		Constant.activity = AddNewSurveyActivity.this;

		getUIControlId();

		String dirNamePath = Constant.SD_CARD_PATH;
		dirFile = new File(dirNamePath);
		if (!dirFile.exists()) {
			dirFile.mkdir();
		}

		if (Constant.ISUPDATEMODE) {
			surveyItem = (Survey) getIntent().getSerializableExtra("survey");
			titleTextView.setText(Constant.SURVEY_NAME);
		}

		dbHandler = new DatabaseHandler(getApplicationContext());
		if (Constant.ISUPDATEMODE) {
			setValueToFiled();
		} else {
			Calendar c = Calendar.getInstance();
			year = c.get(Calendar.YEAR);
			month = c.get(Calendar.MONTH);
			day = c.get(Calendar.DAY_OF_MONTH);
			month = month + 1;
			String dateValue = "";
			if (month < 10) {
				dateValue = "0" + month;
			} else {
				dateValue = dateValue + month;
			}
			if (day < 10) {
				dateValue = dateValue + "-0" + day;
			} else {
				dateValue = dateValue + "-" + day;
			}
			dateValue = dateValue + "-" + year;
			editText_Scheduled.setText(dateValue);
		}
		
		if(NetworkUtil.getConnectivityStatus(mContext) == 1 || NetworkUtil.getConnectivityStatus(mContext) == 2 ) {
			GetStatesTask getStatesTask = new GetStatesTask();
			getStatesTask.execute();
		}
		else {
			Toast.makeText(mContext, NetworkUtil.getConnectivityStatusString(mContext), Toast.LENGTH_LONG).show();
		}
		
	}

	public class GetStatesTask extends AsyncTask<Void, Void, String> {

		GetStatesTask() {
        }

        @Override
        protected String doInBackground(Void... params) {
            return NetworkUtil.getStates();
        }

		@Override
        protected void onPostExecute(String result) {
			JSONObject reader;
			try {
				if(result != null) {
					reader = new JSONObject(result);
					String status = reader.getString("status");
					
			        if (status.equals("Success")) {
			        	JSONArray statesJSONArray = reader.getJSONArray("data");
			        	
			        	statesArray = new String[statesJSONArray.length()];
			        	for(int i = 0; i < statesJSONArray.length(); i ++) {
			        		JSONObject state = (JSONObject) statesJSONArray.get(i);
			        		statesArray[i] = state.getString("state_name");
			        	}
			        }
				}
				else {
					Toast.makeText(mContext, "Server Error!", Toast.LENGTH_LONG).show();
				}
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Toast.makeText(mContext, "Server Error!", Toast.LENGTH_LONG).show();
			}
	    }

        @Override
        protected void onCancelled() {

        }
    }
	
	
	/**
	 * This method used to get User Interface(Layout) Control Id. This method called only one time in Activity Life cycle.
	 */
	@SuppressWarnings("rawtypes")
	public void getUIControlId() {
		progressLayout = (RelativeLayout) findViewById(R.id.progressLayout);
		progressLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		
		progressLayout.setVisibility(View.GONE);
		editText_SurveyName = (EditText) findViewById(R.id.editText_SurveyName_AddNewSurvey);
		editText_Add_Line1 = (EditText) findViewById(R.id.editText_Add1_AddNewSurvey);
		editText_Add_Line2 = (EditText) findViewById(R.id.editText_Add2_AddNewSurvey);
		editText_City = (EditText) findViewById(R.id.editText_City_AddNewSurvey);
		editText_Zip = (EditText) findViewById(R.id.editText_Zip_AddNewSurvey);
		editText_CName = (EditText) findViewById(R.id.editText_CName_AddNewSurvey);
		editText_Phone = (EditText) findViewById(R.id.editText_Phone_AddNewSurvey);
		editText_Email = (EditText) findViewById(R.id.editText_Email_AddNewSurvey);
		editText_Scheduled = (EditText) findViewById(R.id.editText_Scheduled_AddNewSurvey);
		editText_Note = (EditText) findViewById(R.id.editText_Note_AddNewSurvey);
		editText_Howmanyunits=(EditText) findViewById(R.id.editText_Howmanyunits_AddNewSurvey);
	    editText_Buildingtype=(EditText) findViewById(R.id.editText_Buildingtype_AddNewSurvey);
		editText_refferedby=(EditText) findViewById(R.id.editText_refferedby_AddNewSurvey);
		editText_totalsquarefootage=(EditText) findViewById(R.id.editText_totalsquarefootage_AddNewSurvey);
		editText_utilitycmpny=(EditText) findViewById(R.id.editText_utilitycmpny_AddNewSurvey);
		editText_accountnum=(EditText) findViewById(R.id.editText_accountnum_AddNewSurvey);
		cameraImageView = (ImageView) findViewById(R.id.cameraTextView_Addsurvey);
	    remove_Image=(TextView) findViewById(R.id.removeImageTextView_addsurvey);;
		
		spinner_State = (Spinner) findViewById(R.id.spinner_state);

		titleTextView = (TextView) findViewById(R.id.titleTextView_NewSurvey);
		cancelTextView = (TextView) findViewById(R.id.cancel_TextView_AddNewSurvey);
		saveTextView = (TextView) findViewById(R.id.save_TextView_AddNewSurvey);
		deleteTextView = (TextView) findViewById(R.id.delete_Survey_TextView);

		ArrayAdapter stateAdapter = ArrayAdapter.createFromResource(this,
				R.array.state, R.layout.spinner_layout); // change the last
															// argument here to
															// your xml above.
		stateAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_State.setAdapter(stateAdapter);
		
		if (Constant.ISUPDATEMODE) {
			deleteTextView.setVisibility(View.VISIBLE);
		}
		cancelTextView.setOnTouchListener(this);
		saveTextView.setOnTouchListener(this);
		deleteTextView.setOnTouchListener(this);
		editText_Scheduled.setOnTouchListener(this);
		cameraImageView.setOnTouchListener(this);
		remove_Image.setOnTouchListener(this);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnTouchListener#onTouch(android.view.View,
	 * android.view.MotionEvent)
	 */
	@SuppressWarnings("deprecation")
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		int viewId = v.getId();
		switch (viewId) {
		case R.id.cancel_TextView_AddNewSurvey:
			showAlertDialog();
			break;
		case R.id.save_TextView_AddNewSurvey:
			addNewSurvey();
			break;
		case R.id.delete_Survey_TextView:
			deleteSurvey();
			break;
		case R.id.editText_Scheduled_AddNewSurvey:
			showDialog(DATE_PICKER_ID);
			break;
		case R.id.removeImageTextView_addsurvey:
			cameraImageView.setImageResource(R.drawable.icon_camera);
			remove_Image.setVisibility(View.GONE);
			finaleImagePath = "";
			updateImage();
            break;
		case R.id.cameraTextView_Addsurvey:
			PreperImageSelectionDailog();
			break;
		}
		return false;
	}

	/**
	 * This method used to add new survey in the database.
	 */
	@SuppressWarnings("static-access")
	public void addNewSurvey() {
		if (isValidate()) {
			if (isValidAddress()) {

					values = new ContentValues();
					
					NewSurvey newSurvey = new NewSurvey();
					
					newSurvey.supervisor_id = Constant.SUPERVISOR_ID;
					
					values.put(DatabaseHandler.KEY_SNAME, Character.toUpperCase(editText_SurveyName
							.getText().toString().charAt(0)) + editText_SurveyName
							.getText().toString().substring(1) );
					
					newSurvey.survey_facility_name = Character.toUpperCase(editText_SurveyName
							.getText().toString().charAt(0)) + editText_SurveyName
							.getText().toString().substring(1);
					
					values.put(DatabaseHandler.KEY_ADDRESS_LINE1,
							editText_Add_Line1.getText().toString());
					
					newSurvey.survey_facility_add_l1 = editText_Add_Line1.getText().toString() + "";
					
					values.put(DatabaseHandler.KEY_ADDRESS_LINE2,
							editText_Add_Line2.getText().toString());
					
					newSurvey.survey_facility_add_l2 = editText_Add_Line2.getText().toString() + "";
					
					values.put(DatabaseHandler.KEY_CITY, editText_City
							.getText().toString());
					
					newSurvey.survey_facility_city = editText_City
							.getText().toString(); 
					
					values.put(DatabaseHandler.KEY_STATE, spinner_State
							.getSelectedItem().toString());
					
					if(statesArray.length == 0) {
						Toast.makeText(mContext, "Server Error!", Toast.LENGTH_LONG);
						return;
					}
					for (int i = 0; i < statesArray.length; i++) {
						if(statesArray[i].equalsIgnoreCase(spinner_State
							.getSelectedItem().toString())) {
							newSurvey.state_id = Integer.toString(i + 1);
						}
					}
					
					values.put(DatabaseHandler.KEY_CNAME, editText_CName
							.getText().toString());
					
					newSurvey.survey_contact_name = editText_CName
							.getText().toString() + "";
					
					values.put(DatabaseHandler.KEY_ZIP, editText_Zip.getText()
							.toString());
					
					newSurvey.survey_facility_zip = editText_Zip.getText()
							.toString() + "";
					
					if(editText_Email.getText().toString() != null && editText_Email.getText().toString().length() > 0 && !isValidEmail()) {
						Toast.makeText(mContext, "Invalid Email Address.", Toast.LENGTH_LONG).show();
						return;
					}
					
					values.put(DatabaseHandler.KEY_EMAIL, editText_Email
							.getText().toString());
					
					newSurvey.survey_contact_email = editText_Email
							.getText().toString() + "";
					
					values.put(DatabaseHandler.KEY_PHONE, editText_Phone
							.getText().toString());
					
					newSurvey.survey_contact_phone = editText_Phone
							.getText().toString() + "";
					
					values.put(DatabaseHandler.KEY_SCHEDULED,
							editText_Scheduled.getText().toString());
					
					newSurvey.survey_schedule_date = editText_Scheduled.getText().toString() + "";
					
					values.put(DatabaseHandler.KEY_NOTE, editText_Note
							.getText().toString());
					
					newSurvey.survey_note = editText_Note
							.getText().toString() + "";
					
					values.put(DatabaseHandler.KEY_BUNIT,
							editText_Howmanyunits.getText().toString());
					
					newSurvey.survey_building_units = editText_Howmanyunits.getText().toString() + "";
					
					values.put(DatabaseHandler.KEY_BTYPE,
							editText_Buildingtype.getText().toString());
					
					newSurvey.survey_building_type = editText_Buildingtype.getText().toString() + "";
					
					values.put(DatabaseHandler.KEY_BREFF,
							editText_refferedby.getText().toString());
					
					newSurvey.survey_reffered_by = editText_refferedby.getText().toString() + "";
					
					values.put(DatabaseHandler.KEY_BTOTAL_FOOTAGE,
							editText_totalsquarefootage.getText().toString());
					
					newSurvey.survey_sq_foot = editText_totalsquarefootage.getText().toString() + "";
					
					values.put(DatabaseHandler.KEY_UTILITY_CMPNY,
							editText_utilitycmpny.getText().toString());
					
					newSurvey.survey_utility_company = editText_utilitycmpny.getText().toString() + "";
					
					values.put(DatabaseHandler.KEY_ACCOUNT_NUMBER,
							editText_accountnum.getText().toString());
					
					newSurvey.survey_account_no = editText_accountnum.getText().toString();
					
					values.put(DatabaseHandler.KEY_PATH, finaleImagePath);
					
					if(finaleImagePath != null) {
						byte[] bytes;
						byte[] buffer = new byte[8192];
						ByteArrayOutputStream output = new ByteArrayOutputStream();
						int bytesRead;
						try {
							InputStream inputStream = new FileInputStream(finaleImagePath);//You can get an inputStream using any IO API
							while ((bytesRead = inputStream.read(buffer)) != -1) {
						    output.write(buffer, 0, bytesRead);
						}
						} catch (IOException e) {
							e.printStackTrace();
						}
						bytes = output.toByteArray();
						newSurvey.survey_image_b64 = Base64.encodeToString(bytes, Base64.DEFAULT);
					}
					
					DropboxHelper.SaveToDropbox(finaleImagePath);
					
					if (Constant.ISUPDATEMODE) {
						int row = dbHandler.updateSurvey(values,
								surveyItem.surveyId);
						if (row > 0)
							finish();
					} else {
					//Save to server.				
						if(NetworkUtil.getConnectivityStatus(mContext) == 1 || NetworkUtil.getConnectivityStatus(mContext) == 2 ) {
							progressLayout.setVisibility(View.VISIBLE);
							AddNewSurveyTask addNewSurveyTask = new AddNewSurveyTask(newSurvey);
							addNewSurveyTask.execute();
						}
						else {
							Toast.makeText(mContext, NetworkUtil.getConnectivityStatusString(mContext), Toast.LENGTH_LONG);
						}
					}
					
			} else {
				Toast.makeText(getApplicationContext(),
						"Please enter city or select State", Toast.LENGTH_SHORT)
						.show();
			}
		} else {
			Toast.makeText(getApplicationContext(),
					"Please enter Facility Name", Toast.LENGTH_SHORT)
					.show();
		}
	}
	
	void saveToDatabase (ContentValues values) {
		
		values.put(DatabaseHandler.KEY_SID, Integer.valueOf(Constant.SURVEY_ID));
		int rowid = (int) dbHandler.addNewSurvey(values);
		if (rowid > 0) {
			Pref.setValue(Constant.PREF_LAST_SURVERY_ID, rowid
					+ "");
			if (editText_SurveyName.getText().length() > 15) {
				String name = editText_SurveyName.getText()
						.toString().substring(0, 15)
						+ "...";
				Constant.SURVEY_NAME = Character.toUpperCase(name.charAt(0)) + name.substring(1);
			} else {
				Constant.SURVEY_NAME = Character.toUpperCase(editText_SurveyName
						.getText().toString().charAt(0)) + editText_SurveyName
						.getText().toString().substring(1);
			}
			//Constant.SURVEY_ID = rowid + "";

			Constant.SURVEY_NAME_PATH = ImageUtil
					.getValidName(editText_SurveyName.getText()
							.toString());

			Intent floorListIntent = new Intent(
					getApplicationContext(), FloorList.class);
			startActivity(floorListIntent);
			finish();
		}

	}
	
	public class AddNewSurveyTask extends AsyncTask<Void, Void, String> {

        NewSurvey mNewSurvey;

        AddNewSurveyTask(NewSurvey newSurvey) {
        	mNewSurvey = newSurvey;
        }

        @Override
        protected String doInBackground(Void... params) {
            return NetworkUtil.postAddNewSurveyApp(mNewSurvey);
        }

		@Override
        protected void onPostExecute(String result) {
			JSONObject reader;
			try {
				progressLayout.setVisibility(View.GONE);
				reader = new JSONObject(result);
				String status = reader.getString("status");
		        if (status.equals("Success")) {
		        	Constant.SURVEY_ID = reader.getJSONObject("data").getString("survey_id");
		        	saveToDatabase(values);
		        }
		        else {
		        	Toast.makeText(mContext, "Server Error", Toast.LENGTH_LONG).show();
		        }
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				Toast.makeText(mContext, "Server Error", Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}
	    }

        @Override
        protected void onCancelled() {

        }
    }
	
	static final int REQUEST_LINK_TO_DBX = 0;
	
	/**
	 *   This method used to check Survey Name validation.
	 *    
	 * @return boolean value true or false
	 */
	public boolean isValidate() {
		if (editText_SurveyName.getText().length() <= 0) {
			return false;
		}
		return true;
	}

	/**
	 *   This method used to check Zip or City, State validation.
	 *    
	 * @return boolean value true or false
	 */
	public boolean isValidAddress() {
	
		if (editText_City.getText().length() > 0) {
			return true;
		}
		
		return false;
	}

	/**
	 *  This method used to check Valid format of email address.
	 *    
	 * @return boolean value true or false
	 */
	public boolean isValidEmail() {
		Pattern pattern1 = Pattern
				.compile("^([a-zA-Z0-9_.-])+@([a-zA-Z0-9_.-])+\\.([a-zA-Z])+([a-zA-Z])+");

		if (editText_Email.getText().length() == 0) {
			return true;
		} else {
			Matcher matcher1 = pattern1.matcher(editText_Email.getText()
					.toString());
			if (matcher1.matches())
				return true;
		}
		return false;
	}

	public void showAlertDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setMessage("No changes will be save!");
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
				activityCloseTransition();
			}
		});
		builder.setNegativeButton("No", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		
		AlertDialog alertDialog = builder.create();
		
		alertDialog.show();
	}

	/**
	 *  This method used to delete Survey from Database.
	 */
	public void deleteSurvey() {
		AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(mContext);
		deleteBuilder
				.setMessage("Are you sure you want remove all data of this building?");
		deleteBuilder.setPositiveButton("Yes",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						int rowid = dbHandler
								.deleteSurveyWithChiledRecord(surveyItem.surveyId);
						if (rowid > 0) {
							Toast.makeText(mContext,
									"Recored Deleted successfully",
									Toast.LENGTH_SHORT).show();
							Constant.ISSURVEYDELETED = true;
							activityCloseTransition();
						}
					}
				});
		deleteBuilder.setNegativeButton("No",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		AlertDialog confirmDialog = deleteBuilder.create();
		confirmDialog.show();
	}

	@Override
	@Deprecated
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_PICKER_ID:
			Calendar c = Calendar.getInstance();
			year = c.get(Calendar.YEAR);
			month = c.get(Calendar.MONTH);
			day = c.get(Calendar.DAY_OF_MONTH);
			return new DatePickerDialog(this, this, year, month, day);
		}
		return null;
	}

	/**
	 * This Method called when you set date in DatePicker
	 * 
	 * @param view
	 * @param selectedYear
	 * @param selectedMonth
	 * @param selectedDay
	 */
	@Override
	public void onDateSet(DatePicker view, int selectedYear, int selectedMonth,
			int selectedDay) {
		Calendar c = Calendar.getInstance();
		c.set(selectedYear, selectedMonth, selectedDay);
		year = selectedYear;
		month = selectedMonth;
		day = selectedDay;
		month = month + 1;
		String dateValue = "";

		if (month < 10) {
			dateValue = "0" + month;
		} else {
			dateValue = dateValue + month;
		}
		if (day < 10) {
			dateValue = dateValue + "-0" + day;
		} else {
			dateValue = dateValue + "-" + day;
		}
		dateValue = dateValue + "-" + year;
		editText_Scheduled.setText(dateValue);
	}

	public void setValueToFiled() {
		if (surveyItem != null) {

			editText_SurveyName.setText(surveyItem.surveyName);
			editText_Add_Line1.setText(surveyItem.address1);
			editText_Add_Line2.setText(surveyItem.address2);
			editText_City.setText(surveyItem.city);
			editText_Zip.setText(surveyItem.zip);
			editText_CName.setText(surveyItem.contanctName);
			editText_Phone.setText(surveyItem.phone);
			editText_Email.setText(surveyItem.email);
			editText_Scheduled.setText(surveyItem.scheduledDate);
			editText_Note.setText(surveyItem.note);
			editText_Howmanyunits.setText(surveyItem.buildingUnit);
		    editText_Buildingtype.setText(surveyItem.buildingType);
			editText_refferedby.setText(surveyItem.reffBy);
			editText_totalsquarefootage.setText(surveyItem.totalSquareFootage);
			editText_utilitycmpny.setText(surveyItem.utilityCmpny);
			editText_accountnum.setText(surveyItem.accountNumber);

			
			String stateName = surveyItem.state;
			int statePosition = 0;
			for (int i = 0; i < spinner_State.getAdapter().getCount(); i++) {
				if (stateName.equalsIgnoreCase(spinner_State.getAdapter()
						.getItem(i).toString())) {
					statePosition = i;
					break;
				}
			}
			spinner_State.setSelection(statePosition);
		}
	}
	
	//  camera view
	public void PreperImageSelectionDailog() {
		String str = editText_SurveyName.getText().toString();
		if(str == null || str.equals("")) {
			Toast.makeText(mContext,
					"Please fill the facility name.",
					Toast.LENGTH_SHORT).show();
			editText_SurveyName.requestFocus();
		}
		else {
			String dirNamePath = Constant.SD_CARD_PATH + "/" + Character.toUpperCase(editText_SurveyName
					.getText().toString().charAt(0)) + editText_SurveyName
					.getText().toString().substring(1);
			dirFile = new File(dirNamePath);
			if (!dirFile.exists()) {
				dirFile.mkdir();
			}
		imageOptionAdapter = new ArrayAdapter<String>(this,
				android.R.layout.select_dialog_item, getResources()
						.getStringArray(R.array.image_select_option));
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Select Image");
		builder.setAdapter(imageOptionAdapter,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int item) {
						// pick from camera
						if (item == 0) {

							String fileName = dirFile.getAbsolutePath() + "/"+ Character.toUpperCase(editText_SurveyName
									.getText().toString().charAt(0)) + editText_SurveyName
									.getText().toString().substring(1) + "_"
									+ System.currentTimeMillis() + ".jpg";

							Intent intent = new Intent(
									MediaStore.ACTION_IMAGE_CAPTURE);
							mImageCaptureUri = Uri.fromFile(new File(fileName));
							intent.putExtra(
									android.provider.MediaStore.EXTRA_OUTPUT,
									mImageCaptureUri);
							try {
								intent.putExtra("return-data", true);
								startActivityForResult(intent, PICK_FROM_CAMERA);
							} catch (ActivityNotFoundException e) {
								e.printStackTrace();
							}
						} else if (item == 1) {
							// pick from file
							Intent i = new Intent(
									Intent.ACTION_PICK,
									android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
							startActivityForResult(i, PICK_FROM_FILE);
						} else if (item == 2) {
							dialog.dismiss();
						}
					}
				});

		final AlertDialog dialog = builder.create();
		dialog.show();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode != RESULT_OK)
			return;

		switch (requestCode) {
		case PICK_FROM_CAMERA:
			try {
				finaleImagePath = mImageCaptureUri.getPath();
				thumbnail = ImageUtil.decodeFile(new File(finaleImagePath), 60,
						80);
				Matrix matrix = new Matrix();
				matrix.postRotate(90);
				Bitmap resizedBitmap = Bitmap.createBitmap(thumbnail, 0, 0, thumbnail.getWidth(), thumbnail.getHeight(), matrix, true);
				cameraImageView.setImageBitmap(resizedBitmap);
				remove_Image.setVisibility(View.VISIBLE);

				if (Constant.IS_RESIZE_ENABLE)
					ImageUtil.saveResizedImage(finaleImagePath, true);

					updateImage();

			} catch (Exception e) {
				Log.e("Error: ", e.getMessage());
				e.printStackTrace();
			}
			break;
		case PICK_FROM_FILE:
			try {
				mImageCaptureUri = data.getData();
				String[] filePathColumn = { MediaColumns.DATA };
				Cursor cursor = getContentResolver().query(mImageCaptureUri,
						filePathColumn, null, null, null);
				cursor.moveToFirst();
				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				finaleImagePath = cursor.getString(columnIndex);
				cursor.close();
				thumbnail = ImageUtil.decodeFile(new File(finaleImagePath), 60,
						80);
				 cameraImageView.setImageBitmap(thumbnail);
				 remove_Image.setVisibility(View.VISIBLE);

				String destinationName = dirFile.getAbsolutePath() + "/"+ Character.toUpperCase(editText_SurveyName
						.getText().toString().charAt(0)) + editText_SurveyName
						.getText().toString().substring(1) + "_"
						+ System.currentTimeMillis() + ".jpg";

				File source = new File(finaleImagePath);

				File destination = new File(destinationName);

				if (source.exists()) {
					@SuppressWarnings("resource")
					FileChannel src = new FileInputStream(source).getChannel();
					@SuppressWarnings("resource")
					FileChannel dst = new FileOutputStream(destination)
							.getChannel();
					dst.transferFrom(src, 0, src.size());
					src.close();
					dst.close();

					if (Constant.IS_RESIZE_ENABLE) {
						ImageUtil.saveResizedImage(destinationName, false);
						finaleImagePath = destinationName;
					}
					updateImage();
				}

			} catch (Exception e) {
				Log.e("Error: ", e.getMessage());
				e.printStackTrace();
			}
			break;
		
		}
	}
	public void updateImage() {
		//ContentValues values1 = new ContentValues();
		//values1.put(DatabaseHandler.KEY_PATH, finaleImagePath);
		//dbHandler.updateSurvey(values1, surveyItem.surveyId);
	}
	
	/*@Override
	protected void onResume() {
		super.onResume();
           
			String imagePath = finaleImagePath;
			
			if (imagePath != null && !imagePath.equals("")) {
				thumbnail = ImageUtil.decodeFile(new File(imagePath), 60, 80);
				cameraImageView.setImageBitmap(thumbnail);
				
				remove_Image.setVisibility(View.VISIBLE);
			} else {
				remove_Image.setVisibility(View.GONE);
				cameraImageView.setImageResource(R.drawable.icon_camera);
			}
            }*/

	@Override
	protected void onDestroy() {
		super.onDestroy();
		thumbnail = null;
		dbHandler = null;
		surveyItem = null;
	}

	public void activityCloseTransition() {
		finish();
		overridePendingTransition(R.animator.in_from_left,
				R.animator.out_to_right);
	}
	
	public class FontChangeCrawler
	{
	    private Typeface typeface;

	    public FontChangeCrawler(Typeface typeface)
	    {
	        this.typeface = typeface;
	    }

	    public FontChangeCrawler(AssetManager assets, String assetsFontFileName)
	    {
	        typeface = Typeface.createFromAsset(assets, assetsFontFileName);
	    }

	    public void replaceFonts(ViewGroup viewTree)
	    {
	        View child;
	        for(int i = 0; i < viewTree.getChildCount(); ++i)
	        {
	            child = viewTree.getChildAt(i);
	            if(child instanceof ViewGroup)
	            {
	                // recursive call
	                replaceFonts((ViewGroup)child);
	            }
	            else if(child instanceof TextView)
	            {
	                // base case
	                ((TextView) child).setTypeface(typeface);
	            }
	        }
	    }
	}
}