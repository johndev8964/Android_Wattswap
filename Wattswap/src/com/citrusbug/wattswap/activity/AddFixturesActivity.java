package com.citrusbug.wattswap.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ContentValues;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Window;
import android.widget.ListView;
import android.widget.Toast;

import com.citrusbug.wattswap.R;
import com.citrusbug.wattswap.activity.EditFixuresActivity.AddFixtureTask;
import com.citrusbug.wattswap.adapter.FixtureTypeAdapter;
import com.citrusbug.wattswap.util.Constant;
import com.citrusbug.wattswap.util.DatabaseHandler;
import com.citrusbug.wattswap.util.NetworkUtil;
import com.citrusbug.wattswap.util.Pref;
import com.citrusbug.wattswap.util.NetworkUtil.NewFixture;
import com.citrusbug.wattswap.util.NetworkUtil.NewSurvey;

public class AddFixturesActivity extends Activity {

	// Fixture ListView object
	ListView selectFixtureListView;
	String[] strArray;
	FixtureTypeAdapter adapter;
	// DatabasdeHandler object for database operation
	DatabaseHandler dbHandler;
	static String buildingName, codeTextView, styleTextView, mountingTextView,
			controlledTextView, optionTextView, heightTextView,ballasttypeTextView,ballastfactorTextView;
	Resources rs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// this method calling for hide Title from title bar.
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		// set UI layout to activity
		setContentView(R.layout.activity_addnewfixure);

		//selectFixtureListView = (ListView) findViewById(R.id.selectFixtureList);
		// For access application resources
		rs = getResources();
		//selectFixtureListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		// selectFixtureListView.setTextFilterEnabled(true);

		// Access fixture type array from array.xml
		//strArray = getResources().getStringArray(R.array.Typefixures);

		// DatabaseHandler object initialized to access database instance
		dbHandler = new DatabaseHandler(this);

		// FixturesTypeAdapter Initialize
		//adapter = new FixtureTypeAdapter(getApplicationContext(), strArray);

		// FixtureTypeAdapter set to Fixture ListView
//		selectFixtureListView.setAdapter(adapter);
//		selectFixtureListView.setClickable(false);
//
//		selectFixtureListView.setOnItemClickListener(new OnItemClickListener() {
//			@Override
//			public void onItemClick(AdapterView<?> arg0, View v, int position,
//					long arg3) {
//
//				Constant.ISFIXUPDATEMODE = false;
//				Constant.FIX_NAME_PATH = ImageUtil
//						.getValidName(strArray[position]);
//				Intent intentEditFixures = new Intent(getApplicationContext(),
//						EditFixuresActivity.class);
//				intentEditFixures.putExtra("type", strArray[position]);
//				startActivity(intentEditFixures);
//				finish();
//
//				overridePendingTransition(R.animator.in_from_right,
//						R.animator.out_to_left);
//
//			}
//		});

	}

	public void setValueToDefault() {
		if (buildingName.equalsIgnoreCase("Linear T-12")) {
			codeTextView = rs.getStringArray(R.array.CodeLinearT12)[0];
			styleTextView = rs.getStringArray(R.array.StyleLinearT12)[0];
		} else if (buildingName.equalsIgnoreCase("Linear T-8")) {
			codeTextView = rs.getStringArray(R.array.CodeLinearT8)[0];
			styleTextView = rs.getStringArray(R.array.StyleLinearT8_T5)[0];
		} else if (buildingName.equalsIgnoreCase("Linear T-5")) {
			codeTextView = rs.getStringArray(R.array.CodeLinearT5)[0];
			styleTextView = rs.getStringArray(R.array.StyleLinearT8_T5)[0];
		} else if (buildingName.equalsIgnoreCase("Circline")) {
			codeTextView = rs.getStringArray(R.array.CodeCircline)[0];
			styleTextView = rs.getStringArray(R.array.StyleCircline)[0];
		} else if (buildingName.equalsIgnoreCase("Inc")) {
			codeTextView = rs.getStringArray(R.array.CodeInc)[0];
			styleTextView = rs.getStringArray(R.array.StyleInc)[0];
		} else if (buildingName.equalsIgnoreCase("Inc - Hal")) {
			codeTextView = rs.getStringArray(R.array.CodeIncHal)[0];
			styleTextView = rs.getStringArray(R.array.StyleIncHal)[0];
		} else if (buildingName.equalsIgnoreCase("Exit Inc")) {
			codeTextView = rs.getStringArray(R.array.CodeExitInc)[0];
			styleTextView = rs.getStringArray(R.array.StyleExit)[0];
		} else if (buildingName.equalsIgnoreCase("Exit CFL")) {
			codeTextView = rs.getStringArray(R.array.CodeExitCFL)[0];
			styleTextView = rs.getStringArray(R.array.StyleExit)[0];
		} else if (buildingName.equalsIgnoreCase("Exit LED")) {
			codeTextView = rs.getStringArray(R.array.CodeExitLED)[0];
			styleTextView = rs.getStringArray(R.array.StyleExit)[0];
		} else if (buildingName.equalsIgnoreCase("CFL")) {
			codeTextView = rs.getStringArray(R.array.CodeCFL)[0];
			styleTextView = rs.getStringArray(R.array.StyleCFL)[0];
		} else if (buildingName.equalsIgnoreCase("MH")) {
			codeTextView = rs.getStringArray(R.array.CodeMH)[0];
			styleTextView = rs.getStringArray(R.array.StyleMH_HPS)[0];
		} else if (buildingName.equalsIgnoreCase("HPS")) {
			codeTextView = rs.getStringArray(R.array.CodeHPS)[0];
			styleTextView = rs.getStringArray(R.array.StyleMH_HPS)[0];
		}
		mountingTextView = rs.getStringArray(R.array.Mounting)[0];
		controlledTextView = rs.getStringArray(R.array.Controlled)[0];
		optionTextView = rs.getStringArray(R.array.Options)[0];
		heightTextView = rs.getStringArray(R.array.Height)[0];
		ballasttypeTextView = rs.getStringArray(R.array.Ballast_type)[0];
		ballastfactorTextView = rs.getStringArray(R.array.Ballast_factor)[0];
		
	}

	@SuppressWarnings("static-access")
	public void addNewFixtures() {

		ContentValues value = new ContentValues();
		NewFixture newFixture = new NewFixture();
		newFixture.area_id = Constant.AREA_ID;
		newFixture.survey_id = Constant.SURVEY_ID;
		newFixture.floor_id = Constant.FLOOR_ID;
		
		value.put(DatabaseHandler.KEY_AID, Constant.AREA_ID);
		
		value.put(DatabaseHandler.KEY_FIXTURE_NAME, buildingName);
		newFixture.type = buildingName;
		
		value.put(DatabaseHandler.KEY_FIX_COUNT, "0");
		newFixture.count = "0";
		
		value.put(DatabaseHandler.KEY_CODE, codeTextView);
		newFixture.code = codeTextView;
		
		value.put(DatabaseHandler.KEY_STYLE, styleTextView);
		newFixture.style = styleTextView;
		
		value.put(DatabaseHandler.KEY_MOUNTING, mountingTextView);
		newFixture.mounting = mountingTextView;
		
		value.put(DatabaseHandler.KEY_CONTROLLED, controlledTextView);
		newFixture.controlled = controlledTextView;
		
		value.put(DatabaseHandler.KEY_OPTINON, optionTextView);
		newFixture.option = optionTextView;
		
		value.put(DatabaseHandler.KEY_HEIGHT, heightTextView);
		newFixture.height = heightTextView;
		
		value.put(DatabaseHandler.KEY_FVALUE, "");
		newFixture.hrs_per_day = "";
		
		value.put(DatabaseHandler.KEY_SVALUE, "");
		newFixture.days_per_week = "";
		
		value.put(DatabaseHandler.KEY_ANSWER, "");
		value.put(DatabaseHandler.KEY_BULBSVALUE, "");
		newFixture.bulbs_per_fixture = "";
		
		value.put(DatabaseHandler.KEY_WATTSVALUE, "");
		newFixture.watts_per_bulb = "";
		
		value.put(DatabaseHandler.KEY_ANSWERBULBS, "");
		value.put(DatabaseHandler.KEY_BALLAST_TYPE, ballasttypeTextView );
		newFixture.ballast = "";
		
		value.put(DatabaseHandler.KEY_BALLAST_FACTOR, ballastfactorTextView );
		newFixture.factor = ballastfactorTextView;
		
		value.put(DatabaseHandler.KEY_NOTE, "");
		newFixture.notes = "";
		
		newFixture.fixture_images_b64 = "";
		
		if(NetworkUtil.getConnectivityStatus(this) == 1 || NetworkUtil.getConnectivityStatus(this) == 2 ) {
			AddFixtureTask fixtureTask = new AddFixtureTask(newFixture);
			fixtureTask.execute();
		}
		else {
			Toast.makeText(this, NetworkUtil.getConnectivityStatusString(this), Toast.LENGTH_LONG).show();
		}
		
		int rowid;

		rowid = (int) dbHandler.addNewFixtures(value);
		Pref.setValue(Constant.PREF_LAST_FIXTURES_ID, "" + rowid);
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
			
	        if (result == null) {
	        	Toast.makeText(Constant.CONTEXT, "Server Error", Toast.LENGTH_LONG).show();
	        }
	        else {
	        	JSONObject reader;
				try {
					reader = new JSONObject(result);
					String status = reader.getString("status");
			        if (status.equals("success")) {

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
}