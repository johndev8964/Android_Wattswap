package com.citrusbug.wattswap.activity;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.citrusbug.wattswap.R;
import com.citrusbug.wattswap.activity.AddNewSurveyActivity.FontChangeCrawler;
import com.citrusbug.wattswap.util.ConnectionDetector;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends Activity {

	TextView backTextView;
	TextView getDirectionTextView;
	
	String address;
	GoogleMap googleMap;
	double latitude=0;
	double longitude=0;
	
	double source_latitude=0;
	double source_longitude=0;
	double destination_latitude=0;
	double destination_longitude=0;
	
	private static final int TWO_MINUTES = 1000 * 60 * 1;
	public LocationManager locationManager;
	public MyLocationListener listener;
	public Location previousBestLocation = null;
	
	Context mContext;
	private ProgressDialog mProgressDialog;
    
	ConnectionDetector cd;
	
	  @Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			setContentView(R.layout.activity_map);
			
			FontChangeCrawler fontChanger = new FontChangeCrawler(getAssets(), "gothic.ttf");
		    fontChanger.replaceFonts((ViewGroup)this.findViewById(android.R.id.content));
			
			if(getIntent()!=null){
				address = getIntent().getStringExtra("address");
			}
		//   address="Swastik cross road, Navarngpura, Ahmedabad, Gujarat"; // Latitude: 23.03514360000 Longitude:72.565182699999920000
			mContext=this;
		    cd=new ConnectionDetector(mContext);
		    
			mProgressDialog=new ProgressDialog(MapActivity.this);
			mProgressDialog.setMessage("Please wait...");
			mProgressDialog.setIndeterminate(false);
			
			
			backTextView=(TextView)findViewById(R.id.back_TextView_Map);
			getDirectionTextView=(TextView)findViewById(R.id.getdirection_TextView_Map);
			
			 try {
		            // Loading map
		            initilizeMap();
		        } catch (Exception e) {
		            e.printStackTrace();
		        }
			 
			 backTextView.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					// TODO Auto-generated method stub
					activityCloseTransition();
					return false;
				}
			});
			 
			 getDirectionTextView.setOnTouchListener(new OnTouchListener() {
				
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					// TODO Auto-generated method stub
					if(cd.isConnectingToInternet()){
						if(source_latitude!=0&&source_longitude!=0){
							if(destination_latitude!=0&&destination_longitude!=0){
								String uri = "http://maps.google.com/maps?f=d&hl=en&saddr="+source_latitude+","+source_longitude+"&daddr="+destination_latitude+","+destination_longitude;
								Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
								intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
								startActivity(intent);
							}else{
								Toast.makeText(mContext,"Destination location info not available",Toast.LENGTH_SHORT).show();
							}
						}else{
							Toast.makeText(mContext,"Current location info not available",Toast.LENGTH_SHORT).show();
						}
					}else{
						Toast.makeText(mContext,"Internet connection not available!!!",Toast.LENGTH_SHORT).show();
					}
					return false;
				}
			});
		}
	  
	  /**
	     * Method to load map. If map is not created it will create it for you
	     * */
	    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
		@SuppressLint("NewApi")
		private void initilizeMap() {
	        if (googleMap == null) {
	            googleMap = ((MapFragment) getFragmentManager().findFragmentById(
	            		
	                    R.id.map)).getMap();
	       
	            googleMap.setMyLocationEnabled(true);
	            
	            if(cd.isConnectingToInternet()){
	            Geocoder geoCoder = new Geocoder(getBaseContext(), Locale.getDefault());
		        try {
		        	  if(address!=null&&!address.equals("")){
		        		  List<Address> addresses = geoCoder.getFromLocationName(address, 5);
		       
		   	               if (addresses.size() > 0) {
		   	            	   latitude=addresses.get(0).getLatitude();
		   	            	   longitude=addresses.get(0).getLongitude();
		   	            		
		   	            	   destination_latitude=latitude;
		   	            	   destination_longitude=longitude;
		   	            	   
				   	   	        LatLng ll=new LatLng(latitude,longitude);
				   	   	        googleMap.moveCamera(CameraUpdateFactory.newLatLng(ll));
				                // Zoom in the Google Map
				                googleMap.animateCamera(CameraUpdateFactory.zoomTo(16));  
				   	   	     
				   	            // create marker
			   	                MarkerOptions marker = new MarkerOptions().position(ll).title(""+address);
			   	            	marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
			   	   	            googleMap.addMarker(marker);
		   	   	              
		        	  }
		           }    
		           } catch (IOException e) {
		               e.printStackTrace();
		           }
		       
		        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	            listener = new MyLocationListener();        
	            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 4000, 0, listener);
	            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 4000, 0, listener);

	        }else{
				Toast.makeText(mContext,"Internet connection not available!!!",Toast.LENGTH_SHORT).show();
                return;
	        }
	      }
	  }
	    
	    /**
		 * This method used to display activity transition from left to right
		 */
		public void activityCloseTransition(){
			finish();
			overridePendingTransition(R.animator.in_from_left,
						R.animator.out_to_right);
		}
		public class MyLocationListener implements LocationListener{
	        @Override
			public void onLocationChanged(final Location loc){
	            Log.i("**************************************", "Location changed");
	            if(isBetterLocation(loc, previousBestLocation)) {
	            	source_latitude=loc.getLatitude();
	            	source_longitude=loc.getLongitude();
	            //    drawMarker(loc);
	            }                               
	        }
	        @Override
			public void onProviderDisabled(String provider){
	            Toast.makeText( getApplicationContext(), "Gps is Disabled ", Toast.LENGTH_SHORT ).show();
	        }
	        @Override
			public void onProviderEnabled(String provider){
	            Toast.makeText( getApplicationContext(), "Gps is Enabled", Toast.LENGTH_SHORT).show();
	        }
	        @Override
			public void onStatusChanged(String provider, int status, Bundle extras){
	        }
	       }
	    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
	        if (currentBestLocation == null) {
	            // A new location is always better than no location
	            return true;
	        }

	        // Check whether the new location fix is newer or older
	        long timeDelta = location.getTime() - currentBestLocation.getTime();
	        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
	        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
	        boolean isNewer = timeDelta > 0;

	        // If it's been more than two minutes since the current location, use the new location
	        // because the user has likely moved
	        if (isSignificantlyNewer) {
	            return true;
	        // If the new location is more than two minutes older, it must be worse
	        } else if (isSignificantlyOlder) {
	            return false;
	        }

	        // Check whether the new location fix is more or less accurate
	        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
	        boolean isLessAccurate = accuracyDelta > 0;
	        boolean isMoreAccurate = accuracyDelta < 0;
	        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

	        // Check if the old and new location are from the same provider
	        boolean isFromSameProvider = isSameProvider(location.getProvider(),
	                currentBestLocation.getProvider());

	        // Determine location quality using a combination of timeliness and accuracy
	        if (isMoreAccurate) {
	            return true;
	        } else if (isNewer && !isLessAccurate) {
	            return true;
	        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
	            return true;
	        }
	        return false;
	    }
	    
	    /** Checks whether two providers are the same */
	    private boolean isSameProvider(String provider1, String provider2) {
	        if (provider1 == null) {
	          return provider2 == null;
	        }
	        return provider1.equals(provider2);
	    }
	    
	    @Override
	    protected void onDestroy() {
	    	// TODO Auto-generated method stub
	    	super.onDestroy();
	    	if(locationManager!=null&&listener!=null){
	    		locationManager.removeUpdates(listener);	
	    	}
	     	cd=null;
	    }
	    
	    public class FontChangeCrawler
		{
		    private Typeface typeface;

		    public FontChangeCrawler(Typeface typeface)
		    {
		        this.typeface = typeface;
		    }

		    public FontChangeCrawler(AssetManager assets, String assetsFontFileName)
		    {
		        typeface = Typeface.createFromAsset(assets, assetsFontFileName);
		    }

		    public void replaceFonts(ViewGroup viewTree)
		    {
		        View child;
		        for(int i = 0; i < viewTree.getChildCount(); ++i)
		        {
		            child = viewTree.getChildAt(i);
		            if(child instanceof ViewGroup)
		            {
		                // recursive call
		                replaceFonts((ViewGroup)child);
		            }
		            else if(child instanceof TextView)
		            {
		                // base case
		                ((TextView) child).setTypeface(typeface);
		            }
		        }
		    }
		}
}
