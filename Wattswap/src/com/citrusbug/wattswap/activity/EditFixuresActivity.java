package com.citrusbug.wattswap.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;

import org.json.JSONException;
import org.json.JSONObject;

import com.citrusbug.wattswap.R;
import com.citrusbug.wattswap.adapter.FixtureTypeAdapter;
import com.citrusbug.wattswap.bean.Fixtures;
import com.citrusbug.wattswap.util.Constant;
import com.citrusbug.wattswap.util.DatabaseHandler;
import com.citrusbug.wattswap.util.DropboxHelper;
import com.citrusbug.wattswap.util.ImageUtil;
import com.citrusbug.wattswap.util.NetworkUtil;
import com.citrusbug.wattswap.util.NetworkUtil.NewFixture;
import com.citrusbug.wattswap.util.Pref;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;

import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


public class EditFixuresActivity extends Activity implements OnTouchListener,
		OnClickListener, OnItemClickListener, OnFocusChangeListener {
	
	RelativeLayout progressLayout;
	// LinearLayout object
	LinearLayout codeLinearLayout;
	LinearLayout styleLinearLayout;
	LinearLayout mountingLinearLayout;
	LinearLayout controlledLinearLayout;
	LinearLayout optionLinearLayout;
	LinearLayout heightLinearLayout;
	LinearLayout ballasttypeLinearLayout;
	LinearLayout ballastfactorLinearLayout;
	LinearLayout fixturetypeLinearLayout;
	
	TextView homeTextView;
	ImageView saveTextView;
	ImageView goDetailedFixture;
	
	static TextView noteTextView;
	static TextView answerTextView;
	static TextView codeTextView;
	static TextView styleTextView;
	static TextView mountingTextView;
	static TextView controlledTextView;
	static TextView optionTextView;
	static TextView heightTextView;
	static TextView ballasttypeTextView;
	static TextView ballastfactorTextView;
	static TextView bulbAnswertextview;
	static TextView backleftTextview;
	static TextView floornameTextview;
	static TextView areanameTextview;
	static TextView fixturetypeTextview;
	static TextView DeleteFixture;
	static TextView Bulb;
	ImageView cameraImageView;
	ImageView pluseImageView;
	ImageView minusaImageView;

	EditText firstEt;
	EditText secondEt;
	EditText et_Note;
	EditText countEt;
	EditText bulbEt;
	EditText wattsEt;
	
	EditText otherOption;
	
	String floorName;
	String areaName;
	
	
	Context mContext;
	Bitmap thumbnail;

	private static final int PICK_FROM_CAMERA = 1001;
	private static final int PICK_FROM_FILE = 1002;
    
	private Uri mImageCaptureUri;
	ArrayAdapter<String> imageOptionAdapter;
	String finaleImagePath = null;
	String buildingName;
	TextView txttitle;
	Fixtures fixInfo;
	int count = 1;

	boolean isVisible = false;
	FixtureTypeAdapter typeadapter;
	// DatabasdeHandler object for database operation
	DatabaseHandler dbHandler;

	// Resource object for access application resources like string
	Resources rs;

	File dirFile;
	Intent receivedIntent;
	String type;
	String arrayName;
	String[] strArray;
	private String newFixtureItem;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// For hide title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// For set user interfcae layout to this activity
		setContentView(R.layout.activity_editfixtures);
		mContext = this;
	    Constant.CONTEXT = this;
	    
	    Typeface face = Typeface.createFromAsset(getAssets(), "gothic.ttf");
	    FontChangeCrawler fontChanger = new FontChangeCrawler(getAssets(), "gothic.ttf");
	    fontChanger.replaceFonts((ViewGroup)this.findViewById(android.R.id.content));
	    
		txttitle = (TextView)findViewById(R.id.title_TextView_EditFixtures);
		txttitle.setTypeface(face);
		fixturetypeTextview= (TextView) findViewById(R.id.fixturetypeTextView);
		fixturetypeTextview.setTypeface(face);
		floornameTextview= (TextView) findViewById(R.id.floorname_editfixture);
		floornameTextview.setTypeface(face);
		areanameTextview= (TextView) findViewById(R.id.areaname_editfixture);
		areanameTextview.setTypeface(face);
		DeleteFixture=(TextView)findViewById(R.id.deleteTextView_fixtures);
		DeleteFixture.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (Constant.ISFIXUPDATEMODE) {
				deleteSurvey();
				}
				else{
					Toast.makeText(mContext,
							"Recored not saved",
							Toast.LENGTH_SHORT).show();
					
				}
				
			}
		});
		
		String fName = getIntent().getStringExtra("floorname");
		String aName = getIntent().getStringExtra("areaname");
		if (Constant.ISFIXUPDATEMODE) {
			fixInfo = (Fixtures) getIntent().getSerializableExtra("fixures");
			fixturetypeTextview.setText(fixInfo.fixtureName);
			txttitle.setText(Constant.SURVEY_NAME);
			floornameTextview.setText(fName);
			
			areanameTextview.setText(aName);
		} else {
			buildingName = getIntent().getStringExtra("type");
			fixturetypeTextview.setText(buildingName);
			txttitle.setText(Constant.SURVEY_NAME);
			floornameTextview.setText(fName);
			areanameTextview.setText(aName);
		}
		
		
		// For access application resources
		rs = getResources();
		
		//DatabaseHandler object initialized to access database instance 
		dbHandler = new DatabaseHandler(mContext);

		// Called for get Layout control id.
		getUiControlId();
		
		
		//SurveyApp SD card Directory path
		String serveyDir = Constant.SD_CARD_PATH + "/"
				+ Constant.SURVEY_NAME_PATH;

		File surveyFile = new File(serveyDir);

		if (!surveyFile.exists()) {
			surveyFile.mkdir();
		}

		String floorDir = surveyFile.getAbsolutePath() + "/" + Constant.FLOOR_NAME_PATH;
		File floorFile =  new File(floorDir);
		if (!floorFile.exists()) {
			floorFile.mkdir();
		}

		String areaDir =  floorDir + "/" + Constant.AREA_NAME_PATH;
		File areaFile = new File(areaDir);
		if (!areaFile.exists()) {
			areaFile.mkdir();
		}
		
		String fixDir = areaDir + "/" + Constant.FIX_NAME_PATH;
		dirFile = new File(fixDir);

		if (!dirFile.exists()) {
			dirFile.mkdir();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnTouchListener#onTouch(android.view.View,
	 * android.view.MotionEvent)
	 */
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		int viewId = v.getId();
		switch (viewId) {
		case R.id.home_TextView_EditFixtures:
			finish();
			Intent intenthome = new Intent(mContext,
					HomeActivity.class);
			startActivity(intenthome);
			activityCloseTransition();
			break;
		case R.id.save_TextView_EditFixtures:
			addNewFixtures();
			break;
		case R.id.backleft_editfixture:
			finish();
			activityCloseTransition();
			break;	
		case R.id.minus_imageView:
			if (count > 0) {
				count = Integer.parseInt(countEt.getText().toString()) - 1;
				countEt.setText("" + count);
			}
			break;
		case R.id.plus_imageView:
			count = Integer.parseInt(countEt.getText().toString()) + 1;
			countEt.setText("" + count);
			break;
		case R.id.cameraTextView_EditFixures:
			pickSelector();
			break;
		case R.id.goDetailedFixtureImageView:
			
			Intent intentDetailedFixture = new Intent(mContext,
					DetailedFixtureActivity.class);
			intentDetailedFixture.putExtra("areaname", areanameTextview.getText().toString());
			intentDetailedFixture.putExtra("typename", fixturetypeTextview.getText().toString());
			intentDetailedFixture.putExtra("codename", codeTextView.getText().toString());
			intentDetailedFixture.putExtra("counts", countEt.getText().toString());
			intentDetailedFixture.putExtra("watts", wattsEt.getText().toString());
			intentDetailedFixture.putExtra("hours", firstEt.getText().toString());
			startActivity(intentDetailedFixture);
			activityOpenTransition();
			break;
//		case R.id.deleteTextView_fixtures:
//			deleteSurvey();
//			break;
		}
		return false;
	}
	
	

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		int action = event.getAction();
		int keyCode = event.getKeyCode();
		switch (keyCode) {
		case KeyEvent.KEYCODE_VOLUME_UP:
			if (action == KeyEvent.ACTION_DOWN) {
				// TODO
				count = Integer.parseInt(countEt.getText().toString()) + 1;
				countEt.setText("" + count);
			}
			return true;
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			if (action == KeyEvent.ACTION_DOWN) {
				if (count > 0) {
					count = Integer.parseInt(countEt.getText().toString()) - 1;
					countEt.setText("" + count);
				}
			}
			return true;
		default:
			return super.dispatchKeyEvent(event);
		}
	}

	/**
	 * This method used to start SelectionActivity. SelectionActivity Display
	 * List based on Type and requestCode
	 * 
	 * @param type
	 *            should be code,style, mouting,option etc
	 * @param requestCode
	 *            used to identify that SelectionActivity open for which type.
	 * 
	 */
	public void startSelectionActivity(String type, int requestCode) {
		Intent selectionIntent = new Intent(mContext, SelectionActivity.class);
		selectionIntent.putExtra("type", type);
		selectionIntent.putExtra("name", buildingName);
		startActivityForResult(selectionIntent, requestCode);
	}

	public void getUiControlId() {
		progressLayout = (RelativeLayout) findViewById(R.id.progressLayout);
		progressLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		progressLayout.setVisibility(View.GONE);
		
		Typeface font = Typeface.createFromAsset( getAssets(), "fontawesome-webfont.ttf" );
		Typeface face = Typeface.createFromAsset(getAssets(), "gothic.ttf");
		fixturetypeLinearLayout= (LinearLayout) findViewById(R.id.type_ll);
		codeLinearLayout = (LinearLayout) findViewById(R.id.code_ll);
		styleLinearLayout = (LinearLayout) findViewById(R.id.style_ll);
		mountingLinearLayout = (LinearLayout) findViewById(R.id.mounting_ll);
		controlledLinearLayout = (LinearLayout) findViewById(R.id.controlled_ll);
		optionLinearLayout = (LinearLayout) findViewById(R.id.option_ll);
		heightLinearLayout = (LinearLayout) findViewById(R.id.height_ll);
		ballasttypeLinearLayout= (LinearLayout) findViewById(R.id.balasttypell);
		ballastfactorLinearLayout=(LinearLayout) findViewById(R.id.balastfactorll);
		homeTextView = (TextView) findViewById(R.id.home_TextView_EditFixtures);
		homeTextView.setTypeface(font);
		saveTextView = (ImageView) findViewById(R.id.save_TextView_EditFixtures);
	//	saveTextView.setTypeface(font);
		Bulb=(TextView) findViewById(R.id.bulb_TextView_EditFixture);
		Bulb.setTypeface(font);
		answerTextView = (TextView) findViewById(R.id.answerTextView);
		bulbAnswertextview=(TextView) findViewById(R.id.answer2TextView);
		//noteTextView = (TextView) findViewById(R.id.noteTextView_fixtures);
		
		codeTextView = (TextView) findViewById(R.id.codeTextView);
		codeTextView.setTypeface(face);
		styleTextView = (TextView) findViewById(R.id.styleTextView);
		styleTextView.setTypeface(face);
		mountingTextView = (TextView) findViewById(R.id.mountingTextView);
		mountingTextView.setTypeface(face);
		controlledTextView = (TextView) findViewById(R.id.controlledTextView);
		controlledTextView.setTypeface(face);
		optionTextView = (TextView) findViewById(R.id.optionTextView);
		optionTextView.setTypeface(face);
		heightTextView = (TextView) findViewById(R.id.heightTextView);
		heightTextView.setTypeface(face);
		ballasttypeTextView= (TextView) findViewById(R.id.balasttypetextview);
		ballasttypeTextView.setTypeface(face);
		ballastfactorTextView= (TextView) findViewById(R.id.balastfactortextview);
		ballastfactorTextView.setTypeface(face);
		backleftTextview= (TextView) findViewById(R.id.backleft_editfixture);
		backleftTextview.setTypeface(font);
		
		countEt = (EditText) findViewById(R.id.countValueEditText_EditFixtures);
		countEt.setText("" + count);

		minusaImageView = (ImageView) findViewById(R.id.minus_imageView);
		pluseImageView = (ImageView) findViewById(R.id.plus_imageView);
		cameraImageView = (ImageView) findViewById(R.id.cameraTextView_EditFixures);
		
		firstEt = (EditText) findViewById(R.id.first_editText);
		secondEt = (EditText) findViewById(R.id.second_editText);
		et_Note = (EditText) findViewById(R.id.et_note_editfixtures);
		bulbEt= (EditText) findViewById(R.id.bulb_editText);
		wattsEt= (EditText) findViewById(R.id.wattsperbulb_editText);
		
		goDetailedFixture = (ImageView) findViewById(R.id.goDetailedFixtureImageView);
		
		 
		bulbEt.addTextChangedListener(new TextWatcher(){

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
				if (bulbEt.getText().length() > 0
						&& wattsEt.getText().length() > 0) {
					int bulbValue = Integer.parseInt(bulbEt.getText()
							.toString());
					int wattsValue = Integer.parseInt(wattsEt.getText()
							.toString());
					int sum = (bulbValue * wattsValue);
					bulbAnswertextview.setText("" + sum);
				} else {
					bulbAnswertextview.setText("");
				}
			}
		});

		wattsEt.addTextChangedListener(new TextWatcher() {
			
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			if (bulbEt.getText().length() > 0
					&& wattsEt.getText().length() > 0) {
				int bulbValue = Integer.parseInt(bulbEt.getText()
						.toString());
				int wattsValue = Integer.parseInt(wattsEt.getText()
						.toString());
				int sum = (bulbValue * wattsValue);
				bulbAnswertextview.setText("" + sum);
			} else {
				bulbAnswertextview.setText("");
			}
		}
	});

		
		
		
		////////////// logic for hours and weeks.////////////
		firstEt.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				String txt=firstEt.getText().toString();
				if(txt.length()> 1){
				
					if(Integer.parseInt(txt)>24){
						firstEt.setText(txt.substring(0, 1));
						firstEt.setSelection(1);
					}
					else{
						//secondEt.requestFocus();
					}
				}
				
				
				/*Calc logic*/
				if (firstEt.getText().length() > 0
						&& secondEt.getText().length() > 0) {
					int firstValue = Integer.parseInt(firstEt.getText()
							.toString());
					int secondValue = Integer.parseInt(secondEt.getText()
							.toString());
					int sum = (firstValue * secondValue) * 52;
					answerTextView.setText("" + sum);
				} else {
					answerTextView.setText("");
				}
			}
		});
		
		secondEt.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				String txt=secondEt.getText().toString();
				
				if(txt.length()> 0 && Integer.parseInt(txt)>7){
					secondEt.setText("");
				}
				
				if (firstEt.getText().length() > 0
						&& secondEt.getText().length() > 0) {
					int firstValue = Integer.parseInt(firstEt.getText()
							.toString());
					int secondValue = Integer.parseInt(secondEt.getText()
							.toString());
					int sum = (firstValue * secondValue) * 52;
					answerTextView.setText("" + sum);
				} else {
					answerTextView.setText("");
				}
				
			}
		});
		
		cameraImageView.setOnTouchListener(this);
		minusaImageView.setOnTouchListener(this);
		pluseImageView.setOnTouchListener(this);
		homeTextView.setOnTouchListener(this);
		backleftTextview.setOnTouchListener(this);
		saveTextView.setOnTouchListener(this);
		goDetailedFixture.setOnTouchListener(this);
		
		codeLinearLayout.setOnClickListener(this);
		styleLinearLayout.setOnClickListener(this);
		mountingLinearLayout.setOnClickListener(this);
		controlledLinearLayout.setOnClickListener(this);
		optionLinearLayout.setOnClickListener(this);
		heightLinearLayout.setOnClickListener(this);
		ballasttypeLinearLayout.setOnClickListener(this);
		ballastfactorLinearLayout.setOnClickListener(this);
		fixturetypeLinearLayout.setOnClickListener(this);
		
		if (Constant.ISFIXUPDATEMODE) {
			setDataToUI();
		} else {
			if (buildingName != null)
				setValueToUI();
		}

//		firstEt.setOnKeyListener(new View.OnKeyListener() {
//			
//			@Override
//			public boolean onKey(View v, int keyCode, KeyEvent event) {
//				// TODO Auto-generated method stub
//				if(firstEt.getText().toString().equals("3")&&!(keyCode==KeyEvent.KEYCODE_BACK||keyCode==KeyEvent.KEYCODE_BACKSLASH))
//					return true;
//				return false;
//			}
//		});
		

	}
	
	/**
	 *  This method used to delete Survey from Database.
	 */
	public void deleteSurvey() {
		AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(mContext);
		deleteBuilder
				.setMessage("Are you sure you want remove all data of this Fixture?");
		deleteBuilder.setPositiveButton("Yes",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						int rowid = dbHandler
								.deleteFixture(fixInfo.fixtureId);
						Toast.makeText(mContext,
								"Recored Deleted successfully"+ rowid,
								Toast.LENGTH_SHORT).show();
						
						 finish();
						 activityCloseTransition();
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

	/**
	 * This method used to display activity transition from left to right
	 */
	public void activityCloseTransition() {
		hideKeyboard();
		overridePendingTransition(R.animator.in_from_left,
				R.animator.out_to_right);
	}
	
	public void activityOpenTransition() {
		
		overridePendingTransition(R.animator.in_from_right,
				R.animator.out_to_left);
	}
	/**
	 * This method used to set value to UI when User in edit mode
	 * 
	 */
	public void setValueToUI() {
		if (buildingName.equalsIgnoreCase("Linear T-12")) {
			codeTextView.setText(rs.getStringArray(R.array.CodeLinearT12)[0]);
			styleTextView.setText(rs.getStringArray(R.array.StyleLinearT12)[0]);
		} else if (buildingName.equalsIgnoreCase("Linear T-8")) {
			codeTextView.setText(rs.getStringArray(R.array.CodeLinearT8)[0]);
			styleTextView
					.setText(rs.getStringArray(R.array.StyleLinearT8_T5)[0]);
		} else if (buildingName.equalsIgnoreCase("Linear T-5")) {
			codeTextView.setText(rs.getStringArray(R.array.CodeLinearT5)[0]);
			styleTextView
					.setText(rs.getStringArray(R.array.StyleLinearT8_T5)[0]);
		} else if (buildingName.equalsIgnoreCase("Circline")) {
			codeTextView.setText(rs.getStringArray(R.array.CodeCircline)[0]);
			styleTextView.setText(rs.getStringArray(R.array.StyleCircline)[0]);
		} else if (buildingName.equalsIgnoreCase("Inc")) {
			codeTextView.setText(rs.getStringArray(R.array.CodeInc)[0]);
			styleTextView.setText(rs.getStringArray(R.array.StyleInc)[0]);
		} else if (buildingName.equalsIgnoreCase("Inc - Hal")) {
			codeTextView.setText(rs.getStringArray(R.array.CodeIncHal)[0]);
			styleTextView.setText(rs.getStringArray(R.array.StyleIncHal)[0]);
		} else if (buildingName.equalsIgnoreCase("Exit Inc")) {
			codeTextView.setText(rs.getStringArray(R.array.CodeExitInc)[0]);
			styleTextView.setText(rs.getStringArray(R.array.StyleExit)[0]);
		} else if (buildingName.equalsIgnoreCase("Exit CFL")) {
			codeTextView.setText(rs.getStringArray(R.array.CodeExitCFL)[0]);
			styleTextView.setText(rs.getStringArray(R.array.StyleExit)[0]);
		} else if (buildingName.equalsIgnoreCase("Exit LED")) {
			codeTextView.setText(rs.getStringArray(R.array.CodeExitLED)[0]);
			styleTextView.setText(rs.getStringArray(R.array.StyleExit)[0]);
		} else if (buildingName.equalsIgnoreCase("CFL")) {
			codeTextView.setText(rs.getStringArray(R.array.CodeCFL)[0]);
			styleTextView.setText(rs.getStringArray(R.array.StyleCFL)[0]);
		} else if (buildingName.equalsIgnoreCase("MH")) {
			codeTextView.setText(rs.getStringArray(R.array.CodeMH)[0]);
			styleTextView.setText(rs.getStringArray(R.array.StyleMH_HPS)[0]);
		} else if (buildingName.equalsIgnoreCase("HPS")) {
			codeTextView.setText(rs.getStringArray(R.array.CodeHPS)[0]);
			styleTextView.setText(rs.getStringArray(R.array.StyleMH_HPS)[0]);
		} else {
			codeTextView.setText(rs.getStringArray(R.array.CodeOther)[0]);
			styleTextView.setText(rs.getStringArray(R.array.StyleOther)[0]);
		}
		
		mountingTextView.setText(rs.getStringArray(R.array.Mounting)[0]);
		controlledTextView.setText(rs.getStringArray(R.array.Controlled)[0]);
		optionTextView.setText(rs.getStringArray(R.array.Options)[0]);
		heightTextView.setText(rs.getStringArray(R.array.Height)[0]);
		ballasttypeTextView.setText(rs.getStringArray(R.array.Ballast_type)[0]);
		ballastfactorTextView.setText(rs.getStringArray(R.array.Ballast_factor)[0]);
	}

	// Call Back method to get the Selection Name form SelectionActivity
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case 1:
			if (data == null)
				return;
			codeTextView.setText(data.getStringExtra("name"));

			break;
		case 2:
			if (data == null)
				return;
			styleTextView.setText(data.getStringExtra("name"));
			break;
		case 3:
			if (data == null)
				return;
			mountingTextView.setText(data.getStringExtra("name"));
			break;
		case 4:
			if (data == null)
				return;
			controlledTextView.setText(data.getStringExtra("name"));
			break;
		case 5:
			if (data == null)
				return;
			optionTextView.setText(data.getStringExtra("name"));
			break;
		case 6:
			if (data == null)
				return;
			heightTextView.setText(data.getStringExtra("name"));
			break;
		case 7:
			if (data == null)
				return;
			ballasttypeTextView.setText(data.getStringExtra("name"));
			break;
		case 8:
			if (data == null)
				return;
			ballastfactorTextView.setText(data.getStringExtra("name"));
			break;
		case PICK_FROM_CAMERA:
			if (resultCode != RESULT_OK)
				return;
			try {
				Log.d("Return Image path", mImageCaptureUri.getPath());
				finaleImagePath = mImageCaptureUri.getPath();

				thumbnail = ImageUtil.decodeFile(new File(finaleImagePath), 60,
						80);
        		Matrix matrix = new Matrix();
				matrix.postRotate(90);
				Bitmap resizedBitmap = Bitmap.createBitmap(thumbnail, 0, 0, thumbnail.getWidth(), thumbnail.getHeight(), matrix, true);
				cameraImageView.setImageBitmap(resizedBitmap);
				
				if (Constant.IS_RESIZE_ENABLE)
					ImageUtil.saveResizedImage(finaleImagePath, true);

			} catch (Exception e) {
				Log.e("Error: ", e.getMessage());
				e.printStackTrace();
			}
			break;
		case PICK_FROM_FILE:
			if (resultCode != RESULT_OK)
				return;

			mImageCaptureUri = data.getData();
			String[] filePathColumn = { MediaColumns.DATA };
			Cursor cursor = getContentResolver().query(mImageCaptureUri,
					filePathColumn, null, null, null);
			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			finaleImagePath = cursor.getString(columnIndex);
			cursor.close();
			thumbnail = ImageUtil.decodeFile(new File(finaleImagePath), 60, 80);
			cameraImageView.setImageBitmap(thumbnail);

			String destinationName;

			int no;

			if (Constant.ISFIXUPDATEMODE) {
				no = Integer.parseInt(fixInfo.fixtureId);
			} else {
				no = Integer.parseInt(Pref.getValue(
						Constant.PREF_LAST_FIXTURES_ID, "0")) + 1;
			}
			destinationName = dirFile.getAbsolutePath() + "/"
					+ Constant.FLOOR_NAME_PATH + "_" + Constant.AREA_NAME_PATH
					+ "_" + Constant.FIX_NAME_PATH + "_" + no + "_"
					+ System.currentTimeMillis() + ".jpg";

			File source = new File(finaleImagePath);
			File destination = new File(destinationName);

			try {
				if (source.exists()) {
					@SuppressWarnings("resource")
					FileChannel src = new FileInputStream(source).getChannel();
					@SuppressWarnings("resource")
					FileChannel dst = new FileOutputStream(destination)
							.getChannel();
					dst.transferFrom(src, 0, src.size());
					src.close();
					dst.close();
					Log.d("@@@@@@@@",
							"********** File Successfully copyed ***********");

					if (Constant.IS_RESIZE_ENABLE) {
						ImageUtil.saveResizedImage(destinationName, false);
						finaleImagePath = destinationName;
					}

				}
			} catch (Exception e) {
				// TODO: handle exception
				Log.e("Error: ", e.getMessage());
			}
			break;
		}
	}

	@Override
	public void onClick(View v) {
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(countEt.getWindowToken(), 0);
		
		int viewId = v.getId();
		switch (viewId) {
		case R.id.code_ll:
			startSelectionActivity("code", 1);
			 
			break;
		case R.id.style_ll:
			//startSelectionActivity("style", 2);
			Intent selectionIntent = new Intent(mContext, MultiSelectionActivity.class);
			selectionIntent.putExtra("type", "style");
			selectionIntent.putExtra("name", buildingName);
			startActivityForResult(selectionIntent, 2);
			break;
		case R.id.mounting_ll:
			//startSelectionActivity("mounting", 3);
			Intent selectionIntent1 = new Intent(mContext, MultiSelectionActivity.class);
			selectionIntent1.putExtra("type", "mounting");
			selectionIntent1.putExtra("name", buildingName);
			startActivityForResult(selectionIntent1, 3);
			break;
		case R.id.controlled_ll:
			startSelectionActivity("controlled", 4);
			break;
		case R.id.option_ll:
			
			startSelectionActivity("option", 5);
			break;
		case R.id.height_ll:
			startSelectionActivity("height", 6);
			break;
		case R.id.balasttypell:
			startSelectionActivity("Ballast_type", 7);
			break;
		case R.id.balastfactorll:
			startSelectionActivity("Ballast_factor", 8);
			break;
		case R.id.type_ll:
			showalertwithfixturevalue();
			
			break;
			
		}
	}
	
	
	public void showalertwithfixturevalue(){
		 // mSelectedItems = new ArrayList();
		 // Where we track the selected items
		
		strArray = getResources().getStringArray(R.array.Typefixures);
		//CharSequence[] items = strArray;
		typeadapter = new FixtureTypeAdapter(getApplicationContext(), strArray);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		//final ArrayList seletedItems=new ArrayList();
		// Set the dialog title
		Typeface font = Typeface.createFromAsset(getAssets(),
				"gothic.ttf");
		
		TextView dialogTitle = new TextView(this);
		dialogTitle.setText("Add Fixture");
		dialogTitle.setTextSize(26);
		dialogTitle.setTextColor(getResources().getColor(R.color.blue_top_bar));
		dialogTitle.setPadding(40, 40, 40, 40);
		dialogTitle.setTypeface(font);
		builder.setCustomTitle(dialogTitle);
		
		newFixtureItem = null;
		// Specify the list array, the items to be selected by default (null for none),
		// and the listener through which to receive callbacks when items are selected
	    builder.setAdapter(typeadapter, null)
	    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int position) {
            	   if(typeadapter.mSelected == -1) {
        			   if(otherOption.getText().toString().length() > 0) {
        				   newFixtureItem = Character.toUpperCase(otherOption
   								.getText().toString().charAt(0)) + otherOption
   								.getText().toString().substring(1);
        				   Constant.FIX_NAME_PATH = ImageUtil.getValidName(newFixtureItem);
                		   //String fan=strArray[position];
       	   				   if(buildingName==null)
       	   					   Constant.ISFIXUPDATEMODE = true;
       	   				   buildingName = newFixtureItem;
       	   				   fixturetypeTextview.setText(newFixtureItem);
       	   				   setValueToUI();
        			   }
        			   else {
        				   dialog.dismiss();
        			   }
        		   }
        		   else {
        			   Constant.FIX_NAME_PATH = ImageUtil.getValidName(newFixtureItem);
            		   //String fan=strArray[position];
   	   				   if(buildingName==null)
   	   					   Constant.ISFIXUPDATEMODE = true;
   	   				   buildingName = newFixtureItem;
   	   				   fixturetypeTextview.setText(newFixtureItem);
   	   				   setValueToUI();
        		   }
            	}
           })    
       
       .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialog, int id) {
        	   dialog.dismiss();
           }
       });

	    final AlertDialog dialog = builder.create();

	    otherOption = new EditText(this);
	    otherOption.setHint("Other");
	    otherOption.setTextColor(this.getResources().getColor(R.color.gray));
	    otherOption.setTextSize(26);
	    otherOption.setTypeface(font);
	    otherOption.setSingleLine(true);
	    otherOption.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
	    
	    otherOption.clearFocus();
	    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(otherOption.getWindowToken(), 0);
	    otherOption.setOnFocusChangeListener(this);
	    
	    final float scale = getResources().getDisplayMetrics().density;
	    int padding_60dp = (int) (60 * scale + 0.5f);
	    int padding_10dp = (int) (10 * scale + 0.5f);
	    
	    otherOption.setPadding(padding_60dp, 0, 10, padding_10dp);
	    otherOption.setHeight(padding_60dp);
	    
	    dialog.getListView().addFooterView(otherOption);
		
	    dialog.getListView().setDivider(null);
	    dialog.getListView().setDividerHeight(0);
	    dialog.getListView().setOnItemClickListener(this);
	    dialog.show();
	    
	    dialog.getWindow().clearFlags( WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM); 
	    dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
   }
	 
	

	 
	

	/**
	 * This method used to display Image Selection dialog.
	 * 
	 */
	public void pickSelector() {
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
							String fileName;
							int no;
							if (Constant.ISFIXUPDATEMODE) {
								no = Integer.parseInt(fixInfo.fixtureId);
							} else {
								no = Integer.parseInt(Pref.getValue(
										Constant.PREF_LAST_FIXTURES_ID, "0")) + 1;
							}

							fileName = dirFile.getAbsolutePath() + "/"
									+ Constant.FLOOR_NAME_PATH + "_"
									+ Constant.AREA_NAME_PATH + "_"
									+ Constant.FIX_NAME_PATH + "_" + no + "_"
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

	/**
	 * This method used to Insert and Update Fixture data in Database
	 * 
	 */
	@SuppressWarnings("static-access")
	public void addNewFixtures() {

		ContentValues value = new ContentValues();
		NewFixture newFixture = new NewFixture();
		
		value.put(DatabaseHandler.KEY_AID, Constant.AREA_ID);
		newFixture.area_id = Constant.AREA_ID;
		newFixture.survey_id = Constant.SURVEY_ID;
		newFixture.floor_id = Constant.FLOOR_ID;
		
		value.put(DatabaseHandler.KEY_FIXTURE_NAME, buildingName);
		newFixture.type = buildingName;
		
		value.put(DatabaseHandler.KEY_FIX_COUNT, countEt.getText().toString());
		newFixture.count = countEt.getText().toString();
		
		value.put(DatabaseHandler.KEY_CODE, codeTextView.getText().toString());
		newFixture.code = codeTextView.getText().toString();
		
		value.put(DatabaseHandler.KEY_STYLE, styleTextView.getText().toString());
		newFixture.style = styleTextView.getText().toString();
		
		value.put(DatabaseHandler.KEY_MOUNTING, mountingTextView.getText()
				.toString());
		newFixture.mounting = mountingTextView.getText()
				.toString();
		
		value.put(DatabaseHandler.KEY_CONTROLLED, controlledTextView.getText()
				.toString());
		newFixture.controlled = controlledTextView.getText()
				.toString();
		
		value.put(DatabaseHandler.KEY_OPTINON, optionTextView.getText()
				.toString());
		newFixture.option = optionTextView.getText()
				.toString();
		
		value.put(DatabaseHandler.KEY_HEIGHT, heightTextView.getText()
				.toString());
		newFixture.height = heightTextView.getText()
				.toString(); 
		
		value.put(DatabaseHandler.KEY_FVALUE, firstEt.getText().toString());
		newFixture.hrs_per_day = firstEt.getText().toString();
		
		value.put(DatabaseHandler.KEY_SVALUE, secondEt.getText().toString());
		newFixture.days_per_week = secondEt.getText().toString();
		
		value.put(DatabaseHandler.KEY_ANSWER, answerTextView.getText().toString());
		
		value.put(DatabaseHandler.KEY_BALLAST_TYPE, ballasttypeTextView.getText().toString());
		newFixture.ballast = ballasttypeTextView.getText().toString();
		
		value.put(DatabaseHandler.KEY_BALLAST_FACTOR, ballastfactorTextView.getText().toString());
		newFixture.factor = ballastfactorTextView.getText().toString();
		
		value.put(DatabaseHandler.KEY_BULBSVALUE, bulbEt.getText().toString());
		newFixture.bulbs_per_fixture = bulbEt.getText().toString();
		
		value.put(DatabaseHandler.KEY_WATTSVALUE, wattsEt.getText().toString());
		newFixture.watts_per_bulb = wattsEt.getText().toString();
		
		value.put(DatabaseHandler.KEY_ANSWERBULBS, bulbAnswertextview.getText().toString());
		
		value.put(DatabaseHandler.KEY_NOTE, et_Note.getText().toString());
		newFixture.notes = et_Note.getText().toString();
		
		if (finaleImagePath != null){
			value.put(DatabaseHandler.KEY_PATH, finaleImagePath);
			DropboxHelper.SaveToDropbox(finaleImagePath);
			
			byte[] bytes;
			byte[] buffer = new byte[8192];
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			int bytesRead;
			try {
				@SuppressWarnings("resource")
				InputStream inputStream = new FileInputStream(finaleImagePath);//You can get an inputStream using any IO API
				while ((bytesRead = inputStream.read(buffer)) != -1) {
			    output.write(buffer, 0, bytesRead);
			}
			} catch (IOException e) {
				e.printStackTrace();
			}
			bytes = output.toByteArray();
			newFixture.fixture_images_b64 = Base64.encodeToString(bytes, Base64.DEFAULT);
		}
		
		int rowid;
		if (Constant.ISFIXUPDATEMODE) {
			rowid = dbHandler.updateFixtures(value, fixInfo.fixtureId);
		} 
		else {
			rowid = (int) dbHandler.addNewFixtures(value);
			Pref.setValue(Constant.PREF_LAST_FIXTURES_ID, "" + rowid);
		}
		
		if (rowid > 0) {
			Toast.makeText(mContext, "Record saved successfully",
					Toast.LENGTH_SHORT).show();
		}
		
		if(NetworkUtil.getConnectivityStatus(mContext) == 1 || NetworkUtil.getConnectivityStatus(mContext) == 2 ) {
			progressLayout.setVisibility(View.VISIBLE);
			AddFixtureTask fixtureTask = new AddFixtureTask(newFixture);
			fixtureTask.execute();
		}
		else {
			Toast.makeText(mContext, NetworkUtil.getConnectivityStatusString(mContext), Toast.LENGTH_LONG).show();
		}
		
		
	}
	
	public class AddFixtureTask extends AsyncTask<Void, Void, String> {

        final NewFixture mFixture;

        AddFixtureTask(NewFixture newFixture) {
        	mFixture = newFixture;
        }

        @Override
        protected String doInBackground(Void... params) {
            return NetworkUtil.addFixture(mFixture);
        }

		@Override
        protected void onPostExecute(String result) {
			progressLayout.setVisibility(View.GONE);
	        if (result == null) {
	        	Toast.makeText(Constant.CONTEXT, "Server Error", Toast.LENGTH_LONG).show();
	        }
	        else {
	        	JSONObject reader;
				try {
					reader = new JSONObject(result);
					String status = reader.getString("status");
			        if (status.equals("success")) {
			        	finish();
						activityCloseTransition();
			        }
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }
			
	    }

        @Override
        protected void onCancelled() {

        }
    }
	
	
	/**
	 *  This method used to set Data to Layout control when Activity open in Edit Mode. 
	 */
	public void setDataToUI() {
		buildingName = fixInfo.fixtureName;
		fixturetypeTextview.setText(fixInfo.fixtureName);
		codeTextView.setText(fixInfo.code);
		styleTextView.setText(fixInfo.style);
		mountingTextView.setText(fixInfo.mounting);
		controlledTextView.setText(fixInfo.controlled);
		optionTextView.setText(fixInfo.option);
		heightTextView.setText(fixInfo.height);
		ballasttypeTextView.setText(fixInfo.ballastType);
		ballastfactorTextView.setText(fixInfo.ballastFactor);
		answerTextView.setText(fixInfo.answer);
		firstEt.setText(fixInfo.firstValue);
		secondEt.setText(fixInfo.secondValue);
		bulbEt.setText(fixInfo.bulbValue);
		wattsEt.setText(fixInfo.wattsValue);
		bulbAnswertextview.setText(fixInfo.bulbAnswer);
		
		// countTextView.setText(fixInfo.fixtureCount);
		countEt.setText(fixInfo.fixtureCount);
		if (fixInfo.note != null && !fixInfo.note.equals("")) {
			et_Note.setText(fixInfo.note);
			et_Note.setVisibility(View.VISIBLE);
		}
		if (fixInfo.imagePath != null && !fixInfo.imagePath.equals("")) {
			thumbnail = ImageUtil.decodeFile(new File(fixInfo.imagePath), 70,
					70);
			cameraImageView.setImageBitmap(thumbnail);
		}
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
	private void hideKeyboard() {    
	    // Check if no view has focus: 
	    View view = this.getCurrentFocus();
	    if (view != null) {
	        InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
	        inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	    } 
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
		// TODO Auto-generated method stub
		newFixtureItem = strArray[position];
		typeadapter.mSelected = position;
		typeadapter.notifyDataSetChanged();
		otherOption.clearFocus();
		otherOption.clearComposingText();
		
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(otherOption.getWindowToken(), 0);
	}

	@Override
	public void onFocusChange(View view, boolean focus) {
		// TODO Auto-generated method stub
		if(view == otherOption && view.isFocused()) {
			typeadapter.mSelected = -1;
			typeadapter.notifyDataSetChanged();
		}
	}
}

