package com.citrusbug.wattswap.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.citrusbug.wattswap.bean.Area;
import com.citrusbug.wattswap.bean.Fixtures;
import com.citrusbug.wattswap.bean.Floor;
import com.citrusbug.wattswap.bean.Survey;

public class DatabaseHandler extends SQLiteOpenHelper {

	// Application Database Version Number
	private static final int DATABASE_VERSION = 1;
	// Application Database name
	private static final String DATABASE_NAME = "surveyapp";

	// Instance of this class
	private static DatabaseHandler sInstance;

	// Survey Info table name
	private static final String TABLE_SURVEY_INFO = "tbl_survey_info";
	// Floor Info table name
	private static final String TABLE_FLOOR_INFO = "tbl_floor_info";
	// Area Info table name
	private static final String TABLE_AREA_INFO = "tbl_area_info";
	// Fixtures Info table name
	private static final String TABLE_FIXTURES_INFO = "tbl_fixtures_info";

	// Images table name
	// private static final String TABLE_IMAGES="tbl_images";

	// Survey Info table column name
	public static final String KEY_SID = "s_id";
	public static final String KEY_SNAME = "sname";
	public static final String KEY_CNAME = "cname";
	public static final String KEY_ADDRESS_LINE1 = "address_line1";
	public static final String KEY_ADDRESS_LINE2 = "address_line2";
	public static final String KEY_CITY = "city";
	public static final String KEY_STATE = "state";
	public static final String KEY_ZIP = "zip";
	public static final String KEY_EMAIL = "email";
	public static final String KEY_PHONE = "phone";
	public static final String KEY_SCHEDULED = "sheduled";
	public static final String KEY_NOTE = "note";
	public static final String KEY_BUNIT = "bunit";
	public static final String KEY_BTYPE = "btype";
	public static final String KEY_BREFF = "breffby";
	public static final String KEY_BTOTAL_FOOTAGE = "total_footage";
	public static final String KEY_UTILITY_CMPNY = "utility_cmpny";
	public static final String KEY_ACCOUNT_NUMBER = "account_num";
	public static final String KEY_STATUS = "status";

	// Images table column name
	public static final String KEY_IID = "i_id";
	public static final String KEY_PATH = "path";
	public static final String KEY_TYPE = "type";

	// Floor table column name
	public static final String KEY_FID = "f_id";
	public static final String KEY_FLOOR_DESCRIPTION = "floor_description";

	// Area table column name
	public static final String KEY_AID = "a_id";
	public static final String KEY_LNAME = "location_name";

	// fixture table column name
	public static final String KEY_FIXTURE_ID = "fix_id";
	public static final String KEY_FIXTURE_NAME = "fix_name";
	public static final String KEY_FIX_COUNT = "fix_count";
	public static final String KEY_CODE = "code";
	public static final String KEY_STYLE = "style";
	public static final String KEY_MOUNTING = "mounting";
	public static final String KEY_CONTROLLED = "controlled";
	public static final String KEY_OPTINON = "option";
	public static final String KEY_HEIGHT = "height";
	public static final String KEY_FVALUE = "fvalue";
	public static final String KEY_SVALUE = "svalue";
	public static final String KEY_ANSWER = "answer";
	public static final String KEY_BALLAST_TYPE = "ballasttype";
	public static final String KEY_BALLAST_FACTOR = "ballastfactor";
	public static final String KEY_BULBSVALUE = "Bulbsvalue";
	public static final String KEY_WATTSVALUE = "wattsvalue";
	public static final String KEY_ANSWERBULBS = "answerbulbs";
	
	// String statement for create TABLE_SURVEY_INFO table structure
	private static final String CREATE_SURVEY_INFO_TABLE = "CREATE TABLE "
			+ TABLE_SURVEY_INFO + " ( " + KEY_SID
			+ " INTEGER ," + KEY_SNAME + " TEXT,"
			+ KEY_ADDRESS_LINE1 + " TEXT," + KEY_ADDRESS_LINE2 + " TEXT,"
			+ KEY_CITY + " TEXT," + KEY_STATE + " TEXT," + KEY_ZIP + " TEXT,"
			+ KEY_CNAME + " TEXT," + KEY_PHONE + " TEXT," + KEY_EMAIL
			+ " TEXT," + KEY_SCHEDULED + " DATE," + KEY_NOTE + " TEXT ,"
			+ KEY_BUNIT + " INTEGER ," + KEY_BTYPE + " TEXT ," + KEY_BREFF
			+ " TEXT ," + KEY_BTOTAL_FOOTAGE + " INTEGER ," + KEY_UTILITY_CMPNY
			+ " TEXT," + KEY_ACCOUNT_NUMBER + " TEXT ," + KEY_STATUS
			+ " TEXT Default 1," + KEY_PATH + " TEXT " + " ) ";

	// String statement for create TABLE_FLOOR_INFO table structure
	private static final String CREATE_FLOOR_INFO_TABLE = "CREATE TABLE "
			+ TABLE_FLOOR_INFO + " ( " + KEY_FID
			+ " INTEGER ," + KEY_SID + " INTEGER ,"
			+ KEY_FLOOR_DESCRIPTION + " TEXT " + " ) ";

	// String statement for create TABLE_AREA_INFO table structure
	private static final String CREATE_AREA_INFO_TABLE = "CREATE TABLE "
			+ TABLE_AREA_INFO + " ( " + KEY_AID
			+ " INTEGER ," + KEY_FID + " INTEGER ,"
			+ KEY_LNAME + " TEXT " + " ) ";

	// String statement for create TABLE_FIXTURES_INFO table structure
	private static final String CREATE_FIXTURES_INFO_TABLE = "CREATE TABLE "
			+ TABLE_FIXTURES_INFO + " ( " + KEY_FIXTURE_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_AID + " INTEGER ,"
			+ KEY_FIXTURE_NAME + " TEXT ," + KEY_FIX_COUNT + " INTEGER ,"
			+ KEY_CODE + " TEXT ," + KEY_STYLE + " TEXT ," + KEY_MOUNTING
			+ " TEXT ," + KEY_CONTROLLED + " TEXT ," + KEY_OPTINON + " TEXT ,"
			+ KEY_HEIGHT + " TEXT ," + KEY_FVALUE + " INTEGER ," + KEY_SVALUE
			+ " INTEGER ," + KEY_ANSWER + " INTEGER ," + KEY_PATH + " TEXT ,"
			+ KEY_NOTE + " TEXT ,"+ KEY_BALLAST_TYPE + " TEXT ,"
			+ KEY_BALLAST_FACTOR + " TEXT ,"
			+ KEY_BULBSVALUE + " INTEGER ,"
			+ KEY_WATTSVALUE + " INTEGER ,"
			+ KEY_ANSWERBULBS +" INTEGER " + " ) ";

	/**
	 * Returns an Instance of DatabaseHandler. Singleton Pattern.
	 * 
	 * @param context
	 *            an Object of Application Context
	 * 
	 * @return an Object of DatabaseHandler
	 */
	public static DatabaseHandler getInstance(Context context) {
		// Use the application context, which will ensure that you
		// don't accidentally leak an Activity's context.
		// See this article for more information: http://bit.ly/6LRzfx
		if (sInstance == null) {
			sInstance = new DatabaseHandler(context.getApplicationContext());
		}

		return sInstance;
	}

	Context _context;

	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);

		_context = context;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite
	 * .SQLiteDatabase)
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_SURVEY_INFO_TABLE);
		db.execSQL(CREATE_FLOOR_INFO_TABLE);
		db.execSQL(CREATE_AREA_INFO_TABLE);
		db.execSQL(CREATE_FIXTURES_INFO_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_SURVEY_INFO);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_FLOOR_INFO);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_AREA_INFO);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_FIXTURES_INFO);
		// Create tables again
		onCreate(db);
	}

	/**
	 * This method used to add new Survey Record in table tbl_survey_info
	 * 
	 * @param values
	 *            is an Object of ContentValues which contains new survey data
	 * @return the newly inserted row id , or -1 if an error occurred default is
	 *         return is 0
	 */
	public double addNewSurvey(ContentValues values) {
		double row = 0;
		try {
			SQLiteDatabase db = this.getWritableDatabase();
			row = (int) db.insert(TABLE_SURVEY_INFO, null, values);
			db.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return row;
	}

	/**
	 * This method used to add new Fixtures Record in table tbl_fixtures_info
	 * 
	 * @param values
	 *            is an Object of ContentValues which contains new fixtures data
	 * @return the newly inserted row id , or -1 if an error occurred default is
	 *         return is 0
	 */
	public double addNewFixtures(ContentValues values) {
		double row = 0;
		try {
			SQLiteDatabase db = this.getWritableDatabase();
			row = (int) db.insert(TABLE_FIXTURES_INFO, null, values);
			db.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return row;
	}

	/**
	 * This method used to update Fixtures Record in table tbl_fixtures_info
	 * 
	 * @param values
	 *            is an Object of ContentValues which contains new fixtures data
	 * @param fid
	 *            is Fixtures Id
	 * @return the number of rows affected
	 */
	public int updateFixtures(ContentValues values, String fid) {
		int row = 0;
		try {
			SQLiteDatabase db = this.getWritableDatabase();
			row = db.update(TABLE_FIXTURES_INFO, values, KEY_FIXTURE_ID + "="
					+ fid, null);
			db.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return row;
	}

	/**
	 * This method used to delete all Survey Record from table tbl_survey_info
	 * which has sid
	 * 
	 * @param sid
	 *            SurveyId
	 * 
	 * @return number of deleted rows, 0 otherwise.
	 */
	public int deleteSurvey(String sid) {
		int row = 0;
		try {
			SQLiteDatabase db = this.getWritableDatabase();
			row = db.delete(TABLE_SURVEY_INFO, KEY_SID + "=" + sid, null);
			db.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return row;
	}

	/**
	 * This method used to delete Floor Record from tbl_floor_info which has fid
	 * and it also delete all Area record from tbl_area_info which has fid
	 * reference.
	 * 
	 * @param fid
	 *            Floor Id
	 * 
	 * @return number of deleted row
	 */
	public int deleteFloorWithChiledRecord(String fid) {
		int rowss = 0;
		SQLiteDatabase db = this.getReadableDatabase();
		String selectAreaQuery = "Select a_id from " + TABLE_AREA_INFO
				+ " where " + KEY_FID + "=" + fid;
		try {
			Cursor aCursor = db.rawQuery(selectAreaQuery, null);
			if (aCursor != null && aCursor.getCount() > 0) {
				aCursor.moveToFirst();
				for (int j = 0; j < aCursor.getCount(); j++) {
					int row = db.delete(TABLE_FIXTURES_INFO, KEY_AID + "="
							+ aCursor.getString(0), null);
					Log.d("AID " + aCursor.getString(0)
							+ " No of Fix Deleted: ", row + "");
					aCursor.moveToNext();
				}

				aCursor.close();
			}
			db.delete(TABLE_AREA_INFO, KEY_FID + "=" + fid, null);
			db.delete(TABLE_FLOOR_INFO, KEY_FID + "=" + fid, null);
			db.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rowss;
	}

	/**
	 * This method used to deleted Area record from tbl_area_info which has aid
	 * and also delete all fixture record from tbl_fixtures_info which has aid
	 * reference
	 * 
	 * @param aid
	 *            Area Id
	 * 
	 * @return number of delete area record
	 */
	public int deleteAreaWithChiledRecord(String aid) {
		int rowss = 0;
		SQLiteDatabase db = this.getReadableDatabase();
		try {
			db.delete(TABLE_FIXTURES_INFO, KEY_AID + "=" + aid, null);
			rowss = db.delete(TABLE_AREA_INFO, KEY_AID + "=" + aid, null);
			db.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rowss;
	}

	/**
	 * This method used to delete fixture record from tbl_fixtures_info which
	 * has fixId
	 * 
	 * @param fixId
	 *            Fixture Id
	 * 
	 * @return number of deleted row
	 * 
	 */
	public int deleteFixture(String fixId) {
		int rowss = 0;
		SQLiteDatabase db = this.getReadableDatabase();
		try {
			db.delete(TABLE_FIXTURES_INFO, KEY_FIXTURE_ID + "=" + fixId, null);
			db.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rowss;
	}

	/**
	 * This method used to delete all Survey Record from table tbl_survey_info
	 * which has sid and delete record from tbl_flor_info, tbl_area_info and
	 * tbl_fixtures_info which contains its reference
	 * 
	 * @param sid
	 *            SurveyId
	 * 
	 * @return the number of rows affected, 0 otherwise.
	 */
	public int deleteSurveyWithChiledRecord(String sid) {
		int rowss = 0;

		SQLiteDatabase db = this.getReadableDatabase();
		String selectQuery = "Select f_id from " + TABLE_FLOOR_INFO + " where "
				+ KEY_SID + "=" + sid;
		String selectAreaQuery = "Select a_id from " + TABLE_AREA_INFO
				+ " where " + KEY_FID + "=";
		try {
			Cursor fCursor = db.rawQuery(selectQuery, null);
			if (fCursor != null && fCursor.getCount() > 0) {
				fCursor.moveToFirst();
				for (int i = 0; i < fCursor.getCount(); i++) {
					String areaQuery = selectAreaQuery + fCursor.getString(0);
					Cursor aCursor = db.rawQuery(areaQuery, null);

					if (aCursor != null && aCursor.getCount() > 0) {
						aCursor.moveToFirst();
						for (int j = 0; j < aCursor.getCount(); j++) {
							int row = db.delete(TABLE_FIXTURES_INFO, KEY_AID
									+ "=" + aCursor.getString(0), null);
							Log.d("AID " + aCursor.getString(0)
									+ " No of Fix Deleted: ", row + "");
							aCursor.moveToNext();
						}
						aCursor.close();
					}
					int rows = db.delete(TABLE_AREA_INFO, KEY_FID + "="
							+ fCursor.getString(0), null);
					Log.d("FID " + fCursor.getString(0)
							+ " No of Area Deleted: ", rows + "");
					fCursor.moveToNext();
				}
				fCursor.close();
			}
			int rows = db.delete(TABLE_FLOOR_INFO, KEY_SID + "=" + sid, null);
			Log.d("SID " + sid + " No of Floor Deleted: ", rows + "");
			rowss = db.delete(TABLE_SURVEY_INFO, KEY_SID + "=" + sid, null);
			Log.d("SID " + sid + " No of Survey Deleted ", rowss + "");
			db.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rowss;
	}

	/**
	 * This method used to update Survey Record in table tbl_survey_info
	 * 
	 * @param values
	 *            is an Object of ContentValues which contains new Survey data
	 * @param sid
	 *            is Survey Id
	 * @return the number of rows affected
	 */
	public int updateSurvey(ContentValues values, String sid) {
		int row = 0;
		try {
			SQLiteDatabase db = this.getWritableDatabase();
			row = db.update(TABLE_SURVEY_INFO, values, KEY_SID + "=" + sid,
					null);
			db.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return row;
	}

	/**
	 * This method used to add new Floor Record in table tbl_floor_info
	 * 
	 * @param values
	 *            is an Object of ContentValues which contains new Floor data
	 * 
	 * @return the newly inserted row id , or -1 if an error occurred default is
	 *         return is 0
	 */
	public double addNewFloor(ContentValues values) {
		double row = 0;
		try {
			SQLiteDatabase db = this.getWritableDatabase();
			row = (int) db.insert(TABLE_FLOOR_INFO, null, values);
			db.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return row;
	}

	/**
	 * This method used to update Floor table record which has fid.
	 * 
	 * @param values
	 *            contains data which you want to update
	 * 
	 * @param fid
	 *            Floor Id
	 * 
	 * @return row number
	 */
	public int updateFloor(ContentValues values, String fid) {
		int row = 0;
		try {
			SQLiteDatabase db = this.getWritableDatabase();
			row = db.update(TABLE_FLOOR_INFO, values, KEY_FID + "=" + fid, null);
			db.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return row;
	}

	/**
	 * This method used to update Area record which has aid.
	 * 
	 * @param values
	 *            contains data which you want to update
	 * 
	 * @param aid
	 *            Area Id
	 * 
	 * @return row number
	 */
	public int updateArea(ContentValues values, String aid) {
		int row = 0;
		try {
			SQLiteDatabase db = this.getWritableDatabase();
			row = db.update(TABLE_AREA_INFO, values, KEY_AID + "=" + aid, null);
			db.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return row;
	}

	/**
	 * This method used to add new Area Record in table tbl_area_info
	 * 
	 * @param values
	 *            is an Object of ContentValues which contains new Area data
	 * 
	 * @return the newly inserted row id , or -1 if an error occurred default is
	 *         return is 0
	 */
	public double addArea(ContentValues values) {
		double row = 0;
		try {
			SQLiteDatabase db = this.getWritableDatabase();
			row = (int) db.insert(TABLE_AREA_INFO, null, values);
			db.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return row;
	}

	/**
	 * This method used to get Survey list data based on status parameter
	 * 
	 * @param status
	 *            should be 1 or 0 means open and closed respectivly
	 * 
	 * @return an object of list of Survey type
	 */
	public ArrayList<Survey> getSurveyList(String status) {
		ArrayList<Survey> surveyList = new ArrayList<Survey>();
		;
		Survey surveyItem;
		SQLiteDatabase db = this.getReadableDatabase();
		String selectQuery = "Select * from " + TABLE_SURVEY_INFO + " where "
				+ KEY_STATUS + "=" + status;
		try {
			Cursor sCursor = db.rawQuery(selectQuery, null);
			if (sCursor != null && sCursor.getCount() > 0) {
				sCursor.moveToFirst();
				for (int i = 0; i < sCursor.getCount(); i++) {
					surveyItem = new Survey(sCursor.getString(0),
							sCursor.getString(1), sCursor.getString(2),
							sCursor.getString(3), sCursor.getString(4),
							sCursor.getString(5), sCursor.getString(6),
							sCursor.getString(7), sCursor.getString(8),
							sCursor.getString(9), sCursor.getString(10),
							sCursor.getString(11), sCursor.getString(12),
							sCursor.getString(13), sCursor.getString(14),
							sCursor.getString(15), sCursor.getString(16),
							sCursor.getString(17), sCursor.getString(18),
							sCursor.getString(19));
					surveyList.add(surveyItem);
					sCursor.moveToNext();
				}
				Log.d("No of Record in Surver: ", "" + surveyList.size());
				sCursor.close();

			}
			db.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return surveyList;
	}

	/**
	 * This method used to get Fixtures List data based on areaid parameter
	 * 
	 * @param areaId
	 *            AreaId
	 * 
	 * @return an object of list of Fixtures type
	 */
	public ArrayList<Fixtures> getFixturesList(String areaId) {
		ArrayList<Fixtures> fixturesList = new ArrayList<Fixtures>();
		;
		Fixtures fixItem;
		SQLiteDatabase db = this.getReadableDatabase();
		String selectQuery = "Select * from " + TABLE_FIXTURES_INFO + " where "
				+ KEY_AID + "=" + areaId;
		try {
			Cursor sCursor = db.rawQuery(selectQuery, null);
			if (sCursor != null && sCursor.getCount() > 0) {
				sCursor.moveToFirst();
				for (int i = 0; i < sCursor.getCount(); i++) {
					fixItem = new Fixtures(sCursor.getString(0),
							sCursor.getString(1), sCursor.getString(2),
							sCursor.getString(3), sCursor.getString(4),
							sCursor.getString(5), sCursor.getString(6),
							sCursor.getString(7), sCursor.getString(8),
							sCursor.getString(9), sCursor.getString(10),
							sCursor.getString(11), sCursor.getString(12),
							sCursor.getString(13), sCursor.getString(14),
							sCursor.getString(15),sCursor.getString(16),
							sCursor.getString(17),sCursor.getString(18),
							sCursor.getString(19));
					fixturesList.add(fixItem);
					sCursor.moveToNext();
				}
				Log.d("No of Record in Surver: ", "" + fixturesList.size());
				sCursor.close();

			}
			db.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return fixturesList;
	}
	
	
	

	/**
	 * This method used to get Survey Detail of specific survey from
	 * tbl_survey_info which has sid
	 * 
	 * @param sid
	 *            Survey Id
	 * 
	 * @return Survey object which contain survey data.
	 * 
	 */
	public Survey getSurveyInfo(String sid) {
		Survey surveyItem = null;
		SQLiteDatabase db = this.getReadableDatabase();
		String selectQuery = "Select * from " + TABLE_SURVEY_INFO + " where "
				+ KEY_SID + "=" + sid;
		try {
			Cursor sCursor = db.rawQuery(selectQuery, null);
			if (sCursor != null && sCursor.getCount() > 0) {
				sCursor.moveToFirst();
				surveyItem = new Survey(sCursor.getString(0),
						sCursor.getString(1), sCursor.getString(2),
						sCursor.getString(3), sCursor.getString(4),
						sCursor.getString(5), sCursor.getString(6),
						sCursor.getString(7), sCursor.getString(8),
						sCursor.getString(9), sCursor.getString(10),
						sCursor.getString(11), sCursor.getString(12),
						sCursor.getString(13), sCursor.getString(14),
						sCursor.getString(15), sCursor.getString(16),
						sCursor.getString(17), sCursor.getString(18),
						sCursor.getString(19));

				sCursor.close();

			}
			db.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return surveyItem;
	}

	/**
	 * This method used to get Floor List data based on sid parameter
	 * 
	 * @param sid
	 *            Survey Id
	 * 
	 * @return an object of list of Floor type
	 */
	public ArrayList<Floor> getFloorList(String sid) {
		ArrayList<Floor> floorList = null;
		int areaCount = 0;
		int fixCount = 0;

		Floor floorItem;
		SQLiteDatabase db = this.getReadableDatabase();
		String selectQuery = "Select * from " + TABLE_FLOOR_INFO + " where "
				+ KEY_SID + "=" + sid;
		String selectArea = "Select a_id from " + TABLE_AREA_INFO + " where "
				+ KEY_FID + "=";

		try {

			Cursor sCursor = db.rawQuery(selectQuery, null);

			if (sCursor != null && sCursor.getCount() > 0) {

				floorList = new ArrayList<Floor>();

				sCursor.moveToFirst();

				for (int i = 0; i < sCursor.getCount(); i++) {

					areaCount = 0;

					fixCount = 0;

					Cursor aCursor = db.rawQuery(
							selectArea + sCursor.getString(0), null);

					if (aCursor != null && aCursor.getCount() > 0) {

						areaCount = aCursor.getCount();

						aCursor.moveToFirst();

						fixCount = 0;

						for (int j = 0; j < aCursor.getCount(); j++) {

							int count = getNumberOfFixtures(aCursor
									.getString(0));

							fixCount = fixCount + count;

							aCursor.moveToNext();

						}
						aCursor.close();
					}
					floorItem = new Floor(sCursor.getString(0),
							sCursor.getString(2), areaCount + "", fixCount + "");
					floorList.add(floorItem);

					sCursor.moveToNext();
				}
				Log.d("No of Record in Floor Table: ", "" + floorList.size());
				sCursor.close();

			}
			db.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return floorList;
	}

	/**
	 * This method used to get Floor List data based on sid parameter
	 * 
	 * @param sid
	 *            Survey Id and floorname
	 * 
	 * @return an object of list of Floor type
	 */
	public ArrayList<Floor> getFloorList(String sid, String floorname) {
		ArrayList<Floor> floorList = null;
		int areaCount = 0;
		int fixCount = 0;

		Floor floorItem;
		SQLiteDatabase db = this.getReadableDatabase();
		String selectQuery = "Select * from " + TABLE_FLOOR_INFO + " where "
				+ KEY_SID + "=" + sid + " and " + KEY_FLOOR_DESCRIPTION + "='"
				+ floorname + "'";
		String selectArea = "Select a_id from " + TABLE_AREA_INFO + " where "
				+ KEY_FID + "=";

		try {

			Cursor sCursor = db.rawQuery(selectQuery, null);

			if (sCursor != null && sCursor.getCount() > 0) {

				floorList = new ArrayList<Floor>();

				sCursor.moveToFirst();

				for (int i = 0; i < sCursor.getCount(); i++) {

					areaCount = 0;

					fixCount = 0;

					Cursor aCursor = db.rawQuery(
							selectArea + sCursor.getString(0), null);

					if (aCursor != null && aCursor.getCount() > 0) {

						areaCount = aCursor.getCount();

						aCursor.moveToFirst();

						fixCount = 0;

						for (int j = 0; j < aCursor.getCount(); j++) {

							int count = getNumberOfFixtures(aCursor
									.getString(0));

							fixCount = fixCount + count;

							aCursor.moveToNext();

						}
						aCursor.close();
					}
					floorItem = new Floor(sCursor.getString(0),
							sCursor.getString(2), areaCount + "", fixCount + "");
					floorList.add(floorItem);

					sCursor.moveToNext();
				}
				Log.d("No of Record in Floor Table: ", "" + floorList.size());
				sCursor.close();
			}
			db.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return floorList;
	}

	/**
	 * This method used to get List of Area from tble_area_info table which has
	 * fid.
	 * 
	 * @param fid
	 *            Fixture Id
	 * 
	 * @return list of Area.
	 * 
	 */
	public ArrayList<Area> getAreaList(String fid) {
		ArrayList<Area> areaList = null;
		Area areaItem;
		SQLiteDatabase db = this.getReadableDatabase();
		String selectQuery = "Select * from " + TABLE_AREA_INFO + " where "
				+ KEY_FID + "=" + fid;
		try {

			Cursor sCursor = db.rawQuery(selectQuery, null);

			if (sCursor != null && sCursor.getCount() > 0) {

				areaList = new ArrayList<Area>();

				sCursor.moveToFirst();

				for (int i = 0; i < sCursor.getCount(); i++) {

					int count = getNumberOfFixtures(sCursor.getString(0));

					areaItem = new Area(sCursor.getString(0), fid,
							sCursor.getString(2), count + "");

					areaList.add(areaItem);

					sCursor.moveToNext();
				}
				Log.d("No of Record in Floor Table: ", "" + areaList.size());
				sCursor.close();
			}
			db.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return areaList;
	}
	
	/////////////////////
	
	public ArrayList<Area> getAreaListById(String areaid) {
		ArrayList<Area> areaList = null;
		Area areaItem;
		SQLiteDatabase db = this.getReadableDatabase();
		String selectQuery = "Select * from " + TABLE_AREA_INFO + " where "
				+ KEY_AID + "=" + areaid;
		try {

			Cursor sCursor = db.rawQuery(selectQuery, null);

			if (sCursor != null && sCursor.getCount() > 0) {

				areaList = new ArrayList<Area>();

				sCursor.moveToFirst();

				for (int i = 0; i < sCursor.getCount(); i++) {

					int count = getNumberOfFixtures(sCursor.getString(0));

					areaItem = new Area(sCursor.getString(0),sCursor.getString(1),
							sCursor.getString(2), count + "");

					areaList.add(areaItem);

					sCursor.moveToNext();
				}
				Log.d("No of Record in Floor Table: ", "" + areaList.size());
				sCursor.close();
			}
			db.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return areaList;
	}
	
	
	
	/////////////////////////

	/**
	 * This method used to calculate number of floor,number of area and number
	 * of fixture count in perticular survey.
	 * 
	 * @param sid
	 *            SurveyID
	 * 
	 * @return return an object of HasMap
	 */
	public HashMap<String, String> GetCount(String sid) {
		HashMap<String, String> hasMap = new HashMap<String, String>();
		SQLiteDatabase db = this.getReadableDatabase();
		String selectQuery = "Select f_id from " + TABLE_FLOOR_INFO + " where "
				+ KEY_SID + "=" + sid;
		String selectAreaQuery = "Select a_id from " + TABLE_AREA_INFO
				+ " where " + KEY_FID + "=";
		String selectFixtureQuery = "Select fix_id,fix_count from "
				+ TABLE_FIXTURES_INFO + " where " + KEY_AID + "=";
		int floorCount = 0;
		int areaCount = 0;
		int fixCount = 0;
		int totalfixCount = 0;
		try {
			Cursor fCursor = db.rawQuery(selectQuery, null);

			if (fCursor != null && fCursor.getCount() > 0) {
				floorCount = fCursor.getCount();
				fCursor.moveToFirst();

				for (int i = 0; i < fCursor.getCount(); i++) {

					String areaQuery = selectAreaQuery + fCursor.getString(0);
					Cursor aCursor = db.rawQuery(areaQuery, null);

					if (aCursor != null && aCursor.getCount() > 0) {

						areaCount = areaCount + aCursor.getCount();
						aCursor.moveToFirst();

						for (int j = 0; j < aCursor.getCount(); j++) {

							String fixQuery = selectFixtureQuery
									+ aCursor.getString(0);
							Log.d("Fix Query:", fixQuery);
							Cursor fixCursor = db.rawQuery(fixQuery, null);
							if (fixCursor != null && fixCursor.getCount() > 0) {
								Log.d("Fix Cursor Count:", fixCursor.getCount()
										+ "");
								fixCount = fixCount + fixCursor.getCount();
								fixCursor.moveToFirst();
								for (int k = 0; k < fixCursor.getCount(); k++) {
									int value = Integer.parseInt(fixCursor
											.getString(1));
									Log.d("Count Value", "" + value);
									totalfixCount = totalfixCount + value;
									fixCursor.moveToNext();
								}
							}
							aCursor.moveToNext();
						}
						aCursor.close();
					}
					fCursor.moveToNext();
				}
				fCursor.close();
			}
			db.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		hasMap.put("floor", "" + floorCount);
		hasMap.put("area", "" + areaCount);
		hasMap.put("fixture", "" + fixCount);
		hasMap.put("totalfixcount", "" + totalfixCount);

		return hasMap;
	}

	// public String getImagePath(String sid){
	// SQLiteDatabase db=this.getReadableDatabase();
	// Cursor imageCursor=db.query(TABLE_IMAGES,new
	// String[]{KEY_PATH},KEY_SID+"="+sid,null,null,null,null);
	// if(imageCursor!=null&&imageCursor.getCount()>0){
	// imageCursor.moveToFirst();
	// Log.d("Image Path",imageCursor.getString(0));
	// return imageCursor.getString(0);
	// }
	// return null;
	// }

	/**
	 * This method used to get Number of Survey which Scheduled date is Today
	 * date
	 * 
	 * @return count number
	 */
	@SuppressLint("SimpleDateFormat")
	public int getTodayScheduled() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
		int count = 0;
		try {
			SQLiteDatabase db = this.getReadableDatabase();
			Cursor cursor = db.query(TABLE_SURVEY_INFO,
					new String[] { KEY_SCHEDULED }, KEY_STATUS + "=1", null,
					null, null, null, null);
			if (cursor != null && cursor.getCount() > 0) {
				cursor.moveToFirst();
				Date scheduledDate;
				try {
					for (int i = 0; i < cursor.getCount(); i++) {
						scheduledDate = dateFormat.parse(cursor.getString(0));
						String currentdate = dateFormat.format(new Date());
						Date todayDate = dateFormat.parse(currentdate);
						if (scheduledDate.compareTo(todayDate) < 0) {
						} else if (scheduledDate.compareTo(todayDate) > 0) {
						} else {
							Log.d("DatabaseHandle", "Scheduled Date is equal");
							count++;
						}
					}
				} catch (ParseException e) {
					Log.e("DatabaseHandler", e.getMessage());
					e.printStackTrace();
				}
				cursor.close();
			}
			db.close();
		} catch (SQLException e) {
			Log.e("DatabaseHandler", e.getMessage());
			e.printStackTrace();

		}
		return count;
	}

	/**
	 * This method used to get HTML format String of all information of Survey.
	 * This method also responsible for creating HTML file and CSV file of
	 * Survey detail.
	 * 
	 * @param sid
	 *            Survey Id
	 * 
	 * @return StringBuilder
	 * 
	 */
	@SuppressLint("DefaultLocale")
	public StringBuilder getSurveyStringTemplet(String sid) {
		
		StringBuilder htmlFormate = new StringBuilder();
		StringBuilder csvFormate = new StringBuilder();
		StringBuilder bodyString = new StringBuilder();
		
		String buildingName = null;
		String floorName;
		Constant.CSV_URI = null;
		Constant.HTML_URI = null;
		Constant.FIXTURE_IMAGE_LIST = new ArrayList<String>();

		String csvColumn = "\"Building Name\",\"Address\",\"Building Dropbox Image\",\"Floor\",\"Location\",\"Type\",\"Code\",\"Style\",\"Mount\",\"Control\",\"Height\",\"Total Hours\",\"Option\",\"Description\",\"Count\",\"Hours per Day\",\"Days per Week\",\"Ballast Type\",\"Ballast Factor\",\"Bulbs per Watts\",\"Fixture Dropbox Image\"\n";
		csvFormate.append(csvColumn);

		htmlFormate
				.append("<!DOCTYPE html><html><body><table align=\"left\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"font-family: Helvetica,Arial,sans-serif; width: 100%; height: 100%; background: #e6e6e6; color: #5e5e5e; border-collapse: separate; border: 0; margin: 0px; padding: 20px 0px 20px 0px;\"><tbody style=\"vertical-align: top;\">");
		
		bodyString
		.append("<!DOCTYPE html><html><body><table align=\"left\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"font-family: Helvetica,Arial,sans-serif; width: 100%; height: 100%; background: #e6e6e6; color: #5e5e5e; border-collapse: separate; border: 0; margin: 0px; padding: 20px 0px 20px 0px;\"><tbody style=\"vertical-align: top;\">");

		SQLiteDatabase db = this.getReadableDatabase();

		String selectSurveyQuery = "Select * from " + TABLE_SURVEY_INFO
				+ " where " + KEY_SID + "=" + sid;
		String selectQuery = "Select f_id,floor_description from "
				+ TABLE_FLOOR_INFO + " where " + KEY_SID + "=" + sid;
		String selectAreaQuery = "Select a_id,location_name from "
				+ TABLE_AREA_INFO + " where " + KEY_FID + "=";
		String selectFixtureQuery = "Select * from " + TABLE_FIXTURES_INFO
				+ " where " + KEY_AID + "=";

		try {
			Cursor sCursor = db.rawQuery(selectSurveyQuery, null);

			if (sCursor != null && sCursor.getCount() > 0) {
				sCursor.moveToFirst();
				buildingName = sCursor.getString(1);
				String description = sCursor.getString(2) + " "
						+ sCursor.getString(3) + " " + sCursor.getString(4)
						+ " " + sCursor.getString(5) + " "
						+ sCursor.getString(6);

				String imageFilePath = sCursor.getString(19);
				String dropboxLink = DropboxHelper.GetLink(imageFilePath);
				if (dropboxLink == null) {
					dropboxLink = "";
				}

				htmlFormate
						.append("<tr height=\"200\" style=\"height: 200px;\"><td colspan=\"1\" border=\"0\">&nbsp;</td><td colspan=\"1\" border=\"0\" width=\"20\" style=\"width: 20px\">&nbsp;</td><td colspan=\"1\" border=\"0\" width=\"700\" style=\"width: 700px;\">"
								+ "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"width: 100%; border-collapse: separate; border: 0; margin: 0px; padding: 0px 0px 30px 0px;\"><tbody><tr height=\"180\" style=\"height: 180px; vertical-align: top;\">"
								+ "<td colspan=\"1\" border=\"0\" width=\"550\" style=\"width: 550px; padding: 35px 0px 0px 10px;\"><table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"width: 100%; border-collapse: separate; border: 0; margin: 0px; padding: 0px 0px 0px 0px;\"><tbody><tr><td> <span style=\"text-decoration: none; color: #2f2f36; font-weight: bold; font-size: 38px; line-height: 44px;\">");
				htmlFormate.append(buildingName);
				htmlFormate
						.append("</span></td></tr><tr><br><td style=\"padding: 0px 0px 0px 3px;\"><span style=\"text-decoration: none; color: #a0a0a5; font-weight: normal; font-size: 16px; line-height: 24px;\">");

				htmlFormate.append(description);

				
				bodyString
				.append("<tr height=\"200\" style=\"height: 200px;\"><td colspan=\"1\" border=\"0\">&nbsp;</td><td colspan=\"1\" border=\"0\" width=\"20\" style=\"width: 20px\">&nbsp;</td><td colspan=\"1\" border=\"0\" width=\"700\" style=\"width: 700px;\">"
						+ "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"width: 100%; border-collapse: separate; border: 0; margin: 0px; padding: 0px 0px 30px 0px;\"><tbody><tr height=\"180\" style=\"height: 180px; vertical-align: top;\">"
						+ "<td colspan=\"1\" border=\"0\" width=\"550\" style=\"width: 550px; padding: 35px 0px 0px 10px;\"><table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"width: 100%; border-collapse: separate; border: 0; margin: 0px; padding: 0px 0px 0px 0px;\"><tbody><tr><td> <span style=\"text-decoration: none; color: #2f2f36; font-weight: bold; font-size: 38px; line-height: 44px;\">");
				bodyString.append(buildingName);
				bodyString
					.append("</span></td></tr><tr><br><td style=\"padding: 0px 0px 0px 3px;\"><span style=\"text-decoration: none; color: #a0a0a5; font-weight: normal; font-size: 16px; line-height: 24px;\">");

				bodyString.append(description);

		
				if (dropboxLink != null && !dropboxLink.equals("")) {
					htmlFormate.append("<br/>");
					htmlFormate
							.append("<span><b>Dropbox image:&nbsp;&nbsp;</b>");
					htmlFormate.append(dropboxLink);
					htmlFormate.append("</span>");
					htmlFormate.append("<br/>");
				}

				htmlFormate.append("</td></tr></tbody></table></td></tr>");
				bodyString.append("</td></tr></tbody></table></td></tr>");
				
				Cursor fCursor = db.rawQuery(selectQuery, null);
				if (fCursor != null && fCursor.getCount() > 0) {
					fCursor.moveToFirst();
					for (int i = 0; i < fCursor.getCount(); i++) {
						htmlFormate
								.append("<tr style=\"vertical-align: top;\"><td colspan=\"2\" border=\"0\" style=\"padding: 5px 0px 0px 0px;\"><table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"width: 100%; height: 100%; border-collapse: separate; border: 0; margin: 0px; padding: 5px; background-color: #fafafa;\"><tbody><tr height=\"32\" style=\"height: 42px; color: #fff; background-color: #00bfff;\"><br><td colspan=\"1\" width=\"80\" style=\"font-size: 18px; padding: 0px 0px 0px 10px;\">FLOOR:</td><td colspan=\"1\" style=\"font-size: 18px; padding: 0px 0px 0px 10px;\"><span>&nbsp;&nbsp;");
						floorName = fCursor.getString(1);
						htmlFormate.append(floorName);
						htmlFormate.append("</td></tr>");
						Log.d("Floor  Name", fCursor.getString(1));
						String areaQuery = selectAreaQuery
								+ fCursor.getString(0);
						Cursor aCursor = db.rawQuery(areaQuery, null);
						if (aCursor != null && aCursor.getCount() > 0) {
							aCursor.moveToFirst();
							for (int j = 0; j < aCursor.getCount(); j++) {
								htmlFormate
										.append(" <tr><td colspan=\"2\"><table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"color: #aaadb4; width: 100%; height: 100%; border-collapse: separate; border: 0; margin: 0px; padding: 0px; background-color: #fafafa; padding: 0px 0px 10px 0px;\"><tbody><tr height=\"32\" style=\"height: 32px;\"><br><td colspan=\"1\" width=\"80\" style=\"padding: 0px 0px 0px 10px;\">AREA:</td><td colspan=\"1\" style=\"color: deepskyblue; padding: 0px 0px 0px 10px;\"><span>&nbsp;&nbsp;");
								htmlFormate.append(aCursor.getString(1));
								htmlFormate.append("</span></td></tr>");
								Log.d("@@@@@@@@@",
										"******Area Record***********");
								Log.d("Area  Name", aCursor.getString(1));

								String fixQuery = selectFixtureQuery
										+ aCursor.getString(0);
								Log.d("Fix Query:", fixQuery);
								Cursor fixCursor = db.rawQuery(fixQuery, null);

								if (fixCursor != null
										&& fixCursor.getCount() > 0) {

									fixCursor.moveToFirst();
									Log.d("@@@@@@@@@",
											"******Fixtures Record***********");
									htmlFormate
											.append("<br><tr><td colspan=\"2\" style=\"border-top: 1px solid #aaadb4;\"><table align=\"left\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"font-weight: normal; font-size: 10px; line-height: 16px; width: 100%; height: 100%; border-collapse: separate; border: 0; margin: 0px; padding: 0px; background-color: #f1f1f1;\"><thead align=\"left\" style=\"background-color: #e5e5e5; color: #5e5e5e;\"><tr><th style=\"padding: 0px 0px 0px 5px;\">&nbsp;COUNT</th><th>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;TYPE</th><th>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;CODE</th><th>&nbsp;&nbsp;&nbsp;&nbsp;STYLE</th><th>&nbsp;&nbsp;&nbsp;&nbsp;MOUNT</th><th>&nbsp;&nbsp;&nbsp;&nbsp;CONTROL</th><th>&nbsp;&nbsp;&nbsp;&nbsp;HEIGHT</th><th>"
													+ "&nbsp;&nbsp;&nbsp;&nbsp;HOURS</th><th>&nbsp;&nbsp;&nbsp;&nbsp;OPTION</th><th>&nbsp;&nbsp;&nbsp;&nbsp;NOTE</th><th>&nbsp;&nbsp;&nbsp;&nbsp;BALLAST_TYPE</th><th>&nbsp;&nbsp;&nbsp;&nbsp;BALLAST_FACTOR</th><th>&nbsp;&nbsp;&nbsp;&nbsp;BULBS_PER_WATTS</th><th>&nbsp;&nbsp;&nbsp;&nbsp;FIXTURE_DROPBOX_IMAGE</th><th>&nbsp;</th></tr></thead> <tbody style=\"color: #5e5e5e;\">");
									for (int k = 0; k < fixCursor.getCount(); k++) {

										String hours = fixCursor.getString(12);// +" ("+fixCursor.getString(10)+"-"+fixCursor.getString(11)+")";
										String imagePath = fixCursor
												.getString(13);
										String dropboxFixtureLink = DropboxHelper
												.GetLink(imagePath);
										if (dropboxFixtureLink == null) {
											dropboxFixtureLink = "";
										}
										String row = "<br><tr><td style=\"padding: 0px 0px 0px 5px;\">&nbsp;&nbsp;"
												+ fixCursor.getString(3)
												+ "</td><td>&nbsp;&nbsp;&nbsp;"
												+ fixCursor.getString(2)
												+ "</td><td>&nbsp;&nbsp;&nbsp;&nbsp;"
												+ fixCursor.getString(4)
												+ "</td><td>&nbsp;&nbsp;&nbsp;&nbsp;"
												+ fixCursor.getString(5)
												+ "</td><td>&nbsp;&nbsp;&nbsp;&nbsp;"
												+ fixCursor.getString(6)
												+ "</td><td>&nbsp;&nbsp;&nbsp;&nbsp;"
												+ fixCursor.getString(7)
												+ "</td><td>&nbsp;&nbsp;&nbsp;&nbsp;"
												+ fixCursor.getString(9)
												+ "</td><td>&nbsp;&nbsp;&nbsp;&nbsp;"
												+ fixCursor.getString(12)
												+ " ("
												+ fixCursor.getString(10)
												+ "-"
												+ fixCursor.getString(11)
												+ ")"
												+ "</td><td>&nbsp;&nbsp;&nbsp;&nbsp;"
												+ fixCursor.getString(8)
												+ "</td><td>&nbsp;&nbsp;&nbsp;&nbsp;"
												+ fixCursor.getString(14)
												+ "</td><td>&nbsp;&nbsp;&nbsp;&nbsp;"
												+ fixCursor.getString(15)
												+ "</td><td>&nbsp;&nbsp;&nbsp;&nbsp;"
												+ fixCursor.getString(16)
												+ "</td><td>&nbsp;&nbsp;&nbsp;&nbsp;"
												+ fixCursor.getString(19)
												+ "</td><td>&nbsp;&nbsp;&nbsp;&nbsp;"
												+ dropboxFixtureLink
												+ "</td><td>&nbsp;</td></tr>";
										htmlFormate.append(row);

										// csvFormate.append("\""
										// +buildingName.toUpperCase()+"\",\""
										// +dropboxLink.toUpperCase() + "\",\""
										// +floorName + "\",\"" +
										// aCursor.getString(1).toUpperCase() +
										// "\",\"" +
										// fixCursor.getString(2).toUpperCase()
										// + "\",\"" +
										// fixCursor.getString(4).toUpperCase()
										// + "\",\"" +
										// fixCursor.getString(5).toUpperCase()
										// + "\",\"" +
										// fixCursor.getString(6).toUpperCase()
										// + "\",\"" +
										// fixCursor.getString(7).toUpperCase()
										// + "\",\"" + fixCursor.getString(9)+
										// "\",\"" +hours+ "\",\"" +
										// fixCursor.getString(8).toUpperCase()
										// + "\",\"" +
										// fixCursor.getString(14).toUpperCase()
										// + "\",\"" +
										// fixCursor.getString(3).toUpperCase()
										// + "\",\"" +
										// fixCursor.getString(10).toUpperCase()
										// + "\",\"" +
										// fixCursor.getString(11).toUpperCase()
										// + "\",\"" +
										// dropboxFixtureLink.toUpperCase() +
										// "\" \n");
										String csvRowString = "\""
												+ buildingName.toUpperCase().replace('\"',' ')
												+ "\",\""
												+ description.toUpperCase().replace('\"',' ')
												+ "\",\""
												+ dropboxLink.toUpperCase().replace('\"',' ')
												+ "\",\""
												+ floorName.replace('\"',' ')
												+ "\",\""
												+ aCursor.getString(1)
														.toUpperCase().replace('\"',' ')
												+ "\",\""
												+ fixCursor.getString(2)
														.toUpperCase().replace('\"',' ')
												+ "\",\""
												+ fixCursor.getString(4)
														.toUpperCase().replace('\"',' ')
												+ "\",\""
												+ fixCursor.getString(5)
														.toUpperCase().replace('\"',' ')
												+ "\",\""
												+ fixCursor.getString(6)
														.toUpperCase().replace('\"',' ')
												+ "\",\""
												+ fixCursor.getString(7)
														.toUpperCase().replace('\"',' ')
												+ "\",\""
												+ fixCursor.getString(9).replace('\"',' ')
												+ "\",\""
												+ hours
												+ "\",\""
												+ fixCursor.getString(8)
														.toUpperCase().replace('\"',' ')
												+ "\",\""
												+ fixCursor.getString(14)
														.toUpperCase().replace('\"',' ')
												+ "\",\""
												+ fixCursor.getString(3)
														.toUpperCase().replace('\"',' ')
												+ "\",\""
												+ fixCursor.getString(10)
														.toUpperCase().replace('\"',' ')
												+ "\",\""
												+ fixCursor.getString(11)
														.toUpperCase().replace('\"',' ')
												+ "\",\""
												+ fixCursor.getString(15)
														.toUpperCase().replace('\"',' ')
												+ "\",\""
												+ fixCursor.getString(16)
														.toUpperCase().replace('\"',' ')
												+ "\",\""
												+ fixCursor.getString(19)
														.toUpperCase().replace('\"',' ')
												+ "\",\""
												+ dropboxFixtureLink
														.toUpperCase().replace('\"',' ')
												+ "\" \n";
										
										
										csvFormate.append(csvRowString);

										if (imagePath != null
												&& !imagePath.equals("")) {
											Constant.FIXTURE_IMAGE_LIST
													.add(imagePath);
										}

										fixCursor.moveToNext();
									}
									htmlFormate
											.append("</tbody></table></td></tr>");
									fixCursor.close();
								} else {
									csvFormate.append("\""
											+ buildingName.toUpperCase().replace('\"',' ')
											+ "\",\""
											+ description.toUpperCase().replace('\"',' ')
											+ "\",\""
											+ dropboxLink.toUpperCase().replace('\"',' ')
											+ "\",\""
											+ floorName.replace('\"',' ')
											+ "\",\""
											+ aCursor.getString(1)
													.toUpperCase().replace('\"',' ') + "\"\n");
								}
								htmlFormate
										.append("</tbody></table></td></tr>");
								aCursor.moveToNext();
							}
							aCursor.close();
						} else {
							csvFormate.append("\"" + buildingName.toUpperCase().replace('\"',' ')
									+ "\",\"" + description.toUpperCase().replace('\"',' ')
									+ "\",\"" + dropboxLink.toUpperCase().replace('\"',' ')
									+ "\",\"" + floorName.replace('\"',' ') + "\"\n");
						}
						htmlFormate.append("</tbody></table></td></tr>");
						fCursor.moveToNext();
					}
					fCursor.close();
				} else {
					csvFormate.append("\"" + buildingName.toUpperCase().replace('\"',' ')
							+ "\",\"" + description.toUpperCase().replace('\"',' ') + "\",\""
							+ dropboxLink.toUpperCase().replace('\"',' ') + "\"\n");
				}
				htmlFormate
						.append("</tbody></table></td><td colspan=\"1\" border=\"0\" width=\"20\" style=\"width: 20px\">&nbsp;</td><td colspan=\"1\" border=\"0\">&nbsp;</td></tr>");

				bodyString
				.append("</tbody></table></td><td colspan=\"1\" border=\"0\" width=\"20\" style=\"width: 20px\">&nbsp;</td><td colspan=\"1\" border=\"0\">&nbsp;</td></tr>");
				
				sCursor.close();
			}

			String dirNamePath = Constant.SD_CARD_PATH + "/"
					+ Constant.SURVEY_NAME_PATH + "_" + Constant.SURVEY_ID;
			File dirFile = new File(dirNamePath);
			if (!dirFile.exists()) {
				dirFile.mkdir();
			}

			String path = dirFile.getPath() + "/" + Constant.SURVEY_NAME_PATH
					+ "_" + Constant.SURVEY_ID + ".csv";

			Log.d("CSV Path:::::::::::", path);

			File file = new File(path);
			try {
				FileOutputStream out = new FileOutputStream(file);
				out.write(csvFormate.toString().getBytes());
				out.close();

			} catch (IOException e) {
				Log.e("BROKEN", "Could not write file " + e.getMessage());
			}
			Constant.CSV_URI = Uri.fromFile(file);

			htmlFormate.append("</tbody></table>");
			bodyString.append("</tbody></table>");
			
			String osVersion = System.getProperty("os.version");
			String device = android.os.Build.DEVICE;
			String product = android.os.Build.PRODUCT;
			String deviceName = android.os.Build.MODEL;
			String deviceMan = android.os.Build.MANUFACTURER;
			String macAddres = getMACAddress();
			if (macAddres == null) {
				macAddres = "";
			}
			String imeiNum = getIMEInumber();

			String deviceInfoTable = "<br/><br/><br/><h2>Device Info:</h2><table><tbody><tr><td>Device:&nbsp;&nbsp;</td><td>"
					+ device
					+ "</td><td style=\"width:20px\">&nbsp;&nbsp;&nbsp;&nbsp;</td></tr><tr><td>Model:&nbsp;&nbsp;</td><td>"
					+ deviceName
					+ "</td><td style=\"width:20px\">&nbsp;&nbsp;&nbsp;&nbsp;</td></tr><tr><td>OS:&nbsp;&nbsp;</td><td>"
					+ osVersion
					+ "</td><td style=\"width:20px\">&nbsp;&nbsp;&nbsp;&nbsp;</td></tr><tr><td>Manufacturer:&nbsp;&nbsp;</td><td>"
					+ deviceMan
					+ "</td><td style=\"width:20px\">&nbsp;&nbsp;&nbsp;&nbsp;</td></tr><tr><td>Product:&nbsp;&nbsp;</td><td>"
					+ product
					+ "</td><td style=\"width:20px\">&nbsp;&nbsp;&nbsp;&nbsp;</td></tr><tr><td>IMEI:&nbsp;&nbsp;</td><td>"
					+ imeiNum
					+ "</td><td style=\"width:20px\">&nbsp;&nbsp;&nbsp;&nbsp;</td></tr><tr><td>MAC Address:&nbsp;&nbsp;</td><td>"
					+ macAddres
					+ "</td><td style=\"width:20px\">&nbsp;&nbsp;&nbsp;&nbsp;</td></tr></tbody></table>";

			htmlFormate.append(deviceInfoTable);
			bodyString.append(deviceInfoTable);

			htmlFormate.append("</body></html>");
			bodyString.append("</body></html>");

			String pathHTML = dirFile.getPath() + "/"
					+ Constant.SURVEY_NAME_PATH + "_" + Constant.SURVEY_ID
					+ ".html";

			File htmlFile = new File(pathHTML);
			try {
				FileOutputStream out = new FileOutputStream(htmlFile);
				out.write(htmlFormate.toString().getBytes());
				out.close();
			} catch (IOException e) {
				Log.e("BROKEN", "Could not write file " + e.getMessage());
			}
			Constant.HTML_URI = Uri.fromFile(htmlFile);

		} catch (SQLException e) {
			e.printStackTrace();
		}
		Log.d("HTML FORMATE", htmlFormate.toString());
		db.close();
		return bodyString;
	}

	private String getIMEInumber() {
		if (_context != null) {
			TelephonyManager TelephonyMgr = (TelephonyManager) _context
					.getSystemService(Context.TELEPHONY_SERVICE);
			String m_deviceId = TelephonyMgr.getDeviceId();
			return m_deviceId;
		}
		return null;
	}

	private String getMACAddress() {
		if (_context != null) {

			WifiManager m_wm = (WifiManager) _context
					.getSystemService(Context.WIFI_SERVICE);
			String m_wlanMacAdd = m_wm.getConnectionInfo().getMacAddress();
			return m_wlanMacAdd;
		}
		return null;
	}

	public int getNumberOfFixtures(String areaId) {
		SQLiteDatabase db = this.getReadableDatabase();
		String selectQuery = "Select fix_id from " + TABLE_FIXTURES_INFO
				+ " where " + KEY_AID + "=" + areaId;
		Cursor fcursor = db.rawQuery(selectQuery, null);
		int count = 0;
		if (fcursor != null && fcursor.getCount() > 0) {
			count = fcursor.getCount();

			fcursor.close();
		}

		return count;
	}
}
