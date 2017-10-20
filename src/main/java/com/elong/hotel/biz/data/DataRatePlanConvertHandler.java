package com.elong.hotel.biz.data;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.elong.hotel.biz.IBizHandler;
import com.elong.hotel.biz.order.AgentNameClassifier;
import com.elong.hotel.constant.Const;
import com.elong.nb.DataRatePlanStatistic;

public class DataRatePlanConvertHandler implements IBizHandler {

	@Override
	public List<JSONObject> handle(JSONObject jsonObject) {
		List<JSONObject> rst = new LinkedList<JSONObject>();
		DataRatePlanStatistic model = JSON.parseObject(jsonObject.toJSONString(), DataRatePlanStatistic.class);
		
		String proxyid = AgentNameClassifier.classify(model.getProxyId());
		// rp数量
		Map<String, Object> ratePlanMap = new HashMap<String, Object>();
		ratePlanMap.put(Const.BUSINESS_TYPE, model.getBusiness_type());
		ratePlanMap.put(Const.LOG_TIME, model.getLog_time());
		ratePlanMap.put("proxyid", proxyid);
		ratePlanMap.put("count", model.getRatePlanSize());
		JSONObject jsonObj = new JSONObject(ratePlanMap);
		rst.add(jsonObj);
		// 房型状态
		Map<String, Object> roomStatusMap = new HashMap<String, Object>();
		roomStatusMap.put(Const.BUSINESS_TYPE, model.getBusiness_type());
		roomStatusMap.put(Const.LOG_TIME, model.getLog_time());
		roomStatusMap.put("proxyid", proxyid);
		roomStatusMap.put("roomTrueCount", model.getRoomTrueSize());
		roomStatusMap.put("roomFalseCount", model.getRoomFalseSize());
		jsonObj = new JSONObject(roomStatusMap);
		rst.add(jsonObj);
		return rst;
	}

}
