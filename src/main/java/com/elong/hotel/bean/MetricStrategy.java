package com.elong.hotel.bean;

import java.io.Serializable;

public enum MetricStrategy implements Serializable {
	simleAdd,
	fieldAdd;

	public boolean isSimpleAdd() {
		return MetricStrategy.simleAdd.equals(this);
	}

	public boolean isFieldAdd() {
		return MetricStrategy.fieldAdd.equals(this);
	}
}
