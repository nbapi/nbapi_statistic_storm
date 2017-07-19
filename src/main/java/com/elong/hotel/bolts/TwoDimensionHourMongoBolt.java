package com.elong.hotel.bolts;

import java.net.UnknownHostException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;

import com.elong.hotel.bean.TwoDimensionHourStaticResult;
import com.elong.hotel.util.CustomUtil;
import com.elong.hotel.util.MongoConfig;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.WriteResult;

/**
 * 二维数据的入库
 * 
 */
public class TwoDimensionHourMongoBolt extends BaseRichBolt {

	private static final long serialVersionUID = -5479806889659026817L;

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
			TwoDimensionHourStaticResult bean = (TwoDimensionHourStaticResult) input.getValue(0);
			String collectionName = bean.getBusinessType() + "_" + "multi";
			
			DBObject query = new BasicDBObject();

			// 将要插入的字段放入到DBObject中
			query.put("date", bean.getTime());
			query.put("dimension", bean.getDimension());
			query.put("hourRange", bean.getHourRange());
			query.put("metric", bean.getMetric());

			DBObject incUpdates = new BasicDBObject();

			String key = CustomUtil.deleteDot(bean.getOneDimensionItemName())
					+ "."
					+ CustomUtil.deleteDot(bean.getTwoDimensionItemName());
			incUpdates.put(key, bean.getDimensionItemValue());
			incUpdates.put("total", bean.getDimensionItemValue());

			DBObject updateSetValue = new BasicDBObject();
			updateSetValue.put("$inc", incUpdates);

			// 将数据存入数据库
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
		declarer.declare(new Fields("mongo-update"));
	}
}
