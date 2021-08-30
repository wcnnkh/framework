package io.basc.framework.amqp;

import java.io.IOException;

@FunctionalInterface
public interface MessageListener {
	void onMessage(String exchange, String routingKey, Message message) throws IOException;
}
