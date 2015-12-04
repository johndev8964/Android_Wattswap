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
import com.citrusbug.wattswap.activity.AddNewAreaActivity;
import com.citrusbug.wattswap.bean.Area;
import com.citrusbug.wattswap.bean.Fixtures;
import com.citrusbug.wattswap.util.Constant;
import com.citrusbug.wattswap.util.DatabaseHandler;
import com.citrusbug.wattswap.util.NetworkUtil;

/**
 *  This AreaListAdapter class used to inflate Area listView 
 *  
 * @author Alkesh
 *
 */
public class AreaListAdapter extends BaseAdapter {

	
	private Context mContext;
	private List<Area> areaList;
	LayoutInflater inflater;
	DatabaseHandler dbHandler;
	CopyAreaDetector callbackCopy;
	DeleteAreaDetector callbackDelete;
	
	public AreaListAdapter(Context mContext,List<Area> areaList, CopyAreaDetector callbackCopy, DeleteAreaDetector callbackDelete){
		   this.mContext=mContext;
		   this.areaList=areaList;
		   inflater=LayoutInflater.from(mContext);
		   dbHandler=new DatabaseHandler(mContext);
		   this.callbackCopy = callbackCopy;
		   this.callbackDelete = callbackDelete;
		   
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return areaList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return areaList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		   final ViewHolder holder;
		final Area object=(Area)getItem(position);
		
        if(convertView==null){
        	convertView=View.inflate(mContext,R.layout.listitem_layout_area,null);
        	
        	holder=new ViewHolder();
        	Typeface face = Typeface.createFromAsset(mContext.getAssets(), "gothic.ttf");
        	holder.itemTextView=(TextView)convertView.findViewById(R.id.listItemTextView);
        	holder.itemTextView.setTypeface(face);
        	holder.areaCountTextView=(TextView)convertView.findViewById(R.id.areaCountTextViewItem);
        	holder.fixturesCountTextView=(TextView)convertView.findViewById(R.id.fixCountTextViewItem);
        	holder.lineView=convertView.findViewById(R.id.lineView);
            holder.viewFlipper=(ViewFlipper)convertView.findViewById(R.id.listViewFlipper);
        	 
            holder.viewFlipper.setInAnimation(mContext,R.animator.in_from_right);
            holder.viewFlipper.setOutAnimation(mContext,R.animator.out_to_right);
            holder.viewFlipper.setFlipInterval(1500);
            
    	    holder.editButton=(Button) convertView.findViewById(R.id.editButtonItem);
            holder.editButton.setTag(position);
            holder.deleteButton=(Button) convertView.findViewById(R.id.deleteButtonItem);
            holder.deleteButton.setTag(position);
            holder.copyareaButton=(Button)convertView.findViewById(R.id.copyButtonItem);
            holder.copyareaButton.setTag(position);
             
        	convertView.setTag(holder);
        }else{
			holder = (ViewHolder) convertView.getTag();
        }
        
        holder.itemTextView.setText(object.loctaionName);
        
        holder.areaCountTextView.setVisibility(View.GONE);
        holder.lineView.setVisibility(View.GONE);
        holder.fixturesCountTextView.setText(object.noOfFixture);
        
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
				Constant.ISAREAUPDATEMODE=true;		
				Intent editIntent=new Intent(mContext, AddNewAreaActivity.class);
				editIntent.putExtra("areaid",object.areaId);
				editIntent.putExtra("name",object.loctaionName);
				mContext.startActivity(editIntent);
			}
		});
          
		holder.deleteButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showAlertDialog(position,object.areaId);
			}
		});
		
		holder.copyareaButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showCopyAlertDialog(position,object.areaId);
			}
		});
		
		return convertView;
	}
	
	
   

	
	public static class ViewHolder{
		TextView itemTextView;
		
		TextView areaCountTextView;
		TextView fixturesCountTextView;
		View lineView;
		
		public ViewFlipper viewFlipper;
		Button editButton;
		Button deleteButton;
		Button copyareaButton;
	}	
	
	public static void setFont(TextView itemTextView) {
	    Typeface tf = Typeface.createFromAsset(itemTextView.getContext()
	            .getAssets(), "gothic.ttf");

	    itemTextView.setTypeface(tf);

	}
	
	
	public void showAlertDialog(final int position,final String areaid) {
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setMessage("Are you sure do you wan't delete Record!");
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				DeleteAreaTask deleteAreaTask = new DeleteAreaTask(areaid);
				deleteAreaTask.execute();
				
				areaList.remove(position);
				dbHandler.deleteAreaWithChiledRecord(areaid);
				notifyDataSetChanged();
				callbackDelete.onDeleteArea();
			}
		});
		builder.setNegativeButton("No", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				callbackDelete.onDeleteArea();
			}
		});
		AlertDialog alertDialog = builder.create();
		alertDialog.show();
	}
	
	public void showCopyAlertDialog(final int position,final String areaid) {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        // Setting Dialog Title
        alertDialog.setTitle("Area Name");

        // Setting Dialog Message
        alertDialog.setMessage("Enter Area Name:");
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
                    	String areaname=input.getText().toString();
                    	if(areaname.trim().length()==0){
                    		Toast.makeText(mContext, "Please enter any name.", Toast.LENGTH_SHORT).show();
                    		showCopyAlertDialog(position, areaid);
                    	}else{
                    		CopyAreaTask copyAreaTask = new CopyAreaTask(areaid, areaname);
                    		copyAreaTask.execute();
	                    	//create new area data same with different name.....
	                    	//insert area data here...
	                    	List<Area> areass=dbHandler.getAreaListById(areaid);
	                    	if(areass!=null&&areass.size()>0){
	                    		Area tmp=areass.get(0);
	                    		
	                    		ContentValues val= new ContentValues();
	                    		val.put(DatabaseHandler.KEY_LNAME, areaname);
	                    		val.put(DatabaseHandler.KEY_FID, tmp.floorId);
	                    		
	        					int rowidarea=(int)dbHandler.addArea(val);
	        					
			        					ArrayList<Fixtures> fixtures=dbHandler.getFixturesList(tmp.areaId);
			        					
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
			                    
	                    	callbackCopy.onCopyArea();
                    	
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
	
	public interface CopyAreaDetector{
		
		public void onCopyArea();
	}
	
	public interface DeleteAreaDetector{
		
		public void onDeleteArea();
	}
	
	public class CopyAreaTask extends AsyncTask<Void, Void, String> {

        final String mArea_name;
        final String mArea_id;

        CopyAreaTask(String area_id, String area_name) {
        	mArea_name = area_name;
        	mArea_id = area_id;
        }

        @Override
        protected String doInBackground(Void... params) {
            return NetworkUtil.copyArea(mArea_id, mArea_name);
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
	
	public class DeleteAreaTask extends AsyncTask<Void, Void, String> {

        final String mArea_id;

        DeleteAreaTask(String area_id) {
        	mArea_id = area_id;
        }

        @Override
        protected String doInBackground(Void... params) {
            return NetworkUtil.deleteArea(mArea_id);
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
