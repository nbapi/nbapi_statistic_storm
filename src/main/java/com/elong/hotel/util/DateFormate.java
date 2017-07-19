package com.elong.hotel.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 将字符串类型的日期转换成date类型的日期格式 将日期加上相应的天数
 */
public class DateFormate {

	public final static String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

	public final static String YYYY_MM_DD = "yyyy-MM-dd";

	public final static String YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";

	public static Date Formate(String time) {
		return Formate(time, YYYY_MM_DD_HH_MM_SS);
	}

	public static Date Formate(String time, String pattern) {
		SimpleDateFormat spf = new SimpleDateFormat(pattern);
		Date date = null;
		try {
			date = spf.parse(time);

		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	public static String getDateStr(String day, int dayAddNum) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date nowDate = null;
		try {
			nowDate = df.parse(day);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Date newDate = new Date(nowDate.getTime() - dayAddNum * 24 * 60 * 60
				* 1000);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String newDay = simpleDateFormat.format(newDate);
		return newDay;
	}

	public static String convertDate(Date date, String pattern) {
		SimpleDateFormat df;
		String returnValue = "";

		if (date != null) {
			df = new SimpleDateFormat(pattern);
			returnValue = df.format(date);
		}
		return returnValue;
	}
}
