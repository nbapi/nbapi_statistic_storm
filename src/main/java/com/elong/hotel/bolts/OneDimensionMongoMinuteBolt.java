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

import com.elong.hotel.bean.OneDimensionMinuteStaticResult;
import com.elong.hotel.constant.Const;
import com.elong.hotel.util.CustomUtil;
import com.elong.hotel.util.MongoConfig;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.WriteResult;

/**
 * 实时数据的入库
 */
public class OneDimensionMongoMinuteBolt extends BaseRichBolt {

	private static final long serialVersionUID = -1444353347874713846L;

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
			OneDimensionMinuteStaticResult bean = (OneDimensionMinuteStaticResult) input
					.getValue(0);
			String collectionName = bean.getBusinessType()
					+ Const.COLLECTION_SUFFIX_MINUTE;

			DBObject query = new BasicDBObject();

			query.put("dimension", bean.getDimension());
			query.put("dateTime", bean.getTime());
			query.put("metric", bean.getMetric());

			DBObject incUpdates = new BasicDBObject();

			if (StringUtils.isEmpty(bean.getDimensionItemName())) {
				boltErrorLogger.error("dimension item is emputy", bean);
				return;
			}

			String key = "dimensionValue."
					+ CustomUtil.deleteDot(bean.getDimensionItemName());
			incUpdates.put(key, bean.getDimensionItemValue());
			incUpdates.put("total", bean.getDimensionItemValue());

			DBObject setUpdates = new BasicDBObject();
			setUpdates.put("time", bean.getTimeRange());
			setUpdates.put("date", bean.getDate());

			DBObject updateSetValue = new BasicDBObject();
			updateSetValue.put("$inc", incUpdates);
			updateSetValue.put("$set", setUpdates);

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
	}
}
