package com.citrusbug.wattswap.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import com.citrusbug.wattswap.R;

public class FixtureTypeAdapterMultiSel  extends BaseAdapter {
	
    private Context mContext;
    private String []fixureTypeList;
    private boolean[] checkList;
	public FixtureTypeAdapterMultiSel(Context mContext,String []fixureTypeList){
		   this.mContext=mContext;
		   this.fixureTypeList=fixureTypeList;
		   checkList=new boolean[fixureTypeList.length];
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;	
        if(convertView==null){
        	convertView=View.inflate(mContext,R.layout.multiselection_row_layout,null);
        	holder=new ViewHolder();
        	holder.itemTextView=(TextView)convertView.findViewById(R.id.listItemTextView);
        	holder.chkBox = (CheckBox)convertView.findViewById(R.id.checkBox1);
        	convertView.setTag(holder);
        }else{
			holder = (ViewHolder) convertView.getTag();
        }
        holder.itemTextView.setCompoundDrawablesWithIntrinsicBounds(null,null,null,null);
        holder.itemTextView.setText(fixureTypeList[position]);
        
        if(checkList[position]){
        	holder.chkBox.setChecked(true);
        }else{
        	holder.chkBox.setChecked(false);
        }
        holder.chkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked){
					checkList[position]=true;
				}else{
					checkList[position]=false;
				}
				FixtureTypeAdapterMultiSel.this.notifyDataSetChanged();
			}
		});
		return convertView;
	}
	static class ViewHolder{
		TextView itemTextView;
		CheckBox chkBox;
	}
	
	public ArrayList<Integer> getSelectedPosition(){
		ArrayList<Integer> selectedpos=new ArrayList<Integer>(); 
		int count=0;
		
		for(int pos=0;pos<checkList.length;pos++){
			if(checkList[pos]){
				selectedpos.add(pos);
			}
		}
		
		return selectedpos;
	}
	
	public void setSelected(int pos){
		if(checkList[pos])
			checkList[pos]=false;
		else
			checkList[pos]=true;
		FixtureTypeAdapterMultiSel.this.notifyDataSetChanged();
	}
	
}
