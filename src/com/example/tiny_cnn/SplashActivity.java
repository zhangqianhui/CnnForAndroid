package com.example.tiny_cnn;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import jni.Predictor;
import jni.Predictor.ModelType.Type;
import jni.testCnn;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ProgressBar;

public class SplashActivity extends Activity{

	
	private  ProgressBar  dataLoadPro ;
    private loadDataNati loadDataHandler ;
    private static Predictor predictor ;
    public static Predictor getPredictor() {return predictor ;}
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splashlayout);
		myhandler.sendEmptyMessageDelayed(0 , 2000);	
		
		dataLoadPro = (ProgressBar)findViewById(R.id.dataLoadPro);
		loadDataHandler = new loadDataNati();
        new Thread(new loadDataThread()).start();
        /*启动新的线程来进行数据加载*/
        dataLoadPro.setVisibility(View.VISIBLE);
	}
	 @Override  
	 public void onBackPressed() {  
		 
		 	Intent intent = new Intent();
			intent.setAction(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_HOME);
			SplashActivity.this.startActivity(intent);
			
	 }  
	 
	 @SuppressLint("HandlerLeak") private Handler myhandler = new Handler()
		{
			public void handleMessage(android.os.Message msg) {
				
				   switch (msg.what) {
				   
				   case 0:
					   
					   Intent i = new Intent(SplashActivity.this , MainActivity.class);
					   startActivity(i);
					   SplashActivity.this.finish();
					  
					   break;
			
				   }
			}
		};
		
		 class loadDataNati extends Handler{

				@Override
				public void handleMessage(Message msg) {
					
					// TODO Auto-generated method stub
					super.handleMessage(msg);
					
					switch(msg.what)
					{
					case 0:
						
						dataLoadPro.setVisibility(View.GONE);
						
						/*create the predictor for tiny_cnn*/
						
						//predictor = new Predictor(new Predictor.ModelType(Type.TinyCNN));
					
						
						/*create the predictor for caffe */
						
						predictor = new Predictor(new Predictor.ModelType(Type.CAFFE));
						
						break;
					}
				} 
		 } 
		 public void deepFile(Context con , String path) 
	     { 
	         try 
	         { 
	            String str[] = con.getAssets().list(path);
	            
	            if (str.length > 0) { 
	            	
	         	   //如果是目录 
	               File file = new File(Environment.getExternalStorageDirectory() + "/"+path); 
	               file.mkdirs(); 
	               
	               for (String string : str) { 
	             	  
	                  path = path + "/" + string ; 
	                   // textView.setText(textView.getText()+"\t"+path+"\t"); 
	                  deepFile(con , path); 
	                  path = path.substring(0 , path.lastIndexOf('/')); 
	               } 
	               
	            } else
	            {
	         	   //如果是文件 
	                InputStream is = con.getAssets().open(path); 
	                File f = new  File(Environment.getExternalStorageDirectory() 
	                        + "/" + path);

	                FileOutputStream fos = new FileOutputStream(f); 
	                
	               byte[] buffer = new byte[1024]; 
	               int count = 0 ; 
	                 while (true) { 
	                   count++; 
	                   int len = is.read(buffer); 
	                    if (len == -1) { 
	                       break; 
	                  } 
	                  fos.write(buffer, 0, len); 
	              } 
	              is.close(); 
	              fos.close(); 
	          } 
	       } catch (IOException e) { 
	    	   
	            // TODO Auto-generated catch block 
	            e.printStackTrace(); 
	        } 
	     }
		 
		 class loadDataThread implements Runnable{

				@Override
				public void run() {
					
					// TODO Auto-generated method stub
					/*数据加载*/
					Message  msg = new Message();
					msg.what = 0 ;
					/*转义数据*/
					deepFile(getApplicationContext() , "tinyfile");
					/*写进xml*/
					loadDataHandler.sendMessage(msg);
					
				}
		     }

}


