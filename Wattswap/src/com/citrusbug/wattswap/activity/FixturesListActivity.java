package com.citrusbug.wattswap.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.citrusbug.wattswap.R;
import com.citrusbug.wattswap.adapter.FixtureTypeAdapter;
import com.citrusbug.wattswap.adapter.FixturesListAdapter;
import com.citrusbug.wattswap.adapter.FixturesListAdapter.DeleteFixtureDetector;
import com.citrusbug.wattswap.adapter.FixturesListAdapter.ViewHolder;
import com.citrusbug.wattswap.bean.Fixtures;
import com.citrusbug.wattswap.util.Constant;
import com.citrusbug.wattswap.util.DatabaseHandler;
import com.citrusbug.wattswap.util.ImageUtil;
import com.citrusbug.wattswap.util.Pref;
import com.citrusbug.wattswap.util.SwipeDetector;

public class FixturesListActivity extends Activity implements OnItemClickListener, OnFocusChangeListener, DeleteFixtureDetector {

	TextView homeTextView;
	ImageView addNewFixureTextView;
	TextView titleTextView;
	TextView finishTextView;
	TextView dottextview;
	TextView floorTextView;
	TextView areaTextView;
	TextView fixturetextview;
	ListView fixuresListView;
	
	EditText otherOption;
	
	Context mContext;
	DatabaseHandler dbHandler;
	List<Fixtures> fixureList;
	FixturesListAdapter adapter;
	
	String floorName;
	String areaName;

	SwipeDetector swipeDetector;
	ImageView edAdd;
	TextView addnewfixtureHint;
	
	Button btn_add;
	
	String[] strArray;
	
	public static ArrayList<String> imagePaths;
	FixtureTypeAdapter typeadapter;
	
	private ViewHolder prefHolder;
	private Fixtures prefFixture;
	private String newFixtureItem;
	private Boolean isDialogOpen = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_fixtureslist);
		mContext = this;

		floorName = getIntent().getStringExtra("floorname");
		areaName = getIntent().getStringExtra("areaname");

		dbHandler = new DatabaseHandler(mContext);
		swipeDetector = new SwipeDetector() {
			@Override
			public void onActionUp() {
				// TODO Auto-generated method stub
				
				super.onActionUp();
				if (swipeDetector.getAction() == SwipeDetector.Action.LR) {
					for (int position = 0; position < adapter.getCount(); position++) {
						Fixtures fixtureItem = (Fixtures) adapter
								.getItem(position);
						fixtureItem.isVisible = false;
					}
					adapter.notifyDataSetChanged();
				}
			}
		};
		
		Typeface face = Typeface.createFromAsset(getAssets(), "gothic.ttf");
		floorTextView = (TextView) findViewById(R.id.floor_TextView_FixtureList);
		floorTextView.setTypeface(face);
		areaTextView = (TextView) findViewById(R.id.area_TextView_FixtureList);
		areaTextView.setTypeface(face);
		fixturetextview= (TextView) findViewById(R.id.fixtures_TextView_FixtureList);
		fixturetextview.setTypeface(face);
		floorTextView.setText(floorName);
		areaTextView.setText(areaName);

		floorTextView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//Constant.ISFINISH=true;
				AreaListActivity.goFloor = true;
				finish();
				activityCloseTransition();
			
			}
		});
		
		areaTextView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				activityCloseTransition();
				finish();
			}
		});
		
		Typeface font = Typeface.createFromAsset( getAssets(), "fontawesome-webfont.ttf" );
		fixuresListView = (ListView) findViewById(R.id.fixturesListview);

		homeTextView = (TextView) findViewById(R.id.home_TextView_FixturesList);
		homeTextView.setTypeface(font);
	
		addNewFixureTextView = (ImageView) findViewById(R.id.plus);

		
//		dottextview= (TextView) findViewById(R.id.dot_TextView_FloorList);
//		dottextview.setTypeface(font);
		
		titleTextView = (TextView) findViewById(R.id.title_TextView_FixtureList);
		titleTextView.setTypeface(face);
		//finishTextView = (TextView) findViewById(R.id.finish_fixtures);

		titleTextView.setText(Constant.SURVEY_NAME);

		homeTextView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				Intent intenthome = new Intent(mContext,
						HomeActivity.class);
				startActivity(intenthome);
				
				activityCloseTransition();
				return false;
			}
		});
		
		addNewFixureTextView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
//				Intent intentAddNewArea = new Intent(mContext,AddFixturesActivity.class);
//				startActivity(intentAddNewArea);
//				activityOpenTransition();
				showalertwithfixturevalue();
				return false;
				
			}
		});

//		finishTextView.setOnTouchListener(new OnTouchListener() {
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				finish();
//				Constant.ISFINISH = true;
//				return false;
//			}
//		});

		fixuresListView.setOnTouchListener(swipeDetector);

		fixuresListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View arg1,
					int position, long arg3) {

				ViewHolder holder = (ViewHolder) arg1.getTag();
				Fixtures fixItem = (Fixtures) adapter
						.getItemAtPosition(position);

				if (swipeDetector.swipeDetected()) {
					if (swipeDetector.getAction() == SwipeDetector.Action.LR) {
						if (holder.viewFlipper.getDisplayedChild() != 0) {
							holder.viewFlipper.showPrevious();
							fixItem.isVisible = false;
						}
					}
					if (swipeDetector.getAction() == SwipeDetector.Action.RL) {
						if (holder.viewFlipper.getDisplayedChild() == 0) {
							if(prefHolder != null && prefHolder != holder && prefFixture.isVisible == true) {
								prefHolder.viewFlipper.showPrevious();
								prefFixture.isVisible = false;
							}
							holder.viewFlipper.showNext();
							fixItem.isVisible = true;
							prefHolder = holder;
							prefFixture = fixItem;
						}
					}
				} else {
					try{
						hideKeyboard();
						Constant.ISFIXUPDATEMODE = true;
						Constant.FLOOR_NAME_PATH = ImageUtil
								.getValidName(fixureList.get(position).fixtureName);
					
						Intent fixIntent = new Intent(getApplicationContext(),
								EditFixuresActivity.class);
						fixIntent.putExtra("fixures", fixureList.get(position));
						fixIntent.putExtra("floorname", floorName);
						fixIntent.putExtra("areaname", areaName);
						startActivity(fixIntent);
						activityOpenTransition();
				}
				catch (IndexOutOfBoundsException e)
				{
					System.out.println(e);}  
				}
			}
		});
		
		//add footer to listview

		View footer=getLayoutInflater().inflate(R.layout.addnewfixturefooter, null);
		edAdd = (ImageView)footer.findViewById(R.id.txt_new);
		//edAdd.setTypeface(font);
		footer.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showalertwithfixturevalue();
			}
		});
		addnewfixtureHint=(TextView)footer.findViewById(R.id.hh);
		addnewfixtureHint.setHint("add fixture");
		addnewfixtureHint.setTypeface(face);
		edAdd.setOnClickListener(new View.OnClickListener() {
			
		@Override
		public void onClick(View v) {
				// TODO Auto-generated method stub
//				Intent intentAddNewArea = new Intent(mContext,AddFixturesActivity.class);
//				startActivity(intentAddNewArea);
//				activityOpenTransition();
				showalertwithfixturevalue();
			}
		});
		 
		
		fixuresListView.addFooterView(footer);
		fixureList = dbHandler.getFixturesList(Constant.AREA_ID);
		adapter = new FixturesListAdapter(mContext, fixureList, this);
		fixuresListView.setAdapter(adapter);
		
		imagePaths = new ArrayList<String>();
		
		for(int i = 0;i < fixureList.size();i ++) {
			Fixtures fixtureItem = fixureList.get(i);
			if(fixtureItem.imagePath != null) {
				imagePaths.add(fixtureItem.imagePath);
			}
		}
		
		//Save and Load fixtureType array.
		
		/*if(Pref.getValue("install", null) == null) {
			Pref.setValue("install", "install");
			String[] fixtureTypeArray = getResources().getStringArray(R.array.Typefixures);
			Pref.saveFixtureTypeArray(fixtureTypeArray, "fixtureType", mContext);
		}*/
	}

	@Override
	protected void onResume() {
		super.onResume();
		fixureList = dbHandler.getFixturesList(Constant.AREA_ID);
		adapter = new FixturesListAdapter(mContext, fixureList, this);
		fixuresListView.setAdapter(adapter);
		
		if( !isDialogOpen ) {
			showalertwithfixturevalue();
		}
		
	}

	public void activityCloseTransition() {
		finish();
		overridePendingTransition(R.animator.in_from_left,
				R.animator.out_to_right);
	}

	public void activityOpenTransition() {
		overridePendingTransition(R.animator.in_from_right,
				R.animator.out_to_left);
	}
	public void showalertwithfixturevalue() {
		 // mSelectedItems = new ArrayList();
		 // Where we track the selected items
		isDialogOpen = true;
		
		strArray = getResources().getStringArray(R.array.Typefixures);
		//strArray = Pref.loadFixtureTypeArray("fixtureType", mContext);
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
        				   Constant.ISFIXUPDATEMODE = false;
                    	   Constant.FIX_NAME_PATH = ImageUtil.getValidName(newFixtureItem);
        	   			   Intent intentEditFixures = new Intent(getApplicationContext(), EditFixuresActivity.class);
        	   			   intentEditFixures.putExtra("type", newFixtureItem);
        	   			   intentEditFixures.putExtra("floorname", floorName);
        	   			   intentEditFixures.putExtra("areaname", areaName);
        				   startActivity(intentEditFixures);
        	   			   //finish();
        				   overridePendingTransition(R.animator.in_from_right,
        	   						R.animator.out_to_left);
        				  
        			   }
        			   else {
        				   dialog.dismiss();
        			   }
        		   }
        		   else {
        			   Constant.ISFIXUPDATEMODE = false;
                	   Constant.FIX_NAME_PATH = ImageUtil.getValidName(newFixtureItem);
    	   			   Intent intentEditFixures = new Intent(getApplicationContext(), EditFixuresActivity.class);
    	   			   intentEditFixures.putExtra("type", newFixtureItem);
    	   			   intentEditFixures.putExtra("floorname", floorName);
    	   			   intentEditFixures.putExtra("areaname", areaName);
    				   startActivity(intentEditFixures);
    	   			   //finish();
    				   overridePendingTransition(R.animator.in_from_right,
    	   						R.animator.out_to_left);
    				   
        		   }
            	   isDialogOpen = false;
               }
           })    
       
       .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialog, int id) {
        	   dialog.dismiss();
        	   isDialogOpen = false;
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
		if(view.isFocused()) {
			typeadapter.mSelected = -1;
			typeadapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onDeleteFixture() {
		// TODO Auto-generated method stub
		onResume();
	}
	
}
