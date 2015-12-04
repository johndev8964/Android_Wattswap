package com.citrusbug.wattswap.adapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.util.ArrayList;

import com.citrusbug.wattswap.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class CustomImageGrid extends BaseAdapter{
    
	private Context mContext;
    private final ArrayList<String> Imageid;
    
    public CustomImageGrid(Context c, ArrayList<String> Imageid ) {
          mContext = c;
          this.Imageid = Imageid;
    }
    
    @Override
    public int getCount() {
      // TODO Auto-generated method stub
      return Imageid.size();
    }
    
    @Override
    public Object getItem(int position) {
      // TODO Auto-generated method stub
      return null;
    }
    
    @Override
    public long getItemId(int position) {
      // TODO Auto-generated method stub
      return 0;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      // TODO Auto-generated method stub
      View grid;
      LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      
      if (convertView == null) {
    	  grid = new View(mContext);
    	  grid = inflater.inflate(R.layout.grid_single, null);
          ImageView imageView = (ImageView) grid.findViewById(R.id.grid_image);
          
          File imageFile=new File(Imageid.get(position));
      	  if(imageFile.exists()){
      		//Bitmap thumbnail = (BitmapFactory.decodeFile(object.imagePath));
      		Bitmap thumbnail =decodeFile(imageFile, 100, 100);
         	imageView.setImageBitmap(thumbnail);
      	  }

      } 
      else {
            grid = (View) convertView;
      }
      return grid;
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
}
