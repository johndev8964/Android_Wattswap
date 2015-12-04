package com.citrusbug.wattswap.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
 
public class NetworkUtil {
     
    public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 2;
    public static int TYPE_NOT_CONNECTED = 0;
    public static int INVALID = 3;
     
    public static int getConnectivityStatus(Context context) {
       try{
    	ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
 
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return TYPE_WIFI;
             
            if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return TYPE_MOBILE;
        } 
        return TYPE_NOT_CONNECTED;
       }
       catch(Exception e){
    	   
       }
       return INVALID;
    }
     
    public static String getConnectivityStatusString(Context context) {
        int conn = NetworkUtil.getConnectivityStatus(context);
        if(conn == NetworkUtil.INVALID){
        	return null;
        }
        String status = null;
        if (conn == NetworkUtil.TYPE_WIFI) {
            status = "Wifi enabled";
        } else if (conn == NetworkUtil.TYPE_MOBILE) {
            status = "Mobile data enabled";
        } else if (conn == NetworkUtil.TYPE_NOT_CONNECTED) {
            status = "Not connected to Internet";
        }
        return status;
    }
    
    public static String postLoginInfo(String email, String password, String device_id, String device_type, String mac_id) {
    	try {
        	String loginUrl = "http://s550561979.onlinehome.us/facility/index.php/webservices/applogin";
        	
        	HttpPost post = new HttpPost(loginUrl);
        	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);
            nameValuePairs.add(new BasicNameValuePair("supervisor_email", email));
            nameValuePairs.add(new BasicNameValuePair("supervisor_password", password));
            nameValuePairs.add(new BasicNameValuePair("device_id", device_id));
            nameValuePairs.add(new BasicNameValuePair("device_type", device_type));
            nameValuePairs.add(new BasicNameValuePair("mac_id", mac_id));
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpClient client = new DefaultHttpClient();
	        HttpResponse response = client.execute(post);

	        HttpEntity entity = response.getEntity();
	        String responseText = EntityUtils.toString(entity);

	        return responseText;
	    } 
    	catch (Exception e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	    return null;
    	}
    }
    
    public static String getStates() {
    	try {
        	String getStatesUrl = "http://s550561979.onlinehome.us/facility/index.php/webservices/getstates";
        	
        	HttpPost post = new HttpPost(getStatesUrl);
			HttpClient client = new DefaultHttpClient();
	        HttpResponse response = client.execute(post);

	        HttpEntity entity = response.getEntity();
	        String responseText = EntityUtils.toString(entity);

	        return responseText;
	    } 
    	catch (Exception e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	    return null;
    	}
    }
    
    public static class NewSurvey {
    	public static String supervisor_id;
    	public static String survey_facility_name;
    	public static String survey_facility_add_l1;
    	public static String survey_facility_add_l2;
    	public static String survey_facility_city;
    	public static String state_id;
    	public static String survey_facility_zip;
    	public static String survey_contact_name;
    	public static String survey_contact_phone;
    	public static String survey_contact_email;
    	public static String survey_schedule_date;
    	public static String survey_note;
    	public static String survey_building_units;
    	public static String survey_building_type;
    	public static String survey_reffered_by;
    	public static String survey_sq_foot;
    	public static String survey_utility_company;
    	public static String survey_account_no;
    	public static String survey_image_b64;
    }
    
    public static class NewFixture {
    	public static String survey_id;
    	public static String floor_id;
    	public static String area_id;
    	public static String count;
    	public static String type;
    	public static String code;
    	public static String style;
    	public static String mounting;
    	public static String controlled;
    	public static String option;
    	public static String height;
    	public static String hrs_per_day;
    	public static String days_per_week;
    	public static String notes;
    	public static String ballast;
    	public static String factor;
    	public static String bulbs_per_fixture;
    	public static String watts_per_bulb;
    	public static String fixture_images_b64;
    }
    
    public static String postAddNewSurveyApp(NewSurvey newSurvey) {
    	try {
        	String addNewSurveyUrl = "http://s550561979.onlinehome.us/facility/index.php/webservices/addNewSurvey";
        	
        	HttpPost post = new HttpPost(addNewSurveyUrl);
        	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(19);
            nameValuePairs.add(new BasicNameValuePair("supervisor_id", NewSurvey.supervisor_id));
            nameValuePairs.add(new BasicNameValuePair("survey_facility_name", NewSurvey.survey_facility_name));
            nameValuePairs.add(new BasicNameValuePair("survey_facility_add_l1", NewSurvey.survey_facility_add_l1));
            nameValuePairs.add(new BasicNameValuePair("survey_facility_add_l2", NewSurvey.survey_facility_add_l2));
            nameValuePairs.add(new BasicNameValuePair("survey_facility_city", NewSurvey.survey_facility_city));
            nameValuePairs.add(new BasicNameValuePair("state_id", NewSurvey.state_id));
            nameValuePairs.add(new BasicNameValuePair("survey_facility_zip", NewSurvey.survey_facility_zip));
            nameValuePairs.add(new BasicNameValuePair("survey_contact_name", NewSurvey.survey_contact_name));
            nameValuePairs.add(new BasicNameValuePair("survey_contact_phone", NewSurvey.survey_contact_phone));
            nameValuePairs.add(new BasicNameValuePair("survey_contact_email", NewSurvey.survey_contact_email));
            nameValuePairs.add(new BasicNameValuePair("survey_schedule_date", NewSurvey.survey_schedule_date));
            nameValuePairs.add(new BasicNameValuePair("survey_note", NewSurvey.survey_note));
            nameValuePairs.add(new BasicNameValuePair("survey_building_units", NewSurvey.survey_building_units));
            nameValuePairs.add(new BasicNameValuePair("survey_building_type", NewSurvey.survey_building_type));
            nameValuePairs.add(new BasicNameValuePair("survey_reffered_by", NewSurvey.survey_reffered_by));
            nameValuePairs.add(new BasicNameValuePair("survey_sq_foot", NewSurvey.survey_sq_foot));
            nameValuePairs.add(new BasicNameValuePair("survey_utility_company", NewSurvey.survey_utility_company));
            nameValuePairs.add(new BasicNameValuePair("survey_account_no", NewSurvey.survey_account_no));
            nameValuePairs.add(new BasicNameValuePair("survey_images_b64", NewSurvey.survey_image_b64));
            
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpClient client = new DefaultHttpClient();
	        HttpResponse response = client.execute(post);

	        HttpEntity entity = response.getEntity();
	        String responseText = EntityUtils.toString(entity);

	        return responseText;

		} 
		catch (Exception e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	    return null;
    	}
    }
    
    public static String addFloor(String survey_id, String floor_name) {
    	try {
        	String addFloorUrl = "http://s550561979.onlinehome.us/facility/index.php/webservices/addFloorToSurvey";
        	
        	HttpPost post = new HttpPost(addFloorUrl);
        	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("survey_id", survey_id));
            nameValuePairs.add(new BasicNameValuePair("floor_name", floor_name));
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpClient client = new DefaultHttpClient();
	        HttpResponse response = client.execute(post);

	        HttpEntity entity = response.getEntity();
	        String responseText = EntityUtils.toString(entity);

	        return responseText;
	    } 
    	catch (Exception e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	    return null;
    	}
    }
    
    public static String addArea(String survey_id, String floor_id, String area_name) {
    	try {
        	String addAreaUrl = "http://s550561979.onlinehome.us/facility/index.php/webservices/addAreaToFloor";
        	
        	HttpPost post = new HttpPost(addAreaUrl);
        	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
            nameValuePairs.add(new BasicNameValuePair("survey_id", survey_id));
            nameValuePairs.add(new BasicNameValuePair("floor_id", floor_id));
            nameValuePairs.add(new BasicNameValuePair("area_name", area_name));
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpClient client = new DefaultHttpClient();
	        HttpResponse response = client.execute(post);

	        HttpEntity entity = response.getEntity();
	        String responseText = EntityUtils.toString(entity);

	        return responseText;
	    } 
    	catch (Exception e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	    return null;
    	}
    }
    
    @SuppressWarnings("static-access")
	public static String addFixture(NewFixture newFixture) {
    	try {
        	String url = "http://s550561979.onlinehome.us/facility/index.php/webservices/addFixturetoareanew";
        	
        	HttpPost post = new HttpPost(url);
        	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(19);
        	nameValuePairs.add(new BasicNameValuePair("survey_id", newFixture.survey_id));
        	nameValuePairs.add(new BasicNameValuePair("floor_id", newFixture.floor_id));
            nameValuePairs.add(new BasicNameValuePair("area_id", newFixture.area_id));
            nameValuePairs.add(new BasicNameValuePair("count", newFixture.count));
            nameValuePairs.add(new BasicNameValuePair("type", newFixture.type));
        	nameValuePairs.add(new BasicNameValuePair("code", newFixture.code));
            nameValuePairs.add(new BasicNameValuePair("style", newFixture.style));
            nameValuePairs.add(new BasicNameValuePair("mounting", newFixture.mounting));
            nameValuePairs.add(new BasicNameValuePair("controlled", newFixture.controlled));
        	nameValuePairs.add(new BasicNameValuePair("option", newFixture.option));
            nameValuePairs.add(new BasicNameValuePair("height", newFixture.height));
            nameValuePairs.add(new BasicNameValuePair("hrs_per_day", newFixture.hrs_per_day));
        	nameValuePairs.add(new BasicNameValuePair("days_per_week", newFixture.days_per_week));
            nameValuePairs.add(new BasicNameValuePair("notes", newFixture.notes));
            nameValuePairs.add(new BasicNameValuePair("ballast", newFixture.ballast));
        	nameValuePairs.add(new BasicNameValuePair("factor", newFixture.factor));
            nameValuePairs.add(new BasicNameValuePair("bulbs_per_fixture", newFixture.bulbs_per_fixture));
            nameValuePairs.add(new BasicNameValuePair("watts_per_bulb", newFixture.watts_per_bulb));
            nameValuePairs.add(new BasicNameValuePair("fixture_images_b64", newFixture.fixture_images_b64));
            
            post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpClient client = new DefaultHttpClient();
	        HttpResponse response = client.execute(post);

	        HttpEntity entity = response.getEntity();
	        String responseText = EntityUtils.toString(entity);

	        return responseText;
	    } 
    	catch (Exception e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	    return null;
    	}
    }
    
    public static String copyFloor(String floor_id, String floor_name) {
    	try {
    		String url = "http://s550561979.onlinehome.us/facility/index.php/webservices/copyFloor";
        	
        	HttpPost post = new HttpPost(url);
        	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        	nameValuePairs.add(new BasicNameValuePair("floor_id", floor_id));
        	nameValuePairs.add(new BasicNameValuePair("floor_name", floor_name));
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpClient client = new DefaultHttpClient();
	        HttpResponse response = client.execute(post);

	        HttpEntity entity = response.getEntity();
	        String responseText = EntityUtils.toString(entity);

	        return responseText;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
    }
    
    public static String editFloor(String floor_id, String floor_name) {
    	try {
    		String url = "http://s550561979.onlinehome.us/facility/index.php/webservices/editFloor";
        	
        	HttpPost post = new HttpPost(url);
        	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        	nameValuePairs.add(new BasicNameValuePair("floor_id", floor_id));
        	nameValuePairs.add(new BasicNameValuePair("floor_name", floor_name));
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpClient client = new DefaultHttpClient();
	        HttpResponse response = client.execute(post);

	        HttpEntity entity = response.getEntity();
	        String responseText = EntityUtils.toString(entity);

	        return responseText;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
    }
    
    public static String deleteFloor(String floor_id) {
    	try {
    		String url = "http://s550561979.onlinehome.us/facility/index.php/webservices/deleteFloor";
        	
        	HttpPost post = new HttpPost(url);
        	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
        	nameValuePairs.add(new BasicNameValuePair("floor_id", floor_id));
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpClient client = new DefaultHttpClient();
	        HttpResponse response = client.execute(post);

	        HttpEntity entity = response.getEntity();
	        String responseText = EntityUtils.toString(entity);

	        return responseText;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
    }
    
    public static String copyArea(String area_id, String area_name) {
    	try {
    		String url = "http://s550561979.onlinehome.us/facility/index.php/webservices/copyArea";
        	
        	HttpPost post = new HttpPost(url);
        	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        	nameValuePairs.add(new BasicNameValuePair("area_id", area_id));
        	nameValuePairs.add(new BasicNameValuePair("area_name", area_name));
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpClient client = new DefaultHttpClient();
	        HttpResponse response = client.execute(post);

	        HttpEntity entity = response.getEntity();
	        String responseText = EntityUtils.toString(entity);

	        return responseText;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
    }
    
    public static String editArea(String area_id, String area_name) {
    	try {
    		String url = "http://s550561979.onlinehome.us/facility/index.php/webservices/editArea";
        	
        	HttpPost post = new HttpPost(url);
        	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        	nameValuePairs.add(new BasicNameValuePair("area_id", area_id));
        	nameValuePairs.add(new BasicNameValuePair("area_name", area_name));
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpClient client = new DefaultHttpClient();
	        HttpResponse response = client.execute(post);

	        HttpEntity entity = response.getEntity();
	        String responseText = EntityUtils.toString(entity);

	        return responseText;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
    }
    
    public static String deleteArea(String area_id) {
    	try {
    		String url = "http://s550561979.onlinehome.us/facility/index.php/webservices/deleteArea";
        	
        	HttpPost post = new HttpPost(url);
        	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
        	nameValuePairs.add(new BasicNameValuePair("floor_id", area_id));
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpClient client = new DefaultHttpClient();
	        HttpResponse response = client.execute(post);

	        HttpEntity entity = response.getEntity();
	        String responseText = EntityUtils.toString(entity);

	        return responseText;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
    }
    
    public static String deletefixture(String fixture_id) {
    	try {
    		String url = "http://s550561979.onlinehome.us/facility/index.php/webservices/deleteFixture";
        	
        	HttpPost post = new HttpPost(url);
        	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
        	nameValuePairs.add(new BasicNameValuePair("fixture_id", fixture_id));
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpClient client = new DefaultHttpClient();
	        HttpResponse response = client.execute(post);

	        HttpEntity entity = response.getEntity();
	        String responseText = EntityUtils.toString(entity);

	        return responseText;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
    }
}