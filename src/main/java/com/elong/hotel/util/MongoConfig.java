package com.elong.hotel.util;

import java.util.Properties;

public class MongoConfig {
	private static String ip;
	private static Integer port;
	private static String dbName;

	static {
		Properties dimensionProp = PropertiesHelper
				.getEnvPropertise("GlobalConfig");
		ip = dimensionProp.getProperty("ip");
		dbName = dimensionProp.getProperty("dbName");
		String strport = dimensionProp.getProperty("port");
		port = Integer.parseInt(strport);
	}

	private MongoConfig() {}

	public static String getIp() {
		return ip;
	}

	public static String getDbName() {
		return dbName;
	}

	public static Integer getPort() {
		return port;
	}
}
