package com.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

public class imageDeal {

	/*rotate the Image View*/
	 public static Bitmap adjustPhotoRotation(Bitmap bm , final int orientationDegree)
	 {
	         Matrix m = new Matrix();
	         m.setRotate(orientationDegree, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
	         float targetX, targetY;
	         if (orientationDegree == 90) {
	        	 
	         targetX = bm.getHeight();
	         targetY = 0;
	         
	         } 
	         else {
	        	 
	         targetX = bm.getHeight();
	         targetY = bm.getWidth();
	         
		     }
		     final float[] values = new float[9];
		     m.getValues(values);
	
		     float x1 = values[Matrix.MTRANS_X];
		     float y1 = values[Matrix.MTRANS_Y];
	
		     m.postTranslate(targetX - x1, targetY - y1);
		     Bitmap bm1 = Bitmap.createBitmap(bm.getHeight() , bm.getWidth(), Bitmap.Config.ARGB_8888);
	
		     Paint paint = new Paint();
		     Canvas canvas = new Canvas(bm1);
		     canvas.drawBitmap(bm , m , paint); 
	     
	     return bm1 ; 
	 }
	
}
