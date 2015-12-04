package com.citrusbug.wattswap.adapter;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.citrusbug.wattswap.R;
import com.citrusbug.wattswap.activity.AddNewFloorActivity;
import com.citrusbug.wattswap.bean.Area;
import com.citrusbug.wattswap.bean.Fixtures;
import com.citrusbug.wattswap.bean.Floor;
import com.citrusbug.wattswap.util.Constant;
import com.citrusbug.wattswap.util.DatabaseHandler;
import com.citrusbug.wattswap.util.NetworkUtil;

/**
 *  This FloorListAdapter used to inflate Floor ListView.
 *  
 * @author Alkesh
 *
 */
public class FloorListAdapter extends BaseAdapter {
	 private Context mContext;
	 private List<Floor> floorList; 
     LayoutInflater inflater;
	 AlertDialog.Builder dialogBuilder;
	 DatabaseHandler dbHandler;
	 
	 CopyDetector callbackCopy;
	 DeleteDetector callbackDelete;
	public FloorListAdapter(Context mContext, List<Floor> floorList, CopyDetector callbackCopy, DeleteDetector callbackDelete){
		   this.mContext=mContext;
		   this.floorList=floorList;
		   inflater=LayoutInflater.from(mContext);
		   dbHandler=new DatabaseHandler(mContext);
		   this.callbackCopy = callbackCopy;
		   this.callbackDelete = callbackDelete;
	}
	   
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return floorList.size();
	}

	@Override
	public Object getItem(int position) {
		return floorList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		final ViewHolder holder;
		final Floor object=(Floor)getItem(position);
   
		if(convertView==null){
	        convertView=inflater.inflate(R.layout.listitem_layout_floor,parent,false);     	
	        holder=new ViewHolder();
	        Typeface face = Typeface.createFromAsset(mContext.getAssets(), "gothic.ttf");
	        
	        holder.itemTextView=(TextView)convertView.findViewById(R.id.listItemTextView);
	        holder.itemTextView.setTypeface(face);
	        holder.areaCountTextView=(TextView)convertView.findViewById(R.id.areaCountTextViewItem);
	        holder.fixturesCountTextView=(TextView)convertView.findViewById(R.id.fixCountTextViewItem);
	        
	        holder.viewFlipper=(ViewFlipper)convertView.findViewById(R.id.listViewFlipper);
	        
	        holder.viewFlipper.setInAnimation(mContext,R.animator.in_from_right);
	        holder.viewFlipper.setOutAnimation(mContext,R.animator.out_to_right);
	        holder.viewFlipper.setFlipInterval(1500);
	        
	        holder.editButton=(Button) convertView.findViewById(R.id.editButtonItem);
	        holder.editButton.setTag(position);
	        holder.deleteButton=(Button) convertView.findViewById(R.id.deleteButtonItem);
	        holder.deleteButton.setTag(position);
	        holder.copyButton=(Button)convertView.findViewById(R.id.copyButtonItem);
	        holder.deleteButton.setTag(position);
	      	convertView.setTag(holder);
        }else{		
        	holder = (ViewHolder) convertView.getTag();
        }

        
		holder.itemTextView.setText(object.floorDiscription);
        holder.areaCountTextView.setText(object.noOfArea);
        holder.fixturesCountTextView.setText(object.noOfFixures);
        
        if(object.isVisible){
        	if(holder.viewFlipper.getDisplayedChild()==0){
				holder.viewFlipper.showNext();
			}
        }else{
        	if(holder.viewFlipper.getDisplayedChild()!=0){
				holder.viewFlipper.showPrevious();
			}
        }
        
		holder.editButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Constant.ISFLOORUPDATEMODE=true;		
				Intent editIntent=new Intent(mContext,AddNewFloorActivity.class);
				editIntent.putExtra("floorid",object.floorId);
				editIntent.putExtra("name",object.floorDiscription);
				mContext.startActivity(editIntent);
			}
		});
          
		holder.deleteButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showAlertDialog(position,object.floorId);
			}
		});
           
		holder.copyButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showCopyAlertDialog(position,object.floorId);
			}
		});
		
        return convertView;
	}

	public static class ViewHolder{
		TextView itemTextView;
		TextView areaCountTextView;
		TextView fixturesCountTextView;
		public ViewFlipper viewFlipper;
		Button editButton;
		Button deleteButton;
		Button copyButton;
		
	}
	
	public void showAlertDialog(final int position,final String floorid) {
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setMessage("Are you sure do you wan't delete Record!");
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				DeleteFloorTask deleteFloorTask = new DeleteFloorTask(floorid);
				deleteFloorTask.execute();
				floorList.remove(position);
				dbHandler.deleteFloorWithChiledRecord(floorid);
				notifyDataSetChanged();
				callbackDelete.onDeleteFloor();
			}
		});
		builder.setNegativeButton("No", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				callbackDelete.onDeleteFloor();
			}
		});
		AlertDialog alertDialog = builder.create();
		alertDialog.show();
	}
	
	public void showCopyAlertDialog(final int position,final String floorid) {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        // Setting Dialog Title
        alertDialog.setTitle("Floor Name");

        // Setting Dialog Message
        alertDialog.setMessage("Enter Floor Name:");
        final EditText input = new EditText(mContext);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                LayoutParams.MATCH_PARENT,
                                LayoutParams.MATCH_PARENT);
		input.setLayoutParams(lp);
		alertDialog.setView(input);
		
        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    @Override
					public void onClick(DialogInterface dialog,int which) {
                        // Write your code here to execute after dialog
                    	String floorname=input.getText().toString();
                    	if(floorname.trim().length()==0){
                    		Toast.makeText(mContext, "Please enter any name.", Toast.LENGTH_SHORT).show();
                    		showCopyAlertDialog(position, floorid);
                    	}else{
	                    	CopyFloorTask copyFloorTask = new CopyFloorTask(floorid, floorname);
	                    	copyFloorTask.execute();
                    		
                    	}
                    }
                });
        alertDialog.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    @Override
					public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog
                        dialog.cancel();
                    }
                });

        alertDialog.show();
	}
	
	public interface CopyDetector{
		public void onCopyFloor();
	}
	
	public interface DeleteDetector{
		public void onDeleteFloor();
	}
	
	private void copyToDatabase(String floorid, String floorname) {
		//create new floor data same with different name.....
    	//insert floor data here...
    	List<Floor> floors=dbHandler.getFloorList(Constant.SURVEY_ID, floorname);
    	if(floors!=null&&floors.size()>0){
    		Floor tmp = floors.get(0);
    		ArrayList<Area> areas=dbHandler.getAreaList(floorid);
    		//now insert areas if available....
    		if(areas!=null){
        		ContentValues val;
        		for(Area area:areas){
            		val= new ContentValues();
            		val.put(DatabaseHandler.KEY_LNAME, area.loctaionName);
            		val.put(DatabaseHandler.KEY_FID, Constant.FLOOR_ID);
					int rowidarea=(int)dbHandler.addArea(val);
					//fetch and insert all fixures of area....
					ArrayList<Fixtures> fixtures=dbHandler.getFixturesList(area.areaId);
					for(Fixtures fixture:fixtures){
						//insert fixtures...
						ContentValues value = new ContentValues();

						value.put(DatabaseHandler.KEY_AID, rowidarea);
						value.put(DatabaseHandler.KEY_FIXTURE_NAME, fixture.fixtureName);
						value.put(DatabaseHandler.KEY_FIX_COUNT, fixture.fixtureCount);
						value.put(DatabaseHandler.KEY_CODE, fixture.code);
						value.put(DatabaseHandler.KEY_STYLE, fixture.style);
						value.put(DatabaseHandler.KEY_MOUNTING, fixture.mounting);
						value.put(DatabaseHandler.KEY_CONTROLLED, fixture.controlled);
						value.put(DatabaseHandler.KEY_OPTINON, fixture.option);
						value.put(DatabaseHandler.KEY_HEIGHT, fixture.height);
						value.put(DatabaseHandler.KEY_FVALUE, fixture.firstValue);
						value.put(DatabaseHandler.KEY_SVALUE, fixture.secondValue);
						value.put(DatabaseHandler.KEY_ANSWER, fixture.answer);
						value.put(DatabaseHandler.KEY_BALLAST_TYPE, fixture.ballastType);
						value.put(DatabaseHandler.KEY_BALLAST_FACTOR, fixture.ballastFactor);
						value.put(DatabaseHandler.KEY_BULBSVALUE, fixture.bulbValue);
						value.put(DatabaseHandler.KEY_WATTSVALUE, fixture.wattsValue);
						value.put(DatabaseHandler.KEY_ANSWERBULBS, fixture.bulbAnswer);

						value.put(DatabaseHandler.KEY_NOTE, fixture.note);
						value.put(DatabaseHandler.KEY_PATH,fixture.imagePath);
						dbHandler.addNewFixtures(value);
					}
        		}
    		}
    	}else{
    		ContentValues values=new ContentValues();
        	values.put(DatabaseHandler.KEY_FLOOR_DESCRIPTION, floorname);
        	values.put(DatabaseHandler.KEY_SID, Constant.SURVEY_ID);
        	int rowId = (int) dbHandler.addNewFloor(values);
        	if(rowId>0){
        		//inserted...
        		//get all areas of this floor....
        		ArrayList<Area> areas=dbHandler.getAreaList(floorid);
        		//now insert areas if available....
        		if(areas!=null){
            		ContentValues val;
            		for(Area area:areas){
                		val= new ContentValues();
                		val.put(DatabaseHandler.KEY_LNAME, area.loctaionName);
                		val.put(DatabaseHandler.KEY_FID, rowId);
    					int rowidarea=(int)dbHandler.addArea(val);
    					//fetch and insert all fixures of area....
    					ArrayList<Fixtures> fixtures=dbHandler.getFixturesList(area.areaId);
    					for(Fixtures fixture:fixtures){
    						//insert fixtures...
    						ContentValues value = new ContentValues();

    						value.put(DatabaseHandler.KEY_AID, rowidarea);
    						value.put(DatabaseHandler.KEY_FIXTURE_NAME, fixture.fixtureName);
    						value.put(DatabaseHandler.KEY_FIX_COUNT, fixture.fixtureCount);
    						value.put(DatabaseHandler.KEY_CODE, fixture.code);
    						value.put(DatabaseHandler.KEY_STYLE, fixture.style);
    						value.put(DatabaseHandler.KEY_MOUNTING, fixture.mounting);
    						value.put(DatabaseHandler.KEY_CONTROLLED, fixture.controlled);
    						value.put(DatabaseHandler.KEY_OPTINON, fixture.option);
    						value.put(DatabaseHandler.KEY_HEIGHT, fixture.height);
    						value.put(DatabaseHandler.KEY_FVALUE, fixture.firstValue);
    						value.put(DatabaseHandler.KEY_SVALUE, fixture.secondValue);
    						value.put(DatabaseHandler.KEY_ANSWER, fixture.answer);
    						value.put(DatabaseHandler.KEY_BALLAST_TYPE, fixture.ballastType);
    						value.put(DatabaseHandler.KEY_BALLAST_FACTOR, fixture.ballastFactor);
    						value.put(DatabaseHandler.KEY_BULBSVALUE, fixture.bulbValue);
    						value.put(DatabaseHandler.KEY_WATTSVALUE, fixture.wattsValue);
    						value.put(DatabaseHandler.KEY_ANSWERBULBS, fixture.bulbAnswer);

    						value.put(DatabaseHandler.KEY_NOTE, fixture.note);
    						value.put(DatabaseHandler.KEY_PATH,fixture.imagePath);
    						dbHandler.addNewFixtures(value);
    					}
            		}
        		}
        	}
    	}
    	callbackCopy.onCopyFloor();
	}
	
	public class CopyFloorTask extends AsyncTask<Void, Void, String> {

        final String mFloor_name;
        final String mFloor_id;

        CopyFloorTask(String floor_id, String floor_name) {
        	mFloor_name = floor_name;
        	mFloor_id = floor_id;
        }

        @Override
        protected String doInBackground(Void... params) {
            return NetworkUtil.copyFloor(mFloor_id, mFloor_name);
        }

		@Override
        protected void onPostExecute(String result) {
			JSONObject reader;
			try {
				reader = new JSONObject(result);
				String status = reader.getString("status");
		        if (status.equals("success")) {
		        	Constant.FLOOR_ID = reader.getJSONObject("data").getString("floor_id");
		        	copyToDatabase(mFloor_id, mFloor_name);
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
	
	public class DeleteFloorTask extends AsyncTask<Void, Void, String> {

        final String mFloor_id;

        DeleteFloorTask(String floor_id) {
        	mFloor_id = floor_id;
        }

        @Override
        protected String doInBackground(Void... params) {
            return NetworkUtil.deleteFloor(mFloor_id);
        }

		@Override
        protected void onPostExecute(String result) {
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

        @Override
        protected void onCancelled() {

        }
    }
}