package com.elong.hotel.service;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.elong.hotel.bean.Metric;
import com.elong.hotel.bean.MetricStrategy;
import com.elong.hotel.constant.Const;
import com.elong.hotel.util.MongoConfig;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class DimensionMetricService {

	public static Map<String, Map<String[], Set<Metric>>> getDimensionMetricMapping(
			String dimension2Filter) {
		DB mongoDB;
		try {
			mongoDB = new MongoClient(MongoConfig.getIp(),
					MongoConfig.getPort()).getDB(MongoConfig.getDbName());
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		}

		List<DBObject> dbObjs = mongoDB.getCollection(Const.C_MODULE).find()
				.toArray();

		if (dbObjs.size() == 0)
			return null;

		Map<String, Map<String[], Set<Metric>>> rst = new HashMap<String, Map<String[], Set<Metric>>>();
		for (DBObject dbObj : dbObjs) {

			String moduleBizType = dbObj.get(Const.BUSINESS_TYPE).toString();
			String moduleId = dbObj.get("_id").toString();
			DBObject queryIDRelation = new BasicDBObject();
			queryIDRelation.put(Const.MODULE_ID, moduleId);

			List<DBObject> dimensionList = mongoDB
					.getCollection(Const.C_DIMENSION).find(queryIDRelation)
					.toArray();

			// 只保留dimension2Filter维的维度
			List<DBObject> filterDimensionList = new LinkedList<DBObject>();
			for (DBObject item : dimensionList) {
				// 判断当前是几维
				String count = item.get("dimension_count").toString();
				if (!dimension2Filter.equals(count)) {
					continue;
				}
				filterDimensionList.add(item);
			}

			Set<String> setDemsion = new HashSet<String>();
			for (DBObject obj : filterDimensionList) {
				setDemsion.add(obj.get("_id").toString());
			}

			DBObject queryDimensionMetric = new BasicDBObject();
			queryDimensionMetric.put("dimension_id", new BasicDBObject("$in",
					setDemsion));
			DBCursor cursorDimensionMetric = mongoDB.getCollection(
					"C_DIMENSION_METRIC_RELATION").find(queryDimensionMetric);

			Map<String, Metric> metricMap = findAllMetric(mongoDB);
			Map<String, Set<Metric>> dimensionMetricMap = new HashMap<String, Set<Metric>>();
			while (cursorDimensionMetric.hasNext()) {
				DBObject obj = cursorDimensionMetric.next();

				String dimensionId = (String) obj.get("dimension_id");
				String metircId = (String) obj.get("metric_id");

				if (null == dimensionMetricMap.get(dimensionId)) {
					Set<Metric> setDimensionMetric = new HashSet<Metric>();
					setDimensionMetric.add(metricMap.get(metircId));

					dimensionMetricMap.put(dimensionId, setDimensionMetric);
				} else {
					dimensionMetricMap.get(dimensionId).add(
							metricMap.get(metircId));
				}
			}

			Map<String[], Set<Metric>> dimensionNameMetricMap = new HashMap<String[], Set<Metric>>();
			for (DBObject item : filterDimensionList) {
				String id = item.get("_id").toString();
				String dimension = item.get("dimension_name").toString();
				dimensionNameMetricMap.put(new String[] { id, dimension },
						dimensionMetricMap.get(id));
			}

			rst.put(moduleBizType, dimensionNameMetricMap);
		}

		return rst;
	}

	private static Map<String, Metric> findAllMetric(DB mongoDB) {
		Map<String, Metric> metricMap = new HashMap<String, Metric>();
		DBCursor cursorMetric = mongoDB.getCollection("C_METRIC").find();
		while (cursorMetric.hasNext()) {
			DBObject dbObj = cursorMetric.next();
			String name = dbObj.get("name").toString();
			MetricStrategy strategy;
			String fields = null;
			String formula = dbObj.get("formula").toString();
			if (StringUtils.isBlank(formula)) {
				strategy = MetricStrategy.simleAdd;
			} else {
				strategy = MetricStrategy.fieldAdd;
				fields = formula;
			}
			String id = String.valueOf(cursorMetric.curr().get("_id"));

			Metric metric = new Metric();
			metric.setFields(fields);
			metric.setName(name);
			metric.setStrategy(strategy);
			metricMap.put(id, metric);
		}

		return metricMap;

	}
}
