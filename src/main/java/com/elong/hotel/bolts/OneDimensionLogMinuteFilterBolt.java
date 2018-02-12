package com.elong.hotel.bolts;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.elong.hotel.bean.Metric;
import com.elong.hotel.constant.Const;
import com.elong.hotel.util.CustomUtil;
import com.elong.hotel.util.DateFormate;

public class OneDimensionLogMinuteFilterBolt extends BaseRichBolt {

	private static final long serialVersionUID = 1L;
	private OutputCollector collector;
	protected final static Logger boltErrorLogger = LoggerFactory.getLogger("bolt-error-log");

	@SuppressWarnings("rawtypes")
	@Override
	public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
		this.collector = collector;
	}

	@Override
	public void execute(Tuple input) {
		try {
			String businessType = input.getString(0);
			String dimensionKey = input.getString(1);
			String metricStr = input.getString(2);
			Metric metric = JSONObject.parseObject(metricStr, Metric.class);
			if (!metric.getStrategy().isMinuteAdd())
				return;

			JSONObject jsonObj = (JSONObject) input.getValue(3);
			Date logTime = DateFormate.Formate(jsonObj.getString(Const.LOG_TIME));
			String dateTime = DateFormatUtils.format(logTime, DateFormate.YYYY_MM_DD_HH_MM);

			Map<String, Object> keyMap = new HashMap<String, Object>();
			keyMap.put("collection", businessType);
			keyMap.put("dimension", dimensionKey);
			keyMap.put("dateTime", dateTime);
			keyMap.put("metric", metric.getName());
			keyMap.put("dimensionItem", "dimensionValue." + CustomUtil.deleteDot(jsonObj.getString(dimensionKey)));
			String fieldGroupingKey = DigestUtils.md5Hex(JSON.toJSONString(keyMap));
			collector.emit(new Values(fieldGroupingKey, businessType, dimensionKey, metricStr, jsonObj));
		} catch (Exception e) {
			boltErrorLogger.error(e.getMessage());
		}
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("fieldGroupingKey", "businessType", "dimensionKey", "metric", "logJson"));
	}

}
