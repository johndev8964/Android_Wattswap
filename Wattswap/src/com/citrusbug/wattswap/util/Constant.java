package com.citrusbug.wattswap.util;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Environment;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.TextView;

public class Constant {
	
	//APPLICATION PREFERENCE FILE NAME
    public static String PREF_FILE = "PREF_SURVEY_APP";
	
   //APPLICATION CONTEXT OBJECT
    public static Context CONTEXT;
    
   //SD card directory name or path
 	public static String SD_CARD_PATH=Environment.getExternalStorageDirectory().getPath()+"/Wattswap";
 	
   //Preference name to store last survey id
   public static String PREF_LAST_SURVERY_ID="PREF_LAST_SURVERY_ID";
   
 //Preference name to store last survey id
   public static String PREF_LAST_FIXTURES_ID="PREF_LAST_FIXTURES_ID";
 	
 	//Activity object
 	 public static Activity activity;
 		
 //=================== APPLICATION CONSTANT ================================
  	
	public static boolean ISUPDATEMODE=false;
	public static boolean ISFIXUPDATEMODE=false;
	public static boolean ISFLOORUPDATEMODE=false;
	public static boolean ISAREAUPDATEMODE=false;
	
	public static boolean ISFINISH=false;
	public static boolean ISSURVEYDELETED=false;
	public static boolean ISFIXTUREDELETED=false;
	public static boolean IS_RESIZE_ENABLE=true;
	

	public static String SURVEY_NAME="";
	public static String SURVEY_NAME_PATH="";
	public static String FLOOR_NAME_PATH="";
	public static String AREA_NAME_PATH="";
	public static String FIX_NAME_PATH="";
	
	public static String SUPERVISOR_ID = "0";
	public static String SURVEY_ID="0";
	public static String FLOOR_ID="0";
	public static String AREA_ID="0";
	
	public static String FIXTURE_RESPONSE_TYPE_ID="0";
	public static Uri CSV_URI=null;
	
	public static Uri HTML_URI=null;
	
	
	public static ArrayList<String> FIXTURE_IMAGE_LIST=new ArrayList<String>();
	
	public static ViewConfiguration vc =null;
    public static int SWIPE_THRESHOLD=0;
    public static int SWIPE_VELOCITY_THRESHOLD=0;
    
    
	
}