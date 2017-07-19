package com.elong.hotel.bolts;

import java.util.Date;
import java.util.Map;

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
import com.elong.hotel.bean.TwoDimensionHourStaticResult;
import com.elong.hotel.constant.Const;
import com.elong.hotel.util.DateFormate;

/**
 * 多维度数据统计bolt 将统计的结果保存到一个map表中
 */

public class TwoDimensionHourCountBolt extends BaseRichBolt {

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

	@Override
	public void execute(Tuple input) {
		// 解析传过来的数据
		String businessType = input.getString(0);
		String dimensionOne = input.getString(1);
		String dimensionTwo = input.getString(2);
		String dimensionKey = dimensionOne + "&" + dimensionTwo;
		try {
			String metricStr = input.getString(3);
			Metric metric = JSONObject.parseObject(metricStr, Metric.class);

			JSONObject jsonObj = (JSONObject) input.getValue(4);
			String timeKey = jsonObj.getString(Const.LOG_TIME);

			int index = timeKey.indexOf(" ");
			String yearMonthDate = timeKey.substring(0, index);

			int[] hourMinutes = calculateHourInterval(timeKey);
			int hourRange = hourMinutes[0];

			// 将相应的值赋值到resultBean中，resultBean作为map的value保存
			TwoDimensionHourStaticResult resultBean = new TwoDimensionHourStaticResult();
			resultBean.setBusinessType(businessType);
			resultBean.setTime(yearMonthDate);
			resultBean.setDimension(dimensionKey);
			resultBean.setHourRange(hourRange);
			resultBean.setMetric(metric.getName());

			String dimensionOneValue = jsonObj.getString(dimensionOne);
			String dimensionTwoValue = jsonObj.getString(dimensionTwo);

			resultBean.setOneDimensionItemName(dimensionOneValue);
			resultBean.setTwoDimensionItemName(dimensionTwoValue);

			// 根据不同的方法相加
			int count = 0;
			if (metric.getStrategy().isSimpleAdd()) {
				count = 1;
			} else if (metric.getStrategy().isFieldAdd()) {
				count = Integer.parseInt(jsonObj.getString(metric.getFields()));
			}
			resultBean.setDimensionItemValue(count);

			collector.emit(new Values(resultBean));
		} catch (Exception e) {
			boltErrorLogger.error(e.getMessage());
		}
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("logip-counter"));
	}

	@SuppressWarnings("deprecation")
	private int[] calculateHourInterval(String logTime) {
		Date date = DateFormate.Formate(logTime);
		int hours = date.getHours();
		int minute = date.getMinutes();
		return new int[] { hours, minute };
	}

}
