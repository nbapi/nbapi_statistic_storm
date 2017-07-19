package com.elong.hotel.bean;

import java.io.Serializable;


/**
 * 数据模型
 * 
 * @author junwei.yang
 * @version 1.0
 */
public class OneDimensionMinuteStaticResult implements Serializable {

	private static final long serialVersionUID = 4872775265268797011L;

	private String businessType;
	private String time;
	private String dimension;
	private String timeRange;
	private String metric;
	
	private String date;
	private String dimensionItemName = "";
	private Long dimensionItemValue = 0l;

	public OneDimensionMinuteStaticResult() {
	}
	
	public String getBusinessType() {
		return businessType;
	}

	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getDimension() {
		return dimension;
	}

	public void setDimension(String dimension) {
		this.dimension = dimension;
	}

	public String getTimeRange() {
		return timeRange;
	}

	public void setTimeRange(String timeRange) {
		this.timeRange = timeRange;
	}

	public String getMetric() {
		return metric;
	}

	public void setMetric(String metric) {
		this.metric = metric;
	}

	/**
     * @return the date
     */
    public String getDate() {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(String date) {
        this.date = date;
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
	
    @Override
	public String toString() {
		return "StaticBean [time=" + getTime() + ", dimension="
				+ getDimension() + ", timeRange=" + getTimeRange()
				+ ", metric=" + getMetric() + ", dimensionValue="
				+ getDimensionItemValue() +"]";
	}
}
