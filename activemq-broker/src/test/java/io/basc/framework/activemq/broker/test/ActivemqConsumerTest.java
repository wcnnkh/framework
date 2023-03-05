package io.basc.framework.activemq.broker.test;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;

import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;

public class ActivemqConsumerTest {
	private static Logger loger = LoggerFactory.getLogger(ActiviemqProducerTest.class);

	public static void main(String[] args) throws JMSException {
		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
		Connection connection = connectionFactory.createConnection();
		connection.start();
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		MessageConsumer messageConsumer = session.createConsumer(session.createQueue("queueName"));
		messageConsumer.setMessageListener((e) -> {
			loger.info(e.toString());
		});
	}
}
