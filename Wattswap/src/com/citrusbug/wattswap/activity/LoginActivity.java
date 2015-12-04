package com.citrusbug.wattswap.activity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import com.citrusbug.wattswap.R;
import com.citrusbug.wattswap.util.Constant;
import com.citrusbug.wattswap.util.NetworkUtil;
import com.citrusbug.wattswap.util.Pref;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class LoginActivity extends Activity implements OnClickListener {
	private EditText emailId;
	private EditText password;
	private Button   loginButton;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Constant.CONTEXT = getApplicationContext(); 
		Pref.openPref();
		if(Pref.getValue("LOGIN", null) != null) {
			Constant.SUPERVISOR_ID = Pref.getValue("LOGIN", null);
			goToMain();
		}
		else {
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			setContentView(R.layout.activity_login);
			emailId = (EditText) findViewById(R.id.emailText);
			password = (EditText) findViewById(R.id.passwordText);
			loginButton = (Button) findViewById(R.id.loginButton);
			loginButton.setOnClickListener(this);
		}
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		String mEmailId = emailId.getText().toString();
		String mPassword = password.getText().toString();
		String mDevice_type = android.os.Build.MODEL;
		String mDevice_id = getIMEInumber();
		String mMac_id = getMACAddress();
		if (mMac_id == null) {
			mMac_id = "";
		}
		
		if(isValidEmail()) {
			if(mPassword.length() > 0) {
				LoginTask loginTask = new LoginTask(mEmailId, mPassword, mDevice_id, mDevice_type, mMac_id);
				loginTask.execute();
			}
			else {
				Toast.makeText(this, "Please enter Password.", Toast.LENGTH_LONG).show();
			}
		}
	}
	
	public boolean isValidEmail() {
		Pattern pattern1 = Pattern
				.compile("^([a-zA-Z0-9_.-])+@([a-zA-Z0-9_.-])+\\.([a-zA-Z])+([a-zA-Z])+");

		if (emailId.getText().length() == 0) {
			Toast.makeText(this, "Please enter Supervisor email id.", Toast.LENGTH_LONG).show();
			return false;
		} else {
			Matcher matcher1 = pattern1.matcher(emailId.getText()
					.toString());
			if (matcher1.matches())
				return true;
		}
		return false;
	}
	
	private String getIMEInumber() {
		if (this != null) {
			TelephonyManager TelephonyMgr = (TelephonyManager) this
					.getSystemService(Context.TELEPHONY_SERVICE);
			String m_deviceId = TelephonyMgr.getDeviceId();
			return m_deviceId;
		}
		return null;
	}

	private String getMACAddress() {
		if (this != null) {

			WifiManager m_wm = (WifiManager) this
					.getSystemService(Context.WIFI_SERVICE);
			String m_wlanMacAdd = m_wm.getConnectionInfo().getMacAddress();
			return m_wlanMacAdd;
		}
		return null;
	}
	
	public class LoginTask extends AsyncTask<Void, Void, String> {

        final String mEmailId;
        final String mPassword;
        final String mDevice_id;
        final String mDevice_type;
        final String mMac_id;

        LoginTask(String emailId, String password, String device_id, String device_type, String mac_id) {
        	mEmailId = emailId;
        	mPassword = password;
        	mDevice_id = device_id;
        	mDevice_type = device_type;
        	mMac_id = mac_id;
        }

		@Override
        protected String doInBackground(Void... params) {
            return NetworkUtil.postLoginInfo(mEmailId, mPassword, mDevice_id, mDevice_type, mMac_id);
        }

		@Override
        protected void onPostExecute(String result) {
			JSONObject reader;
			try {
				reader = new JSONObject(result);
				String status = reader.getString("status");
		        if (status.equals("Success")) {
		        	Constant.SUPERVISOR_ID = reader.getJSONObject("data").getString("supervisor_id");
		        	Pref.openPref();
		        	Pref.setValue("LOGIN", Constant.SUPERVISOR_ID);
		        	goToMain();
		        }
		        else {
		        	Toast.makeText(getApplicationContext(), "Please check wifi or Try after few minutes.", Toast.LENGTH_LONG).show();
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
	
	public void goToMain() {
		
		Intent intent = new Intent(this, HomeActivity.class);
		this.startActivity(intent);
		this.finish();
	}
}
