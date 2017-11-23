package com.elong.hotel.biz.incr;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
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
		String businessType = StringUtils.substringBefore(model.getBusiness_type(), "_");
		String incrType = StringUtils.substringAfter(model.getBusiness_type(), "_");

		Map<String, Object> dimensionKeyValue = new HashMap<String, Object>();
		dimensionKeyValue.put(Const.BUSINESS_TYPE, businessType);
		dimensionKeyValue.put(Const.LOG_TIME, model.getLog_time());

		String agentName = AgentNameClassifier.classify(model.getProxyId());
		dimensionKeyValue.put("agentNameGroup_" + incrType, agentName); // 按分销商统计订单量
		dimensionKeyValue.put("isEmptyStatus", model.isEmptyStatus() ? "empty count" : "no empty count");

		// 按分销商统计空查询
		if (model.isEmptyStatus()) {
			dimensionKeyValue.put("agentNameCount_" + incrType, agentName);
		}

		// 当前时间
		Date logTime = null;
		try {
			logTime = DateUtils.parseDate(model.getLog_time(), new String[] { "yyyy-MM-dd HH:mm:ss" });
		} catch (ParseException e) {
			e.printStackTrace();
		}
		// 查询延迟
		Date queryTime = null;
		try {
			queryTime = DateUtils.parseDate(model.getQueryTime(), new String[] { "yyyy-MM-dd HH:mm:ss" });
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if (queryTime != null) {
			long timeDiff = (logTime.getTime() - queryTime.getTime()) / (1000 * 60);
			dimensionKeyValue.put("minuteVal", timeDiff);
		}

		JSONObject jsonObj = new JSONObject(dimensionKeyValue);
		rst.add(jsonObj);
		return rst;
	}

}
