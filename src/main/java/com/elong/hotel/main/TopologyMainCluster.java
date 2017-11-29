package com.elong.hotel.main;

import backtype.storm.Config;
import backtype.storm.StormSubmitter;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;

import com.elong.hotel.bolts.LogCollectBolt;
import com.elong.hotel.bolts.OneDimensionLogFilterBolt;
import com.elong.hotel.bolts.OneDimensionLogMinuteFilterBolt;
import com.elong.hotel.bolts.OneDimensionMinuteCountBolt;
import com.elong.hotel.bolts.OneDimensionMinuteLastCountBolt;
import com.elong.hotel.bolts.OneDimensionMongoMinuteBolt;
import com.elong.hotel.spouts.LogReaderSpoutsForKafka;

public class TopologyMainCluster {

	public static void main(String[] args) {

		TopologyBuilder builder = new TopologyBuilder();

		builder.setSpout("spout_log_reader", new LogReaderSpoutsForKafka("orderSubmitQueue"), 18);

		builder.setBolt("log1-collect-handler", new LogCollectBolt(), 18).localOrShuffleGrouping("spout_log_reader");

		builder.setBolt("log2-normalizer-single", new OneDimensionLogFilterBolt(), 18).localOrShuffleGrouping("log1-collect-handler");

		builder.setBolt("log3-count-minute-other", new OneDimensionMinuteCountBolt(), 144).directGrouping("log2-normalizer-single");

		builder.setBolt("log4-minuteval-filter", new OneDimensionLogMinuteFilterBolt(), 18).directGrouping("log2-normalizer-single");

		builder.setBolt("log5-count-minute-last", new OneDimensionMinuteLastCountBolt(), 18).fieldsGrouping("log4-minuteval-filter",
				new Fields("fieldGroupingKey"));

		builder.setBolt("log6-update-minute", new OneDimensionMongoMinuteBolt(), 72).localOrShuffleGrouping("log3-count-minute-other")
				.localOrShuffleGrouping("log5-count-minute-last");

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
