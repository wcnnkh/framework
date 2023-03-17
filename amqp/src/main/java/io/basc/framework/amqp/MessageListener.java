package io.basc.framework.amqp;

import java.io.IOException;

@FunctionalInterface
public interface MessageListener<T> {
	void onMessage(String exchange, String routingKey, Message<T> message) throws IOException;
}
