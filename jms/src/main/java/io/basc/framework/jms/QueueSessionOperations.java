package io.basc.framework.jms;

import javax.jms.JMSException;
import javax.jms.QueueSession;

import io.basc.framework.util.Source;

public class QueueSessionOperations extends SessionOperations<QueueSession> {

	public QueueSessionOperations(Source<? extends QueueSession, ? extends JMSException> source) {
		super(source);
	}
}
