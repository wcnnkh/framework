package io.basc.framework.jms;

import javax.jms.JMSException;
import javax.jms.Message;

public interface MessageCodec<T> {
	MessageBuilder encode(T source) throws JMSException;

	T decode(Message message) throws JMSException;
}
