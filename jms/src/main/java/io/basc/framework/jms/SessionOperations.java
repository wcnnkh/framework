package io.basc.framework.jms;

import javax.jms.JMSException;
import javax.jms.Session;

import io.basc.framework.util.Processor;
import io.basc.framework.util.Source;
import io.basc.framework.util.StreamOperations;

public class SessionOperations<T extends Session> extends JmsOperations<T, SessionOperations<T>> {

	public SessionOperations(Source<? extends T, ? extends JMSException> source) {
		super(source);
	}

	public <S> SessionOperations(StreamOperations<S, ? extends JMSException> sourceStreamOperations,
			Processor<? super S, ? extends T, ? extends JMSException> processor) {
		super(sourceStreamOperations, processor);
	}
}
