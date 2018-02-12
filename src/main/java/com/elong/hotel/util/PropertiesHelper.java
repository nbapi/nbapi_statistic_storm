package com.elong.hotel.util;

import java.io.InputStream;
import java.util.Properties;

public class PropertiesHelper {
	
	public  static Object getEnvPropertise(String key, String proFileName) {
		Properties pro = new Properties();
		try {
			InputStream is = PropertiesHelper.class.getClassLoader()
					.getResourceAsStream("conf/custom/env/"+proFileName + ".properties");
			pro.load(is);
		} catch (Exception e) { 
			e.printStackTrace();
			System.out.println("get properties fail, the file is " + proFileName + ", the error message is " + e.getMessage());
		}
		
		return pro.get(key);
	}
	
	public static  Properties getEnvPropertise(String proFileName) {
		Properties pro = new Properties();
		try {
			InputStream is = PropertiesHelper.class.getClassLoader()
						.getResourceAsStream("conf/custom/env/"+proFileName + ".properties");
			pro.load(is);
		} catch (Exception e) { 
			e.printStackTrace();
			System.out.println("get properties fail, the file is " + proFileName + ", the error message is " + e.getMessage());
		}	
		return pro;
	}
}