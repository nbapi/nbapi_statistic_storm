package com.elong.hotel.bean;

import java.io.Serializable;

/**
 * 数据模型
 * 
 * @author junwei.yang
 * 
 */
public class TwoDimensionHourStaticResult implements Serializable {

	private static final long serialVersionUID = -1719519984582309379L;

	private String businessType;
	private String time;
	private String dimension;
	private Integer hourRange;
	private String metric;

	private String oneDimensionItemName;
	private String twoDimensionItemName;
	private Integer dimensionItemValue;
	
	public String getBusinessType() {
		return businessType;
	}

	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}

	public String getMetric() {
		return metric;
	}

	public void setMetric(String metric) {
		this.metric = metric;
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

	public Integer getHourRange() {
		return hourRange;
	}

	public void setHourRange(Integer hourRange) {
		this.hourRange = hourRange;
	}

	/**
	 * 得到oneDimensionItemName的值
	 * 
	 * @return oneDimensionItemName的值
	 */
	public String getOneDimensionItemName() {
		return oneDimensionItemName;
	}

	/**
	 * 设置oneDimensionItemName的值
	 * 
	 * @param oneDimensionItemName
	 *            被设置的值
	 */
	public void setOneDimensionItemName(String oneDimensionItemName) {
		this.oneDimensionItemName = oneDimensionItemName;
	}

	/**
	 * 得到twoDimensionItemName的值
	 * 
	 * @return twoDimensionItemName的值
	 */
	public String getTwoDimensionItemName() {
		return twoDimensionItemName;
	}

	/**
	 * 设置twoDimensionItemName的值
	 * 
	 * @param twoDimensionItemName
	 *            被设置的值
	 */
	public void setTwoDimensionItemName(String twoDimensionItemName) {
		this.twoDimensionItemName = twoDimensionItemName;
	}

	/**
	 * 得到dimensionItemValue的值
	 * 
	 * @return dimensionItemValue的值
	 */
	public Integer getDimensionItemValue() {
		return dimensionItemValue;
	}

	/**
	 * 设置dimensionItemValue的值
	 * 
	 * @param dimensionItemValue
	 *            被设置的值
	 */
	public void setDimensionItemValue(Integer dimensionItemValue) {
		this.dimensionItemValue = dimensionItemValue;
	}

	@Override
	public String toString() {
		return "ResultBean [time=" + time + ", dimension=" + dimension
				+ ", hourRange=" + hourRange + ", metric=" + metric + "]";
	}

}
