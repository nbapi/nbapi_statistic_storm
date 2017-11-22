package com.elong.hotel.main;

import backtype.storm.Config;
import backtype.storm.StormSubmitter;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;

import com.elong.hotel.bolts.LogCollectBolt;
import com.elong.hotel.bolts.OneDimensionLogFilterBolt;
import com.elong.hotel.bolts.OneDimensionMinuteCountBolt;
import com.elong.hotel.bolts.OneDimensionMongoMinuteBolt;
import com.elong.hotel.spouts.LogReaderSpoutsForKafka;

public class TopologyMainCluster {

	public static void main(String[] args) {

		TopologyBuilder builder = new TopologyBuilder();

		builder.setSpout("spout_log_reader", new LogReaderSpoutsForKafka("orderSubmitQueue"), 18);
		LogCollectBolt logCollectBolt = new LogCollectBolt();
		builder.setBolt("bolt_log_collect", logCollectBolt, 18).localOrShuffleGrouping("spout_log_reader");

		builder.setBolt("log-normalizer-single", new OneDimensionLogFilterBolt(), 18).localOrShuffleGrouping("bolt_log_collect");

		builder.setBolt("log-count-minute", new OneDimensionMinuteCountBolt(), 36).fieldsGrouping("log-normalizer-single",
				new Fields("fieldGroupingKey"));
		builder.setBolt("log-update-minute", new OneDimensionMongoMinuteBolt(), 36).localOrShuffleGrouping("log-count-minute");

		Config conf = new Config();
		conf.setDebug(true);

		// cluster mode:
		try {
			conf.setNumWorkers(18);
			StormSubmitter.submitTopology("NBAPIStatisticTopology", conf, builder.createTopology());
		} catch (Exception e) {
			e.printStackTrace();
		}

		// local mode:
		// try {
		// conf.setMaxTaskParallelism(1);
		// LocalCluster cluster = new LocalCluster();
		// cluster.submitTopology("NBAPIStatisticTopology", conf,
		// builder.createTopology());
		//
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
	}
}
