package com.elong.hotel.bolts;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;

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
import com.elong.hotel.service.DimensionMetricService;
import com.elong.hotel.util.CustomUtil;
import com.elong.hotel.util.DateFormate;

public class OneDimensionLogFilterBolt extends BaseRichBolt {

	private static final long serialVersionUID = 890798096085674932L;

	private OutputCollector collector;

	protected Map<String, Map<String[], Set<Metric>>> dimensionMetricMap;

	@Override
	public void prepare(@SuppressWarnings("rawtypes") Map stormConf, TopologyContext context, OutputCollector collector) {
		this.collector = collector;
		dimensionMetricMap = DimensionMetricService.getDimensionMetricMapping("one");
	}

	@Override
	public void execute(Tuple input) {
		JSONObject jsonObj = (JSONObject) input.getValue(0);
		String module_key = jsonObj.getString(Const.BUSINESS_TYPE);
		if (module_key == null)
			return;
		if (!dimensionMetricMap.containsKey(module_key))
			return;

		Date logTime = DateFormate.Formate(jsonObj.getString(Const.LOG_TIME));
		String dateTime = DateFormatUtils.format(logTime, DateFormate.YYYY_MM_DD_HH_MM);

		for (Entry<String[], Set<Metric>> e : dimensionMetricMap.get(module_key).entrySet()) {
			String dimensionItemName = jsonObj.getString(e.getKey()[1]); // 判断是不是发给
																			// 该统计维度的消息
			if (StringUtils.isNotEmpty(dimensionItemName)) {
				for (Metric metric : e.getValue()) {
					String dimensionKey = e.getKey()[1];
					Map<String, Object> fieldGroupingMap = new HashMap<String, Object>();
					fieldGroupingMap.put("collection", module_key);
					fieldGroupingMap.put("dimension", dimensionKey);
					fieldGroupingMap.put("dateTime", dateTime);
					fieldGroupingMap.put("metric", metric.getName());
					fieldGroupingMap.put("dimensionItem", "dimensionValue." + CustomUtil.deleteDot(dimensionItemName));
					String fieldGroupingKey = JSON.toJSONString(fieldGroupingMap);
					String metricJson = JSON.toJSON(metric).toString();
					collector.emit(new Values(fieldGroupingKey, module_key, dimensionKey, metricJson, jsonObj));
				}
			}
		}
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("fieldGroupingKey", "businessType", "dimensionKey", "metric", "logJson"));
	}

}
