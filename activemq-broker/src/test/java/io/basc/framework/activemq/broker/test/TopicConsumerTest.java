package io.basc.framework.activemq.broker.test;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

import io.basc.framework.jms.JmsDelayableEventDispatcher;
import io.basc.framework.jms.JmsOperations;
import io.basc.framework.jms.MessageBuilder;
import io.basc.framework.jms.MessageCodec;
import io.basc.framework.jms.TopicConnectionOperations;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;

public class TopicConsumerTest {
	private static Logger logger = LoggerFactory.getLogger(TopicConsumerTest.class);

	public static void main(String[] args) throws JMSException {
		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
		JmsOperations jmsOperations = new TopicConnectionOperations(connectionFactory, "test_topic");
		JmsDelayableEventDispatcher<String> jmsDelayableEventDispatcher = new JmsDelayableEventDispatcher<>(
				jmsOperations, new MessageCodec<String>() {

					@Override
					public MessageBuilder encode(String source) throws JMSException {
						return (session) -> session.createTextMessage(source);
					}

					@Override
					public String decode(Message message) throws JMSException {
						if (message instanceof TextMessage) {
							return ((TextMessage) message).getText();
						}
						return null;
					}

				});

		jmsDelayableEventDispatcher.registerListener((e) -> {
			logger.info(e);
		});
	}
}
