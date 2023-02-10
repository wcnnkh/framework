package io.basc.framework.jms;

import javax.jms.JMSException;
import javax.jms.Session;

import io.basc.framework.transaction.Transaction;
import io.basc.framework.transaction.TransactionManager;
import io.basc.framework.transaction.TransactionUtils;
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
	
	@Override
	public T get() throws JMSException {
		TransactionManager transactionManager = TransactionUtils.getManager();
			Transaction transaction = transactionManager.getTransaction();
		return super.get();
	}
}
