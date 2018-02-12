package com.elong.hotel.util;

import org.apache.commons.lang.StringUtils;

public class CustomUtil {

	public static String deleteDot(String item) {
		if (StringUtils.isBlank(item)) {
			return "";
		}
		return item.replace(".", "");
	}
}
