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

		builder.setSpout("spout_reader", new LogReaderSpoutsForKafka("orderSubmitQueue"), 36);

		builder.setBolt("log1-handler", new LogCollectBolt(), 36).localOrShuffleGrouping("spout_reader");

		builder.setBolt("log2-normalizer", new OneDimensionLogFilterBolt(), 36).localOrShuffleGrouping("log1-handler");

		builder.setBolt("log3-count-other", new OneDimensionMinuteCountBolt(), 144).localOrShuffleGrouping("log2-normalizer");

		builder.setBolt("log4-last-filter", new OneDimensionLogMinuteFilterBolt(), 18).localOrShuffleGrouping("log2-normalizer");

		builder.setBolt("log5-count-last", new OneDimensionMinuteLastCountBolt(), 18).fieldsGrouping("log4-last-filter",
				new Fields("fieldGroupingKey"));

		builder.setBolt("log6-update", new OneDimensionMongoMinuteBolt(), 72).localOrShuffleGrouping("log3-count-other")
				.localOrShuffleGrouping("log5-count-last");

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
