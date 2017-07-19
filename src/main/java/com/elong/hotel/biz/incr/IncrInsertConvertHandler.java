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
import com.elong.hotel.constant.Const;
import com.elong.nb.IncrInsertStatistic;

public class IncrInsertConvertHandler implements IBizHandler {

	@Override
	public List<JSONObject> handle(JSONObject jsonObject) {
		List<JSONObject> rst = new LinkedList<JSONObject>();
		IncrInsertStatistic model = JSON.parseObject(jsonObject.toJSONString(), IncrInsertStatistic.class);
		// 当前时间
		Date logTime = null;
		try {
			logTime = DateUtils.parseDate(model.getLog_time(), new String[] { "yyyy-MM-dd HH:mm:ss" });
		} catch (ParseException e) {
			e.printStackTrace();
		}
		// 主库插入时间
		Date masterInsertTime = null;
		try {
			masterInsertTime = DateUtils.parseDate(model.getInsertTime(), new String[] { "yyyy-MM-dd HH:mm:ss" });
		} catch (ParseException e) {
			e.printStackTrace();
		}
		// 数据变化时间
		Date masterChangeTime = null;
		try {
			masterChangeTime = DateUtils.parseDate(model.getChangeTime(), new String[] { "yyyy-MM-dd HH:mm:ss" });
		} catch (ParseException e) {
			e.printStackTrace();
		}
		// 从库插入时间
		Date slaveInsertTime = null;
		try {
			slaveInsertTime = DateUtils.parseDate(model.getSlaveInsertTime(), new String[] { "yyyy-MM-dd HH:mm:ss" });
		} catch (ParseException e) {
			e.printStackTrace();
		}
		// 插入延迟
		Map<String, Object> insertDelayMap = new HashMap<String, Object>();
		insertDelayMap.put(Const.BUSINESS_TYPE, model.getBusiness_type());
		insertDelayMap.put(Const.LOG_TIME, model.getLog_time());
		insertDelayMap.put("insertDelayIncrType", model.getIncrType());
		long insertDelayMinutes = (logTime.getTime() - masterInsertTime.getTime()) / (1000 * 60);
		insertDelayMap.put("timeDiff", insertDelayMinutes);
		JSONObject jsonObj = new JSONObject(insertDelayMap);
		rst.add(jsonObj);
		// 数据延迟
		Map<String, Object> changeDelayMap = new HashMap<String, Object>();
		changeDelayMap.put(Const.BUSINESS_TYPE, model.getBusiness_type());
		changeDelayMap.put(Const.LOG_TIME, model.getLog_time());
		changeDelayMap.put("changeDelayIncrType", model.getIncrType());
		long changeDelayMinutes = (masterInsertTime.getTime() - masterChangeTime.getTime()) / (1000 * 60);
		changeDelayMap.put("timeDiff", changeDelayMinutes);
		jsonObj = new JSONObject(changeDelayMap);
		rst.add(jsonObj);
		// 主从延迟
		Map<String, Object> masterSlaveDelayMap = new HashMap<String, Object>();
		masterSlaveDelayMap.put(Const.BUSINESS_TYPE, model.getBusiness_type());
		masterSlaveDelayMap.put(Const.LOG_TIME, model.getLog_time());
		masterSlaveDelayMap.put("masterSlaveDelayIncrType", model.getIncrType());
		long masterSlaveDelayMinutes = (masterInsertTime.getTime() - slaveInsertTime.getTime()) / (1000 * 60);
		masterSlaveDelayMap.put("timeDiff", masterSlaveDelayMinutes);
		jsonObj = new JSONObject(masterSlaveDelayMap);
		rst.add(jsonObj);
		return rst;
	}

}
