package com.citrusbug.wattswap.util;

import java.io.File;
import android.os.AsyncTask;
import android.util.Log;

import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxFile;
import com.dropbox.sync.android.DbxFileSystem;
import com.dropbox.sync.android.DbxPath;

public class DropboxAsyncUploader extends AsyncTask<String, Integer, String> {

	DbxAccountManager mdbxAccountManager;

	public DropboxAsyncUploader(DbxAccountManager dbxAccountManager) {
		mdbxAccountManager = dbxAccountManager;
	}

	@Override
	protected String doInBackground(String... params) {
		if(params == null || params.length<=0){
			return null;
		}
		try {
			for (String path : params) {

				if (mdbxAccountManager != null && path != null
						&& path.length() > 0) {

					DbxFileSystem dbxFs = DbxFileSystem
							.forAccount(mdbxAccountManager.getLinkedAccount());
					File file = new File(path);
					String fileName = file.getName();
					DbxFile testFile = dbxFs.create(new DbxPath(fileName));

					testFile.writeFromExistingFile(file, false);
					testFile.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "Executed";
	}

	@Override
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		//super.onPostExecute(result);
		Log.d("",result);
	
	}
	
	
}
