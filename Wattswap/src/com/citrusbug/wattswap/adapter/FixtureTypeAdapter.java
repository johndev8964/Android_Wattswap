package com.citrusbug.wattswap.adapter;

import com.citrusbug.wattswap.R;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EdgeEffect;
import android.widget.EditText;
import android.widget.TextView;
/**
 *  This FixtureTypeAdapter class used to inflate Fixture Type ListView 
 *   
 * @author Alkesh
 *
 */
public class FixtureTypeAdapter extends BaseAdapter {
	
   private Context mContext;
   private String [] fixureTypeList;
   public int mSelected = -1;
   public FixtureTypeAdapter(Context mContext, String []fixureTypeList){
		   this.mContext= mContext;
		   this.fixureTypeList= fixureTypeList;
   }
	@Override
	public int getCount() {
		return fixureTypeList.length;
	}
	@Override
	public Object getItem(int position) {
		return fixureTypeList[position];
	}
	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;	
		if(convertView == null){
    		convertView=View.inflate(mContext, R.layout.selectionlist_row_layout, null);
        	holder=new ViewHolder();
        	TextView itemTextView = (TextView)convertView.findViewById(R.id.listItemTextView);
        	Typeface font = Typeface.createFromAsset(mContext.getAssets(),"gothic.ttf");
        	itemTextView.setTypeface(font);
        	holder.itemTextView = itemTextView;
        	convertView.setTag(holder);
		}
		else{
			holder = (ViewHolder) convertView.getTag();
        }
        
        holder.itemTextView.setCompoundDrawablesWithIntrinsicBounds(null,null,null,null);
        holder.itemTextView.setText(fixureTypeList[position]);
        
        if (position == mSelected) {
        	convertView.setBackgroundResource(R.color.gray);
        	holder.itemTextView.setTextColor(mContext.getResources().getColor(R.color.lite_blue));
        }
        else {
        	convertView.setBackgroundColor(0x00000000);
        	holder.itemTextView.setTextColor(mContext.getResources().getColor(R.color.gray));
        }
        	

		return convertView;
	}
	static class ViewHolder{
		TextView itemTextView;
	}
	
}