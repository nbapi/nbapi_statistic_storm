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
import com.elong.hotel.bean.OneDimensionMinuteStaticResult;
import com.elong.hotel.constant.Const;
import com.elong.hotel.util.DateFormate;

/**
 * 计算实时数据的bolt
 * */

public class OneDimensionMinuteCountBolt extends BaseRichBolt {

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
			if (metric.getStrategy().isMinuteAdd())
				return;

			JSONObject jsonObj = (JSONObject) input.getValue(3);
			Date logTime = DateFormate.Formate(jsonObj.getString(Const.LOG_TIME));
			String timeRange = DateFormatUtils.format(logTime, "HH:mm");
			String dateTime = DateFormatUtils.format(logTime, DateFormate.YYYY_MM_DD_HH_MM);

			OneDimensionMinuteStaticResult staticBean = new OneDimensionMinuteStaticResult();

			staticBean.setBusinessType(businessType);
			staticBean.setTime(dateTime);
			staticBean.setDimension(dimensionKey);
			staticBean.setTimeRange(timeRange);
			staticBean.setMetric(metric.getName());
			staticBean.setDate(DateFormatUtils.format(logTime, DateFormate.YYYY_MM_DD));

			// 根据不同的方法相加
			long value = 0;
			if (metric.getStrategy().isSimpleAdd()) {
				value = 1;
			} else if (metric.getStrategy().isFieldAdd()) {
				value = Long.parseLong(jsonObj.getString(metric.getFields()));
			}

			staticBean.setDimensionItemName(jsonObj.getString(dimensionKey));
			staticBean.setDimensionItemValue(value);

			collector.emit(new Values(staticBean));
		} catch (Exception e) {
			boltErrorLogger.error(e.getMessage());
		}
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("field_minute_counter"));
	}

}
