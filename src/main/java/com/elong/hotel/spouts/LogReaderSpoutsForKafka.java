package com.elong.hotel.spouts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.message.MessageAndMetadata;
import kafka.serializer.StringDecoder;

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

	private static final int CUSTOMER_THREAD_NUM = 20;

	private ExecutorService executorService;

	private SpoutOutputCollector collector;
	@SuppressWarnings("unused")
	private TopologyContext context;

	private List<ConsumerIterator<String, String>> its = new ArrayList<ConsumerIterator<String,String>>();

	private String topic;

	public LogReaderSpoutsForKafka(String topic) {
		this.topic = topic;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
		this.collector = collector;
		this.context = context;
		this.executorService = Executors.newFixedThreadPool(CUSTOMER_THREAD_NUM);

		Properties properties = PropertiesHelper.getEnvPropertise("kafka");
		ConsumerConfig config = new ConsumerConfig(properties);
		ConsumerConnector consumer = kafka.consumer.Consumer.createJavaConsumerConnector(config);

		Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
		topicCountMap.put(topic, new Integer(CUSTOMER_THREAD_NUM));

		StringDecoder keyDecoder = new StringDecoder(null);
		StringDecoder valueDecoder = new StringDecoder(null);

		Map<String, List<KafkaStream<String, String>>> consumerMap = consumer.createMessageStreams(topicCountMap, keyDecoder, valueDecoder);
		List<KafkaStream<String, String>> streams = consumerMap.get(topic);
		for (KafkaStream stream : streams) {
			@SuppressWarnings("unchecked")
			ConsumerIterator<String, String> it = stream.iterator();
			its.add(it);
		}
	}

	@Override
	public void nextTuple() {
		try {
			for (final ConsumerIterator<String, String> it : its) {
				executorService.submit(new Runnable() {
					@Override
					public void run() {
						if (it.hasNext()) {
							MessageAndMetadata<String, String> msgAndMetadata = it.next();
							String message = msgAndMetadata.message();
							if (StringUtils.isEmpty(message))
								return;
							collector.emit(new Values(topic, message));
						}
					}
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("topic", "spout_field_idc1_log"));
	}

}
