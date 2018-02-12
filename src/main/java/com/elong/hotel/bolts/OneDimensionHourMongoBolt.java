package com.elong.hotel.bolts;

import java.net.UnknownHostException;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;

import com.elong.hotel.bean.OneDimensionHourStaticResult;
import com.elong.hotel.util.CustomUtil;
import com.elong.hotel.util.MongoConfig;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.WriteResult;

/**
 * 单个维度入库
 */
public class OneDimensionHourMongoBolt extends BaseRichBolt {

	private static final long serialVersionUID = 4009023140777397459L;

	private final Logger boltErrorLogger = LoggerFactory
			.getLogger("bolt-error-log");
	private DB mongoDB;

	public void prepare(@SuppressWarnings("rawtypes") Map stormConf,
			TopologyContext context, OutputCollector collector) {
		try {
			mongoDB = new MongoClient(MongoConfig.getIp(),
					MongoConfig.getPort()).getDB(MongoConfig.getDbName());
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void execute(Tuple input) {
		try {
			OneDimensionHourStaticResult statisticResult = (OneDimensionHourStaticResult) input
					.getValue(0);
			String collectionName = statisticResult.getBusinessType();

			DBObject query = new BasicDBObject();

			query.put("date", statisticResult.getDate());
			query.put("dimension", statisticResult.getDimension());
			query.put("hourRange", statisticResult.getHourRange());
			query.put("metric", statisticResult.getMetric());

			DBObject incUpdates = new BasicDBObject();
			String dimensionItemName = CustomUtil.deleteDot(statisticResult
					.getDimensionItemName());
			if (StringUtils.isEmpty(dimensionItemName)) {
				return;
			}

			String dimensionValue = "dimensionValue." + dimensionItemName;

			incUpdates.put(dimensionValue,
					statisticResult.getDimensionItemValue());
			incUpdates.put("total", statisticResult.getDimensionItemValue());

			DBObject updateSetValue = new BasicDBObject("$inc", incUpdates);

			WriteResult writeResult = mongoDB.getCollection(collectionName)
					.update(query, updateSetValue, true, false);
			// 打印错误级别的log
			if (writeResult.getN() < 0) {
				boltErrorLogger.error("This is name:{},value:{}",
						collectionName, updateSetValue);
			}

		} catch (Exception e) {
			boltErrorLogger.error(e.getMessage());
		}
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
	}

}
