package com.citrusbug.wattswap.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Pref {
	
	public static SharedPreferences sharedPreferences = null;
	
	public static void openPref(){
		
		sharedPreferences = Constant.CONTEXT.getSharedPreferences(Constant.PREF_FILE, Context.MODE_PRIVATE);		
	}
	
	public static String getValue(String key, String defaultValue)
	{
		Pref.openPref();
		String result = Pref.sharedPreferences.getString(key, defaultValue);
		Pref.sharedPreferences = null;
		return result;
	}
	
	public static void setValue(String key, String value)
	{
		Pref.openPref();
		Editor prefsPrivateEditor = Pref.sharedPreferences.edit();
		prefsPrivateEditor.putString(key, value);
		prefsPrivateEditor.commit();
		prefsPrivateEditor = null;
		Pref.sharedPreferences = null;
	}
	
	public static boolean saveFixtureTypeArray(String[] arrList, String arrayName, Context mContext) { 
		Pref.openPref();
		SharedPreferences.Editor editor = Pref.sharedPreferences.edit();
		
		editor.putInt(arrayName +"_size", arrList.length);
		
		for(int i=0;i<arrList.length;i++)
			editor.putString(arrayName + "_" + i, arrList[i]);
		
		Pref.sharedPreferences = null;
		return editor.commit();
	}

	public static String[] loadFixtureTypeArray(String arrayName, Context mContext) {
		Pref.openPref();
		int size = Pref.sharedPreferences.getInt(arrayName + "_size", 0);
		
		String[] arrAns = new String[size];
		
		for(int i = 0;i < size;i++){
			String strItem = Pref.sharedPreferences.getString(arrayName + "_" + i, null);
			
			if(strItem == null) continue;
			
			arrAns[i] = strItem;
		}
		
		Pref.sharedPreferences = null;	
		return arrAns;
	}
}
