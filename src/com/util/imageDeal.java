package com.util;

import org.opencv.android.Utils;
import org.opencv.core.CvException;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Range;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Environment;
import android.widget.Toast;

public class imageDeal {
		
	public static Mat FaceDector(Mat matofbyte) {
		
		Mat faceMat = null ;
		//Mat[] mat = new Mat[10];
		String facestr = Environment.getExternalStorageDirectory()+"/tinyfile/haarcascade_frontalface_alt2.xml";
		CascadeClassifier detector =  new CascadeClassifier(facestr);
        MatOfRect faceDetections = new MatOfRect();
        detector.detectMultiScale(matofbyte , faceDetections , 1.1 , 2 , 0 , new Size(10 , 10) , new Size(300 , 300));
        if(faceDetections.toArray().length != 0)
        {
        	Rect rect = faceDetections.toArray()[0];
        	faceMat = new Mat(matofbyte , new Range(rect.y , rect.y + rect.height)  , new Range(rect.x , rect.x + rect.width));
    		//result = Bitmap.createBitmap(oldBit , rect.x  , rect.y , rect.width   , rect.height);
    		//Toast.makeText(context , "execute2" , Toast.LENGTH_SHORT).show();
        }
        else
        {
        	return matofbyte;
        }
        //Size s = new Size(100 , 100);
        //Imgproc.resize(faceMat , faceMat, s);
        return faceMat; 
	}

//	/*rotate the Image View*/
//	 public static Bitmap adjustPhotoRotation(Bitmap bm , final int orientationDegree)
//	 {
//	         Matrix m = new Matrix();
//	         m.setRotate(orientationDegree , (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
//	         float targetX, targetY;
//	         if (orientationDegree == 90) {
//	        	 
//	        	 targetX = bm.getHeight();
//	        	 targetY = 0;
//	         } 
//	         else {
//	        	 
//	         targetX = bm.getHeight();
//	         targetY = bm.getWidth();
//	         
//		     }
//		     final float[] values = new float[9];
//		     m.getValues(values);
//	
//		     float x1 = values[Matrix.MTRANS_X];
//		     float y1 = values[Matrix.MTRANS_Y];
//	
//		     m.postTranslate(targetX - x1, targetY - y1);
//		     Bitmap bm1 = Bitmap.createBitmap(bm.getHeight() , bm.getWidth(), Bitmap.Config.ARGB_8888);
//	
//		     Paint paint = new Paint();
//		     Canvas canvas = new Canvas(bm1);
//		     canvas.drawBitmap(bm , m , paint); 
//	     
//	     return bm1 ; 
//	 }
	 
	 static Bitmap adjustPhotoRotation(Bitmap bm , final int orientation)
	 {
		 Matrix m = new Matrix();
		 m.setRotate(orientation, bm.getWidth()/2 , bm.getHeight()/2);
		 try{
			 
			 Bitmap bm1 = Bitmap.createBitmap(bm , 0 , 0 , bm.getWidth() , bm.getHeight() , m , true);
			 return bm1 ;
			 
		 }catch(OutOfMemoryError ex)
		 {
			 
		 }
		 
		 return null;
	 }
	 
	 public static Mat matConvertBit(Bitmap bitmap) throws NullPointerException
	 {
		    Mat tmp ;
		    
			try{
				
				/*旋转90度*/
				bitmap = adjustPhotoRotation(bitmap , 90);
				/*缩小*/
				//bitmap = small(bitmap);
				/*先转化为mat*/
				tmp = new Mat(bitmap.getHeight() , bitmap.getWidth() , CvType.CV_16UC4 , new Scalar(4));
				Utils.bitmapToMat(bitmap , tmp);
				/**/
				Imgproc.cvtColor(tmp , tmp , Imgproc.COLOR_RGB2BGR);
				Size s = new Size(350 , 400);
				Imgproc.resize(tmp ,tmp , s);
				/*normalizes the brightness and increases the contrast of the image*/
				/*增强对比度*/
				//Imgproc.equalizeHist(tmp , tmp);
			}
			catch(CvException e)
			{
				e.printStackTrace();
				return null ;
			}
			
			return FaceDector(tmp);
		}
}
