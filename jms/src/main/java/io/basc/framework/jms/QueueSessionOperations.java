package io.basc.framework.jms;

import javax.jms.JMSException;
import javax.jms.QueueSession;

import io.basc.framework.util.Processor;
import io.basc.framework.util.Source;
import io.basc.framework.util.StreamOperations;

public class QueueSessionOperations extends SessionOperations<QueueSession> {

	public QueueSessionOperations(Source<? extends QueueSession, ? extends JMSException> source) {
		super(source);
	}

	public <S> QueueSessionOperations(StreamOperations<S, ? extends JMSException> sourceStreamOperations,
			Processor<? super S, ? extends QueueSession, ? extends JMSException> processor) {
		super(sourceStreamOperations, processor);
	}
}
