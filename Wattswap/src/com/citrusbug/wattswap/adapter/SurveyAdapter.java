package com.citrusbug.wattswap.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.citrusbug.wattswap.R;
import com.citrusbug.wattswap.bean.Survey;

/**
 *   This SurveyAdapter class used to inflate Survey ListView with number of Open or Close Survey
 *    
 * @author Alkesh
 */
@SuppressLint("SimpleDateFormat")
public class SurveyAdapter extends BaseAdapter {
   private Context mContext;
   private List<Survey> surveyList;
   LayoutInflater inflater;
   Date TodayDate;
   SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");

   public SurveyAdapter(Context mContext,List<Survey> surveyList){
	   this.mContext=mContext;
	   this.surveyList=surveyList;
	   TodayDate=new Date();
	   
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
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		Survey object=(Survey)getItem(position);
		
        if(convertView==null){
        	convertView=View.inflate(mContext,R.layout.listitem_layout_home,null);
        	holder=new ViewHolder();
        	
        	Typeface font = Typeface.createFromAsset(mContext.getAssets(),"gothic.ttf");
        	holder.surveyNameTextView=(TextView)convertView.findViewById(R.id.itemSurveyName_TextView);
        	holder.surveyNameTextView.setTypeface(font);
        	holder.descriptionTextView=(TextView)convertView.findViewById(R.id.itemDescription_TextView);
        	holder.descriptionTextView.setTypeface(font);
        	holder.dateTextView=(TextView)convertView.findViewById(R.id.itemdate_TextView);
        	holder.dateTextView.setTypeface(font);
        	convertView.setTag(holder);
        } else {
			holder = (ViewHolder) convertView.getTag();
        }
        
        if(!object.scheduledDate.equals("")){
        	
        	try {
        		Date scheduledDate = dateFormat.parse(object.scheduledDate);
				String currentdate=dateFormat.format(new Date());
				Date  todayDate=dateFormat.parse(currentdate);
				if(scheduledDate.compareTo(todayDate)<0){
					holder.dateTextView.setText("Past "+object.scheduledDate);
	                holder.dateTextView.setTextColor(Color.RED);
				   }else{
		                holder.dateTextView.setText(object.scheduledDate);
				   }
			} catch (ParseException e) {
				e.printStackTrace();
			}
        }
        holder.surveyNameTextView.setText(object.surveyName);
        holder.descriptionTextView.setText(object.address1+" "+object.address2+" "+object.city+" "+object.state);
		return convertView;
	}

	static class ViewHolder{
		TextView surveyNameTextView;
		TextView descriptionTextView;
		TextView dateTextView;
	}
}
