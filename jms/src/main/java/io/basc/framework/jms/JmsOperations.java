package io.basc.framework.jms;

import javax.jms.JMSException;

import io.basc.framework.util.Processor;
import io.basc.framework.util.Source;
import io.basc.framework.util.StandardStreamOperations;
import io.basc.framework.util.StreamOperations;

public abstract class JmsOperations<T, C extends JmsOperations<T, C>> extends StandardStreamOperations<T, JMSException, C> {

	public <S> JmsOperations(StreamOperations<S, ? extends JMSException> sourceStreamOperations,
			Processor<? super S, ? extends T, ? extends JMSException> processor) {
		super(sourceStreamOperations, processor);
	}

	public JmsOperations(Source<? extends T, ? extends JMSException> source) {
		super(source);
	}
}
