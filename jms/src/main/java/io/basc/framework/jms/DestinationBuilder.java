package io.basc.framework.jms;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.Topic;

@FunctionalInterface
public interface DestinationBuilder<T extends Destination> {
	T build(Session session) throws JMSException;

	public static DestinationBuilder<Topic> topic(String name) {
		return (session) -> session.createTopic(name);
	}

	public static DestinationBuilder<Queue> queue(String name) {
		return (session) -> session.createQueue(name);
	}
}
