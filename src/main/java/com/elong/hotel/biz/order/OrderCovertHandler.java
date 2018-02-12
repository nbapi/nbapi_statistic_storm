package com.elong.hotel.biz.order;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.elong.hotel.biz.IBizHandler;
import com.elong.hotel.constant.Const;
import com.elong.hotel.util.PropertiesHelper;
import com.elong.nb.OrderSubmitStatistic;

public class OrderCovertHandler implements IBizHandler {

	private Map<String, String> cities = null;
	private Set<String> codes = null;
	
	public OrderCovertHandler() {
		cities = new HashMap<String, String>();
		Properties p = PropertiesHelper.getEnvPropertise("city");
		for (Entry<Object, Object> e : p.entrySet()) {
			cities.put((String) e.getKey(), (String) e.getValue());
		}
		
		codes = new HashSet<String>();
		String errorCode = PropertiesHelper.getEnvPropertise("errorCode", "GlobalConfig").toString();
		for(String code : errorCode.split(",")){
			codes.add(code);
		}
	}

	public List<JSONObject> handle(JSONObject jsonObject){
		List<JSONObject> rst = new LinkedList<JSONObject>();
		
		OrderSubmitStatistic oss = JSON.parseObject(jsonObject.toJSONString(), OrderSubmitStatistic.class);
		
		Map<String, Object> dimensionKeyValue = new HashMap<String, Object>();
		dimensionKeyValue.put(Const.BUSINESS_TYPE, oss.getBusiness_type());
		dimensionKeyValue.put(Const.LOG_TIME, oss.getLog_time());
		
		dimensionKeyValue.put("agentNameGroupSubmit", AgentNameClassifier.classify(oss.getAgentId()));	//按分销商统计订单量
		dimensionKeyValue.put("nightTotal", oss.getRoomNightsCount());		//间夜量
		dimensionKeyValue.put("orderSubmitStatus", oss.isOrderSubmitStatus() ? "成功" : "失败");
		
		if (oss.isOrderSubmitStatus()){
			dimensionKeyValue.put("idcSubmit", StringUtils.startsWith(oss.getServerIp(), "172") || StringUtils.startsWith(oss.getServerIp(), "10.88.") ? "IDC1" : "IDC2");
			dimensionKeyValue.put("paymentTypeSubmit", oss.getPaymentType());
		}
		
		if (!oss.isOrderSubmitStatus() && StringUtils.isNotEmpty(oss.getSubmitFailureReason())){
			String reason = "其他";
			int index = oss.getSubmitFailureReason().indexOf("|");
			if (index != -1) {
				String code = oss.getSubmitFailureReason().substring(0, index);
				if (codes.contains(code)) reason = code;
			}
			dimensionKeyValue.put("submitFailureReason", reason);
		}
		
		//预订省份
		if (oss.isOrderSubmitStatus() && StringUtils.isNotEmpty(oss.getCityId())){
			String cityName = cities.get(oss.getCityId().substring(0,2) + "00");
			dimensionKeyValue.put("cityName", StringUtils.isEmpty(cityName) ? "其他" : cityName);
		}
		
		// 预订距离天数
		Date arrivalDate = null;
		try {
			arrivalDate = DateUtils.parseDate(oss.getArrivalDate(), new String[] {"yyyy-MM-dd HH:mm:ss"});
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if (arrivalDate != null){
			Date now = new Date();
			long between_days = (arrivalDate.getTime() - now.getTime()) / (1000*3600*24);
			String inDays = null;
			if (between_days <= 3) inDays = "三天内";
			else if (between_days > 3 && between_days <= 7) inDays = "三到七天";
			else if (between_days > 7 && between_days <= 15) inDays = "七到十五天";
			else if (between_days > 15 && between_days <=30) inDays = "十五到一月";
			else inDays = "一月以上";
			dimensionKeyValue.put("inDays", inDays);
		}

		JSONObject jsonsubmit = new JSONObject(dimensionKeyValue);
		
		rst.add(jsonsubmit);
		return rst;
	}
}
