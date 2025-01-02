package io.basc.framework.jms;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.Topic;

import io.basc.framework.util.Function;
import io.basc.framework.util.Source;

public class TopicConnectionOperations extends ConnectionOperations<Connection, Session, Topic> {

	public TopicConnectionOperations(ConnectionFactory connectionFactory, String topicName) {
		this(connectionFactory, Session.AUTO_ACKNOWLEDGE, topicName);
	}

	public TopicConnectionOperations(ConnectionFactory connectionFactory, int acknowledgeMode, String topicName) {
		this(connectionFactory, (c) -> c.createSession(false, acknowledgeMode), topicName);
	}

	public TopicConnectionOperations(ConnectionFactory connectionFactory,
			Function<? super Connection, ? extends Session, ? extends JMSException> connectionProcessor,
			String topicName) {
		this(() -> connectionFactory.createConnection(), connectionProcessor, topicName);
	}

	@SuppressWarnings("unchecked")
	public <T extends Connection> TopicConnectionOperations(Source<? extends T, ? extends JMSException> source,
			Function<? super T, ? extends Session, ? extends JMSException> connectionProcessor, String topicName) {
		super(source, (t) -> connectionProcessor.process((T) t), (s) -> s.createTopic(topicName));
	}
}
