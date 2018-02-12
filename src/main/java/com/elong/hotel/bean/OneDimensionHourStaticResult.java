package com.elong.hotel.bean;

import java.io.Serializable;

public class OneDimensionHourStaticResult implements Serializable {
	
	private static final long serialVersionUID = 1574767264509424104L;

	/**
	 * time 时间(具体某一天) dimension 维度 hourRange 时间间隔("0-1": 1) dimensionValue
	 * 具体维度下的不同的值
	 */
	private String businessType;
	private String date;
	private String dimension;
	private Integer hourRange;
	private String metric;
	private String dimensionItemName = "";
	private Long dimensionItemValue = 0l;

	public String getBusinessType() {
		return businessType;
	}

	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getDimension() {
		return dimension;
	}

	public void setDimension(String dimension) {
		this.dimension = dimension;
	}

	public Integer getHourRange() {
		return hourRange;
	}

	public void setHourRange(Integer hourRange) {
		this.hourRange = hourRange;
	}

	@Override
	public String toString() {
		return "StatisticResult [date=" + date + ", dimension=" + dimension
				+ ", hourRange=" + hourRange + ", dimensionValue="
				+ ", metric=" + metric + "]";
	}

	public String getMetric() {
		return metric;
	}

	public void setMetric(String metric) {
		this.metric = metric;
	}

	/**
	 * @return the dimensionItemName
	 */
	public String getDimensionItemName() {
		return dimensionItemName;
	}

	/**
	 * @param dimensionItemName
	 *            the dimensionItemName to set
	 */
	public void setDimensionItemName(String dimensionItemName) {
		this.dimensionItemName = dimensionItemName;
	}

	/**
	 * @return the dimensionItemValue
	 */
	public Long getDimensionItemValue() {
		return dimensionItemValue;
	}

	/**
	 * @param dimensionItemValue
	 *            the dimensionItemValue to set
	 */
	public void setDimensionItemValue(Long dimensionItemValue) {
		this.dimensionItemValue = dimensionItemValue;
	}

}
