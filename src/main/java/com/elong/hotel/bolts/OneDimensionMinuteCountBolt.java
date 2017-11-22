package com.elong.hotel.bolts;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import backtype.storm.Config;
import backtype.storm.Constants;
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
import com.elong.hotel.bean.OneDimensionMinuteStaticResult;
import com.elong.hotel.constant.Const;
import com.elong.hotel.util.CustomUtil;
import com.elong.hotel.util.DateFormate;

/**
 * 计算实时数据的bolt
 * */

public class OneDimensionMinuteCountBolt extends BaseRichBolt {

	private static final long serialVersionUID = 1L;
	private OutputCollector collector;
	protected final static Logger boltErrorLogger = LoggerFactory.getLogger("bolt-error-log");

	/** 
	 * 按照fieldgrouping方式发送bolt,不考虑多线程问题
	 *
	 * Map<String,OneDimensionMinuteStaticResult> OneDimensionMinuteCountBolt.java memoryStaticBeanMap
	 */
	private Map<String, OneDimensionMinuteStaticResult> memoryStaticBeanMap = new ConcurrentHashMap<String, OneDimensionMinuteStaticResult>();

	/** 
	 * 判断是否为tick消息
	 *
	 * @param tuple
	 * @return
	 */
	public static boolean isTickTuple(Tuple tuple) {
		return tuple.getSourceComponent().equals(Constants.SYSTEM_COMPONENT_ID)
				&& tuple.getSourceStreamId().equals(Constants.SYSTEM_TICK_STREAM_ID);
	}

	/** 
	 * Storm组件的定时器
	 *
	 * @return 
	 *
	 * @see backtype.storm.topology.base.BaseComponent#getComponentConfiguration()    
	 */
	@Override
	public Map<String, Object> getComponentConfiguration() {
		Map<String, Object> conf = new HashMap<String, Object>();
		conf.put(Config.TOPOLOGY_TICK_TUPLE_FREQ_SECS, 60);// 每60s持久化一次数据
		return conf;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
		this.collector = collector;
	}

	@Override
	public void execute(Tuple input) {
		try {
			// 定时器触发的，从内存中取数据emit,同时清除内存中已emit数据
			if (isTickTuple(input)) {
				Collection<OneDimensionMinuteStaticResult> memoryStaticBeanList = memoryStaticBeanMap.values();
				Iterator<OneDimensionMinuteStaticResult> iter = memoryStaticBeanList.iterator();
				while (iter.hasNext()) {
					OneDimensionMinuteStaticResult memoryStaticBean = iter.next();
					collector.emit(new Values(memoryStaticBean));
					iter.remove();
				}
				return;
			}

			String businessType = input.getString(1);
			String dimensionKey = input.getString(2);
			String metricStr = input.getString(3);
			Metric metric = JSONObject.parseObject(metricStr, Metric.class);
			JSONObject jsonObj = (JSONObject) input.getValue(4);

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
			} else if (metric.getStrategy().isMinuteAdd()) {
				value = Long.parseLong(jsonObj.getString(metric.getFields()));
			}

			staticBean.setDimensionItemName(jsonObj.getString(dimensionKey));
			staticBean.setDimensionItemValue(value);

			// 按分钟计算方式，数据存入内存，等定时器emit
			if (metric.getStrategy().isMinuteAdd()) {
				Map<String, Object> keyMap = new HashMap<String, Object>();
				keyMap.put("collection", staticBean.getBusinessType());
				keyMap.put("dimension", staticBean.getDimension());
				keyMap.put("dateTime", staticBean.getTime());
				keyMap.put("metric", staticBean.getMetric());
				keyMap.put("dimensionItem", "dimensionValue." + CustomUtil.deleteDot(staticBean.getDimensionItemName()));
				String keyStr = JSON.toJSONString(keyMap);
				memoryStaticBeanMap.put(keyStr, staticBean);
			} else {
				collector.emit(new Values(staticBean));
			}
		} catch (Exception e) {
			boltErrorLogger.error(e.getMessage());
		}
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("field_minute_counter"));
	}

}
