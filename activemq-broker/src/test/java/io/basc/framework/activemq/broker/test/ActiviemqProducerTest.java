package io.basc.framework.activemq.broker.test;

import javax.jms.JMSException;

import org.apache.activemq.ActiveMQConnectionFactory;

import io.basc.framework.jms.QueueConnectionOperations;
import io.basc.framework.jms.TopicConnectionOperations;
import io.basc.framework.util.XUtils;

public class ActiviemqProducerTest {
	public static void main(String[] args) throws JMSException, InterruptedException {
		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
		QueueConnectionOperations queueConnectionOperations = new QueueConnectionOperations(connectionFactory,
				"test_queue");
		TopicConnectionOperations topicConnectionOperations = new TopicConnectionOperations(connectionFactory,
				"test_topic");
		while (true) {
			Thread.sleep(1000L);
			queueConnectionOperations.send((e) -> e.createTextMessage(XUtils.getUUID()));
			Thread.sleep(1000L);
			topicConnectionOperations.send((e) -> e.createTextMessage(XUtils.getUUID()));
		}
	}

}
