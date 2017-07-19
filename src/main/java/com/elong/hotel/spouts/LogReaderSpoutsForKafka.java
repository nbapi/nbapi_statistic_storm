package com.elong.hotel.spouts;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.message.MessageAndMetadata;
import kafka.serializer.StringDecoder;
import kafka.utils.VerifiableProperties;

import org.apache.commons.lang.StringUtils;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

import com.elong.hotel.util.PropertiesHelper;

@SuppressWarnings("serial")
public class LogReaderSpoutsForKafka extends BaseRichSpout {

	private SpoutOutputCollector collector;
	@SuppressWarnings("unused")
	private TopologyContext context;

	private ConsumerIterator<String, String> it;
	
	private String topic;
	
	public LogReaderSpoutsForKafka(String topic){
		this.topic = topic;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void open(Map conf, TopologyContext context,
			SpoutOutputCollector collector) {
		this.collector = collector;
		this.context = context;

		Properties properties = PropertiesHelper
				.getEnvPropertise("kafka");
		ConsumerConfig config = new ConsumerConfig(properties);
		ConsumerConnector consumer = kafka.consumer.Consumer
				.createJavaConsumerConnector(config);

		Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
		topicCountMap.put(topic, new Integer(1));

		StringDecoder keyDecoder = new StringDecoder(new VerifiableProperties());
		StringDecoder valueDecoder = new StringDecoder(
				new VerifiableProperties());

		Map<String, List<KafkaStream<String, String>>> consumerMap = consumer
				.createMessageStreams(topicCountMap, keyDecoder, valueDecoder);

		KafkaStream<String, String> stream = consumerMap.get(topic).get(0);
		it = stream.iterator();

	}

	@Override
	public void nextTuple() {
		try {
			if (it.hasNext()) {
				MessageAndMetadata<String, String> msgAndMetadata = it.next();
				String message = msgAndMetadata.message();
				if (StringUtils.isEmpty(message))
					return;
				this.collector.emit(new Values(topic,message));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("topic","spout_field_idc1_log"));
	}

}
