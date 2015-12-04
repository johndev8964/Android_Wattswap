package com.citrusbug.wattswap.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.citrusbug.wattswap.R;
import com.citrusbug.wattswap.activity.AddNewFloorActivity.AddFloorTask;
import com.citrusbug.wattswap.adapter.FloorListAdapter;
import com.citrusbug.wattswap.adapter.FloorListAdapter.CopyDetector;
import com.citrusbug.wattswap.adapter.FloorListAdapter.DeleteDetector;
import com.citrusbug.wattswap.adapter.FloorListAdapter.ViewHolder;
import com.citrusbug.wattswap.bean.Floor;
import com.citrusbug.wattswap.util.Constant;
import com.citrusbug.wattswap.util.DatabaseHandler;
import com.citrusbug.wattswap.util.ImageUtil;
import com.citrusbug.wattswap.util.NetworkUtil;
import com.citrusbug.wattswap.util.SwipeDetector;

public class FloorList extends Activity implements CopyDetector, DeleteDetector {
	TextView homeTextView;
	ImageView addNewFloorTextView;
	TextView dotshow;
	TextView titleTextView;
	ListView floorListView;
	TextView areatextview;
	TextView floortextview;
	TextView fixturetextview;
	Context mContext;
	DatabaseHandler dbHandler;
	List<Floor> floorList;
	FloorListAdapter floorAdapter;
	// Set the touch listener
	SwipeDetector swipeDetector;

	EditText edAdd;
	ImageView btn_add;

	ImageView textview1;
	TextView textview2;
	private TextView textview3;
	private TextView textview4;
	
	private ViewHolder prefHolder;
	private Floor prefFloor;
	
	private View footer1;
	private ViewFlipper vf;
	private boolean isEdAddFocus = false;
	
	ContentValues values;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_floorlist);
		mContext = this;
		dbHandler = new DatabaseHandler(mContext);
		Typeface font = Typeface.createFromAsset(getAssets(),
				"fontawesome-webfont.ttf");
		Typeface face = Typeface.createFromAsset(getAssets(), "gothic.ttf");
		
		floorListView = (ListView) findViewById(R.id.floor_listview);
		
		homeTextView = (TextView) findViewById(R.id.home_TextView_FloorList);
		addNewFloorTextView = (ImageView) findViewById(R.id.plus);

		homeTextView.setTypeface(font);

		areatextview = (TextView) findViewById(R.id.area_TextView_FloorList);
		areatextview.setTypeface(face);
		floortextview = (TextView) findViewById(R.id.floor_TextView_FloorList);
		floortextview.setTypeface(face);
		fixturetextview = (TextView) findViewById(R.id.fixtures_TextView_FloorList);
		fixturetextview.setTypeface(face);

		titleTextView = (TextView) findViewById(R.id.title_TextView_FloorList);
		titleTextView.setTypeface(face);
		titleTextView.setText(Constant.SURVEY_NAME);

		homeTextView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				ActivityCloseTransition();
				return false;
			}
		});
		swipeDetector = new SwipeDetector() {
			@Override
			public void onActionUp() {
				// TODO Auto-generated method stub
				
				super.onActionUp();
				if (swipeDetector.getAction() == SwipeDetector.Action.LR) {
					if(prefFloor != null && prefFloor.isVisible == true) {
						vf.setInAnimation(inFromRightAnimation());
						vf.setOutAnimation(outToLeftAnimation());
						vf.showNext();
						edAdd.requestFocus();
						isEdAddFocus = true;
					}
					for (int position = 0; position < floorAdapter.getCount(); position++) {
						Floor floorItem = (Floor) floorAdapter
								.getItem(position);
						floorItem.isVisible = false;
					}
					floorAdapter.notifyDataSetChanged();
				}
			}
		};

		addNewFloorTextView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(prefFloor == null || prefFloor.isVisible != true) {
					vf.setInAnimation(inFromLeftAnimation());
					vf.setOutAnimation(outToRightAnimation());
					vf.showPrevious();
				}
				
				Constant.ISFLOORUPDATEMODE = false;
				Intent intentAddNewFloor = new Intent(mContext,
						AddNewFloorActivity.class);
				startActivity(intentAddNewFloor);
				ActivityOpenTransition();
				return false;
			}
		});

		floorListView.setOnTouchListener(swipeDetector);

		floorListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View arg1,
					int position, long arg3) {
				ViewHolder holder = (ViewHolder) arg1.getTag();
				Floor floorItem = (Floor) adapter.getItemAtPosition(position);

				if (swipeDetector.swipeDetected()) {
					if (swipeDetector.getAction() == SwipeDetector.Action.LR) {
						if (holder.viewFlipper.getDisplayedChild() != 0) {
							holder.viewFlipper.showPrevious();
							floorItem.isVisible = false;
						}
					}
					if (swipeDetector.getAction() == SwipeDetector.Action.RL) {
						if (holder.viewFlipper.getDisplayedChild() == 0) {
							if(prefHolder != null && prefHolder != holder && prefFloor.isVisible == true) {
								prefHolder.viewFlipper.showPrevious();
								prefFloor.isVisible = false;
							}
							holder.viewFlipper.showNext();
							floorItem.isVisible = true;
							prefHolder = holder;
							prefFloor = floorItem;
							if(isEdAddFocus) {
								vf.setInAnimation(inFromLeftAnimation());
								vf.setOutAnimation(outToRightAnimation());
								vf.showPrevious();
								edAdd.clearFocus();
								isEdAddFocus = false;
							}
							
						}
					}
				} else {
					try {
						if(prefFloor == null || prefFloor.isVisible != true) {
							vf.setInAnimation(inFromLeftAnimation());
							vf.setOutAnimation(outToRightAnimation());
							vf.showPrevious();
						}
						Constant.FLOOR_ID = floorList.get(position).floorId;
						Constant.FLOOR_NAME_PATH = ImageUtil
								.getValidName(floorList.get(position).floorDiscription);
						Intent intentAreaList = new Intent(mContext,
								AreaListActivity.class);
						intentAreaList.putExtra("floorname",
								floorList.get(position).floorDiscription);
						startActivity(intentAreaList);
						ActivityOpenTransition();
					} catch (IndexOutOfBoundsException e) {
						System.out.println(e);
					}

				}
			}
		});

		// add footer to listview

		// final View footer=getLayoutInflater().inflate(R.layout.addnewfield,
		// null);
		//
		// edAdd = (EditText)footer.findViewById(R.id.txt_new);
		// btn_add = (Button)footer.findViewById(R.id.btn_add);
		//
		// edAdd.setHint("Add new Floor");
		// btn_add.setOnClickListener(new View.OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		// if (edAdd.getText().length() > 0) {
		// ContentValues values = new ContentValues();
		// values.put(DatabaseHandler.KEY_FLOOR_DESCRIPTION,
		// edAdd.getText().toString());
		//
		// values.put(DatabaseHandler.KEY_SID, Constant.SURVEY_ID);
		// dbHandler.addNewFloor(values);
		// edAdd.setText("");
		// onResume();
		// } else {
		// Toast.makeText(mContext,
		// "Please enter floor Discription value",
		// Toast.LENGTH_SHORT).show(); }
		// }
		// });
		//
		//
		//
		// floorListView.addFooterView(footer);
		// floorList = new ArrayList<Floor>();
		// floorAdapter = new FloorListAdapter(mContext, floorList,this);
		// floorListView.setAdapter(floorAdapter);
		//

		// /add second footer
		footer1 = getLayoutInflater().inflate(
				R.layout.addtextbydefault, null);
		vf = (ViewFlipper) footer1
				.findViewById(R.id.viewflipper);
		footer1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				vf.setInAnimation(inFromRightAnimation());
				vf.setOutAnimation(outToLeftAnimation());
				vf.showNext();
				if(prefHolder != null && prefFloor.isVisible == true) {
					prefHolder.viewFlipper.showPrevious();
					prefFloor.isVisible = false;
				}
				edAdd.requestFocus();
				isEdAddFocus = true;
			}
		});

		textview1 = (ImageView) footer1.findViewById(R.id.txt_newadd);
		//textview1.setTypeface(font);
		textview2 = (TextView) footer1.findViewById(R.id.aa);
		textview2.setHint("add floor");
		textview2.setTypeface(face);
		
		floorListView.addFooterView(footer1);
		floorList = new ArrayList<Floor>();
		floorAdapter = new FloorListAdapter(mContext, floorList, this, this);
		floorListView.setAdapter(floorAdapter);
		edAdd = (EditText) footer1.findViewById(R.id.txt_new);

		edAdd.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(prefHolder != null && prefFloor.isVisible == true) {
					prefHolder.viewFlipper.showPrevious();
					prefFloor.isVisible = false;
				}
			}
		}	
		);
		
		btn_add = (ImageView) footer1.findViewById(R.id.btn_add);

		edAdd.setHint("add new floor");
		edAdd.setTypeface(face);
		btn_add.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (edAdd.getText().length() > 0) {
					
					
					AddFloorTask addFloorTask = new AddFloorTask(Character.toUpperCase(edAdd
							.getText().toString().charAt(0)) + edAdd
							.getText().toString().substring(1));
					
					addFloorTask.execute();
					
					
				} else {

					Toast.makeText(mContext,
							"Please enter floor Discription value",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		textview1.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				vf.setInAnimation(inFromRightAnimation());

				vf.setOutAnimation(outToLeftAnimation());

				vf.showNext();
				if(prefHolder != null && prefFloor.isVisible == true) {
					prefHolder.viewFlipper.showPrevious();
					prefFloor.isVisible = false;
				}
				edAdd.requestFocus();
				isEdAddFocus = true;
			}
		});
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
	
	private void saveToDatabase() {
		values = new ContentValues();
		values.put(DatabaseHandler.KEY_FLOOR_DESCRIPTION, Character.toUpperCase(edAdd
				.getText().toString().charAt(0)) + edAdd
				.getText().toString().substring(1));

		values.put(DatabaseHandler.KEY_SID, Constant.SURVEY_ID);
		values.put(DatabaseHandler.KEY_FID, Constant.FLOOR_ID);
		dbHandler.addNewFloor(values);
		edAdd.setText("");
		isEdAddFocus = false;
		onResume();
		//Constant.FLOOR_ID = floorList.get(floorList.size()-1).floorId;
		Constant.FLOOR_NAME_PATH = ImageUtil
				.getValidName(floorList.get(floorList.size()-1).floorDiscription);
		Intent intentAreaList = new Intent(mContext,
				AreaListActivity.class);
		intentAreaList.putExtra("floorname", floorList.get(floorList.size()-1).floorDiscription);
		startActivity(intentAreaList);
		ActivityOpenTransition();
	}
	
	private Animation inFromRightAnimation() {

		Animation inFromRight = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, +1.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);
		inFromRight.setDuration(100);
		inFromRight.setInterpolator(new AccelerateInterpolator());
		return inFromRight;
	}

	private Animation outToLeftAnimation() {
		Animation outtoLeft = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, -1.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);
		outtoLeft.setDuration(100);
		outtoLeft.setInterpolator(new AccelerateInterpolator());
		return outtoLeft;
	}

	private Animation inFromLeftAnimation() {
		Animation inFromLeft = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, -1.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);
		inFromLeft.setDuration(100);
		inFromLeft.setInterpolator(new AccelerateInterpolator());
		return inFromLeft;
	}

	private Animation outToRightAnimation() {
		Animation outtoRight = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, +1.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);
		outtoRight.setDuration(100);
		outtoRight.setInterpolator(new AccelerateInterpolator());
		return outtoRight;
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (Constant.ISFINISH)
			finish();

		floorList = dbHandler.getFloorList(Constant.SURVEY_ID);
		if (floorList != null) {
			floorAdapter = new FloorListAdapter(mContext, floorList, this, this);
			floorListView.setAdapter(floorAdapter);
			floorListView.setSelection(floorList.size() - 1);
		} else {
			Log.d("Floor List", "null");
		}
		
		if(!isEdAddFocus){
			vf.setInAnimation(inFromRightAnimation());
			vf.setOutAnimation(outToLeftAnimation());
			vf.showNext();
		}
		edAdd.requestFocus();
	    
	    this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
	    isEdAddFocus = true;
		
	}

	
	public void ActivityCloseTransition() {
		finish();
		
		overridePendingTransition(R.animator.in_from_left,
				R.animator.out_to_right);
	}

	public void ActivityOpenTransition() {
		isEdAddFocus = false;
		overridePendingTransition(R.animator.in_from_right,
				R.animator.out_to_left);
	}

	@Override
	public void onCopyFloor() {
		// TODO Auto-generated method stub
		prefFloor.isVisible = false;
		onResume();
	}

	@Override
	public void onDeleteFloor() {
		// TODO Auto-generated method stub
		prefFloor.isVisible = false;
		onResume();
	}
	
}

