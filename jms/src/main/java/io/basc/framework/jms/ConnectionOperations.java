package io.basc.framework.jms;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Session;

import io.basc.framework.lang.Nullable;
import io.basc.framework.util.ConsumeProcessor;
import io.basc.framework.util.Processor;
import io.basc.framework.util.RunnableProcessor;
import io.basc.framework.util.Source;
import io.basc.framework.util.StreamOperations;

public class ConnectionOperations<T extends Connection> extends JmsOperations<T, ConnectionOperations<T>> {

	public ConnectionOperations(Source<? extends T, ? extends JMSException> source) {
		super(source);
	}

	public <S> ConnectionOperations(StreamOperations<S, ? extends JMSException> sourceStreamOperations,
			Processor<? super S, ? extends T, ? extends JMSException> processor,
			@Nullable ConsumeProcessor<? super T, ? extends JMSException> closeProcessor,
			@Nullable RunnableProcessor<? extends JMSException> closeHandler) {
		super(sourceStreamOperations, processor, closeProcessor, closeHandler);
	}

	public <S extends Session> SessionOperations<S> sessionOperations(
			Processor<? super T, ? extends S, ? extends JMSException> processor) {
		return new SessionOperations<>(this, processor, (e) -> e.close(), null);
	}
}
