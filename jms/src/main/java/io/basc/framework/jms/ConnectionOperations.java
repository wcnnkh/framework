package io.basc.framework.jms;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Session;

import io.basc.framework.util.Processor;
import io.basc.framework.util.Source;

public class ConnectionOperations<T extends Connection> extends JmsOperations<T, ConnectionOperations<T>> {

	public ConnectionOperations(Source<? extends T, ? extends JMSException> source) {
		super(source);
		super.onClose((e) -> e.close());
	}

	public ConnectionOperations(T connection) {
		super(() -> connection);
	}

	public <S extends Session> SessionOperations<S> sessionOperations(
			Processor<? super T, ? extends S, ? extends JMSException> processor) {
		return new SessionOperations<>(() -> processor.process(get()));
	}
}
