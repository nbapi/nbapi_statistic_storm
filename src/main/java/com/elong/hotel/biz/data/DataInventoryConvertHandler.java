package com.elong.hotel.biz.data;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.elong.hotel.biz.IBizHandler;
import com.elong.hotel.biz.order.AgentNameClassifier;
import com.elong.hotel.constant.Const;
import com.elong.nb.DataInventoryPlanStatistic;

public class DataInventoryConvertHandler implements IBizHandler {

	@Override
	public List<JSONObject> handle(JSONObject jsonObject) {
		List<JSONObject> rst = new LinkedList<JSONObject>();
		DataInventoryPlanStatistic model = JSON.parseObject(jsonObject.toJSONString(), DataInventoryPlanStatistic.class);
		String businessType = model.getBusiness_type();
		businessType = StringUtils.substringBefore(businessType, "_");
		
		String invProxyId = AgentNameClassifier.classify(model.getInvProxyId());
		// inv数量
		Map<String, Object> invMap = new HashMap<String, Object>();
		invMap.put(Const.BUSINESS_TYPE, businessType);
		invMap.put(Const.LOG_TIME, model.getLog_time());
		invMap.put("invProxyId", invProxyId);
		invMap.put("count", model.getInvSize());
		JSONObject jsonObj = new JSONObject(invMap);
		rst.add(jsonObj);
		// inv状态
		Map<String, Object> roomStatusMap = new HashMap<String, Object>();
		roomStatusMap.put(Const.BUSINESS_TYPE, businessType);
		roomStatusMap.put(Const.LOG_TIME, model.getLog_time());
		roomStatusMap.put("invProxyId", invProxyId);
		roomStatusMap.put("invTrueCount", model.getInvTrueSize());
		roomStatusMap.put("invFalseCount", model.getInvFalseSize());
		jsonObj = new JSONObject(roomStatusMap);
		rst.add(jsonObj);
		return rst;
	}

}
