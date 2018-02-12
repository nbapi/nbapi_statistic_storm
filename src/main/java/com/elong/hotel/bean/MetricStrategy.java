package com.elong.hotel.bean;

import java.io.Serializable;

public enum MetricStrategy implements Serializable {

	simleAdd, fieldAdd, minuteAdd;

	public boolean isSimpleAdd() {
		return MetricStrategy.simleAdd.equals(this);
	}

	public boolean isFieldAdd() {
		return MetricStrategy.fieldAdd.equals(this);
	}

	public boolean isMinuteAdd() {
		return MetricStrategy.minuteAdd.equals(this);
	}

}
