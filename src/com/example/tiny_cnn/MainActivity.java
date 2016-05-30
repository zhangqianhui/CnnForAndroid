/*
 *The time: 2016/5/30
 * */

package com.example.tiny_cnn;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import jni.testCnn;

import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.util.CameraPreview;
import com.util.FillManager;
import com.util.imageDeal;

public class MainActivity extends Activity implements OnClickListener{

	private Camera mCamera ;
    private CameraPreview mPreview ;
    private Button login_button ;
    private final String TAG = "loginActivity"; 
    private FrameLayout preview ; 
	//private final String  scaleFile = Environment.getExternalStorageDirectory() +"/myfacedata/testcsvscalefile" ;
	private ProgressBar loginPro ;
	private myHandler controlMessage ;
	private Button open ;
	static {
		
	    if (!OpenCVLoader.initDebug()) {
	        // Handle initialization error
	    }
	    else
	    {
	    	System.loadLibrary("opencv_java"); 
	    	System.loadLibrary("tinycnn");
	    }
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		controlMessage = new myHandler();
		
		preview = (FrameLayout)findViewById(R.id.camera_preview);
		mCamera = getCameraInstance();
		setRightCameraOrientation(mCamera);
        mPreview = new CameraPreview(this , mCamera);
        preview.addView(mPreview);
        login_button = (Button)findViewById(R.id.login_button);
        login_button.setOnClickListener(this);
        open = (Button)findViewById(R.id.open);
        //open.setOnClickListener(this);
	}
	
	@SuppressLint("NewApi") @SuppressWarnings("deprecation")
	public static Camera getCameraInstance(){
		
	    Camera c = null;
	    
	    try {
	    	
	        c = Camera.open(FindFrontCamera()); // attempt to get a Camera instance
	    
	    }
	    catch (Exception e){
	        // Camera is not available (in use or does not exist)
	    }
	    
	    return c; // returns null if camera is unavailable
	}
    @SuppressWarnings("deprecation")
	@SuppressLint("NewApi") private static int FindFrontCamera(){  
    	
        int cameraCount = 0 ;  
        @SuppressWarnings("deprecation")
		Camera.CameraInfo cameraInfo = new Camera.CameraInfo();  
        cameraCount = Camera.getNumberOfCameras(); // get cameras number  
                
        for ( int camIdx = 0; camIdx < cameraCount;camIdx++ ) {  
            Camera.getCameraInfo( camIdx, cameraInfo ); // get camerainfo  
            if ( cameraInfo.facing ==Camera.CameraInfo.CAMERA_FACING_BACK ) {   
               return camIdx ;  
            }  
        }  
        
        return -1 ;  
    } 
    
    private void setRightCameraOrientation(Camera mCamera) {  
        
        CameraInfo info = new android.hardware.Camera.CameraInfo();  
        Camera.getCameraInfo(FindFrontCamera() , info);
        int rotation = this.getWindowManager().getDefaultDisplay()  
                .getRotation();  
        
        int degrees = 0;  
        
        switch (rotation) {  
        
        case Surface.ROTATION_0:  
        	
            degrees = 0;  
            
            break;  
            
        case Surface.ROTATION_90:  
        	
            degrees = 90;  
            
            break;  
            
        case Surface.ROTATION_180:  
        	
            degrees = 180;  
            
            break;  
            
        case Surface.ROTATION_270:  
        	
            degrees = 270;  
            
            break;  
        }  
        
        int result;  
        
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {  
        	
            result = (info.orientation + degrees) % 360;  
            result = (360 - result) % 360; // compensate the mirror  
            
        } else { 
        	// back-facing  
            result = (info.orientation - degrees + 360) % 360;  
        }  
        mCamera.setDisplayOrientation(result);  
    }
	@Override
	public void onClick(View v) {
		
		if(v.getId() == R.id.open)
		{
			Intent i = new Intent(  
                    Intent.ACTION_PICK,  
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);  

            startActivityForResult(i, 1);  
		}
		else
		mCamera.takePicture(null, null, mPicture);
	}
	
	private PictureCallback mPicture = new PictureCallback() {
		
	    @Override
	    public void onPictureTaken(byte[] data , Camera camera) {
            
	    	Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length); 
	        Bitmap rateBm = imageDeal.adjustPhotoRotation(bm , 90);
	    	File pictureFile = new File(FillManager.getInstance(getApplicationContext()).getImg());
	        if (pictureFile == null)
	        {	
	            return ;
	        }
	        try{
	        	
	            FileOutputStream fos = new FileOutputStream(pictureFile);
	            rateBm.compress(Bitmap.CompressFormat.JPEG , 90 , fos);
	            fos.flush();
	            fos.close();
	            
	        } catch (FileNotFoundException e) {
	        	
	            Log.d(TAG, "File not found: " + e.getMessage());
	            
	        } catch (IOException e) {
	        	
	            Log.d(TAG, "Error accessing file: " + e.getMessage());
	        }
	        catch(NullPointerException  e)
	        {
	        	e.printStackTrace();
	        	
	        }
	        catch(Exception e){
	        	
	        	e.printStackTrace();
	        }
	        
	        mCamera.startPreview();
	        
	        new Thread(new newThread("")).start();
	       
	    }
	};
	
	@SuppressLint("HandlerLeak") class myHandler extends Handler{

		@Override
		public void handleMessage(Message msg) {
			
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			
			switch(msg.what)
			{
			case 0:
				
				Toast.makeText(MainActivity.this ,"cost time"+msg.arg1 , Toast.LENGTH_SHORT).show();
				if(msg.arg2 == 0)
				Toast.makeText(MainActivity.this ,"This car is Volkswagen car", Toast.LENGTH_SHORT).show();
				else
				{
					Toast.makeText(MainActivity.this ,"Thia car is not Volkswagen car", Toast.LENGTH_SHORT).show();
				}
				break ; 
			}
		}
	}

	class newThread implements Runnable{
		
		String str ;
		@Override
		public void run() {
			
			// TODO Auto-generated method stub
			
			long start = System.currentTimeMillis();
			String path = FillManager.getInstance(MainActivity.this).getImg();
			//String path = str;
			Mat mat = Highgui.imread(path , 0);
			Imgproc.resize(mat , mat , new Size(100 , 100));
			int result = testCnn.jniPredict(mat.nativeObj , testCnn.model_file);
			long end = System.currentTimeMillis();
			Message msg = new Message();
			msg.what = 0 ;
			msg.arg1 = (int)(end - start);
			msg.arg2 = result ;
			controlMessage.sendMessage(msg);
		}
		
		public newThread(String path)
		{
			str = path ;
		}
	}
	
	@Override  
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
        super.onActivityResult(requestCode, resultCode, data);  
   
        if (requestCode == 1 && resultCode == RESULT_OK && null != data) {  
            Uri selectedImage = data.getData();  
            String[] filePathColumn = { MediaStore.Images.Media.DATA };  
   
            Cursor cursor = getContentResolver().query(selectedImage,  
                    filePathColumn, null, null, null);  
            cursor.moveToFirst();  
   
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);  
            String picturePath = cursor.getString(columnIndex);  
            cursor.close();  
           
            
        }  
   
    }  
		
}
