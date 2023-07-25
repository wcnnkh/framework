package io.basc.framework.jms;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageListener;
import javax.jms.Session;

import io.basc.framework.util.function.Processor;
import io.basc.framework.util.function.Source;
import io.basc.framework.util.registry.Registration;

public class ConnectionOperations<T extends Connection, S extends Session, D extends Destination>
		extends AbstractJmsOperations<T, ConnectionOperations<T, S, D>> {
	private volatile T connection;
	private final Processor<? super T, ? extends S, ? extends JMSException> connectionProcessor;

	private volatile SessionOperations<S, D> sessionOperations;

	private final Processor<? super S, ? extends D, ? extends JMSException> sessionProcessor;

	public ConnectionOperations(Source<? extends T, ? extends JMSException> source,
			Processor<? super T, ? extends S, ? extends JMSException> connectionProcessor,
			Processor<? super S, ? extends D, ? extends JMSException> sessionProcessor) {
		super(source);
		this.connectionProcessor = connectionProcessor;
		this.sessionProcessor = sessionProcessor;
	}

	@Override
	public Registration bind(MessageListener messageListener) throws JMSException {
		return getSessionOperations().bind(messageListener);
	}

	@Override
	public Registration bind(String messageSelector, MessageListener messageListener) throws JMSException {
		return getSessionOperations().bind(messageSelector, messageListener);
	}

	@Override
	public void close() throws JMSException {
		try {
			super.close();
		} finally {
			if (connection != null) {
				synchronized (this) {
					if (connection != null) {
						try {
							close(connection);
						} finally {
							connection = null;
						}
					}
				}
			}
		}
	}

	public SessionOperations<S, D> createSessionOperations(T connection) {
		return createSessionOperations(connection, connectionProcessor, sessionProcessor);
	}

	public SessionOperations<S, D> createSessionOperations(T connection,
			Processor<? super T, ? extends S, ? extends JMSException> connectionProcessor,
			Processor<? super S, ? extends D, ? extends JMSException> sessionProcessor) {
		SessionOperations<S, D> sessionOperations = new SessionOperations<S, D>(
				() -> connectionProcessor.process(connection), sessionProcessor);
		sessionOperations.copyConfig(this);
		return sessionOperations;
	}

	@Override
	public T get() throws JMSException {
		if (connection == null) {
			synchronized (this) {
				if (connection == null) {
					connection = super.get();
					connection.start();
				}
			}
		}
		return connection;
	}

	public Processor<? super T, ? extends S, ? extends JMSException> getConnectionProcessor() {
		return connectionProcessor;
	}

	public final SessionOperations<S, D> getSessionOperations() throws JMSException {
		if (sessionOperations == null) {
			synchronized (this) {
				if (sessionOperations == null) {
					sessionOperations = createSessionOperations(get());
				}
			}
		}
		return sessionOperations;
	}

	public Processor<? super S, ? extends D, ? extends JMSException> getSessionProcessor() {
		return sessionProcessor;
	}

	@Override
	public void send(MessageBuilder messageBuilder) throws JMSException {
		getSessionOperations().send(messageBuilder);
	}
}
