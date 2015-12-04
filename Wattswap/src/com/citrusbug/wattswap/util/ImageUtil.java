package com.citrusbug.wattswap.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;

public class ImageUtil {	
	/**
	 * This method used to decode Image file as per Height and Width which is provided in argument
	 * @param file should be image file
	 * @param WIDTH should be int width of image. 
	 * @param HIGHT should be int Height of image.
	 * @return an object of Bitmap 
	 */
	public static Bitmap decodeFile(File file, int WIDTH, int HIGHT) {
		try {
			// Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(file), null, o);

			// The new size we want to scale to
			final int REQUIRED_WIDTH = WIDTH;
			final int REQUIRED_HIGHT = HIGHT;
			// Find the correct scale value. It should be the power of 2.
			int scale = 1;
			while (o.outWidth / scale / 2 >= REQUIRED_WIDTH
					&& o.outHeight / scale / 2 >= REQUIRED_HIGHT)
				scale *= 2;

			// Decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			return BitmapFactory.decodeStream(new FileInputStream(file), null, o2);

		} catch (FileNotFoundException e) {
		}
		return null;
	}
    
	public static void saveResizedImage(String imagePath, boolean isCamera){
		 File resizeFile = new File(imagePath);
		 Bitmap bm=ImageUtil.decodeFile(new File(imagePath), 600, 1000);
		 
		 Matrix matrix = new Matrix();
		 matrix.postRotate(90);
		 Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
		 
		 
		try {
			FileOutputStream out = new FileOutputStream(resizeFile);
			if(isCamera) {
				resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
			}
			else {
				bm.compress(Bitmap.CompressFormat.JPEG, 90, out);
			}
            out.flush();
            out.close();
            
		} catch (IOException e) {
			Log.e("BROKEN", "Could not write file " + e.getMessage());
	    }
	}
	
	public static String getValidName(String path){
		return path.replace(" ","_");
	}
}
