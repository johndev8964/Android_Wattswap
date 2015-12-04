package com.citrusbug.wattswap.activity;


import org.json.JSONException;
import org.json.JSONObject;

import com.citrusbug.wattswap.R;
import com.citrusbug.wattswap.util.Constant;
import com.citrusbug.wattswap.util.DatabaseHandler;
import com.citrusbug.wattswap.util.NetworkUtil;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AddNewAreaActivity extends Activity {
	
	
	Context mContext; 
	DatabaseHandler dbHandler;
	String areaid;
	String locationName;
	
	// Text View object
	TextView backTextView;
	TextView saveTextView;
	TextView locationNameTextView;
	
	EditText editText_LocationName;
	ContentValues values;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	    // this method calling for hide Title from title bar.
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		//set layout to activity 
		setContentView(R.layout.activity_addnewfloor);
		mContext = this;
		//dbHandler object initialized to perform database operation
		dbHandler = new DatabaseHandler(mContext);
		locationNameTextView = (TextView) findViewById(R.id.lbl_floor_discription);
		locationNameTextView.setText(getResources().getString(
				R.string.lbl_location_name));

		backTextView = (TextView) findViewById(R.id.backTextView_AddFloor);
		saveTextView = (TextView) findViewById(R.id.saveTextView_AddFloor);
		editText_LocationName = (EditText) findViewById(R.id.floorDescriptionTextView_AddFloor);

		// This block is called for user want to edit Area name
		if (Constant.ISAREAUPDATEMODE) {
			areaid = getIntent().getStringExtra("areaid");
			locationName = getIntent().getStringExtra("name");
			editText_LocationName.setText(locationName);
		}

		backTextView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				activityCloseTransition();
				return false;
			}
		});

		saveTextView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (editText_LocationName.getText().length() > 0) {
					
					if (Constant.ISAREAUPDATEMODE) {
						if(NetworkUtil.getConnectivityStatus(mContext) == 1 || NetworkUtil.getConnectivityStatus(mContext) == 2 ) {
							EditAreaTask editAreaTask = new EditAreaTask(areaid, editText_LocationName
								.getText().toString());
							editAreaTask.execute();
						}
						else {
							Toast.makeText(mContext, NetworkUtil.getConnectivityStatusString(mContext), Toast.LENGTH_LONG);
						}
						
					} else {
						if(NetworkUtil.getConnectivityStatus(mContext) == 1 || NetworkUtil.getConnectivityStatus(mContext) == 2 ) {
							AddAreaTask addAreaTask = new AddAreaTask(editText_LocationName
								.getText().toString());
							addAreaTask.execute();
						}
						else {
							Toast.makeText(mContext, NetworkUtil.getConnectivityStatusString(mContext), Toast.LENGTH_LONG);
						}
						
					}
				} else {
					Toast.makeText(mContext,
							"Please enter location name value",
							Toast.LENGTH_SHORT).show();
				}
				return false;
			}
		});
	}

	public class AddAreaTask extends AsyncTask<Void, Void, String> {

        final String mArea_name;

        AddAreaTask(String area_name) {
        	mArea_name = area_name;
        }

        @Override
        protected String doInBackground(Void... params) {
            return NetworkUtil.addArea(Constant.SURVEY_ID, Constant.FLOOR_ID, mArea_name);
        }

		@Override
        protected void onPostExecute(String result) {
			JSONObject reader;
			try {
				reader = new JSONObject(result);
				String status = reader.getString("status");
		        if (status.equals("success")) {
		        	Constant.AREA_ID = reader.getJSONObject("data").getString("area_id");
		        	values.put(DatabaseHandler.KEY_FID, Constant.FLOOR_ID);
		        	values.put(DatabaseHandler.KEY_AID, Constant.AREA_ID);
					int rowId = (int) dbHandler.addArea(values);
					if (rowId > 0) {
						activityCloseTransition();
					} else {
						Toast.makeText(mContext,
								"Error in inserting Area data",
								Toast.LENGTH_SHORT).show();
					}
		        }
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }

        @Override
        protected void onCancelled() {

        }
    }
	
	public class EditAreaTask extends AsyncTask<Void, Void, String> {

        final String mArea_name;
        final String mArea_id;

        EditAreaTask(String area_id, String area_name) {
        	mArea_name = area_name;
        	mArea_id = area_id;
        }

        @Override
        protected String doInBackground(Void... params) {
            return NetworkUtil.editArea(mArea_id, mArea_name);
        }

		@Override
        protected void onPostExecute(String result) {
			JSONObject reader;
			try {
				reader = new JSONObject(result);
				String status = reader.getString("status");
		        if (status.equals("success")) {
		        	Constant.AREA_ID = reader.getJSONObject("data").getString("area_id");
		        	int rowId = 0;
					values = new ContentValues();
					values.put(DatabaseHandler.KEY_LNAME, editText_LocationName
							.getText().toString());
					rowId = dbHandler.updateArea(values, Constant.AREA_ID);
					if (rowId > 0) {
						activityCloseTransition();
					} else {
						Toast.makeText(mContext,
								"Error in inserting Area data",
								Toast.LENGTH_SHORT).show();
					}
		        }
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }

        @Override
        protected void onCancelled() {

        }
    }
	
	public void activityCloseTransition() {
		finish();
		Constant.ISAREAUPDATEMODE = false;
		overridePendingTransition(R.animator.in_from_left,
				R.animator.out_to_right);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Constant.ISAREAUPDATEMODE = false;
	}
}