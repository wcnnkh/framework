package io.basc.framework.jms;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.Session;

import io.basc.framework.util.Pipeline;
import io.basc.framework.util.Source;

public class QueueConnectionOperations extends ConnectionOperations<Connection, Session, Queue> {

	public QueueConnectionOperations(ConnectionFactory connectionFactory, String queueName) {
		this(connectionFactory, Session.AUTO_ACKNOWLEDGE, queueName);
	}

	public QueueConnectionOperations(ConnectionFactory connectionFactory, int acknowledgeMode, String queueName) {
		this(connectionFactory, (c) -> c.createSession(false, acknowledgeMode), queueName);
	}

	public QueueConnectionOperations(ConnectionFactory connectionFactory,
			Pipeline<? super Connection, ? extends Session, ? extends JMSException> connectionProcessor,
			String queueName) {
		this(() -> connectionFactory.createConnection(), connectionProcessor, queueName);
	}

	@SuppressWarnings("unchecked")
	public <T extends Connection> QueueConnectionOperations(Source<? extends T, ? extends JMSException> source,
			Pipeline<? super T, ? extends Session, ? extends JMSException> connectionProcessor, String queueName) {
		super(source, (t) -> connectionProcessor.process((T) t), (s) -> s.createQueue(queueName));
	}
}
