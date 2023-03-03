package io.basc.framework.activemq.broker.test;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;

import io.basc.framework.util.XUtils;

public class ActiviemqProducerTest {
	public static void main(String[] args) throws JMSException, InterruptedException {
		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
		Connection connection = connectionFactory.createConnection();
		connection.start();

		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		MessageProducer messageProducer = session.createProducer(session.createQueue("queueName"));

		while (true) {
			Thread.sleep(1000L);
			Message message = session.createTextMessage(XUtils.getUUID());
			messageProducer.send(message);
		}
	}

}
