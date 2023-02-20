package io.basc.framework.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;

public interface MessageSendingStrategy<P extends MessageProducer, M extends Message, E extends Throwable> {
	void send(P producer, M message) throws E, JMSException;
}
