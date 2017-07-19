package com.elong.hotel.bolts;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

import com.alibaba.fastjson.JSONObject;
import com.elong.hotel.biz.DefaultBizHandler;
import com.elong.hotel.biz.IBizHandler;
import com.elong.hotel.constant.Const;
import com.elong.hotel.util.PropertiesHelper;

public class LogCollectBolt extends BaseRichBolt {

	private static final long serialVersionUID = 890798096085674932L;
	private OutputCollector outputCollector;

	private Map<String, IBizHandler> handlers;

	@Override
	public void prepare(@SuppressWarnings("rawtypes") Map stormConf,
			TopologyContext context, OutputCollector collector) {
		this.outputCollector = collector;

		handlers = new HashMap<String, IBizHandler>();

		Properties p = PropertiesHelper.getEnvPropertise("handlers");
		try {
			for (Entry<Object, Object> e : p.entrySet()) {
				Class<?> clazz = Class.forName((String) e.getValue());
				IBizHandler biz = (IBizHandler) (clazz.newInstance());
				handlers.put((String) (e.getKey()), biz);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	@Override
	public void execute(Tuple input) {
		String message = input.getString(1);

		if (StringUtils.isNotEmpty(message)) {
			JSONObject logJson = JSONObject.parseObject(message);
			if (logJson == null) {
				return;
			}

			// 开始预处理
			List<JSONObject> rst = null;
			try {
				IBizHandler handler = handlers.get(logJson.getString(Const.BUSINESS_TYPE));
				if (handler == null) handler = new DefaultBizHandler();
				rst = handler.handle(logJson);
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (rst != null)
				for (JSONObject jo : rst)
					outputCollector.emit(new Values(jo));
		}

	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("logJson"));
	}

}
