package com.citrusbug.wattswap.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.citrusbug.wattswap.R;
import com.citrusbug.wattswap.adapter.FloorListAdapter.CopyDetector;
import com.citrusbug.wattswap.bean.Survey;
import com.citrusbug.wattswap.util.DatabaseHandler;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ViewFlipper;

public class SurveyClosedAdapter extends BaseAdapter {
	private Context mContext;
	private List<Survey> surveyList;
	LayoutInflater inflater;
	Date TodayDate;
	@SuppressLint("SimpleDateFormat")
	SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
	DatabaseHandler dbHandler;
	CopyDetector callback;
	public SurveyClosedAdapter(Context mContext, List<Survey> surveyList,CopyDetector cb) {
		this.mContext = mContext;
		this.surveyList = surveyList;
		TodayDate = new Date();
		dbHandler=new DatabaseHandler(mContext);
		this.callback=cb;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return surveyList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return surveyList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		final Survey object = (Survey) getItem(position);

		if (convertView == null) {
			convertView = View.inflate(mContext,
					R.layout.listitem_layout_home_closed, null);
			holder = new ViewHolder();
			Typeface font = Typeface.createFromAsset(mContext.getAssets(),"gothic.ttf");
			holder.surveyNameTextView = (TextView) convertView
					.findViewById(R.id.itemSurveyName_TextView);
			holder.surveyNameTextView.setTypeface(font);
			holder.descriptionTextView = (TextView) convertView
					.findViewById(R.id.itemDescription_TextView);
			holder.descriptionTextView.setTypeface(font);
			holder.dateTextView = (TextView) convertView
					.findViewById(R.id.itemdate_TextView);
			holder.dateTextView.setTypeface(font);
			
			holder.viewFlipper=(ViewFlipper)convertView.findViewById(R.id.listViewFlipper);
	        
	        holder.viewFlipper.setInAnimation(mContext,R.animator.in_from_right);
	        holder.viewFlipper.setOutAnimation(mContext,R.animator.out_to_right);
	        holder.viewFlipper.setFlipInterval(1500);
	        holder.deleteButton=(Button)convertView.findViewById(R.id.deleteButtonItem);
	        holder.deleteButton.setTag(position);
	        holder.reopenButton=(Button)convertView.findViewById(R.id.reopenButtonItem);
	        holder.deleteButton.setTag(position);
	        
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (!object.scheduledDate.equals("")) {

			try {
				Date scheduledDate = dateFormat.parse(object.scheduledDate);
				String currentdate = dateFormat.format(new Date());
				Date todayDate = dateFormat.parse(currentdate);
				if (scheduledDate.compareTo(todayDate) < 0) {
					holder.dateTextView.setText("Past " + object.scheduledDate);
					holder.dateTextView.setTextColor(Color.RED);
				} else {
					holder.dateTextView.setText(object.scheduledDate);
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		if(object.isVisible){
        	if(holder.viewFlipper.getDisplayedChild()==0){
				holder.viewFlipper.showNext();
			}
        }else{
        	if(holder.viewFlipper.getDisplayedChild()!=0){
				holder.viewFlipper.showPrevious();
			}
        }
		
		holder.deleteButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showDeleteAlertDialog(position,object.surveyId);
			}
		});
		
		holder.reopenButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showreOpenAlertDialog(position,object.surveyId);
			}
		});
		
		holder.surveyNameTextView.setText(object.surveyName);
		holder.descriptionTextView.setText(object.address1 + " "
				+ object.address2 + " " + object.city + " " + object.state);
		return convertView;
	}

	public static class ViewHolder {
		TextView surveyNameTextView;
		TextView descriptionTextView;
		TextView dateTextView;
		public ViewFlipper viewFlipper;
		Button deleteButton;
		Button reopenButton;
	}
	
	public void showDeleteAlertDialog(final int position,final String surveyid) {
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setMessage("Are you sure do you wan't delete Survey!");
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//delete survey here...
				dbHandler.deleteSurveyWithChiledRecord(surveyid);
				callback.onCopyFloor();
			}
		});
		builder.setNegativeButton("No", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		AlertDialog alertDialog = builder.create();
		alertDialog.show();
	}
	
	public void showreOpenAlertDialog(final int position,final String surveyid) {
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setMessage("Are you sure do you want to reopen Survey!");
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				ContentValues values=new ContentValues();
				values.put(DatabaseHandler.KEY_STATUS, "1");
				dbHandler.updateSurvey(values, surveyid);
				callback.onCopyFloor();
			}
		});
		builder.setNegativeButton("No", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		AlertDialog alertDialog = builder.create();
		alertDialog.show();
	}
}
