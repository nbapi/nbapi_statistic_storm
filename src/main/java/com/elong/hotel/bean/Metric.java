package com.elong.hotel.bean;

import java.io.Serializable;

public class Metric implements Serializable{
	@Override
	public String toString() {
		return "Metric [name=" + name + ", strategy=" + strategy + ", fields="
				+ fields + "]";
	}

	private static final long serialVersionUID = -2848162845960983587L;
	
	private String name;
	private MetricStrategy strategy;
	private String fields;
	
	public Metric() {}
	

	public Metric(MetricStrategy strategy,String name) {
		this.strategy = strategy;
		this.name = name;
	}
	
	public Metric(MetricStrategy strategy,String name, String fields) {
		this.strategy = strategy;
		this.name = name;
		this.fields = fields;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public MetricStrategy getStrategy() {
		return strategy;
	}
	public void setStrategy(MetricStrategy strategy) {
		this.strategy = strategy;
	}

	public String getFields() {
		return fields;
	}

	public void setFields(String fields) {
		this.fields = fields;
	}
	
}
