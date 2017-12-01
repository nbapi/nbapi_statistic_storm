package com.elong.hotel.bolts;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

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

public class OneDimensionLogFilterBolt extends BaseRichBolt {

	private static final long serialVersionUID = 890798096085674932L;

	private Random rand = new Random();

	private OutputCollector collector;

	protected Map<String, Map<String[], Set<Metric>>> dimensionMetricMap;

	private List<Integer> minuteList;
	private List<Integer> otherList;

	@Override
	public void prepare(@SuppressWarnings("rawtypes") Map stormConf, TopologyContext context, OutputCollector collector) {
		this.collector = collector;
		dimensionMetricMap = DimensionMetricService.getDimensionMetricMapping("one");
		minuteList = context.getComponentTasks("log4-last-filter");
		otherList = context.getComponentTasks("log3-count-other");
	}

	@Override
	public void execute(Tuple input) {
		JSONObject jsonObj = (JSONObject) input.getValue(0);
		String module_key = jsonObj.getString(Const.BUSINESS_TYPE);
		if (module_key == null)
			return;
		if (!dimensionMetricMap.containsKey(module_key))
			return;

		for (Entry<String[], Set<Metric>> e : dimensionMetricMap.get(module_key).entrySet()) {
			String dimensionItemName = jsonObj.getString(e.getKey()[1]); // 判断是不是发给该统计维度的消息
			if (StringUtils.isEmpty(dimensionItemName))
				continue;
			for (Metric metric : e.getValue()) {
				String dimensionKey = e.getKey()[1];
				String metricJson = JSON.toJSON(metric).toString();
				Values values = new Values(module_key, dimensionKey, metricJson, jsonObj);
				if (metric.getStrategy().isMinuteAdd()) {
					int idx = rand.nextInt(minuteList.size());
					collector.emitDirect(minuteList.get(idx), values);
				} else {
					int idx = rand.nextInt(otherList.size());
					collector.emitDirect(otherList.get(idx), values);
				}
			}
		}
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(true, new Fields("businessType", "dimensionKey", "metric", "logJson"));
	}

}
