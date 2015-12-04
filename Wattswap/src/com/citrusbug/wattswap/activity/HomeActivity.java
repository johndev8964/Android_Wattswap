package com.citrusbug.wattswap.activity;

import java.io.File;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.citrusbug.wattswap.R;
import com.citrusbug.wattswap.adapter.SurveyAdapter;
import com.citrusbug.wattswap.adapter.SurveyClosedAdapter;
import com.citrusbug.wattswap.adapter.FloorListAdapter.CopyDetector;
import com.citrusbug.wattswap.adapter.SurveyClosedAdapter.ViewHolder;
import com.citrusbug.wattswap.bean.Survey;
import com.citrusbug.wattswap.util.Constant;
import com.citrusbug.wattswap.util.DatabaseHandler;
import com.citrusbug.wattswap.util.DropboxHelper;
import com.citrusbug.wattswap.util.ImageUtil;
import com.citrusbug.wattswap.util.SwipeDetector;
import com.dropbox.sync.android.DbxAccountManager;

///
@SuppressLint("ResourceAsColor")
public class HomeActivity extends Activity implements OnTouchListener,
		CopyDetector {

	// Layout control object
	// private MainLayout mLayout;
	RelativeLayout leftRL;
	RelativeLayout rightRL;
	DrawerLayout drawerLayout;

	private ImageButton btMenu;

	private TextView addNewSurveyTextView_Dashborad;
	private TextView addNewSurveyTextView_Home;
	private TextView openTextView;
	private TextView closeTextView;
	private TextView todayScheduledTextView;
	private ImageView signindropbox;
	private ListView surveyListView;
	private List<Survey> surveyList;

	// DatabaseHandler Object for database accesss
	DatabaseHandler dbHandler;
	// SurveyAdapter Object for inflate SurveyListView
	SurveyAdapter surveyAdapter;
	SurveyClosedAdapter surveyClosedAdapter;
	SwipeDetector swipeDetector;
	// It allows access to application-specific resources and classes
	private Context mContetx;

	String status = "1";

	/*
	 * (non-Javadoc)

	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Set the user interface layout for this Activity
		// The layout file is defined in the project
		// res/layout/activity_home.xml file
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_home);
		// mLayout = (MainLayout) this.getLayoutInflater().inflate(
		// R.layout.activity_home, null);
		//
		// this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// setContentView(mLayout);
		FontChangeCrawler fontChanger = new FontChangeCrawler(getAssets(), "gothic.ttf");
	    fontChanger.replaceFonts ((ViewGroup) this.findViewById(android.R.id.content));
	    
		leftRL = (RelativeLayout) findViewById(R.id.whatYouWantInLeftDrawer);
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mContetx = this;
		dbHandler = new DatabaseHandler(mContetx);
		getUIControlId();

		 DropboxHelper.mDbxAcctMgr =
		 DbxAccountManager.getInstance(getApplicationContext(),
		 DropboxHelper.DROPBOX_API_KEY, DropboxHelper.DROPBOX_API_SECRET);
		 if(DropboxHelper.mDbxAcctMgr.hasLinkedAccount()){
			 signindropbox.setEnabled(true);
			 signindropbox.setImageResource(R.drawable.icon_dropboxsyncenabled);
		 }
		 else{
			 signindropbox.setEnabled(true);
			 signindropbox.setImageResource(R.drawable.icon_dropboxsignin);
		 }

		// btMenu.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// // Show/hide the menu
		// toggleMenu(v);
		//
		// }
		// });

		Log.d("Sd Card path: ", Constant.SD_CARD_PATH);

		File sdCardFile = new File(Constant.SD_CARD_PATH);

		if (!sdCardFile.exists()) {
			boolean isCreated = sdCardFile.mkdir();
			Log.d("Sd Card Directory created ", "" + isCreated);
		}

		swipeDetector = new SwipeDetector() {
			@Override
			public void onActionUp() {
				// TODO Auto-generated method stub
				super.onActionUp();
				if (status == "0") {
					if (swipeDetector.getAction() == SwipeDetector.Action.LR) {
						for (int position = 0; position < surveyClosedAdapter
								.getCount(); position++) {
							Survey floorItem = (Survey) surveyClosedAdapter
									.getItem(position);
							floorItem.isVisible = false;
						}
						surveyClosedAdapter.notifyDataSetChanged();
					}
				}
			}
		};
		surveyListView.setOnTouchListener(swipeDetector);
	}

	// public void onOpenLeftDrawer(View view)
	// {
	// drawerLayout.openDrawer(leftRL);
	// }

	public void toggleMenu(View v) {
		drawerLayout.openDrawer(leftRL);
		// mLayout.toggleMenu();

	}

	//
	// @Override
	// public void onBackPressed() {
	// if (mLayout.isMenuShown()) {
	// mLayout.toggleMenu();
	// } else {
	// super.onBackPressed();
	// }
	// }

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
		case R.id.add_New_Survey_TextView_HomeDashboard:
			startNewSurveyActivity();
			break;
		case R.id.add_New_Survey_TextView_Home:
			startNewSurveyActivity();
			break;
		case R.id.open_TextView_Home:
			openTextView.setTextColor(getResources()
					.getColor(R.color.openstate));
			closeTextView.setTextColor(getResources().getColor(
					R.color.closestate));
			status = "1";
			displaySurveyList(status);
			break;
		case R.id.close_TextView_Home:
			openTextView.setTextColor(getResources().getColor(
					R.color.closestate));
			closeTextView.setTextColor(getResources().getColor(
					R.color.openstate));
			status = "0";
			displaySurveyList(status);
			break;
		}
		return false;
	}

	/**
	 * This method used to get Control id of Layout(User Interface)
	 */
	public void getUIControlId() {

		// get User Interface Control Id
		btMenu = (ImageButton) findViewById(R.id.button_menu);
		addNewSurveyTextView_Dashborad = (TextView) findViewById(R.id.add_New_Survey_TextView_HomeDashboard);
		addNewSurveyTextView_Home = (TextView) findViewById(R.id.add_New_Survey_TextView_Home);
		openTextView = (TextView) findViewById(R.id.open_TextView_Home);
		closeTextView = (TextView) findViewById(R.id.close_TextView_Home);
		surveyListView = (ListView) findViewById(R.id.survey_listview_Home);
		todayScheduledTextView = (TextView) findViewById(R.id.scheduled_Value_TextView_HomeDashboard);
		signindropbox = (ImageView) findViewById(R.id.sign_in_dropbox_button);
		// set OnTouch Listener to UI control Touch event
		addNewSurveyTextView_Dashborad.setOnTouchListener(this);
		addNewSurveyTextView_Home.setOnTouchListener(this);
		openTextView.setOnTouchListener(this);
		closeTextView.setOnTouchListener(this);

		signindropbox.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				DropboxHelper.mDbxAcctMgr = DbxAccountManager.getInstance(
						getApplicationContext(), DropboxHelper.DROPBOX_API_KEY,
						DropboxHelper.DROPBOX_API_SECRET);
				if (!DropboxHelper.mDbxAcctMgr.hasLinkedAccount()) {
					DropboxHelper.mDbxAcctMgr.startLink(
							(Activity) v.getContext(),
							DropboxHelper.DROPBOX_LINK_REQUEST_CODE);
				} else {
					DropboxHelper.mDbxAcctMgr.unlink();
					signindropbox
							.setImageResource(R.drawable.icon_dropboxsignin);
				}

			}
		});
		surveyListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				if (status.equals("0") && swipeDetector.swipeDetected()) {
					ViewHolder holder = (ViewHolder) arg1.getTag();
					Survey survey = (Survey) surveyClosedAdapter
							.getItem(position);
					if (swipeDetector.getAction() == SwipeDetector.Action.LR) {
						if (holder.viewFlipper.getDisplayedChild() != 0) {
							holder.viewFlipper.showPrevious();
							survey.isVisible = false;
						}
					}
					if (swipeDetector.getAction() == SwipeDetector.Action.RL) {
						if (holder.viewFlipper.getDisplayedChild() == 0) {
							holder.viewFlipper.showNext();
							survey.isVisible = true;
						}
					}
				} else {
					if (surveyList.get(position).surveyName.length() > 15) {
						String name = surveyList.get(position).surveyName
								.substring(0, 15) + "...";
						Constant.SURVEY_NAME = name;
					} else {
						Constant.SURVEY_NAME = surveyList.get(position).surveyName;
					}

					Constant.SURVEY_NAME_PATH = ImageUtil
							.getValidName(surveyList.get(position).surveyName);
					Intent detailViewIntent = new Intent(
							getApplicationContext(), SurveyDetailActivity.class);
					detailViewIntent.putExtra("sid",
							surveyList.get(position).surveyId);
					startActivity(detailViewIntent);
					activityTransition();

				}
			}
		});
	}

	/**
	 * This method start AddNewSurveyActivity for creating new Survey
	 */
	public void startNewSurveyActivity() {
		Constant.ISUPDATEMODE = false;
		Intent newSurveyIntent = new Intent(getApplicationContext(),
				AddNewSurveyActivity.class);
		startActivity(newSurveyIntent);
		activityTransition();
	}

	/**
	 * This method display activity open transition from right to left
	 */
	public void activityTransition() {
		overridePendingTransition(R.animator.in_from_right,
				R.animator.out_to_left);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
		int todayscheduled = dbHandler.getTodayScheduled();
		Log.d("No of Today Scheduled: ", "" + todayscheduled);
		todayScheduledTextView.setText("" + todayscheduled);
		displaySurveyList(status);
	}

	/**
	 * This method used to fill listview by list of Survey
	 * 
	 * @param status
	 *            should be 1 for open survey and 0 for close survey
	 * 
	 */
	public void displaySurveyList(String status) {
		surveyList = dbHandler.getSurveyList(status);
		Log.d("Survey List not null", surveyList.size() + "");
		if (status == "1") {
			surveyAdapter = new SurveyAdapter(mContetx, surveyList);
			surveyListView.setAdapter(surveyAdapter);
		} else {
			surveyClosedAdapter = new SurveyClosedAdapter(mContetx, surveyList,
					this);
			surveyListView.setAdapter(surveyClosedAdapter);
		}
	}

	@Override
	public void onCopyFloor() {
		// TODO Auto-generated method stub
		onResume();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == DropboxHelper.DROPBOX_LINK_REQUEST_CODE) {
			if (resultCode == Activity.RESULT_OK) {

				signindropbox
						.setImageResource(R.drawable.icon_dropboxsyncenabled);
			} else {

				signindropbox.setImageResource(R.drawable.icon_dropboxsignin);
			}
		} else {
			super.onActivityResult(requestCode, resultCode, data);
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
}