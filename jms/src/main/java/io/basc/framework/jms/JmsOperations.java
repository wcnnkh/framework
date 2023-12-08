package io.basc.framework.jms;

import javax.jms.JMSException;
import javax.jms.MessageListener;

import io.basc.framework.util.Registration;

public interface JmsOperations {
	Registration bind(MessageListener messageListener) throws JMSException;

	Registration bind(String messageSelector, MessageListener messageListener) throws JMSException;

	void send(MessageBuilder messageBuilder) throws JMSException;
}
