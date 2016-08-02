package jni;

import android.os.Environment;

public class testCnn {
	
	/*jni interface*/
	public static native int jniPredict(long addr , String model_addr);
	public static String model_file = Environment.getExternalStorageDirectory() + "/tinyfile/carlogo";
}
