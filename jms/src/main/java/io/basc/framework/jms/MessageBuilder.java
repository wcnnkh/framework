package io.basc.framework.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

@FunctionalInterface
public interface MessageBuilder {
	Message build(Session session) throws JMSException;
}
