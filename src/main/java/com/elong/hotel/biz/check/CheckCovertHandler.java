package com.elong.hotel.biz.check;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.elong.hotel.biz.IBizHandler;
import com.elong.hotel.biz.order.AgentNameClassifier;
import com.elong.hotel.constant.Const;
import com.elong.hotel.util.PropertiesHelper;
import com.elong.nb.OrderCheckStatistic;

public class CheckCovertHandler implements IBizHandler{

	private Set<String> codes = null;
	
	public CheckCovertHandler(){
		codes = new HashSet<String>();
		String errorCode = PropertiesHelper.getEnvPropertise("checkerrorCode", "GlobalConfig").toString();
		for(String code : errorCode.split(",")){
			codes.add(code);
		}
	}
	
	@Override
	public List<JSONObject> handle(JSONObject jsonObject) {
		List<JSONObject> rst = new LinkedList<JSONObject>();
		
		OrderCheckStatistic ocs = JSON.parseObject(jsonObject.toJSONString(), OrderCheckStatistic.class);
		Map<String, Object> dimensionKeyValue = new HashMap<String, Object>();
		dimensionKeyValue.put(Const.BUSINESS_TYPE, ocs.getBusiness_type());
		dimensionKeyValue.put(Const.LOG_TIME, ocs.getLog_time());
		
		dimensionKeyValue.put("agentNameGroupCheck", AgentNameClassifier.classify(ocs.getAgentId()));	//按分销商统计订单量
		dimensionKeyValue.put("nightTotal", ocs.getRoomNightsCount());		//间夜量
		dimensionKeyValue.put("orderCheckStatus", ocs.isOrderCheckStatus() ? "成功" : "失败");
		
		if (!ocs.isOrderCheckStatus() && StringUtils.isNotEmpty(ocs.getCheckFailureReason())){
			String reason = "其他";
			int index = ocs.getCheckFailureReason().indexOf("|");
			if (index != -1) {
				String code = ocs.getCheckFailureReason().substring(0, index);
				if (codes.contains(code)) reason = code;
			}
			dimensionKeyValue.put("checkFailureReason", reason);
		}
		
		JSONObject jsoncheck = new JSONObject(dimensionKeyValue);
		rst.add(jsoncheck);
		return rst;
	}

}
