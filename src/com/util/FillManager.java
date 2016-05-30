package com.util;

import java.io.File;

import android.content.Context;
import android.os.Environment;

public class FillManager {
	
	private static String FileDir = "/tinyfile/" ;
	private static FillManager instance ;
	
	public static FillManager getInstance(Context con)
	{
		if(instance == null)
		{
			instance = new FillManager(con);
		}
		return instance ;
	}
	private  FillManager(Context con)
	{
		/*save*/
	}

	public String getImg()
	{
		createDir();
		return Environment.getExternalStorageDirectory()+FileDir+"test2.png";
	}
	void createDir()
	{
		File f = new File(Environment.getExternalStorageDirectory()+FileDir);
		if(!f.exists())
		{
			f.mkdir();
		}
	}


}
