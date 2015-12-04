package com.citrusbug.wattswap.adapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.opengl.Visibility;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.citrusbug.wattswap.R;
import com.citrusbug.wattswap.bean.Fixtures;
import com.citrusbug.wattswap.util.DatabaseHandler;

/**
 *  This FixturesListAdapter class used to inflate Fixture ListView  with number of fixtures 
 *   
 * @author Alkesh
 *
 */
public class FixturesListAdapter extends BaseAdapter {

    private Context mContext;
	private List<Fixtures> fixuresList;
	
	LayoutInflater inflater;
	AlertDialog.Builder dialogBuilder;
	DatabaseHandler dbHandler;
	DeleteFixtureDetector deleteDetector;
	
	public FixturesListAdapter(Context mContext, List<Fixtures> fixuresList, DeleteFixtureDetector detector){
		   this.mContext=mContext;
		   this.fixuresList=fixuresList;
		   this.deleteDetector = detector;
		   inflater=LayoutInflater.from(mContext);
		   dbHandler=new DatabaseHandler(mContext);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return fixuresList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return fixuresList.get(position);
		
	}
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		
		final Fixtures object=(Fixtures)getItem(position);
        if(convertView==null){
        	convertView=inflater.inflate(R.layout.listitem_layout_fixtures,parent,false);
        	
        	holder=new ViewHolder();
        	Typeface face = Typeface.createFromAsset(mContext.getAssets(), "gothic.ttf");
        	holder.attachmentImageView=(ImageView)convertView.findViewById(R.id.itemImageFixures);
        	
        	holder.fixturesNameTextView=(TextView)convertView.findViewById(R.id.itemFixuresName);
        	holder.fixturesNameTextView.setTypeface(face);
        	holder.fixturesDescriptionTextView=(TextView)convertView.findViewById(R.id.itemFixuresDiscription);
        	holder.fixturesDescriptionTextView.setTypeface(face);
        	
        	holder.fixturesCountTextView=(TextView)convertView.findViewById(R.id.itemFixuresCount);
        	
        	
        	holder.viewFlipper=(ViewFlipper)convertView.findViewById(R.id.listViewFlipper);
        	
            holder.viewFlipper.setInAnimation(mContext,R.animator.in_from_right);
            holder.viewFlipper.setOutAnimation(mContext,R.animator.out_to_right);
            holder.viewFlipper.setFlipInterval(1500);
             
            holder.deleteButton=(Button) convertView.findViewById(R.id.deleteButtonItem);
            holder.deleteButton.setTag(position);
            
        	convertView.setTag(holder);
        	
        }else{
			holder = (ViewHolder) convertView.getTag();
        }
        
        holder.fixturesNameTextView.setText(object.fixtureName);
        holder.fixturesDescriptionTextView.setText(object.code+" "+object.style+"\n"+object.mounting);
        holder.fixturesCountTextView.setText(object.fixtureCount);
        
        if(object.isVisible){
        	if(holder.viewFlipper.getDisplayedChild()==0){
				holder.viewFlipper.showNext();
			}
        }else{
        	if(holder.viewFlipper.getDisplayedChild()!=0){
				holder.viewFlipper.showPrevious();
			}
        }
        
        if(object.imagePath!=null){
        	File imageFile=new File(object.imagePath);
        	if(imageFile.exists()){
        		//Bitmap thumbnail = (BitmapFactory.decodeFile(object.imagePath));
        		Bitmap thumbnail =decodeFile(imageFile,70,70);
           	    holder.attachmentImageView.setImageBitmap(thumbnail);
        	}
        	else {
        		parent.removeView(holder.attachmentImageView);
        	}
        }
        
        else {
        	LinearLayout fixtureItem = (LinearLayout) convertView;
        	fixtureItem.removeView(holder.attachmentImageView);
        }
        
        holder.deleteButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showAlertDialog(position,object.fixtureId);
			}
		});
        
		return convertView;
	}
	public static class ViewHolder{
		TextView  fixturesNameTextView;
		TextView  fixturesDescriptionTextView;
		TextView  fixturesCountTextView;
		ImageView attachmentImageView;
		
		public ViewFlipper viewFlipper;
		Button deleteButton;
		
	}	
	
	public static Bitmap decodeFile(File f,int WIDTH,int HIGHT){
	     try {
	         //Decode image size
	         BitmapFactory.Options o = new BitmapFactory.Options();
	         o.inJustDecodeBounds = true;
	         BitmapFactory.decodeStream(new FileInputStream(f),null,o);

	         //The new size we want to scale to
	         final int REQUIRED_WIDTH=WIDTH;
	         final int REQUIRED_HIGHT=HIGHT;
	         //Find the correct scale value. It should be the power of 2.
	         int scale=1;
	         while(o.outWidth/scale/2>=REQUIRED_WIDTH && o.outHeight/scale/2>=REQUIRED_HIGHT)
	             scale*=2;

	         //Decode with inSampleSize
	         BitmapFactory.Options o2 = new BitmapFactory.Options();
	         o2.inSampleSize=scale;
	         return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
	         
	     } catch (FileNotFoundException e) {}
	     return null;
	 }
	

	public void showAlertDialog(final int position,final String fixid) {
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setMessage("Are you sure do you wan't delete Record!");
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				fixuresList.remove(position);
				int rowid=dbHandler.deleteFixture(fixid);
				Toast.makeText(mContext,
						"Recored Deleted successfully",
						Toast.LENGTH_SHORT).show();
				notifyDataSetChanged();
				deleteDetector.onDeleteFixture();
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
	
	public interface DeleteFixtureDetector{
		
		public void onDeleteFixture();
	}
}