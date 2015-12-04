package com.citrusbug.wattswap.util;

import java.io.File;
import com.dropbox.sync.android.DbxAccountManager;

public class DropboxHelper {

	public static int DROPBOX_LINK_REQUEST_CODE =599;
	public static String DROPBOX_API_KEY ="w97du9dspfo6adj";
	public static String DROPBOX_API_SECRET ="ksspyo5se35a7cs";
	
	public static DbxAccountManager mDbxAcctMgr;
	
	public static void SaveToDropbox(String path){
		if(mDbxAcctMgr!=null && mDbxAcctMgr.hasLinkedAccount()){
			new DropboxAsyncUploader(mDbxAcctMgr).execute(path);
		}
	}
	
	public static String GetLink(String path){
	
	try{
		File file = new File(path);
		String fileName = file.getName();
		return "/Apps/Wattswap/" + fileName;
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
}


