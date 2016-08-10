package jni;

import android.os.Environment;

public class testCnn {
	
	/*jni interface*/
	public static native int jniPredict(long addr , String model_addr);
	public static String tiny_file = Environment.getExternalStorageDirectory() + "/tinyfile/carlogo";
	public static String caffeModel = Environment.getExternalStorageDirectory() + "/tinyfile/gender.caffemodel";
	public static String caffePro = Environment.getExternalStorageDirectory() + "/tinyfile/deploy.prototxt";
	public static String caffeMean = Environment.getExternalStorageDirectory() + "/tinyfile/gender_mean.binaryproto";
	
	public static native int jniPredict2(long addr , String caffeModel , String caffePro);
}
