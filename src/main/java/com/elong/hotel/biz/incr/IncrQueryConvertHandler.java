package com.elong.hotel.biz.incr;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DateUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.elong.hotel.biz.IBizHandler;
import com.elong.hotel.biz.order.AgentNameClassifier;
import com.elong.hotel.constant.Const;
import com.elong.nb.IncrQueryStatistic;

public class IncrQueryConvertHandler implements IBizHandler {

	@Override
	public List<JSONObject> handle(JSONObject jsonObject) {
		List<JSONObject> rst = new LinkedList<JSONObject>();

		IncrQueryStatistic model = JSON.parseObject(jsonObject.toJSONString(), IncrQueryStatistic.class);
		Map<String, Object> dimensionKeyValue = new HashMap<String, Object>();
		dimensionKeyValue.put(Const.BUSINESS_TYPE, model.getBusiness_type() + "_" + model.getIncrType());
		dimensionKeyValue.put(Const.LOG_TIME, model.getLog_time());

		String agentName = AgentNameClassifier.classify(model.getProxyId());
		dimensionKeyValue.put("incrType", model.getIncrType());
		dimensionKeyValue.put("agentNameGroup", agentName); // 按分销商统计订单量
		dimensionKeyValue.put("isEmptyStatus", model.isEmptyStatus() ? "empty count" : "no empty count");

		// 按分销商统计空查询
		if (model.isEmptyStatus()) {
			dimensionKeyValue.put("agentNameCount", agentName);
		}

		// 查询延迟
		Date queryTime = null;
		try {
			queryTime = DateUtils.parseDate(model.getQueryTime(), new String[] { "yyyy-MM-dd HH:mm:ss" });
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if (queryTime != null) {
			Date now = new Date();
			long timeDiff = (now.getTime() - queryTime.getTime()) / (1000 * 60);
			dimensionKeyValue.put("timeDiff", timeDiff);
		}

		JSONObject jsonObj = new JSONObject(dimensionKeyValue);
		rst.add(jsonObj);
		return rst;
	}

}
