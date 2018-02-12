package com.elong.hotel.bolts;

import java.util.Map;
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

/**
 * 二维维度按照俩个维度分组
 * 
 */
public class TwoDimensionLogFilterBolt extends BaseRichBolt {

	private static final long serialVersionUID = 6902457132570522583L;

	private OutputCollector collector;

	protected Map<String, Map<String[], Set<Metric>>> dimensionMetricMap;

	@Override
	public void prepare(@SuppressWarnings("rawtypes") Map stormConf,
			TopologyContext context, OutputCollector collector) {
		this.collector = collector;
		dimensionMetricMap = DimensionMetricService
				.getDimensionMetricMapping("two");
	}

	@Override
	public void execute(Tuple input) {
		JSONObject jsonObj = (JSONObject) input.getValue(0);
		String module_key = jsonObj.getString(Const.BUSINESS_TYPE);
		if (module_key == null) return;
		if (!dimensionMetricMap.containsKey(module_key)) return;
		
		for (Entry<String[], Set<Metric>> e : dimensionMetricMap.get(module_key).entrySet()) {
			String str[] = e.getKey()[1].split("&");
			if (str.length == 2
					&& StringUtils.isNotEmpty(jsonObj.getString(str[0]))
					&& StringUtils.isNotEmpty(jsonObj.getString(str[1]))) {
				for (Metric metric : e.getValue()) {
					String metricJson = JSON.toJSON(metric).toString();
					collector.emit(new Values(module_key, str[0], str[1], metricJson,
							jsonObj));
				}
			}
		}
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("businessType", "dimesionOne", "dimesionTwo", "metric",
				"logJson"));
	}
}
