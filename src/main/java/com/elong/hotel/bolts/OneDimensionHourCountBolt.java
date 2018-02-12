package com.elong.hotel.bolts;

import java.util.Date;
import java.util.Map;

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

import com.alibaba.fastjson.JSONObject;
import com.elong.hotel.bean.Metric;
import com.elong.hotel.bean.OneDimensionHourStaticResult;
import com.elong.hotel.constant.Const;
import com.elong.hotel.util.DateFormate;

/**
 * 对从LogFilterBolt中传递过来的数据进行封装 emit 的是resultMap
 * resultMap是以yearMonthDate(时间)+dimension(维度)+hourRange(时间段)为key 以statisticResul
 * t 为value 数据传递到MongoBolt进行数据库的操作
 */

public class OneDimensionHourCountBolt extends BaseRichBolt {

	private static final long serialVersionUID = 1L;
	
	private final static Logger boltErrorLogger = LoggerFactory
			.getLogger("bolt-error-log");
	
	private OutputCollector collector;

	@SuppressWarnings("rawtypes")
	@Override
	public void prepare(Map stormConf, TopologyContext context,
			OutputCollector collector) {
		this.collector = collector;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void execute(Tuple input) {
		try {
			String businessType = input.getString(0);
			String dimensionKey = input.getString(1);
			String metricStr = input.getString(2);
			Metric metric = JSONObject.parseObject(metricStr, Metric.class);
			JSONObject jsonObj = (JSONObject) input.getValue(3);
			String log_time = jsonObj.getString(Const.LOG_TIME);

			OneDimensionHourStaticResult statisticResult = new OneDimensionHourStaticResult();
			statisticResult.setBusinessType(businessType);
			statisticResult.setDimension(dimensionKey);
			Date logTime = DateFormate.Formate(log_time);
			Integer hourRange = logTime.getHours();
			statisticResult.setHourRange(hourRange);

			statisticResult.setDate(DateFormatUtils.format(logTime,
					DateFormate.YYYY_MM_DD));
			statisticResult.setMetric(metric.getName());

			long value = 0;
			if (metric.getStrategy().isSimpleAdd()) {
				value = 1;
			} else if (metric.getStrategy().isFieldAdd()) {
				value = Long.parseLong(jsonObj.getString(metric.getFields()));
			}

			statisticResult.setDimensionItemName(jsonObj
					.getString(dimensionKey));
			statisticResult.setDimensionItemValue(value);

			collector.emit(new Values(statisticResult));
		} catch (Exception e) {
			boltErrorLogger.error(e.getMessage());
		}
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("logip-counter"));
	}

}
