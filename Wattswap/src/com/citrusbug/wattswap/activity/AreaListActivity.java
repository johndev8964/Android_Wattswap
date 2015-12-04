package com.citrusbug.wattswap.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;



import com.citrusbug.wattswap.R;
import com.citrusbug.wattswap.adapter.AreaListAdapter;
import com.citrusbug.wattswap.adapter.AreaListAdapter.CopyAreaDetector;
import com.citrusbug.wattswap.adapter.AreaListAdapter.DeleteAreaDetector;
import com.citrusbug.wattswap.adapter.AreaListAdapter.ViewHolder;
import com.citrusbug.wattswap.bean.Area;
import com.citrusbug.wattswap.util.Constant;
import com.citrusbug.wattswap.util.DatabaseHandler;
import com.citrusbug.wattswap.util.ImageUtil;
import com.citrusbug.wattswap.util.NetworkUtil;
import com.citrusbug.wattswap.util.SwipeDetector;

public class AreaListActivity extends Activity implements CopyAreaDetector, DeleteAreaDetector {
	TextView homeTextView;
	ImageView addNewAreaTextView;
	TextView titleTextView;
	TextView floorTextView;
	TextView dottextview;
	TextView fixturetextview;
	ListView areaListView;
	TextView areatextview;
	Context mContext;
	DatabaseHandler dbHandler;
	List<Area> areaList;
	AreaListAdapter areaAdapter;
	String floorName;

	// SwipeDetector object used to perform swipe event on listview item
	SwipeDetector swipeDetector;
	EditText edAdd;
	ImageView btn_add;
	ContentValues values;
	private ImageView textview1;
	private TextView textview2;
	private ViewHolder prefHolder;
	private Area prefArea;
	private View footer1;
	private ViewFlipper vf;
	private boolean isEdAddFocus = false;
	
	public static boolean goFloor = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_arealist);
		mContext = this;
		Constant.ISFINISH = false;

		dbHandler = new DatabaseHandler(mContext);
		floorName = getIntent().getStringExtra("floorname");
		swipeDetector = new SwipeDetector();

		Typeface font = Typeface.createFromAsset(getAssets(),
				"fontawesome-webfont.ttf");
		Typeface face = Typeface.createFromAsset(getAssets(), "gothic.ttf");
		areaListView = (ListView) findViewById(R.id.areaListview);
		homeTextView = (TextView) findViewById(R.id.home_TextView_AreaList);
		homeTextView.setTypeface(font);

		addNewAreaTextView = (ImageView) findViewById(R.id.plus);
		titleTextView = (TextView) findViewById(R.id.title_TextView_AreaList);
		titleTextView.setTypeface(face);
		floorTextView = (TextView) findViewById(R.id.floor_TextView_AreaList);
		floorTextView.setTypeface(face);
		floorTextView.setText(floorName);
		fixturetextview = (TextView) findViewById(R.id.fixtures_TextView_AreaList);
		fixturetextview.setTypeface(face);
		areatextview = (TextView) findViewById(R.id.area_TextView_AreaList);
		areatextview.setTypeface(face);

		floorTextView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// Constant.ISFINISH=true;
				activityCloseTransition();
				finish();

			}
		});

		titleTextView.setText(Constant.SURVEY_NAME);

		// homeTextView.setOnTouchListener(new OnTouchListener() {
		// @Override
		// public boolean onTouch(View v, MotionEvent event) {
		// activityCloseTransition();
		// return false;
		// Intent intent = new Intent(this, HomeActivity.class);
		// }
		// });

		homeTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Intent intenthome = new Intent(mContext, HomeActivity.class);
				startActivity(intenthome);
				activityCloseTransition();
				// return false;
			}
		});
		
		swipeDetector = new SwipeDetector() {
			@Override
			public void onActionUp() {
				// TODO Auto-generated method stub
				super.onActionUp();
				if (swipeDetector.getAction() == SwipeDetector.Action.LR) {
					
					if(prefArea != null && prefArea.isVisible == true) {
						vf.setInAnimation(inFromRightAnimation());
						vf.setOutAnimation(outToLeftAnimation());
						vf.showNext();
						edAdd.requestFocus();
						isEdAddFocus = true;
					}
					
					for (int position = 0; position < areaAdapter.getCount(); position++) {
						Area areaItem = (Area) areaAdapter.getItem(position);
						areaItem.isVisible = false;
					}
					areaAdapter.notifyDataSetChanged();
				}
			}
		};

		addNewAreaTextView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(prefArea == null || prefArea.isVisible != true) {
					vf.setInAnimation(inFromLeftAnimation());
					vf.setOutAnimation(outToRightAnimation());
					vf.showPrevious();
				}
				Constant.ISAREAUPDATEMODE = false;
				Intent intentAddNewArea = new Intent(mContext,
						AddNewAreaActivity.class);
				startActivity(intentAddNewArea);
				activityOpenTransition();
				return false;
			}
		});

		areaListView.setOnTouchListener(swipeDetector);

		areaListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adpter, View arg1,
					int postion, long arg3) {
				ViewHolder holder = (ViewHolder) arg1.getTag();
				Area areaItem = (Area) adpter.getItemAtPosition(postion);

				if (swipeDetector.swipeDetected()) {
					if (swipeDetector.getAction() == SwipeDetector.Action.LR) {
						if (holder.viewFlipper.getDisplayedChild() != 0) {
							holder.viewFlipper.showPrevious();
							areaItem.isVisible = false;
						}
					}
					if (swipeDetector.getAction() == SwipeDetector.Action.RL) {
						if (holder.viewFlipper.getDisplayedChild() == 0) {
							if(prefHolder != null && prefHolder != holder && prefArea.isVisible == true) {
								prefHolder.viewFlipper.showPrevious();
								prefArea.isVisible = false;
							}
							holder.viewFlipper.showNext();
							areaItem.isVisible = true;
							prefHolder = holder;
							prefArea = areaItem;
							
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
						edAdd.clearFocus();
						InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(edAdd.getWindowToken(), 0);
						
						if(prefArea == null || prefArea.isVisible != true) {
							vf.setInAnimation(inFromLeftAnimation());
							vf.setOutAnimation(outToRightAnimation());
							vf.showPrevious();
						}
						
						Constant.AREA_ID = areaList.get(postion).areaId;
						Constant.AREA_NAME_PATH = ImageUtil
								.getValidName(areaList.get(postion).loctaionName);
						Intent intentFixure = new Intent(mContext,
								FixturesListActivity.class);
						intentFixure.putExtra("floorname", floorName);
						intentFixure.putExtra("areaname",
								areaList.get(postion).loctaionName);
						startActivity(intentFixure);
						activityOpenTransition();
					} catch (IndexOutOfBoundsException e) {
						System.out.println(e);
					}
				}
			}
		});

		// //add footer to listview
		//
		// View footer=getLayoutInflater().inflate(R.layout.addnewfield, null);
		// edAdd = (EditText)footer.findViewById(R.id.txt_new);
		// btn_add = (Button)footer.findViewById(R.id.btn_add);
		// edAdd.setHint("Add new Area");
		// btn_add.setOnClickListener(new View.OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		// if (edAdd.getText().length() > 0) {
		// ContentValues values = new ContentValues();
		// values.put(DatabaseHandler.KEY_LNAME, edAdd.getText().toString());
		//
		// values.put(DatabaseHandler.KEY_FID, Constant.FLOOR_ID);
		// dbHandler.addArea(values);
		// edAdd.setText("");
		// onResume();
		// } else {
		// Toast.makeText(mContext,"Please enter location name value",Toast.LENGTH_SHORT).show();
		// }
		// }
		// });
		//
		// areaListView.addFooterView(footer);
		// areaList = new ArrayList<Area>();
		// areaAdapter = new AreaListAdapter(mContext, areaList, this);
		// areaListView.setAdapter(areaAdapter);
		//

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
				if(prefHolder != null && prefArea.isVisible == true) {
					prefHolder.viewFlipper.showPrevious();
					prefArea.isVisible = false;
				}
				edAdd.requestFocus();
				isEdAddFocus = true;
			}
		});
		textview1 = (ImageView) footer1.findViewById(R.id.txt_newadd);
		//textview1.setTypeface(font);
		textview2 = (TextView) footer1.findViewById(R.id.aa);
		textview2.setHint("add area");
		textview2.setTypeface(face);
		areaListView.addFooterView(footer1);
		areaList = new ArrayList<Area>();
		areaAdapter = new AreaListAdapter(mContext, areaList, this, this);
		areaListView.setAdapter(areaAdapter);
		edAdd = (EditText) footer1.findViewById(R.id.txt_new);
		edAdd.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(prefHolder != null && prefArea.isVisible == true) {
					prefHolder.viewFlipper.showPrevious();
					prefArea.isVisible = false;
				}
			}
		}	
		);
		
		btn_add = (ImageView) footer1.findViewById(R.id.btn_add);

		edAdd.setHint("add new area");
		edAdd.setTypeface(face);
		btn_add.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (edAdd.getText().length() > 0) {
					values = new ContentValues();
					values.put(DatabaseHandler.KEY_LNAME, Character.toUpperCase(edAdd
							.getText().toString().charAt(0)) + edAdd
							.getText().toString().substring(1));

					values.put(DatabaseHandler.KEY_FID, Constant.FLOOR_ID);
					
					if(NetworkUtil.getConnectivityStatus(mContext) == 1 || NetworkUtil.getConnectivityStatus(mContext) == 2 ) {
						AddAreaTask addAreaTask = new AddAreaTask(Character.toUpperCase(edAdd
								.getText().toString().charAt(0)) + edAdd
								.getText().toString().substring(1));
						addAreaTask.execute();
					}
					else {
						Toast.makeText(mContext, NetworkUtil.getConnectivityStatusString(mContext), Toast.LENGTH_LONG);
					}				
					
				} else {
					Toast.makeText(mContext,
							"Please enter location name value",
							Toast.LENGTH_SHORT).show();
				}
				/*vf.setInAnimation(inFromLeftAnimation());

				vf.setOutAnimation(outToRightAnimation());

				vf.showPrevious();*/
			}

		});

		textview1.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				vf.setInAnimation(inFromRightAnimation());

				vf.setOutAnimation(outToLeftAnimation());

				vf.showNext();
				if(prefHolder != null && prefArea.isVisible == true) {
					prefHolder.viewFlipper.showPrevious();
					prefArea.isVisible = false;
				}
				edAdd.requestFocus();
				isEdAddFocus = true;
			}
		});
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

		areaList = dbHandler.getAreaList(Constant.FLOOR_ID);
		if (areaList != null) {
			areaAdapter = new AreaListAdapter(mContext, areaList, this, this);
			areaListView.setAdapter(areaAdapter);
			areaListView.setSelection(areaList.size() - 1);
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
		
		if(goFloor) {
			new Handler().postDelayed(new Runnable(){
	            @Override
	            public void run() {
	                /* Create an Intent that will start the Menu-Activity. */
	            	activityCloseTransition();
					finish();
					goFloor = false;
	            }
	        }, 500);
		}
	}

	public void activityCloseTransition() {
		finish();
		overridePendingTransition(R.animator.in_from_left,
				R.animator.out_to_right);
	}

	public void activityOpenTransition() {
		isEdAddFocus = false;
		overridePendingTransition(R.animator.in_from_right,
				R.animator.out_to_left);
	}

	@Override
	public void onCopyArea() {
		// TODO Auto-generated method stub
		prefArea.isVisible = false;
		onResume();
	}

	@Override
	public void onDeleteArea() {
		// TODO Auto-generated method stub
		prefArea.isVisible = false;
		onResume();
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
		        	values.put(DatabaseHandler.KEY_AID, Constant.AREA_ID);
		        	dbHandler.addArea(values);
		        	
		        	edAdd.setText("");
					edAdd.clearFocus();
					InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(edAdd.getWindowToken(), 0);
					
					isEdAddFocus = false;
					onResume();
					Constant.AREA_ID = areaList.get(areaList.size()-1).areaId;
					Constant.AREA_NAME_PATH = ImageUtil
							.getValidName(areaList.get(areaList.size()-1).loctaionName);
					Intent intentFixure = new Intent(mContext,
							FixturesListActivity.class);
					intentFixure.putExtra("floorname", floorName);
					intentFixure.putExtra("areaname",
							areaList.get(areaList.size()-1).loctaionName);
					startActivity(intentFixure);
					activityOpenTransition();
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
}