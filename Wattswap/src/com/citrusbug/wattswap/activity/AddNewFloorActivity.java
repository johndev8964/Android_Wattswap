package com.citrusbug.wattswap.activity;

import org.json.JSONException;
import org.json.JSONObject;

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

import com.citrusbug.wattswap.R;
import com.citrusbug.wattswap.util.Constant;
import com.citrusbug.wattswap.util.DatabaseHandler;
import com.citrusbug.wattswap.util.NetworkUtil;
import com.citrusbug.wattswap.util.NetworkUtil.NewSurvey;

public class AddNewFloorActivity extends Activity {
	TextView backTextView;
	TextView saveTextView;
	EditText editText_FloorDescription;
	Context mContext;
	DatabaseHandler dbHandler;
	String floorId;
	String floorname;
	ContentValues values;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	    // this method calling for hide Title from title bar.
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_addnewfloor);
		mContext = this;
		dbHandler = new DatabaseHandler(mContext);

		backTextView = (TextView) findViewById(R.id.backTextView_AddFloor);
		saveTextView = (TextView) findViewById(R.id.saveTextView_AddFloor);
		editText_FloorDescription = (EditText) findViewById(R.id.floorDescriptionTextView_AddFloor);

		if (Constant.ISFLOORUPDATEMODE) {
			floorId = getIntent().getStringExtra("floorid");
			floorname = getIntent().getStringExtra("name");
			editText_FloorDescription.setText(floorname);
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
				if (editText_FloorDescription.getText().length() > 0) {
					
					if (Constant.ISFLOORUPDATEMODE) {
						if(NetworkUtil.getConnectivityStatus(mContext) == 1 || NetworkUtil.getConnectivityStatus(mContext) == 2 ) {
							EditFloorTask editFloorTask = new EditFloorTask(floorId, editText_FloorDescription.getText().toString());
							editFloorTask.execute();
						}
						else {
							Toast.makeText(mContext, NetworkUtil.getConnectivityStatusString(mContext), Toast.LENGTH_LONG);
						}
						
					} else {
						if(NetworkUtil.getConnectivityStatus(mContext) == 1 || NetworkUtil.getConnectivityStatus(mContext) == 2 ) {
							AddFloorTask addFloorTask = new AddFloorTask(editText_FloorDescription.getText().toString());
							addFloorTask.execute();
						}
						else {
							Toast.makeText(mContext, NetworkUtil.getConnectivityStatusString(mContext), Toast.LENGTH_LONG);
						}
					}
					
				} else {
					Toast.makeText(mContext,
							"Please enter floor Discription value",
							Toast.LENGTH_SHORT).show();
				}
				return false;
			}
		});
	}
	
	private void saveToDatabase() {
		values.put(DatabaseHandler.KEY_FID, Constant.FLOOR_ID);
		values.put(DatabaseHandler.KEY_SID, Constant.SURVEY_ID);
		int rowId = (int) dbHandler.addNewFloor(values);
		if (rowId > 0) {
			activityCloseTransition();
		} else {
			Toast.makeText(mContext,
					"Error in inserting floor data",
					Toast.LENGTH_SHORT).show();
		}
	}
	
	private void updateToDatabase() {
		int rowId = 0;
		values = new ContentValues();
		values.put(DatabaseHandler.KEY_FLOOR_DESCRIPTION,
				editText_FloorDescription.getText().toString());
		rowId = dbHandler.updateFloor(values, Constant.FLOOR_ID);
		if (rowId > 0) {
			activityCloseTransition();
		} else {
			Toast.makeText(mContext,
					"Error in inserting floor data",
					Toast.LENGTH_SHORT).show();
		}
	}
	
	public class AddFloorTask extends AsyncTask<Void, Void, String> {

        final String mFloor_name;

        AddFloorTask(String floor_name) {
        	mFloor_name = floor_name;
        }

        @Override
        protected String doInBackground(Void... params) {
            return NetworkUtil.addFloor(Constant.SURVEY_ID, mFloor_name);
        }

		@Override
        protected void onPostExecute(String result) {
			JSONObject reader;
			try {
				reader = new JSONObject(result);
				String status = reader.getString("status");
		        if (status.equals("success")) {
		        	Constant.FLOOR_ID = reader.getJSONObject("data").getString("floor_id");
		        	saveToDatabase();
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
	
	public class EditFloorTask extends AsyncTask<Void, Void, String> {

        final String mFloor_name;
        final String mFloor_id;

        EditFloorTask(String floor_id, String floor_name) {
        	mFloor_name = floor_name;
        	mFloor_id = floor_id;
        }

        @Override
        protected String doInBackground(Void... params) {
            return NetworkUtil.editFloor(mFloor_id, mFloor_name);
        }

		@Override
        protected void onPostExecute(String result) {
			JSONObject reader;
			try {
				reader = new JSONObject(result);
				String status = reader.getString("status");
		        if (status.equals("success")) {
		        	Constant.FLOOR_ID = reader.getJSONObject("data").getString("floor_id");
		        	updateToDatabase();
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
		Constant.ISFLOORUPDATEMODE = false;
		overridePendingTransition(R.animator.in_from_left,
				R.animator.out_to_right);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Constant.ISFLOORUPDATEMODE = false;
	}
}